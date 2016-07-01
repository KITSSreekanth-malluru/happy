package com.castorama.commerce.clientspace;

import static com.castorama.commerce.profile.Constants.ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.APPARTEMENT_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.CARTE_ATOUT_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_DERNIERE_MODIF_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_OF_BIRTH_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_SUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_UNSUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.EMAIL_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.JARDIN_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.LOGIN_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.MAISON_CAMPAGNE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.MAISON_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.NB_PERSONNES_ATOUT_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.PREF_STORE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.PROFILE_ID_FIELD_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.PROPRIETAIRE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RECEIVEEMAIL_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RESEIVEOFFERS_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_DOB;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_EMAIL_ADDRESS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_ACCES_PARTENAIRES_CASTO;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_CARTE_ATOUT;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_JARDIN;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_MAISON;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_STORE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_NON_EXISTENT_EMAIL;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_SELECT_SUBSCRIPTION;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.GenericFormHandler;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.castorama.integration.experian.ExperianRequestHelper;
import com.castorama.model.Abonnement;
import com.castorama.utils.CommonHelper;
import com.castorama.utils.ContextTools;
import com.castorama.utils.Validator;

/**
 * Newsletter Subscription Form handler class.
 *
 * @author Katsiaryna_Dmitrievich
 */
public class CastNewsletterFormHandler extends RepositoryFormHandler {
    /**
     * 
     */
    private static final String SOURCE_INSCRIPTION_WEBSITE = "Site web";

    /** CREATE_SUBSCR property */
    private static final String CREATE_SUBSCR = "createSubscr";

	/** NEWSLETTERS_QUERY_PARAM property */
    private static final String NEWSLETTERS_QUERY_PARAM = "newsletters";

    /** PARTNERS property */
    private static final String PARTNERS = "partners";

    /** NEWSLETTERS property */
    private static final String NEWSLETTERS = "newsletter";

    /** BOTH property */
    private static final String BOTH = "both";

    /** NONE property */
    private static final String NONE = "none";

    /** Email param for for URL. */
    private static final String EMAIL_URL_PARAM = "?email=";

    /** Magasin view name. */
    private static final String MAGASIN_VIEW = "magasin";

    
    protected static final String TRUE = "true";
    protected static final String FALSE = "false";

    /** Date Format pattern for DOB. */
    private static final String DATE_FORMAT = "ddMMyyyy";

    /** Source web. */
    private static final String SOURCE_WEB = "web";

    /** Email that was entered in footer. */
    private String mFooterMail;

    /** Selected preffered store. */
    private String mPrefStore;

    /**
     * Update Preffered store. This property is added because it is impossible
     * to remove value from field mPrefStore if it was stored in db before.
     * (getPrefStore() returns value from db if mPrefStore is empty).
     */
    private String mUpdatePrefStore;

    /** Tab to go to. */
    private String mActiveNewsletterTab;
    
    /** Date of birth. */
    private String mDob;

    /**
     * Update Date of birth. This property is added because it is impossible to
     * remove value from field dob if it was stored in db before. (getDob()
     * returns value from db if mDob is empty).
     */
    private String mUpdateDob;

    /** Current Profile. */
    private Profile mProfile;

    /** Experian system integration tool */
    private ExperianRequestHelper mExperianRequestHelper;

    /** Flag: if user doesn't select subscription on castorama newsletters. */
    private boolean saved;
    
    /** Flag: if user enter valid email in myNewsletters2.jsp. */
    private boolean validatedEmail;

    /** Email field. */
    private String mEmail;

    /** Save state before update. <code>null</code> for new item */
    private Boolean oldSubscribled;

    /** Save state before update. <code>null</code> for new item */
    private Boolean oldSubscribledOffers;

    /**
     * Flag: if user decided on unsubscribe page to change info in "to know you
     * better" block
     */
    private boolean changeInfo;

    /** If user decided to unsubscribe. */
    private boolean unsubscribe;

    /** Shows if newsletter subscription was updated. */
    private boolean updated;

    /** Common helper component. */
    private CommonHelper commonHelper;

    /**
     * Flag shows if newsletter subscription is created from catalog's
     * reservation.
     */
    private boolean fromCatalogReservation;

    /** Flag shows that need Experian notify while profile is updated */
    private boolean needExperianNotify;

    /** Context tools. */
    private ContextTools contextTools;

    /**
     * Gets <code>changeInfo</code>: if user decided on unsubscribe page to
     * change info in "to know you better" block
     *
     * @return the changeInfo
     */
    public boolean isChangeInfo() {
        return changeInfo;
    }

    /**
     * Sets <code>changeInfo</code>: if user decided on unsubscribe page to
     * change info in "to know you better" block
     *
     * @param changeInfo the changeInfo to set
     */
    public void setChangeInfo(boolean changeInfo) {
        this.changeInfo = changeInfo;
    }

    /**
     * Returns the id of preferred store for current newsletter profile. If it
     * is empty - returns value from db.
     *
     * @return the prefStore - ID of current newsletter profile's preferred
     *         store
     */
    public String getPrefStore() {
        String repositoryId = getRepositoryId();

        if (!StringUtils.isEmpty(repositoryId)) {
            Repository repository = getRepository();
            RepositoryItem magasin = null;
            if (repository != null) {
                try {
                    RepositoryItem magasinItem = repository.getItem(repositoryId, getItemDescriptorName());
                    if (magasinItem != null) {
                        magasin = (RepositoryItem) magasinItem.getPropertyValue(PREF_STORE_NEWSLETTER_PROP);
                    }

                } catch (RepositoryException e) {
                    if (isLoggingDebug()) {
                        logError("Impossible to find store");
                    }
                }
                if (magasin != null) {
                    mPrefStore = magasin.getRepositoryId();
                }
            }
        } else {
            mPrefStore = null;
        }  // end if-else
        return mPrefStore;
    }

    /**
     * Sets preferred store
     *
     * @param prefStore the prefStore to set
     */
    public void setPrefStore(final String prefStore) {
        this.mPrefStore = prefStore;
        setUpdatePrefStore(prefStore);
    }

    /**
     * Gets updated value for preferred store.
     *
     * @return the mUpdatePrefStore
     */
    public String getUpdatePrefStore() {
        return mUpdatePrefStore;
    }

    /**
     * Sets upadted value for preferred store
     *
     * @param updatePrefStore the mUpdatePrefStore to set
     */
    public void setUpdatePrefStore(String updatePrefStore) {
        mUpdatePrefStore = updatePrefStore;
    }

    /**
     * Gets activeNewsletterTab value 
     *
     * @return the mActiveNewsletterTab
     */
    public String getActiveNewsletterTab() {
        return mActiveNewsletterTab;
    }

    /**
     * Sets activeNewsletterTab to which you should go
     * 
     * @param mActiveNewsletterTab
     */
    public void setActiveNewsletterTab(String mActiveNewsletterTab) {
        this.mActiveNewsletterTab = mActiveNewsletterTab;
    }
    
    /**
     * Returns the date of birth. If it is empty - returns value from db
     * (because it should be prepopulated on myProfile page).
     *
     * @return the mDob
     */
    public String getDob() {
        String repositoryId = getRepositoryId();
        if (repositoryId != null) {
            Repository repository = getRepository();
            try {
                if ((repository != null) && (repository.getItem(repositoryId, getItemDescriptorName()) != null)) {
                    Date date =
                        (Date) repository.getItem(repositoryId, getItemDescriptorName()).getPropertyValue(DATE_OF_BIRTH_NEWSLETTER_PROP);

                    if (date != null) {
                        mDob = formatDate(date);
                    }
                }
            } catch (ParseException e) {
                if (isLoggingDebug()) {
                    logDebug("Impossible format date to string .");
                }
            } catch (RepositoryException e) {
                if (isLoggingDebug()) {
                    logError("Impossible to find date of birth");
                }
            }
        }  // end if
        return mDob;
    }

    /**
     * Validate email on myNewsletters2.jsp.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleValidateEmail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                        throws ServletException, IOException {
        
        String email = getEmail();
        if (!validateEmail(email)) {
            
            setValidatedEmail(false);
            return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
        } 
        else {
            
            RepositoryItem existSubscribtion;
            try {
                existSubscribtion =
                        getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, email,
                                                                        getRepository(), getItemDescriptorName());
                if (existSubscribtion == null) {
                    
                    setValidatedEmail(false);
                    getCommonHelper().generateFormException(MSG_NON_EXISTENT_EMAIL, this, RESOURCE_BUNDLE);
                    return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
                }
            } catch (RepositoryException e) {
                
                if (isLoggingDebug()) {
                    logDebug("Impossible to check email existance");
                }
            }
        }
        
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }

        setUpdateSuccessURL(getUpdateSuccessURL() + "?email=" + email);
        setValueProperty(EMAIL_NEWSLETTER_PROP, email);
        setValidatedEmail(true);
        
        return checkFormRedirect(getUpdateSuccessURL(), getUpdateSuccessURL(), pRequest, pResponse);
    }
    
    /**
     * Update subscription on myNewsletters2.jsp: "to know your better block".
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdateToKnowYouBetter(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                        throws ServletException, IOException {
        String email = getEmail();
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }
        if (StringUtils.isBlank(getRepositoryId())) {
            checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            return false;
        }
        setUpdateSuccessURL(getUpdateSuccessURL() + EMAIL_URL_PARAM + email);
        setUpdateErrorURL(getUpdateErrorURL() + EMAIL_URL_PARAM + email);
        if (!validateAdditionalInfo() | !validateNewsletterStore() | getFormError()) {
            setSaved(true);
            checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            return false;
        }
        updateSpecialInfo(false);
        updateDOB();
        RepositoryItem existSubscribtion;
        setUpdated(true);

        try {
            existSubscribtion =
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                getRepository(), getItemDescriptorName());

            if (existSubscribtion == null) {
                setCreateSuccessURL(getUpdateSuccessURL());
                setCreateErrorURL(getUpdateErrorURL());
                return super.handleCreate(pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }
        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Update preferred store for current newsletter profile (email should be
     * entered).
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdateMagasin(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                throws ServletException, IOException {
        String email = getEmail();
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }
        if (StringUtils.isBlank(getRepositoryId())) {
            checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            return false;
        }
        updateSpecialInfo(true);
        setValueProperty(ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP, true);
        try {
            if (
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                    getRepository(), getItemDescriptorName()) == null) {
                setCreateSuccessURL(getUpdateSuccessURL());
                setCreateErrorURL(getUpdateErrorURL());
                return super.handleCreate(pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }
        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Clear RECEIVEEMAIL and RESEIVEOFFERS if they were set to 'false'.
     */
    private void setCorrectSubscriptions() {
        String reseiveEmail = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
        String reseiveOffersEmail = (String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP);
        if (!StringUtils.isBlank(reseiveEmail) && !TRUE.equalsIgnoreCase(reseiveEmail)) {
            getValue().remove(RECEIVEEMAIL_NEWSLETTER_PROP);
        }
        if (!StringUtils.isBlank(reseiveOffersEmail) && !TRUE.equalsIgnoreCase(reseiveOffersEmail)) {
            getValue().remove(RESEIVEOFFERS_NEWSLETTER_PROP);
        }
    }

    /**
     * Update subscription from profile page.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdateFromProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
        if (!validateNewsletterStore() | !validateAdditionalInfo()) {
            return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
        }
        String email = getEmail();
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }
        if (StringUtils.isBlank(getRepositoryId())) {
            checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            return false;
        }
        updateSpecialInfo(true);
        RepositoryItem existSubscribtion;
        try {
            existSubscribtion =
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                getRepository(), getItemDescriptorName());

            if (existSubscribtion == null) {
                setCreateSuccessURL(getUpdateSuccessURL());
                setCreateErrorURL(getUpdateErrorURL());
                return super.handleCreate(pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }
        updateDOB();
        setUpdated(true);
        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Checks if user decided to subscribe on castorama newsletters or partner's
     * offers.
     *
     * @return <code>true</code> if user check receiveEmail or receiveOffers
     *         checkbox, <code>false</code> otherwise
     */
    public boolean isSubscribe() {
        boolean result = false;
        String receiveEmail = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
        String receiveOfferse = (String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP);
        if ((TRUE.compareToIgnoreCase(receiveEmail) == 0) ||
                (TRUE.compareToIgnoreCase(receiveOfferse) == 0)) {
            result = true;
        }
        return result;
    }

    /**
     * This method is called just before the item is deleted from the
     * repository: call notifyExperianUnsubscrible()
     *
     * @see    #notifyExperianUnsubscrible()
     *
     * @param  request  the servlet's request
     * @param  response the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    @Override protected void preDeleteItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                    throws ServletException, IOException {
        notifyExperianUnsubscrible();

        super.preDeleteItem(request, response);
    }

    /**
     * This method is called just after the item is updated: update preferred
     * store if action is not 'unsubscribe'. Notify Experian.
     *
     * @param  request  the servlet's request
     * @param  response the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    @Override protected void postUpdateItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                     throws ServletException, IOException {
        // update preferred store info only in case if unsubscribe set in false
        if (!isUnsubscribe()) {
            updateStore(response);
        }

        if ((null == getOldSubscribled()) || (null == getOldSubscribledOffers())) {
            // Create subscription
            notifyExperianSubscrible();
        } else {
            Abonnement abonnement = Abonnement.getInstance(getRepositoryItem());

            boolean hasSubscrible =
                (!getOldSubscribled() && abonnement.getSubscribled()) ||
                (!getOldSubscribledOffers() && abonnement.getSubscribledPartners());

            boolean hasUnSubscrible =
                (getOldSubscribled() && !abonnement.getSubscribled()) ||
                (getOldSubscribledOffers() && !abonnement.getSubscribledPartners());

            if (!hasSubscrible && !hasUnSubscrible) {
                String receiveEmail = (String) getRepositoryItem().getPropertyValue(RECEIVEEMAIL_NEWSLETTER_PROP);
                if (abonnement.getSubscribled() || receiveEmail.equalsIgnoreCase(FALSE)){
                    notifyExperianUpdate();
                }
            } else {
                if (hasUnSubscrible) {
                    notifyExperianUnsubscrible();
                }

                if (hasSubscrible) {
                    notifyExperianSubscrible();
                }
            }
        }  // end if-else

        super.postUpdateItem(request, response);
    }

    /**
     * This method is called just after the item creation process is finished.
     *
     * @param     request  the servlet's request
     * @param     response the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override protected void postCreateItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                     throws ServletException, IOException {
        updateStore(response);
        notifyExperianSubscrible();
        Boolean.valueOf((String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP));
        boolean receiveEmail = Boolean.valueOf((String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP));

        boolean receiveOfferse = Boolean.valueOf((String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP));
        String newsletterRegistration = NONE;
        if (receiveEmail) {
            if (receiveOfferse) {
                newsletterRegistration = BOTH;
            } else {
                newsletterRegistration = NEWSLETTERS;
            }
        } else if (receiveOfferse) {
            newsletterRegistration = PARTNERS;
        }
        request.addQueryParameter(NEWSLETTERS_QUERY_PARAM, newsletterRegistration);
        super.postCreateItem(request, response);
    }

    /**
     * Updates profile id field if <code>checkLogin</code> is <code>true</code>,
     * Sets to <code>APPARTEMENT</code> <code>true</code> if <code>maison</code>
     * is <code>false</code>, sets <code>false</code> to <code>
     * appartement</code> if <code>maison</code> is <code>true</code>.
     *
     * @param checkLogin - show if profile id should be updated
     */
    private void updateSpecialInfo(final boolean checkLogin) {
        final Profile profile = getProfile();
        if ((profile != null) && checkLogin && !profile.isTransient()) {
            setValueProperty(PROFILE_ID_FIELD_NEWSLETTER_PROP, profile.getRepositoryId());
        }
        Boolean maison = (Boolean) getValueProperty(MAISON_NEWSLETTER_PROP);
        if (maison != null) {
            setValueProperty(APPARTEMENT_NEWSLETTER_PROP, !maison);
        }
    }

    /**
     * Validate "to know your better" block: validate date.
     *
     * @return <code>true</code> if validation is successful
     */
    public boolean validateAdditionalInfo() {
        return validateAdditionalInfo(this);
    }
    
    /**
     * Validate "to know your better" block: validate date.
     *
     * @return <code>true</code> if validation is successful
     */
    public boolean validateAdditionalInfo(final GenericFormHandler formHandler) {
        
        boolean result = true;
        final String dob = getUpdateDob();
        if (!StringUtils.isBlank(dob)) {
            if (!Validator.validateDate(dob)) {
                getCommonHelper().generateFormException(MSG_INCORRECT_DOB, formHandler, RESOURCE_BUNDLE);
                result = false;
            }
        }
        
        Boolean haveStore = (Boolean) getValueProperty(ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP);
        if (haveStore == null) {
            getCommonHelper().generateFormException(MSG_MISSED_ACCES_PARTENAIRES_CASTO, formHandler, RESOURCE_BUNDLE);
            result = false;
        }
           
        Boolean haveMaison = (Boolean) getValueProperty(MAISON_NEWSLETTER_PROP);
        if (haveMaison == null) {
            getCommonHelper().generateFormException(MSG_MISSED_MAISON, formHandler, RESOURCE_BUNDLE);
            result = false;
        }
        
        Boolean haveJardin = (Boolean) getValueProperty(JARDIN_NEWSLETTER_PROP);
        if (haveJardin == null) {
            getCommonHelper().generateFormException(MSG_MISSED_JARDIN, formHandler, RESOURCE_BUNDLE);
            result = false;
        }
        
        if (!result) {
            setValidatedEmail(true);
        }
        
        return result;
    }

    /**
     * Convert date to string using date format ddMMyyyy.
     *
     * @param  date - Date to convert.
     *
     * @return date in string
     *
     * @throws ParseException
     */
    private String formatDate(final Date date) throws ParseException {
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        String myDate = df.format(date);
        return myDate;
    }

    /**
     * Sets dob
     *
     * @param dob the mDob to set
     */
    public void setDob(String dob) {
        mDob = dob;
        setUpdateDob(dob);
    }

    /**
     * Get dob value.
     *
     * @return the mUpdateDob
     */
    public String getUpdateDob() {
        return mUpdateDob;
    }

    /**
     * Set dob value.
     *
     * @param updateDob the mUpdateDob to set
     */
    public void setUpdateDob(String updateDob) {
        mUpdateDob = updateDob;
    }

    /**
     * Gets current profile.
     *
     * @return profile
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Set profile.
     *
     * @param profile the mProfile to set
     */
    public void setProfile(Profile profile) {
        mProfile = profile;
    }

    /**
     * Validate email: it should be not empty and satisfied regexp.
     *
     * @param  email - email to validate
     *
     * @return <code>true</code> if email is correct, <code>false</code>
     *         otherwise
     */
    public boolean validateEmail(final String email) {
        boolean result = false;
        if (StringUtils.isBlank(email)) {
            getCommonHelper().generateFormException(MSG_MISSED_EMAIL, this, RESOURCE_BUNDLE);
        } else {
            if (Validator.validateEmail(email)) {
                result = true;
            } else {
                getCommonHelper().generateFormException(MSG_INCORRECT_EMAIL_ADDRESS, this, RESOURCE_BUNDLE);
            }
        }
        return result;
    }

    /**
     * Add subscription: add new subscription if subscription with such <code>
     * email</code> doesn't exist. otherwise - update existed.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws IOException
     * @throws ServletException
     */
    public boolean handleAddSubscribtion(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                  throws IOException, ServletException {
        String email = getEmail();
        String reseiveEmail = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
        setCreateErrorURL(getCreateErrorURL() + EMAIL_URL_PARAM + email);

        if (!validateEmail(email) | !validateNewsletterStore() | !validateSubscription(reseiveEmail)) {
            return checkFormRedirect(getCreateErrorURL(), getCreateErrorURL(), pRequest, pResponse);
        }

        setRepositoryId(email);

        if (StringUtils.isBlank(getRepositoryId())) {
            checkFormRedirect(getCreateErrorURL(), getCreateErrorURL(), pRequest, pResponse);
            return false;
        }

        setCreateSuccessURL(getCreateSuccessURL() + EMAIL_URL_PARAM + email);
        setValueProperty(EMAIL_NEWSLETTER_PROP, email);

        updateSpecialInfo(true);
        RepositoryItem existedSubsc;
        try {
            existedSubsc =
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                getRepository(), getItemDescriptorName());
            if (existedSubsc != null) {
                setUpdateErrorURL(getCreateErrorURL());
                setUpdateSuccessURL(getCreateSuccessURL());

                if (newSubscriptions(existedSubsc)) {
                	pRequest.addQueryParameter(CREATE_SUBSCR, Boolean.TRUE.toString());
                }

                /*
                 * Update subscription profile if user decided to add new subscription.
                 */

                return super.handleUpdate(pRequest, pResponse);
            } else {
                pRequest.addQueryParameter(CREATE_SUBSCR, Boolean.TRUE.toString());
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }  // end try-catch
        return super.handleCreate(pRequest, pResponse);
    }

	/**
	 * Gets <code>true</code> if new subscriptions were created for current subscriber.
	 * @param existedSubsc - current subscriber
	 * @return <code>true</code> if new subscriptions were created for current subscriber
	 */
	private boolean newSubscriptions(RepositoryItem existedSubsc) {
		String oldNewsletter = (String) existedSubsc.getPropertyValue(RECEIVEEMAIL_NEWSLETTER_PROP);
		String newNewsletter = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
		String oldOffers = (String) existedSubsc.getPropertyValue(RESEIVEOFFERS_NEWSLETTER_PROP);
		String newOffers = (String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP);
		return (TRUE.compareToIgnoreCase(oldNewsletter) != 0 && TRUE.compareToIgnoreCase(newNewsletter) == 0) ||
		        (TRUE.compareToIgnoreCase(oldOffers) != 0 && TRUE.compareToIgnoreCase(newOffers) == 0);
	}

    /**
     * Check if checkbox with value {@link #RESEIVE_EMAIL_TRUE} was checked.
     * Otherwise - generate exception.   *
     *
     * @param  receiveNewsletter - checkbox value
     *
     * @return <code>true</code> if receiveNewsletter = true, <code>false</code>
     *         otherwise
     */
    private boolean validateSubscription(String receiveNewsletter) {
        if (StringUtils.isBlank(receiveNewsletter) || !TRUE.equalsIgnoreCase(receiveNewsletter)) {
            getCommonHelper().generateFormException(MSG_SELECT_SUBSCRIPTION, this, RESOURCE_BUNDLE);
            return false;
        } else {
            setSaved(true);
            return true;
        }
    }

    /**
     * Validates store: it shouldn't be empty if <code>
     * accesPartenairesCasto</code> was set to <code>true</code>.
     *
     * @return <code>true</code> if
     *         <li>accesPartenairesCasto set to <code>true</code> and fields
     *         store is not empty
     *         <li>accesPartenairesCasto set to <code>false</code><br />
     *         and <code>false</code> otherwise
     */
    public boolean validateNewsletterStore() {
        return validateNewsletterStore(this);
    }

    /**
     * Validates store: it shouldn't be empty if <code>
     * accesPartenairesCasto</code> was set to <code>true</code>.
     *
     * @return <code>true</code> if
     *         <li>accesPartenairesCasto set to <code>true</code> and fields
     *         store is not empty
     *         <li>accesPartenairesCasto set to <code>false</code><br />
     *         and <code>false</code> otherwise
     */
    public boolean validateNewsletterStore(final GenericFormHandler formHandler) {
        Boolean haveStore = (Boolean) getValueProperty(ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP);
        if (StringUtils.isBlank(getUpdatePrefStore()) && (haveStore != null) && haveStore) {
            getCommonHelper().generateFormException(MSG_MISSED_STORE, formHandler, RESOURCE_BUNDLE);
            return false;
        }
        return true;
    }

    /**
     * Gets <code>email</code> property.
     *
     * @return mEmail
     */
    public String getEmail() {
        if (mEmail == null || mEmail.length() == 0) {
            Profile profile = getProfile();
            if (null != profile) {
                if (profile.isTransient()) {
                    mEmail = "";
                } else {
                    Repository mutRepos = getRepository();
                    try {
                        RqlStatement findEmailRQL =
                            RqlStatement.parseRqlStatement(PROFILE_ID_FIELD_NEWSLETTER_PROP + " = ?0 AND not " +
                                                           DATE_SUBSCRIBE_NEWSLETTER_PROP + " is null ORDER BY " +
                                                           DATE_SUBSCRIBE_NEWSLETTER_PROP + " SORT DESC ");
                        RepositoryView emailView = mutRepos.getView(getItemDescriptorName());
                        Object[] rqlparams = new Object[1];
                        rqlparams[0] = profile.getRepositoryId();
                        RepositoryItem[] emailList = findEmailRQL.executeQuery(emailView, rqlparams);

                        if ((emailList != null) && (emailList.length != 0)) {
                            mEmail = (String) emailList[0].getPropertyValue(EMAIL_NEWSLETTER_PROP);
                        } else {
                            mEmail = (String) profile.getPropertyValue(EMAIL_NEWSLETTER_PROP);

                            /* If email is empty - get value from login. */
                            if ((mEmail == null) || ((mEmail != null) && (mEmail.length() == 0))) {
                                mEmail = (String) profile.getPropertyValue(LOGIN_PROFILE_PROP);
                            }
                        }
                    } catch (RepositoryException e) {
                        if (isLoggingDebug()) {
                            logDebug("Errors are occured while reading emails from repository. ");
                        }
                        mEmail = "";
                    }  // end try-catch
                }  // end if-else
            }  // end if
        }  // end if

        return mEmail;
    }

    /**
     * Sets mEmail.
     *
     * @param email
     */
    public void setEmail(final String email) {
        mEmail = email;
    }

    /**
     * Creates subscription after account creating.
     *
     * @param  pRequest
     * @param  pResponse
     * @param  login     - entered email address (login)
     *
     * @return
     *
     * @throws IOException
     * @throws ServletException
     */
    public boolean handleCreateFromProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                           final String login) throws IOException, ServletException {
        setValueProperty(EMAIL_NEWSLETTER_PROP, login);
        setRepositoryId(login);
        String email = getEmail();
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }
        if (StringUtils.isBlank(getRepositoryId())) {
            checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
            return false;
        }
        updateSpecialInfo(true);
        updateDOB();
        RepositoryItem existSubscribtion;
        try {
            existSubscribtion =
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                getRepository(), getItemDescriptorName());

            if (existSubscribtion != null) {
                setUpdateErrorURL(getCreateErrorURL());
                setUpdateSuccessURL(getCreateSuccessURL());
                return super.handleUpdate(pRequest, pResponse);
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }

        return handleCreate(pRequest, pResponse);

    }

    /**
     * Befor create: set correct subscribe dates,  check if user subscribes.
     *
     * @param  request  the servlet's request
     * @param  response the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    @Override protected void preCreateItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                    throws ServletException, IOException {
        /* Set subscription dates if email was subscribed. */
        setSubscriptionDates();
        setCorrectSubscriptions();
        setSourceInscriotion();
        super.preCreateItem(request, response);
    }

    /**
     * Sets sourceInscription to "website"
     */
    private void setSourceInscriotion() {
        setValueProperty("sourceInscription", SOURCE_INSCRIPTION_WEBSITE);
    }

    /**
     * Set subscription dates (dateInscription and dateOffersInscription) if
     * email was subscribed while newsletter profile <b>creating</b>.
     */
    private void setSubscriptionDates() {
        String reseiveEmail = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
        String reseiveOffersEmail = (String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP);
        if (!StringUtils.isBlank(reseiveEmail) && TRUE.equalsIgnoreCase(reseiveEmail)) {
            setValueProperty(DATE_SUBSCRIBE_NEWSLETTER_PROP, new Date());
        }
        if (!StringUtils.isBlank(reseiveOffersEmail) && TRUE.equalsIgnoreCase(reseiveOffersEmail)) {
            setValueProperty(DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP, new Date());
        }
    }

    /**
     * Before item delete: check and updates subscribe dates.
     *
     * @param  request  the servlet's request
     * @param  response the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    @Override protected void preUpdateItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                    throws ServletException, IOException {
        updateSubscriptionDates();

        Abonnement abonnement = Abonnement.getInstance(getRepositoryItem());
        setOldSubscribled(abonnement.getSubscribled());
        setOldSubscribledOffers(abonnement.getSubscribledPartners());

        Boolean isCastoramaSubscribed = abonnement.getSubscribled();
        if (!isCastoramaSubscribed){
            setSourceInscriotion();
        }

        setCorrectSubscriptions();
        super.preUpdateItem(request, response);
    }

    /**
     * Set subscription dates (dateInscription and dateOffersInscription) if
     * email was subscribe while newsletter profile <b>updating</b>.
     *
     * @throws ServletException
     */
    private void updateSubscriptionDates() throws ServletException {
        String reseiveEmail = (String) getValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP);
        String reseiveOffersEmail = (String) getValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP);
        RepositoryItem existSubscribtion;
        try {
            existSubscribtion =
                getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, getRepositoryId(),
                                                                getRepository(), getItemDescriptorName());
            if (existSubscribtion != null) {
                if (TRUE.equalsIgnoreCase(reseiveEmail)) {
                    setValueProperty(DATE_SUBSCRIBE_NEWSLETTER_PROP, new Date());
                }
                if (TRUE.equalsIgnoreCase(reseiveOffersEmail)) {
                    setValueProperty(DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP, new Date());
                }
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
        }  // end try-catch
    }

    /**
     * Update preferred store if <code>accesPartenairesCasto=true</code>.
     *
     * @param pResponse the response of the form handler
     * @throws ServletException
     */
    private void updateStore(DynamoHttpServletResponse pResponse) throws ServletException {
        String storeId = getUpdatePrefStore();
        if (storeId != null)
            getContextTools().updateNewsletterStore(pResponse, getRepositoryId(), storeId);
    }

    /**
     * Update DOB.
     */
    private void updateDOB() {
        final String dob = getUpdateDob();
        try {
            if (!StringUtils.isBlank(dob)) {
                setValueProperty(DATE_OF_BIRTH_NEWSLETTER_PROP, parseDate(dob));
            } else {
                setValueProperty(DATE_OF_BIRTH_NEWSLETTER_PROP, "");
            }
        } catch (ParseException e) {
            setValueProperty(DATE_OF_BIRTH_NEWSLETTER_PROP, "");
        }

    }

    /**
     * Convert string (in format ddMMyyyy) into date.
     *
     * @param  date - string date to convert
     *
     * @return date
     *
     * @throws ParseException
     */
    private Date parseDate(final String date) throws ParseException {
        Date myDate = null;
        if (date != null) {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            myDate = df.parse(date);
        }
        return myDate;
    }

    /**
     * Notify Experian system for remove subscription.
     */
    private void notifyExperianUnsubscrible() {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addRemoveSubscriptionRequest(getEmail(), SOURCE_WEB);
        } catch (Throwable e) {
            logError(e);
        }
    }

    /**
     * Notify Experian system for update subscription.
     */
    private void notifyExperianUpdate() {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addChangeSubscriptionRequest(getEmail(), SOURCE_WEB);
        } catch (Throwable e) {
            logError(e);
        }
    }

    /**
     * Notify Experian system for create subscription.
     */
    private void notifyExperianSubscrible() {
        ExperianRequestHelper service = getExperianRequestHelper();

        try {
            service.addCreateSubscriptionRequest(getEmail(), SOURCE_WEB);
        } catch (Throwable e) {
            logError(e);
        }
    }

    /**
     * Set footer email - email that entered in subscription block in footer.
     *
     * @return the mFooterMail
     */
    public String getFooterMail() {
        return mFooterMail;
    }

    /**
     * Sets email in footer.
     *
     * @param footerMail the mFooterMail to set
     */
    public void setFooterMail(String footerMail) {
        mFooterMail = footerMail;
    }

    /**
     * Gets saved option: if subscription was correct and saved.
     *
     * @return the saved
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Sets saved option: if subscription was correct and saved.
     *
     * @param saved the saved to set
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    
    /**
     * Gets validatedEmail option: if email was correct and existed.
     *
     * @return the validatedEmail
     */
    public boolean isValidatedEmail() {
        return validatedEmail;
    }

    /**
     * Sets validatedEmail option: if email was correct and existed.
     *
     * @param validatedEmail the validatedEmail to set
     */
    public void setValidatedEmail(boolean validatedEmail) {
        this.validatedEmail = validatedEmail;
    }

    /**
     * Gets unsubscribe property: If user decided to unsubscribe
     *
     * @return the unsubscribe
     */
    public boolean isUnsubscribe() {
        return unsubscribe;
    }

    /**
     * Sets unsubscribe property: If user decided to unsubscribe
     *
     * @param unsubscribe the unsubscribe to set
     */
    public void setUnsubscribe(boolean unsubscribe) {
        this.unsubscribe = unsubscribe;
    }

    /**
     * Gets unsubscribe property: If user decided to unsubscribe
     *
     * @return the updated
     */
    public boolean isUpdated() {
        return updated;
    }

    /**
     * Sets flag thst shows if newsletter subscription was updated.
     *
     * @param updated the updated to set
     */
    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    /**
     * Creates new element in abonnement newsletter with id <code>
     * newEmail</code> and all properties are copied from <code>oldEmail</code>
     *
     * @param  pRequest  - request
     * @param  pResponse - response
     * @param  newEmail  - new id and email - destination
     * @param  oldEmail  - id of element copy from
     *
     * @throws ServletException
     * @throws IOException
     */
    public void copyElement(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                            final String newEmail, final String oldEmail) throws ServletException, IOException {
        try {
            MutableRepositoryItem oldElement =
                (MutableRepositoryItem) getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, oldEmail,
                                                                                        getRepository(),
                                                                                        getItemDescriptorName());
            MutableRepositoryItem newElement =
                (MutableRepositoryItem) getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, newEmail,
                                                                                        getRepository(),
                                                                                        getItemDescriptorName());
            if ((newElement == null) && (oldElement != null)) {
                newElement = getRepository().createItem(newEmail, getItemDescriptorName());

                final Iterator<String> propIterator = getNewsletterPropertyList().iterator();

                while (propIterator.hasNext()) {
                    String prop = propIterator.next();
                    if (prop != null) {
                        newElement.setPropertyValue(prop, oldElement.getPropertyValue(prop));
                    }
                }
                getRepository().addItem(newElement);

                try {
                    boolean receiveEmail =
                        Boolean.valueOf((String) newElement.getPropertyValue(RECEIVEEMAIL_NEWSLETTER_PROP));

                    boolean reseiveOffers =
                        Boolean.valueOf((String) newElement.getPropertyValue(RESEIVEOFFERS_NEWSLETTER_PROP));

                    if (receiveEmail || reseiveOffers) {
                        ExperianRequestHelper service = getExperianRequestHelper();
                        service.addCreateSubscriptionRequest(newEmail, SOURCE_WEB);
                    }
                } catch (Throwable e) {
                    logError(e);
                }
            }  // end if
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Errors are occured while coping. ");
            }
        }  // end try-catch
    }

    /**
     * Creates list of properties of abonnement newsletter to copy.
     *
     * @return list of properties
     */
    private List<String> getNewsletterPropertyList() {
        List<String> nl = new ArrayList<String>(10);
        nl.add(RESEIVEOFFERS_NEWSLETTER_PROP);
        nl.add(RECEIVEEMAIL_NEWSLETTER_PROP);
        nl.add(ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP);
        nl.add(PREF_STORE_NEWSLETTER_PROP);
        nl.add(DATE_DERNIERE_MODIF_NEWSLETTER_PROP);
        nl.add(PROFILE_ID_FIELD_NEWSLETTER_PROP);
        nl.add(DATE_OF_BIRTH_NEWSLETTER_PROP);
        nl.add(DATE_SUBSCRIBE_NEWSLETTER_PROP);
        nl.add(DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP);
        nl.add(DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP);
        nl.add(DATE_UNSUBSCRIBE_NEWSLETTER_PROP);
        nl.add(APPARTEMENT_NEWSLETTER_PROP);
        nl.add(MAISON_NEWSLETTER_PROP);
        nl.add(JARDIN_NEWSLETTER_PROP);
        nl.add(PROPRIETAIRE_NEWSLETTER_PROP);
        nl.add(NB_PERSONNES_ATOUT_NEWSLETTER_PROP);
        nl.add(MAISON_CAMPAGNE_NEWSLETTER_PROP);
        nl.add(CARTE_ATOUT_NEWSLETTER_PROP);
        return nl;
    }

    /**
     * Gets Request Sender for EXPERIAN.
     *
     * @return the experianRequestHelper
     */
    public ExperianRequestHelper getExperianRequestHelper() {
        return mExperianRequestHelper;
    }

    /**
     * Sets Request Helper for EXPERIAN.
     *
     * @param pExperianRequestHelper the experianRequestHelper to set
     */
    public void setExperianRequestHelper(ExperianRequestHelper pExperianRequestHelper) {
        mExperianRequestHelper = pExperianRequestHelper;
    }

    /**
     * Gets flag: save state before update
     *
     * @return the oldIsSubscribled
     */
    public Boolean getOldSubscribled() {
        return oldSubscribled;
    }

    /**
     * Sets flag: save state before update
     *
     * @param oldIsSubscribled the oldIsSubscribled to set
     */
    public void setOldSubscribled(Boolean oldIsSubscribled) {
        this.oldSubscribled = oldIsSubscribled;
    }

    /**
     * Gets flag: save state before update
     *
     * @return the oldIsSubscribledOffers
     */
    public Boolean getOldSubscribledOffers() {
        return oldSubscribledOffers;
    }

    /**
     * Sets flag: save state before update
     *
     * @param oldIsSubscribledOffers the oldIsSubscribledOffers to set
     */
    public void setOldSubscribledOffers(Boolean oldIsSubscribledOffers) {
        this.oldSubscribledOffers = oldIsSubscribledOffers;
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

    /**
     * Gets flag that shows if newsletter subscription is created from catalog's
     * reservation.
     *
     * @return the fromCatalogReservation
     */
    public boolean isFromCatalogReservation() {
        return fromCatalogReservation;
    }

    /**
     * Sets flag that shows if newsletter subscription is created from catalog's
     * reservation.
     *
     * @param fromCatalogReservation the fromCatalogReservation to set
     */
    public void setFromCatalogReservation(boolean fromCatalogReservation) {
        this.fromCatalogReservation = fromCatalogReservation;
    }

    /**
     * Gets flag shows that need Experian notify
     *
     * @return the needExperianNotify
     */
    public boolean isNeedExperianNotify() {
        return needExperianNotify;
    }

    /**
     * Sets flag shows that need Experian notify
     *
     * @param needExperianNotify the needExperianNotify to set
     */
    public void setNeedExperianNotify(boolean needExperianNotify) {
        this.needExperianNotify = needExperianNotify;
    }

    /**
     * Notify Experian when profile is updated
     *
     * @param email the user login
     */
    public void notifyExperianUpdate(String email) {
        if (isNeedExperianNotify()) {
            try {
                RepositoryItem newsletter = getRepository().getItem(email, getItemDescriptorName());
                if(newsletter!=null) {
                    String receiveEmail =
                    		(String) newsletter.getPropertyValue(RECEIVEEMAIL_NEWSLETTER_PROP);

                    String reseiveOffers =
                    		(String) newsletter.getPropertyValue(RESEIVEOFFERS_NEWSLETTER_PROP);

                    //add request if user is subscribed
                    if (receiveEmail.equalsIgnoreCase(TRUE) || receiveEmail.equalsIgnoreCase(FALSE)
                            || reseiveOffers.equalsIgnoreCase(TRUE) || reseiveOffers.equalsIgnoreCase(FALSE)) {
                        ExperianRequestHelper service = getExperianRequestHelper();
                        service.addChangeSubscriptionRequest(email, SOURCE_WEB);
                    }	
                }
            } catch (Throwable e) {
                logError(e);
            }

            setNeedExperianNotify(false);
        }
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
}
