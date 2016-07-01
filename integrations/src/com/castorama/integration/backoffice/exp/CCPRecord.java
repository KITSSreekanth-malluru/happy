/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 *
 */
class CCPRecord {
	
	private static final DateFormat submitDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
		
	private String orderId;
	
	private String paymentMethod;
	
	private String transactionId = "";
	
	private String transactionSertificate = "";
	
	private String autorisationNumber = "";
	
	private String numCheque = "";

	private String libelleBanque = "";

	private String atoutPaymenOption = "";
	
	private String comment = "";
	
	private double transactionAmount;
	
	private Date authExpire;

	private Date dateTransaction;
	
	private Date atoutValidDate;
	
	/**
	 * 
	 */
	public CCPRecord() {
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CCP|");
		
		UtilFormat.fillEnd(sb, orderId, 20, ' ').append('|');
		UtilFormat.fillStart(sb, "" + paymentMethod, 2, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(transactionAmount), 13, '0').append('|');
		
		sb.append("EUR|");
		
		if (null != authExpire) {
			UtilFormat.fillEnd(sb, submitDateFormat.format(authExpire), 12, ' ').append('|');
		} else {
			UtilFormat.fillEnd(sb, "", 12, ' ').append('|');
		}
		UtilFormat.fillEnd(sb, transactionId, 20, ' ').append('|');
		UtilFormat.fillEnd(sb, transactionSertificate, 20, ' ').append('|');
		UtilFormat.fillStart(sb, "" + autorisationNumber, 20, '0').append('|');

		if (null != dateTransaction) {
			UtilFormat.fillEnd(sb, submitDateFormat.format(dateTransaction), 12, ' ').append('|');
		} else {
			UtilFormat.fillEnd(sb, "", 12, ' ').append('|');
		}

		UtilFormat.fillEnd(sb, getNumCheque(), 50, ' ').append('|');
		UtilFormat.fillEnd(sb, libelleBanque, 30, ' ').append('|');

		if (null != atoutValidDate) {
			UtilFormat.fillEnd(sb, submitDateFormat.format(atoutValidDate), 12, ' ').append('|');
		} else {
			UtilFormat.fillEnd(sb, "", 12, ' ').append('|');
		}
		
		UtilFormat.fillStart(sb, "" + atoutPaymenOption, 3, '0').append('|');
		UtilFormat.fillEnd(sb, "" + comment, 100, ' ').append('|').append('\n');
		
		return sb.toString();
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
		this.orderId = UtilFormat.valueToString(orderId);
	}

	/**
	 * @return the autorisationNumber
	 */
	public String getAutorisationNumber() {
		if (null == autorisationNumber) {
			return "";
		} else {
			return autorisationNumber;
		}
	}

	/**
	 * @param value the autorisationNumber to set
	 */
	public void setAutorisationNumber(String value) {
		this.autorisationNumber = UtilFormat.valueToString(value);
	}

	/**
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param value the paymentMethod to set
	 */
	public void setPaymentMethod(String value) {
		this.paymentMethod = UtilFormat.valueToString(value);
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = UtilFormat.deleteCRLF(UtilFormat.valueToString(comment));
	}

	/**
	 * @return the authExpire
	 */
	public Date getAuthExpire() {
		return authExpire;
	}

	/**
	 * @param authExpire the authExpire to set
	 */
	public void setAuthExpire(Date authExpire) {
		this.authExpire = authExpire;
	}

	/**
	 * @return the transactionAmount
	 */
	public double getTransactionAmount() {
		return transactionAmount;
	}

	/**
	 * @param d the transactionAmount to set
	 */
	public void setTransactionAmount(Double d) {
		this.transactionAmount = UtilFormat.valueToDouble(d);
	}

	/**
	 * @return the transactionSertificate
	 */
	public String getTransactionSertificate() {
		return transactionSertificate;
	}

	/**
	 * @param transactionSertificate the transactionSertificate to set
	 */
	public void setTransactionSertificate(String transactionSertificate) {
		this.transactionSertificate = UtilFormat.valueToString(transactionSertificate);
	}

	/**
	 * @return the numCheque
	 */
	public String getNumCheque() {
		return numCheque;
	}

	/**
	 * @param numCheque the numCheque to set
	 */
	public void setNumCheque(String numCheque) {
		this.numCheque = UtilFormat.valueToString(numCheque);
	}

	/**
	 * @return the libelleBanque
	 */
	public String getLibelleBanque() {
		return libelleBanque;
	}

	/**
	 * @param libelleBanque the libelleBanque to set
	 */
	public void setLibelleBanque(String libelleBanque) {
		this.libelleBanque = UtilFormat.valueToString(libelleBanque);
	}

	/**
	 * @return the atoutValidDate
	 */
	public Date getAtoutValidDate() {
		return atoutValidDate;
	}

	/**
	 * @param atoutValidDate the atoutValidDate to set
	 */
	public void setAtoutValidDate(Date atoutValidDate) {
		this.atoutValidDate = atoutValidDate;
	}

	/**
	 * @return the atoutPaymenOption
	 */
	public String getAtoutPaymenOption() {
		return atoutPaymenOption;
	}

	/**
	 * @param atoutPaymenOption the atoutPaymenOption to set
	 */
	public void setAtoutPaymenOption(String atoutPaymenOption) {
		this.atoutPaymenOption = UtilFormat.valueToString(atoutPaymenOption);
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = UtilFormat.valueToString(transactionId);
	}

	/**
	 * @return the dateTransaction
	 */
	public Date getDateTransaction() {
		return dateTransaction;
	}

	/**
	 * @param dateTransaction the dateTransaction to set
	 */
	public void setDateTransaction(Date dateTransaction) {
		this.dateTransaction = dateTransaction;
	}
}
