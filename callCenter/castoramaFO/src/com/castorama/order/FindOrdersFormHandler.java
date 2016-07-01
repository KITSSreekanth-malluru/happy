package com.castorama.order;




import java.io.IOException;
import java.sql.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderUserMessage;
import atg.commerce.order.SimpleOrderManager;
import atg.core.util.DateDoodads;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

public class FindOrdersFormHandler extends GenericFormHandler
 implements Servlet
{
 /* member class not found */
 class DateRange {}


 public FindOrdersFormHandler()
 {
     mOrderIds = null;
     mDateRanges = new Hashtable();
     mUseRequestLocale = true;
 }

 public void destroy()
 {
 }

 public String generatePropertyPath(String s)
 {
     return getAbsoluteName() + "." + s;
 }

 public Date getDateConstant(String s, String s1)
     throws CommerceException
 {
     if((s == null) & (s.trim().length() == 0))
         {
         throw new InvalidParameterException();
         }
     if(s.compareTo(sResourceBundle.getString("since")) == 0)
     {
         if(s1 == null || s1.trim().length() == 0)
         {
             return null;
         }
         try
         {
             java.util.Date date = DateDoodads.stringDate(s1);
             return new Date(date.getYear(), date.getMonth(), date.getDate());
         }
         catch(IllegalArgumentException _ex)
         {
             throw new InvalidParameterException();
         }
     }
     Date date1 = new Date(System.currentTimeMillis());
     Object obj = getDateRanges().get(s);
     if(obj != null && (obj instanceof DateRange))
     {
         return ((com.castorama.order.DateRange)obj).getDateBoundary(date1);
     }
     if(isLoggingWarning())
         {
         logWarning("Unknown Date Range [" + s + "] -- using today instead");
         }
     return date1;
 }

 protected Hashtable getDateRanges()
 {
     if(mDateRanges.isEmpty())
     {
         mDateRanges.put(sResourceBundle.getString("week"), new com.castorama.order.DateRange(0, 0, 7, sResourceBundle.getString("week")));
         mDateRanges.put(sResourceBundle.getString("oneMonth"), new com.castorama.order.DateRange(0, 1, 0, sResourceBundle.getString("oneMonth")));
         mDateRanges.put(sResourceBundle.getString("threeMonths"), new com.castorama.order.DateRange(0, 3, 0, sResourceBundle.getString("threeMonths")));
         mDateRanges.put(sResourceBundle.getString("sixMonths"), new com.castorama.order.DateRange(0, 6, 0, sResourceBundle.getString("sixMonths")));
         mDateRanges.put(sResourceBundle.getString("year"), new com.castorama.order.DateRange(1, 0, 0, sResourceBundle.getString("year")));
         mDateRanges.put(sResourceBundle.getString("since"), new com.castorama.order.DateRange(0, 0, 0, sResourceBundle.getString("since")));
     }
     return mDateRanges;
 }

 public String[] getDateRangesAsStrings()
 {
     return (String[])getDateRanges().keySet().toArray(new String[0]);
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

 public String getFromDate()
 {
     return mFromDate;
 }

 public String getFromDateRange()
 {
     return mFromDateRange;
 }

 public String getGeneralFailureURL()
 {
     return mGeneralFailureURL;
 }

 public String getGeneralSuccessURL()
 {
     return mGeneralSuccessURL;
 }

 public String getLogin()
 {
     return mLogin;
 }

 public Order getOrder()
 {
     return mOrder;
 }

 public String getOrderId()
 {
     return mOrderId;
 }

 public List getOrderIds()
 {
     return mOrderIds;
 }

 public SimpleOrderManager getOrderManager()
 {
     return mOrderManager;
 }

 protected String getProfileIdFromLogin(String s)
     throws CommerceException
 {
     if(mProfileTools == null)
         {
         throw new InvalidParameterException();
         }
     if(s == null || s.trim().length() == 0)
         {
         throw new InvalidParameterException();
         }
     Profile profile = new Profile();
     if(mProfileTools.locateUserFromLogin(s, profile))
         {
         return profile.getRepositoryId();
         }
     else
         {
         return null;
         }
 }

 public ProfileTools getProfileTools()
 {
     return mProfileTools;
 }

 public String getSearchType()
 {
     return mSearchType;
 }

 public ServletConfig getServletConfig()
 {
     return null;
 }

 public String getServletInfo()
 {
     return "";
 }

 public String getToDate()
 {
     return mToDate;
 }

 public String getToDateRange()
 {
     return mToDateRange;
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

 public boolean handleSearchByDateRange(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     SimpleOrderManager simpleordermanager = getOrderManager();
     if(simpleordermanager == null)
     {
         String s = OrderUserMessage.format("noOrderManager", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = getToDate();
     String s2 = getFromDate();
     String s3 = getFromDateRange();
     String s4 = getToDateRange();
     if(s1 != null)
     {
         s1.trim();
     }
     if(s2 != null)
         {
         s2.trim();
         }
     if(s4 != null)
         {
         s4.trim();
         }
     if(s3 != null)
         {
         s3.trim();
         }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
         {
         return false;
         }
     Date date = null;
     Date date1 = null;
     try
     {
         date = getDateConstant(s3, s2);
         date1 = getDateConstant(s4, s1);
         if(date1 == null)
         {
             String s5 = OrderUserMessage.format("noToDate", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s10 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s5, s10, "noToDate"));
         }
         if(date == null)
         {
             String s6 = OrderUserMessage.format("noFromDate", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s11 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s6, s11, "noFromDate"));
         }
         if(date1 != null && date != null && date1.equals(date))
         {
             date = new Date(date1.getYear(), date1.getMonth(), date1.getDate() - 1);
         }
         if(date1 != null && date != null && date.after(date1))
         {
             String s7 = OrderUserMessage.format("fromIsBiggerThanTo", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s12 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s7, s12, "fromIsBiggerThanTo"));
         }
     }
     catch(CommerceException _ex)
     {
         String s8 = OrderUserMessage.format("errorDateFormat", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s13 = generatePropertyPath("dateRange");
         addFormException(new DropletFormException(s8, s13, "errorDateFormat"));
     }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
         {
         return false;
         }
     try
     {
         List list = simpleordermanager.getOrderIdsWithinDateRange(date, date1);
         setOrderIds(list);
     }
     catch(CommerceException _ex)
     {
         String s9 = OrderUserMessage.format("errorSearching", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s14 = generatePropertyPath("dateRange");
         addFormException(new DropletFormException(s9, s14, "errorSearching"));
     }
     return checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public boolean handleSearchByOrderId(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     SimpleOrderManager simpleordermanager = getOrderManager();
     if(simpleordermanager == null)
     {
         String s = OrderUserMessage.format("noOrderManager", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = getOrderId();
     if(s1 == null || s1.trim().length() == 0)
     {
         String s2 = OrderUserMessage.format("noOrderId", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s3 = generatePropertyPath("id");
         addFormException(new DropletFormException(s2, s3, "noOrderId"));
     }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
         {
         return false;
         }
     try
     {
         if(s1 != null)
         {
             s1 = s1.trim();
         }
        setOrder(simpleordermanager.loadOrder(s1));
        Repository repository = getRepository();
        mOrderPourAffichage = repository.getItem(s1);               
     }
     catch(RepositoryException _ex)
     {
         if(isLoggingDebug())
             {
             logDebug("Order Does Not Exist");
             }
         setOrder(null);
         mOrderPourAffichage = null;               
     }
     catch(CommerceException _ex)
     {
         if(isLoggingDebug())
             {
             logDebug("Order Does Not Exist");
             }
         setOrder(null);
         mOrderPourAffichage = null;               
     }
     return checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public boolean handleSearchByProfileId(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     List listeOrders = null;
     SimpleOrderManager simpleordermanager = getOrderManager();
     if(simpleordermanager == null)
     {
         String s = OrderUserMessage.format("noOrderManager", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = null;
     String s2 = getLogin();
     try
     {
         s1 = getProfileIdFromLogin(s2.trim());
         if(s1 == null || s1.trim().length() == 0)
         {
             String s3 = OrderUserMessage.format("noUserProfile", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s6 = generatePropertyPath(".id");
             addFormException(new DropletFormException(s3, s6, "noUserProfile"));
         }
     }
     catch(CommerceException _ex)
     {
         String s4 = OrderUserMessage.format("noUserProfile", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s7 = generatePropertyPath("id");
         addFormException(new DropletFormException(s4, s7, "noUserProfile"));
     }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
     {
         return false;
     }
     try
     {
        
         setOrderIds(simpleordermanager.getOrderIdsForProfile(s1));         
        
     }
     catch(CommerceException _ex)
     {
         String s5 = OrderUserMessage.format("errorSearching", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s8 = generatePropertyPath("profileId");
         addFormException(new DropletFormException(s5, s8, "errorSearching"));
     }
    
     return checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public boolean handleSearchByProfileIdAndDateRange(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     SimpleOrderManager simpleordermanager = getOrderManager();
     if(simpleordermanager == null)
     {
         String s = OrderUserMessage.format("noOrderManager", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = getToDate();
     String s2 = getFromDate();
     String s3 = getFromDateRange();
     String s4 = getToDateRange();
     if(s1 != null)
         {
         s1.trim();
         }
     if(s2 != null)
         {
         s2.trim();
         }
     if(s4 != null)
         {
         s4.trim();
         }
     if(s3 != null)
         {
         s3.trim();
         }
     String s5 = null;
     try
     {
         String s6 = getLogin();
         if(s6 == null || s6.trim().length() == 0)
         {
             return handleSearchByDateRange(dynamohttpservletrequest, dynamohttpservletresponse);
         }
         s5 = getProfileIdFromLogin(s6);
         if(s5 == null || s5.trim().length() == 0)
             {
             return checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
             }
     }
     catch(CommerceException _ex)
     {
         String s7 = OrderUserMessage.format("errorSearching", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s8 = generatePropertyPath("dateRange");
         addFormException(new DropletFormException(s7, s8, "errorSearching"));
     }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
     {
         return false;
     }
     Date date = null;
     Date date1 = null;
     try
     {
         date = getDateConstant(s3, s2);
         date1 = getDateConstant(s4, s1);
         if(date1 == null)
         {
             String s9 = OrderUserMessage.format("noToDate", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s13 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s9, s13, "noToDate"));
         }
         if(date == null)
         {
             String s10 = OrderUserMessage.format("noFromDate", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s14 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s10, s14, "noFromDate"));
         }
         if(date1 != null && date != null && date1.equals(date))
         {
             date = new Date(date1.getYear(), date1.getMonth(), date1.getDate() - 1);
         }
         if(date1 != null && date != null && date.after(date1))
         {
             String s11 = OrderUserMessage.format("fromIsBiggerThanTo", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
             String s15 = generatePropertyPath("dateRange");
             addFormException(new DropletFormException(s11, s15, "fromIsBiggerThanTo"));
         }
     }
     catch(CommerceException _ex)
     {
         String s12 = OrderUserMessage.format("errorDateFormat", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s16 = generatePropertyPath("dateRange");
         addFormException(new DropletFormException(s12, s16, "errorDateFormat"));
     }
     if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
         {
         return false;
         }
     try
     {
         if(isLoggingDebug())
         {
             logDebug("Search for Orders owned by " + s5 + " between " + date + " and " + date1);
         }
         List list = simpleordermanager.getOrderIdsForProfileWithinDateRange(s5, date, date1);
         setOrderIds(list);
     }
     catch(CommerceException commerceexception)
     {
         if(isLoggingError())
             {
             logError(commerceexception);
             }
         String s17 = OrderUserMessage.format("errorSearching", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         String s18 = generatePropertyPath("dateRangeProfile");
         addFormException(new DropletFormException(s17, s18, "errorSearching"));
     }
     return checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public void init(ServletConfig servletconfig)
 {
 }

 public boolean isUseRequestLocale()
 {
     return mUseRequestLocale;
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     boolean flag = false;
     SimpleOrderManager simpleordermanager = getOrderManager();
     if(simpleordermanager == null)
     {
         String s = OrderUserMessage.format("noOrderManager", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
         throw new ServletException(s);
     }
     String s1 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("searchType"));
     if(s1 == null)
     {
         s1 = getSearchType();
     }
     if(s1 != null)
         {
         s1.trim();
         }
     if(isLoggingDebug())
         {
         logDebug("Search: " + s1);
         }
     if(s1.compareTo("byProfileLogin") == 0)
         {
         try
         
         {
             String s2 = getProfileIdFromLogin(dynamohttpservletrequest.getParameter(ParameterName.getParameterName("login")));
             if(s2 == null || s2.trim().length() == 0)
                 {
                 s2 = getProfileIdFromLogin(getLogin());
                 }
             setOrderIds(simpleordermanager.getOrderIdsForProfile(s2));
         }
         catch(CommerceException commerceexception)
         {
             if(isLoggingError())
             {
                 logError(commerceexception);
             }
             flag = true;
         }
         }
     else
     if(s1.compareTo("byDateRange") == 0)
     {
         String s3 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("fromDate"));
         if(s3 == null || s3.trim().length() == 0)
             {
             s3 = getFromDate();
             }
         String s6 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("toDate"));
         if(s6 == null || s6.trim().length() == 0)
             {
             s6 = getToDate();
             }
         String s8 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("fromDateRange"));
         if(s8 == null || s8.trim().length() == 0)
         {
             s8 = getFromDateRange();
         }
         String s10 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("toDateRange"));
         if(s10 == null || s10.trim().length() == 0)
         {
             s10 = getToDateRange();
         }
         try
         {
             Date date = getDateConstant(s8, s3);
             Date date1 = getDateConstant(s10, s6);
             if(date1 == null)
             {
                 date1 = new Date(System.currentTimeMillis());
             }
             if(date == null)
             {
                 date = new Date(System.currentTimeMillis());
             }
             if(date1 != null && date != null && date1.equals(date))
             {
                 date = new Date(date1.getYear(), date1.getMonth(), date1.getDate() - 1);
             }
             if(date.after(date1))
             {
                 String s14 = OrderUserMessage.format("fromIsBiggerThanTo", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
                 if(isLoggingError())
                 {
                     logError(s14);
                 }
                 flag = true;
             }
             List list = simpleordermanager.getOrderIdsWithinDateRange(date, date1);
             setOrderIds(list);
         }
         catch(CommerceException commerceexception1)
         {
             if(isLoggingError())
             {
                 logError(commerceexception1);
             }
             flag = true;
         }
     } else
     if(s1.compareTo("byProfileAndDateRange") == 0)
     {
         String s4 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("fromDate"));
         if(s4 == null || s4.trim().length() == 0)
         {
             s4 = getFromDate();
         }
         String s7 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("toDate"));
         if(s7 == null || s7.trim().length() == 0)
         {
             s7 = getToDate();
         }
         String s9 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("fromDateRange"));
         if(s9 == null || s9.trim().length() == 0)
         {
             s9 = getFromDateRange();
         }
         String s11 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("toDateRange"));
         if(s11 == null || s11.trim().length() == 0)
         {
             s11 = getToDateRange();
         }
         String s12 = dynamohttpservletrequest.getParameter(ParameterName.getParameterName("login"));
         if((s4 == null || s4.trim().length() == 0) && (s9 == null || s9.trim().length() == 0))
         {
             try
             {
                 String s13 = getProfileIdFromLogin(s12);
                 setOrderIds(simpleordermanager.getOrderIdsForProfile(s13));
             }
             catch(CommerceException commerceexception2)
             {
                 if(isLoggingError())
                     {
                     logError(commerceexception2);
                     }
                 flag = true;
             }
         }
         else
         
         if(s12 == null || s12.trim().length() == 0)
         {
             try
             {
                 Date date2 = getDateConstant(s9, s4);
                 Date date4 = getDateConstant(s11, s7);
                 if(date4 == null)
                     {
                     date4 = new Date(System.currentTimeMillis());
                     }
                 if(date2 == null)
                 {
                     date2 = new Date(System.currentTimeMillis());
                 }
                 if(date4 != null && date2 != null && date4.equals(date2))
                     {
                     date2 = new Date(date4.getYear(), date4.getMonth(), date4.getDate() - 1);
                     }
                 if(date2.after(date4))
                 {
                     String s15 = OrderUserMessage.format("fromIsBiggerThanTo", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
                     if(isLoggingError())
                         {
                         logError(s15);
                         }
                     flag = true;
                 }
                 List list1 = simpleordermanager.getOrderIdsWithinDateRange(date2, date4);
                 setOrderIds(list1);
             }
             catch(CommerceException commerceexception3)
             {
                 if(isLoggingError())
                     {
                     logError(commerceexception3);
                     }
                 flag = true;
             }
         }
         
         else
         {
             try
             {
                 Date date3 = getDateConstant(s9, s4);
                 Date date5 = getDateConstant(s11, s7);
                 String s16 = getProfileIdFromLogin(s12);
                 if(date5 == null)
                     {
                     date5 = new Date(System.currentTimeMillis());
                     }
                 if(date3 == null)
                     {
                     date3 = new Date(System.currentTimeMillis());
                     }
                 if(date5 != null && date3 != null && date5.equals(date3))
                 {
                     date3 = new Date(date5.getYear(), date5.getMonth(), date5.getDate() - 1);
                 }
                 if(date3.after(date5))
                 {
                     String s17 = OrderUserMessage.format("fromIsBiggerThanTo", getUserLocale(dynamohttpservletrequest, dynamohttpservletresponse));
                     if(isLoggingError())
                     {
                         logError(s17); 
                     }
                        
                     flag = true;
                 }
                 List list2 = simpleordermanager.getOrderIdsForProfileWithinDateRange(s16, date3, date5);
                 setOrderIds(list2);
             }
             catch(CommerceException commerceexception4)
             {
                 if(isLoggingError())
                     {
                     logError(commerceexception4);
                     }
                 flag = true;
             }
         }
     } else
     {
         String s5 = dynamohttpservletrequest.getParameter("orderId");
         if(s5 == null)
         {
             s5 = getOrderId();
         }
         try
         {
             setOrder(simpleordermanager.loadOrder(s5));
             Repository repositoryOrder = getRepository();
             mOrderPourAffichage = repositoryOrder.getItem(s5);                          
         }
         catch(RepositoryException _ex)
         {
             logError("EXECPTION : "+ _ex);
             flag = true;
         }
         catch(CommerceException _ex2)
         {
             logError("EXECPTION : "+ _ex2);
             flag = true;
         }
         catch(ClassCastException _ex3)
         {
             logError("EXECPTION : "+ _ex3);
             flag = true;
         }
     }
     if(flag)
         {
         dynamohttpservletrequest.serviceLocalParameter("error", dynamohttpservletrequest, dynamohttpservletresponse);
         }
     else
     {
         dynamohttpservletrequest.setParameter("laCommandeEnCours", mOrderPourAffichage);
         dynamohttpservletrequest.serviceLocalParameter("output", dynamohttpservletrequest, dynamohttpservletresponse);
     }
 }

 public void service(ServletRequest servletrequest, ServletResponse servletresponse)
     throws ServletException, IOException
 {
     DynamoHttpServletRequest dynamohttpservletrequest;
     DynamoHttpServletResponse dynamohttpservletresponse;
     try
     {
         dynamohttpservletrequest = (DynamoHttpServletRequest)servletrequest;
         dynamohttpservletresponse = (DynamoHttpServletResponse)servletresponse;
     }
     catch(ClassCastException _ex)
     {
         return;
     }
     service(dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public void setDefaultLocale(Locale locale)
 {
     mDefaultLocale = locale;
 }

 public void setFromDate(String s)
 {
     mFromDate = s;
 }

 public void setFromDateRange(String s)
 {
     mFromDateRange = s;
 }

 public void setGeneralFailureURL(String s)
 {
     mGeneralFailureURL = s;
 }

 public void setGeneralSuccessURL(String s)
 {
     mGeneralSuccessURL = s;
 }

 public void setLogin(String s)
 {
     mLogin = s;
 }

 public void setOrder(Order order)
 {
     mOrder = order;
 }

 public void setOrderId(String s)
 {
     mOrderId = s;
 }

 public void setOrderIds(List list)
 {
     mOrderIds = list;
 }

 public void setOrderManager(SimpleOrderManager simpleordermanager)
 {
     mOrderManager = simpleordermanager;
 }

 public void setProfileTools(ProfileTools profiletools)
 {
     mProfileTools = profiletools;
 }

 public void setSearchType(String s)
 {
     mSearchType = s;
 }

 public void setToDate(String s)
 {
     mToDate = s;
 }

 public void setToDateRange(String s)
 {
     mToDateRange = s;
 }

 public void setUseRequestLocale(boolean flag)
 {
     mUseRequestLocale = flag;
 }

 public static final String CLASS_VERSION = "$Id: FindOrdersFormHandler.java,v 1.23.2.1.4.3 2000/10/31 17:45:03 mrutman Exp $";
 public static final String DATE_RANGE_WEEK = "week";
 public static final String DATE_RANGE_ONE_MONTH = "oneMonth";
 public static final String DATE_RANGE_THREE_MONTHS = "threeMonths";
 public static final String DATE_RANGE_SIX_MONTHS = "sixMonths";
 public static final String DATE_RANGE_YEAR = "year";
 public static final String DATE_RANGE_SINCE = "since";
 public static final String SEARCH_TYPE = "searchType";
 public static final String ORDER_ID = "orderId";
 public static final String PROFILE_LOGIN = "login";
 public static final String DATE_FROM = "fromDate";
 public static final String DATE_TO = "toDate";
 public static final String DATE_RANGE_FROM = "fromDateRange";
 public static final String DATE_RANGE_TO = "toDateRange";
 static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
 public static final String BY_ORDER_ID = "byOrderId";
 public static final String BY_PROFILE_LOGIN = "byProfileLogin";
 public static final String BY_DATE_RANGE = "byDateRange";
 public static final String BY_PROFILE_AND_DATE_RANGE = "byProfileAndDateRange";
 public static final String MSG_NO_ORDER_ID = "noOrderId";
 public static final String MSG_NO_ORDER_MANAGER = "noOrderManager";
 public static final String MSG_NO_USER_PROFILE = "noUserProfile";
 public static final String MSG_FROM_DATE = "noFromDate";
 public static final String MSG_TO_DATE = "noToDate";
 public static final String MSG_FROM_AFTER_TO = "fromIsBiggerThanTo";
 public static final String MSG_ERROR_SEARCHING = "errorSearching";
 public static final String MSG_ERROR_DATE_FORMAT = "errorDateFormat";
 public static final String OUTPUT = "output";
 public static final String ERROR = "error";
 protected static final String SEARCHFORMRESOURCES = "atg.repository.servlet.RepositoryServletResources";
 protected static ResourceBundle sResourceBundle = ResourceBundle.getBundle("atg.repository.servlet.RepositoryServletResources", Locale.getDefault());
 SimpleOrderManager mOrderManager;
 public Order mOrder;
 String mOrderId;
 public List mOrderIds;
 String mSearchType;
 String mLogin;
 Hashtable mDateRanges;
 String mFromDate;
 String mToDate;
 String mToDateRange;
 String mFromDateRange;
 ProfileTools mProfileTools;
 String mGeneralFailureURL;
 String mGeneralSuccessURL;
 boolean mUseRequestLocale;
 Locale mDefaultLocale;
 Repository mRepository;
 public RepositoryItem mOrderPourAffichage;

 public RepositoryItem getOrderPourAffichage()
{
    return mOrderPourAffichage;
}

public void setOrderPourAffichage(RepositoryItem a_orderPourAffichage)
{
    mOrderPourAffichage = a_orderPourAffichage;
}

public Repository getRepository()
{
    return mRepository;
}

public void setRepository(Repository a_repository)
{
    mRepository = a_repository;
}

}