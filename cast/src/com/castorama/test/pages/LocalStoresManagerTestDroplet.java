package com.castorama.test.pages;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.castorama.commerce.localStores.LocalStoresManager;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: casto_dev
 * Date: 12/16/15
 * Time: 5:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocalStoresManagerTestDroplet extends DynamoServlet {
    private final static String OUTPUT = "output";

    private final static String ERROR = "error";

    private final static String SKU_ID = "skuId";

    private final static String STORE_ID = "storeId";

    private final static String PACK_ID = "packId";

    private final static String SKU_ITEM_DESCRIPTOR = "sku";

    private final static String CASTO_PACK_ITEM_DESCRIPTOR = "casto-pack";

    private final static String PRODUCT_ITEM_DESCRIPTOR = "product";

    private final static String CHILD_SKUS = "childSkus";

    private final static String BUNDLE_LINKS = "bundleLinks";

    private final static String SKU_RESULT_EMPTY = "Can't be calculated because sku ID is not valid";

    private final static String PACK_RESULT_EMPTY = "Can't be calculated because pack ID is not valid";

    private final static String SKU_MISSED_MESSAGE = "There are not sku with this ID";

    private final static String PACK_MISSED_MESSAGE = "There are not pack with this ID";

    private final static String STORE_MISSED_MESSAGE = "There are not store with this ID";

    private final static String ERROR_MESSAGE = "errorMessage";

    private final static String RESULT_FOR_SKU = "resultForSku";

    private final static String RESULT_FOR_SKU_WITH_BUNDLE = "resultForSkuWithBundle";

    private final static String RESULT_FOR_PACK = "resultForPack";

    private final static String GET_STORE_RQL = "storeId = ?0";


    private Repository mStoreRepository;

    private Repository mProductRepository;

    private LocalStoresManager mLocalStoresManager;

    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        try {
            String storeId = pRequest.getParameter(STORE_ID);
            String skuId = pRequest.getParameter(SKU_ID);
            String packId = pRequest.getParameter(PACK_ID);

            Repository storeRep = getStoreRepository();
            RepositoryView repView;
            repView = storeRep.getView("magasin");
            RqlStatement storesRQL = RqlStatement.parseRqlStatement(GET_STORE_RQL);
            RepositoryItem[] stores = storesRQL.executeQuery(repView, new Object[]{storeId});
            if (stores != null && stores.length > 0) {
                RepositoryItem store = stores[0];
                String resultForSku = SKU_RESULT_EMPTY;
                String resultForSkuWithBundleLinks = null;
                String resultForPack = PACK_RESULT_EMPTY;
                List<RepositoryItem> bundleLinks = null;
                if (skuId != null && !skuId.isEmpty()) {
                    RepositoryItem sku = getProductRepository().getItem(skuId, SKU_ITEM_DESCRIPTOR);
                    if (sku != null) {
                        resultForSku = String.valueOf(getLocalStoresManager().isEligibleForCcInStore(sku, store));
                        bundleLinks = (List) sku.getPropertyValue(BUNDLE_LINKS);
                        if (bundleLinks != null && !bundleLinks.isEmpty()) {
                            resultForSkuWithBundleLinks = String.valueOf(getLocalStoresManager().isEligibleForCcInStore(bundleLinks, store));
                        }
                    } else {
                        resultForSku = SKU_MISSED_MESSAGE;
                    }
                }
                if (packId != null && !packId.isEmpty()) {
                   RepositoryItem pack = getProductRepository().getItem(packId, CASTO_PACK_ITEM_DESCRIPTOR);
                   if (pack != null) {
                       bundleLinks = null;
                       List<RepositoryItem> childSkus = (List<RepositoryItem>) pack.getPropertyValue(CHILD_SKUS);
                       if ((childSkus != null) && !childSkus.isEmpty()) {
                           RepositoryItem bundleLinkSku = childSkus.get(0);
                           bundleLinks = (List) bundleLinkSku.getPropertyValue(BUNDLE_LINKS);
                           resultForPack = String.valueOf(getLocalStoresManager().isEligibleForCcInStore(bundleLinks, store));
                       }
                   } else {
                       resultForPack = PACK_MISSED_MESSAGE;
                   }
                }
                pRequest.setParameter(RESULT_FOR_SKU, resultForSku);
                pRequest.setParameter(RESULT_FOR_SKU_WITH_BUNDLE, resultForSkuWithBundleLinks);
                pRequest.setParameter(RESULT_FOR_PACK, resultForPack);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                //serve output parameters
            } else {
                pRequest.setParameter(ERROR_MESSAGE, STORE_MISSED_MESSAGE);
                pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (ServletException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (IOException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }

    public LocalStoresManager getLocalStoresManager() {
        return mLocalStoresManager;
    }

    public void setLocalStoresManager(LocalStoresManager pLocalStoresManager) {
        this.mLocalStoresManager = pLocalStoresManager;
    }

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        this.mStoreRepository = pStoreRepository;
    }

    public Repository getProductRepository() {
        return mProductRepository;
    }

    public void setProductRepository(Repository pProductRepository) {
        this.mProductRepository = pProductRepository;
    }
}
