package com.castorama.atout;
import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_ADRESSE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CIVILITE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CODEPOSTAL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CONTRACT_NUMBER;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_MESSAGE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_MOTIF;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_NOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_PRENOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_TELEPHONE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_VILLE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_CODEPOSTAL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_EMAIL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.repository.RepositoryException;
import atg.service.dynamo.LangLicense;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.constantes.CastoConstantes;
import com.castorama.contact.BaseContactUsFormHandler;
import com.castorama.utils.Validator;

/**
 * Send email to the user mentioned in the component's properties file(to the
 * CASTORAMA) in the case user successful fulfilled "Contact carte castorama" form
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastContactAtoutFormHandler extends BaseContactUsFormHandler {
	
    /** profileId parameter */
    private String mProfileId;

    /** message property */
    private String mMessage;

    /** contractNumber property */
    private String mContractNumber;

    /** motif parameter */
    private String mMotif;

    /** defaultEmailInfo property */
    private TemplateEmailInfo mDefaultEmailInfo;

    /** templateUrl property */
    private String mTemplateUrl = null;

    /** successUrl property */
    private String mMotifValue;

    /**
     * Returns profileId property.
     *
     * @return profileId property.
     */
    public String getProfileId() {
        return mProfileId;
    }

    /**
     * Sets the value of the profileId property.
     *
     * @param pProfileId parameter to set.
     */
    public void setProfileId(String pProfileId) {
        mProfileId = pProfileId;
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
     * Returns contractNumber property.
     *
     * @return contractNumber property.
     */
    public String getContractNumber() {
        return mContractNumber;
    }

    /**
     * Sets the value of the contractNumber property.
     *
     * @param pContractNumber parameter to set.
     */
    public void setContractNumber(String pContractNumber) {
        mContractNumber = pContractNumber;
    }

    /**
     * Returns motif property.
     *
     * @return motif property.
     */
    public String getMotif() {
        return mMotif;
    }

    /**
     * Sets the value of the motif property.
     *
     * @param pMotif parameter to set.
     */
    public void setMotif(String pMotif) {
        mMotif = pMotif;
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
        mDefaultEmailInfo = pDefaultEmailInfo;
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
        mTemplateUrl = pTemplateUrl;
    }

    /**
     * Sends email to the user mentioned in the properties file if user's passed
     * data are correct
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @return true if email has been sent
     *
     * @throws Exception - exception
     */
    public boolean handleSendEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                            throws Exception {
        validateUserData(pRequest, pResponse);
        if (!checkFormRedirect(null, getErrorUrl(), pRequest, pResponse)) {
            return false;
        }

        sendEmail(pRequest, pResponse);

        checkFormRedirect(getSuccessUrl(), getErrorUrl(), pRequest, pResponse);
        clearAfterSubmission();
        return true;

    }

    /**
     * Send email to the user which determined in peroperties file
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws TemplateEmailException - exception
     * @throws RepositoryException    - exception
     */
    protected void sendEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                      throws TemplateEmailException, RepositoryException {
        TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();

        emailInfo.setTemplateURL(getTemplateUrl());
        String fromEmail = getEmail();
        String fromName = getNom();

        if (!StringUtils.isEmpty(fromEmail)) {
            String qualifiedFrom = getPrenom() + " " + fromName + "<" + fromEmail + ">";
            emailInfo.setMessageFrom(qualifiedFrom.trim());
        }
        String subject = emailInfo.getMessageSubject() + " - " + getMotifValue();
        emailInfo.setMessageSubject(subject);

        HashMap params = new HashMap();
        params.put(CastoConstantes.CIVILITE, getCivilite());
        params.put(CastoConstantes.NOM, fromName);
        params.put(CastoConstantes.PRENOM, getPrenom());

        params.put(CastoConstantes.EMAIL, fromEmail);
        params.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        params.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        params.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        params.put(LOCALITY_ADDRESS_PROP, getLocality());
        params.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        params.put(CITY_ADDRESS_PROP, getCity());
        params.put(COUNTRY_ADDRESS_PROP, getCountry());
        params.put(CastoConstantes.TELEPHONE, getPhone());
        params.put(CastoConstantes.MESSAGE, getMessage());
        params.put(CastoConstantes.MOTIF, getMotif());
        params.put(CastoConstantes.MOTIF_TITLE, getMotifValue());
        params.put(CastoConstantes.CONTRACTNUMBER, getContractNumber());

        emailInfo.setTemplateParameters(params);

        List recipents = new ArrayList();
        recipents.add(emailInfo.getMessageTo());
        getEmailSender().sendEmailMessage(emailInfo, recipents, isSendEmailInSeparateThread(), isPersistEmails());
    }

    /**
     * Validates user data
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @return true if all data are correct, false if submitted form contains
     *         incorrect data
     */
    protected boolean validateUserData(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException{
        if (StringUtils.isBlank(getCivilite())) {
            String msg = formatMessage(MSG_EMPTY_CIVILITE, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_CIVILITE));
        }

        if (StringUtils.isBlank(getNom())) {
            String msg = formatMessage(MSG_EMPTY_NOM, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_NOM));
        }
        if (StringUtils.isBlank(getPrenom())) {
            String msg = formatMessage(MSG_EMPTY_PRENOM, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_PRENOM));
        }

        if (StringUtils.isBlank(getEmail())) {
            String msg = formatMessage(MSG_EMPTY_EMAIL, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_EMAIL));
        }

        if (StringUtils.isBlank(getContractNumber())) {
            String msg = formatMessage(MSG_EMPTY_CONTRACT_NUMBER, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_CONTRACT_NUMBER));
        }

        validateAddress(convertPrimaryAddress(), this);
        
        if (StringUtils.isBlank(getPhone())) {
            String msg = formatMessage(MSG_EMPTY_TELEPHONE, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_TELEPHONE));
        }

        if (StringUtils.isBlank(getMotif())) {
            String msg = formatMessage(MSG_EMPTY_MOTIF, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_MOTIF));
        }

        if (StringUtils.isBlank(getMessage())) {
            String msg = formatMessage(MSG_EMPTY_MESSAGE, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_MESSAGE));
        }

        if (!StringUtils.isBlank(getEmail()) && !com.castorama.utils.Validator.validateEmail(getEmail())) {
            String msg = formatMessage(MSG_INVALID_EMAIL, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_INVALID_EMAIL));
        }

        return false;

    }
    
    public String getMotifValue() {
        return mMotifValue;
    }

    public void setMotifValue(String motifValue) {
        mMotifValue = motifValue;
    }
    
    /**
     * clears parameters after submission from "current form"
     */
    public void clearAfterSubmission() {
        super.clearAfterSubmission();
        setMessage(null);
        setMotif(null);
        setContractNumber(null);
    }
}
