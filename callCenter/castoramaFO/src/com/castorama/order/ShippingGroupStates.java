package com.castorama.order;



//Referenced classes of package atg.commerce.states:
//         ObjectStates, StateDefinitions

public class ShippingGroupStates extends ObjectStates
{

 public ShippingGroupStates()
 {
     INITIAL = "initial";
     PROCESSING = "processing";
     PENDING_REMOVE = "pending_remove";
     REMOVED = "removed";
     FAILED = "failed";
     PENDING_SHIPMENT = "pending_shipment";
     NO_PENDING_ACTION = "no_pending_action";
     PENDING_MERCHANT_ACTION = "pending_merchant_action";
     StateDefinitions.SHIPPINGGROUPSTATES = this;
 }

 public static final String CLASS_VERSION = "$Id: ShippingGroupStates.java,v 1.2 2000/03/10 16:27:31 sperman Exp $";
 public String INITIAL;
 public String PROCESSING;
 public String PENDING_REMOVE;
 public String REMOVED;
 public String FAILED;
 public String PENDING_SHIPMENT;
 public String NO_PENDING_ACTION;
 public String PENDING_MERCHANT_ACTION;
 
 
}

