package com.castorama.invite;

import static com.castorama.commerce.profile.ErrorCodeConstants.*;

import java.io.IOException;

import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;

import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.ProfileTools;

import com.castorama.scenario.InviteMessageSource;

import com.castorama.utils.Validator;

/**
 * Form handler for "Invite a friend" page
 *
 * @author Andrei_Raichonak
 */
public class InviteFormHanlder extends GenericFormHandler {
    /** RESOURCE_BUNDLE constant. */
    public static String RESOURCE_BUNDLE = "com.castorama.commerce.profile.CastProfileResources";

    /** bundle constant. */
    private static ResourceBundle bundle =
        ResourceUtils.getBundle(RESOURCE_BUNDLE, atg.service.dynamo.LangLicense.getLicensedDefault());

    /** inviteMessageSource property. */
    private InviteMessageSource mInviteMessageSource;

    /** profile property. */
    private RepositoryItem mProfile;

    /** email property. */
    private String mEmail;

    /** firstName property. */
    private String mFirstName;

    /** lastName property. */
    private String mLastName;

    /** errorURL property. */
    private String mErrorURL;

    /** successURL property. */
    private String mSuccessURL;

    /** profileTools property. */
    private ProfileTools mProfileTools;

    /**
     * Returns inviteMessageSource property.
     *
     * @return inviteMessageSource property.
     */
    public InviteMessageSource getInviteMessageSource() {
        return mInviteMessageSource;
    }

    /**
     * Sets the value of the inviteMessageSource property.
     *
     * @param pInviteMessageSource parameter to set.
     */
    public void setInviteMessageSource(InviteMessageSource pInviteMessageSource) {
        mInviteMessageSource = pInviteMessageSource;
    }

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public RepositoryItem getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param pProfile parameter to set.
     */
    public void setProfile(RepositoryItem pProfile) {
        mProfile = pProfile;
    }

    /**
     * Returns profileTools property.
     *
     * @return profileTools property.
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Sets the value of the profileTools property.
     *
     * @param profileTools parameter to set.
     */
    public void setProfileTools(ProfileTools profileTools) {
        mProfileTools = profileTools;
    }

    /**
     * Handles invitation
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws IOException exception
     * @throws ServletException 
     */
    public void handleInvite(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                      throws IOException, ServletException {
        if (validateForm(pRequest, pResponse)) {
            try {
                getInviteMessageSource().fireInviteMessage(getProfile(), getEmail(), getFirstName(), getLastName());
            } catch (Exception e) {
                logError(e);
            }

            checkFormRedirect(getSuccessURL(), getSuccessURL(), pRequest, pResponse);
        } else {
        	checkFormRedirect(getErrorURL(), getErrorURL(), pRequest, pResponse);
        }
    }

    /**
     * Validates the form
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected boolean validateForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        resetFormExceptions();

        if (StringUtils.isBlank(getEmail())) {
            addFormException(new DropletFormException(bundle.getString(MSG_MISSING_EMAIL_ADDRESS),
                                                      getAbsoluteName() + ".editValue."));
        } else if (!Validator.validateEmail(getEmail().trim())) {
            addFormException(new DropletFormException(bundle.getString(MSG_INCORRECT_EMAIL_ADDRESS),
                                                      getAbsoluteName() + ".editValue."));
        } else if (isExistingUser(getEmail().trim())) {
            addFormException(new DropletFormException(bundle.getString(MSG_EXISTING_ADDRESS),
                                                      getAbsoluteName() + ".editValue."));
        }
        if (StringUtils.isBlank(getLastName())) {
            addFormException(new DropletFormException(bundle.getString(MSG_MISSED_FRIEND_LAST_NAME),
                                                      getAbsoluteName() + ".editValue."));
        }
        if (StringUtils.isBlank(getFirstName())) {
            addFormException(new DropletFormException(bundle.getString(MSG_MISSED_FRIEND_FIRST_NAME),
                                                      getAbsoluteName() + ".editValue."));
        }
        return !getFormError();
    }

    /**
     * Checks if user with specified email is registered in the system
     *
     * @param  pEmail email to check against
     *
     * @return true if user with specified email exists
     */
    private boolean isExistingUser(String pEmail) {
        RepositoryItem user = mProfileTools.getItemFromEmail(pEmail);

        return (user != null);
    }

    /**
     * Returns lastName property.
     *
     * @return lastName property.
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Sets the value of the lastName property.
     *
     * @param pLastName parameter to set.
     */
    public void setLastName(String pLastName) {
        mLastName = pLastName;
    }

    /**
     * Returns email property.
     *
     * @return email property.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Sets the value of the email property.
     *
     * @param pEmail parameter to set.
     */
    public void setEmail(String pEmail) {
        mEmail = pEmail;
    }

    /**
     * Returns firstName property.
     *
     * @return firstName property.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Sets the value of the firstName property.
     *
     * @param pFirstName parameter to set.
     */
    public void setFirstName(String pFirstName) {
        mFirstName = pFirstName;
    }

    /**
     * Returns errorURL property.
     *
     * @return errorURL property.
     */
    public String getErrorURL() {
        return mErrorURL;
    }

    /**
     * Sets the value of the errorURL property.
     *
     * @param pErrorURL parameter to set.
     */
    public void setErrorURL(String pErrorURL) {
        mErrorURL = pErrorURL;
    }

    /**
     * Returns successURL property.
     *
     * @return successURL property.
     */
    public String getSuccessURL() {
        return mSuccessURL;
    }

    /**
     * Sets the value of the successURL property.
     *
     * @param pSuccessURL parameter to set.
     */
    public void setSuccessURL(String pSuccessURL) {
        mSuccessURL = pSuccessURL;
    }

}
