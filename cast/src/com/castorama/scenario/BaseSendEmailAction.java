package com.castorama.scenario;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.TransactionManager;

import atg.commerce.claimable.ClaimableManager;

import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionTools;

import atg.process.ContextParameterProcessor;
import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;
import atg.process.ProcessExecutionContextImpl;

import atg.process.action.ActionImpl;

import atg.repository.Repository;

import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.lockmanager.TimeExceededException;

import atg.service.webappregistry.WebApp;
import atg.service.webappregistry.WebAppRegistry;

import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.invite.ReferrerProgramConfig;

import com.castorama.scenario.configuration.BaseSendEmailConfiguration;

/**
 * Base class for referrer program actions. Encapsulates emailing.
 *
 * @author Andrei_Raichonak
 */
public abstract class BaseSendEmailAction extends ActionImpl implements ScenarioConstants {
    /** emailInfo property. */
    private TemplateEmailInfo mEmailInfo;

    /** emailSender property. */
    private TemplateEmailSender mEmailSender;

    /** webAppRegistry property. */
    private WebAppRegistry mWebAppRegistry;

    /** mLogger property */
    protected ActionLogger mLogger;

    /** mClientLockManager property */
    protected ClientLockManager mClientLockManager;

    /** transactionManager property. */
    protected TransactionManager mTransactionManager;

    /** claimableManager property. */
    protected ClaimableManager mClaimableManager;

    /** referrerProgramConfig property. */
    protected ReferrerProgramConfig mReferrerProgramConfig;

    /** profileRepository property. */
    protected Repository mProfileRepository;

    /** promotionTools property. */
    protected PromotionTools mPromotionTools;

    /**
     * @see atg.process.action.ActionImpl#configure(java.lang.Object)
     */
    public void configure(Object pConfiguration) throws ProcessException {
        BaseSendEmailConfiguration config = (BaseSendEmailConfiguration) pConfiguration;
        mEmailInfo = config.getEmailInfo();

        if (mEmailInfo == null) {
            throw new ProcessException("EmailInfo is not specified");
        }

        mEmailSender = config.getIndividualEmailSender();

        if (mEmailSender == null) {
            throw new ProcessException("EmailSender is not specified");
        }

        mWebAppRegistry = config.getWebAppRegistry();

        if (mWebAppRegistry == null) {
            throw new ProcessException("WebAppRegistry is not specified");
        }

        mTransactionManager = config.getTransactionManager();

        if (mTransactionManager == null) {
            throw new ProcessException("Transaction Manager not found");
        }

        mReferrerProgramConfig = config.getReferrerProgramConfig();

        if (mReferrerProgramConfig == null) {
            throw new ProcessException("ReferrerProgramConfig is not set");
        }

        mProfileRepository = config.getProfileRepository();

        if (mProfileRepository == null) {
            throw new ProcessException("Profile Repository not found");
        }

        mClaimableManager = config.getClaimableManager();

        if (mClaimableManager == null) {
            throw new ProcessException("Claimable Manager not found");
        }

        mPromotionTools = config.getPromotionTools();

        if (mPromotionTools == null) {
            throw new ProcessException(PromotionConstants.getStringResource(PromotionConstants.PROMOTION_TOOLS_NOT_FOUND));
        }

        mClientLockManager = config.getClientLockManager();

        mLogger = config.getActionLogger();
    }  // end method configure

    /**
     * @see atg.process.action.ActionImpl#initialize(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public void initialize(Map pParameters) throws ProcessException {
        storeRequiredParameter(pParameters, PARAM_TEMPLATE, String.class);
        storeOptionalParameter(pParameters, PARAM_MAILING_NAME, String.class);
        storeOptionalParameter(pParameters, PARAM_MESSAGE_SUBJECT, String.class);
        storeOptionalParameter(pParameters, PARAM_MESSAGE_FROM, String.class);
        storeOptionalParameter(pParameters, PARAM_MESSAGE_CC, String.class);

    }

    /**
     * Executes action's logic
     *
     * @param  pContext execution context
     *
     * @throws ProcessException if the action can not be executed
     */
    protected void executeAction(ProcessExecutionContext pContext) throws ProcessException {
        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("Entering executeAction");
        }
        boolean gotLock = false;
        Serializable lockName = getLockName(pContext);
        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("LockName is " + lockName);
        }
        try {
            gotLock = acquireLock(lockName);

            if (gotLock) {
                doExecuteAction(pContext);
            } else {
                if (mLogger.isLoggingDebug()) {
                    mLogger.logDebug("Already locked. Sleeping.");
                }
            }
        } finally {
            if (gotLock) {
                releaseLock(lockName);
            }
        }
        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("Exiting executeAction");
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pContext ToDo: DOCUMENT ME!
     *
     * @throws ProcessException ToDo: DOCUMENT ME!
     */
    protected abstract void doExecuteAction(ProcessExecutionContext pContext) throws ProcessException;

    /**
     * Returns lockName property.
     *
     * @param  pContext parameter to set.
     *
     * @return lockName property.
     */
    protected abstract Serializable getLockName(ProcessExecutionContext pContext);

    /**
     * Send email using passed parameters
     *
     * @param  pContext   parameter
     * @param  pSubject   parameter
     * @param  pFromEmail parameter
     * @param  pToEmail   parameter
     * @param  pTemplate  parameter
     * @param  pParams    parameter
     *
     * @throws atg.process.action.ActionException exception
     */
    @SuppressWarnings("unchecked")
    protected void sendEmail(ProcessExecutionContext pContext, String pSubject, String pFromEmail, String pToEmail,
                             String pTemplate, Map<String, String> pParams) throws atg.process.action.ActionException {
        // String pReferrer, String pCouponNumber,
        try {
            TemplateEmailInfo emailInfo = createTemplateEmailInfo(pContext, pSubject, pFromEmail, pTemplate, pParams);
            List recipients = new ArrayList();
            recipients.add(pToEmail);

            mEmailSender.sendEmailMessage(emailInfo, recipients, true, true);
        } catch (TemplateEmailException e) {
            throw new atg.process.action.ActionException(new atg.process.action.FailedActionInfo(pContext, e));
        } catch (ProcessException e) {
            throw new atg.process.action.ActionException(new atg.process.action.FailedActionInfo(pContext, e));
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pContext      parameter
     * @param  pEmailSubject parameter
     * @param  pFromEmail    parameter
     * @param  pTemplate     parameter
     * @param  pParams       parameter
     *
     * @return
     *
     * @throws ProcessException exception
     */
    @SuppressWarnings("unchecked")
    protected TemplateEmailInfo createTemplateEmailInfo(ProcessExecutionContext pContext, String pEmailSubject,
                                                        String pFromEmail, String pTemplate,
                                                        Map<String, String> pParams) throws ProcessException {
        TemplateEmailInfo emailInfo = mEmailInfo.copy();

        if (pContext instanceof ProcessExecutionContextImpl) {
            String strBatchExecutionId = ((ProcessExecutionContextImpl) pContext).getBatchExecutionId();

            if (strBatchExecutionId != null) {
                emailInfo.setBatchExecutionId(strBatchExecutionId);
            }
        }

        String templateName = resolveTemplateURL(pTemplate);

        emailInfo.setTemplateURL(templateName);
        Map params = new HashMap();
        params.put(MESSAGE_PARAM_NAME, pContext.getMessage());
        params.putAll(pParams);

        ContextParameterProcessor paramProcessor =
            new ContextParameterProcessor(pContext.getProcessInstance(), pContext.isIndividual(),
                                          pContext.getMessageType());

        emailInfo.addParameterProcessor(paramProcessor);

        emailInfo.setTemplateParameters(params);

        if (emailInfo instanceof TemplateEmailInfoImpl) {
            TemplateEmailInfoImpl emailInfoImpl = (TemplateEmailInfoImpl) emailInfo;
            String mailingNameParam = (String) getParameterValue(PARAM_MAILING_NAME, pContext);

            if (mailingNameParam != null) {
                emailInfoImpl.setMailingName(mailingNameParam);
            }

            if (pEmailSubject != null) {
                emailInfoImpl.setMessageSubject(pEmailSubject);
            }

            if (pFromEmail != null) {
                emailInfoImpl.setMessageFrom(pFromEmail);
            }
        }

        return emailInfo;
    }  // end method createTemplateEmailInfo

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pTemplateName parameter
     *
     * @return
     *
     * @throws ProcessException exception
     */
    private String resolveTemplateURL(String pTemplateName) throws ProcessException {
        if (pTemplateName == null) {
            return null;
        }
        String templateName = pTemplateName;
        if (pTemplateName != null) {
            if (pTemplateName.indexOf(COLON) == -1) {
                templateName = pTemplateName;
            } else {
                String appName = pTemplateName.substring(0, pTemplateName.indexOf(COLON));
                String path = pTemplateName.substring(pTemplateName.indexOf(COLON) + 1);
                WebApp app = mWebAppRegistry.getWebAppByName(appName);
                if (app != null) {
                    String contextRoot = app.getProperty(CONTEXT_ROOT_WEB_APP_PROPERTY);
                    if (!contextRoot.startsWith(SLASH)) {
                        contextRoot = SLASH + contextRoot;
                    }
                    templateName = contextRoot + path;
                } else {
                    throw new ProcessException("Can not find WebApp");
                }
            }  // end if-else
        }  // end if

        return templateName;
    }  // end method resolveTemplateURL

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pLockName ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    protected boolean acquireLock(Serializable pLockName) {
        // unused boolean gotLock = false;
        boolean globalLock = false;
        boolean release = false;

        try {
            globalLock = mClientLockManager.acquireWriteLock(pLockName, Thread.currentThread(), 1000);

            // unused gotLock = true;
            if (globalLock) {
                if (mLogger.isLoggingDebug()) {
                    mLogger.logDebug("Obtained global lock");
                }
                return true;
            } else if (mClientLockManager.getUseLockServer() == false) {
                if (mLogger.isLoggingDebug()) {
                    mLogger.logDebug("ClientLockManager not using global lock server -- accepting local lock instead.");
                }
                return true;
            } else {
                if (mLogger.isLoggingDebug()) {
                    mLogger.logDebug("Could not obtain global lock.");
                }
                release = true;
            }
        } catch (DeadlockException de) {
            if (mLogger.isLoggingError()) {
                mLogger.logError(de);
            }
        } catch (TimeExceededException te) {
            if (mLogger.isLoggingError()) {
                mLogger.logError(te);
            }
        } finally {
            try {
                if (release) {
                    mClientLockManager.releaseWriteLock(pLockName);
                }
            } catch (LockManagerException lme) {
                if (mLogger.isLoggingError()) {
                    mLogger.logError(lme);
                }
            }
        }  // end try-catch-finally
        return false;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pLockName ToDo: DOCUMENT ME!
     */
    protected void releaseLock(Serializable pLockName) {
        try {
            ClientLockManager clm = mClientLockManager;
            clm.releaseWriteLock(pLockName);
            if (mLogger.isLoggingDebug()) {
                mLogger.logDebug("Lock Released");
            }
        } catch (LockManagerException lme) {
            if (mLogger.isLoggingError()) {
                mLogger.logError(lme);
            }
        }
    }

}
