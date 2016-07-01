package com.castorama.order;


//Referenced classes of package atg.commerce.states:
//         ObjectStates, StateDefinitions

public class CommerceItemStates extends ObjectStates
{

 public CommerceItemStates()
 {
     INITIAL = "initial";
     PENDING_REMOVE = "pending_remove";
     REMOVED = "removed";
     StateDefinitions.COMMERCEITEMSTATES = this;
 }

 public static final String CLASS_VERSION = "$Id: CommerceItemStates.java,v 1.2 2000/03/10 16:26:39 sperman Exp $";
 public static final int DEFAULT_INITIAL = 0;
 public String INITIAL;
 public String PENDING_REMOVE;
 public String REMOVED;
}