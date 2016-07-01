package com.castorama.seo;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 *
 *
 * @author EPAM team
 */
public class UrlRewriteService extends GenericService {
    
    /** AMP property */
    private static final String AMP = "&";


    /** QUESTION_URL_PARAM property */
    private static final String QUESTION_URL_PARAM = "question=";

    /** PARAM_URI property */
    private static final String PARAM_URI = "uri";

    /** PARAM_QUERY_STRING property */
    private static final String PARAM_QUERY_STRING = "qs";

    /** SEARCH_TYPE_URL_PARAM property */
    private static final String SEARCH_TYPE_URL_PARAM = "&searchType=";

    /** SEARCH_REDIRECT_URL property */
    private static final String SEARCH_REDIRECT_URL = "/search/searchResults.jsp?";

    /** CARD_PRICE_PRODS_URL property */
    private static final String CARD_PRICE_PRODS_URL = "/castCatalog/cardPriceAvailableProducts.jsp?";

    /** HEX_ENCODED_PLUS property */
    private static final String HEX_ENCODED_PLUS = "%2B";

    /** REG_EXP_PLUS property */
    private static final String REG_EXP_PLUS = "\\+";

    /** CATEGORY_ID_URL_PARAM property */
    private static final String CATEGORY_ID_URL_PARAM = "?categoryId=";

    /** PRODUCT_ID_URL_PARAM property */
    private static final String PRODUCT_ID_URL_PARAM = "?productId=";

    /** DOCUMENT_ID_URL_PARAM property */
    private static final String DOCUMENT_ID_URL_PARAM = "?documentId=";

    /** TOPIC_ID_URL_PARAM property */
    private static final String TOPIC_ID_URL_PARAM = "?topicId=";
    
    /** FILTER_ID_URL_PARAM property */
    private static final String FILTER_ID_URL_PARAM = "?filterTag=";

    /** PROMO_INFO_ID_URL_PARAM property */
    private static final String PROMO_INFO_ID_URL_PARAM = "?promoId=";

    /** CLOSE property */
    private static final String CLOSE = "close";

    /** PROPERTY_URL property */
    private static final String PROPERTY_URL = "url";

    /** PROPERTY_TEMPLATE property */
    private static final String PROPERTY_TEMPLATE = "template";

    /** PROPERTY_DISPLAY_NAME property */
    private static final String PROPERTY_DISPLAY_NAME = "displayName";

    /** PROPERTY_DISPLAY_NAME property */
    private static final String PROPERTY_TITLE = "title";

    /** PROPERTY_WRAP_PAGE property */
    private static final String PROPERTY_WRAP_PAGE = "wrapPage";

    /** CHARSET_UTF_8 property */
    private static final String CHARSET_UTF_8 = "UTF-8";

    /** ITEM_CATEGORY constant. */
    private static final String CASTO_CARD_PRICE = "castocardprices";

    /** ITEM_CATEGORY constant. */
    private static final String ITEM_CATEGORY = "category";

    /** ITEM_PRODUCT constant. */
    private static final String ITEM_PRODUCT = "product";

    /** ITEM_PROMO_INFO constant. */
    private static final String ITEM_PROMO_INFO = "promoInformation";

    /** ITEM_DOCUMENT constant. */
    private static final String ITEM_DOCUMENT = "castoramaDocument";

    /** ITEM_TOPIC constant. */
    private static final String ITEM_TOPIC = "thematique";
    
    /** ITEM_TOPIC constant. */
    private static final String ITEM_FILTER = "castoDocFilterTag";

    /** HEADER_LOCATION constant. */
    private static final String HEADER_LOCATION = "Location";

    /** HEADER_CONNECTION constant. */
    private static final String HEADER_CONNECTION = "Connection";

    /** HOME_PAGE constant. */
    private static final String HOME_PAGE = "home.jsp";

    /** CAST_CATALOG constant */
    private static final String CAST_CATALOG = "castCatalog";

    /** WRAP_PAGE constant */
    private static final String WRAP_PAGE = "wrapPageContent.jsp";

    /** PATTERN_PRODUCT constant. */
    private static final Pattern PATTERN_PRODUCT = Pattern.compile("/([^/]+?)/([^/]+?)-([^/]+?)\\.html([^/]*?)");

    /** PATTERN_CATEGORY constant. */
    // /store/([^/]+?)/([^/]+?)-([^/]+?)\.htm([^/]*?) 
//    private static final Pattern PATTERN_CATEGORY = Pattern.compile("/([^/]+?)/([^/]+?)-([^/]+?)\\.htm([^/]*?)");
    public static final Pattern PATTERN_CATEGORY = Pattern.compile("/store/?([^/]+?)-([^/]+?)\\.htm([^/]*?)");

    /** PATTERN_DOCUMENT constant. */
    private static final Pattern PATTERN_DOCUMENT = Pattern.compile("/([^/]+?)/doc/([^/]+?)-([^/]+?)\\.html([^/]*?)");

    /** PATTERN_TOPIC constant. */
    private static final Pattern PATTERN_TOPIC = Pattern.compile("/([^/]+?)/lancez-vous/([^/]+?)-([^/]+?)\\.html([^/]*?)");
    
    /** PATTERN_FILTER constant. */
    private static final Pattern PATTERN_FILTER = Pattern.compile("/([^/]+?)/conseils-et-forums/([^/]+?)\\+([^/]+?)\\.html([^/]*?)");

    /** PATTERN_SEARCH constant. */
    public static final Pattern PATTERN_SEARCH = Pattern.compile("/([^/]+?)/rechercher([^/]?)/([^/|?]*)");

    /** PATTERN_CARD_PRICE constant. */
    public static final Pattern PATTERN_CARD_PRICE = Pattern.compile("/([^/]+?)/offres-carte-castorama([^/|?]*)");

    /** PATTERN_WRAP constant. */
    public static final Pattern PATTERN_WRAP = Pattern.compile("/([^/]+?)/pages/([^/]+?)\\.html([^/]*?)");

    private static final String SEARCH_MODE = "osearchmode";

    private static final String SEARCH_MODE_REG = "reg";

    private static final String SEARCH_MODE_TAGCLOUD = "tagcloud";

    /** productCatalog property. */
    private Repository mProductCatalog;

    /** SEOConfiguration property */
    private SEOConfiguration mSEOConfiguration;

    /** mCachedLegacyURLManager property */
    private LegacyURLManager mCachedLegacyURLManager;

    /** mLegacyURLManager property */
    private LegacyURLManager mLegacyURLManager;

    /** mUseURLCache property */
    private boolean mUseURLCache;

    /** mDefaultSearchType property */
    private String mDefaultSearchType = "2";

    /** mRedirectPage property */
    private String mRedirectPage = "redirectPage.jsp";

    /**
     *
     * @param  pServletRequest  parameter
     * @param  pServletResponse parameter
     * @param  filterChain      parameter
     * @param  pServletContext  parameter
     *
     * @return
     *
     * @throws IOException      exception
     * @throws ServletException exception
     */
    protected boolean doFilterRequest(HttpServletRequest pServletRequest, HttpServletResponse pServletResponse,
                                      FilterChain filterChain, ServletContext pServletContext) throws IOException,
                                                                                                      ServletException {
        if (isLoggingDebug()) {
            logDebug("Start of UrlRewriteService.service");
        }
        // Log information about the HTTP request
        if ((isLoggingDebug()) && (pServletRequest instanceof HttpServletRequest)) {
            HttpServletRequest httpRequest = (HttpServletRequest) pServletRequest;
            logDebug("Request URI is <" + httpRequest.getRequestURI() + ">");
            logDebug("Request URL is <" + httpRequest.getRequestURL() + ">");
            logDebug("Request query string is <" + httpRequest.getQueryString() + ">");
            logDebug("Request servlet path is <" + httpRequest.getServletPath() + ">");
        }
        DynamoHttpServletRequest dynRequest =
            ServletUtil.getDynamoRequest(pServletContext, pServletRequest, pServletResponse);
        DynamoHttpServletResponse dynResponse = ServletUtil.getDynamoResponse(dynRequest, pServletResponse);
        if (PATTERN_PRODUCT.matcher(dynRequest.getRequestURI()).matches()) {
            return processProduct(dynRequest, dynResponse);
        } else if (PATTERN_CATEGORY.matcher(dynRequest.getRequestURI()).matches()) {
            String redirectLegacyCategoryId = null;
            if (getSEOConfiguration().isSupportLegacyURLs()) {
                if (isUseURLCache()) {
                    redirectLegacyCategoryId =
                        getCachedLegacyURLManager().getCategoryForURL(dynRequest.getRequestURI());
                } else {
                    String uri = dynRequest.getRequestURI();
                    String catStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTM));
                    String catId = catStr.substring(catStr.lastIndexOf('-') + 1);
                    redirectLegacyCategoryId = getLegacyURLManager().getCategoryForURL(catId);
                }
            }
            if (redirectLegacyCategoryId != null) {
                return processLegacyLink(redirectLegacyCategoryId, dynRequest, dynResponse);
            } else {
                return processCategory(dynRequest, dynResponse);
            }
        } else if (PATTERN_DOCUMENT.matcher(dynRequest.getRequestURI()).matches()){
            return processDocument(dynRequest, dynResponse);
        } else if (PATTERN_TOPIC.matcher(dynRequest.getRequestURI()).matches()){
            return processTopic(dynRequest, dynResponse);
        } else if (PATTERN_FILTER.matcher(dynRequest.getRequestURI()).matches()){
            return processFilter(dynRequest, dynResponse);
        } else if (PATTERN_SEARCH.matcher(dynRequest.getRequestURI()).matches()) {
            String searchType = SEOUtils.getSearchType(dynRequest.getRequestURI());
            if (searchType == null || searchType.length()==0) return true;
            return processSearch(dynRequest, dynResponse, searchType);
        } else if (PATTERN_CARD_PRICE.matcher(dynRequest.getRequestURI()).matches()) {
            return processSearch(dynRequest, dynResponse, CASTO_CARD_PRICE);
        } else if (PATTERN_WRAP.matcher(dynRequest.getRequestURI()).matches()) {
            return processWrap(dynRequest, dynResponse);
        } else {
            return true;
        }  // end if-else
    }

    protected boolean processWrap(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
                                    throws ServletException, IOException {
        String uri = pRequest.getRequestURI();
        String promoId = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTML));

        RepositoryItem promoInfo = null;
        try {
            promoInfo = getProductCatalog().getItem(promoId, ITEM_PROMO_INFO);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Can not find promoInformation with id=" + promoId + ". Wrong URL?" + uri);
            }
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }
        if (promoInfo == null) {
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }

        String realURI = getPromoInfoURI(promoInfo);
        RequestDispatcher dispatcher =
                pRequest.getRequestDispatcher(realURI +
                                              (StringUtils.isBlank(pRequest.getQueryString())
                                               ? "" : ('&' + pRequest.getQueryString())));
        dispatcher.forward(pRequest, pResponse);

        return false;
    }

    /**
     *
     * @param  pRequest    parameter
     * @param  pResponse   parameter
     * @param  pSearchType parameter
     *
     * @return
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @SuppressWarnings("unchecked")
    protected boolean processSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                    String pSearchType) throws ServletException, IOException {
        String uri = pRequest.getRequestURI();
        String searchTerm = uri.substring(uri.lastIndexOf('/') + 1);
        searchTerm = URLDecoder.decode(searchTerm, CHARSET_UTF_8);
        String searchTermPrepared = SEOUtils.prepareNameForURL(searchTerm);
        String searchTermSession = (String) pRequest.getSession().getAttribute("searchTerm");
        pRequest.getSession().removeAttribute("searchTerm");

        /*String searchMode = (String) pRequest.getQueryParameter(SEARCH_MODE);

        if( searchMode != null && !searchMode.equals(SEARCH_MODE_REG) && !searchMode.equals(SEARCH_MODE_TAGCLOUD)) {
            pRequest.addQueryParameter(SEARCH_MODE, "");
        }    */
        String searchMode = pRequest.getParameter(SEARCH_MODE);

        if( searchMode != null && !searchMode.equals(SEARCH_MODE_REG) && !searchMode.equals(SEARCH_MODE_TAGCLOUD)) {
            pRequest.setParameter(SEARCH_MODE, "");
        }

        /*if (searchTerm.contains("slashEscape")){
            searchTerm = searchTerm.replace("slashEscape","");
        } */
        /*if (searchTerm.contains((":"))) {
            searchTerm = searchTerm.replace(":", "");
        } */

        if (searchTermSession != null)
            searchTerm = searchTermSession;
        if (searchTermSession != null || searchTerm.equals(searchTermPrepared)) {
            if (searchTerm.contains("%")){
                searchTerm = searchTerm.replace("%","%25");
            }
            if (searchTerm.contains("-")){
                searchTerm = searchTerm.replace('-', ' ');
            }
            if (searchTerm.contains("&")){
                searchTerm = searchTerm.replace("&", "%26");
            }
            /*if (searchTerm.contains((":"))) {
                searchTerm = searchTerm.replace(":", "");
            }*/

            StringBuffer urlBuffer = null;
            if (CASTO_CARD_PRICE.equals(pSearchType)) {
                urlBuffer = new StringBuffer(CARD_PRICE_PRODS_URL);
                urlBuffer.append(QUESTION_URL_PARAM).append(SEARCH_TYPE_URL_PARAM).append(pSearchType);
            } else {
                urlBuffer = new StringBuffer(SEARCH_REDIRECT_URL);
                urlBuffer.append(QUESTION_URL_PARAM).append(searchTerm.trim()).append(SEARCH_TYPE_URL_PARAM).append(pSearchType);
            }

            Enumeration names = pRequest.getQueryParameterNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                String value = pRequest.getParameter(name);
                if (name.equals(SEARCH_MODE)) {
                    if ( value != null && !value.equals(SEARCH_MODE_REG) && !value.equals(SEARCH_MODE_TAGCLOUD)) {
                        value="";
                    }
                }
                /*if (name.equals("question")) {
                    if (value.equals(":")) {
                        value="";
                    }
                } */
                urlBuffer.append('&').append(name).append('=').append(value);
            }
            RequestDispatcher dispatcher = pRequest.getRequestDispatcher(urlBuffer.toString());
            pRequest.getSession().setAttribute("lastViewedPage", pRequest.getRequestURIWithQueryString());
            dispatcher.forward(pRequest, pResponse);
        } else {
            pResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            pResponse.setHeader(HEADER_CONNECTION, CLOSE);
            pResponse.setHeader(HEADER_LOCATION, uri.substring(0, uri.lastIndexOf('/') + 1) + searchTermPrepared + 
                    (StringUtils.isBlank(pRequest.getQueryString()) ? "" : ('?' + pRequest.getQueryString())));
            pRequest.getSession().setAttribute("searchTerm", searchTermPrepared);
        }
        return false;
    }

    /**
     *
     * @param  redirectCategoryId parameter
     * @param  pRequest           parameter
     * @param  pResponse          parameter
     *
     * @return
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    protected boolean processLegacyLink(String redirectCategoryId, DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        try {
            String url = SEOUtils.getCategoryURL(redirectCategoryId, getProductCatalog());
            pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            pResponse.setHeader(HEADER_CONNECTION, CLOSE);
            pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + url);
        } catch (Exception e) {
            logError(e);
            RequestDispatcher dispatcher = pRequest.getRequestDispatcher(HOME_PAGE);
            dispatcher.forward(pRequest, pResponse);
        }
        return false;
    }

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @return
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    protected boolean processCategory(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                               throws ServletException, IOException {
        String uri = pRequest.getRequestURI();
        String catStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTM));
        String catId = catStr.substring(catStr.lastIndexOf('-') + 1);
        RepositoryItem category = null;
        try {
            category = getProductCatalog().getItem(catId, ITEM_CATEGORY);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Can not find category with id=" + catId + ". Wrong URL?" + uri);
            }

            // navigateHome(pRequest, pResponse);
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }
        if (category == null) {
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }
        String actualDisplayName =
            SEOUtils.prepareNameForURL((String) category.getPropertyValue(PROPERTY_DISPLAY_NAME));
        
        String displayNameFromURL =
            catStr.substring(0, catStr.lastIndexOf('-')).replaceAll(REG_EXP_PLUS, HEX_ENCODED_PLUS);
        displayNameFromURL = URLDecoder.decode(displayNameFromURL, CHARSET_UTF_8);
        if (displayNameFromURL.equals(actualDisplayName)) {
            int startNum = uri.indexOf("/store/")+7;
            int endNum = uri.lastIndexOf('/');
            String trailUrlString = "";
            if (endNum > startNum){
                trailUrlString = URLDecoder.decode(
                        uri.substring(startNum, endNum),
                        CHARSET_UTF_8);
                trailUrlString = StringUtils.replace(trailUrlString, '/', ":");
                trailUrlString = StringUtils.replace(trailUrlString, '_', "|");
            }
            String trailParam = pRequest.getQueryParameter("trail");
            if (null != trailParam && trailParam.length()!=0) trailUrlString="";
//            trailUrlString = URLEncoder.encode(trailUrlString, CHARSET_UTF_8);
            String realURI = getCategoryURI(category);
            String forwardUrl = realURI + (trailUrlString.length()!=0?"&trail="+trailUrlString:"")+
            (StringUtils.isBlank(pRequest.getQueryString())
                    ? "" : ('&' + pRequest.getQueryString()));
            RequestDispatcher dispatcher = 
                pRequest.getRequestDispatcher(forwardUrl);
            dispatcher.forward(pRequest, pResponse);
//            redirectToRightPage(pRequest, pResponse, realURI);
        } else {
            pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            pResponse.setHeader(HEADER_CONNECTION, CLOSE);
            pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + SEOUtils.getCategoryURL(category) + 
                    (StringUtils.isBlank(pRequest.getQueryString())
                    ? "" : ('?' + pRequest.getQueryString())));
        }

        return false;
    }

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @return
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    protected boolean processProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        String uri = pRequest.getRequestURI();
        String prodStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTML));
        String prodId = prodStr.substring(prodStr.lastIndexOf('-') + 1);
        RepositoryItem product = null;
        try {
            product = getProductCatalog().getItem(prodId, ITEM_PRODUCT);
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Can not find product with id=" + prodId + ". Wrong URL?" + uri);
            }
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }
        if (product == null) {
            pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return true;
        }
        String actualDisplayName = SEOUtils.prepareNameForURL((String) product.getPropertyValue(PROPERTY_DISPLAY_NAME));
        String displayNameFromURL =
            prodStr.substring(0, prodStr.lastIndexOf('-')).replaceAll(REG_EXP_PLUS, HEX_ENCODED_PLUS);
        displayNameFromURL = URLDecoder.decode(displayNameFromURL, CHARSET_UTF_8);
        if (displayNameFromURL.equals(actualDisplayName)) {
            String realURI = getProductURI(product);
            RequestDispatcher dispatcher =
                pRequest.getRequestDispatcher(realURI +
                                              (StringUtils.isBlank(pRequest.getQueryString())
                                               ? "" : ('&' + pRequest.getQueryString())));
            dispatcher.forward(pRequest, pResponse);
//            redirectToRightPage(pRequest, pResponse, realURI);
        } else {
            pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            pResponse.setHeader(HEADER_CONNECTION, CLOSE);
            pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + SEOUtils.getProductURL(product));
        }

        return false;
    }


    /**
    *
    * @param  pRequest  parameter
    * @param  pResponse parameter
    *
    * @return
    *
    * @throws ServletException exception
    * @throws IOException      exception
    */
   protected boolean processDocument(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                             throws ServletException, IOException {
       String uri = pRequest.getRequestURI();
       String docStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_DOC));
       String docId = docStr.substring(docStr.lastIndexOf('-') + 1);
       RepositoryItem document = null;
       try {
           document = getProductCatalog().getItem(docId, ITEM_DOCUMENT);
       } catch (Exception e) {
           if (isLoggingError()) {
               logError("Can not find document with id=" + docId + ". Wrong URL?" + uri);
           }
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       if (document == null) {
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       String actualDisplayName = SEOUtils.prepareNameForURL((String) document.getPropertyValue(PROPERTY_TITLE));
       String displayNameFromURL =
           docStr.substring(0, docStr.lastIndexOf('-')).replaceAll(REG_EXP_PLUS, HEX_ENCODED_PLUS);
       displayNameFromURL = URLDecoder.decode(displayNameFromURL, CHARSET_UTF_8);
       if (displayNameFromURL.equals(actualDisplayName)) {
           String realURI = getDocumentURI(document);
           RequestDispatcher dispatcher =
               pRequest.getRequestDispatcher(realURI +
                                             (StringUtils.isBlank(pRequest.getQueryString())
                                              ? "" : ('&' + pRequest.getQueryString())));
           dispatcher.forward(pRequest, pResponse);
//           redirectToRightPage(pRequest, pResponse, realURI);
       } else {
           pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
           pResponse.setHeader(HEADER_CONNECTION, CLOSE);
           pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + SEOUtils.getDocumentURL(document));
       }

       return false;
   }

   /**
    *
    * @param  pRequest  parameter
    * @param  pResponse parameter
    *
    * @return
    *
    * @throws ServletException exception
    * @throws IOException      exception
    */
   protected boolean processTopic(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
   throws ServletException, IOException {
       String uri = pRequest.getRequestURI();
       String topicStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTML));
       String topicId = topicStr.substring(topicStr.lastIndexOf('-') + 1);
       RepositoryItem topic = null;
       try {
           topic = getProductCatalog().getItem(topicId, ITEM_TOPIC);
       } catch (Exception e) {
           if (isLoggingError()) {
               logError("Can not find topic with id=" + topicId + ". Wrong URL?" + uri);
           }
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       if (topic == null) {
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       String actualDisplayName = SEOUtils.prepareNameForURL((String) topic.getPropertyValue(PROPERTY_TITLE));
       String displayNameFromURL =
           topicStr.substring(0, topicStr.lastIndexOf('-')).replaceAll(REG_EXP_PLUS, HEX_ENCODED_PLUS);
       displayNameFromURL = URLDecoder.decode(displayNameFromURL, CHARSET_UTF_8);
       if (displayNameFromURL.equals(actualDisplayName)) {
           String realURI = getTopicURI(topic);
           RequestDispatcher dispatcher =
               pRequest.getRequestDispatcher(realURI +
                       (StringUtils.isBlank(pRequest.getQueryString())
                               ? "" : ('&' + pRequest.getQueryString())));
           dispatcher.forward(pRequest, pResponse);
       } else {
           pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
           pResponse.setHeader(HEADER_CONNECTION, CLOSE);
           pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + SEOUtils.getTopicURL(topic));
       }
       
       return false;
   }
   
   protected boolean processFilter(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
   throws ServletException, IOException {
       String uri = pRequest.getRequestURI();
       String filterStr = uri.substring(uri.lastIndexOf('/') + 1, uri.lastIndexOf(SEOUtils.EXTENSION_HTML));
       String filterId = filterStr.substring(filterStr.lastIndexOf('+') + 1);
       RepositoryItem filter = null;
       try {
           filter = getProductCatalog().getItem(filterId, ITEM_FILTER);
       } catch (Exception e) {
           if (isLoggingError()) {
               logError("Can not find filter with id=" + filterId + ". Wrong URL?" + uri);
           }
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       if (filter == null) {
           pResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return true;
       }
       String actualDisplayName = SEOUtils.prepareNameForURL((String) filter.getPropertyValue("tagTitle"));
       String displayNameFromURL =
           filterStr.substring(0, filterStr.lastIndexOf('+')).replaceAll(REG_EXP_PLUS, HEX_ENCODED_PLUS);
       displayNameFromURL = URLDecoder.decode(displayNameFromURL, CHARSET_UTF_8);
       if (displayNameFromURL.equals(actualDisplayName)) {
           String realURI = getFilterURI(filter);
           RequestDispatcher dispatcher =
               pRequest.getRequestDispatcher(realURI +
                       (StringUtils.isBlank(pRequest.getQueryString())
                               ? "" : ('&' + pRequest.getQueryString())));
           dispatcher.forward(pRequest, pResponse);
       } else {
           pResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
           pResponse.setHeader(HEADER_CONNECTION, CLOSE);
           pResponse.setHeader(HEADER_LOCATION, pRequest.getContextPath() + SEOUtils.getFilterURL(filter));
       }
       
       return false;
   }

    /*private void redirectToRightPage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String realURI) throws ServletException, IOException{
        String queryString = pRequest.getQueryString();
        String temp =null;
        if (queryString!=null && queryString.length()>0){
            pRequest.setParameter(PARAM_QUERY_STRING, pRequest.getQueryString());
            pRequest.setParameter(PARAM_URI, pRequest.getRequestURI());
            pResponse.setRequest(pRequest);
            StringBuffer tmp = new StringBuffer();
            tmp.append('/').append(getRedirectPage().trim()).append('?').append(pRequest.getQueryString());
            RequestDispatcher dispatcher =
                pRequest.getRequestDispatcher(tmp.toString());
            dispatcher.include(pRequest, pResponse);
        } else {
            temp = pRequest.getParameter(PARAM_QUERY_STRING);
            String req = realURI +((temp!=null && temp.length()!=0)?'&' + temp:"");
            RequestDispatcher dispatcher =
                pRequest.getRequestDispatcher(req);
            dispatcher.forward(pRequest, pResponse);
        }
    }*/
    

    /**
     * Returns productURI property.
     *
     * @param  pProduct parameter to set.
     *
     * @return productURI property.
     */
    protected String getProductURI(RepositoryItem pProduct) {
        try {
            RepositoryItem template = (RepositoryItem) pProduct.getPropertyValue(PROPERTY_TEMPLATE);
            if (template == null) {
                if (isLoggingError()) {
                    logError("Product " + pProduct + " doesn't have template. Redirecting to home.");
                }
                return HOME_PAGE;
            }
            String url = (String) template.getPropertyValue(PROPERTY_URL);
            if (url == null) {
                if (isLoggingError()) {
                    logError("Product template " + template + " doesn't have url. Redirecting to home.");
                }
                return HOME_PAGE;
            }
            url += PRODUCT_ID_URL_PARAM + pProduct.getRepositoryId();
            return url;
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error. Redirecting to home.");
                logError(e);
            }
            return HOME_PAGE;
        }  // end try-catch
    }

    /**
     * Returns categoryURI property.
     *
     * @param  pCategory parameter to set.
     *
     * @return categoryURI property.
     */
    protected String getCategoryURI(RepositoryItem pCategory) {
        try {
            RepositoryItem template = (RepositoryItem) pCategory.getPropertyValue(PROPERTY_TEMPLATE);
            if (template == null) {
                return HOME_PAGE;
            }
            String url = (String) template.getPropertyValue(PROPERTY_URL);
            if (url == null) {
                return HOME_PAGE;
            }
            url += CATEGORY_ID_URL_PARAM + pCategory.getRepositoryId();
            return url;
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error. Redirecting to home.");
                logError(e);
            }
            return HOME_PAGE;
        }
    }

    /**
     * Returns documentURI property.
     *
     * @param  pDocument parameter to set.
     *
     * @return documentURI property.
     */
    protected String getDocumentURI(RepositoryItem pDocument) {
        try {
            RepositoryItem template = (RepositoryItem) pDocument.getPropertyValue(PROPERTY_TEMPLATE);
            if (template == null) {
                if (isLoggingError()) {
                    logError("Document " + pDocument + " doesn't have template. Redirecting to home.");
                }
                return HOME_PAGE;
            }
            String url = (String) template.getPropertyValue(PROPERTY_URL);
            if (url == null) {
                if (isLoggingError()) {
                    logError("Product template " + template + " doesn't have url. Redirecting to home.");
                }
                return HOME_PAGE;
            }
            url += DOCUMENT_ID_URL_PARAM + pDocument.getRepositoryId();
            return url;
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error. Redirecting to home.");
                logError(e);
            }
            return HOME_PAGE;
        }  // end try-catch
    }

    /**
     * Returns topicURI property.
     *
     * @param  pTopic parameter to set.
     *
     * @return topicURI property.
     */
    protected String getTopicURI(RepositoryItem pTopic) {
        StringBuffer url = new StringBuffer();
        url.append("/troc/topicDetails.jsp");
        url.append(TOPIC_ID_URL_PARAM + pTopic.getRepositoryId());
        return url.toString();
    }
    
    /**
     * Returns filterURI property.
     *
     * @param  pFilter parameter to set.
     *
     * @return filterURI property.
     */
    protected String getFilterURI(RepositoryItem pFilter) {
        StringBuffer url = new StringBuffer();
        url.append("/conseils-et-forums.jsp");
        url.append(FILTER_ID_URL_PARAM + pFilter.getRepositoryId());
        return url.toString();
    }
    
    /**
     * Returns promoInfoURI property.
     *
     * @param  pPromoInfo parameter to set.
     *
     * @return promoInfoURI property.
     */
    protected String getPromoInfoURI(RepositoryItem pPromoInfo) {
        StringBuffer url = new StringBuffer();
        try {
            boolean wrapPage = (Boolean)pPromoInfo.getPropertyValue(PROPERTY_WRAP_PAGE);
            if (wrapPage) {
                url.append("/castCatalog/wrapPageContent.jsp");
                url.append(PROMO_INFO_ID_URL_PARAM + pPromoInfo.getRepositoryId());
            } else {
                String urlProperty = (String)pPromoInfo.getPropertyValue(PROPERTY_URL);
                if (urlProperty != null) {
                    url.append(urlProperty);
                } else {
                    if (isLoggingError()) {
                        logError("Promotional Inforamtion item " + pPromoInfo + " doesn't have url property and wrapPage property is false. Redirecting to home page.");
                    }
                    return HOME_PAGE;
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error. Redirecting to home.");
                logError(e);
            }
            return HOME_PAGE;
        }
        return url.toString();
    }

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    protected void navigateHome(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                         throws ServletException, IOException {
        RequestDispatcher dispatcher = pRequest.getRequestDispatcher(HOME_PAGE);
        dispatcher.forward(pRequest, pResponse);
    }

    /**
     * Returns productCatalog property.
     *
     * @return productCatalog property.
     */
    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    /**
     * Sets the value of the productCatalog property.
     *
     * @param pProductCatalog parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
    }

    /**
     * Returns SEOConfiguration property.
     *
     * @return SEOConfiguration property.
     */
    public SEOConfiguration getSEOConfiguration() {
        return mSEOConfiguration;
    }

    /**
     * Sets the value of the SEOConfiguration property.
     *
     * @param pConfiguration parameter to set.
     */
    public void setSEOConfiguration(SEOConfiguration pConfiguration) {
        mSEOConfiguration = pConfiguration;
    }

    /**
     * Returns legacyURLManager property.
     *
     * @return legacyURLManager property.
     */
    public LegacyURLManager getLegacyURLManager() {
        return mLegacyURLManager;
    }

    /**
     * Sets the value of the legacyURLManager property.
     *
     * @param pLegacyURLManager parameter to set.
     */
    public void setLegacyURLManager(LegacyURLManager pLegacyURLManager) {
        mLegacyURLManager = pLegacyURLManager;
    }

    /**
     * Returns useURLCache property.
     *
     * @return useURLCache property.
     */
    public boolean isUseURLCache() {
        return mUseURLCache;
    }

    /**
     * Sets the value of the useURLCache property.
     *
     * @param pUseURLCache parameter to set.
     */
    public void setUseURLCache(boolean pUseURLCache) {
        mUseURLCache = pUseURLCache;
    }

    /**
     * Returns cachedLegacyURLManager property.
     *
     * @return cachedLegacyURLManager property.
     */
    public LegacyURLManager getCachedLegacyURLManager() {
        return mCachedLegacyURLManager;
    }

    /**
     * Sets the value of the cachedLegacyURLManager property.
     *
     * @param pCachedLegacyURLManager parameter to set.
     */
    public void setCachedLegacyURLManager(LegacyURLManager pCachedLegacyURLManager) {
        mCachedLegacyURLManager = pCachedLegacyURLManager;
    }

    /**
     * Returns defaultSearchType property.
     *
     * @return defaultSearchType property.
     */
    public String getDefaultSearchType() {
        return mDefaultSearchType;
    }

    /**
     * Sets the value of the defaultSearchType property.
     *
     * @param pDefaultSearchType parameter to set.
     */
    public void setDefaultSearchType(String pDefaultSearchType) {
        mDefaultSearchType = pDefaultSearchType;
    }

    /**
     * Returns redirectPage property.
     *
     * @return defaultSearchType property.
     */
    public String getRedirectPage() {
        return mRedirectPage;
    }

    /**
     * Sets the value of the redirectPage property.
     *
     * @param pRedirectPage parameter to set.
     */
    public void setRedirectPage(String pRedirectPage) {
        this.mRedirectPage = pRedirectPage;
    }

    private String getMergedTrail(String urlTrail, String paramTrail){
        StringBuffer sb = new StringBuffer(urlTrail);
        String[] values = paramTrail.split(":");
        for (int i = 0; i < values.length/2; i++){
            if (urlTrail.indexOf(values[2*i+1]) < 0){
                sb.append(':');
                sb.append(values[2*i]);
                sb.append(':');
                sb.append(values[2*i+1]);
            }
        }
        return sb.toString();
    }

}
