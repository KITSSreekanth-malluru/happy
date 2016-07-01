package com.castorama.commerce.profile;

import static com.castorama.commerce.profile.Constants.*;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_DELIVERY_TO_HOME;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;

import atg.repository.RepositoryItem;

/**
 * Helper class for profile manipulation utilities.
 * 
 * @author Katsiaryna Dmitrievich
 */
public class CastProfileTools extends CommerceProfileTools {

    /**
     * Checks role of the user: every user has property <code>
     * GROUPS_PROFILE_PROP</code> set of elements of type <code>group</code>.
     * Every <code>group</code> has property <code>
     * LIBELLE_GROUP_PROFILE_PROP</code> - role name.<br />
     * Search role <code>pRole</code> among all group's <code>
     * LIBELLE_GROUP_PROFILE_PROP</code> properties of user with login name
     * <code>pUserLogin</code>.
     * 
     * @see Constants#GROUPS_PROFILE_PROP
     * @see Constants#LIBELLE_GROUP_PROFILE_PROP
     * 
     * @param pUserLogin - login name of the user
     * @param pRole - searching role
     * 
     * @return
     *         <li> <code>true</code> if role was found among all group's <code>
     *         LIBELLE_GROUP_PROFILE_PROP</code> properties of the user
     *         <li><code>false</code> otherwise
     */
    public boolean checkRole(final String pUserLogin, final String pRole) {
        boolean hasGroup = false;

        final RepositoryItem user = getItem(pUserLogin, null);
        if ((user != null) && isFoUser(pUserLogin)) {
            final Set<RepositoryItem> groups = (Set<RepositoryItem>) user.getPropertyValue(GROUPS_PROFILE_PROP);

            final Iterator<RepositoryItem> it = groups.iterator();
            while (it.hasNext() && !hasGroup) {
                final RepositoryItem group = it.next();
                final String groupName = (String) group.getPropertyValue(LIBELLE_GROUP_PROFILE_PROP);
                if (pRole.equalsIgnoreCase(groupName)) {
                    hasGroup = true;
                }
            }
        }

        return hasGroup;
    }

    /**
     * Checks if user with login name <code>pLogin</code> is a bespoke user.
     * 
     * @see Constants#FO_USER_PROFILE_PROP
     * 
     * @param pLogin - user login name
     * 
     * @return
     *         <li> <code>true</code> if property <code>
     *         FO_USER_PROFILE_PROP</code> is true for user with login name
     *         <code>pLogin</code>
     *         <li><code>false</code> otherwise
     */
    public boolean isFoUser(final String pLogin) {
        boolean result = false;
        final RepositoryItem user = getItem(pLogin, null);
        if (null != user.getPropertyValue(FO_USER_PROFILE_PROP)) {
            result = ((Boolean) user.getPropertyValue(FO_USER_PROFILE_PROP)).booleanValue();
        }

        return result;
    }

    public int findOrderToLoad(List pOrders) {
        if (pOrders.size() < 0)
            return -1;

//        if (isSelectLastModifiedOrder())
//            return 0;

        Iterator i = pOrders.iterator();
        Order curOrder = null;
        int counter = 0;
        while (i.hasNext()) {
            curOrder = (Order) i.next();
            if (!curOrder.isExplicitlySaved() && curOrder instanceof CastOrderImpl
                    && ((CastOrderImpl) curOrder).getDeliveryType().equals(DELIVERY_TYPE_OPTION_DELIVERY_TO_HOME)) {
                if (isLoggingDebug())
                    logDebug("The selected order is: " + curOrder.getId());
                return counter;
            }
            counter++;
        }
        return -1;
    }

    public int findOrderLocalToLoad(List pOrders) {
        if (pOrders.size() < 0)
            return -1;

        Iterator i = pOrders.iterator();
        Order curOrder = null;
        int counter = 0;
        while (i.hasNext()) {
            curOrder = (Order) i.next();
            if (!curOrder.isExplicitlySaved() && curOrder instanceof CastOrderImpl
                    && ((CastOrderImpl) curOrder).getDeliveryType().equals(DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT)) {
                if (isLoggingDebug())
                    logDebug("The selected order is: " + curOrder.getId());
                return counter;
            }
            counter++;
        }
        return -1;
    }

    public void repriceShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart,
            PricingModelHolder pUserPricingModels, Locale pLocale, String pPricingOperation) throws CommerceException {
        if (isLoggingDebug())
            logDebug("In repriceShoppingCarts");

        if (!isRepriceOrderOnLogin())
            return;

        if (pShoppingCart instanceof CastOrderHolder) {
            CastOrderHolder castShoppingCart = (CastOrderHolder) pShoppingCart;
            if (!castShoppingCart.isCurrentLocalEmpty()) {
                Order order = castShoppingCart.getCurrentLocal();
                repriceOrder(order, pProfile, pUserPricingModels, pLocale, pPricingOperation);
            }
        }
        super.repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale, pPricingOperation);
    }

    public void loadShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart) throws CommerceException {
        super.loadShoppingCarts(pProfile, pShoppingCart);
        if (isLoggingDebug())
            logDebug("loading orders for profile " + pProfile);

        if ((pProfile == null) || (pShoppingCart == null))
            return;

        if (!shouldLoadShoppingCarts(pShoppingCart))
            return;
        if (pShoppingCart instanceof CastOrderHolder) {
            int orderState = StateDefinitions.ORDERSTATES.getStateValue(OrderStates.INCOMPLETE);
            List orders = getOrderManager().getOrderQueries().getOrdersForProfileInState(pProfile.getRepositoryId(),
                    orderState, super.getSortOrdersByProperty(), super.isAscendingOrder());
    
            if (isLoggingDebug())
                logDebug("Found orders for profile[" + pProfile.getRepositoryId() + "]=" + orders);
            if ((orders != null) && (orders.size() > 0)) {
    
                int order_index = findOrderLocalToLoad(orders);
    
                if (isLoggingDebug())
                    logDebug("The index returned is: " + order_index);
    
                Order persistentCurrent = null;
    
                if (order_index >= 0)
                    persistentCurrent = (Order) orders.remove(order_index);
                else {
                    if (isLoggingDebug())
                        logDebug("The index returned is not in the list");
                }
    
                if (persistentCurrent != null ) {
                    CastOrderHolder castShoppingCart = (CastOrderHolder) pShoppingCart;
                    if (castShoppingCart.isCurrentLocalEmpty()) {
                        if (isLoggingDebug())
                            logDebug("Current order is empty, so make " + persistentCurrent + " current");
                        castShoppingCart.setCurrentLocal((CastOrderImpl) persistentCurrent);
                    } else {
                        Order activeCurrent = castShoppingCart.getCurrentLocal();
                        if (isMergeOrders()) {
                            if (isLoggingDebug())
                                logDebug("Merge order " + activeCurrent + " into order " + persistentCurrent);
    
                            synchronized (persistentCurrent) {
                                getOrderManager().mergeOrders(activeCurrent, persistentCurrent);
                            }
    
                            if (isLoggingDebug())
                                logDebug("Make merged order " + persistentCurrent + " current");
                            castShoppingCart.setCurrentLocal((CastOrderImpl) persistentCurrent);
                        }
                        else if (!activeCurrent.getId().equals(persistentCurrent.getId())) {
                            if (isLoggingDebug())
                                logDebug("Do not merge, so add " + persistentCurrent + " to saved list");
                            castShoppingCart.getSaved().add(persistentCurrent);
                        }
                    }
                }
            }
        }
    }

    public void persistShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart) throws CommerceException {
        super.persistShoppingCarts(pProfile, pShoppingCart);

        if (isLoggingDebug())
            logDebug("In persistShoppingCarts for local order");

        if (pShoppingCart instanceof CastOrderHolder) {

            CastOrderHolder castShoppingCart = (CastOrderHolder) pShoppingCart;

            if (castShoppingCart.isCurrentLocalExists()) {
                try {
                    Order currentLocal = ((CastOrderHolder) pShoppingCart).getCurrentLocal();
                    synchronized (currentLocal) {
                        updateOrderProfileId(currentLocal, pProfile);
                        if ((currentLocal != null) && (currentLocal.isTransient()) && (pShoppingCart.isPersistOrders())) {
                            persistOrderIfNeeded(currentLocal);
                        }
                    }
                } catch (CommerceException e) {
                    if (isLoggingError())
                        logError(e);
                }
            }
        }
    }
}
