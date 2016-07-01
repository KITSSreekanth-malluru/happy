package com.castorama.commerce.order;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.BILLING_ADDRESS;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.FIRST_NAME_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.TITLE_PROFILE_PROP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.adapter.gsa.GSAItem;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroupImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.states.OrderStates;
import atg.commerce.states.PaymentGroupStates;
import atg.commerce.states.ShippingGroupStates;
import atg.commerce.states.StateDefinitions;
import atg.core.exception.ContainerException;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.repository.ConcurrentUpdateException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.QueryOptions;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.repository.rql.RqlStatement;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.lockmanager.TimeExceededException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.commerce.pricing.CastoShippingTools;
import com.castorama.commerce.pricing.Constants;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.commerce.states.CastOrderStates;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Extends SimpleOrderManager class to add specific logic.
 * used by /atg/commerce/order/OrderManager component.
 *
 * @author EPAM team
 */
public class CastOrderManager extends SimpleOrderManager {

    //region Statics
    /** ORDER_ID_MESS */
    protected static final String ORDER_ID_MESS = "orderId = ";
    
    /** MY_RESOURCE_NAME constant. */
    static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

    /** BO_ORDER_ITEM_NAME constant. */
    public static final String BO_ORDER_ITEM_NAME = "BO_SCD_VENTE_WEB";

    /** BO_MESSAGE_ITEM_NAME constant. */
    public static final String BO_MESSAGE_ITEM_NAME = "BO_SCD_CONTACT";

    /** BO_REFUNDS_ITEM_NAME constant. */
    public static final String BO_REFUNDS_ITEM_NAME = "BO_SCD_REMBOURSEMENT";

    /** BO_RETURNS_ITEM_NAME constant. */
    public static final String BO_RETURNS_ITEM_NAME = "BO_SCD_RETOUR";

    /** BO_DELIVERIES_ITEM_NAME constant. */
    public static final String BO_DELIVERIES_ITEM_NAME = "BO_VTE_ENLEVEMENT";

    /** BO_FACTURE_ITEM_NAME constant. */
    public static final String BO_FACTURE_ITEM_NAME = "BO_SCD_FACTURE_AVOIR";

    /** BO_ORDER_ID constant. */
    public static final String BO_ORDER_ID = "cvente";

    /** BO_ORDER constant. */
    public static final String BO_ORDER = "BO_ORDER";

    /** BO_ORDER_STATE constant. */
    public static final String BO_ORDER_STATE = "TETAT_COMMANDE_C602";

    /** BO_ORDER_CANCELLED constant. */
    public static final String BO_ORDER_CANCELLED = "DSUPPRESSION";

    /** NCOMMANDE_CLIENT constant. */
    public static final String NCOMMANDE_CLIENT = "NCOMMANDE_CLIENT";

    /** CENLEVEMENT constant. */
    public static final String CENLEVEMENT = "CENLEVEMENT";

    /** CRECLAMATION constant. */
    public static final String CRECLAMATION = "CRECLAMATION";

    /** DCLOTURE constant. */
    public static final String DCLOTURE = "DCLOTURE";

    /** ORDER_ID constant. */
    public static final String ORDER_ID = "order_id";

    /** BO_SCD_RECLAMATION constant. */
    public static final String BO_SCD_RECLAMATION = "BO_SCD_RECLAMATION";

    /** FILTER_BY constant. */
    public static final String FILTER_BY = " = ?0";

    /** FILTER_BY_TRANSPORTEUR constant. */
    public static final String FILTER_BY_TRANSPORTEUR = "TRANSPORTEUR != \"RUPTURE\" AND ";

    /** ORDER_BY_DCONTACT_SORT_DESC constant. */
    public static final String ORDER_BY_DCONTACT_SORT_DESC = " ORDER BY DCONTACT SORT DESC";

    /** ORDER_BY_DREMBOURSEMENT_DESC constant. */
    public static final String ORDER_BY_DREMBOURSEMENT_DESC = " ORDER BY DREMBOURSEMENT DESC";

    /** ORDER_BY_DRETOUR_MARCHANDISE_DESC constant. */
    public static final String ORDER_BY_DRETOUR_MARCHANDISE_DESC = " ORDER BY DRETOUR_MARCHANDISE DESC";

    /** ORDER_BY_DATE_EXPEDITION_REELLE_DESC constant. */
    public static final String ORDER_BY_DATE_EXPEDITION_REELLE_DESC = " ORDER BY DATE_EXPEDITION_REELLE DESC";

    /** DATEREADED constant. */
    public static final String DATEREADED = "DATEREADED";

    /** RESPONSE_REQUIRED constant. */
    public static final String RESPONSE_REQUIRED = "RESPONSE_REQUIRED";

    /** NUMCONTACT constant. */
    public static final String NUMCONTACT = "NUMCONTACT";

    /** CASTO_CONTACT constant. */
    public static final String CASTO_CONTACT = "CASTO_CONTACT";

    /** DATELECTURECLIENT constant. */
    public static final String DATELECTURECLIENT = "DATELECTURECLIENT";

    /** ORDER_STATE constant. */
    public static final String ORDER_STATE = "ORDER_STATE";

    /** MESSAGES_COUNT constant. */
    public static final String MESSAGES_COUNT = "MESSAGES_COUNT";

    /** LAST_UNREADED constant. */
    public static final String LAST_UNREADED = "LAST_UNREADED";

    /** LAST_MESSAGE constant. */
    public static final String LAST_MESSAGE = "LAST_MESSAGE";

    /** UNREADED_COUNT constant. */
    public static final String UNREADED_COUNT = "UNREADED_COUNT";

    /** IDORDER constant. */
    public static final String IDORDER = "IDORDER";

    /** NUMDOSSIER constant. */
    public static final String NUMDOSSIER = "NUMDOSSIER";

    /** CONTENU constant. */
    public static final String CONTENU = "CONTENU";

    /** LCONTACT constant. */
    public static final String LCONTACT = "LCONTACT";

    /** CMOTIF_C618 constant. */
    public static final String CMOTIF_C618 = "CMOTIF_C618";

    /** CMOTIF_RECLAMATION_C618 constant. */
    public static final String CMOTIF_RECLAMATION_C618 = "CMOTIF_RECLAMATION_C618";

    /** DATECONTACT constant. */
    public static final String DATECONTACT = "DATECONTACT";

    /** DCONTACT constant. */
    public static final String DCONTACT = "DCONTACT";

    /** TYPECONTACT_C632 constant. */
    public static final String TYPECONTACT_C632 = "TYPECONTACT_C632";

    /** CTYPE_CONTACT_C632 constant. */
    public static final String CTYPE_CONTACT_C632 = "CTYPE_CONTACT_C632";

    /** INITIATEURCONTACT_C659 constant. */
    public static final String INITIATEURCONTACT_C659 = "INITIATEURCONTACT_C659";

    /** CSENS_CONTACT_C659 constant. */
    public static final String CSENS_CONTACT_C659 = "CSENS_CONTACT_C659";

    /** DENVOI constant. */
    public static final String DENVOI = "DENVOI";

    /** ORDER_VIEW_NAME constant. */
    public static final String ORDER_VIEW_NAME = "order";

    /** LOCALE_FR constant. */
    public static final String LOCALE_FR = "fr_FR";

    /** EMAIL_PARAMETER_CIVILITE constant. */
    public static final String EMAIL_PARAMETER_CIVILITE = "civilite";

    /** EMAIL_PARAMETER_NOM constant. */
    public static final String EMAIL_PARAMETER_NOM = "nom";

    /** EMAIL_PARAMETER_PRENOM constant. */
    public static final String EMAIL_PARAMETER_PRENOM = "prenom";

    /** EMAIL_PARAMETER_EMAIL constant. */
    public static final String EMAIL_PARAMETER_EMAIL = "email";

    /** EMAIL_PARAMETER_ADRESSE constant. */
    public static final String EMAIL_PARAMETER_ADRESSE = "adresse";

    /** EMAIL_PARAMETER_CODEPOSTAL constant. */
    public static final String EMAIL_PARAMETER_CODEPOSTAL = "codePostal";

    /** EMAIL_PARAMETER_VILLE constant. */
    public static final String EMAIL_PARAMETER_VILLE = "ville";

    /** EMAIL_PARAMETER_TELEPHONE constant. */
    public static final String EMAIL_PARAMETER_TELEPHONE = "telephone";

    /** EMAIL_PARAMETER_MESSAGE constant. */
    public static final String EMAIL_PARAMETER_MESSAGE = "message";

    /** EMAIL_PARAMETER_MOTIF constant. */
    public static final String EMAIL_PARAMETER_MOTIF = "motif";

    /** EMAIL_PARAMETER_LOCALE constant. */
    public static final String EMAIL_PARAMETER_LOCALE = "locale";

    /** CVENTE constant. */
    public static final String CVENTE = "CVENTE";

    /** MESSAGE_QUESTION constant. */
    public static final String MESSAGE_QUESTION = "messageQuestion";

    /** ORDRE constant. */
    public static final String ORDRE = "ordre";

    /** CLIENT constant. */
    public static final Short CLIENT = (short) 2;
    /** BACK_OFFICE constant. */
    public static final Short BACK_OFFICE = (short) 1;
    //endregion

    //region Dependencies
    /** repositoryBO property */
    private Repository mRepositoryBO;

    /** repositoryMessages property */
    private Repository mRepositoryMessages;

    /** repositoryMotifs property */
    private Repository mRepositoryMotifs;

    /** defaultEmailInfo property */
    private TemplateEmailInfo mDefaultEmailInfo;

    /** emailSender property */
    private TemplateEmailSender mEmailSender = null;
    
    /** Request Submitted email subject. */
    private String mMessageRequestSubmittedSubject;

    /** templateUrl property */
    private String mTemplateUrl = null;

    /** sendEmailInSeparateThread property */
    private boolean mSendEmailInSeparateThread = false;

    /** persistEmails property */
    private boolean mPersistEmails = false;

    /** shippingTools property */
    private CastoShippingTools mShippingTools = null;
    
    /** clientLockManager property */
    protected ClientLockManager mClientLockManager;

    /** orderStates property */
    private CastOrderStates mOrderStates;

    /** bOOrderStates property */
    private BOOrderStates mBOOrderStates;
    
    /** journalisationRepository property. */
    private MutableRepository mJournalisationRepository;
    //endregion

    //region Values
    /** orderLockTimeout property. */
    private int mOrderLockTimeout = 5000;
    
    /** attemptCount property. */
    private int mAttemptCount = 15;

    /** concurentDelay property. */
    private long mConcurentDelay = 200;
    //endregion

    /**
     * Creates a new CastOrderManager object.
     */
    public CastOrderManager() {
    }

    /**
     * Creates a new CastOrderManager object according parameters.
     *
     * @param  pProfileId         the profile Id.
     * @param  pOrderPriceInfo    the order PriceInfo object.
     * @param  pTaxPriceInfo      the tax PriceInfo object.
     * @param  pShippingPriceInfo the shipping PriceInfo object.
     * @param  pOrderType         the order type.
     *
     * @return new order.
     *
     * @throws CommerceException when order could not be created.
     */
    @Override public Order createOrder(String pProfileId, OrderPriceInfo pOrderPriceInfo, TaxPriceInfo pTaxPriceInfo,
                                       ShippingPriceInfo pShippingPriceInfo, String pOrderType)
                                throws CommerceException {

        return (CastOrderImpl) super.createOrder(pProfileId, pOrderPriceInfo, pTaxPriceInfo, pShippingPriceInfo,
                                          pOrderType);
    }
    
  

    /**
     * Returns Back Ofiice order relevant to Front Office order by Front Office
     * order Id
     *
     * @param  pOrderFOId the Front Office order Id
     *
     * @return the Back Office order
     */
    public RepositoryItem getOrderBO(String pOrderFOId) {
        RepositoryItem result = null;
        if (!StringUtils.isBlank(pOrderFOId)) {
            try {
                RqlStatement findOrderBO = RqlStatement.parseRqlStatement(NCOMMANDE_CLIENT + FILTER_BY);
                RepositoryView ordersBOView = getRepositoryBO().getView(BO_ORDER_ITEM_NAME);
                RepositoryItem[] ordersBO = findOrderBO.executeQuery(ordersBOView, new Object[] {pOrderFOId});
                if ((null != ordersBO) && (1 == ordersBO.length)) {
                    result = ordersBO[0];
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns Back Ofiice order relevant to Front Office order by Front Office
     * order Id
     *
     * @param  pDeliveryId pOrderFO - Front Office order Id
     *
     * @return - Back Ofiice order
     */
    public RepositoryItem getFactureBO(String pDeliveryId) {
        RepositoryItem result = null;
        if (!StringUtils.isBlank(pDeliveryId)) {
            try {
                RqlStatement findOrderBO = RqlStatement.parseRqlStatement(CENLEVEMENT + FILTER_BY);
                RepositoryView ordersBOView = getRepositoryBO().getView(BO_FACTURE_ITEM_NAME);
                RepositoryItem[] facturesBO = findOrderBO.executeQuery(ordersBOView, new Object[] {pDeliveryId});
                if ((null != facturesBO) && (1 == facturesBO.length)) {
                    result = facturesBO[0];
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns Back Ofiice order relevant to Front Office order
     *
     * @param  pOrderFO the Front Office order
     *
     * @return the Back Ofiice order
     */
    public RepositoryItem getOrderBO(RepositoryItem pOrderFO) {
        RepositoryItem result = null;
        if (null != pOrderFO) {
            result = getOrderBO(pOrderFO.getRepositoryId());
        }
        return result;
    }

    /**
     * Returns Front Ofiice order relevant to Back Office order
     *
     * @param  pOrderBO the Back Office order
     *
     * @return the Front Ofiice order
     */
    public RepositoryItem getOrderFO(RepositoryItem pOrderBO) {
        RepositoryItem result = null;
        if (null != pOrderBO) {
            try {
                result =
                    getOrderTools().getOrderRepository().getItem(((String) pOrderBO.getPropertyValue(NCOMMANDE_CLIENT)),
                                                                 ORDER_VIEW_NAME);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns orderFO property.
     *
     * @param  pOrderFOId the parameter to set.
     *
     * @return the orderFO property.
     */
    public RepositoryItem getOrderFO(String pOrderFOId) {
        
        RepositoryItem result = null;

        if ( isLoggingInfo() ) {
            logInfo("getOrderFO(" + pOrderFOId + ") - start");
        }
        
        if (null != pOrderFOId) {
            try {
                result = getOrderTools().getOrderRepository().getItem(pOrderFOId, ORDER_VIEW_NAME);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("getOrderFO(" + pOrderFOId + ") - finish: " + result);
        }
        
        return result;
    }

    /**
     * Returns updatable front-ofiice order.
     *
     * @param  pOrderFOId the parameter to set.
     *
     * @return the updatable front-ofiice order.
     */
    public MutableRepositoryItem getUpdatableOrderFO(String pOrderFOId) {
        MutableRepositoryItem result = null;
        if (null != pOrderFOId) {
            try {
                /* remove item from cache before get */
                result = ((MutableRepository) getOrderTools().getOrderRepository()).
                    getItemForUpdate(pOrderFOId, ORDER_VIEW_NAME);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }
    
    /**
     * Returns updatable front-ofiice order. Clear cache if nedeed.
     *
     * @param  pOrderFOId the parameter to set.
     * @param  pUpdateCache the parameter to set.
     *
     * @return the updatable front-ofiice order.
     */
    public MutableRepositoryItem getUpdatableOrderFO(String pOrderFOId, boolean pUpdateCache) {
        if (pUpdateCache) {
            try {
                GSAItemDescriptor itemDescriptor = (GSAItemDescriptor) getOrderTools().getOrderRepository().getItemDescriptor(ORDER_VIEW_NAME);
                itemDescriptor.invalidateCachedItem(pOrderFOId);
                Thread.sleep(getConcurentDelay());
            } catch (RepositoryException e) {
                if (isLoggingError()){
                    vlogError(e, "Product id is {0} and updateCache is {1} ", pOrderFOId, pUpdateCache);
                }
            } catch (InterruptedException e) {
                if ( isLoggingError() ) {
                    logError("Wait order " + pOrderFOId + " has been interrapted.");
                }
            }
        }
        return  getUpdatableOrderFO(pOrderFOId);
    }
    
    /**
     * Returns state of the Back Office order by order id.
     *
     * @param  pOrderId the Back Office order.
     *
     * @return state of the Back Office order.
     */
    public long getStateBO(String pOrderId) {
        long result = -1;
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RepositoryItem orderBO = getRepositoryBO().getItem(pOrderId, BO_ORDER_ITEM_NAME);
                result = (Short) orderBO.getPropertyValue(BO_ORDER_STATE);
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns cancelledBO property.
     *
     * @param  pOrderId parameter to set.
     *
     * @return cancelledBO property.
     */
    public java.util.Date getCancelledBO(String pOrderId) {
        java.util.Date result = null;
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RepositoryItem orderBO = getRepositoryBO().getItem(pOrderId, BO_ORDER_ITEM_NAME);
                result = (java.util.Date) orderBO.getPropertyValue(BO_ORDER_CANCELLED);
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Gets order messages
     *
     * @param  pOrderFOItem - Front Office order repository item
     * @param  pOrderBOItem - Back Office order repository item
     *
     * @return
     */
    public RepositoryItem[] getMessages(RepositoryItem pOrderFOItem, RepositoryItem pOrderBOItem, boolean pAllMessages) {
        RepositoryItem[] result = null;
        result =
            getMessages((null == pOrderBOItem) ? null : pOrderBOItem.getRepositoryId(),
                        (null == pOrderFOItem) ? null : pOrderFOItem.getRepositoryId(), pAllMessages);
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Gets order refunds
     *
     * @param  pOrderFOItem - Front Office order repository item
     * @param  pOrderBOItem - Back Office order repository item
     *
     * @return - refunds array
     */
    public RepositoryItem[] getRefunds(RepositoryItem pOrderFOItem, RepositoryItem pOrderBOItem) {
        RepositoryItem[] result = null;
        result =
            getRefunds((null == pOrderBOItem) ? null : pOrderBOItem.getRepositoryId(),
                       (null == pOrderFOItem) ? null : pOrderFOItem.getRepositoryId());
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Gets order returns
     *
     * @param  pOrderFOItem - Front Office order repository item
     * @param  pOrderBOItem - Back Office order repository item
     *
     * @return - returns array
     */
    public RepositoryItem[] getReturns(RepositoryItem pOrderFOItem, RepositoryItem pOrderBOItem) {
        RepositoryItem[] result = null;
        result =
            getReturns((null == pOrderBOItem) ? null : pOrderBOItem.getRepositoryId(),
                       (null == pOrderFOItem) ? null : pOrderFOItem.getRepositoryId());
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Gets order deliveries
     *
     * @param  pOrderFOItem - Front Office order repository item
     * @param  pOrderBOItem - Back Office order repository item
     *
     * @return - deliveries array
     */
    public RepositoryItem[] getDeliveries(RepositoryItem pOrderFOItem, RepositoryItem pOrderBOItem) {
        RepositoryItem[] result = null;
        result =
            getDeliveries((null == pOrderBOItem) ? null : pOrderBOItem.getRepositoryId(),
                          (null == pOrderFOItem) ? null : pOrderFOItem.getRepositoryId());
        return (null == result) ? new RepositoryItem[] {} : result;
    }

    /**
     * Gets order messages
     *
     * @param  pOrderId   - Back Office order repository item id
     * @param  pOrderFOId - Front Office order repository item id
     *
     * @return order messages array
     */
    public RepositoryItem[] getMessages(String pOrderId, String pOrderFOId, boolean pAllMessages) {
        RepositoryItem[] result = new RepositoryItem[] {};
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RqlStatement statement =
                    RqlStatement.parseRqlStatement(BO_ORDER_ID + FILTER_BY + ORDER_BY_DCONTACT_SORT_DESC);
                RepositoryView view = getRepositoryBO().getView(BO_MESSAGE_ITEM_NAME);
                RepositoryItem[] items = statement.executeQueryUncached(view, new Object[] {pOrderId});
                if (null != items) {
                    List<RepositoryItem> list = new ArrayList<RepositoryItem>(items.length);
                    Calendar longAgo = Calendar.getInstance();
                    longAgo.setTimeInMillis(System.currentTimeMillis());
                    longAgo.add(Calendar.MONTH, -6);
                    Date longAgoDate = longAgo.getTime();
                    for (RepositoryItem item : items) {
                        if (pAllMessages || BACK_OFFICE.equals(
                                item.getPropertyValue(CastoConstantesCommande.BO_MESSAGE_PROPERTY_CSENS_CONTACT_C659))) {

                            RepositoryItem contactFile = getContactFile(((Integer) item
                                    .getPropertyValue(CRECLAMATION)).toString());
                            if (pAllMessages
                                    || ((null != contactFile)
                                    && ((null == contactFile.getPropertyValue(DCLOTURE)) || longAgoDate
                                    .before(((Date) contactFile.getPropertyValue(DCLOTURE)))))) {
                                list.add(item);
                                if ((null != pOrderFOId) && (item instanceof GSAItem)) {
                                    ((GSAItem) item).setPropertyValue(ORDER_ID, pOrderFOId);
                                }
                            }
                        }
                    }
                    result = (RepositoryItem[]) list.toArray(new RepositoryItem[list.size()]);

                }  // end if
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }  // end try-catch
        }  // end if
        return result;
    }

    /**
     * Returns contactFile property.
     *
     * @param  pContactFileId parameter to set.
     *
     * @return contactFile property.
     */
    public RepositoryItem getContactFile(String pContactFileId) {
        RepositoryItem result = null;
        if (!StringUtils.isBlank(pContactFileId)) {
            try {
                result = getRepositoryBO().getItem(pContactFileId, BO_SCD_RECLAMATION);
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Gets order refunds
     *
     * @param  pOrderId   - Back Office order repository item id
     * @param  pOrderFOId - Front Office order repository item id
     *
     * @return order refunds array
     */
    public RepositoryItem[] getRefunds(String pOrderId, String pOrderFOId) {
        RepositoryItem[] result = new RepositoryItem[] {};
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RqlStatement statement =
                    RqlStatement.parseRqlStatement(BO_ORDER_ID + FILTER_BY + ORDER_BY_DREMBOURSEMENT_DESC);
                RepositoryView view = getRepositoryBO().getView(BO_REFUNDS_ITEM_NAME);
                RepositoryItem[] items = statement.executeQuery(view, new Object[] {pOrderId});
                if ( null != items ) {
                    float amount = 0;
                    for (RepositoryItem repositoryItem : items) {
                        Float r_amount = (Float) repositoryItem.getPropertyValue(CastoConstantesCommande.BO_REFUND_PROPERTY_AMOUNT);
                        if ( null != r_amount ) {
                            amount += r_amount;
                        }
                    }
                    if ( 1 < amount ) {
                        result = items;
                    }
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Gets order returns
     *
     * @param  pOrderId   - Back Office order repository item id
     * @param  pOrderFOId - Front Office order repository item id
     *
     * @return order returns array
     */
    public RepositoryItem[] getReturns(String pOrderId, String pOrderFOId) {
        RepositoryItem[] result = new RepositoryItem[] {};
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RqlStatement statement =
                    RqlStatement.parseRqlStatement(BO_ORDER_ID + FILTER_BY + ORDER_BY_DRETOUR_MARCHANDISE_DESC);
                RepositoryView view = getRepositoryBO().getView(BO_RETURNS_ITEM_NAME);
                RepositoryItem[] items = statement.executeQuery(view, new Object[] {pOrderId});
                if (null != items) {
                    result = items;
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Gets order deliveries
     *
     * @param  pOrderId   - Back Office order repository item id
     * @param  pOrderFOId - Front Office order repository item id
     *
     * @return order deliveries array
     */
    public RepositoryItem[] getDeliveries(String pOrderId, String pOrderFOId) {
        RepositoryItem[] result = new RepositoryItem[] {};
        if ((null != pOrderId) && (0 < pOrderId.length())) {
            try {
                RqlStatement statement =
                    RqlStatement.parseRqlStatement(BO_ORDER_ID + FILTER_BY +
                                                   ORDER_BY_DATE_EXPEDITION_REELLE_DESC);
                RepositoryView view = getRepositoryBO().getView(BO_DELIVERIES_ITEM_NAME);
                RepositoryItem[] items = statement.executeQuery(view, new Object[] {pOrderId});
                if ( null != items ) {
                    ArrayList<RepositoryItem> deliveries = new ArrayList<RepositoryItem>(items.length);   
                    for(RepositoryItem item: items) {
                        String tr = (String) item.getPropertyValue("TRANSPORTEUR");
                        if ( null == tr || !"RUPTURE".equalsIgnoreCase(tr) ) {
                            deliveries.add(item);
                        }
                    }
                    result = deliveries.toArray(new RepositoryItem[deliveries.size()]);
                }
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Gets order details by Front Office order id.
     *
     * @param  pOrderFOId - Front Office order id.
     * @param  pProfileId - Profile id.
     *
     * @return order details.
     */
    public CastOrderDetails getOrderDetails(String pOrderFOId, String pProfileId) {
        CastOrderDetails result = null;
        if ( null != pProfileId && 0 < pProfileId.trim().length() ) {
            RepositoryItem orderFOItem = null;
            try {
                orderFOItem = getOrderTools().getOrderRepository().getItem(pOrderFOId, ORDER_VIEW_NAME);
                String profileId = (String)orderFOItem.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID);
                if ( pProfileId.equalsIgnoreCase(profileId) ) {
                    result = new CastOrderDetails(orderFOItem, this);
                } else {
                    if ( isLoggingDebug() ) {
                        logDebug("Order " + pOrderFOId + " don't belong to user " + pProfileId);
                    }
                }
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns messageState property.
     *
     * @param  pMessageId parameter to set.
     *
     * @return messageState property.
     */
    public Map getMessageState(String pMessageId) {
        Map<String, Object> result = new HashMap<String, Object>();
        if ( !StringUtils.isBlank(pMessageId) ) {
            Boolean responseRequired = Boolean.FALSE;
            Date dateReaded = getMessageReaded(pMessageId);
            if ( null == dateReaded ) {
                try {
                    RepositoryItem message = getRepositoryBO().getItem(pMessageId, BO_MESSAGE_ITEM_NAME);
                    responseRequired = (Boolean) message.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED);
                    if (responseRequired) {
                        RepositoryItem contactFile =
                            getContactFile((message.getPropertyValue(CRECLAMATION)).toString());
                        if ((null == contactFile) || (null != contactFile.getPropertyValue(DCLOTURE))) {
                            responseRequired = false;
                        }
                    }
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
            result.put(DATEREADED, dateReaded);
            result.put(RESPONSE_REQUIRED, responseRequired);
        }  // end if
        return result;
    }

    /**
     * Returns messageReaded property.
     *
     * @param  pMessageId parameter to set.
     *
     * @return messageReaded property.
     */
    public Date getMessageReaded(String pMessageId) {
        Date result = null;
        try {
            RqlStatement findByOrder = RqlStatement.parseRqlStatement(NUMCONTACT + FILTER_BY);
            RepositoryView messagesView = getRepositoryMessages().getView(CASTO_CONTACT);
            RepositoryItem[] messages = findByOrder.executeQuery(messagesView, new Object[] {pMessageId});
            if ((null != messages) && (1 == messages.length)) {
                result = (Date) messages[0].getPropertyValue(DATELECTURECLIENT);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Returns orderMessageStates property.
     *
     * @param  pOrderFOId parameter.
     *
     * @return orderMessageStates property.
     */
    public Map getOrderMessageStates(String pOrderFOId) {
        Map<String, Object> result = new HashMap<String, Object>();
        RepositoryItem orderBO = getOrderBO(pOrderFOId);
        if ( null != orderBO ) {
            RepositoryItem orderFO = getOrderFO(pOrderFOId);

            String orderId = orderBO.getRepositoryId();
            RepositoryItem[] messages = getMessages(orderId, null, false);
            result.put(MESSAGES_COUNT, Integer.toString(messages.length));
            boolean responseRequired = false;
            int unreaded = 0;
            RepositoryItem lastUnreaded = null;
            RepositoryItem lastMessage = null;
            for (int i = 0; i < messages.length; i++) {
                lastMessage = messages[i];
                java.util.Date dateReaded = getMessageReaded(lastMessage.getRepositoryId());
                if ( null == dateReaded ) {
                    unreaded++;
                    lastUnreaded = lastMessage;
                    if ( 0 == i ) {
                        responseRequired = (Boolean) lastMessage.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED);
                        if (responseRequired) {
                            RepositoryItem contactFile =
                                getContactFile(((Integer) lastMessage.getPropertyValue(CRECLAMATION)).toString());
                            if ((null == contactFile) || (null != contactFile.getPropertyValue(DCLOTURE))) {
                                responseRequired = false;
                            }
                        }
                    }
                }
            }
            result.put(BO_ORDER, orderBO);
            result.put(ORDER_STATE, orderFO.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM));
            result.put(LAST_MESSAGE, lastMessage);
            result.put(LAST_UNREADED, lastUnreaded);
            result.put(UNREADED_COUNT, Integer.toString(unreaded));
            result.put(RESPONSE_REQUIRED, responseRequired ? CastoConstantes.TRUE : CastoConstantes.FALSE);
        } else {
            result.put(BO_ORDER, null);
            result.put(ORDER_STATE, null);
            result.put(LAST_MESSAGE, null);
            result.put(LAST_UNREADED, null);
            result.put(UNREADED_COUNT, null);
            result.put(RESPONSE_REQUIRED, CastoConstantes.FALSE);
        }
        // end if
        return result;
    }

    /**
     * Changes message status.
     *
     * @param pMessage message to change status.
     */
    public void changeMessageStatus(RepositoryItem pMessage) {
        try {
            String messageId = pMessage.getRepositoryId();
            RepositoryItem order = getOrderFOByMessage(pMessage);
            if (null != order) {
                String orderId = order.getRepositoryId();
                RqlStatement findByOrder = RqlStatement.parseRqlStatement(NUMCONTACT + FILTER_BY);
                RepositoryView messagesView = getRepositoryMessages().getView(CASTO_CONTACT);
                RepositoryItem[] messages = findByOrder.executeQuery(messagesView, new Object[] {messageId});
                Date nowDate = new Date(System.currentTimeMillis());
                if ((null == messages) || (0 == messages.length)) {
                    MutableRepository r = (MutableRepository) getRepositoryMessages();
                    MutableRepositoryItem i = r.createItem(CASTO_CONTACT);
                    i.setPropertyValue(NUMCONTACT, Integer.parseInt(messageId));
                    i.setPropertyValue(IDORDER, orderId);
                    i.setPropertyValue(NUMDOSSIER, pMessage.getPropertyValue(CRECLAMATION));
                    i.setPropertyValue(CONTENU, pMessage.getPropertyValue(LCONTACT));
                    i.setPropertyValue(CMOTIF_C618,
                                       getContactFile(((Integer) pMessage.getPropertyValue(CRECLAMATION)).toString())
                                       .getPropertyValue(CMOTIF_RECLAMATION_C618));
                    i.setPropertyValue(DATECONTACT, pMessage.getPropertyValue(DCONTACT));
                    i.setPropertyValue(TYPECONTACT_C632, pMessage.getPropertyValue(CTYPE_CONTACT_C632));
                    i.setPropertyValue(INITIATEURCONTACT_C659, pMessage.getPropertyValue(CSENS_CONTACT_C659));
                    i.setPropertyValue(DATELECTURECLIENT, nowDate);
                    r.addItem(i);
                } else if (1 == messages.length) {
                    MutableRepository r = (MutableRepository) getRepositoryMessages();
                    MutableRepositoryItem i = r.getItemForUpdate(messages[0].getRepositoryId(), CASTO_CONTACT);
                    i.setPropertyValue(DATELECTURECLIENT, nowDate);
                    r.updateItem(i);
                }  // end if-else
            }  // end if
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch
    }
    
    public boolean hasOrders(String pUserId) {
        boolean result = false;
        RepositoryView ordersFOView;
        try {
            final RqlStatement countAll = RqlStatement.parseRqlStatement("profileId = ?0 AND not state = ?1");

            ordersFOView = getOrderTools().getOrderRepository().getView(ORDER_VIEW_NAME);
            final int ordersCount = countAll.executeCountQuery(ordersFOView, new Object[] {pUserId, OrderStates.INCOMPLETE.toUpperCase()} );

            result = (ordersCount > 0);
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }
    

    /**
     * Reads Back Office orders to Fron Office for user by user id.
     *
     * @param  pUserId - user id.
     *
     * @return count of readed orders.
     */
    public long readOrders(String pUserId) {
        long result = 0;
        try {
            RepositoryView ordersView = getRepositoryBO().getView(BO_ORDER_ITEM_NAME);
            RepositoryItem[] orders = ordersView.executeQuery(ordersView.getQueryBuilder().createUnconstrainedQuery());
            if (null != orders) {
                RepositoryView ordersFOView = getOrderTools().getOrderRepository().getView(ORDER_VIEW_NAME);
                RepositoryItem[] ordersFO =
                    ordersFOView.executeQuery(ordersFOView.getQueryBuilder().createUnconstrainedQuery());
                List<String> listOrderFOIds = new ArrayList<String>(ordersFO.length);
                for (RepositoryItem anOrdersFO : ordersFO) {
                    listOrderFOIds.add(anOrdersFO.getRepositoryId());
                }
                for (int i = 0; i < orders.length; i++) {
                    String order_id = (String) orders[i].getPropertyValue(NCOMMANDE_CLIENT);
                    if (!listOrderFOIds.contains(order_id)) {
                        // tests that we could
                        OrderImpl order =
                            (OrderImpl) createOrder(pUserId, "C" + (10000 + i), getOrderTools().getDefaultOrderType());
                        java.util.Date submittedDate = (java.util.Date) orders[i].getPropertyValue(DENVOI);
                        if (null != submittedDate) {
                            order.setSubmittedTime(submittedDate.getTime());
                        }
                        List pgl = order.getPaymentGroups();
                        for (Object aPgl : pgl) {
                            PaymentGroupImpl pg = (PaymentGroupImpl) aPgl;
                            pg.setStateAsString(PaymentGroupStates.SETTLED.toUpperCase());
                        }
                        List sgl = order.getShippingGroups();
                        for (Object aSgl : sgl) {
                            ShippingGroupImpl sg = (ShippingGroupImpl) aSgl;
                            sg.setStateAsString(ShippingGroupStates.NO_PENDING_ACTION.toUpperCase());
                        }
                        order.setStateAsString(OrderStates.NO_PENDING_ACTION.toUpperCase());
                        addOrder(order);

                        String id = order.getId();

                        MutableRepository rbo = (MutableRepository) getRepositoryBO();
                        MutableRepositoryItem obo =
                            rbo.getItemForUpdate(orders[i].getRepositoryId(), BO_ORDER_ITEM_NAME);
                        obo.setPropertyValue(NCOMMANDE_CLIENT, id);
                        rbo.updateItem(obo);
                    }  // end if
                }  // end for
            }  // end if
        } catch (ContainerException ce) {
            if (isLoggingError()) {
                logError(ce);
            }
        }  // end try-catch
        return result;
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
     * Returns repositoryMessages property.
     *
     * @return repositoryMessages property.
     */
    public Repository getRepositoryMessages() {
        return mRepositoryMessages;
    }

    /**
     * Sets the value of the repositoryMessages property.
     *
     * @param pRepositoryMessages parameter to set.
     */
    public void setRepositoryMessages(Repository pRepositoryMessages) {
        this.mRepositoryMessages = pRepositoryMessages;
    }

    /**
     * Sends e-mails.
     *
     * @param pTools   the ProfileTools.
     * @param pUser    the user RepositoryItem.
     * @param mMotif   the string value motif.
     * @param mMessage the message.
     */
    public void sendEmail(ProfileTools pTools, RepositoryItem pUser, String mMotif, String mMessage) {
        if (null != pUser) {
            String email = (String) pUser.getPropertyValue(EMAIL_PROFILE_PROP);
            String fromName = (String) pUser.getPropertyValue(LAST_NAME_PROFILE_PROP);

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(EMAIL_PARAMETER_CIVILITE, pUser.getPropertyValue(TITLE_PROFILE_PROP));
            params.put(EMAIL_PARAMETER_NOM, fromName);
            params.put(EMAIL_PARAMETER_PRENOM, pUser.getPropertyValue(FIRST_NAME_PROFILE_PROP));
            params.put(EMAIL_PARAMETER_EMAIL, email);
            RepositoryItem contactInfo = (RepositoryItem) pUser.getPropertyValue(BILLING_ADDRESS);
            params.put(EMAIL_PARAMETER_ADRESSE, contactInfo.getPropertyValue(ADDRESS_1_ADDRESS_PROP));
            params.put(EMAIL_PARAMETER_CODEPOSTAL, contactInfo.getPropertyValue(POSTAL_CODE_ADDRESS_PROP));
            params.put(EMAIL_PARAMETER_VILLE, contactInfo.getPropertyValue(CITY_ADDRESS_PROP));
            params.put(EMAIL_PARAMETER_TELEPHONE, contactInfo.getPropertyValue(PHONE_NUMBER_ADDRESS_PROP));
            params.put(EMAIL_PARAMETER_MESSAGE, mMessage);
            params.put(EMAIL_PARAMETER_MOTIF, mMotif);

            TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();

            emailInfo.setTemplateURL(getTemplateUrl());
            emailInfo.setMessageSubject(getMessageRequestSubmittedSubject());

            if (!StringUtils.isEmpty(email)) {
                String qualifiedFrom = fromName + "<" + email + ">";
                emailInfo.setMessageFrom(qualifiedFrom.trim());
            }

            Locale userLocale = null;
            String locale = null;
            DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

            if (request != null) {
                if (request.getRequestLocale() != null) {
                    userLocale = request.getRequestLocale().getLocale();

                    if (userLocale != null) {
                        locale = userLocale.getLanguage() + "_" + userLocale.getCountry();
                    }
                }
            }

            if (StringUtils.isEmpty(locale)) {
                locale = LOCALE_FR;
            }

            params.put(EMAIL_PARAMETER_LOCALE, locale);

            emailInfo.setTemplateParameters(params);

            List<String> recipents = new ArrayList<String>();
            recipents.add(emailInfo.getMessageTo());
            try {
                getEmailSender().sendEmailMessage(emailInfo, recipents, isSendEmailInSeparateThread(),
                                                  isPersistEmails());
            } catch (TemplateEmailException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }  // end if
    }

    /**
     * Returns message property.
     *
     * @param  messageId parameter to set.
     *
     * @return message property.
     */
    public RepositoryItem getMessage(String messageId) {
        RepositoryItem result = null;
        try {
            result = getRepositoryBO().getItem(messageId, BO_MESSAGE_ITEM_NAME);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Returns orderFOByMessage property.
     *
     * @param  messageId parameter to set.
     *
     * @return orderFOByMessage property.
     */
    public RepositoryItem getOrderFOByMessage(String messageId) {
        return getOrderFOByMessage(getMessage(messageId));
    }

    /**
     * Returns orderFOByMessage property.
     *
     * @param  message parameter to set.
     *
     * @return orderFOByMessage property.
     */
    public RepositoryItem getOrderFOByMessage(RepositoryItem message) {
        RepositoryItem result = null;
        try {
            String orderBOId = ((Long) message.getPropertyValue(CVENTE)).toString();
            RepositoryItem orderBO = getRepositoryBO().getItem(orderBOId, BO_ORDER_ITEM_NAME);
            result = getOrderFO(orderBO);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Create new message.
     *
     * @param orderId      the order id.
     * @param messageMotif the motif code.
     * @param messageText  the message.
     */
    public void newMessage(String orderId, Short messageMotif, String messageText) {
        try {
            MutableRepository r = (MutableRepository) getRepositoryMessages();
            MutableRepositoryItem i = r.createItem(CASTO_CONTACT);
            i.setPropertyValue(IDORDER, orderId);
            i.setPropertyValue(CONTENU, messageText);
            i.setPropertyValue(CMOTIF_C618, messageMotif);
            i.setPropertyValue(DATECONTACT, new Date(System.currentTimeMillis()));
            i.setPropertyValue(TYPECONTACT_C632, (short) 1);
            i.setPropertyValue(INITIATEURCONTACT_C659, CLIENT);
            r.addItem(i);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }

    /**
     * Create reply on message.
     *
     * @param messageId   the message id.
     * @param messageText the message.
     */
    public void replayMessage(String messageId, String messageText) {
        try {
            RepositoryItem message = getMessage(messageId);
            RepositoryItem order = getOrderFOByMessage(message);
            if ((null != order) && (null != message)) {
                String orderId = order.getRepositoryId();
                MutableRepository r = (MutableRepository) getRepositoryMessages();
                MutableRepositoryItem i = r.createItem(CASTO_CONTACT);
                i.setPropertyValue(IDORDER, orderId);
                i.setPropertyValue(CONTENU, messageText);
                i.setPropertyValue(CMOTIF_C618,
                                   getContactFile(((Integer) message.getPropertyValue(CRECLAMATION)).toString())
                                   .getPropertyValue(CMOTIF_RECLAMATION_C618));
                i.setPropertyValue(DATECONTACT, new Date(System.currentTimeMillis()));
                i.setPropertyValue(TYPECONTACT_C632, message.getPropertyValue(CTYPE_CONTACT_C632));
                i.setPropertyValue(INITIATEURCONTACT_C659, CLIENT);
                r.addItem(i);
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch
    }

    /**
     * Create message copy.
     *
     * @param messageId the message id.
     */
    public void copyMessage(String messageId) {
        try {
            RepositoryItem message = getMessage(messageId);
            RepositoryItem order = getOrderFOByMessage(message);
            if ((null != order) && (null != message)) {
                String orderId = order.getRepositoryId();
                MutableRepository r = (MutableRepository) getRepositoryMessages();
                MutableRepositoryItem i = r.createItem(CASTO_CONTACT);
                i.setPropertyValue(IDORDER, orderId);
                i.setPropertyValue(CONTENU, message.getPropertyValue(LCONTACT));
                i.setPropertyValue(CMOTIF_C618,
                                   getContactFile((message.getPropertyValue(CRECLAMATION)).toString())
                                   .getPropertyValue(CMOTIF_RECLAMATION_C618));
                i.setPropertyValue(DATECONTACT, new Date(System.currentTimeMillis()));
                i.setPropertyValue(TYPECONTACT_C632, message.getPropertyValue(CTYPE_CONTACT_C632));
                i.setPropertyValue(INITIATEURCONTACT_C659, CLIENT);
                r.addItem(i);
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch
    }

    /**
     * Returns questions property.
     *
     * @return questions property.
     */
    public RepositoryItem[] getQuestions() {
        RepositoryItem[] result = null;
        try {
            RepositoryView questionsView = getRepositoryMotifs().getView(MESSAGE_QUESTION);
            SortDirectives sortDirectives = new SortDirectives();
            sortDirectives.addDirective(new SortDirective(ORDRE, SortDirective.DIR_ASCENDING));
            result =
                questionsView.executeQuery(questionsView.getQueryBuilder().createUnconstrainedQuery(),
                                           new QueryOptions(0, -1, sortDirectives, new String[] {ORDRE}));
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Chekcs for last message.
     *
     * @param  messageId    the message id.
     * @param  messageOrder the message order.
     *
     * @return true when last message, otherwise false.
     */
    public boolean checkForLastMessage(String messageId, RepositoryItem messageOrder) {
        RepositoryItem[] items = getMessages(null, messageOrder, false);
        return ((null != messageId) && (null != items) && (0 < items.length) &&
                messageId.equals(items[0].getRepositoryId()));
    }

    /**
     * Saves order summary details.
     * 
     * @param pOrder order.
     */
    public void saveInfoForOrderSummary(CastOrderImpl pOrder) {
        CastoShippingTools shippingTools = getShippingTools();
        CastOrderTools orderTools = (CastOrderTools) getOrderTools(); 
        
        double totalDiscount = 0;
        double processingFees = 0;
        double shippingFees = 0;
        double shippingDiscount = 0;
        double totalWeight = 0;
        double itemsDiscount = 0;
        
        double itemWeight = 0;
        long itemQuantity = 0;
        try {
            
            OrderPriceInfo orderPriceInfo = pOrder.getPriceInfo();
            if ((orderPriceInfo != null) && (orderPriceInfo.getAdjustments() != null)) {
                for (PricingAdjustment pa : (List<PricingAdjustment>) orderPriceInfo.getAdjustments()) {
                    if (Constants.HANDLING_PRICE_ADJUSTMENT_DESCRIPTION.equalsIgnoreCase(pa.getAdjustmentDescription())) {
                        processingFees += pa.getTotalAdjustment();
                    }
                }
            }
            
            List<CommerceItem> commerceItems = (List<CommerceItem>) pOrder.getCommerceItems();
            for (CommerceItem item : commerceItems) {
                itemQuantity = item.getQuantity();
                itemWeight = shippingTools.getWeight(item);
                totalWeight += (itemQuantity * itemWeight);

                if (item instanceof CastCommerceItemImpl) {
                    CastCommerceItemImpl citem = (CastCommerceItemImpl) item;
                    citem.setDeliveryPeriod(new Integer(shippingTools.calculateDeliveries(citem.getCatalogRefId(), itemQuantity)));
                }
            }   

            itemsDiscount=orderTools.getItemsDiscount(pOrder);
            totalDiscount = itemsDiscount + orderTools.getOrderDiscount(pOrder);
            
            List<ShippingGroup> shippingGroups = (List<ShippingGroup>) pOrder.getShippingGroups();
            for (ShippingGroup shippingGroup : shippingGroups) {
                ShippingPriceInfo priceInfo = shippingGroup.getPriceInfo();
                if ( null != priceInfo ) {
                    List<PricingAdjustment> adjs = (List<PricingAdjustment>) priceInfo.getAdjustments();
                    if ( null != adjs ) {
                        for (PricingAdjustment adj : adjs) {
                            double tmpAmount = adj.getTotalAdjustment();
                            shippingFees += tmpAmount;
                            if (tmpAmount < 0) {
                                shippingDiscount += tmpAmount;
                            }
                        }
                    }
                }  // end if
            }  // end for
        } catch (Exception e) {
            if ( isLoggingError() ) {
                logError(e);
            }
        }  // end try-catch

        pOrder.setItemsDiscount(itemsDiscount);
        pOrder.setTotalDiscount(totalDiscount);
        pOrder.setProcessingFees(processingFees);
        pOrder.setShippingFees(shippingFees);
        pOrder.setShippingDiscount(shippingDiscount);
        pOrder.setTotalWeight(totalWeight);
    }
    
    /**
     * Acquires lock.
     * 
     * @param pLockName
     *            lock name.
     * @return lock.
     */
    public boolean acquireLock(Serializable pLockName, long timeout) {
        boolean globalLock = false;
        globalLock = acquireGlobalLock(pLockName, timeout);
        if (globalLock) {
            return true;
        } else {
            ClientLockManager clientLockManager = getClientLockManager();
            if (!clientLockManager.getUseLockServer()) {
                if (isLoggingDebug()) {
                    logDebug("ClientLockManager not using global lock server -- accepting local lock instead.");
                }
                return true;
            } else {
                if (isLoggingDebug()) {
                    logDebug("Could not obtain lock.");
                }
            }
        }
        return false;
    }

    /**
     * Acquires global lock only.
     * 
     * @param pLockName
     *            lock name.
     * @return lock.
     */
    public boolean acquireGlobalLock(Serializable pLockName, long timeout) {
        boolean globalLock = false;
        boolean release = false;
        ClientLockManager clientLockManager = getClientLockManager();
        try {
            globalLock = clientLockManager.acquireWriteLock(pLockName,
                    Thread.currentThread(), timeout);
            if (globalLock) {
                if (isLoggingDebug()) {
                    logDebug("Obtained global lock");
                }
                return true;
            } else {
                if (isLoggingDebug()) {
                    logDebug("Could not obtain global lock.");
                }
                release = true;
            }
        } catch (DeadlockException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (TimeExceededException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } finally {
            try {
                if (release) {
                    clientLockManager.releaseWriteLock(pLockName);
                }
            } catch (LockManagerException lme) {
                if (isLoggingError()) {
                    logError(lme);
                }
            }
        }
        return false;
    }
    
    /**
     * Releases lock.
     * @param pLockName lock anme.
     */
    public void releaseLock(Serializable pLockName) {
        try {
            ClientLockManager clientLockManager = getClientLockManager();
            clientLockManager.releaseWriteLock(pLockName);
            if ( isLoggingDebug() ) {
                logDebug("Lock Released");
            }
        } catch (LockManagerException lme) {
            if ( isLoggingError() ) {
                logError(lme);
            }
        }
    }

     public Integer increaseTransactionNumber(String orderId) {
            Integer result = null;
            
            if ( isLoggingInfo() ) {
                logInfo("increaseTransactionNumber(" + orderId + ") - start");
            }
            
            boolean clearCache = false;
            int attemptCount = getAttemptCount();
            for (int i = 0; i < attemptCount; i++){

                MutableRepositoryItem order = getUpdatableOrderFO(orderId, clearCache);
                
                if ( isLoggingInfo() ) {
                    logInfo("increaseTransactionNumber(" + orderId + ") - order is retrieved: " + order);
                }
                if ( null != order ) {
                    Integer transCount = (Integer) order.getPropertyValue("transactionCount");
                    if(transCount != null) {
                        int transactionCount = ++transCount;
    
                        if ( isLoggingInfo() ) {
                            logInfo("increaseTransactionNumber(" + orderId + ") - current transactionCount: " + transactionCount);
                        }
                        try {
                            //get Mutable Repository
                            MutableRepository repository = ((MutableRepository) order.getRepository());
                            //ConcurrentUpdateException caught updating an item during a commit.  
                            //Call updateItem explicitly to catch and handle this exception.
                            //MK: we will try get exception in case when order doesn't have last version
                            repository.updateItem(order);
                            
                            order.setPropertyValue("transactionCount", transactionCount);
    
                            if ( isLoggingInfo() ) {
                                logInfo("increaseTransactionNumber(" + orderId + ") - new transactionCount: " + transactionCount);
                            }
    
                            repository.updateItem(order);
                            result = transactionCount;
                            
                            if ( isLoggingInfo() ) {
                                logInfo("increaseTransactionNumber(" + orderId + ") - transactionCount updated successfully");
                            }
                        } catch (ConcurrentUpdateException cue) {
                            if ( isLoggingWarning() ) {
                                logWarning(i + " attempt to update order.",cue);
                            }
                            clearCache = true;
                            continue;
                        } catch (RepositoryException e) {
                            if ( isLoggingError() ) {
                                logError(e);
                            }
                        }
                    } else {
                        if ( isLoggingError() ) {
                            logError("increaseTransactionNumber(" + orderId + ") - current transactionCount is null");
                            logError(order.toString());
                        }
                    }
                }
                break;

            }

            if ( isLoggingInfo() ) {
                logInfo("increaseTransactionNumber(" + orderId + ") - finish: " + result);
            }

            return result;
        }

    /**
     * Marks order as handled Paybox.
     * @param orderId order id.
     * @param isHandled is handled.
      */
    public void markOrderAsHandledPaybox(String orderId, Boolean isHandled, String paymentSource, String userId) {
        if ( isLoggingInfo() ) {
            logInfo("markOrderAsHandledPaybox(" + orderId + ", " + isHandled + ", " + paymentSource + ", " + userId + ") - start");
        }

        boolean gotLock = false;
        try {
            gotLock = acquireLock(orderId, getOrderLockTimeout());

            if ( isLoggingInfo() ) {
                logInfo("markOrderAsHandledPaybox(" + orderId + ") - lock is acquired: " + gotLock);
            }
            if (gotLock) {
                boolean clearCache = false;
                int attemptCount = getAttemptCount();
                for (int i = 0; i < attemptCount; i++){
                    MutableRepositoryItem order = getUpdatableOrderFO(orderId);
                    if ( null != order ) {
                        try {
                            //get Mutable Repository
                            MutableRepository repository = ((MutableRepository) order.getRepository());
                            //ConcurrentUpdateException caught updating an item during a commit.  
                            //Call updateItem explicitly to catch and handle this exception.
                            //MK: we will try get exception in case when order doesn't have last version
                            repository.updateItem(order);
                            
                            order.setPropertyValue("payboxHandled", isHandled);
                            if ( null != paymentSource ) {
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_SOURCE, paymentSource);
                            }
                            if ( null != userId ) {
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_USER_ID, userId);
                            }
                            repository.updateItem(order);
                        } catch (ConcurrentUpdateException cue) {
                            if ( isLoggingWarning() ) {
                                logWarning(i + " attempt to update order.",cue);
                            }
                            clearCache = true;
                            continue;
                        } catch (RepositoryException e) {
                            if ( isLoggingError() ) {
                                logError(ORDER_ID_MESS + orderId, e);
                            }
                        }
                    }
                    break;
                }
            }
        } finally {
            if (gotLock) {
                releaseLock(orderId);

                if ( isLoggingInfo() ) {
                    logInfo("markOrderAsHandledPaybox(" + orderId + ") - lock is released");
                }
            }            
        }

        if ( isLoggingInfo() ) {
            logInfo("markOrderAsHandledPaybox(" + orderId + ", " + isHandled + ", " + paymentSource + ", " + userId + ") - finish");
        }
    }

    /**
     * Gets order as handled paybox.
      */
    public boolean getOrderAsHandledPaybox(String orderId) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("getOrderAsHandledPaybox(" + orderId + ") - start");
        }

        RepositoryItem order = getOrderFO(orderId);
        if ( null != order ) {
            Object payboxHandled = order.getPropertyValue("payboxHandled");

            if ( isLoggingInfo() ) {
                logInfo("getOrderAsHandledPaybox(" + orderId + ") - payboxHandled: " + payboxHandled);
            }

            if ( null == payboxHandled ) {
                result = false;
            } else if ( payboxHandled instanceof Boolean ) {
                result = (Boolean) payboxHandled;
            } else {
                if ( isLoggingError() ) {
                    logError("Wrong payboxHandled value " + payboxHandled + " in order: " + orderId);
                }
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("getOrderAsHandledPaybox(" + orderId + ") - finish: " + result);
        }

        return result;
    }
    
    /**
     * Checks when order status is submitted.
     * @param orderId order id.
     * @return true when order status is submitted, otherwise false.
     */
    public boolean checkSubmittedOrderStatus(String orderId) {
       return checkOrderStatus(orderId, OrderStates.SUBMITTED);
    }

    /**
     * Checks when order status is failed.
     * @param orderId order id.
     * @return true when order status is failed, otherwise false.
     */
    public boolean checkFailedOrderStatus(String orderId) {
        return checkOrderStatus(orderId, OrderStates.FAILED);
    }

    /**
     * Checks rder status.
     * @param orderId order id.
     * @param orderStatus status for check
     * @return true when order status is submitted, otherwise false.
     */
    private boolean checkOrderStatus(String orderId, String orderStatus) {
        boolean result = false;

        if (isLoggingInfo()) {
            vlogInfo("checkOrderStatus {0} for status {1} - start", orderId, orderStatus);
        }

        RepositoryItem order = getOrderFO(orderId);

        if (isLoggingInfo()) {
            vlogInfo("checkOrderStatus {0} - order is retrieved: {1}", orderId, order);
        }

        if (null != order) {
            Object state = order.getPropertyValue("state");

            if (isLoggingInfo()) {
                vlogInfo("checkOrderStatus {0} - order status is retrieved: {1}", orderId, order);
            }

            if (null != state) {
                result = state.toString().toLowerCase().endsWith(orderStatus);
            }
        }

        if (isLoggingInfo()) {
            vlogInfo("checkOrderStatus {0} - finish: {1}", orderId, result);
        }

        return result;
    }

    /**
     * Set submitted date to Front-Office order.
     */
    public void setSubmittedDateFO(String orderId) {
        if ( isLoggingDebug() ) {
            logDebug("Try locked setSubmittedDateFO: " + orderId);
        }
        boolean gotLock = false;
        try {
            gotLock = acquireLock(orderId, getOrderLockTimeout());
            if (gotLock) {
                int attemptCount = getAttemptCount();
                for (int i = 0; i < attemptCount; i++){
                    MutableRepositoryItem order = getUpdatableOrderFO(orderId);
                    if ( null != order ) {
                        try {
                            //get Mutable Repository
                            MutableRepository repository = ((MutableRepository) order.getRepository());
                            //ConcurrentUpdateException caught updating an item during a commit.  
                            //Call updateItem explicitly to catch and handle this exception.
                            //MK: we will try get exception in case when order doesn't have last version
                            repository.updateItem(order);
                            
                            order.setPropertyValue("submitteddate", new java.sql.Timestamp(System.currentTimeMillis()));
                            ((MutableRepository) order.getRepository()).updateItem(order);
                        } catch (ConcurrentUpdateException cue) {
                            if ( isLoggingWarning() ) {
                                logWarning(i + " attempt to update order.",cue);
                            }
                            continue;
                        } catch (RepositoryException e) {
                            if ( isLoggingError() ) {
                                logError(ORDER_ID_MESS + orderId, e);
                            }
                        }
                    }
                    break;
                }
                
            } else {
                if ( isLoggingDebug() ) {
                    logDebug("Already locked. Sleeping.");
                }
            }
        } finally {
            if (gotLock) {
                releaseLock(orderId);
            }
        }
    }

    /**
     * Set state to Front-Office order.
     */
    public boolean setStateFO(String orderId, String state) {
        boolean result = false;
        if ( isLoggingDebug() ) {
            logDebug("Try locked setStateFO: " + orderId + ", status = " + state);
        }
        boolean gotLock = false;
        try {
            gotLock = acquireLock(orderId, getOrderLockTimeout());
            if (gotLock) {
                if ( !checkSubmittedOrderStatus(orderId) ) {
                    int attemptCount = getAttemptCount();
                    for (int i = 0; i < attemptCount; i++){
                        MutableRepositoryItem order = getUpdatableOrderFO(orderId);
                        if ( null != order ) {
                            try {
                                //get Mutable Repository
                                MutableRepository repository = ((MutableRepository) order.getRepository());
                                //ConcurrentUpdateException caught updating an item during a commit.  
                                //Call updateItem explicitly to catch and handle this exception.
                                //MK: we will try get exception in case when order doesn't have last version
                                repository.updateItem(order);
                                
                                order.setPropertyValue("state", state);
                                repository.updateItem(order);
                                result = true;
                            } catch (ConcurrentUpdateException cue) {
                                if ( isLoggingWarning() ) {
                                    logWarning(i + " attempt to update order.",cue);
                                }
                                continue;
                            } catch (RepositoryException e) {
                                if ( isLoggingError() ) {
                                    logError(ORDER_ID_MESS+state, e);
                                }
                            }
                        }
                        break;
                    }
                } else {
                    result = true;
                }
            } else {
                if ( isLoggingDebug() ) {
                    logDebug("Already locked. Sleeping.");
                }
            }
        } finally {
            if (gotLock) {
                releaseLock(orderId);
            }
        }
        return result;
    }

    /**
     * Set PENDING PAYBOX state to Front-Office order.
     */
    public boolean setPendingPayboxStateFO(String orderId) {
        if ( isLoggingInfo() ) {
            logInfo("setPendingPayboxStateFO(" + orderId + ") - start");
        }

        boolean result = setPendingPayboxStateFO(orderId, true);
        
        if ( isLoggingInfo() ) {
            logInfo("setPendingPayboxStateFO(" + orderId + ") - finish: " + result);
        }

        return result;
    }
    
    public boolean setFailedPayboxStateFO(String orderId) {
        if ( isLoggingInfo() ) {
            logInfo("setFailedPayboxStateFO(" + orderId + ") - start");
        }

        boolean result = setFailedPayboxStateFO(orderId, true);
        
        if ( isLoggingInfo() ) {
            logInfo("setFailedPayboxStateFO(" + orderId + ") - finish: " + result);
        }

        return result;
    }

    
    /**
     * Set PENDING PAYBOX state to Front-Office order.
     */
    public boolean setPendingPayboxStateFO(String orderId, boolean withLock) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + ") - start");
        }
        
        boolean gotLock = false;
        try {
            gotLock = !withLock || acquireLock(orderId, getOrderLockTimeout());
            
            if ( isLoggingInfo() ) {
                logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + 
                            ") - lock is acquired: " + gotLock);
            }

            if (gotLock) {
                result = checkSubmittedOrderStatus(orderId);

                if ( isLoggingInfo() ) {
                    logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + 
                                ") - order is already submit: " + result);
                }

                if ( !result ) {
                    
                    boolean clearCache = false;
                    int attemptCount = getAttemptCount();
                    for (int i = 0; i < attemptCount; i++){

                        MutableRepositoryItem order = getUpdatableOrderFO(orderId, clearCache);
    
                        if ( isLoggingInfo() ) {
                            logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + 
                                        ") - order is retrieved: " + order);
                        }

                        if ( null != order ) {
                            try {
                                //get Mutable Repository
                                MutableRepository repository = ((MutableRepository) order.getRepository());
                                //ConcurrentUpdateException caught updating an item during a commit.  
                                //Call updateItem explicitly to catch and handle this exception.
                                //MK: we will try get exception in case when order doesn't have last version
                                repository.updateItem(order);
                                
                                /*CACHE MODE - inherit */ 
                                order.setPropertyValue("lastModifiedDate", new java.sql.Timestamp(System.currentTimeMillis()));
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue("state", getOrderStates().getStateString(getOrderStates().
                                                                    getStateValue(CastOrderStates.PENDING_PAYBOX)));
                                int boOrderStateNum = getBOOrderStates().getStateValue(BOOrderStates.PENDING_PAYBOX);
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE, 
                                                            getBOOrderStates().getStateString(boOrderStateNum));
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM, boOrderStateNum);
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_DETAIL, 
                                                        getBOOrderStates().getStateDescription(boOrderStateNum));
                                repository.updateItem(order);
                                result = true;
    
                                if ( isLoggingInfo() ) {
                                    logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + 
                                                ") - 'PENDING_PAYBOX' status is set successfully");
                                }
                            } catch (ConcurrentUpdateException cue) {
                                if ( isLoggingWarning() ) {
                                    logWarning(i + " attempt to update order.",cue);
                                }
                                clearCache = true;
                                continue;
                            } catch (RepositoryException e) {
                                if ( isLoggingError() ) {
                                    logError(ORDER_ID_MESS + orderId, e);
                                }
                            }
                        }
                        break;
                    }

                }
            }
        } finally {
            if (gotLock && withLock) {
                releaseLock(orderId);
            }
            
            if ( isLoggingInfo() ) {
                logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + ") - lock is released");
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + ") - finish: " + result);
        }
        
        return result;
    }
    
    /**
     * Set FAILED PAYBOX state to Front-Office order.
     */
    public boolean setFailedPayboxStateFO(String orderId, boolean withLock) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("setFailedPayboxStateFO(" + orderId + ", " + withLock + ") - start");
        }
        
        boolean gotLock = false;
        try {
            gotLock = !withLock || acquireLock(orderId, getOrderLockTimeout());
            
            if ( isLoggingInfo() ) {
                logInfo("setFailedPayboxStateFO(" + orderId + ", " + withLock + 
                            ") - lock is acquired: " + gotLock);
            }

            if (gotLock) {
                result = checkSubmittedOrderStatus(orderId);

                if ( isLoggingInfo() ) {
                    logInfo("setFailedPayboxStateFO(" + orderId + ", " + withLock + 
                                ") - order is already submit: " + result);
                }

                if ( !result ) {
                    
                    boolean clearCache = false;
                    int attemptCount = getAttemptCount();
                    for (int i = 0; i < attemptCount; i++){

                        MutableRepositoryItem order = getUpdatableOrderFO(orderId, clearCache);
    
                        if ( isLoggingInfo() ) {
                            logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + 
                                        ") - order is retrieved: " + order);
                        }

                        if ( null != order ) {
                            try {
                                //get Mutable Repository
                                MutableRepository repository = ((MutableRepository) order.getRepository());
                                //ConcurrentUpdateException caught updating an item during a commit.  
                                //Call updateItem explicitly to catch and handle this exception.
                                //MK: we will try get exception in case when order doesn't have last version
                                repository.updateItem(order);
                                
                                /*CACHE MODE - inherit */ 
                                order.setPropertyValue("lastModifiedDate", new java.sql.Timestamp(System.currentTimeMillis()));
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue("state", getOrderStates().getStateString(getOrderStates().
                                                                    getStateValue(CastOrderStates.FAILED)));
                                int boOrderStateNum = getBOOrderStates().getStateValue(BOOrderStates.FAILED);
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE, 
                                                            getBOOrderStates().getStateString(boOrderStateNum));
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM, boOrderStateNum);
                                /*CACHE MODE - DISABLED*/ 
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_DETAIL, 
                                                        getBOOrderStates().getStateDescription(boOrderStateNum));
                                repository.updateItem(order);
                                result = true;
    
                                if ( isLoggingInfo() ) {
                                    logInfo("setFailedPayboxStateFO(" + orderId + ", " + withLock + 
                                                ") - 'FAILED' status is set successfully");
                                }
                            } catch (ConcurrentUpdateException cue) {
                                if ( isLoggingWarning() ) {
                                    logWarning(i + " attempt to update order.",cue);
                                }
                                clearCache = true;
                                continue;
                            } catch (RepositoryException e) {
                                if ( isLoggingError() ) {
                                    logError(ORDER_ID_MESS + orderId, e);
                                }
                            }
                        }
                        break;
                    }

                }
            }
        } finally {
            if (gotLock && withLock) {
                releaseLock(orderId);
            }
            
            if ( isLoggingInfo() ) {
                logInfo("setFailedPayboxStateFO(" + orderId + ", " + withLock + ") - lock is released");
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("setPendingPayboxStateFO(" + orderId + ", " + withLock + ") - finish: " + result);
        }
        
        return result;
    }
    
    /**
     * Set PENDING REMOVE state to Front-Office order.
     */
    public boolean setPendingRemoveStateFO(String orderId, boolean withLock) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + ") - start");
        }
        
        boolean gotLock = false;
        try {
            gotLock = !withLock || acquireLock(orderId, getOrderLockTimeout());
            
            if ( isLoggingInfo() ) {
                logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + 
                            ") - lock is acquired: " + gotLock);
            }

            if (gotLock) {
                result = checkSubmittedOrderStatus(orderId);

                if ( isLoggingInfo() ) {
                    logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + 
                                ") - order is already submit: " + result);
                }

                if ( !result ) {
                    int attemptCount = getAttemptCount();
                    for (int i = 0; i < attemptCount; i++){
                        MutableRepositoryItem order = getUpdatableOrderFO(orderId);
    
                        if ( isLoggingInfo() ) {
                            logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + 
                                        ") - order is retrieved: " + order);
                        }
    
                        if ( null != order ) {
                            try {
                                //get Mutable Repository
                                MutableRepository repository = ((MutableRepository) order.getRepository());
                                //ConcurrentUpdateException caught updating an item during a commit.  
                                //Call updateItem explicitly to catch and handle this exception.
                                //MK: we will try get exception in case when order doesn't have last version
                                repository.updateItem(order);
                                
                                order.setPropertyValue("lastModifiedDate", new java.sql.Timestamp(System.currentTimeMillis()));
                                order.setPropertyValue("state", getOrderStates().getStateString(getOrderStates().
                                                                    getStateValue(CastOrderStates.PENDING_REMOVE)));
                                int boOrderStateNum = getBOOrderStates().getStateValue(BOOrderStates.PENDING_REMOVE);
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE, 
                                                            getBOOrderStates().getStateString(boOrderStateNum));
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM, boOrderStateNum);
                                order.setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_DETAIL, 
                                                            getBOOrderStates().getStateDescription(boOrderStateNum));
                                repository.updateItem(order);
                                result = true;

                                if ( isLoggingInfo() ) {
                                    logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + 
                                                ") - 'PENDING_REMOVE' status is set successfully");
                                }
                            } catch (ConcurrentUpdateException cue) {
                                if ( isLoggingWarning() ) {
                                    logWarning(i + " attempt to update order.",cue);
                                }
                                continue;
                            } catch (RepositoryException e) {
                                if ( isLoggingError() ) {
                                    logError(ORDER_ID_MESS + orderId, e);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } finally {
            if (gotLock && withLock) {
                releaseLock(orderId);
            }
            
            if ( isLoggingInfo() ) {
                logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + ") - lock is released");
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("setPendingRemoveStateFO(" + orderId + ", " + withLock + ") - finish: " + result);
        }
        
        return result;
    }

    /**
     * Checks when order status is complete.
     * @param orderId order id.
     * @return true when order status is complete, otherwise false.
     */
    public boolean checkCompleteOrderStatus(String orderId) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("checkCompleteOrderStatus(" + orderId + ") - start");
        }
        
        RepositoryItem order = getOrderFO(orderId);

        if ( isLoggingInfo() ) {
            logInfo("checkCompleteOrderStatus(" + orderId + ") - order is retrieved: " + order);
        }
        
        if ( null != order ) {
            Object state = order.getPropertyValue("state");
            
            if ( isLoggingInfo() ) {
                logInfo("checkCompleteOrderStatus(" + orderId + ") - order status is retrieved: " + state);
            }
            
            if ( null != state ) {
                if ( OrderStates.INCOMPLETE.equalsIgnoreCase(state.toString()) ) {
                    if ( getOrderAsHandledPaybox(orderId) ) {
                        result = setPendingPayboxStateFO(orderId);
                    } 
                } else {
                    result = true;
                }
            } else {
                if ( isLoggingError() ) {
                    logError("Wrong order state " + orderId);
                }
            }
        } else {
            if ( isLoggingError() ) {
                logError("Wrong order " + orderId);
            }
        }
        
        if ( isLoggingInfo() ) {
            logInfo("checkCompleteOrderStatus(" + orderId + ") - finish: " + result);
        }
        
        return result;
    }
    
    /**
     * Checks when order status is incomplete.
     * @param orderId order id.
     * @return true when order status is complete, otherwise false.
     */
    public boolean fixIncompleteOrderStatus(String orderId) {
        boolean result = false;
        
        if ( isLoggingInfo() ) {
            logInfo("fixIncompleteOrderStatus(" + orderId + ") - start");
        }
        
        RepositoryItem order = getOrderFO(orderId);

        if ( isLoggingInfo() ) {
            logInfo("fixIncompleteOrderStatus(" + orderId + ") - order is retrieved: " + order);
        }
        
        if ( null != order ) {
            Object state = order.getPropertyValue("state");
            
            if ( isLoggingInfo() ) {
                logInfo("fixIncompleteOrderStatus(" + orderId + ") - order status is retrieved: " + state);
            }
            
            if ( null != state ) {
                if ( OrderStates.INCOMPLETE.equalsIgnoreCase(state.toString()) ) {
                    if ( getOrderAsHandledPaybox(orderId) ) {
                        result = setPendingRemoveStateFO(orderId, true);
                    } 
                } else {
                    result = true;
                }
            } else {
                if ( isLoggingError() ) {
                    logError("Wrong order state " + orderId);
                }
            }
        } else {
            if ( isLoggingError() ) {
                logError("Wrong order " + orderId);
            }
        }
        
        if ( isLoggingInfo() ) {
            logInfo("fixIncompleteOrderStatus(" + orderId + ") - finish: " + result);
        }
        
        return result;
    }

    /**
     * Removes order.
     * @param orderId order id.
     * @param checkPendingRemove check when order in pending remove state.
     */
    public void removeOrder(String orderId, boolean checkPendingRemove) {
        if ( !checkPendingRemove || checkPendingRemove(orderId) ) {
            try {
                removeOrder(orderId);
            } catch (CommerceException e) {
                if ( isLoggingError() ) {
                    logError("Can't remove order: " + orderId, e);
                }
            }
        }
    }

    /**
     * Check Pending Remove.
     * @param orderId order id.
     */
    public boolean checkPendingRemove(String orderId) {
        boolean result = false;
        RepositoryItem order = getOrderFO(orderId);
        if ( null != order ) {
            Object state = order.getPropertyValue("state");
            if ( null != state ) {
                String pendingRemove = getOrderStateString(CastOrderStates.PENDING_REMOVE, null); 
                result = pendingRemove.equalsIgnoreCase(state.toString());
            }
        }
        return result;
    }

    /**
     * Process log order into journal.
     * @param pOrder order.
     */
    public boolean logOrder(CastOrderImpl pOrder, String pAction) {
        boolean result = false;
        if ( null != pOrder ) {
            MutableRepository repository = getJournalisationRepository();
            if ( null != repository ) {
                try {
                    MutableRepositoryItem journalItem = repository.createItem(CastoConstantes.JOURNAL_DEFAULT_ITEM);
                    journalItem.setPropertyValue(CastoConstantes.JOURNAL_DATE_ACTION_PROPERTY, new java.util.Date());
                    journalItem.setPropertyValue(CastoConstantes.JOURNAL_ACTION_PROPERTY, pAction);
                    journalItem.setPropertyValue(CastoConstantes.JOURNAL_ORDER_ID_PROPERTY, pOrder.getId());
                    String profileId = pOrder.getPaymentUserId();
                    if ( null != profileId ) { 
                        journalItem.setPropertyValue(CastoConstantes.JOURNAL_USER_ID_PROPERTY, profileId);
                        RepositoryItem profileItem = getProfileItem(profileId);
                        if ( null != profileItem ) {
                            Object login = profileItem.getPropertyValue(com.castorama.commerce.profile.Constants.LOGIN_PROFILE_PROP);
                            if ( null != login ) {
                                journalItem.setPropertyValue(CastoConstantes.JOURNAL_LOGIN_PROPERTY, login.toString());
                            }
                        }
                    }
                    repository.addItem(journalItem);
                    result = true;
                } catch (RepositoryException e) {
                    if ( isLoggingError() ) {
                        logError("Can't log order " + pOrder.getId() + " into repository.");
                    }
                }
            }
        } else {
            if ( isLoggingError() ) {
                logError("Can't null order.");
            }
        }
        return result;
    }
    
    /**
     * Returns profile repository item by profile id.
     * @param pProfileId profile id.
     * @return profile repository item by profile id.
     */
    private RepositoryItem getProfileItem(String pProfileId) {
        RepositoryItem result = null;
        OrderTools orderTools = getOrderTools();
        if ( null != orderTools ) {
            Repository profileRepository = orderTools.getProfileRepository();
            if ( null != profileRepository ) {
                ProfileTools profileTools = orderTools.getProfileTools();
                if ( null != profileTools ) { 
                    try {
                        result = profileRepository.getItem(pProfileId, profileTools.getDefaultProfileType());
                    } catch (RepositoryException e) {
                        if ( isLoggingError() ) {
                            logError("Can't get profie repository item: " + pProfileId);
                        }
                    }
                } else {
                    if ( isLoggingError() ) {
                        logError("profileTools not found.");
                    }
                }
             } else {
                if ( isLoggingError() ) {
                    logError("profileRepository not found.");
                }
             }
        } else {
            if ( isLoggingError() ) {
                logError("orderTools not found.");
            }
        }
        return result;
    }
    
    
    public void mergeOrders(Order pSrcOrder, Order pDestOrder, boolean pMergeShippingGroups, boolean pRemoveSrcOrder) throws CommerceException {
        Order[] mergeable = areOrdersMergeable(pSrcOrder, pDestOrder);
        if (mergeable != null) {
              if (mergeable.length == 1) {
                  Object [] args = {mergeable[0].getId()};
                  throw new CommerceException(ResourceUtils.getMsgResource("OrderIsNotMergeable", MY_RESOURCE_NAME, sResourceBundle, args));
              } else {
                  Object[] args = {mergeable[0].getId(), mergeable[1].getId() };
                  throw new CommerceException(ResourceUtils.getMsgResource("OrdersAreNotMergeable", MY_RESOURCE_NAME, sResourceBundle, args));
              }
        }
          
        Boolean lAtoutFlag = getOrderLAtoutFlag((OrderImpl)pSrcOrder);
        if(lAtoutFlag){
            ((OrderImpl) pDestOrder).getRepositoryItem().setPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT, lAtoutFlag);
        }
        super.mergeOrders(pSrcOrder, pDestOrder, pMergeShippingGroups, pRemoveSrcOrder);
    }
      
    private Boolean getOrderLAtoutFlag(OrderImpl pOrder) {
        if (pOrder.getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT) == null) {
            return false;
        } else {
            return ((Boolean) pOrder.getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT));
        }
    }
    
    
    /**
     * Returns order state string value.
     * @param stateKey state key value.
     * @param defValue default value.
     * @return order state string value.
     */
    public String getOrderStateString(String stateKey, String defValue) {
        String result = getOrderStates().getStateString(getOrderStates().getStateValue(stateKey));
        return (null != result) ? result : defValue;
    }
    
    /**
     * Returns defaultEmailInfo property.
     *
     * @return defaultEmailInfo property.
     */
    public TemplateEmailInfo getDefaultEmailInfo() {
        return mDefaultEmailInfo;
    }

    /**
     * Sets the value of the defaultEmailInfo property.
     *
     * @param pDefaultEmailInfo parameter to set.
     */
    public void setDefaultEmailInfo(TemplateEmailInfo pDefaultEmailInfo) {
        this.mDefaultEmailInfo = pDefaultEmailInfo;
    }

    /**
     * Returns emailSender property.
     *
     * @return emailSender property.
     */
    public TemplateEmailSender getEmailSender() {
        return mEmailSender;
    }

    /**
     * Sets the value of the emailSender property.
     *
     * @param pEmailSender parameter to set.
     */
    public void setEmailSender(TemplateEmailSender pEmailSender) {
        this.mEmailSender = pEmailSender;
    }

    /**
     * Returns templateUrl property.
     *
     * @return templateUrl property.
     */
    public String getTemplateUrl() {
        return mTemplateUrl;
    }

    /**
     * Sets the value of the templateUrl property.
     *
     * @param pTemplateUrl parameter to set.
     */
    public void setTemplateUrl(String pTemplateUrl) {
        this.mTemplateUrl = pTemplateUrl;
    }

    /**
     * Returns sendEmailInSeparateThread property.
     *
     * @return sendEmailInSeparateThread property.
     */
    public boolean isSendEmailInSeparateThread() {
        return mSendEmailInSeparateThread;
    }

    /**
     * Sets the value of the sendEmailInSeparateThread property.
     *
     * @param pSendEmailInSeparateThread parameter to set.
     */
    public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
        this.mSendEmailInSeparateThread = pSendEmailInSeparateThread;
    }

    /**
     * Returns persistEmails property.
     *
     * @return persistEmails property.
     */
    public boolean isPersistEmails() {
        return mPersistEmails;
    }

    /**
     * Sets the value of the persistEmails property.
     *
     * @param pPersistEmails parameter to set.
     */
    public void setPersistEmails(boolean pPersistEmails) {
        this.mPersistEmails = pPersistEmails;
    }

    /**
     * Returns repositoryMotifs property.
     *
     * @return repositoryMotifs property.
     */
    public Repository getRepositoryMotifs() {
        return mRepositoryMotifs;
    }

    /**
     * Sets the value of the repositoryMotifs property.
     *
     * @param pRepositoryMotifs parameter to set.
     */
    public void setRepositoryMotifs(Repository pRepositoryMotifs) {
        mRepositoryMotifs = pRepositoryMotifs;
    }

    /**
     * Returns shippingTools.
     * @return the shippingTools.
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * Sets shippingTools.
     * @param pShippingTools the shippingTools to set.
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        mShippingTools = pShippingTools;
    }

    /**
     * @return the clientLockManager
     */
    public ClientLockManager getClientLockManager() {
        return mClientLockManager;
    }

    /**
     * @param pClientLockManager the clientLockManager to set
     */
    public void setClientLockManager(ClientLockManager pClientLockManager) {
        this.mClientLockManager = pClientLockManager;
    }

    /**
     * @return the orderStates
     */
    public CastOrderStates getOrderStates() {
        return mOrderStates;
    }

    /**
     * @param pOrderStates the orderStates to set
     */
    public void setOrderStates(CastOrderStates pOrderStates) {
        this.mOrderStates = pOrderStates;
    }

    /**
     * @return the bOOrderStates
     */
    public BOOrderStates getBOOrderStates() {
        return mBOOrderStates;
    }

    /**
     * @param pOrderStates the bOOrderStates to set
     */
    public void setBOOrderStates(BOOrderStates pOrderStates) {
        mBOOrderStates = pOrderStates;
    }

    /**
     * @return the messageRequestSubmittedSubject
     */
    public String getMessageRequestSubmittedSubject() {
        return mMessageRequestSubmittedSubject;
    }

    /**
     * @param messageRequestSubmittedSubject the messageRequestSubmittedSubject to set
     */
    public void setMessageRequestSubmittedSubject(String messageRequestSubmittedSubject) {
        this.mMessageRequestSubmittedSubject = messageRequestSubmittedSubject;
    }

    /**
     * @return the journalisationRepository
     */
    public MutableRepository getJournalisationRepository() {
        return mJournalisationRepository;
    }

    /**
     * @param pJournalisationRepository the journalisationRepository to set
     */
    public void setJournalisationRepository(MutableRepository pJournalisationRepository) {
        this.mJournalisationRepository = pJournalisationRepository;
    }

    /**
     * @return the orderLockTimeout
     */
    public int getOrderLockTimeout() {
        return mOrderLockTimeout;
    }
    
    /**
     * @param pOrderLockTimeout the orderLockTimeout to set
     */
    public void setOrderLockTimeout(int pOrderLockTimeout) {
        this.mOrderLockTimeout = pOrderLockTimeout;
    }

    /**
     * @return the concurentDelay
     */
    public long getConcurentDelay() {
        return mConcurentDelay;
    }
    
    /**
     * @param pConcurentDelay the concurentDelay to set
     */
    public void setConcurentDelay(long pConcurentDelay) {
        this.mConcurentDelay = pConcurentDelay;
    }
    
    /**
     * @return the attemptCount
     */
    public int getAttemptCount() {
        return mAttemptCount;
    }
    
    /**
     * @param pAttemptCount the attemptCount to set
     */
    public void setAttemptCount(int pAttemptCount) {
        this.mAttemptCount = pAttemptCount;
    }
    

}
