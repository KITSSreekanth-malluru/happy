package com.castorama.servlet;

import static com.castorama.commerce.profile.Constants.CURRENT_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LOCAL_PRICE_LIST;
import static com.castorama.commerce.profile.Constants.WRAPPED_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_POPUP_SESSION_PARAM;
import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_SESSION_PARAM;
import static com.castorama.utils.ContextTools.WEB_CONTEXT_STORE_ID;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.castorama.commerce.catalog.CastCatalogTools;
import com.castorama.utils.ContextState;
import com.castorama.utils.ContextTools;
import com.castorama.utils.StoreTools;

/**
 * This class required for setting local price lists for user.
 * 
 * @author EPAM team
 */
public class CastoramaContextServlet extends InsertableServletImpl {
    /** Sale price list profile property name. */
    private static final String SALE_PRICE_LIST = "salePriceList";

    /** Price list property name. */
    private static final String PRICE_LIST = "priceList";

    /** Catalog profile property name. */
    private static final String CATALOG = "catalog";

    /** Dynamo internal mime type. */
    private static final String DYNAMO_INTERNAL = "dynamo-internal";

    /** PRODUCT_DELIVERY_PAGE constant */
    private static final String PRODUCT_DELIVERY_PAGE = "/store/castCatalog/includes/productDelivery.jsp";

    /** EMAIL_TEMPLATE_PAGE constant */
    private static final String EMAIL_TEMPLATE_PAGE = "/store/emailTemplates";

    /** CHECK_STOCK_LEVEL_PAGE constant */
    private static final String CHECK_STOCK_LEVEL_PAGE = "/store/castCatalog/includes/checkStockLevel.jsp";
    
    /** SOFINCO_XML_PAGE constant */
    private static final String SOFINCO_XML_PAGE = "/store/castCatalog/sofincoXML.jsp";

    /** CONTEXT_STATE_PARAM constant */
    private static final String CONTEXT_STATE_PARAM = "contextState";

    /** Catalog tools. */
    private CatalogTools mCatalogTools;

    /** Pricelist manager. */
    private PriceListManager mPriceListManager;

    /** mTransactionManager property */
    private TransactionManager mTransactionManager;

    /** Store tools. */
    private StoreTools storeTools;
    
    /** Context tools. */
    private ContextTools contextTools;

    /**
     * Create cookies with chosen store id and sets price lists for user.
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws IOException exception
     * @throws ServletException exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException,
            ServletException {
        if (!excludeRequest(pRequest)) {
            try {
                ContextState cs = getContextTools().getContextState(pRequest, pResponse);
                setShoppingContext(pRequest, pResponse, cs);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        passRequest(pRequest, pResponse);
    }

    /**
     * Sets the value of the shoppingContext property.
     * 
     * @param pRequest parameter to set.
     * @param pResponse parameter to set.
     * @param cs parameter to set.
     * 
     * @throws IOException - exception.
     * @throws ServletException - exception.
     * @throws RepositoryException - exception.
     */
    public void setShoppingContext(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
            ContextState cs) throws IOException, ServletException {
        TransactionDemarcation td = new TransactionDemarcation();
        try {
            td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
            Profile pProfile = cs.getProfile();
            
            if (hasProperty(CATALOG, pProfile)) {
                RepositoryItem catalog = (RepositoryItem) pProfile.getPropertyValue(CATALOG);
                if (catalog == null) {
                    RepositoryItem castCatalog = ((CastCatalogTools) getCatalogTools()).getCastoramaCatalog();
                    pProfile.setPropertyValue(CATALOG, castCatalog);
                }
            }
            
            if (hasProperty(CURRENT_LOCAL_STORE_PROFILE_PROP, pProfile)) {
                // Bonnes Affaires popup logic
                String uri = pRequest.getRequestURIWithQueryString();
                if (cs.isPrevBonnesAffaires() && isAddToCartUri(uri)) {
                    // If user has a basket in store context and added a BA item to his basket, system displays bonnes affaires popup
                    if (cs.wasChanged() && cs.isCartNotEmpty()) {
                        pRequest.getSession().setAttribute(BONNES_AFFAIRES_POPUP_SESSION_PARAM, true);
                        pRequest.getSession().setAttribute(BONNES_AFFAIRES_SESSION_PARAM, true);
                        return;
                    }
                    
                    // If user hasn't basket in store context and added a BA item to the basket, save current local context
                    getContextTools().createBACookie(pResponse, cs.getWrappedContext());
                    // and user is switched to "real" web context.
                    cs.setNewContext(WEB_CONTEXT_STORE_ID);
                }
                
                // Change web context to local store context if BA cookie exist if finalize checkout or erase basket
                if (uri.contains("/store/checkout/full/confirmation.jsp") 
                        || ((uri.contains("/store/checkout/preshopping.jsp") || uri.contains("/store/checkout/cart.jsp")) && !cs.isCartNotEmpty())) {
                    String newContext = getContextTools().restoreCookie(pRequest,pResponse);
                    if (newContext!=null) {
                        cs.setCurrentContext(newContext);
                        cs.setNewContext(newContext);
                    }
                }
                
                boolean isXMLPrices=false;
                
                if(uri.contains("store/xmlPrices.jsp")){
                    isXMLPrices=true;
                }
                
                // Sets current local store (responsible for c&c, localPrices, product lifecycle etc.)
                RepositoryItem currentLocalStore = getStoreTools().getStore(cs.getActualContext());
                if (currentLocalStore == null) {
                    // If invalid store was set in cookie (e.g. 9999999) need to set web context
                    currentLocalStore = getStoreTools().getStore(WEB_CONTEXT_STORE_ID);
                    cs.setNewContext(WEB_CONTEXT_STORE_ID);
                }
                pProfile.setPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP, currentLocalStore);
                
                // Sets store for displaying in header (Bonnes Affaires case)
                RepositoryItem wrappedCurrentLocalStore = getStoreTools().getStore(cs.getWrappedContext());
                pProfile.setPropertyValue(WRAPPED_LOCAL_STORE_PROFILE_PROP, wrappedCurrentLocalStore);
                
                // Updates orders magasinId in case of they differ than current local store (except Bonnes Affaires)
                if (!cs.isBonnesAffaires())
                    getContextTools().updateShoppingCart(cs, currentLocalStore.getRepositoryId());
                
                // Rules for updating user profile and cookies
                if (cs.isForceChangeContext()) {
                    // Changes context immediately with cleaning of basket according to cdao, leadformance(case 3.1) or user choice on erase basket popup
                    if (!cs.getForceContext().equals(cs.getCurrentContext()) && cs.isChangeLocalContext()) {
                        getContextTools().clearBasket(cs);
                    }
                    if (!isXMLPrices){
                    getContextTools().updateContext(pResponse, pProfile.getRepositoryId(), cs.getForceContext());
                    }
                } else if (cs.isDisplayBonnesAffairesPopup()) {
                    // Do nothing before user make choice
                } else if (cs.isDisplayEraseBasketPopup()) {
                    // Do nothing before user make choice
                } else if (!cs.isBonnesAffaires()){
                    // Changes context in all other cases (cookies, leadformance, profile etc.)
                    if (cs.wasCookieCleared() && cs.isChangeLocalContext()) {
                        // Clear basket in case of user clear only s_cdao cookie
                        getContextTools().clearBasket(cs); 
                    }
                      
                    if (!isXMLPrices){
                    getContextTools().updateContext(pResponse, pProfile.getRepositoryId(), cs.getNewContext());
                   }
                }
                
                // Send contextState on page to display erase basket popup or Bonnes Affaires popup if needed
                pRequest.setParameter(CONTEXT_STATE_PARAM, cs);
            }
            
            if (hasProperty(PRICE_LIST, pProfile)) {
                RepositoryItem priceList = (RepositoryItem) pProfile.getPropertyValue(PRICE_LIST);
                if (priceList == null) {
                    priceList = getPriceListManager().getDefaultPriceList();
                    pProfile.setPropertyValue(PRICE_LIST, priceList);
                }
            }
            
            if (hasProperty(SALE_PRICE_LIST, pProfile)) {
                RepositoryItem salePriceList = (RepositoryItem) pProfile.getPropertyValue(SALE_PRICE_LIST);
                if (salePriceList == null) {
                    salePriceList = getPriceListManager().getDefaultSalePriceList();
                    pProfile.setPropertyValue(SALE_PRICE_LIST, salePriceList);
                }
            }
            RepositoryItem store = (RepositoryItem) pProfile.getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
            if (hasProperty(LOCAL_PRICE_LIST, pProfile)) {
                if (store != null) {
                    RepositoryItem localPriceList = (RepositoryItem) store.getPropertyValue(LOCAL_PRICE_LIST);
                    if (localPriceList != null) {
                        pProfile.setPropertyValue(LOCAL_PRICE_LIST, localPriceList);
                    }
                } else {
                    pProfile.setPropertyValue(LOCAL_PRICE_LIST, null);
                }
            }
        } catch (Exception e) {
            try {
                getTransactionManager().getTransaction().setRollbackOnly();
            } catch (javax.transaction.SystemException se) {
                if (isLoggingError()) {
                    logError(se);
                }
            }
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
    }

    /**
     * Checks if profile has given property
     * 
     * @param pPropertyName - property name
     * @param pProfile - profile
     * 
     * @return true if profile has given property
     * 
     * @throws RepositoryException if repository exception occur
     */
    protected boolean hasProperty(String pPropertyName, Profile pProfile) throws RepositoryException {
        return pProfile.getItemDescriptor().hasProperty(pPropertyName);
    }

    /**
     * Checks if need to exclude this request from processing
     * 
     * @param pRequest
     * @return true if need to exclude. otherwise false.
     */
    private boolean excludeRequest(DynamoHttpServletRequest pRequest) {
        String mimeType = pRequest.getMimeType();
        
        if (isLoggingDebug()) {
            logDebug("Checking Mime Type " + mimeType);
        }
        
        String uri = pRequest.getRequestURI();
        return !mimeType.startsWith(DYNAMO_INTERNAL)
                // These 3 lines are needed to correct work of Bonnes Affaires logic
                || uri.equals(PRODUCT_DELIVERY_PAGE)
                || uri.equals(CHECK_STOCK_LEVEL_PAGE)
                || uri.equals(SOFINCO_XML_PAGE)
                // E-mail is sent to a separate thread and its request hasn't cookie -> context sets to web context
                || uri.contains(EMAIL_TEMPLATE_PAGE)
                // Filter requests on error pages
                || uri.contains("/store/global/404.jsp")
                // Filter requests on images, styles and js
                || uri.contains("/store/images")
                || uri.contains("/store/castCatalog/images")
                || uri.contains("/store/styles")
                || uri.contains("/store/js")
                //StoreLocator implementation. StoreList and autoComplete
                || uri.contains("/store/magasin/storeInfoList.jsp")
                || uri.contains("/store/castCommon/includes/CityOrPostalAutocomplete.jsp");
    }

    /**
     * Checks if request went from add to cart action
     * 
     * @param uri
     * @return true if it's uri of adding item to cart
     */
    private boolean isAddToCartUri(String uri) {
        return uri.contains("addToCartSmall.jsp") 
                || uri.contains("addToCartArea.jsp");
    }

    /**
     * Returns catalogTools property.
     * 
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Returns priceListManager property.
     * 
     * @return priceListManager property.
     */
    public PriceListManager getPriceListManager() {
        return mPriceListManager;
    }

    /**
     * Sets the value of the catalogTools property.
     * 
     * @param tools parameter to set.
     */
    public void setCatalogTools(CatalogTools tools) {
        mCatalogTools = tools;
    }

    /**
     * Sets the value of the priceListManager property.
     * 
     * @param manager parameter to set.
     */
    public void setPriceListManager(PriceListManager manager) {
        mPriceListManager = manager;
    }

    /**
     * Returns transactionManager property.
     * 
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     * 
     * @param pTransactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns storeTools property.
     * 
     * @return storeTools property.
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * Sets the value of the storeTools property.
     * 
     * @param storeTools parameter to set.
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }
    
    /**
     * Returns contextTools property.
     * 
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return contextTools;
    }

    /**
     * Sets the value of the contextTools property.
     * 
     * @param contextTools the contextTools to set
     */
    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }
}
