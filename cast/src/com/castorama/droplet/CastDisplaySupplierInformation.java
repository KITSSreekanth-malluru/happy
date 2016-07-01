package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CastDisplaySupplierInformation extends DynamoServlet{
	
	public static final String SHIPPMENT_TYPE_PARAM="shippment_type";
	public static final String SHIPPMENT_CARRIER_PARAM="shippment_carrier";
	public static final String REFERENCE_TRACKING_PARAM="tracking_number";
	public static final String REFERENCE_TRACKING_URL_PARAM="url_transporteur";
	public static final String SHIPPMENT_CARIER_COLISSIMO="COLISSIMO";
	public static final String CONDITION = "condition";
	
	/** OUTPUT parameter name. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		String shippementCarrier=(String)pRequest.getObjectParameter(SHIPPMENT_CARRIER_PARAM);
		String referenceTracking=(String)pRequest.getObjectParameter(REFERENCE_TRACKING_PARAM);
		String referenceTrackingUrl=(String)pRequest.getObjectParameter(REFERENCE_TRACKING_URL_PARAM);
		int condition;
		
		if(shippementCarrier!=null && shippementCarrier.equals(SHIPPMENT_CARIER_COLISSIMO)){
			//Display reference tracking, display carrier If PNS: display link  with reference tracking, 
			//if LDF display only link reference tracking 
			 condition=1;
		}
		else{
		 if(shippementCarrier!=null && ! shippementCarrier.equals("")){
			if(referenceTracking!=null && !referenceTracking.equals("")){
				if(referenceTrackingUrl!=null && !referenceTrackingUrl.equals("")){
					//Display reference tracking, display carrier. 
					//If PNS no display link package tracking.
					//If LDF display link package tracking
					condition=2;
				}else{
					//Display reference tracking,  display carrier
					//If PNS no display link package tracking
					//If LDF no display link package tracking
					//Display msg "Le transporteur vous contactera..."
					condition=3;
				}
				
			 } else{
				//No display reference tracking no display carrier,
				//If PNS no display link package tracking
				//If LDF no display link package tracking
				//Display msg "Le transporteur vous contactera..."
				 condition=4;
			}
			
		 }else{ 
			  if(referenceTracking!=null && !referenceTracking.equals("")){
			     if(referenceTrackingUrl!=null && !referenceTrackingUrl.equals("")){
				   condition=5;
			     }
			     else {
				   condition=6;
			     }
			   
			  }
			    //No display reference tracking no display carrier,
				//If PNS no display link package tracking
			    //If LDF no display link package tracking
			    //Display msg "Le transporteur vous contactera..."
			   condition=4;
			
		 }
	   }
		
		pRequest.setParameter(CONDITION, condition);
        pRequest.serviceParameter(OUTPUT, pRequest,pResponse);
		
	}
}
