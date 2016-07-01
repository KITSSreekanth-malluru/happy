package com.castorama.order;



import atg.commerce.CommerceException;

public class ShippingGroupNotFoundException extends CommerceException
{

 public ShippingGroupNotFoundException()
 {
 }

 public ShippingGroupNotFoundException(String s)
 {
     super(s);
 }

 public ShippingGroupNotFoundException(String s, Throwable throwable)
 {
     super(s, throwable);
 }

 public ShippingGroupNotFoundException(Throwable throwable)
 {
     super(throwable);
 }

 public static final String CLASS_VERSION = "$Id: ShippingGroupNotFoundException.java,v 1.2 1999/11/18 19:11:48 mannyp Exp $";
}