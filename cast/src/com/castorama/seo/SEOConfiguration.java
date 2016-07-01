package com.castorama.seo;

import atg.nucleus.GenericService;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class SEOConfiguration extends GenericService {
    /** SupportLegacyURLs property */
    private boolean mSupportLegacyURLs = true;

    /**Search placeholder property (using for configuration placeholder message) **/
    private String mSearchFormPlaceholder;

    /**Message property for case when there are no results for the search**/
    private String mNoResultsMessage;

    /**Message property for proposition to continue search**/
    private String mContinueSearchMessage;

    /**
     * Returns supportLegacyURLs property.
     *
     * @return true if the application supports legacy URLs.
     */
    public boolean isSupportLegacyURLs() {
        return mSupportLegacyURLs;
    }

    /**
     * Sets the value of the supportLegacyURLs property.
     *
     * @param pSupportLegacyURLs parameter to set.
     */
    public void setSupportLegacyURLs(boolean pSupportLegacyURLs) {
        mSupportLegacyURLs = pSupportLegacyURLs;
    }

    public String getSearchFormPlaceholder() {
        return mSearchFormPlaceholder;
    }

    public void setSearchFormPlaceholder(String mSearchFormPlaceholder) {
        this.mSearchFormPlaceholder = mSearchFormPlaceholder;
    }

    public String getNoResultsMessage() {
        return mNoResultsMessage;
    }

    public void setNoResultsMessage(String mNoResultsMessage) {
        this.mNoResultsMessage = mNoResultsMessage;
    }

    public String getContinueSearchMessage() {
        return mContinueSearchMessage;
    }

    public void setContinueSearchMessage(String continueSearchMessage) {
        this.mContinueSearchMessage = continueSearchMessage;
    }
}
