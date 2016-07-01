package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

/**
 * Calculate list and sale price of the packs. Sort them by the price with ascending order
 * 
 * @author Alena_Karpenkava
 * 
 */
public class CastSortPackByPriceDroplet extends DynamoServlet {

	/** Output parameters */
	static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	static final ParameterName EMPTY = ParameterName.getParameterName("empty");
	static final String ERROR = "error";
	static final String PACK_LIST = "packList";

	/** Input parameters */
	static final String PACKS = "packs";

	/** Constants */
	static final String ITEM = "item";
	static final String CHILD_SKUS = "childSKUs";
	static final String BUNDLE_LINKS = "bundleLinks";
	static final String TYPE_ARTICLE = "typeArticle";
	static final String REPOSITORY_ID = "id";
	static final String STOCK_LEVEL = "stockLevel";
	static final String PRICE_LIST = "priceList";
	static final String SALE_PRICE_LIST = "salePriceList";
	
	/** STORE constant. */
    static final String STORE = "store";
    
    /** SV_AVAILABLE_MAP constant. */
    static final String SV_AVAILABLE_MAP = "svAvailableMap";
    

	/** Price list manager */
	private PriceListManager mPriceListManager;
	/** Inventory manager */
	private CastRepositoryInventoryManager mInventoryManager;

	/**
	 * @param pPriceListManager
	 *            the mPriceListManager to set
	 */
	public void setPriceListManager(PriceListManager pPriceListManager) {
		mPriceListManager = pPriceListManager;
	}

	/**
	 * @return the mPriceListManager
	 */
	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}

	/**
	 * @return the mInventoryManager
	 */
	public CastRepositoryInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * @param pInventoryManager
	 *            the mInventoryManager to set
	 */
	public void setInventoryManager(CastRepositoryInventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}

	/**
	 * Calculate list and sale price of the packs. Sort them by the price with ascending order.
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

		if (plManager != null) {
			try {
				Exception error = null;
				Set packsSet = (Set) pRequest.getObjectParameter(PACKS);
				if (packsSet != null) {
				    
				    RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE);
			        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);
				    
					List packs = new ArrayList(packsSet);
					List<RepositoryItem> availablePacks = availablePacks(packs, plManager, store, svAvailableMap);

					List<RepositoryItem> packList = new ArrayList<RepositoryItem>();
					List<Double> packPrices = new ArrayList<Double>();

					if (availablePacks != null) {

						RepositoryItem profile = ServletUtil.getCurrentUserProfile();
						RepositoryItem priceList = plManager.getPriceList(profile, PRICE_LIST);
						RepositoryItem salePriceList = plManager.getPriceList(profile, SALE_PRICE_LIST);

						for (int i = 0; i < availablePacks.size(); i++) {
							RepositoryItem pack = availablePacks.get(i);
							List skus = (List<RepositoryItem>) pack.getPropertyValue(CHILD_SKUS);
							if (skus != null && skus.size() > 0) {
								RepositoryItem sku = (RepositoryItem) skus.get(0);

								RepositoryItem lPrice = null;
								RepositoryItem sPrice = null;

								String repositoryId = sku.getRepositoryId();
								if (priceList != null) {
									lPrice = plManager.getPrice(priceList, null, repositoryId);
								}
								if (salePriceList != null) {
									sPrice = plManager.getPrice(salePriceList, null, repositoryId);
								}
								Double packListPrice = 0.0;
								Double packSalePrice = 0.0;

								if (lPrice != null) {
									Object oPrice = lPrice.getPropertyValue(plManager.getListPricePropertyName());
									if (oPrice != null) {
										packListPrice = (Double) oPrice;
									}
								}
								if (sPrice != null) {
									Object oPrice = sPrice.getPropertyValue(plManager.getListPricePropertyName());
									if (oPrice != null) {
										if (oPrice != null) {
											packSalePrice = (Double) oPrice;
										}
									}
								} else {
									packSalePrice = packListPrice;
								}

								boolean added = false;
								if (packPrices.isEmpty()) {
									packPrices.add(packSalePrice);
									packList.add(pack);
								} else {
									for (int j = 0; j < packPrices.size(); j++) {
										if ((Double) packPrices.get(j) > packSalePrice) {
											packPrices.add(j, packSalePrice);
											packList.add(j, pack);
											added = true;
											break;
										}
									}
									if (!added) {
										packPrices.add(packSalePrice);
										packList.add(pack);
									}
								}
							}
						}
						pRequest.setParameter(PACK_LIST, packList);

						if ((error != null) && (isLoggingError())) {
							logError(error);
						}
						pRequest.setParameter(ERROR, error);
						if (packList == null) {
							pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
						} else {
							pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
						}
					}
				}
			} catch (PriceListException e) {
				logError("PriceListException", e);
			}
		}
	}

	/**
	 * Filter packs by "type article" and "stock level".
	 * 
	 * @param packs -
	 *            list of packs.
	 * @return list of the available packs.
	 * @throws PriceListException
	 */
	private List availablePacks(List<RepositoryItem> packs, PriceListManager plManager, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) 
	        throws PriceListException {
		List<RepositoryItem> availablePacks = new ArrayList<RepositoryItem>();

		if (packs != null) {
			for (RepositoryItem packItem : packs) {
				List<RepositoryItem> skus = (List<RepositoryItem>) packItem.getPropertyValue(CHILD_SKUS);
				if (skus != null && skus.size() > 0) {
					RepositoryItem packSku = skus.get(0);
					String skuRepositoryId = packSku.getRepositoryId();
					List<RepositoryItem> bundledLinks = (List<RepositoryItem>) packSku.getPropertyValue(BUNDLE_LINKS);
					boolean available = false;
					if (getInventoryManager() != null) {
					    // store parameter
					    // svAvailableMap parameter
					    StockLevelStatus stockLevelStatusForPack = getInventoryManager().inventoryStockLevelStatusForPack(
								bundledLinks, packSku, pStore, svAvailableMap);
						switch (stockLevelStatusForPack) {
						case S0:
							available = false;
							break;
						case S1:
							available = true;
							break;
						case S2:
							available = false;
							break;
						case S3:
							available = true;
							break;
						case S4:
							available = true;
							break;
						case S5:
							available = true;
							break;
						case S6:
                            available = true;
                            break;
						case S7:
                            available = true;
                            break;
						case S8:
                            available = true;
                            break;
						case S9:
                            available = true;
                            break;
						case S10:
                            available = true;
                            break;
						}
					}
					if (available) {
						RepositoryItem profile = ServletUtil.getCurrentUserProfile();
						RepositoryItem priceList = plManager.getPriceList(profile, PRICE_LIST);
						if (priceList != null) {
							boolean availableSku = true;
							for (RepositoryItem o : bundledLinks) {
								RepositoryItem sku = (RepositoryItem) o.getPropertyValue(ITEM);
								String repositoryId = sku.getRepositoryId();
								RepositoryItem lPrice = plManager.getPrice(priceList, null, repositoryId);
								if (lPrice == null) {
									availableSku = false;
									break;
								}
							}
							if (availableSku) {
								availablePacks.add(packItem);
							}
						}
					}
				}
			}
		}
		return availablePacks;
	}
}