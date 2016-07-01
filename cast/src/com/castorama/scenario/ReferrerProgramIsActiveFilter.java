package com.castorama.scenario;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.filter.ExpressionFilter;
import atg.process.filter.Filter;

import com.castorama.invite.ReferrerProgramConfig;

import com.castorama.scenario.configuration.ReferrerProgramIsActiveConfiguration;

/**
 *
 * @author Andrei_Raichonak
 */
public class ReferrerProgramIsActiveFilter extends ExpressionFilter {
    /** referrerProgramConfig property. */
    private ReferrerProgramConfig mReferrerProgramConfig;

    /**
     * @see atg.process.filter.ExpressionFilter#configure(java.lang.String, java.lang.Object)
     */
    public void configure(String pOperator, Object pConfiguration) throws ProcessException {
        ReferrerProgramIsActiveConfiguration config = (ReferrerProgramIsActiveConfiguration) pConfiguration;
        mReferrerProgramConfig = config.getReferrerProgramConfig();
    }

    /**
     * @see atg.process.filter.Filter#evaluate(atg.process.ProcessExecutionContext)
     */
    protected Filter evaluate(ProcessExecutionContext pContext) throws ProcessException {
        if (mReferrerProgramConfig.isReferrerProgrameActive()) {
            return Filter.TRUE;
        } else {
            return Filter.FALSE;
        }
    }
}
