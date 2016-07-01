package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

/**
 * Sort multiple SKU product's drop down values by the price with ascending order.
 * 
 * @author Alena_Karpenkava
 * 
 */
public class CastSortSkuDropDownDroplet extends DynamoServlet {

	/** Output parameters */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final ParameterName EMPTY = ParameterName.getParameterName("empty");
	static final String ERROR = "error";
	static final String SKU_LIST = "skuList";

	/** Constants */
	static final String PRICE_LIST = "priceList";
	static final String SALE_PRICE_LIST = "salePriceList";
	static final String PRICE_LIST_EXCEPTION = "PriceListException";

	/** Input parameters */
	static final String CHILD_SKUS = "childSKUs";
	static final String IS_MULTI_SKU = "isMultiSku";
	
	/** STORE constant. */
    static final String STORE = "store";
    
    /** SV_AVAILABLE_MAP constant. */
    static final String SV_AVAILABLE_MAP = "svAvailableMap";
	
	/** Price list manager */
	private PriceListManager mPriceListManager;

	/**
	 * @param pPriceListManager
	 *            the mPriceListManager to set
	 */
	public void setPriceListManager(PriceListManager pPriceListManager) {
		mPriceListManager = pPriceListManager;
	}

	/**
	 * @return mPriceListManager
	 */
	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}
	
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
	 * Sort multiple SKU product's drop down values by the price with ascending order.
	 * 
	 * @param pRequest -
	 *            request
	 * @param pResponse -
	 *            response
	 * @throws IOException -
	 *             exception
	 * @throws ServletException -
	 *             exception
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		PriceListManager plManager = getPriceListManager();
		CastRepositoryInventoryManager inventoryManager = getInventoryManager();
		
		if (plManager != null) {
			try {
				Exception error = null;
				List skus = (List) pRequest.getObjectParameter(CHILD_SKUS);
				Boolean isMultiSku = pRequest.getObjectParameter(IS_MULTI_SKU) == null ? false : new Boolean((String)pRequest.getObjectParameter(IS_MULTI_SKU));
				
				RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE);
		        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);
		        
				if (skus != null) {

					List<RepositoryItem> skuList = new ArrayList<RepositoryItem>();
					List<Double> skuPrices = new ArrayList<Double>();

					RepositoryItem profile = ServletUtil.getCurrentUserProfile();
					RepositoryItem priceList = plManager.getPriceList(profile, PRICE_LIST);
					RepositoryItem salePriceList = plManager.getPriceList(profile, SALE_PRICE_LIST);

					for (int i = 0; i < skus.size(); i++) {
						RepositoryItem sku = (RepositoryItem) skus.get(i);

						RepositoryItem lPrice = null;
						RepositoryItem sPrice = null;

						String repositoryId = sku.getRepositoryId();
						if (priceList != null) {
							lPrice = plManager.getPrice(priceList, null, repositoryId);
						}
						if (salePriceList != null) {
							sPrice = plManager.getPrice(salePriceList, null, repositoryId);
						}
						Double skuListPrice = 0.0;
						Double skuSalePrice = 0.0;

						if (lPrice != null) {
							Object oPrice = lPrice.getPropertyValue(plManager.getListPricePropertyName());
							if (oPrice != null) {
								skuListPrice = (Double) oPrice;
							}
						}
						if (sPrice != null) {
							Object oPrice = sPrice.getPropertyValue(plManager.getListPricePropertyName());
							if (oPrice != null) {
								if (oPrice != null) {
									skuSalePrice = (Double) oPrice;
								}
							}
						} else {
							skuSalePrice = skuListPrice;
						}
						if (inventoryManager != null) {
				            // store parameter
				            // svAvailableMap parameter
							if (!isMultiSku || (isMultiSku && 
							        inventoryManager.inventoryStockLevelStatusSKU((String) sku.getRepositoryId(), store, svAvailableMap) != StockLevelStatus.S0)) {
    							boolean added = false;
    							if (skuPrices.isEmpty()) {
    								skuPrices.add(skuSalePrice);
    								skuList.add(sku);
    							} else {
    								for (int j = 0; j < skuPrices.size(); j++) {
    									if ((Double) skuPrices.get(j) > skuSalePrice) {
    										skuPrices.add(j, skuSalePrice);
    										skuList.add(j, sku);
    										added = true;
    										break;
    									}
    								}
    								if (!added) {
    									skuPrices.add(skuSalePrice);
    									skuList.add(sku);
    								}
    							}
							}
						}
					}
					pRequest.setParameter(SKU_LIST, skuList);

					if ((error != null) && (isLoggingError())) {
						logError(error);
					}
					pRequest.setParameter(ERROR, error);
					if (skuList == null) {
						pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
					} else {
						pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
					}
				}
			} catch (PriceListException e) {
				logError(PRICE_LIST_EXCEPTION, e);
			}
		}
	}
}
