package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.scenario.SearchMessage;
import com.castorama.scenario.SearchMessageSource;

/**
 * This droplet sends {@link SearchMessage}.
 *
 * @author Epam Team
 */
public class SearchMessageDroplet extends DynamoServlet {
    /** query param name. */
    private static final String QUERY_PARAM = "query";

    /** Messahe Sender. */
    private SearchMessageSource mSearchMessageSource;

    /** Profile. */
    private String mProfile;

    /** enable/disable TypeAheadRepository update. */
    private boolean mTypeAheadEnable;

    /**
     * Gets {@link #QUERY_PARAM} add it into {@link SearchMessage} and fire the
     * sending
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException an application specific error occurred
     *                             processing this request
     * @exception IOException      an error occurred reading data from the
     *                             request or writing data to the response.
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        if (isTypeAheadEnable()) {
	        String query = pRequest.getParameter(QUERY_PARAM);
	        if (!StringUtils.isBlank(getProfile())) {
	            RepositoryItem profile = (RepositoryItem) pRequest.resolveName(getProfile());
	            if ((profile != null) && (query != null) && (query.length() > 0)) {
	                int maxLength = mSearchMessageSource.getQueryMaxLength();
	                if (query.length()>maxLength){
	                    query = query.substring(0, maxLength);
	                }
	                getSearchMessageSource().fireSearchMessage(profile, query);
	            }
	        }
    	}
    }

    /**
     * Gets SearchMessageSource class instance.
     *
     * @return the mSearchMessageSource
     */
    public SearchMessageSource getSearchMessageSource() {
        return mSearchMessageSource;
    }

    /**
     * Sets SearchMessageSource class instance.
     *
     * @param searchMessageSource the mSearchMessageSource to set
     */
    public void setSearchMessageSource(SearchMessageSource searchMessageSource) {
        mSearchMessageSource = searchMessageSource;
    }

    /**
     * Gets profile.
     *
     * @return profile
     */
    public String getProfile() {
        return mProfile;
    }

    /**
     * Sets profile.
     *
     * @param pProfile to set
     */
    public void setProfile(String pProfile) {
        mProfile = pProfile;
    }

	/**
	 * @return the typeAheadEnable
	 */
	public boolean isTypeAheadEnable() {
		return mTypeAheadEnable;
	}

	/**
	 * @param typeAheadEnable the typeAheadEnable to set
	 */
	public void setTypeAheadEnable(boolean typeAheadEnable) {
		this.mTypeAheadEnable = typeAheadEnable;
	}
}
