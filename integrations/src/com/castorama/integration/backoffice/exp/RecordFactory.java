package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import java.util.List;

import atg.nucleus.Nucleus;
import com.castorama.commerce.pricing.CastVATManager;
import com.castorama.constantes.CastoConstantesOrders;
import com.castorama.model.CastoSku;
import com.castorama.model.SkuLink;
import com.castorama.payment.PayboxPaymentStates;
import com.castorama.payment.PaymentStates;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

class RecordFactory {
	
	private static final DateFormat expireDateFormat = new SimpleDateFormat("yyMMdd HH:mm");

	private static final DateFormat paymentDateFormat = new SimpleDateFormat("yyyy-M-d HH:mm");
	
	private PayboxPaymentStates payboxPaymentStates;
	
	private PriceListManager mPriceListManager;

    private CastVATManager mCastVATManager;

	/**
	 * @param payboxPaymentStates
	 */
	public void setPayboxPaymentStates(PayboxPaymentStates payboxPaymentStates) {
		this.payboxPaymentStates = payboxPaymentStates;
	}

	/**
	 * @return the payboxPaymentStates
	 */
	public PayboxPaymentStates getPayboxPaymentStates() {
		return payboxPaymentStates;
	}
	
	private PaymentStates paymentStates;

	/**
	 * @return the paymentStates
	 */
	public PaymentStates getPaymentStates() {
		return paymentStates;
	}

	/**
	 * @param paymentStates the paymentStates to set
	 */
	public void setPaymentStates(PaymentStates paymentStates) {
		this.paymentStates = paymentStates;
	}
	
	/**
	 * @return the mPriceListManager
	 */
	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}

	/**
	 * @param priceListManager the mPriceListManager to set
	 */
	public void setPriceListManager(PriceListManager priceListManager) {
		mPriceListManager = priceListManager;
	}

    CastVATManager getCastVATManager() {
        return mCastVATManager;
    }

    void setCastVATManager(CastVATManager mCastVATManager) {
        this.mCastVATManager = mCastVATManager;
    }

    public COLRecord getCOLRecord(RepositoryItem item) {
		COLRecord result = new COLRecord();

		result.setOrderId((String) item.getPropertyValue("IDORDER"));
		result.setContactContent((String) item.getPropertyValue("CONTENU"));
		result.setContactDate((Date) item.getPropertyValue("DATECONTACT"));
		result.setLectureContactDate((Date) item.getPropertyValue("DATELECTURECLIENT"));
		result.setContactInitiator((Short) item.getPropertyValue("INITIATEURCONTACT_C659"));
		result.setContactReason((Short) item.getPropertyValue("CMOTIF_C618"));
		result.setContactNumber((Integer) item.getPropertyValue("NUMCONTACT"));
		result.setContactId((String) item.getPropertyValue("IDCONTACT"));		
		result.setContactType((Short) item.getPropertyValue("TYPECONTACT_C632"));
		result.setFileNumber((Integer) item.getPropertyValue("NUMDOSSIER"));
		
		return result;
	}

    @SuppressWarnings("unchecked")
    public CCERecord getCCERecord(RepositoryItem itm, RepositoryItem sipsItem, long totalPFT, long totalPFL) throws RecordParsingException {
        CCERecord result = new CCERecord();
        double VAT = mCastVATManager.getVATValue();
        try {
            RepositoryItem paymentGroup = null;
            Collection groups = (Collection) itm.getPropertyValue("paymentGroups");
            if (null != groups && !groups.isEmpty()) {
                paymentGroup = (RepositoryItem) groups.iterator().next();
            }

            if (null != paymentGroup) {
                getPaymentGroupProperties(paymentGroup, result);
            }

            RepositoryItem shippingGroup = null;
            groups = (Collection) itm.getPropertyValue("shippingGroups");
            if (null != groups && !groups.isEmpty()) {
                shippingGroup = (RepositoryItem) groups.iterator().next();
            }

            if (null != shippingGroup) {
                getShippingGroupProperties(shippingGroup, result, totalPFT, totalPFL);
            }

            if (null != sipsItem) {
                getSipsProperties(sipsItem, result);
            }

            result.setOrderId((String) itm.getPropertyValue("id"));
            result.setSubmited((Date) itm.getPropertyValue("submittedDate"));
            result.setClientCode((String) itm.getPropertyValue("profileId"));

            result.setAmountShippingCost((Double) itm.getPropertyValue("montantPFL"));
            result.setAmmointPFL((Double) itm.getPropertyValue("montantPFL"));
            double handlingPrice = UtilFormat.valueToDouble((Double) itm.getPropertyValue("processingFees"));
            result.setAmmointPFT(handlingPrice);

            RepositoryItem priceinfo = (RepositoryItem) itm.getPropertyValue("priceInfo");
            double priceInfoAmount = UtilFormat.valueToDouble((Double) priceinfo.getPropertyValue("amount"));

            Double amountTotalTTC_EU = priceInfoAmount + result.getAmountShippingCost() + handlingPrice;

            result.setAmountTotalTTC_EU(amountTotalTTC_EU);
            result.setAmountTotalHT_EU(amountTotalTTC_EU / (1 + VAT / 100));
            result.setTransactionAmount(result.getAmountTotalTTC_EU());

            double remize = 0.0;
            if (null != shippingGroup) {
                RepositoryItem shippingPriceInfo = (RepositoryItem) shippingGroup.getPropertyValue("priceInfo");

                Double noremize = ((Double) shippingPriceInfo.getPropertyValue("rawShipping"));
                Double totalArticles = (Double) priceinfo.getPropertyValue("rawSubTotal");

                remize = (null == totalArticles) ? 0.0 : totalArticles;
                remize += (null == noremize) ? 0.0 : noremize;
                remize -= amountTotalTTC_EU;
                remize += handlingPrice;
                remize = Math.abs(remize);
                remize += result.getAmmointPFL();
                remize -= (Double) shippingPriceInfo.getPropertyValue("rawShipping");
                remize = Math.abs(remize);
            }

            result.setRemise(remize);
            result.setComment((String) itm.getPropertyValue("Commentaire"));

            result.setRemovementDate((Date) itm.getPropertyValue("datesuppression"));
            result.setMessageTransport((String) itm.getPropertyValue("messageTransporteur"));
            
            result.setCastoramaCardNumber((String) itm.getPropertyValue("numCarteAtout_avtCodeReview"));
        } catch (RecordParsingException e) {
            throw e;
        } catch (Throwable e) {
            throw new RecordParsingException(e.getMessage(), e);
        }

        return result;
    }

    private void getSipsProperties(RepositoryItem sipsItem, CCERecord result) throws RecordParsingException {
		result.setAutorisationNumber((String) sipsItem.getPropertyValue("authorisation_id"));

		String date = (String) sipsItem.getPropertyValue("date_trans_expire");
		String time = (String) sipsItem.getPropertyValue("payment_time");
		
		if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
			try {
				result.setAuthExpire(expireDateFormat.parse(date + "01 " + time));
			} catch (ParseException  e) {
				throw new RecordParsingException("Illegal value for expire date");
			}
		} else {
			// we can add here throwing exception and break order export
		}
		
		result.setTransactionReference((String) sipsItem.getPropertyValue("transaction_id"));
		result.setTransactionSertificate((String) sipsItem.getPropertyValue("payment_certificate"));

		date = (String) sipsItem.getPropertyValue("payment_date");
		
		if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
			try {
				result.setTransactionDate(paymentDateFormat.parse(date + ' ' + time));
			} catch (ParseException  e) {
				throw new RecordParsingException("Illegal value for payment date");
			}
		} else {
			// we can add here throwing exception and break order export
		}
	}

	private void getShippingGroupProperties(RepositoryItem group, CCERecord record, long totalPFT, long totalPFL) throws RecordParsingException {
		String civ = (String) group.getPropertyValue("prefix");
		if ("mr".equalsIgnoreCase(civ)) {
			record.setDeliveryCode(1);
		} else if ("mrs".equalsIgnoreCase(civ)) {
			record.setDeliveryCode(2);
		} else if ("miss".equalsIgnoreCase(civ)) {
			record.setDeliveryCode(3);
		} else if ("organization".equalsIgnoreCase(civ)) {
			record.setDeliveryCode(5);
		} else {
			record.setDeliveryCode(null);
		}

		record.setDeliveryAddress1((String)group.getPropertyValue("address1"));
		record.setDeliveryAddress2((String)group.getPropertyValue("address2"));
		record.setDeliveryAddress3((String)group.getPropertyValue("address3"));
		record.setDeliveryCity((String)group.getPropertyValue("city"));
		record.setDeliveryNom((String)group.getPropertyValue("lastName"));
		record.setDeliveryPrenom((String)group.getPropertyValue("firstName"));
		record.setDeliveryPay((String)group.getPropertyValue("country"));
		record.setDeliveryPayCode((String)group.getPropertyValue("stateAddress"));
		record.setDeliveryZipCode((String)group.getPropertyValue("postalCode"));
		record.setShippingPhone((String)group.getPropertyValue("phoneNumber"));
		record.setShippingCompany((String)group.getPropertyValue("companyName"));
		record.setDeliveryPhoneNumber2((String)group.getPropertyValue("phoneNumber2"));
		record.setDeliveryLocality((String)group.getPropertyValue("locality"));
//**P19: change calculation of Weight. - to be deleted after the customer's check.
		record.setPoidsPFT(totalPFT/*(Integer) group.getPropertyValue("poidsPFT")*/);
		record.setPoidsPFL(totalPFL/*(Integer) group.getPropertyValue("poidsPFL")*/);
	}
	

	private void getPaymentGroupProperties(RepositoryItem group, CCERecord record) throws RecordParsingException {
		String civ = (String) group.getPropertyValue("prefix");
		if ("mr".equalsIgnoreCase(civ)) {
			record.setCFCode(1);
		} else if ("mrs".equalsIgnoreCase(civ)) {
			record.setCFCode(2);
		} else if ("miss".equalsIgnoreCase(civ)) {
			record.setCFCode(3);
		} else if ("organization".equalsIgnoreCase(civ)) {
			record.setCFCode(5);
		} else {
			record.setCFCode(null);
		}
		
		record.setAddressFacture1((String) group.getPropertyValue("address1"));
		record.setAddressFacture2((String)group.getPropertyValue("address2"));
		record.setAddressFacture3((String)group.getPropertyValue("address3"));
		record.setNomFacture((String)group.getPropertyValue("lastName"));
		record.setPrenomFacture((String)group.getPropertyValue("firstName"));
		record.setFactureZipCode((String)group.getPropertyValue("postalCode"));
		record.setFactureVille((String)group.getPropertyValue("city"));
		record.setFacturePayCode((String)group.getPropertyValue("stateAddress"));
		record.setFacturePays((String)group.getPropertyValue("country"));
		record.setPaymentPhone((String)group.getPropertyValue("phoneNumber"));
		record.setNumCheque((String)group.getPropertyValue("numcheque"));
		record.setLibelleBanque((String)group.getPropertyValue("libelleBanque"));
		record.setAmountChequeEUR((Double) group.getPropertyValue("montantChequeEuros"));
		record.setAtoutValidDate((Date) group.getPropertyValue("dateValidAtout"));
		record.setAtoutPaymenOption((String)group.getPropertyValue("optionPaiementAtout"));
		record.setPaymentCompany((String) group.getPropertyValue("companyName"));
		record.setTransactionId((String) group.getPropertyValue("idTransaction"));
		record.setPbAuthorization((String) group.getPropertyValue("numAutorisation"));
		record.setPbTransaction((String) group.getPropertyValue("numTransaction"));
		record.setPbTransactionDate((Date) group.getPropertyValue("dateTransaction"));
		record.setPbPaymentDate((Date) group.getPropertyValue("datePaiement"));
		record.setFacturePhoneNumber2((String)group.getPropertyValue("phoneNumber2"));
		record.setFactureLocality((String)group.getPropertyValue("locality"));

		String pm = (String) group.getPropertyValue("paymentMethod");
		record.setPaymentMethod(getPeymentMethodCode(pm));
	}
	
	public void getPromotionProperties(CCERecord cce, RepositoryItem order)
		throws RecordParsingException {
		String result = "";
		RepositoryItem priceInfo = (RepositoryItem)order.getPropertyValue("priceInfo");
		if (priceInfo != null && (Boolean)priceInfo.getPropertyValue("discounted")){
			List <RepositoryItem> ajustments = (List <RepositoryItem>) priceInfo.getPropertyValue("adjustments");
			for (Iterator<RepositoryItem> it = ajustments.iterator(); it.hasNext();){
				RepositoryItem ajustment = it.next();
				RepositoryItem promotion = (RepositoryItem)ajustment.getPropertyValue("pricingModel");
				if (promotion != null){
					String name = (String)promotion.getPropertyValue("displayName");
					result += name;
				}
			}
		}
		
		result = result.length() > 200 ? result.substring(0, 200) : result;
		cce.setPromotionName(result); 
	}
	
	private int getPeymentMethodCode(String method) {
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.CREDIT_CARD)))) {
			return 1;
		}
		
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.CALL_CENTER)))) {
			return 1;
		}
		
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.ATOUT)))) {
			return 2;
		}
		
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.CHEQUE)))) {
			return 3;
		}

		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.VIREMENT)))) {
			return 3;
		}
		
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.CADEAU)))) {
			return 5;
		}
		
		if (method.equalsIgnoreCase(
				getPaymentStates().getStateString(getPaymentStates().getStateValue(PaymentStates.MIX)))) {
			return 6;
		}
		
		return 0;
	}

	public CCPRecord getCCPRecord(RepositoryItem itm, CCERecord cce, RepositoryItem sipsitem) throws RecordParsingException {
		try {
			CCPRecord result = new CCPRecord();
			
			result.setOrderId(cce.getOrderId());
			int paymentMethod = getPeymentMethodCode((String)itm.getPropertyValue("paymentMethod"));
			result.setPaymentMethod("" + paymentMethod);
			

			if ( 3 == paymentMethod ) {
				result.setTransactionAmount(0.0);
			}
			
			if ( null != sipsitem ) {
				if ( 3 != paymentMethod ) {
					double amount = UtilFormat.valueToDouble((Number) sipsitem.getPropertyValue("montant"));
					amount = amount / 100;
					result.setTransactionAmount(amount);
				}
				
				String date = (String) sipsitem.getPropertyValue("date_trans_expire");
				String time = (String) sipsitem.getPropertyValue("payment_time");
				
				if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
					try {
						result.setAuthExpire(expireDateFormat.parse(date + "01 " + time));
					} catch (ParseException  e) {
						throw new RecordParsingException("Illegal value for expire date");
					}
				}

				date = (String) sipsitem.getPropertyValue("payment_date");
				if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
					try {
						result.setDateTransaction(paymentDateFormat.parse(date + ' ' + time));
					} catch (ParseException  e) {
						throw new RecordParsingException("Illegal value for payment date");
					}
				}
				
				result.setTransactionId((String) sipsitem.getPropertyValue("transaction_id"));
				result.setTransactionSertificate((String) sipsitem.getPropertyValue("payment_certificate"));
				result.setAutorisationNumber((String) sipsitem.getPropertyValue("authorisation_id"));
			}
			
			result.setNumCheque((String)itm.getPropertyValue("numcheque"));
			result.setLibelleBanque((String)itm.getPropertyValue("libelleBanque"));
			result.setAtoutValidDate((Date) itm.getPropertyValue("dateValidAtout"));
			result.setAtoutPaymenOption((String)itm.getPropertyValue("optionPaiementAtout"));
			result.setComment((String)itm.getPropertyValue("commentaireCheque"));

			return result;
		} catch (Throwable e) {
			throw new RecordParsingException(e.getMessage(), e);
		}
	}

	public List<CCPRecord> getCCPRecords(List<RepositoryItem> payments, CCERecord cce, List<RepositoryItem> sipsitems) throws RecordParsingException {
		try {
			List<CCPRecord> result = new ArrayList<CCPRecord>();
			
			for(RepositoryItem payment: payments) {
				int paymentMethod = getPeymentMethodCode((String)payment.getPropertyValue("paymentMethod"));
				if ( 6 == paymentMethod ) {
					if ( null != sipsitems ) {
						for (RepositoryItem sipsitem: sipsitems) {
							CCPRecord record = new CCPRecord(); 
							record.setOrderId(cce.getOrderId());
							
							double amount = UtilFormat.valueToDouble((Number) sipsitem.getPropertyValue("montant"));
							amount = amount / 100;
							record.setTransactionAmount(amount);
							
							String date = (String) sipsitem.getPropertyValue("date_trans_expire");
							String time = (String) sipsitem.getPropertyValue("payment_time");
							if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
								try {
									record.setAuthExpire(expireDateFormat.parse(date + "01 " + time));
								} catch (ParseException  e) {
									throw new RecordParsingException("Illegal value for expire date");
								}
							}
							date = (String) sipsitem.getPropertyValue("payment_date");
							if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
								try {
									record.setDateTransaction(paymentDateFormat.parse(date + ' ' + time));
								} catch (ParseException  e) {
									throw new RecordParsingException("Illegal value for payment date");
								}
							}
							record.setTransactionId((String) sipsitem.getPropertyValue("transaction_id"));
							record.setTransactionSertificate((String) sipsitem.getPropertyValue("payment_certificate"));
							record.setAutorisationNumber((String) sipsitem.getPropertyValue("authorisation_id"));
							
							record.setNumCheque((String)payment.getPropertyValue("numcheque"));
							record.setLibelleBanque((String)payment.getPropertyValue("libelleBanque"));
							record.setAtoutValidDate((Date) payment.getPropertyValue("dateValidAtout"));
							record.setAtoutPaymenOption((String)payment.getPropertyValue("optionPaiementAtout"));
							record.setComment((String)payment.getPropertyValue("commentaireCheque"));

							record.setPaymentMethod("" + getPaymentMeansCode(sipsitem.getPropertyValue("payment_means")));
								
							result.add(record);
						}
					}
				} else {
					CCPRecord record = new CCPRecord(); 
					record.setOrderId(cce.getOrderId());
					record.setPaymentMethod("" + paymentMethod);
					
					if ( 3 == paymentMethod ) {
						record.setTransactionAmount(cce.getTransactionAmount());
						cce.setTransactionAmount(0.0);
					}
					
					if ( null != sipsitems && 0 < sipsitems.size() ) {
						RepositoryItem sipsitem = sipsitems.get(0);
						if ( 3 != paymentMethod ) {
							double amount = UtilFormat.valueToDouble((Number) sipsitem.getPropertyValue("montant"));
							amount = amount / 100;
							record.setTransactionAmount(amount);
						}
						
						String date = (String) sipsitem.getPropertyValue("date_trans_expire");
						String time = (String) sipsitem.getPropertyValue("payment_time");
						
						if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
							try {
								record.setAuthExpire(expireDateFormat.parse(date + "01 " + time));
							} catch (ParseException  e) {
								throw new RecordParsingException("Illegal value for expire date");
							}
						}

						date = (String) sipsitem.getPropertyValue("payment_date");
						if (null != date && !"".equals(date) && null != time && !"".equals(time)) {
							try {
								record.setDateTransaction(paymentDateFormat.parse(date + ' ' + time));
							} catch (ParseException  e) {
								throw new RecordParsingException("Illegal value for payment date");
							}
						}
						
						record.setTransactionId((String) sipsitem.getPropertyValue("transaction_id"));
						record.setTransactionSertificate((String) sipsitem.getPropertyValue("payment_certificate"));
						record.setAutorisationNumber((String) sipsitem.getPropertyValue("authorisation_id"));
					}
					
					record.setNumCheque((String)payment.getPropertyValue("numcheque"));
					record.setLibelleBanque((String)payment.getPropertyValue("libelleBanque"));
					record.setAtoutValidDate((Date) payment.getPropertyValue("dateValidAtout"));
					record.setAtoutPaymenOption((String)payment.getPropertyValue("optionPaiementAtout"));
					record.setComment((String)payment.getPropertyValue("commentaireCheque"));
					result.add(record);
				}
			}
			return result;
		} catch (Throwable e) {
			throw new RecordParsingException(e.getMessage(), e);
		}
	}

	public int getPaymentMeansCode(Object pPaymentMeans) {
		int result = 0;
		if ( null != pPaymentMeans ) {
			String paymentMeans = pPaymentMeans.toString();
			if ( "AMEX".equalsIgnoreCase(paymentMeans) 
					|| "CB".equalsIgnoreCase(paymentMeans)
					|| "EUROCARD_MASTERCARD".equalsIgnoreCase(paymentMeans)
					|| "VISA".equalsIgnoreCase(paymentMeans) 
					|| "MASTERCARD".equalsIgnoreCase(paymentMeans)
					) {
				result = 1;
			} else if ("SOFINCO".equalsIgnoreCase(paymentMeans)) {
				result = 2;
			} else if ("SVS".equalsIgnoreCase(paymentMeans)) {
				result = 5;
			}
		}
		
		return result;
	}
	
	public List<CCLRecord> getCCLRecords(RepositoryItem commerseItem, Repository productRepository)
		throws RecordParsingException {
		
		try {
			List<CCLRecord> resultList = new ArrayList<CCLRecord>();
			
			String refId = (String) commerseItem.getPropertyValue("catalogRefId");
			CastoSku sku = CastoSku.getInstance(productRepository, refId);
				
			List<SkuLink> links = sku.getBundleLinks();
			
			if (null != links && !links.isEmpty()) {
				List<CastoSku> skuList = parsePackPrice(commerseItem, links);
				if (skuList != null) {
					for (int i=0; i < skuList.size(); i++) {
						resultList.add(getCCLRecord(commerseItem, skuList.get(i),
								UtilFormat.valueToLong(links.get(i).getQuantity())));
					}
				}
				/*for (SkuLink skuLink : links) {
					resultList.add(getCCLRecord(commerseItem, skuLink));
				}*/
			} else {
				resultList.add(getCCLRecord(commerseItem, sku, 1));
			}
			
			return resultList;
		} catch (RepositoryException e) {
			throw new RecordParsingException(e.getMessage(), e);
		}
	}

	private CCLRecord getCCLRecord(RepositoryItem commerceItem, CastoSku sku, long multiplier) 
		throws RecordParsingException {
		
		try {
			CCLRecord result = new CCLRecord();
			
			RepositoryItem order = (RepositoryItem) commerceItem.getPropertyValue("order");
			result.setOrderId((String) order.getPropertyValue("id"));
			
			Long quantity = ((Long) commerceItem.getPropertyValue("quantity")) * multiplier;
			result.setQuantity(quantity);
//			result.setQuantity(multiplier * result.getQuantity());

            // in old version: "GeneralementLivreEnHeures" (String) - delivery period in days.
            // in new version: "deliveryPeriod" (Integer) - delivery period in days.
            Integer deliveryPeriod = (Integer) commerceItem.getPropertyValue(CastoConstantesOrders.CITEM_PROPERTY_DELIVERY_PERIOD);
            if (null == deliveryPeriod) {
                result.setDelaiLivraison(0);
            } else if (-1 == deliveryPeriod) {
                result.setDelaiLivraison(60);
            } else if (-2 == deliveryPeriod) {
                result.setDelaiLivraison(15);
            } else {
                result.setDelaiLivraison(deliveryPeriod);
            }

            double someForf = 0.0;
			
			double montantFraisLivraisonNonRemise = 
				UtilFormat.valueToDouble((Number) order.getPropertyValue("montantFraisLivraisonNonRemise"));
				
			if ( 0.0 != montantFraisLivraisonNonRemise ) {
				double montantRemiseLivraisonEtMonteeEtage = 
					UtilFormat.valueToDouble((Number) order.getPropertyValue("montantRemiseLivraisonEtMonteeEtage"));
				
				double coeff = (montantFraisLivraisonNonRemise - 
						montantRemiseLivraisonEtMonteeEtage) / montantFraisLivraisonNonRemise;
				
				someForf = UtilFormat.valueToDouble((Number) commerceItem.getPropertyValue("sommeForfaitaire"));
				someForf *= (double)result.getQuantity();
				someForf *= coeff;
			}

			result.setSomeForf(someForf);
			
			if (null == sku) {
				return result;
			}
			
			result.setCodeArticle(sku.getCodeArticle());
			
			result.setLibelleArticle((String) sku.getPropertyValue("LibelleDescriptifArticle"));
			
			result.setUnits((String) sku.getPropertyValue("uniteUV"));

			result.setPoids((Integer) sku.getPropertyValue("poidsUV"));

			if (sku.getPrice() == null) {
				Double unitPrice = getUnitPrice(commerceItem, multiplier);
				if ( null != unitPrice ) {
					result.setPrice(unitPrice);
				}
			} else {
				result.setPrice(sku.getPrice());
			}
			
			boolean f = UtilFormat.valueToBoolean((Boolean) sku.getPropertyValue("LivraisonDirecteFournisseur"));
			if (f) {
				result.setLDF(1);
			} else {
				result.setLDF(0);
			}

			f = UtilFormat.valueToBoolean((Boolean) sku.getPropertyValue("horsNormes"));		
			if (f) {
				result.setHN(1);
			} else {
				result.setHN(0);
			}
			
			result.setFrais((Float) sku.getPropertyValue("fraisExceptionnel"));
			
			boolean expiration = UtilFormat.valueToBoolean((Boolean)commerceItem.getPropertyValue("exonerationPFT"));
			if (expiration) {
				result.setPoidsPFT(0);
			} else {
				result.setPoidsPFT((Integer) sku.getPropertyValue("poidsUV"));
			}
			
			expiration = UtilFormat.valueToBoolean((Boolean)commerceItem.getPropertyValue("exonerationPFE"));
			if (expiration) {
				result.setPoidsPFL(0);
			} else {
				result.setPoidsPFL((Integer) sku.getPropertyValue("poidsUV"));
			}
			return result;
			
		} catch (Throwable e) {
			throw new RecordParsingException("Export CCP record failure:" + e.getMessage(), e);
		}
	}
	
	/**
	 * Returns unit price.
	 * @return unit price.
	 */
	private Double getUnitPrice(RepositoryItem commerceItem, long multiplier) {
		Double result = null;
		Long quantity = (Long) commerceItem.getPropertyValue("quantity");
		RepositoryItem priceInfo = (RepositoryItem) commerceItem.getPropertyValue("priceInfo");
		if ( null != quantity && null != priceInfo ) {
			Double amount = (Double) priceInfo.getPropertyValue("amount");
            if (null != amount) {
                result = amount / (quantity * multiplier);
            }
        }
		return result;
	}

	public void getProfileProperties(CCERecord cce, RepositoryItem profile) throws RecordParsingException {
		cce.setLogin((String) profile.getPropertyValue("email"));
	}
	

	private List<CastoSku> parsePackPrice(RepositoryItem commerceItem, List<SkuLink> list) {
		List<CastoSku> skuList = null;
		RepositoryItem priceInfo = (RepositoryItem) commerceItem.getPropertyValue("priceInfo");
		if ( null != priceInfo ) {
			Double price = null;
			Double skuPriceTotal = 0D;
			Double totalPrice = getUnitPrice(commerceItem, 1);
			
			skuList = new ArrayList<CastoSku>();
			for (SkuLink link : list) {
				skuList.add(link.getItem());
			}
			Boolean onSale = (Boolean) priceInfo.getPropertyValue("onSale");
			for (CastoSku sku : skuList) {
				price = null;
				if (null != onSale && onSale) {
					Boolean onSaleSku = (Boolean) sku.getPropertyValue("onSale");
					if (onSaleSku != null && onSaleSku) {
						price = getSkuPrice(sku, "salePriceList");
					} else {
						price = getSkuPrice(sku, "priceList");
					}
				} else {
					price = getSkuPrice(sku, "priceList");
				}
				if (price != null) {
					sku.setPrice(price);
					skuPriceTotal += price;
				}
			}
			
			if (skuPriceTotal > totalPrice) {
				double delta = (skuPriceTotal - totalPrice)/totalPrice;
				double prt = 0.0;
				for (int i=0; i < skuList.size(); i++) {
					CastoSku cs = skuList.get(i);
					Double p1 = null;
					if (i == skuList.size() - 1) {
						p1 = totalPrice - prt;
					} else {
						Double p = cs.getPrice();
						p1 = p - (delta * p);
						prt += p1;
					}
					cs.setPrice(p1);
				}
			}
		}
		return skuList;
	}
	
	private Double getSkuPrice(CastoSku skuItem, String propertyName) {
		Double result = null;
		try {
			RepositoryItem priceList = getPriceListManager().getPriceList(null, propertyName, true);
			if (priceList != null) {
				RepositoryItem price = getPriceListManager().getPrice(priceList, null, skuItem.getRepositoryItem());
	            
	            if (price != null) {
	                Object listPrice = price.getPropertyValue("listPrice");
	                if (listPrice != null) {
		                result = (Double)listPrice;
	                }
	            }
			}
			
		} catch (PriceListException e) {
            result = null;
		}

		return result;
	}
	
}
