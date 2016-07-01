package com.castorama.login;



import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.droplet.DropletFormException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

//Referenced classes of package atg.commerce.csr:
//         RepositorySearchForm, RepositoryAttribute, RepositorySearcher

public class RepositorySearchBy3AttributesForm extends RepositorySearchForm
{

 public RepositorySearchBy3AttributesForm()
 {
     mAttribute = new RepositoryAttribute[3];
 }

 public void beforeGet(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
 {
     init();
     super.beforeGet(dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public boolean beforeSet(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws DropletFormException
 {
     init();
     return true;
 }

 public Query createSearch()
 {
     Vector vector = new Vector();
     RepositorySearcher repositorysearcher = getRepositorySearcher();
     if(repositorysearcher != null)
     {
         if(mAttribute[0].getValue() != null && !mAttribute[0].getValue().equals(""))
         {
             vector.add(super.createSearch(mAttribute[0]));
         }
         if(mAttribute[1].getValue() != null && !mAttribute[1].getValue().equals(""))
             {
             vector.add(super.createSearch(mAttribute[1]));
             }
         if(mAttribute[2].getValue() != null && !mAttribute[2].getValue().equals(""))
             {
             vector.add(super.createSearch(mAttribute[2]));
             }
         if(vector.size() == 0)
         {
             addFormException(new DropletFormException("Please fill out at least one of the provided fields", "", "cannotBuildQuery"));
             return null;
         }
         QueryBuilder querybuilder = null;
         try
         {
             if((querybuilder = repositorysearcher.getQueryBuilder()) != null)
             {
                 return querybuilder.createAndQuery((Query[])vector.toArray(new Query[0]));
             }
         }
         catch(RepositoryException repositoryexception)
         {
             addFormException(new DropletFormException("Cannot build query ", repositoryexception, "cannotBuildQuery"));
             if(isLoggingError())
             {
                 logError(repositoryexception);
             }
         }
     }
     return null;
 }

 public String getAttributeName1()
 {
     return mAttribute[1].getName();
 }

 public String getAttributeName2()
 {
     return mAttribute[2].getName();
 }

 public String getAttributeName3()
 {
     return mAttribute[0].getName();
 }

 public String getAttributeRelation1()
 {
     return mAttribute[1].getRelation();
 }

 public String getAttributeRelation2()
 {
     return mAttribute[2].getRelation();
 }

 public String getAttributeRelation3()
 {
     return mAttribute[0].getRelation();
 }

 public String getAttributeValue1()
 {
     return mAttribute[1].getValue();
 }

 public String getAttributeValue2()
 {
     return mAttribute[2].getValue();
 }

 public String getAttributeValue3()
 {
     return mAttribute[0].getValue();
 }

 public boolean handleSearch(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     setQuery(createSearch());
     return checkFormError(getErrorURL(), getSuccessURL(), dynamohttpservletrequest, dynamohttpservletresponse);
 }

 protected void init()
 {
     if(!(mAttribute[0] instanceof RepositoryAttribute))
         {
         mAttribute[0] = new RepositoryAttribute();
         }
     if(!(mAttribute[1] instanceof RepositoryAttribute))
         {
         mAttribute[1] = new RepositoryAttribute();
         }
     if(!(mAttribute[2] instanceof RepositoryAttribute))
         {
         mAttribute[2] = new RepositoryAttribute();
         }
 }

 public void service(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     setQuery(createSearch());
     dynamohttpservletrequest.serviceLocalParameter("output", dynamohttpservletrequest, dynamohttpservletresponse);
 }

 public void setAttributeName1(String s)
 {
     mAttribute[1].setName(s);
 }

 public void setAttributeName2(String s)
 {
     mAttribute[2].setName(s);
 }

 public void setAttributeName3(String s)
 {
     mAttribute[0].setName(s);
 }

 public void setAttributeRelation1(String s)
 {
     mAttribute[1].setRelation(s);
 }

 public void setAttributeRelation2(String s)
 {
     mAttribute[2].setRelation(s);
 }

 public void setAttributeRelation3(String s)
 {
     mAttribute[0].setRelation(s);
 }

 public void setAttributeValue1(String s)
 {
     mAttribute[1].setValue(s);
 }

 public void setAttributeValue2(String s)
 {
     mAttribute[2].setValue(s);
 }

 public void setAttributeValue3(String s)
 {
     mAttribute[0].setValue(s);
 }

 public static final String CLASS_VERSION = "$Id: RepositorySearchBy3AttributesForm.java,v 1.3.2.1.4.2 2000/12/04 06:53:39 mannyp Exp $";
 public static final String ATTRIBUTE_1_NAME = "attributeName1";
 public static final String ATTRIBUTE_1_VALUE = "attributeValue1";
 public static final String ATTRIBUTE_1_RELATION = "attributeRelation1";
 public static final String ATTRIBUTE_2_NAME = "attributeName2";
 public static final String ATTRIBUTE_2_VALUE = "attributeValue2";
 public static final String ATTRIBUTE_2_RELATION = "attributeRelation2";
 public static final String ATTRIBUTE_3_NAME = "attributeName3";
 public static final String ATTRIBUTE_3_VALUE = "attributeValue3";
 public static final String ATTRIBUTE_3_RELATION = "attributeRelation3";
 public static final String DATE_RANGE = "dateRange";
 public static final String SEARCH_TYPE = "searchType";
 RepositoryAttribute mAttribute[];
}