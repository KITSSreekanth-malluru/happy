package com.castorama.commerce.order.processor;

import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.order.CastOrderImpl;

import java.util.HashMap;
import java.util.List;

/**
 * Processor for saving inventory in DB.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProcUpdateInventory extends GenericService implements PipelineProcessor {
    /**
     * RESOURCE_NAME constant.
     */
    private static final String RESOURCE_NAME = "com.castorama.commerce.order.CastOrderResources";
    /**
     * INVENTORY_DECREASE_EXCEPTION constant.
     */
    private static final String INVENTORY_DECREASE_EXCEPTION = "inventoryDecreaseException";
    /**
     * LOCAL_STOCK_DECREASE_EXCEPTION constant.
     */
    private static final String LOCAL_STOCK_DECREASE_EXCEPTION = "localStockDecreaseException";
    /**
     * NEED_UPDATE_INVENTORY constant.
     */
    private static final String NEED_UPDATE_INVENTORY = "needUpdateInventory";
    /**
     * CC_RETRAIT constant.
     */
    private static final String CC_RETRAIT = "clickAndCollect";
    /**
     * sResourceBundle property
     */
    private static java.util.ResourceBundle sResourceBundle =
            java.util.ResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
    /**
     * SUCCESS constant.
     */
    private final int SUCCESS = 1;

    /* (non-Javadoc)
     * @see atg.service.pipeline.PipelineProcessor#getRetCodes()
     */
    public int[] getRetCodes() {
        int[] retCodes = {SUCCESS};
        return retCodes;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
     */
    public int runProcess(Object pParam, PipelineResult pResult) throws Exception {
        HashMap map = (HashMap) pParam;
        CastOrderImpl order = (CastOrderImpl) map.get(PipelineConstants.ORDER);
        Boolean needUpdateInventory = (Boolean) map.get(NEED_UPDATE_INVENTORY);
        CastRepositoryInventoryManager invManager = (CastRepositoryInventoryManager) map.get(PipelineConstants.INVENTORYMANAGER);

        if (order.getCommerceItemCount() > 0 && (needUpdateInventory != null && needUpdateInventory)) {
            List<CommerceItem> cis = order.getCommerceItems();
            if (CC_RETRAIT.equals(order.getDeliveryType())) {
                for (CommerceItem ci : cis) {
                    if (!invManager.purchaseLocalStock(ci.getCatalogRefId(), ci.getQuantity(), order.getMagasinId())) {
                        String eMessage = sResourceBundle.getString(LOCAL_STOCK_DECREASE_EXCEPTION);
                        pResult.addError(LOCAL_STOCK_DECREASE_EXCEPTION, eMessage);
                        if (isLoggingError()) {
                            vlogError("Error in ProcUpdateInventory.runProcess: purchaseLocalStock return false for" +
                                    " CatalogRefId = {0}, quantity = {1}, magasinId = {2} ",
                                    ci.getCatalogRefId(), ci.getQuantity(), order.getMagasinId());
                        }
                    }
                }
            } else {
                try {
                    for (CommerceItem ci : cis) {
                        invManager.purchase(ci.getCatalogRefId(), ci.getQuantity());
                    }
                } catch (InventoryException ie) {
                    String eMessage = sResourceBundle.getString(INVENTORY_DECREASE_EXCEPTION);
                    pResult.addError(INVENTORY_DECREASE_EXCEPTION, eMessage);
                    if (isLoggingError()) {
                        logError("InventoryException in ProcUpdateInventory.runProcess: ", ie);
                    }
                }
            }
        }

        return SUCCESS;
    }

}
