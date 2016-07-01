package com.castorama.integration.webservice.inventory.support;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import com.castorama.integration.webservice.HttpService;
import com.castorama.integration.webservice.inventory.exception.NotImplementedException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * @author EPAM team
 */
public class HttpRequestService extends GenericService implements HttpService<String> {

    //region Dependencies
    private static final String STRING_ENCODING = "UTF-8";
    private PoolingHttpClientConnectionManager cm;
    private int connectionsNumber;
    
    private int connectTimeout;
    private int socketTimeout;    
    //endregion

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public HttpRequestService() {
        cm = new PoolingHttpClientConnectionManager();
    }

    @Override
    public String doPostRequest(URI uri, String body) throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        StringEntity stringentity = new StringEntity(body, STRING_ENCODING);

        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(RequestConfig.custom().setSocketTimeout(getSocketTimeout()).setConnectTimeout(getConnectTimeout()).build());
        //set connection keep-alive for better request performance
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.setEntity(stringentity);
        logRequestHeaders(httpPost);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        logResponseHeaders(httpResponse);
        HttpEntity entity = httpResponse.getEntity();

        return EntityUtils.toString(entity);
    }

    private void logRequestHeaders(HttpRequestBase request) {
        if (isLoggingDebug()) {
            Header[] headers = request.getAllHeaders();
            for (Header header : headers) {
                vlogDebug("[Request header]: [{0}] - [{1}]", header.getName(), header.getValue());
            }
        }
    }

    private void logResponseHeaders(HttpMessage requestBase) {
        if (isLoggingDebug()) {
            Header[] headers = requestBase.getAllHeaders();
            for (Header header : headers) {
                vlogDebug("[Response header]: [{0}] - [{1}]", header.getName(), header.getValue());
            }
        }
    }

    @Override
    public String doGetRequest(URI uri, String body) {
        throw new NotImplementedException();
    }

    @Override
    public void doStartService() throws ServiceException {
        cm.setMaxTotal(connectionsNumber);
    }

    //region Getters/Setters
    public int getConnectionsNumber() {
        return connectionsNumber;
    }

    public void setConnectionsNumber(int connectionsNumber) {
        this.connectionsNumber = connectionsNumber;
    }
    //endregion

}
