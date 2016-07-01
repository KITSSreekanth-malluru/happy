package com.castorama.mail;

import static com.castorama.commerce.profile.Constants.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import com.castorama.utils.PasswordLinkGenerator;

import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ForgotPasswordHandler;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.email.TemplateEmailException;

/**
 *
 * @author  EPAM team
 */
public class CastForgotPasswordHandler extends ForgotPasswordHandler {
    
    /** Password link item descriptor name.*/
    private static final String LINK_DESCRIPTOR = "passwordLink";

    /** User email property.*/
    private static final String PROPERTY_EMAIL = "email";

    /** Key property name. */
    private static final String PROPERTY_KEY = "key";

    /** Expiration date property name. */
    private static final String PROPERTY_DATE = "date";
    
    /** Repository property.*/
    private Repository linkRepository;

    /** Key for link property. */
    private String keyForLink;
    
    /**
     *
     * @param  request     parameter
     * @param  response    parameter
     * @param  profile     parameter
     * @param  newPassword parameter
     *
     * @return 
     */
    //@Override 
    protected Map generateNewPasswordTemplateParams(DynamoHttpServletRequest request,
                                                              DynamoHttpServletResponse response,
                                                              RepositoryItem profile) {//, String newPassword) {
        //Map params = super.generateNewPasswordTemplateParams(request, response, profile, newPassword);
        Map params = new HashMap();
        params.put("lastName", profile.getPropertyValue(LAST_NAME_PROFILE_PROP));
        params.put("civilite", profile.getPropertyValue(TITLE_PROFILE_PROP));
        params.put("email", profile.getPropertyValue(LOGIN_PROFILE_PROP));
        params.put("link", keyForLink);
        return params;
    }
    
    public boolean handleForgotPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
            throws ServletException, IOException {
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            int status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse);
            if (status != STATUS_SUCCESS)
                return status == STATUS_ERROR_STAY;
            preForgotPassword(pRequest, pResponse);
            status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse);
            if (status != STATUS_SUCCESS) 
                return status == STATUS_ERROR_STAY;
            ProfileTools ptools = getProfileTools();
            PropertyManager pmgr = ptools.getPropertyManager();
            String loginPropertyName = pmgr.getLoginPropertyName();
            String login = getStringValueProperty(loginPropertyName);
            String emailPropertyName = pmgr.getEmailAddressPropertyName();
            String email = getStringValueProperty(emailPropertyName);
            MutableRepositoryItem[] users = super.lookupUsers(login, email);
            if (users == null) {
                String msg = formatUserMessage(MSG_NO_SUCH_PROFILE, pRequest);
                addFormException(new DropletException(msg, MSG_NO_SUCH_PROFILE));
            } else {
                try {
                    for (int i = 0; i < users.length; i++) {
                        addKey(login, (users[i].getPropertyValue(PROPERTY_EMAIL)).toString());
                        ptools.sendEmailToUser(users[i], isSendEmailInSeparateThread(), isPersistEmails(), getTemplateEmailSender(), 
                                getTemplateEmailInfo(), generateNewPasswordTemplateParams(pRequest, pResponse, users[i]));
                    }
                } catch (TemplateEmailException exc) {
                    String msg = formatUserMessage(MSG_ERR_SENDING_EMAIL, pRequest);
                    addFormException(new DropletException(msg, exc, MSG_ERR_SENDING_EMAIL));
                    if (isLoggingError()) {
                        logError(exc);
                    }
                }
            }
            postForgotPassword(pRequest, pResponse);
            if ((status = checkFormError(getForgotPasswordErrorURL(), pRequest, pResponse)) != STATUS_SUCCESS)
                return status == STATUS_ERROR_STAY;
            if (!checkFormSuccess(getForgotPasswordSuccessURL(), pRequest, pResponse))
                return false;
        } catch (TransactionDemarcationException e) {
            if(isLoggingError()){
                logError(e);
            }
        } finally {
            try {
                if (tm != null)
                    td.end();
            } catch (TransactionDemarcationException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return true;
    }

    private void addKey(String key, String userId) {
        
        MutableRepository mutableRepository = (MutableRepository)getLinkRepository();
        try {
            RepositoryItem item = getLinkRepository().getItem(userId, LINK_DESCRIPTOR);
            keyForLink=getKeyForLink(key);
            if(item == null) {
                MutableRepositoryItem mutableRepositoryItem = mutableRepository.createItem(LINK_DESCRIPTOR);
                mutableRepositoryItem.setPropertyValue(PROPERTY_EMAIL, userId);
                mutableRepositoryItem.setPropertyValue(PROPERTY_DATE, getCurrentDate());
                mutableRepositoryItem.setPropertyValue(PROPERTY_KEY, keyForLink);
                mutableRepository.addItem(mutableRepositoryItem);
            } else {

                MutableRepositoryItem mutableRepositoryItem = mutableRepository.getItemForUpdate(item.getRepositoryId(),LINK_DESCRIPTOR);
                mutableRepositoryItem.setPropertyValue(PROPERTY_DATE, getCurrentDate());
                mutableRepositoryItem.setPropertyValue(PROPERTY_KEY, keyForLink);
                mutableRepository.updateItem(mutableRepositoryItem);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("RepositoryException during add link for forgot password functionality.");
            }
        }
    }

    private Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(today.getTime());
        return sqlDate;
    }

    public Repository getLinkRepository() {
        return linkRepository;
    }

    public void setLinkRepository(Repository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public String getKeyForLink() {
        return keyForLink;
    }
    
    public String getKeyForLink(String message) {
        String key = null;
        try {
            key = PasswordLinkGenerator.generateKeyParameter(message);
        } catch (NoSuchAlgorithmException e) {
            String msg = "NoSuchAlgoritmException during encode key for forgot password functionality";
            if (isLoggingError()) {
                logError(msg);
            }
        }
        return key;
    }

    public void setKeyForLink(String keyForLink) {
        this.keyForLink = keyForLink;
    }
}
