package com.castorama.seo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import atg.nucleus.Nucleus;
import atg.servlet.BrowserTyper;

/**
 * Based on http://www.digitalsanctuary.com/tech-blog/general/jboss-jsessionid-parameter-remove.html
 * 
 * TODO Associate this URL only to requests that generate SESSION
 * TODO Test association only to  <dispatcher>REQUEST</dispatcher>, if it does not work put it on every thing
 * <filter-mapping>
    <filter-name>PageFilter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
  </filter-mapping>
 * </pre>
 */
public class DisableUrlSessionFilter implements Filter {

    /**
     * The request header with the User-Agent information in it.
     */
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";

    /**
     * Filters requests to disable URL-based session identifiers.
     * 
     * @param pRequest
     *            the request
     * @param pResponse
     *            the response
     * @param pChain
     *            the chain
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ServletException
     *             the servlet exception
     */
    
    private static BrowserTyper mTyper = null; 
    
    static {
         mTyper = (BrowserTyper) Nucleus.getGlobalNucleus().resolveName("/atg/dynamo/servlet/pipeline/BrowserTyper");
    }
    
    
    public void doFilter(final ServletRequest pRequest,
            final ServletResponse pResponse, final FilterChain pChain)
            throws IOException, ServletException {
        // skip non-http requests
        if (!(pRequest instanceof HttpServletRequest)) {
            pChain.doFilter(pRequest, pResponse);
            return;
        }

    
        HttpServletRequest httpRequest = (HttpServletRequest) pRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) pResponse;

        
        boolean isRobot = false;

        if (httpRequest != null) {
            String userAgent = httpRequest.getHeader(USER_AGENT_HEADER_NAME);
            // TODO Modify this to Use ATG way to detect ROBOTS
            isRobot = isRobot(userAgent, httpRequest, httpResponse);
            if(httpResponse != null) {
              	setHttpOnlySessionId(httpRequest, httpResponse);
            }
        }

        try {
        	pChain.doFilter(pRequest, pResponse);
        } finally {
          if (isRobot) {
                    // Wrap only 
                    HttpSession httpSession = httpRequest.getSession(false);
                    if(httpSession != null) {
                    	httpSession.setMaxInactiveInterval(30);
                    }
          }
        }
    }

    private boolean isRobot(String userAgent, HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        boolean temp = mTyper.isBrowserType("robot", userAgent);
        return temp;
    }
    
    private void setHttpOnlySessionId(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession();
    	if(session != null) {
    		String sessionId = session.getId();
    		response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionId + "; HttpOnly; Path=/");
    	}
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
    
}
