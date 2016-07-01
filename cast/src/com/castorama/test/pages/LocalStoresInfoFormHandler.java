package com.castorama.test.pages;

import atg.droplet.GenericFormHandler;
import atg.nucleus.naming.ParameterName;
import atg.repository.*;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Epam team
 * Date: 12/2/12
 * Time: 8:27 PM
 */
public class LocalStoresInfoFormHandler extends GenericFormHandler {


    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    private static final String RQL_STORE_INFO = "id != ?0 and storeId = ?1";
    private static final String RQL_CODE_ARTICLE_TO_SKU = "codeArticle = ?0";
    private static final String WEB_STORE_ID = "999";

    private Store getStorePrices(String castoStoreId) {
        try {
            Repository storeRep = getStoreRepository();
            RepositoryView repView;
            repView = storeRep.getView("magasin");
            RqlStatement storesRQL = RqlStatement.parseRqlStatement(RQL_STORE_INFO);
            RepositoryItem[] stores = storesRQL.executeQuery(repView, new Object[]{WEB_STORE_ID, castoStoreId});
            if (stores != null) {
                if (stores[0] != null) {
                    RepositoryItem store = stores[0];
                    RepositoryItem storeLocalPriceList = (RepositoryItem) store.getPropertyValue("localPriceList");
                    Boolean retraitMagasin = (Boolean) store.getPropertyValue("retraitMagasin");
                    Boolean localPrix = (Boolean) store.getPropertyValue("localPrix");
                    return new Store(castoStoreId, store.getRepositoryId(),
                            storeLocalPriceList.getRepositoryId(),
                            retraitMagasin, localPrix);

                }
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                vlogError(
                        re,
                        "Error in LocalStoresInfoFormHandler.initializeStoresPricesMapping(): {0}",
                        re.getMessage());
            }
        }
        return null;
    }

    private String getSkuCodeArticle(String codeArticle) {
        try {
            Repository prodRep = getProductCatalog();
            RepositoryView repView = prodRep.getView("sku");
            RqlStatement skusRQL = RqlStatement.parseRqlStatement(RQL_CODE_ARTICLE_TO_SKU);
            RepositoryItem[] skus = skusRQL.executeQuery(repView, new Object[]{codeArticle});
            if (skus != null) {
                RepositoryItem sku = skus[0];
                if (sku != null) {
                    return sku.getRepositoryId();
                }
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                vlogError(
                        e,
                        "Error in LocalStoresInfoFormHandler.initializeSkuCodeArticleMapping(): {0}",
                        e.getMessage());
            }
        }
        return null;
    }

    private LocalStoreInfo getStoreInfo(Store store, String codeArticle) {
        LocalStoreInfo localStoreInfo = new LocalStoreInfo();
        if (storeId == null || codeArticle == null) {
            return null;
        }
        if (store != null) {
            String skuId = getSkuCodeArticle(codeArticle);
            if (skuId != null) {
                localStoreInfo.setSkuId(skuId);
                localStoreInfo.setCastoramaStoreId(Integer.parseInt(store.getCastoramaStoreId()));
                localStoreInfo.setCodeArticle(Integer.parseInt(codeArticle));
                localStoreInfo.setStoreId(Integer.parseInt(store.getATGStoreId()));
                localStoreInfo.setSkuId(skuId);
                try {

                    RepositoryItemDescriptor repositoryItemDescriptor = getLocalPricesRepository().getItemDescriptor("price");
                    RepositoryView repositoryView = repositoryItemDescriptor.getRepositoryView();
                    QueryBuilder queryBuilder = repositoryView.getQueryBuilder();
                    Query getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("priceList"),
                                    queryBuilder.createConstantQueryExpression(store.getLocalPriceList()),
                                    QueryBuilder.EQUALS),
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("skuId"),
                                    queryBuilder.createConstantQueryExpression(skuId),
                                    QueryBuilder.EQUALS)
                    });
                    RepositoryItem[] repositoryItems = repositoryView.executeQuery(getStoreQuery);
                    if (repositoryItems != null) {
                        localStoreInfo.setLocalPrice((Double) repositoryItems[0].getPropertyValue("listPrice"));
                    }

                    repositoryItemDescriptor = getLocalStoresInfoRepository().getItemDescriptor("ecotaxeInfo");
                    repositoryView = repositoryItemDescriptor.getRepositoryView();
                    queryBuilder = repositoryView.getQueryBuilder();
                    getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("storeId"),
                                    queryBuilder.createConstantQueryExpression(store.getATGStoreId()),
                                    QueryBuilder.EQUALS),
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("skuId"),
                                    queryBuilder.createConstantQueryExpression(skuId),
                                    QueryBuilder.EQUALS)
                    });
                    repositoryItems = repositoryView.executeQuery(getStoreQuery);
                    if (repositoryItems != null) {
                        localStoreInfo.setMd3e((Double) repositoryItems[0].getPropertyValue("md3e"));
                        localStoreInfo.setMrep((Double) repositoryItems[0].getPropertyValue("mrep"));
                    }

                    repositoryItemDescriptor = getLocalStoresInfoRepository().getItemDescriptor("promoInfo");
                    repositoryView = repositoryItemDescriptor.getRepositoryView();
                    queryBuilder = repositoryView.getQueryBuilder();
                    getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("storeId"),
                                    queryBuilder.createConstantQueryExpression(store.getATGStoreId()),
                                    QueryBuilder.EQUALS),
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("skuId"),
                                    queryBuilder.createConstantQueryExpression(skuId),
                                    QueryBuilder.EQUALS)
                    });
                    repositoryItems = repositoryView.executeQuery(getStoreQuery);
                    if (repositoryItems != null) {
                        localStoreInfo.setfPromo((Boolean) repositoryItems[0].getPropertyValue("fPromo"));
                        localStoreInfo.setCodePromo((Integer) repositoryItems[0].getPropertyValue("cPromo"));
                    }

                    repositoryItemDescriptor = getLocalStoresInfoRepository().getItemDescriptor("skuClickCollectFlag");
                    repositoryView = repositoryItemDescriptor.getRepositoryView();
                    queryBuilder = repositoryView.getQueryBuilder();
                    getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("storeId"),
                                    queryBuilder.createConstantQueryExpression(store.getATGStoreId()),
                                    QueryBuilder.EQUALS),
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("skuId"),
                                    queryBuilder.createConstantQueryExpression(skuId),
                                    QueryBuilder.EQUALS)
                    });
                    repositoryItems = repositoryView.executeQuery(getStoreQuery);
                    if (repositoryItems != null) {
                        localStoreInfo.setClickCollectFlag(false);
                    } else {
                        localStoreInfo.setClickCollectFlag(true);
                    }

                    repositoryItemDescriptor = getLocalStoresInfoRepository().getItemDescriptor("discount");
                    repositoryView = repositoryItemDescriptor.getRepositoryView();
                    queryBuilder = repositoryView.getQueryBuilder();
                    getStoreQuery = queryBuilder.createAndQuery(new Query[]{
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("storeId"),
                                    queryBuilder.createConstantQueryExpression(store.getATGStoreId()),
                                    QueryBuilder.EQUALS),
                            queryBuilder.createComparisonQuery(
                                    queryBuilder.createPropertyQueryExpression("skuId"),
                                    queryBuilder.createConstantQueryExpression(skuId),
                                    QueryBuilder.EQUALS)
                    });
                    repositoryItems = repositoryView.executeQuery(getStoreQuery);
                    if (repositoryItems != null) {
                        localStoreInfo.setDisplayDiscount(true);
                        Date beginDate = (Date) repositoryItems[0].getPropertyValue("displayDiscountBeginDate");
                        Date endDate = (Date) repositoryItems[0].getPropertyValue("displayDiscountEndDate");
                        if (beginDate != null) {
                            localStoreInfo.setDisplayDiscountBeginDate(new java.util.Date(beginDate.getTime()));
                        }
                        if (endDate != null) {
                            localStoreInfo.setDisplayDiscountEndDate(new java.util.Date(endDate.getTime()));
                        }
                    } else {
                        localStoreInfo.setDisplayDiscount(false);
                    }

                } catch (RepositoryException re) {
                    if (isLoggingError()) {
                        vlogError(re, "RepositoryException: {0}", re.getMessage());
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return localStoreInfo;
    }

    /**
     * Submit handler
     */
    public boolean handleSubmit(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws IOException, ServletException {

        Store selectedStore = getStorePrices(storeId);

        LocalStoreInfo localStoreInfo = getStoreInfo(selectedStore, codeArticle);

        if (localStoreInfo != null) {
            pRequest.setParameter("storeInfo", localStoreInfo);
        }
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

        return true;
    }

    public String getSubmit() {
        return null;
    }

    /**
     * Name property
     */
    private String storeId;
    private String codeArticle;

    private Repository mStoreRepository;
    private Repository mLocalPricesRepository;
    private Repository mLocalStoresInfoRepository;
    private Repository mProductCatalog;

    private Map<String, Store> mStoresPricesMap = new HashMap<String, Store>();
    private Map<String, String> mCodeArticleSkuIDMap = new HashMap<String, String>();

    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    public void setProductCatalog(Repository mProductCatalog) {
        this.mProductCatalog = mProductCatalog;
    }

    public Map<String, String> getCodeArticleSkuIDMap() {
        return mCodeArticleSkuIDMap;
    }

    public void setCodeArticleSkuIDMap(Map<String, String> mCodeArticleSkuIDMap) {
        this.mCodeArticleSkuIDMap = mCodeArticleSkuIDMap;
    }

    public Map<String, Store> getStoresPricesMap() {
        return mStoresPricesMap;
    }

    public void setStoresPricesMap(Map<String, Store> pStoresPricesMap) {
        mStoresPricesMap = pStoresPricesMap;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(String codeArticle) {
        this.codeArticle = codeArticle;
    }

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository mStoreRepository) {
        this.mStoreRepository = mStoreRepository;
    }

    public Repository getLocalPricesRepository() {
        return mLocalPricesRepository;
    }

    public void setLocalPricesRepository(Repository mLocalPricesRepository) {
        this.mLocalPricesRepository = mLocalPricesRepository;
    }

    public Repository getLocalStoresInfoRepository() {
        return mLocalStoresInfoRepository;
    }

    public void setLocalStoresInfoRepository(Repository mLocalStoresInfoRepository) {
        this.mLocalStoresInfoRepository = mLocalStoresInfoRepository;
    }
}
