package com.castorama.profile;

import atg.repository.*;
import atg.commerce.order.*;
import atg.commerce.pricing.*;
import atg.commerce.states.*;
import atg.commerce.profile.*;
import atg.commerce.CommerceException;
import java.util.*;


public class CastoProfileTools extends CommerceProfileTools
{
  

  //-----------------------------------------------
  /**
   * When a login operation occurs, we attempt to find any old shopping carts
   * from previous sessions. In this process we may move items from any transient
   * shopping carts to their previous persistent shopping carts. However if the
   * <code>mergeOrders</code> property is to set false (default true), then we persist 
   * any existing transient shopping carts that may have built up while navigating as an 
   * anonymous user. All the shopping carts are also repriced to make sure that their
   * prices are up to date. This can be toggeled through the <code>repriceOrderOnLogin</code>
   * property. 
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @exception CommerceException is any errors occur while loading, repricing or persisting
   * the shopping carts
   */
  // TODO MIGRATION : removed, we now use lock in the new 7.2 version
//  public void loadUserShoppingCartForLogin(RepositoryItem pProfile,
//                                           OrderHolder pShoppingCart,
//                                           PricingModelHolder pUserPricingModels,
//                                           Locale pLocale)
//       throws CommerceException
//  {
//    loadShoppingCarts(pProfile, pShoppingCart);
//    repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale);
//    persistShoppingCarts(pProfile, pShoppingCart);
//  }

  /**
   * This method finds all the shopping carts for the user and places them into the
   * session-scope OrderHolder component. "Shopping Carts" are defined to be orders
   * whose state is INCOMPLETE. The orders are sorted by last activity date, and the
   * last order touched is made the current order. This method will optionally
   * merge the current shopping cart for the session (from the OrderHolder.current property)
   * with the first persistent order loaded from the database. This functionality is 
   * toggeled through the <code>mergeOrders</code> property of this form handler.
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @exception CommerceException is any errors occur while loading the shopping carts
   */  
  // TODO MIGRATION : this is a copy of the original 5.1.1 method... not useful
//  public void loadShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart)
//       throws CommerceException
//  {
//    if (isLoggingDebug())
//      logDebug("loading orders for profile " + pProfile);
//    
//    if ((pProfile == null) || (pShoppingCart == null))
//      return;
//
//    int orderState  = StateDefinitions.ORDERSTATES.getStateValue(OrderStates.INCOMPLETE);
//    List orders = getOrderManager().getOrdersForProfileInState(pProfile.getRepositoryId(), 1, 0,
//                                                               orderState, getSortOrdersByProperty(),
//                                                               isAscendingOrder());
//    if (isLoggingDebug())
//      logDebug("Found orders for profile[" + pProfile.getRepositoryId() + "]=" + orders);
//    if ((orders != null) && (orders.size() > 0)) {
//      Order persistentCurrent = (Order)orders.remove(0);
//      if (pShoppingCart.isCurrentEmpty()) {
//        if (isLoggingDebug())
//          logDebug("Current order is empty, so make " + persistentCurrent + " current");
//        pShoppingCart.setCurrent(persistentCurrent);
//      }
//      else if (isMergeOrders()) {
//        // if we are supposed to merge the orders, then put
//        // everything in the current order for this session into
//        // the first persistent order, and make the persistent order
//        // the current for this session
//        Order activeCurrent = pShoppingCart.getCurrent();            
//        if (isLoggingDebug())
//          logDebug("Merge order " + activeCurrent + " into order " + persistentCurrent);
//        getOrderManager().mergeOrders(activeCurrent, persistentCurrent);
//        if (isLoggingDebug())
//          logDebug("Make merged order " + persistentCurrent + " current");
//        pShoppingCart.setCurrent(persistentCurrent);
//      }
//      else {
//        if (isLoggingDebug())
//          logDebug("Do not merge, so add " + persistentCurrent + " to saved list");
//        pShoppingCart.getSaved().add(persistentCurrent);
//      }
//      
//      // If we have any more persistent orders left, also add those to the saved list
//      int size = orders.size();
//      if (size > 0) {
//        if (isLoggingDebug())
//          logDebug("Add the rest of the orders " + orders + " to the saved list");
//        for (int c=0; c<size; c++) {
//          Order order = (Order)orders.get(c);
//          if (isLoggingDebug())
//            logDebug("Adding order " + order + " to saved list");
//          pShoppingCart.getSaved().add(order);
//        }
//      }
//    }
//  }


} // end of class
