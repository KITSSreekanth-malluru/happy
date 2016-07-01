package com.castorama.order;


import atg.commerce.CommerceException;

public class InvalidParameterException extends CommerceException
{

 public InvalidParameterException()
 {
 }

 public InvalidParameterException(String s)
 {
     super(s);
 }

 public InvalidParameterException(String s, Throwable throwable)
 {
     super(s, throwable);
 }

 public InvalidParameterException(Throwable throwable)
 {
     super(throwable);
 }

 public static final String CLASS_VERSION = "$Id: InvalidParameterException.java,v 1.1 1999/12/03 23:43:32 mannyp Exp $";
}