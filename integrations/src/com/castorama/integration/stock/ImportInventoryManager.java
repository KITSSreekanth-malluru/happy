package com.castorama.integration.stock;

import static com.castorama.utils.CastUtils.buildSVSProductId;

import atg.nucleus.ServiceException;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import com.castorama.stockvisualization.RemoteStockCacheAccessor;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class ImportInventoryManager extends IntegrationBase implements Schedulable {
    /**
     * LOCK_EXTENSION property
     */
    private static final String LOCK_EXTENSION = ".lock";

    public static String ARCHIVE_FORMAT_ZIP = ".ZIP";

    public static String ARCHIVE_FORMAT_Z = ".Z";

    /**
     * INSERT_INTO_TEMP_TABLE_SQL property
     */
    public static final String INSERT_INTO_TEMP_TABLE_SQL =
            "insert into MS_INVENTORY_TEMP (PRODUCT_ID, STORE_ID, STOCK_AVAILABLE, FILE_SEQ, STOCK_SNAPSHOT_DATE, CREATED_DATE, UPDATED_DATE)" +
                    " values(?,?,?,?,?,sysdate,sysdate)";

    /**
     * CREATE_TEMP_TABLE_SQL property
     */
    public static String CREATE_TEMP_TABLE_SQL =
            "  CREATE TABLE MS_INVENTORY_TEMP " + "(PRODUCT_ID VARCHAR2(40 BYTE), " + "STORE_ID NUMBER(4,0), " +
                    "STOCK_SNAPSHOT_DATE DATE," + "FILE_SEQ NUMBER(9,0), " + "CREATED_DATE DATE, " + "UPDATED_DATE DATE, " +
                    "STOCK_AVAILABLE NUMBER(12,2))";

    /**
     * TEMP_TABLE_NAME property
     */
    public static String TEMP_TABLE_NAME = "MS_INVENTORY_TEMP";

    /**
     * DROP_TEMP_TABLE_SQL property
     */
    public static String DROP_TEMP_TABLE_SQL = "DROP TABLE MS_INVENTORY_TEMP PURGE";

    /**
     * DELETE_STORE_INFO_SQL property
     */
    public static String DELETE_STORE_INFO_SQL = "DELETE MS_INVENTORY WHERE STORE_ID = ? ";

    /**
     * MERGE_STORE_INFO_FROM_TEMP_SQL property
     */
    public static String MERGE_STORE_INFO_FROM_TEMP_SQL =
            "MERGE INTO ms_inventory inv " + "USING ( " +
                    "SELECT PRODUCT_ID, STORE_ID, STOCK_AVAILABLE, FILE_SEQ, STOCK_SNAPSHOT_DATE, CREATED_DATE, UPDATED_DATE " +
                    "FROM ms_inventory_temp " + " ) inv_temp " +
                    "ON (inv_temp.STORE_ID = inv.STORE_ID and inv_temp.PRODUCT_ID = inv.PRODUCT_ID) " + "WHEN MATCHED THEN " +
                    "UPDATE SET inv.STOCK_AVAILABLE = inv_temp.STOCK_AVAILABLE," +
                    "inv.FILE_SEQ = inv_temp.FILE_SEQ, " +
                    "inv.STOCK_SNAPSHOT_DATE = inv_temp.STOCK_SNAPSHOT_DATE, " +
                    "inv.UPDATED_DATE = inv_temp.UPDATED_DATE " + "WHEN NOT MATCHED THEN " +
                    "INSERT (inv.PRODUCT_ID, inv.STORE_ID, inv.STOCK_AVAILABLE, inv.FILE_SEQ, inv.STOCK_SNAPSHOT_DATE, inv.CREATED_DATE, inv.UPDATED_DATE) " +
                    "VALUES (inv_temp.PRODUCT_ID, inv_temp.STORE_ID, inv_temp.STOCK_AVAILABLE, inv_temp.FILE_SEQ, inv_temp.STOCK_SNAPSHOT_DATE, inv_temp.CREATED_DATE, inv_temp.UPDATED_DATE)";

//    /**
//     * DELETE_PRODUCTS_SQL property
//     */
//    public static String DELETE_PRODUCTS_SQL =
//            "DELETE MS_INVENTORY inv WHERE STORE_ID = ? and not exists (select 1 from MS_INVENTORY_TEMP inv_temp where inv_temp.PRODUCT_ID = inv.PRODUCT_ID)";

    /* Generated column id */
    private static final String GENERATED_COLUMNS[] = {"ID"};

    /**
     * SELECT_STATUS_SQL property
     */
    private static String SELECT_STATUS_SQL =
            "SELECT ID FROM MS_INVENTORY_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    /**
     * INSERT_STATUS_SQL property
     */
    private static String INSERT_STATUS_SQL =
            "INSERT INTO MS_INVENTORY_SYNC_JOURNAL (STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS) VALUES(?, ?, ?, SYSDATE, ?)";

    /**
     * UPDATE_STATUS_SQL property
     */
    private static String UPDATE_STATUS_SQL =
            "UPDATE MS_INVENTORY_SYNC_JOURNAL SET ENTITY_NUMBER = ?, FILE_SEQ = ?, LAST_UPDATE = SYSDATE, STATUS = ? WHERE TO_CHAR(LAST_UPDATE, 'DD/MM/YYYY') = TO_CHAR(SYSDATE, 'DD/MM/YYYY') AND STORE_ID = ?";

    /**
     * DELETE_OLD_STATUSES_SQL property
     */
    private static String DELETE_OLD_STATUSES_SQL =
            "DELETE FROM MS_INVENTORY_SYNC_JOURNAL WHERE (SYSDATE - LAST_UPDATE) > ?";

    /**
     * DELETE_OLD_ERRORS_SQL property
     */
    private static String DELETE_OLD_ERRORS_SQL =
            "DELETE FROM MS_STOCK_SERVICE_ERR_LOG WHERE (SYSDATE - ERROR_DATE) > ?";

    /**
     * UPDATE STATUSES
     */
    private static final int STATUS_OK = 0;
    private static final int STATUS_KO = 1;

    /**
     * workingDir property
     */
    private File workingDir;

    /**
     * archiveDir property
     */
    private File archiveDir;

    /**
     * errorDir property
     */
    private File errorDir;

    /**
     * mDatePattern property
     */
    private String mDatePattern;

    /**
     * mDataSource property
     */
    private DataSource mDataSource;

    /**
     * mBatchSize property
     */
    private int mBatchSize = 10000;

    private String archiveFormat;

    /**
     * mBatchSize property
     */
    private int mKeepInArchiveDays = 20;

    /**
     * mKeepInDb property
     */
    private int mKeepInDb = 365;

    /**
     * scheduler property
     */
    private Scheduler scheduler;

    /**
     * schedule property
     */
    private Schedule schedule;

    /**
     * jobId property
     */
    int jobId;

    /**
     * remoteStockCacheAccessor property
     */
    private RemoteStockCacheAccessor mRemoteStockCacheAccessor;

    /**
     * Gets the remoteStockCacheAccessor property.
     *
     * @return the remoteStockCacheAccessor
     */
    public final RemoteStockCacheAccessor getRemoteStockCacheAccessor() {
        return mRemoteStockCacheAccessor;
    }

    /**
     * Sets the remoteStockCacheAccessor property.
     *
     * @param pRemoteStockCacheAccessor the remoteStockCacheAccessor to set
     */
    public final void setRemoteStockCacheAccessor(
            RemoteStockCacheAccessor pRemoteStockCacheAccessor) {
        mRemoteStockCacheAccessor = pRemoteStockCacheAccessor;
    }

    /**
     * castoramaStoreId property
     */
    private int castoramaStoreId;

    /**
     * Gets the castoramaStoreId property.
     *
     * @return the castoramaStoreId
     */
    public int getCastoramaStoreId() {
        return castoramaStoreId;
    }

    /**
     * Sets the castoramaStoreId property.
     *
     * @param castoramaStoreId the castoramaStoreId to set
     */
    public void setCastoramaStoreId(int castoramaStoreId) {
        this.castoramaStoreId = castoramaStoreId;
    }

    /**
     * in progress flag property
     */
    private boolean mInProgress = false;

    /**
     * The schedule this service will run on
     *
     * @return the schedule this service will run on
     */
    public Schedule getSchedule() {
        return this.schedule;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param schedule the schedule this service will run on
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * This service's scheduler
     *
     * @return this service's scheduler
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param scheduler this service's scheduler
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Connects to the queue and starts listening for messages.
     *
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job =
                new ScheduledJob("ImportInventoryManager", "Load inventory from stores", getAbsoluteName(), getSchedule(),
                        this, ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);

        if (isLoggingDebug()) {
            logDebug("ImportInventoryManager service started. Job id : " + jobId);
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     *
     * @throws ServiceException ToDo: DOCUMENT ME!
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(jobId);

        if (isLoggingDebug()) {
            logDebug("ImportInventoryManager service stopped. Job id : " + jobId);
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    public void importInventory() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException("Unable to start inventory import... The service is being executing. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ImportInventoryManager - ImportInventory");
        }

        try {
            mInProgress = true;

            File workingDir = getWorkingDir();
            checkMandatoryFolders();

            PrintWriter logWriter = null;
            File workingFile = null;
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
                        logInfo("Done. File: " + workingFile.getName() + " Time (seconds): "
                                + ((System.currentTimeMillis() - startTimeMillis) / 1000));
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
                        logWriter.println(e);
                    }
                } finally {
                    if (logWriter != null) {
                        logWriter.close();
                    }
                }  // end try-catch-finally

                File destDir = null;
                if (success) {
                    destDir = getArchiveDir();
                } else {
                    destDir = getErrorDir();
                }
                MiscUtils.copyFile(workingFile,
                        new File(destDir.getAbsolutePath() + File.separator + workingFile.getName()));
                workingFile.delete();
                MiscUtils.copyFile(statusFile,
                        new File(destDir.getAbsolutePath() + File.separator + statusFile.getName()));
                statusFile.delete();
            }
            deleteOldFiles(getArchiveDir());
            deleteOldFiles(getErrorDir());

        } catch (FileNotFoundException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            mInProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ImportInventoryManager - ImportInventory");
        }
    }

    public void cleanDb() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException("Unable to start database cleaning... The service is being executing. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ImportInventoryManager - cleanDb");
        }

        mInProgress = true;

        Connection connection = null;
        PreparedStatement deleteStatement = null;
        try {
            connection = mDataSource.getConnection();
            deleteStatement = connection.prepareStatement(DELETE_OLD_STATUSES_SQL);
            deleteStatement.setInt(1, mKeepInDb);
            deleteStatement.execute();

            close(deleteStatement);

            deleteStatement = connection.prepareStatement(DELETE_OLD_ERRORS_SQL);
            deleteStatement.setInt(1, mKeepInDb);
            deleteStatement.execute();
        } catch (SQLException exception) {
            if (isLoggingError()) {
                logError(exception);
            }
        } finally {
            close(deleteStatement);
            close(connection);

            mInProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ImportInventoryManager - cleanDb");
        }
    }

    public void cleanAllInventoryForStoreId() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException("Unable to start inventory cleaning... The service is being executing. Please wait...");
        }
        
        if (getCastoramaStoreId() <= 0) {
            throw new ServiceException("Unable to start inventory cleaning... Please set the correct castoramaStoreId field.");
        }

        if (isLoggingDebug()) {
            logDebug("start - ImportInventoryManager - cleanAllInventoryForStoreId");
        }

        mInProgress = true;

        int storeId = getCastoramaStoreId();
        setCastoramaStoreId(0);

        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = mDataSource.getConnection();
            ps = con.prepareStatement(DELETE_STORE_INFO_SQL);
            ps.setInt(1, storeId);
            ps.execute();
        } catch (SQLException exception) {
            if (isLoggingError()) {
                logError(exception);
            }
        } finally {
            close(ps);
            close(con);

            mInProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ImportInventoryManager - cleanAllInventoryForStoreId");
        }
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
            }
        }
    }

    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
            }
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
                        String lockFileName =
                                (index != -1) ? (path.substring(0, index) + LOCK_EXTENSION) : (path + LOCK_EXTENSION);
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
            String workName =
                    name + "_" + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) +
                            inpFile.getName().substring(name.length(), inpFile.getName().length());
            workFile = new File(getWorkingDir(), workName);

            MiscUtils.copyFile(inpFile, workFile);

            inpFile.delete();
        }
        return workFile;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param workingFile ToDo: DOCUMENT ME!
     * @param log         ToDo: DOCUMENT ME!
     * @throws IOException
     * @throws ZipException
     */
    private void processArchive(File workingFile, PrintWriter log) throws ZipException, IOException {
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
                logError("Unexpected file format: " + workingFile.getName() + ". Only .Z and .ZIP archives are processed");
            }
            return;
        }
    }

    private void processZArchive(File workingFile, PrintWriter log) throws FileNotFoundException, IOException {
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
                    logInfo("Start loading of " + tarEntry.getName());
                    log.println("Start loading of " + tarEntry.getName());
                }

                slIS = new SizeLimiterInputStream(tarIS, tarEntry.getSize());
                processStore(slIS, log);

                if (isLoggingInfo()) {
                    logInfo(tarEntry.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
                    log.println(tarEntry.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
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
        }  // end try-finally
        tarFile.delete();
    }

    private void processZipArchive(File workingFile, PrintWriter log) throws FileNotFoundException, IOException {
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
                processStore(zipfile.getInputStream(ze), log);
                if (isLoggingInfo()) {
                    logInfo(ze.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
                    log.println(ze.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
                }
            }
        } finally {
            if (zipfile != null) {
                zipfile.close();
            }
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param is  ToDo: DOCUMENT ME!
     * @param log ToDo: DOCUMENT ME!
     */
    private void processStore(InputStream is, PrintWriter log) {
        Connection con = null;

        Integer statusImportId = null;

        Integer sequence = null;
        int itemCount = 0;
        Integer storeId = null;

        SimpleDateFormat sdf = new SimpleDateFormat(getDatePattern());

        createTempTable();

        String line;
        boolean autoCommit = true;
        PreparedStatement ps = null;
        boolean wrongFile = false;
        boolean isFileStatusOK = true;
        List<String> codeArticlesToInvalidate = new ArrayList<String>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            con = getDataSource().getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            //load data into temp table
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            Date extractionDate = null;
            int lineCount = 0;

            try {
                if (isLoggingDebug()) {
                    logDebug("Importing data to temporary table.");
                }

                ps = con.prepareStatement(INSERT_INTO_TEMP_TABLE_SQL);
                while ((line = br.readLine()) != null && !wrongFile) {
                    StringTokenizer st = new StringTokenizer(line.trim(), ";");
                    int count = st.countTokens();

                    if (count == 3) {
                        try {
                            sequence = Integer.valueOf(st.nextToken());
                            storeId = Integer.valueOf(st.nextToken());
                            extractionDate = sdf.parse(st.nextToken());
                        } catch (NumberFormatException nfe) {
                            wrongFile = true;
                            isFileStatusOK = false;
                            if (isLoggingError()) {
                                logError("Incorrect sequence or storeId format: " + line + "\n" + nfe);
                                log.println("Incorrect sequence or storeId format: " + line + "\n" + nfe);
                            }
                        } catch (ParseException e) {
                            wrongFile = true;
                            isFileStatusOK = false;
                            if (isLoggingError()) {
                                logError("Incorrect date format: " + line + "\n" + e);
                                log.println("Incorrect date format: " + line + "\n" + e);
                            }
                        }
                        continue;
                    } else if (count == 2) {
                        if (sequence != null) {
                            boolean wrongData = false;
                            Float stockAvailable = null;
                            String productId = buildSVSProductId(st.nextToken());
                            try {
                                String str = st.nextToken().trim();
                                str = str.replace(',', '.');
                                stockAvailable = Float.parseFloat(str);
                            } catch (NumberFormatException nfe) {
                                wrongData = true;
                                isFileStatusOK = false;
                                if (isLoggingError()) {
                                    logError("Incorrect stock theoretical/reserved format: " + line + "\n" + nfe);
                                    log.println("Incorrect theoretical/reserved format: " + line + "\n" + nfe);
                                }
                            }

                            if (!wrongData) {
                                codeArticlesToInvalidate.add(productId);
                                try {
                                    ps.setString(1, productId);
                                    ps.setInt(2, storeId);
                                    ps.setFloat(3, stockAvailable);
                                    ps.setInt(4, sequence);
                                    ps.setDate(5, new java.sql.Date(extractionDate.getTime()));

                                    ps.addBatch();

                                    lineCount++;
                                    itemCount++;

                                } catch (SQLException sqlException) {
                                    isFileStatusOK = false;
                                    if (isLoggingWarning()) {
                                        logWarning("Unable to import data from line: " + line, sqlException);
                                        log.println("Unable to import data from line: " + line + "Error: " + sqlException);
                                    }
                                }
                            } else {
                                if (isLoggingWarning()) {
                                    logWarning("Unable to import data from line: " + line);
                                    log.println("Unable to import data from line: " + line);
                                }
                            }

                        } else {
                            wrongFile = true;
                            isFileStatusOK = false;
                            if (isLoggingError()) {
                                logError("Incorrect extraction format: missing header.");
                                log.println("Incorrect extraction format: missing header.");
                            }
                        }  // end if-else
                    } else if (count == 1 && sequence.compareTo(Integer.valueOf(st.nextToken())) == 0) {
                        if (isLoggingDebug()) {
                            logDebug("End of file with sequence: " + sequence);
                        }
                    } else {
                        isFileStatusOK = false;
                        if (isLoggingError()) {
                            logError("Incorrect number of parameters in string: " + line);
                            log.println("Incorrect number of parameters in string: " + line);
                        }
                    }  // end if-else

                    if (lineCount == getBatchSize()) {
                        ps.executeBatch();
                        con.commit();
                        lineCount = 0;
                    }
                }
                if (lineCount > 0) {
                    ps.executeBatch();
                    con.commit();
                }
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException e) {
                    }
                    ps = null;
                }
            }  // end try-finally

            if (storeId != null && !wrongFile) {

                boolean exception = false;
                try {
//                    if (isLoggingDebug()) {
//                        logDebug("Deleting old data");
//                    }
//
//                    try {
//                        ps = con.prepareStatement(DELETE_PRODUCTS_SQL);
//                        ps.setInt(1, storeId);
//                        ps.execute();
//                    } finally {
//                        if (ps != null) {
//                            try {
//                                ps.close();
//                            } catch (SQLException e) {
//                            }
//                            ps = null;
//                        }
//                    }

                    if (isLoggingDebug()) {
                        logDebug("Merging data");
                    }

                    try {
                        ps = con.prepareStatement(MERGE_STORE_INFO_FROM_TEMP_SQL);
                        ps.execute();
                    } finally {
                        if (ps != null) {
                            try {
                                ps.close();
                            } catch (SQLException e) {
                            }
                            ps = null;
                        }
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
                }  // end try-catch-finally
            }
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
                log.println(e);
            }
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
                log.println(e);
            }
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
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
                        logError(e);
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
        }  // end try-catch-finally

        dropTempTable();

        int status = STATUS_OK;
        if (!isFileStatusOK) {
            status = STATUS_KO;
        }

        statusImportId = createStatus(storeId, itemCount, sequence, status);

        if (statusImportId != null) {
            if (isLoggingDebug()) {
                vlogDebug("Status for store {0} has been updated, current status: {1}", storeId, status);
            }
        } else {
            if (isLoggingDebug()) {
                vlogDebug("Status for store {0} has not been updated.", storeId);
            }
        }

        if (storeId != null && codeArticlesToInvalidate.size() > 0) {
            getRemoteStockCacheAccessor().invalidateCache(storeId, codeArticlesToInvalidate);
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    private void createTempTable() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating temporary table.");
        }

        try {
            con = getDataSource().getConnection();

            //check if temp table exists
            rs = con.getMetaData().getTables(null, null, TEMP_TABLE_NAME, new String[]{"TABLE"});
            if (rs.next()) {
//                rs.close();

                //table exists

                try {
                    rs.close();
                } catch (SQLException e) {
                }

                try {
                    ps = con.prepareStatement(DROP_TEMP_TABLE_SQL);
                    ps.execute();
                } finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) {
                            if (isLoggingError()) {
                                vlogError(e, "Error from createFCCTempTable method {0}", e.getMessage());
                            }
                        }
                    }
                }
            }

            ps = con.prepareStatement(CREATE_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }

        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary table created.");
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     */
    private void dropTempTable() {
        Connection con = null;
        PreparedStatement ps = null;

        if (isLoggingDebug()) {
            logDebug("Dropping temporary table.");
        }

        try {
            con = getDataSource().getConnection();
            ps = con.prepareStatement(DROP_TEMP_TABLE_SQL);
            ps.execute();
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary table dropped.");
        }
    }

    private Integer createStatus(Integer store, Integer entityNumber, Integer fileSequence, Integer status) {
        Integer generatedId = null;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (isLoggingDebug()) {
            logDebug("Creating inventory import status.");
        }

        try {
            con = getDataSource().getConnection();

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
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Inventory import status created.");
        }

        return generatedId;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param dir ToDo: DOCUMENT ME!
     */
    private void deleteOldFiles(File dir) {
        if (isLoggingDebug()) {
            logDebug("Deleting old files from " + dir.getName());
        }

        File[] listFiles = dir.listFiles();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -getKeepInArchiveDays());
        long timeInMillis = calendar.getTimeInMillis();

        if ((listFiles != null) && (listFiles.length > 0)) {
            for (File f : listFiles) {
                if (f.lastModified() < timeInMillis) {
                    f.delete();
                }
            }
        }

    }

    /**
     * Returns dataSource property.
     *
     * @return dataSource property.
     */
    public DataSource getDataSource() {
        return mDataSource;
    }

    /**
     * Sets the value of the dataSource property.
     *
     * @param dataSource parameter to set.
     */
    public void setDataSource(DataSource dataSource) {
        mDataSource = dataSource;
    }

    /**
     * Returns datePattern property.
     *
     * @return datePattern property.
     */
    public String getDatePattern() {
        return mDatePattern;
    }

    /**
     * Sets the value of the datePattern property.
     *
     * @param datePatterns parameter to set.
     */
    public void setDatePattern(String datePatterns) {
        mDatePattern = datePatterns;
    }

    /**
     * Returns batchSize property.
     *
     * @return batchSize property.
     */
    public int getBatchSize() {
        return mBatchSize;
    }

    /**
     * Sets the value of the batchSize property.
     *
     * @param batchSize parameter to set.
     */
    public void setBatchSize(int batchSize) {
        mBatchSize = batchSize;
    }

    /**
     * Returns keepInArchiveDays property.
     *
     * @return keepInArchiveDays property.
     */
    public int getKeepInArchiveDays() {
        return mKeepInArchiveDays;
    }

    /**
     * Sets the value of the keepInArchiveDays property.
     *
     * @param keepInArchiveDays parameter to set.
     */
    public void setKeepInArchiveDays(int keepInArchiveDays) {
        mKeepInArchiveDays = keepInArchiveDays;
    }

    public int getKeepInDb() {
        return mKeepInDb;
    }

    public void setKeepInDb(int pKeepInDb) {
        mKeepInDb = pKeepInDb;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param scheduler ToDo: DOCUMENT ME!
     * @param job       ToDo: DOCUMENT ME!
     */
    public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
        if (isLoggingInfo()) {
            logInfo("Perform sheduled task. Job name : " + job.getJobName());
        }
        try {
            importInventory();
            cleanDb();
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }
}
