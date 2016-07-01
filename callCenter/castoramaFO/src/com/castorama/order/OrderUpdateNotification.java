package com.castorama.order;


import atg.commerce.fulfillment.*;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class OrderUpdateNotification extends ModifyOrderNotification
{

 public OrderUpdateNotification()
 {
 }

 public void addItemAdd(String s, String s1)
 {
     GenericAdd genericadd = createGenericAdd(1, s1, 4, s);
     addModification(genericadd);
 }

 public void addItemDelete(String s, String s1)
 {
     GenericRemove genericremove = createGenericRemove(1, s1, 4, s);
     addModification(genericremove);
 }

 public void addItemQuantityUpdate(String s, long l)
 {
         GenericUpdate genericupdate = new GenericUpdate();
         genericupdate.setPropertyName("quantity");
         genericupdate.setNewValue(new Long(l));
         genericupdate.setTargetId(s);
         genericupdate.setTargetType(1);
         addModification(genericupdate);
 }

 void addModification(Modification modification)
 {
     Modification amodification[] = getModifications();
     int i = 0;
     if(amodification != null)
         {
         i = amodification.length;
         }
     Vector vector = new Vector(i + 1);
     for(int j = 0; j < i; j++)
         {
         vector.add(j, amodification[j]);
         }

     vector.add(modification);
     setModifications((Modification[])vector.toArray(new Modification[0]));
 }

 public void addOrderStateUpdate(String s, long l)
 {
     GenericUpdate genericupdate = new GenericUpdate();
     genericupdate.setPropertyName("state");
     genericupdate.setNewValue((new Long(l)).toString());
     genericupdate.setTargetId(s);
     genericupdate.setTargetType(4);
     addModification(genericupdate);
 }

 public void addPaymentGroupAdd(String s, String s1)
 {
     GenericAdd genericadd = createGenericAdd(3, s1, 4, s);
     addModification(genericadd);
 }

 public void addPaymentGroupCredit(String s, List list, List list1)
 {
     GenericUpdate genericupdate = new GenericUpdate();
     genericupdate.setPropertyName("creditStatus");
     genericupdate.setOriginalValue((Serializable)list);
     genericupdate.setNewValue((Serializable)list1);
     genericupdate.setTargetId(s);
     genericupdate.setTargetType(3);
     addModification(genericupdate);
 }

 public void addPaymentGroupDelete(String s, String s1)
 {
     GenericRemove genericremove = createGenericRemove(3, s1, 4, s);
     addModification(genericremove);
 }

 public void addPaymentGroupUpdate(String s)
 {
     PaymentGroupUpdate paymentgroupupdate = new PaymentGroupUpdate();
     Vector vector = new Vector(1);
     vector.add(s);
     paymentgroupupdate.setPaymentGroupIds((String[])vector.toArray(new String[0]));
     addModification(paymentgroupupdate);
 }

 public void addShippingGroupAdd(String s, String s1)
 {
     GenericAdd genericadd = createGenericAdd(2, s1, 4, s);
     addModification(genericadd);
 }

 public void addShippingGroupDelete(String s, String s1)
 {
     GenericRemove genericremove = createGenericRemove(2, s1, 4, s);
     addModification(genericremove);
 }

 public void addShippingGroupUpdate(String s)
 {
     ShippingGroupUpdate shippinggroupupdate = new ShippingGroupUpdate();
     Vector vector = new Vector(1);
     vector.add(s);
     shippinggroupupdate.setShippingGroupIds((String[])vector.toArray(new String[0]));
     addModification(shippinggroupupdate);
 }

 protected GenericAdd createGenericAdd(int i, String s, int j, String s1)
 {
     GenericAdd genericadd = new GenericAdd();
     genericadd.setTargetType(i);
     genericadd.setTargetId(s);
     genericadd.setContainerType(j);
     genericadd.setContainerId(s1);
     return genericadd;
 }

 protected GenericRemove createGenericRemove(int i, String s, int j, String s1)
 {
     GenericRemove genericremove = new GenericRemove();
     genericremove.setTargetType(i);
     genericremove.setTargetId(s);
     genericremove.setContainerType(j);
     genericremove.setContainerId(s1);
     return genericremove;
 }

 public void destroy()
 {
     setModifications(null);
 }

 public boolean isEmpty()
 {
     Modification amodification[] = getModifications();
     if(amodification == null)
         {
         return true;
         }
     return amodification.length == 0;
 }

 void removeModifications(Vector vector)
 {
     Modification amodification[] = getModifications();
     if(amodification == null)
         {
         return;
         }
     int i = amodification.length;
     Vector vector1 = new Vector(i);
     for(int j = 0; j < i; j++)
         {
         if(!vector.contains(new Integer(j)))
         {
             vector1.add(j, amodification[j]);
         }
      }
     setModifications((Modification[])vector1.toArray(new Modification[0]));
 }

 public static final String CLASS_VERSION = "$Id: OrderUpdateNotification.java,v 1.7.4.1.2.2 2000/10/24 15:08:10 mrutman Exp $";
}