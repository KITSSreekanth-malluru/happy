package com.castorama.invite;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Helper droplet for checking if current Referrer Program is active
 *
 * @author Andrei_Raichonak
 */
public class ReferrerProgramAvailabilityDroplet extends DynamoServlet {
    /** referrerProgramConfig property. */
    private ReferrerProgramConfig mReferrerProgramConfig;

    /**
     * Returns referrerProgramConfig property.
     *
     * @return referrerProgramConfig property.
     */
    public ReferrerProgramConfig getReferrerProgramConfig() {
        return mReferrerProgramConfig;
    }

    /**
     * Sets the value of the referrerProgramConfig property.
     *
     * @param pReferrerProgramConfig parameter to set.
     */
    public void setReferrerProgramConfig(ReferrerProgramConfig pReferrerProgramConfig) {
        mReferrerProgramConfig = pReferrerProgramConfig;
    }

    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        if (getReferrerProgramConfig().isReferrerProgrameActive()) {
            pRequest.serviceLocalParameter("true", pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter("false", pRequest, pResponse);
        }
    }
}
