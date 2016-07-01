package com.castorama.order;



import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderUserMessage;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;

//Referenced classes of package atg.commerce.csr:
//         OrderComponentIsModifiable

public class ShippingGroupIsModifiable extends OrderComponentIsModifiable
{

 public ShippingGroupIsModifiable()
 {
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
     Order order = getOrder();
     if(order == null || !(order instanceof Order))
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(OrderComponentIsModifiable.ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s2 = OrderUserMessage.format("noOrderToModify", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s2);
     }
     boolean flag1 = false;
     try
     {
         flag1 = EditableShippingGroup(s, order);
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

 public static final String CLASS_VERSION = "$Id: ShippingGroupIsModifiable.java,v 1.4 2000/07/18 15:39:15 mrutman Exp $";
}