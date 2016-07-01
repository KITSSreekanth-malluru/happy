package com.castorama.scenario.configuration;

import atg.nucleus.GenericService;

import com.castorama.invite.ReferrerProgramConfig;

/**
 *
 * @author Andrei_Raichonak
 */
public class ReferrerProgramIsActiveConfiguration extends GenericService {
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

}
