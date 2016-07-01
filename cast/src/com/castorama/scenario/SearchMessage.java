package com.castorama.scenario;

import javax.servlet.http.HttpSession;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import atg.userprofiling.Profile;

/**
 * Message to be fired after user has run search
 *
 * @author Andrei_Raichonak
 */
public class SearchMessage implements java.io.Serializable {
    /** serialVersionUID constant. */

    private static final long serialVersionUID = 2636214612491093296L;

    /** profile property. */
    private RepositoryItem mProfile;

    /** searchQuery, sessionId, parentSessionId properties. */
    private String mSearchQuery, mSessionId, mParentSessionId;

    /**
     * Creates a new SearchMessage object.
     *
     * @param pProfile     parameter
     * @param pSearchQuery parameter
     */
    public SearchMessage(RepositoryItem pProfile, String pSearchQuery) {
        if (pProfile instanceof Profile) {
            mProfile = ((Profile) pProfile).getDataSource();
        } else {
            mProfile = pProfile;
        }

        mSearchQuery = pSearchQuery;

        DynamoHttpServletRequest request = null;

        if ((request = ServletUtil.getCurrentRequest()) != null) {
            setParentSessionId(ServletUtil.getParentSessionId(request));

            HttpSession session = null;

            if ((session = request.getSession(false)) != null) {
                setSessionId(session.getId());
            }
        }
    }

    /**
     * Returns subject property.
     *
     * @return subject property.
     */
    public RepositoryItem getSubject() {
        return mProfile;
    }

    /**
     * Returns referrer profile
     *
     * @return profile
     */
    public RepositoryItem getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param pReferrer parameter to set.
     */
    public void setProfile(RepositoryItem pReferrer) {
        mProfile = pReferrer;
    }

    /**
     * Returns profileId property.
     *
     * @return profileId property.
     */
    public String getProfileId() {
        if (mProfile != null) {
            return mProfile.getRepositoryId();
        }

        return null;
    }

    /**
     * Returns searchQuery property.
     *
     * @return searchQuery property.
     */
    public String getSearchQuery() {
        return mSearchQuery;
    }

    /**
     * Sets the value of the searchQuery property.
     *
     * @param pSearchQuery parameter to set.
     */
    public void setSearchQuery(String pSearchQuery) {
        mSearchQuery = pSearchQuery;
    }

    /**
     * Returns sessionId property.
     *
     * @return sessionId property.
     */
    public String getSessionId() {
        return mSessionId;
    }

    /**
     * Sets the value of the sessionId property.
     *
     * @param pSessionId parameter to set.
     */
    public void setSessionId(String pSessionId) {
        mSessionId = pSessionId;
    }

    /**
     * Returns parentSessionId property.
     *
     * @return parentSessionId property.
     */
    public String getParentSessionId() {
        return mParentSessionId;
    }

    /**
     * Sets the value of the parentSessionId property.
     *
     * @param pParentSessionId parameter to set.
     */
    public void setParentSessionId(String pParentSessionId) {
        mParentSessionId = pParentSessionId;
    }
}
