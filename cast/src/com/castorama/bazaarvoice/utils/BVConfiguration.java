package com.castorama.bazaarvoice.utils;

import com.castorama.utils.ServerSetting;

import atg.nucleus.GenericService;

/**
 * Configuration class for BazaarVoice.
 *
 * @author EPAM team
 */
public class BVConfiguration extends GenericService {
    /** serverSetting property. */
    private ServerSetting mServerSetting;

    /** bvRoot property. */
    private String mBvRoot;

    /** documentDomain property. */
    private String mDocumentDomain;

    /** bvDisplayCode property. */
    private String mBvDisplayCode;

    /** submitUrl property. */
    private String mSubmitUrl;

    /**
     * Returns serverSetting property.
     *
     * @return serverSetting property.
     */
    public ServerSetting getServerSetting() {
        return mServerSetting;
    }

    /**
     * Sets the value of the serverSetting property.
     *
     * @param pServerSetting parameter to set.
     */
    public void setServerSetting(ServerSetting pServerSetting) {
        mServerSetting = pServerSetting;
    }

    /**
     * Returns bvRoot property.
     *
     * @return bvRoot property.
     */
    public String getBvRoot() {
        return mBvRoot;
    }

    /**
     * Sets the value of the bvRoot property.
     *
     * @param pBvRoot parameter to set.
     */
    public void setBvRoot(String pBvRoot) {
        mBvRoot = pBvRoot;
    }

    /**
     * Returns documentDomain property.
     *
     * @return documentDomain property.
     */
    public String getDocumentDomain() {
        return mDocumentDomain;
    }

    /**
     * Sets the value of the documentDomain property.
     *
     * @param pDocumentDomain parameter to set.
     */
    public void setDocumentDomain(String pDocumentDomain) {
        mDocumentDomain = pDocumentDomain;
    }

    /**
     * Returns bvDisplayCode property.
     *
     * @return bvDisplayCode property.
     */
    public String getBvDisplayCode() {
        return mBvDisplayCode;
    }

    /**
     * Sets the value of the bvDisplayCode property.
     *
     * @param pBvDisplayCode parameter to set.
     */
    public void setBvDisplayCode(String pBvDisplayCode) {
        mBvDisplayCode = pBvDisplayCode;
    }

    /**
     * Returns submitUrl property.
     *
     * @return submitUrl property.
     */
    public String getSubmitUrl() {
        if (mSubmitUrl.startsWith("http://") || mSubmitUrl.startsWith("https://")) {
            return mSubmitUrl;
        } else {
            return getServerSetting().getHost() + mSubmitUrl;
        }
    }

    /**
     * Sets the value of the submitUrl property.
     *
     * @param pSubmitUrl parameter to set.
     */
    public void setSubmitUrl(String pSubmitUrl) {
        mSubmitUrl = pSubmitUrl;
    }
}
