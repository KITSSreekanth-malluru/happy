package com.castorama.contact;

import static com.castorama.commerce.profile.Constants.*;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_CITY;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_POSTAL_CODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.GenericFormHandler;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.commerce.profile.CastAddressHelper;
import com.castorama.utils.Validator;

/**
 * ContactUsFormHandler class contains logic for handling contactUs.jsp page.
 *
 * @author EPAM team
 */
public class BaseContactUsFormHandler extends GenericFormHandler {
    
    /** France country code. */
    public static final String FRANCE = "France";
    
    /** civilite property */
    private String mCivilite;

    /** nom property */
    private String mNom;

    /** prenom property */
    private String mPrenom;

    /** email property */
    private String mEmail;

    /** address1 property */
    private String mAddress1;

    /** address2 property */
    private String mAddress2;

    /** address3 property */
    private String mAddress3;

    /** locality property */
    private String mLocality;

    /** postalCode property */
    private String mPostalCode;

    /** city property */
    private String mCity;

    /** country property */
    private String mCountry;
    
    /** country code property */
    private String mState;

    /** phone property */
    private String mPhone;
    
    /** profile property */
    private Profile mProfile;
    
    /** Address Helper. */
    private CastAddressHelper mAddressHelper;
    
    /** successUrl property */
    private String mSuccessUrl;

    /** errorUrl property */
    private String mErrorUrl;

    /** resourceBundleName property */
    private String mResourceBundleName;

    /** resourceBundle property */
    private ResourceBundle mResourceBundle;
    
    /** validator property */
    private Validator mValidator;

    /** defaultEmailInfo property */
    private TemplateEmailInfo mDefaultEmailInfo;

    /** emailSender property */
    private TemplateEmailSender mEmailSender = null;
    
    /** templateUrl property */
    private String mTemplateUrl = null;

    /** sendEmailInSeparateThread property */
    private boolean mSendEmailInSeparateThread = false;

    /** persistEmails property */
    private boolean mPersistEmails = false;

    /**
     * Creates a new ContactUsFormHandler object.
     */
    public BaseContactUsFormHandler() {
    }
    /**
     * Clears the form after successful submit
     */
    protected void clearAfterSubmission() {
        setCivilite(null);
        setNom(null);
        setPrenom(null);
        setEmail(null);
        setAddress1(null);
        setAddress2(null);
        setAddress3(null);
        setLocality(null);
        setCountry(null);
        setPostalCode(null);
        setCity(null);
        setPhone(null);
    }

    /**
     * Returns profileId property.
     *
     * @return profileId property.
     */
    public String getProfileId() {
        Profile profile = getProfile();
        if (!profile.isTransient()) {
            return profile.getRepositoryId();
        } else {
            return "";
        }
    }

    /**
     * Returns propertiesList property.
     *
     * @param  pKey parameter to set.
     *
     * @return propertiesList property.
     */
    public List<String> getPropertiesList(String pKey) {
        List<String> results = new ArrayList<String>();
        ResourceBundle messageBundle = ResourceBundle.getBundle(getResourceBundleName());
        String message = messageBundle.getString(pKey);
        StringTokenizer tok = new StringTokenizer(message, ",");
        while (tok.hasMoreElements()) {
            results.add((String) tok.nextElement());
        }
        return results;
    }

    /**
     * Create map with address properties.
     *
     * @return map with address properties
     */
    public Map<String, String> convertPrimaryAddress() {
        Map<String, String> address = new HashMap<String, String>();

        address.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        address.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        address.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        address.put(LOCALITY_ADDRESS_PROP, getLocality());
        address.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        address.put(CITY_ADDRESS_PROP, getCity());

        String country = getCountry();
        if ((country == null) || ((country != null) && (country.length() == 0))) {
            country = DEFAULT_COUNTRY_NAME;
            setCountry(country);
            address.put(COUNTRY_ADDRESS_PROP, getCountry());
        }

        address.put(COUNTRY_ADDRESS_PROP, country);
        address.put(PHONE_NUMBER_ADDRESS_PROP, getPhone());
        return address;
    }

    /**
     * Gets error flag for code postal.
     *
     * @return the mFlagErrorCp
     */
    public boolean isFlagErrorCp() {
        return getAddressHelper().isFlagErrorCp();
    }

    /**
     * Sets Error flag for code postal.
     *
     * @param flagErrorCp the mFlagErrorCp to set
     */
    public void setFlagErrorCp(boolean flagErrorCp) {
        getAddressHelper().setFlagErrorCp(flagErrorCp);
    }


    public boolean validateAddress(final Map<String, String> values, final GenericFormHandler formHandler)
                            throws ServletException {
        boolean result = true;
        if (values == null) {
            result = false;
        } else {
            if (StringUtils.isBlank(values.get(ADDRESS_1_ADDRESS_PROP))) {
                getAddressHelper().getCommonHelper().generateFormException("msgEmptyStreet1", formHandler, getResourceBundleName());
                result = false;
            }
            String city = values.get(CITY_ADDRESS_PROP);
            if (StringUtils.isBlank(city)) {
                getAddressHelper().getCommonHelper().generateFormException("msgEmptyVille", formHandler, getResourceBundleName());
                result = false;
            } else {
                city = city.trim();
            }
            String pc = values.get(POSTAL_CODE_ADDRESS_PROP);
            if (StringUtils.isBlank(pc)) {
                getAddressHelper().getCommonHelper().generateFormException("msgEmptyCodepostal", formHandler, getResourceBundleName());
                result = false;
            } else {
                pc = pc.trim();
                if (FRANCE.equalsIgnoreCase(values.get(COUNTRY_ADDRESS_PROP))) {
                    if (!getAddressHelper().existFrancePostalCode(pc)) {
                        getAddressHelper().getCommonHelper().generateFormException(MSG_INCORRECT_POSTAL_CODE, formHandler, getResourceBundleName());
                        result = false;
                    } else if (!getAddressHelper().verifyCityViaCp(pc, city)) {
                        setFlagErrorCp(true);
                        getAddressHelper().getCommonHelper().generateFormException(MSG_INCORRECT_CITY, formHandler, getResourceBundleName());
                        result = false;
                    } else {
                        setFlagErrorCp(false);
                    }
                }
            }  // end if-else

        }  // end if-else
        return result;
    }

    /**
     * Format error message - get message from resource bundle na dreturn it.
     *
     * @param  pMsg      - message key
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @return formatted error message for passed error message key
     */
    public String formatMessage(String pMsg, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        return ResourceUtils.getMsgResource(pMsg, getResourceBundleName(), getResourceBundle());
    }

    /**
     * Validates the given String.
     *
     * @param  pValue - value to validate
     * @param  pRegex - validatnf regexp
     *
     * @return <code>true</code> if validation is successful
     */
    public boolean validate(String pValue, String pRegex) {
        boolean result = true;
        if (pRegex != null) {
            final Pattern pattern = Pattern.compile(pRegex);
            final Matcher matcher = pattern.matcher(pValue);
            if (!matcher.matches()) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Returns phone property.
     *
     * @return phone property.
     */
    public String getPhone() {
        if ((mPhone == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mPhone = getProfileAddressPropertyValue(PHONE_NUMBER_ADDRESS_PROP);
        }
        return mPhone;
    }

    /**
     * Sets the value of the phone property.
     *
     * @param pPhone parameter to set.
     */
    public void setPhone(String pPhone) {
        this.mPhone = pPhone;
    }

    /**
     * Returns city property.
     *
     * @return city property.
     */
    public String getCity() {
        if ((mCity == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mCity = getProfileAddressPropertyValue(CITY_ADDRESS_PROP);
        }
        return mCity;
    }

    /**
     * Sets the value of the city property.
     *
     * @param pCity parameter to set.
     */
    public void setCity(String pCity) {
        this.mCity = pCity;
    }

    /**
     * Returns postalCode property.
     *
     * @return postalCode property.
     */
    public String getPostalCode() {
        if ((mPostalCode == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mPostalCode = getProfileAddressPropertyValue(POSTAL_CODE_ADDRESS_PROP);
        }
        return mPostalCode;
    }

    /**
     * Sets the value of the postalCode property.
     *
     * @param pPostalCode parameter to set.
     */
    public void setPostalCode(String pPostalCode) {
        this.mPostalCode = pPostalCode;
    }

    /**
     * Returns address1 property.
     *
     * @return address1 property.
     */
    public String getAddress1() {
        if ((mAddress1 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mAddress1 = getProfileAddressPropertyValue(ADDRESS_1_ADDRESS_PROP);
        }
        return mAddress1;
    }

    /**
     * Sets the value of the address1 property.
     *
     * @param pAddress1 parameter to set.
     */
    public void setAddress1(String pAddress1) {
        this.mAddress1 = pAddress1;
    }

    /**
     * Returns address2 property.
     *
     * @return address2 property.
     */
    public String getAddress2() {
        if ((mAddress2 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mAddress2 = getProfileAddressPropertyValue(ADDRESS_2_ADDRESS_PROP);
        }
        return mAddress2;
    }

    /**
     * Sets the value of the address2 property.
     *
     * @param pAddress2 parameter to set.
     */
    public void setAddress2(String pAddress2) {
        this.mAddress2 = pAddress2;
    }

    /**
     * Returns address3 property.
     *
     * @return address3 property.
     */
    public String getAddress3() {
        if ((mAddress3 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mAddress3 = getProfileAddressPropertyValue(ADDRESS_3_ADDRESS_PROP);
        }
        return mAddress3;
    }

    /**
     * Sets the value of the address3 property.
     *
     * @param pAddress3 parameter to set.
     */
    public void setAddress3(String pAddress3) {
        this.mAddress3 = pAddress3;
    }

    /**
     * Returns locality property.
     *
     * @return locality property.
     */
    public String getLocality() {
        if ((mLocality == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mLocality = getProfileAddressPropertyValue(LOCALITY_ADDRESS_PROP);
        }
        return mLocality;
    }

    /**
     * Sets the value of the locality property.
     *
     * @param pLocality parameter to set.
     */
    public void setLocality(String pLocality) {
        this.mLocality = pLocality;
    }

    /**
     * Returns country property.
     *
     * @return country property.
     */
    public String getCountry() {
        if ((mCountry == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mCountry = getProfileAddressPropertyValue(COUNTRY_ADDRESS_PROP);
        }
        return mCountry;
    }
    
    /**
     * Returns country code property.
     *
     * @return country property.
     */
    public String getState() {
        if ((mState == null) && (getProfile() != null) && !getProfile().isTransient()) {
        	mState = getProfileAddressPropertyValue(STATE_ADDRESS_PROP);
        }
        return mState;
    }

    /**
     * Sets the value of the country property.
     *
     * @param pCountry parameter to set.
     */
    public void setCountry(String pCountry) {
        this.mCountry = pCountry;
    }
    
    /**
     * Sets the value of the country code property.
     *
     * @param pCountry parameter to set.
     */
    public void setState(String pState) {
        this.mState = pState;
    }

    /**
     * Returns email property.
     *
     * @return email property.
     */
    public String getEmail() {
        if ((mEmail == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mEmail = (String) getProfile().getPropertyValue(EMAIL_PROFILE_PROP);
        }
        return mEmail;
    }

    /**
     * Sets the value of the email property.
     *
     * @param pEmail parameter to set.
     */
    public void setEmail(String pEmail) {
        this.mEmail = pEmail;
    }

    /**
     * Returns prenom property.
     *
     * @return prenom property.
     */
    public String getPrenom() {
        if ((mPrenom == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mPrenom = (String) getProfile().getPropertyValue(FIRST_NAME_PROFILE_PROP);
        }
        return mPrenom;
    }

    /**
     * Sets the value of the prenom property.
     *
     * @param pPrenom parameter to set.
     */
    public void setPrenom(String pPrenom) {
        this.mPrenom = pPrenom;
    }

    /**
     * Returns nom property.
     *
     * @return nom property.
     */
    public String getNom() {
        if ((mNom == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mNom = (String) getProfile().getPropertyValue(LAST_NAME_PROFILE_PROP);
        }
        return mNom;
    }

    /**
     * Sets the value of the nom property.
     *
     * @param pNom parameter to set.
     */
    public void setNom(String pNom) {
        this.mNom = pNom;
    }


    /**
     * Gets profile billing address.
     *
     * @param  property - property to get
     *
     * @return propery value
     */
    private String getProfileAddressPropertyValue(final String property) {
        RepositoryItem billingAddress = (RepositoryItem) getProfile().getPropertyValue(BILLING_ADDRESS);
        return (String) billingAddress.getPropertyValue(property);
    }

    /**
     * Returns civilite property.
     *
     * @return civilite property.
     */
    public String getCivilite() {
        if ((mCivilite == null) && (getProfile() != null) && !getProfile().isTransient()) {
            mCivilite = (String) getProfile().getPropertyValue(TITLE_PROFILE_PROP);
        }
        return mCivilite;
    }

    /**
     * Sets the value of the civilite property.
     *
     * @param pCivilite parameter to set.
     */
    public void setCivilite(String pCivilite) {
        this.mCivilite = pCivilite;
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
     * @param pProfile parameter to set.
     */
    public void setProfile(Profile pProfile) {
        this.mProfile = pProfile;
    }

    /**
     * Gets Address Helper instance that contains address manipulation
     * utilities.
     *
     * @return mAddressHelper
     */
    public CastAddressHelper getAddressHelper() {
        return mAddressHelper;
    }

    /**
     * Sets Address Helper instance that contains address manipulation
     * utilities.
     *
     * @param pAddressHelper
     */
    public void setAddressHelper(CastAddressHelper pAddressHelper) {
        this.mAddressHelper = pAddressHelper;
    }

    /**
     * Returns successUrl property.
     *
     * @return successUrl property.
     */
    public String getSuccessUrl() {
        return mSuccessUrl;
    }

    /**
     * Sets the value of the successUrl property.
     *
     * @param pSuccessUrl parameter to set.
     */
    public void setSuccessUrl(String pSuccessUrl) {
        mSuccessUrl = pSuccessUrl;
    }

    /**
     * Returns errorUrl property.
     *
     * @return errorUrl property.
     */
    public String getErrorUrl() {
        return mErrorUrl;
    }

    /**
     * Sets the value of the errorUrl property.
     *
     * @param pErrorUrl parameter to set.
     */
    public void setErrorUrl(String pErrorUrl) {
        mErrorUrl = pErrorUrl;
    }

    /**
     * Returns validator property.
     *
     * @return validator property.
     */
    public Validator getValidator() {
        return mValidator;
    }

    /**
     * Sets the value of the validator property.
     *
     * @param pValidator parameter to set.
     */
    public void setValidator(Validator pValidator) {
        mValidator = pValidator;
    }

    /**
     * Returns sendEmailInSeparateThread property.
     *
     * @return sendEmailInSeparateThread property.
     */
    public boolean getSendEmailInSeparateThread() {
        return mSendEmailInSeparateThread;
    }

    /**
     * Sets the value of the sendEmailInSeparateThread property.
     *
     * @param pSendEmailInSeparateThread parameter to set.
     */
    public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
        mSendEmailInSeparateThread = pSendEmailInSeparateThread;
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
        mPersistEmails = pPersistEmails;
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
     * Returns sendEmailInSeparateThread property.
     *
     * @return sendEmailInSeparateThread property.
     */
    public boolean isSendEmailInSeparateThread() {
        return mSendEmailInSeparateThread;
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
        mEmailSender = pEmailSender;
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
     * Returns resourceBundleName property.
     *
     * @return resourceBundleName property.
     */
    public String getResourceBundleName() {
        return mResourceBundleName;
    }

    /**
     * Sets resourceBundleName property
     *
     * @param pResourceBundleName - resourceBundleName to set
     */
    public void setResourceBundleName(String pResourceBundleName) {
        mResourceBundleName = pResourceBundleName;
    }

    /**
     * Returns resourceBundle property
     *
     * @return the resourceBundle property
     */
    public ResourceBundle getResourceBundle() {
        if (mResourceBundle == null) {
            mResourceBundle = ResourceBundle.getBundle(getResourceBundleName(), LangLicense.getLicensedDefault());
        }
        return mResourceBundle;
    }
}
