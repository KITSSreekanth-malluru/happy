package com.castorama.payment;

import atg.nucleus.GenericService;

/**
 * Payment common configuration
 * @author Vasili_Ivus
 *
 */

public class PaymentConfiguration extends GenericService {
	
	/**
	 * To display information about possible payment terms for corresponding order
	 */
	private String mSofincoURL;
	
	/**
	 * The name of query parameter of amount
	 */
	private String mQueryAmount;
	
	/**
	 * The name of query parameter of reference
	 */
	private String mQueryReference;

	/**
	 * To display information about possible payment terms for corresponding order
	 */
	private String mSofincoRang;
	
	private String sofincoSecretKey;
	
	public void setSofincoSecretKey(String sofincoSecretKey) {
		this.sofincoSecretKey = sofincoSecretKey;
	}
	
	public String getSofincoSecretKey() {
		return sofincoSecretKey;
	}	

	/**
	 * Gets value of the Sofinco URL (page displaying possible payment terms for corresponding order)  
	 * @return sofincoURL string value of the Sofinco URL 
	 */
	public String getSofincoURL() {
		return mSofincoURL;
	}

	/**
	 * Sets value of the Sofinco URL (page displaying possible payment terms for corresponding order)
	 * @param pSofincoURL string value of the Sofinco URL
	 */
	public void setSofincoURL(String pSofincoURL) {
		this.mSofincoURL = pSofincoURL;
	}

	/**
	 * Gets name of query paramenter of amount
	 * @return name of query paramenter of amount
	 */
	public String getQueryAmount() {
		return mQueryAmount;
	}

	/**
	 * Sets name of query paramenter of amount
	 * @param pQueryAmount name of query paramenter of amount
	 */
	public void setQueryAmount(String pQueryAmount) {
		this.mQueryAmount = pQueryAmount;
	}

	/**
	 * Gets name of query paramenter of reference
	 * @return name of query paramenter of reference
	 */
	public String getQueryReference() {
		return mQueryReference;
	}

	/**
	 * Sets name of query paramenter of reference
	 * @param pQueryReference name of query paramenter of reference
	 */
	public void setQueryReference(String pQueryReference) {
		this.mQueryReference = pQueryReference;
	}

	/**
	 * @return the sofincoRang
	 */
	public String getSofincoRang() {
		return mSofincoRang;
	}

	/**
	 * @param sofincoRang the sofincoRang to set
	 */
	public void setSofincoRang(String pSofincoRang) {
		this.mSofincoRang = pSofincoRang;
	}
	
}
