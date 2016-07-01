package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns image url and name, when empty values in sku or cannot find sku in catalog.
 * 
 * @author Vasili_Ivus
 *
 */
public class ProductNameDroplet extends DynamoServlet {

	/** URL_PARAM constant. */
	public static final String URL_PARAM = "url";
	/** NAME_PARAM constant. */
	public static final String NAME_PARAM = "name";
	/** OUTPUT constant. */
	public static final String OUTPUT = "output";
	/** OUT_URL_PARAM constant. */
	public static final String OUT_URL_PARAM = "outURL";
	/** OUT_NAME_PARAM constant. */
	public static final String OUT_NAME_PARAM = "outName";
	/** NO_PRODUCT_MESSAGE constant. */
	public static final String NO_PRODUCT_MESSAGE = "Ce produit n'est pas.";
	/** imagePlaceholderURL parameter. */
	private String mImagePlaceholderURL = "";
	
	/**
	 * Returns image url and name, when empty values in sku or cannot find sku in catalog.
	 * 
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException {

		String url = pRequest.getParameter(URL_PARAM);
		String name = pRequest.getParameter(NAME_PARAM);
		if ( null == url || 0 == url.trim().length() ) {
			url = getImagePlaceholderURL();
		}
		if ( null == name || 0 == name.trim().length() ) {
			name = NO_PRODUCT_MESSAGE; 
		}
        pRequest.setParameter(OUT_URL_PARAM, url);
        pRequest.setParameter(OUT_NAME_PARAM, name);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
	}

	/**
	 * Returns imagePlaceholderURL parameter value.
	 * 
	 * @return the imagePlaceholderURL
	 */
	public String getImagePlaceholderURL() {
		return mImagePlaceholderURL;
	}

	/**
	 * Sets imagePlaceholderURL parameter value.
	 * 
	 * @param pImagePlaceholderURL the imagePlaceholderURL to set
	 */
	public void setImagePlaceholderURL(String pImagePlaceholderURL) {
		this.mImagePlaceholderURL = pImagePlaceholderURL;
	}
	
	

}
