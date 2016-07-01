package com.castorama.search.config;

import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.search.adapter.customization.CustomizationDataAdapterImpl;
import atg.search.adapter.customization.CustomizationItem;
import atg.search.adapter.customization.CustomizationType;
import atg.search.adapter.exception.CustomizationItemNotFoundException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StoresPricesUpdateAdapter extends CustomizationDataAdapterImpl {


    /**
     * name of store item descriptor.
     */
    private static final String STORE_ITEM_DESCRIPTOR = "magasin";

    /**
     * name of local prix property.
     */
    private static final String PROPERTY_LOCAL_PRIX = "localPrix";

    /**
     * name of local list price list property.
     */
    private static final String PROPERTY_LOCAl_PRICE_LIST = "localPriceList";

    /**
     * get all local stores RQL
     */
    private static final String RQL_STORE_STATEMENT = "ALL";

    /**
     * M2 constant
     */
    private static final String M2 = "m2";
    /**
     * PUPUV2 constant
     */
    private static final String PUPUV2 = "PUPUV";
    /**
     * CUP constant
     */
    private static final String CUP = "CUP";
    /**
     * SHOW_M2_PRICE_FIRST constant
     */
    private static final String SHOW_M2_PRICE_FIRST = "showM2PriceFirst";

    private static final String TYPE = "type";
    private static final String TYPE_PRODUCT = "product";
    private static final String PRICES_FOR_SALE_PRICE = "SELECT /*+ USE_NL(CAST_PRICE1 CAST_PRICE2)*/ CAST_PRICE1.SKU_ID, CAST_PRICE2.LIST_PRICE FROM (SELECT TMP1.SKU_ID, TMP1.ROW_ID FROM ( SELECT TMP.SKU_ID , TMP.ROW_ID , ROWNUM as ROW_NUM FROM ( SELECT SKU_ID, ROWID as ROW_ID FROM CAST_PRICE WHERE PRICE_LIST = ? ORDER BY sku_id) TMP) TMP1 WHERE TMP1.ROW_NUM BETWEEN ? AND ?) CAST_PRICE1 , CAST_PRICE CAST_PRICE2 WHERE CAST_PRICE1.ROW_ID=CAST_PRICE2.ROWID";
    private static final String COUNT_PRICES_FOR_SALE_PRICE = "select count(price_id) from cast_price where price_list = ?";
    
    private static final String END_QUERY_REPORT = "</propertyReport></queryReport>";
    private static final String START_QUERY_REPORT = "<queryReport><propertyReport>";

    private String mDisplayName = "Commerce Stores Prices Update Adapter";
    private Repository mStoreRepository;
    private RepositoryItemGroup mIndexedItemsGroup;
    private DataSource mDataSource;

    private Map<String, List<String>> mProductsToSkus = new HashMap<String, List<String>>();
    private Map<String, String> mProductsToTypes = new HashMap<String, String>();
    private Map<String, List<String>> mSkuToProducts = new HashMap<String, List<String>>();
    private Map<String, Float> mSkuToPUPUV = new HashMap<String, Float>();
    //init mBatchSize by 1000
    private int mBatchSize = 1000;


    @Override
    public List enumerateCustomizationItems() {
        initializeSkuProductMaps();
        List<String> items = new ArrayList<String>();
        try {
            Repository storeRep = getStoreRepository();
            RepositoryView repView;
            repView = storeRep.getView(STORE_ITEM_DESCRIPTOR);
            RqlStatement storesRQL = RqlStatement.parseRqlStatement(RQL_STORE_STATEMENT);
            RepositoryItem[] stores = storesRQL.executeQuery(repView, null);
            for (RepositoryItem store : stores) {
                if (store != null) {
                    // retraitMagasin and localPrix aren't null
                    Boolean localPrix = (Boolean) store.getPropertyValue(PROPERTY_LOCAL_PRIX);
                    if (localPrix) {
                        RepositoryItem storeLocalPriceList = (RepositoryItem) store
                                .getPropertyValue(PROPERTY_LOCAl_PRICE_LIST);
                        if (storeLocalPriceList != null) {
                            items.add(storeLocalPriceList.getRepositoryId());
                        }
                    }
                }
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                vlogError(
                        re,
                        "Error in com.castorama.search.config.StoresPricesUpdateAdapter.enumerateCustomizationItems(): {0}",
                        re.getMessage());
            }
            return Collections.EMPTY_LIST;
        }
        return items;
    }

    @Override
    public List enumerateCustomizationItemsByType(
            CustomizationType pCustomizationType) {
        if (pCustomizationType != CustomizationType.AUXILIARY_DATA) {
            return (Collections.EMPTY_LIST);
        }

        return enumerateCustomizationItems();
    }

    public List enumerateAutoApplyCustomizationItemsByTypeAndProject(
            CustomizationType pCustomizationType, String pProjectId)
            throws CustomizationItemNotFoundException {
        if (!isAutoApplyAll()) {
            return Collections.EMPTY_LIST;
        }

        if (pCustomizationType != CustomizationType.AUXILIARY_DATA) {
            return (Collections.EMPTY_LIST);
        }

        return enumerateCustomizationItems();
    }

    @Override
    public CustomizationItem getCustomizationItem(
            CustomizationType pCustomizationType, String pCustomizationItemId)
            throws CustomizationItemNotFoundException {
        if (pCustomizationType != CustomizationType.AUXILIARY_DATA) {
            throw new CustomizationItemNotFoundException(
                    "Unknown customization type: " + pCustomizationType);
        }

        StoresPricesUpdateCustomizationItem item = new StoresPricesUpdateCustomizationItem(
                this, pCustomizationItemId);

        return (item);
    }

    @Override
    public String getDisplayName() {
        return mDisplayName;
    }

    @Override
    public Collection getSupportedCustomizationTypes() {
        Collection supportedTypes = new ArrayList();
        supportedTypes.add(CustomizationType.AUXILIARY_DATA);

        return (supportedTypes);
    }

    public String getBatchXMLAsString(String pPriceListId, int startNum, int endNum) throws SQLException {
        Connection con = null;
        boolean autoCommit = true;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder xml = new StringBuilder(500 * getBatchSize());
        xml.append(START_QUERY_REPORT);
        try {
            con = getDataSource().getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            ps = con.prepareStatement(PRICES_FOR_SALE_PRICE);
            ps.setString(1, pPriceListId);
            ps.setInt(2, startNum);
            ps.setInt(3, endNum);
            rs = ps.executeQuery();
            while (rs.next()) {
                String skuId = rs.getString(1);
                double priceList = rs.getDouble(2);
                priceList = mSkuToPUPUV.containsKey(skuId) ? priceList / mSkuToPUPUV.get(skuId) : priceList;
                String productId = mSkuToProducts.containsKey(skuId) ? mSkuToProducts.get(skuId).get(0) : null;
                if (productId != null) {
                    String type = mProductsToTypes.get(productId);
                    xml = xml
                            .append("<property name=\"childSKUs.price@")
                            .append(pPriceListId)
                            .append("\" value=\"")
                            .append(priceList)
                            .append("\" type=\"float\"><![CDATA[atgrep:/CustomProductCatalog/")
                            .append(type).append("/")
                            .append(productId)
                            .append("?catalog=masterCatalog&locale=fr]]></property>");
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getBatchXMLAsString method {0}",
                                e.getMessage());
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getBatchXMLAsString method {0}",
                                e.getMessage());
                    }
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getBatchXMLAsString method {0}",
                                e.getMessage());
                    }
                }
            }
            xml = xml.append(END_QUERY_REPORT);
            return xml.toString();
        } // end try-catch-finally
    }

    public int getPricesCount(String pPriceListId) throws SQLException {
        Connection con = null;
        boolean autoCommit = true;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuilder xml = new StringBuilder(START_QUERY_REPORT);
        try {
            con = getDataSource().getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            ps = con.prepareStatement(COUNT_PRICES_FOR_SALE_PRICE);
            ps.setString(1, pPriceListId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int count = rs.getInt(1);
                return count;
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getPricesCount method {0}",
                                e.getMessage());
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getPricesCount method {0}",
                                e.getMessage());
                    }
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        vlogError(e,
                                "Error from getPricesCount method {0}",
                                e.getMessage());
                    }
                }
            }
            xml = xml.append(END_QUERY_REPORT);
        } // end try-catch-finally
        return 0;
    }

    private void initializeSkuProductMaps() {
        RepositoryItemGroup indexedItems = getIndexedItemsGroup();
        RepositoryItem[] indexedRepItems = indexedItems.getGroupMembers();
        Map<String, List<String>> productsToSkus = mProductsToSkus;
        Map<String, String> productsToTypes = mProductsToTypes;
        for (RepositoryItem indexedRepItem : indexedRepItems) {
            String type = (String) indexedRepItem.getPropertyValue(TYPE);
            if (type == null || type.isEmpty()) {
                type = TYPE_PRODUCT;
            }
            productsToTypes.put(indexedRepItem.getRepositoryId(), type);

            List<RepositoryItem> skus = (List<RepositoryItem>) indexedRepItem
                    .getPropertyValue("childSKUs");
            if (skus != null) {
                List<String> skusIds = new ArrayList<String>();
                for (RepositoryItem sku : skus) {
                    skusIds.add(sku.getRepositoryId());
                    fillSkuToPUPUV(sku);
                }
                productsToSkus.put(indexedRepItem.getRepositoryId(), skusIds);
            }
        }
        Map<String, List<String>> skuToProducts = mSkuToProducts;
        for (Map.Entry<String, List<String>> productToSkus : productsToSkus
                .entrySet()) {
            String productId = productToSkus.getKey();
            List<String> skusIds = productToSkus.getValue();
            for (String skuId : skusIds) {
                List<String> productsIds = skuToProducts.get(skuId);
                if (productsIds == null) {
                    productsIds = new ArrayList<String>();
                }
                productsIds.add(productId);
                skuToProducts.put(skuId, productsIds);

            }
        }
    }
    
    private void fillSkuToPUPUV(RepositoryItem sku) {
        Object objShowM2PriceFirst = sku.getPropertyValue(SHOW_M2_PRICE_FIRST);
        if (objShowM2PriceFirst != null && objShowM2PriceFirst instanceof Boolean) {
            Boolean showM2PriceFirst = (Boolean) objShowM2PriceFirst;
            if (showM2PriceFirst) {
                Object objCodeDeVente = sku.getPropertyValue(CUP);
                if (objCodeDeVente != null && objCodeDeVente instanceof String) {
                    String codeDeVente = (String) objCodeDeVente;
                    if (codeDeVente.equalsIgnoreCase(M2)) {
                        Object objPUPUV = sku.getPropertyValue(PUPUV2);
                        if (objPUPUV != null && objPUPUV instanceof Float) {
                            Float PUPUV = (Float) objPUPUV;
                            if (PUPUV > 0) {
                                mSkuToPUPUV.put(sku.getRepositoryId(), PUPUV);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean isAutoApplyAll() {
        return true;
    }

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }

    public RepositoryItemGroup getIndexedItemsGroup() {
        return mIndexedItemsGroup;
    }

    public void setIndexedItemsGroup(RepositoryItemGroup pIndexedItemsGroup) {
        mIndexedItemsGroup = pIndexedItemsGroup;
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
        this.mBatchSize = pBatchSize;
    }
}
