package com.castorama.integration.webservice;

/**
 * @author EPAM team
 */
public interface Processor<S, T> {

    public S process(T itemToProcess);

}
