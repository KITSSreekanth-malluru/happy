package com.castorama.servlet;

import java.io.IOException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.pipeline.InsertableServletImpl;

import com.castorama.commerce.profile.SessionBean;
import com.castorama.seo.SEOUtils;

/**
 * Maintain tabs on search results page(save tabs in session) and cleans when
 * necessary.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class MultiFacetedSearchSessionCleanerServlet extends InsertableServletImpl {
    /**
     * Constant for left part of regular expression for retrieving source code
     * from request's queryString parameter.
     */
    public static final String REGEXP_PART_ONE = "[?&]";

    /**
     * Constant for right part of regular expression for retrieving source code
     * from request's queryString parameter.
     */
    public static final String REGEXP_PART_TWO = "=([^=&#]*)";

    /*
     * Open parameter names
     */
    /** referer constant */
    public static final String REFERER = "referer";

    /** facetedSearchSessionBeanPath constant */
    private String mFacetedSearchSessionBeanPath;

    /** safeReferers constant */
    private List<String> mSafeReferers;

    /** multiSearchType constant */
    private String mMultiSearchType;

    /**
     * Returns facetedSearchSessionBeanPath property
     *
     * @return the facetedSearchSessionBeanPath property
     */
    public String getFacetedSearchSessionBeanPath() {
        return mFacetedSearchSessionBeanPath;
    }

    /**
     * Sets facetedSearchSessionBeanPath property
     *
     * @param pFacetedSearchSessionBeanPath the facetedSearchSessionBeanPath to
     *                                      set
     */
    public void setFacetedSearchSessionBeanPath(String pFacetedSearchSessionBeanPath) {
        mFacetedSearchSessionBeanPath = pFacetedSearchSessionBeanPath;
    }

    /**
     * Returns safeReferers property
     *
     * @return the safeReferers property
     */
    public List<String> getSafeReferers() {
        return mSafeReferers;
    }

    /**
     * Sets safeReferers property
     *
     * @param pSafeReferers the safeReferers to set
     */
    public void setSafeReferers(List<String> pSafeReferers) {
        mSafeReferers = pSafeReferers;
    }

    /**
     * Returns multiSearchType property
     *
     * @return the multiSearchType property
     */
    public String getMultiSearchType() {
        return mMultiSearchType;
    }

    /**
     * Sets multiSearchType property
     *
     * @param pMultiSearchType the multiSearchType to set
     */
    public void setMultiSearchType(String pMultiSearchType) {
        mMultiSearchType = pMultiSearchType;
    }

    /**
     * Filter Faceted Session Bean values for removing extra parameters from
     * session bean.
     *
     * @param     pRequest  - http request
     * @param     pResponse - http response
     *
     * @exception IOException      if an error occurred while reading or writing
     *                             the servlet request
     * @exception ServletException if an error occurred while processing the
     *                             servlet request
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                 throws IOException, ServletException {
        String referer = pRequest.getHeader(REFERER);
        String pragma = pRequest.getHeader("pragma");
        String accept = pRequest.getHeader("accept");

        String cacheControl = pRequest.getHeader("cache-control");

        if (isLoggingDebug()) {
            logDebug("Referer " + referer);
        }

        if (!StringUtils.isBlank(referer) && (getSafeReferers() != null) && !getSafeReferers().isEmpty() &&
                !StringUtils.isBlank(getFacetedSearchSessionBeanPath()) &&
                (StringUtils.isBlank(pragma) || !pragma.equalsIgnoreCase("no-cache")) &&
                (StringUtils.isBlank(cacheControl) || !cacheControl.equalsIgnoreCase("no-cache")) &&
                !StringUtils.isBlank(accept) && accept.contains("text/html")) {
            boolean isSafeReferer = false;
            String currentSearchType = SEOUtils.getSearchType(pRequest.getRequestURI());
            String previousSearchType = SEOUtils.getSearchType(referer);
            

            // case referer from post method
            if (previousSearchType == null) {
                SessionBean sb = (SessionBean) pRequest.resolveName(getFacetedSearchSessionBeanPath().trim());
                if (sb != null) {
                    previousSearchType = (String) sb.getValues().get("previousSearchType");
                }
            }

            if ((currentSearchType != null) && (previousSearchType != null) &&
                    currentSearchType.equals(previousSearchType) && currentSearchType.equals(getMultiSearchType())) {
                for (String sr : getSafeReferers()) {
                    if (!StringUtils.isBlank(sr) && referer.contains(sr.trim()) &&
                            (referer.indexOf(sr.trim()) < referer.indexOf("?"))) {
                        isSafeReferer = true;

                        break;
                    }
                }
            }

            if (!isSafeReferer) {
                try {
                    SessionBean sb = (SessionBean) pRequest.resolveName(getFacetedSearchSessionBeanPath().trim());
                    if ((sb != null) && (sb.getValues() != null) && !sb.getValues().isEmpty()) {
                        sb.getValues().clear();
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }  // end catch
            }
        }  // end if

        passRequest(pRequest, pResponse);
    }
}
