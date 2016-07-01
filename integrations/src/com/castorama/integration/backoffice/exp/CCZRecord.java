/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 *
 */
class CCZRecord {
	
	private final int recordCount;
	
	private final long journalId;
	
	public CCZRecord(long journalId, int recordCount) {
		this.journalId = journalId;
		this.recordCount = recordCount;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CCZ|");
		
		UtilFormat.fillStart(sb, "" + journalId, 18, '0').append('|');
		UtilFormat.fillStart(sb, "" + recordCount, 12, '0').append('|').append('\n');
		
		return sb.toString();
	}

}
