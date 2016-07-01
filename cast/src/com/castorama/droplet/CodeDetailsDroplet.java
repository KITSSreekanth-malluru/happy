package com.castorama.droplet;

import java.io.IOException;

import java.util.Properties;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM team
 */
public class CodeDetailsDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** DETAILS constant. */
    public static final String DETAILS = "details";

    /** CODE constant. */
    public static final String CODE = "code";

    /** DEFAULT_VALUE constant. */
    public static final String DEFAULT_VALUE = "default";

    /** detailsMap property. */
    private Properties mDetailsMap;

    /** showDetails property. */
    private boolean mShowDetails = true;

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String details = null;
        String code = pRequest.getParameter(CODE);
        String def = pRequest.getParameter(DEFAULT_VALUE);
        if (null == def) {
            def = "";
        }
        if (isShowDetails()) {
            Properties detailsMap = getDetailsMap();
            if ((null != code) && (0 < code.length()) && (null != detailsMap)) {
                details = detailsMap.getProperty(code);
            }
        } else {
            details = "";
        }
        pRequest.setParameter(DETAILS, (null == details) ? def : details);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }

    /**
     * Returns detailsMap property.
     *
     * @return detailsMap property.
     */
    public Properties getDetailsMap() {
        return mDetailsMap;
    }

    /**
     * Sets the value of the detailsMap property.
     *
     * @param pDetailsMap parameter to set.
     */
    public void setDetailsMap(Properties pDetailsMap) {
        mDetailsMap = pDetailsMap;
    }

    /**
     * Returns showDetails property.
     *
     * @return showDetails property.
     */
    public boolean isShowDetails() {
        return mShowDetails;
    }

    /**
     * Sets the value of the showDetails property.
     *
     * @param pShowDetails parameter to set.
     */
    public void setShowDetails(boolean pShowDetails) {
        mShowDetails = pShowDetails;
    }

}
