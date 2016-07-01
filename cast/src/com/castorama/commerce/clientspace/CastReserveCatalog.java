package com.castorama.commerce.clientspace;

import static com.castorama.commerce.profile.ErrorCodeConstants.*;
import static com.castorama.commerce.profile.Constants.*;

import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.repository.servlet.RepositoryFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.Profile;

import com.castorama.commerce.profile.CastAddressHelper;

import com.castorama.utils.CommonHelper;
import com.castorama.utils.Validator;

/**
 * FormHandler class for catalog reservation.
 *
 * @author Katsiaryna_Dmitrievich
 */
public class CastReserveCatalog extends RepositoryFormHandler {
    /** Address Helper. */
    private CastAddressHelper mAddressHelper;

    /** Civilite filed - for name prefix. */
    private String civilite;

    /** Prenom filed - for first name. */
    private String prenom;

    /** Nom filed - for last name. */
    private String nom;

    /** Address 1. */
    private String adresse;

    /** Address 2. */
    private String adresse2;

    /** Address 3. */
    private String adresse3;

    /** Address 4. */
    private String adresse4;

    /** Postal Code filed. */
    private String codePostal;

    /** City filed. */
    private String ville;

    /** State field (for country code). */
    private String state;

    /** Country name field. */
    private String pays;

    /** Phone number 1 filed. */
    private String phoneNumber1;

    /** Phone number 2 field. */
    private String phoneNumber2;

    /** Current Profile. */
    private Profile mProfile;

    /** Newsletter profile. */
    private CastNewsletterFormHandler mNewsletterProfile;

    /** Common helper component. */
    private CommonHelper commonHelper;

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
     * Gets newsletter profile form handler.
     *
     * @return the mNewsletterProfile
     */
    public CastNewsletterFormHandler getNewsletterProfile() {
        return mNewsletterProfile;
    }

    /**
     * Sets newsletter profuke form handler.
     *
     * @param newsletterProfile the mNewsletterProfile to set
     */
    public void setNewsletterProfile(CastNewsletterFormHandler newsletterProfile) {
        mNewsletterProfile = newsletterProfile;
    }

    /**
     * Gets code postal error flag.
     *
     * @return the mFlagErrorCp
     */
    public boolean isFlagErrorCp() {
        return getAddressHelper().isFlagErrorCp();
    }

    /**
     * Sets code postal error flag.
     *
     * @param flagErrorCp the mFlagErrorCp to set
     */
    public void setFlagErrorCp(boolean flagErrorCp) {
        getAddressHelper().setFlagErrorCp(flagErrorCp);
    }

    /**
     * Create map with address properties.
     *
     * @return map with address properties
     */
    public Map<String, String> convertAddress() {
        Map<String, String> address = new HashMap<String, String>();
        address.put(PREFIX_ADDRESS_PROP, getCivilite());
        address.put(FIRST_NAME_ADDRESS_PROP, getPrenom());
        address.put(LAST_NAME_ADDRESS_PROP, getNom());

        address.put(ADDRESS_1_ADDRESS_PROP, getAdresse());
        address.put(ADDRESS_2_ADDRESS_PROP, getAdresse2());
        address.put(ADDRESS_3_ADDRESS_PROP, getAdresse3());
        address.put(LOCALITY_ADDRESS_PROP, getAdresse4());
        address.put(POSTAL_CODE_ADDRESS_PROP, getCodePostal());
        address.put(CITY_ADDRESS_PROP, getVille());
        address.put(STATE_ADDRESS_PROP, getState());
        address.put(PHONE_NUMBER_ADDRESS_PROP, getPhoneNumber1());
        address.put(PHONE_NUMBER_2_ADDRESS_PROP, getPhoneNumber2());
        return address;
    }

    /**
     * Validate email.
     *
     * @param  email - email to validate
     * @param  check - flag shows if email should be validated.
     *
     * @return <code>true</code> if it is not empty and correct, <code>
     *         false</code> otherwise.
     */
    public boolean validateEmail(final String email, final boolean check) {
        boolean result = false;
        if (!check) {
            result = true;
        } else {
            if (StringUtils.isBlank(email)) {
                getCommonHelper().generateFormException(MSG_MISSED_CAT_RES_EMAIL, this, RESOURCE_BUNDLE);
            } else {
                if (Validator.validateEmail(email)) {
                    result = true;
                } else {
                    getCommonHelper().generateFormException(MSG_INCORRECT_EMAIL_ADDRESS, this, RESOURCE_BUNDLE);
                }
            }
        }
        return result;
    }

    /**
     * Reserve catalog. Validate entered data before reservation. If data is not
     * valid redirects {@link #getCreateErrorURL()} otherwise redirects on
     * {@link #getCreateSuccessURL()}
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @return <code>false</code> if redirects successfully, <code>true</code>
     *         otherwise
     *
     * @throws ServletException if servlet exception occured
     * @throws IOException      if servlet io exception occured
     */
    @Override public boolean handleCreate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        final Map<String, String> account = convertAddress();
        final String email = (String) getValueProperty(EMAIL_RESERVATION_FIELD);
        final CastNewsletterFormHandler newsletterProfile = getNewsletterProfile();
		final boolean subscribe = newsletterProfile.isSubscribe();
        if (validateEmail(email, subscribe) & getAddressHelper().validateNameAddress(account, this) &
                getAddressHelper().validateAddress(account, this) & newsletterProfile.validateNewsletterStore(this) &
                newsletterProfile.validateAdditionalInfo(this)) {
            setAddress(account);
            String oldRepId = existSubscriptionId(email);
            if (oldRepId != null) {
                setRepositoryId(oldRepId);
                super.handleUpdate(pRequest, pResponse);
            } else {
                super.handleCreate(pRequest, pResponse);
            }
            if (!getFormError()) {
                if (subscribe) {
                	newsletterProfile.setFromCatalogReservation(true);
                    newsletterProfile.handleCreateFromProfile(pRequest, pResponse, email);
                }
                return checkFormRedirect(getCreateSuccessURL(), getCreateSuccessURL(), pRequest, pResponse);
            } else {
            	if ( getCommonHelper().ifDbError(this)) {
            		setCreateErrorURL(getCommonHelper().getDbErrorURL());
                }
                return checkFormRedirect(getCreateErrorURL(), getCreateErrorURL(), pRequest, pResponse);
            }
        } else {
            return checkFormRedirect(getCreateErrorURL(), getCreateErrorURL(), pRequest, pResponse);
        }  // end if-else
    }

    /**
     * Set address properties into reservation profile from address map.
     *
     * @param address - map with address properties.
     */
    private void setAddress(final Map<String, String> address) {
        setValueProperty(TITLE_RESERVATION_FIELD, address.get(PREFIX_ADDRESS_PROP));
        setValueProperty(FIRST_NAME_RESERVATION_FIELD, address.get(FIRST_NAME_ADDRESS_PROP));
        setValueProperty(LAST_NAME_RESERVATION_FIELD, address.get(LAST_NAME_ADDRESS_PROP));

        setValueProperty(ADDRESS_1_RESERVATION_FIELD, address.get(ADDRESS_1_ADDRESS_PROP));
        setValueProperty(ADDRESS_2_RESERVATION_FIELD, address.get(ADDRESS_2_ADDRESS_PROP));
        setValueProperty(ADDRESS_3_RESERVATION_FIELD, address.get(ADDRESS_3_ADDRESS_PROP));
        setValueProperty(ADDRESS_4_RESERVATION_FIELD, address.get(LOCALITY_ADDRESS_PROP));
        setValueProperty(POSTAL_CODE_RESERVATION_FIELD, address.get(POSTAL_CODE_ADDRESS_PROP));
        setValueProperty(CITY_RESERVATION_FIELD, address.get(CITY_ADDRESS_PROP));
        setValueProperty(COUNTRY_RESERVATION_FIELD, address.get(STATE_ADDRESS_PROP));
        setValueProperty(PHONE_NUMBER_RESERVATION_FIELD, address.get(PHONE_NUMBER_ADDRESS_PROP));
        setValueProperty(PHONE_NUMBER_2_RESERVATION_FIELD, address.get(PHONE_NUMBER_2_ADDRESS_PROP));
        setValueProperty(DATE_RESERVATION_FIELD, new Date());
    }

    /**
     * Checks if item with email=<code>email</code> exist in repository
     *
     * @param  email - searching email address
     *
     * @return <code>repositoryId</code> of the first item with such email,
     *         <code>null</code> otherwise
     *
     * @throws ServletException
     */
    private String existSubscriptionId(final String email) throws ServletException {
        String result = null;
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            final Repository repos = getRepository();
            try {
                final RqlStatement findEmailRQL = RqlStatement.parseRqlStatement("email = ?0");
                final RepositoryView emailView = repos.getView(getItemDescriptorName());
                Object[] rqlparams = new Object[1];
                rqlparams[0] = email;
                final RepositoryItem[] emailList = findEmailRQL.executeQuery(emailView, rqlparams);

                if ((emailList != null) && (emailList.length > 0)) {
                    result = emailList[0].getRepositoryId();
                }
            } catch (RepositoryException e) {
                if (isLoggingDebug()) {
                    logDebug(e.getCause());
                }
            }
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
        return result;
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
     * Sets profile
     *
     * @param profile the mProfile to set
     */
    public void setProfile(Profile profile) {
        mProfile = profile;
    }

    /**
     * Gets prefix.
     *
     * @return the civilite
     */
    public String getCivilite() {
        if ((civilite == null) && (getProfile() != null) && !getProfile().isTransient()) {
            civilite = (String) getProfile().getPropertyValue(TITLE_PROFILE_PROP);
        }
        return civilite;
    }

    /**
     * Sets prefix
     *
     * @param civilite the civilite to set
     */
    public void setCivilite(String civilite) {
        this.civilite = civilite;
    }

    /**
     * Gets first name.
     *
     * @return the prenom
     */
    public String getPrenom() {
        if ((prenom == null) && (getProfile() != null) && !getProfile().isTransient()) {
            prenom = (String) getProfile().getPropertyValue(FIRST_NAME_PROFILE_PROP);
        }
        return prenom;
    }

    /**
     * Sets first name.
     *
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Gets last name.
     *
     * @return the nom
     */
    public String getNom() {
        if ((nom == null) && (getProfile() != null) && !getProfile().isTransient()) {
            nom = (String) getProfile().getPropertyValue(LAST_NAME_PROFILE_PROP);
        }
        return nom;
    }

    /**
     * Sets last name.
     *
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Gets address 1.
     *
     * @return the adresse
     */
    public String getAdresse() {
        if ((adresse == null) && (getProfile() != null) && !getProfile().isTransient()) {
            adresse = getProfileAddressPropertyValue(ADDRESS_1_ADDRESS_PROP);
        }
        return adresse;
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
     * Sets address 1.
     *
     * @param adresse the adresse to set
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * Gets Address 2.
     *
     * @return the adresse2
     */
    public String getAdresse2() {
        if ((adresse2 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            adresse2 = getProfileAddressPropertyValue(ADDRESS_2_ADDRESS_PROP);
        }
        return adresse2;
    }

    /**
     * Sets address 2.
     *
     * @param adresse2 the adresse2 to set
     */
    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    /**
     * Gets address 3.
     *
     * @return the adresse3
     */
    public String getAdresse3() {
        if ((adresse3 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            adresse3 = getProfileAddressPropertyValue(ADDRESS_3_ADDRESS_PROP);
        }
        return adresse3;
    }

    /**
     * Sets address 3.
     *
     * @param adresse3 the adresse3 to set
     */
    public void setAdresse3(String adresse3) {
        this.adresse3 = adresse3;
    }

    /**
     * Gets address 4.
     *
     * @return the adresse4
     */
    public String getAdresse4() {
        if ((adresse4 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            adresse4 = getProfileAddressPropertyValue(LOCALITY_ADDRESS_PROP);
        }
        return adresse4;
    }

    /**
     * Sets address 4.
     *
     * @param adresse4 the adresse4 to set
     */
    public void setAdresse4(String adresse4) {
        this.adresse4 = adresse4;
    }

    /**
     * Gets Postal Code.
     *
     * @return the codePostal
     */
    public String getCodePostal() {
        if ((codePostal == null) && (getProfile() != null) && !getProfile().isTransient()) {
            codePostal = getProfileAddressPropertyValue(POSTAL_CODE_ADDRESS_PROP);
        }
        return codePostal;
    }

    /**
     * Sets Postal Code.
     *
     * @param codePostal the codePostal to set
     */
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    /**
     * Gets city.
     *
     * @return the ville
     */
    public String getVille() {
        if ((ville == null) && (getProfile() != null) && !getProfile().isTransient()) {
            ville = getProfileAddressPropertyValue(CITY_ADDRESS_PROP);
        }
        return ville;
    }

    /**
     * Sets city.
     *
     * @param ville the ville to set
     */
    public void setVille(String ville) {
        this.ville = ville;
    }

    /**
     * Gets country name.
     *
     * @return the pays
     */
    public String getPays() {
        if ((pays == null) && (getProfile() != null) && !getProfile().isTransient()) {
            pays = getProfileAddressPropertyValue(STATE_ADDRESS_PROP);
        }
        return pays;
    }

    /**
     * Sets country name.
     *
     * @param pays the pays to set
     */
    public void setPays(String pays) {
        this.pays = pays;
    }

    /**
     * Gets phone number 1.
     *
     * @return the phoneNumber1
     */
    public String getPhoneNumber1() {
        if ((phoneNumber1 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            phoneNumber1 = getProfileAddressPropertyValue(PHONE_NUMBER_ADDRESS_PROP);
        }
        return phoneNumber1;
    }

    /**
     * Sets phone number 1.
     *
     * @param phoneNumber1 the phoneNumber1 to set
     */
    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    /**
     * Gets Phone Number 2.
     *
     * @return the phoneNumber2
     */
    public String getPhoneNumber2() {
        if ((phoneNumber2 == null) && (getProfile() != null) && !getProfile().isTransient()) {
            phoneNumber2 = getProfileAddressPropertyValue(PHONE_NUMBER_2_ADDRESS_PROP);
        }
        return phoneNumber2;
    }

    /**
     * Sets Phone Number 2.
     *
     * @param phoneNumber2 the phoneNumber2 to set
     */
    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    /**
     * Gets State  - country code.
     *
     * @return the state
     */
    public String getState() {
        if ((state == null) && (getProfile() != null) && !getProfile().isTransient()) {
            state = getProfileAddressPropertyValue(STATE_ADDRESS_PROP);
        }
        return state;
    }

    /**
     * Sets state - country code.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
