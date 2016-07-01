/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 *
 */
class CCRRecord {

	private final int recordCount;
	
	private final String orderId;
	
	/**
	 * @param detCount 
	 * 
	 */
	public CCRRecord(String orderId, int recordCount) {
		this.orderId = orderId;
		this.recordCount = recordCount;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CCR|");
		
		UtilFormat.fillEnd(sb, orderId, 20, ' ').append('|');
		UtilFormat.fillStart(sb, "" + recordCount, 12, '0').append('|').append('\n');
		
		return sb.toString();
	}

}
