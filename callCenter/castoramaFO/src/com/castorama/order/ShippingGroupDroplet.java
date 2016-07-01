package com.castorama.order;



import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ShippingGroupDroplet extends DynamoServlet
{

 public ShippingGroupDroplet()
 {
 }

 public String getGroupId()
 {
     return mGroupId;
 }

 public HardgoodShippingGroup getHardgoodShippingGroup()
 {
     return mHardgoodShippingGroup;
 }

 public Order getOrder()
 {
     return mOrder;
 }

 public OrderManager getOrderManager()
 {
     return mOrderManager;
 }

 public ShippingGroup getShippingGroup()
 {
     return mShippingGroup;
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     ShippingGroup shippinggroup = null;
     Order order = getOrder();
     if(order != null)
     {
         try
         {
             shippinggroup = order.getShippingGroup(dynamohttpservletrequest.getParameter(GROUP_ID));
         }
         /*catch(ShippingGroupNotFoundException shippinggroupnotfoundexception)
         {
             if(isLoggingError())
             {
                 logError(shippinggroupnotfoundexception);
             }
         }
         catch(InvalidParameterException invalidparameterexception)
         {
             if(isLoggingError())
             {
                 logError(invalidparameterexception);
             }
         }*/
         catch (Exception e)
         {
             if(isLoggingError())
             {
                 logError(e);
             }
         }
     }
     else
     {
         throw new ServletException("No Related Order Found");
     }
     if(shippinggroup != null)
     {
         if(shippinggroup instanceof HardgoodShippingGroup)
         {
             setHardgoodShippingGroup((HardgoodShippingGroup)shippinggroup);
             dynamohttpservletrequest.serviceLocalParameter(H_GROUP, dynamohttpservletrequest, dynamohttpservletresponse);
         } else
         if(shippinggroup instanceof ShippingGroup)
         {
             setShippingGroup(shippinggroup);
             dynamohttpservletrequest.serviceLocalParameter(S_GROUP, dynamohttpservletrequest, dynamohttpservletresponse);
         } else
         {
             setShippingGroup(null);
             dynamohttpservletrequest.serviceLocalParameter(EMPTY, dynamohttpservletrequest, dynamohttpservletresponse);
         }
     } else
     {
         if(isLoggingError())
         {
             logError("Shipping Group was un-obtainable");
         }
         dynamohttpservletrequest.serviceLocalParameter(EMPTY, dynamohttpservletrequest, dynamohttpservletresponse);
     }
 }

 public void setGroupId(String s)
 {
     mGroupId = s;
 }

 public void setHardgoodShippingGroup(HardgoodShippingGroup hardgoodshippinggroup)
 {
     mHardgoodShippingGroup = hardgoodshippinggroup;
 }

 public void setOrder(Order order)
 {
     mOrder = order;
 }

 public void setOrderManager(OrderManager ordermanager)
 {
     mOrderManager = ordermanager;
 }

 public void setShippingGroup(ShippingGroup shippinggroup)
 {
     mShippingGroup = shippinggroup;
 }

 public static final String CLASS_VERSION = "$Id: ShippingGroupDroplet.java,v 1.3 2000/07/18 15:41:34 mrutman Exp $";
 public static final ParameterName GROUP_ID = ParameterName.getParameterName("id");
 public static final ParameterName H_GROUP = ParameterName.getParameterName("hardgoodShippingGroup");
 public static final ParameterName S_GROUP = ParameterName.getParameterName("shippingGroup");
 public static final ParameterName EMPTY = ParameterName.getParameterName("empty");
 Order mOrder;
 String mGroupId;
 HardgoodShippingGroup mHardgoodShippingGroup;
 ShippingGroup mShippingGroup;
 OrderManager mOrderManager;

}