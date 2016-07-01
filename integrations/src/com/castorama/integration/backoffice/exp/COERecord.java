package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.castorama.integration.backoffice.exp.UtilFormat;

class COERecord {

	private static final String PATTERN_DATE = "dd/MM/yyyy H:m:s";

	private static final String SEPARATOR = "|";
	
	private final String fileId;

	public COERecord(String fileId) {
		this.fileId = fileId;
	}
	
	@Override
	public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("COE|");
        UtilFormat.fillStart(result, getFileId(), 18, '0').append(SEPARATOR);
        
        DateFormat df = new SimpleDateFormat(PATTERN_DATE);
        UtilFormat.fillEnd(result, df.format(new Date()), 20, ' ').append(SEPARATOR).append('\n');

        return result.toString();
	}

	/**
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}

}
