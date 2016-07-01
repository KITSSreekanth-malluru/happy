package com.castorama.checkout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.promotion.PromotionTools;

import atg.commerce.util.NoLockNameException;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;

import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.RollbackFunctionalityConfiguration;

import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.profile.SessionBean;

/**
 * CastShoppingCartFormHandler class contains inherits base functionality from
 * CartModifierFormHandler and adds Castorama specific functionality.
 *
 * @author EPAM team
 */
public class CastShoppingCartFormHandler extends CartModifierFormHandler {
    /** CASTO_GROUPED_PRODUCT constant */
    public static final String CASTO_GROUPED_PRODUCT = "casto-grouped-product";

    /** MSG_REMOVE_ILLEGAL_ITEMS constant */
    public static final String MSG_REMOVE_ILLEGAL_ITEMS = "msgRemoveIllegalItems";

    /** PRICE_LIST_EXCEPTION constant */
    static final String PRICE_LIST_EXCEPTION = "PriceListException";

    /** PRICE_LIST constant */
    public static final String PRICE_LIST = "priceList";

    /** preshopping.jsp constant */
    private static final String PRESHOPPING_JSP = "preshopping.jsp";

    /** Default continue redirect URL */
    private static final String DEFAULT_CONTINUE_URL = "/store/";

    /** The AcceptSalesConditions property */
    private boolean mAcceptSalesConditions;

    /** productType property */
    private String mProductType;

    /** acceptCarteLAtout property */
    private boolean mAcceptCarteLAtout;

    /** lAtoutChangedOnPage property */
    private boolean mLAtoutChangedOnPage;

    /** promotionTools constant. */
    PromotionTools mPromotionTools;

    /** itemToRecalculate property */
    private String mItemToRecalculate;

    /** Current session. */
    private SessionBean mSessionBean;

    /** mRollbackFunctionalityConfiguration property. */
    private RollbackFunctionalityConfiguration mRollbackFunctionalityConfiguration;

    /** clientLockManager property */
    protected ClientLockManager mClientLockManager;

    /** Continue redirect URL */
    private String mContinueURL;

    /**
     * Creates a new CastShoppingCartFormHandler object.
     */
    public CastShoppingCartFormHandler() {
    }

    /**
     * Returns productType property.
     *
     * @return productType property.
     */
    public String getProductType() {
        return mProductType;
    }

    /**
     * Sets the value of the productType property.
     *
     * @param pProductType parameter to set.
     */
    public void setProductType(String pProductType) {
        mProductType = pProductType;
    }

    /**
     * Returns acceptSalesConditions property.
     *
     * @return acceptSalesConditions property.
     */
    public boolean getAcceptSalesConditions() {
        return mAcceptSalesConditions;
    }

    /**
     * Sets the value of the acceptSalesConditions property.
     *
     * @param pAcceptSalesConditions parameter to set.
     */
    public void setAcceptSalesConditions(boolean pAcceptSalesConditions) {
        mAcceptSalesConditions = pAcceptSalesConditions;
    }

    /**
     * Returns rollbackFunctionalityConfiguration property.
     *
     * @return rollbackFunctionalityConfiguration property.
     */
    public RollbackFunctionalityConfiguration getRollbackFunctionalityConfiguration() {
        return mRollbackFunctionalityConfiguration;
    }

    /**
     * Sets the value of the rollbackFunctionalityConfiguration property.
     *
     * @param pRollbackFunctionalityConfiguration parameter to set.
     */
    public void setRollbackFunctionalityConfiguration(RollbackFunctionalityConfiguration pRollbackFunctionalityConfiguration) {
        mRollbackFunctionalityConfiguration = pRollbackFunctionalityConfiguration;
    }

    /**
     * Returns clientLockManager property.
     *
     * @return clientLockManager property.
     */
    public ClientLockManager getClientLockManager() {
        return mClientLockManager;
    }

    /**
     * Sets the value of the clientLockManager property.
     *
     * @param pClientLockManager parameter to set.
     */
    public void setClientLockManager(ClientLockManager pClientLockManager) {
        mClientLockManager = pClientLockManager;
    }

    /**
     * Returns acceptCarteLAtout property.
     *
     * @return acceptCarteLAtout property.
     */
    public boolean getAcceptCarteLAtout() {
        if(!getLAtoutChangedOnPage()){
            Order order = getOrder();
            if (order instanceof CastOrderImpl) {
                CastOrderImpl castOrder = (CastOrderImpl) order;
                boolean orderAtout = castOrder.getPayeCarteAtout();
                if(orderAtout){
                    mAcceptCarteLAtout = true;
                }
            }
        }
        return mAcceptCarteLAtout;
    }

    /**
     * Sets the value of the acceptCarteLAtout property.
     *
     * @param pAcceptCarteLAtout parameter to set.
     */
    public void setAcceptCarteLAtout(boolean pAcceptCarteLAtout) {
        mAcceptCarteLAtout = pAcceptCarteLAtout;
    }

    /**
     * Returns lAtoutInitialized property.
     *
     * @return lAtoutInitialized property.
     */
    public boolean getLAtoutChangedOnPage() {
        return mLAtoutChangedOnPage;
    }

    /**
     * Sets the value of the lAtoutInitialized property.
     *
     * @param pLAtoutInitialized parameter to set.
     */
    public void setLAtoutChangedOnPage(boolean pLAtoutChangedOnPage) {
        mLAtoutChangedOnPage = pLAtoutChangedOnPage;
    }

    int getSavedAmountForOrder(Order pOrder) {
        if (pOrder== null) throw new NullPointerException("Order can't be empty");
        final double total = pOrder.getPriceInfo().getTotal();

        if((total >= 450) && (total < 1150)) {
            return (int) Math.round(total / 100) * 5;
        } else if (total >= 1150) {
            return (int) Math.round(total / 100) * 10;
        } else return 0;
    }

    int getNumberOfHundredsForOrder(Order pOrder) {
        if (pOrder== null) throw new NullPointerException("Order can't be empty");
        final double total = pOrder.getPriceInfo().getTotal();

        if(total >= 450) {
            return (int) Math.round(total / 100) * 100;
        } else return 0;
    }

    public int getSavedAmount() {
        return getSavedAmountForOrder(getOrder());
    }

    public int getNumberOfHundreds() {
        return getNumberOfHundredsForOrder(getOrder());
    }

    /**
     * Set the PromotionTools property.
     *
     * @param pPromotionTools
     */
    public void setPromotionTools(PromotionTools pPromotionTools) {
        mPromotionTools = pPromotionTools;
    }

    /**
     * Return the PromotionTools property.
     *
     * @return The PromotionTools component
     */
    public PromotionTools getPromotionTools() {
        return mPromotionTools;
    }

    /**
     * Returns itemToRecalculate property.
     *
     * @return itemToRecalculate property.
     */
    public String isItemToRecalculate() {
        return mItemToRecalculate;
    }

    /**
     * Sets the value of the itemToRecalculate property.
     *
     * @param pItemToRecalculate parameter to set.
     */
    public void setItemToRecalculate(String pItemToRecalculate) {
        this.mItemToRecalculate = pItemToRecalculate;
    }

    /**
     * Sets the value of the deleteItem property.
     *
     * @param itemToDelteId parameter to set.
     */
    public void setDeleteItem(String itemToDelteId) {
        try {
            Integer.parseInt(itemToDelteId.substring(2));
            setRemovalRelationshipIds(new String[] {itemToDelteId,});
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Exception when value of the deleteItem property is set for itemToDeleteId = " + itemToDelteId + ". \n" + e.getStackTrace());
            }
        }
    }

    public void preSetOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        checkOrdersAtoutProperty();
    }

    public void preMoveToPurchaseInfo(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        checkOrdersAtoutProperty();
    }

    protected void checkOrdersAtoutProperty() {
        Order order = getOrder();
        if (order instanceof CastOrderImpl) {
            CastOrderImpl castOrder = (CastOrderImpl) order;
            boolean userAtout = getAcceptCarteLAtout();
            boolean orderAtout = castOrder.getPayeCarteAtout();
            if (userAtout != orderAtout) {
                castOrder.setPayeCarteAtout(userAtout);
                try {
                    getOrderManager().updateOrder(castOrder);
                    setOrder(castOrder);
                } catch (CommerceException e) {
                    if (isLoggingError()) {
                        logError("Atout property wasn't set to order correctly. \nOrder:"+castOrder+"\n" + e);
                    }
                }
            }
        }
    }

    /**
     * This method is called when the page is loaded. It checks if product
     * changes status from sold online to sold only in store or product price
     * becomes unavailable, it removes product from shopping cart.
     */
    public boolean handleInitForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException, CommerceException, RunProcessException {
        CastClaimableManager manager = (CastClaimableManager) getClaimableManager();
        Order order = getOrder();
        if (order != null) {

            boolean reprice = false;
            MutableRepositoryItem profile = (MutableRepositoryItem) getProfile();
            List<RepositoryItem> promotions = manager.getPromotions(order);

            List<RepositoryItem> deletedPromotions = new ArrayList<RepositoryItem>();
            for (Iterator<RepositoryItem> it = promotions.iterator();it.hasNext();){
                RepositoryItem promotion = it.next();
                if (/*couponPromotionIds.contains(promotion.getRepositoryId())&& */manager.isPromotionNotCompatible(promotion, promotions)){
                    deletedPromotions.add(promotion);
                    getPromotionTools().revokePromotion(profile, promotion, false);
                    reprice = true;
                }
            }
            getPromotionTools().initializePricingModels(pRequest, pResponse);

            synchronized (order) {
                boolean[] isRemovedAndNotEnough = ((CastOrderTools) getOrderManager().getOrderTools()).removeCommerceItemIds(order);

                if (reprice || isRemovedAndNotEnough[0] || isRemovedAndNotEnough[1]) {
                    Map extraParams = createRepriceParameterMap();
                    runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(), getUserLocale(),
                            getProfile(), extraParams);
                    getPromotionTools().addAllPromotions(profile, deletedPromotions);
                    if (isRemovedAndNotEnough[0] || isRemovedAndNotEnough[1]) {
                        if (isRemovedAndNotEnough[0]) {
                            String msg = formatUserMessage(MSG_REMOVE_ILLEGAL_ITEMS, pRequest, pResponse);
                            addFormException(new DropletException(msg, null, MSG_REMOVE_ILLEGAL_ITEMS));
                            pRequest.setAttribute(MSG_REMOVE_ILLEGAL_ITEMS, MSG_REMOVE_ILLEGAL_ITEMS);

                        }
                        if (isRemovedAndNotEnough[1]) {
                            addFormException(new DropletException(null));
                        }

                        getPromotionTools().initializePricingModels(pRequest, pResponse);
                    }
                }
            }
        }
        return checkFormRedirect (getSetOrderByRelationshipIdSuccessURL(),
                getSetOrderByRelationshipIdErrorURL(),
                pRequest, pResponse);
    }

    /**
     * Deletes given item from Order
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public boolean handleDeleteItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        return handleSetOrderByRelationshipId(pRequest, pResponse);
    }

    /**
     * Override to maintain Castorama functionality.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException    if an error occurs
     * @throws IOException         if an error occurs
     * @throws CommerceException   if an error occurs
     * @throws RunProcessException if an error occurs
     */
    @Override protected void modifyOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                                         DynamoHttpServletResponse pResponse)
            throws ServletException, IOException, CommerceException,
            RunProcessException {
        super.modifyOrderByRelationshipId(pRequest, pResponse);
        Order order = getOrder();
        if (order != null) {
            synchronized (order) {
                boolean[] isRemovedAndNotEnough = ((CastOrderTools) getOrderManager().getOrderTools()).removeCommerceItemIds(order);

                if (isRemovedAndNotEnough[0] || isRemovedAndNotEnough[1]) {
                    Map extraParams = createRepriceParameterMap();
                    runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(), getUserLocale(),
                            getProfile(), extraParams);
                    if (isRemovedAndNotEnough[0]) {
                        String msg = formatUserMessage(MSG_REMOVE_ILLEGAL_ITEMS, pRequest, pResponse);
                        addFormException(new DropletException(msg, null, MSG_REMOVE_ILLEGAL_ITEMS));
                    }
                    if (isRemovedAndNotEnough[1]) {
                        addFormException(new DropletException(null));
                    }
                }
            }
        }

    }

    /**
     * Override to maintain Castorama functionality.
     *
     * @param  request  DynamoHttpServletRequest object
     * @param  response DynamoHttpServletResponse object
     *
     * @return
     *
     * @throws ServletException if an error occurs
     * @throws IOException      if an error occurs
     */
    @Override public boolean handleAddItemToOrder(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws ServletException, IOException {

        Order order = getOrder();

        List<RepositoryItem> promotions = new ArrayList<RepositoryItem>();
        getPromotionTools().getOrderPromotions(order, promotions);

        if ( null != order ) {
            OrderManager orderManager = getOrderManager();
            if (orderManager instanceof CastOrderManager) {
                CastOrderManager com = (CastOrderManager) orderManager;
                String orderId = order.getId();
                if (com.checkCompleteOrderStatus(orderId)) {
                    CastOrderHolder orderHolder = (CastOrderHolder) getShoppingCart();
                    Order current = orderHolder.getCurrent();
                    orderHolder.setCurrent(null);
                    if (com.checkSubmittedOrderStatus(orderId) ) {
                        orderHolder.setLast(current);
                    }
                }
                //com.removeOrder(orderId, true);
            }
        }
        checkProductId(request);
        checkQuantity(request);
        checkCatalogRefIds(request);
        return super.handleAddItemToOrder(request, response);
    }

    /**
     * Checks the "productId" parameter in the DynamoHttpServletRequest request
     *
     * @param request DynamoHttpServletRequest request
     */
    private void checkProductId(DynamoHttpServletRequest request) {
        String productId = getProductId();
        if ((null == productId) || (0 == productId.length())) {
            productId = request.getParameter("productId");
            if ((null != productId) && (0 < productId.length())) {
                setProductId(productId);
            }
        }
    }

    /**
     * Checks the "quantity" parameter in the DynamoHttpServletRequest request
     *
     * @param request DynamoHttpServletRequest request
     */
    private void checkQuantity(DynamoHttpServletRequest request) {
        long quantity = getQuantity();
        if (0 == quantity) {
            try {
                quantity = Long.parseLong(request.getParameter("quantity"));
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError("Wrong quantity parameter. \n" + e);
                }
            }
            if (0 < quantity) {
                setQuantity(quantity);
            }
        }
    }

    /**
     * Checks the "catalogRefIds" parameter in the DynamoHttpServletRequest
     * request
     *
     * @param request DynamoHttpServletRequest request
     */
    private void checkCatalogRefIds(DynamoHttpServletRequest request) {
        String[] catalogRefIds = getCatalogRefIds();
        if ((null == catalogRefIds) || (0 == catalogRefIds.length)) {
            String catalogRefId = request.getParameter("catalogRefIds");
            if ((null != catalogRefId) && (0 < catalogRefId.length())) {
                setCatalogRefIds(new String[] {catalogRefId});
            }
        }
    }

    /**
     * Override to maintain Castorama functionality.
     *
     * @param     request  DynamoHttpServletRequest object
     * @param     response DynamoHttpServletResponse object
     *
     * @return
     *
     * @exception ServletException if an error occurs
     * @exception IOException      if an error occurs
     */
    @Override public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest request,
                                                       DynamoHttpServletResponse response) throws IOException,
            ServletException {
        checkRemovalCommerceIds(request);
        return super.handleRemoveItemFromOrder(request, response);
    }

    /**
     * Checks the "catalogRefIds" parameter in the DynamoHttpServletRequest
     * request and adds it to the removalCommerceIds property
     *
     * @param request DynamoHttpServletRequest request
     */
    protected void checkRemovalCommerceIds(DynamoHttpServletRequest request) {
        String[] removalCommerceIds = getRemovalCommerceIds();
        if ((null == removalCommerceIds) || (0 == removalCommerceIds.length)) {
            String removalCommerceId = request.getParameter("removalCommerceIds");
            if ((null != removalCommerceId) && (0 < removalCommerceId.length())) {
                setRemovalCommerceIds(new String[] {removalCommerceId});
            }
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pId  parameter
     * @param  pIds parameter
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected final boolean haveId(String pId, String[] pIds) {
        if ((pIds != null) && (pId != null)) {
            int length = pIds.length;
            for (int c = 0; c < length; c++) {
                if (pId.equals(pIds[c])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Called before any work is done by the handleAddMultipleItemsToOrder
     * method. It currently does nothing.
     *
     * @param     pRequest  DynamoHttpServletRequest object
     * @param     pResponse DynamoHttpServletResponse object
     *
     * @exception ServletException if an error occurs
     * @exception IOException      if an error occurs
     */
    public void preAddMultipleItemsToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        if ((getQuantity() > 0) && !StringUtils.isBlank(getProductType()) &&
                CASTO_GROUPED_PRODUCT.equalsIgnoreCase(getProductType())) {
            AddCommerceItemInfo[] items = getItems();
            for (AddCommerceItemInfo item : items) {
                item.setQuantity(item.getQuantity() * getQuantity());
            }
        }
    }

    public void preRemoveItemFromOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        SessionBean sb = getSessionBean();
        if(sb != null) {
            List<String> productsDeleted = (List<String>)sb.getValues().get("productsDeleted");

            if(productsDeleted == null) {
                productsDeleted = new ArrayList();
            }
            String[] removalCommerceIds = getRemovalCommerceIds();
            if (removalCommerceIds == null) return;
            for (String commerceId : removalCommerceIds) {
                // get the CommerceItem
                try {
                    CommerceItem item = getOrder().getCommerceItem(commerceId);
                    RepositoryItem productRef = (RepositoryItem) item.getAuxiliaryData().getProductRef();
                    productsDeleted.add(0, productRef.getRepositoryId());
                } catch (CommerceItemNotFoundException e) {
                    if (isLoggingError()) {
                        logError("Item wasn't removed from session bean: item not found. \n" + e);
                    }
                } catch (InvalidParameterException e) {
                    if (isLoggingError()) {
                        logError("Item wasn't removed from session bean: invalid parameter. \n" + e);
                    }
                }
            }
            sb.getValues().put("productsDeleted", productsDeleted);
        }
    }

    /**
     * Gets session.
     *
     * @return the sessionBean
     */
    public SessionBean getSessionBean() {
        return mSessionBean;
    }

    /**
     * Sets session.
     *
     * @param sessionBean the sessionBean to set
     */
    public void setSessionBean(SessionBean sessionBean) {
        this.mSessionBean = sessionBean;
    }

    /* (non-Javadoc)
     * @see atg.commerce.order.purchase.PurchaseProcessFormHandler#afterSet(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean afterSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        if ((profileItem != null) && (curShCart != null) && (curShCart.getCurrent() != null)) {
            Order curOrder = null;
            if (curShCart instanceof CastOrderHolder){
                curOrder = ((CastOrderHolder)curShCart).getCurrentlySelected();
            } else {
                curOrder = curShCart.getCurrent();
            }
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("CastShoppingCartFormHandler release lock : Profile ID : " + profileItem.getRepositoryId() +
                            "; Order ID : " + curOrder.getId());
                }
            }
        }
        return super.afterSet(pRequest, pResponse);
    }

    /* (non-Javadoc)
     * @see atg.commerce.order.purchase.PurchaseProcessFormHandler#beforeSet(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean beforeSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        if ((profileItem != null) && (curShCart != null) && (curShCart.getCurrent() != null)) {
            Order curOrder = null;
            if (curShCart instanceof CastOrderHolder){
                curOrder = ((CastOrderHolder)curShCart).getCurrentlySelected();
            } else {
                curOrder = curShCart.getCurrent();
            }
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("CastShoppingCartFormHandler get lock : Profile ID : " + profileItem.getRepositoryId() +
                            "; Order ID : " + curOrder.getId());
                }
            }
        }
        return super.beforeSet(pRequest, pResponse);
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *!
     *
     * @param  pRequest parameter
     *
     * @throws DeadlockException exception
     */
    protected void acquireTransactionLock(DynamoHttpServletRequest pRequest) throws DeadlockException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    RepositoryItem profileItem = getProfile();
                    if (profileItem != null) {
                        String profileId = profileItem.getRepositoryId();
                        pRequest.setAttribute(LOCK_NAME_ATTRIBUTE_NAME, profileId);
                        service.acquireTransactionLock(profileId);
                    } else {
                        service.acquireTransactionLock();
                    }
                }
            } catch (NoLockNameException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.acquireTransactionLock(pRequest);
        }  // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *
     *
     * @param  pRequest parameter
     *
     * @throws LockManagerException exception
     */
    protected void releaseTransactionLock(DynamoHttpServletRequest pRequest) throws LockManagerException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    String lockName = (String) pRequest.getAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    if (lockName != null) {
                        service.releaseTransactionLock(lockName);
                        pRequest.removeAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    } else {
                        service.releaseTransactionLock();
                    }
                }
            } catch (LockManagerException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.releaseTransactionLock(pRequest);
        }  // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *
     *
     * @return lockService property.
     */
    protected TransactionLockService getLockService() {
        TransactionLockFactory factory = getConfiguration().getTransactionLockFactory();
        TransactionLockService tls = null;
        if (factory != null) {
            tls = factory.getServiceInstance(this);
            RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
            if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
                tls.setClientLockManager(getClientLockManager());
            }
        }
        return tls;
    }

    /**
     * Redirects to continue URL
     *
     * @param pRequest servlet's request
     * @param pResponse servlet's response
     * @return redirect result
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleContinue(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        if (getContinueURL() == null || getContinueURL().length() == 0 || getContinueURL().contains(PRESHOPPING_JSP)) {
            setContinueURL(DEFAULT_CONTINUE_URL);
        }
        return checkFormRedirect(getContinueURL(), getContinueURL(), pRequest, pResponse);
    }

    /**
     * Returns continue redirect URL
     *
     * @return continue redirect URL
     */
    public String getContinueURL() {
        return mContinueURL;
    }

    /**
     * Sets continue redirect URL
     *
     * @param pContinueURL continue redirect URL to set
     */
    public void setContinueURL(String pContinueURL) {
        this.mContinueURL = pContinueURL;
    }
}  // end of class