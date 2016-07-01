package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Extends DynamoServlet to returns payment amounts by gift card and credit card. 
 * 
 * @author Vasili_Ivus
 *
 */
public class OrderPaymentDroplet extends DynamoServlet {

	/*
	 * credit+gift:
	 * Carte cadeau / Gift Card
	 * Carte bleue / Carte  Blue
	 * Carte visa / Visa Card
	 * Carte Eurocard Mastercard / Master Card
	 * Carte American Express/ American Express Card
	 * money transfer:
	 * Virement Bancaire : en attente de paiement’ 
	 * sofinco:
	 * Carte l’Atout’ : [la somme du paiement retourné pour carte l’Atout] date amount
	 * 
	 * Téléphone : en attente de paiement’ 
	 * 
	 * Chèque : en attente de paiement’ 
	 */
	/** Output name of order was paid by Eurocard. */
	private static final String CARTE_EUROCARD_MASTERCARD_PAYMENT_TYPE = "Carte Eurocard Mastercard";
	/** Output name of order was paid by American Express card. */
	private static final String CARTE_AMERICAN_EXPRESS_PAYMENT_TYPE = "Carte American Express";
	/** Output name of order was paid by Visa card. */
	private static final String CARTE_VISA_PAYMENT_TYPE = "Carte visa";
	/** Output name of order was paid by bleue card. */
	private static final String CARTE_BLEUE_PAYMENT_TYPE = "Carte bleue";
	/** Output name of order was paid by cadeau card. */
	private static final String CARTE_CADEAU_PAYMENT_TYPE = "Carte cadeau";
	/** Output name of order was paid by Castorama (Atout) card. */
	private static final String CARTE_CASTORAMA_PAYMENT_TYPE = "Carte Castorama";
	/** ORDER_ID constant. */
	private static final String ORDER_ID = "orderId";
	/** PAYMENT_METHOD constant. */
	private static final String PAYMENT_METHOD = "paymentMethod";
	/** OUTPUT constant. */
	private static final String OUTPUT = "output";
	/** PAYMENT_DESCRIPTION constant. */
	public final static String PAYMENT_DESCRIPTION = "paymentDescription";
	/** PAYMENT_AMOUNT constant. */
	public final static String PAYMENT_AMOUNT = "paymentAmount";
	/** PAYMENT_TYPE constant. */
	public final static String PAYMENT_TYPE = "paymentType";
	/** INDEX constant. */
	public final static String INDEX = "index";
	/** SIZE constant. */
	public final static String SIZE = "size";
	/** OUTPUT_START constant. */
	public final static String OUTPUT_START = "outputStart";
	/** OUTPUT_END constant. */
	public final static String OUTPUT_END = "outputEnd";
	/** EMPTY constant. */
	public final static String EMPTY = "empty";

	/** LOG_SIPS constant. */
	private static final String LOG_SIPS = "logsips";
	/** PROPERTTY_ORDER_ID constant. */
	private static final String PROPERTTY_ORDER_ID = "order_id";
	/** PROPERTTY_RESPONSE_CODE constant. */
	private static final String PROPERTTY_RESPONSE_CODE = "response_code";
	/** PROPERTTY_AMOUNT constant. */
	private static final String PROPERTTY_AMOUNT = "montant";
	/** PROPERTTY_PAYMENT_TYPE constant. */
	private static final String PROPERTTY_PAYMENT_TYPE = "payment_means";
	/** OK_RESPONSE_CODE constant. */
	private static final String OK_RESPONSE_CODE = "00000";
	/** Repository property. */
	private Repository mRepository;
	/** Gift card types property. */
	private List<String> mGiftCardTypes;
	/** Credit card types property. */
	private List<String> mCreditCardTypes;

	/** Extends service method of DynamoServlet to returns payment amounts by gift card and credit card.
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws ServletException, IOException {
		
		boolean isEmpty = true;
		String paymentMethod = pRequest.getParameter(PAYMENT_METHOD);
		if ( null != paymentMethod && 0 < paymentMethod.trim().length() ) {
			List<OrderPaymentElement> elements = new ArrayList<OrderPaymentElement>();
			if ( paymentMethod.equalsIgnoreCase("Call-Center") ) {
				elements.add(new OrderPaymentElement("Téléphone", "en attente de paiement", null));
			} else if ( paymentMethod.equalsIgnoreCase("Virement") ) {
				elements.add(new OrderPaymentElement("Virement Bancaire", "en attente de paiement", null));
			} else if ( paymentMethod.equalsIgnoreCase("Cheque") ) {
				elements.add(new OrderPaymentElement("Chèque", "en attente de paiement", null));
			} else if ( paymentMethod.equalsIgnoreCase("Atout") ) {
				String orderId = pRequest.getParameter(ORDER_ID);
				Repository repository = getRepository();
				long sofincoAmount = 0;
				if ( null != repository ) {
					try {
		                RepositoryView logsipsView = repository.getView(LOG_SIPS);
		                QueryBuilder queryBuilder = logsipsView.getQueryBuilder();
		                QueryExpression propertyQuery = queryBuilder.createPropertyQueryExpression(PROPERTTY_ORDER_ID);
		                QueryExpression valueQuery = queryBuilder.createConstantQueryExpression(orderId);
		                Query query = queryBuilder.createComparisonQuery(propertyQuery, valueQuery, QueryBuilder.EQUALS);
		                RepositoryItem[] logsipsItems = logsipsView.executeQuery(query);
		                String paymentType;
		                String responseCode;
		                Integer amount;
		                if ( null != logsipsItems && 0 < logsipsItems.length ) {
		                    for (RepositoryItem logsipsItem : logsipsItems) {
		                        responseCode = (String) logsipsItem.getPropertyValue(PROPERTTY_RESPONSE_CODE);
		                        if ( isErrorCodeOK(responseCode) ) {
		                        	amount = (Integer) logsipsItem.getPropertyValue(PROPERTTY_AMOUNT);
		                        	if ( null != amount ) {
		                        		paymentType = (String) logsipsItem.getPropertyValue(PROPERTTY_PAYMENT_TYPE);
		                        		if ( "SOFINCO".equalsIgnoreCase(paymentType) ) {
		                        			sofincoAmount += amount; 
		                        		}
		                        	}
		                        }
		                    }
		                }
					} catch (Exception e) {
						if ( isLoggingError() ) {
							logError(e);
						}
					}
				}
				elements.add(new OrderPaymentElement(CARTE_CASTORAMA_PAYMENT_TYPE, null, new Double(sofincoAmount/100.0)));
			} else {
				String orderId = pRequest.getParameter(ORDER_ID);
				Repository repository = getRepository();
				long giftAmount = 0;
				long blueAmount = 0;
				long visaAmount = 0;
				long amexAmount = 0;
				long euroAmount = 0;
				if ( null != repository ) {
					try {
		                RepositoryView logsipsView = repository.getView(LOG_SIPS);
		                QueryBuilder queryBuilder = logsipsView.getQueryBuilder();
		                QueryExpression propertyQuery = queryBuilder.createPropertyQueryExpression(PROPERTTY_ORDER_ID);
		                QueryExpression valueQuery = queryBuilder.createConstantQueryExpression(orderId);
		                Query query = queryBuilder.createComparisonQuery(propertyQuery, valueQuery, QueryBuilder.EQUALS);
		                RepositoryItem[] logsipsItems = logsipsView.executeQuery(query);
		                String paymentType;
		                String responseCode;
		                Integer amount;
		                if ( null != logsipsItems && 0 < logsipsItems.length ) {
		                    for (RepositoryItem logsipsItem : logsipsItems) {
		                        responseCode = (String) logsipsItem.getPropertyValue(PROPERTTY_RESPONSE_CODE);
		                        if ( isErrorCodeOK(responseCode) ) {
		                        	amount = (Integer) logsipsItem.getPropertyValue(PROPERTTY_AMOUNT);
		                        	if ( null != amount ) {
		                        		paymentType = (String) logsipsItem.getPropertyValue(PROPERTTY_PAYMENT_TYPE);
		                        		if ( "SVS".equalsIgnoreCase(paymentType) ) {
		                        			giftAmount += amount; 
		                        		} else if ( "CB".equalsIgnoreCase(paymentType) ) {
		                        			blueAmount += amount;
		                        		} else if ( "VISA".equalsIgnoreCase(paymentType) ) {
		                        			visaAmount += amount;
		                        		} else if ( "AMEX".equalsIgnoreCase(paymentType) ) {
		                        			amexAmount += amount;
		                        		} else if ( "EUROCARD_MASTERCARD".equalsIgnoreCase(paymentType) ) {
		                        			euroAmount += amount;
		                        		} else if ( "MASTERCARD".equalsIgnoreCase(paymentType) ) {
		                        			euroAmount += amount;
		                        		}
		                        		
		                        	}
		                        }
		                    }
		                }
						if ( 0 != giftAmount ) {
							elements.add(new OrderPaymentElement(CARTE_CADEAU_PAYMENT_TYPE, null, new Double(giftAmount/100.0)));
						}
						if ( 0 != blueAmount ) {
							elements.add(new OrderPaymentElement(CARTE_BLEUE_PAYMENT_TYPE, null, new Double(blueAmount/100.0)));
						}
						if ( 0 != visaAmount ) {
							elements.add(new OrderPaymentElement(CARTE_VISA_PAYMENT_TYPE, null, new Double(visaAmount/100.0)));
						}
						if ( 0 != amexAmount ) {
							elements.add(new OrderPaymentElement(CARTE_AMERICAN_EXPRESS_PAYMENT_TYPE, null, new Double(amexAmount/100.0)));
						}
						if ( 0 != euroAmount ) {
							elements.add(new OrderPaymentElement(CARTE_EUROCARD_MASTERCARD_PAYMENT_TYPE, null, new Double(euroAmount/100.0)));
						}
						
					} catch (Exception e) {
						if ( isLoggingError() ) {
							logError(e);
						}
					}
				}
			}
			
			int count = 0;
			if ( 0 < elements.size() ) {
			    pRequest.setParameter(SIZE, elements.size());
			    pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
			    Iterator<OrderPaymentElement> iterator = elements.iterator(); 
			    while (iterator.hasNext() ) {
			    	pRequest.setParameter(INDEX, count);
			    	OrderPaymentElement element = iterator.next();
			    	pRequest.setParameter(PAYMENT_TYPE, element.type);
			    	pRequest.setParameter(PAYMENT_DESCRIPTION, element.description);
			    	pRequest.setParameter(PAYMENT_AMOUNT, element.amount);
			        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			        count++;
			   }
			   pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
			   isEmpty = false;
			}
		}
        if ( isEmpty ) {
        	pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
	}

	/**
	 * Checks when response code is ok.
	 * 
	 * @param pErrorCode response code.
	 * @return true when response code is ok, otherwise false.
	 */
	protected boolean isErrorCodeOK(String pErrorCode) {
		if ( null != pErrorCode ) {
			return OK_RESPONSE_CODE.equalsIgnoreCase(pErrorCode);
		}
		return false;
	}
	
	/**
	 * Checks when payment is gift type.
	 * 
	 * @param pPaymentType payment type.
	 * @return true when payment is gift type, otherwise false.
	 */
	protected boolean isGiftPayment(String pPaymentType) {
		return getGiftCardTypes().contains(pPaymentType);
	}

	/**
	 * Checks when payment is credit type.
	 * 
	 * @param pPaymentType payment type.
	 * @return true when payment is credit type, otherwise false.
	 */
	protected boolean isCreditPayment(String pPaymentType) {
		return getCreditCardTypes().contains(pPaymentType);
	}
		
	/**
	 * Returns the repository.
	 * @return the repository.
	 */
	public Repository getRepository() {
		return mRepository;
	}

	/**
	 * Sets repository.
	 * @param pRepository the repository to set.
	 */
	public void setRepository(Repository pRepository) {
		this.mRepository = pRepository;
	}

	/**
	 * Returns gift card types list.
	 * @return the list of gift card types.
	 */
	public List<String> getGiftCardTypes() {
		return mGiftCardTypes;
	}

	/**
	 * Sets the list of gift card types.
	 * @param pGiftCardTypes the list of gift card types to set.
	 */
	public void setGiftCardTypes(List<String> pGiftCardTypes) {
		this.mGiftCardTypes = pGiftCardTypes;
	}

	/**
	 * Returns the list of credit card types. 
	 * @return the list of credit card types.
	 */
	public List<String> getCreditCardTypes() {
		return mCreditCardTypes;
	}

	/**
	 * Sets the list of credit card types.
	 * @param pCreditCardTypes the list of credit card types to set.
	 */
	public void setCreditCardTypes(List<String> pCreditCardTypes) {
		this.mCreditCardTypes = pCreditCardTypes;
	}
	
	class OrderPaymentElement {
		String type;
		String description;
		Double amount;
		OrderPaymentElement (String type, String description, Double amount) {
			this.type = type;
			this.description = description;
			this.amount = amount;
		}
	}
	
	
}
