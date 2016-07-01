package com.castorama.seo;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class WrapPageLinkDroplet extends DynamoServlet {
    
    /** PAGES_STRING constant. */
    private static final String PAGES_STRING = "pages";
    /** HTML_EXP constant. */
    private static final String HTML_EXP = ".html";
    
    /** PARAM_ID constant. */
    private static final String PARAM_ID = "id";    
    /** CONTEXT_PATH constant. */
    private static final String CONTEXT_PATH = "contextPath";
    
    /** URL constant. */
    private static final String URL = "url";    
    
    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";
    

    /**
     * 
     * Forms url for wrapped promoInformation according to next pattern
     * %contextPath%/wrap/id-%promoInfo_repositoryId%.html
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
        //accessing input param        
        String contextPath = null;
        contextPath = request.getParameter(CONTEXT_PATH);
        
        String id = null;
        id = request.getParameter(PARAM_ID);
        if (id == null) {
            if (isLoggingError()) {
                logError("parameter " + PARAM_ID + " is required");
            }
            throw new ServletException("parameter " + PARAM_ID + " is required");
        }        
        
        //generating url-address
        StringBuffer resultUrl = new StringBuffer();
        if (contextPath != null) {
            resultUrl.append(contextPath);
        }
        resultUrl.append('/').append(PAGES_STRING);
        resultUrl.append('/').append(id).append(HTML_EXP);
        
        //setting output parameter to generated url-address
        request.setParameter(URL, resultUrl.toString());
        
        //rendering output
        request.serviceParameter(OPARAM_OUTPUT, request, response);        
    }
}
