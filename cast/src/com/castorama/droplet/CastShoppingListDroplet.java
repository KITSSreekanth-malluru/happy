package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

public class CastShoppingListDroplet extends DynamoServlet {
    
    /** PARAM_SKU_ID constant for input parameter name */
    private static final String PARAM_SKU_ID = "skuIds";
    /** PARAM_ACTION constant for input parameter name*/
    private static final String PARAM_ACTION = "action";
    /** PARAM_AMOUNT constant for output parameter name*/
    private static final String PARAM_AMOUNT = "amount";
    /** PARAM_ACTION constant for output parameter name*/
    private static final String PARAM_LIST_PRODUCTS = "productsList";
    /** PARAM_STORE constant for input parameter name*/
    private static final String PARAM_STORE = "store";
    
    /** ACTION_ADD constant */
    private static final String ACTION_ADD = "add";
    /** ACTION_REMOVE constant */
    private static final String ACTION_REMOVE = "remove";
    
    /** TYPE_CASTO_SKU constant for repository item property */
    private static final String TYPE_CASTO_SKU = "casto_sku";
    /** PROPERTY_WISH_LIST constant for sku's property name */
    private static final String PROPERTY_SHOPPING_LIST = "shoppingList";
    
    /** NOT_PROCESSED_SKUS constant */
    private static final String NOT_PROCESSED_SKUS = "notProcessed";
    
    /** SV_AVAILABLE_MAP constant. */
    private static final String SV_AVAILABLE_MAP = "svAvailableMap";
    
    /** OUTPUT constant */
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");    
    /** ERROR constant */
    private static final ParameterName ERROR = ParameterName.getParameterName("error");
    
    /** repository property */
    private Repository mRepository;

    /** profile property */
    private Profile mProfile;
    
    /** Repository Inventory Manager property */
    private CastRepositoryInventoryManager mRepositoryInventoryManager;

    /**
     * Depending on input parameter 'action' value can add or remove items from ShoppingListBean
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
        throws ServletException, IOException {
        
        String action = pRequest.getParameter(PARAM_ACTION);
        String skuIds = pRequest.getParameter(PARAM_SKU_ID);
        RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(PARAM_STORE);
        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);
        
        String[] skuArray = {};
        if (skuIds != null) {
        	skuArray = skuIds.split(",");
        }
        
        if (ACTION_ADD.equals(action)) {
        	List<String> notAddSkus = null;
        	notAddSkus = addProductToShoppingList(skuArray);
        	if (notAddSkus != null && notAddSkus.size() > 0) {        		
        		pRequest.setParameter(NOT_PROCESSED_SKUS, listToStringWithComma(notAddSkus));
                pRequest.serviceParameter(ERROR, pRequest, pResponse);
        	}
        } else if (ACTION_REMOVE.equals(action)) {
            if (skuIds != null) {
            	List<String> notRemSkus = null;
            	notRemSkus = removeProductFromShoppingList(skuArray);
            	if (notRemSkus != null && notRemSkus.size() > 0) {        		
            		pRequest.setParameter(NOT_PROCESSED_SKUS, listToStringWithComma(notRemSkus));
                    pRequest.serviceParameter(ERROR, pRequest, pResponse);
            	}
            } else {
                if (isLoggingError()) {
                    logError("'skuIds' param is required in case of 'remove' action.");
                }
                throw new ServletException("'skuIds' param is required in case of 'remove' action.");
            }            
        } else {
            List<RepositoryItem> result = new ArrayList<RepositoryItem>();
            
            List<RepositoryItem> shoppingList = (List<RepositoryItem>)getProfile().getPropertyValue(PROPERTY_SHOPPING_LIST);
            for (RepositoryItem skuItem: shoppingList) {
				// store parameter
                // svAvailableMap parameter
                StockLevelStatus stockLevelStatus = getRepositoryInventoryManager().inventoryStockLevelStatus(skuItem, store, svAvailableMap);
				if (stockLevelStatus == StockLevelStatus.S0) {
					if (isLoggingDebug()) {
						logDebug("sku with id=" + skuItem.getPropertyValue("id") + " is not available in stock");
					}
				    continue;
				}
				result.add(skuItem);
            }
            
            pRequest.setParameter(PARAM_AMOUNT, result.size());
            pRequest.setParameter(PARAM_LIST_PRODUCTS, result);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        }
    }
    
    @SuppressWarnings("unchecked")
	private List<String> removeProductFromShoppingList(String[] remSkuIds) {
		List<RepositoryItem> shoppingList = (List<RepositoryItem>)getProfile().getPropertyValue(PROPERTY_SHOPPING_LIST);
		List<String> notRemItemSkus = new ArrayList<String>();
		notRemItemSkus.addAll(Arrays.asList(remSkuIds));
		for (String skuId: remSkuIds) {
			try {
				RepositoryItem skuItem = getRepository().getItem(skuId, TYPE_CASTO_SKU);
				if (shoppingList.contains(skuItem)) {
					shoppingList.remove(skuItem);
					if (isLoggingDebug()) {
						logDebug("Removed SKU with id = " + skuId + " from shopping list");
					}
					notRemItemSkus.remove(skuId);
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e.getMessage());
				}
			}
		}
		return notRemItemSkus;
	}

    @SuppressWarnings("unchecked")
	private List<String> addProductToShoppingList(String[] addSkuIds) {
		List<RepositoryItem> shoppingList = (List<RepositoryItem>)getProfile().getPropertyValue(PROPERTY_SHOPPING_LIST);
		List<String> notAddItemSkus = new ArrayList<String>();
		notAddItemSkus.addAll(Arrays.asList(addSkuIds));
		for (String skuId: addSkuIds) {
			try {
				RepositoryItem skuItem = getRepository().getItem(skuId, TYPE_CASTO_SKU);
				if (!shoppingList.contains(skuItem)) {
					shoppingList.add(skuItem);
					if (isLoggingDebug()) {
						logDebug("Added SKU with id = " + skuId + " to shopping list");
					}
					notAddItemSkus.remove(skuId);
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e.getMessage());
				}
			}
		}
		return notAddItemSkus;
	}
	
	private String listToStringWithComma(List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<list.size(); i++) {
			builder.append(list.get(i));
			if (i != list.size() - 1) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	/**
     * Returns repository property
     *
     * @return the repository property
     */
    public Repository getRepository() {
        return mRepository;
    }
    
    /**
     * Sets repository property
     *
     * @param pRepository the repository to set
     */
    public void setRepository(Repository pRepository) {
        this.mRepository = pRepository;
    }
    
    /**
     * Returns profile property
     *
     * @return the profile property
     */
    public Profile getProfile() {
        return mProfile;
    }
    
    /**
     * Sets profile property
     *
     * @param pProfile the profile to set
     */
    public void setProfile(Profile pProfile) {
        this.mProfile = pProfile;
    }
    
    /**
     * Returns repositoryInventoryManager property
     *
     * @return the repositoryInventoryManager property
     */
    public CastRepositoryInventoryManager getRepositoryInventoryManager() {
        return mRepositoryInventoryManager;
    }

    /**
     * Sets repositoryInventoryManager property
     *
     * @param pRepositoryInventoryManager the repositoryInventoryManager to set
     */
    public void setRepositoryInventoryManager(CastRepositoryInventoryManager pRepositoryInventoryManager) {
        mRepositoryInventoryManager = pRepositoryInventoryManager;
    }
}
