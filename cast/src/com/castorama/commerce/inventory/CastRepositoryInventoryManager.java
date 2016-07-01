package com.castorama.commerce.inventory;

import static com.castorama.utils.CastUtils.buildSVSProductId;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.castorama.commerce.localStores.LocalStoresManager;
import com.castorama.stockvisualization.RemoteStockCacheAccessor;
import com.castorama.stockvisualization.StockModel;
import com.castorama.stockvisualization.StockVisualizationException;
import com.castorama.stockvisualization.StockVisualizationManager;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.RepositoryInventoryManager;

import atg.core.util.StringUtils;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Extension of repository inventory manager for maintaining "Stock Calculation
 * Rules" functionality
 *
 * @author Katsiaryna Sharstsiuk
 * 
 */
public class CastRepositoryInventoryManager extends RepositoryInventoryManager {
    /** PACK constant. */
    private static final String PACK = "pack";

    /** PRODUCT constant. */
    private static final String PRODUCT = "product";

    /** CHILD_SKUS constant. */
    private static final String CHILD_SKUS = "childSkus";

    /** BUNDLE_LINKS constant. */
    private static final String BUNDLE_LINKS = "bundleLinks";

    /** SKU constant. */
    private static final String SKU = "sku";
    
    /** CODE_ARTICLE constant. */
    private static final String CODE_ARTICLE = "codeArticle";

    /** QUANTITE_LIMITEE constant. */
    private static final String QUANTITE_LIMITEE = "quantiteLimitee";

    /** RETRAIT_MOMENTANE_MOTIFS_CODIFIES constant. */
    private static final String RETRAIT_MOMENTANE_MOTIFS_CODIFIES = "retraitMomentaneMotifsCodifies";

    /** ITEM constant. */
    private static final String ITEM = "item";
    
    /** QUANTITY constant. */
    private static final String QUANTITY = "quantity";

    /** DATE_FIN_DESTOCK constant. */
    private static final String DATE_FIN_DESTOCK = "dateFinDestock";

    /** DATE_DEB_DESTOCK constant. */
    private static final String DATE_DEB_DESTOCK = "dateDebDestock";

    /** TYPE_ARTICLE constant. */
    private static final String TYPE_ARTICLE = "typeArticle";

    /** DATE_FIN_VIE constant. */
    private static final String DATE_FIN_VIE = "dateFinVie";

    /** DELIVERY_TYPE constant. */
    public static final String DELIVERY_TYPE = "typeExpedition";

    /** DELIVERY_BY_PROVIDER constant. */
    public static final int DELIVERY_BY_PROVIDER = 2;

    /** DELIVERY_BY_STORE constant. */
    public static final int DELIVERY_BY_STORE = 3;
    
    /** CC_STORE constant. */
    public static final String CC_STORE = "retraitMagasin";
    
    /** LOCAL_STORE constant. */
    public static final String LOCAL_STORE = "localPrix";
    
    /** STORE_ID constant. */
    public static final String STORE_ID = "storeId";
    
    /** CASTORAMA_DIRECT_ID constant. */
    public static final String CASTORAMA_DIRECT_ID = "999";
    
    /** QUANTITY_TO_QUERY_SV constant. */
    public static final String QUANTITY_TO_QUERY_SV = "1";
    
    /** SV_PROD_DELIMETER constant. */
    public static final String SV_PROD_DELIMETER = ",";
    
    /** STATUS_REDUCE_SUCCESS constant. */
    public static final String STATUS_REDUCE_SUCCESS = "101";
    
    /** SV stock level status enum. */
    public static enum StockLevelStatus {
        S0(0),S1(1),S2(2),S3(3),S4(4),S5(5),S6(6),S7(7),S8(8),S9(9),S10(10);
        
        private int statusNumber;
        
        private StockLevelStatus(int statusNumber) {
            this.statusNumber = statusNumber;
        }
        
        public int getStatusNumber() {
            return statusNumber;
        }
    }
    
    /** SV stock level status class for encapsulating status and availability info. */
    public class StockLevelStatusAndAvail {
        private StockLevelStatus stockLevelStatus;
        private int svStockAvailable;
        
        public StockLevelStatus getStockLevelStatus() {
            return stockLevelStatus;
        }
        
        public int getSvStockAvailable() {
            return svStockAvailable;
        }
        
        public StockLevelStatusAndAvail(StockLevelStatus stockLevelStatus, int svStockAvailable) {
            this.stockLevelStatus = stockLevelStatus;
            this.svStockAvailable = svStockAvailable;
        }
    }

    /** This parameter shows that inventoryStockLevelStatus calculate in a new way.*/  
    private Boolean modifiedVersion;
    
    /** Number of month to compare withdrawal date */
    private int numberOfMonthToCompareWithdrawalDate = 3;

    /** remoteStockCacheAccessor property */
    private RemoteStockCacheAccessor mRemoteStockCacheAccessor;

    /**
     * Determines  item stock level status
     *
     * @param  pItem parameter
     *
     * @return StockLevelStatus which determines item stock level status
     */
    public StockLevelStatus inventoryStockLevelStatus(RepositoryItem pItem, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
        return inventoryStockLevelStatusCC(pItem, pStore, svAvailableMap).getStockLevelStatus();
    }
    
	@SuppressWarnings("unchecked")
    public StockLevelStatusAndAvail inventoryStockLevelStatusCC(RepositoryItem pItem, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
	    StockLevelStatusAndAvail result = new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
        try {
            if (pItem != null) {
                String itemDescriptorName = pItem.getItemDescriptor().getItemDescriptorName();

                List<RepositoryItem> childSkus = null;
                List<RepositoryItem> bundleLinks = null;
                RepositoryItem bundleLinkSku = null;
                if (!StringUtils.isBlank(itemDescriptorName)) {
                    if (itemDescriptorName.contains(PRODUCT) || itemDescriptorName.contains(PACK)) {
                        childSkus = (List<RepositoryItem>) pItem.getPropertyValue(CHILD_SKUS);
                        if ((childSkus != null) && !childSkus.isEmpty()) {
                            bundleLinkSku = childSkus.get(0);
                            bundleLinks = (List) bundleLinkSku.getPropertyValue(BUNDLE_LINKS);
                        }

                    } else if (itemDescriptorName.contains(SKU)) {
                        bundleLinkSku = pItem;
                        bundleLinks = (List) pItem.getPropertyValue(BUNDLE_LINKS);
                        if ((bundleLinks == null) || bundleLinks.isEmpty()) {
                            childSkus = new ArrayList();
                            childSkus.add(pItem);
                        }
                    }
                }

                if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
                    result = inventoryStockLevelStatusForPackCC(bundleLinks, bundleLinkSku, pStore, svAvailableMap);
                } else if ((childSkus != null) && !childSkus.isEmpty()) {
                    result = inventoryStockLevelStatusRegularProduct(childSkus, pStore, svAvailableMap);
                }

            }  // end if
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e.getMessage(), e);
            }
            result = new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
        }  // end try-catch
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public StockLevelStatus inventoryStockLevelStatusForIndexingOnly(RepositoryItem pItem, RepositoryItem pStore, 
            Set<String> webNotAvailable, Map<Integer, Integer> svAvailable, Set<String> excludedSkus) {
        StockLevelStatus result = StockLevelStatus.S0;
        try {
            if (pItem != null) {
                String itemDescriptorName = pItem.getItemDescriptor().getItemDescriptorName();

                List<RepositoryItem> childSkus = null;
                List<RepositoryItem> bundleLinks = null;
                RepositoryItem bundleLinkSku = null;
                if (!StringUtils.isBlank(itemDescriptorName)) {
                    if (itemDescriptorName.contains(PRODUCT)) {
                        childSkus = (List<RepositoryItem>) pItem.getPropertyValue(CHILD_SKUS);
                        if ((childSkus != null) && !childSkus.isEmpty()) {
                            bundleLinkSku = childSkus.get(0);
                            bundleLinks = (List) bundleLinkSku.getPropertyValue(BUNDLE_LINKS);
                        }

                    } else if (itemDescriptorName.contains(SKU)) {
                        bundleLinkSku = pItem;
                        bundleLinks = (List) pItem.getPropertyValue(BUNDLE_LINKS);
                        if ((bundleLinks == null) || bundleLinks.isEmpty()) {
                            childSkus = new ArrayList();
                            childSkus.add(pItem);
                        }
                    }
                }

                if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
                    result = inventoryStockLevelStatusForGpForIndexingOnly(bundleLinks, bundleLinkSku, 
                            pStore, webNotAvailable, svAvailable, excludedSkus);
                } else if ((childSkus != null) && !childSkus.isEmpty()) {
                    result = inventoryStockLevelStatusRegularProductForIndexingOnly(childSkus, 
                            pStore, webNotAvailable, svAvailable, excludedSkus);
                }

            }  // end if
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e.getMessage(), e);
            }
            result = StockLevelStatus.S0;
        }  // end try-catch
        return result;
    }

    /**
     * Get Inventory Stock level by skuId.
     * @param pSkuId - id of the searching sku
     * @return inventory level of the sku
     */
	public StockLevelStatus inventoryStockLevelStatusSKU(String pSkuId, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
	    return inventoryStockLevelStatusSKUCC(pSkuId, pStore, svAvailableMap).getStockLevelStatus();
	}
	
    public StockLevelStatusAndAvail inventoryStockLevelStatusSKUCC(String pSkuId, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
    	RepositoryItem sku = null;
		try {
			sku = getCatalogRefItem(pSkuId);
		} catch (RepositoryException e) {
			return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
		}
    	return inventoryStockLevelStatusSKU(sku, pStore, svAvailableMap);
    }

    /**
     * Determines  item stock level status
     *
     * @param  pSku parameter
     *
     * @return StockLevelStatusAndAvail which determines item stock level status
     */
    public StockLevelStatusAndAvail inventoryStockLevelStatusSKU(RepositoryItem pSku, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
        if (pSku != null) {
            if (isWithdrawal(pSku)) {
                return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
            } else {
                int svStockAvailable = 0;
                boolean svStockQueried = false;
                if (isCcStoreInSession(pStore) && localStoresManager.isEligibleForCcInStore(pSku, pStore)) {
                    svStockAvailable = getSVStockAvailable(pSku, pStore, svAvailableMap);
                    svStockQueried = true;
                }
                if (svStockAvailable > 0) {
                    if (isDestockingOrClearanceSale(pSku)) {
                        if (isSoldOnlyInStore(pSku)) {
                            return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                        } else {
                            if (isDirectDelivryFromStore(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                            } else {
                                if (isStockPositive(pSku)) {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S6, svStockAvailable);
                                } else {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                                }
                            }
                        }
                    } else {
                        if (isWithdrawalDateReached(pSku)) {
                            if (isStockPositive(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S6, svStockAvailable);
                            } else {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                            }
                        } else {
                            if (isSoldOnlyInStore(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                            } else {
                                if (isDirectDelivryFromStore(pSku)) {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                                } else {
                                    if (isDirectSupplier(pSku)) {
                                        return new StockLevelStatusAndAvail(StockLevelStatus.S8, svStockAvailable);
                                    } else {
                                        if (isComparisonDateAndWithdrawalDateLessEqualNMonth(pSku)) {
                                            if (isStockPositive(pSku)) {
                                                return new StockLevelStatusAndAvail(StockLevelStatus.S6, svStockAvailable);
                                            } else {
                                                return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                                            }
                                        } else {
                                            if (isTemporaryWithdrawal(pSku)) {
                                                if (isStockPositive(pSku)) {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S6, svStockAvailable);
                                                } else {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
                                                }
                                            } else {
                                                if (isStockPositive(pSku)) {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S10, svStockAvailable);
                                                } else {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S9, svStockAvailable);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (isDestockingOrClearanceSale(pSku)) {
                        if (isSoldOnlyInStore(pSku)) {
                            return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                        } else {
                            if (isDirectDelivryFromStore(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                            } else {
                                if (isStockPositive(pSku)) {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S1, 0);
                                } else {
                                    if (isLocalStoreInSession(pStore)) {
                                        if (!svStockQueried) {
                                            svStockAvailable = getSVStockAvailable(pSku, pStore, svAvailableMap);
                                        }
                                        if (svStockAvailable > 0) {                                            
                                            return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                                        } else {
                                            return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
                                        }
                                    } else {
                                        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
                                    }
                                }
                            }
                        }
                    } else {
                        if (isWithdrawalDateReached(pSku)) {
                            if (isStockPositive(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S1, 0);
                            } else {
                                if (isLocalStoreInSession(pStore)) {
                                    if (!svStockQueried) {
                                        svStockAvailable = getSVStockAvailable(pSku, pStore, svAvailableMap);
                                    }
                                    if (svStockAvailable > 0) {
                                        return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                                    } else {
                                        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
                                    }
                                } else {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
                                }
                            }
                        } else {
                            if (isSoldOnlyInStore(pSku)) {
                                return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                            } else {
                                if (isDirectDelivryFromStore(pSku)) {
                                    return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                                } else {
                                    if (isDirectSupplier(pSku)) {
                                        return new StockLevelStatusAndAvail(StockLevelStatus.S3, 0);
                                    } else {
                                        if (isComparisonDateAndWithdrawalDateLessEqualNMonth(pSku)) {
                                            if (isStockPositive(pSku)) {
                                                return new StockLevelStatusAndAvail(StockLevelStatus.S1, 0);
                                            } else {
                                                return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                                            }
                                        } else {
                                            if (isTemporaryWithdrawal(pSku)) {
                                                if (isStockPositive(pSku)) {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S1, 0);
                                                } else {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                                                }
                                            } else {
                                                if (isStockPositive(pSku)) {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S5, 0);
                                                } else {
                                                    return new StockLevelStatusAndAvail(StockLevelStatus.S4, 0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
    }
    
    public StockLevelStatus inventoryStockLevelStatusSKUForIndexingOnly(RepositoryItem pSku, 
            RepositoryItem pStore, Set<String> webNotAvailable, Map<Integer, Integer> svAvailable, Set<String> excludedSkus) {
        if (pSku != null) {
            if (isWithdrawal(pSku)) {
                return StockLevelStatus.S0;
            } else {
                if (isCcStoreInSession(pStore) && (excludedSkus != null) && !excludedSkus.contains(pSku.getRepositoryId()) && 
                        (svAvailable != null) && svAvailable.containsKey(pSku.getPropertyValue(CODE_ARTICLE))) {
                    if (isDestockingOrClearanceSale(pSku)) {
                        if (isSoldOnlyInStore(pSku)) {
                            return StockLevelStatus.S7;
                        } else {
                            if (isDirectDelivryFromStore(pSku)) {
                                return StockLevelStatus.S7;
                            } else {
                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                    return StockLevelStatus.S6;
                                } else {
                                    return StockLevelStatus.S7;
                                }
                            }
                        }
                    } else {
                        if (isWithdrawalDateReached(pSku)) {
                            if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                return StockLevelStatus.S6;
                            } else {
                                return StockLevelStatus.S7;
                            }
                        } else {
                            if (isSoldOnlyInStore(pSku)) {
                                return StockLevelStatus.S7;
                            } else {
                                if (isDirectDelivryFromStore(pSku)) {
                                    return StockLevelStatus.S7;
                                } else {
                                    if (isDirectSupplier(pSku)) {
                                        return StockLevelStatus.S8;
                                    } else {
                                        if (isComparisonDateAndWithdrawalDateLessEqualNMonth(pSku)) {
                                            if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                return StockLevelStatus.S6;
                                            } else {
                                                return StockLevelStatus.S7;
                                            }
                                        } else {
                                            if (isTemporaryWithdrawal(pSku)) {
                                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                    return StockLevelStatus.S6;
                                                } else {
                                                    return StockLevelStatus.S7;
                                                }
                                            } else {
                                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                    return StockLevelStatus.S10;
                                                } else {
                                                    return StockLevelStatus.S9;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (isDestockingOrClearanceSale(pSku)) {
                        if (isSoldOnlyInStore(pSku)) {
                            return StockLevelStatus.S2;
                        } else {
                            if (isDirectDelivryFromStore(pSku)) {
                                return StockLevelStatus.S2;
                            } else {
                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                    return StockLevelStatus.S1;
                                } else {
                                    if (isLocalStoreInSession(pStore)) {
                                        if ((svAvailable != null) && svAvailable.containsKey(pSku.getPropertyValue(CODE_ARTICLE))) {                                            
                                            return StockLevelStatus.S2;
                                        } else {
                                            return StockLevelStatus.S0;
                                        }
                                    } else {
                                        return StockLevelStatus.S0;
                                    }
                                }
                            }
                        }
                    } else {
                        if (isWithdrawalDateReached(pSku)) {
                            if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                return StockLevelStatus.S1;
                            } else {
                                if (isLocalStoreInSession(pStore)) {
                                    if ((svAvailable != null) && svAvailable.containsKey(pSku.getPropertyValue(CODE_ARTICLE))) {
                                        return StockLevelStatus.S2;
                                    } else {
                                        return StockLevelStatus.S0;
                                    }
                                } else {
                                    return StockLevelStatus.S0;
                                }
                            }
                        } else {
                            if (isSoldOnlyInStore(pSku)) {
                                return StockLevelStatus.S2;
                            } else {
                                if (isDirectDelivryFromStore(pSku)) {
                                    return StockLevelStatus.S2;
                                } else {
                                    if (isDirectSupplier(pSku)) {
                                        return StockLevelStatus.S3;
                                    } else {
                                        if (isComparisonDateAndWithdrawalDateLessEqualNMonth(pSku)) {
                                            if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                return StockLevelStatus.S1;
                                            } else {
                                                return StockLevelStatus.S2;
                                            }
                                        } else {
                                            if (isTemporaryWithdrawal(pSku)) {
                                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                    return StockLevelStatus.S1;
                                                } else {
                                                    return StockLevelStatus.S2;
                                                }
                                            } else {
                                                if ((webNotAvailable != null) && !webNotAvailable.contains(pSku.getRepositoryId())) {
                                                    return StockLevelStatus.S5;
                                                } else {
                                                    return StockLevelStatus.S4;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return StockLevelStatus.S0;
    }

    /**
     * Determines  item stock level status
     *
     * @param  pChildSkus parameter
     *
     * @return StockLevelStatusAndAvail which determines item stock level status
     */
    public StockLevelStatusAndAvail inventoryStockLevelStatusRegularProduct(List<RepositoryItem> pChildSkus, RepositoryItem pStore,
            Map<Integer, Integer> svAvailableMap) {
        if ((pChildSkus != null) && !pChildSkus.isEmpty()) {
            StockLevelStatusAndAvail skuStockLevelStatus = null;
            SortedMap<StockLevelStatus, Integer> stockLevelStatusMap = new TreeMap<StockLevelStatus, Integer>();
            for (RepositoryItem sku : pChildSkus) {
                skuStockLevelStatus = inventoryStockLevelStatusSKU(sku, pStore, svAvailableMap); 
                stockLevelStatusMap.put(skuStockLevelStatus.getStockLevelStatus(), skuStockLevelStatus.getSvStockAvailable());
            }
            
            if (stockLevelStatusMap.lastKey() != null) {
                if (isModifiedVersion() != null && isModifiedVersion()) {
                    if (stockLevelStatusMap.lastKey() == StockLevelStatus.S2 && stockLevelStatusMap.containsKey(StockLevelStatus.S1)) {
                        return new StockLevelStatusAndAvail(StockLevelStatus.S1, stockLevelStatusMap.get(StockLevelStatus.S1));
                    } else if (stockLevelStatusMap.lastKey() == StockLevelStatus.S7 && stockLevelStatusMap.containsKey(StockLevelStatus.S6)) {
                        return new StockLevelStatusAndAvail(StockLevelStatus.S6, stockLevelStatusMap.get(StockLevelStatus.S6));
                    } else {
                        return new StockLevelStatusAndAvail(stockLevelStatusMap.lastKey(), stockLevelStatusMap.get(stockLevelStatusMap.lastKey()));
                    }
                } else {
                    return new StockLevelStatusAndAvail(stockLevelStatusMap.lastKey(), stockLevelStatusMap.get(stockLevelStatusMap.lastKey()));
                }
            }
        }
        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
    }
    
    public StockLevelStatus inventoryStockLevelStatusRegularProductForIndexingOnly(List<RepositoryItem> pChildSkus, 
            RepositoryItem pStore, Set<String> webNotAvailable, Map<Integer, Integer> svAvailable, Set<String> excludedSkus) {
        
        if ((pChildSkus != null) && !pChildSkus.isEmpty()) {
            StockLevelStatus skuStockLevelStatus = null;
            SortedSet<StockLevelStatus> stockLevelStatusSet = new TreeSet<StockLevelStatus>();
            for (RepositoryItem sku : pChildSkus) {
                skuStockLevelStatus = inventoryStockLevelStatusSKUForIndexingOnly(sku, 
                        pStore, webNotAvailable, svAvailable, excludedSkus);
                stockLevelStatusSet.add(skuStockLevelStatus);
            }
            
            if (stockLevelStatusSet.last() != null) {
                if (isModifiedVersion() != null && isModifiedVersion()) {
                    if (stockLevelStatusSet.last() == StockLevelStatus.S2 && stockLevelStatusSet.contains(StockLevelStatus.S1)) {
                        return StockLevelStatus.S1;
                    } else if (stockLevelStatusSet.last() == StockLevelStatus.S7 && stockLevelStatusSet.contains(StockLevelStatus.S6)) {
                        return StockLevelStatus.S6;
                    } else {
                        return stockLevelStatusSet.last();
                    }
                } else {
                    return stockLevelStatusSet.last();
                }
            }
        }
        return StockLevelStatus.S0;
    }

    /**
     * Determines  item stock level status
     *
     * @param  pBundleLinks parameter
     * @param  pSku         parameter
     *
     * @return StockLevelStatus which determines item stock level status
     */
    public StockLevelStatus inventoryStockLevelStatusForPack(List<RepositoryItem> pBundleLinks, RepositoryItem pSku, RepositoryItem pStore,
            Map<Integer, Integer> svAvailableMap) {
        return inventoryStockLevelStatusForPackCC(pBundleLinks, pSku, pStore, svAvailableMap).getStockLevelStatus();
    }
    
    public StockLevelStatusAndAvail inventoryStockLevelStatusForPackCC(List<RepositoryItem> pBundleLinks, RepositoryItem pSku, RepositoryItem pStore,
            Map<Integer, Integer> svAvailableMap) {
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty()) {
            // make one call to StockViz for all bundle skus and get svAvailableMap for further processing
            int svStockAvailable = 0;
            if (isCcStoreInSession(pStore) && localStoresManager.isEligibleForCcInStore(pBundleLinks, pStore)) {
                svStockAvailable = getSVStockAvailable(pBundleLinks, pStore, svAvailableMap);
            }
            
            boolean availableForD2H = true;
            boolean availableForCC = true;
            Set<StockLevelStatus> packSkusStatuses = new HashSet<StockLevelStatus>();
            for (RepositoryItem skuLink : pBundleLinks) {
                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                if (sku != null) {
                    StockLevelStatus skuStatus = inventoryStockLevelStatusSKU(sku, pStore, svAvailableMap).getStockLevelStatus();
                    if (skuStatus == StockLevelStatus.S2){
                        return new StockLevelStatusAndAvail(StockLevelStatus.S2, 0);
                    }
                    availableForD2H = availableForD2H  && isPackAvailableForDeliveryToHome(skuStatus);
                    availableForCC = availableForCC && isPackAvailableForClickAndCollect(skuStatus);
                    packSkusStatuses.add(skuStatus);
                    if (!availableForD2H && !availableForCC)
                        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
                }
            }
            
            boolean showStockRestant = needToShowStockRestantForPack(packSkusStatuses);
            
            if (availableForCC && svStockAvailable > 0) {
                if (availableForD2H) {
                    if (showStockRestant) {
                        return new StockLevelStatusAndAvail(StockLevelStatus.S6, svStockAvailable);
                    }
                    return new StockLevelStatusAndAvail(StockLevelStatus.S8, svStockAvailable);
                }
                return new StockLevelStatusAndAvail(StockLevelStatus.S7, svStockAvailable);
            }
            
            if (availableForD2H) {
                if (showStockRestant) {
                    return new StockLevelStatusAndAvail(StockLevelStatus.S1, 0);
                }
                return new StockLevelStatusAndAvail(StockLevelStatus.S3, 0);
            }
        }
        return new StockLevelStatusAndAvail(StockLevelStatus.S0, 0);
    }

    public StockLevelStatus inventoryStockLevelStatusForGpForIndexingOnly(List<RepositoryItem> pBundleLinks, RepositoryItem pSku,
            RepositoryItem pStore, Set<String> webNotAvailable, Map<Integer, Integer> svAvailable, Set<String> excludedSkus) {
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty()) {
            int svStockAvailable = 0;
            if (isCcStoreInSession(pStore) && !isGpExcludedCCForIndexingOnly(pBundleLinks, excludedSkus) && 
                    isGpSvAvailableForIndexingOnly(pBundleLinks, svAvailable)) {
                // means greater than 0
                svStockAvailable = 1;
            }
            
            boolean availableForD2H = true;
            boolean availableForCC = true;
            Set<StockLevelStatus> packSkusStatuses = new HashSet<StockLevelStatus>();
            for (RepositoryItem skuLink : pBundleLinks) {
                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                if (sku != null) {
                    StockLevelStatus skuStatus = inventoryStockLevelStatusSKUForIndexingOnly(sku, pStore, webNotAvailable, svAvailable, excludedSkus);
                    if (skuStatus == StockLevelStatus.S2){
                        return StockLevelStatus.S2;
                    }
                    availableForD2H = availableForD2H  && isPackAvailableForDeliveryToHome(skuStatus);
                    availableForCC = availableForCC && isPackAvailableForClickAndCollect(skuStatus);
                    packSkusStatuses.add(skuStatus);
                    if (!availableForD2H && !availableForCC)
                        return StockLevelStatus.S0;
                }
            }
            
            boolean showStockRestant = needToShowStockRestantForPack(packSkusStatuses);
            
            if (availableForCC && svStockAvailable > 0) {
                if (availableForD2H) {
                    if (showStockRestant) {
                        return StockLevelStatus.S6;
                    }
                    return StockLevelStatus.S8;
                }
                return StockLevelStatus.S7;
            }
            
            if (availableForD2H) {
                if (showStockRestant) {
                    return StockLevelStatus.S1;
                }
                return StockLevelStatus.S3;
            }
        }
        return StockLevelStatus.S0;
    }
    
    private boolean isGpExcludedCCForIndexingOnly(List<RepositoryItem> pBundleLinks, Set<String> excludedSkus) {
        boolean result = false;
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty() && (excludedSkus != null)) {
            for (RepositoryItem skuLink : pBundleLinks) {
                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                if ((sku != null) && excludedSkus.contains(sku.getRepositoryId())) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    private boolean isGpSvAvailableForIndexingOnly(List<RepositoryItem> pBundleLinks, Map<Integer, Integer> svAvailable) {
        boolean result = false;
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty() && (svAvailable != null)) {
            result = true;
            for (RepositoryItem skuLink : pBundleLinks) {
                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                Long quantity = (Long) skuLink.getPropertyValue(QUANTITY);
                if (sku != null && quantity != null) {
                    Integer sa = svAvailable.get((Integer) sku.getPropertyValue(CODE_ARTICLE));
                    if ((sa == null) || (sa < quantity)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private boolean isPackAvailableForDeliveryToHome(StockLevelStatus stockLevelStatus) {
        if (stockLevelStatus == StockLevelStatus.S0 
                ||stockLevelStatus == StockLevelStatus.S2 
                || stockLevelStatus == StockLevelStatus.S7) {
            return false;
        }
        return true;
    }

    private boolean isPackAvailableForClickAndCollect(StockLevelStatus stockLevelStatus) {
        if (stockLevelStatus == StockLevelStatus.S0 
                || stockLevelStatus == StockLevelStatus.S1 
                || stockLevelStatus == StockLevelStatus.S2
                || stockLevelStatus == StockLevelStatus.S3
                || stockLevelStatus == StockLevelStatus.S4
                || stockLevelStatus == StockLevelStatus.S5) {
            return false;
        }
        return true;
    }

    private boolean needToShowStockRestantForPack(Set<StockLevelStatus> packSkusStatuses) {
        boolean showStockRestant = false;
        for (StockLevelStatus stockLevelStatus : packSkusStatuses) {
            if (stockLevelStatus == StockLevelStatus.S1 
                    || stockLevelStatus == StockLevelStatus.S6) {
                showStockRestant=true;
                break;
            }
        }
        return showStockRestant;
    }

    /* the same result as inventoryStockLevel == 2 (maybe a little faster... used only to display free shipping message) */
    public boolean isSoldOnlyInStoreForFreeShippingOnly(RepositoryItem skuItem, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap){
        return isSoldOnlyInStore(skuItem) || isSkuSoldOnlyInStoreForFreeShippingOnly(skuItem, pStore, svAvailableMap);
    }

    /**
     * Returns SV stock available values filtered map for different repository items, decides whick skus to query
     * @param  pItems parameter, pStore parameter
     * @return SV stock available values map
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getSVStockAvailableMultiForFilteredItems(List<RepositoryItem> pItems, RepositoryItem pStore) {
        List<RepositoryItem> filteredSkus = new ArrayList<RepositoryItem>();
        if (pItems != null && pStore != null) {
            for (RepositoryItem pItem : pItems) {
                try {
                    String itemDescriptorName = pItem.getItemDescriptor().getItemDescriptorName();
    
                    List<RepositoryItem> childSkus = null;
                    List<RepositoryItem> bundleLinks = null;
                    RepositoryItem bundleLinkSku = null;
                    if (!StringUtils.isBlank(itemDescriptorName)) {
                        if (itemDescriptorName.contains(PRODUCT) || itemDescriptorName.contains(PACK)) {
                            childSkus = (List<RepositoryItem>) pItem.getPropertyValue(CHILD_SKUS);
                            if ((childSkus != null) && !childSkus.isEmpty()) {
                                bundleLinkSku = childSkus.get(0);
                                bundleLinks = (List<RepositoryItem>) bundleLinkSku.getPropertyValue(BUNDLE_LINKS);
                            }
    
                        } else if (itemDescriptorName.contains(SKU)) {
                            bundleLinkSku = pItem;
                            bundleLinks = (List<RepositoryItem>) pItem.getPropertyValue(BUNDLE_LINKS);
                            if ((bundleLinks == null) || bundleLinks.isEmpty()) {
                                childSkus = new ArrayList<RepositoryItem>();
                                childSkus.add(pItem);
                            }
                        }
                    }
    
                    if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
                        if (isSVStockCheckNeededForPack(bundleLinks, pStore)) {
                            filteredSkus.add(bundleLinkSku);
                            
                            for (RepositoryItem skuLink : bundleLinks) {
                                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                                if (sku != null) {
                                    filteredSkus.add(sku);
                                }
                            }
                        }
                    } else if ((childSkus != null) && !childSkus.isEmpty()) {
                        for (RepositoryItem childSku : childSkus) {
                            if (isSVStockCheckNeededSku(childSku, pStore)) {
                                filteredSkus.add(childSku);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }
        
        return getSVStockAvailableMulti(filteredSkus, pStore);
    }
    
    /**
     * Returns SV stock available values map for list of skus
     * @param  pSkus parameter, pStore parameter
     * @return SV stock available values map
     */
    public Map<Integer, Integer> getSVStockAvailableMulti(List<RepositoryItem> pSkus, RepositoryItem pStore) {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        if (pSkus != null && pSkus.size() > 0 && pStore != null && !CASTORAMA_DIRECT_ID.equals(pStore.getRepositoryId()) 
                && !mStockVisualizationManager.isStockVisualizationDisabled()) {
            
            StringBuilder sb = new StringBuilder();
            for (RepositoryItem pSku : pSkus) {
                sb.append(pSku.getPropertyValue(CODE_ARTICLE).toString());
                sb.append(SV_PROD_DELIMETER);
            }
            String prodList = sb.substring(0, sb.length() - 1);
            
            try {
                List<StockModel> stockModels = mStockVisualizationManager.queryStockLevelByStoreId(prodList, 
                        pStore.getPropertyValue(STORE_ID).toString(), QUANTITY_TO_QUERY_SV, Boolean.toString(false));
                for (StockModel stockModel : stockModels) {
                    String prodId = stockModel.getProdId();
                    int pId = 0;
                    try {
                        pId = Integer.parseInt(prodId);
                    } catch (Exception e) {
                        if (isLoggingError()) {
                            logError(e);
                        }
                    }
                    
                    if (pId != 0) {
                        String stockAvailable = stockModel.getStockAvailable();
                        int sa = 0;
                        try {
                            sa = Integer.parseInt(stockAvailable);
                        } catch (Exception e) {
                            if (isLoggingError()) {
                                logError(e);
                            }
                        }
                        
                        result.put(pId, sa);
                    }
                }
            } catch (StockVisualizationException sve) {
                if (isLoggingError()) {
                    logError(sve);
                }
            }
        }
        return result;
    }
    
    private boolean isSVStockCheckNeededSku(RepositoryItem pSku, RepositoryItem pStore) {
        boolean result = false;
        if (!isWithdrawal(pSku)) {
            if (isCcStoreInSession(pStore) && localStoresManager.isEligibleForCcInStore(pSku, pStore)) {
                result = true;
            } else {
                if (isDestockingOrClearanceSale(pSku)) {
                    if (!isSoldOnlyInStore(pSku) && !isDirectDelivryFromStore(pSku) && 
                            // removed due to possible performance issues
                            //!isStockPositive(pSku) && 
                            isLocalStoreInSession(pStore)) {
                        result = true;
                    }
                } else {
                    if (isWithdrawalDateReached(pSku) &&
                            // removed due to possible performance issues
                            // !isStockPositive(pSku) && 
                            isLocalStoreInSession(pStore)) {
                        result = true;
                    }
                }
            }
        }
        
        return result;
    }
    
    private boolean isSVStockCheckNeededForPack(List<RepositoryItem> pBundleLinks, RepositoryItem pStore) {
        boolean result = false;
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty()) {
            for (RepositoryItem skuLink : pBundleLinks) {
                RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                if ((sku != null) && isSVStockCheckNeededSku(sku, pStore)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
    
    private boolean isSkuSoldOnlyInStoreForFreeShippingOnly(RepositoryItem skuItem, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap){
        boolean result = false;
        if (!isWithdrawal(skuItem)) {
            if (isDestockingOrClearanceSale(skuItem)) {
                if (isDirectDelivryFromStore(skuItem)) {
                    result = true;
                }
            } else {
                if (!isWithdrawalDateReached(skuItem)) {
                    if (isDirectDelivryFromStore(skuItem)) {
                        result = true;
                    } else {
                        if (!isDirectSupplier(skuItem) && isComparisonDateAndWithdrawalDateLessEqualNMonth(skuItem) && !isStockPositive(skuItem)) {
                            result = true;
                        }
                    }
                }
            }
            if (!result) {
                if (isCcStoreInSession(pStore) && localStoresManager.isEligibleForCcInStore(skuItem, pStore)) {
                    if (getSVStockAvailable(skuItem, pStore, svAvailableMap) > 0) {
                        if (isDestockingOrClearanceSale(skuItem)) {
                            if (!isDirectDelivryFromStore(skuItem) && !isStockPositive(skuItem)) {
                                result = true;
                            }
                        } else {
                            if (isWithdrawalDateReached(skuItem) && !isStockPositive(skuItem)) {
                                result = true;
                            }
                        }
                    }
                } else {
                    if (isDestockingOrClearanceSale(skuItem)) {
                        if (!isDirectDelivryFromStore(skuItem) && !isStockPositive(skuItem) &&
                                isLocalStoreInSession(pStore) && (getSVStockAvailable(skuItem, pStore, svAvailableMap) > 0)) {
                            result = true;
                        }
                    } else {
                        if (isWithdrawalDateReached(skuItem) && !isStockPositive(skuItem) && isLocalStoreInSession(pStore) && 
                                (getSVStockAvailable(skuItem, pStore, svAvailableMap) > 0)) {
                            result = true;
                        }
                    }
                }
            }
            
            if (isTemporaryWithdrawal(skuItem) && !isStockPositive(skuItem)) {
                result = true;
            }
        } 
        
        return result;
    }
    	
    /**
     * Check whether item is withdrawal.
     * @param  pSku
     * @return true if item is withdrawal
     */
    private boolean isWithdrawal(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Integer withdrawalType = (Integer) pSku.getPropertyValue(RETRAIT_MOMENTANE_MOTIFS_CODIFIES);
            if ((withdrawalType != null) && ((withdrawalType == 1) || (withdrawalType == 2))) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check whether item is temporary withdrawal
     * @param  pSku
     * @return true if item is temporary withdrawal
     */
    private boolean isTemporaryWithdrawal(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Integer withdrawalType = (Integer) pSku.getPropertyValue(RETRAIT_MOMENTANE_MOTIFS_CODIFIES);
            if ((withdrawalType != null) && (withdrawalType == 3)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check whether product is destocking or on clearanceSale.
     * @param  pSku
     * @return true if product is destocking or on clearanceSale.
     */
    private boolean isDestockingOrClearanceSale(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Date dateDebDestock = (Date) pSku.getPropertyValue(DATE_DEB_DESTOCK);
            Date dateFinDestock = (Date) pSku.getPropertyValue(DATE_FIN_DESTOCK);
            Boolean quantiteLimitee = (Boolean) pSku.getPropertyValue(QUANTITE_LIMITEE);
            Date currentDate = new Date();
            if (((dateDebDestock != null) && (dateFinDestock != null) && dateDebDestock.before(currentDate) &&
                     currentDate.before(dateFinDestock)) || ((quantiteLimitee != null) && quantiteLimitee)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check whether withdrawal date was reached.
     * @param  pSku parameter
     * @return true if withdrawal date was reached
     */
    private boolean isWithdrawalDateReached(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Date currentDate = new Date();
            Date withdrawalBeginDate = (Date) pSku.getPropertyValue(DATE_FIN_VIE);
            if ((withdrawalBeginDate != null) && currentDate.after(withdrawalBeginDate)) {
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Compare withdrawal date and current date using defined number of monthes.
     * @param  pSku parameter
     * @return true if comparison date and withdrawal date is less or equal n month
     */
    private boolean isComparisonDateAndWithdrawalDateLessEqualNMonth(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            GregorianCalendar now = new GregorianCalendar();
            now.add(GregorianCalendar.MONTH, numberOfMonthToCompareWithdrawalDate);
            Date nMonthLaterDate = now.getTime();
            
            Date withdrawalBeginDate = (Date) pSku.getPropertyValue(DATE_FIN_VIE);
            if ((withdrawalBeginDate != null) && !nMonthLaterDate.before(withdrawalBeginDate)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check whether product sold only in store and returns true if sold only in
     * store
     * @param  pSku parameter
     * @return true if product is sold only in store
     */
    private boolean isSoldOnlyInStore(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Integer typeArticle = (Integer) pSku.getPropertyValue(TYPE_ARTICLE);
            if ((typeArticle != null) && (typeArticle == 3)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check product deliver by directSupplier.
     * @param  pSku parameter
     * @return true if product deliver by directSupplier
     */
    private boolean isDirectSupplier(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Integer delivertype = (Integer) pSku.getPropertyValue(DELIVERY_TYPE);

            if (delivertype != null) {
                if (delivertype.intValue() == DELIVERY_BY_PROVIDER) {
                    result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * If articleType = 1 or 2, check the expeditionType.
     * If expeditionType = 3, display 'Actuellement indisponible'/'Currently not available'
     * as we do for products that are withdrawn from sale.
     */
    private boolean isDirectDelivryFromStore(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Integer delivertype = (Integer) pSku.getPropertyValue(DELIVERY_TYPE);
            if (delivertype != null) {
                if (delivertype.intValue() == DELIVERY_BY_STORE) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Check whether stock is positive and returns true if stock is positive
     * @param  pSku parameter
     * @return true if stock is positive, otherwise is false
     */
    private boolean isStockPositive(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            try {
                long stockLevel = queryStockLevel(pSku.getRepositoryId());
                if ((stockLevel == -1) || (stockLevel > 0)) {
                    result = true;
                }
            } catch (InventoryException e) {
                if (isLoggingError()) {
                    logError("Inventory exception for sku : " + pSku.getRepositoryId() + " - " + e.getMessage());
                }
            }
        }
        return result;
    }

    @Override
    public int purchase(String pId, long pHowMany) throws InventoryException {
        int purchaseStatus = super.purchase(pId, pHowMany);
        if (purchaseStatus == INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
            setStockLevel(pId, 0);
        }
        return purchaseStatus;
    }
    
    public boolean purchaseLocalStock(String pId, long pHowMany, String magasinId) {
        
        RepositoryItem sku = null;
        try {
            sku = getCatalogRefItem(pId);
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError(re);
            }
        }

        RepositoryItem store = null;
        try {
            store = mStockVisualizationManager.getMagasin(magasinId);
        } catch (StockVisualizationException sve) {
            if (isLoggingError()) {
                logError(sve);
            }
        }

        List<RepositoryItem> bundleLinks = (List<RepositoryItem>)sku.getPropertyValue(BUNDLE_LINKS);
        if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
            Map<RepositoryItem, Integer> skusQuantity = new HashMap<RepositoryItem, Integer>();
            Map<Integer, Integer> svAvailableMap = new HashMap<Integer, Integer> ();
            List<RepositoryItem> skusToQuery = prepareSkus(bundleLinks, svAvailableMap, skusQuantity);
            return purchaseLocalStockForPack(skusToQuery, skusQuantity, pHowMany, store);
        }

        return purchaseLocalStock(sku, pHowMany, store);
    }
    
    private boolean purchaseLocalStockForPack(List<RepositoryItem> skusToQuery, Map<RepositoryItem, Integer> skusQuantity, long pHowMany, RepositoryItem store) {
        boolean result = true;
        if (skusToQuery != null && skusQuantity != null && skusToQuery.size() == skusQuantity.size()) {
            for (int i = 0; i < skusToQuery.size(); i++){
                RepositoryItem tempSku = skusToQuery.get(i);
                int bundleQuantity = skusQuantity.get(tempSku).intValue();
                // reduce quantity for every bundle sku
                long finalQuantity = bundleQuantity*pHowMany;
                boolean isOk = purchaseLocalStock(tempSku, finalQuantity, store);
                // for pack product continue payment even can't reduce stock for sku
                //result = result && isOk; 
                if (!isOk) {
                    //log error message and continue reduce quantity for next sku
                    if (isLoggingError()){
                        String codeArticle = ((Integer) tempSku.getPropertyValue(CODE_ARTICLE)).toString();
                        String storeId = store.getPropertyValue(STORE_ID).toString();
                        String message = "Error when reducing stock for sku = {0} (codeArticle = {1} ), quantity = {2}, store = {3};";
                        vlogError(message, tempSku, codeArticle, finalQuantity, storeId);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @param sku
     * @param pHowMany
     * @param store
     * @return
     */
    private boolean purchaseLocalStock(RepositoryItem sku, long pHowMany, RepositoryItem store) {
        boolean success = false;
        Integer storeId;
        if (sku != null && store != null && !CASTORAMA_DIRECT_ID.equals(store.getRepositoryId())
                && !mStockVisualizationManager.isStockVisualizationDisabled()) {
            Integer ca = (Integer) sku.getPropertyValue(CODE_ARTICLE);
            try {
                storeId = Integer.parseInt(store.getPropertyValue(STORE_ID).toString());
                List<StockModel> stockModels = mStockVisualizationManager.queryStockLevelByStoreId(ca.toString(),
                        store.getPropertyValue(STORE_ID).toString(), Long.toString(pHowMany), Boolean.toString(true));
                if (stockModels.size() > 0) {
                    if (STATUS_REDUCE_SUCCESS.equals(stockModels.get(0).getStatus())) {
                        success = true;
                    }
                }
                List<String> codeArticlesToInvalidate = new ArrayList<String>();
                codeArticlesToInvalidate.add(buildSVSProductId(ca.toString()));
                getRemoteStockCacheAccessor().invalidateCache(storeId, codeArticlesToInvalidate);
            } catch (StockVisualizationException sve) {
                if (isLoggingError()) {
                    logError(sve);
                }
            }
        }

        return success;
    }
    
    /**
     * Returns SV stock available value
     * @param  pSku parameter, pStore parameter, svAvailableMap
     * @return SV stock available value
     */
    private int getSVStockAvailable(RepositoryItem pSku, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
        int result = 0;
        if (pSku != null && pStore != null && !CASTORAMA_DIRECT_ID.equals(pStore.getRepositoryId()) 
                && !mStockVisualizationManager.isStockVisualizationDisabled()) {
            Integer ca = (Integer) pSku.getPropertyValue(CODE_ARTICLE);
            if (svAvailableMap != null && svAvailableMap.containsKey(ca)) {
                result = svAvailableMap.get(ca);
            } else {
                try {
                    List <StockModel> stockModels = mStockVisualizationManager.queryStockLevelByStoreId(ca.toString(), 
                            pStore.getPropertyValue(STORE_ID).toString(), QUANTITY_TO_QUERY_SV, Boolean.toString(false));
                    if (stockModels.size() > 0) {
                        String stockAvailable = stockModels.get(0).getStockAvailable();  
                        try {
                            result = Integer.parseInt(stockAvailable);
                        } catch (Exception e) {
                            if (isLoggingError()) {
                                logError(e);
                            }
                        }
                    }
                } catch (StockVisualizationException sve) {
                    if (isLoggingError()) {
                        logError(sve);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Returns SV stock available value
     * @param  pBundleLinks parameter, pStore parameter, svAvailableMap
     * @return SV stock available value
     */
    private int getSVStockAvailable(List<RepositoryItem> pBundleLinks, RepositoryItem pStore, Map<Integer, Integer> svAvailableMap) {
        int result = 0;
        if ((pBundleLinks != null) && !pBundleLinks.isEmpty() && (pStore != null) && !CASTORAMA_DIRECT_ID.equals(pStore.getRepositoryId()) 
                && !mStockVisualizationManager.isStockVisualizationDisabled()) {
            Map<RepositoryItem, Integer> skusQuantity = new HashMap<RepositoryItem, Integer>();
            List<RepositoryItem> skusToQuery = prepareSkus(pBundleLinks, svAvailableMap, skusQuantity);
            
            if (!skusToQuery.isEmpty())  {
                if (svAvailableMap != null) {
                    svAvailableMap.putAll(getSVStockAvailableMulti(skusToQuery, pStore));
                } else {
                    svAvailableMap = getSVStockAvailableMulti(skusToQuery, pStore);
                }
            }
            
            int minPackQuantity = Integer.MAX_VALUE;
            for (RepositoryItem bundleSku : skusQuantity.keySet()) {
                Integer ca = (Integer) bundleSku.getPropertyValue(CODE_ARTICLE);
                int stock = svAvailableMap.get(ca);
                int quantity = skusQuantity.get(bundleSku);
                int currPackQuantity = (int) (stock / quantity);
                if (currPackQuantity < minPackQuantity) {
                    minPackQuantity = currPackQuantity;
                }
            }
            
            result = minPackQuantity;
        }
        return result;
    }

    /**
     * @param pBundleLinks
     * @param svAvailableMap
     * @param skusQuantity
     * @return
     */
    private List<RepositoryItem> prepareSkus(List<RepositoryItem> pBundleLinks, Map<Integer, Integer> svAvailableMap, Map<RepositoryItem, Integer> skusQuantity) {
        List<RepositoryItem> skusToQuery = new ArrayList<RepositoryItem>();
        
        for (RepositoryItem skuLink : pBundleLinks) {
            RepositoryItem sku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
            Long quantity = (Long) skuLink.getPropertyValue(QUANTITY);
            
            if (sku != null && quantity != null) {
                skusQuantity.put(sku, quantity.intValue());
                
                Integer ca = (Integer) sku.getPropertyValue(CODE_ARTICLE);
                if (svAvailableMap == null || !svAvailableMap.containsKey(ca)) {
                    skusToQuery.add(sku);
                }
            }
        }
        return skusToQuery;
    }
    
    /**
     * 
     * @param  pStore parameter
     * @return 
     */
    private boolean isCcStoreInSession(RepositoryItem pStore) {
        boolean result = false;
        if (pStore != null) {
            Boolean r = (Boolean) pStore.getPropertyValue(CC_STORE);
            if (r != null) {
                result = r;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param  pStore parameter
     * @return 
     */
    private boolean isLocalStoreInSession(RepositoryItem pStore) {
        boolean result = false;
        if (pStore != null) {
            Boolean r = (Boolean) pStore.getPropertyValue(LOCAL_STORE);
            if (r != null) {
                result = r;
            }
        }
        
        return result;
    }
    
    public int getNumberOfMonthToCompareWithdrawalDate() {
        return numberOfMonthToCompareWithdrawalDate;
    }

    public void setNumberOfMonthToCompareWithdrawalDate(
            int numberOfMonthToCompareWithdrawalDate) {
        this.numberOfMonthToCompareWithdrawalDate = numberOfMonthToCompareWithdrawalDate;
    }
    
    public Boolean isModifiedVersion() {
        return modifiedVersion;
    }

    public void setModifiedVersion(Boolean modifiedVersion) {
        this.modifiedVersion = modifiedVersion;
    }
    
    private StockVisualizationManager mStockVisualizationManager;

    public StockVisualizationManager getStockVisualizationManager() {
      return mStockVisualizationManager;
    }

    public void setStockVisualizationManager(StockVisualizationManager pStockVisualizationManager) {
      mStockVisualizationManager = pStockVisualizationManager;
    }
    
    public LocalStoresManager localStoresManager;
    
    public LocalStoresManager getLocalStoresManager() {
        return localStoresManager;
    }
    
    public void setLocalStoresManager(LocalStoresManager localStoresManager) {
        this.localStoresManager = localStoresManager;
    }

    public RemoteStockCacheAccessor getRemoteStockCacheAccessor() {
        return mRemoteStockCacheAccessor;
    }

    public void setRemoteStockCacheAccessor(RemoteStockCacheAccessor mRemoteStockCacheAccessor) {
        this.mRemoteStockCacheAccessor = mRemoteStockCacheAccessor;
    }
}
