package com.castorama.droplet;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.inventory.InventoryException;

import atg.commerce.order.CommerceItem;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.utils.StoreTools;

/**
 * Determine whether order contains "immediate withdrawal" items. Return true if order
 * contains "immediate withdrawal" products, otherwise false.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class StockAvailabilityOrderProductsDroplet extends DynamoServlet {
    /*
     * Constants
     */
    /** TRUE constant. */
    public static final String TRUE = "true";

    /** FALSE constant. */
    public static final String FALSE = "false";

    /** EXAMINED_ORDER constant. */
    public static final String EXAMINED_ORDER = "examinedOrder";

    /*
     * Properties
     */
    /** inventoryManager property */
    private CastRepositoryInventoryManager mInventoryManager;

    /** catalogTools property. */
    private CatalogTools mCatalogTools;
    
    /** Store tools. */
    private StoreTools storeTools;

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
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }
    
    /**
     * @return the storeTools
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * @param storeTools the storeTools to set
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }

    /**
     * Render true if order contains "immediate withdrawal" products, otherwise
     * returns false. order - is input parameter.
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        CastRepositoryInventoryManager inventoryManager = getInventoryManager();
        Object orderObj = pRequest.getObjectParameter(EXAMINED_ORDER);
        boolean result = false;
        if ((inventoryManager != null) && (orderObj != null) && (orderObj instanceof CastOrderImpl) &&
                (getCatalogTools() != null)) {
            CastOrderImpl order = (CastOrderImpl) orderObj;
            List<CommerceItem> commerceItems = order.getCommerceItems();
            if ((commerceItems != null) && !commerceItems.isEmpty()) {
                for (CommerceItem ci : commerceItems) {
                    String skuId = ci.getCatalogRefId();
                    try {
                        RepositoryItem ri = getCatalogTools().findSKU(skuId);
                        RepositoryItem store = getStoreTools().getStore(((CastOrderImpl) order).getMagasinId());
                        
                        // store parameter - no need to process c&c statuses (isCCOrder parameter on jsp)
                        //TODO svAvailableMap parameter
                        if (ri != null) {
                            StockLevelStatus stockStatus = inventoryManager.inventoryStockLevelStatus(ri, store, null);
                            long stockLevel = 0;
                            try {
                                stockLevel = inventoryManager.queryStockLevel(skuId);
                            } catch (InventoryException e) {
                                if (isLoggingError()) {
                                    logError("Inventory exception for sku : " + skuId + " - " + e.getMessage());
                                }
                            }
                            if ((stockStatus == StockLevelStatus.S1 && stockLevel != -1) || (stockStatus == StockLevelStatus.S6)) {
                                result = true;
                                break;
                            }
                        }
                    } catch (RepositoryException e) {
                        if (isLoggingError()) {
                            logError(e.getMessage());
                        }
                        result = false;
                    }
                }
            }
        }  // end if
        if (result) {
            pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
        }
    }

}
