package com.castorama.scenario;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import atg.dms.patchbay.MessageSource;
import atg.dms.patchbay.MessageSourceContext;

import atg.nucleus.GenericService;

import atg.repository.RepositoryItem;

/**
 *
 * @author Andrei_Raichonak
 */
public class InviteMessageSource extends GenericService implements MessageSource {
    /** PORT_NAME constant. */
    public static final String PORT_NAME = "Invite";

    /** MESSAGE_TYPE constant. */
    public static final String MESSAGE_TYPE = "com.castorama.scenario.Invite";

    /** messageSourceContext property. */
    private MessageSourceContext mMessageSourceContext;

    /** sendingMessages property. */
    private boolean mSendingMessages = false;

    /**
     * Returns messageSourceContext property.
     *
     * @return messageSourceContext property.
     */
    public MessageSourceContext getMessageSourceContext() {
        return mMessageSourceContext;
    }

    /**
     * @see atg.dms.patchbay.MessageSource#setMessageSourceContext(atg.dms.patchbay.MessageSourceContext)
     */
    public void setMessageSourceContext(MessageSourceContext pContext) {
        mMessageSourceContext = pContext;
    }

    /**
     * @see atg.dms.patchbay.MessageSource#startMessageSource()
     */
    public void startMessageSource() {
        mSendingMessages = true;
    }

    /**
     * @see atg.dms.patchbay.MessageSource#stopMessageSource()
     */
    public void stopMessageSource() {
        mSendingMessages = false;
    }

    /**
     * Sets the value of the inviteInfo property.
     *
     * @param pReferrer  parameter to set.
     * @param pReferee   parameter to set.
     * @param pFirstName parameter to set.
     * @param pLastName  parameter to set.
     */
    public void setInviteInfo(RepositoryItem pReferrer, String pReferee, String pFirstName, String pLastName) {
        fireInviteMessage(pReferrer, pReferee, pFirstName, pLastName);
    }

    /**
     * Fires a InviteMessage
     *
     * @param pReferrer  parameter
     * @param pReferee   parameter
     * @param pFirstName parameter
     * @param pLastName  parameter
     */
    public void fireInviteMessage(RepositoryItem pReferrer, String pReferee, String pFirstName, String pLastName) {
        if (mSendingMessages) {
            try {
                ObjectMessage message = mMessageSourceContext.createObjectMessage(PORT_NAME);
                message.setJMSType(MESSAGE_TYPE);
                message.setObject(new InviteMessage(pReferrer, pReferee, pFirstName, pLastName));
                mMessageSourceContext.sendMessage(PORT_NAME, message);
            } catch (JMSException jmse) {
                if (isLoggingError()) {
                    logError(jmse);
                }
            }
        } else {
            if (isLoggingDebug()) {
                logDebug("Attempt to send a message");
            }
        }
    }  // end method fireInviteMessage

}
