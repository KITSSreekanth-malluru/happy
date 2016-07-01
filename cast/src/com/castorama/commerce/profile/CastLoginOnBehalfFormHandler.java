package com.castorama.commerce.profile;

import static com.castorama.commerce.profile.Constants.ADMIN_LOGIN_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_PASSWORD_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_ROLE;
import static com.castorama.commerce.profile.Constants.LOGIN_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.Constants.CURRENT_LOCAL_STORE_PROFILE_PROP;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INCORRECT_ONBEHALF_EMAIL;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileForm;
import atg.userprofiling.ProfileRequestServlet;

import com.castorama.utils.ContextTools;

/**
 * Profile Form Handler class that includes on behalf login handlers.
 *
 * @author  Katsiaryna Dmitrievich
 */
public class CastLoginOnBehalfFormHandler extends CastProfileFormHandler {
    /** mStoredSendProfileCookies property */
    //private boolean mStoredSendProfileCookies;
    
    /** ProfileRequestServlet component path. */
    private String mProfileRequestServlet;
    
    /** ContextTools component. */
    private ContextTools contextTools;

    /**
     * Override {@link ProfileForm#findUser}: add functionality: If user logins
     * "on behalf" it's unnecessary to check password
     *
     * @param     pLogin             user login
     * @param     pPassword          user password
     * @param     pProfileRepository user repository
     * @param     pRequest           the servlet's request
     * @param     pResponse          the servlet's response
     *
     * @return    <code>null</code> if user wasn't found in repository, profile
     *            RepositoryItem otherwise
     *
     * @exception RepositoryException if there was an error while accessing the
     *                                Profile Repository
     * @exception ServletException    if there was an error while executing the
     *                                code
     * @exception IOException         if there was an error with servlet io
     */
    @Override protected RepositoryItem findUser(String pLogin, String pPassword, Repository pProfileRepository,
                                                DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                         throws RepositoryException, ServletException, IOException {
        // If user logins "on behalf" it's unnecessary to check password
        // Authentication succeeded, return the associated profile item.
        return getProfileTools().getItem(pLogin, null, getLoginProfileType());
    }

    /**
     * Action for login "on behalf". Reads <code>adminLogin</code> and <code>
     * adminPassword</code> from session, if they are correct and entered
     * customer's login name is correct: login as customer.<br />
     * If <code>adminLogin</code> and <code>adminPassword</code> from session
     * are not correct redirect on <code>mLoginErrorURL</code> page. If entered
     * customer's login is not valid add <code>
     * MSG_INCORRECT_ONBEHALF_EMAIL</code> error. If all data is correct
     * redirect to <code>mLoginSuccessURL</code>
     *
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    @Override protected void preLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        final SessionBean sb = (SessionBean) pRequest.resolveName(getSessionBean());
        final String adminLogin = (String) sb.getValues().get(ADMIN_LOGIN_SESSION_PARAM);
        final String adminPassword = (String) sb.getValues().get(ADMIN_PASSWORD_SESSION_PARAM);

        try {
            if ((adminLogin == null) || ((adminLogin != null) && (adminLogin.trim().length() == 0)) ||
                    !((CastProfileTools) getProfileTools()).checkRole(adminLogin, ADMIN_ROLE) ||
                    (findUser(adminLogin, adminPassword, getProfile().getRepository(), pRequest, pResponse) == null)) {
            	if (isLoggingWarning()) {
            		logWarning("Session for admin user was expired or non-admin user tries to login \"on behalf\"");
            	}
                addFormException(new DropletException("Session for admin user was expired or non-admin user tries to login \"on behalf\""));
            } else {
                final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);

                if (!validateLogin(login, null, true, pRequest, pResponse) ||
                        ((CastProfileTools) getProfileTools()).checkRole(login, ADMIN_ROLE)) {
                    getCommonHelper().generateFormException(MSG_INCORRECT_ONBEHALF_EMAIL, this, RESOURCE_BUNDLE);
                    if(isLoggingDebug()) {
                    	logDebug("Admin user inputted incorrect email to login \"on behalf\" - " + login);
                    }
                    setLoginErrorURL(getOnBehalfURL());
                    return;
                }

                // do not send cookie for on behalf users.
                ProfileRequestServlet prs = (ProfileRequestServlet) pRequest.resolveName(getProfileRequestServlet(), false);
                prs.disableSendCookie(pRequest);
                prs.disableAutoLogin(pRequest);
                
                // God Mode support for Click & Collect
                RqlStatement findClientRQL = RqlStatement.parseRqlStatement("email = ?0");
                RepositoryView userView = getProfile().getRepository().getView("user");
                Object[] rqlparams = new Object[] {login};
                RepositoryItem[] profiles = findClientRQL.executeQuery(userView, rqlparams);
                if (profiles!= null && profiles.length > 0){
                    RepositoryItem currentLocalStore = (RepositoryItem)profiles[0].getPropertyValue(CURRENT_LOCAL_STORE_PROFILE_PROP);
                    if (currentLocalStore != null) {
                        getContextTools().createCookie(pResponse, currentLocalStore.getRepositoryId());
                    }
                }
                
                // do not update any properties for global component CookieManager because updated settings can affect other sessions 
                //setStoredSendProfileCookies(getProfileTools().getCookieManager().isSendProfileCookies());                
                //getProfileTools().getCookieManager().setSendProfileCookies(false);
            }  // end if-else
        } catch (RepositoryException e) {
        }  // end try-catch
        super.preLoginUser(pRequest, pResponse);
    }

    /**
     * Restore sendProfileCookies property in CookieManager after postLoginUser().
     *
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
/*    @Override protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
        super.postLoginUser(pRequest, pResponse);
        //restore sendProfileCookies property
        getProfileTools().getCookieManager().setSendProfileCookies(isStoredSendProfileCookies());
    }
*/
    /**
     * Sets mStoredSendProfileCookies property.
     *
     * @return the mStoredSendProfileCookies
     */
/*    public boolean isStoredSendProfileCookies() {
        return mStoredSendProfileCookies;
    }
*/
    /**
     * Gets mStoredSendProfileCookies property.
     *
     * @param pSendProfileCookies the sendProfileCookies to set
     */
/*    public void setStoredSendProfileCookies(boolean pSendProfileCookies) {
        this.mStoredSendProfileCookies = pSendProfileCookies;
    }
*/    

    /**
     * Gets ProfileRequestServlet path.
     *
     * @return the profileRequestServlet
     */
    public String getProfileRequestServlet() {
        return mProfileRequestServlet;
    }

    /**
     * Sets ProfileRequestServlet path.
     *
     * @param pProfileRequestServlet the profileRequestServlet to set
     */
    public void setProfileRequestServlet(String pProfileRequestServlet) {
        this.mProfileRequestServlet = pProfileRequestServlet;
    }

    /**
     * @return the contextTools
     */
    public ContextTools getContextTools() {
        return contextTools;
    }

    /**
     * @param contextTools the contextTools to set
     */
    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }
}
