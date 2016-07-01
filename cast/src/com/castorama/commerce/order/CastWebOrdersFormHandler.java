package com.castorama.commerce.order;

import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.ProfileTools;

import com.castorama.constantes.CastoConstantesOrders;

import com.castorama.droplet.ProfileInfoDroplet;

/**
 * Handles web orders form requests.
 *
 * @author EPAM team
 */
public class CastWebOrdersFormHandler extends GenericFormHandler {
    /** RESOURCE_BUNDLE_NAME constant. */
    private static final String RESOURCE_BUNDLE_NAME = "com.castorama.commerce.order.CastWebOrdersResources";

    /** MOTIFS_RESOURCE_BUNDLE_NAME constant. */
    private static final String MOTIFS_RESOURCE_BUNDLE_NAME = "com.castorama.commerce.order.MotifResources";

    /** MESSAGE_ID constant. */
    private static final String MESSAGE_ID = "messageId";

    /** ORDER_VIEW constant. */
    private static final String ORDER_VIEW = "orderView";

    /** VIEW constant. */
    private static final String VIEW = "view";

    /** REPLAY constant. */
    private static final String REPLAY = "replay";

    /** PRE_NEW constant. */
    private static final String PRE_NEW = "pre_new";

    /** NEW constant. */
    private static final String NEW = "new";

    /** SENT constant. */
    private static final String SENT = "sent";

    /** SENDER_CASTORAMA constant. */
    private static final String SENDER_CASTORAMA = "Castorama.fr";

    /** MSG_WRONG_NEW_MESSAGE constant. */
    private static final String MSG_WRONG_NEW_MESSAGE =
        "Les champs précédés d'un astérisque sont obligatoires, merci de les renseigner";

    /** MSG_WRONG_REPLAY_MESSAGE constant. */
    private static final String MSG_WRONG_REPLAY_MESSAGE = "Veuillez saisir votre réponse.";

    /** MOTIF constant. */
    private static final String MOTIF = "motif";

    /** MODE constant. */
    private static final String MODE = "mode";

    /** resourceBundle property */
    private ResourceBundle resourceBundle = null;

    /** motifsResourceBundle property */
    private ResourceBundle motifsResourceBundle = null;

    /** mOrderManager property */
    private CastOrderManager mOrderManager;

    /** profileTools property */
    private ProfileTools mProfileTools;

    /** openMessage property */
    private String mOpenMessage;

    /** orderId property */
    private String mOrderId;

    /** messageId property */
    private String mMessageId;

    /** messageSender property */
    private Short mMessageSender;

    /** canReplay property */
    private boolean mCanReplay;

    /** canResend property */
    private boolean mCanResend;

    /** messageDate property */
    private Date mMessageDate;

    /** message property */
    private RepositoryItem mMessage;

    /** messageMotif property */
    private Short mMessageMotif;

    /** messageMode property */
    private Short mMessageMode;

    /** messageOrder property */
    private RepositoryItem mMessageOrder;

    /** responseRequired property */
    private boolean mResponseRequired;

    /** messageText property */
    private String mMessageText;

    /** messageReplay property */
    private String mMessageReplay;

    /** successURL property */
    private String mSuccessURL;

    /** errorURL property */
    private String mErrorURL;

    /**
     * Creates a new CastWebOrdersFormHandler object.
     */
    public CastWebOrdersFormHandler() {
        resourceBundle =
            java.util.ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME,
                                               atg.service.dynamo.LangLicense.getLicensedDefault());

        motifsResourceBundle =
            java.util.ResourceBundle.getBundle(MOTIFS_RESOURCE_BUNDLE_NAME,
                                               atg.service.dynamo.LangLicense.getLicensedDefault());
    }

    /**
     * Handle open message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    public boolean handleOpenMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        final String openMessage = getOpenMessage();
        if (VIEW.equalsIgnoreCase(openMessage)) {
            return handleViewMessage(pRequest, pResponse);
        } else if (REPLAY.equalsIgnoreCase(openMessage)) {
            return replayMessage(pRequest, pResponse);
        } else if (PRE_NEW.equalsIgnoreCase(openMessage)) {
            return pre_newMessage(pRequest, pResponse);
        } else if (NEW.equalsIgnoreCase(openMessage)) {
            return handleNewMessage(pRequest, pResponse);
        } else {
            return closeMessage(pRequest, pResponse);
        }
    }

    /**
     * Handle view message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    private boolean handleViewMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                               throws ServletException, IOException {
        final String messageId = getMessageId();
        try {
            RepositoryItem messageItem =
                getOrderManager().getRepositoryBO().getItem(messageId, CastoConstantesOrders.BO_MESSAGE_VIEW_NAME);
            getOrderManager().changeMessageStatus(messageItem);
            setMessageText((String) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_TEXT));
            setMessageMode((Short) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MODE));
            setMessageDate((Date) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_DATE));
            setMessageSender((Short) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_SENDER));
            setMessageOrder(getOrderManager().getRepositoryBO().getItem(((Long) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_ORDER))
                                                                        .toString(),
                                                                        CastoConstantesOrders.BO_ORDER_VIEW_NAME));
            setMessageMotif((Short) getOrderManager().getContactFile(((Integer) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MOTIF))
                                                                     .toString()).getPropertyValue(CastoConstantesOrders.BO_MOTIF_PROPERTY_MOTIF));
            setResponseRequired((Boolean) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED));

            boolean canResend = false;
            if (new Short((short) 2).equals(getMessageSender())) {
                RepositoryItem contactFile =
                    getOrderManager().getContactFile(((Integer) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MOTIF))
                                                     .toString());
                if (null == contactFile.getPropertyValue(CastoConstantesOrders.BO_MOTIF_PROPERTY_DATE_CLOSED)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.add(Calendar.HOUR, -10 * 24);
                    if (calendar.before(getMessageDate())) {
                        canResend = true;
                    }
                }
            }
            setCanResend(canResend);

            boolean canReplay = false;
            if (new Short((short) 1).equals(getMessageSender())) {
                RepositoryItem contactFile =
                    getOrderManager().getContactFile(((Integer) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MOTIF))
                                                     .toString());
                if (null == contactFile.getPropertyValue(CastoConstantesOrders.BO_MOTIF_PROPERTY_DATE_CLOSED)) {
                    if (getOrderManager().checkForLastMessage(messageId, getMessageOrder())) {
                        canReplay = true;
                    }
                }
            }
            setCanReplay(canReplay);

            pRequest.addQueryParameter(ORDER_VIEW, VIEW);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch
        return checkFormRedirect(getCancelURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Show new message template logic.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    private boolean pre_newMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                            throws ServletException, IOException {
        pRequest.addQueryParameter(ORDER_VIEW, NEW);
        return checkFormRedirect(getCancelURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Replay on message logic.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    private boolean replayMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        clear();
        return checkFormRedirect(getCancelURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Handle new message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    public boolean handleNewMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
        Short messageMotif = getMessageMotif();
        String messageText = getMessageText();
        if ((null != messageMotif) && checkMessageText(messageText)) {
            CastOrderManager com = getOrderManager();
            com.newMessage(getOrderId(), messageMotif, messageText);
            if (null == com.getOrderBO(getOrderId())) {
                com.sendEmail(getProfileTools(), getUser(getOrderId()), getMotif(messageMotif), messageText);
            }
            pRequest.addQueryParameter(ORDER_VIEW, SENT);
            clear();
        } else {
            addFormException(new DropletException(MSG_WRONG_NEW_MESSAGE));
            pRequest.addQueryParameter(ORDER_VIEW, NEW);
        }
        return checkFormRedirect(getSuccessURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Handle copy message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    public boolean handleCopyMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        getOrderManager().copyMessage(getMessageId());
        clear();
        return checkFormRedirect(getSuccessURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Handle replay message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    public boolean handleReplay(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                         throws ServletException, IOException {
        String messageText = getMessageText();
        if (checkMessageText(messageText)) {
        	CastOrderManager com = getOrderManager();
        	com.replayMessage(getMessageId(), messageText);
            pRequest.addQueryParameter(ORDER_VIEW, SENT);
            clear();
        } else {
            addFormException(new DropletException(MSG_WRONG_REPLAY_MESSAGE));
            pRequest.addQueryParameter(MESSAGE_ID, getMessageId());
            pRequest.addQueryParameter(ORDER_VIEW, REPLAY);
        }
        return checkFormRedirect(getSuccessURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Handle view replay message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    public boolean handleViewReplay(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
        final String messageId = getMessageId();
        try {
            RepositoryItem messageItem =
                getOrderManager().getRepositoryBO().getItem(messageId, CastoConstantesOrders.BO_MESSAGE_VIEW_NAME);
            getOrderManager().changeMessageStatus(messageItem);
            setMessageText((String) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_TEXT));
            setMessageMode((Short) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MODE));
            setMessageDate((Date) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_DATE));
            setMessageSender((Short) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_SENDER));
            setMessageOrder(getOrderManager().getRepositoryBO().getItem(((Long) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_ORDER))
                                                                        .toString(),
                                                                        CastoConstantesOrders.BO_ORDER_VIEW_NAME));
            setMessageMotif((Short) getOrderManager().getContactFile(((Integer) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_MOTIF))
                                                                     .toString()).getPropertyValue(CastoConstantesOrders.BO_MOTIF_PROPERTY_MOTIF));
            setResponseRequired((Boolean) messageItem.getPropertyValue(CastoConstantesOrders.BO_MESSAGE_PROPERTY_REQUIRED));
            pRequest.addQueryParameter(ORDER_VIEW, REPLAY);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch
        return checkFormRedirect(getCancelURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Handle close message request.
     *
     * @param  pRequest  the dynamo http servlet request.
     * @param  pResponse the dynamo http servlet response.
     *
     * @return true when remains on same page, otherwise false.
     *
     * @throws ServletException when errors in logic operations.
     * @throws IOException      when errors in io operations.
     */
    private boolean closeMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                          throws ServletException, IOException {
        clear();
        return checkFormRedirect(getSuccessURL(), getCancelURL(), pRequest, pResponse);
    }

    /**
     * Clear message data.
     */
    private void clear() {
        setMessageText(null);
        setMessageMode(null);
        setMessageDate(null);
        setMessageSender(null);
        setMessageOrder(null);
        setMessageMotif(null);
        setResponseRequired(false);
    }

    /**
     * Check when message text correct.
     *
     * @param  pMessageText the message text.
     *
     * @return true when message text is correct, otherwise false.
     */
    private boolean checkMessageText(String pMessageText) {
        return ((null != pMessageText) && (0 < pMessageText.trim().length()) && (1000 > pMessageText.length()));
    }

    /**
     * Returns messageMotifLegend property.
     *
     * @return messageMotifLegend property.
     */
    public String getMessageMotifLegend() {
        String result = "";
        Short motif = getMessageMotif();
        if (null != motif) {
            try {
                result = resourceBundle.getString(MOTIF + "." + motif);
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
     * @return messageModeLegend property.
     */
    public String getMessageModeLegend() {
        String result = "";
        Short mode = getMessageMode();
        if (null != mode) {
            try {
                result = resourceBundle.getString(MODE + "." + mode);
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
     * @return messageFrom property.
     */
    public String getMessageFrom() {
        String result = "";
        Short sender = getMessageSender();
        if (null != sender) {
            if (1 == sender) {
                result = SENDER_CASTORAMA;
            } else {
                try {
                    result =
                        ProfileInfoDroplet.getProfileName((String) getOrderManager().getOrderFO(getMessageOrder())
                                                          .getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID),
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
     * @return messageTo property.
     */
    public String getMessageTo() {
        String result = "";
        Short sender = getMessageSender();
        if (null != sender) {
            if (2 == sender) {
                result = SENDER_CASTORAMA;
            } else {
                try {
                    result =
                        ProfileInfoDroplet.getProfileName((String) getOrderManager().getOrderFO(getMessageOrder())
                                                          .getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID),
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
     * Returns legendsForMotif property.
     *
     * @return legendsForMotif property.
     */
    public Map getLegendsForMotif() {
        Map result = new TreeMap();
        try {
            for (Enumeration keys = resourceBundle.getKeys(); keys.hasMoreElements();) {
                String key = (String) keys.nextElement();
                if ((null != key) && key.startsWith(MOTIF)) {
                    result.put(key, resourceBundle.getString(key));
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Returns legendsForMode property.
     *
     * @return legendsForMode property.
     */
    public Map getLegendsForMode() {
        Map result = new TreeMap();
        try {
            for (Enumeration keys = resourceBundle.getKeys(); keys.hasMoreElements();) {
                String key = (String) keys.nextElement();
                if ((null != key) && key.startsWith(MODE)) {
                    result.put(key, resourceBundle.getString(key));
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Returns user property.
     *
     * @param  orderId parameter to set.
     *
     * @return user property.
     */
    private RepositoryItem getUser(String orderId) {
        RepositoryItem result = null;
        try {
            String profileId =
                (String) getOrderManager().getOrderFO(orderId).getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID);
            ProfileTools profileTools = getProfileTools();
            result = profileTools.getProfileRepository().getItem(profileId, profileTools.getDefaultProfileType());
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return result;
    }

    /**
     * Returns motif property.
     *
     * @param  motif parameter to set.
     *
     * @return motif property.
     */
    public String getMotif(Short motif) {
        String result = "";
        if ( null != motif ) {
        	try {
        		result = motifsResourceBundle.getString(MOTIF + "." + motif);
        	} catch (Exception e) {
        		if (isLoggingError()) {
        			logError(e);
        		}
        	}
        }
        return result;
    }

    /**
     * Returns motifs property.
     *
     * @return motifs property.
     */
    public RepositoryItem[] getMotifs() {
        return getOrderManager().getQuestions();
    }

    /**
     * Returns messageId property.
     *
     * @return messageId property.
     */
    public String getMessageId() {
        return mMessageId;
    }

    /**
     * Sets the value of the messageId property.
     *
     * @param pMessageId parameter to set.
     */
    public void setMessageId(String pMessageId) {
        this.mMessageId = pMessageId;
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
        this.mOrderManager = pOrderManager;
    }

    /**
     * Returns messageSender property.
     *
     * @return messageSender property.
     */
    public Short getMessageSender() {
        return mMessageSender;
    }

    /**
     * Sets the value of the messageSender property.
     *
     * @param pMessageSender parameter to set.
     */
    public void setMessageSender(Short pMessageSender) {
        this.mMessageSender = pMessageSender;
    }

    /**
     * Returns messageDate property.
     *
     * @return messageDate property.
     */
    public Date getMessageDate() {
        return mMessageDate;
    }

    /**
     * Sets the value of the messageDate property.
     *
     * @param pMessageDate parameter to set.
     */
    public void setMessageDate(Date pMessageDate) {
        this.mMessageDate = pMessageDate;
    }

    /**
     * Returns messageMotif property.
     *
     * @return messageMotif property.
     */
    public Short getMessageMotif() {
        return mMessageMotif;
    }

    /**
     * Sets the value of the messageMotif property.
     *
     * @param pMessageMotif parameter to set.
     */
    public void setMessageMotif(Short pMessageMotif) {
        this.mMessageMotif = pMessageMotif;
    }

    /**
     * Returns messageMode property.
     *
     * @return messageMode property.
     */
    public Short getMessageMode() {
        return mMessageMode;
    }

    /**
     * Sets the value of the messageMode property.
     *
     * @param pMessageMode parameter to set.
     */
    public void setMessageMode(Short pMessageMode) {
        this.mMessageMode = pMessageMode;
    }

    /**
     * Returns messageOrder property.
     *
     * @return messageOrder property.
     */
    public RepositoryItem getMessageOrder() {
        return mMessageOrder;
    }

    /**
     * Sets the value of the messageOrder property.
     *
     * @param pMessageOrder parameter to set.
     */
    public void setMessageOrder(RepositoryItem pMessageOrder) {
        this.mMessageOrder = pMessageOrder;
    }

    /**
     * Returns messageText property.
     *
     * @return messageText property.
     */
    public String getMessageText() {
        return mMessageText;
    }

    /**
     * Sets the value of the messageText property.
     *
     * @param pMessageText parameter to set.
     */
    public void setMessageText(String pMessageText) {
        this.mMessageText = pMessageText;
    }

    /**
     * Returns openMessage property.
     *
     * @return openMessage property.
     */
    public String getOpenMessage() {
        return mOpenMessage;
    }

    /**
     * Sets the value of the openMessage property.
     *
     * @param pOpenMessage parameter to set.
     */
    public void setOpenMessage(String pOpenMessage) {
        this.mOpenMessage = pOpenMessage;
    }

    /**
     * Returns successURL property.
     *
     * @return successURL property.
     */
    public String getSuccessURL() {
        return mSuccessURL;
    }

    /**
     * Sets the value of the successURL property.
     *
     * @param pSuccessURL parameter to set.
     */
    public void setSuccessURL(String pSuccessURL) {
        this.mSuccessURL = pSuccessURL;
    }

    /**
     * Returns errorURL property.
     *
     * @return errorURL property.
     */
    public String getErrorURL() {
        return mErrorURL;
    }

    /**
     * Sets the value of the errorURL property.
     *
     * @param pErrorURL parameter to set.
     */
    public void setErrorURL(String pErrorURL) {
        this.mErrorURL = pErrorURL;
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

    /**
     * Returns responseRequired property.
     *
     * @return responseRequired property.
     */
    public boolean isResponseRequired() {
        return mResponseRequired;
    }

    /**
     * Sets the value of the responseRequired property.
     *
     * @param pResponseRequired parameter to set.
     */
    public void setResponseRequired(boolean pResponseRequired) {
        this.mResponseRequired = pResponseRequired;
    }

    /**
     * Returns messageReplay property.
     *
     * @return messageReplay property.
     */
    public String getMessageReplay() {
        return mMessageReplay;
    }

    /**
     * Sets the value of the messageReplay property.
     *
     * @param pMessageReplay parameter to set.
     */
    public void setMessageReplay(String pMessageReplay) {
        this.mMessageReplay = pMessageReplay;
    }

    /**
     * Returns orderId property.
     *
     * @return orderId property.
     */
    public String getOrderId() {
        return mOrderId;
    }

    /**
     * Sets the value of the orderId property.
     *
     * @param pOrderId parameter to set.
     */
    public void setOrderId(String pOrderId) {
        mOrderId = pOrderId;
    }

    /**
     * Returns message property.
     *
     * @return message property.
     */
    public RepositoryItem getMessage() {
        return mMessage;
    }

    /**
     * Sets the value of the message property.
     *
     * @param pMessage parameter to set.
     */
    public void setMessage(RepositoryItem pMessage) {
        this.mMessage = pMessage;
    }

    /**
     * Returns canResend property.
     *
     * @return canResend property.
     */
    public boolean isCanResend() {
        return mCanResend;
    }

    /**
     * Sets the value of the canResend property.
     *
     * @param pCanResend parameter to set.
     */
    public void setCanResend(boolean pCanResend) {
        this.mCanResend = pCanResend;
    }

    /**
     * Returns canReplay property.
     *
     * @return canReplay property.
     */
    public boolean isCanReplay() {
        return mCanReplay;
    }

    /**
     * Sets the value of the canReplay property.
     *
     * @param pCanReplay parameter to set.
     */
    public void setCanReplay(boolean pCanReplay) {
        this.mCanReplay = pCanReplay;
    }

}
