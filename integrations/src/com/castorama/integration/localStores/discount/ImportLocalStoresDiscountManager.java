package com.castorama.integration.localStores.discount;

import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.repository.*;

import com.castorama.core.RepositoryInvalidationService;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.localStores.common.Store;
import com.castorama.integration.localStores.util.LocalStoresUtils;
import com.castorama.stockvisualization.RemoteStockCacheAccessor;
import com.castorama.integration.util.MiscUtils;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: Epam team
 * Date: 10/29/12
 * Time: 4:54 PM
 */
public class ImportLocalStoresDiscountManager extends IntegrationBase {

    /**
     * LOCK_EXTENSION property
     */
    private static final String LOCK_EXTENSION = ".lock";

    public static String ARCHIVE_FORMAT_CSV = ".CSV";

    /**
     * INSERT_INTO_TEMP_TABLE_SQL property
     */
    public static final String INSERT_INTO_TEMP_TABLE_SQL =
            "insert into DISCOUNT_TEMP (DISCOUNT_ID, STORE_ID, SKU_ID, DISPLAY_DISCOUNT_BEGIN_DATE, DISPLAY_DISCOUNT_END_DATE, DISPLAY_DISCOUNT)" +
                    " values(?,?,?,?,?,?)";

    /**
     * CREATE_TEMP_TABLE_SQL property
     */
    public static String CREATE_TEMP_TABLE_SQL =
            "  CREATE TABLE DISCOUNT_TEMP(" +
                    "DISCOUNT_ID VARCHAR2(40) NOT NULL," +
                    "STORE_ID VARCHAR2(40) NOT NULL," +
                    "SKU_ID VARCHAR2(40) NOT NULL," +
                    "DISPLAY_DISCOUNT_BEGIN_DATE DATE NOT NULL," +
                    "DISPLAY_DISCOUNT_END_DATE DATE NOT NULL," +
                    "DISPLAY_DISCOUNT NUMBER(1,0) NULL," +
                    "PRIMARY KEY (DISCOUNT_ID)" +
                    ")";

    /**
     * TEMP_TABLE_NAME property
     */
    public static String TEMP_TABLE_NAME = "DISCOUNT_TEMP";

    /**
     * DROP_TEMP_TABLE_SQL property
     */
    public static String DROP_TEMP_TABLE_SQL = "DROP TABLE DISCOUNT_TEMP PURGE";

    /**
     * MERGE_STORE_INFO_FROM_TEMP_SQL property
     */
    public static String MERGE_DISCOUNT_FROM_TEMP_SQL =
            "MERGE INTO DISCOUNT disc USING ( " +
                    "SELECT DISCOUNT_ID, STORE_ID, SKU_ID, DISPLAY_DISCOUNT_BEGIN_DATE, DISPLAY_DISCOUNT_END_DATE, DISPLAY_DISCOUNT " +
                    "FROM DISCOUNT_TEMP WHERE DISPLAY_DISCOUNT = 1) disc_temp " +
                    "ON (disc_temp.DISCOUNT_ID = disc.DISCOUNT_ID) WHEN MATCHED THEN " +
                    "UPDATE SET disc.STORE_ID = disc_temp.STORE_ID," +
                    "disc.SKU_ID = disc_temp.SKU_ID, " +
                    "disc.DISPLAY_DISCOUNT_BEGIN_DATE = disc_temp.DISPLAY_DISCOUNT_BEGIN_DATE, " +
                    "disc.DISPLAY_DISCOUNT_END_DATE = disc_temp.DISPLAY_DISCOUNT_END_DATE, " +
                    "disc.DISPLAY_DISCOUNT = disc_temp.DISPLAY_DISCOUNT WHEN NOT MATCHED THEN " +
                    "INSERT (disc.DISCOUNT_ID,disc.STORE_ID,disc.SKU_ID, disc.DISPLAY_DISCOUNT_BEGIN_DATE, disc.DISPLAY_DISCOUNT_END_DATE, disc.DISPLAY_DISCOUNT) " +
                    "VALUES (disc_temp.DISCOUNT_ID,disc_temp.STORE_ID, disc_temp.SKU_ID, disc_temp.DISPLAY_DISCOUNT_BEGIN_DATE, disc_temp.DISPLAY_DISCOUNT_END_DATE, disc_temp.DISPLAY_DISCOUNT)";

    /**
     * DELETE_PRODUCTS_SQL property
     */
    public static String DELETE_PRODUCTS_SQL =
            "DELETE FROM DISCOUNT disc WHERE disc.DISCOUNT_ID IN" +
                    "(select disc_temp.DISCOUNT_ID from DISCOUNT_TEMP disc_temp WHERE disc_temp.DISPLAY_DISCOUNT = 0)";


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
     * mBatchSize property
     */
    private int mKeepInArchiveDays = 20;

    /**
     * remoteStockCacheAccessor property
     */
    private RemoteStockCacheAccessor mRemoteStockCacheAccessor;

    private Map<String, String> mCodeArticleSkuIDMap;
    private Map<String, Store> mStoresPricesMap;
    private Repository mStoreRepository;
    private Repository mLocalPricesRepository;
    private Repository mLocalStoresInfoRepository;
    private DataSource mDataSource;
    private RepositoryInvalidationService mInvalidationService;
    private LocalStoresUtils mLocalStoresUtils;

    public LocalStoresUtils getLocalStoresUtils() {
        return mLocalStoresUtils;
    }

    public void setLocalStoresUtils(LocalStoresUtils mLocalStoresUtils) {
        this.mLocalStoresUtils = mLocalStoresUtils;
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

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }

    public Repository getLocalPricesRepository() {
        return mLocalPricesRepository;
    }

    public void setLocalPricesRepository(Repository pLocalPricesRepository) {
        mLocalPricesRepository = pLocalPricesRepository;
    }

    public Repository getLocalStoresInfoRepository() {
        return mLocalStoresInfoRepository;
    }

    public void setLocalStoresInfoRepository(
            Repository pLocalStoresInfoRepository) {
        mLocalStoresInfoRepository = pLocalStoresInfoRepository;
    }

    public Map<String, String> getCodeArticleSkuIDMap() {
        return mCodeArticleSkuIDMap;
    }

    public void setCodeArticleSkuIDMap(Map<String, String> pCodeArticleSkuIDMap) {
        mCodeArticleSkuIDMap = pCodeArticleSkuIDMap;
    }

    private String getSkuIdForCodeArticle(String pCodeArticle) {
        String skuId = null;
        if (getCodeArticleSkuIDMap().containsKey(pCodeArticle)) {
            skuId = getCodeArticleSkuIDMap().get(pCodeArticle);
        }
        return skuId;
    }


    /**
     * in progress flag property
     */
    private boolean mInProgress = false;

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
                vlogError("Exception during initialization: {0} ", e.getMessage());
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
    }

    private boolean isStoreSkuHaveLocalPrice(String storeAtgId, String priceList, String skuId) {
        try {
            RepositoryItemDescriptor repositoryItemDescriptor = getLocalPricesRepository().getItemDescriptor("price");
            RepositoryView repositoryView = repositoryItemDescriptor.getRepositoryView();
            QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
            Query getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                    queryBuilder.createComparisonQuery(
                            queryBuilder.createPropertyQueryExpression("priceList"),
                            queryBuilder.createConstantQueryExpression(priceList),
                            QueryBuilder.EQUALS),
                    queryBuilder.createComparisonQuery(
                            queryBuilder.createPropertyQueryExpression("skuId"),
                            queryBuilder.createConstantQueryExpression(skuId),
                            QueryBuilder.EQUALS)
            });
            RepositoryItem[] repositoryItems = repositoryView.executeQuery(getStoreQuery);
            if (repositoryItems == null) {
                return false;
            }
            
            repositoryItemDescriptor = getLocalStoresInfoRepository().getItemDescriptor("promoInfo");
            repositoryView = repositoryItemDescriptor.getRepositoryView();
            queryBuilder = repositoryView.getQueryBuilder();
            getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                    queryBuilder.createComparisonQuery(
                            queryBuilder.createPropertyQueryExpression("storeId"),
                            queryBuilder.createConstantQueryExpression(storeAtgId),
                            QueryBuilder.EQUALS),
                    queryBuilder.createComparisonQuery(
                            queryBuilder.createPropertyQueryExpression("skuId"),
                            queryBuilder.createConstantQueryExpression(skuId),
                            QueryBuilder.EQUALS)
            });
            repositoryItems = repositoryView.executeQuery(getStoreQuery);
            if (repositoryItems != null) {
                Boolean result = (Boolean) repositoryItems[0].getPropertyValue("fPromo");
                if (result == null)
                    result = false;
                return result;
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                vlogError(re, "RepositoryException: {0}", re.getMessage());
            }
        }
        return false;
    }

    public void importDiscount() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException("Unable to start discount import... The service is being executing. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ImportLocalStoresDiscountManager - importDiscount");
        }
        boolean isSuccess = initialize();
        if (isSuccess) {
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

                        processCSV(workingFile, logWriter);
                        if (isLoggingInfo()) {
                            logInfo("Done. File: " + workingFile.getName() + " Time (seconds): "
                                    + ((System.currentTimeMillis() - startTimeMillis) / 1000));
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

                    File destDir;
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
                clean();
                mInProgress = false;
            }
        } else {
            if (isLoggingError()) {
                logError("Unable to continue display discount import");
            }
        }

        if (isLoggingDebug()) {
            logDebug("finish - ImportLocalStoresDiscountManager - importDiscount");
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
                        String lockFileName = (index != -1) ? (path.substring(0, index) + LOCK_EXTENSION) : (path + LOCK_EXTENSION);
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
                    name + "_" + Constants.DATE_FORMAT_CSV.format(new java.util.Date()) +
                            inpFile.getName().substring(name.length(), inpFile.getName().length());
            workFile = new File(getWorkingDir(), workName);
            MiscUtils.copyFile(inpFile, workFile);
            inpFile.delete();
        }
        return workFile;
    }

    private void processCSV(File workingFile, PrintWriter log) throws IOException {
        if (workingFile == null) {
            if (isLoggingDebug()) {
                logDebug("Source file is null.");
            }
            return;
        }
        if (workingFile.getName().toUpperCase().endsWith(ARCHIVE_FORMAT_CSV)) {
            processFile(workingFile, log);
        } else {
            if (isLoggingError()) {
                logError("Unexpected file format: " + workingFile.getName() + ". Only .CSV files are processed");
            }
        }
    }

    private void processFile(File workingFile, PrintWriter log) throws IOException {
        FileInputStream outputStream = null;
        try {

            long startTimeMillis = System.currentTimeMillis();
            if (isLoggingInfo()) {
                logInfo("Start loading of " + workingFile.getName());
                log.println("Start loading of " + workingFile.getName());
            }
            outputStream = new FileInputStream(workingFile);
            processDiscount(outputStream, log);
            if (isLoggingInfo()) {
                logInfo(workingFile.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
                log.println(workingFile.getName() + " is loaded in " + ((System.currentTimeMillis() - startTimeMillis) / 1000) + " seconds");
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void processDiscount(InputStream is, PrintWriter log) {
        Connection con = null;
        String line;
        Set<String> itemToInvalidateIds = new HashSet<String>();
        SimpleDateFormat sdf = new SimpleDateFormat(getDatePattern());
        createTempTable();
        PreparedStatement ps = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            java.util.Date currentTime = new java.util.Date();
            Date currentDate = new Date(sdf.parse(sdf.format(currentTime)).getTime());
            con = getDataSource().getConnection();
            con.setAutoCommit(false);
            //load data into temp table
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            int lineCount = 0;
            if (isLoggingDebug()) {
                logDebug("Importing data to temporary table.");
            }

            ps = con.prepareStatement(INSERT_INTO_TEMP_TABLE_SQL);
            while ((line = br.readLine()) != null) {
                String[] currentRow = line.split(",");
                boolean wrongData = false;
                Date beginDate = null;
                Date endDate = null;
                Integer displayDiscount = 1;
                String storeId = null;
                String skuId = null;
                // if line was empty or ",,,," currentRow will be empty string array and next expressions will throw exception
                if (currentRow.length > 1) {
                    storeId = currentRow[0];
                    skuId = getSkuIdForCodeArticle(currentRow[1].trim());
                }
                if (!StringUtils.isBlank(storeId) && !StringUtils.isBlank(skuId)) {
                    Store store = getStoresPricesMap().get(storeId);
                    if (store != null) {
                        if (!store.isLocalPrix()) {
                            if (isLoggingError()) {
                                logError("There is no localPrice flag for store: " + storeId);
                                log.println("There is no localPrice flag for store: " + storeId);
                            }
                            continue;
                        } else if (!isStoreSkuHaveLocalPrice(store.getAtgStoreId(), store.getLocalPriceList(), skuId)) {
                            if (isLoggingError()) {
                                logError("There is no local price or fPromo==false for store " + storeId + " for sku with id " + skuId);
                                log.println("There is no local price or fPromo==false for store " + storeId + " for sku with id " + skuId);
                            }
                            continue;
                        }
                        try {
                            if (currentRow.length == 5 && !StringUtils.isBlank(currentRow[4])) {
                                displayDiscount = Integer.parseInt(currentRow[4]);
                            } else {
                                displayDiscount = 1;
                            }
                            
                            if (displayDiscount == 1) {
                                // if displayDiscount field empty and endDate or both dates are empty
                                if (currentRow.length < 4) {
                                    wrongData = true;
                                } else {
                                    beginDate = new Date(sdf.parse(currentRow[2]).getTime());
                                    endDate = new Date(sdf.parse(currentRow[3]).getTime());
                                    if (((endDate.getTime() < beginDate.getTime()) || (endDate.getTime() < currentDate.getTime()))) {
                                        wrongData = true;
                                    }
                                }
                                if (wrongData == true) {
                                    if (isLoggingError()) {
                                        logError("Incorrect begin/end date in line: " + line);
                                        log.println("Incorrect begin/end date in line: " + line);
                                    }
                                }
                           } else {
                               beginDate = currentDate;
                               endDate = currentDate;
                           }
                        } catch (NumberFormatException nfe) {
                            wrongData = true;
                            if (isLoggingError()) {
                                logError("Invalid format of display discount flag in line: " + line + "\n" + nfe);
                                log.println("Invalid format of display discount flag in line: " + line + "\n" + nfe);
                            }
                        } catch (ParseException pe) {
                            wrongData = true;
                            if (isLoggingError()) {
                                logError("Incorrect date format in line: " + line + "\n" + pe);
                                log.println("Incorrect date format in line: " + line + "\n" + pe);
                            }
                        }
                    } else {
                        wrongData = true;
                        if (isLoggingError()) {
                            logError("Incorrect or invalid format of store_id in line: " + line);
                            log.println("Incorrect or invalid format of store_id in line: " + line);
                        }
                    }
                    String itemId;
                    if (!wrongData) {
                        try {
                            itemId = store.getAtgStoreId() + skuId;
                            ps.setString(1, itemId);
                            itemToInvalidateIds.add(itemId);
                            ps.setString(2, store.getAtgStoreId());
                            ps.setString(3, skuId);
                            ps.setDate(4, beginDate);
                            ps.setDate(5, endDate);
                            ps.setInt(6, displayDiscount);
                            ps.executeUpdate();
                            lineCount++;
                        } catch (SQLException sqlException) {
                            if (isLoggingError()) {
                                logError("Unable to import data from line (perhaps duplicate rows is present): " + line);
                                log.println("Unable to import data from line (perhaps duplicate rows is present): " + line);
                            } else if (isLoggingDebug()) {
                                logError("Unable to import data from line (perhaps duplicate rows is present): "
                                        + line, sqlException);
                            }
                        } finally {
                            con.commit();
                        }
                    } else {
                        if (isLoggingError()) {
                            logError("Unable to import data due to previous error from line: " + line);
                            log.println("Unable to import data due to previous error from line: " + line);
                        }
                    }// end if-else
                } else {
                    if (isLoggingError()) {
                        logError("Unable to import data from line: " + line + ". Incorrect line format.");
                        log.println("Unable to import data from line: " + line + ". Incorrect line format.");
                    }
                }
            }

            closeStatement(ps);

            if (lineCount > 0) {
                boolean exception = false;
                try {
                    if (isLoggingDebug()) {
                        logDebug("Deleting data");
                    }
                    try {
                        ps = con.prepareStatement(DELETE_PRODUCTS_SQL);
                        ps.execute();
                    } finally {
                        closeStatement(ps);
                    }
                    if (isLoggingDebug()) {
                        logDebug("Merging data");
                    }
                    try {
                        ps = con.prepareStatement(MERGE_DISCOUNT_FROM_TEMP_SQL);
                        ps.execute();
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
        } catch (ParseException e) {
            if (isLoggingError()) {
                logError(e);
                log.println(e);
            }
        } finally {
            closeStatement(ps);
            closeConnection(con);
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

            dropTempTable();
            invalidateDiscountsCaches(itemToInvalidateIds);
        }  // end try-catch-finally
    }

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
                //table exists

                try {
                    rs.close();
                } catch (SQLException e) {
                }

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
                logError(e);
            }
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
            closeConnection(con);
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary table created.");
        }
    }

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
            closeStatement(ps);
            closeConnection(con);
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Temporary table dropped.");
        }
    }

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

    private void invalidateDiscountsCaches(
            Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(getLocalStoresInfoRepository(), "discount", pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during discount cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                vlogError(messagePrefix + e.getMessage());
            }
        }
    }
}
