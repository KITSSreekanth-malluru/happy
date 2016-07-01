package com.castorama.order;


import javax.jms.JMSException;

import atg.commerce.fulfillment.GenericRemove;
import atg.commerce.fulfillment.Modification;
import atg.commerce.fulfillment.ModifyOrder;
import atg.commerce.messaging.SourceSinkTemplate;
import atg.nucleus.ServiceException;

//Referenced classes of package atg.commerce.csr:
//         OrderUpdateNotification

public class MessageDispatcher extends SourceSinkTemplate
{

 public MessageDispatcher()
 {
     mModifyNotificationPort = null;
     mModifyPort = null;
     mMessageSourceName = "CancelOrderMessageDispatcher";
 }

 public void doStartService()
     throws ServiceException
 {
     super.doStartService();
     if(isLoggingInfo())
         {
         logInfo("Starting Customer Service Messaging");
         }
     startMessageSource();
 }

 public String getMessageSourceName()
 {
     return mMessageSourceName;
 }

 public String getModifyNotificationPort()
 {
     return mModifyNotificationPort;
 }

 public String getModifyPort()
 {
     return mModifyPort;
 }

 public void sendCancelOrder(String s)
 {
    
     Modification amodification[] = new Modification[1];
     ModifyOrder modifyorder = new ModifyOrder();
     modifyorder.setOrderId(s);
     modifyorder.setSource(getMessageSourceName());
     modifyorder.setOriginalSource(getMessageSourceName());
     modifyorder.setOriginalId(modifyorder.getId());
     GenericRemove genericremove = new GenericRemove();
     genericremove.setTargetType(4);
     genericremove.setTargetId(s);
     amodification[0] = genericremove;
     modifyorder.setModifications(amodification);
     try
     {
         sendCommerceMessage(modifyorder, getModifyPort());
     }
     catch(JMSException jmsexception)
     {
         if(isLoggingError())
             {
             
             logError(jmsexception);
             }
     }
 }

 public void sendOrderUpdateNotification(OrderUpdateNotification orderupdatenotification)
 {
     orderupdatenotification.setSource(getMessageSourceName());
     orderupdatenotification.setOriginalSource(getMessageSourceName());
     orderupdatenotification.setOriginalId(orderupdatenotification.getId());
     try
     {
         sendCommerceMessage(orderupdatenotification, getModifyNotificationPort());
     }
     catch(JMSException jmsexception)
     {
         if(isLoggingError())
             {
             logError(jmsexception);
             }
     }
 }

 public void setMessageSourceName(String s)
 {
     mMessageSourceName = s;
 }

 public void setModifyNotificationPort(String s)
 {
     mModifyNotificationPort = s;
 }

 public void setModifyPort(String s)
 {
     mModifyPort = s;
 }

 public static final String CLASS_VERSION = "$Id: MessageDispatcher.java,v 1.8 2000/07/18 19:10:37 mrutman Exp $";
 String mModifyNotificationPort;
 String mModifyPort;
 String mMessageSourceName;
}