package atg.mobile;

import atg.nucleus.GenericService;

/**
 * Sets some properties for mobile application.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class MobileConfiguration extends GenericService {
    /** mResourceBundle constant. */
    private String mResourceBundle;

    /** mServerVersion constant. */
    private String mServerVersion;

    /** mIphoneUrlPrefix constant. */
    private String mIphoneUrlPrefix;

    /** mIphoneCurrentAppVersion constant. */
    private String mIphoneCurrentAppVersion;

    /** mIphoneMinimumSupportedAppVersion constant. */
    private String mIphoneMinimumSupportedAppVersion;

    /** mRetinaDynamicImages property - default value is true. */
    private boolean mRetinaDynamicImages = true;

    /**
     * Returns resourceBundle property.
     *
     * @return resourceBundle property.
     */
    public String getResourceBundle() {
        return mResourceBundle;
    }

    /**
     * Sets the value of the resourceBundle property.
     *
     * @param pResourceBundle parameter to set.
     */
    public void setResourceBundle(String pResourceBundle) {
        mResourceBundle = pResourceBundle;
    }

    /**
     * Returns serverVersion property.
     *
     * @return serverVersion property.
     */
    public String getServerVersion() {
        return mServerVersion;
    }

    /**
     * Sets the value of the serverVersion property.
     *
     * @param pServerVersion parameter to set.
     */
    public void setServerVersion(String pServerVersion) {
    }

    /**
     * Returns iphoneUrlPrefix property.
     *
     * @return iphoneUrlPrefix property.
     */
    public String getIphoneUrlPrefix() {
        return mIphoneUrlPrefix;
    }

    /**
     * Sets the value of the iphoneUrlPrefix property.
     *
     * @param pIphoneUrlPrefix parameter to set.
     */
    public void setIphoneUrlPrefix(String pIphoneUrlPrefix) {
        mIphoneUrlPrefix = pIphoneUrlPrefix;
    }

    /**
     * Returns iphoneCurrentAppVersion property.
     *
     * @return iphoneCurrentAppVersion property.
     */
    public String getIphoneCurrentAppVersion() {
        return mIphoneCurrentAppVersion;
    }

    /**
     * Sets the value of the iphoneCurrentAppVersion property.
     *
     * @param pIphoneCurrentAppVersion parameter to set.
     */
    public void setIphoneCurrentAppVersion(String pIphoneCurrentAppVersion) {
        mIphoneCurrentAppVersion = pIphoneCurrentAppVersion;
    }

    /**
     * Returns iphoneMinimumSupportedAppVersion property.
     *
     * @return iphoneMinimumSupportedAppVersion property.
     */
    public String getIphoneMinimumSupportedAppVersion() {
        return mIphoneMinimumSupportedAppVersion;
    }

    /**
     * Sets the value of the iphoneMinimumSupportedAppVersion property.
     *
     * @param pIphoneMinimumSupportedAppVersion parameter to set.
     */
    public void setIphoneMinimumSupportedAppVersion(String pIphoneMinimumSupportedAppVersion) {
        mIphoneMinimumSupportedAppVersion = pIphoneMinimumSupportedAppVersion;
    }

    /**
     * Returns retinaDynamicImages property.
     *
     * @return retinaDynamicImages property.
     */
    public boolean isRetinaDynamicImages() {
        return mRetinaDynamicImages;
    }

    /**
     * Sets the value of the retinaDynamicImages property.
     *
     * @param pRetinaDynamicImages parameter to set.
     */
    public void setRetinaDynamicImages(boolean pRetinaDynamicImages) {
        mRetinaDynamicImages = pRetinaDynamicImages;
    }

}
