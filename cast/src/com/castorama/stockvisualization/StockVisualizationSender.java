/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.stockvisualization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import atg.nucleus.GenericService;

/**
 * Provides ability to send requests to configured URL and
 * retrieve data from response in string representation.
 *
 * @author Aliaksandr Belakurski
 */
public class StockVisualizationSender extends GenericService {
    // constants
    static final String REQUEST_METHOD = "POST";
    static final String ACCEPT_HEADER_NAME = "accept";
    static final String CONTENT_LENGTH = "Content-Length";
    private int connectTimeout;
    private int readTimeout;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Performs requesting web service via provided URL.
     *
     * @param pURL
     * @param pMIMEType
     * @return
     * @throws StockVisualizationException
     */
    public String sendRequest(URL pURL, String pMIMEType) throws StockVisualizationException {

        if (isLoggingDebug()) {
            logDebug("about to send request to the following URL - " + pURL);
        }
        if (pURL == null) {
            throw new StockVisualizationException("provided destination URL is null");
        }
        if (pMIMEType == null) {
            throw new StockVisualizationException("provided MIME type is null");
        }

        String response;
        InputStream is = null;
        URLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL postURL = new URL(pURL.getProtocol(), pURL.getHost(), pURL.getPort(), pURL.getPath());
            connection = postURL.openConnection();
            connection.setConnectTimeout(getConnectTimeout());
            connection.setReadTimeout(getReadTimeout());
            setupHeaders((HttpURLConnection) connection, pMIMEType, String.valueOf(pURL.getQuery().length()));
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(pURL.getQuery());
            out.close();

            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String newLine;
            while ((newLine = reader.readLine()) != null) {
                sb.append(newLine);
            }
            response = sb.toString();
        } catch (SocketTimeoutException e) {
            if (isLoggingError()){
                logError("Socket timeout error during VSK call:" +
                        "\n URL=" + pURL +
                        "\n connectionTimeOut=" + connection.getConnectTimeout() +
                        "\n readTimeOut=" + connection.getReadTimeout(), e);
            }
            throw new StockVisualizationException("Unable to send request to the following URL: " + pURL, e);
        } catch (IOException ioExc) {
            if (isLoggingError())
                logError("unexpected error occured during polling of web servic " + ioExc);

            throw new StockVisualizationException("Unable to send request to the following URL - " + pURL, ioExc);
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException ioExc) {
                    if (isLoggingError())
                        logError("Error during buffer reader of connection to URL: " + connection.getURL() + " closing.", ioExc);
                }
            if (is != null)
                try {
                    is.close();
                } catch (IOException ioExc) {
                    if (isLoggingError())
                        logError("Error during input stream of connection to URL: " + connection.getURL() + " closing.", ioExc);
                }
            if ((connection != null) &&
                    (connection instanceof HttpURLConnection))
                try {
                    ((HttpURLConnection) connection).disconnect();
                } catch (Exception exc) {
                    if (isLoggingError())
                        logError("Error during connection to URL: " + connection.getURL() + " disconect.", exc);
                }
        }

        if (isLoggingDebug()){
            logDebug("the following response was has been received from service " + response);
        }

        return response;
    }

    /*
     * Fills request headers with appropriate values.
     */
    private void setupHeaders(HttpURLConnection pConnection, String pMIMEType, String contentLength)
            throws ProtocolException {
        pConnection.setDoOutput(true);
        pConnection.setRequestMethod(REQUEST_METHOD);
        pConnection.setRequestProperty(ACCEPT_HEADER_NAME, pMIMEType);
        pConnection.setRequestProperty(CONTENT_LENGTH, contentLength);
    }

}
