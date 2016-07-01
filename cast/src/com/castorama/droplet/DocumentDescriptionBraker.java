package com.castorama.droplet;

import java.io.IOException;

import java.text.BreakIterator;

import java.util.Locale;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Return document description of passed length.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class DocumentDescriptionBraker extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** DESCRIPTION constant. */
    public static final String DESCRIPTION = "description";

    /** LENGTH constant. */
    public static final String LENGTH = "length";

    /** UPDATED_DESCRIPTION constant. */
    public static final String UPDATED_DESCRIPTION = "updatedDescription";

    /**
     * Returns document description of passed length.
     *
     * @param  pRequest  request
     * @param  pResponse response
     *
     * @throws ServletException error
     * @throws IOException      error
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String description = pRequest.getParameter(DESCRIPTION);
        String length = pRequest.getParameter(LENGTH);
        int intLength = 0;
        String result = description;

        if (!StringUtils.isBlank(length)) {
            try {
                intLength = Integer.parseInt(length);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
        }

        if (!StringUtils.isBlank(description) && (intLength > 0) && (description.length() > intLength)) {
            BreakIterator boundary = BreakIterator.getWordInstance(Locale.FRENCH);
            boundary.setText(description);
            int index = boundary.preceding(intLength);
            result = description.substring(0, index);
        }
        pRequest.setParameter(UPDATED_DESCRIPTION, result);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
