package com.castorama.commerce.order;

import java.util.Iterator;
import java.util.List;

import atg.commerce.CommerceException;

import atg.commerce.inventory.InventoryManager;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

import atg.nucleus.GenericService;

/**
 * Helper class for validate ordrers.
 *
 * @author  EPAM team
 */
public class CastOrderValidationHelper extends GenericService {

	/** mCommerceItemManager property */
    private CommerceItemManager mCommerceItemManager;

    /** mOrderManager property */
    private CastOrderManager mOrderManager;

    /** mInventoryManager property */
    private InventoryManager mInventoryManager;

    /**
     * Validate ordrer to clear items thats not presents in the stock.
     *
     * @param  pOrder order to validate.
     *
     * @return true when order chaged, otherwise false.
     */
    public boolean validate(Order pOrder) {
        boolean changed = false;
        try {
            List commerceItems = pOrder.getCommerceItems();
            if ( null != commerceItems ) {
                CommerceItem[] items = (CommerceItem[]) commerceItems.toArray(new CommerceItem[commerceItems.size()]);
                for ( int i = 0; i < items.length; i++ ) {
                    long stockLevel = getInventoryManager().queryStockLevel(items[i].getCatalogRefId());
                    if ( -1 != stockLevel ) { // value -1 means that inventory is unlimited
                        if ( 0 < stockLevel ) {
                            long quantity = items[i].getQuantity();
                            if ( stockLevel < quantity ) {
                                changed = true;
                                items[i].setQuantity(stockLevel);
                                checkRalations(pOrder, items[i], stockLevel, quantity);
                            }
                        } else {
                            changed = true;
                            getCommerceItemManager().removeItemFromOrder(pOrder, items[i].getId());
                        }
                    }
                }
            } 
            if ( changed ) {
                getOrderManager().updateOrder(pOrder);
            }
        } catch (Exception e) {
            if ( isLoggingError() ) {
                logError(e);
            }
        }
        return changed;
    }

    /**
     * Checks the order item quality in all items relationships with stock level. Clear overflow.  
     *
     * @param  order order to be cheked.
     * @param  item item to be cheked.
     * @param  stockLevel stock level.
     * @param  quantity quality of the order item.
     *
     * @throws CommerceException when something wrong while clearing.
     */
    private void checkRalations(Order order, CommerceItem item, long stockLevel, long quantity)
                         throws CommerceException {
        long q = 0;
        List rel = item.getShippingGroupRelationships();
        for ( Iterator it = rel.iterator(); it.hasNext(); ) {
            Object o = (Object) it.next();
            if ( o instanceof ShippingGroupCommerceItemRelationship ) {
                q += ((ShippingGroupCommerceItemRelationship) o).getQuantity();
            }
        }
        if ( q > stockLevel ) {
            quantity = quantity - stockLevel;
            for ( Iterator it = rel.iterator(); it.hasNext(); ) {
                Object o = (Object) it.next();
                if ( o instanceof ShippingGroupCommerceItemRelationship ) {
                    ShippingGroupCommerceItemRelationship sc = (ShippingGroupCommerceItemRelationship) o;
                    long q1 = sc.getQuantity();
                    if ( q1 >= quantity ) {
                        getCommerceItemManager().removeItemQuantityFromShippingGroup(order, item.getId(),
                                                                                     sc.getShippingGroup().getId(),
                                                                                     quantity);
                        break;
                    } else {
                        getCommerceItemManager().removeItemQuantityFromShippingGroup(order, item.getId(),
                                                                                     sc.getShippingGroup().getId(),
                                                                                     q1);
                        quantity = quantity - q1;
                    }
                }
            }
        } else if ( q < stockLevel ) {
            for (Iterator it = rel.iterator(); it.hasNext();) {
                Object o = (Object) it.next();
                if (o instanceof ShippingGroupCommerceItemRelationship) {
                    ShippingGroupCommerceItemRelationship sc = (ShippingGroupCommerceItemRelationship) o;
                    getCommerceItemManager().addItemQuantityToShippingGroup(order, item.getId(),
                                                                            sc.getShippingGroup().getId(),
                                                                            stockLevel - q);
                    break;
                }
            }
        }
    }

    /**
     * Returns commerceItemManager property.
     *
     * @return commerceItemManager property.
     */
    public CommerceItemManager getCommerceItemManager() {
        return mCommerceItemManager;
    }

    /**
     * Sets the value of the commerceItemManager property.
     *
     * @param commerceItemManager parameter to set.
     */
    public void setCommerceItemManager(CommerceItemManager commerceItemManager) {
        this.mCommerceItemManager = commerceItemManager;
    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param orderManager parameter to set.
     */
    public void setOrderManager(CastOrderManager orderManager) {
        this.mOrderManager = orderManager;
    }

    /**
     * Returns inventoryManager property.
     *
     * @return inventoryManager property.
     */
    public InventoryManager getInventoryManager() {
        return mInventoryManager;
    }

    /**
     * Sets the value of the inventoryManager property.
     *
     * @param inventoryManager parameter to set.
     */
    public void setInventoryManager(InventoryManager inventoryManager) {
        this.mInventoryManager = inventoryManager;
    }

}
