package com.castorama.scenario;

import atg.nucleus.Nucleus;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.filter.ExpressionFilter;
import atg.process.filter.Filter;

import atg.repository.Repository;

/**
 *
 * @author     Andrei_Raichonak
 * @deprecated
 */
public class GodFatherFilter extends ExpressionFilter {
    /** profileRepository property. */
    private Repository mProfileRepository;

    /**
     * @see atg.process.filter.Filter#evaluate(atg.process.ProcessExecutionContext)
     */
    protected Filter evaluate(ProcessExecutionContext pContext) throws ProcessException {
        if (mProfileRepository == null) {
            mProfileRepository =
                (Repository) Nucleus.getGlobalNucleus().resolveName("/atg/userprofiling/ProfileAdapterRepository");
        }

        if (mProfileRepository == null) {
            throw new ProcessException("No Profile Repository");
        }

        return Filter.TRUE;
    }  // end method evaluate

}
