package com.castorama.checkout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import atg.adapter.gsa.ChangeAwareList;
import atg.adapter.gsa.ChangeAwareSet;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.promotion.CouponFormHandler;
import atg.commerce.promotion.PromotionException;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.core.util.StringUtils;

import com.castorama.commerce.profile.CastProfileFormHandler;

/**
 * This FormHandler is used to "claim" a promotion and add this promotion to the the users' list of promotions.
 *
 * @author EPAM team
 */
public class CastCouponFormHandler extends CouponFormHandler {
	/** Session property name: coupon id. */
	private static final String CLAIMED_COUPON_ID = "claimedCouponId";

	/** BUNDLE constant */
	public static final String BUNDLE = "com.castorama.checkout.ShoppingCartResources";


	/** COUPON_CODE constant */
        public static final String PROMOTION = "promotion";

	/** claimCouponSuccessURL property */
	String mClaimCouponSuccessURL;

	/** claimCouponErrorURL property */
	String mClaimCouponErrorURL;

	/** profile property */
	private Profile profile;

	/** claimedPromotion property */
	private RepositoryItem claimedPromotion;

	/** profile handler property */
	private CastProfileFormHandler profileHandler;

	/** shopping cart property */
	private OrderHolder mShoppingCart;

	  /**
	   * Sets property ShoppingCart
	   *
	   * @param pShoppingCart an <code>OrderHolder</code> value
	   */
	  public void setShoppingCart(OrderHolder pShoppingCart) {
	    mShoppingCart = pShoppingCart;
	  }

	  /**
	   * Returns property ShoppingCart
	   * @return an <code>OrderHolder</code> value
	   *
	   */
	  public OrderHolder getShoppingCart() {
	    return mShoppingCart;
	  }

	/**
	 * Return the profileHandler property.
	 *
	 * @return profileHandler property.
	 */
	public CastProfileFormHandler getProfileHandler() {
		return profileHandler;
	}

	/**
	 * Set the profileHandler property.
	 *
	 * @param pProfileHandler
	 */
	public void setProfileHandler(CastProfileFormHandler pProfileHandler) {
		profileHandler = pProfileHandler;
	}

	/**
	 * Set the claimCouponSuccessURL property.
	 *
	 * @param pClaimCouponSuccessURL
	 */
	public void setClaimCouponSuccessURL(String pClaimCouponSuccessURL) {
		mClaimCouponSuccessURL = pClaimCouponSuccessURL;
	}

	/**
	 * Return the claimCouponSuccessURL property.
	 *
	 * @return claimCouponSuccessURL property.
	 */
	public String getClaimCouponSuccessURL() {
		return mClaimCouponSuccessURL;
	}

	/**
	 * Set the claimCouponErrorURL property.
	 *
	 * @param pClaimCouponErrorURL
	 */
	public void setClaimCouponErrorURL(String pClaimCouponErrorURL) {
		mClaimCouponErrorURL = pClaimCouponErrorURL;
	}

	/**
	 * Return the claimCouponErrorURL property.
	 *
	 * @return claimCouponErrorURL property.
	 */
	public String getClaimCouponErrorURL() {
		return mClaimCouponErrorURL;
	}

	/**
	 * Returns property profile.
	 *
	 * @return The value of the property profile.
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * Sets property profile.
	 *
	 * @param profile
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * Returns property claimedPromotion.
	 *
	 * @return The value of the property claimedPromotion.
	 */
	public RepositoryItem getClaimedPromotion() {
		return claimedPromotion;
	}

	/**
	 * Sets property claimedPromotion.
	 *
	 * @param claimedPromotion
	 */
	public void setClaimedPromotion(RepositoryItem claimedPromotion) {
		this.claimedPromotion = claimedPromotion;
	}

	
    /*
    * @param  pRequest  parameter
    * @param  pResponse parameter
    *
    * @return 
    *
    * @throws ServletException exception
    * @throws IOException      exception
    */
   public boolean handleApplyLast(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {

	   CastClaimableManager manager = (CastClaimableManager) getClaimableManager();
	   List<RepositoryItem> climedPromotions = manager.getClimedPromotions(getProfile());
	   Order order = getShoppingCart().getCurrent();
	   List<RepositoryItem> promotions = manager.getPromotions(order);

	   try {
		   manager.cleanClimedPromotions(getProfile(), climedPromotions);
	   } catch (ClaimableException ce){
		   try {
				Throwable sourceException = ce.getSourceException();
				if (sourceException instanceof PromotionException) {
					PromotionException pe = (PromotionException) sourceException;
					processException(pe.getErrorCode(), pRequest);
				} else {
					processException(ce.getErrorCode(), pRequest);
				}
			} catch (Exception e) {
				processException("noCouponFound", pRequest);
				if (isLoggingError()) {
					logError("Unhandled error occured while generating ClaimableException");
					logError(e.getMessage());
				}
			}
			return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL, pRequest, pResponse);
		}
	   promotions.removeAll(climedPromotions);
	   climedPromotions.clear();
	   return handleClaimCoupon(pRequest, pResponse, promotions);
   }

   /**
	 * Method that is invoked to claim a coupon. It sets to form the appropriate promotion.
	 */
	@Override
	public boolean handleClaimCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		CastClaimableManager manager = (CastClaimableManager) getClaimableManager();
		Order order = getShoppingCart().getCurrent();
		List<RepositoryItem> promotions = manager.getPromotions(order);

		return handleClaimCoupon(pRequest, pResponse, promotions);
	}
   
   /**
	 * Method that is invoked to claim a coupon. It sets to form the appropriate promotion.
	 */
	public boolean handleClaimCoupon(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, List<RepositoryItem> promotions)
			throws ServletException, IOException {
		RepositoryItem claimedPromo;
		HttpSession session = pRequest.getSession();
		CastClaimableManager manager = (CastClaimableManager) getClaimableManager();
		List<RepositoryItem> climedPromotions = manager.getClimedPromotions(getProfile());

		try {
			claimedPromo = manager.claimThisCoupon(getProfile().getRepositoryId(), getCouponClaimCode(), promotions, climedPromotions);
		    setClaimedPromotion(claimedPromo);
		} catch (ClaimableException ce) {
			try {
				Throwable sourceException = ce.getSourceException();
				if (sourceException instanceof PromotionException) {
					PromotionException pe = (PromotionException) sourceException;
					processException(pe.getErrorCode(), pRequest);
				} else {
					processException(ce.getErrorCode(), pRequest);
				}
			} catch (Exception e) {
				processException("noCouponFound", pRequest);
				if (isLoggingError()) {
					logError("Unhandled error occured while generating ClaimableException");
					logError(e.getMessage());
				}
			}
			return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL, pRequest, pResponse);
		}
		climedPromotions.add(((RepositoryItem)getClaimedPromotion().getPropertyValue(PROMOTION)));
		manager.updateClimedPromotions(getProfile(), climedPromotions);
		session.setAttribute(CLAIMED_COUPON_ID, claimedPromo.getRepositoryId());
		return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL, pRequest, pResponse);
	}

	/**
	 * If an error occurs, this method will obtain the correct message from resource bundle.
	 */
	protected void processException(String pErrorCodeId, DynamoHttpServletRequest pRequest) throws ServletException,
			IOException {
		Locale usersLocale = getUserLocale(pRequest);
		ResourceBundle bundle = ResourceUtils.getBundle(BUNDLE, usersLocale);
		String errorMsg = bundle.getString(pErrorCodeId);
		addFormException(new DropletException(errorMsg, pErrorCodeId));
	}

	/**
	 * validate entered login and password before log in. If validation is successful invoke to claim a coupon
	 *
	 * @param pRequest
	 *            the servlet's request
	 * @param pResponse
	 *            the servlet's response
	 *
	 * @return true if there is no redirect, false if there is
	 *
	 * @throws ServletException
	 *             if a servlet error occurs
	 * @throws IOException
	 *             if the redirect fails
	 */
	public boolean handleLogin(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		CastProfileFormHandler profHandler = getProfileHandler();
		if (profHandler != null) {
			boolean handleLogin = profHandler.handleLogin(pRequest, pResponse);
			if (!handleLogin) {
				return handleClaimCoupon(pRequest, pResponse);
			} else {
				return handleLogin;
			}
		} else {
			return checkFormRedirect(mClaimCouponSuccessURL, mClaimCouponErrorURL, pRequest, pResponse);
		}
	}
}
