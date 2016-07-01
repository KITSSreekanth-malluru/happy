package com.castorama;

/**
 * Component for retrieving resource on UI
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastWebAppConfiguration {
    /** resourceBundle property. */
    private String mResourceBundle;

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

}
