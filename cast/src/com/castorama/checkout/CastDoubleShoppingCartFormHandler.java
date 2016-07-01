package com.castorama.checkout;

import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_POPUP_SESSION_PARAM;
import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_PRODUCT_SESSION_PARAM;
import static com.castorama.utils.ContextTools.BONNES_AFFAIRES_SESSION_PARAM;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.utils.ContextState;
import com.castorama.utils.ContextTools;

/**
 * CastShoppingCartFormHandler class contains inherits base functionality from
 * CartModifierFormHandler and adds Castorama specific functionality.
 * 
 * @author EPAM TEAM
 */
public class CastDoubleShoppingCartFormHandler extends CastShoppingCartFormHandler {
    /**
     * 
     */
    private static final int ORDER_TYPE_LOCAL = 2;

    /** Web Active property. */
    private boolean mWebActive = true;

    /** contextTools property */
    protected ContextTools contextTools;

    /** set WebStockToAdd property */
    private long mWebStockToAdd;

    /** set LocalStockToAdd property */
    private long mLocalStockToAdd ;
    
    /** Add to both orders  property.
     *  0 - not add to anything
     *  1 - add to web order
     *  2 - add to local order
     *  another - add to both orders
     */
    private int mAddToBoth = 0;

    String[] mRemovalCatalogRefIds;

    /**
     * Sets property RemovalCatalogRefIds
     * 
     * @param pRemovalCatalogRefIds a <code>String[]</code> value
     */
    public void setRemovalCatalogRefIds(String[] pRemovalCatalogRefIds) {
        mRemovalCatalogRefIds = pRemovalCatalogRefIds;
    }

    /**
     * Returns property RemovalCatalogRefIds
     * 
     * @return a <code>String[]</code> value
     * @beaninfo description: The list of catalog ref ids used during item
     *           removal
     * 
     */
    public String[] getRemovalCatalogRefIds() {
        return mRemovalCatalogRefIds;
    }

    String[] mRemovalLocalCommerceIds;

    /**
     * Sets property RemovalLocalCommerceIds
     * 
     * @param pRemovalLocalCommerceIds a <code>String[]</code> value
     */
    public void setRemovalLocalCommerceIds(String[] pRemovalLocalCommerceIds) {
        mRemovalLocalCommerceIds = pRemovalLocalCommerceIds;
    }

    /**
     * Returns property RemovalLocalCommerceIds
     * 
     * @return a <code>String[]</code> value
     * @beaninfo description: The list of commerce ids used during item removal
     * 
     */
    public String[] getRemovalLocalCommerceIds() {
        return mRemovalLocalCommerceIds;
    }

    /**
     * Creates a new CastShoppingCartFormHandler object.
     */
    public CastDoubleShoppingCartFormHandler() {
    }

    protected void checkOrdersAtoutProperty() {
        if (isWebActive()){
            try {
                super.checkOrdersAtoutProperty();
                switchActive();
                super.checkOrdersAtoutProperty();
            } finally {
                resetActiveOrder();
            }
        } else {
            super.checkOrdersAtoutProperty();
        }
    }

    /**
     * Create to maintain Castorama functionality.
     * 
     * @param pRequest DynamoHttpServletRequest object
     * @param pResponse DynamoHttpServletResponse object
     * 
     * @return
     * 
     * @throws ServletException if an error occurs
     * @throws IOException if an error occurs
     */
    public boolean handleAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        // Bonnes Affaires logic. Don't touch anything here, please!
        ContextState cs = getContextTools().getContextState(pRequest, pResponse);
        
        if (cs.getSavedBAProductId() != null) {
            // If user choose continuer on bonnes affaires popup, system should clear his basket
            getContextTools().clearBasket(cs);
            // To skip next "if" clause
            cs.setDisplayBonnesAffairesPopup(false);
            // To not display erase basket popup on preshopping page
            pRequest.getSession().setAttribute(BONNES_AFFAIRES_SESSION_PARAM, true);
            // To save previous context
            getContextTools().createBACookie(pResponse, cs.getWrappedContext());
            // To switch to real web context
            getContextTools().createSessionCookie(pResponse);
            // To not display bonnes affaires popup
            pRequest.getSession().removeAttribute(BONNES_AFFAIRES_POPUP_SESSION_PARAM);
            // To not do this logic next time at adding items to cart
            pRequest.getSession().removeAttribute(BONNES_AFFAIRES_PRODUCT_SESSION_PARAM);
        }
        
        /*
         *  If user has a basket in store context and added a BA item to his basket, system shouldn't added item to cart, 
         *  but remember chosen product, stay on this page and display BA popup.
        */
        if (cs.isDisplayBonnesAffairesPopup()) {
            pRequest.getSession().setAttribute(BONNES_AFFAIRES_PRODUCT_SESSION_PARAM, getProductId());
            addFormException(new DropletException("Need to display bonnes affaires popup"));
        }
        
        return super.handleAddItemToOrder(pRequest, pResponse);
    }

    /**
     * Add item to delivery to home and click and collect orders
     */
    protected void doAddItemsToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        if (isLoggingDebug())
            logDebug("Starting doAddItemsToOrder");

        if (!isAddToWeb() && !isAddToLocal()) return;
        try {
            Map extraParams = createRepriceParameterMap();
            AddCommerceItemInfo[] items = getItems();
            long [][] qties = {{0l,0l}};
            if (getShoppingCart() instanceof CastOrderHolder && items != null){
                qties = new long[items.length][2];
                for (int i = 0; i < items.length; i++) {
                    qties[i] = ((CastOrderHolder)getShoppingCart()).getSkuQuantities(items[i].getCatalogRefId());
                }
            }
            AddCommerceItemInfo[] addItems = items;
            long[] addItemsQuantities = new long[addItems.length];
            for (int i = 0; i < addItems.length; i++){
                addItemsQuantities[i] = addItems[i].getQuantity();
            }

            if (isAddToWeb()){
                Order order = getOrder();
                if (order == null) {
                    String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
                    throw new ServletException(msg);
                }
                setShippingGroup(null);
                getPurchaseProcessHelper().addItemsToOrder(order, getShippingGroup(), getProfile(), getItems(true, qties, addItemsQuantities),
                        getUserLocale(), getCatalogKey(pRequest, pResponse), getUserPricingModels(), this, extraParams);
            }
            if (isAddToLocal()){
                switchActive();
                Order orderLocal = getOrder();
                setShippingGroup(null);
                getPurchaseProcessHelper().addItemsToOrder(orderLocal, getShippingGroup(), getProfile(), getItems(false, qties, addItemsQuantities),
                        getUserLocale(), getCatalogKey(pRequest, pResponse), getUserPricingModels(), this, extraParams);
            }
            setAddToBoth(null);

        } catch (CommerceException ce) {
            processException(ce, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
        } finally {
            resetActiveOrder();
            setAddToBoth(null);
        }
    }

    protected void deleteItems(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        @SuppressWarnings("unused")
        String commerceId;
        Order order = getOrder();
        Map extraParams = createRepriceParameterMap();

        try {
            if (getRemovalCommerceIds() != null) {
                List deletedSkus = getPurchaseProcessHelper().deleteItems(order, getRemovalCommerceIds(),
                        getUserPricingModels(), getUserLocale(), getProfile(), this, extraParams);
            }
            switchActive();
            Order orderLocal = getOrder();
            synchronized (orderLocal) {
                if (getRemovalLocalCommerceIds() != null) {
                    List deletedLocalSkus = getPurchaseProcessHelper().deleteItems(orderLocal,
                            getRemovalLocalCommerceIds(), getUserPricingModels(), getUserLocale(), getProfile(), this,
                            extraParams);
                    setDeletedSkus(deletedLocalSkus);
                }
            }
        } catch (CommerceException ce) {
            processException(ce, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
        } finally {
            resetActiveOrder();
        }
    }

    public boolean isWebActive() {
        return mWebActive;
    }

    public void setWebActive(boolean pWebActive) {
        this.mWebActive = pWebActive;
    }

    public boolean isAddToWeb() {
        return (mAddToBoth != 2 && mAddToBoth != 0);
    }

    public boolean isAddToLocal() {
        return (mAddToBoth != 1 && mAddToBoth != 0);
    }
    
    public void setAddToBoth(String pAddToBoth) {
        pAddToBoth = pAddToBoth!=null?pAddToBoth.trim().toLowerCase():null;
        if (pAddToBoth == null) {
            this.mAddToBoth = 0;
        } else if ("web".equals(pAddToBoth)) {
            this.mAddToBoth = 1;
        } else if ("local".equals(pAddToBoth)) {
            this.mAddToBoth = 2;
        } else if ("both".equals(pAddToBoth)) {
            this.mAddToBoth = 3;
        } else {
            this.mAddToBoth = 0;
        }
    }

    public void switchActive() {
        this.mWebActive = false;
    }

    public void resetActiveOrder() {
        this.mWebActive = true;
    }

    Order mOrder;
    Order mOrderLocal;

    /**
     * Set the Order property.
     * 
     * @param pOrder an <code>Order</code> value
     */
    public void setOrder(Order pOrder) {
        if (isWebActive()) {
            mOrder = pOrder;
        } else {
            mOrderLocal = pOrder;
        }
    }

    /**
     * Return the Order property.
     * 
     * @return an <code>Order</code> value
     */
    public Order getOrder() {
        if (isWebActive()) {
            if (mOrder != null)
                return mOrder;
            else
                return getShoppingCart().getCurrent();
        } else {
            if (mOrderLocal != null)
                return mOrderLocal;
            else {
                OrderHolder oh = getShoppingCart();
                if (oh instanceof CastOrderHolder) {
                    return ((CastOrderHolder) oh).getCurrentLocal();
                } else {
                    return oh.getCurrent();
                }
            }
        }
    }

    public void setDeleteItem(String itemToDeleteId) {
        try {
            String[] commerceIds = itemToDeleteId.split(",");
            if (commerceIds[0] != null && !StringUtils.isEmpty(commerceIds[0])) {
                setRemovalCommerceIds(new String[] { commerceIds[0].trim(),});
            }
            if (commerceIds[1] != null && !StringUtils.isEmpty(commerceIds[1].trim())) {
                setRemovalLocalCommerceIds(new String[] { commerceIds[1].trim(),});
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Exception when value of the deleteItem property is set for itemToDeleteId = " + itemToDeleteId + ". \n" + e.getStackTrace());
            }
        }
    }

    public boolean handleDeleteItem(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        return handleRemoveItemFromOrder(pRequest, pResponse);
    }


    public int getLocalSavedAmount() {
        try {
            switchActive();
            return getSavedAmountForOrder(getOrder());
        } finally {
            resetActiveOrder();
        }

    }
    
    public int getLocalNumberOfHundreds() {
        try {
            switchActive();
            return getNumberOfHundredsForOrder(getOrder());
        } finally {
            resetActiveOrder();
        }

    }

    protected void modifyOrderByRelationshipId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException, CommerceException, RunProcessException {
        if (isWebActive()) {
            try {
                // recalculate web order
                super.modifyOrderByRelationshipId(pRequest, pResponse);
                // switch to local order
                switchActive();
                // recalculate local order !!!!!!!!!!!!!!!!!!!!!!!!!!
                super.modifyOrderByRelationshipId(pRequest, pResponse);
            } finally {
                // reset to web order
                resetActiveOrder();
            }
        } else {
            super.modifyOrderByRelationshipId(pRequest, pResponse);
        }
    }

    public void preSetOrderByRelationshipId(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        checkOrdersAtoutProperty();
    }

    public boolean handleMoveToPurchaseInfoByRelId(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        int selectedOrder = -1;
        if (getShoppingCart() instanceof CastOrderHolder){
            CastOrderHolder shoppingCart = (CastOrderHolder) getShoppingCart();
            selectedOrder = shoppingCart.getSelectedOrder();
            if (selectedOrder == ORDER_TYPE_LOCAL){
//                shoppingCart.switchOrders();
            } else {
                //switch to the web order   
                resetActiveOrder();
            }
        }
        boolean superResult = false;
        superResult = super.handleMoveToPurchaseInfoByRelId(pRequest, pResponse);
        if (selectedOrder == ORDER_TYPE_LOCAL){
            resetActiveOrder();
        }
        return superResult;
    }
    
    public boolean handleInitForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
        throws ServletException, IOException, CommerceException, RunProcessException {
//            resetActiveOrder();
//            Order order = getOrder();
//            if (order instanceof CastOrderImpl){
//                CastOrderImpl castOrder = (CastOrderImpl) order;
//                String deliveryType = castOrder.getDeliveryType();
//                CastOrderHolder shoppingCart = (CastOrderHolder) getShoppingCart();
//                if (deliveryType!= null && deliveryType.equals(CLICK_AND_COLLECT_DELIVERY_TYPE)){
//                    shoppingCart.switchOrders();
//                } else {
//                    CastOrderImpl localOrder = shoppingCart.getCurrentLocal();
//                    if (localOrder != null && localOrder.getId().equals(castOrder.getId())) {
//                        shoppingCart.setCurrentLocal(null);
//                        shoppingCart.getCurrentLocal();
//                    } else {
//                    }
//                }
//            }
        Boolean webResult = false, localResult = false;
        if (isWebActive()){
            try {
                // init form for web order
                webResult = super.handleInitForm(pRequest, pResponse);
                // switch to local order
                switchActive();
                // init form for local order
                localResult = super.handleInitForm(pRequest, pResponse);
                return webResult && localResult;
            } finally {
                // reset to web order
                resetActiveOrder();
            }
        } else {
            return super.handleInitForm(pRequest, pResponse);
        }
    }

    /**
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return contextTools;
    }

    /**
     * @param contextTools the contextTools to set
     */
    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }

    /**
     * @param pWebStockToAdd the webStockToAdd to set
     */
    public void setWebStockToAdd(String pWebStockToAdd){
        long value = Long.MAX_VALUE;
        if (pWebStockToAdd != null) {
            value = Long.parseLong(pWebStockToAdd);
        }
        if (value > 0) {
            mWebStockToAdd = value;
        } else {
            mWebStockToAdd = Long.MAX_VALUE;
        }
    }

    /**
     * @param pLocalStockToAdd the localStockToAdd to set
     */
    public void setLocalStockToAdd(String pLocalStockToAdd){
        long value = Long.MAX_VALUE;
        if (pLocalStockToAdd != null) {
            value = Long.parseLong(pLocalStockToAdd);
        }
        if (value > 0) {
            mLocalStockToAdd = value;
        } else {
            mLocalStockToAdd = Long.MAX_VALUE;
        }
    }

    /**
     * Returns items array of property holders for add operations according to order type
     *
     * @return an array of item level property holders for add operations
     */
    public AddCommerceItemInfo [] getItems(boolean pIsWebOrderType, long[][] pOrderQty, long[] pAddQuantity) {
        AddCommerceItemInfo[] items = getItems();
        for (int i = 0; i < items.length; i++){
            long canAddQuantity = pIsWebOrderType?mWebStockToAdd-pOrderQty[i][0]:(mLocalStockToAdd-pOrderQty[i][1]);
            if (canAddQuantity < 0) canAddQuantity = 0;
            if (pAddQuantity[i] > canAddQuantity){
                items[i].setQuantity(canAddQuantity);
            } else {
                items[i].setQuantity(pAddQuantity[i]);
            }
        }
      return items;
    }

} // end of class
