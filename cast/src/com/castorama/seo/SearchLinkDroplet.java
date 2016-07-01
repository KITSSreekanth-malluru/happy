package com.castorama.seo;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class SearchLinkDroplet extends DynamoServlet {
    
    /** CONTEXT_PATH constant for input parameter name */
    private static final String CONTEXT_PATH = "contextPath";
    /** QUESTION constant for input parameter name */
    private static final String QUESTION = "question";
    /** URL constant for output parameter name*/
    private static final String URL = "url";
    
    /** CONTEXT_PATH constant for oparam name*/
    private static final String OPARAM_OUTPUT = "output";
    /** CONTEXT_PATH constant*/
    private static final char SPACE_CHARACTER = ' ';
    /** CONTEXT_PATH constant*/
    private static final char DEF_CHARACTER = '-';
    
    private String mSearchPattern;
    private Map<String, String> mReplaceMap;

    /**
     * 
     * Replace symbols in question of search and form well-formed url
     * 
     * @param pRequest
     *            parameter
     * @param pResponse
     *            parameter
     * 
     * @throws ServletException
     *             exception
     * @throws IOException
     *             exception
     */
    public void service(DynamoHttpServletRequest request,
            DynamoHttpServletResponse response) throws ServletException, IOException {
        //accessing input parameters
        String contextPath = null;
        contextPath = request.getParameter(CONTEXT_PATH);
        if (contextPath == null) {
            if (isLoggingError()) {
                logError("parameter " + CONTEXT_PATH + " is required");
            }
            throw new ServletException("parameter " + CONTEXT_PATH + " is required");
        }
        
        String question = null;
        question = request.getParameter(QUESTION);
        if (question == null) {
            if (isLoggingError()) {
                logError("parameter " + QUESTION + " is required");
            }
            throw new ServletException("parameter " + QUESTION + " is required");
        }
        
        //replacing symbols specifying in replaceMap as keys
        question = replaceSymbolsInQuestion(question);
        
        //form url
        StringBuffer url = new StringBuffer();
        url.append(contextPath).append('/').append(mSearchPattern).append('/');        
        url.append(question);
        
        request.setParameter(URL, url.toString());
        
        request.serviceParameter(OPARAM_OUTPUT, request, response);
        
    }
    
    /**
     * Replace all symbols in pQuestion string with symbols that include in mReplaceMap
     * @param pQuestion string to replace symbol in
     * @return
     */
    private String replaceSymbolsInQuestion(String pQuestion) {        
        //workaround for space character
        String newQuestion = pQuestion.replace(SPACE_CHARACTER, DEF_CHARACTER);
        
        if (getReplaceMap() != null) {
            Set<String> symbolsToReplace = getReplaceMap().keySet();
            for (String symbols: symbolsToReplace) {
                if (newQuestion.contains(symbols)) {
                    String replacement = getReplaceMap().get(symbols);
                    newQuestion = newQuestion.replace(symbols, replacement);
                }
            }
        }
        return newQuestion;
    }
    
    /**
     * return mSearchPattern property
     * @return mSearchPattern property value
     */
    public String getSearchPattern() {
        return mSearchPattern;
    }
    
    /**
     * set pSearchPattern property
     * @param pSearchPattern property value
     */
    public void setSearchPattern(String pSearchPattern) {
        this.mSearchPattern = pSearchPattern;
    }
    
    /**
     * return mReplaceMap property
     * @return mReplaceMap property value
     */
    public Map<String, String> getReplaceMap() {
        return mReplaceMap;
    }
    
    /**
     * set pReplaceMap property
     * @param pReplaceMap map
     */
    public void setReplaceMap(Map<String, String> pReplaceMap) {
        this.mReplaceMap = pReplaceMap;
    }

}
