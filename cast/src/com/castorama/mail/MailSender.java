package com.castorama.mail;

import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.constantes.CastoConstantes.MESSAGE;
import static com.castorama.constantes.CastoConstantesCommande.BO_REFUND_PROPERTY_AMOUNT;
import static com.castorama.constantes.CastoConstantesOrders.BO_DELIVERIES_ITEM_NAME;
import static com.castorama.constantes.CastoConstantesOrders.BO_MESSAGE_PROPERTY_ORDER;
import static com.castorama.constantes.CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED;
import static com.castorama.constantes.CastoConstantesOrders.BO_MESSAGE_PROPERTY_TEXT;
import static com.castorama.constantes.CastoConstantesOrders.BO_MESSAGE_VIEW_NAME;
import static com.castorama.constantes.CastoConstantesOrders.BO_ORDER_ID;
import static com.castorama.constantes.CastoConstantesOrders.BO_ORDER_VIEW_NAME;
import static com.castorama.constantes.CastoConstantesOrders.BO_REFUNDS_ITEM_NAME;
import static com.castorama.constantes.CastoConstantesOrders.NCOMMANDE_CLIENT;
import static com.castorama.constantes.CastoConstantesOrders.ORDER_PROPERTY_DELIVERY_TYPE;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.fulfillment.OrderFulfillmentTools;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.CastConfiguration;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.profile.Constants;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.commerce.states.CastOrderStates;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesOrders;
import com.castorama.payment.PaymentStates;
import com.castorama.utils.Validator;

/**
 * MailSenderManager sends e-mails according to specified conditions and
 * time-table.
 *
 * @author EPAM team
 */
public class MailSender extends GenericService {
    /** PAYEMENT_GROUPS constant. */
    private static final String PAYEMENT_GROUPS = "paymentGroups";
    /** Refund payment method email param. */
    private static final String REFUND_MODE_EMAIL_PARAM = "refundMode";

    /** Refund payment method. */
    private static final String CMODE_REMBOURSEMENT_C636 = "CMODE_REMBOURSEMENT_C636";

    /** PROCESSING_FEES property */
    private static final String PROCESSING_FEES = "processingFees";

    /** TAX property */
    private static final String TAX = "tax";

    /** SHIPPING property */
    private static final String SHIPPING = "shipping";

    /** AMOUNT property */
    private static final String AMOUNT = "amount";

    /** AVIS_EXPEDITION property */
    private static final String AVIS_EXPEDITION = "AVIS_EXPEDITION";

    /** AVIS_PREPA property */
    private static final String AVIS_PREPA = "AVIS_PREPA";

    /** AVIS_MESSAGE property */
    private static final String AVIS_MESSAGE = "AVIS_MESSAGE";

    /** AVIS_REMBOURSEMENT property */
    private static final String AVIS_REMBOURSEMENT = "AVIS_REMBOURSEMENT";

    /** AVIS_INVALIDATING_CHECK property */
    private static final String AVIS_INVALIDATING_CHECK = "AVIS_INVALIDATING_CHECK";

    /** AVIS_NO_CHECK property */
    private static final String AVIS_NO_CHECK = "AVIS_NO_CHECK";

    /** AVIS_ATOUT property */
    private static final String AVIS_ATOUT = "AVIS_ATOUT";

    /** AVIS_CREDIT_CARD property */
    private static final String AVIS_CREDIT_CARD = "AVIS_CREDIT_CARD";

    /** AVIS_CHECK property */
    private static final String AVIS_CHECK = "AVIS_CHECK";

    /** AVIS_MIX property */
    public static final String AVIS_MIX = "AVIS_MIX";

    /** AVIS_CADEAU property */
    public static final String AVIS_CADEAU = "AVIS_CADEAU";

    /** LOG_MAIL_CREMBOURSEMENT_PROPERTY property */
    private static final String LOG_MAIL_CREMBOURSEMENT_PROPERTY = "CREMBOURSEMENT";

    /** LOG_MAIL_CENLEVEMENT_PROPERTY property */
    private static final String LOG_MAIL_CENLEVEMENT_PROPERTY = "CENLEVEMENT";

    /** LOG_MAIL_CCONTACT_PROPERTY property */
    private static final String LOG_MAIL_CCONTACT_PROPERTY = "CCONTACT";

    /** LOG_MAIL_QUESTIONNAIRE_ID_PROPERTY property */
    private static final String LOG_MAIL_QUESTIONNAIRE_ID_PROPERTY = "QUESTIONNAIRE_ID";

    /** LOG_MAIL_PROFILE_ID_PROPERTY property */
    private static final String LOG_MAIL_PROFILE_ID_PROPERTY = "PROFILE_ID";

    /** LOG_MAIL_DATE_ENVOIE_PROPERTY property */
    private static final String LOG_MAIL_DATE_ENVOIE_PROPERTY = "DATE_ENVOIE";

    /** LOG_MAIL_ORDER_ID_PROPEPTY property */
    private static final String LOG_MAIL_ORDER_ID_PROPEPTY = "ORDER_ID";

    /** LOG_MAIL_ITEM_DESCRIPTOR property */
    private static final String LOG_MAIL_ITEM_DESCRIPTOR = "suiviCommandeMail";

    /** RESPONSE_CODE_LOGSIP_PROP constant */
    private static final String RESPONSE_CODE_LOGSIP_PROP = "response_code";

    /** LOGSIPS_VIEW constant */
    private static final String LOGSIPS_VIEW = "logsips";

    /** ORDER_ID constant */
    private static final String ORDER_ID = "order_id";

    /** REFUND_DATE_EMAIL_PARAM constant */
    private static final String REFUND_DATE_EMAIL_PARAM = "refundDate";

    /** REFUND_AMOUNT_EMAIL_PARAM constant */
    private static final String REFUND_AMOUNT_EMAIL_PARAM = "refundAmount";

    /** TOTAL_PRICE_EMAIL_PARAM constant */
    private static final String TOTAL_PRICE_EMAIL_PARAM = "totalPrice";

    /** REFUND_DATE constant */
    private static final String REFUND_DATE_PROP = "DREMBOURSEMENT";

    /** ORDER_EMAILS constant */
    public static final String ORDER_EMAILS = "orderEmails";

    /** ORDER constant */
    private static final String ORDER = "order";

    /** PROFILE_ID constant */
    private static final String PROFILE_ID = "profileId";

    /** USER constant */
    private static final String USER = "user";

    /** ORDER_DATE constant */
    private static final String ORDER_DATE = "submittedDate";

    /** CONFIRMATION_LATOUT constant */
    public static final String CONFIRMATION_LATOUT = "confirmationLAtout";

    /** CONFIRMATION_CB constant */
    public static final String CONFIRMATION_CB = "confirmationCB";

    /** CONFIRMATION_CB_AND_GC constant */
    public static final String CONFIRMATION_CB_AND_GC = "confirmationCBandGC";

    /** RECEPTION_CHECK constant */
    public static final String RECEPTION_CHECK = "receptionCheck";

    /** RECEPTION_NO_CHECK constant */
    public static final String RECEPTION_NO_CHECK = "receptionNoCheck";

    /** INVALIFATIONG_CHECK constant */
    public static final String INVALIDATING_CHECK = "invalidatingCheck";

    /** SHIPMENT_PREPARATION constant */
    public static final String SHIPMENT_PREPARATION = "shipmentPreparation";

    /** CONFIRMATION_SHIPMENT constant */
    public static final String CONFIRMATION_SHIPMENT = "confirmationShipment";

    /** REFUND_SUBMITED constant */
    public static final String REFUND_SUBMITED = "refundIsSubmitted";

    /** BO_SHIPPING_STATUS constant */
    private static final String BO_SHIPPING_STATUS = "CETAT_OL_C651";

    /** SHIPMENT_TYPE constant*/
    private static final String SHIPMENT_TYPE = "CEXPEDITION_C657";

    /** LDF_SHIPMENT_TYPE constant*/
    private static final int LDF_SHIPMENT_TYPE = 2;

    /** ORDER_BY_DREMBOURSEMENT_DESC constant. */
    public static final String ORDER_BY_DREMBOURSEMENT_DESC = " ORDER BY DREMBOURSEMENT DESC";

    /** DEFAULT_START_FROM constant. */
    protected static final int DEFAULT_START_FROM = 0;

    /** DEFAULT_CHUNK_SIZE constant. */
    protected static final int DEFAULT_CHUNK_SIZE = 1000;

    /** FILTER_BY constant. */
    public static final String FILTER_BY = " = ?0";

    /** ORDER_STATE_SHIPPING property */
    private static final Integer ORDER_STATE_UNDER_PREPARATION_3 = 3;

    /** ORDER_STATE_PREPARING property */
    private static final Integer ORDER_STATE_UNDER_PREPARATION_4 = 4;

    /** ORDER_STATE_SHIPPED property */
    private static final Integer ORDER_STATE_SHIPPED_5 = 5;

    /** ORDER_STATE_SHIPPED property */
    private static final Integer ORDER_STATE_SHIPPED_9 = 9;

    /** CASTO_CONTACT property */
    private static final int CASTO_CONTACT = 1;

    /** PROP_CCLIENT_WEB property */
    private static final String PROP_CCLIENT_WEB = "CCLIENT_WEB";

    /** PROP_EMAILED property */
    private static final String PROP_EMAILED = "EMAILED";

    /** ITEM_USER property */
    public static final String ITEM_USER = "user";

    /** PROPERTY_LAST_NAME property */
    public static final String PROPERTY_LAST_NAME = "lastName";

    /** PROPERTY_FIRST_NAME property */
    public static final String PROPERTY_FIRST_NAME = "firstName";

    /** PROPERTY_LOGIN property */
    public static final String PROPERTY_LOGIN = "login";

    /** CODE_OK property */
    public static final String CODE_OK = "00000";

    /** LOG_SIPIS_REPOSITORY property */
    public static final String LOG_SIPIS_REPOSITORY = LOGSIPS_VIEW;

    /** ORDER_LAST_MODIFIED_DATE property */
    public static final String ORDER_LAST_MODIFIED_DATE = "lastModifiedDate";
    
    private static final String CAST_CONFIGURATION_PATH = "/com/castorama/CastConfiguration";
    
    /** EML_RXTENSION constant */
    public static final String EML_EXTENSION = ".eml";

    /**
     * Order States.
     *
     * @author EPAM team
     */
    enum OrderValidationState {
        CHECK(CastOrderStates.PENDING_CHEQUE), CALL_CENTER(CastOrderStates.PENDING_CALL_CENTER),
        TRANSFER(CastOrderStates.PENDING_VIREMENT);

        /** mType property */
        String mType;

        /**
         * Creates a new OrderValidationState object.
         *
         * @param pType type
         */
        OrderValidationState(String pType) {
            this.mType = pType;
        }
    }

    /** orderFetchingDays property */
    private int orderFetchingDays;

    /** paymentStates property */
    private PaymentStates mPaymentStates;

    /** emailSender property */
    private TemplateEmailSender mEmailSender = null;

    /** confirmationLAtoutTemplate property */
    private TemplateEmailInfoImpl mConfirmationLAtoutTemplate = null;

    /** confirmationCBTemplate property */
    private TemplateEmailInfoImpl mConfirmationCBTemplate = null;

    /** confirmationCBandGCTemplate property */
    private TemplateEmailInfoImpl mConfirmationCBandGCTemplate = null;

    /** confirmationClickAndCollectTemplate property */
    private TemplateEmailInfoImpl mConfirmationClickAndCollectTemplate = null;

    /** receptionCheckTemplate property */
    private TemplateEmailInfoImpl mReceptionCheckTemplate = null;

    /** receptionNoCheckTemplate property */
    private TemplateEmailInfoImpl mReceptionNoCheckTemplate = null;

    /** invalidatingCheckTemplate property */
    private TemplateEmailInfoImpl mInvalidatingCheckTemplate = null;

    /** shipmentPreparationTemplate property */
    private TemplateEmailInfoImpl mShipmentPreparationTemplate = null;

    /** confirmationShipmentTemplate property */
    private TemplateEmailInfoImpl mConfirmationShipmentTemplate = null;

    /** refundIsSubmittedTemplate property */
    private TemplateEmailInfoImpl mRefundIsSubmittedTemplate = null;

    /** newMessageReceivedTemplate property */
    private TemplateEmailInfoImpl mNewMessageReceivedTemplate = null;

    /** messageNeedsAnswerTemplate property */
    private TemplateEmailInfoImpl mMessageNeedsAnswerTemplate = null;

    /** orderTools property */
    private OrderTools mOrderTools = null;

    /** orderManager property */
    private OrderManager mOrderManager;

    /** orderStates property */
    private CastOrderStates mOrderStates;

    /** BOOrderStates property */
    private BOOrderStates mBOOrderStates;

    /** repositoryBO property */
    private Repository mRepositoryBO;

    /** profileRepository property */
    private Repository mProfileRepository;

    /** logSipsRepository property */
    private Repository mLogSipsRepository;

    /** orderFulfillmentTools property */
    private OrderFulfillmentTools mOrderFulfillmentTools;

    /** LogRepository property */
    private Repository mLogRepository;

    /** TransactionManager property */
    private TransactionManager mTransactionManager;

    /**
     * Send or not email for LDF products.
     */
    private boolean mSendForLDFProducts;

    /**
     * Creates a new MailSenderManager object.
     */
    public MailSender() {
    }

    /**
     * Check every order and sends appropriate e-mails if needed.
     *
     * @throws RepositoryException Exception exception
     */
    public void sendEmails() throws RepositoryException {
        checkSuccessOrderStatuses();
        checkUnsuccessOrderStatuses();
        checkRefundStatuses();
        checkShipmentStatuses();
        checkMessageStatuses();
    }

    /**
     * Checks all Orders in Status Validated and send Email for the ones for
     * which email has not yet been sent
     *
     * @throws RepositoryException Exception exception
     */
    private void checkSuccessOrderStatuses() throws RepositoryException {
        if (isLoggingDebug()) {
            logDebug("Job checkSuccessOrderStatuses was started. ");
        }
        int startPosition = DEFAULT_START_FROM;
        int portion = DEFAULT_CHUNK_SIZE;

        int tempChunkSize = 1;
        int tempChunk = 1;

        String filter = " BOState CONTAINS IGNORECASE \"" + BOOrderStates.VALIDE + "\" AND lastModifiedDate > ?0 ";

        int countOrders = countOrders(filter, getOrderTools().getOrderRepository(), ORDER, getOrderFetchingStartDate());

        if (portion > 0) {
            tempChunkSize = countOrders / portion;
            if (tempChunkSize == 0) {
                tempChunkSize = 1;
            } else if ((countOrders % portion) > 0) {
                tempChunkSize++;
            }
        }
        for (; tempChunk <= tempChunkSize; tempChunk++) {
            RepositoryItem[] orders =
                getChunkOrders(filter, startPosition, portion, getOrderTools().getOrderRepository(), ORDER);
            if (orders != null) {
                for (RepositoryItem order : orders) {
                    try {
                        CastOrderImpl castOrder = (CastOrderImpl) getOrderManager().loadOrder(order.getRepositoryId());
                        List paymentsGroups = (List) castOrder.getPropertyValue(PAYEMENT_GROUPS);
                        int groupSize = paymentsGroups.size();
                        if ((paymentsGroups != null) && (groupSize > 0)) {
                            MutableRepositoryItem paymentGroup = (MutableRepositoryItem) paymentsGroups.get(0);

                            String paymentMethod = (String) paymentGroup.getPropertyValue(CastoConstantesCommande.PAYMENT_GROUP_PAYMENT_METHOD);

                            RepositoryItem pUser = getUser(order);
                            if (pUser != null) {
                                Map<String, Object> params = new HashMap<String, Object>();
                                String orderId = order.getRepositoryId();

                                RepositoryView logsipsView = getLogSipsRepository().getView(LOGSIPS_VIEW);
                                QueryBuilder queryBuilder = logsipsView.getQueryBuilder();
                                QueryExpression propertyQuery = queryBuilder.createPropertyQueryExpression(ORDER_ID);
                                QueryExpression valueQuery = queryBuilder.createConstantQueryExpression(orderId);
                                Query query =
                                    queryBuilder.createComparisonQuery(propertyQuery, valueQuery, QueryBuilder.EQUALS);
                                RepositoryItem[] logsipsItems = logsipsView.executeQuery(query);
                                if ((logsipsItems != null) && (logsipsItems.length > 0)) {
                                    for (RepositoryItem logsipsItem : logsipsItems) {
                                        String responseCode =
                                            (String) logsipsItem.getPropertyValue(RESPONSE_CODE_LOGSIP_PROP);
                                        if (isErrorCodeOK(responseCode)) {
                                            params.put("transId", logsipsItem.getPropertyValue("transaction_id"));
                                        }
                                    }
                                }

                                if (paymentMethod.equalsIgnoreCase(PaymentStates.ATOUT)) {
                                    sendConfirmationLAtoutEmail(order, pUser, params);

                                } else if (paymentMethod.equalsIgnoreCase(PaymentStates.CREDIT_CARD)) {
                                    sendConfirmationCCEmail(order, pUser, params);

                                } else if (paymentMethod.equalsIgnoreCase(PaymentStates.CHEQUE)) {
                                    if (!isEmailSent(order, RECEPTION_CHECK, AVIS_CHECK, null, null)) {
                                        sendEmailWithParams(order, pUser, getReceptionCheckTemplate(), null);
                                        logSendingMail(orderId, pUser.getRepositoryId(), AVIS_CHECK,
                                                       LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);
                                    }
                                } else if (paymentMethod.equalsIgnoreCase(PaymentStates.MIX)) {
                                    sendConfirmationMixEmail(order, pUser, params, AVIS_MIX);
                                } else if (paymentMethod.equalsIgnoreCase(PaymentStates.CADEAU)) {
                                    sendConfirmationMixEmail(order, pUser, params, AVIS_CADEAU);
                                }
                            }  // end if-else
                        }  // end if
                    } catch (Exception ex) {
                        logError("Error occured in method checkSuccessOrderStatuses while checking order : " +
                                 order.getRepositoryId());
                        logError(ex);
                    }  // end try-catch
                }  // end for
                startPosition += portion;
            }  // end if
        }  // end for
        if (isLoggingDebug()) {
            logDebug("End of checkSuccessOrderStatuses. ");
        }
    }

    /**
     * Send Confirmaion Email for MIX payment.
     *
     * @param order      - order that was payed by credit card
     * @param pUser      - user
     * @param params     - email params
     * @param questionnaireValue - questionnaireValue param
     */
    public void sendConfirmationMixEmail(RepositoryItem order, RepositoryItem pUser, Map<String, Object> params,
                                         String questionnaireValue) {
        String orderId = order.getRepositoryId();
        if (!isEmailSent(order, CONFIRMATION_CB_AND_GC, questionnaireValue, null, null)) {//AVIS_MIX
        	sendEmailWithParams(order, pUser, getConfirmationCBandGCTemplate(), params, true);
            logSendingMail(orderId, pUser.getRepositoryId(), questionnaireValue, LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);
        }
    }

    /**
     * Send Confirmation credit card email.
     *
     * @param order  - order that was payed by credit card
     * @param pUser  - user
     * @param params - email params
     */
    public void sendConfirmationCCEmail(RepositoryItem order, RepositoryItem pUser, Map<String, Object> params) {
        String orderId = order.getRepositoryId();
        if (!isEmailSent(order, CONFIRMATION_CB, AVIS_CREDIT_CARD, null, null)) {
            if (DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT.equals(order.getPropertyValue(ORDER_PROPERTY_DELIVERY_TYPE))) {
            	sendEmailWithParams(order, pUser, getConfirmationClickAndCollectTemplate(), params, true);
            } else {
            	sendEmailWithParams(order, pUser, getConfirmationCBTemplate(), params, true);
            }
            logSendingMail(orderId, pUser.getRepositoryId(), AVIS_CREDIT_CARD, LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);
        }
    }

    /**
     * Send Confirmation LAtout email
     *
     * @param order  - order that was payed by LAtout card
     * @param pUser  - user
     * @param params -
     */
    public void sendConfirmationLAtoutEmail(RepositoryItem order, RepositoryItem pUser, Map<String, Object> params) {
        String orderId = order.getRepositoryId();
        if (!isEmailSent(order, CONFIRMATION_LATOUT, AVIS_ATOUT, null, null)) {
            if (DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT.equals(order.getPropertyValue(ORDER_PROPERTY_DELIVERY_TYPE))) {
            	sendEmailWithParams(order, pUser, getConfirmationClickAndCollectTemplate(), params, true);
            } else {
            	sendEmailWithParams(order, pUser, getConfirmationLAtoutTemplate(), params, true);
            }
            logSendingMail(orderId, pUser.getRepositoryId(), AVIS_ATOUT, LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);
        }
    }

    /**
     * Check all non-validated orders in status Submitted. If Current Date -
     * Submission Date >= 10 days and email has not yet been sent, send Email
     * reminding to submit a payment. If Current Date - Submission Date >= 20
     * days and this email has not yet been sent, send Email explaining that
     * Order has been cancelled by the portal.
     *
     * @throws RepositoryException Exception exception
     */
    private void checkUnsuccessOrderStatuses() throws RepositoryException {
        if (isLoggingDebug()) {
            logDebug("Job checkUnsuccessOrderStatuses was started. ");
        }
        int startPosition = DEFAULT_START_FROM;
        int portion = DEFAULT_CHUNK_SIZE;

        int tempChunkSize = 1;
        int tempChunk = 1;

        StringBuffer conditions = new StringBuffer(100);
        OrderValidationState[] values = OrderValidationState.values();
        int size = values.length;
        for (OrderValidationState state : values) {
            conditions.append("( BOState = \"").append(state.mType.toUpperCase()).append('"').append(')');
            if (state.ordinal() != (size - 1)) {
                conditions.append(" OR ");
            }
        }
        conditions.append(" AND ").append(ORDER_LAST_MODIFIED_DATE).append(" > ?0 ");
        int countOrders =
            countOrders(conditions.toString(), getOrderTools().getOrderRepository(), ORDER,
                        getOrderFetchingStartDate());

        if (portion > 0) {
            tempChunkSize = countOrders / portion;
            if (tempChunkSize == 0) {
                tempChunkSize = 1;
            } else if ((countOrders % portion) > 0) {
                tempChunkSize++;
            }
        }
        for (; tempChunk <= tempChunkSize; tempChunk++) {
            RepositoryItem[] orders =
                getChunkOrders(conditions.toString(), startPosition, portion, getOrderTools().getOrderRepository(),
                               ORDER);
            if (orders != null) {
                for (RepositoryItem order : orders) {
                    try {
                        Date submittedDate = (Date) order.getPropertyValue(ORDER_DATE);
                        Date today = new Date();
                        int miliSecondsPerDay = 1000 * 60 * 60 * 24;
                        long deltaDays = (today.getTime() - submittedDate.getTime()) / miliSecondsPerDay;

                        CastOrderImpl castOrder = (CastOrderImpl) getOrderManager().loadOrder(order.getRepositoryId());

                        double total = getOrderTotal(castOrder);

                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put(ORDER_DATE, submittedDate);
                        params.put(TOTAL_PRICE_EMAIL_PARAM, total);

                        RepositoryItem pUser = getUser(order);
                        if (pUser != null) {
                            if (deltaDays >= 20) {
                                if (!isEmailSent(order, INVALIDATING_CHECK, AVIS_INVALIDATING_CHECK, null, null)) {
                                    sendEmailWithParams(order, pUser, getInvalidatingCheckTemplate(), params);

                                    // Order is to be canceled by portal
                                    BOOrderStates boOrderStates = getBOOrderStates();
                                    if (boOrderStates != null) {
                                        int boStateNum = boOrderStates.getStateValue(BOOrderStates.PENDING_REMOVE);
                                        castOrder.setBOState(boOrderStates.getStateString(boStateNum));
                                        castOrder.setBOStateNum(boStateNum);
                                        castOrder.setBOStateDetail((boOrderStates.getStateDescription(boStateNum)));
                                    }

                                    TransactionDemarcation td = new TransactionDemarcation();
                                    try {
                                        td.begin(getTransactionManager());

                                        getOrderManager().updateOrder(castOrder);

                                        logSendingMail(order.getRepositoryId(), pUser.getRepositoryId(),
                                                       AVIS_INVALIDATING_CHECK, LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);

                                    } catch (Exception e) {
                                        try {
                                            getTransactionManager().getTransaction().setRollbackOnly();
                                        } catch (Exception ex) {
                                            if (isLoggingError()) {
                                                logError(ex);
                                            }
                                        }
                                        if (isLoggingError()) {
                                            logError(e);
                                        }
                                    } finally {
                                        try {
                                            td.end();
                                        } catch (TransactionDemarcationException e) {
                                            if (isLoggingError()) {
                                                logError(e);
                                            }
                                        }
                                    }  // end try-catch-finally
                                }  // end if
                            } else if (deltaDays >= 10) {
                                if (!isEmailSent(order, RECEPTION_NO_CHECK, AVIS_NO_CHECK, null, null)) {
                                    sendEmailWithParams(order, pUser, getReceptionNoCheckTemplate(), params);
                                    logSendingMail(order.getRepositoryId(), pUser.getRepositoryId(), AVIS_NO_CHECK,
                                                   LOG_MAIL_CREMBOURSEMENT_PROPERTY, null);
                                }
                            }  // end if-else
                        }  // end if
                    } catch (Exception ex) {
                        logError("Error occured in method checkUnsuccessOrderStatuses() while checking order : " +
                                 order.getRepositoryId());
                        logError(ex);
                    }  // end try-catch
                }  // end for
            }  // end if
            startPosition += portion;
        }  // end for
        if (isLoggingDebug()) {
            logDebug("End of checkUnsuccessOrderStatuses. ");
        }
    }

    /**
     * Gets Order Total Amount.
     *
     * @param  castOrder - order
     *
     * @return Order Total Amount
     */
    private double getOrderTotal(CastOrderImpl castOrder) {
        RepositoryItem priceInfo =
            (RepositoryItem) castOrder.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PRICE_INFO);
        double total = 0.0;
        if (null != priceInfo) {
            total += getDoubleValue(priceInfo.getPropertyValue(AMOUNT), 0D);
            total += getDoubleValue(priceInfo.getPropertyValue(SHIPPING), 0D);
            total += getDoubleValue(priceInfo.getPropertyValue(TAX), 0D);
        }
        total += getDoubleValue(castOrder.getPropertyValue(PROCESSING_FEES), 0D);
        return total;
    }

    /**
     * Returns double value of Object when it is Double, otherwise default
     * value.
     *
     * @param  value    object.
     * @param  defvalue default value.
     *
     * @return double value of Object when it is Double, otherwise default
     *         value.
     */
    private double getDoubleValue(Object value, double defvalue) {
        double result;
        if (value instanceof Double) {
            result = (Double) value;
        } else {
            result = defvalue;
        }
        return result;
    }

    /**
     * Check if there is Refund returned with Order Status Feed. check if Refund
     * Amount >1 Euro - send email notification if such Refund found and email
     * has not yet been sent for this refund.
     *
     * @throws RepositoryException
     */
    private void checkRefundStatuses() throws RepositoryException {
        if (isLoggingDebug()) {
            logDebug("Job checkRefundStatuses was started. ");
        }
        int startPosition = DEFAULT_START_FROM;
        int portion = DEFAULT_CHUNK_SIZE;

        int tempChunkSize = 1;
        int tempChunk = 1;

        String condition = BO_REFUND_PROPERTY_AMOUNT + " > 1 ";

        int countOrders = countOrders(condition, getRepositoryBO(), BO_REFUNDS_ITEM_NAME, null);

        if (portion > 0) {
            tempChunkSize = countOrders / portion;
            if (tempChunkSize == 0) {
                tempChunkSize = 1;
            } else if ((countOrders % portion) > 0) {
                tempChunkSize++;
            }
        }
        for (; tempChunk <= tempChunkSize; tempChunk++) {
            RepositoryItem[] refunds =
                getChunkOrders(BO_REFUND_PROPERTY_AMOUNT + " > 1", startPosition, portion, getRepositoryBO(),
                               BO_REFUNDS_ITEM_NAME);
            if (refunds != null) {
                for (RepositoryItem refund : refunds) {
                    try {
                        Long orderBOId = (Long) refund.getPropertyValue(BO_ORDER_ID);
                        if (orderBOId != null) {
                            CastOrderManager com = (CastOrderManager) getOrderManager();
                            RepositoryItem orderBO =
                                getRepositoryBO().getItem(orderBOId.toString(), BO_ORDER_VIEW_NAME);
                            RepositoryItem orderFO = com.getOrderFO(orderBO);
                            if ((orderFO != null) && (orderBO != null)) {
                                Date lastModified = (Date) orderFO.getPropertyValue(ORDER_LAST_MODIFIED_DATE);
                                Date fetchingStartDate = getOrderFetchingStartDate();
                                if (lastModified.after(fetchingStartDate)) {
                                    final RepositoryItem user = getUser(orderFO);
                                    if (user != null) {
                                        float amount = (Float) refund.getPropertyValue(BO_REFUND_PROPERTY_AMOUNT);
                                        Date refundDate = (Date) refund.getPropertyValue(REFUND_DATE_PROP);
                                        short refundMode = (Short) refund.getPropertyValue(CMODE_REMBOURSEMENT_C636);
                                        Map<String, Object> params = new HashMap<String, Object>();
                                        params.put(REFUND_AMOUNT_EMAIL_PARAM, amount);
                                        params.put(REFUND_DATE_EMAIL_PARAM, refundDate);
                                        params.put(REFUND_MODE_EMAIL_PARAM, refundMode);

                                        if (
                                            !isEmailSent(orderFO, REFUND_SUBMITED, AVIS_REMBOURSEMENT,
                                                             LOG_MAIL_CREMBOURSEMENT_PROPERTY,
                                                             new Integer(refund.getRepositoryId()))) {
                                            sendEmailWithParams(orderFO, user, getRefundIsSubmittedTemplate(), params);
                                            logSendingMail(orderFO.getRepositoryId(), user.getRepositoryId(),
                                                           AVIS_REMBOURSEMENT, LOG_MAIL_CREMBOURSEMENT_PROPERTY,
                                                           new Integer(refund.getRepositoryId()));
                                        }  // end if
                                    }  // end if
                                }  // end if
                            }  // end if
                        }  // end if
                    } catch (Exception ex) {
                        logError("Error occured in method checkRefundStatuses while checking refund : " +
                                 refund.getRepositoryId());
                        logError(ex);
                    }  // end try-catch
                }  // end for
            }  // end if
            startPosition += portion;
        }  // end for
        if (isLoggingDebug()) {
            logDebug("End of checkRefundStatuses. ");
        }
    }

    /**
     * Check all Shipments and send email for the shipment in Shipped if such
     * email has not yet been send, otherwise send e-mail.
     *
     * @throws RepositoryException Exception exception
     */
    private void checkShipmentStatuses() throws RepositoryException {
        if (isLoggingDebug()) {
            logDebug("Job checkShipmentStatuses was started. ");
        }
        int startPosition = DEFAULT_START_FROM;
        int portion = DEFAULT_CHUNK_SIZE;

        int tempChunkSize = 1;
        int tempChunk = 1;

        String filter = " lastModifiedDate > ?0 ";

        int countOrders = countOrders(filter, getOrderTools().getOrderRepository(), ORDER, getOrderFetchingStartDate());

        if (portion > 0) {
            tempChunkSize = countOrders / portion;
            if (tempChunkSize == 0) {
                tempChunkSize = 1;
            } else if ((countOrders % portion) > 0) {
                tempChunkSize++;
            }
        }
        for (; tempChunk <= tempChunkSize; tempChunk++) {
            // Get Front-office Orders
            RepositoryItem[] orders =
                getChunkOrders(filter, startPosition, portion, getOrderTools().getOrderRepository(), ORDER);
            if (orders != null) {
                for (RepositoryItem order : orders) {
                    try {
                        // Get Back-office Orders by Front-office order ID
                        RepositoryView view = getRepositoryBO().getView(BO_ORDER_VIEW_NAME);
                        RqlStatement statement = RqlStatement.parseRqlStatement(NCOMMANDE_CLIENT + " = ?0 ");
                        RepositoryItem[] boOrders =
                                statement.executeQuery(view, new Object[]{order.getRepositoryId()});
                        if (boOrders != null) {
                            for (RepositoryItem boOrder : boOrders) {
                                // Get Deliveries by Back-office order ID
                                view = getRepositoryBO().getView(BO_DELIVERIES_ITEM_NAME);
                                statement = RqlStatement.parseRqlStatement(BO_ORDER_ID + " = ?0 ");
                                RepositoryItem[] shipments =
                                        statement.executeQuery(view, new Object[]{boOrder.getRepositoryId()});
                                if (shipments != null) {
                                    for (RepositoryItem shipment : shipments) {
                                        RepositoryItem user = getUser(order);
                                        if (user != null) {
                                            Map<String, Object> params = new HashMap<String, Object>();
                                            params.put("shipmentItem", shipment);
                                            int thisState = (Short) shipment.getPropertyValue(BO_SHIPPING_STATUS);
                                            int shipmentType = (Short) shipment.getPropertyValue(SHIPMENT_TYPE);
                                            if (!(shipmentType == LDF_SHIPMENT_TYPE) || mSendForLDFProducts) {
                                                if ((thisState == ORDER_STATE_SHIPPED_5) ||
                                                        (thisState == ORDER_STATE_SHIPPED_9)) {
                                                    final String confirmationShipmentFlag =
                                                            CONFIRMATION_SHIPMENT + shipment.getRepositoryId();
                                                    if (
                                                            !isEmailSent(order, confirmationShipmentFlag, AVIS_EXPEDITION,
                                                                    LOG_MAIL_CENLEVEMENT_PROPERTY,
                                                                    new Integer(shipment.getRepositoryId()))) {
                                                        sendEmailWithParams(order, user, getConfirmationShipmentTemplate(),
                                                                params);
                                                        logSendingMail(order.getRepositoryId(), user.getRepositoryId(),
                                                                AVIS_EXPEDITION, LOG_MAIL_CENLEVEMENT_PROPERTY,
                                                                new Integer(shipment.getRepositoryId()));
                                                    }
                                                } else if ((thisState == ORDER_STATE_UNDER_PREPARATION_3) ||
                                                        (thisState == ORDER_STATE_UNDER_PREPARATION_4)) {
                                                    final String shimentPreparationFlag =
                                                            SHIPMENT_PREPARATION + shipment.getRepositoryId();
                                                    if (
                                                            !isEmailSent(order, shimentPreparationFlag, AVIS_PREPA,
                                                                    LOG_MAIL_CENLEVEMENT_PROPERTY,
                                                                    new Integer(shipment.getRepositoryId()))) {
                                                        sendEmailWithParams(order, user, getShipmentPreparationTemplate(),
                                                                params);
                                                        logSendingMail(order.getRepositoryId(), user.getRepositoryId(),
                                                                AVIS_PREPA, LOG_MAIL_CENLEVEMENT_PROPERTY,
                                                                new Integer(shipment.getRepositoryId()));
                                                    }
                                                }  // end if-else
                                            }  // end if
                                        }
                                    }  // end for
                                }  // end if
                            }  // end for
                        }  // end if
                    } catch (Exception ex) {
                        logError("Error occured in method checkShipmentStatuses while checking order : " +
                                 order.getRepositoryId());
                        logError(ex);
                    }  // end try-catch
                }  // end for
                startPosition += portion;
            }  // end if
        }  // end for
        if (isLoggingDebug()) {
            logDebug("End of checkShipmentStatuses. ");
        }
    }

    /**
     * Check if there is a Message returned with Order Status Feed. If message
     * does not have a Require Response Flag set or message has a "Require
     * Response Flag set" - send appropriate email if email has not yet been
     * sent for this message
     *
     * @throws RepositoryException RepositoryException if there was an error
     *                             while attempting to return the RepositoryItem
     */
    private void checkMessageStatuses() throws RepositoryException {
        if (isLoggingDebug()) {
            logDebug("Job checkMessageStatuses was started. ");
        }
        String filter = "EMAILED is null and CSENS_CONTACT_C659 = " + CASTO_CONTACT;

        MutableRepository boRepository = (MutableRepository) getRepositoryBO();
        RepositoryView contactView = boRepository.getView(BO_MESSAGE_VIEW_NAME);
        RqlStatement statement = RqlStatement.parseRqlStatement(filter);

        int startPosition = DEFAULT_START_FROM;
        int portion = DEFAULT_CHUNK_SIZE;

        int tempChunkSize = 1;
        int tempChunk = 1;

        int countSkus = statement.executeCountQuery(contactView, null);

        if (portion > 0) {
            tempChunkSize = countSkus / portion;
            if (tempChunkSize == 0) {
                tempChunkSize = 1;
            } else if ((countSkus % portion) > 0) {
                tempChunkSize++;
            }
        }
        for (; tempChunk <= tempChunkSize; tempChunk++) {
            String rqlQuery = filter + " RANGE ?0+";
            Object[] rqlParams;
            RqlStatement rqlStatement;
            if (portion > 0) {
                rqlParams = new Object[2];
                rqlParams[0] = startPosition;
                rqlParams[1] = portion;
                rqlStatement = RqlStatement.parseRqlStatement(rqlQuery + "?1");
            } else {
                rqlParams = new Object[1];
                rqlParams[0] = startPosition;
                rqlStatement = RqlStatement.parseRqlStatement(rqlQuery);
            }

            RepositoryItem[] items = rqlStatement.executeQueryUncached(contactView, rqlParams);
            if (items != null) {
                for (RepositoryItem item : items) {
                    Long webId = (Long) item.getPropertyValue(BO_MESSAGE_PROPERTY_ORDER);

                    RepositoryItem webItem = getRepositoryBO().getItem(webId.toString(), BO_ORDER_VIEW_NAME);
                    String userId = (String) webItem.getPropertyValue(PROP_CCLIENT_WEB);

                    RepositoryItem user = getProfileRepository().getItem(userId, ITEM_USER);
                    if (user != null) {
                        boolean needAnswer = (Boolean) item.getPropertyValue(BO_MESSAGE_PROPERTY_REQUIRED);

                        try {
                            String orderId = (String) webItem.getPropertyValue(NCOMMANDE_CLIENT);

                            if (null != orderId) {
                                CastOrderManager com = (CastOrderManager) getOrderManager();
                                RepositoryItem orderFO = com.getOrderFO(orderId);
                                if (orderFO != null) {
                                    Date lastModified = (Date) orderFO.getPropertyValue(ORDER_LAST_MODIFIED_DATE);
                                    Date fetchingStartDate = getOrderFetchingStartDate();
                                    if (lastModified.after(fetchingStartDate)) {
                                        Map<String, Object> params = new HashMap<String, Object>();
                                        params.put(MESSAGE, (String) item.getPropertyValue(BO_MESSAGE_PROPERTY_TEXT));

                                        if (needAnswer) {
                                            sendEmailWithParams(orderFO, user, getMessageNeedsAnswerTemplate(), params);
                                        } else {
                                            sendEmailWithParams(orderFO, user, getNewMessageReceivedTemplate(), params);
                                        }

                                        TransactionDemarcation td = new TransactionDemarcation();
                                        try {
                                            td.begin(getTransactionManager());

                                            if (needAnswer) {
                                                logSendingMail(orderFO.getRepositoryId(), user.getRepositoryId(),
                                                               AVIS_MESSAGE, LOG_MAIL_CCONTACT_PROPERTY,
                                                               new Integer(item.getRepositoryId()));
                                            } else {
                                                logSendingMail(orderFO.getRepositoryId(), user.getRepositoryId(),
                                                               AVIS_MESSAGE, LOG_MAIL_CCONTACT_PROPERTY,
                                                               new Integer(item.getRepositoryId()));
                                            }

                                            ((MutableRepositoryItem) item).setPropertyValue(PROP_EMAILED, new Date());
                                            boRepository.updateItem((MutableRepositoryItem) item);
                                        } catch (Exception e) {
                                            try {
                                                getTransactionManager().getTransaction().setRollbackOnly();
                                            } catch (Exception ex) {
                                                if (isLoggingError()) {
                                                    logError(ex);
                                                }
                                            }
                                            if (isLoggingError()) {
                                                logError(e);
                                            }
                                        } finally {
                                            try {
                                                td.end();
                                            } catch (TransactionDemarcationException e) {
                                                if (isLoggingError()) {
                                                    logError(e);
                                                }
                                            }
                                        }  // end try-catch-finally
                                    }  // end if
                                }  // end if
                            }  // end if
                        } catch (Exception e) {
                            if (isLoggingError()) {
                                logError(e);
                            }
                        }  // end try-catch
                    } else {
                        if (isLoggingDebug()) {
                            logDebug("User not found by Id=" + userId);
                        }
                    }  // end if-else
                }  // end for
            }  // end if
            startPosition += portion;
        }  // end for
        if (isLoggingDebug()) {
            logDebug("End of checkMessageStatuses. ");
        }
    }

    /**
     * Returns the Profile according to user property from the given Order.
     *
     * @param  pOrder - order
     *
     * @return user repository item
     *
     * @throws RepositoryException RepositoryException if there was an error
     *                             while attempting to return the RepositoryItem
     */
    private RepositoryItem getUser(RepositoryItem pOrder) throws RepositoryException {
        String profileId = (String) pOrder.getPropertyValue(PROFILE_ID);
        return getProfileRepository().getItem(profileId, USER);
    }

    /**
     * Checks if e-mail of specified type is sent for the given Order
     *
     * @param  pOrder             order
     * @param  pType              email type
     * @param  questionnaireValue questionnaireValue
     * @param  mailType           mailType
     * @param  mailTypeValue      mailTypeValue
     *
     * @return <code>true</code> if emails have this type
     */
    private boolean isEmailSent(RepositoryItem pOrder, String pType, String questionnaireValue, String mailType,
                                Integer mailTypeValue) {
        // Following check is for legacy orders only. This flag is not set to new submitted orders.
        Map emails = (Map) pOrder.getPropertyValue(ORDER_EMAILS);
        boolean orderFlag = (emails.get(pType) == null) ? false : (Boolean) emails.get(pType);

        RepositoryItem[] mails = null;
        Object[] params = new Object[2];
        params[0] = pOrder.getRepositoryId();
        params[1] = questionnaireValue;

        if (getLogRepository() != null) {
            try {
                StringBuffer filter = new StringBuffer();
                filter.append(LOG_MAIL_ORDER_ID_PROPEPTY).append(" = ?0 AND ").append(LOG_MAIL_QUESTIONNAIRE_ID_PROPERTY)
                .append(" = ?1 ");

                if (!StringUtils.isBlank(mailType) && (mailTypeValue != null)) {
                    params = new Object[3];
                    params[0] = pOrder.getRepositoryId();
                    params[1] = questionnaireValue;
                    params[2] = mailTypeValue;

                    filter.append(" AND ").append(mailType).append(" = ?2 ");
                }

                RepositoryView mailView = getLogRepository().getView(LOG_MAIL_ITEM_DESCRIPTOR);
                RqlStatement statement = RqlStatement.parseRqlStatement(filter.toString());
                mails = statement.executeQuery(mailView, params);

            } catch (Exception l_e) {
                if (isLoggingError()) {
                    logError("ERROR log mail : " + l_e);
                }
            }  // end try-catch
        }  // end if
        boolean emailsFlag = (mails != null);

        return orderFlag || emailsFlag;

    }
    /**
     * Fills in values into e-mail template and sends e-mail.
     * Overloading sendEmailWithParams method. 
     */
    
    private void sendEmailWithParams(final RepositoryItem pOrder, final RepositoryItem pUser,
                                     final TemplateEmailInfoImpl pEmailInfo, Map<String, Object> pParams,
                                     boolean isBCCandPDFincluded) {	    	
    	if (pParams == null) {
            pParams = new HashMap<String, Object>();
        }
        if ((pOrder == null) || (pUser == null) || (pEmailInfo == null)) {
            logError("Params are empty. Email is not sent");
            return;
        }
        TemplateEmailInfoImpl templateEmailInfo = (TemplateEmailInfoImpl) pEmailInfo.copy();
        try {
            CastOrderImpl castOrder = (CastOrderImpl) getOrderManager().loadOrder(pOrder.getRepositoryId());
            pParams.put(ORDER, castOrder);
            
            if(isBCCandPDFincluded) {
    	        File[] filesToAttach = ((CastOrderTools)getOrderManager().getOrderTools()).createEmailAttachments(castOrder);
    	        templateEmailInfo.setMessageAttachments(filesToAttach);
            }
            
        } catch (CommerceException e1) {
            if (isLoggingDebug()) {
                logDebug("Impossible to load oreder [" + pOrder.getRepositoryId() + "]");
            }
            pParams.put(ORDER, pOrder);
        }
        pParams.put(USER, pUser);
        templateEmailInfo.setTemplateParameters(pParams);

        List<String> recipents = new ArrayList<String>();
        templateEmailInfo.setMessageTo((String) pUser.getPropertyValue(Constants.EMAIL_PROFILE_PROP));
        recipents.add(templateEmailInfo.getMessageTo());
        String subjectsBase = templateEmailInfo.getMessageSubject();
        templateEmailInfo.setMessageSubject(subjectsBase + pOrder.getRepositoryId());
        
        if(isBCCandPDFincluded) {
	        
	        StringBuffer bccField = new StringBuffer();
	        CastConfiguration castConfiguration =
	                (CastConfiguration) Nucleus.getGlobalNucleus().resolveName(CAST_CONFIGURATION_PATH);
	        ArrayList<String> bccEmails = castConfiguration.getBccListForConfirmationEmails();
	        for(String email : bccEmails) {
	        	if (Validator.validateEmail(email)) {
	            	bccField.append(email+",");
	        	}
	        }
	        
	        if(!StringUtils.isBlank(bccField.toString())) {
	        	templateEmailInfo.setMessageBcc(bccField.toString());
	        }
	        
	        String tempFilesDestFolder = castConfiguration.getConfirmEmailsDestFolder();
            StringBuffer emlFilePath = new StringBuffer();
            
	        if (!StringUtils.isEmpty(tempFilesDestFolder)) {       	
	        	emlFilePath.append(tempFilesDestFolder);    		
	        	if(!tempFilesDestFolder.endsWith("/")){
	        		emlFilePath.append("/");
	        	}

	            emlFilePath.append(pOrder.getRepositoryId());
	            emlFilePath.append(EML_EXTENSION);            
	
	            Message message;
				try {
					message = getEmailSender().createMessage(templateEmailInfo, pUser.getPropertyValue(EMAIL_PROFILE_PROP));
					try {
						message.writeTo(new FileOutputStream(new File(emlFilePath.toString())));
					} catch (FileNotFoundException e) {
						if(isLoggingError()){
							logError("It's impossible to create new EML file: " + emlFilePath, e);
						}
					} catch (IOException e) {
						if(isLoggingError()){
							logError("Unable to open FileOutputStream", e);
						}
					} catch (MessagingException e) {
						if(isLoggingError()){
							logError("Error while creating EML file for order: " + pOrder.getRepositoryId(), e);
						}
					}
				} catch (TemplateEmailException e) {
					if(isLoggingError()){
						logError("Confirmation email is not created for order: " + pOrder.getRepositoryId(), e);
					}
				}	
	        }        
        }

        try {
            getEmailSender().sendEmailMessage(templateEmailInfo, recipents, true, false);
            if (isLoggingDebug()) {
                logDebug("Massage " + templateEmailInfo.getMessageSubject() + " was sent to " +
                         templateEmailInfo.getMessageTo());
            }
        } catch (TemplateEmailException e) {
            if (isLoggingError()) {
                logError("Email is not sent");
            }
        }
    }
    
    /**
     * Fills in values into e-mail template and sends e-mail.
     *
     * @param pOrder     oredr
     * @param pUser      profile
     * @param pEmailInfo email template
     * @param pParams    email params
     */
    private void sendEmailWithParams(final RepositoryItem pOrder, final RepositoryItem pUser,
                                     final TemplateEmailInfoImpl pEmailInfo, Map<String, Object> pParams) {
    	
    	sendEmailWithParams(pOrder, pUser, pEmailInfo, pParams, false);
    }

    /**
     * Gets count of orders that are searched via <code>filter</code>
     *
     * @param  filter     filter to search orders
     * @param  repository repository
     * @param  viewName   repository view name
     * @param  param      query params
     *
     * @return count of orders that are searched via <code>filter</code>
     *
     * @throws RepositoryException if query executes with errors
     */
    protected int countOrders(String filter, Repository repository, String viewName, Object param)
                       throws RepositoryException {
        RepositoryView ordersView = repository.getView(viewName);
        RqlStatement requestRQLCount = RqlStatement.parseRqlStatement(filter);
        return requestRQLCount.executeCountQuery(ordersView, new Object[] {param});
    }

    /**
     * Returns chunkOrders property.
     *
     * @param  filter        parameter to set.
     * @param  startPosition parameter to set.
     * @param  portion       parameter to set.
     * @param  repository    repository
     * @param  viewName      repository view name
     *
     * @return chunkOrders property.
     *
     * @throws RepositoryException - exception
     */
    protected RepositoryItem[] getChunkOrders(String filter, int startPosition, int portion, Repository repository,
                                              String viewName) throws RepositoryException {
        String rqlQuery = filter + " RANGE ?1+";

        RepositoryView ordersView = repository.getView(viewName);
        Object[] rqlParams;
        RqlStatement rqlStatement;
        if (portion > 0) {
            rqlParams = new Object[3];
            rqlParams[0] = getOrderFetchingStartDate();
            rqlParams[1] = startPosition;
            rqlParams[2] = portion;
            rqlStatement = RqlStatement.parseRqlStatement(rqlQuery + "?2");
        } else {
            rqlParams = new Object[2];
            rqlParams[0] = getOrderFetchingStartDate();
            rqlParams[1] = startPosition;
            rqlStatement = RqlStatement.parseRqlStatement(rqlQuery);
        }

        return rqlStatement.executeQueryUncached(ordersView, rqlParams);
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pOrderId           order id
     * @param pProfileId         profile id
     * @param questionnaireValue questionnaireValue
     * @param mailType           mail type
     * @param paramValue         params
     */
    protected void logSendingMail(String pOrderId, String pProfileId, String questionnaireValue, String mailType,
                                  Integer paramValue) {
        if (getLogRepository() == null) {
            if (isLoggingError()) {
                logError("mail log repopstory is not defined");
            }
            return;
        }
        try {
            MutableRepository l_logRepo = (MutableRepository) getLogRepository();
            MutableRepositoryItem l_log = l_logRepo.createItem(LOG_MAIL_ITEM_DESCRIPTOR);
            l_log.setPropertyValue(LOG_MAIL_ORDER_ID_PROPEPTY, pOrderId);
            l_log.setPropertyValue(LOG_MAIL_DATE_ENVOIE_PROPERTY, new java.util.Date());
            l_log.setPropertyValue(LOG_MAIL_PROFILE_ID_PROPERTY, pProfileId);
            l_log.setPropertyValue(LOG_MAIL_QUESTIONNAIRE_ID_PROPERTY, questionnaireValue);
            l_log.setPropertyValue(mailType, paramValue);
            l_logRepo.addItem(l_log);
        } catch (Exception l_e) {
            if (isLoggingError()) {
                logError("ERROR log mail : " + l_e);
            }
        }
    }

    /**
     * Sets the OrderFulfillmentTools property
     *
     * @param pOrderFulfillmentTools the OrderFulfillmentTools to set
     */
    public void setOrderFulfillmentTools(OrderFulfillmentTools pOrderFulfillmentTools) {
        mOrderFulfillmentTools = pOrderFulfillmentTools;
    }

    /**
     * Returns pOrderFulfillmentTools property
     *
     * @return the OrderFulfillmentTools
     */
    public OrderFulfillmentTools getOrderFulfillmentTools() {
        return mOrderFulfillmentTools;
    }

    /**
     * Returns the orderStates property
     *
     * @return the orderStates
     */
    public CastOrderStates getOrderStates() {
        return mOrderStates;
    }

    /**
     * Sets the orderStates property
     *
     * @param pOrderStates the orderStates to set
     */
    public void setOrderStates(CastOrderStates pOrderStates) {
        mOrderStates = pOrderStates;
    }

    /**
     * Returns the bOOrderStates property
     *
     * @return the bOOrderStates
     */
    public BOOrderStates getBOOrderStates() {
        return mBOOrderStates;
    }

    /**
     * Sets the bOOrderStates property
     *
     * @param pOrderStates the bOOrderStates to set
     */
    public void setBOOrderStates(BOOrderStates pOrderStates) {
        mBOOrderStates = pOrderStates;
    }

    /**
     * Returns repositoryBO property.
     *
     * @return repositoryBO property.
     */
    public Repository getRepositoryBO() {
        return mRepositoryBO;
    }

    /**
     * Sets the value of the repositoryBO property.
     *
     * @param pRepositoryBO parameter to set.
     */
    public void setRepositoryBO(Repository pRepositoryBO) {
        this.mRepositoryBO = pRepositoryBO;
    }

    /**
     * Sets the email send component. This is configured in the component
     * property file.
     *
     * @param pEmailSender - the email send component
     */
    public void setEmailSender(TemplateEmailSender pEmailSender) {
        mEmailSender = pEmailSender;
    }

    /**
     * Returns emailSender property.
     *
     * @return emailSender property.
     */
    public TemplateEmailSender getEmailSender() {
        return this.mEmailSender;
    }

    /**
     * Returns the orderTools property
     *
     * @return orderTools property
     */
    public OrderTools getOrderTools() {
        return mOrderTools;
    }

    /**
     * Sets the orderTools property
     *
     * @param pOrderTools orderTools property
     */
    public void setOrderTools(OrderTools pOrderTools) {
        mOrderTools = pOrderTools;
    }

    /**
     * Returns confirmationLAtoutTemplate property.
     *
     * @return confirmationLAtoutTemplate property.
     */
    public TemplateEmailInfoImpl getConfirmationLAtoutTemplate() {
        return mConfirmationLAtoutTemplate;
    }

    /**
     * Sets the value of the confirmationLAtoutTemplate property.
     *
     * @param confirmationLAtoutTemplate parameter to set.
     */
    public void setConfirmationLAtoutTemplate(TemplateEmailInfoImpl confirmationLAtoutTemplate) {
        this.mConfirmationLAtoutTemplate = confirmationLAtoutTemplate;
    }

    /**
     * Returns confirmationCBTemplate property.
     *
     * @return confirmationCBTemplate property.
     */
    public TemplateEmailInfoImpl getConfirmationCBTemplate() {
        return mConfirmationCBTemplate;
    }

    /**
     * Sets the value of the confirmationCBTemplate property.
     *
     * @param confirmationCBTemplate parameter to set.
     */
    public void setConfirmationCBTemplate(TemplateEmailInfoImpl confirmationCBTemplate) {
        this.mConfirmationCBTemplate = confirmationCBTemplate;
    }

    /**
     * Returns confirmationCBandGCTemplate property.
     *
     * @return confirmationCBandGCTemplate property.
     */
    public TemplateEmailInfoImpl getConfirmationCBandGCTemplate() {
        return mConfirmationCBandGCTemplate;
    }

    /**
     * Sets the value of the confirmationCBandGCTemplate property.
     *
     * @param confirmationCBandGCTemplate parameter to set.
     */
    public void setConfirmationCBandGCTemplate(TemplateEmailInfoImpl confirmationCBandGCTemplate) {
        this.mConfirmationCBandGCTemplate = confirmationCBandGCTemplate;
    }

    /**
     * Returns confirmationClickAndCollectTemplate property.
     *
     * @return confirmationClickAndCollectTemplate property.
     */
    public TemplateEmailInfoImpl getConfirmationClickAndCollectTemplate() {
        return mConfirmationClickAndCollectTemplate;
    }

    /**
     * Sets the value of the confirmationClickAndCollectTemplate property.
     *
     * @param confirmationClickAndCollectTemplate parameter to set.
     */
    public void setConfirmationClickAndCollectTemplate(TemplateEmailInfoImpl confirmationClickAndCollectTemplate) {
        this.mConfirmationClickAndCollectTemplate = confirmationClickAndCollectTemplate;
    }

    /**
     * Returns receptionCheckTemplate property.
     *
     * @return receptionCheckTemplate property.
     */
    public TemplateEmailInfoImpl getReceptionCheckTemplate() {
        return mReceptionCheckTemplate;
    }

    /**
     * Sets the value of the receptionCheckTemplate property.
     *
     * @param receptionCheckTemplate parameter to set.
     */
    public void setReceptionCheckTemplate(TemplateEmailInfoImpl receptionCheckTemplate) {
        this.mReceptionCheckTemplate = receptionCheckTemplate;
    }

    /**
     * Returns receptionNoCheckTemplate property.
     *
     * @return receptionNoCheckTemplate property.
     */
    public TemplateEmailInfoImpl getReceptionNoCheckTemplate() {
        return mReceptionNoCheckTemplate;
    }

    /**
     * Sets the value of the receptionNoCheckTemplate property.
     *
     * @param receptionNoCheckTemplate parameter to set.
     */
    public void setReceptionNoCheckTemplate(TemplateEmailInfoImpl receptionNoCheckTemplate) {
        this.mReceptionNoCheckTemplate = receptionNoCheckTemplate;
    }

    /**
     * Returns invalidatingCheckTemplate property.
     *
     * @return invalidatingCheckTemplate property.
     */
    public TemplateEmailInfoImpl getInvalidatingCheckTemplate() {
        return mInvalidatingCheckTemplate;
    }

    /**
     * Sets the value of the invalidatingCheckTemplate property.
     *
     * @param invalidatingCheckTemplate parameter to set.
     */
    public void setInvalidatingCheckTemplate(TemplateEmailInfoImpl invalidatingCheckTemplate) {
        this.mInvalidatingCheckTemplate = invalidatingCheckTemplate;
    }

    /**
     * Returns shipmentPreparationTemplate property.
     *
     * @return shipmentPreparationTemplate property.
     */
    public TemplateEmailInfoImpl getShipmentPreparationTemplate() {
        return mShipmentPreparationTemplate;
    }

    /**
     * Sets the value of the shipmentPreparationTemplate property.
     *
     * @param pShipmentPreparationTemplate parameter to set.
     */
    public void setShipmentPreparationTemplate(TemplateEmailInfoImpl pShipmentPreparationTemplate) {
        this.mShipmentPreparationTemplate = pShipmentPreparationTemplate;
    }

    /**
     * Returns confirmationShipmentTemplate property.
     *
     * @return confirmationShipmentTemplate property.
     */
    public TemplateEmailInfoImpl getConfirmationShipmentTemplate() {
        return mConfirmationShipmentTemplate;
    }

    /**
     * Sets the value of the confirmationShipmentTemplate property.
     *
     * @param pConfirmationShipmentTemplate parameter to set.
     */
    public void setConfirmationShipmentTemplate(TemplateEmailInfoImpl pConfirmationShipmentTemplate) {
        this.mConfirmationShipmentTemplate = pConfirmationShipmentTemplate;
    }

    /**
     * Returns refundIsSubmittedTemplate property.
     *
     * @return refundIsSubmittedTemplate property.
     */
    public TemplateEmailInfoImpl getRefundIsSubmittedTemplate() {
        return mRefundIsSubmittedTemplate;
    }

    /**
     * Sets the value of the refundIsSubmittedTemplate property.
     *
     * @param pRefundIsSubmittedTemplate parameter to set.
     */
    public void setRefundIsSubmittedTemplate(TemplateEmailInfoImpl pRefundIsSubmittedTemplate) {
        this.mRefundIsSubmittedTemplate = pRefundIsSubmittedTemplate;
    }

    /**
     * Returns newMessageReceivedTemplate property.
     *
     * @return newMessageReceivedTemplate property.
     */
    public TemplateEmailInfoImpl getNewMessageReceivedTemplate() {
        return mNewMessageReceivedTemplate;
    }

    /**
     * Sets the value of the newMessageReceivedTemplate property.
     *
     * @param pNewMessageReceivedTemplate parameter to set.
     */
    public void setNewMessageReceivedTemplate(TemplateEmailInfoImpl pNewMessageReceivedTemplate) {
        this.mNewMessageReceivedTemplate = pNewMessageReceivedTemplate;
    }

    /**
     * Returns messageNeedsAnswerTemplate property.
     *
     * @return messageNeedsAnswerTemplate property.
     */
    public TemplateEmailInfoImpl getMessageNeedsAnswerTemplate() {
        return mMessageNeedsAnswerTemplate;
    }

    /**
     * Sets the value of the messageNeedsAnswerTemplate property.
     *
     * @param pMessageNeedsAnswerTemplate parameter to set.
     */
    public void setMessageNeedsAnswerTemplate(TemplateEmailInfoImpl pMessageNeedsAnswerTemplate) {
        this.mMessageNeedsAnswerTemplate = pMessageNeedsAnswerTemplate;
    }

    /**
     * Returns profileRepository property.
     *
     * @return profileRepository property.
     */
    public Repository getProfileRepository() {
        return mProfileRepository;
    }

    /**
     * Sets the value of the profileRepository property.
     *
     * @param profileRepository parameter to set.
     */
    public void setProfileRepository(Repository profileRepository) {
        mProfileRepository = profileRepository;
    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(OrderManager pOrderManager) {
        mOrderManager = pOrderManager;
    }

    /**
     * Returns errorCodeOK property.
     *
     * @param  pErrorCode parameter to set.
     *
     * @return errorCodeOK property.
     */
    protected boolean isErrorCodeOK(String pErrorCode) {
        if (pErrorCode != null) {
            return CODE_OK.equalsIgnoreCase(pErrorCode);
        }
        return false;
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
     * Returns logSipsRepository property.
     *
     * @return logSipsRepository property.
     */
    public Repository getLogSipsRepository() {
        return mLogSipsRepository;
    }

    /**
     * Sets the value of the logSipsRepository property.
     *
     * @param LogSipsRepository parameter to set.
     */
    public void setLogSipsRepository(Repository LogSipsRepository) {
        mLogSipsRepository = LogSipsRepository;
    }

    /**
     * Returns logRepository property.
     *
     * @return logRepository property.
     */
    public Repository getLogRepository() {
        return mLogRepository;
    }

    /**
     * Sets the value of the logRepository property.
     *
     * @param pLogRepository parameter to set.
     */
    public void setLogRepository(Repository pLogRepository) {
        mLogRepository = pLogRepository;
    }

    /**
     * Returns transactionManager property.
     *
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     *
     * @param pTransactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns orderFetchingStartDate property.
     *
     * @return orderFetchingStartDate property.
     */
    public Date getOrderFetchingStartDate() {
        Calendar calendar = Calendar.getInstance();
        int thisDay = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, thisDay - getOrderFetchingDays());
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Returns orderFetchingDays property.
     *
     * @return orderFetchingDays property.
     */
    public int getOrderFetchingDays() {
        return orderFetchingDays;
    }

    /**
     * Sets the value of the orderFetchingDays property.
     *
     * @param orderFetchingDays parameter to set.
     */
    public void setOrderFetchingDays(int orderFetchingDays) {
        this.orderFetchingDays = orderFetchingDays;
    }

    public boolean isSendForLDFProducts() {
        return mSendForLDFProducts;
    }

    public void setSendForLDFProducts(boolean mSendForLDFProducts) {
        this.mSendForLDFProducts = mSendForLDFProducts;
    }
}
