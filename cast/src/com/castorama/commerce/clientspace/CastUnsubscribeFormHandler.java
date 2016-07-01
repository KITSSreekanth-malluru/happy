package com.castorama.commerce.clientspace;

import static com.castorama.commerce.profile.Constants.DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.DATE_UNSUBSCRIBE_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.EMAIL_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RECEIVEEMAIL_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RESEIVEOFFERS_NEWSLETTER_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_ENTER_RESPONSE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_NON_EXISTENT_EMAIL;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.model.Abonnement;

/**
 * Form Handler for Unsubscribe Action.
 *
 * @author Katsiaryna Dmitrievich
 */
public class CastUnsubscribeFormHandler extends CastNewsletterFormHandler {
    /**
     * Value of item from radiogroup: unsubscribe from castorama partners
     * newsletters.
     */
    private static final String UNSUBSCRIBE_CAST_NEWSL = "offers";

    /**
     * Value of item from radiogroup: unsubscribe from castorama newsletters.
     */
    private static final String UNSUBSCRIBE_ALL_NEWSLETTERS = "newsletters";

    /**
     * Unsubscribe action, 3 values:
     * <li>UNSUBSCRIBE_OFFERS
     * <li>UNSUBSCRIBE_NEWSLETTERS
     */
    private String unsubscribeAction;

    /**
     * Unsubscribe user if user has inputed valid email.
     *
     * @param  pRequest
     * @param  pResponse
     *
     * @return
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleUnsubscribe(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                              throws ServletException, IOException {
        String email = getEmail();
        if (!validateEmail(email) | !validateRadioAnswers()) {
            return checkFormRedirect(getUpdateErrorURL(), getUpdateErrorURL(), pRequest, pResponse);
        }
        if (StringUtils.isBlank(getRepositoryId()) && !StringUtils.isBlank(email)) {
            setRepositoryId(email);
        }

        setUnsubscribe(true);
        
        setUpdateSuccessURL(getUpdateSuccessURL() + "?email=" + email);
        setValueProperty(EMAIL_NEWSLETTER_PROP, email);

        return super.handleUpdate(pRequest, pResponse);
    }

    /**
     * Checks if answer was entered.
     *
     * @return <code>true</code> if answer was entered
     */
    private boolean validateRadioAnswers() {
        boolean result = true;
        if (StringUtils.isBlank(getUnsubscribeAction())) {
        	getCommonHelper().generateFormException(MSG_ENTER_RESPONSE, this, RESOURCE_BUNDLE);
            result = false;
        }
        return result;
    }

    /**
     * Override {@link CastNewsletterFormHandler#validateEmail(String)}: checks
     * if email was subscribed.
     *
     * @param  email - email to validate
     *
     * @return <code>true</code> if email is correct and exist
     */
    @Override public boolean validateEmail(String email) {
        boolean result = super.validateEmail(email);
        try {
            if ((getCommonHelper().repositoryItemByPropertyValue(EMAIL_NEWSLETTER_PROP, email, getRepository(),
                                                                getItemDescriptorName()) == null) && result) {
            	getCommonHelper().generateFormException(MSG_NON_EXISTENT_EMAIL, this, RESOURCE_BUNDLE);
                result = false;
            }
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("Impossible to check email existance");
            }
            result = false;
        }
        return result;
    }

    /**
     * Gets unsubscribeAction, 3 values:
     * <li>UNSUBSCRIBE_OFFERS
     * <li>UNSUBSCRIBE_NEWSLETTERS
     * <li>UNSUBSCRIBE_CHANGE_INFO
     *
     * @return the unsubscribeAction
     */
    public String getUnsubscribeAction() {
        return unsubscribeAction;
    }

    /**
     * Sets unsubscribeAction, 3 values:
     * <li>UNSUBSCRIBE_OFFERS
     * <li>UNSUBSCRIBE_NEWSLETTERS
     * <li>UNSUBSCRIBE_CHANGE_INFO
     *
     * @param unsubscribeAction the unsubscribeAction to set
     */
    public void setUnsubscribeAction(String unsubscribeAction) {
        this.unsubscribeAction = unsubscribeAction;
    }

    /**
     * Before Update: check UnsubscribeAction and sets correct properties.
     *
     * @param  request  servlet's request
     * @param  response servlet's response
     *
     * @throws ServletException if servlet exception
     * @throws IOException      if servlet io exception
     */
    @Override protected void preUpdateItem(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                                    throws ServletException, IOException {
        if (UNSUBSCRIBE_ALL_NEWSLETTERS.equals(getUnsubscribeAction())) {
            setValueProperty(RECEIVEEMAIL_NEWSLETTER_PROP, false);
            setValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP, false);
            setValueProperty(DATE_UNSUBSCRIBE_NEWSLETTER_PROP, new Date());
            setValueProperty(DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP, new Date());
        } else if (UNSUBSCRIBE_CAST_NEWSL.equals(getUnsubscribeAction())) {
            setValueProperty(RESEIVEOFFERS_NEWSLETTER_PROP, false);
            setValueProperty(DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP, new Date());
        }

        Abonnement abonnement = Abonnement.getInstance(getRepositoryItem());
        setOldSubscribled(abonnement.getSubscribled());
        setOldSubscribledOffers(abonnement.getSubscribledPartners());
    }
}
