package com.castorama.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatusAndAvail;

/**
 * Check availability of current product or sku on "Stock Calculation rules"
 * basis and render suitable oparam.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class StockAvailabilityDroplet extends DynamoServlet {
	/*
     * Constants
     */

	/** SKU_ID parameter name. */
	private static final String SKU_ID = "skuId";

    /** IS_MULTI_SKU_PRODUCT parameter name. */
    private static final String IS_MULTI_SKU_PRODUCT = "isMultiSkuProduct";
    
    /** IS_MODIFIED_VERSION parameter name. */
    private static final String IS_MODIFIED_VERSION = "isModifiedVersion";

    /** IMMIDIATE_WITHDRAWAL constant. */
    public static final String IMMIDIATE_WITHDRAWAL = "immidiateWithdrawal";

    /** REMAINING_STOCK constant. */
    public static final String REMAINING_STOCK = "remainingStock";
    
    /** CC_REMAINING_STOCK constant. */
    public static final String CC_REMAINING_STOCK = "ccRemainingStock";

    /** SOLD_ONLY_IN_STORE constant. */
    public static final String SOLD_ONLY_IN_STORE = "soldOnlyInStore";
    
    /** CC_SOLD_ONLY_IN_STORE constant. */
    public static final String CC_SOLD_ONLY_IN_STORE = "ccSoldOnlyInStore";

    /** DIRECT_SUPPLIER constant. */
    public static final String DIRECT_SUPPLIER = "directSupplier";

    /** LONG_DELIVERY_TIME constant. */
    public static final String LONG_DELIVERY_TIME = "longDeliveryTime";

    /** REGULAR_DELIVERY_TIME constant. */
    public static final String REGULAR_DELIVERY_TIME = "regularLongDeliveryTime";

    /** DELIVERY_TIME constant. */
    public static final String DELIVERY_TIME = "deliveryTime";
    
    /** CC_DELIVERY_TIME constant. */
    public static final String CC_DELIVERY_TIME = "ccDeliveryTime";

    /** DEFAULT constant. */
    public static final String DEFAULT = "default";

    /** PRODUCT constant. */
    public static final String PRODUCT = "product";

    /** SKU constant. */
    public static final String SKU = "sku";
    
    /** STORE constant. */
    public static final String STORE = "store";
    
    /** SV_STOCK_AVAILABLE constant. */
    public static final String SV_STOCK_AVAILABLE = "svStockAvailable";
    
    /** SV_AVAILABLE_MAP constant. */
    public static final String SV_AVAILABLE_MAP = "svAvailableMap";
    

    /*
     * Properties
     */
    /** inventoryManager property */
    private CastRepositoryInventoryManager mInventoryManager;

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
        CastRepositoryInventoryManager inventoryManager = getInventoryManager();
        RepositoryItem product = (RepositoryItem) pRequest.getLocalParameter(PRODUCT);
        RepositoryItem sku = (RepositoryItem) pRequest.getObjectParameter(SKU);
        String skuId = (String) pRequest.getObjectParameter(SKU_ID);
        RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE);
        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);
        Boolean isMultiSkuProduct = pRequest.getObjectParameter(IS_MULTI_SKU_PRODUCT)!=null ? Boolean.valueOf(pRequest.getParameter(IS_MULTI_SKU_PRODUCT)) : null;
        Boolean isModifiedVersion = pRequest.getObjectParameter(IS_MODIFIED_VERSION)!=null ? Boolean.valueOf(pRequest.getParameter(IS_MODIFIED_VERSION)) : false;
        if (inventoryManager != null) {
            StockLevelStatusAndAvail stockLevelStatus = inventoryManager.new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
            
            inventoryManager.setModifiedVersion(isModifiedVersion);
            
            // store parameter
            // svAvailableMap parameter
            if (isMultiSkuProduct != null && isMultiSkuProduct && product != null) {
                stockLevelStatus = inventoryManager.inventoryStockLevelStatusCC(product, store, svAvailableMap);
            } else if (sku != null) {
                stockLevelStatus = inventoryManager.inventoryStockLevelStatusCC(sku, store, svAvailableMap);
            } else if (product != null) {
                stockLevelStatus = inventoryManager.inventoryStockLevelStatusCC(product, store, svAvailableMap);
            } else if (skuId != null) {
            	stockLevelStatus = inventoryManager.inventoryStockLevelStatusSKUCC(skuId, store, svAvailableMap);
            }
            switch (stockLevelStatus.getStockLevelStatus()) {
                case S0:
                    pRequest.serviceLocalParameter(IMMIDIATE_WITHDRAWAL, pRequest, pResponse);
                    break;

                case S1:
                    pRequest.serviceLocalParameter(REMAINING_STOCK, pRequest, pResponse);
                    break;

                case S2:
                    pRequest.serviceLocalParameter(SOLD_ONLY_IN_STORE, pRequest, pResponse);
                    break;

                case S3:
                    pRequest.serviceLocalParameter(DELIVERY_TIME, pRequest, pResponse);
                    break;

                case S4:
                    pRequest.serviceLocalParameter(DELIVERY_TIME, pRequest, pResponse);
                    break;

                case S5:
                    pRequest.serviceLocalParameter(DELIVERY_TIME, pRequest, pResponse);
                    break;
                    
                case S6:
                    pRequest.setParameter(SV_STOCK_AVAILABLE, stockLevelStatus.getSvStockAvailable());
                    pRequest.serviceLocalParameter(CC_REMAINING_STOCK, pRequest, pResponse);
                    break;
                    
                case S7:
                    pRequest.setParameter(SV_STOCK_AVAILABLE, stockLevelStatus.getSvStockAvailable());
                    pRequest.serviceLocalParameter(CC_SOLD_ONLY_IN_STORE, pRequest, pResponse);
                    break;
                    
                case S8:
                    pRequest.setParameter(SV_STOCK_AVAILABLE, stockLevelStatus.getSvStockAvailable());
                    pRequest.serviceLocalParameter(CC_DELIVERY_TIME, pRequest, pResponse);
                    break;
                    
                case S9:
                    pRequest.setParameter(SV_STOCK_AVAILABLE, stockLevelStatus.getSvStockAvailable());
                    pRequest.serviceLocalParameter(CC_DELIVERY_TIME, pRequest, pResponse);
                    break;
                    
                case S10:
                    pRequest.setParameter(SV_STOCK_AVAILABLE, stockLevelStatus.getSvStockAvailable());
                    pRequest.serviceLocalParameter(CC_DELIVERY_TIME, pRequest, pResponse);
                    break;    

                default:
                    pRequest.serviceLocalParameter(DEFAULT, pRequest, pResponse);
                    break;
            }
        }  // end if

    }
}
