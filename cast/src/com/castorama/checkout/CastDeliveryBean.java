package com.castorama.checkout;

import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryItem;

/**
 * Represents delivery bean
 *
 * @author Epam Team
 */
public class CastDeliveryBean {
    /** type property */
    private int mType;

    /** delivery parameter */
    private double mDelivery;
    
     /** supplierName property */
    private String mSupplierName;

    /** deliveryTime property */
    private String mDeliveryTime;

    /** deliveryItems property */
    private List<RepositoryItem> mDeliveryItems = new ArrayList<RepositoryItem>();

    /**
     * Constructor
     */
    public CastDeliveryBean() {
    }

    /**
     * Returns delivery property.
     *
     * @return delivery property.
     */
    public double getDelivery() {
        return mDelivery;
    }

    /**
     * Sets the value of the delivery property.
     *
     * @param pDelivery parameter to set.
     */
    public void setDelivery(double pDelivery) {
        mDelivery = pDelivery;
    }

    /**
     * Returns deliveryItems property.
     *
     * @return deliveryItems property.
     */
    public List<RepositoryItem> getDeliveryItems() {
        return mDeliveryItems;
    }

    /**
     * Sets the value of the deliveryItems property.
     *
     * @param pDeliveryItems parameter to set.
     */
    public void setDeliveryItems(List<RepositoryItem> pDeliveryItems) {
        mDeliveryItems = pDeliveryItems;
    }

    /**
     * Add items to deliveryItems list
     *
     * @param pItem
     */
    public void addDeliveryItem(RepositoryItem pItem) {
        mDeliveryItems.add(pItem);
    }

    /**
     * Returns type property.
     *
     * @return type property.
     */
    public int getType() {
        return mType;
    }

    /**
     * Sets the value of the type property.
     *
     * @param pType parameter to set.
     */
    public void setType(int pType) {
        mType = pType;
    }

    /**
     * Returns deliveryTime property.
     *
     * @return deliveryTime property.
     */
    public String getDeliveryTime() {
        return mDeliveryTime;
    }

    /**
     * Sets the value of the deliveryTime property.
     *
     * @param pDeliveryTime parameter to set.
     */
    public void setDeliveryTime(String pDeliveryTime) {
        mDeliveryTime = pDeliveryTime;
    }
    
        /**
     * Returns supplierName property.
     *
     * @return supplierName property.
     */
    public String getSupplierName() {
        return mSupplierName;
    }

    /**
     * Sets the value of the supplierName property.
     *
     * @param pSupplierName parameter to set.
     */
    public void setSupplierName(String pSupplierName) {
        mSupplierName = pSupplierName;
    }
}
