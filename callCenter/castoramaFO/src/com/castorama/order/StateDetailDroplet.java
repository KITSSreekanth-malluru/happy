package com.castorama.order;


import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.states.ObjectStates;
import atg.core.util.ResourceUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

//Referenced classes of package atg.commerce.order:
//         InvalidParameterException

public class StateDetailDroplet extends DynamoServlet
{

 public StateDetailDroplet()
 {
 }

 public String format(String s)
 {
     return getResourceBundle(getDefaultLocale(), getResourceBundleLocation()).getString(s);
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

 public ResourceBundle getResourceBundle(Locale locale, String s)
     throws MissingResourceException
 {
     return ResourceUtils.getBundle(s, locale);
 }

 public String getResourceBundleLocation()
 {
     return mResourceBundleLocation;
 }

 String getStateDetail(int i)
     throws CommerceException
 {
     ObjectStates objectstates = getStates();
     if(objectstates == null)
     {
         throw new InvalidParameterException();
     }
     try
     {
         return format(objectstates.getStateString(i));
     }
     catch(MissingResourceException missingresourceexception)
     {
         if(isLoggingError())
             {
             logError(missingresourceexception);
             }
     }
     return null;
 }

 public ObjectStates getStates()
 {
     return mStates;
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     ObjectStates objectstates = getStates();
     if(objectstates == null)
     {
         throw new ServletException("No States Defined");
     }
     try
     {
         String s = dynamohttpservletrequest.getParameter(STATE);
         String s1 = getStateDetail(Integer.parseInt(s));
         dynamohttpservletrequest.setParameter("detailedState", s1);
         dynamohttpservletrequest.serviceLocalParameter(OUTPUT, dynamohttpservletrequest, dynamohttpservletresponse);
     }
     catch(CommerceException commerceexception)
     {
         if(isLoggingError())
             {
             logError(commerceexception);
             }
         dynamohttpservletrequest.serviceLocalParameter(ERROR, dynamohttpservletrequest, dynamohttpservletresponse);
     }
 }

 public void setDefaultLocale(Locale locale)
 {
     mDefaultLocale = locale;
 }

 public void setResourceBundleLocation(String s)
 {
     mResourceBundleLocation = s;
 }

 public void setStates(ObjectStates objectstates)
 {
     mStates = objectstates;
 }

 public static final String CLASS_VERSION = "$Id: StateDetailDroplet.java,v 1.1 2000/06/06 20:46:56 cynthia Exp $";
 public static final ParameterName STATE = ParameterName.getParameterName("state");
 public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
 public static final ParameterName ERROR = ParameterName.getParameterName("error");
 static final String DETAILED_STATE = "detailedState";
 Locale mDefaultLocale;
 String mResourceBundleLocation;
 ObjectStates mStates;

}