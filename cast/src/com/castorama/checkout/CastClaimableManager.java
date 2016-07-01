package com.castorama.checkout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.ChangeAwareSet;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionException;
import atg.commerce.promotion.PromotionTools;
import atg.core.util.ResourceUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.Profile;

/**
 * This class inherits almost all logic from ClaimableManager class. The only
 * difference is - method claimThisCoupon() returns appropriate promotion for
 * the given coupon code.
 *
 * @author EPAM team
 */
public class CastClaimableManager extends ClaimableManager {
    /** PROMOTION_RESOURCE_NAME constant. */
    static final String PROMOTION_RESOURCE_NAME = "atg.commerce.promotion.PromotionResources";
    
    /** PROMOTION_RESOURCE_NAME constant. */
    static final String MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS = "notCompatibleWithOthers";

    /** PROMOTION_RESOURCE_NAME constant. */
    static final String MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS_COUPONS = "notCompatibleWithOthersCoupons";

    /** PROMOTION constant */
    public static final String PROMOTION = "promotion";

    /** PROMOTION_USES_PROPERTY constant */
    public static final String PROMOTION_USES_PROPERTY = "uses";
    
    /** PROMOTION_NOT_COMPATIBLE_PROPERTY constant */
    public static final String PROMOTION_NOT_COMPATIBLE_PROPERTY = "opeco_nc_all";
    
    /** PROMOTION_NON_COMPATIBLE_LIST_PROPERTY constant */
    public static final String PROMOTION_NON_COMPATIBLE_LIST_PROPERTY = "opeco_nc";
    
    /** REFEREE constant */
    public static final String REFEREE = "referee";
    
    
    public List<RepositoryItem> getPromotions(Order order){
		List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();
        getPromotionTools().getOrderPromotions(order, promotions);

        for (ShippingGroup shippingGroup : (List<ShippingGroup>) order.getShippingGroups()) {
            getPromotionTools().getShippingPromotions(shippingGroup, promotions);
            for (CommerceItemRelationship commerceItemRelationShip :
                (List<CommerceItemRelationship>) shippingGroup.getCommerceItemRelationships()) {
                CommerceItem commerceItem = commerceItemRelationShip.getCommerceItem();
                getPromotionTools().getItemPromotions(commerceItem, promotions);
                }
        }
        return promotions;
    }
    
    /**
     * This method claims the given coupon and returns appropriate promotion. It
     * uses some logic from ClaimableManager.claimCoupon() method.
     * @see    ClaimableManager#claimCoupon(String pProfileId, String
     *         pCouponClaimCode)
     */
    public RepositoryItem claimThisCoupon(String pProfileId, String pCouponClaimCode, List<RepositoryItem> promotions, List<RepositoryItem> climedPromotions) throws ClaimableException {
        RepositoryItem promotion = null;
        TransactionDemarcation td = new TransactionDemarcation();

        try { 
            td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

            MutableRepositoryItem profile = (MutableRepositoryItem) getPromotionTools().getProfile(pProfileId);

            if (profile == null) {
                throw new ClaimableException(PromotionConstants.MSG_NULL_PROFILE,
                                             ResourceUtils.getMsgResource(PromotionConstants.MSG_NULL_PROFILE,
                                                                          PROMOTION_RESOURCE_NAME,
                                                                          sPromotionResourceBundle));
            }

            promotion = findAndClaimCoupon(profile, pCouponClaimCode, promotions, climedPromotions);

            try {
            	getPromotionTools().grantPromotion(profile,
                                              (RepositoryItem) promotion.getPropertyValue(getClaimableTools()
                                                                                          .getPromotionPropertyName()));
            	getPromotionTools().initializePricingModels();
            } catch (PromotionException pe) {
                throw new ClaimableException(pe);
            }

            RepositoryView repositoryView =
                getPromotionTools().getProfileRepository().getItemDescriptor(REFEREE).getRepositoryView();
            RqlStatement statement = RqlStatement.parseRqlStatement("couponid = ?0");
            RepositoryItem[] referee = statement.executeQuery(repositoryView, new Object[] {pCouponClaimCode});
            if ((referee != null) && (referee.length == 1)) {
                MutableRepositoryItem mri =
                    ((MutableRepository) getPromotionTools().getProfileRepository()).getItemForUpdate(referee[0]
                                                                                                      .getRepositoryId(),
                                                                                                      REFEREE);
                mri.setPropertyValue("applied", true);
                ((MutableRepository) getPromotionTools().getProfileRepository()).updateItem(mri);
            }

            return promotion;
        } catch (ClaimableException ce) {
            try {
                getTransactionManager().getTransaction().setRollbackOnly();
            } catch (javax.transaction.SystemException se) {
                if (isLoggingError()) {
                    logError(se);
                }
            }
            throw ce;
        } catch (Exception exc) {
            if (isLoggingError()) {
                logError(exc);
            }

            try {
                getTransactionManager().getTransaction().setRollbackOnly();
            } catch (javax.transaction.SystemException se) {
                if (isLoggingError()) {
                    logError(se);
                }
            }
            throw new ClaimableException(exc);
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException tde) {
                if (isLoggingError()) {
                    logError(tde);
                }
            }
        }  // end try-catch-finally
    }
    
    
    public RepositoryItem findAndClaimCoupon(MutableRepositoryItem profile, String pCouponClaimCode, List<RepositoryItem> promotions, List<RepositoryItem> climedPromotions) throws ClaimableException {

      RepositoryItem coupon;

      if (pCouponClaimCode == null || pCouponClaimCode.trim().length() == 0) {
        String[] msgArgs = { pCouponClaimCode };
        throw new ClaimableException(PromotionConstants.MSG_NO_COUPON_FOUND, 
              ResourceUtils.getMsgResource(PromotionConstants.MSG_NO_COUPON_FOUND, PROMOTION_RESOURCE_NAME, sPromotionResourceBundle, msgArgs));
      }

      coupon = claimItem(pCouponClaimCode);
      
      if (coupon == null || !checkPromotionType(coupon)) {
        String[] msgArgs = { pCouponClaimCode };
        throw new ClaimableException(PromotionConstants.MSG_NO_COUPON_FOUND, 
              ResourceUtils.getMsgResource(PromotionConstants.MSG_NO_COUPON_FOUND, PROMOTION_RESOURCE_NAME, sPromotionResourceBundle, msgArgs));
      }

      if (checkCouponisUsed(coupon, profile)) {
    	  String[] msgArgs = { pCouponClaimCode };
    	  throw new ClaimableException(PromotionConstants.MSG_NO_COUPON_FOUND, 
                  ResourceUtils.getMsgResource(PromotionConstants.MSG_NO_COUPON_FOUND, PROMOTION_RESOURCE_NAME, sPromotionResourceBundle, msgArgs));
      }

      boolean isExpired = checkExpireDate(coupon);

      if (isExpired == true) {
        String args[] = { pCouponClaimCode };
        throw new ClaimableException(PromotionConstants.MSG_COUPON_EXPIRED, 
              ResourceUtils.getMsgResource(PromotionConstants.MSG_COUPON_EXPIRED, PROMOTION_RESOURCE_NAME, sPromotionResourceBundle, args));
      }

      if (checkCouponNotCompatible(coupon, climedPromotions)) {
          throw new ClaimableException(MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS_COUPONS, MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS_COUPONS);
      }
      
      if (isPromotionNotCompatible(coupon, promotions)) {
          throw new ClaimableException(MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS, MSG_COUPON_NOT_COMPATIBLE_WITH_OTHERS);
      }

      return coupon;
    }
    
   
    /**
     * Checks to see if the promotion is compatible with all other promotions.
     *
     * @param pClaimedPromo
     * @return true if the item is compatible.
     **/
    protected boolean isPromotionNotCompatible(RepositoryItem pClaimedPromo, List<RepositoryItem> pActivePromotions) {

    	RepositoryItem promotion = !checkPromotionType(pClaimedPromo) ? pClaimedPromo : (RepositoryItem) pClaimedPromo.getPropertyValue(PROMOTION);
        Object unCompatibleObject = promotion.getPropertyValue(PROMOTION_NOT_COMPATIBLE_PROPERTY);
        Object unCompatiblePromotions = promotion.getPropertyValue(PROMOTION_NON_COMPATIBLE_LIST_PROPERTY);
        
        boolean nonCompatible = false;
        if (!pActivePromotions.isEmpty() && (pActivePromotions.size() > 1 || !pActivePromotions.contains(promotion))) {
            nonCompatible = (unCompatibleObject != null) ? (Boolean) unCompatibleObject : false;
            if (!nonCompatible) {
                if(unCompatiblePromotions != null){
                    List<String> activeIds = new ArrayList<String>();
                    for(RepositoryItem promo : pActivePromotions){
                    	activeIds.add(promo.getRepositoryId());
                    }
                    List<RepositoryItem> unCompatibleList = (List<RepositoryItem>) unCompatiblePromotions;
                    for(RepositoryItem unCompatiblePromotion : unCompatibleList){
                        if(activeIds.contains(unCompatiblePromotion.getRepositoryId())){
                            nonCompatible = true;
                            break;
                        }
                    }
                } else {
                    nonCompatible = true;
                }
            }
        }
        return nonCompatible;
    }

    protected boolean checkCouponisUsed(RepositoryItem coupon, MutableRepositoryItem profile) {
        RepositoryItem promotion = (RepositoryItem) coupon.getPropertyValue(getClaimableTools().getPromotionPropertyName());
        boolean allowMultiple = (Boolean) promotion.getPropertyValue(getPromotionTools().getAllowMultipleProperty());
        int usedPromotionCount = 0;
        if (allowMultiple) {
            Collection promotionStatuses = (Collection) profile.getPropertyValue(getPromotionTools().getActivePromotionsProperty());
            for (Object promotionObject : promotionStatuses) {
                RepositoryItem promotionStatus = (RepositoryItem) promotionObject;
                RepositoryItem usedPromotion = (RepositoryItem) promotionStatus.getPropertyValue(PROMOTION);
                if (usedPromotion.getRepositoryId().equals(promotion.getRepositoryId())) {
                    usedPromotionCount++;
                    int usesValue = (Integer) promotion.getPropertyValue(PROMOTION_USES_PROPERTY);
                    if (usedPromotionCount == usesValue) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean checkCouponNotCompatible(RepositoryItem pClaimedPromo, List<RepositoryItem> pActivePromotions) {

    	boolean result = isPromotionNotCompatible(pClaimedPromo, pActivePromotions);
    	if (!result){
    		List<RepositoryItem> testList = Arrays.asList(new RepositoryItem[]{pClaimedPromo});
    		for (Iterator<RepositoryItem> it = pActivePromotions.iterator(); it.hasNext();){
    			result = isPromotionNotCompatible(it.next(), testList);
    			if (result) break;
    		}
    	}
    	
    	return result;
    }    
    
    public void updateClimedPromotions(Profile profile, List<RepositoryItem> climedPromotions){
    	Set<String> promotionIds = new LinkedHashSet<String>();
    	for (Iterator<RepositoryItem> it = climedPromotions.iterator(); it.hasNext();){
    		promotionIds.add(it.next().getRepositoryId());
    	}
    	profile.setPropertyValue("appliedCoupons", promotionIds);
    }
    
    public void cleanClimedPromotions(Profile profile, List<RepositoryItem> climedPromotions) throws ClaimableException{
    	
    	PromotionTools tools = getPromotionTools();
    	try {
	    	for (Iterator<RepositoryItem> it = climedPromotions.iterator(); it.hasNext();){
	    		tools.revokePromotion(profile, it.next(), true);
	    	}
	    	tools.initializePricingModels();
    	} catch (PromotionException pe){
    		throw new ClaimableException(PromotionConstants.MSG_ERROR_CLAIMING_COUPON, PromotionConstants.MSG_ERROR_CLAIMING_COUPON);
    	} catch (Exception e){
    		throw new ClaimableException(e.getMessage());
    	} 
    }
    
    public List<RepositoryItem> getClimedPromotions(Profile profile){
    
		List<RepositoryItem> climedPromotions = new ArrayList<RepositoryItem>();
		ChangeAwareSet appliedCoupons = (ChangeAwareSet)profile.getPropertyValue("appliedCoupons");  
		ChangeAwareList activePromotions = (ChangeAwareList)profile.getPropertyValue("activePromotions");
		for (Iterator it = activePromotions.iterator(); it.hasNext();){
			RepositoryItem promotionStatus = (RepositoryItem)it.next();
			RepositoryItem activePromotion = (RepositoryItem)promotionStatus.getPropertyValue("promotion");
			if (appliedCoupons.contains(activePromotion.getRepositoryId())){
				climedPromotions.add(activePromotion);
			}
		}
		updateClimedPromotions(profile, climedPromotions);
		return climedPromotions;
    }

}
