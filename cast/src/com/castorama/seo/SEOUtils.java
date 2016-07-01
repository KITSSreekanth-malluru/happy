package com.castorama.seo;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author EPAM team
 */
public class SEOUtils {
    private static final String ITEM_CASTORAMA_DOCUMENT = "castoramaDocument";

    /** CHARSET_UTF_8 constant. */
    private static final String CHARSET_UTF_8 = "UTF-8";

    /** EXTENSION_HTML constant. */
    public static final String EXTENSION_HTML = ".html";

    /** EXTENSION_HTM constant. */
    public static final String EXTENSION_HTM = ".htm";

    /** EXTENSION_DOC constant. */
    public static final String EXTENSION_DOC = ".html";

    /** PROPERTY_DISPLAY_NAME constant. */
    public static final String PROPERTY_DISPLAY_NAME = "displayName";

    /** PROPERTY_TITLE constant. */
    public static final String PROPERTY_TITLE = "title";

    /** ITEM_CATEGORY constant. */
    public static final String ITEM_CATEGORY = "category";
    /** Map with diacritics chars*/
    private static HashMap<String, String> replacedCharacters;
    
    /** PATTERN_SEARCH constant. */
    public static final Pattern PATTERN_PRODUCT_SEARCH = Pattern.compile("(htt(p|ps):/)?(/([^/]+))+/rechercher/([^/|^/?]*)(\\?([^/|^/?]+))?");

    /** PATTERN_DOCUMENT_SEARCH property */
    public static final Pattern PATTERN_DOCUMENT_SEARCH = Pattern.compile("(htt(p|ps):/)?(/([^/]+))+/rechercher2/([^/|^/?]*)(\\?([^/|^/?]+))?");

    /** PATTERN_STORE_SEARCH property */
    public static final Pattern PATTERN_STORE_SEARCH = Pattern.compile("(htt(p|ps):/)?(/([^/]+))+/rechercher3/([^/|^/?]*)(\\?([^/|^/?]+))?");

    /** PATTERN_MULTI_SEARCH property */
    public static final Pattern PATTERN_MULTI_SEARCH = Pattern.compile("(htt(p|ps):/)?(/([^/]+))+/rechercher4/([^/|^/?]*)(\\?([^/|^/?]+))?");
    
    /** searchPatternToSearchTypeMap property */
    private static final Map<Pattern, String> searchPatternToSearchTypeMap = new HashMap<Pattern, String>();
    
    /** Initializing diactritics map*/
    static {

        replacedCharacters = new HashMap<String, String>();
        replacedCharacters.put(Character.toString('\u00C0'), "A");
        replacedCharacters.put(Character.toString('\u00E0'), "a");
        replacedCharacters.put(Character.toString('\u00C2'), "A");
        replacedCharacters.put(Character.toString('\u00E2'), "a");
        replacedCharacters.put(Character.toString('\u00C4'), "A");
        replacedCharacters.put(Character.toString('\u00E4'), "a");
        replacedCharacters.put(Character.toString('\u00C7'), "C");
        replacedCharacters.put(Character.toString('\u00E7'), "c");
        replacedCharacters.put(Character.toString('\u00C8'), "E");
        replacedCharacters.put(Character.toString('\u00E8'), "e");
        replacedCharacters.put(Character.toString('\u00C9'), "E");
        replacedCharacters.put(Character.toString('\u00E9'), "e");
        replacedCharacters.put(Character.toString('\u00CA'), "E");
        replacedCharacters.put(Character.toString('\u00EA'), "e");
        replacedCharacters.put(Character.toString('\u00CB'), "E");
        replacedCharacters.put(Character.toString('\u00EB'), "e");
        replacedCharacters.put(Character.toString('\u00CE'), "I");
        replacedCharacters.put(Character.toString('\u00EE'), "i");
        replacedCharacters.put(Character.toString('\u00CF'), "I");
        replacedCharacters.put(Character.toString('\u00EF'), "i");
        replacedCharacters.put(Character.toString('\u00D4'), "O");
        replacedCharacters.put(Character.toString('\u00F4'), "o");
        replacedCharacters.put(Character.toString('\u00D6'), "O");
        replacedCharacters.put(Character.toString('\u00F6'), "o");
        replacedCharacters.put(Character.toString('\u00D9'), "U");
        replacedCharacters.put(Character.toString('\u00F9'), "u");
        replacedCharacters.put(Character.toString('\u00DB'), "U");
        replacedCharacters.put(Character.toString('\u00FB'), "u");
        replacedCharacters.put(Character.toString('\u00DC'), "U");
        replacedCharacters.put(Character.toString('\u00FC'), "u");
        replacedCharacters.put(Character.toString('\u0178'), "Y");
        replacedCharacters.put(Character.toString('\u00FF'), "y");
        
        searchPatternToSearchTypeMap.put(PATTERN_MULTI_SEARCH, "1");
        searchPatternToSearchTypeMap.put(PATTERN_PRODUCT_SEARCH, "2");
        searchPatternToSearchTypeMap.put(PATTERN_DOCUMENT_SEARCH, "3");
        searchPatternToSearchTypeMap.put(PATTERN_STORE_SEARCH, "4");
    }

    
    /**
     *
     * @param  pName parameter
     *
     * @return 
     * @throws UnsupportedEncodingException 
     */
    public static String prepareNameForURL(String pName) {
        String name = pName.replaceAll(" ", "-").replaceAll("/", "-");
        try {
            name = replaceChars(name);
            name = URLEncoder.encode(name, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
        }

        return name;
    }

    /**
     * Returns categoryURL property.
     *
     * @param  pCategotyID parameter to set.
     * @param  pRepository parameter to set.
     *
     * @return categoryURL property.
     *
     * @throws RepositoryException          - exception
     * @throws UnsupportedEncodingException - exception
     */
    public static String getCategoryURL(String pCategotyID, Repository pRepository)
                                 throws RepositoryException, UnsupportedEncodingException {
        RepositoryItem category = pRepository.getItem(pCategotyID, ITEM_CATEGORY);

        if (category != null) {
            return SEOUtils.getCategoryURL(category);
        } else {
            return null;
        }
    }

    /**
     * Returns categoryURL property.
     *
     * @param  pCategory parameter to set.
     *
     * @return categoryURL property.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String getCategoryURL(RepositoryItem pCategory) throws UnsupportedEncodingException {
        String name = (String) pCategory.getPropertyValue(PROPERTY_DISPLAY_NAME);
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append('/').append(name).append('-').append(pCategory.getRepositoryId()).append(EXTENSION_HTM);

        return url.toString();
    }

    /**
     * Returns productURL property.
     *
     * @param  pProductID  parameter to set.
     * @param  pRepository parameter to set.
     *
     * @return productURL property.
     *
     * @throws RepositoryException          - exception
     * @throws UnsupportedEncodingException - exception
     */
    public static String getProductURL(String pProductID, Repository pRepository) throws RepositoryException,
                                                                                         UnsupportedEncodingException {
        RepositoryItem product = pRepository.getItem(pProductID, "product");

        if (product != null) {
            return SEOUtils.getProductURL(product);
        } else {
            return null;
        }
    }

    /**
     * Returns productURL property.
     *
     * @param  pProduct parameter to set.
     *
     * @return productURL property.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String getProductURL(RepositoryItem pProduct) throws UnsupportedEncodingException {
        String name = (String) pProduct.getPropertyValue(PROPERTY_DISPLAY_NAME);
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append('/').append(name).append('-').append(pProduct.getRepositoryId()).append(EXTENSION_HTML);

        return url.toString();
    }
    /**
     * Returns documentURL property.
     *
     * @param  pDocumentID  parameter to set.
     * @param  pRepository parameter to set.
     *
     * @return documentURL property.
     *
     * @throws RepositoryException          - exception
     * @throws UnsupportedEncodingException - exception
     */
    public static String getDocumentURL(String pDocumentID, Repository pRepository) throws RepositoryException,
                                                                                         UnsupportedEncodingException {
        RepositoryItem document = pRepository.getItem(pDocumentID, ITEM_CASTORAMA_DOCUMENT);

        if (document != null) {
            return SEOUtils.getDocumentURL(document);
        } else {
            return null;
        }
    }

    /**
     * Returns documentURL property.
     *
     * @param  pDocument parameter to set.
     *
     * @return documentURL property.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String getDocumentURL(RepositoryItem pDocument) throws UnsupportedEncodingException {
        String externalURL = getNotEmptyDocExtURL(pDocument);
        if (externalURL != null) {
            return externalURL;
        }
        String name = (String) pDocument.getPropertyValue(PROPERTY_TITLE);
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append("/doc/").append(name).append('-').append(pDocument.getRepositoryId()).append(EXTENSION_DOC);

        return url.toString();
    }
    public static String getDocumentURL(RepositoryItem pDocument,String location) throws UnsupportedEncodingException {
        String externalURL = getNotEmptyDocExtURL(pDocument);
        if (externalURL != null) {
            return externalURL;
        }
        String name = (String) pDocument.getPropertyValue(PROPERTY_TITLE);
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append(location).append("/doc/").append(name).append('-').append(pDocument.getRepositoryId()).append(EXTENSION_DOC);

        return url.toString();
    }
    /**
     * Returns topicURL property.
     *
     * @param  pTopic parameter to set.
     *
     * @return topicURL property.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String getTopicURL(RepositoryItem pTopic) throws UnsupportedEncodingException {
        String name = (String) pTopic.getPropertyValue(PROPERTY_TITLE);
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append("/lancez-vous/").append(name).append('-').append(pTopic.getRepositoryId()).append(EXTENSION_HTML);

        return url.toString();
    }
    /**
     * Returns filterURL property.
     *
     * @param  pFilter parameter to set.
     *
     * @return filterURL property.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String getFilterURL(RepositoryItem pFilter) throws UnsupportedEncodingException {
        String name = (String) pFilter.getPropertyValue("tagTitle");
        name = SEOUtils.prepareNameForURL(name);

        StringBuffer url = new StringBuffer();
        url.append("/conseils-et-forums/").append(name).append('+').append(pFilter.getRepositoryId()).append(EXTENSION_HTML);

        return url.toString();
    }
    /**
     * Replaces chars with diacritic on chars without diacritic.
     *
     * @param  str String with diacritic chars.
     *
     * @return string without diacritic chars.
     *
     * @throws UnsupportedEncodingException - exception
     */
    public static String replaceChars(String str) throws UnsupportedEncodingException{
        StringBuffer url = new StringBuffer(str);
        for (String c: replacedCharacters.keySet()){
            int num = 0;
            while ((num = url.indexOf(c))>=0){
                url.replace(num, num+1, replacedCharacters.get(c));
            }
        }
        Scanner sc = new Scanner(url.toString()).useDelimiter("[\\W&&[^-]]");
        url.delete(0, url.length());
        while (sc.hasNext()){
            url.append(sc.next());
        }
        return url.toString();
    }
    
    /**
     * Returns search type value by url.
     *
     * @param  url String.
     *
     * @return search type value.
     */
    public static String getSearchType(String url) {
        if (PATTERN_PRODUCT_SEARCH.matcher(url).matches()){
            return getSearchType(SEOUtils.PATTERN_PRODUCT_SEARCH);
        } else if (PATTERN_DOCUMENT_SEARCH.matcher(url).matches()){
            return getSearchType(SEOUtils.PATTERN_DOCUMENT_SEARCH);
        } else if (SEOUtils.PATTERN_STORE_SEARCH.matcher(url).matches()){
            return getSearchType(SEOUtils.PATTERN_STORE_SEARCH);
        } else if (SEOUtils.PATTERN_MULTI_SEARCH.matcher(url).matches()){
            return getSearchType(SEOUtils.PATTERN_MULTI_SEARCH);
        } else {
            return null;
        }
        
    }

    /**
     * Returns search type value by pattern.
     *
     * @param  pattern search pattern
     *
     * @return search type value.
     */
    public static String getSearchType(Pattern pattern) {
        return searchPatternToSearchTypeMap.get(pattern);
    }

    /**
     * REmoves empty parameters from URL
     *
     * @param  URL string
     *
     * @return URL string without empty parameters.
     */
    public static String removeEmptyParamsFromURL(String url) {
        if (null == url || url.length()==0) return null;
        int pos = url.lastIndexOf('?');
        String tempUrl = null;
        String queryString = null;
        if (pos < 0){
            tempUrl = "";
            queryString = url;
        } else {
            tempUrl = url.substring(0, pos);
            if (pos<url.length()){
                queryString = url.substring(pos+1);
            } else {
                return url.substring(0, url.length()-1);
            }
        }
        Pattern p = Pattern.compile("([^/|^?|^&]+=[^/|^?|^&]*)&?", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(queryString);
        StringBuilder sb = new StringBuilder(tempUrl);
        sb.append('?');
        while (m.find()){
            String g = m.group(1);
            if (!g.endsWith("=")){
                sb.append(g);
                sb.append('&');
            }
        }
        if (sb.length()>0){
            sb.delete(sb.length()-1, sb.length());
        }
        return sb.toString();
    }
    
    public static String createQueryStringWithoutEmptyParams(DynamoHttpServletRequest pRequest, Set<ParameterName> params){
        Iterator<ParameterName> it  = params.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()){
            ParameterName paramName = it.next();
            Object paramValue = pRequest.getLocalParameter(paramName);
            String sParamValue = paramValue!=null?paramValue.toString():null;
            if (!StringUtils.isEmpty(sParamValue)){
                sb.append(paramName.toString());
                sb.append('=');
                sb.append(sParamValue);
                sb.append('&');
            }
            
        }
        if (null != sb && sb.length()>0){
            sb.delete(sb.length()-1, sb.length());
            sb.insert(0, '?');
        }
        return sb.toString();
    }
    
    public static String getNotEmptyDocExtURL(RepositoryItem pDocument){
        String externalURL = (String) pDocument.getPropertyValue("externalURL");
        if (externalURL != null && externalURL.length() > 0) {
            return externalURL;
        }
        return null;
    }
}
