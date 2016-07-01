package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 */
class COLRecord
{
    private static final char SEPARATOR = '|';
    
    private static final String PATTERN_DATE = "dd/MM/yyyy H:m:s";

    protected String orderId = "";

    protected String contactNumber = "";

    protected String fileNumber = "";

    protected String contactType = "";

    protected String contactReason = "";

    protected String contactInitiator = "";

    protected Date contactDate;

    protected Date lectureContactDate;

    protected String contactContent = "";

    protected String contactId = "";
    
    
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("COL|");
        UtilFormat.fillEnd(result, getOrderId(), 20, ' ').append(SEPARATOR);
        UtilFormat.fillEnd(result, getContactId(), 20, ' ').append(SEPARATOR);
        UtilFormat.fillStart(result, getContactNumber(), 12, '0').append(SEPARATOR);
        UtilFormat.fillStart(result, getFileNumber(), 12, '0').append(SEPARATOR);
        UtilFormat.fillEnd(result, getContactType(), 2, ' ').append(SEPARATOR);
        UtilFormat.fillEnd(result, getContactReason(), 4, ' ').append(SEPARATOR);
        UtilFormat.fillEnd(result, getContactInitiator(), 2, ' ').append(SEPARATOR);
        
        DateFormat df = new SimpleDateFormat(PATTERN_DATE);
        
        UtilFormat.fillEnd(result, df.format(getContactDate()), 20, ' ').append(SEPARATOR);

        if (null != getLectureContactDate()) {
            UtilFormat.fillEnd(result, df.format(getLectureContactDate()), 20, ' ').append(SEPARATOR);
        } else {
            UtilFormat.fillEnd(result, "", 20, ' ').append(SEPARATOR);
        }
        
        String content = getContactContent().replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "<br>");
        content = content.replaceAll("\\t", " ");
        UtilFormat.fillEnd(result, content, 2000, ' ').append(SEPARATOR).append('\n');

        return result.toString();
    }


	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}


	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}


	/**
	 * @param tNumbecontactNumber the contactNumber to set
	 */
	public void setContactNumber(Integer tNumbecontactNumber) {
		this.contactNumber = UtilFormat.valueToString(tNumbecontactNumber);
	}

	/**
	 * @return the contactType
	 */
	public String getContactType() {
		return contactType;
	}


	/**
	 * @param value the contactType to set
	 */
	public void setContactType(Short value) {
		this.contactType = UtilFormat.valueToString(value);
	}


	/**
	 * @return the contactReason
	 */
	public String getContactReason() {
		return contactReason;
	}


	/**
	 * @param value the contactReason to set
	 */
	public void setContactReason(Short value) {
		this.contactReason = UtilFormat.valueToString(value);
	}


	/**
	 * @return 
	 */
	public String getContactInitiator() {
		return contactInitiator;
	}


	/**
	 * @param short1
	 */
	public void setContactInitiator(Short value) {
		this.contactInitiator = UtilFormat.valueToString(value);
	}


	/**
	 * @return the contactDate
	 */
	public Date getContactDate() {
		return contactDate;
	}


	/**
	 * @param contactDate the contactDate to set
	 */
	public void setContactDate(Date contactDate) {
		this.contactDate = contactDate;
	}


	/**
	 * @return the lectureContactDate
	 */
	public Date getLectureContactDate() {
		return lectureContactDate;
	}


	/**
	 * @param lectureContactDate the lectureContactDate to set
	 */
	public void setLectureContactDate(Date lectureContactDate) {
		this.lectureContactDate = lectureContactDate;
	}


	/**
	 * @return
	 */
	public String getContactContent() {
		return contactContent;
	}


	/**
	 * @param contactContent
	 */
	public void setContactContent(String contactContent) {
		this.contactContent = contactContent;
	}


	/**
	 * @return the contactId
	 */
	public String getContactId() {
		return contactId;
	}


	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}


	/**
	 * @return the fileNumber
	 */
	public String getFileNumber() {
		return fileNumber;
	}


	/**
	 * @param value the fileNumber to set
	 */
	public void setFileNumber(Integer value) {
		this.fileNumber = UtilFormat.valueToString(value);
	}
}