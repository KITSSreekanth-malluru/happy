package com.castorama.iphone;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import atg.nucleus.Nucleus;
import atg.servlet.BrowserTyper;

public class IphoneFilter implements Filter {
    
    private static final String IPHONE_JSP = "/iphone.jsp";

    private static final String IPHONE = "Iphone";
    
    private static final String CASTORAMA_IPHONE_ASKED = "CASTORAMA_IPHONE_ASKED";

    /**
     * The request header with the User-Agent information in it.
     */
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";

    private static final String PARAMETER_MOBILE = "fromIphone";

    private static final String STRING_TRUE = "true";

    private static BrowserTyper mTyper = null;

    static {
         mTyper = (BrowserTyper) Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/servlet/pipeline/BrowserTyper");
    }
    
    
    public void doFilter(final ServletRequest pRequest,
            final ServletResponse pResponse, final FilterChain pChain)
            throws IOException, ServletException {
        if (!(pRequest instanceof HttpServletRequest)) {
            pChain.doFilter(pRequest, pResponse);
            return;
        }

    
        HttpServletRequest httpRequest = (HttpServletRequest) pRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) pResponse;

        
        boolean isIphone;

            String userAgent = httpRequest.getHeader(USER_AGENT_HEADER_NAME);
            //ATG way to detect iphone
            isIphone = isIphone(userAgent, httpRequest, httpResponse);
            

            if (isIphone) {
            String iphoneParameter = httpRequest.getParameter(PARAMETER_MOBILE);
            if (iphoneParameter != null) {
                if (iphoneParameter.equalsIgnoreCase(STRING_TRUE)) {
                    pChain.doFilter(pRequest, pResponse);
                } else {
                RequestDispatcher dispatcher = pRequest.getRequestDispatcher(IPHONE_JSP);
                dispatcher.forward(pRequest, pResponse);
                }
            } else {
                if (isAlreadyActive(httpRequest)) {
                pChain.doFilter(pRequest, pResponse);
                } else {
                    RequestDispatcher dispatcher = pRequest.getRequestDispatcher(IPHONE_JSP);
                    dispatcher.forward(pRequest, pResponse);
                }
            }
            
        } else {
            pChain.doFilter(pRequest, pResponse);
        }
    }

    private boolean isIphone(String userAgent, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        boolean temp = mTyper.isBrowserType(IPHONE, userAgent);
        return temp;
    }


    /**
     * Unused.
     * 
     * @param pConfig
     *            the config
     * 
     * @throws ServletException
     *             the servlet exception
     */
    public void init(final FilterConfig pConfig) throws ServletException {
    }

    /**
     * Unused.
     */
    public void destroy() {
    }

    private boolean isAlreadyActive(HttpServletRequest httpRequest) {
        HttpSession httpSession = httpRequest.getSession(true);
        Boolean active = (Boolean)httpSession.getAttribute(CASTORAMA_IPHONE_ASKED);
        if (active != null) {
            return true;
        } else {
            httpSession.setAttribute(CASTORAMA_IPHONE_ASKED, Boolean.TRUE);
            return false;
        }
    }


}
