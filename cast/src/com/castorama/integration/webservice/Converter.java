package com.castorama.integration.webservice;

/**
 * @author EPAM team
 */
public interface Converter<T, S> {

    T convertToMessage(S s) throws Exception;

    S convertFromMessage(T t) throws Exception;

}
