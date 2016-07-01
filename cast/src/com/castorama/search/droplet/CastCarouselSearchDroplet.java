package com.castorama.search.droplet;

import java.io.IOException;

import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.commerce.search.refinement.CommerceFacetSearchTools;

import atg.core.util.StringUtils;

import atg.nucleus.Nucleus;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.search.formhandlers.QueryFormHandler;

import atg.search.routing.command.search.QueryRequest;
import atg.search.routing.command.search.Result;
import atg.search.routing.command.search.SearchResultsResponse;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;

import com.castorama.commerce.profile.SessionBean;

/**
 * Retrieves all product for carousel based on passed parameters.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastCarouselSearchDroplet extends DynamoServlet {
    /*
     * Open parameters
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** PIVOT_CATEGORY_ID constant. */
    public static final String PIVOT_CATEGORY_ID = "pivotCategoryId";

    /** DOCUMENT constant. */
    public static final String DOCUMENT = "document";

    /** TRAIL constant. */
    public static final String TRAIL = "trail";

    /** QUESTION constant. */
    public static final String QUESTION = "question";

    /** FOUND_PRODUCT_IDS constant. */
    public static final String FOUND_PRODUCT_IDS = "foundProductIds";

    /** PRODUCT_ID constant. */
    public static final String PRODUCT_ID = "productId";

    /** PREVIOUS_TRAIL constant. */
    public static final String PREVIOUS_TRAIL = "previousTrail";

    /** CATEGORY_FACET_ID constant. */
    public static final String CATEGORY_FACET_ID = "categoryFacetId";

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

    /** REFERER constant. */
    public static final String REFERER = "referer";

    /** DOC_SORT_MODE constant. */
    public static final String DOC_SORT_MODE = "docSortMode";

    /** DOC_SORT_ORDER constant. */
    public static final String DOC_SORT_ORDER = "docSortOrder";

    /** DOC_SORT_PROP constant. */
    public static final String DOC_SORT_PROP = "docSortProp";

    /** UNDERSCORE constant. */
    public static final String UNDERSCORE = "_";

    /** REPOSITORY_ID constant. */
    public static final String REPOSITORY_ID = "$repositoryId";

    /** COLON constant. */
    public static final String COLON = ":";

    /** ENCODING_UTF constant. */
    public static final String ENCODING_UTF = "UTF-8";

    /** FACETED_SEARCH_QUERY_STRING constant. */
    public static final String FACETED_SEARCH_QUERY_STRING = "facetedSearchQueryString";

    /** CATEGORIES constant. */
    public static final String CATEGORIES = "categories";

    /** cache property */
    private Hashtable<String, CacheDataEntry> mCache = new Hashtable<String, CacheDataEntry>();

    /** defaultCacheCheckSeconds property */
    private int mDefaultCacheCheckSeconds = 60;

    /*
     * Properties
     */
    /** carouselFHName property */
    private String mCarouselFHName;

    /** carouselFSTName property */
    private String mCarouselFSTName;

    /** carouselSBName property */
    private String mCarouselSBName;

    /** trailParameterName property */
    private String mTrailParameterName = "trail";

    /** questionParameterName property */
    private String mQuestionParameterName = "question";

    /** searchTypeParameterName property */
    private String mSearchTypeParameterName = "searchType";

    /** questionFacetId property */
    private String mQuestionFacetId = "SRCH";

    /**
     * Returns carouselFHName property
     *
     * @return the carouselFHName property
     */
    public String getCarouselFHName() {
        return mCarouselFHName;
    }

    /**
     * Sets the value of the carouselFHName property.
     *
     * @param pCarouselFHName parameter to set.
     */
    public void setCarouselFHName(String pCarouselFHName) {
        mCarouselFHName = pCarouselFHName;
    }

    /**
     * Returns carouselFSTName property.
     *
     * @return carouselFSTName property.
     */
    public String getCarouselFSTName() {
        return mCarouselFSTName;
    }

    /**
     * Sets the value of the carouselFSTName property.
     *
     * @param pCarouselFSTName parameter to set.
     */
    public void setCarouselFSTName(String pCarouselFSTName) {
        mCarouselFSTName = pCarouselFSTName;
    }

    /**
     * Returns carouselSBName property.
     *
     * @return carouselSBName property.
     */
    public String getCarouselSBName() {
        return mCarouselSBName;
    }

    /**
     * Sets the value of the carouselSBName property.
     *
     * @param pCarouselSBName parameter to set.
     */
    public void setCarouselSBName(String pCarouselSBName) {
        mCarouselSBName = pCarouselSBName;
    }

    /**
     * Returns trailParameterName property.
     *
     * @return trailParameterName property.
     */
    public String getTrailParameterName() {
        return mTrailParameterName;
    }

    /**
     * Sets the value of the trailParameterName property.
     *
     * @param pTrailParameterName parameter to set.
     */
    public void setTrailParameterName(String pTrailParameterName) {
        mTrailParameterName = pTrailParameterName;
    }

    /**
     * Returns questionParameterName property.
     *
     * @return questionParameterName property.
     */
    public String getQuestionParameterName() {
        return mQuestionParameterName;
    }

    /**
     * Sets the value of the questionParameterName property.
     *
     * @param pQuestionParameterName parameter to set.
     */
    public void setQuestionParameterName(String pQuestionParameterName) {
        mQuestionParameterName = pQuestionParameterName;
    }

    /**
     * Returns searchTypeParameterName property.
     *
     * @return searchTypeParameterName property.
     */
    public String getSearchTypeParameterName() {
        return mSearchTypeParameterName;
    }

    /**
     * Sets the value of the searchTypeParameterName property.
     *
     * @param pSearchTypeParameterName parameter to set.
     */
    public void setSearchTypeParameterName(String pSearchTypeParameterName) {
        mSearchTypeParameterName = pSearchTypeParameterName;
    }

    /**
     * Returns questionFacetId property.
     *
     * @return questionFacetId property.
     */
    public String getQuestionFacetId() {
        return mQuestionFacetId;
    }

    /**
     * Sets the value of the questionFacetId property.
     *
     * @param pQuestionFacetId parameter to set.
     */
    public void setQuestionFacetId(String pQuestionFacetId) {
        mQuestionFacetId = pQuestionFacetId;
    }

    /**
     * Returns correct list of products for carousel. 1) if not emty document
     * object so we on document details page and should retrieve carousel for
     * this document. Get 50 products are attached to document's pivot category.
     * 2) In other case retrives trail parameter and returns carousel based on
     * it.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCarouselSearchDroplet.service");
            }
            String referer = pRequest.getHeader(REFERER);

            Object document = pRequest.getObjectParameter(DOCUMENT);
            String pivotCategoryId = pRequest.getParameter(PIVOT_CATEGORY_ID);
            String productId = pRequest.getParameter(PRODUCT_ID);
            String categoryFacetId = pRequest.getParameter(CATEGORY_FACET_ID);
            String docSortMode = pRequest.getParameter(DOC_SORT_MODE);
            String docSortOrder = pRequest.getParameter(DOC_SORT_ORDER);
            String docSortProp = pRequest.getParameter(DOC_SORT_PROP);
            List<String> productIds = new ArrayList<String>();

            String resultedTrail = null;

            QueryFormHandler carouselFH = null;
            CommerceFacetSearchTools carouselFST = null;
            SessionBean carouselSBName = null;

            if (!StringUtils.isBlank(getCarouselFHName()) && !StringUtils.isBlank(getCarouselFSTName()) &&
                    !StringUtils.isBlank(getCarouselSBName())) {
                carouselFH = (QueryFormHandler) pRequest.resolveName(getCarouselFHName());
                carouselFST = (CommerceFacetSearchTools) pRequest.resolveName(getCarouselFSTName());
                carouselSBName = (SessionBean) pRequest.resolveName(getCarouselSBName());
            }

            if (carouselSBName != null) {
                referer = (String) carouselSBName.getValues().get(FACETED_SEARCH_QUERY_STRING);

            }
            if (!StringUtils.isBlank(referer) && !referer.contains("?")) {
                referer = "?".concat(referer);
            }
            if ((carouselFH != null) && (carouselFH.getSearchRequest() != null) &&
                    (carouselFH.getSearchRequest() instanceof QueryRequest) && (carouselFST != null) &&
                    (carouselSBName != null)) {
                if ((document != null) && (document instanceof RepositoryItem)) {
                    if (!StringUtils.isEmpty(productId)) {
                        // Only one product available at the ui from which this document was opened available at the ui
                        carouselSBName.getValues().clear();
                    } else {
                        Object categoriesObj = ((RepositoryItem) document).getPropertyValue(CATEGORIES);
                        if ((categoriesObj != null) && (categoriesObj instanceof Set) &&
                                !((Set) categoriesObj).isEmpty()) {
                            Object docCategoryObj = ((Set) categoriesObj).iterator().next();
                            if ((docCategoryObj != null) && (docCategoryObj instanceof RepositoryItem)) {
                                String categoryId = ((RepositoryItem) docCategoryObj).getRepositoryId();
                                if (!StringUtils.isBlank(categoryId) && !StringUtils.isBlank(categoryFacetId)) {
                                    resultedTrail = categoryFacetId.concat(COLON).concat(categoryId);
                                }
                            }
                        }
                    }
                } else {
                    if (!StringUtils.isBlank(referer)) {
                        Pattern getValue = Pattern.compile(REGEXP_PART_ONE + getTrailParameterName() + REGEXP_PART_TWO);
                        Matcher getParam = getValue.matcher(referer);
                        String trail = null;
                        String question = null;
                        String searchType = null;

                        if (getParam.find()) {
                            trail = getParam.group(1);
                        }

                        getValue = Pattern.compile(REGEXP_PART_ONE + getQuestionParameterName() + REGEXP_PART_TWO);
                        getParam = getValue.matcher(referer);
                        if (getParam.find()) {
                            question = getParam.group(1);
                        }
                        getValue = Pattern.compile(REGEXP_PART_ONE + getSearchTypeParameterName() + REGEXP_PART_TWO);
                        getParam = getValue.matcher(referer);
                        if (getParam.find()) {
                            searchType = getParam.group(1);
                        }

                        if (!StringUtils.isBlank(trail)) {
                            resultedTrail = URLDecoder.decode(trail, ENCODING_UTF);
                        } else if (!StringUtils.isBlank(pivotCategoryId) && StringUtils.isBlank(searchType) &&
                                       !StringUtils.isBlank(categoryFacetId)) {
                            resultedTrail = categoryFacetId.concat(COLON).concat(pivotCategoryId);

                        } else if (!StringUtils.isBlank(question) && !StringUtils.isBlank(searchType)) {
                            resultedTrail =
                                URLDecoder.decode(getQuestionFacetId().concat(COLON).concat(question), ENCODING_UTF);
                        }
                    }  // end if
                }  // end if-else

                if (!StringUtils.isBlank(resultedTrail)) {
                    long expireTime = mDefaultCacheCheckSeconds * 1000;
                    String expireTimeStr = pRequest.getParameter("cacheCheckSeconds");
                    if (expireTimeStr != null) {
                        try {
                            expireTime = Long.parseLong(expireTimeStr) * 1000;
                        } catch (NumberFormatException e) {
                            if (isLoggingError()) {
                                logError("bad value for cacheCheckSeconds parameter");
                            }
                        }
                    }

                    String cacheKey = "";
                    if (!StringUtils.isBlank(docSortMode)) {
                        cacheKey = cacheKey.concat(docSortMode);
                    }
                    if (!StringUtils.isBlank(docSortOrder)) {
                        if (!StringUtils.isBlank(cacheKey)) {
                            cacheKey = cacheKey.concat(UNDERSCORE);
                        }
                        cacheKey = cacheKey.concat(docSortOrder);
                    }
                    if (!StringUtils.isBlank(docSortProp)) {
                        if (!StringUtils.isBlank(cacheKey)) {
                            cacheKey = cacheKey.concat(UNDERSCORE);
                        }
                        cacheKey = cacheKey.concat(docSortProp);
                    }
                    if (!StringUtils.isBlank(resultedTrail)) {
                        if (!StringUtils.isBlank(cacheKey)) {
                            cacheKey = cacheKey.concat(UNDERSCORE);
                        }
                        cacheKey = cacheKey.concat(resultedTrail);
                    }

                    CacheDataEntry cdata = (CacheDataEntry) mCache.get(cacheKey);
                    if ((cdata != null) && (cdata.data != null) &&
                            !shouldRegenerateCachedData(pRequest, cdata, expireTime)) {
                        productIds.addAll(cdata.data);
                    } else {
                        carouselFST.setFacetTrail(resultedTrail);
                        if (!StringUtils.isBlank(docSortMode)) {
                            ((QueryRequest) carouselFH.getSearchRequest()).setDocSort(QueryRequest.DocSort.valueOf(docSortMode));
                        }
                        if (!StringUtils.isBlank(docSortOrder)) {
                            ((QueryRequest) carouselFH.getSearchRequest()).setDocSortOrder(QueryRequest.DocSortOrder
                                                                                           .valueOf(docSortOrder));
                        }
                        if (!StringUtils.isBlank(docSortProp)) {
                            ((QueryRequest) carouselFH.getSearchRequest()).setDocSortProp(docSortProp);
                        }
                        carouselFH.handleSearch(pRequest, pResponse);
                        List<Result> searchResults =
                            ((SearchResultsResponse) carouselFH.getSearchResponse()).getResults();

                        if ((searchResults != null) && !searchResults.isEmpty()) {
                            for (Result searchResult : searchResults) {
                                if (searchResult.getDocument().getProperties().get(REPOSITORY_ID) != null) {
                                    productIds.add((String) searchResult.getDocument().getProperties().get(REPOSITORY_ID));
                                }
                            }
                        }
                        mCache.put(cacheKey, new CacheDataEntry(new ArrayList<String>(productIds)));
                    }  // end if-else

                    carouselSBName.getValues().put(FOUND_PRODUCT_IDS, productIds);
                    carouselSBName.getValues().put(PREVIOUS_TRAIL, resultedTrail);
                }  // end if

            }  // end if

        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCarouselSearchDroplet.service");
            }
        }  // end try-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }

    /**
     * Check whether cache should be invalidated.
     *
     * @param  pRequest   http request
     * @param  pCdata     parameter
     * @param  pCheckTime parameter
     *
     * @return true if cache should be regenerated
     */
    boolean shouldRegenerateCachedData(DynamoHttpServletRequest pRequest, CacheDataEntry pCdata, long pCheckTime) {
        long now = System.currentTimeMillis();
        if ((now - pCdata.creationTime) > pCheckTime) {
            if (isLoggingDebug()) {
                logDebug("invalidating the cache for: " + ServletUtil.getCurrentPathInfo(pRequest));
            }
            return true;
        }
        return false;
    }

    /**
     * Remove cached element from mCache hashtable by pKey
     *
     * @param pKey for removing item
     */
    public void removeCachedElement(String pKey) {
        mCache.remove(pKey);
    }

    /**
     * Flush cache for carousel.
     */
    public void flushCache() {
        mCache = new Hashtable<String, CacheDataEntry>();
        if (isLoggingDebug()) {
            logDebug("flush the entire cache");
        }
        return;
    }

    /**
     * Returns defaultCacheCheckSeconds property.
     *
     * @return defaultCacheCheckSeconds property.
     */
    public int getDefaultCacheCheckSeconds() {
        return mDefaultCacheCheckSeconds;
    }

    /**
     * Sets the value of the defaultCacheCheckSeconds property.
     *
     * @param pDefaultCacheCheckSeconds parameter to set.
     */
    public void setDefaultCacheCheckSeconds(int pDefaultCacheCheckSeconds) {
        mDefaultCacheCheckSeconds = pDefaultCacheCheckSeconds;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected Servlet createAdminServlet() {
        return new CacheAdminServlet(this, getNucleus());
    }

    /**
     * Class for caching carousel.
     *
     * @author EPAM team
     */
    class CacheDataEntry {
        /** The last time we recreated this content */
        long creationTime;

        /** The data */
        List<String> data;

        /**
         * Creates a new CacheDataEntry object.
         *
         * @param pData parameter to set.
         */
        public CacheDataEntry(List<String> pData) {
            creationTime = System.currentTimeMillis();
            data = pData;
        }

        /**
         * Returns length property.
         *
         * @return length property.
         */
        int getLength() {
            if (data == null) {
                return 0;
            }
            int length = 0;
            for (String str : data) {
                length += str.length();
            }
            return length;
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @author  EPAM team
     * @version ToDo: INSERT VERSION NUMBER
     */
    class CacheAdminServlet extends atg.nucleus.ServiceAdminServlet {
        /**
         * Creates a new CacheAdminServlet object. ToDo: DOCUMENT ME!
         *
         * @param pService ToDo: DOCUMENT ME!
         * @param pNucleus ToDo: DOCUMENT ME!
         */
        public CacheAdminServlet(Object pService, Nucleus pNucleus) {
            super(pService, pNucleus);
        }

        /**
         * ToDo: DOCUMENT ME!
         *
         * @param  pRequest  ToDo: DOCUMENT ME!
         * @param  pResponse ToDo: DOCUMENT ME!
         * @param  pOut      ToDo: DOCUMENT ME!
         *
         * @throws ServletException ToDo: DOCUMENT ME!
         * @throws IOException      ToDo: DOCUMENT ME!
         */
        protected void normalAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse, ServletOutputStream pOut)
                            throws ServletException, IOException {
            pOut.println("<form action=\"" + pRequest.getRequestURI() + "\" method=\"post\">");
            pOut.println("<input type=\"hidden\" name=\"removeItemsFromCache\" value=\"true\">");
            pOut.println("<h2>Cache usage statistics</h2>");
            pOut.println("<table border>");
            String[] globalKeys = {"entry", "creationTime", "cacheSize", "cacheData", "removeItemsFromCache"};

            // print header
            pOut.println("<tr>");
            for (int i = 0; i < globalKeys.length; i++) {
                pOut.println("<th>" + globalKeys[i] + "</th>");
            }
            pOut.println("</tr>");

            // print cache entries
            int nEntries = 0;
            int charSize = 0;
            int charEntries = 0;
            final int maxViewableEntries = 20000;

            for (Enumeration e = mCache.keys(); e.hasMoreElements(); nEntries++) {
                if (nEntries > maxViewableEntries) {
                    // too many entries, print ... and finish
                    pOut.print("<tr><th colspan=3>   ..............");
                    pOut.println("</th></tr>");
                    break;
                }
                Object cacheDataKey = e.nextElement();
                CacheDataEntry cacheEntry = (CacheDataEntry) mCache.get(cacheDataKey);
                Date creationTime = new Date();
                creationTime.setTime(cacheEntry.creationTime);

                int len = 0;
                if (cacheEntry.data != null) {
                    len = cacheEntry.getLength();
                    charSize += len;
                    charEntries++;
                }
                String[] itemCacheValues = {"" + nEntries, creationTime.toString(), "" + len, "<a href=\"?cacheDataKey=" +
                        ServletUtil.escapeURLString(cacheDataKey.toString()) + "\">" + cacheDataKey + "</a>", "<input type=\"checkbox\" name=\"remove\" value=\"" +
                        ServletUtil.escapeURLString(cacheDataKey.toString()) + "\">"};

                pOut.println("<tr>");
                for (int i = 0; i < itemCacheValues.length; i++) {
                    pOut.println("<th>" + itemCacheValues[i] + "</th>");
                }
                pOut.println("</tr>");
            }  // end for
            pOut.println("</table>");
            pOut.println("<input type=\"submit\" value=\"Remove items from Cache\" name\"submit\">");
            pOut.println("</form>");
            if (nEntries <= maxViewableEntries) {
                StringBuffer sb = new StringBuffer("<p>Cache contains ");
                sb.append(charEntries);
                sb.append(" containing ").append(charSize).append(" characters.");
                pOut.println(sb.toString());
            }
        }

        /**
         * ToDo: DOCUMENT ME!
         *
         * @param  pRequest  ToDo: DOCUMENT ME!
         * @param  pResponse ToDo: DOCUMENT ME!
         * @param  pOut      ToDo: DOCUMENT ME!
         *
         * @throws ServletException ToDo: DOCUMENT ME!
         * @throws IOException      ToDo: DOCUMENT ME!
         */
        protected void printAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse, ServletOutputStream pOut)
                           throws ServletException, IOException {
            String cacheDataKey = pRequest.getParameter("cacheDataKey");
            String removeItemsFromCache = pRequest.getParameter("removeItemsFromCache");

            // then you are going to the normal admin page use to work
            if ((cacheDataKey == null) && (removeItemsFromCache == null)) {
                // normalAdmin(pRequest, pResponse, pOut);
                normalAdmin(pRequest, pResponse, pOut);
            }
            if (cacheDataKey != null) {
                // you want to see the data for one cache name
                viewCacheItemAdmin(pRequest, pResponse, pOut);
            }
            if (removeItemsFromCache != null) {
                // you want to remove one or more items from the cache
                removeItemsFromCacheAdmin(pRequest, pResponse, pOut);
            }
        }

        /**
         * ToDo: DOCUMENT ME!
         *
         * @param  pRequest  ToDo: DOCUMENT ME!
         * @param  pResponse ToDo: DOCUMENT ME!
         * @param  pOut      ToDo: DOCUMENT ME!
         *
         * @throws ServletException ToDo: DOCUMENT ME!
         * @throws IOException      ToDo: DOCUMENT ME!
         */
        void removeItemsFromCacheAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse,
                                       ServletOutputStream pOut) throws ServletException, IOException {
            String[] removeItems = pRequest.getParameterValues("remove");

            // URLDecoder urlDecoder = new URLDecoder();
            if (removeItems != null) {
                for (int i = 0; i < removeItems.length; i++) {
                    String decodeKeyName = URLDecoder.decode(removeItems[i]);
                    removeCachedElement(decodeKeyName);
                }
                pOut.println("<h2>Cache items have been removed</h2>");
            } else {
                pOut.println("<h2>No cache items were selected for removal</h2>");
            }
        }

        /**
         * ToDo: DOCUMENT ME!
         *
         * @param  pRequest  ToDo: DOCUMENT ME!
         * @param  pResponse ToDo: DOCUMENT ME!
         * @param  pOut      ToDo: DOCUMENT ME!
         *
         * @throws ServletException ToDo: DOCUMENT ME!
         * @throws IOException      ToDo: DOCUMENT ME!
         */
        private void viewCacheItemAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse,
                                        ServletOutputStream pOut) throws ServletException, IOException {
            String cacheDataKey = pRequest.getParameter("cacheDataKey");
            if (cacheDataKey == null) {
                logWarning("Error: cacheDataKey was null");
                return;
            }

            pOut.println("<h2>Cache item data is below</h2>");
            pOut.println("<table border>");

            pOut.println("<tr><td><h3>Data</h3></td></tr>");
            pOut.println("<tr><td>");
            CacheDataEntry cacheEntry = null;
            Enumeration e = mCache.keys();
            while (e.hasMoreElements()) {
                Object elem = e.nextElement();
                if (elem.toString().equals(cacheDataKey)) {
                    cacheEntry = (CacheDataEntry) mCache.get(elem);
                    break;
                }
            }
            String cacheData = null;
            if (cacheEntry != null) {
                cacheData = ServletUtil.escapeHtmlString(cacheEntry.toString());
            } else {
                cacheData = "couldn't find data for key =" + cacheDataKey;
            }

            pOut.println(cacheData);
            pOut.println("</td></tr>");

            pOut.println("<tr><td><h3>Html</h3></td></tr>");
            pOut.println("<tr><td>");
            cacheData = (cacheEntry == null) ? "null" : cacheEntry.toString();
            pOut.println(cacheData);
            pOut.println("</td></tr>");

            pOut.println("</table>");
        }
    }

}
