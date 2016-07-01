package com.castorama.scenario;

import atg.commerce.CommerceException;

import atg.commerce.order.OrderManager;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.filter.ExpressionFilter;
import atg.process.filter.Filter;

import atg.repository.RepositoryItem;

import com.castorama.scenario.configuration.FirstOrderFilterConfiguration;

/**
 *
 * @author Andrei_Raichonak
 */
public class FirstOrderFilter extends ExpressionFilter {
    /** mOrderManager property. */
    private OrderManager mOrderManager;

    /**
     * @see atg.process.filter.ExpressionFilter#configure(java.lang.String, java.lang.Object)
     */
    public void configure(String pOperator, Object pConfiguration) throws ProcessException {
        FirstOrderFilterConfiguration config = (FirstOrderFilterConfiguration) pConfiguration;
        mOrderManager = config.getOrderManager();
    }

    /**
     * @see atg.process.filter.Filter#evaluate(atg.process.ProcessExecutionContext)
     */
    protected Filter evaluate(ProcessExecutionContext pContext) throws ProcessException {
        RepositoryItem profile = (RepositoryItem) pContext.getSubject();

        try {
            int count =
                mOrderManager.getOrderCountForProfile(profile.getRepositoryId()) -
                mOrderManager.getOrderCountForProfileInState(profile.getRepositoryId(), new int[] {0});

            return (count == 1) ? Filter.TRUE : Filter.FALSE;
        } catch (CommerceException e) {
            throw new ProcessException(e);
        }
    }

}
