package com.castorama.commerce.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.TransactionManager;

import atg.adapter.gsa.GSAItem;
import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.castorama.commerce.profile.Constants;

/**
 * Extends OrderHolder class to add Last Added Commerce Item logic.
 *
 * @author EPAM team
 */
public class CastOrderHolder extends OrderHolder {
    /** CLICK_AND_COLLECT_ORDER_TYPE constant. */
    private static final String CLICK_AND_COLLECT_ORDER_TYPE = "clickAndCollect";

    /** VAT Tax property name. */
    private static final String TAUX_TVA = "tauxTVA";

    /** validationHelper property */
    private CastOrderValidationHelper validationHelper;

    /** Catalog tools. */
    private CatalogTools mCatalogTools;

    /** Selected Order. */
    private int mSelectedOrder;

    /**
     * Returns lastAddedCommerceItem property.
     *
     * @return lastAddedCommerceItem property.
     */
    public CastCommerceItemImpl getLastAddedCommerceItem() {
        CastCommerceItemImpl result = null;
        Order order = getCurrent();
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items) {
                for (Iterator it = items.iterator(); it.hasNext();) {
                    CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                    if ((null == result) || (0 < result.compareTo(item))) {
                        result = item;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns lastAddedCommerceItemLocal property.
     *
     * @return lastAddedCommerceItemLocal property.
     */
    public CastCommerceItemImpl getLastAddedCommerceItemLocal() {
        CastCommerceItemImpl result = null;
        Order orderLocal = getCurrentLocal();
        if (null != orderLocal) {
            List items = orderLocal.getCommerceItems();
            if (null != items) {
                for (Iterator it = items.iterator(); it.hasNext();) {
                    CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                    if ((null == result) || (0 < result.compareTo(item))) {
                        result = item;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns lastAddedCommerceItem property.
     *
     * @return lastAddedCommerceItem property.
     */
    public CastCommerceItemImpl getLastAddedCommerceItemFromTwoOrders() {
        CastCommerceItemImpl result = null;
        Order order = getCurrent();
        Date lastTime = new Date(0);
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items) {
                for (Iterator it = items.iterator(); it.hasNext();) {
                    CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                    if ((null == result) || (0 < result.compareTo(item))) {
                        result = item;
                        lastTime = item.getCreationDate();
                    }
                }
            }
        }
        Order orderLocal = getCurrentLocal();
        if (null != orderLocal) {
            List items = orderLocal.getCommerceItems();
            if (null != items) {
                for (Iterator it = items.iterator(); it.hasNext();) {
                    CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                    if ((null == result) || (0 < result.compareTo(item))) {
                        Date itemDate = item.getCreationDate();
                        if (itemDate != null && lastTime.getTime() < itemDate.getTime()){
                            result = item;
                            lastTime = item.getCreationDate();
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns ohterCommerceItems property.
     *
     * @return ohterCommerceItems property.
     */
    public List getOhterCommerceItems() {
        List result = new ArrayList();
        Order order = getCurrent();
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items) {
                CastCommerceItemImpl last = getLastAddedCommerceItem();
                if (null == last) {
                    result.addAll(items);
                } else {
                    for (Iterator it = items.iterator(); it.hasNext();) {
                        CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                        if ((null != item) && !last.getId().equals(item.getId())) {
                            result.add(item);
                        }
                    }
                }
                Collections.sort(result);
            }
        }
        return result;
    }

    /**
     * Returns quantities of SKU from both orders.
     *
     * @return ohterCommerceItems property.
     */
    public long[] getSkuQuantities(String pSkuId) {
        long[] qties= {0l,0l};
        if (pSkuId == null || pSkuId.length() == 0) return qties;
        qties[0] = getSkuQuantity(pSkuId, getCurrent());
        qties[1] = getSkuQuantity(pSkuId, getCurrentLocal());
        return qties;
    }

    /**
     * @param pSkuId
     * @param order
     */
    private long getSkuQuantity(String pSkuId, Order order) {
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items && items.size() > 0) {
                for (Iterator it = items.iterator(); it.hasNext();) {
                    CastCommerceItemImpl item = (CastCommerceItemImpl) it.next();
                    if (null != item) {
                        String orderSkuId = item.getCatalogRefId();
                        if (orderSkuId != null && orderSkuId.equals(pSkuId)){
                            return item.getQuantity();
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    /**
     * Returns cartEmpty property.
     *
     * @return cartEmpty property.
     */
    public boolean isCartEmpty() {
        boolean result = true;
        Order order = getCurrent();
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items) {
                result = (0 == items.size());
            }
        }
        return result;
    }

    /**
     * Returns cartEmpty property.
     *
     * @return cartEmpty property.
     */
    public boolean isCartLocalEmpty() {
        boolean result = true;
        Order order = getCurrentLocal();
        if (null != order) {
            List items = order.getCommerceItems();
            if (null != items) {
                result = (0 == items.size());
            }
        }
        return result;
    }

    /**
     * Gets Price for Zanox: VAT Tax is deducted  from Sale Price if On Sale
     * flag = True and from List Price, if On Sale flag = False.
     *
     * @return price for zanox
     */
    public double getZanoxPrice() {
        BigDecimal price = new BigDecimal(0);
        Order order = getLast();
        if (null != order) {
            List<CommerceItem> commerceItems = order.getCommerceItems();
            if ((commerceItems != null) && !commerceItems.isEmpty()) {
                for (CommerceItem ci : commerceItems) {
                    String skuId = ci.getCatalogRefId();
                    ItemPriceInfo priceInfo = ci.getPriceInfo();
                    if (priceInfo != null) {
                        try {
                            BigDecimal amount = new BigDecimal("" + priceInfo.getAmount());

                            RepositoryItem ri = getCatalogTools().findSKU(skuId);
                            if (ri != null) {
                                Float tauxTVAFloat = (Float) ri.getPropertyValue(TAUX_TVA);
                                BigDecimal tauxTVA = new BigDecimal(0);
                                if (tauxTVAFloat != null) {
                                    tauxTVA = new BigDecimal("" + tauxTVAFloat);
                                }
                                BigDecimal factor =
                                    new BigDecimal(100).add(tauxTVA).divide(new BigDecimal(100), 10,
                                                                                 RoundingMode.HALF_UP);
                                price =
                                    price.add(amount.divide(factor, 10, RoundingMode.HALF_UP));
                            }
                        } catch (RepositoryException e) {
                            if (isLoggingError()) {
                                logError("CommerceException occurs when get price for Zanox for order " + order.getId() + ". " + e.getMessage());
                            }
                        }  // end try-catch
                    }  // end if
                }  // end for
            }  // end if
        }  // end if
        return (price != null) ? price.doubleValue() : 0;
    }

    /**
     * Validate current order.
     *
     * @see    CastOrderValidationHelper.
     *
     * @return true when order changed, otherwise false.
     */
    public boolean validateCurrentOrder() {
        return getValidationHelper().validate(getCurrent());
    }

    /**
     * Returns validationHelper property.
     *
     * @return validationHelper property.
     */
    public CastOrderValidationHelper getValidationHelper() {
        return validationHelper;
    }

    /**
     * Sets the value of the validationHelper property.
     *
     * @param validationHelper parameter to set.
     */
    public void setValidationHelper(CastOrderValidationHelper validationHelper) {
        this.validationHelper = validationHelper;
    }

    /**
     * Returns totalCommerceItemCount property.
     *
     * @return totalCommerceItemCount property.
     */
    public long getTotalCommerceItemCount() {
        long total = 0;
        Order current = getCurrent();
        if (current != null) {
            checkOrderStatus(current);
        }
        CastOrderImpl currentLocal = getCurrentLocal();
        if (currentLocal!=null){
            checkOrderLocalStatus(currentLocal);
        }
        if (current != null) {
            List currentShippingGroups = current.getShippingGroups();
            if (currentShippingGroups != null && currentShippingGroups.size() > 0) {
                // returns only first shipping group for web order
                ShippingGroup webShippingGroup = (ShippingGroup) currentShippingGroups.get(0);
                if (currentLocal != null) {
                    List currentLocalShippingGroups = currentLocal.getShippingGroups();
                    if (currentLocalShippingGroups != null && currentLocalShippingGroups.size() > 0) {
                        // returns only first shipping group for web order
                        ShippingGroup localShippingGroup = (ShippingGroup) currentLocalShippingGroups.get(0);
                        if (localShippingGroup != null && localShippingGroup.getCommerceItemRelationshipCount() > 0) {
                            CastOrderTools orderTools = (CastOrderTools) getOrderManager().getOrderTools();
                            ArrayList<ArrayList<CommerceItemRelationship>> calculatedGroups = orderTools.calculateGroups(webShippingGroup, localShippingGroup);
                            ArrayList<CommerceItemRelationship> mergedWebRelationships = calculatedGroups.get(0);
                            ArrayList<CommerceItemRelationship> mergedLocalRelationships = calculatedGroups.get(1);
                            long webCount = 0, localCount=0;
                            for (int i = 0; i < mergedWebRelationships.size(); i++) {
                                webCount = mergedWebRelationships.get(i)!=null?mergedWebRelationships.get(i).getQuantity():0;
                                localCount = mergedLocalRelationships.get(i)!=null?mergedLocalRelationships.get(i).getQuantity():0;
                                total += Math.max(webCount, localCount);
                            }
                        } else {
                            // count in case when local order is empty
                            if (current != null) {
                                total = current.getTotalCommerceItemCount();
                            }
                        }
                    } else {
                        // count in case when local order is empty
                        if (current != null) {
                            total = current.getTotalCommerceItemCount();
                        }
                    }
                } else {
                    // count in case when local order is empty
                    if (current != null) {
                        total = current.getTotalCommerceItemCount();
                    }
                }
            } else {
                // count in case when web order is empty
                if (currentLocal != null) {
                    total = currentLocal.getTotalCommerceItemCount();
                }
            }
        } else {
            // count in case when web order is empty
            if (currentLocal != null) {
                total = currentLocal.getTotalCommerceItemCount();
            }
        }

        return total;
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
     * Sets the value of the catalogTools property.
     *
     * @param tools parameter to set.
     */
    public void setCatalogTools(CatalogTools tools) {
        mCatalogTools = tools;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param order ToDo: DOCUMENT ME!
     */
    private void checkOrderStatus(Order order) {
        if ( isLoggingInfo() ) {
            logInfo("checkOrderStatus() - start: " + order);
        }

        if (null != order) {
            OrderManager orderManager = getOrderManager();
            if (orderManager instanceof CastOrderManager) {
                CastOrderManager com = (CastOrderManager) orderManager;
                String orderId = order.getId();
                if (com.fixIncompleteOrderStatus(orderId)) {
                    Order current = getCurrent();
                    setCurrent(null);
                    if (com.checkSubmittedOrderStatus(orderId)) {
                        setLast(current); 
                    }
                }
                //com.removeOrder(orderId, true);
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("checkOrderStatus() - finish: " + order);
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param order ToDo: DOCUMENT ME!
     */
    private void checkOrderLocalStatus(Order order) {
        if ( isLoggingInfo() ) {
            logInfo("checkOrderStatus() - start: " + order);
        }
        
        if (null != order) {
            OrderManager orderManager = getOrderManager();
            if (orderManager instanceof CastOrderManager) {
                CastOrderManager com = (CastOrderManager) orderManager;
                String orderId = order.getId();
                if (com.fixIncompleteOrderStatus(orderId)) {
                    Order current = getCurrentLocal();
                    setCurrentLocal(null);
                    if (com.checkSubmittedOrderStatus(orderId)) {
                        setLast(current); 
                    }
                }
                //com.removeOrder(orderId, true);
            }
        }
        
        if ( isLoggingInfo() ) {
            logInfo("checkOrderStatus() - finish: " + order);
        }
    }
    

    /** CurrentLocal property*/
    private CastOrderImpl mCurrentLocal;

    /**
     * Sets property CurrentLocal
     **/
    public synchronized void setCurrentLocal(CastOrderImpl pCurrentLocal) {
      mCurrentLocal = pCurrentLocal;
      super.setRestorableOrders(null);
    }

    /**
     * Returns the current local Order, optionally loading or
     * creating an order if the current local order is null.
     *
     * @param pEnsureOrder
     *    If true and the current order is null, attempt to load previously saved
     *    local order or create and return a new local order for the user.  If false and the
     *    current local order is null, simply return null.
     **/
    public synchronized CastOrderImpl getCurrentLocal(boolean pEnsureOrder)
    {
      if (pEnsureOrder)
        return getCurrentLocal();
      else
        return mCurrentLocal;
    }

    /**
     * Returns the current local Order. If the current local order is null 
     * this method will automatically load the user's saved orders and make one of
     * them the current local order, or create a new local order if no suitabe saved
     * orders are found.
     *
     * @see atg.commerce.profile.CommerceProfileTools#loadShoppingCarts
     **/
    public synchronized CastOrderImpl getCurrentLocal() {
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = null;

        td = new TransactionDemarcation();

        try {
            td.begin(tm);

            if (!validActiveShoppingCart(mCurrentLocal)) {
                if (isLoggingDebug())
                    logDebug("The local order " + mCurrentLocal + " cannot be used as a shopping cart");

//                mCurrentLocal = null;
                setCurrentLocal(null);
                try {
                    if ((getProfile() != null) && (!getProfile().isTransient()) && (getProfileTools() != null)) {
                        if (isLoggingDebug())
                            logDebug("Attempting to find old shopping carts for user " + getProfile());
                        getProfileTools().loadShoppingCarts(getProfile(), this);
                    }
                } catch (CommerceException exc) {
                    if (isLoggingError())
                        logError("CommerceException occurs during get current local order for profile " + getProfile() + ". Attempting to find old shopping carts for user was failed. " + exc.getMessage(), exc);
                }
            }

            if (mCurrentLocal == null) {
                try {
                    Order localOrder = createInitialOrderLocal(getProfile());
                    if (isLoggingDebug())
                        logDebug("Created initial shopping cart " + mCurrentLocal);
                    if (localOrder instanceof CastOrderImpl){
//                        mCurrentLocal = (CastOrderImpl) localOrder;
                        setCurrentLocal((CastOrderImpl) localOrder);
                    }
                } catch (CommerceException exc) {
                    if (isLoggingError())
                        logError("CommerceException occurs during get current local order for profile " + getProfile() + ". Created initial shopping cart was failed. " + exc.getMessage(), exc);
                }
            }
        } catch (TransactionDemarcationException tde) {
            if (isLoggingError())
                logError(tde);
        } finally {
            try {
                td.end();
            } catch (TransactionDemarcationException tde) {
                if (isLoggingError())
                    logError(tde);
            }
        }
        return mCurrentLocal;
    }

    /** Create initial order */
    protected Order createInitialOrder(RepositoryItem pProfile) 
    throws CommerceException
    {
        Order tOrder = super.createInitialOrder(pProfile);
        if (tOrder instanceof CastOrderImpl){
            setStoreId(pProfile, tOrder);
        }
        return tOrder;
    }

    /** Create initial order */
    protected Order createInitialOrderLocal(RepositoryItem pProfile) 
    throws CommerceException
    {
        Order tOrder = super.createInitialOrder(pProfile);
        if (tOrder instanceof CastOrderImpl){
            GSAItem magasin = (GSAItem) pProfile.getPropertyValue(Constants.CURRENT_LOCAL_STORE_PROFILE_PROP);
            String magasinId = null;
            if (magasin != null)
                magasinId = magasin.getRepositoryId();
            if (magasinId != null && magasinId.length()>0){
                ((CastOrderImpl)tOrder).setMagasinId(magasinId.toString());
                ((CastOrderImpl)tOrder).setDeliveryType(CLICK_AND_COLLECT_ORDER_TYPE);
            } else {
                if (isLoggingWarning()){
                    logWarning("profile: "+ pProfile + "contains incorrect magasin id ("+(magasinId!=null?"":"epmpy")+")");
                }
            }
        }
        return tOrder;
    }

    /**
     * Sets store id from Profile to specific order
     * 
     * @param pProfile
     * @param order
     */
    private void setStoreId(RepositoryItem pProfile, Order order) {
        GSAItem magasin = (GSAItem) pProfile.getPropertyValue(Constants.CURRENT_LOCAL_STORE_PROFILE_PROP);
        String magasinId = null;
        if (magasin != null)
            magasinId = magasin.getRepositoryId();
        if (magasinId != null && magasinId.length() > 0) {
            CastOrderImpl castOrder = (CastOrderImpl) order;
            try {
                castOrder.setMagasinId(magasinId);
                getOrderManager().updateOrder(castOrder);
            } catch (CommerceException e) {
                if (isLoggingError())
                    logError("CommerceException occurs when set storeId = " + magasinId + " for order " + order.getId() + ". " + e.getMessage(), e);
            }
        } else {
            if (isLoggingWarning()) {
                logWarning("profile: " + pProfile + "contains incorrect magasin id ("
                        + (magasinId != null ? "" : "empty") + ")");
            }
        }
    }

    /**
     * Sets store id from Profile to both order and local order
     * 
     * @param pProfile
     * @param tOrder
     */
    public void setStoreIdForProfileOrders (RepositoryItem pProfile, String profileStoreId) {
        Order current = getCurrent();
        if (current instanceof CastOrderImpl){
            String currentMagasinId = ((CastOrderImpl)current).getMagasinId();
            if (profileStoreId != null && currentMagasinId != null && !profileStoreId.equals(currentMagasinId)){
                setStoreId(pProfile, current);
            }
        }
        Order currentLocal = getCurrentLocal();
        if (currentLocal instanceof CastOrderImpl){
            String currentLocalMagasinId = ((CastOrderImpl)currentLocal).getMagasinId();
            if (profileStoreId != null && currentLocalMagasinId != null && !profileStoreId.equals(currentLocalMagasinId)){
                setStoreId(pProfile, currentLocal);
            }
        }
    }

    /**
     * Set selected order
     * 0 - nothing 
     * 1 - web (delivery to home)
     * 2 - local (pick up from store)
     * any other value - nothing
     * @param pSelectedOrder the mSelectedOrder to set
     */
    public void setSelectedOrder(int pSelectedOrder) {
        if (pSelectedOrder == 1 || pSelectedOrder == 2){
            this.mSelectedOrder = pSelectedOrder;
        } else {
            this.mSelectedOrder = 0;
        }
    }

    /**
     * Retuns selected order:
     * 0 - nothing 
     * 1 - web (delivery to home)
     * 2 - local (pick up from store)
     * @return the mSelectedOrder
     */
    public int getSelectedOrder() {
        return mSelectedOrder;
    }

    public boolean isCurrentLocalEmpty() {
        if ((mCurrentLocal == null) || (mCurrentLocal.getCommerceItemCount() <= 0))
            return true;
        else
            return false;
    }

    public boolean isCurrentlySelectedEmpty() {
        if (mSelectedOrder==1) return isCurrentEmpty();
        if (mSelectedOrder==2) return isCurrentLocalEmpty();
        return true;
    }

    public Order getCurrentlySelected(){
        if (mSelectedOrder==1) return getCurrent();
        if (mSelectedOrder==2) return getCurrentLocal();
        return null;
    }

    public boolean isCurrentLocalExists() {
        if (mCurrentLocal != null)
            return true;
        else
            return false;
    }
}
