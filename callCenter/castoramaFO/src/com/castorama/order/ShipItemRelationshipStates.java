package com.castorama.order;



//Referenced classes of package atg.commerce.states:
//         ObjectStates, StateDefinitions

public class ShipItemRelationshipStates extends ObjectStates
{

 public ShipItemRelationshipStates()
 {
     INITIAL = "initial";
     PENDING_DELIVERY = "pending_delivery";
     DELIVERED = "delivered";
     PRE_ORDERED = "pre_ordered";
     BACK_ORDERED = "back_ordered";
     PENDING_REMOVE = "pending_remove";
     REMOVED = "removed";
     PENDING_RETURN = "pending_return";
     RETURNED = "returned";
     FAILED = "failed";
     ITEM_NOT_FOUND = "item_not_found";
     OUT_OF_STOCK = "out_of_stock";
     StateDefinitions.SHIPITEMRELATIONSHIPSTATES = this;
 }

 public static final String CLASS_VERSION = "$Id: ShipItemRelationshipStates.java,v 1.3 2000/05/23 16:37:37 sperman Exp $";
 public String INITIAL;
 public String PENDING_DELIVERY;
 public String DELIVERED;
 public String PRE_ORDERED;
 public String BACK_ORDERED;
 public String PENDING_REMOVE;
 public String REMOVED;
 public String PENDING_RETURN;
 public String RETURNED;
 public String FAILED;
 public String ITEM_NOT_FOUND;
 public String OUT_OF_STOCK;
}