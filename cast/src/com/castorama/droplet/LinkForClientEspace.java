package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.nucleus.naming.ParameterName;


public class LinkForClientEspace  extends DynamoServlet{
		
	   /** URL constant. */
    public static final String URL = "url";
    
      /** Text for constant. */
    public static final String URLTEXT = "URLTEXT";
	
	/** OUTPUT parameter name. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY parameter name. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** Disabled or Enabled the link */
    public boolean isVisible;
    
    /** Disabled or Enabled the link */
    public String url;
    
	public String urlText;
	
	public String getUrlText() {
		return urlText;
	}
	
	public void setUrlText(String urlText) {
		this.urlText = urlText;
	}
	
    
    public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
   public void setUrl(String url){
	   this.url=url;
	   
   }
   
   public boolean getIsVisible(){
	   return isVisible;
	  
   }
   public String getUrl(){
	   return url;
	   
   }
          
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	if(isVisible){
    		   pRequest.setParameter(URL, url);
               pRequest.setParameter(URLTEXT, urlText);
               pRequest.serviceParameter(OUTPUT, pRequest,pResponse);
       	}
    	else {
    		   pRequest.serviceParameter(EMPTY, pRequest, pResponse);
    	}
    	
    }
    
}