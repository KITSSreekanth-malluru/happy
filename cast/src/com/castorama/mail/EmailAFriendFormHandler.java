package com.castorama.mail;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.droplet.GenericFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.Profile;

import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.commerce.profile.Constants;

import com.castorama.utils.CommonHelper;
import com.castorama.utils.Validator;

/**
 *
 * @author EPAM team
 */
public class EmailAFriendFormHandler extends GenericFormHandler {
    /** Resource bundle name. */
    public static String RESOURCE_BUNDLE = "com.castorama.resources.CastWebAppResources";

    /** EMAIL_FROM constant. */
    private static final String EMAIL_FROM = "emailFrom";

    /** EMAIL_TO constant. */
    private static final String EMAIL_TO = "emailTo";

    /** MESSAGE constant. */
    private static final String MESSAGE = "message";

    /** Incorrect email from message code. */
    private static final String MSG_INCORRECT_EMAIL_FROM = "msg.email.a.friend.from.incorrect";

    /** Missed email from message code. */
    private static final String MSG_EMPTY_EMAIL_FROM = "msg.email.a.friend.from.empty";

    /** Incorrect email to message code. */
    private static final String MSG_INCORRECT_EMAIL_TO = "msg.email.a.friend.to.incorrect";

    /** Missed email to message code. */
    private static final String MSG_EMPTY_EMAIL_TO = "msg.email.a.friend.to.empty";

    /** emailFrom property. */
    private String mEmailFrom;

    /** emailTo property. */
    private String mEmailTo;

    /** message property. */
    private String mMessage;

    /** hasErrors property. */
    private String mHasErrors;

    /** sendErrorURL property. */
    private String mSendErrorURL;

    /** sendSuccessURL property. */
    private String mSendSuccessURL;

    /** productId property. */
    private String mProductId;

    /** mProfile property. */
    private Profile mProfile;

    /** mTemplateEmailSender property. */
    TemplateEmailSender mTemplateEmailSender = null;

    /** mTemplateEmailInfo property. */
    TemplateEmailInfoImpl mTemplateEmailInfo = null;

    /** Common helper component. */
    private CommonHelper commonHelper;

    /**
     * Sets the property TemplateEmailSender
     *
     * @param pTemplateEmailSender parameter
     */
    public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
        this.mTemplateEmailSender = pTemplateEmailSender;
    }

    /**
     * Returns templateEmailSender property.
     *
     * @return templateEmailSender property.
     */
    public TemplateEmailSender getTemplateEmailSender() {
        return mTemplateEmailSender;
    }

    /**
     * Sets the property TemplateEmailInfo
     *
     * @param pTemplateEmailInfo parameter
     */
    public void setTemplateEmailInfo(TemplateEmailInfoImpl pTemplateEmailInfo) {
        this.mTemplateEmailInfo = pTemplateEmailInfo;
    }

    /**
     * Returns templateEmailInfo property.
     *
     * @return templateEmailInfo property.
     */
    public TemplateEmailInfoImpl getTemplateEmailInfo() {
        return mTemplateEmailInfo;
    }

    /**
     * Returns emailFrom property.
     *
     * @return emailFrom property.
     */
    public String getEmailFrom() {
        if ((mEmailFrom == null) || (mEmailFrom.length() == 0)) {
            if ((mProfile != null) && !mProfile.isTransient()) {
                mEmailFrom = (String) mProfile.getPropertyValue(Constants.EMAIL_PROFILE_PROP);
            }
        }

        return mEmailFrom;
    }

    /**
     * Sets the value of the emailFrom property.
     *
     * @param pEmailFrom parameter to set.
     */
    public void setEmailFrom(String pEmailFrom) {
        mEmailFrom = pEmailFrom;
    }

    /**
     * Returns emailTo property.
     *
     * @return emailTo property.
     */
    public String getEmailTo() {
        return mEmailTo;
    }

    /**
     * Sets the value of the emailTo property.
     *
     * @param pEmailTo parameter to set.
     */
    public void setEmailTo(String pEmailTo) {
        mEmailTo = pEmailTo;
    }

    /**
     * Returns message property.
     *
     * @return message property.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Sets the value of the message property.
     *
     * @param pMessage parameter to set.
     */
    public void setMessage(String pMessage) {
        mMessage = pMessage;
    }

    /**
     *
     * Send email.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @return
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public boolean handleSend(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                       throws ServletException, IOException {
        boolean result = true;
        boolean isValidSenderEmailFormat = false;
        boolean isValidRecipientEmailFormat = false;

        if ((mEmailTo == null) || (mEmailTo.length() == 0)) {
            getCommonHelper().generateFormException(MSG_EMPTY_EMAIL_TO, this, RESOURCE_BUNDLE);
        } else {
            isValidRecipientEmailFormat = Validator.validateEmail(getEmailTo());

            if (!isValidRecipientEmailFormat) {
                getCommonHelper().generateFormException(MSG_INCORRECT_EMAIL_TO, this, RESOURCE_BUNDLE);
            }
        }
        if ((mEmailFrom == null) || (mEmailFrom.length() == 0)) {
            getCommonHelper().generateFormException(MSG_EMPTY_EMAIL_FROM, this, RESOURCE_BUNDLE);
        } else {
            isValidSenderEmailFormat = Validator.validateEmail(getEmailFrom());

            if (!isValidSenderEmailFormat) {
                getCommonHelper().generateFormException(MSG_INCORRECT_EMAIL_FROM, this, RESOURCE_BUNDLE);
            }
        }
        if (isValidSenderEmailFormat && isValidRecipientEmailFormat) {
            TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getTemplateEmailInfo();

            emailInfo.setMessageFrom(getEmailFrom());
            emailInfo.setMessageTo(getEmailTo());

            List recipents = new ArrayList();
            recipents.add(emailInfo.getMessageTo());
            Map params = new HashMap();
            params.put("to", getEmailTo());
            params.put("from", getEmailFrom());

            params.put("message", getMessage());
            params.put("productId", getProductId());

            emailInfo.setTemplateParameters(params);

            try {
                getTemplateEmailSender().sendEmailMessage(emailInfo, recipents, true, false);
            } catch (TemplateEmailException e) {
                logError(e);
            }

            setHasErrors("false");
            result = checkFormRedirect(getSendSuccessURL(), getSendSuccessURL(), pRequest, pResponse);
        } else {
            setHasErrors("true");
            result = checkFormRedirect(getSendErrorURL(), getSendErrorURL(), pRequest, pResponse);
        }  // end if-else
        return result;
    }

    /**
     * Returns sendErrorURL property.
     *
     * @return sendErrorURL property.
     */
    public String getSendErrorURL() {
        return mSendErrorURL;
    }

    /**
     * Sets the value of the sendErrorURL property.
     *
     * @param pSendErrorURL parameter to set.
     */
    public void setSendErrorURL(String pSendErrorURL) {
        mSendErrorURL = pSendErrorURL;
    }

    /**
     * Returns sendSuccessURL property.
     *
     * @return sendSuccessURL property.
     */
    public String getSendSuccessURL() {
        return mSendSuccessURL;
    }

    /**
     * Sets the value of the sendSuccessURL property.
     *
     * @param pSendSuccessURL parameter to set.
     */
    public void setSendSuccessURL(String pSendSuccessURL) {
        mSendSuccessURL = pSendSuccessURL;
    }

    /**
     * Returns hasErrors property.
     *
     * @return hasErrors property.
     */
    public String getHasErrors() {
        return mHasErrors;
    }

    /**
     * Sets the value of the hasErrors property.
     *
     * @param pHasErrors parameter to set.
     */
    public void setHasErrors(String pHasErrors) {
        mHasErrors = pHasErrors;
    }

    /**
     * Returns productId property.
     *
     * @return productId property.
     */
    public String getProductId() {
        return mProductId;
    }

    /**
     * Sets the value of the productId property.
     *
     * @param pProductId parameter to set.
     */
    public void setProductId(String pProductId) {
        mProductId = pProductId;
    }

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param profile parameter to set.
     */
    public void setProfile(Profile profile) {
        mProfile = profile;
    }

    /**
     * Gets Common Helper instance.
     *
     * @return the commonHelper
     */
    public CommonHelper getCommonHelper() {
        return commonHelper;
    }

    /**
     * Sets Common Helper instance.
     *
     * @param commonHelper the commonHelper to set
     */
    public void setCommonHelper(CommonHelper commonHelper) {
        this.commonHelper = commonHelper;
    }

}
