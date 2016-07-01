/**
 * 
 */
package com.castorama.model;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 * @author Andrew_Logvinov
 */
public class Coupon extends RepositoryItemWrapper {
    /** DESCRIPTOR_NAME constant. */
	public static final String DESCRIPTOR_NAME = "casto_coupons";
	
    /** ORDER_ID constant. */
	public static final String ORDER_ID = "order_id";
	
    /** COUPON_CODE constant. */
	public static final String COUPON_CODE = "code_coupon";

	/**
	 * Item wrapper constructor.
	 * 
	 * @param repositoryItem item to be wrapped
	 */
	public Coupon(RepositoryItem repositoryItem) {
		super(repositoryItem);
	}
	
	/**
	 * NullPointer safe object factory method.
	 * 
	 * @param repositoryItem item to be wrapped
	 * 
	 * @return new Wrapper or null.
	 */
	public static Coupon getInstance(RepositoryItem repositoryItem){
		return (null == repositoryItem)?null:new Coupon(repositoryItem);
	}
	
	/**
	 * Wrapped getter for {@link Coupon#COUPON_CODE}.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return (String) repositoryItem.getPropertyValue(COUPON_CODE);
	}

	/**
	 * Wrapped setter for {@link Coupon#COUPON_CODE}.
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		((MutableRepositoryItem)repositoryItem).setPropertyValue(COUPON_CODE, code);
	}
	
}
