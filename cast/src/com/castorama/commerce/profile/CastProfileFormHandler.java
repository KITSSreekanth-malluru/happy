/**
 *
 */
package com.castorama.commerce.profile;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.BILLING_ADDRESS;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CONFIRM_PASSWORD_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CURRENT_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.DEFAULT_COUNTRY_NAME;
import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.FIRST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.FIRST_NAME_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LOGIN_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.PASSWORD_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.TELEPHONE_FIXE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PREFIX_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.Constants.SHIPPING_ADDRESS;
import static com.castorama.commerce.profile.Constants.STATE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.TITLE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.UPDATE_TYPE_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.CAST_CARD_NUMBER_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.CARTE_ATOUT_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.CARTE_ATOUT_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.NEWSLETTER_ITEM_DESC;
import static com.castorama.commerce.profile.Constants.DESCRIPTOR_NAME_PROFILE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_DUPLICATE_EMAIL_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_CONFIRMATION_EMAIL_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_CONFIRM_PASSWORD;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_EMAIL_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_LOGIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_PASSWORD;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_PHONE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_PASSWORD_LOGIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_CONFIRM_PASSWORD;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_LOGIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_PASSWORD;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSING_PASSWORD_LOGIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_PHONE_NUMBER;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_PASSWORD_WAS_CHANGED;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.StringUtils;
import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.commerce.clientspace.CastNewsletterFormHandler;
import com.castorama.utils.CommonHelper;
import com.castorama.utils.ContextTools;
import com.castorama.utils.StoreTools;
import com.castorama.utils.Validator;

/**
 * Profile Form Handler class. Provides custom handlers for creating and
 * updating profile.
 *
 * @author EPAM Team
 */
public class CastProfileFormHandler extends CommerceProfileFormHandler {
    /**
     * Session's property: contains URL for redirecting if login was successful.
     */
    private static final String SESS_PROP_LOGIN_SUCCESS_URL = "loginSuccessURL";

    /** Null string constant. */
    private static final String NULL_STRING = "null";

    /** Create Account Notification Email property: password. */
    private static final String EMAIL_PASSWORD = "newpassword";

    /** Create Account Notification Email property: firstName. */
    private static final String EMAIL_FIRST_NAME = "firstName";

    /** Create Account Notification Email property: email. */
    private static final String EMAIL_EMAIL = "email";

    /** Create Account Notification Email property: civilite. */
    private static final String EMAIL_CIVILITE = "civilite";

    /** Create Account Notification Email property: lastName. */
    private static final String EMAIL_LAST_NAME = "lastName";
    
    /** Prefix for new address name. */
    private static final String NEW_ADDRESS_NAME_PREFIX = "Adresse ";
    
    /** Password link item descriptor name. */
    private static final String PASSWORD_LINK_DESCRIPTOR = "passwordLink";
    
    /** FAILED_ATTEMPTS_PROPERTY property */
    private static final String FAILED_ATTEMPTS_PROPERTY = "failedAttempts";

    /** Error URL for new address creation. */
    private String mNewAddressErrorURL;

    /** Success URL for address update. */
    private String mUpdateAddressSuccessURL;

    /** Error URL for address update. */
    private String mUpdateAddressErrorURL;

    /** Success URL for new address creation. */
    private String mNewAddressSuccessURL;

    /** On Behalf URL. */
    private String mOnBehalfURL;

    /** Login Confirmation field. */
    private String mConfirmLogin;

    /** Edited value map. */
    private HashMap mEditValue = new HashMap();

    /** Address to be edited. */
    private String mEditAddress;

    /** Name of address to be removed. */
    private String mRemoveAddress;

    /** Common helper component. */
    private CommonHelper mCommonHelper;

    /** Address name. */
    private String mAddressName;

    /** Shows if newsletter subscription was updated. */
    private boolean mUpdated;
    
    /** Shows if light user account created. */
    private boolean mLightUser;

    /** Newsletter profile. */
    private CastNewsletterFormHandler mNewsletterProfile;

    /** Current session. */
    private String mSessionBean;

    /** Address Helper. */
    private CastAddressHelper mAddressHelper;

    /** Template Email Sender. */
    private TemplateEmailSender mTemplateEmailSender = null;

    /** Template Email Info. */
    private TemplateEmailInfoImpl mTemplateEmailInfo = null;

    /** prefix property */
    private String mPrefix;

    /** firstName property */
    private String mFirstName;

    /** lastName property */
    private String mLastName;

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

    /** state property */
    private String mState;

    /** country property */
    private String mCountry;

    /** phoneNumber property */
    private String mPhoneNumber;

    /** phoneNumber2 property */
    private String mPhoneNumber2;

    /** currentLocalStore property */
    private String mCurrentLocalStore;
    
    /** castoramaCard property, "true" or "false"*/
    private String castoramaCard;
    
    /** castoramaCardNumber property */
    private String castoramaCardNumber;
    
    /** Castorama card validation on the client side */
    private boolean castCardNumberCorrect;

    /**
     * Update Current Local store. This property is added because it is impossible
     * to remove value from field mCurrentLocalStore if it was stored in db before.
     * (getCurrentLocalStore() returns value from db if mCurrentLocalStore is empty).
     */
    private String mUpdateCurrentLocalStore;
    
    /** Store tools. */
    private StoreTools storeTools;

    /** Context tools. */
    private ContextTools contextTools;

    /** Newsletter Repository. */
    private Repository newsletterRepository;

    /** redirect URL. */
    private String mRedirectURL;

    /**
     * Indicates that the billing address should be copied to the shipping
     * address.
     */
    private boolean mShipToBillingAddress;

    /** Profile repository property. */
    private Repository profileRepository;
    
    /** Error URL for password update. */
    private String updatePasswordErrorURL;

    /** Success URL for password update. */
    private String updatePasswordSuccessURL;
    /**
     * Sets the property TemplateEmailSender
     *
     * @param pTemplateEmailSender - template email sender
     */
    public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
        this.mTemplateEmailSender = pTemplateEmailSender;
    }

    /**
     * Gets email sender template
     *
     * @return The value of the property TemplateEmailSender
     */
    public TemplateEmailSender getTemplateEmailSender() {
        return mTemplateEmailSender;
    }

    /**
     * Sets the property TemplateEmailInfo
     *
     * @param pTemplateEmailInfo - email info
     */
    public void setTemplateEmailInfo(TemplateEmailInfoImpl pTemplateEmailInfo) {
        this.mTemplateEmailInfo = pTemplateEmailInfo;
    }

    /**
     * Gets email info.
     *
     * @return The value of the property TemplateEmailInfo
     */
    public TemplateEmailInfoImpl getTemplateEmailInfo() {
        return mTemplateEmailInfo;
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
     * @param pFlagErrorCp the mFlagErrorCp to set
     */
    public void setFlagErrorCp(boolean pFlagErrorCp) {
        getAddressHelper().setFlagErrorCp(pFlagErrorCp);
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
     * Gets name of the address.
     *
     * @return name of address
     */
    public String getAddressName() {
        return mAddressName;
    }

    /**
     * Sets name of the address.
     *
     * @param pAddressName - new address name
     */
    public void setAddressName(String pAddressName) {
        this.mAddressName = pAddressName;
    }

    /**
     * Sets property removeAddress, naming the address to be removed by
     * handleRemoveAddress().
     *
     * @param pRemoveAddress - address to remove
     */
    public void setRemoveAddress(final String pRemoveAddress) {
        mRemoveAddress = pRemoveAddress;
    }

    /**
     * Returns property removeAddress, naming the address to be removed by
     * handleRemoveAddress().
     *
     * @return address to remove
     */
    public String getRemoveAddress() {
        return mRemoveAddress;
    }

    /**
     * Sets property newAddressErrorURL, used to redirect user in case of an
     * error creating an address.
     *
     * @param pNewAddressErrorURL - parameter to set
     */
    public void setNewAddressErrorURL(final String pNewAddressErrorURL) {
        mNewAddressErrorURL = pNewAddressErrorURL;
    }

    /**
     * Returns property newAddressErrorURL, used to redirect user in case of an
     * error creating an address.
     *
     * @return error address URL
     */
    public String getNewAddressErrorURL() {
        return mNewAddressErrorURL;
    }

    /**
     * Sets property updateAddressSuccessURL, used to redirect user when an
     * address is successfully updated.
     *
     * @param pUpdateAddressSuccessURL - parameter to set
     */
    public void setUpdateAddressSuccessURL(final String pUpdateAddressSuccessURL) {
        mUpdateAddressSuccessURL = pUpdateAddressSuccessURL;
    }

    /**
     * Returns property updateAddressSuccessURL, used to redirect user when an
     * address is successfully updated.
     *
     * @return update address URL
     */
    public String getUpdateAddressSuccessURL() {
        return mUpdateAddressSuccessURL;
    }

    /**
     * Sets property updateAddressErrorURL, used to redirect user in case of an
     * error updating an address.
     *
     * @param pUpdateAddressErrorURL - parameter to set
     */
    public void setUpdateAddressErrorURL(final String pUpdateAddressErrorURL) {
        mUpdateAddressErrorURL = pUpdateAddressErrorURL;
    }

    /**
     * Returns property updateAddressErrorURL, used to redirect user in case of
     * an error updating an address.
     *
     * @return update address error URL
     */
    public String getUpdateAddressErrorURL() {
        return mUpdateAddressErrorURL;
    }

    /**
     * Sets property newAddressSuccessURL, used to redirect user after
     * successfully creating an address.
     *
     * @param pNewAddressSuccessURL - parameter to set
     */
    public void setNewAddressSuccessURL(final String pNewAddressSuccessURL) {
        mNewAddressSuccessURL = pNewAddressSuccessURL;
    }

    /**
     * Returns property newAddressSuccessURL, used to redirect user after
     * successfully creating an address.
     *
     * @return new address success URL
     */
    public String getNewAddressSuccessURL() {
        return mNewAddressSuccessURL;
    }

    /**
     * Gets map of values for addresses pending values for an editing operations
     * on the profile.
     *
     * @return The value of the property EditValue. This is a map that stores
     *         the pending values for an editing operations on the profile
     */
    public Map getEditValue() {
        return mEditValue;
    }

    /**
     * Sets property editAddress, naming the address to be edited.
     *
     * @param pEditValue pEditAddress
     */
    public void setEditValue(final Map pEditValue) {
        mEditValue = (HashMap) pEditValue;
    }

    /**
     * Sets property editAddress, naming the address to be edited.
     *
     * @param pEditAddress
     */
    public void setEditAddress(final String pEditAddress) {
        mEditAddress = pEditAddress;
    }

    /**
     * Returns property editAddress, naming the address to be edited.
     *
     * @return map with pending values for an editing operations on the profile
     */
    public String getEditAddress() {
        return mEditAddress;
    }

    /**
     * Sets property shipToBillingAddress, indicating that the billing address
     * should be copied to the shipping address.
     *
     * @param pShipToBillingAddress - parameter to set
     */
    public void setShipToBillingAddress(boolean pShipToBillingAddress) {
        mShipToBillingAddress = pShipToBillingAddress;
    }

    /**
     * Returns property shipToBillingAddress, indicating that the billing
     * address should be copied to the shipping address.
     *
     * @return shipToBillingAddress property
     */
    public boolean isShipToBillingAddress() {
        return mShipToBillingAddress;
    }

    /**
     * A helper method to return the CommercePropertyManager.
     *
     * @return property manager
     */
    private CommercePropertyManager retrieveCommercePropertyManager() {
        return (CommercePropertyManager) getProfileTools().getPropertyManager();
    }

    /**
     * Gets login confirmation.
     *
     * @return login confirmation
     */
    public String getConfirmLogin() {
        return mConfirmLogin;
    }

    /**
     * Sets login confirmation.
     *
     * @param pConfirmLogin login confirmation
     */
    public void setConfirmLogin(final String pConfirmLogin) {
        this.mConfirmLogin = pConfirmLogin;
    }

    /**
     * Validates and updates login (email) and password. If login and password
     * are incorrect - add form exceptions. Otherwise - update credentials and
     * create new subscription if email filed was updated.
     *
     * @param  pRequest  the request of the form handler
     * @param  pResponse the response of the form handler
     *
     * @return <code>true</code> if there is no redirect, false if there is
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    public boolean handleUpdateLoginPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                      throws ServletException, IOException {
        final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);
        final String password = (String) getValueProperty(PASSWORD_PROFILE_PROP);
        final String confirmPassword = (String) getValueProperty(CONFIRM_PASSWORD_PROFILE_PROP);

        if (validateEmail(login, true, true, pRequest, pResponse) & validatePassword(password, confirmPassword)) {
            Profile profile = getProfile();
            profile.setPropertyValue(EMAIL_PROFILE_PROP, login);

            String oldemail = (String) getProfile().getPropertyValue(LOGIN_PROFILE_PROP);

            // Creates new subscription if email was updated.
            if (!login.equalsIgnoreCase(oldemail)) {
                getNewsletterProfile().copyElement(pRequest, pResponse, login, oldemail);
            }
        } else {
            handleClearPassword();
            return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
        }

        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Creates a new shipping address using the entries entered in the editValue
     * map. The address will be indexed using the nickname provided by the user.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @return    If redirect (for whatever reason) to a new page occurred, return false. If NO redirect occurred, return true.
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean handleNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
        // Get editValue map, containing the user form data
        HashMap newAddress = (HashMap) getEditValue();

        return newAddress(pRequest, pResponse, newAddress);

    }

    /**
     * Creates a new shipping address.
     *
     * @param	pRequest  the servlet's request
     * @param   pResponse the servlet's response
     * @param  	newAddress new address values
     *
     * @return If redirect (for whatever reason) to a new page occurred, return false. If NO redirect occurred, return true.
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean newAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                              final Map newAddress) throws ServletException, IOException {
        if (!getAddressHelper().validateNameAddress(newAddress, this) |
                !getAddressHelper().validateAddress(newAddress, this) |
                !getAddressHelper().validatePhone(newAddress, this)) {
            return checkFormRedirect(getNewAddressErrorURL(), getNewAddressErrorURL(), pRequest, pResponse);
        }

        String nickname = getAddressName();
        final CommercePropertyManager cpmgr = retrieveCommercePropertyManager();
        Map addresses = (Map) getProfile().getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

        if ((null == nickname) || (0 == nickname.length())) {
            nickname = NEW_ADDRESS_NAME_PREFIX + (addresses.size() + 1);
        }

        if (!getFormError() && getAddressHelper().addAddress(this, nickname, newAddress)) {
            handleClear(pRequest, pResponse);

            return checkFormRedirect(getNewAddressSuccessURL(), getNewAddressSuccessURL(), pRequest, pResponse);
        } else {
            return checkFormRedirect(getNewAddressErrorURL(), getNewAddressErrorURL(), pRequest, pResponse);
        }
    }
    
    /**
     * Updates local store and Castorama card number
     * 
     * @param  pRequest  the request of the form handler
     * @param  pResponse the response of the form handler
     *
     * @return <code>true</code> if there is no redirect, false if there is
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    public boolean handleUpdateMyInformation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                      throws ServletException, IOException {        
        boolean result=false;
        
        updateStore(pResponse);


        Profile profile = getProfile();
        updateCastoramaCardProperties();

        String email = (String) profile.getPropertyValue(EMAIL_PROFILE_PROP);
        try {
            RepositoryItem newsletterSubscription = getNewsletterRepository().getItem(email, NEWSLETTER_ITEM_DESC);
            MutableRepository mutableRepository = (MutableRepository)newsletterSubscription.getRepository();     
            MutableRepositoryItem mutableNewsletter = mutableRepository.getItemForUpdate(newsletterSubscription.getRepositoryId(), newsletterSubscription.getItemDescriptor().getItemDescriptorName());
            mutableNewsletter.setPropertyValue(CARTE_ATOUT_NEWSLETTER_PROP, Boolean.parseBoolean(castoramaCard));
            mutableRepository.updateItem(mutableNewsletter); 
            result=super.handleUpdate(pRequest, pResponse);
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError("Impossible to update repository item.", re);
            }
        } 
        return result;
    }
    
    /**
     * Validates and updates address block.
     *
     * @param  pRequest  the request of the form handler
     * @param  pResponse the response of the form handler
     *
     * @return true if there is no redirect, false if there is
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    public boolean handleUpdatePrimaryAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                       throws ServletException, IOException {
        final Map values = convertPrimaryAddress();

        return updatePrimaryAddress(pRequest, pResponse, values);
    }

    /**
     * Updates Current Local Store.
     * 
     * @param pResponse the response of the form handler
     * @throws ServletException 
     */
    private void updateStore(DynamoHttpServletResponse pResponse) throws ServletException {
        String storeId = getUpdateCurrentLocalStore();
        if (storeId != null)
            getContextTools().createCookie(pResponse, storeId);
    }

    /**
     * Validate primary address and if it is OK - update it.
     *
     * @param  pRequest  the request of the form handler
     * @param  pResponse the response of the form handler *
     * @param  pValues   - address values
     *
     * @return true if there is no redirect, false if there is
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    public boolean updatePrimaryAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                        final Map pValues) throws ServletException, IOException {
        if (getAddressHelper().validateAddress(pValues, this) 
        		& getAddressHelper().validateNameAddress(pValues, this) 
        		& getAddressHelper().validatePhone(pValues, this)) {
            setPrimaryAddress(pValues);
            checkChangesPrimaryAddress();
            synchronizePrimaryAddress();
        } else {
            if ((getUpdateErrorURL() != null) && !getUpdateErrorURL().equals(NULL_STRING)) {
                return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            }
        }
        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Create map with primary address: copy values from bean properties.
     *
     * @return map with address properties
     */
    public Map<String, String> convertPrimaryAddress() {
        Map<String, String> address = new HashMap<String, String>();
        address.put(PREFIX_ADDRESS_PROP, getPrefix());
        address.put(FIRST_NAME_ADDRESS_PROP, getFirstName());
        address.put(LAST_NAME_ADDRESS_PROP, getLastName());

        address.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        address.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        address.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        address.put(LOCALITY_ADDRESS_PROP, getLocality());
        address.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        address.put(CITY_ADDRESS_PROP, getCity());
        address.put(STATE_ADDRESS_PROP, getState());

        String country = getCountry();

        if ((country == null) || ((country != null) && (country.length() == 0))) {
            country = DEFAULT_COUNTRY_NAME;
            setCountry(country);
            address.put(COUNTRY_ADDRESS_PROP, getCountry());
        }

        address.put(COUNTRY_ADDRESS_PROP, country);
        address.put(PHONE_NUMBER_ADDRESS_PROP, getPhoneNumber());
        address.put(PHONE_NUMBER_2_ADDRESS_PROP, getPhoneNumber2());

        return address;
    }

    /**
     * Sets the value of the primaryAddress property: copy them from map into
     * the values Dictionary .
     *
     * @param pAddress - map with address values
     */
    public void setPrimaryAddress(final Map<String, String> pAddress) {
        setValueProperty(TITLE_PROFILE_PROP, pAddress.get(PREFIX_ADDRESS_PROP));
        setValueProperty(FIRST_NAME_PROFILE_PROP, pAddress.get(FIRST_NAME_ADDRESS_PROP));
        setValueProperty(LAST_NAME_PROFILE_PROP, pAddress.get(LAST_NAME_ADDRESS_PROP));

        setFirstName(pAddress.get(FIRST_NAME_ADDRESS_PROP));
        setLastName(pAddress.get(LAST_NAME_ADDRESS_PROP));
        setPrefix(pAddress.get(PREFIX_ADDRESS_PROP));
        Dictionary<String, String> billingAddress = (Dictionary<String, String>) getValueProperty(BILLING_ADDRESS);
        if (billingAddress == null) {
            billingAddress = new Hashtable<String, String>();
        }
        billingAddress.put(ADDRESS_1_ADDRESS_PROP, pAddress.get(ADDRESS_1_ADDRESS_PROP));
        billingAddress.put(ADDRESS_2_ADDRESS_PROP, pAddress.get(ADDRESS_2_ADDRESS_PROP));
        billingAddress.put(ADDRESS_3_ADDRESS_PROP, pAddress.get(ADDRESS_3_ADDRESS_PROP));
        billingAddress.put(COUNTRY_ADDRESS_PROP, pAddress.get(COUNTRY_ADDRESS_PROP));
        billingAddress.put(LOCALITY_ADDRESS_PROP, pAddress.get(LOCALITY_ADDRESS_PROP));
        billingAddress.put(POSTAL_CODE_ADDRESS_PROP, pAddress.get(POSTAL_CODE_ADDRESS_PROP));
        billingAddress.put(CITY_ADDRESS_PROP, pAddress.get(CITY_ADDRESS_PROP));
        billingAddress.put(STATE_ADDRESS_PROP, pAddress.get(STATE_ADDRESS_PROP));
        billingAddress.put(PHONE_NUMBER_ADDRESS_PROP, pAddress.get(PHONE_NUMBER_ADDRESS_PROP));
        billingAddress.put(PHONE_NUMBER_2_ADDRESS_PROP, pAddress.get(PHONE_NUMBER_2_ADDRESS_PROP));

        setValueProperty(BILLING_ADDRESS, billingAddress);
    }

    /**
     * Validates <code>email</code>, <code>password</code>, <code>
     * address</code>, newsletter's properties.<br>
     * If validation id OK synchronize billing and shipping addresses.<br>
     * Otherwise: clear passwords fields.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override protected void preCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
        final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);
        final String password = (String) getValueProperty(PASSWORD_PROFILE_PROP);
        final String confirmPassword = (String) getValueProperty(CONFIRM_PASSWORD_PROFILE_PROP);
        final String telephoneFixe=pRequest.getParameter(TELEPHONE_FIXE_ADDRESS_PROP);

        if (!isLightUser()) {
            final Map<String, String> account = convertPrimaryAddress();

            validateEmail(login, true, true, pRequest, pResponse);
            validatePassword(password, confirmPassword);
            getAddressHelper().validateNameAddress(account, this);
            getAddressHelper().validateAddress(account, this);
            validatePhone(telephoneFixe, account, this);
            getNewsletterProfile().validateNewsletterStore(this);
            getNewsletterProfile().validateAdditionalInfo(this);

            if (getFormError()) {
                handleClearPassword();
            } else {
                setPrimaryAddress(account);
                synchronizePrimaryAddress();
            }
        } else {
            validateEmail(login, true, true, pRequest, pResponse);
            validatePassword(password, confirmPassword);

            if (getFormError()) {
                handleClearPassword();
            }
        }
    }

    /**
     * Update the secondary address as modified by the user.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @return    true if there is no redirect, false if there is
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                throws ServletException, IOException {
        Map address = getEditValue();

        return updateAddress(pRequest, pResponse, address, getAddressName());

    }

    /**
     * Validate primary address and if it is OK - update it.
     *
     * @param  pRequest    - the request of the form handler
     * @param  pResponse   - the response of the form handler
     * @param  address     - address values
     * @param  addressName - address name to be updated
     *
     * @return true if there is no redirect, false if there is
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    public boolean updateAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                 final Map address, final String addressName) throws ServletException, IOException {
        boolean result = false;
        if (!getAddressHelper().validateNameAddress(address, this) |
                !getAddressHelper().validateAddress(address, this) | !getAddressHelper().validatePhone(address, this)) {
            result = checkFormRedirect(getUpdateAddressErrorURL(), getUpdateAddressErrorURL(), pRequest, pResponse);
        }

        if (!getFormError() && getAddressHelper().updateAddress(address, this, addressName)) {
            result = checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressSuccessURL(), pRequest, pResponse);

        } else {
            if (getCommonHelper().ifDbError(this)) {
                setUpdateAddressErrorURL(getCommonHelper().getDbErrorURL());
            }
            result = checkFormRedirect(getUpdateAddressErrorURL(), getUpdateAddressErrorURL(), pRequest, pResponse);
        }

        return result;
    }

    /**
     * Copy the named seconary address into the editValue map, allowing the user
     * to edit them in the page. Redirect on updateAddressSuccessURL page if there are no errors,
     * on updateAddressErrorURL otherwise.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @return    true if there is no redirect, false if there is
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean handleEditAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        String nickname = getEditAddress();
        boolean result = editAddress(pRequest, pResponse, nickname);
        if (result) {
            return checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressSuccessURL(), pRequest, pResponse);
        } else {
            return checkFormRedirect(getUpdateAddressErrorURL(), getUpdateAddressErrorURL(), pRequest, pResponse);
        }
    }

    /**
     *
     * Copy the named seconary address into the editValue map.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     * @param  	nickname - address name to be prepopulated into editValue map
     *
     * @return <code>true</code> if address was edited successfully
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean editAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String nickname)
                        throws IOException, ServletException {
        if (getProfile().isTransient()) {
            SessionBean sb = (SessionBean) pRequest.resolveName(getSessionBean());
            sb.getValues().put(SESS_PROP_LOGIN_SUCCESS_URL, pRequest.getRequestURI());
            redirectOrForward(pRequest, pResponse, getRedirectURL());
            if (isLoggingDebug()) {
                logDebug("Session expired. Redirect to login jsp. ");
            }
            return true;
        }

        Map editValues = getEditValue();
        return getAddressHelper().editAddress(nickname, editValues);
    }

    /**
     * This handler deletes a secondary address named in the removeAddress
     * property.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @return    true if there is no redirect, false if there is
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    public boolean handleRemoveAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                throws ServletException, IOException {
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();

        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }

            final String nickname = getRemoveAddress();

            return getAddressHelper().removeAddress(nickname);

        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }  // end try-catch-finally
    }

    /**
     * Validates and updates email address.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        final String email = (String) getValueProperty(EMAIL_PROFILE_PROP);
        validateEmail(email, false, true, pRequest, pResponse);

        return super.handleUpdate(pRequest, pResponse);
    }
    
    public boolean handleUpdatePassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        
        boolean result = false;
        RepositoryItemDescriptor profileDescriptor;
        final String login = (String)getValueProperty(LOGIN_PROFILE_PROP);
        RepositoryItem link = null;
        try {
            link = getProfileRepository().getItem(login, PASSWORD_LINK_DESCRIPTOR);
            if(link != null) {
                try {
                    profileDescriptor = getProfileRepository().getItemDescriptor(DESCRIPTOR_NAME_PROFILE);
                    RepositoryView profileView = profileDescriptor.getRepositoryView();
                    QueryBuilder profileBuilder = profileView.getQueryBuilder();
                    Query profileQuery = profileBuilder.createComparisonQuery(
                            profileBuilder.createPropertyQueryExpression(LOGIN_PROFILE_PROP),
                            profileBuilder.createConstantQueryExpression(login),
                            QueryBuilder.EQUALS);
                    RepositoryItem[] profileItems = profileView.executeQuery(profileQuery);
                        
                    if(profileItems != null) {
                        RepositoryItem profileItem = profileItems[0];
                        final String password = (String) getValueProperty(PASSWORD_PROFILE_PROP);
                        final String confirmPassword = (String) getValueProperty(CONFIRM_PASSWORD_PROFILE_PROP);
                        if(validatePassword(password, confirmPassword)) {
                            String cachePassword = getProfileTools().getPropertyManager().generatePassword(login, password);
                            MutableRepository repository = (MutableRepository)getProfileRepository();
                            MutableRepositoryItem mutableRepositoryItem = repository.getItemForUpdate(profileItem.getRepositoryId(), DESCRIPTOR_NAME_PROFILE);
                            mutableRepositoryItem.setPropertyValue(PASSWORD_PROFILE_PROP, cachePassword);
                            mutableRepositoryItem.setPropertyValue(FAILED_ATTEMPTS_PROPERTY, 0);
                            repository.updateItem(mutableRepositoryItem);
                            result = true;
                        } else {
                            handleClearPassword();
                        }
                    } else {
                        handleClearPassword();
                        if (isLoggingDebug()) 
                            logDebug("There is not user with such email: " + login);
                    }
                } catch (RepositoryException e) {
                    handleClearPassword();
                    if (isLoggingError()) {
                        logError("Impossible to update repository item.", e);
                    }
                }
                if(result) {
                    MutableRepository mutableRepository = (MutableRepository) getProfileRepository();
                    try {
                        mutableRepository.removeItem(login, PASSWORD_LINK_DESCRIPTOR);
                    } catch (RepositoryException e) {
                        if (isLoggingError()) {
                            logError("Impossible to remove password link repository item: " + login, e);
                        }
                    }
                }
            } else {
                getCommonHelper().generateFormException(MSG_PASSWORD_WAS_CHANGED, this, RESOURCE_BUNDLE);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("Impossible to find link to change password for user: " + login, e);
            }
        }
        return checkFormRedirect(getUpdatePasswordSuccessURL(), getUpdatePasswordErrorURL(), pRequest, pResponse);
    }
    /**
     * Validate login.
     *
     * @param  pLogin        - login (email address)
     * @param  pCheckConfirm - flag shows if confirmation of the login should be
     *                       validated
     * @param  pIsNew        - flag shows if it is necessary to validate on
     *                       existance such email in the repository
     * @param  pRequest      - the servlet's request
     * @param  pResponse     - the servlet's response
     *
     * @return <code>true</code> if validation is successful
     *
     * @throws ServletException
     * @throws IOException
     */
    protected boolean validateEmail(final String pLogin, final boolean pCheckConfirm, final boolean pIsNew,
                                    DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
        boolean result = true;
        Profile profile = getProfile();

        if (!valueIsEmpty(pLogin)) {
            if (!Validator.validateEmail(pLogin)) {
                getCommonHelper().generateFormException(MSG_INCORRECT_EMAIL_ADDRESS, this, RESOURCE_BUNDLE);
                result = false;
            } else {
                try {
                    boolean userAlreadyExists =
                        userAlreadyExists(pLogin, getProfile().getRepository(), pRequest, pResponse);

                    if ((((profile != null) && (!pLogin.equals(profile.getPropertyValue(LOGIN_PROFILE_PROP)))) ||
                             profile.isTransient()) && userAlreadyExists) {
                        if (pIsNew) {
                            getCommonHelper().generateFormException(MSG_DUPLICATE_EMAIL_ADDRESS, this, RESOURCE_BUNDLE);
                            result = false;
                        }
                    } else if (!pIsNew) {
                        getCommonHelper().generateFormException(MSG_INCORRECT_LOGIN, this, RESOURCE_BUNDLE);
                        result = false;
                    }
                } catch (RepositoryException re) {
                    if (isLoggingError()) {
                        logError("Impossible to check if the user with this email address already exists.", re);
                    }
                }

                if (pCheckConfirm && (getConfirmLogin() != null) && (pLogin.compareTo(getConfirmLogin()) != 0)) {
                    getCommonHelper().generateFormException(MSG_INCORRECT_CONFIRMATION_EMAIL_ADDRESS, this,
                                                            RESOURCE_BUNDLE);
                    result = false;
                }
            }  // end if-else

        } else {
            getCommonHelper().generateFormException(MSG_MISSED_EMAIL, this, RESOURCE_BUNDLE);
            result = false;
        }  // end if-else

        return result;
    }

    /**
     * Validate password.
     *
     * @param  pPassword        - password
     * @param  pConfirmPassword - confirmation password
     *
     * @return <code>true</code> if validation is successful
     */
    private boolean validatePassword(final String pPassword, final String pConfirmPassword) {
        boolean result = true;

        if (!valueIsEmpty(pPassword)) {
            if (!Validator.validatePassword(pPassword)) {
                getCommonHelper().generateFormException(MSG_INCORRECT_PASSWORD, this, RESOURCE_BUNDLE);
                result = false;
            }

            if (valueIsEmpty(pConfirmPassword)) {
                getCommonHelper().generateFormException(MSG_MISSED_CONFIRM_PASSWORD, this, RESOURCE_BUNDLE);
                result = false;
            } else if (pPassword.compareTo(pConfirmPassword) != 0) {
                getCommonHelper().generateFormException(MSG_INCORRECT_CONFIRM_PASSWORD, this, RESOURCE_BUNDLE);
                result = false;
            }
        } else {
            getCommonHelper().generateFormException(MSG_MISSED_PASSWORD, this, RESOURCE_BUNDLE);
            result = false;
        }

        return result;
    }

    /**
     * Validates if login and password correct and exist in database: checks if
     * password and login is not empty, checks if login is exist and password is
     * match.
     *
     * @param  pLogin    - login name
     * @param  pPassword - password
     * @param  pIsAdmin  - if user admin (if true - do not check password)
     * @param  pRequest  - the servlet's request
     * @param  pResponse - the servlet's response
     *
     * @return <code>true</code> if <code>pLogin</code> is exist and <code>
     *         pPassword</code> is correct for this login.
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    protected boolean validateLogin(final String pLogin, final String pPassword, boolean pIsAdmin,
                                    DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
        boolean result = true;

        try {
            if (valueIsEmpty(pLogin)) {
                getCommonHelper().generateFormException(MSG_MISSED_LOGIN, this, RESOURCE_BUNDLE);
                result = false;
            } else if (!userAlreadyExists(pLogin, getProfile().getRepository(), pRequest, pResponse)) {
                getCommonHelper().generateFormException(MSG_INCORRECT_LOGIN, this, RESOURCE_BUNDLE);
                result = false;
            }

            // Validate password for no-admin users only
            if (!pIsAdmin) {
                if (valueIsEmpty(pPassword)) {
                    getCommonHelper().generateFormException(MSG_MISSING_PASSWORD_LOGIN, this, RESOURCE_BUNDLE);
                    result = false;
                } else if (findUser(pRequest, pResponse) == null) {
                    getCommonHelper().generateFormException(MSG_INVALID_PASSWORD_LOGIN, this, RESOURCE_BUNDLE);
                    result = false;
                }
            }

        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError("Impossible to check if the user with this email address already exists.", re);
            }
        }  // end try-catch

        return result;
    }
    
    /**
     * Validates phones on the registration page
     *
     * @param  telephoneFixe - telephone fixe
     * @param  address     phoneNumber - first phone number
     * @param  formHandler
     *
     * @return <code>true</code> if fields are valid, <code>false</code>
     *         otherwise
     */
	private boolean validatePhone(final String telephoneFixe, final Map<String, String> address, 
			final GenericFormHandler formHandler) {	
		
		boolean result = true;
		
		boolean isFrance = false;
        if(DEFAULT_COUNTRY_NAME.equals(address.get(COUNTRY_ADDRESS_PROP)))
        	isFrance = true;
        
        if (StringUtils.isBlank(telephoneFixe) ||
        		StringUtils.isBlank(address.get(PHONE_NUMBER_ADDRESS_PROP))) {
        	
    		getCommonHelper().generateFormException(MSG_MISSED_PHONE_NUMBER, formHandler, RESOURCE_BUNDLE);
    		
    		if((!StringUtils.isBlank(telephoneFixe) && 
    				!Validator.validatePhone(telephoneFixe, isFrance)) ||
            		(!StringUtils.isBlank(address.get(PHONE_NUMBER_ADDRESS_PROP)) && 
            				!Validator.validatePhone(address.get(PHONE_NUMBER_ADDRESS_PROP), isFrance)) ||
                    		(!StringUtils.isBlank(address.get(PHONE_NUMBER_2_ADDRESS_PROP)) && 
                            		!Validator.validatePhone(address.get(PHONE_NUMBER_2_ADDRESS_PROP), isFrance))) {
    			
    			getCommonHelper().generateFormException(MSG_INCORRECT_PHONE, formHandler, RESOURCE_BUNDLE);
    		}
    		result = false;
        } else if (!Validator.validatePhone(telephoneFixe, isFrance) ||
        		!Validator.validatePhone(address.get(PHONE_NUMBER_ADDRESS_PROP), isFrance) ||
        		(!StringUtils.isBlank(address.get(PHONE_NUMBER_2_ADDRESS_PROP)) && !Validator.validatePhone(address.get(PHONE_NUMBER_2_ADDRESS_PROP), isFrance))) {
        	
        	getCommonHelper().generateFormException(MSG_INCORRECT_PHONE, formHandler, RESOURCE_BUNDLE);
        	result = false;
            }
        return result;
	}

    /**
     * Copy values from Basics section (first name, last name and prefix) into
     * billing and shipping address.
     */
    private void synchronizePrimaryAddress() {
        // 1. Copy the first, middle, and last name fields from the user profile
        // to the billing and shipping addresses
        String firstName = getFirstName();
        String lastName = getLastName();
        String title = getPrefix();

        // Extract the billingAddress and shippingAddress, as dictionary
        // objects,
        // from the values dictionary.
        Dictionary<String, String> billingAddress = (Dictionary<String, String>) getValueProperty(BILLING_ADDRESS);
        Dictionary<String, String> shippingAddress = (Dictionary<String, String>) getValueProperty(SHIPPING_ADDRESS);

        // Copy all three names to billing & shipping addresses
        if (billingAddress != null) {
            billingAddress.put(FIRST_NAME_ADDRESS_PROP, firstName);
            billingAddress.put(LAST_NAME_ADDRESS_PROP, lastName);
            billingAddress.put(PREFIX_ADDRESS_PROP, title);
        }

        if ((shippingAddress != null) && !isShipToBillingAddress()) {
            shippingAddress.put(FIRST_NAME_ADDRESS_PROP, firstName);
            shippingAddress.put(LAST_NAME_ADDRESS_PROP, lastName);
            shippingAddress.put(PREFIX_ADDRESS_PROP, title);
        }
        final CommercePropertyManager cpmgr = retrieveCommercePropertyManager();
        Profile profile = getProfile();

        // If the user has asked to use the billing address as her/his
        // shipping address...
        if (isShipToBillingAddress()) {
            RepositoryItem billaddr = (RepositoryItem) profile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
            profile.setPropertyValue(cpmgr.getShippingAddressPropertyName(), billaddr);
        }
    }

    /**
     * Creates newsletters profile. Set the registration date to 'now'. If the
     * user has asked to use the same address for shipping and billing, make it
     * so.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override protected void postCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                     throws ServletException, IOException {
        updateStore(pResponse);

        if (getFormError()) {
            if (getCommonHelper().ifDbError(this)) {
                setCreateErrorURL(getCommonHelper().getDbErrorURL());
            }
            handleClearPassword();
        }
        super.postCreateUser(pRequest, pResponse);

        final CommercePropertyManager cpmgr = retrieveCommercePropertyManager();
        Profile profile = getProfile();

        final Boolean isCreated = (Boolean) pRequest.getObjectParameter(HANDLE_CREATE_PARAM);

        if ((isCreated != null) && (isCreated.booleanValue())) {
        	pRequest.addQueryParameter(HANDLE_CREATE, isCreated.toString());
            // We need the user's profile and shipping addresses

            // 1. Set Registration date to 'now'
            profile.setPropertyValue(cpmgr.getRegistrationDatePropertyName(), new java.util.Date());

            // 2. If the user has asked to use the billing address as her/his
            // shipping address...
            if (isShipToBillingAddress()) {
                RepositoryItem billaddr =
                    (RepositoryItem) profile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
                profile.setPropertyValue(cpmgr.getShippingAddressPropertyName(), billaddr);
            }

            profile.setPropertyValue(EMAIL_PROFILE_PROP, getValueProperty(LOGIN_PROFILE_PROP));
            profile.setPropertyValue(UPDATE_TYPE_PROFILE_PROP, 0);
            updateCastoramaCardProperties();

            final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);
            getNewsletterProfile().handleCreateFromProfile(pRequest, pResponse, login);
        }
        sendCreateAccountEmail();
    }

    /**
     * Send Create Account notification Email.
     */
    private void sendCreateAccountEmail() {
        Profile profile = getProfile();
        ProfileTools ptools = getProfileTools();
        RepositoryItem user =
            ptools.getItem((String) profile.getPropertyValue(LOGIN_PROFILE_PROP), null, getLoginProfileType());
        Map params = new HashMap();
        params.put(EMAIL_LAST_NAME, profile.getPropertyValue(LAST_NAME_PROFILE_PROP));
        params.put(EMAIL_CIVILITE, profile.getPropertyValue(TITLE_PROFILE_PROP));
        params.put(EMAIL_EMAIL, profile.getPropertyValue(LOGIN_PROFILE_PROP));
        params.put(EMAIL_FIRST_NAME, profile.getPropertyValue(FIRST_NAME_PROFILE_PROP));
        params.put(EMAIL_PASSWORD, getValueProperty(PASSWORD_PROFILE_PROP));

        try {
            ptools.sendEmailToUser((MutableRepositoryItem) user, true, false, getTemplateEmailSender(),
                                   getTemplateEmailInfo(), params);
        } catch (TemplateEmailException e) {
            logError("It is impossible to send 'create account' email: " + e.toString());
        }
    }

    private void updateCastoramaCardProperties(){
        Profile profile = getProfile();
        if (castoramaCard != null) {
            profile.setPropertyValue(CARTE_ATOUT_PROFILE_PROP, Boolean.parseBoolean(castoramaCard));
            if (isCastCardNumberCorrect() || !Boolean.parseBoolean(castoramaCard)) {
                profile.setPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP, castoramaCardNumber);
            } else {
                profile.setPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP, null);
            }
        } else {
            profile.setPropertyValue(CARTE_ATOUT_PROFILE_PROP, null);
            profile.setPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP, null);
        }
    }

    /**
     * Resets the field password.
     */
    public void handleClearPassword() {
        Dictionary valueDict = getValue();
        valueDict.remove(PASSWORD_PROFILE_PROP);
        valueDict.remove(CONFIRM_PASSWORD_PROFILE_PROP);
    }

    /**
     * Clear current values: address.
     *
     * @param  pRequest  - the servlet's request
     * @param  pResponse - the servlet's response
     *
     * @return <code>true</code> if redirect fails
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    @Override public boolean handleClear(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                  throws ServletException, IOException {
        Dictionary<String, String> billingAddress = (Dictionary<String, String>) getValueProperty(BILLING_ADDRESS);
        Dictionary<String, String> shippingAddress = (Dictionary<String, String>) getValueProperty(SHIPPING_ADDRESS);

        if (billingAddress != null) {
            for (Enumeration<String> e = billingAddress.keys(); e.hasMoreElements();) {
                String propertyName = e.nextElement();
                billingAddress.remove(propertyName);
            }
        }

        if (shippingAddress != null) {
            for (Enumeration<String> e = shippingAddress.keys(); e.hasMoreElements();) {
                String propertyName = e.nextElement();
                shippingAddress.remove(propertyName);
            }
        }

        Map<String, ?> valueDict = getEditValue();

        if (valueDict != null) {
            valueDict.clear();
        }

        // setExtractDefaultValuesFromProfile(false);
        return super.handleClear(pRequest, pResponse);
    }

    /**
     * Sets newsletter profile instance.
     *
     * @return the mNewsletterProfile
     */
    public CastNewsletterFormHandler getNewsletterProfile() {
        return mNewsletterProfile;
    }

    /**
     * Gets newsletter profile instance.
     *
     * @param pNewsletterProfile the mNewsletterProfile to set
     */
    public void setNewsletterProfile(CastNewsletterFormHandler pNewsletterProfile) {
        mNewsletterProfile = pNewsletterProfile;
    }

    /**
     * After profile update: sets property {@link
     * Constants#UPDATE_TYPE_PROFILE_PROP} to 1.
     *
     * @param  pRequest  - the servlet's request
     * @param  pResponse - the servlet's response
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    @Override protected void postUpdateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                     throws ServletException, IOException {
        updateStore(pResponse);

        if (getFormError() && getCommonHelper().ifDbError(this)) {
            setUpdateErrorURL(getCommonHelper().getDbErrorURL());
        } else {
            Profile profile = getProfile();
            profile.setPropertyValue(UPDATE_TYPE_PROFILE_PROP, 1);
            getNewsletterProfile().notifyExperianUpdate((String) profile.getPropertyValue(LOGIN_PROFILE_PROP));
            setUpdated(true);
            super.postUpdateUser(pRequest, pResponse);
        }
    }

    /**
     * Gets flag that shows if newsletter subscription was updated
     *
     * @return the updated
     */
    public boolean isUpdated() {
        return mUpdated;
    }

    /**
     * Sets flag that shows if newsletter subscription was updated
     *
     * @param pUpdated the updated to set
     */
    public void setUpdated(boolean pUpdated) {
        this.mUpdated = pUpdated;
    }
    
    public boolean isLightUser() {
        return mLightUser;
    }

    public void setLightUser(boolean lightUser) {
        mLightUser = lightUser;
    }

    /**
     * Gets current session.
     *
     * @return the sessionBean
     */
    public String getSessionBean() {
        return mSessionBean;
    }

    /**
     * Sets session.
     *
     * @param pSessionBean the sessionBean to set
     */
    public void setSessionBean(String pSessionBean) {
        this.mSessionBean = pSessionBean;
    }

    /**
     * Gets Common Helper instance.
     *
     * @return the commonHelper
     */
    public CommonHelper getCommonHelper() {
        return mCommonHelper;
    }

    /**
     * Sets Common Helper instance.
     *
     * @param pCommonHelper the commonHelper to set
     */
    public void setCommonHelper(CommonHelper pCommonHelper) {
        this.mCommonHelper = pCommonHelper;
    }

    /**
     * Check if Primary Address was changed. If yes - set NeedExperianNotify in
     * true.
     */
    private void checkChangesPrimaryAddress() {
        getNewsletterProfile().setNeedExperianNotify(true);
    }

    /**
     * Gets Profile Prefix.
     *
     * @return the mPrefix
     */
    public String getPrefix() {
        if ((mPrefix == null) && (getProfile() != null)) {
            mPrefix = (String) getProfile().getPropertyValue(TITLE_PROFILE_PROP);
        }
        return mPrefix;
    }

    /**
     * Sets Profile Prefix.
     *
     * @param pPrefix the mPrefix to set
     */
    public void setPrefix(String pPrefix) {
        mPrefix = pPrefix;
    }

    /**
     * Gets Profile First Name.
     *
     * @return the mFirstName
     */
    public String getFirstName() {
        if ((mFirstName == null) && (getProfile() != null)) {
            mFirstName = (String) getProfile().getPropertyValue(FIRST_NAME_PROFILE_PROP);
        }
        return mFirstName;
    }

    /**
     * Sets Profile First Name.
     *
     * @param pFirstName the mFirstName to set
     */
    public void setFirstName(String pFirstName) {
        mFirstName = pFirstName;
    }

    /**
     * Gets Profile Last name.
     *
     * @return the mLastName
     */
    public String getLastName() {
        if ((mLastName == null) && (getProfile() != null)) {
            mLastName = (String) getProfile().getPropertyValue(LAST_NAME_PROFILE_PROP);
        }
        return mLastName;
    }

    /**
     * Gets Profile Last Name.
     *
     * @param pLastName the mLastName to set
     */
    public void setLastName(String pLastName) {
        mLastName = pLastName;
    }

    /**
     * Gets Profile Address 1.
     *
     * @return the mAddress1
     */
    public String getAddress1() {
        if (mAddress1 == null) {
            mAddress1 = getAddressProperty(ADDRESS_1_ADDRESS_PROP);
        }
        return mAddress1;
    }

    /**
     * Sets Profile Address 1.
     *
     * @param pAddress1 the mAddress1 to set
     */
    public void setAddress1(String pAddress1) {
        mAddress1 = pAddress1;
    }

    /**
     * Gets Profile Address 2.
     *
     * @return the mAddress2
     */
    public String getAddress2() {
        if (mAddress2 == null) {
            mAddress2 = getAddressProperty(ADDRESS_2_ADDRESS_PROP);
        }
        return mAddress2;
    }

    /**
     * Sets Profile Address 2.
     *
     * @param pAddress2 the mAddress2 to set
     */
    public void setAddress2(String pAddress2) {
        mAddress2 = pAddress2;
    }

    /**
     * Gets Profile Address 3.
     *
     * @return the mAddress3
     */
    public String getAddress3() {
        if (mAddress3 == null) {
            mAddress3 = getAddressProperty(ADDRESS_3_ADDRESS_PROP);
        }
        return mAddress3;
    }

    /**
     * Gets Profile Address 3.
     *
     * @param pAddress3 the mAddress3 to set
     */
    public void setAddress3(String pAddress3) {
        mAddress3 = pAddress3;
    }

    /**
     * Gets Profile Locality.
     *
     * @return the mLocality
     */
    public String getLocality() {
        if (mLocality == null) {
            mLocality = getAddressProperty(LOCALITY_ADDRESS_PROP);
        }
        return mLocality;
    }

    /**
     * Sets Profile Locality.
     *
     * @param pLocality the mLocality to set
     */
    public void setLocality(String pLocality) {
        mLocality = pLocality;
    }

    /**
     * Gets Profile Postal Code.
     *
     * @return the mPostalCode
     */
    public String getPostalCode() {
        if (mPostalCode == null) {
            mPostalCode = getAddressProperty(POSTAL_CODE_ADDRESS_PROP);
        }
        return mPostalCode;
    }

    /**
     * Sets Profile Postal Code.
     *
     * @param pPostalCode the mPostalCode to set
     */
    public void setPostalCode(String pPostalCode) {
        mPostalCode = pPostalCode;
    }

    /**
     * Gets Profile City.
     *
     * @return the mCity
     */
    public String getCity() {
        if (mCity == null) {
            mCity = getAddressProperty(CITY_ADDRESS_PROP);
        }
        return mCity;
    }

    /**
     * Sets Profile City.
     *
     * @param pCity the mCity to set
     */
    public void setCity(String pCity) {
        mCity = pCity;
    }

    /**
     * Gets Profile State.
     *
     * @return the mState
     */
    public String getState() {
        if (mState == null) {
            mState = getAddressProperty(STATE_ADDRESS_PROP);
        }
        return mState;
    }

    /**
     * Sets Profile State.
     *
     * @param pState the mState to set
     */
    public void setState(String pState) {
        mState = pState;
    }

    /**
     * Gets Profile Country.
     *
     * @return the mCountry
     */
    public String getCountry() {
        if (mCountry == null) {
            mCountry = getAddressProperty(COUNTRY_ADDRESS_PROP);
        }
        return mCountry;
    }

    /**
     * Sets Profile Country.
     *
     * @param pCountry the mCountry to set
     */
    public void setCountry(String pCountry) {
        mCountry = pCountry;
    }

    /**
     * Gets Profile Phone Number.
     *
     * @return the mPhoneNumber
     */
    public String getPhoneNumber() {
        if (mPhoneNumber == null) {
            mPhoneNumber = getAddressProperty(PHONE_NUMBER_ADDRESS_PROP);
        }
        return mPhoneNumber;
    }

    /**
     * Sets Profile Phone Number.
     *
     * @param pPhoneNumber the mPhoneNumber to set
     */
    public void setPhoneNumber(String pPhoneNumber) {
        mPhoneNumber = pPhoneNumber;
    }

    /**
     * Gets Profile Phone Number 2.
     *
     * @return the mPhoneNumber2
     */
    public String getPhoneNumber2() {
        if (mPhoneNumber2 == null) {
            mPhoneNumber2 = getAddressProperty(PHONE_NUMBER_2_ADDRESS_PROP);
        }
        return mPhoneNumber2;
    }

    /**
     * Sets Profile Phone Number 2.
     *
     * @param pPhoneNumber2 the mPhoneNumber2 to set
     */
    public void setPhoneNumber2(String pPhoneNumber2) {
        mPhoneNumber2 = pPhoneNumber2;
    }

    /**
     * Gets Current Local Store.
     *
     * @return the mCurrentLocalStore
     */
    public String getCurrentLocalStore() {
        if ((mCurrentLocalStore == null) && (getProfile() != null)) {
            RepositoryItem magasin = null;
            magasin = (RepositoryItem) getProfile().getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
            if (magasin != null) {
                mCurrentLocalStore = magasin.getRepositoryId();
            }
        }
        return mCurrentLocalStore;
    }

    /**
     * Sets Current Local Store.
     *
     * @param pCurrentLocalStore the mCurrentLocalStore to set
     */
    public void setCurrentLocalStore(String pCurrentLocalStore) {
        mCurrentLocalStore = pCurrentLocalStore;
        setUpdateCurrentLocalStore(pCurrentLocalStore);
    }

    /**
     * Gets updated value of Current Local Store.
     *
     * @return the mUpdateCurrentLocalStore
     */
    public String getUpdateCurrentLocalStore() {
        return mUpdateCurrentLocalStore;
    }

    /**
     * Sets updated value of Current Local Store.
     *
     * @param pUpdateCurrentLocalStore the mUpdateCurrentLocalStore to set
     */
    public void setUpdateCurrentLocalStore(String pUpdateCurrentLocalStore) {
        mUpdateCurrentLocalStore = pUpdateCurrentLocalStore;
    }

    /**
     * Returns storeTools property.
     * 
     * @return storeTools property.
     */
    public StoreTools getStoreTools() {
        return storeTools;
    }

    /**
     * Sets the value of the storeTools property.
     * 
     * @param storeTools
     *            parameter to set.
     */
    public void setStoreTools(StoreTools storeTools) {
        this.storeTools = storeTools;
    }   
    
    /**
     * Returns contextTools property.
     * 
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return contextTools;
    }

    /**
     * Sets the value of the contextTools property.
     * 
     * @param contextTools the contextTools to set
     */
    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }

    /**
     * Gets newsletterRepository
     *
     * @return the newsletterRepository
     */
    public Repository getNewsletterRepository() {
        return newsletterRepository;
    }

    /**
     * Sets newsletterRepository
     *
     * @param newsletterRepository the newsletterRepository to set
     */
    public void setNewsletterRepository(Repository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    /**
     * Get billing address properties.
     *
     * @param  pValue - address property name
     *
     * @return address property value
     */
    private String getAddressProperty(String pValue) {
        Profile profile = getProfile();
        String result = null;
        if (profile != null) {
            RepositoryItem billingAddress = (RepositoryItem) profile.getPropertyValue(BILLING_ADDRESS);
            if (billingAddress != null) {
                result = (String) billingAddress.getPropertyValue(pValue);
            }
        }
        return result;
    }

    /**
     * Gets redirect url.
     *
     * @return the redirectURL
     */
    public String getRedirectURL() {
        return mRedirectURL;
    }

    /**
     * Sets redirect url.
     *
     * @param pRedirectURL the redirectURL to set
     */
    public void setRedirectURL(String pRedirectURL) {
        this.mRedirectURL = pRedirectURL;
    }

    /**
     * Gets redirect URL for login 'on behalf'.
     *
     * @return the mOnBehalfURL
     */
    public String getOnBehalfURL() {
        return mOnBehalfURL;
    }

    /**
     * Sets redirect URL for login 'on behalf'.
     *
     * @param pOnBehalfURL the mOnBehalfURL to set
     */
    public void setOnBehalfURL(String pOnBehalfURL) {
        mOnBehalfURL = pOnBehalfURL;
    }

    public String getCastoramaCard() {
        Profile profile = getProfile();
        if (profile != null) {
            if (!profile.isTransient()) {
                Object castoramaCardObject = profile.getPropertyValue(CARTE_ATOUT_PROFILE_PROP);
                if (castoramaCardObject != null) {
                    castoramaCard = Boolean.toString((Boolean) castoramaCardObject);
                } else {
                    castoramaCard = null;
                }
            }
        }
        return castoramaCard;
    }

    public void setCastoramaCard(String castoramaCard) {
        if (castoramaCard != null && !castoramaCard.isEmpty()) {
            this.castoramaCard = castoramaCard;
        } else {
            this.castoramaCard = null;
        }
    }

    public String getCastoramaCardNumber() {
        boolean isCastoramaCard = Boolean.parseBoolean(getCastoramaCard());
        if (!isCastCardNumberCorrect() || !isCastoramaCard) {
            Profile profile = getProfile();
            if (profile != null) {
                if (isCastoramaCard) {
                    castoramaCardNumber = (String) profile.getPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP);
                } else {
                    castoramaCardNumber = null;
                }
            }
        }

        return castoramaCardNumber;
    }

    public void setCastoramaCardNumber(String castoramaCardNumber) {
        this.castoramaCardNumber = castoramaCardNumber;
    }
    
    public boolean isCastCardNumberCorrect() {
        return castCardNumberCorrect;
    }

    public void setCastCardNumberCorrect(boolean castCardNumberCorrect) {
        this.castCardNumberCorrect = castCardNumberCorrect;
    }

    public Repository getProfileRepository() {
        return profileRepository;
    }

    public void setProfileRepository(Repository profileRepository) {
        this.profileRepository = profileRepository;
    }
    
    public String getUpdatePasswordSuccessURL() {
        return updatePasswordSuccessURL;
    }

    public void setUpdatePasswordSuccessURL(String updatePasswordSuccessURL) {
        this.updatePasswordSuccessURL = updatePasswordSuccessURL;
    }

    public String getUpdatePasswordErrorURL() {
        return updatePasswordErrorURL;
    }

    public void setUpdatePasswordErrorURL(String updatePasswordErrorURL) {
        this.updatePasswordErrorURL = updatePasswordErrorURL;
    }
}
