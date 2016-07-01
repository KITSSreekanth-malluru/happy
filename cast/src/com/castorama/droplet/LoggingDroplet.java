/**
 * 
 */
package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author EPAM team
 *
 */
public class LoggingDroplet extends DynamoServlet {

    /** MESSAGE constant. */
    private static final String MESSAGE = "message";
    
    /** LOG_LEVEL constant. */
    private static final String LOG_LEVEL = "logLevel";
    private static final String LOG_LEVEL_TRACE = "trace";
    private static final String LOG_LEVEL_DEBUG = "debug";
    private static final String LOG_LEVEL_INFO = "info";
    private static final String LOG_LEVEL_WARN = "warn";
    private static final String LOG_LEVEL_ERROR = "error";

    /**
     * 
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws ServletException exception
     * @throws IOException exception
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        String logLevel = pRequest.getParameter(LOG_LEVEL);
        String message = pRequest.getParameter(MESSAGE);
        
        if ((message != null) && (message.length() > 0)) {
            if (LOG_LEVEL_TRACE.equalsIgnoreCase(logLevel)) {
                if (isLoggingTrace()) {
                    logTrace(message);
                }
            } else if (LOG_LEVEL_DEBUG.equalsIgnoreCase(logLevel)) {
                if (isLoggingDebug()) {
                    logDebug(message);
                }
            } else if (LOG_LEVEL_INFO.equalsIgnoreCase(logLevel)) {
                if (isLoggingInfo()) {
                    logInfo(message);
                }
            } else if (LOG_LEVEL_WARN.equalsIgnoreCase(logLevel)) {
                if (isLoggingWarning()) {
                    logWarning(message);
                }
            } else if (LOG_LEVEL_ERROR.equalsIgnoreCase(logLevel)) {
                if (isLoggingError()) {
                    logError(message);
                }
            } else {
                if (isLoggingInfo()) {
                    logInfo(message);
                }
            }
        }
    }
}
