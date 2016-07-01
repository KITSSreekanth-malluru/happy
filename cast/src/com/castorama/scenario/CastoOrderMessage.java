package com.castorama.scenario;

import atg.commerce.messaging.CommerceMessageImpl;

import atg.commerce.order.Order;

import atg.repository.RepositoryItem;

import atg.userprofiling.Profile;

/**
 * A simple message to notify the system on order submit. NOTE: we don't use
 * OOTB message since we don't want to run Fulfillment module
 *
 * @author Andrei_Raichonak
 */
public class CastoOrderMessage extends CommerceMessageImpl {
    /** serialVersionUID constant. */
    private static final long serialVersionUID = -8416148746415233380L;

    /** profile property. */
    private RepositoryItem mProfile;

    /** order property. */
    private Order mOrder;

    /**
     * Creates a new CastoOrderMessage object.
     *
     * @param pProfile parameter
     * @param pOrder   parameter
     */
    public CastoOrderMessage(RepositoryItem pProfile, Order pOrder) {
        super();

        if (pProfile instanceof Profile) {
            mProfile = ((Profile) pProfile).getDataSource();
        } else {
            mProfile = pProfile;
        }

        mProfile = pProfile;
        mOrder = pOrder;
    }

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public RepositoryItem getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param pProfile parameter to set.
     */
    public void setProfile(RepositoryItem pProfile) {
        mProfile = pProfile;
    }

    /**
     * Returns order property.
     *
     * @return order property.
     */
    public Order getOrder() {
        return mOrder;
    }

    /**
     * Sets the value of the order property.
     *
     * @param pOrder parameter to set.
     */
    public void setOrder(Order pOrder) {
        mOrder = pOrder;
    }

    /**
     * Returns profileId property.
     *
     * @return profileId property.
     */
    public String getProfileId() {
        if (mProfile != null) {
            return mProfile.getRepositoryId();
        }

        return null;
    }

    /**
     * Returns orderId property.
     *
     * @return orderId property.
     */
    public String getOrderId() {
        if (mOrder != null) {
            return mOrder.getId();
        }

        return null;
    }
}
