package com.castorama.utils;

/**
 * Bean for store server setting
 * @author Vasili_Ivus
 *
 */
public class ServerSetting {

	/**
	 * protocol+host+port
	 */ 
	private String mHost;

	/**
	 * secure protocol+host+port
	 */ 
	private String mSecureHost;
	
	/**
	 * constructor without any arguments
	 */
	public ServerSetting() {}

	/**
	 * Gets host
	 * @return host
	 */
	public String getHost() {
		return mHost;
	}

	/**
	 * Sets host
	 * @param pHost host
	 */
	public void setHost(String pHost) {
		this.mHost = pHost;
	}

	/**
	 * @return the secureHost
	 */
	public String getSecureHost() {
		return mSecureHost;
	}

	/**
	 * @param pSecureHost the secureHost to set
	 */
	public void setSecureHost(String pSecureHost) {
		this.mSecureHost = pSecureHost;
	}
	
	
}