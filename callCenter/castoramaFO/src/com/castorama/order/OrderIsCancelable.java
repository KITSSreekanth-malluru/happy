package com.castorama.order;



import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderUserMessage;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

//Referenced classes of package atg.commerce.csr:
//         OrderComponentIsModifiable

public class OrderIsCancelable extends OrderComponentIsModifiable
{

 public OrderIsCancelable()
 {
 }

 public Order getOrder()
 {
     if(super.mOrder == null && mOrderManager != null && super.mId != null && super.mId.trim().length() != 0)
     {
         try
         {
             if(isLoggingDebug())
                 {
                 logDebug("Looking up order #" + super.mId + "...");
                 }
             if(mOrderManager.orderExists(super.mId))
             {
                 super.mOrder = mOrderManager.loadOrder(super.mId);
                 
                     if(isLoggingDebug())
                     {
                     logDebug("...found.");
                     }
             }
         }
         catch(CommerceException commerceexception)
         {
             if(isLoggingError())
                 {
                 logError(commerceexception);
                 }
         }
     }
     return super.mOrder;
 }

 public OrderManager getOrderManager()
 {
     return mOrderManager;
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     boolean flag = false;
     String s = dynamohttpservletrequest.getParameter(OrderComponentIsModifiable.ID);
     if(s == null || s.trim().length() == 0)
         {
         s = getId();
         }
     if(s == null || s.trim().length() == 0)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s1 = OrderUserMessage.format("noGroupId", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s1);
     }
     setId(s);
     Order order = getOrder();
     if(order == null || !(order instanceof Order))
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s2 = OrderUserMessage.format("noOrderToModify", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s2);
     }
     boolean flag1 = true;
     try
     {
         if(EditableOrder(order))
         {
             List list = order.getCommerceItems();
             for(int i = 0; i < list.size(); i++)
             {
                 if(!EditableCommerceItem(((CommerceItem)list.get(i)).getId(), order))
                 {
                     flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.FALSE, dynamohttpservletrequest, dynamohttpservletresponse);
                     return;
                 }
             }
             List list1 = order.getPaymentGroups();
             for(int j = 0; j < list1.size(); j++)
             {
                 if(!EditablePaymentGroup(((PaymentGroup)list1.get(j)).getId(), order))
                 {
                     flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.FALSE, dynamohttpservletrequest, dynamohttpservletresponse);
                     return;
                 }
             }
             List list2 = order.getShippingGroups();
             for(int k = 0; k < list2.size(); k++)
             {
                 if(!EditableShippingGroup(((ShippingGroup)list2.get(k)).getId(), order))
                 {
                     flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.FALSE, dynamohttpservletrequest, dynamohttpservletresponse);
                     return;
                 }
             }

         } else
         {
             flag1 = false;
         }
     }
     catch(CommerceException commerceexception)
     {
         throw new ServletException(commerceexception);
     }
     if(flag1)
         {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.TRUE, dynamohttpservletrequest, dynamohttpservletresponse);
         }
     else
         {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.FALSE, dynamohttpservletrequest, dynamohttpservletresponse);
         }
     if(!flag)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
     }
 }

 public void setOrderManager(OrderManager ordermanager)
 {
     mOrderManager = ordermanager;
 }

 public static final String CLASS_VERSION = "$Id: OrderIsCancelable.java,v 1.1 2000/06/09 20:31:38 mrutman Exp $";
 OrderManager mOrderManager;
}