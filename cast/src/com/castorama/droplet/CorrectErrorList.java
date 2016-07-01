package com.castorama.droplet;

import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.droplet.DropletException;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;
import atg.userdirectory.droplet.Utils;

/**
 * Droplet remove from exception list (Vector of DropletExceptions) the
 * exception with specific <b>errorCode</b>
 * <dt>Input params:
 * <dd>
 * <li><code>errorCode</code> - the exception to remove
 * <dd>
 * <li><code>exceptionList</code> - the whole list of DropletExceptions
 * <dt>Oparams:
 * <dd>
 * <li><code>empty</code> if some params were missed of if the list of <code>
 * exceptions</code> are empty
 * <dd>
 * <li><code>output</code> if the list of <code>exceptions</code> are not empty
 * <dt>Output params:
 * <dd>
 * <li><code>exceptions</code> - the vector of form handler exceptions without
 * exception with error code = <code>errorCode</code>
 *
 * @author Katsiaryna_Dmitrievich
 */
public class CorrectErrorList extends DynamoServlet implements Constants {
    /** Output parameter name: "exceptions". */
    private static final String EXCEPTIONS = "exceptions";

    /** Input parameter name: "exceptionList" - the vector of exceptions. */
    private static final ParameterName EXCEPTION_LIST = ParameterName.getParameterName("exceptionList");

    /**
     * Input parameter name: "errorCode" - error code of exception to remove.
     */
    private static final ParameterName ERROR_CODE = ParameterName.getParameterName("errorCode");

    /**
     * Checks input parameters. Remove exception with error code == <code>
     * errorCode</code> from the vector of exceptions <code>
     * exceptionList</code>.
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        if (isLoggingDebug()) {
            logDebug(this.getClass() + ".service : begin");
        }
        String errorCode = pRequest.getParameter(ERROR_CODE);
        Vector<DropletException> exceptionList = (Vector<DropletException>) pRequest.getObjectParameter(EXCEPTION_LIST);

        if (isMissingParameters(errorCode, exceptionList)) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        Vector<DropletException> updExceptions = (Vector<DropletException>) exceptionList.clone();
        Iterator<DropletException> iter = updExceptions.iterator();
        while (iter.hasNext()) {
            DropletException exc = iter.next();
            if (errorCode.equalsIgnoreCase(exc.getErrorCode())) {
                iter.remove();
            }
        }
        if ((updExceptions != null) && (updExceptions.size() > 0)) {
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            pRequest.setParameter(EXCEPTIONS, updExceptions);
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * This method will determine if there are any missing parameters passed to
     * this droplet. If there are then an error is logged and this method will
     * return true. This method will look for the following parameters:
     * <li>errorCode
     * <li>exceptionList
     *
     * @param  errorCode     - param's value to check
     * @param  exceptionList - param's value to check
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(final String errorCode, final Vector<DropletException> exceptionList) {
        return checkParam(errorCode, ERROR_CODE) && checkParam(exceptionList, EXCEPTION_LIST);
    }

    /**
     * Check parameter: If it is a string it shouldn't be empty, if collection,
     * the size should be more then one. In all others cases value should not be
     * <code>null</code>
     *
     * @param  paramValue    - value of parameter to check
     * @param  parameterName - parameter name
     *
     * @return <code>true</code> if <code>paramValue</code> is incorrect, <code>
     *         false</code> otherwise.
     */
    private boolean checkParam(final Object paramValue, final ParameterName parameterName) {
        if (paramValue instanceof String) {
            if (StringUtils.isBlank((String) paramValue)) {
                if (isLoggingError()) {
                    Object[] args = {parameterName.getName()};
                    logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
                }
                return true;
            }
        } else if (paramValue instanceof Collection) {
            if ((paramValue == null) || ((paramValue != null) && (((Collection) paramValue).size() == 0))) {
                if (isLoggingError()) {
                    Object[] args = {parameterName.getName()};
                    logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
                }
                return true;
            }
        } else {
            if (paramValue == null) {
                if (isLoggingError()) {
                    Object[] args = {parameterName.getName()};
                    logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
                }
                return true;
            }
        }  // end if-else
        return false;
    }
}
