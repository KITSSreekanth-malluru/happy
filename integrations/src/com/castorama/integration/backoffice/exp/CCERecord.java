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
class CCERecord {
	
	private static final DateFormat submitDateFormat = new SimpleDateFormat("yyyyMMddHHmm");

	private static final String codeFrance = "F";
	
	private String facturePays = "";

	private String deliveryCode = "";

	private String deliveryNom = "";

	private String deliveryPrenom = "";

	private String deliveryAddress1 = "";

	private String deliveryAddress2 = "";

	private String deliveryAddress3 = "";

	private String deliveryZipCode = "";

	private String deliveryCity = "";

	private String deliveryPayCode = "";

	private String deliveryPay = "";

	private String comment = "";
	
	private String paymentPhone = "";
	
	private String login = "";
	
	private String authExpire = "";
	
	private String removementDate = "";
	
	private String transactionReference = "";
	
	private String transactionSertificate = "";
	
	private String transactionDate = "";
	
	private String shippingPhone = "";
	
	private String numCheque = "";
	
	private String libelleBanque = "";

	private String atoutPaymenOption = "";
	
	private String messageTransport = "";
	
	private String paymentCompany = "";

	private String shippingCompany = "";

	private String promotionName = "";
	
	private String transactionId = "";

	private String pbAuthorization = "";

	private String pbTransaction = "";

	private String poidsPFT = "";

	private String poidsPFL = "";

	private String atoutValidDate = "";

	private String pbTransactionDate = "";

	private String pbPaymentDate = "";

	private String factureLocality = "";

	private String facturePhoneNumber2 = "";

	private String deliveryLocality = "";

	private String deliveryPhoneNumber2 = "";
	
	private String castoramaCardNumber = "";
	

	private double amountTotalHT_FF;

	private double amountTotalHT_EU;

	private double amountTotalTTC_FF;

	private double amountTotalTTC_EU;

	private double amountShippingCost;

	private double transactionAmount;

	private double amountChequeEUR;
	
	private double ammointPFT;

	private double ammointPFL;

	private double remise;

	private int paymentMethod;
	
	private boolean isTwoPhoneNumbersSend;
	
	/**
	 * 
	 */
	public CCERecord() {
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CCE|");
		
		UtilFormat.fillEnd(sb, getOrderId(), 20, ' ').append('|');
		UtilFormat.fillStart(sb, getAutorisationNumber(), 20, '0').append('|');
		UtilFormat.fillEnd(sb, getSubmited(), 12, ' ').append('|');
		UtilFormat.fillEnd(sb, getClientCode(), 20, ' ').append('|');
		UtilFormat.fillStart(sb, getCFCode(), 1, ' ').append('|');
		UtilFormat.fillEnd(sb, getNomFacture(), 100, ' ').append('|');
		UtilFormat.fillEnd(sb, getPrenomFacture(), 100, ' ').append('|');
		UtilFormat.fillEnd200(sb, getAddressFacture1(), getFactureLocality()).append('|');
		UtilFormat.fillEnd200(sb, getAddressFacture2(), getAddressFacture3()).append('|');
		UtilFormat.fillEnd(sb, getFactureZipCode(), 5, ' ').append('|');
		UtilFormat.fillEnd(sb, getFactureVille(), 50, ' ').append('|');
		UtilFormat.fillEnd(sb, getFacturePayCode(), 3, ' ').append('|');
		UtilFormat.fillEnd(sb, getFacturePays(), 50, ' ').append('|');
		UtilFormat.fillStart(sb, getDeliveryCode(), 1, ' ').append('|');
		UtilFormat.fillEnd(sb, getDeliveryNom(), 100, ' ').append('|');
		UtilFormat.fillEnd(sb, getDeliveryPrenom(), 100, ' ').append('|');
		UtilFormat.fillEnd200(sb, getDeliveryAddress1(), getDeliveryLocality()).append('|');
		UtilFormat.fillEnd200(sb, getDeliveryAddress2(), getDeliveryAddress3()).append('|');
		UtilFormat.fillEnd(sb, getDeliveryZipCode(), 5, ' ').append('|');
		UtilFormat.fillEnd(sb, getDeliveryCity(), 50, ' ').append('|');
		UtilFormat.fillEnd(sb, getDeliveryPayCode(), 3, ' ').append('|');
		UtilFormat.fillEnd(sb, getDeliveryPay(), 50, ' ').append('|');
		// modeLivrason allways 1
		sb.append("1 |");
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmountTotalHT_EU()), 13, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmountTotalTTC_EU()), 13, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmmointPFL()), 13, '0').append('|');
		UtilFormat.fillStart(sb, "" + getPaymentMethod(), 2, '0').append('|');
		sb.append("EUR|");
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getRemise()), 13, '0').append('|');
		UtilFormat.fillEnd(sb, getComment(), 200, ' ').append('|');
		if (getHasComment()) {
			sb.append("1|");
		} else {
			sb.append("0|");
		}
		UtilFormat.fillEnd(sb, getPaymentPhone(), 20, ' ').append('|');
		UtilFormat.fillEnd(sb, getLogin(), 50, ' ').append('|');
		sb.append("01|");
		UtilFormat.fillEnd(sb, getAuthExpire(), 12, ' ').append('|');
		UtilFormat.fillEnd(sb, getRemovementDate(), 12, ' ').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getTransactionAmount()), 13, '0').append('|');
		UtilFormat.fillEnd(sb, getTransactionReference(), 20, ' ').append('|');
		UtilFormat.fillEnd(sb, getTransactionSertificate(), 20, ' ').append('|');
		UtilFormat.fillEnd(sb, getTransactionDate(), 12, ' ').append('|');
		UtilFormat.fillEnd(sb, getShippingPhone(), 20, ' ').append('|');
		if(isTwoPhoneNumbersSend){
		   UtilFormat.fillEnd(sb, getDeliveryPhoneNumber2(), 20, ' ').append('|'); 
		}
		UtilFormat.fillEnd(sb, getNumCheque(), 50, ' ').append('|');
		UtilFormat.fillEnd(sb, getLibelleBanque(), 30, ' ').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmountChequeEUR()), 15, '0').append('|');		
		
		// allways empty numeroCarteAtout
		UtilFormat.fillEnd(sb, "", 50, ' ').append('|');
		
		UtilFormat.fillEnd(sb, getAtoutValidDate(), 12, ' ').append('|');
		UtilFormat.fillStart(sb, getAtoutPaymenOption(), 3, '0').append('|');
		UtilFormat.fillEnd(sb, getMessageTransport(), 78, ' ').append('|');

		UtilFormat.fillEnd(sb, getPaymentCompany(), 40, ' ').append('|');
		UtilFormat.fillEnd(sb, getShippingCompany(), 40, ' ').append('|');
		
		UtilFormat.fillEnd(sb, getPromotionName(), 200, ' ').append('|');
		UtilFormat.fillStart(sb, getPoidsPFT(), 12, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmmointPFT()), 13, '0').append('|');
		UtilFormat.fillStart(sb, getPoidsPFL(), 12, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(getAmmointPFL()), 13, '0').append('|');
		
		UtilFormat.fillEnd(sb, getCastoramaCardNumber(), 19, ' ').append('|').append('\n');
		
		return sb.toString();
	}

	public boolean getIsTwoPhoneNumbersSend() {
		return isTwoPhoneNumbersSend;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setIsTwoPhoneNumbersSend(boolean isTwoPhoneNumbersSend) {
		this.isTwoPhoneNumbersSend = isTwoPhoneNumbersSend;
	}
	
	
	private String orderId = "";

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

	private String autorisationNumber = "";

	/**
	 * @return the autorisationNumber
	 */
	public String getAutorisationNumber() {
		return autorisationNumber;
	}

	/**
	 * @param string the autorisationNumber to set
	 */
	public void setAutorisationNumber(String string) {
		this.autorisationNumber = UtilFormat.valueToString(string);
	}

	private String submited = "";

	/**
	 * @return the submited
	 */
	public String getSubmited() {
		return submited;
	}

	/**
	 * @param submited the submited to set
	 */
	public void setSubmited(Date submited) {
		this.submited = UtilFormat.valueToString(submited, submitDateFormat);
	}

	private String clientCode = "";

	/**
	 * @return the clientCode
	 */
	public String getClientCode() {
		return clientCode;
	}

	/**
	 * @param clientCode the clientCode to set
	 */
	public void setClientCode(String clientCode) {
		this.clientCode = UtilFormat.valueToString(clientCode);
	}

	private String CFCode = "";

	/**
	 * @return the cFCode
	 */
	public String getCFCode() {
		return CFCode;
	}

	/**
	 * @param code the cFCode to set
	 */
	public void setCFCode(Integer code) {
		CFCode = UtilFormat.valueToString(code);
	}

	private String nomFacture = "";

	/**
	 * @return the nomFacture
	 */
	public String getNomFacture() {
		return nomFacture;
	}

	/**
	 * @param nomFacture the nomFacture to set
	 */
	public void setNomFacture(String nomFacture) {
		this.nomFacture = UtilFormat.valueToString(nomFacture);
	}

	private String prenomFacture = "";

	/**
	 * @return the prenomFacture
	 */
	public String getPrenomFacture() {
		return prenomFacture;
	}

	/**
	 * @param prenomFacture the prenomFacture to set
	 */
	public void setPrenomFacture(String prenomFacture) {
		this.prenomFacture = UtilFormat.valueToString(prenomFacture);
	}

	private String addressFacture3  = "";

	private String addressFacture1  = "";

	/**
	 * @return the addressFacture
	 */
	public String getAddressFacture1() {
		return addressFacture1;
	}

	/**
	 * @param addressFacture the addressFacture to set
	 */
	public void setAddressFacture1(String addressFacture) {
		this.addressFacture1 = UtilFormat.deleteCRLF(UtilFormat.valueToString(addressFacture));
	}

	private String addressFacture2 = "";

	/**
	 * @return the addressFacture2
	 */
	public String getAddressFacture2() {
		return addressFacture2;
	}

	/**
	 * @param addressFacture2 the addressFacture2 to set
	 */
	public void setAddressFacture2(String addressFacture2) {
		this.addressFacture2 = UtilFormat.deleteCRLF(UtilFormat.valueToString(addressFacture2));
	}

	private String factureZipCode = "";

	/**
	 * @return the factureZipCode
	 */
	public String getFactureZipCode() {
		return (codeFrance.equalsIgnoreCase(facturePayCode)) ? factureZipCode : "0";
	}

	/**
	 * @param factureZipCode the factureZipCode to set
	 */
	public void setFactureZipCode(String factureZipCode) {
		this.factureZipCode = UtilFormat.valueToString(factureZipCode);
	}

	private String factureVille = "";
	
	/**
	 * @return the factureVille
	 */
	public String getFactureVille() {
		return factureVille;
	}

	/**
	 * @param factureVille the factureVille to set
	 */
	public void setFactureVille(String factureVille) {
		this.factureVille = UtilFormat.valueToString(factureVille);
	}

	private String facturePayCode = "";

	/**
	 * @return the facturePayCode
	 */
	public String getFacturePayCode() {
		return facturePayCode;
	}

	/**
	 * @param facturePayCode the facturePayCode to set
	 */
	public void setFacturePayCode(String facturePayCode) {
		this.facturePayCode = facturePayCode;
	}

	/**
	 * @return the deliveryCode
	 */
	public String getDeliveryCode() {
		return deliveryCode;
	}

	/**
	 * @param deliveryCode the deliveryCode to set
	 */
	public void setDeliveryCode(Integer deliveryCode) {
		this.deliveryCode = UtilFormat.valueToString(deliveryCode);
	}

	/**
	 * @return the deliveryNom
	 */
	public String getDeliveryNom() {
		return deliveryNom;
	}

	/**
	 * @param deliveryNom the deliveryNom to set
	 */
	public void setDeliveryNom(String deliveryNom) {
		this.deliveryNom = UtilFormat.valueToString(deliveryNom);
	}

	/**
	 * @return the deliveryPrenom
	 */
	public String getDeliveryPrenom() {
		return deliveryPrenom;
	}

	/**
	 * @param deliveryPrenom the deliveryPrenom to set
	 */
	public void setDeliveryPrenom(String deliveryPrenom) {
		this.deliveryPrenom = UtilFormat.valueToString(deliveryPrenom);
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress1() {
		return deliveryAddress1;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress1(String deliveryAddress) {
		this.deliveryAddress1 = UtilFormat.deleteCRLF(UtilFormat.valueToString(deliveryAddress));
	}

	/**
	 * @return the deliveryAddress2
	 */
	public String getDeliveryAddress2() {
		return deliveryAddress2;
	}

	/**
	 * @param deliveryAddress2 the deliveryAddress2 to set
	 */
	public void setDeliveryAddress2(String deliveryAddress2) {
		this.deliveryAddress2 = UtilFormat.deleteCRLF(UtilFormat.valueToString(deliveryAddress2));
	}

	/**
	 * @return the deliveryZipCode
	 */
	public String getDeliveryZipCode() {
		return (codeFrance.equalsIgnoreCase(deliveryPayCode)) ? deliveryZipCode : "0";
	}

	/**
	 * @param deliveryZipCode the deliveryZipCode to set
	 */
	public void setDeliveryZipCode(String deliveryZipCode) {
		this.deliveryZipCode = UtilFormat.valueToString(deliveryZipCode);
	}

	/**
	 * @return the deliveryCity
	 */
	public String getDeliveryCity() {
		return deliveryCity;
	}

	/**
	 * @param deliveryCity the deliveryCity to set
	 */
	public void setDeliveryCity(String deliveryCity) {
		this.deliveryCity = UtilFormat.valueToString(deliveryCity);
	}

	/**
	 * @return the deliveryPayCode
	 */
	public String getDeliveryPayCode() {
		return deliveryPayCode;
	}

	/**
	 * @param deliveryPayCode the deliveryPayCode to set
	 */
	public void setDeliveryPayCode(String deliveryPayCode) {
		this.deliveryPayCode = UtilFormat.valueToString(deliveryPayCode);
	}

	/**
	 * @return the deliveryPay
	 */
	public String getDeliveryPay() {
		return deliveryPay;
	}

	/**
	 * @param deliveryPay the deliveryPay to set
	 */
	public void setDeliveryPay(String deliveryPay) {
		this.deliveryPay = UtilFormat.valueToString(deliveryPay);
	}

	/**
	 * @return the amountTotalHT_FF
	 */
	public double getAmountTotalHT_FF() {
		return amountTotalHT_FF;
	}

	/**
	 * TODO unused
	 * 
	 * @param amountTotalHT_FF the amountTotalHT_FF to set
	 */
	public void setAmountTotalHT_FF(Double amountTotalHT_FF) {
		this.amountTotalHT_FF = UtilFormat.valueToDouble(amountTotalHT_FF);
	}

	/**
	 * @return the amountTotalHT_EU
	 */
	public double getAmountTotalHT_EU() {
		return amountTotalHT_EU;
	}

	/**
	 * @param amountTotalHT_EU the amountTotalHT_EU to set
	 */
	public void setAmountTotalHT_EU(Double amountTotalHT_EU) {
		this.amountTotalHT_EU = UtilFormat.valueToDouble(amountTotalHT_EU);
	}

	/**
	 * @return the amountTotalTTC_FF
	 */
	public double getAmountTotalTTC_FF() {
		return amountTotalTTC_FF;
	}

	/**
	 * @param amountTotalTTC_FF the amountTotalTTC_FF to set
	 */
	public void setAmountTotalTTC_FF(Double amountTotalTTC_FF) {
		this.amountTotalTTC_FF = UtilFormat.valueToDouble(amountTotalTTC_FF);
	}

	/**
	 * @return the amountTotalTTC_EU
	 */
	public double getAmountTotalTTC_EU() {
		return amountTotalTTC_EU;
	}

	/**
	 * @param amountTotalTTC_EU the amountTotalTTC_EU to set
	 */
	public void setAmountTotalTTC_EU(Double amountTotalTTC_EU) {
		this.amountTotalTTC_EU = UtilFormat.valueToDouble(amountTotalTTC_EU);
	}

	/**
	 * @return the amountShippingCost
	 */
	public double getAmountShippingCost() {
		return amountShippingCost;
	}

	/**
	 * @param amountShippingCost the amountShippingCost to set
	 */
	public void setAmountShippingCost(Double amountShippingCost) {
		this.amountShippingCost = UtilFormat.valueToDouble(amountShippingCost);
	}

	/**
	 * @return the facturePays
	 */
	public String getFacturePays() {
		return facturePays;
	}

	/**
	 * @param facturePays the facturePays to set
	 */
	public void setFacturePays(String facturePays) {
		this.facturePays = UtilFormat.valueToString(facturePays);
	}

	/**
	 * @return the paymentMethod
	 */
	public int getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = (null == paymentMethod)?0:paymentMethod;
	}

	/**
	 * @return the remise
	 */
	public double getRemise() {
		return remise;
	}

	/**
	 * @param remize the remise to set
	 */
	public void setRemise(Double remize) {
		this.remise = UtilFormat.valueToDouble(remize);
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
	 * @return the hasComment
	 */
	public boolean getHasComment() {
		return (null != comment && ! "".equals(comment));
	}

	/**
	 * @return the paymentPhone
	 */
	public String getPaymentPhone() {
		return paymentPhone;
	}

	/**
	 * @param paymentPhone the paymentPhone to set
	 */
	public void setPaymentPhone(String paymentPhone) {
		this.paymentPhone = UtilFormat.valueToString(paymentPhone).replaceAll("\\D", "");
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = UtilFormat.valueToString(login);
	}

	/**
	 * @return the authExpire
	 */
	public String getAuthExpire() {
		return authExpire;
	}

	/**
	 * @param authExpire the authExpire to set
	 */
	public void setAuthExpire(Date authExpire) {
		this.authExpire = UtilFormat.valueToString(authExpire, submitDateFormat);
	}

	/**
	 * @return the removementDate
	 */
	public String getRemovementDate() {
		return removementDate;
	}

	/**
	 * @param removementDate the removementDate to set
	 */
	public void setRemovementDate(Date removementDate) {
		this.removementDate = UtilFormat.valueToString(removementDate, submitDateFormat);;
	}

	/**
	 * @return the transactionAmount
	 */
	public double getTransactionAmount() {
		return transactionAmount;
	}

	/**
	 * @param value the transactionAmount to set
	 */
	public void setTransactionAmount(Double value) {
		this.transactionAmount = UtilFormat.valueToDouble(value);
	}

	/**
	 * @return the transactionReference
	 */
	public String getTransactionReference() {
		return transactionReference;
	}

	/**
	 * @param transactionReference the transactionReference to set
	 */
	public void setTransactionReference(String transactionReference) {
		this.transactionReference = UtilFormat.valueToString(transactionReference);
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
	 * @return the transactionDate
	 */
	public String getTransactionDate() {
		return transactionDate;
	}

	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = UtilFormat.valueToString(transactionDate, submitDateFormat);
	}

	/**
	 * @return the shippingPhone
	 */
	public String getShippingPhone() {
		return shippingPhone;
	}

	/**
	 * @param shippingPhone the shippingPhone to set
	 */
	public void setShippingPhone(String shippingPhone) {
		this.shippingPhone = UtilFormat.valueToString(shippingPhone).replaceAll("\\D", "");
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
	 * @return the amountChequeEUR
	 */
	public double getAmountChequeEUR() {
		return amountChequeEUR;
	}

	/**
	 * @param amountChequeEUR the amountChequeEUR to set
	 */
	public void setAmountChequeEUR(Double amountChequeEUR) {
		this.amountChequeEUR = UtilFormat.valueToDouble(amountChequeEUR);
	}

	/**
	 * @return the atoutValidDate
	 */
	public String getAtoutValidDate() {
		return atoutValidDate;
	}

	/**
	 * @param atoutValidDate the atoutValidDate to set
	 */
	public void setAtoutValidDate(Date atoutValidDate) {
		this.atoutValidDate = UtilFormat.valueToString(atoutValidDate, submitDateFormat);
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
	 * @return the messageTransport
	 */
	public String getMessageTransport() {
		return messageTransport;
	}

	/**
	 * @param messageTransport the messageTransport to set
	 */
	public void setMessageTransport(String messageTransport) {
		this.messageTransport = UtilFormat.deleteCRLF(UtilFormat.valueToString(messageTransport));
	}

	/**
	 * @return the paymentCompany
	 */
	public String getPaymentCompany() {
		return paymentCompany;
	}

	/**
	 * @param paymentCompany the paymentCompany to set
	 */
	public void setPaymentCompany(String paymentCompany) {
		this.paymentCompany = UtilFormat.valueToString(paymentCompany);
	}

	/**
	 * @return the shippingCompany
	 */
	public String getShippingCompany() {
		return shippingCompany;
	}

	/**
	 * @param shippingCompany the shippingCompany to set
	 */
	public void setShippingCompany(String shippingCompany) {
		this.shippingCompany = UtilFormat.valueToString(shippingCompany);
	}

	/**
	 * @return the couponCode
	 */
	public String getPromotionName() {
		return promotionName;
	}

	/**
	 * @param couponCode the couponCode to set
	 */
	public void setPromotionName(String promotionName) {
		this.promotionName = UtilFormat.valueToString(promotionName);
	}

	/**
	 * @return the poidsPFT
	 */
	public String getPoidsPFT() {
		return poidsPFT;
	}

	/**
	 * @param value the poidsPFT to set
	 */
	public void setPoidsPFT(Long value) {
		this.poidsPFT = UtilFormat.valueToString(value);
	}

	/**
	 * @return the ammointPFT
	 */
	public double getAmmointPFT() {
		return ammointPFT;
	}

	/**
	 * @param ammointPFT the ammointPFT to set
	 */
	public void setAmmointPFT(Double ammointPFT) {
		this.ammointPFT = UtilFormat.valueToDouble(ammointPFT);
	}

	/**
	 * @return the poidsPFL
	 */
	public String getPoidsPFL() {
		return poidsPFL;
	}

	/**
	 * @param value the poidsPFL to set
	 */
	public void setPoidsPFL(Long value) {
		this.poidsPFL = UtilFormat.valueToString(value);
	}

	/**
	 * @return the ammointPFL
	 */
	public double getAmmointPFL() {
		return ammointPFL;
	}

	/**
	 * @param ammointPFL the ammointPFL to set
	 */
	public void setAmmointPFL(Double ammointPFL) {
		this.ammointPFL = UtilFormat.valueToDouble(ammointPFL);
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
	 * @return the pbAuthorization
	 */
	public String getPbAuthorization() {
		return pbAuthorization;
	}

	/**
	 * @param pbAuthorization the pbAuthorization to set
	 */
	public void setPbAuthorization(String pbAuthorization) {
		this.pbAuthorization = UtilFormat.valueToString(pbAuthorization);
	}

	/**
	 * @return the pbTransaction
	 */
	public String getPbTransaction() {
		return pbTransaction;
	}

	/**
	 * @param pbTransaction the pbTransaction to set
	 */
	public void setPbTransaction(String pbTransaction) {
		this.pbTransaction = UtilFormat.valueToString(pbTransaction);
	}

	/**
	 * @return the pbTransactionDate
	 */
	public String getPbTransactionDate() {
		return pbTransactionDate;
	}

	/**
	 * @param pbTransactionDate the pbTransactionDate to set
	 */
	public void setPbTransactionDate(Date pbTransactionDate) {
		this.pbTransactionDate = UtilFormat.valueToString(pbTransactionDate, submitDateFormat);
	}

	/**
	 * @return the pbPaymentDate
	 */
	public String getPbPaymentDate() {
		return pbPaymentDate;
	}

	/**
	 * @param pbPaymentDate the pbPaymentDate to set
	 */
	public void setPbPaymentDate(Date pbPaymentDate) {
		this.pbPaymentDate = UtilFormat.valueToString(pbPaymentDate, submitDateFormat);
	}

	/**
	 * @return the factureLocality
	 */
	public String getFactureLocality() {
		return factureLocality;
	}

	/**
	 * @param factureLocality the factureLocality to set
	 */
	public void setFactureLocality(String factureLocality) {
		this.factureLocality = factureLocality;
	}

	/**
	 * @return the facturePhoneNumber2
	 */
	public String getFacturePhoneNumber2() {
		return facturePhoneNumber2;
	}

	/**
	 * @param facturePhoneNumber2 the facturePhoneNumber2 to set
	 */
	public void setFacturePhoneNumber2(String facturePhoneNumber2) {
		this.facturePhoneNumber2 = facturePhoneNumber2;
	}

	/**
	 * @return the deliveryLocality
	 */
	public String getDeliveryLocality() {
		return deliveryLocality;
	}

	/**
	 * @param deliveryLocality the deliveryLocality to set
	 */
	public void setDeliveryLocality(String deliveryLocality) {
		this.deliveryLocality = deliveryLocality;
	}

	/**
	 * @return the deliveryPhoneNumber2
	 */
	public String getDeliveryPhoneNumber2() {
		return deliveryPhoneNumber2;
	}

	/**
	 * @param deliveryPhoneNumber2 the deliveryPhoneNumber2 to set
	 */
	public void setDeliveryPhoneNumber2(String deliveryPhoneNumber2) {
		this.deliveryPhoneNumber2 = deliveryPhoneNumber2;
	}

	/**
	 * @return the deliveryAddress3
	 */
	public String getDeliveryAddress3() {
		return deliveryAddress3;
	}

	/**
	 * @param deliveryAddress3 the deliveryAddress3 to set
	 */
	public void setDeliveryAddress3(String deliveryAddress3) {
		this.deliveryAddress3 = deliveryAddress3;
	}

	/**
	 * @return the addressFacture3
	 */
	public String getAddressFacture3() {
		return addressFacture3;
	}

	/**
	 * @param addressFacture3 the addressFacture3 to set
	 */
	public void setAddressFacture3(String addressFacture3) {
		this.addressFacture3 = addressFacture3;
	}
	
    /**
    * @return the castoramaCardNumber
    */
   public String getCastoramaCardNumber() {
       return castoramaCardNumber;
   }

    /**
    * @param castoramaCardNumber to set
    */
   public void setCastoramaCardNumber(String castoramaCardNumber) {
       this.castoramaCardNumber = castoramaCardNumber;
   }
}
