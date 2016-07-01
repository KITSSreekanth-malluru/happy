/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 *
 */
class COZRecord {

	private static final String SEPARATOR = "|";
	
	private final int records;

	private final String fileId;

	/**
	 * @param num 
	 * 
	 */
	public COZRecord(String fileId, int records) {
		this.fileId = fileId;
		this.records = records;
	}
	
	@Override
	public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("COZ|");
        UtilFormat.fillStart(result, getFileId(), 18, '0').append(SEPARATOR);
        UtilFormat.fillStart(result, "" + getRecords(), 12, '0').append(SEPARATOR).append('\n');

        return result.toString();
	}

	/**
	 * @return the records
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}

}
