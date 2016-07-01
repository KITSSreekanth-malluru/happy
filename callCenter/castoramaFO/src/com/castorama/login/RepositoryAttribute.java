package com.castorama.login;


import atg.repository.QueryBuilder;

public class RepositoryAttribute
{

 public RepositoryAttribute()
 {
 }

 public RepositoryAttribute(String s, String s1, String s2)
 {
     setName(s);
     setRelation(s1);
     setValue(s2);
 }

 public String getName()
 {
     return mName;
 }

 public int getQueryBuilderRelation()
 {
     byte byte0 = 4;
     if(getRelation() != null)
     {
         if(getRelation().compareTo("==") == 0)
         {
             byte0 = 4;
         }
         else
         if(getRelation().compareTo("!=") == 0)
         {
             byte0 = 5;
         }
         else
         if(getRelation().compareTo("<") == 0)
         {
             byte0 = 6;
         }
         else
         if(getRelation().compareTo(">") == 0)
             {
             byte0 = 7;
             }
         else
         if(getRelation().compareTo("<=") == 0)
         {
             byte0 = 8;
         }
         else
         if(getRelation().compareTo(">=") == 0)
         {
             byte0 = 9;
         }
     }
     return byte0;
 }

 public String getRelation()
 {
     return mRelation;
 }

 public String getValue()
 {
     return mValue;
 }

 public void setName(String s)
 {
     mName = s;
 }

 public void setRelation(String s)
 {
     mRelation = s;
 }

 public void setValue(String s)
 {
     mValue = s;
 }

 public static final String CLASS_VERSION = "$Id: RepositoryAttribute.java,v 1.1 2000/06/08 19:08:43 mrutman Exp $";
 public static final String EQUALS_STRING = "==";
 public static final String NOTEQUALS_STRING = "!=";
 public static final String LESS_STRING = "<";
 public static final String GREATER_STRING = ">";
 public static final String LESSTHANOREQUALS_STRING = "<=";
 public static final String GREATERTHANOREQUALS_STRING = ">=";
 String mName;
 String mValue;
 String mRelation;
}