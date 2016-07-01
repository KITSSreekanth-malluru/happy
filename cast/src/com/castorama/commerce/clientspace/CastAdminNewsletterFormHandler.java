/**
 * Bespoke - Newsletter Subscription Form handler class.
 */
package com.castorama.commerce.clientspace;

import java.io.IOException;

import java.util.Date;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.servlet.RepositoryFormHandler;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.ProfileTools;

import com.castorama.commerce.profile.Constants;

import com.castorama.integration.experian.ExperianRequestHelper;

import com.castorama.utils.Validator;

/**
 *
 * @author EPAM team
 */
public class CastAdminNewsletterFormHandler extends RepositoryFormHandler {
    /** ABONNEMENT_NEWSLETTER constant. */
    public static final String ABONNEMENT_NEWSLETTER = "abonnementNewsletter";

    /** MODIFICATION constant. */
    private static final Integer MODIFICATION = 1;

    /** VALUE_FALSE constant. */
    private static final String VALUE_FALSE = "false";

    /** VALUE_FALSE constant. */
    private static final String SOURCE_WEB = "web";

    /** VALUE_TRUE constant. */
    private static final String VALUE_TRUE = "true";

    /** mProfileTools constant. */
    private ProfileTools mProfileTools;

    /** mExperianRequestHelper constant. */
    private ExperianRequestHelper mExperianRequestHelper;

    /** mReceiveEmail constant. */
    private String mReceiveEmail;

    /** mReseiveOffers constant. */
    private String mReseiveOffers;

    /** mProfileId constant. */
    private String mProfileId;

    /**
     * Update subscription item.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUpdateNewsletter(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        if (isLoggingDebug()) {
            logDebug(">> CastAdminFormHandler.handleUpdateNewsletter()");
        }
        boolean erreur = false;
        try {
            if (null != getValue().get(Constants.EMAIL_NEWSLETTER_PROP)) {
                String email = getValue().get(Constants.EMAIL_NEWSLETTER_PROP).toString();
                if (!StringUtils.isEmpty(email)) {
                    MutableRepository newsletterRepo = getRepository();
                    MutableRepositoryItem newsletterItem =
                        newsletterRepo.getItemForUpdate(email, ABONNEMENT_NEWSLETTER);
                    if (newsletterItem != null) {
                        String oldReceiveEmail = null;
                        String oldReseiveOffers = null;
                        if (newsletterItem.getPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP) != null) {
                            oldReceiveEmail =
                                (String) newsletterItem.getPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP);
                        }
                        if (newsletterItem.getPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP) != null) {
                            oldReseiveOffers =
                                (String) newsletterItem.getPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP);
                        }

                        populateNewsletter(newsletterItem);
                        newsletterRepo.updateItem(newsletterItem);

                        updateProfile();

                        addExperianRequest(email, oldReceiveEmail, oldReseiveOffers);
                    } else {
                        if (isLoggingDebug()) {
                            logDebug("Newsletter Item not found by " + email);
                        }
                        pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=2", pRequest);
                        erreur = true;
                    }  // end if-else

                } else {
                    logError("getProfileTools() == null");
                }  // end if-else
            }  // end if
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }
        } catch (NullPointerException e) {
            if (isLoggingError()) {
                logError(e);
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }
        }  // end try-catch

        if (isLoggingDebug()) {
            logDebug(" << CastAdminFormHandler.handleUpdateNewsletter()");
        }

        if (erreur) {
            return true;
        } else {
            pResponse.sendLocalRedirect(getUpdateSuccessURL(), pRequest);
            return false;
        }
    }

    /**
     * Create subscription item
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleCreateNewsletter(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        if (isLoggingDebug()) {
            logDebug(">> CastAdminFormHandler.handleCreateNewsletter()");
        }
        boolean erreur = false;
        try {
            if (null != getValue().get(Constants.EMAIL_NEWSLETTER_PROP)) {
                String email = getValue().get(Constants.EMAIL_NEWSLETTER_PROP).toString();
                if (validateEmail(email)) {
                    MutableRepository newsletterRepo = getRepository();
                    RepositoryItem nItem = newsletterRepo.getItem(email, ABONNEMENT_NEWSLETTER);
                    if (nItem == null) {
                        MutableRepositoryItem newsletterItem = newsletterRepo.createItem(email, ABONNEMENT_NEWSLETTER);
                        newsletterItem.setPropertyValue("profile", getProfileId());
                        populateNewsletter(newsletterItem);
                        newsletterRepo.addItem(newsletterItem);

                        updateProfile();

                        addExperianRequest(email, null, null);
                    } else {
                        if (isLoggingDebug()) {
                            logDebug("Duplicate email.  " + email);
                        }
                        pResponse.sendLocalRedirect(getCreateErrorURL() + "&erreur=1", pRequest);
                        erreur = true;
                    }
                } else {
                    pResponse.sendLocalRedirect(getCreateErrorURL() + "&erreur=3", pRequest);
                    erreur = true;
                }  // end if-else
            } else {
                if (isLoggingDebug()) {
                    logDebug("Email is null.");
                }
                pResponse.sendLocalRedirect(getCreateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }  // end if-else
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
                pResponse.sendLocalRedirect(getCreateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }
        } catch (NullPointerException e) {
            if (isLoggingError()) {
                logError(e);
                pResponse.sendLocalRedirect(getCreateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }
        }  // end try-catch
        if (isLoggingDebug()) {
            logDebug("<< CastAdminFormHandler.handleCreateNewsletter()");
        }

        if (erreur) {
            return true;
        } else {
            pResponse.sendLocalRedirect(getCreateSuccessURL(), pRequest);
            return false;
        }
    }

    /**
     * Get class ProfileTools
     *
     * @return class ProfileTools
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Set class ProfileTools
     *
     * @param pProfileTools
     */
    public void setProfileTools(ProfileTools pProfileTools) {
        mProfileTools = pProfileTools;
    }

    /**
     * Get value property receiveEmail
     *
     * @return the receiveEmail
     */
    public String getReceiveEmail() {
        return mReceiveEmail;
    }

    /**
     * Set property receiveEmail
     *
     * @param pReceiveEmail
     */
    public void setReceiveEmail(String pReceiveEmail) {
        mReceiveEmail = pReceiveEmail;
    }

    /**
     * Returns reseiveOffers property.
     *
     * @return reseiveOffers property.
     */
    public String getReseiveOffers() {
        return mReseiveOffers;
    }

    /**
     * Sets the value of the reseiveOffers property.
     *
     * @param pReseiveOffers parameter to set.
     */
    public void setReseiveOffers(String pReseiveOffers) {
        mReseiveOffers = pReseiveOffers;
    }

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
     * Returns experianRequestHelper property.
     *
     * @return experianRequestHelper property.
     */
    public ExperianRequestHelper getExperianRequestHelper() {
        return mExperianRequestHelper;
    }

    /**
     * Sets the value of the experianRequestHelper property.
     *
     * @param pExperianRequestHelper parameter to set.
     */
    public void setExperianRequestHelper(ExperianRequestHelper pExperianRequestHelper) {
        mExperianRequestHelper = pExperianRequestHelper;
    }

    /**
     *
     *
     * @param pNewsletterItem parameter
     */
    private void populateNewsletter(MutableRepositoryItem pNewsletterItem) {
        Date currentDate = new Date();
        String strValue = "";
        if (VALUE_TRUE.equals(getReceiveEmail())) {
            pNewsletterItem.setPropertyValue(Constants.DATE_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
            strValue = VALUE_TRUE;
        } else if (VALUE_FALSE.equals(getReceiveEmail())) {
            pNewsletterItem.setPropertyValue(Constants.DATE_UNSUBSCRIBE_NEWSLETTER_PROP, currentDate);
            strValue = VALUE_FALSE;
        }
        pNewsletterItem.setPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP, strValue);

        strValue = "";
        if (VALUE_TRUE.equals(getReseiveOffers())) {
            pNewsletterItem.setPropertyValue(Constants.DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
            strValue = VALUE_TRUE;
        } else if (VALUE_FALSE.equals(getReseiveOffers())) {
            pNewsletterItem.setPropertyValue(Constants.DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP, currentDate);
            strValue = VALUE_FALSE;
        }
        pNewsletterItem.setPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP, strValue);

        pNewsletterItem.setPropertyValue(Constants.DATE_DERNIERE_MODIF_NEWSLETTER_PROP, currentDate);
    }

    /**
     *
     *
     * @throws RepositoryException exception
     */
    private void updateProfile() throws RepositoryException {
        MutableRepository repository = getProfileTools().getProfileRepository();
        MutableRepositoryItem mutableItem = repository.getItemForUpdate(getProfileId(), "user");

        // BDDCC G.V.
        mutableItem.setPropertyValue(Constants.UPDATE_TYPE_PROFILE_PROP, MODIFICATION);
        mutableItem.setPropertyValue("dateMAJprofil", new Date());

        repository.updateItem(mutableItem);
    }

    /**
     *
     *
     * @param  email parameter
     *
     * @return
     */
    private boolean validateEmail(String email) {
        if (!StringUtils.isEmpty(email)) {
            return Validator.validateEmail(email);
        }
        return false;
    }

    /**
     *
     *
     * @param  pEmail            parameter
     * @param  pOldReceiveEmail  parameter
     * @param  pOldReseiveOffers parameter
     *
     * @throws RepositoryException  exception
     * @throws NullPointerException exception
     */
    private void addExperianRequest(String pEmail, String pOldReceiveEmail, String pOldReseiveOffers)
                            throws RepositoryException, NullPointerException {
        if (isLoggingDebug()) {
            logDebug("addExperianRequest: email=" + pEmail + "; oldReceiveEmail=" + pOldReceiveEmail +
                     "; oldReceiveEmail=" + pOldReseiveOffers);
        }
        boolean createRequest = false;
        boolean removeRequest = false;
        if (pOldReceiveEmail == null) {
            createRequest = VALUE_TRUE.equals(getReceiveEmail());
            removeRequest = VALUE_FALSE.equals(getReceiveEmail());
        } else {
            createRequest =
                (!pOldReceiveEmail.equals(getReceiveEmail()) && VALUE_TRUE.equals(getReceiveEmail())) ? true : false;
            removeRequest =
                (!pOldReceiveEmail.equals(getReceiveEmail()) && VALUE_FALSE.equals(getReceiveEmail())) ? true : false;
        }

        boolean createRequestOffers = false;
        boolean removeRequestOffers = false;
        if (pOldReseiveOffers == null) {
            createRequestOffers = VALUE_TRUE.equals(getReseiveOffers());
            removeRequestOffers = VALUE_FALSE.equals(getReseiveOffers());
        } else {
            createRequestOffers =
                (!pOldReceiveEmail.equals(getReseiveOffers()) && VALUE_TRUE.equals(getReseiveOffers())) ? true : false;
            removeRequestOffers =
                (!pOldReceiveEmail.equals(getReseiveOffers()) && VALUE_FALSE.equals(getReseiveOffers())) ? true : false;
        }

        if (isLoggingDebug()) {
            logDebug("createRequest=" + createRequest + "; removeRequest=" + removeRequest);
            logDebug("createRequestOffers=" + createRequestOffers + "; removeRequestOffers=" + removeRequestOffers);
        }
        if (createRequest || createRequestOffers) {
            if (isLoggingDebug()) {
                logDebug("sendCreateSubscriptionRequest");
            }
            getExperianRequestHelper().addCreateSubscriptionRequest(pEmail, SOURCE_WEB);
        }

        if (removeRequest || removeRequestOffers) {
            if (isLoggingDebug()) {
                logDebug("sendRemoveSubscriptionRequest");
            }
            getExperianRequestHelper().addRemoveSubscriptionRequest(pEmail, SOURCE_WEB);
        }
    }

}
