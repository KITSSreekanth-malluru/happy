package com.castorama.integration.localStores.cleanup;

import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import com.castorama.core.RepositoryInvalidationService;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.localStores.common.Store;
import com.castorama.integration.localStores.util.LocalStoresUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Epam team
 * Date: 11/16/12
 * Time: 1:59 AM
 */

public class LocalStoresCleanupManager extends IntegrationBase{

    public static String DELETE_CAST_PRICE =
            "DELETE FROM CAST_PRICE WHERE PRICE_LIST NOT IN" +
                    " (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)";

    public static String SELECT_CAST_PRICE =
            "SELECT PRICE_ID FROM CAST_PRICE WHERE PRICE_LIST NOT IN" +
                    " (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)";

    public static String DELETE_ECOTAX_INFO =
            "DELETE FROM ECOTAX_INFO WHERE ECOTAX_ID IN (" +
                    "SELECT DISTINCT ECOTAX.ECOTAX_ID FROM ECOTAX_INFO ECOTAX  " +
                    "INNER JOIN (SELECT PRICE_LIST, SKU_ID FROM CAST_PRICE WHERE PRICE_LIST NOT IN " +
                    "  (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)) PLS " +
                    "ON PLS.SKU_ID = ECOTAX.SKU_ID AND ECOTAX.STORE_ID IN " +
                    "  (SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE PRICE_LIST_ID=PLS.PRICE_LIST))";

    public static String DELETE_PROMO_INFO =
            "DELETE FROM PROMO_INFO WHERE PROMO_ID IN (" +
                    "SELECT DISTINCT PROMO.PROMO_ID FROM PROMO_INFO PROMO " +
                    " INNER JOIN (SELECT PRICE_LIST, SKU_ID FROM CAST_PRICE WHERE PRICE_LIST NOT IN " +
                    "  (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)) PLS " +
                    " ON PLS.SKU_ID = PROMO.SKU_ID AND PROMO.STORE_ID IN " +
                    " (SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE PRICE_LIST_ID=PLS.PRICE_LIST)) ";

    public static String SELECT_ECOTAX_INFO =
            "SELECT DISTINCT ECOTAX.ECOTAX_ID FROM ECOTAX_INFO ECOTAX " +
                    "INNER JOIN (SELECT PRICE_LIST, SKU_ID FROM CAST_PRICE WHERE PRICE_LIST NOT IN " +
                    "  (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)) PLS " +
                    "ON PLS.SKU_ID = ECOTAX.SKU_ID AND ECOTAX.STORE_ID IN " +
                    "  (SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE PRICE_LIST_ID=PLS.PRICE_LIST)";

    public static String SELECT_PROMO_INFO =
            "SELECT DISTINCT PROMO.PROMO_ID FROM PROMO_INFO PROMO " +
                    " INNER JOIN (SELECT PRICE_LIST, SKU_ID FROM CAST_PRICE WHERE PRICE_LIST NOT IN " +
                    "  (SELECT PRICE_LIST_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1)) PLS " +
                    " ON PLS.SKU_ID = PROMO.SKU_ID AND PROMO.STORE_ID IN" +
                    "  (SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE PRICE_LIST_ID=PLS.PRICE_LIST) ";

    public static String DELETE_DISCOUNT =
            "DELETE FROM DISCOUNT WHERE STORE_ID NOT IN ("
                    + "  SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1 "
                    + ") OR DISPLAY_DISCOUNT_END_DATE < ?";

    public static String DELETE_FCC =
            "DELETE FROM CLICK_COLLECT WHERE STORE_ID NOT IN (" +
                    "SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE RETRAIT_MAGASIN = 1" +
                    ")";

    public static String SELECT_FCC_FOR_CLEANUP =
            "SELECT CC_ITEM_ID FROM CLICK_COLLECT WHERE STORE_ID NOT IN ( " +
                    "SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE RETRAIT_MAGASIN = 1)";

    public static String SELECT_DISCOUNT_FOR_CLEANUP =
            "SELECT DISCOUNT_ID FROM DISCOUNT WHERE STORE_ID NOT IN ("
                    + "  SELECT DISTINCT STORE_ID FROM LOCAL_PRICE_TO_STORE WHERE LOCAL_PRIX = 1 "
                    + ") OR DISPLAY_DISCOUNT_END_DATE < ?";

    public static String TRUNCATE_LOCAL_PRICE_TO_STORE_TABLE = "TRUNCATE TABLE LOCAL_PRICE_TO_STORE";

    public static String INSERT_INTO_LOCAL_PRICE_TO_STORE = "INSERT INTO LOCAL_PRICE_TO_STORE " +
            "(PRICE_LIST_ID, STORE_ID, RETRAIT_MAGASIN, LOCAL_PRIX) VALUES (?,?,?,?)";

    private Repository mLocalPricesRepository;
    private Repository mLocalStoresInfoRepository;
    private DataSource mDataSource;
    private LocalStoresUtils mLocalStoresUtils;

    /**
     * in progress flag property
     */
    private boolean mInProgress = false;

    /**
     * mInvalidationService property
     */
    private RepositoryInvalidationService mInvalidationService;
    private Collection<Store> localStores;


    /**
     * Connects to the queue and starts listening for messages.
     *
     * @throws atg.nucleus.ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        if (isLoggingDebug()) {
            logDebug("LocalStoresCleanupManager service started.");
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     *
     * @throws ServiceException If a service exception occurs.
     */
    public void doStopService() throws ServiceException {
        if (isLoggingDebug()) {
            logDebug("LocalStoresCleanupManager service stopped.");
        }
    }

    private boolean initialize() {
        boolean isException = false;
        try {
            localStores = mLocalStoresUtils.getLocalStores();
        } catch (RepositoryException e) {
            isException = true;
            if (isLoggingError()) {
                vlogError("RepositoryException during initialization: {0}", e.getMessage());
            } else if (isLoggingDebug()) {
                logError("RepositoryException during initialization: ", e);
            }
        } catch (Throwable e) {
            isException = true;
            if (isLoggingError()) {
                vlogError("Exception during initialization: {0}", e.getMessage());
            } else if (isLoggingDebug()) {
                logError("Exception during initialization: ", e);
            }
        }
        return !isException;
    }

    private void clean() {
        if (localStores != null) {
            localStores.clear();
        }
    }

    public void cleanup() throws ServiceException {
        if (mInProgress) {
            throw new ServiceException(
                    "Unable to start local prices cleanup service for all accounts! The service is already started. Please wait...");
        }
        mInProgress = true;
        try {
            boolean isSuccess = initialize();
            if (isSuccess) {
                clearLocalPriceToStoreTable();
                initializeLocalPriceToStoreTable();
                cleanupLocalPrices();
                cleanupDisplayDiscount();
                cleanupClickCollect();
                clearLocalPriceToStoreTable();
            } else {
                if (isLoggingError()) {
                    logError("Unable to start cleanup service.");
                }
            }
        } finally {
            clean();
            mInProgress = false;
        }
    }

    private void cleanupLocalPrices() {

        //Begin local prices cleanup
        if (isLoggingDebug()) {
            logDebug("Start local prices cleanup");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        HashSet<String> localPriceIdsForCleanup = new HashSet<String>();
        HashSet<String> localPromoIdsForCleanup = new HashSet<String>();
        HashSet<String> localEcotaxIdsForCleanup = new HashSet<String>();
        String priceId, ecotaxId, promoId;
        try {
            con = getConnection();
            ps = con.prepareStatement(SELECT_CAST_PRICE);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                priceId = resultSet.getString(1);
                localPriceIdsForCleanup.add(priceId);
                if (isLoggingInfo()) {
                    logInfo("Local price with id " + priceId + " has been been deleted from database.");
                }
            }

            closeResultSet(resultSet);
            closeStatement(ps);

            ps = con.prepareStatement(SELECT_ECOTAX_INFO);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ecotaxId = resultSet.getString(1);
                localEcotaxIdsForCleanup.add(ecotaxId);
                if (isLoggingInfo()) {
                    logInfo("Local ecotax with id " + ecotaxId + " has been been deleted from database.");
                }
            }

            closeResultSet(resultSet);
            closeStatement(ps);

            ps = con.prepareStatement(SELECT_PROMO_INFO);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                promoId = resultSet.getString(1);
                localPromoIdsForCleanup.add(promoId);
                if (isLoggingInfo()) {
                    logInfo("Local promotion with id " + promoId + " has been been deleted from database.");
                }
            }

            closeResultSet(resultSet);
            closeStatement(ps);

            if (localPromoIdsForCleanup.size() > 0) {
                ps = con.prepareStatement(DELETE_PROMO_INFO);
                ps.executeUpdate();
                con.commit();

                closeStatement(ps);
            }
            if (localEcotaxIdsForCleanup.size() > 0) {
                ps = con.prepareStatement(DELETE_ECOTAX_INFO);
                ps.executeUpdate();
                con.commit();

                closeStatement(ps);
            }
            if (localPriceIdsForCleanup.size() > 0) {
                ps = con.prepareStatement(DELETE_CAST_PRICE);
                ps.executeUpdate();
                con.commit();

                closeStatement(ps);
            }
        } catch (SQLException e) {
            performCommitOrRollback(con, false);
            if (isLoggingError()) {
                vlogError(e, "SQLException in cleanupLocalPrices method {0}", e.getMessage());
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(ps);
            closeConnection(con);

            invalidateLocalStoresEcotaxeInfoCaches(localEcotaxIdsForCleanup);
            invalidateLocalStoresPromoInfoCaches(localPromoIdsForCleanup);
            invalidateLocalStoresPriceCaches(localPriceIdsForCleanup);
        }

        if (isLoggingDebug()) {
            logDebug("End local prices cleanup");
        }
        //End local prices cleanup

    }

    private void cleanupDisplayDiscount() {

        //Begin display discount cleanup
        if (isLoggingDebug()) {
            logDebug("Start display cleanup");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        boolean isException = false;
        HashSet<String> idsForCleanup = new HashSet<String>();
        String discountId;
        Date currentDate = getCurrentDate();
        try {
            con = getConnection();
            ps = con.prepareStatement(SELECT_DISCOUNT_FOR_CLEANUP);
            ps.setDate(1, currentDate);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                discountId = resultSet.getString(1);
                idsForCleanup.add(discountId);
                if (isLoggingInfo()) {
                    logInfo("Display discount with id " + discountId + " has been been deleted from database.");
                }
            }

            closeResultSet(resultSet);
            closeStatement(ps);

            if (idsForCleanup.size() > 0) {
                ps = con.prepareStatement(DELETE_DISCOUNT);
                ps.setDate(1, currentDate);
                ps.executeUpdate();

                closeStatement(ps);
            }

        } catch (SQLException e) {
            isException = true;
            if (isLoggingError()) {
                vlogError(e, "SQLException in cleanupDisplayDiscount method {0}", e.getMessage());
            }
        } finally {
            performCommitOrRollback(con, !isException);

            closeResultSet(resultSet);
            closeStatement(ps);
            closeConnection(con);

            invalidateDiscountCaches(idsForCleanup);
        }

        if (isLoggingDebug()) {
            logDebug("End display cleanup");
        }
        //End display discount cleanup

    }

    private void cleanupClickCollect() {

        //Begin FCC cleanup
        if (isLoggingDebug()) {
            logDebug("Start FCC cleanup");
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        boolean isException = false;
        HashSet<String> idsForCleanup = new HashSet<String>();
        String fccId;
        try {
            con = getConnection();
            ps = con.prepareStatement(SELECT_FCC_FOR_CLEANUP);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                fccId = resultSet.getString(1);
                idsForCleanup.add(fccId);
                if (isLoggingInfo()) {
                    logInfo("FCC with id " + fccId + " has been been deleted from database.");
                }
            }

            closeResultSet(resultSet);
            closeStatement(ps);

            if (idsForCleanup.size() > 0) {
                ps = con.prepareStatement(DELETE_FCC);
                ps.executeUpdate();

                closeStatement(ps);
            }
        } catch (SQLException e) {
            isException = true;
            if (isLoggingError()) {
                vlogError(e, "SQLException in importLocalStorePrices method {0}", e.getMessage());
            }
        } finally {
            performCommitOrRollback(con, !isException);

            closeResultSet(resultSet);
            closeStatement(ps);
            closeConnection(con);

            invalidateFccLocalStoresCaches(idsForCleanup);
        }

        if (isLoggingDebug()) {
            logDebug("End FCC cleanup");
        }
        //End FCC cleanup
    }

    private void initializeLocalPriceToStoreTable() {
        //Begin initialize local_price_to_store table
        if (isLoggingDebug()) {
            logDebug("Start initialize local_price_to_store table");
        }

        Connection con = null;
        PreparedStatement ps = null;
        boolean isException = false;
        try {
            con = getConnection();
            ps = con.prepareStatement(INSERT_INTO_LOCAL_PRICE_TO_STORE);
            for (Store store : localStores) {
                ps.setString(1, store.getLocalPriceList());
                ps.setString(2, store.getAtgStoreId());
                ps.setBoolean(3, store.isRetraitMagasin());
                ps.setBoolean(4, store.isLocalPrix());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            isException = true;
            if (isLoggingError()) {
                vlogError(e, "SQLException in initializeLocalPriceToStoreTable method {0}", e.getMessage());
            }
        } finally {
            performCommitOrRollback(con, !isException);
            closeStatement(ps);
            closeConnection(con);
        }

        if (isLoggingDebug()) {
            logDebug("End initialize local_price_to_store table");
        }
        //End truncate local_price_to_store table
    }


    private void clearLocalPriceToStoreTable() {
        //Begin truncate local_price_to_store table
        if (isLoggingDebug()) {
            logDebug("Start truncate local_price_to_store table");
        }

        Connection con = null;
        Statement s = null;
        try {
            con = getConnection();
            s = con.createStatement();
            s.executeUpdate(TRUNCATE_LOCAL_PRICE_TO_STORE_TABLE);
            con.commit();
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "SQLException in truncateLocalPriceToStoreTable method {0}", e.getMessage());
            }
        } finally {
            closeStatement(s);
            closeConnection(con);
        }

        if (isLoggingDebug()) {
            logDebug("End truncate local_price_to_store table");
        }
        //End truncate local_price_to_store table
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

    private void performCommitOrRollback(Connection connection, boolean isCommit) {
        if (isCommit) {
            try {
                connection.commit();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    vlogError(e, "Unable to commit changes in database {0}", e.getMessage());
                }
            }
        } else {
            try {
                connection.rollback();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    vlogError(e, "Unable to rollback changes in database {0}", e.getMessage());
                }
            }
        }
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = getDataSource().getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            if (isLoggingError()) {
                vlogError(e, "Unable to get connection {0}", e.getMessage());
            }
        }
        return connection;
    }

    private void invalidateLocalStoresPriceCaches(Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(
                    getLocalPricesRepository(), "price",
                    pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during local price cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    private void invalidateLocalStoresEcotaxeInfoCaches(Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(
                    getLocalStoresInfoRepository(), "ecotaxeInfo",
                    pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during ecotaxe info cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    private void invalidateLocalStoresPromoInfoCaches(Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(
                    getLocalStoresInfoRepository(), "promoInfo",
                    pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during promo info cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    private void invalidateFccLocalStoresCaches(Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(
                    getLocalStoresInfoRepository(), "skuClickCollectFlag",
                    pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during skuClickCollectFlag cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    private void invalidateDiscountCaches(Set<String> pItemToInvalidateIds) {
        try {
            getInvalidationService().invalidateRepository(
                    getLocalStoresInfoRepository(), "discount",
                    pItemToInvalidateIds);
        } catch (Throwable e) {
            String messagePrefix = "Exception during discount cache invalidation: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
            }
        }
    }

    private static Date getCurrentDate() {
        Calendar cal = new GregorianCalendar();
        cal.clear(Calendar.HOUR_OF_DAY);
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return new Date(cal.getTime().getTime());
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

    public DataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource pDataSource) {
        mDataSource = pDataSource;
    }

    public RepositoryInvalidationService getInvalidationService() {
        return mInvalidationService;
    }

    public void setInvalidationService(RepositoryInvalidationService pInvalidationService) {
        mInvalidationService = pInvalidationService;
    }

    public LocalStoresUtils getLocalStoresUtils() {
        return mLocalStoresUtils;
    }

    public void setLocalStoresUtils(LocalStoresUtils mLocalStoresUtils) {
        this.mLocalStoresUtils = mLocalStoresUtils;
    }
}
