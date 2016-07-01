package com.castorama.invite;

import java.util.Calendar;
import java.util.Date;

import atg.nucleus.GenericService;

import atg.repository.RepositoryItem;

import atg.targeting.DynamicContentTargeter;

/**
 * Configuration component for referrer program
 *
 * @author Andrei_Raichonak
 */
public class ReferrerProgramConfig extends GenericService {
    /** PROPERTY_END_USABLE constant. */
    private static final String PROPERTY_END_USABLE = "endUsable";

    /** referrerCouponTargeter property. */
    private DynamicContentTargeter mReferrerCouponTargeter;

    /** refereeCouponTargeter property. */
    private DynamicContentTargeter mRefereeCouponTargeter;

    /** threshold property. */
    private int mThreshold = 0;

    /** numberOfCouponsToGeneratePerJob property. */
    private int mNumberOfCouponsToGeneratePerJob = 1000;

    /** numberOfCouponsToGeneratePerPromotion property. */
    private int mNumberOfCouponsToGeneratePerPromotion = 1000;

    /** orderLimit property. */
    private int mOrderLimit = 50;

    /**
     * Returns referrerCouponTargeter property.
     *
     * @return referrerCouponTargeter property.
     */
    public DynamicContentTargeter getReferrerCouponTargeter() {
        return mReferrerCouponTargeter;
    }

    /**
     * Sets the value of the referrerCouponTargeter property.
     *
     * @param pReferrerCouponTargeter parameter to set.
     */
    public void setReferrerCouponTargeter(DynamicContentTargeter pReferrerCouponTargeter) {
        mReferrerCouponTargeter = pReferrerCouponTargeter;
    }

    /**
     * Returns refereeCouponTargeter property.
     *
     * @return refereeCouponTargeter property.
     */
    public DynamicContentTargeter getRefereeCouponTargeter() {
        return mRefereeCouponTargeter;
    }

    /**
     * Sets the value of the refereeCouponTargeter property.
     *
     * @param pRefereeCouponTargeter parameter to set.
     */
    public void setRefereeCouponTargeter(DynamicContentTargeter pRefereeCouponTargeter) {
        mRefereeCouponTargeter = pRefereeCouponTargeter;
    }

    /**
     * Returns referrerPromotion property.
     *
     * @return referrerPromotion property.
     */
    public RepositoryItem getReferrerPromotion() {
        RepositoryItem promo = null;

        try {
            Object[] objs = getReferrerCouponTargeter().target(this, 1);

            if ((objs != null) && (objs.length > 0)) {
                return (RepositoryItem) objs[0];
            }
        } catch (Exception e) {
            logError(e);
        }

        return promo;
    }

    /**
     * Returns refereePromotion property.
     *
     * @return refereePromotion property.
     */
    public RepositoryItem getRefereePromotion() {
        RepositoryItem promo = null;

        try {
            Object[] objs = getRefereeCouponTargeter().target(this, 1);

            if ((objs != null) && (objs.length > 0)) {
                return (RepositoryItem) objs[0];
            }
        } catch (Exception e) {
            logError(e);
        }

        return promo;
    }

    /**
     * Returns threshold property.
     *
     * @return threshold property.
     */
    public int getThreshold() {
        return mThreshold;
    }

    /**
     * Sets the value of the threshold property.
     *
     * @param pThreshold parameter to set.
     */
    public void setThreshold(int pThreshold) {
        mThreshold = pThreshold;
    }

    /**
     * Returns numberOfCouponsToGeneratePerJob property.
     *
     * @return numberOfCouponsToGeneratePerJob property.
     */
    public int getNumberOfCouponsToGeneratePerJob() {
        return mNumberOfCouponsToGeneratePerJob;
    }

    /**
     * Sets the value of the numberOfCouponsToGeneratePerJob property.
     *
     * @param pNumberOfCouponsToGeneratePerJob parameter to set.
     */
    public void setNumberOfCouponsToGeneratePerJob(int pNumberOfCouponsToGeneratePerJob) {
        mNumberOfCouponsToGeneratePerJob = pNumberOfCouponsToGeneratePerJob;
    }

    /**
     * Returns numberOfCouponsToGeneratePerPromotion property.
     *
     * @return numberOfCouponsToGeneratePerPromotion property.
     */
    public int getNumberOfCouponsToGeneratePerPromotion() {
        return mNumberOfCouponsToGeneratePerPromotion;
    }

    /**
     * Sets the value of the numberOfCouponsToGeneratePerPromotion property.
     *
     * @param pNumberOfCouponsToGeneratePerPromotion parameter to set.
     */
    public void setNumberOfCouponsToGeneratePerPromotion(int pNumberOfCouponsToGeneratePerPromotion) {
        mNumberOfCouponsToGeneratePerPromotion = pNumberOfCouponsToGeneratePerPromotion;
    }

    /**
     * Returns orderLimit property.
     *
     * @return orderLimit property.
     */
    public int getOrderLimit() {
        return mOrderLimit;
    }

    /**
     * Sets the value of the orderLimit property.
     *
     * @param pOrderLimit parameter to set.
     */
    public void setOrderLimit(int pOrderLimit) {
        mOrderLimit = pOrderLimit;
    }

    /**
     * Returns referrerProgrameActive property.
     *
     * @return referrerProgrameActive property.
     */
    public boolean isReferrerProgrameActive() {
        RepositoryItem referrerPromo = getReferrerPromotion();
        Date referrerExpDate = null;

        if (referrerPromo != null) {
            referrerExpDate = (Date) referrerPromo.getPropertyValue(PROPERTY_END_USABLE);
        }

        RepositoryItem refereePromo = getRefereePromotion();
        Date refereeExpDate = null;

        if (refereePromo != null) {
            refereeExpDate = (Date) refereePromo.getPropertyValue(PROPERTY_END_USABLE);
        }

        Calendar cal = Calendar.getInstance();
        boolean referrerActive =
            (referrerPromo != null) && ((referrerExpDate == null) || cal.getTime().before(referrerExpDate));
        boolean refereeActive =
            (refereePromo != null) && ((refereeExpDate == null) || cal.getTime().before(refereeExpDate));

        return referrerActive && refereeActive;
    }  // end method isReferrerProgrameActive
}
