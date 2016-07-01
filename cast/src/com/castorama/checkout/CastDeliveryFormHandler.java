package com.castorama.checkout;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADMIN_LOGIN_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_PASSWORD_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_ROLE;
import static com.castorama.commerce.profile.Constants.CARTE_ATOUT_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.CARTE_ATOUT_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.CAST_CARD_NUMBER_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.DEFAULT_COUNTRY_NAME;
import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.FIRST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.NEWSLETTER_ITEM_DESC;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PREFIX_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.Constants.STATE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.TITLE_PROFILE_PROP;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_ERROR_MISSED_MAIN_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_ERROR_UPDATE_ORDER;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_LIGHT_USER_SPECIFY_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_LIGHT_USER_SPECIFY_NEW_ADDRESS;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceEvent;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.address.AddressTools;

import com.castorama.RollbackFunctionalityConfiguration;
import com.castorama.commerce.order.CastHardgoodShippingGroup;
import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.profile.CastAddressHelper;
import com.castorama.commerce.profile.CastProfileFormHandler;
import com.castorama.commerce.profile.CastProfileTools;
import com.castorama.commerce.profile.SessionBean;
import com.castorama.utils.CastLoggingHelper;
import com.castorama.utils.CommonHelper;

/**
 * Extends ShippingGroupFormHandler to add custom logic for Castorama.
 * 
 * @author Epam Team
 */
public class CastDeliveryFormHandler extends ShippingGroupFormHandler {
    /** COMMENTAIRE constant */
    private static final String COMMENTAIRE = "commentaire";

    /** Action name for adding address command. */
    private static final String NEW_ACTION = "New";

    /** Action name for assign address command. */
    private static final String ASSIGN_ACTION = "Assign";

    /** Action name for C&C delivery order for update primary address and assign  it command. */
    private static final String UPDATE_PRIMARY_AND_ASSIGN_ACTION = "UpdatePrAddr";

    /** Action name for back command. */
    private static final String BACK_ACTION = "Back";

    /** ACTION_SUCCESS_URL constant. */
    private static final String ACTION_SUCCESS_URL = "delivery.jsp?error=modifierAdresse";

    /** Action name for delete address command. */
    private static final String DELETE_ACTION = "Delete";

    /** Action name for modify address command. */
    private static final String MODIFY_ACTION = "Modify";

    /** max length for gift message. */
    private static int MAX_GIFT_MESSAGE_LENGTH = 200;

    /** MSG_REMOVE_ILLEGAL_ITEMS constant */
    public static final String MSG_REMOVE_ILLEGAL_ITEMS = "msgRemoveIllegalItems";

    /** Resource bundle name. */
    private static final String MY_RESOURCE_NAME = "com.castorama.checkout.DeliveryResources";

    /** Prefix for new address name. */
    private static final String NEW_ADDRESS_NAME_PREFIX = "Adresse ";

    /** form submit action */
    private String mAction;

    /** Address values map. */
    private Map<String, String> editValue = new HashMap<String, String>();

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

    /** address name */
    private String mAddressName;

    /** Address Helper. */
    private CastAddressHelper mAddressHelper;

    /** Address tool. */
    private AddressTools mAddressTools;

    /** URL for success action. */
    public String mActionSuccessURL;

    /** URL for error action. */
    public String mActionErrorURL;

    /** URL for success assign action. */
    public String mAssignSuccessURL;

    /** URL for success modify action. */
    private String mModifySuccessURL;

    /** URL for success delete action. */
    private String mDeleteSuccessURL;

    /** Customer Comment. */
    private String mCommentaire;

    /** Order Gift. */
    private boolean mCommandeCadeau;

    /** Current session. */
    private String sessionBean;

    /** Common helper component. */
    private CommonHelper commonHelper;

    /** Profile Tools. */
    private CastProfileTools profileTools;

    /** profile handler property */
    private CastProfileFormHandler profileHandler;

    /** mRollbackFunctionalityConfiguration property. */
    private RollbackFunctionalityConfiguration mRollbackFunctionalityConfiguration;

    /** clientLockManager property */
    protected ClientLockManager mClientLockManager;
    private CastLoggingHelper castLoggingHelper;
    private String castoramaCardValue;
    private boolean castoramaCardCorrect;

    /**
     * This is called after a Service has been created, placed into the naming hierarchy,
     * and initialized with its configured property values. 
     * The Service should override this method to start any processes it requires.
     * 
     * We check if Billing address of the user is empty and show message if is
     */
    @Override
    public void doStartService() throws ServiceException {
        super.doStartService();

        RepositoryItem profile = getProfile();
        if (getAddressHelper() != null) {
            final CommercePropertyManager cpmgr = getAddressHelper().getCommercePropertyManager();

            RepositoryItem shippingAddress = (RepositoryItem) profile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
            
            Object stateProperty = null;
            if (shippingAddress != null)
                stateProperty = shippingAddress.getPropertyValue(STATE_ADDRESS_PROP);
            
            if (stateProperty == null && !isClickAndCollectDelivery()) {
                // state is null only for light user
                if (isLoggingInfo()) {
                    logInfo("doStartService: Billing Address is empty ");
                }
                getCommonHelper().generateFormException(
                        MSG_ERROR_MISSED_MAIN_ADDRESS, this, RESOURCE_BUNDLE);
            }
        }
    }

    /**
     * Return the profileHandler property.
     * 
     * @return profileHandler property.
     */
    public CastProfileFormHandler getProfileHandler() {
        return profileHandler;
    }

    /**
     * Set the profileHandler property.
     * 
     * @param pProfileHandler
     */
    public void setProfileHandler(CastProfileFormHandler pProfileHandler) {
        profileHandler = pProfileHandler;
    }

    /**
     * Returns flagErrorCp property
     * 
     * @return the mFlagErrorCp
     */
    public boolean isFlagErrorCp() {
        return getAddressHelper().isFlagErrorCp();
    }

    /**
     * Sets Flag for code postal error.
     * 
     * @param flagErrorCp the mFlagErrorCp to set
     */
    public void setFlagErrorCp(boolean flagErrorCp) {
        getAddressHelper().setFlagErrorCp(flagErrorCp);
    }

    /**
     * Returns commentaire - Customer Comment - property
     * 
     * @return the commentaire
     */
    public String getCommentaire() {
        if ((mCommentaire == null) && (editValue != null)) {
            mCommentaire = editValue.get(COMMENTAIRE);
        }
        return mCommentaire;
    }

    /**
     * Sets Customer Comment property
     * 
     * @param pCommentaire the commentaire to set
     */
    public void setCommentaire(String pCommentaire) {
        if (editValue == null) {
            editValue = new HashMap<String, String>();
        }
        editValue.put(COMMENTAIRE, pCommentaire);
        mCommentaire = pCommentaire;
    }

    /**
     * Returns property
     * 
     * @return the commandeCadeau
     */
    public boolean isCommandeCadeau() {
        if (!StringUtils.isEmpty(getCommentaire())) {
            mCommandeCadeau = true;
        }
        return mCommandeCadeau;
    }

    /**
     * Sets property
     * 
     * @param pCommandeCadeau the commandeCadeau to set
     */
    public void setCommandeCadeau(boolean pCommandeCadeau) {
        mCommandeCadeau = pCommandeCadeau;
    }

    /**
     * Returns action property
     * 
     * @return action property
     */
    public String getAction() {
        return mAction;
    }

    /**
     * Sets action property
     * 
     * @param pAction parameter to set
     */
    public void setAction(String pAction) {
        this.mAction = pAction;
    }

    /**
     * Returns addressName property
     * 
     * @return addressName property
     */
    public String getAddressName() {
        return mAddressName;
    }

    /**
     * Sets addressName property
     * 
     * @param pAddressName parameter to set
     */
    public void setAddressName(String pAddressName) {
        this.mAddressName = pAddressName;
    }

    /**
     * Returns addressHelper property
     * 
     * @return addressHelper property
     */
    public CastAddressHelper getAddressHelper() {
        return mAddressHelper;
    }

    /**
     * Sets addressHelper property
     * 
     * @param pAddressHelper parameter to set
     */
    public void setAddressHelper(CastAddressHelper pAddressHelper) {
        this.mAddressHelper = pAddressHelper;
    }

    /**
     * Returns assignSuccessURL property
     * 
     * @return assignSuccessURL property
     */
    public String getAssignSuccessURL() {
        return mAssignSuccessURL;
    }

    /**
     * Sets assignSuccessURL property
     * 
     * @param pAssignSuccessURL parameter to set
     */
    public void setAssignSuccessURL(String pAssignSuccessURL) {
        this.mAssignSuccessURL = pAssignSuccessURL;
    }

    /**
     * Returns modifySuccessURL property
     * 
     * @return modifySuccessURL property
     */
    public String getModifySuccessURL() {
        return mModifySuccessURL;
    }

    /**
     * Sets modifySuccessURL property
     * 
     * @param pModifySuccessURL parameter to set
     */
    public void setModifySuccessURL(String pModifySuccessURL) {
        this.mModifySuccessURL = pModifySuccessURL;
    }

    /**
     * Returns deleteSuccessURL property
     * 
     * @return deleteSuccessURL property
     */
    public String getDeleteSuccessURL() {
        return mDeleteSuccessURL;
    }

    /**
     * Sets deleteSuccessURL property
     * 
     * @param pDeleteSuccessURL parameter to set
     */
    public void setDeleteSuccessURL(String pDeleteSuccessURL) {
        this.mDeleteSuccessURL = pDeleteSuccessURL;
    }

    /**
     * Returns actionSuccessURL property
     * 
     * @return actionSuccessURL property
     */
    public String getActionSuccessURL() {
        return mActionSuccessURL;
    }

    /**
     * Sets actionSuccessURL property
     * 
     * @param pActionSuccessURL parameter to set
     */
    public void setActionSuccessURL(String pActionSuccessURL) {
        mActionSuccessURL = pActionSuccessURL;
    }

    /**
     * Returns actionErrorURL property
     * 
     * @return actionErrorURL property
     */
    public String getActionErrorURL() {
        return mActionErrorURL;
    }

    /**
     * Sets actionErrorURL property
     * 
     * @param pActionErrorURL parameter to set
     */
    public void setActionErrorURL(String pActionErrorURL) {
        mActionErrorURL = pActionErrorURL;
    }

    /**
     * Returns addressTools property
     * 
     * @return addressTools property
     */
    public AddressTools getAddressTools() {
        return mAddressTools;
    }

    /**
     * Sets addressTools property
     * 
     * @param pAddressTools parameter to set
     */
    public void setAddressTools(AddressTools pAddressTools) {
        this.mAddressTools = pAddressTools;
    }

    /**
     * Returns prefix property
     * 
     * @return prefix property
     */
    public String getPrefix() {
        return mPrefix;
    }

    /**
     * Sets prefix property
     * 
     * @param pPrefix parameter to set
     */
    public void setPrefix(String pPrefix) {
        this.mPrefix = pPrefix;
    }

    /**
     * Returns firstName property
     * 
     * @return firstName property
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Sets firstName property
     * 
     * @param pFirstName parameter to set
     */
    public void setFirstName(String pFirstName) {
        this.mFirstName = pFirstName;
    }

    /**
     * Returns lastName property
     * 
     * @return lastName property
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Sets lastName property
     * 
     * @param pLastName parameter to set
     */
    public void setLastName(String pLastName) {
        this.mLastName = pLastName;
    }

    /**
     * Returns address1 property
     * 
     * @return address1 property
     */
    public String getAddress1() {
        return mAddress1;
    }

    /**
     * Sets address1 property
     * 
     * @param pAddress1 parameter to set
     */
    public void setAddress1(String pAddress1) {
        this.mAddress1 = pAddress1;
    }

    /**
     * Returns address2 property
     * 
     * @return address2 property
     */
    public String getAddress2() {
        return mAddress2;
    }

    /**
     * Sets address2 property
     * 
     * @param pAddress2 parameter to set
     */
    public void setAddress2(String pAddress2) {
        this.mAddress2 = pAddress2;
    }

    /**
     * Returns address3 property
     * 
     * @return address3 property
     */
    public String getAddress3() {
        return mAddress3;
    }

    /**
     * Sets address3 property
     * 
     * @param pAddress3 parameter to set
     */
    public void setAddress3(String pAddress3) {
        this.mAddress3 = pAddress3;
    }

    /**
     * Returns locality property
     * 
     * @return locality property
     */
    public String getLocality() {
        return mLocality;
    }

    /**
     * Sets locality property
     * 
     * @param pLocality parameter to set
     */
    public void setLocality(String pLocality) {
        this.mLocality = pLocality;
    }

    /**
     * Returns postalCode property
     * 
     * @return postalCode property
     */
    public String getPostalCode() {
        return mPostalCode;
    }

    /**
     * Sets postalCode property
     * 
     * @param pPostalCode parameter to set
     */
    public void setPostalCode(String pPostalCode) {
        this.mPostalCode = pPostalCode;
    }

    /**
     * Returns city property
     * 
     * @return city property
     */
    public String getCity() {
        return mCity;
    }

    /**
     * Sets city property
     * 
     * @param pCity parameter to set
     */
    public void setCity(String pCity) {
        this.mCity = pCity;
    }

    /**
     * Returns country property
     * 
     * @return country property
     */
    public String getCountry() {
        return mCountry;
    }

    /**
     * Sets country property
     * 
     * @param pCountry parameter to set
     */
    public void setCountry(String pCountry) {
        this.mCountry = pCountry;
    }

    /**
     * Returns phoneNumber property
     * 
     * @return phoneNumber property
     */
    public String getPhoneNumber() {
        return (null == mPhoneNumber) ? null : mPhoneNumber.trim();
    }

    /**
     * Sets phoneNumber property
     * 
     * @param pPhoneNumber parameter to set
     */
    public void setPhoneNumber(String pPhoneNumber) {
        this.mPhoneNumber = pPhoneNumber;
    }

    /**
     * Returns phoneNumber2 property
     * 
     * @return phoneNumber2 property
     */
    public String getPhoneNumber2() {
        return (null == mPhoneNumber2) ? null : mPhoneNumber2.trim();
    }

    /**
     * Sets phoneNumber2 property
     * 
     * @param pPhoneNumber2 parameter to set
     */
    public void setPhoneNumber2(String pPhoneNumber2) {
        this.mPhoneNumber2 = pPhoneNumber2;
    }

    /**
     * Returns state property
     * 
     * @return the state
     */
    public String getState() {
        return mState;
    }

    /**
     * Sets state property
     * 
     * @param pState the mState to set
     */
    public void setState(String pState) {
        mState = pState;
    }

    /**
     * Returns rollbackFunctionalityConfiguration property.
     * 
     * @return rollbackFunctionalityConfiguration property.
     */
    public RollbackFunctionalityConfiguration getRollbackFunctionalityConfiguration() {
        return mRollbackFunctionalityConfiguration;
    }

    /**
     * Sets the value of the rollbackFunctionalityConfiguration property.
     * 
     * @param pRollbackFunctionalityConfiguration parameter to set.
     */
    public void setRollbackFunctionalityConfiguration(RollbackFunctionalityConfiguration pRollbackFunctionalityConfiguration) {
        mRollbackFunctionalityConfiguration = pRollbackFunctionalityConfiguration;
    }

    /**
     * Returns clientLockManager property.
     * 
     * @return clientLockManager property.
     */
    public ClientLockManager getClientLockManager() {
        return mClientLockManager;
    }

    /**
     * Sets the value of the clientLockManager property.
     * 
     * @param pClientLockManager parameter to set.
     */
    public void setClientLockManager(ClientLockManager pClientLockManager) {
        mClientLockManager = pClientLockManager;
    }

    /**
     * Returns address properties.
     * 
     * @return address parameter
     */
    public Map<String, String> getAddress() {
        editValue = new HashMap<String, String>();
        editValue.put(PREFIX_ADDRESS_PROP, getPrefix());
        editValue.put(FIRST_NAME_ADDRESS_PROP, getFirstName());
        editValue.put(LAST_NAME_ADDRESS_PROP, getLastName());
        editValue.put(ADDRESS_1_ADDRESS_PROP, getAddress1());
        editValue.put(ADDRESS_2_ADDRESS_PROP, getAddress2());
        editValue.put(ADDRESS_3_ADDRESS_PROP, getAddress3());
        editValue.put(LOCALITY_ADDRESS_PROP, getLocality());
        editValue.put(POSTAL_CODE_ADDRESS_PROP, getPostalCode());
        editValue.put(CITY_ADDRESS_PROP, getCity());
        editValue.put(STATE_ADDRESS_PROP, getState());       
        String country = getCountry();
        if ((country == null) || ((country != null) && (country.length() == 0))) {
            country = DEFAULT_COUNTRY_NAME;
            setCountry(country);
            editValue.put(COUNTRY_ADDRESS_PROP, getCountry());
        }
        editValue.put(COUNTRY_ADDRESS_PROP, country);   
        editValue.put(PHONE_NUMBER_ADDRESS_PROP, getPhoneNumber());
        editValue.put(PHONE_NUMBER_2_ADDRESS_PROP, getPhoneNumber2());

        return editValue;
    }

    /**
     * Set address: set address properties from <code>address</code>.
     * 
     * @param address - map with address values
     */
    public void setAddress(Map<String, String> address) {
        this.editValue = address;
        if (address != null) {
            String prefix = address.get(PREFIX_ADDRESS_PROP);
            if (prefix == null) {
                prefix = address.get(TITLE_PROFILE_PROP);
            }
            setPrefix(prefix);
            setFirstName(address.get(FIRST_NAME_ADDRESS_PROP));
            setLastName(address.get(LAST_NAME_ADDRESS_PROP));
            setAddress1(address.get(ADDRESS_1_ADDRESS_PROP));
            setAddress2(address.get(ADDRESS_2_ADDRESS_PROP));
            setAddress3(address.get(ADDRESS_3_ADDRESS_PROP));
            setLocality(address.get(LOCALITY_ADDRESS_PROP));
            setPostalCode(address.get(POSTAL_CODE_ADDRESS_PROP));
            setCity(address.get(CITY_ADDRESS_PROP));
            setState(address.get(STATE_ADDRESS_PROP));
            setCountry(address.get(COUNTRY_ADDRESS_PROP));
            setPhoneNumber(address.get(PHONE_NUMBER_ADDRESS_PROP));
            setPhoneNumber2(address.get(PHONE_NUMBER_2_ADDRESS_PROP));
        }
    }

    /**
     * Handler for form action.
     * 
     * @param pRequest - http request
     * @param pResponse - http response
     * 
     * @return true if success, false - otherwise
     * 
     * @throws ServletException if servlet error occurs
     * @throws IOException if IO error occurs
     */
    public boolean handleAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        Transaction tr = null;
        try {
            Order order = getOrder();
            if (order == null) {
                if (isLoggingError()) {
                    OrderHolder shoppingCart = getShoppingCart();
                    logError("getShoppingCart(): " + shoppingCart +
                            ", shoppingCart instanceof CastOrderHolder: " + (shoppingCart instanceof CastOrderHolder) +
                            ", getProfile(): " + getProfile() +
                            ", " + (shoppingCart instanceof CastOrderHolder ? "" + ((CastOrderHolder) shoppingCart).getSelectedOrder()
                            : "not instance of CastOrderHolder"));
                }
            }
            synchronized (order) {
                tr = ensureTransaction();
                List commerceItems = order.getCommerceItems();
                if ((commerceItems == null) || ((commerceItems != null) && (commerceItems.size() == 0))) {
                    redirectOrForward(pRequest, pResponse, (getActionErrorURL() == null) ? "" : getActionErrorURL());
                    return false;
                }
                if (!checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse)) {
                    return false;
                }
                String action = getAction();
                boolean next = preAction(pRequest, pResponse, action, order);
                if (getFormError()) {
                    return checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                }
                if (next) {
                    try {
                        if (order != null) {
                            boolean[] removalAndNotEnoughResult = ((CastOrderTools) getOrderManager().getOrderTools())
                                    .removeCommerceItemIds(order);
                            if (removalAndNotEnoughResult[0]) {
                                String msg = formatUserMessage(MSG_REMOVE_ILLEGAL_ITEMS, pRequest, pResponse);
                                addFormException(new DropletException(msg, null, MSG_REMOVE_ILLEGAL_ITEMS));
                            }
                            if (removalAndNotEnoughResult[1]) {
                                addFormException(new DropletException(null));
                            }
                            if (getFormError()) {
                                setActionErrorURL(getCancelURL());
                            }
                        }
                        if (getFormError()) {
                            return checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                        }

                        action(pRequest, pResponse, action, order);
                        if (getFormError()) {
                            return checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                        }
                        preProcessingLogOrderInfo(getOrder().getId());
                        runProcessValidateShippingGroups(getOrder(), getUserPricingModels(),
                                getUserLocale(pRequest, pResponse), getProfile(), null);

                        runProcess("payboxProcessOrder1", getOrder(), getUserPricingModels(),
                                getUserLocale(pRequest, pResponse), getProfile(), null);
                    } catch (Exception e) {
                        if (isLoggingError()) {
                            logError(e);
                        }
                        getCommonHelper().generateFormException(MSG_ERROR_UPDATE_ORDER, this, MY_RESOURCE_NAME);
                        checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                    } // end try-catch
                    postAction(pRequest, pResponse, action);
                    if (getFormError()) {
                        return checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                    }
                    try {
                        getOrderManager().updateOrder(getOrder());
                    } catch (Exception exc) {
                        if (isLoggingError()) {
                            logError(exc);
                        }
                        getCommonHelper().generateFormException(MSG_ERROR_UPDATE_ORDER, this, MY_RESOURCE_NAME);
                        return checkFormRedirect(null, getActionErrorURL(), pRequest, pResponse);
                    }
                    postProcessingLogOrderInfo(getOrder().getId());

                } // end if
                if (isTransactionMarkedAsRollBack()) {
                    getCommonHelper().generateFormException(MSG_ERROR_UPDATE_ORDER, this, MY_RESOURCE_NAME);
                }
                tr = closeTransaction(tr);
            }
            return checkFormRedirect(getActionSuccessURL(), getActionErrorURL(), pRequest, pResponse);
        } finally {
            closeTransaction(tr);
        } // end try-finally
    }

    /**
     * Validates and updates address block.
     * 
     * @param pRequest
     * @param pResponse
     * 
     * @return
     * 
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdatePrimaryAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        final Map values = convertPrimaryAddress();

        return getProfileHandler().updatePrimaryAddress(pRequest, pResponse, values);
        // return true;

    }

    /**
     * Create map with address properties.
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
     * Pre form action.
     * 
     * @param pRequest - http request.
     * @param pResponse - http response.
     * @param pAction - action name.
     * @param pOrder - order
     * 
     * @return true if next operations needed
     * 
     * @throws ServletException if servlet error occurs
     * @throws IOException if IO error occurs
     */
    protected boolean preAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pAction,
            Order pOrder) throws ServletException, IOException {
        boolean next = true;
        if (null != pAction) {
            if (MODIFY_ACTION.equals(pAction)) {
                preModifyAction(pRequest, pResponse, pOrder);
                next = false;
            } else if (DELETE_ACTION.equals(pAction)) {
                preDeleteAction(pRequest, pResponse, pOrder);
                next = false;
            } else if (BACK_ACTION.equals(pAction)) {
                preBackAction(pRequest, pResponse, pOrder);
                next = false;
            }
        }
        return next;
    }

    /**
     * Update the secondary address as modified by the user.
     * 
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * 
     * @return <code>false</code> if redirect is successful, <code>
     *            true</code> otherwise.
     * 
     * @exception ServletException if there was an error while executing the
     *                code
     * @exception IOException if there was an error with servlet io
     */
    public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        boolean result = false;
        saveGiftMessage(getOrder());
        Map<String, String> address = getAddress();
        if (!getAddressHelper().validateNameAddress(address, this) ||
                !getAddressHelper().validateAddress(address, this) ||
                !getAddressHelper().validatePhone(address, this)) {
            return checkFormRedirect(getActionErrorURL(), getActionErrorURL(), pRequest, pResponse);
        }
        if (!getFormError() && getAddressHelper().updateAddress(address, this, getAddressName())) {
            handleCancel(pRequest, pResponse);
            result = checkFormRedirect(getActionSuccessURL(), getActionErrorURL(), pRequest, pResponse);
        } else {
            result = checkFormRedirect(getActionErrorURL(), getActionErrorURL(), pRequest, pResponse);
        }
        return result;
    }

    public void handleUpdateCastoramaCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        saveCastoramaCardNumber(getOrder());
    }

    /**
     * Form action.
     * 
     * @param pRequest - http request
     * @param pResponse - http response
     * @param pAction - action
     * @param order - oreder
     * 
     * @throws ServletException if servlet error occurs
     * @throws IOException if IO error occurs
     */
    protected void action(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pAction,
            Order order) throws ServletException, IOException {
        if (null != pAction) {
            if (ASSIGN_ACTION.equals(pAction)) {
                assignAction(pRequest, pResponse, order);
            } else if (NEW_ACTION.equals(pAction)) {
                newAction(pRequest, pResponse, order);
            } else if (UPDATE_PRIMARY_AND_ASSIGN_ACTION.equals(pAction)) {
                updatePrimaryAndAssignAction(pRequest, pResponse, order);
            }
        }
    }

    /**
     * Post form action.
     * 
     * @param pRequest - http request
     * @param pResponse - http response
     * @param pAction - action
     * 
     * @throws ServletException if servlet error occurs
     * @throws IOException if IO error occurs
     */
    protected void postAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pAction)
            throws ServletException, IOException {
    }

    /**
     * Pre action for "Modify" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param pOrder order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void preModifyAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
            Order pOrder) throws ServletException, IOException {
        // final CommercePropertyManager cpmgr = getAddressHelper().getCommercePropertyManager();
        // RepositoryItem profile = getProfile();
        Map<String, String> address = getAddress();
        String addressName = getAddressName();

        if ((null == addressName) || (0 == addressName.length())) {
            // getProfileFormHandler().setAddressValues(null,
            // (RepositoryItem)profile.getPropertyValue(cpmgr.getShippingAddressPropertyName()));
            setActionSuccessURL(ACTION_SUCCESS_URL);
        } else {
            // Map addresses = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
            // getProfileFormHandler().setAddressValues(addressName, (RepositoryItem) addresses.get(addressName));
            if (getAddressHelper().editAddress(addressName, address)) {
                setAddress(address);
            } else {
                checkFormRedirect(getActionErrorURL(), getActionErrorURL(), pRequest, pResponse);
            }
        }
        saveGiftMessage(pOrder);
    }

    /**
     * Pre action for "Back" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param pOrder order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void preBackAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Order pOrder)
            throws ServletException, IOException {
        setActionSuccessURL(getCancelURL());
    }

    /**
     * Pre action for "Delete" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param pOrder order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void preDeleteAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
            Order pOrder) throws ServletException, IOException {
        getAddressHelper().removeAddress(getAddressName());
        setActionSuccessURL(getDeleteSuccessURL());
        saveGiftMessage(pOrder);
    }

    /**
     * Action for "Assign" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param order order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void assignAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Order order)
            throws ServletException, IOException {
        try {
            saveGiftMessage(order);
            
            saveCastoramaCardNumber(order);
            
            RepositoryItem profile = getProfile();
            
            final CommercePropertyManager cpmgr = getAddressHelper().getCommercePropertyManager();
            RepositoryItem shippingAddress = (RepositoryItem) profile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
            
            Object stateProperty = null;
            if (shippingAddress != null)
                stateProperty = shippingAddress.getPropertyValue(STATE_ADDRESS_PROP);
            
            if (stateProperty == null) {
                if (isClickAndCollectDelivery()) {
                    getCommonHelper().generateFormException(MSG_ERROR_MISSED_MAIN_ADDRESS, this, RESOURCE_BUNDLE);
                } else {
                    // state is null only for light user
                    getCommonHelper().generateFormException(MSG_LIGHT_USER_SPECIFY_ADDRESS, this, RESOURCE_BUNDLE);
                }
            } else {
                String addressName = getAddressName();
                if (!((null == addressName) || (0 == addressName.length()))) {
                    Map addresses = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
                    shippingAddress = (RepositoryItem) addresses.get(addressName);
                }
                if (checkCountry(shippingAddress)) {
                    if (order.getShippingGroups().isEmpty()) {
                        ShippingGroupManager sgm = getShippingGroupManager();
                        CastHardgoodShippingGroup hgsg = (CastHardgoodShippingGroup) sgm.createShippingGroup();
                        setShippingAddress(hgsg, shippingAddress, addressName);
                        addShippingGroupToMap(hgsg, addressName);
                    } else {
                        for (Iterator i = order.getShippingGroups().iterator(); i.hasNext();) {
                            Object item = i.next();
                            if (item instanceof HardgoodShippingGroup) {
                                CastHardgoodShippingGroup hgsg = (CastHardgoodShippingGroup) item;
                                setShippingAddress(hgsg, shippingAddress, addressName);
                            }
                        }
                    }
                }
            }

            if (order instanceof CastOrderImpl) {
                ((CastOrderImpl) order).setCommandeCadeau(isCommandeCadeau());
                String commentaire = getCommentaire();
                if (commentaire != null && commentaire.length() >= MAX_GIFT_MESSAGE_LENGTH) {
                    commentaire = commentaire.substring(0, MAX_GIFT_MESSAGE_LENGTH);
                }
                ((CastOrderImpl) order).setCommentaire(commentaire);

                final SessionBean sb = (SessionBean) pRequest.resolveName(getSessionBean());
                final String adminLogin = (String) sb.getValues().get(ADMIN_LOGIN_SESSION_PARAM);
                final String adminPassword = (String) sb.getValues().get(ADMIN_PASSWORD_SESSION_PARAM);

                if ((adminLogin != null) && (adminLogin.trim().length() > 0) && (adminPassword != null) &&
                        getProfileTools().checkRole(adminLogin, ADMIN_ROLE)) {
                    ((CastOrderImpl) order).setCdeAdmin(true);
                    ((CastOrderImpl) order).setAdminLogin(adminLogin);
                } else {
                    ((CastOrderImpl) order).setCdeAdmin(false);
                }
            } // end if
            getOrderManager().updateOrder(order);

        } catch (ClassCastException e) {
            if (isLoggingError()) {
                logError("Wrong class cast " + e.getMessage(), e);
            }
        } catch (CommerceException e) {
            if (isLoggingError()) {
                logError("Wrong mark as gift " + e.getMessage(), e);
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e.getMessage(), e);
            }
        } // end try-catch
        setActionSuccessURL(getAssignSuccessURL());

    }

    /**
     * Action for "New" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param order order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void newAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Order order)
            throws ServletException, IOException {
        try {
            saveGiftMessage(order);
            saveCastoramaCardNumber(order);
            if (getAddressHelper().validateCountry(getState(), this)) {
                final CommercePropertyManager cpmgr = getAddressHelper().getCommercePropertyManager();
                RepositoryItem profile = getProfile();

                RepositoryItem shippingAddress = (RepositoryItem) profile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
                
                Object stateProperty = null;
                if (shippingAddress != null)
                    stateProperty = shippingAddress.getPropertyValue(STATE_ADDRESS_PROP);
                
                if (stateProperty == null) {
                    // state is null only for light user
                    getCommonHelper().generateFormException(MSG_LIGHT_USER_SPECIFY_NEW_ADDRESS, this, RESOURCE_BUNDLE);
                }

                Map addresses = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
                String addressName = getAddressName();
                if ((null == addressName) || (0 == addressName.length())) {
                    addressName = NEW_ADDRESS_NAME_PREFIX + (addresses.size() + 1);
                }
                if (getAddressHelper().addAddress(this, addressName, getAddress())) {
                    addresses = (Map) profile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
                    Object value = addresses.get(addressName);
                    if (value instanceof RepositoryItem) {
                        RepositoryItem address = (RepositoryItem) value;

                        if (order.getShippingGroups().isEmpty()) {
                            ShippingGroupManager sgm = getShippingGroupManager();
                            CastHardgoodShippingGroup hgsg = (CastHardgoodShippingGroup) sgm.createShippingGroup();
                            if (null != address) {
                                setShippingAddress(hgsg, address, addressName);
                            }
                            addShippingGroupToMap(hgsg, addressName);
                        } else {
                            if (null != address) {
                                for (Iterator i = order.getShippingGroups().iterator(); i.hasNext();) {
                                    Object item = i.next();
                                    if (item instanceof CastHardgoodShippingGroup) {
                                        setShippingAddress((CastHardgoodShippingGroup) item, address, addressName);
                                    }
                                }
                            }
                        }
                    } // end if
                } // end if
            } // end if

        } catch (ClassCastException e) {
            if (isLoggingError()) {
                logError("Wrong class cast " + e.getMessage(), e);
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e.getMessage(), e);
            }
        } // end try-catch
        setActionSuccessURL(getAssignSuccessURL());
    }

    /**
     * Action for "UpdatePrAddr" command.
     * 
     * @param pRequest
     * @param pResponse
     * @param order order
     * 
     * @throws ServletException
     * @throws IOException
     */
    private void updatePrimaryAndAssignAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Order order)
                    throws ServletException, IOException {
        handleUpdatePrimaryAddress(pRequest, pResponse);
        assignAction(pRequest, pResponse, order);
    }

    /**
     * Checks postal code.
     * 
     * @param pShippingGroup shipping group.
     * @param pAddressItem address item.
     * 
     * @return true if postal code is correct, else false.
     * 
     * @throws CommerceException
     */
    private boolean checkPostalCode(CastHardgoodShippingGroup pShippingGroup, RepositoryItem pAddressItem)
            throws CommerceException {
        boolean result = false;
        if (null != pAddressItem) {
            Object postalCodeProperty = pAddressItem.getPropertyValue(POSTAL_CODE_ADDRESS_PROP);
            String postalCode = null;
            if (null != postalCodeProperty) {
                postalCode = postalCodeProperty.toString();
            }
            result = getAddressHelper().validateCorse(pShippingGroup, this, postalCode);
        }
        return result;
    }

    /**
     * 
     * 
     * @param pAddressItem parameter
     * 
     * @return
     */
    private boolean validateName(RepositoryItem pAddressItem) {
        boolean result = false;
        CastAddressHelper ah = getAddressHelper();
        if ((ah != null) && (pAddressItem != null)) {
            String title = (String) pAddressItem.getPropertyValue(PREFIX_ADDRESS_PROP);
            String firstName = (String) pAddressItem.getPropertyValue(FIRST_NAME_ADDRESS_PROP);
            String lastName = (String) pAddressItem.getPropertyValue(LAST_NAME_ADDRESS_PROP);
            result = ah.validateName(title, firstName, lastName, this);
        }
        return result;
    }

    /**
     * Checks country.
     * 
     * @param pAddressItem address item.
     * 
     * @return true if country is correct, else false.
     * 
     * @throws CommerceException
     */
    private boolean checkCountry(RepositoryItem pAddressItem) throws CommerceException {
        if (isClickAndCollectDelivery()) return true;
        boolean result = false;
        if (null != pAddressItem) {
            Object stateProperty = pAddressItem.getPropertyValue(STATE_ADDRESS_PROP);
            String state = null;
            if (null != stateProperty) {
                state = stateProperty.toString();
            }
            result = getAddressHelper().validateCountry(state, this);
        }
        return result;
    }

    /**
     * Sets shipping address.
     * 
     * @param pShippingGroup shipping group.
     * @param pAddressItem address item.
     * @param pAddressName address name.
     * 
     * @return true if set, else false.
     * 
     * @throws CommerceException
     */
    private boolean setShippingAddress(CastHardgoodShippingGroup pShippingGroup, RepositoryItem pAddressItem,
            String pAddressName) throws CommerceException {
        boolean result = false;
        if (checkPostalCode(pShippingGroup, pAddressItem) && validateName(pAddressItem)) {
            OrderTools.copyAddress(pAddressItem, pShippingGroup.getShippingAddress());
            pShippingGroup.setAdresseLibelle(pAddressName);
            result = true;
        }
        return result;
    }

    /**
     * This method will save gift message the order.
     * 
     * @param order order
     */
    private void saveGiftMessage(Order order) {
        try {
            String commentaire = getCommentaire();
            if (null == commentaire) {
                commentaire = "";
            } else if (commentaire.length() >= MAX_GIFT_MESSAGE_LENGTH) {
                commentaire = commentaire.substring(0, MAX_GIFT_MESSAGE_LENGTH);
            }
            synchronized (order) {
                CastOrderImpl castOrder = (CastOrderImpl) order;
                castOrder.setCommandeCadeau(isCommandeCadeau());
                castOrder.setCommentaire(commentaire);
                getOrderManager().updateOrder(order);
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }

    private void saveCastoramaCardNumber(Order order) {
        try {
            String castoramaCardSelected = getProfileHandler().getCastoramaCard();
            if (isCastoramaCardCorrect()) {
                if (castoramaCardValue == null || castoramaCardValue.isEmpty()) {
                    castoramaCardValue = null;
                    if (castoramaCardSelected != null) {
                        getProfileHandler().getProfile().setPropertyValue(CARTE_ATOUT_PROFILE_PROP, Boolean.parseBoolean(castoramaCardSelected));
                    } else {
                        getProfileHandler().getProfile().setPropertyValue(CARTE_ATOUT_PROFILE_PROP, null);
                    }
                } else {
                    getProfileHandler().getProfile().setPropertyValue(CARTE_ATOUT_PROFILE_PROP, true);
                }
                getProfileHandler().getProfile().setPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP, castoramaCardValue);

                String email = (String) getProfileHandler().getProfile().getPropertyValue(EMAIL_PROFILE_PROP);
                try {
                    RepositoryItem newsletterSubscription = getProfileHandler().getNewsletterRepository().getItem(email, NEWSLETTER_ITEM_DESC);
                    MutableRepository mutableRepository = (MutableRepository) newsletterSubscription.getRepository();
                    MutableRepositoryItem mutableNewsletter = mutableRepository.getItemForUpdate(newsletterSubscription.getRepositoryId(),
                            newsletterSubscription.getItemDescriptor().getItemDescriptorName());
                    mutableNewsletter.setPropertyValue(CARTE_ATOUT_NEWSLETTER_PROP, true);
                    mutableRepository.updateItem(mutableNewsletter);
                } catch (RepositoryException re) {
                    if (isLoggingError()) {
                        logError("Impossible to update repository item.", re);
                    }
                }
            } else {
                if (castoramaCardSelected != null && !castoramaCardSelected.isEmpty()) {
                    getProfileHandler().getProfile().setPropertyValue(CARTE_ATOUT_PROFILE_PROP, Boolean.parseBoolean(castoramaCardSelected));
                } else {
                    getProfileHandler().getProfile().setPropertyValue(CARTE_ATOUT_PROFILE_PROP, null);
                }
                getProfileHandler().getProfile().setPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP, null);
            }
                        
            String number = getProfileHandler().getCastoramaCardNumber();
            if (null != number) {
                synchronized (order) {
                    CastOrderImpl castOrder = (CastOrderImpl) order;
                    castOrder.setCastoramaCardNumber(number);
                    getOrderManager().updateOrder(order);
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
    }

    /**
     * Close transaction
     * 
     * @param tr
     * 
     * @return
     */
    private Transaction closeTransaction(Transaction tr) {
        if (tr != null) {
            commitTransaction(tr);
        }
        return null;
    }

    /**
     * Clear <code>address</code> and address properties.
     */
    private void clearAddress() {
        setPrefix(null);
        setFirstName(null);
        setLastName(null);
        setAddress1(null);
        setAddress2(null);
        setAddress3(null);
        setLocality(null);
        setPostalCode(null);
        setCity(null);
        setCountry(null);
        setState(null);
        setPhoneNumber(null);
        setPhoneNumber2(null);
        setAddress(null);
    }

    /**
     * Returns credit card from order
     * 
     * @param pOrder
     * 
     * @return credit card
     */
    public CreditCard getCreditCard(Order pOrder) {
        if (pOrder == null) {
            return null;
        }

        List paymentGroups = pOrder.getPaymentGroups();

        if (paymentGroups == null) {
            return null;
        }

        int numPayGroups = paymentGroups.size();

        if (numPayGroups == 0) {
            return null;
        }

        PaymentGroup pg = null;

        for (int i = 0; i < numPayGroups; i++) {
            pg = (PaymentGroup) paymentGroups.get(i);

            if (pg instanceof CreditCard) {
                return (CreditCard) pg;
            }
        }

        return null;
    }

    /**
     * Clear address before canceling.
     * 
     * @param request the servlet's request
     * @param response the servlet's response
     * 
     * @return <code>false</code> if redirect is successful, <code>
     *            true</code> otherwise.
     * 
     * @exception ServletException if there was an error while executing the
     *                code
     * @exception IOException if there was an error with servlet io
     */
    @Override
    public boolean handleCancel(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws ServletException, IOException {
        clearAddress();
        return super.handleCancel(request, response);
    }

    /**
     * Gets session.
     * 
     * @return the sessionBean
     */
    public String getSessionBean() {
        return sessionBean;
    }

    /**
     * Sets session.
     * 
     * @param sessionBean the sessionBean to set
     */
    public void setSessionBean(String sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Gets ProfileTools.
     * 
     * @return the profileTools
     */
    public CastProfileTools getProfileTools() {
        return profileTools;
    }

    /**
     * Sets ProfileTools.
     * 
     * @param profileTools the profileTools to set
     */
    public void setProfileTools(CastProfileTools profileTools) {
        this.profileTools = profileTools;
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * atg.commerce.order.purchase.PurchaseProcessFormHandler#afterSet(atg.servlet
     * .DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override
    public boolean afterSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        if ((profileItem != null) && (getOrder() != null)) {
            Order curOrder = getOrder();
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("CastDeliveryFormHandler release lock : Profile ID : " + profileItem.getRepositoryId()
                            + "; Order ID : " + curOrder.getId());

                }
            }
        }
        return super.afterSet(pRequest, pResponse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * atg.commerce.order.purchase.PurchaseProcessFormHandler#beforeSet(atg.
     * servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override
    public boolean beforeSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        if ((profileItem != null) && (getOrder() != null)) {
            Order curOrder = getOrder();
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("CastDeliveryFormHandler get lock : Profile ID : " + profileItem.getRepositoryId()
                            + "; Order ID : " + curOrder.getId());

                }
            }
        }
        return super.beforeSet(pRequest, pResponse);
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     * 
     * @param pRequest parameter
     * 
     * @throws DeadlockException exception
     */
    protected void acquireTransactionLock(DynamoHttpServletRequest pRequest) throws DeadlockException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    RepositoryItem profileItem = getProfile();
                    if (profileItem != null) {
                        String profileId = profileItem.getRepositoryId();
                        pRequest.setAttribute(LOCK_NAME_ATTRIBUTE_NAME, profileId);
                        service.acquireTransactionLock(profileId);
                    } else {
                        service.acquireTransactionLock();
                    }
                }
            } catch (NoLockNameException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.acquireTransactionLock(pRequest);
        } // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     * 
     * @param pRequest parameter
     * 
     * @throws LockManagerException exception
     */
    protected void releaseTransactionLock(DynamoHttpServletRequest pRequest) throws LockManagerException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    String lockName = (String) pRequest.getAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    if (lockName != null) {
                        service.releaseTransactionLock(lockName);
                        pRequest.removeAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    } else {
                        service.releaseTransactionLock();
                    }
                }
            } catch (LockManagerException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.releaseTransactionLock(pRequest);
        } // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     * 
     * 
     * @return lockService property.
     */
    protected TransactionLockService getLockService() {
        TransactionLockFactory factory = getConfiguration().getTransactionLockFactory();
        TransactionLockService tls = null;
        if (factory != null) {
            tls = factory.getServiceInstance(this);
            RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
            if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
                tls.setClientLockManager(getClientLockManager());
            }
        }
        return tls;
    }

    /**
     * Gets isClickAndCollectDelivery property.
     * 
     * @return isClickAndCollectDelivery property.
     */
    private boolean isClickAndCollectDelivery() {
        boolean isClickAndCollectDelivery = false;
        OrderHolder curShCart = getShoppingCart();
        if (curShCart != null) {
            CastOrderImpl curOrder = (CastOrderImpl) getOrder();
            if (curOrder != null) {
                isClickAndCollectDelivery = DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT.equals(curOrder.getDeliveryType());
            }
        }
        return isClickAndCollectDelivery;
    }

    public Order getOrder() {
        OrderHolder shoppingCart = getShoppingCart();
        if (shoppingCart instanceof CastOrderHolder){
            return ((CastOrderHolder)shoppingCart).getCurrentlySelected();
        } else {
            return shoppingCart.getCurrent();
        }
    }

    private void preProcessingLogOrderInfo(String orderId) {
        logInfo("Pre-processing order " + orderId + " info:");
        castLoggingHelper.logOrderInfo(orderId);
    }

    private void postProcessingLogOrderInfo(String orderId) {
        logInfo("Post-processing order " + orderId + " info:");
        castLoggingHelper.logOrderInfo(orderId);
    }

    public CastLoggingHelper getCastLoggingHelper() {
        return castLoggingHelper;
    }

    public void setCastLoggingHelper(CastLoggingHelper castLoggingHelper) {
        this.castLoggingHelper = castLoggingHelper;
    }

    public String getCastoramaCardValue() {
        return (String) getProfileHandler().getProfile().getPropertyValue(CAST_CARD_NUMBER_PROFILE_PROP);
    }

    public void setCastoramaCardValue(String castoramaCardValue) {
        this.castoramaCardValue = castoramaCardValue;
    }

    public boolean isCastoramaCardCorrect() {
        return castoramaCardCorrect;
    }

    public void setCastoramaCardCorrect(boolean castoramaCardCorrect) {
        this.castoramaCardCorrect = castoramaCardCorrect;
    }
}
