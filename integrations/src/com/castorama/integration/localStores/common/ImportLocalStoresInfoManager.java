package com.castorama.integration.localStores.common;

import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import com.castorama.core.RepositoryInvalidationService;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.localStores.util.LocalStoresUtils;
import com.castorama.integration.stock.SizeLimiterInputStream;
import com.castorama.integration.stock.UncompressInputStream;
import com.castorama.integration.util.MiscUtils;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ImportLocalStoresInfoManager extends IntegrationBase{
    /** Constant for index of line code field */
    private static final int LINE_CODE_INDEX = 0;

    /**
     * Constants for the type codes of imported lines.
     */
    private static final int HEADLINE = 1;
    private static final int IMPORT_PRICES = 2;
    private static final int IMPORT_FCC = 3;
    private static final int END_OF_FILE = 4;

    /**
     * PROMO_INFO_DESCRIPTOR_NAME constant.
     */
    private static final String SKU_CLICK_COLLECT_FLAG_DESCRIPTOR_NAME = "skuClickCollectFlag";

    /**
     * PROCESSING_FEE_DESCRIPTOR_NAME constant.
     */
    private static final String PROCESSING_FEE_DESCRIPTOR_NAME = "processingFee";

    /**
     * PROMO_INFO_DESCRIPTOR_NAME constant.
     */
    private static final String PROMO_INFO_DESCRIPTOR_NAME = "promoInfo";

    /**
     * ECOTAXE_INFO_DESCRIPTOR_NAME constant.
     */
    private static final String ECOTAXE_INFO_DESCRIPTOR_NAME = "ecotaxeInfo";

    /**
     * PRICE_DESCRIPTOR_NAME constant.
     */
    private static final String PRICE_DESCRIPTOR_NAME = "price";

    /**
     * LOCK_EXTENSION property
     */
    private static final String LOCK_EXTENSION = ".lock";

    public static String ARCHIVE_FORMAT_ZIP = ".ZIP";

    public static String ARCHIVE_FORMAT_Z = ".Z";

    /**
     * Tables names.
     */
    public static String TEMP_TABLE_NAME = "CAST_PRICE_TEMP";
    public static String PROCESSING_FEE_TEMP_TABLE = "CAST_PROCESSING_FEE_TEMP";
    public static String FCC_TEMP_TABLE = "FCC_TEMP";

    /**
     * Tables creation.
     */
    public static String CREATE_TEMP_TABLE_SQL = 
            "CREATE TABLE CAST_PRICE_TEMP ("
            + "PRICE_ID		    VARCHAR2(40)	PRIMARY KEY"
            + ",PRICE_LIST		VARCHAR2(40)	NULL"
            + ",SKU_ID			VARCHAR2(40)	NULL"
            + ",STORE_ID		VARCHAR2(40)	NULL"
            + ",LIST_PRICE		NUMBER(19,7)	NULL"
            + ",MD3E			NUMBER(19,7)	NULL"
            + ",MREP			NUMBER(19,7)	NULL"
            + ",FPROMO			NUMBER(1,0)		NULL"
            + ",CPROMO 			NUMBER(5,0)		NULL)";

    public static String CREATE_PROCESSING_FEE_TEMP_TABLE_SQL = 
            "CREATE TABLE CAST_PROCESSING_FEE_TEMP ("
            + "FEE_ID      VARCHAR2(40)    PRIMARY KEY"
            + ",STORE_ID   VARCHAR2(40)    NULL"
            + ",AMOUNT     NUMBER(19,7)    NULL)";

    public static String CREATE_FCC_TEMP_TABLE_SQL = 
            "CREATE TABLE FCC_TEMP ("
            + "ID           VARCHAR2(40)    PRIMARY KEY" 
            + ",STORE_ID    VARCHAR2(40)    NULL"
            + ",SKU_ID      VARCHAR2(40)    NULL" 
            + ",FCC         NUMBER(1,0)     NULL)";

    /**
     * Tables filling.
     */
    public static final String INSERT_INTO_TEMP_TABLE_SQL = 
            "insert into CAST_PRICE_TEMP (PRICE_ID, PRICE_LIST, SKU_ID, STORE_ID, LIST_PRICE, MD3E, MREP, FPROMO, CPROMO)"
            + " values(?,?,?,?,?,?,?,?,?)";

    public static final String INSERT_PROCESSING_FEE_TEMP_TABLE_SQL = 
            "insert into CAST_PROCESSING_FEE_TEMP (FEE_ID, STORE_ID, AMOUNT)"
            + " values(?,?,?)";

    public static final String INSERT_FCC_TEMP_TABLE_SQL = 
            "insert into FCC_TEMP (ID, STORE_ID, SKU_ID, FCC)"
            + " values(?,?,?,?)";

    /**
     * Tables cleaning.
     */
    public static String REMOVE_CAST_PRICE = 
            "DELETE FROM CAST_PRICE WHERE PRICE_ID IN "
            + "(SELECT CAST_PRICE.PRICE_ID"
            + " FROM CAST_PRICE LEFT OUTER JOIN CAST_PRICE_TEMP" 
            + " ON CAST_PRICE.PRICE_ID = CAST_PRICE_TEMP.PRICE_ID"
            + " WHERE CAST_PRICE.PRICE_LIST = ?"
            + " AND CAST_PRICE_TEMP.PRICE_ID IS NULL)";

    public static String REMOVE_ECOTAX_INFO = 
            "DELETE FROM ECOTAX_INFO WHERE ECOTAX_ID IN "
            + "(SELECT ECOTAX_INFO.ECOTAX_ID"
            + " FROM ECOTAX_INFO LEFT OUTER JOIN CAST_PRICE_TEMP" 
            + " ON ECOTAX_INFO.ECOTAX_ID = CAST_PRICE_TEMP.PRICE_ID"
            + " WHERE ECOTAX_INFO.STORE_ID = ?"
            + " AND CAST_PRICE_TEMP.PRICE_ID IS NULL)";

    public static String REMOVE_PROMO_INFO = 
            "DELETE FROM PROMO_INFO WHERE PROMO_ID IN "
            + "(SELECT PROMO_INFO.PROMO_ID"
            + " FROM PROMO_INFO LEFT OUTER JOIN CAST_PRICE_TEMP" 
            + " ON PROMO_INFO.PROMO_ID = CAST_PRICE_TEMP.PRICE_ID"
            + " WHERE PROMO_INFO.STORE_ID = ?"
            + " AND CAST_PRICE_TEMP.PRICE_ID IS NULL)";

    public static String REMOVE_CLICK_COLLECT = 
            "DELETE FROM CLICK_COLLECT WHERE CC_ITEM_ID IN "
            + "(SELECT CLICK_COLLECT.CC_ITEM_ID"
            + " FROM CLICK_COLLECT LEFT OUTER JOIN FCC_TEMP" 
            + " ON CLICK_COLLECT.CC_ITEM_ID = FCC_TEMP.ID"
            + " WHERE CLICK_COLLECT.STORE_ID = ?"
            + " AND FCC_TEMP.ID IS NULL)";

    /**
     * Tables merging.
     */
    public static String MERGE_STORE_INFO_FROM_TEMP_SQL = 
            "MERGE INTO CAST_PRICE slp "
            + "USING ( "
            + "  SELECT PRICE_ID, PRICE_LIST, SKU_ID, LIST_PRICE "
            + "  FROM CAST_PRICE_TEMP "
            + "  ) slp_temp "
            + "ON (slp_temp.PRICE_ID = slp.PRICE_ID) "
            + "WHEN MATCHED THEN "
            + "  UPDATE SET slp.LIST_PRICE = slp_temp.LIST_PRICE "
            + "WHEN NOT MATCHED THEN "
            + "  INSERT (slp.PRICE_ID, slp.PRICE_LIST, slp.SKU_ID, slp.LIST_PRICE) "
            + "  VALUES (slp_temp.PRICE_ID, slp_temp.PRICE_LIST, slp_temp.SKU_ID, slp_temp.LIST_PRICE)";

    public static String MERGE_ECOTAX_INFO_FROM_TEMP_SQL = 
            "MERGE INTO ECOTAX_INFO slp "
            + "USING ( "
            + "  SELECT PRICE_ID, SKU_ID, STORE_ID, MD3E, MREP"
            + "  FROM CAST_PRICE_TEMP"
            + "  WHERE MD3E IS NOT NULL OR MREP IS NOT NULL"
            + "  ) slp_temp "
            + "ON (slp_temp.PRICE_ID = slp.ECOTAX_ID) "
            + "WHEN MATCHED THEN "
            + "  UPDATE SET slp.MD3E = slp_temp.MD3E,"
            + "             slp.MREP = slp_temp.MREP "
            + "WHEN NOT MATCHED THEN"
            + "  INSERT (slp.ECOTAX_ID, slp.SKU_ID, slp.STORE_ID, slp.MD3E, slp.MREP)"
            + "  VALUES (slp_temp.PRICE_ID, slp_temp.SKU_ID, slp_temp.STORE_ID, slp_temp.MD3E, slp_temp.MREP)";

    public static String MERGE_PROMO_INFO_FROM_TEMP_SQL = 
            "MERGE INTO PROMO_INFO slp "
            + "USING ( "
            + "  SELECT PRICE_ID, SKU_ID, STORE_ID, FPROMO, CPROMO"
            + "  FROM CAST_PRICE_TEMP"
            + "  ) slp_temp "
            + "ON (slp_temp.PRICE_ID = slp.PROMO_ID) "
            + "WHEN MATCHED THEN "
            + "  UPDATE SET slp.F_PROMO = slp_temp.FPROMO,"
            + "             slp.C_PROMO = slp_temp.CPROMO "
            + "WHEN NOT MATCHED THEN"
            + "  INSERT (slp.PROMO_ID, slp.STORE_ID, slp.SKU_ID, slp.F_PROMO, slp.C_PROMO)"
            + "  VALUES (slp_temp.PRICE_ID, slp_temp.STORE_ID, slp_temp.SKU_ID, slp_temp.FPROMO, slp_temp.CPROMO)";

    public static String MERGE_PROCESSING_FEE_FROM_TEMP_SQL = 
            "MERGE INTO PROCESSING_FEE slp "
            + "USING ( "
            + "  SELECT FEE_ID, STORE_ID, AMOUNT"
            + "  FROM CAST_PROCESSING_FEE_TEMP"
            + "  ) slp_temp "
            + "ON (slp_temp.FEE_ID = slp.FEE_ID) "
            + "WHEN MATCHED THEN "
            + "  UPDATE SET slp.AMOUNT = slp_temp.AMOUNT "
            + "WHEN NOT MATCHED THEN"
            + "  INSERT (slp.FEE_ID, slp.STORE_ID, slp.AMOUNT)"
            + "  VALUES (slp_temp.FEE_ID, slp_temp.STORE_ID, slp_temp.AMOUNT)";

    public static String MERGE_CLICK_COLLECT_FROM_TEMP_SQL = 
            "MERGE INTO CLICK_COLLECT slp "
            + "USING ( "
            + "  SELECT ID, STORE_ID, SKU_ID, FCC"
            + "  FROM FCC_TEMP"
            + "  ) slp_temp "
            + "ON (slp_temp.ID = slp.CC_ITEM_ID) "
            + "WHEN MATCHED THEN "
            + "  UPDATE SET slp.SKU_ID = slp_temp.SKU_ID"
            + "  DELETE WHERE slp_temp.FCC = 1 "
            + "WHEN NOT MATCHED THEN"
            + "  INSERT (slp.CC_ITEM_ID, slp.STORE_ID, slp.SKU_ID)"
            + "  VALUES (slp_temp.ID, slp_temp.STORE_ID, slp_temp.SKU_ID)"
            + "  WHERE slp_temp.FCC = 0";

    /**
     * Tables truncating.
     */
    public static String TRUNCATE_TEMP_TABLE_SQL = "TRUNCATE TABLE CAST_PRICE_TEMP REUSE STORAGE";
    public static String TRUNCATE_PROCESSING_FEE_TEMP_TABLE_SQL = "TRUNCATE TABLE CAST_PROCESSING_FEE_TEMP REUSE STORAGE";
    public static String TRUNCATE_FCC_TEMP_TABLE_SQL = "TRUNCATE TABLE FCC_TEMP REUSE STORAGE";

    /**
     * Tables dropping.
     */
    public static String DROP_TEMP_TABLE_SQL = "DROP TABLE CAST_PRICE_TEMP PURGE";
    public static String DROP_PROCESSING_FEE_TEMP_TABLE_SQL = "DROP TABLE CAST_PROCESSING_FEE_TEMP PURGE";
    public static String DROP_FCC_TEMP_TABLE_SQL = "DROP TABLE FCC_TEMP PURGE";

    /**
     * SELECT_STATUS_SQL property
     */
    private static String SELECT_STATUS_SQL =
            "SELECT ID FROM MS_LOCAL_PRICES_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    /**
     * INSERT_STATUS_SQL property
     */
    private static String INSERT_STATUS_SQL =
            "INSERT INTO MS_LOCAL_PRICES_SYNC_JOURNAL (STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS) VALUES(?, ?, ?, SYSDATE, ?)";

    /**
     * UPDATE_STATUS_SQL property
     */
    private static String UPDATE_STATUS_SQL =
            "UPDATE MS_LOCAL_PRICES_SYNC_JOURNAL SET ENTITY_NUMBER = ?, FILE_SEQ = ?, LAST_UPDATE = SYSDATE, STATUS = ? WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    /* Generated column id */
    private static final String GENERATED_COLUMNS[] = {"ID"};

    /**
     * UPDATE STATUSES
     */
    private static final int STATUS_OK = 0;
    private static final int STATUS_KO = 1;

    private DataSource mStockDataSource;
    private Repository mLocalStoresInfoRepository;
    private DataSource mDataSource;
    private LocalStoresUtils mLocalStoresUtils;

    /**
     * in progress flag property
     */
    private boolean mInProgress = false;

    /**
     * mBatchSize property
     */
    private int mBatchSize = 10000;
    private RepositoryInvalidationService mInvalidationService;
    private Map<String, String> mCodeArticleSkuIDMap;
    private Map<String, Store> mStoresPricesMap;

    /** Sets for storing processed items during import */
    private Set<String> processedPrices = new HashSet<String>();
    private Set<String> processedFCC = new HashSet<String>();

    /**
     * Connects to the queue and starts listening for messages.
     *
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        if (isLoggingDebug()) {
            vlogDebug("ImportLocalStoresInfoManager service started.");
        }
    }

    private boolean initialize() {
        boolean isException = false;
        try {
            mStoresPricesMap = mLocalStoresUtils.getLocalStoresMapping();
            mCodeArticleSkuIDMap = mLocalStoresUtils.getSkuCodeArticleMapping();
        } catch (RepositoryException e) {
            isException = true;
            clean();
            if (isLoggingError()) {
                vlogError("RepositoryException during initialization: {0}", e.getMessage());
            } else if (isLoggingDebug()) {
                logError("RepositoryException during initialization: ", e);
            }
        } catch (SQLException e) {
            isException = true;
            clean();
            if (isLoggingError()) {
                vlogError("SQLException during initialization: {0}", e.getMessage());
            } else if (isLoggingDebug()) {
                logError("SQLException during initialization: ", e);
            }
        } catch (Throwable e) {
            isException = true;
            clean();
            if (isLoggingError()) {
                vlogError("Exception during initialization: {0}", e.getMessage());
            } else if (isLoggingDebug()) {
                logError("Exception during initialization: ", e);
            }
        }
        return !isException;
    }

    private void clean() {
        if (mStoresPricesMap != null) {
            mStoresPricesMap.clear();
        }
        if (mCodeArticleSkuIDMap != null) {
            mCodeArticleSkuIDMap.clear();
        }
        processedPrices = new HashSet<String>();
        processedFCC = new HashSet<String>();
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     *
     * @throws ServiceException
     */
    public void doStopService() throws ServiceException {
        if (isLoggingDebug()) {
            vlogDebug("ImportLocalStoresInfoManager service stopped.");
        }
    }

    public void importLocalStoresInfo() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException(
                    "Unable to start local stores info import... The service is being executing. Please wait...");
        }

        if (isLoggingDebug()) {
            vlogDebug("start - ImportLocalStoresInfoManager - import local stores info");
        }
        boolean isSuccess = initialize();

        if (isSuccess) {

            createTempTable();
            createFCCTempTable();
            createFeeTempTable();

            try {
                mInProgress = true;

                File workingDir = getWorkingDir();
                checkMandatoryFolders();

                PrintWriter logWriter = null;
                File workingFile;
                File statusFile = null;
                while ((workingFile = getWorkingFile()) != null) {
                    if (isLoggingDebug()) {
                        logDebug("processing: " + workingFile);
                    }
                    long startTimeMillis = System.currentTimeMillis();

                    boolean success = true;
                    try {
                        statusFile = new File(workingDir, getFileName(workingFile) + ".log");
                        logWriter = new PrintWriter(statusFile);
                        if (isLoggingInfo()) {
                            logInfo("Data file: " + workingFile.getName());
                        }

                        processArchive(workingFile, logWriter);
                        if (isLoggingInfo()) {
                            logInfo("Done. File: " + workingFile.getName() + " Time (seconds): " + ((System.currentTimeMillis() - startTimeMillis) / 1000));
                        }

                    } catch (ZipException e) {
                        success = false;
                        if (isLoggingError()) {
                            logError(e);
                            logWriter.println(e);
                        }
                    } catch (IOException e) {
                        success = false;
                        if (isLoggingError()) {
                            logError(e);
                            if (logWriter != null) {
                                logWriter.println(e);
                            }
                        }
                    } finally {
                        if (logWriter != null) {
                            logWriter.close();
                        }
                    } // end try-catch-finally

                    File destDir;
                    if (success) {
                        destDir = getArchiveDir();
                    } else {
                        destDir = getErrorDir();
                    }
                    MiscUtils.copyFile(workingFile,
                            new File(destDir.getAbsolutePath() + File.separator
                                    + workingFile.getName()));
                    workingFile.delete();
                    MiscUtils.copyFile(statusFile,
                            new File(destDir.getAbsolutePath() + File.separator
                                    + statusFile.getName()));
                    statusFile.delete();
                }

            } catch (FileNotFoundException e) {
                if (isLoggingError()) {
                    vlogError(
                            e,
                            "Error in com.castorama.integration.lsp.common.ImportLocalStoresInfoManager.importLocalStoresInfo(): {0}",
                            e.getMessage());
                }
            } catch (IOException e) {
                if (isLoggingError()) {
                    vlogError(
                            e,
                            "Error in com.castorama.integration.lsp.common.ImportLocalStoresInfoManager.importLocalStoresInfo(): {0}",
                            e.getMessage());
                }
            } finally {
                invalidateCaches();
                
                dropTempTable();
                dropFeeTempTable();
                dropFCCTempTable();
                
                clean();
                
                mInProgress = false;
            }
        } else {
            if (isLoggingError()) {
                logError("Unable to continue import stores info ");
            }
        }
        if (isLoggingDebug()) {
            vlogDebug("finish - ImportLocalStoresInfoManager - import stores info");
        }
    }

    /**
     * Returns workingFile property.
     *
     * @return workingFile property.
     * @throws IOException - exception
     */
    private File getWorkingFile() throws IOException {
        File inputDir = new File(getRootFolder(), Constants.INPUT_FOLDER);
        File[] inpFiles = inputDir.listFiles();
        File inpFile = null;
        File workFile = null;
        if ((inpFiles != null) && (inpFiles.length > 0)) {
            for (File f : inpFiles) {
                if (f.isFile() && f.exists()) {
                    String path = f.getPath();
                    if (!path.endsWith(LOCK_EXTENSION)) {
                        int index = path.lastIndexOf(".");
                        String lockFileName = (index != -1) ? (path.substring(0, index) + LOCK_EXTENSION)
                                : (path + LOCK_EXTENSION);
                        File lockFile = new File(lockFileName);
                        if (lockFile.isFile() && lockFile.exists()) {
                            continue;
                        }
                        inpFile = f;
                        break;
                    }
                }
            }
        }
        if (inpFile != null) {
            String name = getFileName(inpFile);
            String workName = name
                    + "_"
                    + Constants.DATE_FORMAT_ARCHIVE.format(new Date())
                    + inpFile.getName().substring(name.length(),
                    inpFile.getName().length());
            workFile = new File(getWorkingDir(), workName);
            MiscUtils.copyFile(inpFile, workFile);
            inpFile.delete();
        }
        return workFile;
    }

    private void processZArchive(File workingFile, PrintWriter log)
            throws IOException {
        String tarFileName = workingFile.getName();
        tarFileName = tarFileName.substring(0, tarFileName.length() - 2);
        File tarFile = new File(getWorkingDir(), tarFileName);

        InputStream is = null;
        UncompressInputStream zIs = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(workingFile);
            zIs = new UncompressInputStream(is);
            os = new FileOutputStream(tarFile);
            byte[] buf = new byte[32768];
            int bytesRead;
            while ((bytesRead = zIs.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        } finally {
            if (zIs != null) {
                zIs.close();
            }
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        TarInputStream tarIS = null;
        FileInputStream fileInputStream = null;
        SizeLimiterInputStream slIS = null;

        try {
            fileInputStream = new FileInputStream(tarFile);
            tarIS = new TarInputStream(fileInputStream);
            TarEntry tarEntry;
            while ((tarEntry = tarIS.getNextEntry()) != null) {
                if (tarEntry.isDirectory()) {
                    continue;
                }
                long startTimeMillis = System.currentTimeMillis();
                if (isLoggingInfo()) {
                    vlogInfo("Start loading of {0}", tarEntry.getName());
                    log.println("Start loading of " + tarEntry.getName());
                }

                slIS = new SizeLimiterInputStream(tarIS, tarEntry.getSize());
                importLocalStorePrices(slIS, log);

                if (isLoggingInfo()) {
                    vlogInfo(
                            "{0} is loaded in {1} seconds",
                            tarEntry.getName(),
                            ((System.currentTimeMillis() - startTimeMillis) / 1000));
                    log.println(tarEntry.getName()
                            + " is loaded in "
                            + ((System.currentTimeMillis() - startTimeMillis) / 1000)
                            + " seconds");
                }
            }
        } finally {
            if (slIS != null) {
                slIS.close();
            }
            if (tarIS != null) {
                tarIS.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } // end try-finally
        tarFile.delete();
    }

    private void processArchive(File workingFile, PrintWriter log)
            throws IOException {
        if (workingFile == null) {
            if (isLoggingDebug()) {
                logDebug("Source file is null.");
            }
            return;
        }
        if (workingFile.getName().toUpperCase().endsWith(ARCHIVE_FORMAT_Z)) {
            processZArchive(workingFile, log);
        } else if (workingFile.getName().toUpperCase().endsWith(ARCHIVE_FORMAT_ZIP)) {
            processZipArchive(workingFile, log);
        } else {
            if (isLoggingError()) {
                vlogError("Unexpected file format: {0} . Only .Z and .ZIP archives are processed", workingFile.getName());
            }
        }
    }

    private void processZipArchive(File workingFile, PrintWriter log) throws IOException {
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(workingFile);
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry ze = enumeration.nextElement();

                if (ze.isDirectory()) {
                    continue;
                }

                long startTimeMillis = System.currentTimeMillis();
                if (isLoggingInfo()) {
                    logInfo("Start loading of " + ze.getName());
                    log.println("Start loading of " + ze.getName());
                }
                importLocalStorePrices(zipfile.getInputStream(ze), log);
                if (isLoggingInfo()) {
                    vlogInfo(
                            "{0} is loaded in {1} seconds",
                            ze.getName(),
                            ((System.currentTimeMillis() - startTimeMillis) / 1000));
                    log.println(ze.getName()
                            + " is loaded in "
                            + ((System.currentTimeMillis() - startTimeMillis) / 1000)
                            + " seconds");
                }
            }
        } finally {
            if (zipfile != null) {
                zipfile.close();
            }
        }
    }

    public void importLocalStorePrices(InputStream is, PrintWriter log) {
        Connection con = null;
        boolean processStore = false;
        String localPriceListId = null;

        Integer sequence = null;
        int itemCount = 0;
        Integer castoramaStoreId = null;
        String storeId = null;

        String line = null;
        boolean autoCommit = true;
        PreparedStatement ps = null;
        PreparedStatement psProcessingFee = null;
        PreparedStatement psProcessingFCC = null;
        boolean isFileStatusOK = true;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            con = getDataSource().getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            // load data into temp table
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int lineCount = 0;
            int lineCountFCC = 0;
            try {
                long startTimeMillis = System.currentTimeMillis();
                if (isLoggingDebug()) {
                    logDebug("Importing data to temporary table.");
                }
                ps = con.prepareStatement(INSERT_INTO_TEMP_TABLE_SQL);
                psProcessingFee = con.prepareStatement(INSERT_PROCESSING_FEE_TEMP_TABLE_SQL);
                psProcessingFCC = con.prepareStatement(INSERT_FCC_TEMP_TABLE_SQL);
                boolean newBatch = true;
                Store store = null;
                while ((line = br.readLine()) != null) {
                    String[] st = line.trim().split(";", -1);
                    if (newBatch) {
                        newBatch = false;
                    }
                    int importLineCode = Integer.parseInt(st[LINE_CODE_INDEX]);
                    switch (importLineCode) {
                        case HEADLINE: {
                            if (st.length == 6) {
                                try {
                                    sequence = Integer.valueOf(st[1]);
                                    castoramaStoreId = Integer.valueOf(st[2]);
                                    store = getStoresPricesMap().get(castoramaStoreId.toString());
                                    if (store != null) {
                                        storeId = store.getAtgStoreId();
                                        if (store.isRetraitMagasin() || store.isLocalPrix()) {
                                            localPriceListId = store.getLocalPriceList();
                                            if (st[3] != null && !StringUtils.isEmpty(st[3])) {
                                                psProcessingFee.setString(1, storeId);
                                                psProcessingFee.setString(2, storeId);
                                                psProcessingFee.setDouble(3, Double.parseDouble(st[3]));
                                                psProcessingFee.executeUpdate();
                                            }
                                            processStore = true;
                                            
                                            if (isLoggingError()) {
                                                if (!store.isLocalPrix()) {
                                                    logError("Store " + castoramaStoreId + " has no 'LocalPrix' flag.");
                                                    log.println("Store " + castoramaStoreId + " has no 'LocalPrix' flag.");
                                                } else if (!store.isRetraitMagasin()) {
                                                    logError("Store " + castoramaStoreId + " has no 'RetraitMagasin' flag.");
                                                    log.println("Store " + castoramaStoreId + " has no 'RetraitMagasin' flag.");
                                                }
                                            }
                                        } else {
                                            if (isLoggingError()) {
                                                logError("Store " + castoramaStoreId + " has neither 'RetraitMagasin' nor 'LocalPrix' flags.");
                                                log.println("Store " + castoramaStoreId + " has neither 'RetraitMagasin' nor 'LocalPrix' flags.");
                                            }
                                        }
                                    } else {
                                        if (isLoggingError()) {
                                            logError("Incorrect store id: " + castoramaStoreId);
                                            log.println("Incorrect store id: " + castoramaStoreId);
                                        }
                                        processStore = false;
                                        isFileStatusOK = false;
                                    }
                                } catch (NumberFormatException nfe) {
                                    processStore = false;
                                    isFileStatusOK = false;
                                    if (isLoggingError()) {
                                        vlogError("Incorrect sequence format: {0} \n {1}", line, nfe);
                                        log.println("Incorrect sequence format: " + line + "\n" + nfe);
                                    }
                                }
                            } else {
                                processStore = false;
                                isFileStatusOK = false;
                                if (isLoggingError()) {
                                    vlogError("Incorrect file header format: {0}", line);
                                    log.println("Incorrect file header format: " + line);
                                }
                            }
                            break;
                        }
                        case IMPORT_PRICES: {
                            if (sequence != null && store.isLocalPrix()) {
                                if (st.length == 8) {
                                    Integer codeArticle;
                                    Double localPrice;
                                    Double md3e = null;
                                    Double mrep = null;
                                    boolean fpromo = false;
                                    Integer cpromo = null;
                                    try {
                                        codeArticle = Integer.valueOf(st[1]);
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect codeArticle format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect codeArticle format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }
                                    try {
                                        if (st[2] != null && !StringUtils.isEmpty(st[2])) {
                                            localPrice = Double.valueOf(st[2]);
                                        } else {
                                            isFileStatusOK = false;
                                            if (isLoggingError()) {
                                                vlogError("Incorrect localPrice in line: " + line + " .Skipped.");
                                                log.println("Incorrect localPrice in line: " + line + " .Skipped.");
                                            }
                                            continue;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect local price format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect local price format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }

                                    try {
                                        if (st[3] != null && !StringUtils.isEmpty(st[3])) {
                                            md3e = Double.valueOf(st[3]);
                                        }
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect md3e format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect md3e format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }
                                    try {
                                        if (st[4] != null && !StringUtils.isEmpty(st[4])) {
                                            mrep = Double.valueOf(st[4]);
                                        }
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect mrep format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect mrep format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }
                                    try {
                                        if (st[5] != null && !StringUtils.isEmpty(st[5])) {
                                            fpromo = Integer.parseInt(st[5]) == 1;
                                        }
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect fpromo format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect fpromo format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }
                                    if (fpromo) {
                                        try {
                                            if (st[6] != null && !StringUtils.isEmpty(st[6])) {
                                                cpromo = Integer.valueOf(st[6]);
                                            }
                                        } catch (NumberFormatException nfe) {
                                            isFileStatusOK = false;
                                            if (isLoggingError()) {
                                                vlogError(nfe, "Incorrect cpromo format: {0} \n {1}", line, nfe.getMessage());
                                                log.println("Incorrect cpromo format: " + line + "\n" + nfe);
                                            }
                                            continue;
                                        }
                                    }
                                    String skuId = getSkuIdForCodeArticle(codeArticle.toString());
                                    if (skuId != null) {
                                        if (localPrice != null) {
                                            String itemId = localPriceListId + storeId + skuId;
                                            if (processedPrices.add(itemId)) {
                                                ps.setString(1, itemId);
                                                ps.setString(2, localPriceListId);
                                                ps.setString(3, skuId);
                                                ps.setString(4, storeId);
                                                ps.setDouble(5, localPrice);
                                                if (md3e == null) {
                                                    ps.setNull(6, Types.DOUBLE);
                                                } else {
                                                    ps.setDouble(6, md3e);
                                                }
                                                if (mrep == null) {
                                                    ps.setNull(7, Types.DOUBLE);
                                                } else {
                                                    ps.setDouble(7, mrep);
                                                }
                                                ps.setBoolean(8, fpromo);
                                                if (cpromo != null) {
                                                    ps.setInt(9, cpromo);
                                                } else {
                                                    ps.setNull(9, Types.INTEGER);
                                                }
                                                ps.addBatch();
                                                itemCount++;
                                                lineCount++;
                                            } else {
                                                if (isLoggingError()) {
                                                    logError("The record " + line + " in Local Price import file contains duplicate product price.");
                                                    log.println("The record " + line + " in Local Price import file contains duplicate product price.");
                                                }
                                            }
                                        }
                                    } else {
                                        if (isLoggingError()) {
                                            logError("Store " + castoramaStoreId + ". Code article " + st[1] + " was skipped because it does not exist in online catalogue.");
                                            log.println("Store " + castoramaStoreId + ". Code article " + st[1] + " was skipped because it does not exist in online catalogue.");
                                        }
                                    }
                                } else {
                                    isFileStatusOK = false;
                                    if (isLoggingWarning()) {
                                        vlogWarning("Unable to import data from line, incorrect line format: {0}", line);
                                        log.println("Unable to import data from line, incorrect line format: " + line);
                                    }
                                    continue;
                                }
                            }
                            break;
                        }
                        case IMPORT_FCC: {
                            if (sequence != null && store.isRetraitMagasin()) {
                                if (st.length == 4) {
                                    Integer codeArticle = null;
                                    boolean fcc = false;
                                    try {
                                        codeArticle = Integer.valueOf(st[1]);
                                    } catch (NumberFormatException nfe) {
                                        isFileStatusOK = false;
                                        if (isLoggingError()) {
                                            vlogError(nfe, "Incorrect codeArticle format: {0} \n {1}", line, nfe.getMessage());
                                            log.println("Incorrect codeArticle format: " + line + "\n" + nfe);
                                        }
                                        continue;
                                    }
                                    if (st[2] != null && !StringUtils.isEmpty(st[2]) && "1".equals(st[2])) {
                                        fcc = true;
                                    }
                                    String skuId = getSkuIdForCodeArticle(codeArticle.toString());
                                    if (skuId != null) {
                                        String itemId = storeId + skuId;
                                        if (processedFCC.add(itemId)) {
                                            psProcessingFCC.setString(1, itemId);
                                            psProcessingFCC.setString(2, storeId);
                                            psProcessingFCC.setString(3, skuId);
                                            psProcessingFCC.setBoolean(4, fcc);
                                            psProcessingFCC.addBatch();
                                            lineCountFCC++;
                                        } else {
                                            if (isLoggingError()) {
                                                logError("The record " + line + " in Local Price import file contains duplicate product FCC.");
                                                log.println("The record " + line + " in Local Price import file contains duplicate product FCC.");
                                            }
                                        }
                                    } else {
                                        if (isLoggingError()) {
                                            logError("Store " + castoramaStoreId + ". Code article " + st[1] + " was skipped because it does not exist in online catalogue.");
                                            log.println("Store " + castoramaStoreId + ". Code article " + st[1] + " was skipped because it does not exist in online catalogue.");
                                        }
                                    }
                                } else {
                                    isFileStatusOK = false;
                                    if (isLoggingWarning()) {
                                        vlogWarning("Unable to import data from line, incorrect line format: {0}", line);
                                        log.println("Unable to import data from line, incorrect line format: " + line);
                                    }
                                    continue;
                                }
                            }
                            break;
                        }
                        case END_OF_FILE: {
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    if (!processStore) {
                        break;
                    }
                    if (lineCount == getBatchSize() || (lineCount == (getBatchSize() - 1))) {
                        newBatch = true;
                        ps.executeBatch();
                        con.commit();
                        lineCount = 0;
                    }
                    if (lineCountFCC == getBatchSize() || (lineCountFCC == (getBatchSize() - 1))) {
                        newBatch = true;
                        psProcessingFCC.executeBatch();
                        con.commit();
                        lineCountFCC = 0;
                    }
                }
                if (lineCount > 0) {
                    ps.executeBatch();
                    con.commit();
                }
                if (lineCountFCC > 0) {
                    psProcessingFCC.executeBatch();
                    con.commit();
                }
                if (isLoggingInfo()) {
                    logInfo("Total items count: " + itemCount);
                }
                if (isLoggingDebug()) {
                    logDebug("Time (seconds): parsing data: " + ((System.currentTimeMillis() - startTimeMillis) / 1000));
                }
            } finally {
                closeStatement(ps);
                closeStatement(psProcessingFee);
                closeStatement(psProcessingFCC);
                con.commit();
            } // end try-finally

            if (storeId != null) {
                boolean exception = false;
                try {
                    try {
                        if (processStore) {
                            long startTimeMillis = System.currentTimeMillis();

                            ps = con.prepareStatement(REMOVE_CAST_PRICE);
                            ps.setString(1, localPriceListId);
                            ps.execute();

                            ps = con.prepareStatement(REMOVE_ECOTAX_INFO);
                            ps.setString(1, storeId);
                            ps.execute();

                            ps = con.prepareStatement(REMOVE_PROMO_INFO);
                            ps.setString(1, storeId);
                            ps.execute();

                            ps = con.prepareStatement(REMOVE_CLICK_COLLECT);
                            ps.setString(1, storeId);
                            ps.execute();

                            if (isLoggingDebug()) {
                                logDebug("Time (seconds): cleaning tables: " + ((System.currentTimeMillis() - startTimeMillis) / 1000));
                            }
                            startTimeMillis = System.currentTimeMillis();

                            ps = con.prepareStatement(MERGE_STORE_INFO_FROM_TEMP_SQL);
                            ps.execute();

                            ps = con.prepareStatement(MERGE_ECOTAX_INFO_FROM_TEMP_SQL);
                            ps.execute();

                            ps = con.prepareStatement(MERGE_PROMO_INFO_FROM_TEMP_SQL);
                            ps.execute();

                            ps = con.prepareStatement(MERGE_PROCESSING_FEE_FROM_TEMP_SQL);
                            ps.execute();

                            ps = con.prepareStatement(MERGE_CLICK_COLLECT_FROM_TEMP_SQL);
                            ps.execute();

                            if (isLoggingDebug()) {
                                logDebug("Time (seconds): merging tables: " + ((System.currentTimeMillis() - startTimeMillis) / 1000));
                            }
                        }
                    } finally {
                        closeStatement(ps);
                    }
                } catch (SQLException exc) {
                    exception = true;
                    throw exc;
                } finally {
                    if (exception) {
                        con.rollback();
                    } else {
                        con.commit();
                    }
                } // end try-catch-finally

                int status = STATUS_OK;
                if (!isFileStatusOK) {
                    status = STATUS_KO;
                }

                Integer statusImportId = createStatus(castoramaStoreId, itemCount, sequence, status);

                if (statusImportId != null) {
                    if (isLoggingDebug()) {
                        vlogDebug("Status for store {0} has been updated, current status: {1}", storeId, status);
                    }
                } else {
                    if (isLoggingDebug()) {
                        vlogDebug("Status for store {0} has not been updated.", storeId);
                    }
                }
            }
        } catch (IOException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from importLocalStorePrices method {0}", e.getMessage());
                log.println(e);
            }
        } catch (SQLException e) {
            if (isLoggingError()) {
                if (isLoggingError()) {
                    vlogError(e, "Error from importLocalStorePrices method {0}", e.getMessage());
                    log.println(e);
                }
            }
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e, "Error from importLocalStorePrices method {0}", e.getMessage());
                        log.println(e);
                    }
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e, "Error from importLocalStorePrices method {0}", e.getMessage());
                        log.println(e);
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    vlogError(e, "Unable to close BufferedReader {0}", e.getMessage());
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    vlogError(e, "Unable to close InputStreamReader {0}", e.getMessage());
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    vlogError(e, "Unable to close InputStream {0}", e.getMessage());
                }
            }

            truncateTempTables();
        } // end try-catch-finally
    }

    private void invalidateCaches() {
        try {
            Set<String> hSet = new HashSet<String>();
            getInvalidationService().invalidateRepository(getRepository(), PRICE_DESCRIPTOR_NAME, hSet);
            getInvalidationService().invalidateRepository(getLocalStoresInfoRepository(), ECOTAXE_INFO_DESCRIPTOR_NAME, hSet);
            getInvalidationService().invalidateRepository(getLocalStoresInfoRepository(), PROMO_INFO_DESCRIPTOR_NAME, hSet);
            getInvalidationService().invalidateRepository(getLocalStoresInfoRepository(), PROCESSING_FEE_DESCRIPTOR_NAME, hSet);
            getInvalidationService().invalidateRepository(getLocalStoresInfoRepository(), SKU_CLICK_COLLECT_FLAG_DESCRIPTOR_NAME, hSet);
        } catch (Throwable e) {
            String messagePrefix = "Exception during cache invalidation!: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    /**
     * Create temporary table for cast_price
     */
    private void createTempTable() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            vlogDebug("Creating temporary stores info table.");
        }

        try {
            con = getDataSource().getConnection();

            // check if temp table exists
            rs = con.getMetaData().getTables(null, null,
                    TEMP_TABLE_NAME, new String[]{"TABLE"});
            if (rs.next()) {
                // table exists
                try {
                    ps = con.prepareStatement(DROP_TEMP_TABLE_SQL);
                    ps.execute();
                } finally {
                    closeStatement(ps);
                }
            }

            ps = con.prepareStatement(CREATE_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from createTempTable method {0}",
                        e.getMessage());
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            vlogDebug("Temporary stores info table created.");
        }
    }

    /**
     * Create temporary table for processing_fee
     */
    private void createFeeTempTable() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating fee temporary table.");
        }

        try {
            con = getDataSource().getConnection();

            // check if temp table exists
            rs = con.getMetaData().getTables(null, null,
                    PROCESSING_FEE_TEMP_TABLE, new String[]{"TABLE"});
            if (rs.next()) {
                // table exists
                try {
                    ps = con.prepareStatement(DROP_PROCESSING_FEE_TEMP_TABLE_SQL);
                    ps.execute();
                } finally {
                    closeStatement(ps);
                }
            }

            ps = con.prepareStatement(CREATE_PROCESSING_FEE_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from createFeeTempTable method {0}",
                        e.getMessage());
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            vlogDebug("Temporary fee table created.");
        }
    }

    /**
     * Create temporary table for C&C flags
     */
    private void createFCCTempTable() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating FCC temporary table.");
        }

        try {
            con = getDataSource().getConnection();

            // check if temp table exists
            rs = con.getMetaData().getTables(null, null,
                    FCC_TEMP_TABLE, new String[]{"TABLE"});
            if (rs.next()) {
                // table exists
                try {
                    ps = con.prepareStatement(DROP_FCC_TEMP_TABLE_SQL);
                    ps.execute();
                } finally {
                    closeStatement(ps);
                }
            }
            ps = con.prepareStatement(CREATE_FCC_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from createFCCTempTable method {0}", e.getMessage());
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            vlogDebug("Temporary FCC table created.");
        }
    }

    /**
     * Truncate temporary tables
     */
    private void truncateTempTables() {
        Connection con = null;
        PreparedStatement ps = null;

        if (isLoggingDebug()) {
            vlogDebug("Truncating temporary tables.");
        }

        try {
            con = getDataSource().getConnection();
            try {
                ps = con.prepareStatement(TRUNCATE_TEMP_TABLE_SQL);
                ps.execute();
            } finally {
                closeStatement(ps);
            }
            try {
                ps = con.prepareStatement(TRUNCATE_PROCESSING_FEE_TEMP_TABLE_SQL);
                ps.execute();
            } finally {
                closeStatement(ps);
            }
            try {
                ps = con.prepareStatement(TRUNCATE_FCC_TEMP_TABLE_SQL);
                ps.execute();
            } finally {
                closeStatement(ps);
            }
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from truncateTempTables method {0}", e.getMessage());
            }
        } finally {
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            vlogDebug("Temporary tables truncated.");
        }
    }

    /**
     * Drop cast_price temporary table
     */
    private void dropTempTable() {
        Connection con = null;
        PreparedStatement ps = null;

        if (isLoggingDebug()) {
            vlogDebug("Dropping temporary stores info table.");
        }

        try {
            con = getDataSource().getConnection();
            ps = con.prepareStatement(DROP_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Error from dropTempTable method {0}", e.getMessage());
            }
        } finally {
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            vlogDebug("Temporary stores info table dropped.");
        }
    }

    /**
     * Drop processing fee temporary table
     */
    private void dropFeeTempTable() {
        Connection con = null;
        PreparedStatement ps = null;
        if (isLoggingDebug()) {
            vlogDebug("Dropping fee temporary table.");
        }
        try {
            con = getDataSource().getConnection();
            ps = con.prepareStatement(DROP_PROCESSING_FEE_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary fee table dropped.");
        }
    }

    /**
     * Drop C&C flags temporary table
     */
    private void dropFCCTempTable() {
        Connection con = null;
        PreparedStatement ps = null;

        if (isLoggingDebug()) {
            vlogDebug("Dropping FCC temporary table.");
        }

        try {
            con = getDataSource().getConnection();
            ps = con.prepareStatement(DROP_FCC_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            closeStatement(ps);
            closeConnection(con);
        } // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary FCC table dropped.");
        }
    }

    private Integer createStatus(Integer store, Integer entityNumber, Integer fileSequence, Integer status) {
        Integer generatedId = null;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating local prices import status.");
        }

        try {
            con = getStockDataSource().getConnection();

            ps = con.prepareStatement(SELECT_STATUS_SQL);
            ps.setInt(1, store);
            rs = ps.executeQuery();
            if (rs.next()){
                generatedId = rs.getInt(1);
                rs.close();
                ps.close();
                ps = con.prepareStatement(UPDATE_STATUS_SQL);
                ps.setInt(1, entityNumber);
                ps.setInt(2, fileSequence);
                ps.setInt(3, status);
                ps.setInt(4, store);
                ps.executeUpdate();
            } else {
                rs.close();
                ps.close();
                ps = con.prepareStatement(INSERT_STATUS_SQL, GENERATED_COLUMNS);
                ps.setInt(1, store);
                ps.setInt(2, entityNumber);
                ps.setInt(3, fileSequence);
                ps.setInt(4, status);
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(con);
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Local prices import status created.");
        }

        return generatedId;
    }

    private String getSkuIdForCodeArticle(String pCodeArticle) {
        String skuId = null;
        if (getCodeArticleSkuIDMap().containsKey(pCodeArticle)) {
            skuId = getCodeArticleSkuIDMap().get(pCodeArticle);
        }
        return skuId;
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    vlogError(e, "Unable to close result set {0}", e.getMessage());
                }
            }
        }
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    vlogError(e, "Unable to close statement {0}", e.getMessage());
                }
            }
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    vlogError(e, "Unable to close connection {0}", e.getMessage());
                }
            }
        }
    }

    public Map<String, String> getCodeArticleSkuIDMap() {
        return mCodeArticleSkuIDMap;
    }

    public void setCodeArticleSkuIDMap(Map<String, String> pCodeArticleSkuIDMap) {
        mCodeArticleSkuIDMap = pCodeArticleSkuIDMap;
    }

    public Repository getLocalStoresInfoRepository() {
        return mLocalStoresInfoRepository;
    }

    public void setLocalStoresInfoRepository(
            Repository pLocalStoresInfoRepository) {
        mLocalStoresInfoRepository = pLocalStoresInfoRepository;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource pDataSource) {
        mDataSource = pDataSource;
    }

    public int getBatchSize() {
        return mBatchSize;
    }

    public void setBatchSize(int pBatchSize) {
        mBatchSize = pBatchSize;
    }

    public Map<String, Store> getStoresPricesMap() {
        return mStoresPricesMap;
    }

    public void setStoresPricesMap(Map<String, Store> pStoresPricesMap) {
        mStoresPricesMap = pStoresPricesMap;
    }

    public RepositoryInvalidationService getInvalidationService() {
        return mInvalidationService;
    }

    public void setInvalidationService(
            RepositoryInvalidationService pInvalidationService) {
        mInvalidationService = pInvalidationService;
    }

    public DataSource getStockDataSource() {
        return mStockDataSource;
    }

    public void setStockDataSource(DataSource mStockDataSource) {
        this.mStockDataSource = mStockDataSource;
    }

    public LocalStoresUtils getLocalStoresUtils() {
        return mLocalStoresUtils;
    }

    public void setLocalStoresUtils(LocalStoresUtils mLocalStoresUtils) {
        this.mLocalStoresUtils = mLocalStoresUtils;
    }
}
