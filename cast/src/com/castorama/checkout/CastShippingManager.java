package com.castorama.checkout;

import static com.castorama.constantes.CastoConstantes.DELIVERY_WEEKS;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.OrderManager;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

/**
 * CastShippingManager class - helper for shipping calculation 
 *
 * @author  EPAM team
 */
public class CastShippingManager extends GenericService {
    
    /** SALE_EXP constant */
    public static final String SALE_EXP = ".onSale";

    /** ONE_EXP constant */
    public static final String ONE_EXP = ".one";

    /** TWO_EXP constant */
    public static final String TWO_EXP = ".two";

    /** THREE_EXP constant */
    public static final String THREE_EXP = ".three";

    /** FOUR_EXP constant */
    public static final String FOUR_EXP = ".four";

    /** FIVE_EXP constant */
    public static final String FIVE_EXP = ".five";

    /** SIX_EXP constant */
    public static final String SIX_EXP = ".six";
    
    /** SEVEN_EXP constant */
    public static final String SEVEN_EXP = ".seven";
    
    /** EIGHT_EXP constant */
    public static final String EIGHT_EXP = ".eight";
    

    /** CASTO_SKU constant */
    public static final String CASTO_SKU = "casto_sku";

    /** WEIGHT constant */
    public static final String WEIGHT = "PoidsUV";

    /** DELIVERY_BY_US constant */
    public static final int PNS = 1;

    /** DELIVERY_BY_PROVIDER constant */
    public static final int LDF = 2;

    /** TRANSPORTER_COLISSIMO constant */
    public static final String TRANSPORTER_COLISSIMO = "COLISSIMO";

    /** TRANSPORTER_HEPPNER constant */
    public static final String TRANSPORTER_HEPPNER = "HEPPNER";

    /** TRANSPORTER_PRESTONNE constant */
    public static final String TRANSPORTER_PRESTONNE = "PRESTONNE";

    /** SUPPLIER constant */
    public static final String SUPPLIER = "NomFournisseur";

    /** TRANSPORTER constant */
    public static final String TRANSPORTER = "transporteur";
    
    /** DELIVERY_TYPE constant */
    public static final String DELIVERY_TYPE = "typeExpedition";

    /** DELAI_APPRO_FOURNISSEUR constant */
    public static final String DELAI_APPRO_FOURNISSEUR = "DelaiApproFournisseur";

    /** BUNDLE constant */
    public static final String BUNDLE = "com.castorama.checkout.ShoppingCartResources";
    
    /** DATE_END_DESTOCK constant. */
    private static final String DATE_END_DESTOCK = "dateFinDestock";

    /** DATE_START_DESTOCK constant. */
    private static final String DATE_START_DESTOCK = "dateDebDestock";
    
    /** CUSTOM_DELIVERY_TERM constant. */
    private static final String CUSTOM_DELIVERY_TERM = "customDeliveryTerm";
    
    /** CUSTOM_DELIVERY_SD constant. */
    private static final String CUSTOM_DELIVERY_SD = "customDeliveryStartDate";

    /** CUSTOM_DELIVERY_ED constant. */
    private static final String CUSTOM_DELIVERY_ED = "customDeliveryEndDate";
    
    /** CUSTOM_DELIVERY_TERM_1 constant. */
    private static final String CUSTOM_DELIVERY_TERM_PREFIX = "sous ";
    
    /** CUSTOM_DELIVERY_TERM_1 constant. */
    private static final String CUSTOM_DELIVERY_TERM_1 = "24 heures";
    
    /** CUSTOM_DELIVERY_TERM_2 constant. */
    private static final String CUSTOM_DELIVERY_TERM_2 = "48 heures";
    
    /** CUSTOM_DELIVERY_TERM_3 constant. */
    private static final String CUSTOM_DELIVERY_TERM_3 = "72 heures";
    
    /** CUSTOM_DELIVERY_TERM_7 constant. */
    private static final String CUSTOM_DELIVERY_TERM_7 = "1 semaine";
    
    /** ONE constant */
    public static final int ONE = 1;

    /** TWO constant */
    public static final int TWO = 2;

    /** THREE constant */
    public static final int THREE = 3;

    /** FOUR constant */
    public static final int FOUR = 4;

    /** FIVE constant */
    public static final int FIVE = 5;
    
    /** SIX constant */
    public static final int SIX = 6;
    
    /** SEVEN constant */
    public static final int SEVEN = 7;

    /** EIGHT constant */
    public static final int EIGHT = 8;

    /** INFINITY constant */
    public static final int INFINITY = 1000;

    /** NEG constant */
    public static final int NEG = -1;

    /*
     * Status of SKU "On Sale" constant
     */
    public static final int SALE = -2;

    /** EIGHT_WEEKS constant */
    public static final int EIGHT_WEEKS = 49;
    
    /** SEVEN_WEEKS constant */
    public static final int SEVEN_WEEKS = 42;
    
    /** SIX_WEEKS constant */
    public static final int SIX_WEEKS = 35;

    /** FIVE_WEEKS constant */
    public static final int FIVE_WEEKS = 28;

    /** FOUR_WEEKS constant */
    public static final int FOUR_WEEKS = 21;

    /** THREE_WEEKS constant */
    public static final int THREE_WEEKS = 14;

    /** TWO_WEEKS constant */
    public static final int TWO_WEEKS = 7;

    /** ONE_WEEK constant */
    public static final int ONE_WEEK = 0;

    /** BUNDLE_LINKS constant */
    public static final String BUNDLE_LINKS = "bundleLinks";

    /** CHILD_SKUS constant */
    public static final String CHILD_SKUS = "childSKUs";

    /** QUANTITY constant */
    public static final String QUANTITY = "quantity";

    /** ITEM constant */
    public static final String ITEM = "item";

    /** ID constant */
    public static final String ID = "id";

    /** orderManager property */
    private OrderManager mOrderManager;

    /** repository property */
    private Repository mRepository;

    /** inventoryManager property */
    private InventoryManager mInventoryManager;

    /**
     * Sets the OrderManager property.
     *
     * @param pOrderManager 
     */
    public void setOrderManager(OrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

    /**
     * Return the OrderManager property.
     *
     * @return OrderManager property
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Returns the repository property.
     *
     * @return Repository property
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the repository property.
     *
     * @param pRepository 
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns the inventoryManager property.
     *
     * @return InventoryManager property
     */
    public InventoryManager getInventoryManager() {
        return mInventoryManager;
    }

    /**
     * Sets the inventoryManager property.
     *
     * @param pInventoryManager 
     */
    public void setInventoryManager(InventoryManager pInventoryManager) {
        this.mInventoryManager = pInventoryManager;
    }

    /*
     * Returns the summ of all different deliveries
     */
    public int calculateAllDeliveries(CastDeliveryBean pPNS, List<CastDeliveryBean> pLDFs) {
        int total = 0;

        if (pPNS != null) {
            total++;
        }

        if (pLDFs != null) {
            total += pLDFs.size();
        }

        return total;
    }


    /**
     * Calculates the amount of days for delivery of given RepositoryItem, depending of its amount.
     * If the given item is a pack, calculates the amount for its each childSku.
     */
    public CastDeliveryBean calculateSKUDeliveryInDays(RepositoryItem pSku, double pQuantity) {

        CastDeliveryBean bean = new CastDeliveryBean();
        RepositoryItem[] masterItem = new RepositoryItem[1];
        masterItem[0] = pSku;

        List<RepositoryItem> childItems = null;

        if (masterItem[0] != null) {
            childItems = (List<RepositoryItem>) masterItem[0].getPropertyValue(BUNDLE_LINKS);
        }

        double deliveryDays = 0;

        if ((childItems == null) || ((childItems != null) && (childItems.size() == 0))) {
            deliveryDays = calculateDeliveryDays(pSku, pQuantity);
            String supplier = (String) pSku.getPropertyValue(SUPPLIER);
            Integer delivertype = (Integer) pSku.getPropertyValue(DELIVERY_TYPE);
            if (delivertype == null) {
                delivertype = 0;
            }
            
            if (deliveryDays > 10000) {
                deliveryDays -= 10000;
                bean.setDeliveryTime(CUSTOM_DELIVERY_TERM_PREFIX + getCustomTermString(deliveryDays));
            }
            
            bean.setDelivery(deliveryDays);
            bean.setType(delivertype);
            bean.setSupplierName(supplier);
            bean.addDeliveryItem(pSku);
            
        } else {
            // WE ARE IN PACK
            double maxDeliveryDays = 0;
            for (RepositoryItem skuLink : childItems) {
                double requestedQuantity = pQuantity * ((Long) skuLink.getPropertyValue(QUANTITY)).doubleValue();
                RepositoryItem tmpSku = (RepositoryItem) skuLink.getPropertyValue(ITEM);
                deliveryDays = calculateDeliveryDays(tmpSku, requestedQuantity);
                
                String supplier = (String) tmpSku.getPropertyValue(SUPPLIER);
                Integer delivertype = (Integer) tmpSku.getPropertyValue(DELIVERY_TYPE);
                if (delivertype == null) {
                    delivertype = 0;
                }
                
                double customTmpDelivery = deliveryDays;
                if (deliveryDays > 10000) {
                    deliveryDays -= 10000;
                }
                
                double deliveryDaysReal = 
                        deliveryDays == NEG ? EIGHT_WEEKS : 
                            deliveryDays == SALE ? FOUR_WEEKS : 
                                deliveryDays;

                if (deliveryDaysReal > maxDeliveryDays) {
                    maxDeliveryDays = deliveryDaysReal;
                    
                    if (customTmpDelivery > 10000) {
                        bean.setDeliveryTime(CUSTOM_DELIVERY_TERM_PREFIX + getCustomTermString(deliveryDays));
                    } else {
                        bean.setDeliveryTime(null);
                    }
                    
                    bean.setDelivery(deliveryDays);
                    bean.setType(delivertype);
                    bean.setSupplierName(supplier);
                }
            }
            bean.addDeliveryItem(pSku);
        }

        return bean;
    }

    /**
     * Calculates the amount of days for delivery of given RepositoryItem, depending of its amount.
     * If the given item is a pack, calculates the amount for its each childSku.
     * @param pSku given SKU repository id.
     * @param pQuantity quantity of given SKU.
     * @return the amount of days for delivery for the given SKU object. 
     */
    public double calculateSKUDeliveryInDays(String pSku_Id, double pQuantity) {
        double result = 0;
    	try {
        	RepositoryItem sku = getRepository().getItem(pSku_Id, CASTO_SKU);
        	CastDeliveryBean bean = calculateSKUDeliveryInDays(sku, pQuantity);
        	result = bean.getDelivery();
        } catch (Exception e) {
        	if ( isLoggingError() ) {
        		logError(e);
        	}
        }
    	return result;
    }


    /*
     * According to sent delivery prefix, returns the property from resource bundle.
     */
    public String returnDeliveryString(double pDaysAmount, Locale pLocale) {
        String deliveryString = null;
        String prefix = DELIVERY_WEEKS;
        double amount = calculateDeliveryInWeeks(pDaysAmount);
        
        if (amount == EIGHT) {
            deliveryString = getStringResource(prefix + EIGHT_EXP, pLocale);
        } else if (amount == FIVE) {
            deliveryString = getStringResource(prefix + FIVE_EXP, pLocale);
        } else if (amount == FOUR) {
            deliveryString = getStringResource(prefix + FOUR_EXP, pLocale);
        } else if (amount == THREE) {
            deliveryString = getStringResource(prefix + THREE_EXP, pLocale);
        } else if (amount == TWO) {
            deliveryString = getStringResource(prefix + TWO_EXP, pLocale);
        } else if (amount == ONE) {
            deliveryString = getStringResource(prefix + ONE_EXP, pLocale);
        } else if (amount == SALE) {
            deliveryString = getStringResource(prefix + SALE_EXP, pLocale);
        } else {
            deliveryString = getStringResource(prefix + EIGHT_EXP, pLocale);
        }

        return deliveryString;
    }

    /*
     * Calculates and returns the number of weeks according to given number of days.
     */
    private double calculateDeliveryInWeeks(double pDaysAmount) {
        int weeksCount;


        if (pDaysAmount > SIX_WEEKS) {
            weeksCount = EIGHT;
        } else if (pDaysAmount > FIVE_WEEKS) {
            weeksCount = FIVE;
        } else if (pDaysAmount > FOUR_WEEKS) {
            weeksCount = FOUR;
        } else if (pDaysAmount > THREE_WEEKS) {
            weeksCount = THREE;
        } else if (pDaysAmount > TWO_WEEKS) {
            weeksCount = TWO;
        } else if (pDaysAmount > ONE_WEEK) {
            weeksCount = ONE;
        } else if (pDaysAmount == SALE) {
            weeksCount = SALE;
        } else {
            weeksCount = EIGHT;
        }

        return new Double(weeksCount);
    }

    /*
     * Calculates the amount of days for delivery of SKU, depending of its quantity.
     */
    private double calculateDeliveryDays(RepositoryItem pSku, double pQuantity) {
        String supplier = (String) pSku.getPropertyValue(TRANSPORTER);
        Integer delivertype = (Integer) pSku.getPropertyValue(DELIVERY_TYPE);
        Double aRoughDeliveryTime = (Double) pSku.getPropertyValue(DELAI_APPRO_FOURNISSEUR);

        double quantityRemain = NEG;
        long stockLevel = 0;
        try {
            stockLevel = getInventoryManager().queryStockLevel(pSku.getRepositoryId());
            if (stockLevel == NEG) {
                quantityRemain = INFINITY;
            } else {
                quantityRemain = stockLevel - pQuantity;
            }
        } catch (Exception exception) {
            if (isLoggingInfo()) {
                logInfo("Inventory exception for sku : " + pSku.getRepositoryId());
            }
        }
        
        double deliveryTime = NEG;
        if (delivertype != null) {
            
            double roughDeliveryTime = (aRoughDeliveryTime != null) ? aRoughDeliveryTime : 0;
            if (isDestocking(pSku) && ((delivertype == PNS) || (delivertype == LDF))) {
                if (stockLevel > 0 || stockLevel == NEG) {
                    deliveryTime = SALE;
                }
            } else if (delivertype == PNS) {
                if (quantityRemain >= 0) {
                    String cdt = getCustomTerm(pSku);
                    if ((cdt != null) && (cdt.length() != 0)) {
                        deliveryTime = getCustomTermInDays(cdt) + 10000;
                    } else {
                        if (TRANSPORTER_COLISSIMO.equalsIgnoreCase(supplier)) {
                            deliveryTime = THREE;
                        } else if (TRANSPORTER_HEPPNER.equalsIgnoreCase(supplier)) {
                            deliveryTime = FIVE;
                        } else if (TRANSPORTER_PRESTONNE.equalsIgnoreCase(supplier)) {
                            deliveryTime = 22;
                        }
                    }
                } else {
                    if (TRANSPORTER_COLISSIMO.equalsIgnoreCase(supplier)) {
                        deliveryTime = FIVE + roughDeliveryTime;
                    } else if (TRANSPORTER_PRESTONNE.equalsIgnoreCase(supplier)) {
                        deliveryTime = 24 + roughDeliveryTime;
                    } else if (TRANSPORTER_HEPPNER.equalsIgnoreCase(supplier)) {
                        deliveryTime = SEVEN + roughDeliveryTime;
                    }
                }
            } else if (delivertype == LDF) {
                if (roughDeliveryTime < 4) {
                    deliveryTime = roughDeliveryTime + 10000;
                } else {
                    deliveryTime = TWO + roughDeliveryTime;
                }
            } else {
                deliveryTime = NEG;
            }  // end if-else
        }  // end if

        return deliveryTime;
    }

    /*
     * Returns the property form ResoucreBundle for the specified prefix
     */
    public String getStringResource(String pResourceName, Locale pLocale) throws MissingResourceException {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE);

        try {
            String result = bundle.getString(pResourceName);

            if (result == null) {
                throw new MissingResourceException(result, "atg.commerce.fulfillment.Constants", pResourceName);
            } else {
                return result;
            }
        } catch (MissingResourceException exc) {
            logError("ERROR: Unable to load resource " + bundle + "->" + pResourceName + ": " + exc);
            throw exc;
        }
    }
    

    /**
     * Check whether product is destocking.
     *
     * @param  pSku
     *
     * @return true if product is destocking.
     */
    private boolean isDestocking(RepositoryItem pSku) {
        boolean result = false;
        if (pSku != null) {
            Date dateDebDestock = (Date) pSku.getPropertyValue(DATE_START_DESTOCK);
            Date dateFinDestock = (Date) pSku.getPropertyValue(DATE_END_DESTOCK);
            Date currentDate = new Date();
            if ((dateDebDestock != null) && (dateFinDestock != null) && dateDebDestock.before(currentDate) &&
                     currentDate.before(dateFinDestock)) {
                result = true;
            }
        }
        return result;
    }
    
    private String getCustomTerm(RepositoryItem pSku) {
        String result = null;
        if (pSku != null) {
            String customDeliveryTerm = (String) pSku.getPropertyValue(CUSTOM_DELIVERY_TERM);
            Date customDeliveryStartDate = (Date) pSku.getPropertyValue(CUSTOM_DELIVERY_SD);
            Date customDeliveryEndDate = (Date) pSku.getPropertyValue(CUSTOM_DELIVERY_ED);
            Date currentDate = new Date();
            if ((customDeliveryStartDate != null) && (customDeliveryEndDate != null) 
                    && customDeliveryStartDate.before(currentDate) 
                    && currentDate.before(customDeliveryEndDate)) {
                result = customDeliveryTerm;
            }
        }
        return result;
    }
    
    private double getCustomTermInDays(String term) {
        double days = 0;
        if (CUSTOM_DELIVERY_TERM_1.equals(term)) {
            days = 1;
        } else if (CUSTOM_DELIVERY_TERM_2.equals(term)) {
            days = 2;
        } else if (CUSTOM_DELIVERY_TERM_3.equals(term)) {
            days = 3;
        } else if (CUSTOM_DELIVERY_TERM_7.equals(term)) {
            days = 7;
        } 
        return days;
    }
    
    private String getCustomTermString(double days) {
        String term = null;
        if (days == 1) {
            term = CUSTOM_DELIVERY_TERM_1;
        } else if (days == 2) {
            term = CUSTOM_DELIVERY_TERM_2;
        } else if (days == 3) {
            term = CUSTOM_DELIVERY_TERM_3;
        } else if (days == 7) {
            term = CUSTOM_DELIVERY_TERM_7;
        } 
        return term;
    }
}
