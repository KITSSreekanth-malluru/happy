package com.castorama.droplet;

import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.constantes.CastoConstantesOrders;

import atg.commerce.order.OrderManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.ProfileTools;

/**
 *
 * @author EPAM team
 */
public class MessageDetailsDroplet extends DynamoServlet {

	/** RESOURCE_BUNDLE_NAME constant. */
    private static final String RESOURCE_BUNDLE_NAME = "com.castorama.commerce.order.CastWebOrdersResources";

    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** MESSAGE_ID constant. */
    public static final String MESSAGE_ID = "messageId";

    /** ORDER_ID constant. */
    public static final String ORDER_ID = "orderId";

    /** ORDER constant. */
    public static final String ORDER = "order";

    /** MESSAGE constant. */
    public static final String MESSAGE = "message";

    /** FROM constant. */
    public static final String FROM = "from";

    /** TO constant. */
    public static final String TO = "to";

    /** CAN_REPLAY constant. */
    public static final String CAN_REPLAY = "canReplay";

    /** CAN_RESEND constant. */
    public static final String CAN_RESEND = "canResend";

    /** MOTIF_ID constant. */
    public static final String MOTIF_ID = "motifId";

    /** MOTIF constant. */
    public static final String MOTIF = "motif";

    /** MODE constant. */
    public static final String MODE = "mode";

    /** RESPONSE_REQUIRED constant. */
    public static final String RESPONSE_REQUIRED = "responseRequired";

    /** resourceBundle constant. */
    private ResourceBundle resourceBundle = null;

    /** mOrderManager constant. */
    private OrderManager mOrderManager;

    /** mProfileTools constant. */
    private ProfileTools mProfileTools;

    /**
     * Extends service method of DynamoServlet to read message.
     * 
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override 
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        boolean empty = true;
        // String orderId = pRequest.getParameter(ORDER_ID); NOT USED!!! 
        String messageId = pRequest.getParameter(MESSAGE_ID);
        if ( null != messageId && 0 < messageId.trim().length() ) {
        	OrderManager om = getOrderManager();
        	if (om instanceof CastOrderManager) {
        		CastOrderManager com = (CastOrderManager) om;
                try {
                    RepositoryItem message = com.getRepositoryBO().getItem(messageId, "BO_SCD_CONTACT");
                    RepositoryItem order =
                        com.getRepositoryBO().getItem(((Long) message.getPropertyValue("CVENTE")).toString(),
                                                      "BO_SCD_VENTE_WEB");
                    pRequest.setParameter(ORDER, order);
                    pRequest.setParameter(MESSAGE, message);
                    pRequest.setParameter(CAN_REPLAY, isCanReplay(com, message, order));
                    pRequest.setParameter(CAN_RESEND, isCanResend(com, message));
                    
                    Short motifId = null;
                    String motif = null;
                    Object contactFileId = message.getPropertyValue("CRECLAMATION");
                    if ( null != contactFileId ) {
                    	RepositoryItem contactFile = com.getContactFile(contactFileId.toString());
                    	if ( null != contactFile ) {
                    		motifId = (Short) contactFile.getPropertyValue("CMOTIF_RECLAMATION_C618");
                    		motif = getMessageMotifLegend(motifId);
                    	}
                    }
                    pRequest.setParameter(MOTIF_ID, motifId);
                    pRequest.setParameter(MOTIF, motif);
                                          
//                    getMessageMotifLegend((Short) com.getContactFile(((Integer)
//                                                                                                message
//                                                                                                .getPropertyValue("CRECLAMATION"))
//                                                                                           .toString())
//                                                                .getPropertyValue("CMOTIF_RECLAMATION_C618")));

                    pRequest.setParameter(MODE,
                                          getMessageModeLegend((Short) message.getPropertyValue("CSUPPORT_CONTACT_C633")));
                    pRequest.setParameter(RESPONSE_REQUIRED, isResponseRequired(com, message));
                    pRequest.setParameter(FROM,
                                          getMessageFrom(com, (Short) message.getPropertyValue("CSENS_CONTACT_C659"),
                                                         order));
                    pRequest.setParameter(TO,
                                          getMessageTo(com, (Short) message.getPropertyValue("CSENS_CONTACT_C659"),
                                                       order));
                    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                    com.changeMessageStatus(message);
                    empty = false;
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }  // end try-catch
            }  // end if
        }  // end if
        if (empty) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }
    
    /**
     * Checks when response required.
     * 
     * @param com Order manager.
     * @param message message.
     * @return true when response required, otherwise false.
     */
    private Boolean isResponseRequired(CastOrderManager pOrderManager, RepositoryItem pMessage) {
    	Boolean result;
    	if ( null != pMessage ) {
    		result = (Boolean) pMessage.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED);
    		if ( result.booleanValue() ) {
    			if ( null != pOrderManager.getMessageReaded(pMessage.getRepositoryId()) ) {
    				result = Boolean.FALSE;
    			}
    		}
    	} else {
    		result = Boolean.FALSE;
    	}
    	return result;
    }

	/**
     * Returns canResend property.
     *
     * @param  com     parameter to set.
     * @param  message parameter to set.
     *
     * @return canResend property.
     */
    private Boolean isCanResend(CastOrderManager com, RepositoryItem message) {
        boolean canResend = false;
        if (new Short((short) 2).equals((Short) message.getPropertyValue("CSENS_CONTACT_C659"))) {
            RepositoryItem contactFile =
                com.getContactFile(((Integer) message.getPropertyValue("CRECLAMATION")).toString());
            if (null == contactFile.getPropertyValue("DCLOTURE")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.HOUR, -10 * 24);
                if (calendar.getTime().after((Date) message.getPropertyValue("DCONTACT"))) {
                    canResend = true;
                }
            }
        }

        return new Boolean(canResend);
    }

    /**
     * Returns canReplay property.
     *
     * @param  com     parameter to set.
     * @param  message parameter to set.
     * @param  order   parameter to set.
     *
     * @return canReplay property.
     */
    private Boolean isCanReplay(CastOrderManager com, RepositoryItem message, RepositoryItem order) {
        boolean canReplay = false;
        if (new Short((short) 1).equals((Short) message.getPropertyValue("CSENS_CONTACT_C659"))) {
            RepositoryItem contactFile =
                com.getContactFile(((Integer) message.getPropertyValue("CRECLAMATION")).toString());
            if (null == contactFile.getPropertyValue("DCLOTURE")) {
                if (com.checkForLastMessage(message.getRepositoryId(), order)) {
                    canReplay = true;
                }
            }
        }
        return new Boolean(canReplay);
    }

    /**
     * Returns messageMotifLegend property.
     *
     * @param  motif parameter to set.
     *
     * @return messageMotifLegend property.
     */
    public String getMessageMotifLegend(Short motif) {
        resourceBundle =
            java.util.ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,
                                               atg.service.dynamo.LangLicense.getLicensedDefault());

        String result = "";
        if (null != motif) {
            try {
                result = resourceBundle.getString("motif." + motif);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns messageModeLegend property.
     *
     * @param  mode parameter to set.
     *
     * @return messageModeLegend property.
     */
    public String getMessageModeLegend(Short mode) {
        resourceBundle =
            java.util.ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,
                                               atg.service.dynamo.LangLicense.getLicensedDefault());
        String result = "";
        if (null != mode) {
            try {
                result = resourceBundle.getString("mode." + mode);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

    /**
     * Returns messageFrom property.
     *
     * @param  com    parameter to set.
     * @param  sender parameter to set.
     * @param  order  parameter to set.
     *
     * @return messageFrom property.
     */
    private String getMessageFrom(CastOrderManager com, Short sender, RepositoryItem order) {
        String result = "";
        if (null != sender) {
            if (1 == sender) {
                result = "Castorama.fr";
            } else {
                try {
                    result =
                        ProfileInfoDroplet.getProfileName((String) com.getOrderFO(order).getPropertyValue("profileId"),
                                                          getProfileTools());
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns messageTo property.
     *
     * @param  com    parameter to set.
     * @param  sender parameter to set.
     * @param  order  parameter to set.
     *
     * @return messageTo property.
     */
    private String getMessageTo(CastOrderManager com, Short sender, RepositoryItem order) {
        String result = "";
        if (null != sender) {
            if (2 == sender) {
                result = "Castorama.fr";
            } else {
                try {
                    result =
                        ProfileInfoDroplet.getProfileName((String) com.getOrderFO(order).getPropertyValue("profileId"),
                                                          getProfileTools());
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }
            }
        }
        return result;
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
        this.mProfileTools = pProfileTools;
    }

}
