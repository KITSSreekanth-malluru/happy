package com.castorama.contact;

import java.util.HashMap;
import java.util.Map;

import atg.userprofiling.email.TemplateEmailInfoImpl;

/**
 * CastTemplateEmailInfoImpl class extends TemplateEmailInfoImpl.
 *
 * @author EPAM team
 */
public class CastTemplateEmailInfoImpl extends TemplateEmailInfoImpl {

    /** mMessageToCastodirect property */
    String mMessageToCastodirect;

    /**
     * Returns messageToCastodirect property.
     *
     * @return messageToCastodirect property.
     */
    public String getMessageToCastodirect() {
        return mMessageToCastodirect;
    }

    /**
     * Sets the value of the messageToCastodirect property.
     *
     * @param pMessageToCastodirect parameter to set.
     */
    public void setMessageToCastodirect(String pMessageToCastodirect) {
        mMessageToCastodirect = pMessageToCastodirect;
    }

    /**
     * Copy known properties to pCopy.
     *
     * @param pCopy the info to which to copy properties.
     */
    public void copyPropertiesTo(CastTemplateEmailInfoImpl pCopy) {
        super.copyPropertiesTo(pCopy);
        CastTemplateEmailInfoImpl copy = pCopy;
        copy.setMailingName(getMailingName());
        copy.setMessageReplyTo(getMessageReplyTo());
        copy.setMessageTo(getMessageTo());
        copy.setMessageToCastodirect(getMessageToCastodirect());
        copy.setMessageFrom(getMessageFrom());
        copy.setMessageCc(getMessageCc());
        copy.setMessageBcc(getMessageBcc());
        copy.setMessageSubject(getMessageSubject());
        copy.setContentProcessor(getContentProcessor());
        copy.setProfileId(getProfileId());

        if (getTrackingData() != null) {
            Map tmp = new HashMap();
            tmp.putAll(getTrackingData());
            copy.setTrackingData(tmp);
        }

        if (getTransitoryInfo() != null) {
            copy.setTransitoryInfo(getTransitoryInfo().copy());
        }
    }

    /**
     * Returns a copy of this TemplateEmailInfo.
     *
     * @return ToDo: DOCUMENT ME!
     */
    public CastTemplateEmailInfoImpl copy() {
        CastTemplateEmailInfoImpl info = new CastTemplateEmailInfoImpl();
        copyPropertiesTo(info);
        return info;
    }

}
