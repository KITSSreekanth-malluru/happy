package com.castorama.order;



import atg.commerce.CommerceException;
import atg.commerce.order.*;
import atg.commerce.states.*;
import atg.nucleus.naming.ParameterName;
import atg.servlet.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;

public class OrderComponentIsModifiable extends DynamoServlet
{

 public OrderComponentIsModifiable()
 {
     mOrder = null;
     mFalsePaymentGroupStates = new ArrayList(0);
     mFalseShippingGroupStates = new ArrayList(0);
     mFalseShipItemRelationshipStates = new ArrayList(0);
     mFalseOrderStates = new ArrayList(0);
     mUseRequestLocale = true;
 }

 protected boolean EditableCommerceItem(String s, Order order)
     throws CommerceException
 {
     if(order == null || s == null || s.trim().length() == 0)
         {
         throw new InvalidParameterException();
         }
     if(getShipItemRelationshipStates() == null)
         {
         throw new CommerceException();
         }
     CommerceItem commerceitem = order.getCommerceItem(s);
     String s1 = getShipItemRelationshipStates().getStateString(commerceitem.getState());
     return !mFalseShipItemRelationshipStates.contains(s1);
 }

 protected boolean EditableOrder(Order order)
     throws CommerceException
 {
     if(order == null)
         {
         throw new InvalidParameterException();
         }
     if(getOrderStates() == null)
         {
         throw new CommerceException();
         }
     String s = getOrderStates().getStateString(order.getState());
     return !mFalseOrderStates.contains(s);
 }

 protected boolean EditablePaymentGroup(String s, Order order)
     throws CommerceException
 {
     if(order == null || s == null || s.trim().length() == 0)
         {
         throw new InvalidParameterException();
         }
     /*if(getPaymentGroupStates() == null)
         {
         throw new CommerceException();
         }*/
     PaymentGroup paymentgroup = order.getPaymentGroup(s);
     String s1 = getPaymentGroupStates().getStateString(paymentgroup.getState());
     return !mFalsePaymentGroupStates.contains(s1);
 }

 protected boolean EditableShippingGroup(String s, Order order)
     throws CommerceException
 {
     if(order == null || s == null || s.trim().length() == 0)
         {
         throw new InvalidParameterException();
         }
     if(getShippingGroupStates() == null)
         {
         throw new CommerceException();
         }
     ShippingGroup shippinggroup = order.getShippingGroup(s);
          
     String s1 = getShippingGroupStates().getStateString(shippinggroup.getState());
     return !mFalseShippingGroupStates.contains(s1);
 }

 public Locale getDefaultLocale()
 {
     if(mDefaultLocale != null)
         {
         return mDefaultLocale;
         }
     else
         {
         return Locale.getDefault();
         }
 }

 public String getId()
 {
     return mId;
 }

 public Order getOrder()
 {
     return mOrder;
 }

 public OrderStates getOrderStates()
 {
     return mOrderStates;
 }

 public PaymentGroupStates getPaymentGroupStates()
 {
     return mPaymentGroupStates;
 }

 public ShipItemRelationshipStates getShipItemRelationshipStates()
 {
     return mShipItemRelationshipStates;
 }

 public ShippingGroupStates getShippingGroupStates()
 {
     //ESSAI MODIF
     ShippingGroupStates essai = new ShippingGroupStates();
     mShippingGroupStates = essai;
     
     
     
     return mShippingGroupStates;
 }

 public String getType()
 {
     return mType;
 }

 public Locale getUserLocale(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     Object obj = dynamohttpservletrequest.getObjectParameter(LOCALE_PARAM);
     if(obj instanceof Locale)
         {
         return (Locale)obj;
         }
     if(obj instanceof String)
         {
         return RequestLocale.getCachedLocale((String)obj);
         }
     if(isUseRequestLocale())
     {
         RequestLocale requestlocale = dynamohttpservletrequest.getRequestLocale();
         if(requestlocale != null)
         {
             return requestlocale.getLocale();
         }
     }
     return getDefaultLocale();
 }

 public boolean isUseRequestLocale()
 {
     return mUseRequestLocale;
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     boolean flag = false;
     Order order = getOrder();
     if(order == null || !(order instanceof Order))
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s = OrderUserMessage.format("noOrderToModify", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = dynamohttpservletrequest.getParameter(ID);
     if(s1 == null || s1.trim().length() == 0)
         {
         s1 = getId();
         }
     if(s1 == null || s1.trim().length() == 0)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s2 = OrderUserMessage.format("noGroupId", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s2);
     }
     String s3 = dynamohttpservletrequest.getParameter(TYPE);
     if(s3 == null || s3.trim().length() == 0)
         {
         s3 = getType();
         }
     if(s3 == null || s3.trim().length() == 0)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
         String s4 = OrderUserMessage.format("unknownType", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s4);
     }
     boolean flag1 = false;
     try
     {
         if(s3.equals("paymentGroup"))
         {
             flag1 = EditablePaymentGroup(s1, order);
         }
         else
         if(s3.equals("shippingGroup"))
         {
             flag1 = EditableShippingGroup(s1, order);
         }
         else
         if(s3.equals("commerceItem"))
         {
             flag1 = EditableCommerceItem(s1, order);
         }
         else
         if(s3.equals("order"))
         {
             flag1 = EditableOrder(order);
         } else
         {
             flag = dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
             String s5 = OrderUserMessage.format("unknownType", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             throw new ServletException(s5);
         }
     }
     catch(CommerceException commerceexception)
     {
         throw new ServletException(commerceexception);
     }
     if(flag1)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(TRUE, dynamohttpservletrequest, dynamohttpservletresponse);
     }
     else
         {
         flag = dynamohttpservletrequest.serviceLocalParameter(FALSE, dynamohttpservletrequest, dynamohttpservletresponse);
         }
     if(!flag)
     {
         flag = dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
     }
 }

 public void setDefaultLocale(Locale locale)
 {
     mDefaultLocale = locale;
 }

 public void setFalseOrderStates(String as[])
 {
     for(int i = 0; i < as.length; i++)
     {
         mFalseOrderStates.add(as[i]);
     }

 }

 public void setFalsePaymentGroupStates(String as[])
 {
     for(int i = 0; i < as.length; i++)
     {
         mFalsePaymentGroupStates.add(as[i]);
     }

 }

 public void setFalseShipItemRelationshipStates(String as[])
 {
     for(int i = 0; i < as.length; i++)
     {
         mFalseShipItemRelationshipStates.add(as[i]);
     }

 }

 public void setFalseShippingGroupStates(String as[])
 {
     for(int i = 0; i < as.length; i++)
     {
         mFalseShippingGroupStates.add(as[i]);
     }

 }

 public void setId(String s)
 {
     mId = s;
 }

 public void setOrder(Order order)
 {
     mOrder = order;
 }

 public void setOrderStates(OrderStates orderstates)
 {
     mOrderStates = orderstates;
 }

 public void setPaymentGroupStates(PaymentGroupStates paymentgroupstates)
 {
     mPaymentGroupStates = paymentgroupstates;
 }

 public void setShipItemRelationshipStates(ShipItemRelationshipStates shipitemrelationshipstates)
 {
     mShipItemRelationshipStates = shipitemrelationshipstates;
 }

 public void setShippingGroupStates(ShippingGroupStates shippinggroupstates)
 {
     mShippingGroupStates = shippinggroupstates;
 }

 public void setType(String s)
 {
     mType = s;
 }

 public void setUseRequestLocale(boolean flag)
 {
     mUseRequestLocale = flag;
 }

 public static final String CLASS_VERSION = "$Id: OrderComponentIsModifiable.java,v 1.5 2000/06/09 20:32:11 mrutman Exp $";
 public static final ParameterName ID = ParameterName.getParameterName("id");
 public static final ParameterName TYPE = ParameterName.getParameterName("type");
 public static final ParameterName ORDER = ParameterName.getParameterName("order");
 public static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
 public static final ParameterName TRUE = ParameterName.getParameterName("true");
 public static final ParameterName FALSE = ParameterName.getParameterName("false");
 public static final ParameterName ERROR = ParameterName.getParameterName("error");
 protected static final String MSG_NO_ORDER = "noOrderToModify";
 protected static final String MSG_NO_GROUP_ID = "noGroupId";
 protected static final String MSG_UNKNOWN_TYPE = "unknownType";
 protected static final String SHIPPING_GROUP_TYPE = "shippingGroup";
 protected static final String PAYMENT_GROUP_TYPE = "paymentGroup";
 protected static final String COMMERCE_ITEM_TYPE = "commerceItem";
 protected static final String ORDER_TYPE = "order";
 Order mOrder;
 String mId;
 String mType;
 PaymentGroupStates mPaymentGroupStates;
 ShippingGroupStates mShippingGroupStates;
 ShipItemRelationshipStates mShipItemRelationshipStates;
 OrderStates mOrderStates;
 protected List mFalsePaymentGroupStates;
 protected List mFalseShippingGroupStates;
 protected List mFalseShipItemRelationshipStates;
 protected List mFalseOrderStates;
 Locale mDefaultLocale;
 boolean mUseRequestLocale;

}