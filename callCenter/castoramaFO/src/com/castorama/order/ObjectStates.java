package com.castorama.order;

//Decompiled by DJ v3.5.5.77 Copyright 2003 Atanas Neshkov  Date: 31/10/2006 14:04:57
//Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
//Decompiler options: packimports(3) 
//Source File Name:   ObjectStates.java


import java.util.*;

public class ObjectStates
{

 public ObjectStates()
 {
     mStateNameToIntValue = new HashMap();
     mStateValueToDescription = new HashMap();
     mIntValueToStringValue = new HashMap();
     mStringValueToIntValue = new HashMap();
     mStateValueMap = null;
     mStateStringMap = null;
     mStateDescriptionMap = null;
 }

 public String getStateDescription(int i)
 {
     String s = (String)mStateValueToDescription.get((new Integer(i)).toString());
     return s;
 }

 public String getStateDescription(String s)
 {
     return getStateDescription(getStateFromString(s));
 }

 public Properties getStateDescriptionMap()
 {
     return mStateDescriptionMap;
 }

 public int getStateFromString(String s)
 {
     String s1 = (String)mStringValueToIntValue.get(s);
     return Integer.parseInt(s1);
 }

 public String getStateString(int i)
 {
     String s = (String)mIntValueToStringValue.get((new Integer(i)).toString());
     return s;
 }

 public Properties getStateStringMap()
 {
     return mStateStringMap;
 }

 public int getStateValue(String s)
 {
     String s1 = (String)mStateNameToIntValue.get(s);
     return Integer.parseInt(s1);
 }

 public Properties getStateValueMap()
 {
     return mStateValueMap;
 }

 public void setStateDescriptionMap(Properties properties)
 {
     mStateDescriptionMap = properties;
     Enumeration enumeration = properties.propertyNames();
     Object obj = null;
     String s;
     for(; enumeration.hasMoreElements(); mStateValueToDescription.put(s, properties.getProperty(s)))
         {
         s = (String)enumeration.nextElement();
         }

 }

 public void setStateStringMap(Properties properties)
 {
     mStateStringMap = properties;
     Enumeration enumeration = properties.propertyNames();
     Object obj = null;
     Object obj1 = null;
     String s;
     String s1;
     for(; enumeration.hasMoreElements(); mStringValueToIntValue.put(s1, s))
     {
         s = (String)enumeration.nextElement();
         s1 = properties.getProperty(s);
         mIntValueToStringValue.put(s, s1);
     }

 }

 public void setStateValueMap(Properties properties)
 {
     mStateValueMap = properties;
     Enumeration enumeration = properties.propertyNames();
     Object obj = null;
     String s;
     for(; enumeration.hasMoreElements(); mStateNameToIntValue.put(s, properties.getProperty(s)))
     {
         s = (String)enumeration.nextElement();
     }

 }

 public static final String CLASS_VERSION = "$Id: ObjectStates.java,v 1.1.10.2 2000/10/16 22:33:08 sperman Exp $";
 private HashMap mStateNameToIntValue;
 private HashMap mStateValueToDescription;
 private HashMap mIntValueToStringValue;
 private HashMap mStringValueToIntValue;
 private Properties mStateValueMap;
 private Properties mStateStringMap;
 private Properties mStateDescriptionMap;
}