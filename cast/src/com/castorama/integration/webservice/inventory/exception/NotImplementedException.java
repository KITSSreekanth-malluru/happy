package com.castorama.integration.webservice.inventory.exception;

/**
 * @author EPAM team
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super("This method is not implemented");
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

}
