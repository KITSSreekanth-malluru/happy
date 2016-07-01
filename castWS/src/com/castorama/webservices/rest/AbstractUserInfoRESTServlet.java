package com.castorama.webservices.rest;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.security.ProfileIdentityManager;
import atg.security.SecurityException;
import atg.servlet.security.UserLoginManager;
import atg.userprofiling.ProfileTools;

abstract class AbstractUserInfoRESTServlet extends AbstractJsonRESTServlet {
    private static final String LOGIN_MANAGER_NAME = "/atg/dynamo/security/UserLoginManager";
    
    protected static final String NEWSLETTER_REPOSITORY_NAME = "/atg/registry/Repository/NewsletterGSARepository";
    
    private static final RqlStatement profileNewslettersQuery;
    
    static {
        try {
            profileNewslettersQuery = RqlStatement.parseRqlStatement("profile = ?0");
        } catch (RepositoryException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /* Returns the user profile if the user is authenticated by the given login/password pair.
     * The implementation repeats the authentication process forced by the family of descendants
     * of atg.userprofiling.ProfileForm */
    protected RepositoryItem getProfile(final String login, final String password, final HttpServletRequest request,
            final HttpServletResponse response) {
        try {
            final UserLoginManager loginManager = (UserLoginManager) resolveNucleusComponent(LOGIN_MANAGER_NAME);
            
            if (loginManager == null) {
                throw new IllegalStateException("Cannot resolve " + LOGIN_MANAGER_NAME);
            }

            final ProfileIdentityManager profileIM =
                    (ProfileIdentityManager) loginManager.getIdentityManager(dynamoRequest(request, response));
            
            if (profileIM.checkAuthenticationByPassword(login, password)) {
                final ProfileTools profileTools = profileIM.getProfileTools();
                return profileTools.getItem(login, password);
            }
            return null;
        } catch (SecurityException ex) {
            // the user is not authenticated
            return null;
        }
    }
    
    /**
     * <p>Returns a <i>{@value NEWSLETTER_REPOSITORY_NAME}->abonnementNewsletter</i> repository item
     * which is associated with a given profile. If there are many such <i>abonnementNewsletter</i>
     * repository items then the one with the latest modification date is returned.
     * If no-one <i>abonnementNewsletter</i> is associated with the given profile then
     * <tt>null</tt> is returned.</p>
     * 
     * @param profile the profile whose newsletter subscription is to be returned.
     * 
     * @return a <i>{@value NEWSLETTER_REPOSITORY_NAME}->abonnementNewsletter</i> repository item
     * which is associated with a given profile and has the latest modification date.
     * 
     * @throws RepositoryException if an error is encountered while searching for repository items.
     */
    protected RepositoryItem getNewsletterSubscription(final RepositoryItem profile) throws RepositoryException {
        final Repository newsletterRep = (Repository) resolveNucleusComponent(NEWSLETTER_REPOSITORY_NAME);
        final RepositoryView newsletterView = newsletterRep.getView("abonnementNewsletter");
        final RepositoryItem[] subscriptions = profileNewslettersQuery.executeQuery(
                newsletterView, new Object[]{profile.getRepositoryId()});
        if (subscriptions == null || subscriptions.length == 0) {
            return null;
        }
        RepositoryItem latestSubscription = subscriptions[0];
        Date latestSubscriptionModified = (Date) latestSubscription.getPropertyValue("dateDerniereModification");
        for (int i = 1; i < subscriptions.length; ++i) {
            final RepositoryItem subscr = subscriptions[i];
            final Date subscrModified = (Date) subscr.getPropertyValue("dateDerniereModification");
            if (subscrModified.after(latestSubscriptionModified)) {
                latestSubscription = subscr;
                latestSubscriptionModified = subscrModified;
            }
        }
        return latestSubscription;
    }
    
    protected static JSONObject createInitResult(int statusCode) {
        try {
            return new JSONObject().put("codeRetour", statusCode);
        } catch (JSONException ex) {
            // cannot happen
            throw new IllegalStateException(ex);
        }
    }
}
