package com.castorama.integration.experian;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;


import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ExperianRequestDroplet extends DynamoServlet {

    private static final String ACTION_PARAM = "action";
    private static final String EMAIL_PARAM = "email";
    private static final String SOURCE_WEB = "web";
    private ExperianRequestHelper mExperianRequestHelper;
    private Repository mRepository;

    @Override
    public void service(DynamoHttpServletRequest pRequest,
            DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
        String email = pRequest.getParameter(EMAIL_PARAM);
        String action = pRequest.getParameter(ACTION_PARAM);
        if (email == null || email.length() == 0) {
            if (isLoggingWarning() ) {
                logWarning("parameter \"" + EMAIL_PARAM + "\" must be not empty");
            }
            return;
        }
        if (action == null || action.length() == 0) {
            if (isLoggingError()) {
                logError("parameter \"" + ACTION_PARAM + "\" must be not empty");
            }
            return;
        }

        if (action.equalsIgnoreCase("subscribe")) {
            try {
                if (updateSubscription(email)) {
                    mExperianRequestHelper.addChangeSubscriptionRequest(email, SOURCE_WEB);
                }
            } catch (RepositoryException e) {
                if (isLoggingError()){
                    logError(e);
                }
            } catch (NullPointerException e) {
                if (isLoggingError()){
                    logError(e);
                }
            }
        }
    }
    
    private boolean updateSubscription(String email) throws RepositoryException{
        MutableRepository mr = (MutableRepository) getRepository();
        MutableRepositoryItem mri = mr.getItemForUpdate(email, "abonnementNewsletter");
        if (mri == null){
            return false;
        }
        Object receiveEmailObj = mri.getPropertyValue("receiveEmail");
        String receiveEmail = null;;
        if (receiveEmailObj instanceof String ){
            receiveEmail = (String) receiveEmailObj;
            if (receiveEmail.equalsIgnoreCase("")){
                mri.setPropertyValue("receiveEmail", "true");
                Date currDate = new Date();
                mri.setPropertyValue("dateInscription", currDate);
                mri.setPropertyValue("dateCreation", currDate);
                mr.updateItem(mri);
                return true;
            }
        }
        return false;
    }

    public ExperianRequestHelper getExperianRequestHelper() {
        return mExperianRequestHelper;
    }

    public void setExperianRequestHelper(
            ExperianRequestHelper pExperianRequestHelper) {
        this.mExperianRequestHelper = pExperianRequestHelper;
    }

    public Repository getRepository() {
        return mRepository;
    }

    public void setRepository(Repository pRepository) {
        this.mRepository = pRepository;
    }

}
