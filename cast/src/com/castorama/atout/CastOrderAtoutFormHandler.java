package com.castorama.atout;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_CIVILITE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_DAYS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_MAGASIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_MOMENT;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_NOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_PRENOM;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_EMPTY_TELEPHONE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_EMAIL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.castorama.constantes.CastoConstantes;
import com.castorama.contact.BaseContactUsFormHandler;
import com.castorama.utils.Validator;

/**
 * Send email to the user mentioned in the component's properties file(to the
 * CASTORAMA) in the case user successful fulfilled "Demander carte castorama"
 * form
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastOrderAtoutFormHandler extends BaseContactUsFormHandler {

    /** type property */
    private String mType;

    /** motif property */
    private String mMotif;

    /** magasin property */
    private String mMagasin;

    /** lundi property */
    private Boolean mLundi;

    /** mardi property */
    private Boolean mMardi;

    /** mercredi property */
    private Boolean mMercredi;

    /** jeudi property */
    private Boolean mJeudi;

    /** vendredi property */
    private Boolean mVendredi;

    /** matin property */
    private Boolean mMatin;

    /** apresMidi property */
    private Boolean mApresMidi;

    /**
     * Returns type property.
     *
     * @return type property.
     */
    public String getType() {
        return mType;
    }

    /**
     * Sets the value of the type property.
     *
     * @param pType parameter to set.
     */
    public void setType(String pType) {
        mType = pType;
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
     * Returns magasin property.
     *
     * @return magasin property.
     */
    public String getMagasin() {
        return mMagasin;
    }

    /**
     * Sets the value of the magasin property.
     *
     * @param pMagasin parameter to set.
     */
    public void setMagasin(String pMagasin) {
        mMagasin = pMagasin;
    }

    /**
     * Returns lundi property.
     *
     * @return lundi property.
     */
    public Boolean getLundi() {
        return mLundi;
    }

    /**
     * Sets the value of the lundi property.
     *
     * @param pLundi parameter to set.
     */
    public void setLundi(Boolean pLundi) {
        mLundi = pLundi;
    }

    /**
     * Returns mardi property.
     *
     * @return mardi property.
     */
    public Boolean getMardi() {
        return mMardi;
    }

    /**
     * Sets the value of the mardi property.
     *
     * @param pMardi parameter to set.
     */
    public void setMardi(Boolean pMardi) {
        mMardi = pMardi;
    }

    /**
     * Returns mercredi property.
     *
     * @return mercredi property.
     */
    public Boolean getMercredi() {
        return mMercredi;
    }

    /**
     * Sets the value of the mercredi property.
     *
     * @param pMercredi parameter to set.
     */
    public void setMercredi(Boolean pMercredi) {
        mMercredi = pMercredi;
    }

    /**
     * Returns jeudi property.
     *
     * @return jeudi property.
     */
    public Boolean getJeudi() {
        return mJeudi;
    }

    /**
     * Sets the value of the jeudi property.
     *
     * @param pJeudi parameter to set.
     */
    public void setJeudi(Boolean pJeudi) {
        mJeudi = pJeudi;
    }

    /**
     * Returns vendredi property.
     *
     * @return vendredi property.
     */
    public Boolean getVendredi() {
        return mVendredi;
    }

    /**
     * Sets the value of the vendredi property.
     *
     * @param pVendredi parameter to set.
     */
    public void setVendredi(Boolean pVendredi) {
        mVendredi = pVendredi;
    }

    /**
     * Returns matin property.
     *
     * @return matin property.
     */
    public Boolean getMatin() {
        return mMatin;
    }

    /**
     * Sets the value of the matin property.
     *
     * @param pMatin parameter to set.
     */
    public void setMatin(Boolean pMatin) {
        mMatin = pMatin;
    }

    /**
     * Returns apresMidi property.
     *
     * @return apresMidi property.
     */
    public Boolean getApresMidi() {
        return mApresMidi;
    }

    /**
     * Sets the value of the apresMidi property.
     *
     * @param pApresMidi parameter to set.
     */
    public void setApresMidi(Boolean pApresMidi) {
        mApresMidi = pApresMidi;
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
        return false;

    }

    /**
     * Send email to the user
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
        String firstName = getPrenom();
        String fromName = getNom();

        if (!StringUtils.isEmpty(fromEmail)) {
            String qualifiedFrom = firstName + " " + fromName + "<" + fromEmail + ">";
            emailInfo.setMessageFrom(qualifiedFrom.trim());
        }

        HashMap params = new HashMap();

        params.put(CastoConstantes.NOM, fromName);
        params.put(CastoConstantes.PRENOM, firstName);
        params.put(CastoConstantes.CIVILITE, getCivilite());
        params.put(CastoConstantes.EMAIL, getEmail());

        params.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        params.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        params.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        params.put(LOCALITY_ADDRESS_PROP, getLocality());
        params.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        params.put(CITY_ADDRESS_PROP, getCity());
        params.put(COUNTRY_ADDRESS_PROP, getCountry());
        params.put(CastoConstantes.TELEPHON, getPhone());
        
        params.put(CastoConstantes.MAGASIN, getMagasin());

        params.put(CastoConstantes.LUNDI, getLundi());
        params.put(CastoConstantes.MARDI, getMardi());
        params.put(CastoConstantes.MERCREDI, getMercredi());
        params.put(CastoConstantes.JEUDI, getJeudi());
        params.put(CastoConstantes.VENDREDI, getVendredi());

        params.put(CastoConstantes.MATIN, getMatin());
        params.put(CastoConstantes.APRESMIDI, getApresMidi());

        emailInfo.setTemplateParameters(params);

        if (isLoggingDebug()) {
            java.util.Set entries = params.entrySet();
            java.util.Iterator iter = entries.iterator();

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                logDebug("Key: [" + key + "] - Value: " + entry.getValue() + "\n");
            }
        }

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
    protected boolean validateUserData(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
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
        
        
        HashMap addressValues = (HashMap) convertPrimaryAddress();
        getAddressHelper().validatePhone(addressValues, this);
        
        if (StringUtils.isBlank(getPhone())) {
            String msg = formatMessage(MSG_EMPTY_TELEPHONE, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_TELEPHONE));
        }

        if (((getLundi() == null) || !getLundi()) && ((getMardi() == null) || !getMardi()) &&
                ((getMercredi() == null) || !getMercredi()) && ((getJeudi() == null) || !getJeudi()) &&
                ((getVendredi() == null) || !getVendredi())) {
            String msg = formatMessage(MSG_EMPTY_DAYS, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_DAYS));
        }
        if (((getMatin() == null) || !getMatin()) && ((getApresMidi() == null) || !getApresMidi())) {
            String msg = formatMessage(MSG_EMPTY_MOMENT, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_MOMENT));
        }

        if (StringUtils.isBlank(getMagasin())) {
            String msg = formatMessage(MSG_EMPTY_MAGASIN, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_EMPTY_MAGASIN));
        }
        if (!StringUtils.isBlank(getEmail()) && !Validator.validateEmail(getEmail())) {
            String msg = formatMessage(MSG_INVALID_EMAIL, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_INVALID_EMAIL));
        }

        return false;

    }

    /**
     * clears parameters after submission from "current form"
     */
    protected void clearAfterSubmission() {
        super.clearAfterSubmission();
        setApresMidi(null);
        setLundi(false);
        setMardi(false);
        setJeudi(false);
        setMercredi(false);
        setVendredi(false);
        setMagasin(null);
        setApresMidi(false);
        setMatin(false);
    }

}
