package com.castorama.utils;

import static com.castorama.utils.ContextTools.WEB_CONTEXT_STORE_ID;
import atg.userprofiling.Profile;

import com.castorama.commerce.order.CastOrderHolder;

/**
 * This class required for keeping user context data.
 * 
 * @author EPAM team
 */
public class ContextState {
    /** shoppingCart property */
    private CastOrderHolder shoppingCart;

    /** profile property */
    private Profile profile;

    /** forceContext property */
    private String forceContext;

    /** currentContext property */
    private String currentContext;

    /** newContext property */
    private String newContext;

    /** newContextCastStoreId property (for omniture on erase basket popup) */
    private String newContextCastStoreId;

    /** noCookie property */
    private boolean noCookie;

    /** changeLocalContext property */
    private boolean changeLocalContext;

    /** bonnesAffaires property */
    private boolean bonnesAffaires;

    /** prevBonnesAffaires property */
    private boolean prevBonnesAffaires;

    /** displayBonnesAffairesPopup property */
    private boolean displayBonnesAffairesPopup;

    /** savedBAProductId property */
    private String savedBAProductId;

    /**
     * @param shoppingCart
     */
    public ContextState(CastOrderHolder shoppingCart, Profile profile) {
        this.shoppingCart = shoppingCart;
        this.profile = profile;
    }

    /**
     * @return true if context need to change immediately
     */
    public boolean isForceChangeContext() {
        return forceContext != null;
    }

    /**
     * If user has basket in store A and want to switch to context B
     * 
     * @return true if need to display erase basket popup
     */
    public boolean isDisplayEraseBasketPopup() {
        return !isForceChangeContext() 
                && wasChanged() && isCartNotEmpty() 
                && !isPrevBonnesAffaires() && !isDisplayBonnesAffairesPopup()
                && isChangeLocalContext();
    }

    /**
     * @return true if context was changed
     */
    public boolean wasChanged() {
        boolean isChanged = false;
        if (currentContext != null && newContext != null)
            isChanged = !currentContext.equals(newContext);
        return isChanged;
    }

    /**
     * @return true if cart not empty
     */
    public boolean isCartNotEmpty() {
        if (getShoppingCart()!=null) {
            return !getShoppingCart().isCartEmpty() || !getShoppingCart().isCartLocalEmpty();
        }
        return false;
    }

    /**
     * The id of store, in the context of which we need 
     * to display store and c&c top banner (including Bonnes Affaires pages)
     * 
     * @return wrappedContext
     */
    public String getWrappedContext() {
        String wrappedContext = null;
        if (isForceChangeContext()) {
            wrappedContext = forceContext;
        } else if (isPrevBonnesAffaires() || isDisplayBonnesAffairesPopup()) {
            wrappedContext = newContext;
        } else if (isDisplayEraseBasketPopup()) {
            wrappedContext = currentContext;
        } else {
            wrappedContext = newContext;
        }
        
        if (wrappedContext == null)
            wrappedContext = WEB_CONTEXT_STORE_ID;
        
        return wrappedContext;
    }

    /**
     * The id of store, in the context of which we need to switch
     * 
     * @return actualContext
     */
    public String getActualContext() {
        String actualContext = null;
        if (isBonnesAffaires()) {
            // Switch user to a kind of "fake" (transient) web context 
            actualContext = WEB_CONTEXT_STORE_ID;
        } else {
            actualContext = getWrappedContext();
        }
        
        return actualContext;
    }

    /**
     * @return true if only geolocation cookie was cleared
     */
    public boolean wasCookieCleared() {
        return isNoCookie() && currentContext!=null && !currentContext.equals(WEB_CONTEXT_STORE_ID);
    }

    /**
     * @return the shoppingCart
     */
    public CastOrderHolder getShoppingCart() {
        return shoppingCart;
    }

    /**
     * @param shoppingCart
     *            the shoppingCart to set
     */
    public void setShoppingCart(CastOrderHolder shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    /**
     * @return the profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * @return the forceContext
     */
    public String getForceContext() {
        return forceContext;
    }

    /**
     * @param forceContext
     *            the forceContext to set
     */
    public void setForceContext(String forceContext) {
        this.forceContext = forceContext;
    }

    /**
     * @return the currentContext
     */
    public String getCurrentContext() {
        return currentContext;
    }

    /**
     * @param currentContext
     *            the currentContext to set
     */
    public void setCurrentContext(String currentContext) {
        this.currentContext = currentContext;
    }

    /**
     * @return the newContext
     */
    public String getNewContext() {
        return newContext;
    }

    /**
     * @param newContext
     *            the newContext to set
     */
    public void setNewContext(String newContext) {
        this.newContext = newContext;
    }

    /**
     * @return the newContextCastStoreId
     */
    public String getNewContextCastStoreId() {
        return newContextCastStoreId;
    }

    /**
     * @param newContextCastStoreId the newContextCastStoreId to set
     */
    public void setNewContextCastStoreId(String newContextCastStoreId) {
        this.newContextCastStoreId = newContextCastStoreId;
    }

    /**
     * @return the noCookie
     */
    public boolean isNoCookie() {
        return noCookie;
    }

    /**
     * @param noCookie the noCookie to set
     */
    public void setNoCookie(boolean noCookie) {
        this.noCookie = noCookie;
    }

    /**
     * Indicates that one of the processed contexts has localPrix=true or retraitMagasin=true
     * 
     * @return the changeLocalContext
     */
    public boolean isChangeLocalContext() {
        return changeLocalContext;
    }

    /**
     * @param changeLocalContext the changeLocalContext to set
     */
    public void setChangeLocalContext(boolean changeLocalContext) {
        this.changeLocalContext = changeLocalContext;
    }

    /**
     * Determines whether user navigates Bonnes Affaires category.
     * 
     * @return the bonnesAffaires
     */
    public boolean isBonnesAffaires() {
        return bonnesAffaires && isChangeLocalContext();
    }

    /**
     * @param bonnesAffaires the bonnesAffaires to set
     */
    public void setBonnesAffaires(boolean bonnesAffaires) {
        this.bonnesAffaires = bonnesAffaires;
    }

    /**
     * Determines whether user navigates from Bonnes Affaires category.
     * 
     * @return the prevBonnesAffaires
     */
    public boolean isPrevBonnesAffaires() {
        return prevBonnesAffaires && isChangeLocalContext();
    }

    /**
     * @param prevBonnesAffaires the prevBonnesAffaires to set
     */
    public void setPrevBonnesAffaires(boolean prevBonnesAffaires) {
        this.prevBonnesAffaires = prevBonnesAffaires;
    }

    /**
     * If user has basket in local store context and want to buy Bonnes Affaires product
     * 
     * @return true if need to display bonnes affaires popup
     */
    public boolean isDisplayBonnesAffairesPopup() {
        return !isForceChangeContext()
                && displayBonnesAffairesPopup
                && isChangeLocalContext();
    }

    /**
     * @param displayBonnesAffairesPopup the displayBonnesAffairesPopup to set
     */
    public void setDisplayBonnesAffairesPopup(boolean displayBonnesAffairesPopup) {
        this.displayBonnesAffairesPopup = displayBonnesAffairesPopup;
    }

    /**
     * @return the savedBAProductId
     */
    public String getSavedBAProductId() {
        return savedBAProductId;
    }

    /**
     * @param savedBAProductId the savedBAProductId to set
     */
    public void setSavedBAProductId(String savedBAProductId) {
        this.savedBAProductId = savedBAProductId;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + "   Profile: " + getProfile().getRepositoryId() + 
                "; force:" + forceContext + "; cur:" + currentContext + "; new:" + newContext + 
                "; actual:" + getActualContext() + "; wrapped:" + getWrappedContext() + 
                "; BA:" + isBonnesAffaires() + ";" + isPrevBonnesAffaires() + 
                "; popups:" + isDisplayBonnesAffairesPopup() + ";" + isDisplayEraseBasketPopup();
    }
}
