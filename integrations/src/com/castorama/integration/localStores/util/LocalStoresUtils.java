package com.castorama.integration.localStores.util;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import com.castorama.integration.localStores.common.Store;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Epam team
 * Date: 1/21/13
 * Time: 10:01 PM
 */
public class LocalStoresUtils extends GenericService {

    /**
     * name of store item descriptor.
     */
    private static final String STORE_ITEM_DESCRIPTOR = "magasin";

    /**
     * name of local prix property.
     */
    private static final String PROPERTY_LOCAL_PRIX = "localPrix";

    /**
     * name of retrait magasin property.
     */
    private static final String PROPERTY_RETRAIT_MAGASIN = "retraitMagasin";

    /**
     * store id property
     */
    private static final String PROPERTY_STORE_ID = "storeId";

    /**
     * name of local list price list property.
     */
    private static final String PROPERTY_LOCAl_PRICE_LIST = "localPriceList";

    /**
     * get all local stores RQL
     */
    private static final String RQL_STORE_STATEMENT = "id != 999";

    /**
     * store repository
     */
    private Repository mStoreRepository;

    /**
     * catalog data source
     */
    private DataSource mDataSource;

    /**
     *
     */
    private static String SQL_SELECT_SKU_ID_AND_CODE_ARTICLE = "SELECT SKU_ID, CODEARTICLE FROM CASTO_SKU";


    public Collection<Store> getLocalStores() throws RepositoryException {
        Collection<Store> localStores = new ArrayList<Store>();

        Repository storeRep = getStoreRepository();
        ((GSARepository) storeRep).invalidateCaches();
        RepositoryView repView;
        repView = storeRep.getView(STORE_ITEM_DESCRIPTOR);
        RqlStatement storesRQL = RqlStatement.parseRqlStatement(RQL_STORE_STATEMENT);
        RepositoryItem[] stores = storesRQL.executeQuery(repView, null);
        for (RepositoryItem store : stores) {
            if (store != null) {
                String castoramaStoreId = (String) store.getPropertyValue(PROPERTY_STORE_ID);
                RepositoryItem storeLocalPriceList = (RepositoryItem) store.getPropertyValue(PROPERTY_LOCAl_PRICE_LIST);
                Boolean retraitMagasin = (Boolean) store.getPropertyValue(PROPERTY_RETRAIT_MAGASIN);
                Boolean localPrix = (Boolean) store.getPropertyValue(PROPERTY_LOCAL_PRIX);
                Store localStore;
                if (storeLocalPriceList != null) {
                    localStore = new Store(castoramaStoreId, store.getRepositoryId(),
                            storeLocalPriceList.getRepositoryId(),
                            retraitMagasin, localPrix);
                    localStores.add(localStore);
                }
            }
        }
        return localStores;
    }


    public Map<String, String> getSkuCodeArticleMapping() throws SQLException {
        Map<String, String> codeArticleSkuIDMap = new HashMap<String, String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String skuId;
        String codeArticle;
        try {
            connection = getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_SKU_ID_AND_CODE_ARTICLE);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                skuId = resultSet.getString(1);
                codeArticle = resultSet.getString(2);
                codeArticleSkuIDMap.put(codeArticle, skuId);
            }
        } finally {
            closeResultSet(resultSet);
            closeStatement(preparedStatement);
            closeConnection(connection);
        }
        return codeArticleSkuIDMap;
    }


    public Map<String, Store> getLocalStoresMapping() throws RepositoryException {
        Map<String, Store> storesPricesMap = new HashMap<String, Store>();
        Collection<Store> localStores = getLocalStores();
        for (Store store : localStores) {
            storesPricesMap.put(store.getCastoramaStoreId(), store);
        }
        return storesPricesMap;
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

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource mDataSource) {
        this.mDataSource = mDataSource;
    }
}
