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
public class SearchMessageSource extends GenericService implements MessageSource {
    /** PORT_NAME constant. */
    public static final String PORT_NAME = "CastoSearch";

    /** MESSAGE_TYPE constant. */
    public static final String MESSAGE_TYPE = "com.castorama.scenario.CastoSearch";

    /** messageSourceContext property. */
    private MessageSourceContext mMessageSourceContext;
    
    /** queryMaxLength property. */
    private int mQueryMaxLength;

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
     * @param pQueryMaxLength the queryMaxLength to set
     */
    public void setQueryMaxLength(int pQueryMaxLength) {
        this.mQueryMaxLength = pQueryMaxLength;
    }


    /**
     * Returns int value of the mQueryMaxLength property.
     *  
     * @return int value of the mQueryMaxLength property.
     */
    public int getQueryMaxLength(){
        return mQueryMaxLength;
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
     * Sets the value of the searchInfo property.
     *
     * @param pProfile parameter to set.
     * @param pQuery   parameter to set.
     */
    public void setSearchInfo(RepositoryItem pProfile, String pQuery) {
        fireSearchMessage(pProfile, pQuery);
    }

    /**
     *
     * @param pProfile parameter
     * @param pQuery   parameter
     */
    public void fireSearchMessage(RepositoryItem pProfile, String pQuery) {
        if (mSendingMessages) {
            try {
                ObjectMessage message = mMessageSourceContext.createObjectMessage(PORT_NAME);
                message.setJMSType(MESSAGE_TYPE);
                message.setObject(new SearchMessage(pProfile, pQuery));
                mMessageSourceContext.sendMessage(PORT_NAME, message);

                if (isLoggingDebug()) {
                    logDebug("Send a message: " + pQuery);
                }
            } catch (JMSException jmse) {
                if (isLoggingError()) {
                    logError(jmse);
                }
            }
        } else {
            if (isLoggingDebug()) {
                logDebug("Attempt to send a message");
            }
        }  // end if-else
    }  // end method fireSearchMessage
}
