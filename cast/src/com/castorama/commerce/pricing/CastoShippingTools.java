package com.castorama.commerce.pricing;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.OrderManager;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

import com.castorama.checkout.CastShippingManager;
import com.castorama.constantes.CastoConstantesDefense;

/**
 * Utility methods for shipping
 *
 * @author Epam Team
 */
public class CastoShippingTools extends ApplicationLoggingImpl {

	/** POIDS constant. */
    private static final String POIDS = "PoidsUV";

    /** EXONERATION_PFT constant. */
    private static final String EXONERATION_PFT = "exonerationPFT";

    /** FREE_SHIPPING constant. */
    private static final String FREE_SHIPPING = "exonerationPFE";

    /** FIXED_SHIPPING constant. */
    private static final String FIXED_SHIPPING = "sommeForfaitaire";

    /** BUNDLE_LINKS constant. */
    private static final String BUNDLE_LINKS = "bundleLinks";

    /** ITEM constant. */
    public static final String ITEM = "item";
    
    /** QUANTITY constant. */
    public static final String QUANTITY = "quantity";
    
    /** orderRepository property. */
    private Repository mOrderRepository;

    /** orderManager property. */
    private OrderManager mOrderManager;

    /** orderManager property. */
    private CastShippingManager mShippingManager;

    /**
     * Returns orderRepository property.
     *
     * @return orderRepository property.
     */
    public Repository getOrderRepository() {
        return mOrderRepository;
    }

    /**
     * Set the orderRepository property.
     *
     * @param pOrderRepository parameter
     */
    public void setOrderRepository(Repository pOrderRepository) {
        mOrderRepository = pOrderRepository;
    }

    /**
     * Set the OrderManager property.
     *
     * @param pOrderManager parameter
     */
    public void setOrderManager(OrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

    /**
     * Return the OrderManager property.
     *
     * @return ToDo: DOCUMENT ME!
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Method which returns the sum of the weights of items from shipping group
     * Passed as parameter. Note, this method does not include exemptions from
     * PFT / PFL.
     *
     * @param  pShippingGroup The shipping group.
     *
     * @return The sum of the weights of items from shipping group spent in
     *         Parameter, 0 on failure.
     */
    public int getPoidsShippingGroup(ShippingGroup pShippingGroup) {
        int poids = 0;

        if (null == pShippingGroup) {
            logError("CastoShippingTools.getPoidsShippingGroup(ShippingGroup) : le shipping group est null.");
        } else {
            CommerceItemRelationship relation;
            CommerceItem item;
            AuxiliaryData auxData;
            Object catalogRef;
            Object dPoids;

            for (Iterator relations = pShippingGroup.getCommerceItemRelationships().iterator(); relations.hasNext();) {
                relation = (CommerceItemRelationship) relations.next();

                if (relation != null) {
                    item = relation.getCommerceItem();

                    if (item != null) {
                        auxData = item.getAuxiliaryData();

                        if (auxData != null) {
                            catalogRef = auxData.getCatalogRef();

                            if (catalogRef != null) {
                                try {
                                    dPoids = DynamicBeans.getPropertyValue(catalogRef, POIDS);

                                    if (dPoids != null) {
                                        poids += ((Integer) dPoids).intValue() * item.getQuantity();
                                    }
                                } catch (PropertyNotFoundException pnfe) {
                                    logError("CastoShippingTools.getPoidsShippingGroup(ShippingGroup) : " +
                                             pnfe.toString());
                                }
                            }
                        }
                    }  // end if
                }  // end if
            }  // end for
        }  // end if-else

        return poids;
    }

    /**
     * Method which returns the total weight of the articles of Shipping Group
     * spent A parameter taking into account the exemptions of PFL.
     *
     * @param  pShippingGroup The shipping group.
     *
     * @return The sum of the weights of items from shipping group spent in
     *         Parameter, 0 on failure.
     */
    public int getPoidsShippingGroupExonerationPFL(ShippingGroup pShippingGroup) {
        int poidsPFL = 0;

        if (null == pShippingGroup) {
            logError("CastoShippingTools.getPoidsShippingGroupExonerationPFL(ShippingGroup) : le shipping group est null.");
        } else {
            CommerceItemRelationship relation;
            CommerceItem item;
            AuxiliaryData auxData;
            Object catalogRef;
            Object dPoids;
            Boolean dExoPFE;

            for (Iterator relations = pShippingGroup.getCommerceItemRelationships().iterator(); relations.hasNext();) {
                relation = (CommerceItemRelationship) relations.next();

                if (relation != null) {
                    item = relation.getCommerceItem();

                    if (item != null) {
                        auxData = item.getAuxiliaryData();

                        if (auxData != null) {
                            catalogRef = auxData.getCatalogRef();

                            if (catalogRef != null) {
                                try {
                                    dPoids = DynamicBeans.getPropertyValue(catalogRef, POIDS);

                                    dExoPFE = (Boolean) DynamicBeans.getPropertyValue(catalogRef, FREE_SHIPPING);

                                    if ((dPoids != null) && !dExoPFE.booleanValue()) {
                                        poidsPFL += ((Integer) dPoids).intValue() * item.getQuantity();
                                    }
                                } catch (PropertyNotFoundException pnfe) {
                                    logError("CastoShippingTools.getPoidsShippingGroupExonerationPFL(ShippingGroup) : " +
                                             pnfe.toString());
                                }
                            }
                        }
                    }  // end if
                }  // end if
            }  // end for
        }  // end if-else

        return poidsPFL;
    }

    /**
     * Method which affects the total shipping weight group.
     *
     * @param pShippingGroup The shipping group.
     */
    public void affecterPoidsTotalShippingGroup(ShippingGroup pShippingGroup) {
        try {
            int poids = getPoidsShippingGroup(pShippingGroup);
            int poidsPFT = getPoidsShippingGroupExonerationPFT(pShippingGroup);
            int poidsPFL = getPoidsShippingGroupExonerationPFL(pShippingGroup);

            ShippingGroupImpl shippingGroup = (ShippingGroupImpl) pShippingGroup;

            synchronized (shippingGroup) {
                shippingGroup.setPropertyValue(CastoConstantesDefense.SHIPPPING_GROUP_POIDS_TOTAL, new Integer(poids));
                shippingGroup.setPropertyValue(CastoConstantesDefense.SHIPPPING_GROUP_POIDS_PFT, new Integer(poidsPFT));
                shippingGroup.setPropertyValue(CastoConstantesDefense.SHIPPPING_GROUP_POIDS_PFL, new Integer(poidsPFL));
            }
        } catch (Exception e) {
            logError("CastoShippingTools.affecterPoidsTotalShippingGroup(ShippingGroup) : " + e.toString());
        }

    }

    /**
     * Method which returns the total weight of the articles of Shipping Group
     * spent A parameter taking into account the exemptions from PFT.
     *
     * @param  pShippingGroup The Shipping Group.
     *
     * @return a weight, or 0 on failure.
     */
    public int getPoidsShippingGroupExonerationPFT(ShippingGroup pShippingGroup) {
        int poids = 0;

        if (null == pShippingGroup) {
            logError("CastoShippingTools.getPoidsShippingGroupExonerationPFT(ShippingGroup) : Shipping Group non renseigné.");
        } else {
            List relationShips = pShippingGroup.getCommerceItemRelationships();

            if (relationShips != null) {
                CommerceItemRelationship relation;
                CommerceItem item;
                boolean exonere;
                int quantite;
                AuxiliaryData auxData;
                Object dPoids;
                Object dExonere;

                for (Iterator relations = relationShips.iterator(); relations.hasNext();) {
                    relation = (CommerceItemRelationship) relations.next();
                    item = (CommerceItem) relation.getCommerceItem();

                    if (item != null) {
                        auxData = item.getAuxiliaryData();

                        if (auxData != null) {
                            Object catalogRef = auxData.getCatalogRef();

                            if (catalogRef != null) {
                                try {
                                    dExonere = DynamicBeans.getPropertyValue(catalogRef, EXONERATION_PFT);

                                    if (dExonere == null) {
                                        exonere = false;
                                    } else {
                                        exonere = ((Boolean) dExonere).booleanValue();
                                    }

                                    if (!exonere) {
                                        quantite = (int) item.getQuantity();

                                        dPoids = DynamicBeans.getPropertyValue(catalogRef, POIDS);

                                        if (dPoids != null) {
                                            poids += quantite * ((Integer) dPoids).intValue();
                                        }
                                    }
                                } catch (PropertyNotFoundException pnfe) {
                                    logError("CastoShippingTools.getPoidsShippingGroupExonerationPFT(ShippingGroup) : " +
                                             pnfe.toString());
                                }  // end try-catch
                            }  // end if
                        }  // end if
                    }  // end if
                }  // end for
            }  // end if
        }  // end if-else

        return poids;
    }

    /**
     * Copy information from sku to commerce item
     *
     * @param pShippingGroup
     */
    public void copierInformationsSkusToCommerceItems(ShippingGroup pShippingGroup) {
        if (pShippingGroup != null) {
            List relationShips = pShippingGroup.getCommerceItemRelationships();

            if (relationShips != null) {
                CommerceItemRelationship relation;
                CommerceItem item;

                for (Iterator relations = relationShips.iterator(); relations.hasNext();) {
                    relation = (CommerceItemRelationship) relations.next();
                    item = (CommerceItem) relation.getCommerceItem();

                    if (item != null) {
                        try {
                            CopySkuInfoToCommerceItem.copySkuInfoToCommerceItem(item);
                        } catch (PropertyNotFoundException e) {
                            if (isLoggingError()) {
                                logError(e.getMessage());
                            }
                        }
                    }
                }
            }
        }  // end if

    }

    /**
     * Returns totalWeight property.
     *
     * @param  pShippingGroup parameter to set.
     *
     * @return totalWeight property.
     */
    public double getTotalWeight(ShippingGroup pShippingGroup) {
        return getTotalWeight(pShippingGroup, false, false);
    }

    /**
     * Returns weight property.
     *
     * @param  pCommerceItem parameter to set.
     *
     * @return weight property.
     */
    public double getWeight(CommerceItem pCommerceItem) {
        return getWeight(pCommerceItem, false, false);
    }

    /**
     * Returns calculated weight for shipping.
     */
    public double getWeightForShipping(CommerceItem pCommerceItem) {
        return getWeight(pCommerceItem, true, false);
    }
    
    /**
     * Returns weightForShippingHangling property.
     *
     * @param  pCommerceItem parameter to set.
     *
     * @return weightForShippingHangling property.
     */
    public double getWeightForHandling(CommerceItem pCommerceItem) {
        return getWeight(pCommerceItem, false, true);
    }

    /**
     * Returns fixedShippingPrice property.
     *
     * @return fixedShippingPrice property.
     */
    public double getFixedShippingPrice(ShippingGroup pShippingGroup) {
        double fixedShippingPriceTotal = 0.0;
        double itemQuantity;
        List<ShippingGroupCommerceItemRelationship> relationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
        if ((relationships != null) && (relationships.size() > 0)) {
            for (ShippingGroupCommerceItemRelationship currentRelationship : relationships) {
                CommerceItem item = currentRelationship.getCommerceItem();
                double tempPrice = 0.0;
                if (item != null) {
                    if (currentRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
                        itemQuantity = currentRelationship.getQuantity();
                    } else {
                        itemQuantity =
                            getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(currentRelationship
                                                                                                             .getCommerceItem());
                    }
                    try {
                        Object sku = item.getAuxiliaryData().getCatalogRef();
                        Object weightObject = DynamicBeans.getPropertyValue(sku, POIDS);
                        if (weightObject != null) {
                            // SINGLE SKU
                            // Products with "free shipping" property set to true - are not counted
                            Boolean freeShipping = (Boolean) DynamicBeans.getPropertyValue(sku, FREE_SHIPPING);
                            if (!freeShipping.booleanValue()) {
                                Float fixedShippingPrice = (Float) DynamicBeans.getPropertyValue(sku, FIXED_SHIPPING);
                                if(fixedShippingPrice != null) {
                                	tempPrice += fixedShippingPrice;
                                }
                            }
                        } else {
                            // WE ARE IN PACK
                            List<RepositoryItem> bundledLinks = (List<RepositoryItem>) DynamicBeans.getPropertyValue(sku, BUNDLE_LINKS);
                            for (RepositoryItem link : bundledLinks) {
                                Object tempSku = link.getPropertyValue(ITEM);
                                Boolean freeShipping = (Boolean) DynamicBeans.getPropertyValue(tempSku, FREE_SHIPPING);
                                if (!freeShipping.booleanValue()) {
                                    Float fixedShippingPrice = (Float) DynamicBeans.getPropertyValue(tempSku, FIXED_SHIPPING);
                                    // count fixed shipping price of every SKU in a pack and multiply it by its quantity
                                    if(fixedShippingPrice != null) {
                                    	tempPrice += (fixedShippingPrice * ((Long) link.getPropertyValue(QUANTITY)));
                                    }
                                }
                            }
                        }
                    } catch (PropertyNotFoundException pnfe) {
                        fixedShippingPriceTotal = 0.0;
                    }
                    fixedShippingPriceTotal += (itemQuantity * tempPrice);    
                }
            }
        }
        return fixedShippingPriceTotal;
    }

    /**
     * Calculate the total weight of the items being shipped to the specified
     * ShippingGroup. Iterate through all the items being shipped and extract
     * out the number of items being shipped and each items weight. This it
     * added to the running total of weight.
     *
     * <P><code>getWeight</code> is called to extract the weight for each item.
     *
     * @param  pShippingGroup   a value of type 'ShippingGroup'
     * @param  shipping parameter
     *
     * @return a value of type 'double'
     */
    // possible that quantity is all remaining?
    // what happens if weight is null
    public double getTotalWeight(ShippingGroup pShippingGroup, boolean shipping, boolean handling) {
        double weightTotal = 0.0;
        double itemQuantity, itemWeight;

        List relationships = ((ShippingGroupImpl) pShippingGroup).getCommerceItemRelationships();
        if ((relationships != null) && (relationships.size() > 0)) {
            int listSize = relationships.size();
            for (int i = 0; i < listSize; i++) {
                ShippingGroupCommerceItemRelationship currentRelationship =
                    ((ShippingGroupCommerceItemRelationship) relationships.get(i));
                if (currentRelationship.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
                    itemQuantity = currentRelationship.getQuantity();
                } else {
                    itemQuantity =
                        getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(currentRelationship
                                                                                                         .getCommerceItem());
                }
                itemWeight = getWeight(currentRelationship.getCommerceItem(), shipping, handling);
                weightTotal += (itemQuantity * itemWeight);
            }
        }
        NumberFormat formatter = NumberFormat.getNumberInstance(atg.service.dynamo.LangLicense.getLicensedDefault());
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed(false);
        return Double.parseDouble(formatter.format(weightTotal));
    }

    /**
     * Responsible for getting the weight associated with an instance of a
     * commerce item. This is performed by getting the CatalogRef object from
     * auxiliaryData object and extracting the property named by mWeightProperty
     * out of it. Weight is returned.
     *
     * @param  pCommerceItem    the commerce item whose weight is to be
     *                          extracted
     * @param  shipping parameter
     *
     * @return the weight of a single item
     */
    // catch exception
    private double getWeight(CommerceItem pCommerceItem, boolean shipping, boolean handling) {
        double weight = 0.0;

        if ((pCommerceItem != null) && (getOrderManager().getCatalogTools() != null)) {
            try {
                Object sku = pCommerceItem.getAuxiliaryData().getCatalogRef();

                /*
                 * if (sku == null) { try { sku =
                 * getOrderManager().getCatalogTools().findSKU(pCommerceItem.getCatalogRefId(),
                 * pCommerceItem.getCatalogKey()); } catch (RepositoryException e) { } }
                 */
                if (sku != null) {
                    Object weightObject = DynamicBeans.getPropertyValue(sku, POIDS);
                    if (weightObject != null) {
                        // SINGLE SKU
                        weight = getSkuWeight(sku, shipping, handling);
                    } else {
                        // WE ARE IN PACK
                        List<RepositoryItem> bundledLinks =
                            (List<RepositoryItem>) DynamicBeans.getPropertyValue(sku, BUNDLE_LINKS);
                        for (RepositoryItem link : bundledLinks) {
                            double tempWeight = getSkuWeight(link.getPropertyValue(ITEM), shipping, handling);
                            // count weight of every SKU in a pack and multiply it by its quantity 
                            weight += (tempWeight * ((Long) link.getPropertyValue(QUANTITY)));
                        }
                    }
                }
            } catch (PropertyNotFoundException pnfe) {
                weight = 0;
            }  // end try-catch
        }  // end if
        return weight;
    }
    
    /**
     * Returns skuWeight property.
     *
     * @param  sku              parameter to set.
     * @param  shippingCalculation parameter to set.
     *
     * @return skuWeight property.
     */
    private double getSkuWeight(Object sku, boolean shippingCalculation, boolean handlingCalculation) {
        double weight = 0.0;
        try {
            Object weightObject = DynamicBeans.getPropertyValue(sku, POIDS);
            Boolean freeShipping = (Boolean) DynamicBeans.getPropertyValue(sku, FREE_SHIPPING);
            Float fixedShippingPrice = (Float) DynamicBeans.getPropertyValue(sku, FIXED_SHIPPING);
            Boolean exonerationPFT = (Boolean) DynamicBeans.getPropertyValue(sku, EXONERATION_PFT);
            if (handlingCalculation) {
                if ((exonerationPFT == null) || !exonerationPFT) {
                    if (weightObject != null) {
                        // weight is returned in gram, but should be in kilogram
                        weight = ((Number) weightObject).doubleValue() / 1000;
                    }
                }
            } else if(shippingCalculation) {
                if (!freeShipping) {
                    if (fixedShippingPrice == null || fixedShippingPrice.equals(new Float("0"))) {
                        if (weightObject != null) {
                            // weight is returned in gram, but should be in kilogram
                            weight = ((Number) weightObject).doubleValue() / 1000;
                        }
                    }
                }
            } else {
                // weight is returned in gram, but should be in kilogram
                if (weightObject != null) {
                	weight = ((Number) weightObject).doubleValue() / 1000;
                }
            } 
        } catch (PropertyNotFoundException pnfe) {
            pnfe.printStackTrace();
            weight = 0;
        }  // end try-catch
        return weight;

    }

	/**
	 * Returns shippingManager property.
	 * @return the shippingManager
	 */
	public CastShippingManager getShippingManager() {
		return mShippingManager;
	}

	/**
	 * Sets shippingManager property.
	 * @param pShippingManager the shippingManager to set
	 */
	public void setShippingManager(CastShippingManager pShippingManager) {
		this.mShippingManager = pShippingManager;
	}
    
    /**
     * Calculates a delivery code for the given SKU object.
     * @param pSku given SKU repository id.
     * @param pQuantity quantity of given SKU.
     * @return delivery code for the given SKU object. 
     */
    public int calculateDeliveries(String pSku_Id, double pQuantity) {
        CastShippingManager shippingManager = getShippingManager(); 
        double days = shippingManager.calculateSKUDeliveryInDays(pSku_Id, pQuantity);
        return new Double(days).intValue();
    }
    
    /**
     * Returns delivery period description.
     * @param pSku given SKU repository id.
     * @param pLocale locale.
     * @return delivery period description. 
     */
    public String getDeliveryPeriodDescription(int pDeliveryPeriodCode, Locale pLocale) {
        CastShippingManager shippingManager = getShippingManager(); 
        return shippingManager.returnDeliveryString(pDeliveryPeriodCode, pLocale);
    }


}
