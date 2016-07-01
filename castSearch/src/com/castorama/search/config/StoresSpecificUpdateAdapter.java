package com.castorama.search.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;
import com.castorama.commerce.localStores.LocalStoresManager;
import com.castorama.pricing.CastItemPriceInfo;

import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemGroup;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.search.adapter.customization.CustomizationDataAdapterImpl;
import atg.search.adapter.customization.CustomizationItem;
import atg.search.adapter.customization.CustomizationType;
import atg.search.adapter.exception.CustomizationItemNotFoundException;

public class StoresSpecificUpdateAdapter extends CustomizationDataAdapterImpl {
    
    private static final String END_QUERY_REPORT = "</propertyReport></queryReport>";
    private static final String START_QUERY_REPORT = "<queryReport><propertyReport>";
    private static final String MAGASIN_ID = "magasin";
    private static final String QUERY_ALL = "ALL";
    private static final String CASTORAMA_DIRECT_ID = "999";
    private static final String CASTORAMA_STORE_ID = "storeId";
    private static final String SHOW_CARD_PRICE = "showCardPrice";
    private static final String CHILD_SKUS = "childSKUs";
    private static final String STORE_ID_MAP_KEY = "storeId";

    private static final String KEY_SV_AVAILABLE = "KEY_SV_AVAILABLE";
    private static final String KEY_NOT_ELIGIBLE_SKU = "KEY_NOT_ELIGIBLE_SKU";
    private static final String KEY_GROUP_MEMBERS = "KEY_GROUP_MEMBERS";
    private static final String KEY_EXCLUDED_SKU = "KEY_EXCLUDED_SKU";
    private static final String KEY_WEB_NOT_AVAILABLE = "KEY_WEB_NOT_AVAILABLE";
    
    private static final String TYPE = "type";
    private static final String TYPE_PRODUCT = "product";
    private static final String TYPE_PACK = "casto-pack";
    
    private static final String QUERY_INVENTORY_EMPTY = "SELECT CATALOG_REF_ID FROM DCS_INVENTORY WHERE STOCK_LEVEL = 0";
    private static final String QUERY_STOCK_POSITIVE_FOR_STORE = "SELECT PRODUCT_ID, STOCK_AVAILABLE FROM MS_INVENTORY WHERE STORE_ID = ? AND STOCK_AVAILABLE > 0";
    private static final String QUERY_NOT_ELIGIBLE_FOR_STORE = "SELECT SKU_ID FROM CLICK_COLLECT WHERE STORE_ID = ?";
    
    private static final Integer WEB_SOLD_ONLY_IN_STORE = 1;
    private static final Integer WEB_SOLD_ONLINE = 0;
    private static final Integer CC_RETRAIT = 1;
    private static final Integer CC_DELIVERY = 0;
    
    private String mDisplayName = "Commerce stores specific properties update adapter";
    private RepositoryItemGroup mIndexedItemsGroup;
    
    private Repository mStoreRepository;
    
    private DataSource mCoreDataSource;
    private DataSource mStockDataSource;
    private DataSource mLocalPricesDataSource;
    
    private CastRepositoryInventoryManager mInventoryManager;
    private LocalStoresManager mLocalStoresManager;
    
    private Set<String> excludedSkus;
    private Set<String> webNotAvailable;
    private Map<String, RepositoryItem> stores;
    
    private PricingTools mPricingTools;
    //init mBatchSize by 1000
    private int mBatchSize = 1000;
    private boolean mAddToIndexIfNoCardPrice = false;
    
    @Override
    public List enumerateCustomizationItems() {
        List items = new ArrayList();
        stores = new HashMap<String, RepositoryItem>();
        try {
            RepositoryView storesView = mStoreRepository.getView(MAGASIN_ID);
            RqlStatement storesRQL = RqlStatement.parseRqlStatement(QUERY_ALL);
            RepositoryItem[] storesArray = storesRQL.executeQuery(storesView, null);
            if (storesArray != null) {
                for (RepositoryItem store : storesArray) {
                    if (store != null) {
                        items.add(store.getRepositoryId());
                        stores.put(store.getRepositoryId(), store);
                    }
                }
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                vlogError(re,
                        "Error in com.castorama.search.config.StoresSpecificUpdateAdapter.enumerateCustomizationItems(): {0}",
                        re.getMessage());
            }
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

        StoresSpecificUpdateCustomizationItem item = new StoresSpecificUpdateCustomizationItem(
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

    public Map<String, Object> getMapOfParameters(String pStoreId) throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (mIndexedItemsGroup != null && stores != null && pStoreId != null) {
            RepositoryItem[] indexedRepItems = mIndexedItemsGroup.getGroupMembers();
            Set<String> excludedSkus = mLocalStoresManager.getNotAllowedSkus();
            Set<String> webNotAvailable = queryWebNotAvailable();
            RepositoryItem store = stores.get(pStoreId);
            Map<Integer, Integer> svAvailable = querySvAvailable(store);
            Set<String> notEligibleSkus = queryNotEligible(store);
            map.put(KEY_SV_AVAILABLE, svAvailable);
            map.put(KEY_NOT_ELIGIBLE_SKU, notEligibleSkus);
            map.put(KEY_GROUP_MEMBERS, indexedRepItems);
            map.put(KEY_EXCLUDED_SKU, excludedSkus);
            map.put(KEY_WEB_NOT_AVAILABLE, webNotAvailable);
        }
        return map;
    }
    
    public String getBatchXMLAsString(String pStoreId, int startNum, int endNum, Map<String, Object> mapOfParameters) throws SQLException {
        String result = null;
        if (mapOfParameters.get(KEY_GROUP_MEMBERS) != null && stores != null && pStoreId != null) {
            RepositoryItem[] indexedRepItems = (RepositoryItem[]) mapOfParameters.get(KEY_GROUP_MEMBERS);

            if (indexedRepItems != null && indexedRepItems.length > 0) {
                excludedSkus = (Set<String>) mapOfParameters.get(KEY_EXCLUDED_SKU);
                webNotAvailable = (Set<String>) mapOfParameters.get(KEY_WEB_NOT_AVAILABLE);

                RepositoryItem store = stores.get(pStoreId);

                Set<String> excludedSkusForStore = new HashSet<String>(excludedSkus);
                excludedSkusForStore.addAll((Set<String>) mapOfParameters.get(KEY_NOT_ELIGIBLE_SKU));

                Map<Integer, Integer> svAvailable = (Map<Integer, Integer>) mapOfParameters.get(KEY_SV_AVAILABLE);

                StringBuilder xml = new StringBuilder(START_QUERY_REPORT);

                for ( int i = startNum; i < endNum; i++) {
                    RepositoryItem indexedRepItem = indexedRepItems[i]; 
                    String type = (String) indexedRepItem.getPropertyValue(TYPE);
                    if (type == null || type.isEmpty()) {
                        type = TYPE_PRODUCT;
                    }

                    if (!TYPE_PACK.equals(type)) {
                        StockLevelStatus stockLevelValue = mInventoryManager.inventoryStockLevelStatusForIndexingOnly(indexedRepItem, 
                                store, webNotAvailable, svAvailable, excludedSkusForStore);

                        xml = xml.append("<property name=\"stockLevel@").append(pStoreId).append("\" value=\"")
                        .append(stockLevelValue.getStatusNumber()).append("\" type=\"integer\"><![CDATA[atgrep:/CustomProductCatalog/")
                        .append(type).append("/").append(indexedRepItem.getRepositoryId())
                        .append("?catalog=masterCatalog&locale=fr]]></property>");

                        if (stockLevelValue == StockLevelStatus.S6 || 
                                stockLevelValue == StockLevelStatus.S7 || 
                                stockLevelValue == StockLevelStatus.S8 || 
                                stockLevelValue == StockLevelStatus.S9 || 
                                stockLevelValue == StockLevelStatus.S10) {
                            xml = xml.append("<property name=\"ccMode@").append(pStoreId).append("\" value=\"")
                            .append(CC_RETRAIT).append("\" type=\"integer\"><![CDATA[atgrep:/CustomProductCatalog/")
                            .append(type).append("/").append(indexedRepItem.getRepositoryId())
                            .append("?catalog=masterCatalog&locale=fr]]></property>");
                        }
                        if (stockLevelValue != StockLevelStatus.S0 && 
                                stockLevelValue != StockLevelStatus.S2 && 
                                stockLevelValue != StockLevelStatus.S7) {
                            xml = xml.append("<property name=\"ccMode@").append(pStoreId).append("\" value=\"")
                            .append(CC_DELIVERY).append("\" type=\"integer\"><![CDATA[atgrep:/CustomProductCatalog/")
                            .append(type).append("/").append(indexedRepItem.getRepositoryId())
                            .append("?catalog=masterCatalog&locale=fr]]></property>");
                        }

                        if (CASTORAMA_DIRECT_ID.equals(pStoreId) && stockLevelValue != StockLevelStatus.S0) {
                            Integer availability = WEB_SOLD_ONLINE;
                            if (stockLevelValue == StockLevelStatus.S2) {
                                availability = WEB_SOLD_ONLY_IN_STORE;
                            }
                            xml = xml.append("<property name=\"availability").append("\" value=\"")
                            .append(availability).append("\" type=\"integer\"><![CDATA[atgrep:/CustomProductCatalog/")
                            .append(type).append("/").append(indexedRepItem.getRepositoryId())
                            .append("?catalog=masterCatalog&locale=fr]]></property>");
                        }
                    }

                    int showCardPrice = isShowCardPrice(indexedRepItem, pStoreId);
                    if (isAddToIndexIfNoCardPrice()) {
                        xml = xml.append("<property name=\"cardPriceAvailable@").append(pStoreId).append("\" value=\"")
                                .append(showCardPrice).append("\" type=\"string\"><![CDATA[atgrep:/CustomProductCatalog/")
                                .append(type).append("/").append(indexedRepItem.getRepositoryId())
                                .append("?catalog=masterCatalog&locale=fr]]></property>");

                    } else {
                        if (showCardPrice > 0) {
                            xml = xml.append("<property name=\"cardPriceAvailable@").append(pStoreId).append("\" value=\"")
                                    .append(showCardPrice).append("\" type=\"string\"><![CDATA[atgrep:/CustomProductCatalog/")
                                    .append(type).append("/").append(indexedRepItem.getRepositoryId())
                                    .append("?catalog=masterCatalog&locale=fr]]></property>");
                        }
                    }

                }

                xml = xml.append(END_QUERY_REPORT);
                result = xml.toString();
            }
        }

        return result;
    }

    private int isShowCardPrice(RepositoryItem product, String storeId) {
        int showValue = 0;
        
        List<RepositoryItem> skus = (List<RepositoryItem>) product.getPropertyValue(CHILD_SKUS);
        if ((skus != null) && (skus.size() > 0)) {
            RepositoryItem sku = skus.get(0);
            
            // SHOW_CARD_PRICE property generated on maintenance
            Boolean cardPriceAssignedToSku = (Boolean) sku.getPropertyValue(SHOW_CARD_PRICE);
            if (cardPriceAssignedToSku == null || !cardPriceAssignedToSku) {
                return 0;
            }
            
            try {
                Map<String, Object> extraParams = new HashMap<String, Object>();
                extraParams.put(STORE_ID_MAP_KEY, storeId);
                ItemPriceInfo priceInfo = mPricingTools.calculatePrice(product, sku, 1, false, null, null, extraParams);
                if (priceInfo instanceof CastItemPriceInfo) {
                    CastItemPriceInfo castPriceInfo = (CastItemPriceInfo) priceInfo;
                    Double cardPrice = castPriceInfo.getCardPrice();
                    boolean isValidByDateCCP = castPriceInfo.getIsValidByDateCardPrice();
                    if (cardPrice != null && cardPrice > 0 && isValidByDateCCP) {
                        showValue = 1;
                    }
                }
            } catch (PricingException e) {
                if (isLoggingWarning()) {
                    logWarning(e.getLocalizedMessage());
                }
            }
        }
        
        return showValue;
    }

    private Set<String> queryWebNotAvailable() throws SQLException {
        Set<String> result = new HashSet<String>();

        boolean autoCommit = true;
        Connection con = null;
        Statement s = null;
        ResultSet rs = null;
        try {
            con = mCoreDataSource.getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            s = con.createStatement();
            rs = s.executeQuery(QUERY_INVENTORY_EMPTY);
            while (rs.next()) {
                String skuId = rs.getString(1);
                if (skuId != null) {
                    result.add(skuId);
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from enumerateCustomizationItems method {0}", sqle.getMessage());
                    }
                }
            }
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from enumerateCustomizationItems method {0}", sqle.getMessage());
                    }
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from enumerateCustomizationItems method {0}", sqle.getMessage());
                    }
                }
            }
        }
        
        return result;
    }
    
    private Map<Integer, Integer> querySvAvailable(RepositoryItem pStore) throws SQLException {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        
        boolean autoCommit = true;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = mStockDataSource.getConnection();
            autoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            ps = con.prepareStatement(QUERY_STOCK_POSITIVE_FOR_STORE);
            ps.setString(1, (String) pStore.getPropertyValue(CASTORAMA_STORE_ID));
            rs = ps.executeQuery();
            while (rs.next()) {
                String prodId = rs.getString(1);
                Double sa = rs.getDouble(2);
                int ca = 0;
                try {
                    ca = Integer.parseInt(prodId);
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
                if (ca != 0 && sa != null) {
                    result.put(ca, sa.intValue());
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from querySvAvailable method {0}", sqle.getMessage());
                    }
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from querySvAvailable method {0}", sqle.getMessage());
                    }
                }
            }
            if (con != null) {
                try {
                    con.setAutoCommit(autoCommit);
                    con.close();
                } catch (SQLException sqle) {
                    if (isLoggingError()) {
                        vlogError(sqle, "Error from querySvAvailable method {0}", sqle.getMessage());
                    }
                }
            }
        }
        
        return result;
    }
    
    private Set<String> queryNotEligible(RepositoryItem pStore) throws SQLException {
        Set<String> result = new HashSet<String>();
        if (pStore != null) {
            boolean autoCommit = true;
            Connection con = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                con = mLocalPricesDataSource.getConnection();
                autoCommit = con.getAutoCommit();
                con.setAutoCommit(false);
                ps = con.prepareStatement(QUERY_NOT_ELIGIBLE_FOR_STORE);
                ps.setString(1, pStore.getRepositoryId());
                rs = ps.executeQuery();
                while (rs.next()) {
                    String skuId = rs.getString(1);
                    if (skuId != null) {
                        result.add(skuId);
                    }
                }
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqle) {
                        if (isLoggingError()) {
                            vlogError(sqle, "Error from queryNotEligible method {0}", sqle.getMessage());
                        }
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException sqle) {
                        if (isLoggingError()) {
                            vlogError(sqle, "Error from queryNotEligible method {0}", sqle.getMessage());
                        }
                    }
                }
                if (con != null) {
                    try {
                        con.setAutoCommit(autoCommit);
                        con.close();
                    } catch (SQLException sqle) {
                        if (isLoggingError()) {
                            vlogError(sqle, "Error from queryNotEligible method {0}", sqle.getMessage());
                        }
                    }
                }
            }
        }
        return result;
    }

    public boolean isAutoApplyAll() {
        return true;
    }
    
    public RepositoryItemGroup getIndexedItemsGroup() {
        return mIndexedItemsGroup;
    }

    public void setIndexedItemsGroup(RepositoryItemGroup pIndexedItemsGroup) {
        mIndexedItemsGroup = pIndexedItemsGroup;
    }

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }
    
    public DataSource getCoreDataSource() {
        return mCoreDataSource;
    }

    public void setCoreDataSource(DataSource pCoreDataSource) {
        mCoreDataSource = pCoreDataSource;
    }
    
    public DataSource getStockDataSource() {
        return mStockDataSource;
    }

    public void setStockDataSource(DataSource pStockDataSource) {
        mStockDataSource = pStockDataSource;
    }
    
    public DataSource getLocalPricesDataSource() {
        return mLocalPricesDataSource;
    }

    public void setLocalPricesDataSource(DataSource pLocalPricesDataSource) {
        mLocalPricesDataSource = pLocalPricesDataSource;
    }
    
    public CastRepositoryInventoryManager getInventoryManager() {
        return mInventoryManager;
    }
    
    public void setInventoryManager(CastRepositoryInventoryManager pInventoryManager) {
        mInventoryManager = pInventoryManager;
    }
    
    public LocalStoresManager getLocalStoresManager() {
        return mLocalStoresManager;
    }

    public void setLocalStoresManager(LocalStoresManager pLocalStoresManager) {
        mLocalStoresManager = pLocalStoresManager;
    }
    
    public PricingTools getPricingTools() {
        return mPricingTools;
    }

    public void setPricingTools(PricingTools mPricingTools) {
        this.mPricingTools = mPricingTools;
    }

    public int getBatchSize() {
        return mBatchSize;
    }

    public void setBatchSize(int pBatchSize) {
        this.mBatchSize = pBatchSize;
    }

    public boolean isAddToIndexIfNoCardPrice() {
        return mAddToIndexIfNoCardPrice;
    }

    public void setAddToIndexIfNoCardPrice(boolean mAddToIndexIfNoCardPrice) {
        this.mAddToIndexIfNoCardPrice = mAddToIndexIfNoCardPrice;
    }
}
