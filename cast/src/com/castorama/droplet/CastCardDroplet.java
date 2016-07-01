package com.castorama.droplet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;

import javax.servlet.ServletException;

import com.castorama.CastConfiguration;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Receive XML response from "Sofinko".
 *
 * @author Alena_Karpenkava
 */
public class CastCardDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant. */
    static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** OUT constant. */
    static final String OUT = "out";

    /** Constants */
    static final String OUT_NODE = "<OUT>";

    /** MTT_PARAM constant. */
    static final String MTT_PARAM = "mtt";

    /** URL_PARAM constant. */
    static final String URL_PARAM = "?ws=S&id=114I01&mtt=";

    /** PROXY_SET constant. */
    static final String PROXY_SET = "proxySet";

    /** PROXY_HOST constant. */
    static final String PROXY_HOST = "proxyHost";

    /** PROXY_PORT constant. */
    static final String PROXY_PORT = "proxyPort";

    /** HTTPS_PROXY_SET constant. */
    static final String HTTPS_PROXY_SET = "https.proxySet";

    /** HTTPS_PROXY_HOST constant. */
    static final String HTTPS_PROXY_HOST = "https.proxyHost";

    /** HTTPS_PROXY_PORT constant. */
    static final String HTTPS_PROXY_PORT = "https.proxyPort";

    /** TRUE constant. */
    static final String TRUE = "true";

    /** configuration property. */
    private CastConfiguration mConfiguration;
    
    /** mConnectionTimeout property. */
    private int mConnectionTimeout;

    /** mReadTimeout property. */
    private int mReadTimeout;

    
    /**
     * Returns configuration property.
     *
     * @return configuration property.
     */
    public CastConfiguration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Sets the value of the configuration property.
     *
     * @param pConfiguration parameter to set.
     */
    public void setConfiguration(CastConfiguration pConfiguration) {
        mConfiguration = pConfiguration;
    }

    /**
     * Returns mConnectionTimeout property.
     *
     * @return mConnectionTimeout property.
     */
    public int getConnectionTimeout() {
        return mConnectionTimeout;
    }

    /**
     * Sets the value of the mConnectionTimeout property.
     *
     * @param pConnectionTimeout parameter to set.
     */
    public void setConnectionTimeout(int pConnectionTimeout) {
        mConnectionTimeout = pConnectionTimeout;
    }
    
    /**
     * Returns mReadTimeout property.
     *
     * @return mReadTimeout property.
     */
    public int getReadTimeout() {
        return mReadTimeout;
    }

    /**
     * Sets the value of the mReadTimeout property.
     *
     * @param pReadTimeout parameter to set.
     */
    public void setReadTimeout(int pReadTimeout) {
        mReadTimeout = pReadTimeout;
    }
    
    /**
     * Receive XML response from "Sofinko".
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws IOException      - exception
     * @throws ServletException -exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        pResponse.setDateHeader("Expires", 0);
        pResponse.setDateHeader("Last-Modified", new Date().getTime());
        pResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        pResponse.setHeader("Pragma", "no-cache");
        pResponse.setContentType("text/xml");

/*        System.getProperties().put(PROXY_SET, TRUE);
        System.getProperties().put(PROXY_HOST, getConfiguration().getHttpProxyHost());
        System.getProperties().put(PROXY_PORT, getConfiguration().getHttpProxyPort());
        System.getProperties().put(HTTPS_PROXY_SET, TRUE);
        System.getProperties().put(HTTPS_PROXY_HOST, getConfiguration().getHttpsProxyHost());
        System.getProperties().put(HTTPS_PROXY_PORT, getConfiguration().getHttpsProxyPort());
*/
        BufferedReader in = null;
        URL url = null;
        try {
            url = new URL(getConfiguration().getUrlTranscred() + URL_PARAM + pRequest.getParameter(MTT_PARAM));
            URLConnection urlConn = (URLConnection) url.openConnection();
            urlConn.setConnectTimeout(getConnectionTimeout());
            urlConn.setReadTimeout(getReadTimeout());
            StringBuffer contenu = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "ISO-8859-1"));
            String line = null;
            while ((line = in.readLine()) != null) {
                contenu.append(line);
            }
            String strContent = contenu.toString();
            if ((strContent != null) && (strContent != "")) {
                if (strContent.indexOf(OUT_NODE) != -1) {
                    pRequest.setParameter(OUT, strContent.substring(strContent.indexOf(OUT_NODE)));
                }
            }
            
        } catch (SocketTimeoutException socketTimeoutException) {
            if (isLoggingError()) {
                logError("Error connecting to url \""+url
                        +"\"",
                        socketTimeoutException);
            }           
        } catch (MalformedURLException malformedURLException) {
            if (isLoggingError()) {
                logError(malformedURLException);
            }           
        } catch (FileNotFoundException fileNotFoundException) {
        	if (isLoggingError()) {
        		logError("Failed to open stream to URL\""+url
                        +"\"", fileNotFoundException);
        	}            
        } catch (IOException iOException) {
        	if (isLoggingError()) {
        		logError("Error reading URL content\""+url
                        +"\"", iOException);
        	}            
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }  // end try-catch-finally
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
