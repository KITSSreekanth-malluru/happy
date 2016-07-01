package com.castorama.order;

import com.castorama.commerce.states.CastOrderStates;




//Referenced classes of package atg.commerce.states:
//         OrderStates, ShippingGroupStates, PaymentGroupStates, CommerceItemStates, 
//         ShipItemRelationshipStates

public class StateDefinitions
{

 public StateDefinitions()
 {
 }

 public static final String CLASS_VERSION = "$Id: StateDefinitions.java,v 1.4 2000/03/10 18:50:11 sperman Exp $";
 public static CastOrderStates ORDERSTATES = null;
 public static ShippingGroupStates SHIPPINGGROUPSTATES = null;
 public static PaymentGroupStates PAYMENTGROUPSTATES = null;
 public static CommerceItemStates COMMERCEITEMSTATES = null;
 public static ShipItemRelationshipStates SHIPITEMRELATIONSHIPSTATES = null;

}