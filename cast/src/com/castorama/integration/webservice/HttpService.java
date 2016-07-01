package com.castorama.integration.webservice;

import java.net.URI;

/**
 * @author EPAM team
 */
public interface HttpService<T> {

    public T doPostRequest(URI uri, String body) throws Exception;

    public T doGetRequest(URI uri, String body) throws Exception;

}
