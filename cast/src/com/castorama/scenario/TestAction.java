package com.castorama.scenario;

import java.util.Map;

import atg.commerce.promotion.PromotionActionConfiguration;
import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionTools;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.action.ActionImpl;

import atg.repository.RepositoryItem;

/**
 *
 * @author EPAM team
 */
public class TestAction extends ActionImpl {
    /** PARAM_TEST1 constant. */
    public static final String PARAM_TEST1 = "test1";

    /** PARAM_TEST2 constant. */
    public static final String PARAM_TEST2 = "test2";

    /** promotionTools property. */
    private PromotionTools mPromotionTools;

    /** promotion property. */
    private RepositoryItem mPromotion;

    /** email property. */
    private String mEmail;

    /**
     * @see atg.process.action.ActionImpl#initialize(java.util.Map)
     */
    @Override public void initialize(Map pParameters) throws ProcessException {
        storeRequiredParameter(pParameters, PARAM_TEST1, java.lang.String.class);
        storeRequiredParameter(pParameters, PARAM_TEST2, java.lang.String.class);
    }

    /**
     * @see atg.process.action.ActionImpl#configure(java.lang.Object)
     */
    @Override public void configure(Object pConfiguration) throws ProcessException {
        PromotionActionConfiguration config = (PromotionActionConfiguration) pConfiguration;

        mPromotionTools = config.getPromotionTools();

        if (mPromotionTools == null) {
            throw new ProcessException(PromotionConstants.getStringResource(PromotionConstants.PROMOTION_TOOLS_NOT_FOUND));
        }
    }

    /**
     * @see atg.process.action.ActionImpl#executeAction(atg.process.ProcessExecutionContext)
     */
    @Override protected void executeAction(ProcessExecutionContext pContext) throws ProcessException {
        String promoId = (String) getParameterValue(PARAM_TEST1, pContext);
        System.out.println("TestAction");
        System.out.println("scenario instance = " + pContext.getProcessInstance());
        System.out.println("test1 = " + getParameterValue(PARAM_TEST1, pContext));
        System.out.println("test2 = " + getParameterValue(PARAM_TEST2, pContext));
        System.out.println("message = " + pContext.getMessage());

    }

}
