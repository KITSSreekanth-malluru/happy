package com.castorama.servlet;

import static com.castorama.commerce.profile.Constants.DESCRIPTOR_NAME_PROFILE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.states.OrderStates;
import atg.core.util.StringUtils;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileTools;
import atg.repository.Query;

import com.castorama.commerce.order.CastOrderDetails;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.pricing.CastoShippingTools;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.mail.MailSender;
import com.castorama.payment.PayboxParametersConfiguration;
import com.castorama.payment.PayboxPaymentStates;
import com.castorama.payment.PaymentStates;
import com.castorama.utils.CastLoggingHelper;

/**
 * PayboxLogServlet extension for order submission.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastPayboxLogServlet extends PayboxLogServlet {
    /** NEED_UPDATE_INVENTORY constant. */
    private static final String NEED_UPDATE_INVENTORY = "needUpdateInventory";
    private static final String MOUNT_PARAMETER="montant";
    private static final String AUTO_PARAMETER="auto";
    private static final String ORDER_ID_PARAMETER="ref";
    private static final String TRANSACTION_PARAMETER_VALUE="XXXXXX";
    private static final String ACCEPTED_TRANSACTION_CODE="00000";
    private static final String ERROR_PARAMETER="erreur";
    private static final String LOGSIPS="logsips";
    private static final String ORDER_COLUMN="order_id";
    private static final String CARTE="carte";
    private static final String SOFINCO="SOFINCO";
    private static final String SVS="SVS";
    private static final String MONTANT="montant";
    private static final String ID="id";
    private static final String RESPONSE_CODE = "response_code";
    /* properties */
    /** mMailSender property */
    private MailSender mMailSender;
    /** profileTools property */
    private ProfileTools mProfileTools;
    /** orderManager property */
    private CastOrderManager mOrderManager;
    /** orderStates property */
    private OrderStates mOrderStates;
    /** bOOrderStates property */
    private BOOrderStates mBOOrderStates;
    /** shoppingCartComponentName property */
    private String mShoppingCartComponentName;
    /** orderIdQueryParameterName property */
    private String mOrderIdQueryParameterName;
    /** payboxPaymentStates property */
    private PayboxPaymentStates mPayboxPaymentStates;
    /** paymentStates property */
    private PaymentStates mPaymentStates;
    /** shippingTools property */
    private CastoShippingTools mShippingTools = null;
    private PayboxParametersConfiguration pConfiguration;
    private CastLoggingHelper castLoggingHelper;

    public PayboxParametersConfiguration getConfiguration() {
		return pConfiguration;
	}

    public void setConfiguration(PayboxParametersConfiguration pConfiguration) {
		this.pConfiguration = pConfiguration;
	}

    /**
     * Returns payboxPaymentStates property.
     *
     * @return payboxPaymentStates property.
     */
    public PayboxPaymentStates getPayboxPaymentStates() {
        return mPayboxPaymentStates;
    }

    /**
     * Sets the value of the payboxPaymentStates property.
     *
     * @param pPayboxPaymentStates parameter to set.
     */
    public void setPayboxPaymentStates(PayboxPaymentStates pPayboxPaymentStates) {
        mPayboxPaymentStates = pPayboxPaymentStates;
    }

    /**
     * Returns paymentStates property.
     *
     * @return paymentStates property.
     */
    public PaymentStates getPaymentStates() {
        return mPaymentStates;
    }

    /**
     * Sets the value of the paymentStates property.
     *
     * @param pPaymentStates parameter to set.
     */
    public void setPaymentStates(PaymentStates pPaymentStates) {
        mPaymentStates = pPaymentStates;
    }

    /**
     * Returns profileTools property.
     *
     * @return profileTools property.
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Sets the value of the profileTools property.
     *
     * @param pProfileTools parameter to set.
     */
    public void setProfileTools(ProfileTools pProfileTools) {
        mProfileTools = pProfileTools;
    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(CastOrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

    /**
     * Returns orderStates property.
     *
     * @return orderStates property.
     */
    public OrderStates getOrderStates() {
        return mOrderStates;
    }

    /**
     * Sets the value of the orderStates property.
     *
     * @param pOrderStates parameter to set.
     */
    public void setOrderStates(OrderStates pOrderStates) {
        mOrderStates = pOrderStates;
    }

    /**
     * Returns BOOrderStates property.
     *
     * @return BOOrderStates property.
     */
    public BOOrderStates getBOOrderStates() {
        return mBOOrderStates;
    }

    /**
     * Sets the value of the BOOrderStates property.
     *
     * @param pOrderStates parameter to set.
     */
    public void setBOOrderStates(BOOrderStates pOrderStates) {
        mBOOrderStates = pOrderStates;
    }

    /**
     * Returns shoppingCartComponentName property.
     *
     * @return shoppingCartComponentName property.
     */
    public String getShoppingCartComponentName() {
        return mShoppingCartComponentName;
    }

    /**
     * Sets the value of the shoppingCartComponentName property.
     *
     * @param pShoppingCartComponentName parameter to set.
     */
    public void setShoppingCartComponentName(String pShoppingCartComponentName) {
        mShoppingCartComponentName = pShoppingCartComponentName;
    }

    /**
     * Returns orderIdQueryParameterName property.
     *
     * @return orderIdQueryParameterName property.
     */
    public String getOrderIdQueryParameterName() {
        return mOrderIdQueryParameterName;
    }

    /**
     * Sets the value of the orderIdQueryParameterName property.
     *
     * @param pOrderIdQueryParameterName parameter to set.
     */
    public void setOrderIdQueryParameterName(String pOrderIdQueryParameterName) {
        mOrderIdQueryParameterName = pOrderIdQueryParameterName;
    }

    private int getTransactionsCount(DynamoHttpServletRequest pRequest) {
        int result = 1;

    	if ( isLoggingInfo() ) {
            logInfo("getTransactionsCount(request) - start: " + pRequest.getQueryString());
        }

    	String index = pRequest.getParameter("index");

    	if ( isLoggingInfo() ) {
            logInfo("getTransactionsCount(request) - index: " + index + " - " + pRequest.getQueryString());
        }

    	if ( null != index && 0 < index.trim().length() ) {
            String[] indexes = index.split("-");
            if ( null != indexes && 2 <= indexes.length ) {
            	try {
            		result = Integer.parseInt(indexes[1]);
            		if ( 0 == result ) {
            			result = 1;
            		}
            	} catch (Exception e) {
            		if ( isLoggingDebug() ) {
            			logDebug(e);
            		}
            	}
            }
        }

    	if ( isLoggingInfo() ) {
            logInfo("getTransactionsCount(request) - finish: " + result + " - " + pRequest.getQueryString());
        }

    	return result;
    }

    public RepositoryItem[] getAllOrdersWithId(String orderId) {
        RepositoryItem[] orderItem = null;
        try {
            RepositoryItemDescriptor logSipsOrdersDescriptor = getRepository().getItemDescriptor(LOGSIPS);
            RepositoryView orderView = logSipsOrdersDescriptor.getRepositoryView();
            QueryBuilder orderBuilder = orderView.getQueryBuilder();
            Query getOrderQuery = orderBuilder.createAndQuery(new Query[]{
                    orderBuilder.createComparisonQuery(
                            orderBuilder.createPropertyQueryExpression(ORDER_COLUMN),
                            orderBuilder.createConstantQueryExpression(orderId),
                            QueryBuilder.EQUALS),
                    orderBuilder.createComparisonQuery(
                            orderBuilder.createPropertyQueryExpression(RESPONSE_CODE),
                            orderBuilder.createConstantQueryExpression(ACCEPTED_TRANSACTION_CODE),
                            QueryBuilder.EQUALS)
            });
            orderItem = orderView.executeQuery(getOrderQuery);
            if (isLoggingDebug() && orderItem!=null){
                StringBuilder builder = new StringBuilder();
                for (RepositoryItem item: orderItem){
                    builder.append("item[id - ")
                            .append(item.getPropertyValue(ID))
                            .append("; amount - ")
                            .append(item.getPropertyValue(MONTANT))
                            .append("] ");
                }
                vlogDebug("Was queried {0} LogSips items: {1}", orderItem.length, builder.toString());
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
        }
        return orderItem;
    }

    /**
     * Submit order in case of successful payment.
     *
     * @param pRequest  - http request
     * @param pResponse - http response
     *
     * @return true if success otherwise false
     */
    @Override
    protected boolean postWriteReport(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        boolean result = false;
        String orderId = null;
        String orderIdQueryParameterName = getOrderIdQueryParameterName().trim();
        if (!StringUtils.isBlank(orderIdQueryParameterName))
            orderId = pRequest.getParameter(orderIdQueryParameterName);
        try {
            if (isLoggingInfo()) {
                logInfo("postWriteReport(request) - start: " + pRequest.getQueryString());
            }

            // Checks when "order id query parameter name" property is not empty.
            CastOrderManager com = getOrderManager();

            if (isLoggingInfo()) {
                logInfo("postWriteReport(request) - OrderManager is set: " + com);
            }

            if (null != com) {
                if (isLoggingInfo()) {
                    logInfo("postWriteReport(request) - orderIdQueryParameterName: " + orderIdQueryParameterName);
                }

                if (!StringUtils.isBlank(orderIdQueryParameterName)) {

                    if (isLoggingInfo()) {
                        logInfo("postWriteReport(request) - orderId: " + orderId);
                    }
                    // Checks when "order id" have been received from request.
                    if (!StringUtils.isBlank(orderId) && isErrorCodeOK(pRequest.getParameter("erreur"))) {
                        if (isLoggingInfo()) {
                            logInfo("postWriteReport(" + orderId + ") - isErrorCodeOK() passed: " + orderId);
                        }

                        Integer transactionNumber = com.increaseTransactionNumber(orderId);
                        if (isLoggingInfo()) {
                            logInfo("postWriteReport(" + orderId + ") - transactionNumber: " + transactionNumber);
                        }

                        if (null != transactionNumber && getTransactionsCount(pRequest) == transactionNumber.intValue()) {
                            if (isLoggingInfo()) {
                                logInfo("postWriteReport(" + orderId + ") - before submitOrder()");
                            }

                            //TODO why don't use without lock - it's OK (lock user earlier in PayboxLockServlet.java line 186)
                            //getting order if from request
                            String order = pRequest.getParameter(ORDER_ID_PARAMETER);
                            //getting order mount from request
                            String currentAmount = pRequest.getParameter(MOUNT_PARAMETER);
                            //getting type peiment from request
                            String typePaiment = pRequest.getParameter(CARTE);
                            //getting from orderRepository order by id
                            RepositoryItem orderItem = com.getOrderFO(order);
                            //checking amount order id in LogSips repository
                            RepositoryItem[] orderLogsipsItems = getAllOrdersWithId(order);

                            //checking order mount from Order Repository
                            CastOrderDetails castOrderDetails = new CastOrderDetails(orderItem, com);
                            String orderAmountString = String.format("%.2f", castOrderDetails.getOrderTotal()).replace(".", "");

                            if (isLoggingDebug()) {
                                vlogDebug("Real order amount as string: {0}. Paybox response order amount {1}",
                                        orderAmountString, currentAmount);
                            }

                            boolean isAmountMatch = false;
                            if (orderLogsipsItems != null) {
                                isAmountMatch = checkMount(orderLogsipsItems, orderAmountString);
                            }

                            if (isLoggingDebug()) {
                                vlogDebug("Is order amount is match: {0}", isAmountMatch);
                            }

                            String auto = pRequest.getParameter(AUTO_PARAMETER);
                            String errorCode = pRequest.getParameter(ERROR_PARAMETER);
                            boolean testMode = pConfiguration.getTestMode();
                            boolean isSign = isCorrectSign(pRequest);

                            if (isLoggingDebug()) {
                                vlogDebug("Order process parameters info: orderId - {0}, paymentType - {1}," +
                                        " auto - {2}, errorCode - {3}, testMode - {4}"
                                        , order, typePaiment, auto, errorCode, testMode);
                            }

                            preSubmitLogOrderInfo(orderId);

                            if (auto != null && (testMode || !auto.equals(TRANSACTION_PARAMETER_VALUE))
                                    && errorCode.equals(ACCEPTED_TRANSACTION_CODE) && isSign) {
                                if (isAmountMatch) {
                                    com.setPendingPayboxStateFO(orderId, false);
                                    result = submitOrder(orderId, pRequest, com);
                                    if (isLoggingInfo()) {
                                        logInfo("postWriteReport(" + orderId + ") - after submitOrder(): " + result);
                                    }
                                    postSubmitLogOrderInfo(orderId);
                                } else {
                                    if ((typePaiment.equals(SOFINCO) || typePaiment.equals(SVS))) {
                                        com.setPendingPayboxStateFO(orderId, false);
                                    } else {
                                        com.setFailedPayboxStateFO(orderId, false);
                                        result = false;
                                    }
                                }
                            } else {
                                com.setFailedPayboxStateFO(orderId, false);
                                result = false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(ORDER_ID_MESS + orderId, e);
            }
        }  // end try-catch

        if (isLoggingInfo()) {
            logInfo("postWriteReport(request) - finish: " + result + " - " + pRequest.getQueryString());
        }

        return result;
    }

    public boolean checkMount(RepositoryItem[] orderMount, String castOrderAmountString) {
        int mountFromRepository = 0;
        if (orderMount != null) {
            for (RepositoryItem item : orderMount) {
                mountFromRepository += (Integer) item.getPropertyValue(MONTANT);
            }
            String logSipsTotalAmountAsString = Integer.toString(mountFromRepository);
            if (isLoggingDebug()){
                vlogDebug("Order amount from LogSips repository as string: {0}", logSipsTotalAmountAsString);
            }
            if (logSipsTotalAmountAsString.equals(castOrderAmountString)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Send order email notification.
     *
     * @param pRequest  - http request
     * @param pResponse - http response
     *
     * @return true if success otherwise false
     */
    @Override
    protected boolean postServiceAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    	boolean result = false;

    	if ( isLoggingInfo() ) {
            logInfo("postServiceAction(request) - start: " + pRequest.getQueryString());
        }
    	String orderId=null;
    	String orderIdQueryParameterName = getOrderIdQueryParameterName().trim();
    	if ( !StringUtils.isBlank(orderIdQueryParameterName) )
            orderId = pRequest.getParameter(orderIdQueryParameterName);
    	try {
            // 
            // Checks when "order id query parameter name" property is not empty.
            //
            CastOrderManager com = getOrderManager();

            if ( isLoggingInfo() ) {
                logInfo("postServiceAction(request) - OrderManager is set: " + com);
            }

            if ( null != com ) {


            	if ( isLoggingInfo() ) {
                    logInfo("postServiceAction(request) - orderIdQueryParameterName: " + orderIdQueryParameterName);
                }

            	if ( !StringUtils.isBlank(orderIdQueryParameterName) ) {

            		if ( isLoggingInfo() ) {
                        logInfo("postServiceAction(request) - orderId: " + orderId);
                    }
            		//
            		// Checks when "order id" have been received from request.
            		//
            		if ( !StringUtils.isBlank(orderId) ) {

            			Order order = null;
            	    	try {
            				order = com.loadOrder(orderId);
            	    	} catch (CommerceException e) {
            	            if (isLoggingError()) {
            	                logError(ORDER_ID_MESS + orderId, e);
            	            }
            			}

            			if ( isLoggingInfo() ) {
            	            logInfo("postServiceAction(" + orderId + ") - order is retrieved: " + order);
            	        }

            			if ( null != order ) {
            	            String paymentMethod = updatePaymentMethod(order, pRequest, false);

            	    		if ( isLoggingInfo() ) {
            	                logInfo("postServiceAction(" + orderId + ") - paymentMethod: " + paymentMethod);
            	            }

	                        RepositoryItem profile = null;
	            			try {
	            				profile = getProfileTools().getProfileRepository().getItem(order.getProfileId(),
	            				                                                     DESCRIPTOR_NAME_PROFILE);
	            			} catch (RepositoryException e) {
	            	            if (isLoggingError()) {
	            	                logError(ORDER_ID_MESS + orderId, e);
	            	            }
	            			}

            	    		if ( isLoggingInfo() ) {
            	                logInfo("postServiceAction(" + orderId + ") - profile: " + profile);
            	            }

            	    		RepositoryItem orderItem = com.getOrderFO(orderId);

            	    		if ( isLoggingInfo() ) {
            	                logInfo("postServiceAction(" + orderId + ") - orderItem: " + orderItem);
            	            }

            	    		//
	                        // Checks when profile and order have been readed correctly from repository.
	                        //
	                        //	Send email notification...
	                        //
	                        Map params = new HashMap();
	                        params.put("transId", pRequest.getParameter("idtrans"));
	                        if ( null != profile && null != orderItem ) {
	                            if (PaymentStates.ATOUT.equalsIgnoreCase(paymentMethod)) {
	                                if(getMailSender() != null) {
	                                    getMailSender().sendConfirmationLAtoutEmail(orderItem, profile, params);
	                                }
	                            } else if (PaymentStates.MIX.equalsIgnoreCase(paymentMethod)) {
	                                if(getMailSender() != null) {
	                                    getMailSender().sendConfirmationMixEmail(orderItem, profile, params, MailSender.AVIS_MIX);
	                                }
	                            } else if (PaymentStates.CADEAU.equalsIgnoreCase(paymentMethod)) {
	                                if(getMailSender() != null) {
	                                    getMailSender().sendConfirmationMixEmail(orderItem, profile, params, MailSender.AVIS_CADEAU);
	                                }
	                            } else if (PaymentStates.CREDIT_CARD.equalsIgnoreCase(paymentMethod)) {
	                                if(getMailSender() != null) {
	                            	getMailSender().sendConfirmationCCEmail(orderItem, profile, params);
	                                }
	                            }
	                            result = true;
	                        }
            			}
                    }
            	}
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(ORDER_ID_MESS + orderId, e);
            }
        }  // end try-catch

    	if ( isLoggingInfo() ) {
            logInfo("postServiceAction(request) - finish: " + result + " - " + pRequest.getQueryString());
        }

    	return result;
    }

    /**
     * Submits order.
     * @param orderId order id.
     *
     * @return true if success otherwise false
     */
    private boolean submitOrder(String orderId, DynamoHttpServletRequest pRequest, CastOrderManager com) {
    	boolean result = false;

    	if ( isLoggingInfo() ) {
            logInfo("submitOrder(" + orderId + ") - start");
        }

    	Order order = null;
    	try {
			order = com.loadOrder(orderId);
    	} catch (CommerceException e) {
            if (isLoggingError()) {
                logError(ORDER_ID_MESS + orderId, e);
            }
		}

		if ( isLoggingInfo() ) {
            logInfo("submitOrder(" + orderId + ") - order is retrieved: " + order);
        }

		if ( null != order ) {
            order.setSubmittedTime(System.currentTimeMillis());
            String paymentMethod = updatePaymentMethod(order, pRequest, true);

    		if ( isLoggingInfo() ) {
                logInfo("submitOrder(" + orderId + ") - paymentMethod: " + paymentMethod);
            }

            Map map = new HashMap();
            map.put("paymentMethod", paymentMethod);
            map.put(NEED_UPDATE_INVENTORY, true);
            map.put(CastoConstantesCommande.PIPELINE_CASTO_SHIPPING_TOOLS, getShippingTools());
            try {
        		if ( isLoggingInfo() ) {
                    logInfo("submitOrder(" + orderId + ") - before payboxProcessOrder2");
                }

				com.processOrder(order, "payboxProcessOrder2", com.getProcessOrderMap(null, map));

        		if ( isLoggingInfo() ) {
                    logInfo("submitOrder(" + orderId + ") - after payboxProcessOrder2");
                }

        		com.updateOrder(order);
        		result = true;

        		if ( isLoggingInfo() ) {
                    logInfo("submitOrder(" + orderId + ") - order was updated successfully");
                }
			} catch (CommerceException e) {
	            if (isLoggingError()) {
	                logError(ORDER_ID_MESS + orderId, e);
	            }
			}
        }

    	if ( isLoggingInfo() ) {
            logInfo("submitOrder(" + orderId + ") - finish: " + result);
        }

    	return result;
    }

    /**
     * Update payment method according to the values from sips repository's
     *
     * @param  pOrder
     * @param  withUpdate
     *
     * @return update payment method and return updated
     */
    private String updatePaymentMethod(Order pOrder, DynamoHttpServletRequest pRequest, boolean withUpdate) {
        String paymentMethod = null;

    	if ( isLoggingInfo() ) {
            logInfo("updatePaymentMethod(" + withUpdate + ") - start: " + pRequest.getQueryString());
        }

    	PaymentStates paymentStates = getPaymentStates();
        int transactionsCount = getTransactionsCount(pRequest);
        List paymentGroups = pOrder.getPaymentGroups();
        if ( paymentGroups != null && !paymentGroups.isEmpty() && paymentStates != null) {
        	if ( isLoggingInfo() ) {
                logInfo("updatePaymentMethod(" + withUpdate + ") - paymentGroups: " + paymentGroups + " - " + pRequest.getQueryString());
            }

        	if (transactionsCount == 1) {
                String sipsPaymentType = pRequest.getParameter("carte");

            	if ( isLoggingInfo() ) {
                    logInfo("updatePaymentMethod(" + withUpdate + ") - sipsPaymentType: " + sipsPaymentType + " - " + pRequest.getQueryString());
                }

            	for (Map.Entry<String, String> e : paymentStates.getFOToPayboxStatesMap().entrySet()) {
                    String l = e.getValue();
                    if (!StringUtils.isBlank(l)) {
                        String[] avPayboxMethod = l.split(",");
                        for (String str : avPayboxMethod) {
                            if (str.equalsIgnoreCase(sipsPaymentType)) {
                                paymentMethod = e.getKey();

                                if ( isLoggingInfo() ) {
                                    logInfo("updatePaymentMethod(" + withUpdate + ") - paymentMethod: " + paymentMethod + " - " +
                                    			pRequest.getQueryString());
                                }

                                break;
                            }
                        }
                        if ( null != paymentMethod ) {
                        	break;
                        }
                    }
                }
            } else if (transactionsCount > 1) {
                paymentMethod = paymentStates.getStateString(paymentStates.getStateValue(PaymentStates.MIX));
            }
        }
        if ( null == paymentMethod ) {
        	paymentMethod = PaymentStates.CREDIT_CARD;
        }
        if (withUpdate) {
        	((PaymentGroup) paymentGroups.get(0)).setPaymentMethod(paymentMethod);
        }

    	if ( isLoggingInfo() ) {
            logInfo("updatePaymentMethod(" + withUpdate + ") - finish: " + paymentMethod + " - " + pRequest.getQueryString());
        }

        return paymentMethod;
    }

    private void preSubmitLogOrderInfo(String orderId) {
        logInfo("Pre-submit order " + orderId + " info:");
        castLoggingHelper.logOrderInfo(orderId);

    }

    private void postSubmitLogOrderInfo(String orderId) {
        logInfo("Post-submit order " + orderId + " info:");
        castLoggingHelper.logOrderInfo(orderId);
    }

    /**
     * Gets Shipping Tools
     *
     * @return the shippingTools
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Sets Shipping Tools
     *
     * @param pShippingTools the shippingTools to set
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        this.mShippingTools = pShippingTools;
    }

    /**
     * Gets Mail Sender Manager
     *
     * @return the mailSender
     */
    public MailSender getMailSender() {
        return mMailSender;
    }

    /**
     * Sets Mail Sender Manager
     *
     * @param mailSender the mailSender to set
     */
    public void setMailSender(MailSender mailSender) {
        this.mMailSender = mailSender;
    }

    public CastLoggingHelper getCastLoggingHelper() {
        return castLoggingHelper;
    }

    public void setCastLoggingHelper(CastLoggingHelper castLoggingHelper) {
        this.castLoggingHelper = castLoggingHelper;
    }
}
