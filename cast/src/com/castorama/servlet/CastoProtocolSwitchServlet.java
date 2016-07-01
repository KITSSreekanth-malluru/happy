package com.castorama.servlet;

import atg.nucleus.ServiceException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CastoProtocolSwitchServlet extends InsertableServletImpl {
    /** METHOD_POST property */
    private static final String METHOD_POST = "POST";

    /** DOCUMENT ME! */
    public static final String HTTPS_PROTOCOL = "https";

    /** DOCUMENT ME! */
    public static final String HTTP_PROTOCOL = "http";

    /** DOCUMENT ME! */
    protected String[] mSecureURLList = null;

    /** DOCUMENT ME! */
    protected String[] mIgnoreURLList = null;

    /** DOCUMENT ME! */
    protected String mHostName;

    /** DOCUMENT ME! */
    protected int mPort;

    /** DOCUMENT ME! */
    protected int mSecurePort;

    /** DOCUMENT ME! */
    protected String mSecureHostName;

    /** DOCUMENT ME! */
    protected boolean mActive = false;

    /**
     * @see atg.servlet.pipeline.PipelineableServletImpl#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                 throws IOException, ServletException {
        if (isActive()) {
            String requestURI = pRequest.getRequestURI();
            String requestURIWithQueryString = pRequest.getRequestURIWithQueryString();

            if (isIgnoreURL(requestURI)) {
                passRequest(pRequest, pResponse);
            } else {
                boolean secure = isSecureURL(requestURI);
                boolean isOverHTTPS = HTTPS_PROTOCOL.equalsIgnoreCase(pRequest.getScheme());

                if (isSecureURL(requestURI) && !isOverHTTPS) {
                    String redirectURL = getSecureUrl(requestURIWithQueryString);

                    if (pRequest.getMethod().equals(METHOD_POST)) {
                        throw new ServletException("post is not switchable");
                    }

                    pResponse.sendRedirect(pResponse.encodeRedirectURL(redirectURL));
                } else if (!secure && isOverHTTPS) {
                    // get the URL to the non-secure server
                    String redirectURL = getNonSecureUrl(requestURIWithQueryString);

                    if (pRequest.getMethod().equals(METHOD_POST)) {
                        throw new ServletException("post is not switchable");
                    }

                    pResponse.sendRedirect(pResponse.encodeRedirectURL(redirectURL));
                } else {
                    passRequest(pRequest, pResponse);
                }  // end if-else
            }  // end if-else
        } else {
            passRequest(pRequest, pResponse);
        }  // end if-else
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pURL DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected boolean isSecureURL(String pURL) {
        if ((getSecureURLList() != null) && (getSecureURLList().length > 0)) {
            for (int i = 0; i < getSecureURLList().length; i++) {
                if (pURL.startsWith(getSecureURLList()[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pURL DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected boolean isIgnoreURL(String pURL) {
        if ((getIgnoreURLList() != null) && (getIgnoreURLList().length > 0)) {
            for (int i = 0; i < getIgnoreURLList().length; i++) {
                if (pURL.startsWith(getIgnoreURLList()[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns http url string.
     *
     * @param  request query string.
     *
     * @return http url string.
     *
     * @throws MalformedURLException when utl can not be created.
     */
    public String getNonSecureUrl(String pUrl) throws MalformedURLException {
        return (new URL(HTTP_PROTOCOL, getHostName(), getPort(), pUrl)).toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pUrl DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws MalformedURLException DOCUMENT ME!
     */
    protected String getSecureUrl(String pUrl) throws MalformedURLException {
        return (new URL(HTTPS_PROTOCOL, getSecureHostName(), getSecurePort(), pUrl)).toString();
    }

    /**
     * @see atg.servlet.pipeline.InsertableServletImpl#doStartService()
     */
    public void doStartService() throws ServiceException {
        if ((getSecureHostName() == null) || (getHostName() == null) || (getPort() == 0) || (getSecurePort() == 0)) {
            throw new ServiceException("either secureHostName, hostName, port or securePort not set");
        }

        super.doStartService();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getSecureURLList() {
        return mSecureURLList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pSecureURLList DOCUMENT ME!
     */
    public void setSecureURLList(String[] pSecureURLList) {
        mSecureURLList = pSecureURLList;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getIgnoreURLList() {
        return mIgnoreURLList;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pIgnoreURLList DOCUMENT ME!
     */
    public void setIgnoreURLList(String[] pIgnoreURLList) {
        mIgnoreURLList = pIgnoreURLList;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHostName() {
        return mHostName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pHostName DOCUMENT ME!
     */
    public void setHostName(String pHostName) {
        mHostName = pHostName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getPort() {
        return mPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pPort DOCUMENT ME!
     */
    public void setPort(int pPort) {
        mPort = pPort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSecurePort() {
        return mSecurePort;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pSecurePort DOCUMENT ME!
     */
    public void setSecurePort(int pSecurePort) {
        mSecurePort = pSecurePort;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSecureHostName() {
        return mSecureHostName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pSecureHostName DOCUMENT ME!
     */
    public void setSecureHostName(String pSecureHostName) {
        mSecureHostName = pSecureHostName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isActive() {
        return mActive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pActive DOCUMENT ME!
     */
    public void setActive(boolean pActive) {
        mActive = pActive;
    }
}
