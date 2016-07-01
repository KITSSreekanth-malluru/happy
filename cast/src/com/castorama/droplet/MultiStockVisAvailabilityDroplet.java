package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.search.routing.command.search.Result;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;

/**
 *
 * @author 
 */
public class MultiStockVisAvailabilityDroplet extends DynamoServlet {

    /** SEARCH_RESULTS constant. */
    private static final String SEARCH_RESULTS = "searchResults";
    
    /** PRODUCTS constant. */
    private static final String PRODUCTS = "products";
    
    /** PRODUCTS_SET constant. */
    private static final String PRODUCTS_SET = "productsSet";
    
    /** PRODUCTS_IDS constant. */
    private static final String PRODUCTS_IDS = "productsIds";

    /** SKUS constant. */
    private static final String SKUS = "skus";
    
    /** BUNDLES constant. */
    private static final String BUNDLES = "bundles";
    
    /** CROSS_SELLINGS constant. */
    private static final String CROSS_SELLINGS = "crossSellings";
    
    /** STORE constant. */
    private static final String STORE = "store";
    
    /** ITEM constant. */
    private static final String ITEM = "item";
    
    /** PRODUCT constant. */
    private static final String PRODUCT = "product";
    
    /** REPOSITORY_ID constant. */
    private static final String REPOSITORY_ID = "$repositoryId";
    
    /** SV_AVAILABLE_MAP constant. */
    private static final String SV_AVAILABLE_MAP = "svAvailableMap";
    
    /** OUTPUT constant. */
    private static final String OUTPUT = "output";

    
    /** inventoryManager property */
    private CastRepositoryInventoryManager mInventoryManager;
    
    /** productCatalog property. */
    private Repository mProductCatalog;

    /**
     * Returns inventoryManager property.
     *
     * @return inventoryManager property.
     */
    public CastRepositoryInventoryManager getInventoryManager() {
        return mInventoryManager;
    }

    /**
     * Sets the value of the inventoryManager property.
     *
     * @param pInventoryManager parameter to set.
     */
    public void setInventoryManager(CastRepositoryInventoryManager pInventoryManager) {
        mInventoryManager = pInventoryManager;
    }
    
    /**
     * Returns productCatalog property.
     *
     * @return productCatalog property.
     */
    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    /**
     * Sets the value of the productCatalog property.
     *
     * @param pProductCatalog parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
    }
    

    /**
     * Render suitable oparam on "Stock Calculation rules" basis.
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    @SuppressWarnings("unchecked")
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
            throws ServletException, IOException {
        List<Result> searchResults = (List<Result>) pRequest.getObjectParameter(SEARCH_RESULTS);
        List<RepositoryItem> products = (List<RepositoryItem>) pRequest.getObjectParameter(PRODUCTS);
        Set<RepositoryItem> productsSet = (Set<RepositoryItem>) pRequest.getObjectParameter(PRODUCTS_SET);
        List<String> productsIds = (List<String>) pRequest.getObjectParameter(PRODUCTS_IDS);
        List<RepositoryItem> skus = (List<RepositoryItem>) pRequest.getObjectParameter(SKUS);
        List<RepositoryItem> bundles = (List<RepositoryItem>) pRequest.getObjectParameter(BUNDLES);
        List<RepositoryItem> crossSellings = (List<RepositoryItem>) pRequest.getObjectParameter(CROSS_SELLINGS);
        RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE);
        
        // store parameter
        if (store != null) {
            Map<Integer, Integer> svAvailableMap = null;
            if (searchResults != null) {
                List<RepositoryItem> searchProducts = new ArrayList<RepositoryItem>(searchResults.size());
                for (Result result : searchResults) {
                    String prodId = (String) result.getDocument().getProperties().get(REPOSITORY_ID);
                    RepositoryItem searchProduct = null;
                    try {
                        searchProduct = mProductCatalog.getItem(prodId, PRODUCT);
                    } catch (RepositoryException re) {
                        if (isLoggingError()) {
                            logError(re);
                        }
                    }
                    if (searchProduct != null) {
                        searchProducts.add(searchProduct);
                    }
                }
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(searchProducts, store);
            } else if (productsIds != null) {
                List<RepositoryItem> productsItems = new ArrayList<RepositoryItem>(productsIds.size());
                for (String prodId : productsIds) {
                    RepositoryItem productItem = null;
                    try {
                        productItem = mProductCatalog.getItem(prodId, PRODUCT);
                    } catch (RepositoryException re) {
                        if (isLoggingError()) {
                            logError(re);
                        }
                    }
                    if (productItem != null) {
                        productsItems.add(productItem);
                    }
                }
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(productsItems, store);
            } else if (products != null) {
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(products, store);
            } else if (productsSet != null) {
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(new ArrayList<RepositoryItem>(productsSet), store);
            } else if (skus != null) {
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(skus, store);
            } else if (bundles != null) {
                List<RepositoryItem> bundleSkus = new ArrayList<RepositoryItem>(bundles.size());
                for (RepositoryItem bundle : bundles) {
                    RepositoryItem bundleSku = (RepositoryItem) bundle.getPropertyValue(ITEM);
                    if (bundleSku != null) {
                        bundleSkus.add(bundleSku);
                    }
                }
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(bundleSkus, store);
            }  else if (crossSellings != null) {
                List<RepositoryItem> crossProducts = new ArrayList<RepositoryItem>(crossSellings.size());
                for (RepositoryItem crossSelling : crossSellings) {
                    RepositoryItem crossProduct = (RepositoryItem) crossSelling.getPropertyValue(PRODUCT);
                    if (crossProduct != null) {
                        crossProducts.add(crossProduct);
                    }
                }
                svAvailableMap = getInventoryManager().getSVStockAvailableMultiForFilteredItems(crossProducts, store);
            }
            
            if (svAvailableMap != null && !svAvailableMap.isEmpty()) {
                pRequest.setParameter(SV_AVAILABLE_MAP, svAvailableMap);
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
        }
    }
}
