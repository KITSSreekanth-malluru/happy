package com.castorama.commerce.order;

import java.util.Date;

import com.castorama.constantes.CastoConstantesOrders;

import atg.commerce.order.CommerceItemImpl;

/**
 * Extends CommerceItemImpl class to support 'creation date' property.
 *
 * @author Vasili_Ivus
 */
public class CastCommerceItemImpl extends CommerceItemImpl implements Comparable {
    
    /** quantityWasDecreased property */
    private boolean quantityWasDecreased;

    private long currentInventoryValue;
    
    /**
     * Returns creationDate property.
     *
     * @return creationDate property.
     */
    public java.util.Date getCreationDate() {
        return (java.util.Date) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_CREATION_DATE);
    }

    /**
     * Sets the value of the creationDate property.
     *
     * @param pCreationDate parameter to set.
     */
    public void setCreationDate(java.util.Date pCreationDate) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_CREATION_DATE,
                         ((pCreationDate == null) ? null : new java.sql.Timestamp(pCreationDate.getTime())));
    }
    
    /** 
     * Extends setQuantity method of CommerceItemImpl to change creation date when quantity changed.
     * 
	 * @see atg.commerce.order.CommerceItemImpl#setQuantity(long)
	 */
	@Override
	public void setQuantity(long quantity) {
		setCreationDate(new Date(System.currentTimeMillis()));
		super.setQuantity(quantity);
	}

	/**
     * @param  o parameter
     *
     * @return 
     */
    public int compareTo(Object o) {
        int result = 1;
        if (o instanceof CastCommerceItemImpl) {
            CastCommerceItemImpl item = (CastCommerceItemImpl) o;
            Date d = this.getCreationDate();
            Date itemd = item.getCreationDate();
            if (null == d) {
                if (null == itemd) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if (null == itemd) {
                    result = -1;
                } else {
                    result = itemd.compareTo(d);
                }
            }
        }
        return result;
    }
    
    /**
     * Returns deliveryPeriod property.
     *
     * @return deliveryPeriod property.
     */
    public Integer getDeliveryPeriod() {
        return (Integer) getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_DELIVERY_PERIOD);
    }

    /**
     * Sets the value of the deliveryPeriod property.
     *
     * @param pDeliveryPeriod parameter to set.
     */
    public void setDeliveryPeriod(Integer pDeliveryPeriod) {
        setPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_DELIVERY_PERIOD, pDeliveryPeriod);
    }

    /**
     * Determine whether quantity was decreased due to commerceItem hasn't got enough stock.
     * 
     * @return the quantityWasDecreased
     */
    public boolean isQuantityWasDecreased() {
        if (quantityWasDecreased) {
            setQuantityWasDecreased(false);
            return true;
        }

        return false;
    }

    /**
     * @param quantityWasDecreased the quantityWasDecreased to set
     */
    public void setQuantityWasDecreased(boolean quantityWasDecreased) {
        this.quantityWasDecreased = quantityWasDecreased;
    }

    public long getCurrentInventoryValue() {
        long value = currentInventoryValue;
        currentInventoryValue = 0;
        return value;
    }

    public void setCurrentInventoryValue(long currentInventoryValue) {
        this.currentInventoryValue = currentInventoryValue;
    }
}
