package com.castorama.login;


import java.beans.PropertyEditor;
import java.sql.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.DateDoodads;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

//Referenced classes of package atg.commerce.csr:
//         RepositoryAttribute

public class RepositorySearcher extends ApplicationLoggingImpl
{

 public RepositorySearcher()
 {
     mDateRanges = new Hashtable();
     init();
     mRepository = null;
 }

 public RepositorySearcher(Repository repository)
 {
     mDateRanges = new Hashtable();
     init();
     setRepository(repository);
 }

 protected String buildSearchByAttributeProperty(RepositoryAttribute repositoryattribute)
 {
     String s = null;
     s = getPropertyName(repositoryattribute.getName());
     return s;
 }

 public Query buildSearchByAttributeQuery(QueryBuilder querybuilder, RepositoryAttribute repositoryattribute)
     throws RepositoryException
 {
     Query query = null;
     try
     {
         atg.repository.QueryExpression queryexpression = null;
         Object obj = null;
         Object obj1 = null;
         queryexpression = querybuilder.createPropertyQueryExpression(buildSearchByAttributeProperty(repositoryattribute));
         obj1 = buildSearchByAttributeValue(repositoryattribute);
         if(obj1 == null)
         {
             query = querybuilder.createIsNullQuery(queryexpression);
         } else
         {
             atg.repository.QueryExpression queryexpression1 = querybuilder.createConstantQueryExpression(obj1);
             query = querybuilder.createComparisonQuery(queryexpression, queryexpression1, repositoryattribute.getQueryBuilderRelation());
         }
     }
     catch(IllegalArgumentException _ex)
     {
         throw new RepositoryException();
     }
     return query;
 }

 protected Object buildSearchByAttributeValue(RepositoryAttribute repositoryattribute)
     throws IllegalArgumentException
 {
     Object obj = null;
     Object obj1 = null;
     Object obj2 = null;
     Object obj3 = null;
     if(repositoryattribute.getValue() == null || repositoryattribute.getValue().equals(""))
     {
         return obj;
     }
     obj = repositoryattribute.getValue();
     if(((String)obj).trim().length() == 0)
     {
         return null;
     }
     try
     {
         RepositoryItemDescriptor repositoryitemdescriptor = getRepositoryItemDescriptor();
         DynamicPropertyDescriptor dynamicpropertydescriptor = repositoryitemdescriptor.getPropertyDescriptor(getPropertyName(repositoryattribute.getName()));
         if(dynamicpropertydescriptor != null)
         {
             if(dynamicpropertydescriptor.getPropertyType() == (java.util.Date.class) || dynamicpropertydescriptor.getPropertyType() == (java.sql.Timestamp.class) || dynamicpropertydescriptor.getPropertyType() == (java.sql.Date.class))
             {
                 java.util.Date date = DateDoodads.stringDate(repositoryattribute.getValue());
                 return new Date(date.getYear(), date.getMonth(), date.getDate());
             }
             PropertyEditor propertyeditor = dynamicpropertydescriptor.createPropertyEditor();
             if(propertyeditor != null)
             {
                 propertyeditor.setAsText(repositoryattribute.getValue());
                 obj = propertyeditor.getValue();
             }
         }
     }
     catch(RepositoryException _ex) 
     { 
         if (isLoggingError())
         {
             logError(_ex);
         }
     }
     return obj;
 }

 public Query buildSearchByDateQuery(QueryBuilder querybuilder)
     throws RepositoryException
 {
     return buildSearchByDateQuery(querybuilder, 8, ((java.util.Date) (getDateConstant())));
 }

 public Query buildSearchByDateQuery(QueryBuilder querybuilder, int i, java.util.Date date)
     throws RepositoryException
 {
     Object obj = null;
     atg.repository.QueryExpression queryexpression = querybuilder.createPropertyQueryExpression(getDatePropertyName());
     atg.repository.QueryExpression queryexpression1 = querybuilder.createConstantQueryExpression(date);
     return querybuilder.createComparisonQuery(queryexpression, queryexpression1, i);
 }

 public Date getDateConstant()
     throws RepositoryException
 {
     Date date = new Date(System.currentTimeMillis());
     if(getDateRange().compareTo((String)mDateRanges.get("year")) == 0)
         {
         return new Date(date.getYear() - 1, date.getMonth(), date.getDate());
         }
     if(getDateRange().compareTo((String)mDateRanges.get("sixMonths")) == 0)
         {
         return new Date(date.getYear(), date.getMonth() - 6, date.getDate());
         }
     if(getDateRange().compareTo((String)mDateRanges.get("threeMonths")) == 0)
         {
         return new Date(date.getYear(), date.getMonth() - 3, date.getDate());
         }
     if(getDateRange().compareTo((String)mDateRanges.get("oneMonth")) == 0)
         {
         return new Date(date.getYear(), date.getMonth() - 1, date.getDate());
         }
     if(getDateRange().compareTo((String)mDateRanges.get("oneMonth")) == 0)
         {
         return new Date(date.getYear(), date.getMonth(), date.getDate() - 7);
         }
     String s = getDatePropertyValue();
     if(s == null || s.equals(""))
         {
         return null;
         }
     try
     {
         java.util.Date date1 = DateDoodads.stringDate(s);
         return new Date(date1.getYear(), date1.getMonth(), date1.getDate());
     }
     catch(IllegalArgumentException _ex)
     {
         throw new RepositoryException();
     }
 }

 public String getDatePropertyName()
 {
     return mDatePropertyName;
 }

 public String getDatePropertyValue()
 {
     return mDatePropertyValue;
 }

 public String getDateRange()
 {
     return mDateRange;
 }

 public String[] getDateRanges()
 {
     return (String[])mDateRanges.values().toArray(new String[0]);
 }

 public PropertyEditor getPropertyEditor(String s)
 {
     try
     {
         RepositoryItemDescriptor repositoryitemdescriptor = getRepositoryItemDescriptor();
         String as[] = repositoryitemdescriptor.getPropertyNames();
         if(as != null)
         {
             for(int i = 0; i < as.length; i++)
             {
                 DynamicPropertyDescriptor dynamicpropertydescriptor = repositoryitemdescriptor.getPropertyDescriptor(as[i]);
                 if(dynamicpropertydescriptor != null && s.compareTo(dynamicpropertydescriptor.getDisplayName()) == 0)
                     {
                     return dynamicpropertydescriptor.createPropertyEditor();
                     }
             }

         }
     }
     catch(RepositoryException _ex) 
     { 
         if (isLoggingError())
         {
             logError(_ex);
         }
     }
     return null;
 }

 protected String getPropertyName(String s)
 {
     String s1 = s;
     try
     {
         RepositoryItemDescriptor repositoryitemdescriptor = getRepositoryItemDescriptor();
         String as[] = repositoryitemdescriptor.getPropertyNames();
         if(as != null)
         {
             for(int i = 0; i < as.length; i++)
             {
                 DynamicPropertyDescriptor dynamicpropertydescriptor = repositoryitemdescriptor.getPropertyDescriptor(as[i]);
                 if(dynamicpropertydescriptor != null && s.compareTo(dynamicpropertydescriptor.getDisplayName()) == 0)
                     {
                     return dynamicpropertydescriptor.getName();
                     }
             }

         }
     }
     catch(RepositoryException _ex) 
     { 
         if (isLoggingError())
         {
             logError(_ex);
         }
     }
     return s1;
 }

 public Query getQuery()
 {
     return mQuery;
 }

 public QueryBuilder getQueryBuilder()
     throws RepositoryException
 {
     RepositoryView repositoryview = getRepositoryView();
     return repositoryview.getQueryBuilder();
 }

 public Repository getRepository()
 {
     return mRepository;
 }

 protected RepositoryItemDescriptor getRepositoryItemDescriptor()
     throws RepositoryException
 {
     RepositoryView repositoryview = getRepositoryView();
     return repositoryview.getItemDescriptor();
 }

 protected RepositoryView getRepositoryView()
     throws RepositoryException
 {
     Object obj = null;
     Object obj1 = null;
     if(mRepository == null)
     {
         throw new RepositoryException();
     } else
     {
         String s = getRepositoryViewName();
         RepositoryView repositoryview = mRepository.getView(s);
         return repositoryview;
     }
 }

 protected String getRepositoryViewName()
     throws RepositoryException
 {
     if(mRepository == null)
         {
         throw new RepositoryException();
         }
     else
     {
         return mRepository.getDefaultViewName();
     }
 }

 protected void init()
 {
     if(!mDateRanges.containsKey("week"))
         {
         mDateRanges.put("week", sResourceBundle.getString("week"));
         }
     if(!mDateRanges.containsKey("oneMonth"))
         {
         mDateRanges.put("oneMonth", sResourceBundle.getString("oneMonth"));
         }
     if(!mDateRanges.containsKey("threeMonths"))
     {
         mDateRanges.put("threeMonths", sResourceBundle.getString("threeMonths"));
     }
     if(!mDateRanges.containsKey("sixMonths"))
         {
         mDateRanges.put("sixMonths", sResourceBundle.getString("sixMonths"));
         }
     if(!mDateRanges.containsKey("year"))
     {
         mDateRanges.put("year", sResourceBundle.getString("year"));
     }
     if(!mDateRanges.containsKey("since"))
     {
         mDateRanges.put("since", sResourceBundle.getString("since"));
     }
 }

 public void setDatePropertyName(String s)
 {
     mDatePropertyName = s;
 }

 public void setDatePropertyValue(String s)
 {
     mDatePropertyValue = s;
 }

 public void setDateRange(String s)
 {
     mDateRange = s;
 }

 public void setQuery(Query query)
 {
     mQuery = query;
 }

 public void setRepository(Repository repository)
 {
     mRepository = repository;
 }

 public static final String CLASS_VERSION = "$Id: RepositorySearcher.java,v 1.2 2000/07/18 19:12:14 mrutman Exp $";
 public static final String DATE_RANGE_WEEK = "week";
 public static final String DATE_RANGE_ONE_MONTH = "oneMonth";
 public static final String DATE_RANGE_THREE_MONTHS = "threeMonths";
 public static final String DATE_RANGE_SIX_MONTHS = "sixMonths";
 public static final String DATE_RANGE_YEAR = "year";
 public static final String DATE_RANGE_SINCE = "since";
 protected static final String SEARCHFORMRESOURCES = "atg.repository.servlet.RepositoryServletResources";
 protected static ResourceBundle sResourceBundle = ResourceBundle.getBundle("atg.repository.servlet.RepositoryServletResources", Locale.getDefault());
 Repository mRepository;
 Query mQuery;
 String mDatePropertyName;
 String mDatePropertyValue;
 Hashtable mDateRanges;
 String mDateRange;

}