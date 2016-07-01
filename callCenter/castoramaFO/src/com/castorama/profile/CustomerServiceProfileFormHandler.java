package com.castorama.profile;


import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileAdminFormHandler;

public class CustomerServiceProfileFormHandler extends ProfileAdminFormHandler
{

 public CustomerServiceProfileFormHandler()
 {
 }

 public void preCreateUser(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
     throws ServletException, IOException
 {
     super.preCreateUser(dynamohttpservletrequest, dynamohttpservletresponse);
     if(isLoggingDebug())
         {
         logDebug("Setting Registration Date");
         }
     setValueProperty("registrationDate", new Date());
 }

 public static final String CLASS_VERSION = "$Id: CustomerServiceProfileFormHandler.java,v 1.1.2.1 2000/10/10 16:44:25 mrutman Exp $";
}