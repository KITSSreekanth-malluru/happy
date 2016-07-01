package com.castorama.login;


import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import atg.droplet.GenericFormHandler;
import atg.repository.Query;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

//Referenced classes of package atg.commerce.csr:
//         RepositoryAttribute, RepositorySearcher

public class RepositorySearchForm extends GenericFormHandler
 implements Servlet
{

 public RepositorySearchForm()
 {
     mRepositorySearcher = new RepositorySearcher();
     mAttribute = new RepositoryAttribute();
 }

 protected boolean checkFormError(String s, String s1, DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     if(getFormError())
     {
         String s2 = s;
         if(s2 != null)
         {
             if(isLoggingDebug())
                 {
                 logDebug("error - redirecting to: " + s2);
                 }
             dynamohttpservletresponse.sendLocalRedirect(s2, dynamohttpservletrequest);
             return false;
         }
     } else
     {
         String s3 = s1;
         if(s3 != null)
         {
             dynamohttpservletresponse.sendLocalRedirect(s3, dynamohttpservletrequest);
             return false;
         }
     }
     return true;
 }

 protected synchronized Query createSearch(RepositoryAttribute repositoryattribute)
 {
     atg.repository.QueryBuilder querybuilder = null;
     Query query = null;
     if(mRepositorySearcher != null)
     {
         try
         {
             if((querybuilder = mRepositorySearcher.getQueryBuilder()) != null)
             {
                 if(repositoryattribute != null)
                     {
                     query = mRepositorySearcher.buildSearchByAttributeQuery(querybuilder, repositoryattribute);
                     }
                 else
                     {
                     query = mRepositorySearcher.buildSearchByDateQuery(querybuilder);
                     }
             }
         }
         catch(RepositoryException repositoryexception)
         {
             if(isLoggingError())
                 {
                 logError(repositoryexception);
                 }
         }
     }
     if(isLoggingDebug())
         {
         logDebug("Query = " + query);
         }
     return query;
 }

 public void destroy()
 {
 }

 public String[] getAttributeEnumValues()
 {
     String as[] = null;
     if(mRepositorySearcher != null)
     {
         PropertyEditor propertyeditor = mRepositorySearcher.getPropertyEditor(getAttributeName());
         if(propertyeditor != null)
         {
             as = propertyeditor.getTags();
         }
     }
     return as;
 }

 public String getAttributeName()
 {
     return mAttribute.getName();
 }

 public String getAttributeRelation()
 {
     return mAttribute.getRelation();
 }

 public String getAttributeValue()
 {
     return mAttribute.getValue();
 }

 public String getErrorURL()
 {
     return mErrorURL;
 }

 public Query getQuery()
 {
     return mRepositorySearcher.getQuery();
 }

 public Repository getRepository()
 {
     return mRepositorySearcher.getRepository();
 }

 public RepositorySearcher getRepositorySearcher()
 {
     return mRepositorySearcher;
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

 public String getSuccessURL()
 {
     return mSuccessURL;
 }

 public synchronized boolean handleSearchByAttribute(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     setQuery(createSearch(mAttribute));
     return checkFormError(getErrorURL(), getSuccessURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public synchronized boolean handleSearchByDate(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     setQuery(createSearch(null));
     return checkFormError(getErrorURL(), getSuccessURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public void init(ServletConfig servletconfig)
 {
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     String s = getSearchType();
     if(s != null)
     {
         if(s.compareTo("byDate") == 0)
         {
             setQuery(createSearch(null));
         }
         else
             {
             setQuery(createSearch(mAttribute));
             }
     } else
     {
         if(isLoggingDebug())
         {
             logDebug("Search Type Not Set, using Default: byAttribute");
         }
         setQuery(createSearch(mAttribute));
     }
     dynamohttpservletrequest.serviceLocalParameter("output", dynamohttpservletrequest, dynamohttpservletresponse);
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

 public void setAttributeName(String s)
 {
     mAttribute.setName(s);
 }

 public void setAttributeRelation(String s)
 {
     mAttribute.setRelation(s);
 }

 public void setAttributeValue(String s)
 {
     mAttribute.setValue(s);
 }

 public void setErrorURL(String s)
 {
     mErrorURL = s;
 }

 public void setQuery(Query query)
 {
     mRepositorySearcher.setQuery(query);
 }

 public void setRepository(Repository repository)
 {
     mRepositorySearcher.setRepository(repository);
 }

 public void setRepositorySearcher(RepositorySearcher repositorysearcher)
 {
     mRepositorySearcher = repositorysearcher;
 }

 public void setSearchType(String s)
 {
     mSearchType = s;
 }

 public void setSuccessURL(String s)
 {
     mSuccessURL = s;
 }

 public static final String CLASS_VERSION = "$Id: RepositorySearchForm.java,v 1.4.2.1 2000/07/31 14:54:56 mrutman Exp $";
 public static final String ATTRIBUTE_NAME = "attributeName";
 public static final String ATTRIBUTE_VALUE = "attributeValue";
 public static final String ATTRIBUTE_RELATION = "attributeRelation";
 public static final String DATE_RANGE = "dateRange";
 public static final String SEARCH_TYPE = "searchType";
 public static final String BY_DATE = "byDate";
 public static final String BY_ATTRIBUTE = "byAttribute";
 public static final String OUTPUT = "output";
 protected static final String SEARCHFORMRESOURCES = "atg.repository.servlet.RepositoryServletResources";
 protected static ResourceBundle sResourceBundle = ResourceBundle.getBundle("atg.repository.servlet.RepositoryServletResources", Locale.getDefault());
 RepositorySearcher mRepositorySearcher;
 RepositoryAttribute mAttribute;
 String mSearchType;
 String mSuccessURL;
 String mErrorURL;

}