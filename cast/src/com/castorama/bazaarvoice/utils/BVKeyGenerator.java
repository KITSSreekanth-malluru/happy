package com.castorama.bazaarvoice.utils;

import atg.nucleus.GenericService;

import atg.userprofiling.Profile;

/**
 * BV key generator
 *
 * @author EPAM team
 */
public class BVKeyGenerator extends GenericService {
    /** profile property. */
    private Profile mProfile;

    /** sharedKey property. */
    private String mSharedKey;

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
        mProfile = pProfile;
    }

    /**
     * Returns sharedKey property.
     *
     * @return sharedKey property.
     */
    public String getSharedKey() {
        return mSharedKey;
    }

    /**
     * Sets the value of the sharedKey property.
     *
     * @param pSharedKey parameter to set.
     */
    public void setSharedKey(String pSharedKey) {
        mSharedKey = pSharedKey;
    }

    /**
     * Returns key property.
     *
     * @return key property.
     */
    public String getKey() {
        return BazaarVoiceUtils.encodeUserID(getUser(), getUserEmail(), mSharedKey);
    }

    /**
     * Sets the value of the key property.
     *
     * @param  key parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setKey(String key) {
        throw new UnsupportedOperationException(getClass().getName() + ".setKey(String)");
    }

    /**
     * Get current logged in user name.
     *
     * @return
     */
    private String getUser() {
        try {
            return (String) getProfile().getPropertyValue("id");
        } catch (Throwable e) {
            return null;
        }
    }
    
    /**
     * Get current logged in user email.
     *
     * @return
     */
    private String getUserEmail() {
        try {
            return (String) getProfile().getPropertyValue("email");
        } catch (Throwable e) {
            return null;
        }
    }
}
