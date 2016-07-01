package com.castorama.scenario;

import javax.servlet.http.HttpSession;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import atg.userprofiling.Profile;

/**
 *
 * @author Andrei_Raichonak
 */
public class InviteMessage implements java.io.Serializable {
    /** serialVersionUID constant. */
    private static final long serialVersionUID = -3933115051771126661L;

    /** profile property. */
    private RepositoryItem mProfile;

    /** referee, sessionId, parentSessionId, firstName, lastName properties. */
    private String mReferee, mProfileId, mSessionId, mParentSessionId, mFirstName, mLastName;

    /**
     * Creates a new InviteMessage object.
     *
     * @param pProfile   Referrer profile
     * @param pReferee   Referee email
     * @param pFirstName Referee first name
     * @param pLastName  Referee last name
     */
    public InviteMessage(RepositoryItem pProfile, String pReferee, String pFirstName, String pLastName) {
        if (pProfile instanceof Profile) {
            mProfile = ((Profile) pProfile).getDataSource();
        } else {
            mProfile = pProfile;
        }
        mProfileId = pProfile.getRepositoryId();
        mReferee = pReferee;
        mFirstName = pFirstName;
        mLastName = pLastName;

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
     * Returns referrer profile id
     *
     * @return id
     */
    public String getProfileId() {        
        return mProfileId;
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
     * Returns referee email
     *
     * @return referee email
     */
    public String getReferee() {
        return mReferee;
    }

    /**
     * Sets referee email
     *
     * @param pReferee email
     */
    public void setReferee(String pReferee) {
        mReferee = pReferee;
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

    /**
     * Returns referee first name
     *
     * @return referee first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Sets referee first name
     *
     * @param pFirstName first name
     */
    public void setFirstName(String pFirstName) {
        mFirstName = pFirstName;
    }

    /**
     * Returns referee last name
     *
     * @return referee last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Sets referee last name
     *
     * @param pLastName last name
     */
    public void setLastName(String pLastName) {
        mLastName = pLastName;
    }
}
