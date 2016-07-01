package com.castorama.scenario.configuration;

import javax.transaction.TransactionManager;

import com.castorama.invite.ReferrerProgramConfig;
import com.castorama.scenario.ActionLogger;

import atg.commerce.claimable.ClaimableManager;

import atg.commerce.promotion.PromotionTools;

import atg.repository.Repository;

import atg.service.lockmanager.ClientLockManager;
import atg.service.webappregistry.WebAppRegistry;

import atg.userprofiling.ProfileTools;

import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

/**
 *
 * @author Andrei_Raichonak
 */
public class BaseSendEmailConfiguration {
    /** individualEmailSender property. */
    private TemplateEmailSender mIndividualEmailSender;

    /** webAppRegistry property. */
    private WebAppRegistry mWebAppRegistry;

    /** emailInfo property. */
    private TemplateEmailInfo mEmailInfo;

    /** promotionTools property. */
    private PromotionTools mPromotionTools;

    /** claimableManager property. */
    private ClaimableManager mClaimableManager;

    /** transactionManager property. */
    private TransactionManager mTransactionManager;

    /** profileRepository property */
    private Repository mProfileRepository;

    /** profileTools property. */
    private ProfileTools mProfileTools;

    /** fromEmail property. */
    private String mFromEmail;

    /** emailSubject property. */
    private String mEmailSubject;

    /** referrerProgramConfig property. */
    private ReferrerProgramConfig mReferrerProgramConfig;
    
    private ClientLockManager mClientLockManager;
    
    private ActionLogger mActionLogger;
    /**
     * Returns individualEmailSender property.
     *
     * @return individualEmailSender property.
     */
    public TemplateEmailSender getIndividualEmailSender() {
        return mIndividualEmailSender;
    }

    /**
     * Sets the value of the individualEmailSender property.
     *
     * @param pIndividualEmailSender parameter to set.
     */
    public void setIndividualEmailSender(TemplateEmailSender pIndividualEmailSender) {
        mIndividualEmailSender = pIndividualEmailSender;
    }

    /**
     * Returns webAppRegistry property.
     *
     * @return webAppRegistry property.
     */
    public WebAppRegistry getWebAppRegistry() {
        return mWebAppRegistry;
    }

    /**
     * Sets the value of the webAppRegistry property.
     *
     * @param pWebAppRegistry parameter to set.
     */
    public void setWebAppRegistry(WebAppRegistry pWebAppRegistry) {
        mWebAppRegistry = pWebAppRegistry;
    }

    /**
     * Returns emailInfo property.
     *
     * @return emailInfo property.
     */
    public TemplateEmailInfo getEmailInfo() {
        return mEmailInfo;
    }

    /**
     * Sets the value of the emailInfo property.
     *
     * @param pEmailInfo parameter to set.
     */
    public void setEmailInfo(TemplateEmailInfo pEmailInfo) {
        mEmailInfo = pEmailInfo;
    }

    /**
     * Returns promotionTools property.
     *
     * @return promotionTools property.
     */
    public PromotionTools getPromotionTools() {
        return mPromotionTools;
    }

    /**
     * Sets the value of the promotionTools property.
     *
     * @param pPromotionTools parameter to set.
     */
    public void setPromotionTools(PromotionTools pPromotionTools) {
        mPromotionTools = pPromotionTools;
    }

    /**
     * Returns claimableManager property.
     *
     * @return claimableManager property.
     */
    public ClaimableManager getClaimableManager() {
        return mClaimableManager;
    }

    /**
     * Sets the value of the claimableManager property.
     *
     * @param pClaimableManager parameter to set.
     */
    public void setClaimableManager(ClaimableManager pClaimableManager) {
        mClaimableManager = pClaimableManager;
    }

    /**
     * Returns transactionManager property.
     *
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     *
     * @param pTransactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns profileRepository property.
     *
     * @return profileRepository property.
     */
    public Repository getProfileRepository() {
        return mProfileRepository;
    }

    /**
     * Sets the value of the profileRepository property.
     *
     * @param pProfileRepository parameter to set.
     */
    public void setProfileRepository(Repository pProfileRepository) {
        mProfileRepository = pProfileRepository;
    }

    /**
     * Returns profileTools property.
     *
     * @return profileTools property.
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Sets the value of the profileTools property.
     *
     * @param pProfileTools parameter to set.
     */
    public void setProfileTools(ProfileTools pProfileTools) {
        mProfileTools = pProfileTools;
    }

    /**
     * Returns fromEmail property.
     *
     * @return fromEmail property.
     */
    public String getFromEmail() {
        return mFromEmail;
    }

    /**
     * Sets the value of the fromEmail property.
     *
     * @param pFromEmail parameter to set.
     */
    public void setFromEmail(String pFromEmail) {
        mFromEmail = pFromEmail;
    }

    /**
     * Returns emailSubject property.
     *
     * @return emailSubject property.
     */
    public String getEmailSubject() {
        return mEmailSubject;
    }

    /**
     * Sets the value of the emailSubject property.
     *
     * @param pEmailSubject parameter to set.
     */
    public void setEmailSubject(String pEmailSubject) {
        mEmailSubject = pEmailSubject;
    }

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

	public ClientLockManager getClientLockManager() {
		return mClientLockManager;
	}

	public void setClientLockManager(ClientLockManager pClientLockManager) {
		mClientLockManager = pClientLockManager;
	}

	public ActionLogger getActionLogger() {
		return mActionLogger;
	}

	public void setActionLogger(ActionLogger pActionLogger) {
		mActionLogger = pActionLogger;
	}
}
