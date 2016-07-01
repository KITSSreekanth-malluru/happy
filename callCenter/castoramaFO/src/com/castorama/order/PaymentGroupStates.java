package com.castorama.order;



//Referenced classes of package atg.commerce.states:
//         ObjectStates, StateDefinitions

public class PaymentGroupStates extends ObjectStates
{

 public PaymentGroupStates()
 {
     INITIAL = "initial";
     AUTHORIZED = "authorized";
     SETTLED = "settled";
     AUTHORIZE_FAILED = "authorize_failed";
     SETTLE_FAILED = "settle_failed";
     REMOVED = "removed";
     StateDefinitions.PAYMENTGROUPSTATES = this;
 }

 public static final String CLASS_VERSION = "$Id: PaymentGroupStates.java,v 1.2 2000/03/10 16:27:09 sperman Exp $";
 public String INITIAL;
 public String AUTHORIZED;
 public String SETTLED;
 public String AUTHORIZE_FAILED;
 public String SETTLE_FAILED;
 public String REMOVED;
}