package com.castorama.commerce.profile;

import atg.commerce.order.RepositoryContactInfo;
import atg.repository.MutableRepositoryItem;

/**
 * Extends RepositoryContactInfo to add custom properties.
 * 
 * @author Vasili_Ivus
 *
 */


public class CastRepositoryContactInfo extends RepositoryContactInfo {
	
	/**
	 * Makes an empty CastContactInfo object.
	 */
	public CastRepositoryContactInfo() {
	}

	/**
	 * Makes an CastContactInfo object.
	 */
	public CastRepositoryContactInfo(MutableRepositoryItem pMutableRepositoryItem) {
		super(pMutableRepositoryItem);
	}

	/**
	 * @return the locality
	 */
	public String getLocality() {
		return (String) mRepositoryItem.getPropertyValue(com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP);
	}

	/**
	 * @param pLocality the locality to set
	 */
	public void setLocality(String pLocality) {
		mRepositoryItem.setPropertyValue(com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP, pLocality);
	}

	/**
	 * @return the phoneNumber2
	 */
	public String getPhoneNumber2() {
		return (String) mRepositoryItem.getPropertyValue(com.castorama.commerce.profile.Constants.PHONE_NUMBER_2_ADDRESS_PROP);
	}

	/**
	 * @param pPhoneNumber2 the phoneNumber2 to set
	 */
	public void setPhoneNumber2(String pPhoneNumber2) {
		mRepositoryItem.setPropertyValue(com.castorama.commerce.profile.Constants.PHONE_NUMBER_2_ADDRESS_PROP, pPhoneNumber2);
	}

}
