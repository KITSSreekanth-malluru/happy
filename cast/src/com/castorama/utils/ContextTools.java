package com.castorama.utils;

import static com.castorama.commerce.profile.Constants.CURRENT_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.DESCRIPTOR_NAME_PROFILE;
import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.NEWSLETTER_ITEM_DESC;
import static com.castorama.commerce.profile.Constants.PREF_STORE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.PROFILE_ID_FIELD_NEWSLETTER_PROP;
import static com.castorama.seo.UrlRewriteService.PATTERN_CATEGORY;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;

import atg.adapter.gsa.GSAItem;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.Order;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.castorama.commerce.catalog.CastCatalogTools;
import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.profile.Constants;
import com.castorama.seo.SEOUtils;

public class ContextTools extends GenericService {
    /** Web context StoreId. */
    public static final String WEB_CONTEXT_STORE_ID = "999";

    /** Cookie name. */
    private static final String COOKIE_NAME = "s_cdao";

    /** Bonnes Affaires cookie name. */
    private static final String BA_COOKIE_NAME = "s_spmc";

    /** Cookie path. */
    private static final String COOKIE_PATH = "/";

    /** Cookie age property name. */
    private static final String COOKIE_AGE = "cookieAge";

    /** ATG_STORE_ID_PROP constant. */
    private static final String ATG_STORE_ID_PROP = "id";

    /** Component path profile. */
    public static String COMPONENT_PATH_PROFILE = "/atg/userprofiling/Profile";

    /** Component path shopping cart. */
    public static String COMPONENT_PATH_SHOPPING_CART = "/atg/commerce/ShoppingCart";

    /** CDAO_URL_PARAM constant. */
    private static final String CDAO_URL_PARAM = "cdao";
    
    /** RGR_URL_PARAM constant. */
    private static final String RGR_URL_PARAM = "rgr";

    /** LEADFORMANCE_STORE_URL_PARAM constant. */
    private static final String LEADFORMANCE_STORE_URL_PARAM= "magasinId";

    /** BONNES_AFFAIRES_URL_PARAM constant */
    public static final String BONNES_AFFAIRES_URL_PARAM = "ba";

    /** CATEGORY_ID_URL_PARAM constant */
    public static final String CATEGORY_ID_URL_PARAM = "categoryId";

    /** BONNES_AFFAIRES_PROPERTY constant */
    public static final String BONNES_AFFAIRES_PROPERTY = "bonnesAffaires";

    /** Session property name prevBonnesAffaires. */
    public static final String BONNES_AFFAIRES_SESSION_PARAM = "prevBonnesAffaires";

    /** Session property name displayBonnesAffairesPopup. */
    public static final String BONNES_AFFAIRES_POPUP_SESSION_PARAM = "displayBonnesAffairesPopup";

    /** Session property name savedBAProductId. */
    public static final String BONNES_AFFAIRES_PRODUCT_SESSION_PARAM = "savedBAProductId";

    /** ITEM_CATEGORY constant. */
    private static final String ITEM_CATEGORY = "category";

    /** HEADER_LOCATION constant. */
    private static final String HEADER_LOCATION = "Location";

    /** HEADER_CONNECTION constant. */
    private static final String HEADER_CONNECTION = "Connection";

    /** CLOSE constant */
    private static final String CLOSE = "close";

    /** HOME_PAGE_URL constant */
    private static final String HOME_PAGE_URL = "/store/";

    /** Number regex. */
    private static final String NUMBER_REGEX = "\\d+";

    /** ROBOT constant. */
    private static final String ROBOT = "robot";

    /** Catalog tools. */
    private CatalogTools catalogTools;

    /** Store tools. */
    private StoreTools storeTools;

    /** Order tools. */
    private CastOrderTools orderTools;

    /** Newsletter Repository. */
    private Repository newsletterRepository;

    /** Profile Repository. */
    private Repository profileRepository;

    /** productCatalog Repository. */
    private Repository productCatalog;

    /** TransactionManager property */
    private TransactionManager transactionManager;

    /**
     * Gets the context state object.
     * 
     * @param request
     *            parameter to set.
     * @param response
     *            parameter to set.
     * 
     * @return the contextState
     */
    public ContextState getContextState(DynamoHttpServletRequest request, DynamoHttpServletResponse response) {
        Profile profile = (Profile) request.resolveName(COMPONENT_PATH_PROFILE);
        
        RepositoryItem currentLocalStore = null;
        try {
            if (profile.getItemDescriptor().hasProperty(CURRENT_LOCAL_STORE_PROFILE_PROP))
                currentLocalStore = (RepositoryItem) profile.getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        
        CastOrderHolder shoppingCart = null;
        if (currentLocalStore != null) {
            shoppingCart = (CastOrderHolder)request.resolveName(COMPONENT_PATH_SHOPPING_CART);
        }
        
        ContextState cs = new ContextState(shoppingCart, profile);
        if (currentLocalStore != null)
            cs.setCurrentContext(currentLocalStore.getRepositoryId());
        
        String newContext = null;
        
        String geoCookieStoreId = getGeoCookie(request);
        if (geoCookieStoreId != null) {
            newContext = geoCookieStoreId;
        } else {
            /* 
             * When user's session ends switch user to his previous store context, 
             * if user has been navigating store before starting working with Bonnes Affaires;
             */
            newContext = restoreCookie(request, response);
            if (newContext != null)
                cs.setForceContext(newContext);
            else 
                cs.setNoCookie(true);
        }
        
        String leadFormanceStoreId = request.getParameter(LEADFORMANCE_STORE_URL_PARAM);
        if (leadFormanceStoreId != null && leadFormanceStoreId.matches(NUMBER_REGEX)) {
            newContext = leadFormanceStoreId;
            
            if (!WEB_CONTEXT_STORE_ID.equals(cs.getCurrentContext())) {
                createLongliveCookie(response, newContext); // cases 1-3
            } else {
                cs.setForceContext(newContext); // case 3.1
            }
            // Redirect to home page in case user went to site from leadformance
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader(HEADER_CONNECTION, CLOSE);
            response.setHeader(HEADER_LOCATION, HOME_PAGE_URL);
        }
        
        String castStoreId = request.getParameter(CDAO_URL_PARAM);
        if (castStoreId != null && castStoreId.matches(NUMBER_REGEX)) {
            String atgStoreId = getAtgStoreId(castStoreId);
            if (atgStoreId != null) {
                newContext = atgStoreId;
                cs.setForceContext(newContext);
            }
        }
        
        String rgrStoreId=request.getParameter(RGR_URL_PARAM);
        if(rgrStoreId != null && rgrStoreId.matches(NUMBER_REGEX) ){
        	String atgStoreId =getAtgStoreId(rgrStoreId);
        	if(atgStoreId != null){
        		newContext=atgStoreId;
        		cs.setForceContext(newContext);
        	}
        	
        }
        
        if (newContext != null && !newContext.matches(NUMBER_REGEX)) {
            newContext = WEB_CONTEXT_STORE_ID; 
        }
        
        if (request.isBrowserType(ROBOT)) {
            cs.setNewContext(WEB_CONTEXT_STORE_ID);
        } else {
            cs.setNewContext(newContext);
            RepositoryItem newContextStore = getStoreTools().getStore(newContext);
            String newContextCastStoreId = newContextStore != null ? (String)newContextStore.getPropertyValue(StoreTools.STORE_ID) : null;
            cs.setNewContextCastStoreId(newContextCastStoreId);
        }
        
        fillBonnesAffairesStates(cs, request);
        
        cs.setChangeLocalContext(
                checkStoresLocalPricesOrClickAndCollect(cs.getCurrentContext(), cs.getNewContext()));
        
        return cs;
    }

    /**
     * Fills Bonnes Affaires properties in ContextState object
     * 
     * @param cs
     * @param request
     */
    private void fillBonnesAffairesStates(ContextState cs, DynamoHttpServletRequest request) {
        boolean bonnesAffaires = false;
        
        // don't switch to fake web context if current store doesn't have localPrices or clickAndCollect
        boolean isLocalStore = checkStoresLocalPricesOrClickAndCollect(cs.getNewContext());
        if (isLocalStore) {
            bonnesAffaires = checkBonnesAffairesPageConditions(request);
        }
        
        cs.setBonnesAffaires(bonnesAffaires);
        
        // Sets whether to previous page is Bonnes Affaires
        Object prevBonnesAffaires = request.getSession().getAttribute(BONNES_AFFAIRES_SESSION_PARAM);
        cs.setPrevBonnesAffaires(prevBonnesAffaires!=null?(Boolean)prevBonnesAffaires:false);
        request.getSession().removeAttribute(BONNES_AFFAIRES_SESSION_PARAM);
        
        request.getSession().setAttribute(BONNES_AFFAIRES_SESSION_PARAM, bonnesAffaires);
        
        // Sets whether to need display Bonnes Affaires popup
        Object displayBonnesAffairesPopup = request.getSession().getAttribute(BONNES_AFFAIRES_POPUP_SESSION_PARAM);
        cs.setDisplayBonnesAffairesPopup(displayBonnesAffairesPopup!=null?(Boolean)displayBonnesAffairesPopup:false);
        
        // Sets saved Bonnes Affaires productId
        String savedBAProductId = (String) request.getSession().getAttribute(BONNES_AFFAIRES_PRODUCT_SESSION_PARAM);
        cs.setSavedBAProductId(savedBAProductId);
    }

    /**
     * @param request
     * @param bonnesAffaires
     * @return
     */
    private boolean checkBonnesAffairesPageConditions(DynamoHttpServletRequest request) {
        boolean bonnesAffaires = false;
        
        String bonnesAffairesParam = request.getParameter(BONNES_AFFAIRES_URL_PARAM);
        if (bonnesAffairesParam != null) {
            bonnesAffaires = Boolean.parseBoolean(bonnesAffairesParam);
        } else {
            String categoryId = null;
            
            String uri = request.getRequestURI();
            if (PATTERN_CATEGORY.matcher(uri).matches()) {
                String categoryUriStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTM));
                categoryId = categoryUriStr.substring(categoryUriStr.lastIndexOf('-') + 1);
            } else {
                categoryId = request.getParameter(CATEGORY_ID_URL_PARAM);
            }
            
            if (categoryId != null) {
                RepositoryItem category = null;
                try {
                    category = getProductCatalog().getItem(categoryId, ITEM_CATEGORY);
                    if (category != null) {
                        Boolean bonnesAffairesProperty = (Boolean) category.getPropertyValue(BONNES_AFFAIRES_PROPERTY);
                        bonnesAffaires = (bonnesAffairesProperty != null)?bonnesAffairesProperty:false;
                    }
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError("Can not find category with id=" + categoryId + ". Wrong URL?" + uri);
                    }
                }
            }
        }
        return bonnesAffaires;
    }

    /**
     * Check that some of stores has localPrix=true or retraitMagasin=true
     * 
     * @param storeIds
     */
    private boolean checkStoresLocalPricesOrClickAndCollect(String... storeIds) {
        boolean result = false;
        for (String storeId : storeIds) {
            result = result || getStoreTools().checkStoreLocalPricesOrClickAndCollect(storeId);
        }
        return result;
    }

    /**
     * Updates the context.
     * 
     * @param response parameter to set.
     * @param profileId parameter to set.
     * @param storeId parameter to set.
     */
    public void updateContext(DynamoHttpServletResponse response, String profileId, String storeId) throws ServletException {
        if (storeId != null) {
            createCookie(response, storeId);
            updateProfileStore(response, profileId, storeId);
        }
    }

    /**
     * Update profile preferred store.
     * 
     * @param response
     * @param profileId
     * @param storeId
     * @throws ServletException
     */
    public void updateProfileStore(DynamoHttpServletResponse response, String profileId, String storeId) throws ServletException {
        TransactionDemarcation td = new TransactionDemarcation();
        try {
            td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
            try {
                if (profileId != null) {
                    RepositoryItem magasin = getStoreTools().getStore(storeId);
                    
                    MutableRepositoryItem mutableProfile = ((MutableRepository) getProfileRepository()).getItemForUpdate(profileId, DESCRIPTOR_NAME_PROFILE);
                    if (mutableProfile != null) {
                        RepositoryItem profileStore = (RepositoryItem) mutableProfile.getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
                        if (profileStore == null || !profileStore.getRepositoryId().equals(storeId)) {
                            mutableProfile.setPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP, magasin);
                            ((MutableRepository) getProfileRepository()).updateItem(mutableProfile);
                        }
                        
                        String newsletterId = (String) mutableProfile.getPropertyValue(EMAIL_PROFILE_PROP);
                        if (newsletterId != null) {
                            MutableRepositoryItem newsletter = ((MutableRepository) getNewsletterRepository()).getItemForUpdate(newsletterId, NEWSLETTER_ITEM_DESC);
                            if (newsletter != null) {
                                RepositoryItem newsletterStore = (RepositoryItem) newsletter.getPropertyValue(PREF_STORE_NEWSLETTER_PROP);
                                if (newsletterStore == null || !newsletterStore.getRepositoryId().equals(storeId)) {
                                    newsletter.setPropertyValue(PREF_STORE_NEWSLETTER_PROP, magasin);
                                    ((MutableRepository) getNewsletterRepository()).updateItem(newsletter);
                                }
                            }
                        }
                    }
                }
            } catch (RepositoryException repositoryExc) {
                if (isLoggingError()) {
                    logError(repositoryExc);
                }
            }
        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }
    }

    /**
     * Update newsletter account preferred store.
     * 
     * @param response
     * @param newsletterId
     * @param storeId
     * @throws ServletException
     */
    public void updateNewsletterStore(DynamoHttpServletResponse response, String newsletterId, String storeId) throws ServletException {
        TransactionDemarcation td = new TransactionDemarcation();
        try {
            td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
            try {
                MutableRepositoryItem newsletter = ((MutableRepository) getNewsletterRepository()).getItemForUpdate(newsletterId, NEWSLETTER_ITEM_DESC);
                String newsletterProfileId = (String) newsletter.getPropertyValue(PROFILE_ID_FIELD_NEWSLETTER_PROP);
                if (newsletterProfileId != null) {
                    MutableRepositoryItem profile = ((MutableRepository) getProfileRepository()).getItemForUpdate(newsletterProfileId, DESCRIPTOR_NAME_PROFILE);
                    String profilePrimaryEmail = (String) profile.getPropertyValue(EMAIL_PROFILE_PROP);
                    if (profilePrimaryEmail.equals(newsletterId)) {
                        createCookie(response, storeId);
                        return;
                    }
                }
                
                RepositoryItem newsletterStore = (RepositoryItem) newsletter.getPropertyValue(PREF_STORE_NEWSLETTER_PROP);
                if (newsletterStore == null || !newsletterStore.getRepositoryId().equals(storeId)) {
                    RepositoryItem magasin = getStoreTools().getStore(storeId);
                    newsletter.setPropertyValue(PREF_STORE_NEWSLETTER_PROP, magasin);
                    ((MutableRepository) getNewsletterRepository()).updateItem(newsletter);
                }
                
            } catch (RepositoryException repositoryExc) {
                if (isLoggingError()) {
                    logError(repositoryExc);
                }
            }
        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }
    }

    /**
     * Erases user basket.
     * 
     * @param cs
     */
    public void clearBasket(ContextState cs) {
        CastOrderHolder shoppingCart = cs.getShoppingCart();
        if (shoppingCart != null) {
            getOrderTools().clearOrder(shoppingCart.getCurrent());
            getOrderTools().clearOrder(shoppingCart.getCurrentLocal());
        }
    }

    /**
     * Clear order if its magasinId and currentLocalStore are differs.
     * 
     * @param response
     * @param profile
     * @param order
     * @throws ServletException
     */
    public void clearOrderIfStoreChanged(DynamoHttpServletResponse response, Profile profile, Order order) throws ServletException {
        if (order != null) {
            GSAItem currentLocalStore = (GSAItem) profile.getPropertyValue(Constants.CURRENT_LOCAL_STORE_PROFILE_PROP);
            String profileStoreId = null;
            if (currentLocalStore!= null) 
                profileStoreId = currentLocalStore.getRepositoryId();
            
            String orderStoreId = ((CastOrderImpl)order).getMagasinId();
            
            if (!orderStoreId.equals(profileStoreId)) {
                if (checkStoresLocalPricesOrClickAndCollect(profileStoreId, orderStoreId)) {
                    getOrderTools().clearOrder(order);
                }
                updateProfileStore(response, order.getProfileId(), profileStoreId);
            }
        }
    }

    /**
     * Updates shopping cart store id from Profile
     * 
     * @param cs
     * @param storeId
     */
    public void updateShoppingCart(ContextState cs, String storeId) {
        CastOrderHolder shoppingCart = cs.getShoppingCart();
        if (shoppingCart != null)
            shoppingCart.setStoreIdForProfileOrders(cs.getProfile(), storeId);
    }

    /**
     * Returns cookie value.
     * 
     * @param request
     * @return cookie value
     */
    public String getGeoCookie(DynamoHttpServletRequest request) {
        return request.getCookieParameter(COOKIE_NAME);
    }

    /**
     * Returns BA cookie value.
     * 
     * @param request
     * @return BA cookie value
     */
    public String getBACookie(DynamoHttpServletRequest request) {
        return request.getCookieParameter(BA_COOKIE_NAME);
    }

    /**
     * Create required cookie
     * 
     * @param response
     *            response
     * @param storeId
     *            store id
     */
    public void createCookie(DynamoHttpServletResponse response, String storeId) {
        if (storeId == null || storeId.equals(WEB_CONTEXT_STORE_ID)) {
            createSessionCookie(response);
        } else {
            createLongliveCookie(response, storeId);
        }
    }

    /**
     * Create long live cookie
     * 
     * @param response
     *            response
     * @param storeId
     *            store id
     */
    public void createLongliveCookie(DynamoHttpServletResponse response, String storeId) {
        Cookie cookie = new Cookie(COOKIE_NAME, storeId);
        cookie.setMaxAge(getCookieAge());
        cookie.setPath(COOKIE_PATH);
        response.addCookie(cookie);
    }

    /**
     * Create session cookie
     * 
     * @param response
     *            response
     */
    public void createSessionCookie(DynamoHttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, WEB_CONTEXT_STORE_ID);
        cookie.setPath(COOKIE_PATH);
        response.addCookie(cookie);
    }

    /**
     * Create BA cookie
     * 
     * @param response
     *            response
     * @param storeId
     *            store id
     */
    public void createBACookie(DynamoHttpServletResponse response, String storeId) {
        if (storeId != null && !storeId.equals(WEB_CONTEXT_STORE_ID)) {
            Cookie cookie = new Cookie(BA_COOKIE_NAME, storeId);
            cookie.setMaxAge(getCookieAge());
            cookie.setPath(COOKIE_PATH);
            response.addCookie(cookie);
        }
    }

    /**
     * Delete BA cookie
     * 
     * @param response
     *            response
     */
    public void deleteBACookie(DynamoHttpServletResponse response) {
        Cookie cookie = new Cookie(BA_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath(COOKIE_PATH);
        response.addCookie(cookie);
    }

    /**
     * Restore geo cookie from BA cookie
     * 
     * @param response
     *            response
     */
    public String restoreCookie(DynamoHttpServletRequest request, DynamoHttpServletResponse response) {
        String baCookieStoreId = getBACookie(request);
        if (baCookieStoreId!=null) {
            createBACookie(response, baCookieStoreId);
            deleteBACookie(response);
        }
        return baCookieStoreId;
    }

    /**
     * Returns ATG store id.
     * 
     * @param castStoreId
     *            Castorama store id
     * 
     * @return ATG store id
     */
    public String getAtgStoreId(String castStoreId) {
        RepositoryItem store = getStoreTools().getStoreByCastStoreId(castStoreId);
        if (store != null)
            return (String) store.getPropertyValue(ATG_STORE_ID_PROP);
        return null;
    }

    /**
     * Returns catalogTools property.
     * 
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return catalogTools;
    }

    /**
     * Sets the value of the catalogTools property.
     * 
     * @param tools
     *            parameter to set.
     */
    public void setCatalogTools(CatalogTools tools) {
        catalogTools = tools;
    }

    /**
     * @return the storeTools
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * @param storeTools
     *            the storeTools to set
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }

    /**
     * @return the orderTools
     */
    public CastOrderTools getOrderTools() {
        return orderTools;
    }

    /**
     * @param orderTools the orderTools to set
     */
    public void setOrderTools(CastOrderTools orderTools) {
        this.orderTools = orderTools;
    }

    /**
     * @return the newsletterRepository
     */
    public Repository getNewsletterRepository() {
        return newsletterRepository;
    }

    /**
     * @param newsletterRepository
     *            the newsletterRepository to set
     */
    public void setNewsletterRepository(Repository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    /**
     * @return the profileRepository
     */
    public Repository getProfileRepository() {
        return profileRepository;
    }

    /**
     * @param profileRepository
     *            the profileRepository to set
     */
    public void setProfileRepository(Repository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * @return the productCatalog
     */
    public Repository getProductCatalog() {
        return productCatalog;
    }
    
    /**
     * @param productCatalog the productCatalog to set
     */
    public void setProductCatalog(Repository productCatalog) {
        this.productCatalog = productCatalog;
    }

    /**
     * @return the transactionManager
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * @param transactionManager
     *            the transactionManager to set
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @return the cookieName
     */
    public String getCookieName() {
        return COOKIE_NAME;
    }

    /**
     * @return the cookieName
     */
    public String getBACookieName() {
        return BA_COOKIE_NAME;
    }

    /**
     * Returns cookie age (in seconds).
     * 
     * @return cookie age
     */
    public int getCookieAge() {
        RepositoryItem castCatalog = ((CastCatalogTools) getCatalogTools()).getCastoramaCatalog();
        if (castCatalog != null) {
            return 60 * 60 * 24 * (Integer) castCatalog.getPropertyValue(COOKIE_AGE);
        }
        return 0;
    }

    /**
     * @return the cookiePath
     */
    public String getCookiePath() {
        return COOKIE_PATH;
    }

    /**
     * @return the webContextStoreId
     */
    public String getWebContextStoreId() {
        return WEB_CONTEXT_STORE_ID;
    }
}
