package com.castorama.commerce.profile;

import static com.castorama.commerce.profile.Constants.ADMIN_LOGIN_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_PASSWORD_SESSION_PARAM;
import static com.castorama.commerce.profile.Constants.ADMIN_ROLE;
import static com.castorama.commerce.profile.Constants.AUTO_LOGIN_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LOGIN_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.PASSWORD_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.RESOURCE_BUNDLE;
import static com.castorama.commerce.profile.Constants.DESCRIPTOR_NAME_PROFILE;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_PASSWORD_IS_SENT;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_ACOUNT_LOCKOUT;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_MISSED_LOGIN;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.states.OrderStates;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ForgotPasswordHandler;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileFormHandler;

import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;

/**
 * Profile Form Handler class that includes login/logout handlers.
 *
 * @author Katsiaryna Dmitrievich
 */
public class CastLoginFormHandler extends CastProfileFormHandler {
    /** SAVED_CART_CONTENT property */
    private static final String SAVED_CART_CONTENT = "savedCartContent";
    
    /** FAILED_ATTEMPTS_PROPERTY property */
    private static final String FAILED_ATTEMPTS_PROPERTY = "failedAttempts";
    
    /** FIRST_ATTEMPT_DATE property */
    private static final String FIRST_ATTEMPT_DATE = "firstAttemptDate";
    
    /** LOCKOUT_DATE property */
    private static final String LOCKOUT_DATE = "lockoutDate";

    /** Magasin ID: used when user's favorite store update is required. */
    private String mMagasinId;

    /** Forgot Password Handler. */
    private ForgotPasswordHandler mForgotPasswordHandler;
    
    /** Password lockout delay. */
    private long accountLockoutDelay;
    
    /** Profile repository property.*/
    private Repository profileRepository;

    /**
     * Overrides {@link
     * ProfileFormHandler#preLoginUser(DynamoHttpServletRequest,
     * DynamoHttpServletResponse)} Validate login and password. Checks role. If
     * <b>admin</b> - redirects on "on behalf" page. <b>Otherwise</b> - checks
     * if autoLogged user've input correct login name.
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    @Override protected void preLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);
        final String password = (String) getValueProperty(PASSWORD_PROFILE_PROP);

        if (isLoggingDebug()) {
            logDebug("PreLogin action. Try to login user " + login);
        }

        if (!validateLogin(login, null, true, pRequest, pResponse)) {
            handleClearPassword();

            if (isLoggingDebug()) {
                logDebug("Login and password validation was failed. Login=[" + login + "] password=[" + password + "]");
            }
            return;
        } else if (isUserLocked(login)) {
            if (isLoggingDebug()) {
                logDebug("The user: " + login + " is locked.");
            }
            return;
        } else if (!validateLogin(login, password, false, pRequest, pResponse)) {
            addFailedAttempt(login);
            return;
        }

        final Profile profile = getProfile();

        try {
            if (profile != null) {
                String newUserId = findUser(pRequest, pResponse).getRepositoryId();
                getContextTools().clearOrderIfStoreChanged(pResponse, getProfile(), getLastOrderForProfile(newUserId));
                getContextTools().clearOrderIfStoreChanged(pResponse, getProfile(), getLastOrderLocalForProfile(newUserId));
                
                if ((getProfileTools().getSecurityStatus(profile) ==
                         getProfileTools().getPropertyManager().getSecurityStatusCookie()) &&
                    !login.equals(profile.getPropertyValue(LOGIN_PROFILE_PROP))) {
                    if (isLoggingDebug()) {
                        logDebug("Auto logged user with id=" + profile.getRepositoryId() + " input incorrect login name.");
                    }
    
                    Object prevLoginObj = profile.getPropertyValue(LOGIN_PROFILE_PROP);
                    String prLogin = null;
    
                    if (prevLoginObj != null) {
                        prLogin = prevLoginObj.toString();
                    }
                    if (!login.equals(prLogin)) {
                        // logout auto-logged user
                        logout(pRequest, pResponse);
                        RepositoryItem newUser = findUser(pRequest, pResponse);
    
                        // set current shopping cart as a last incomplete order for user (go not merge with a shopping cart of auto-logged user)
                        getShoppingCart().setCurrent(getLastOrderForProfile(newUser.getRepositoryId()));
                        //set current shopping cart as a last incomplete order local for user (go not merge with a shopping cart of auto-logged user)
                        ((CastOrderHolder)getShoppingCart()).setCurrentLocal((CastOrderImpl)getLastOrderLocalForProfile(newUser.getRepositoryId()));
                    }
                // getCommonHelper().generateFormException(MSG_INVALID_LOGIN_DIFFERENT_USER, this, RESOURCE_BUNDLE);
                }// end if
                // check if user logged in 
                if ((getProfileTools().getSecurityStatus(profile) ==
                         getProfileTools().getPropertyManager().getSecurityStatusSecureLogin() ||
                         getProfileTools().getSecurityStatus(profile) == getProfileTools().getPropertyManager().getSecurityStatusLogin())){
                    if (!login.equals(profile.getPropertyValue(LOGIN_PROFILE_PROP))){
                        logout(pRequest, pResponse);
                    } else {
                        return;
                    }
                }
            }
        } catch (PropertyNotFoundException e) {
            if (isLoggingWarning()) {
                logWarning("PropertyNotFoundException in CastLoginFormHandler#preLoginUser() method.");
            }
        }  // end try-catch

        if (!getFormError()) {
            SessionBean sb = (SessionBean) pRequest.resolveName(getSessionBean());

            if (((CastProfileTools) getProfileTools()).checkRole(login, ADMIN_ROLE)) {
                // if admin - add his password and login name in the session
                sb.getValues().put(ADMIN_LOGIN_SESSION_PARAM, login);
                sb.getValues().put(ADMIN_PASSWORD_SESSION_PARAM, password);

                addFormException(new DropletException("redirect on onbehalf page"));

                if (isLoggingDebug()) {
                    logDebug("User is admin. Redirect him on \"on behalf\" page.");
                }

                setLoginErrorURL(getOnBehalfURL());

                return;
            } else {
                sb.getValues().remove(ADMIN_LOGIN_SESSION_PARAM);
                sb.getValues().remove(ADMIN_PASSWORD_SESSION_PARAM);
            }
        }  // end if

        super.preLoginUser(pRequest, pResponse);
    }

    /**
     * Logout current user.
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @throws ServletException if there was an error while executing the code
     * @throws IOException      if there was an error with servlet io
     */
    private void logout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();

        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }

            boolean expire = getExpireSessionOnLogout();
            setExpireSessionOnLogout(false);
            preLogoutUser(pRequest, pResponse);

            int status = checkFormError(null, pRequest, pResponse);

            if (status != STATUS_SUCCESS) {
                return;
            }

            postLogoutUser(pRequest, pResponse);

            // restore expire flag
            setExpireSessionOnLogout(expire);
        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (TransactionDemarcationException e) {
            }
        }  // end try-catch-finally
    }

    /**
     * After user login: checks if {@link #getMagasinId()} is not empty. If yes,
     * update user's preferred store with this value. Update autoLogin property
     * if it was changed.
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if the redirect fails
     */
    @Override protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                    throws ServletException, IOException {
        final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);
        
        RepositoryItem repositoryItem = getUserByEmail(login);
        MutableRepository mutableRepository = (MutableRepository)getProfileRepository();
        MutableRepositoryItem mutableRepositoryItem;
        try {
            mutableRepositoryItem = mutableRepository.getItemForUpdate(repositoryItem.getRepositoryId(), DESCRIPTOR_NAME_PROFILE);
            mutableRepositoryItem.setPropertyValue(FAILED_ATTEMPTS_PROPERTY, 0);
            mutableRepository.updateItem(mutableRepositoryItem);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("Can't update profile: " + login, e);
            }
        }

        /* Update favorite store for logged user if param 'magasinId' was set. */
        final String magasinId = getMagasinId();

        if ((magasinId != null) && (magasinId.trim().length() > 0)) {
            getNewsletterProfile().setEmail(login);
            getNewsletterProfile().setUpdatePrefStore(magasinId);
            getNewsletterProfile().handleUpdateMagasin(pRequest, pResponse);
        }

        setRepositoryId(getProfile().getRepositoryId());

        /* check autoLogin property and update it if it was changed */
        final boolean newAutoLogin = (Boolean) getValueProperty(AUTO_LOGIN_PROFILE_PROP);
        final Boolean oldAutoLogin = (Boolean) getProfile().getPropertyValue(AUTO_LOGIN_PROFILE_PROP);

        if ((oldAutoLogin != null) && (oldAutoLogin != newAutoLogin)) {
            super.updateUser(pRequest, pResponse);
        }

        long totalCommerceItemCount = countSavedCartContent();

        if (totalCommerceItemCount > 0) {
            pRequest.addQueryParameter(SAVED_CART_CONTENT, String.valueOf(totalCommerceItemCount));
        }

        super.postLoginUser(pRequest, pResponse);
    }

    /**
     * Gets number of items saved in cart from previous session.
     *
     * @return number of items saved in cart from previous session
     */
    private long countSavedCartContent() {
        long totalCommerceItemCount = 0;
        try {
            Order lastOrderForProfile = getLastOrderForProfile(getProfile().getRepositoryId());
            if (lastOrderForProfile != null) {
                totalCommerceItemCount += lastOrderForProfile.getTotalCommerceItemCount();
            }
            lastOrderForProfile = getLastOrderLocalForProfile(getProfile().getRepositoryId());
            if (lastOrderForProfile != null) {
                totalCommerceItemCount += lastOrderForProfile.getTotalCommerceItemCount();
            }
        } catch (RuntimeException e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
        }

        return totalCommerceItemCount;
    }

    /**
     * Gets last incomplete order for user with profile id = profileId
     *
     * @param  profileId profile id of the user
     *
     * @return last incomplete order for user
     */
    public Order getLastOrderForProfile(String profileId) {
        Order persistentCurrent = null;
        if (!StringUtils.isBlank(profileId)) {
            try {
                int orderState = atg.commerce.states.StateDefinitions.ORDERSTATES.getStateValue(OrderStates.INCOMPLETE);
                List<Order> orders =
                    getOrderManager().getOrderQueries().getOrdersForProfileInState(profileId, orderState);

                if ((orders != null) && (orders.size() > 0)) {
                    int order_index = ((CommerceProfileTools) getProfileTools()).findOrderToLoad(orders);

                    if (order_index >= 0) {
                        persistentCurrent = orders.get(order_index);
                    }
                }
            } catch (CommerceException e) {
                if (isLoggingWarning()) {
                    logWarning("Impossible to set omniture property \'s_savedCartContent\' for profile [" +
                               getProfile().getRepositoryId() + "].");
                }
            }
        }  // end if

        return persistentCurrent;
    }

    /**
     * Gets last incomplete order local for user with profile id = profileId
     *
     * @param  profileId profile id of the user
     *
     * @return last incomplete order for user
     */
    public Order getLastOrderLocalForProfile(String profileId) {
        Order persistentCurrent = null;
        if (!StringUtils.isBlank(profileId)) {
            try {
                int orderState = atg.commerce.states.StateDefinitions.ORDERSTATES.getStateValue(OrderStates.INCOMPLETE);
                List<Order> orders =
                    getOrderManager().getOrderQueries().getOrdersForProfileInState(profileId, orderState);

                if ((orders != null) && (orders.size() > 0)) {
                    int order_index = ((CastProfileTools) getProfileTools()).findOrderLocalToLoad(orders);

                    if (order_index >= 0) {
                        persistentCurrent = orders.get(order_index);
                    }
                }
            } catch (CommerceException e) {
                if (isLoggingWarning()) {
                    logWarning("Impossible to set omniture property \'s_savedCartContent\' for profile [" +
                               getProfile().getRepositoryId() + "].");
                }
            }
        }  // end if

        return persistentCurrent;
    }

    /**
     * Send forgot password email.
     *
     * @param  pRequest  - request
     * @param  pResponse - response
     *
     * @return <code>true</code> if update is successful
     *
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleForgotPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                 throws ServletException, IOException {
        final String login = (String) getValueProperty(LOGIN_PROFILE_PROP);

        if ((login.length() > 0) && validateEmail(login, false, false, pRequest, pResponse)) {
            // send forgot password email
            mForgotPasswordHandler.getValue().put(LOGIN_PROFILE_PROP, getValue().get(LOGIN_PROFILE_PROP));
            mForgotPasswordHandler.handleForgotPassword(pRequest, pResponse);

            getCommonHelper().generateFormException(MSG_PASSWORD_IS_SENT, this, RESOURCE_BUNDLE);
            checkFormRedirect(getLoginErrorURL(), getLoginErrorURL(), pRequest, pResponse);
        } else {
            if(login.length() == 0)
                getCommonHelper().generateFormException(MSG_MISSED_LOGIN, this, RESOURCE_BUNDLE);
            checkFormRedirect(getLoginErrorURL(), getLoginErrorURL(), pRequest, pResponse);
        }

        return false;
    }
    
    private void addFailedAttempt(String login) {
        
        RepositoryItem repositoryItem = getUserByEmail(login);
        int failedAttempts = (Integer)repositoryItem.getPropertyValue(FAILED_ATTEMPTS_PROPERTY);
        Date firstAttemptsDate = (Date)repositoryItem.getPropertyValue(FIRST_ATTEMPT_DATE);
        int newFailedAttempts = failedAttempts;
        Date newFirstAttemptsDate = null;
        Date newLockoutDate = null;
        if((firstAttemptsDate != null) && (failedAttempts > 0)) {
            Calendar now = Calendar.getInstance();
            Calendar firstAttemptsCalendar = new GregorianCalendar();
            firstAttemptsCalendar.setTime(firstAttemptsDate);
            if((now.get(Calendar.YEAR) == firstAttemptsCalendar.get(Calendar.YEAR))
                    && (now.get(Calendar.MONTH) == firstAttemptsCalendar.get(Calendar.MONTH))
                    && (now.get(Calendar.DAY_OF_MONTH) == firstAttemptsCalendar.get(Calendar.DAY_OF_MONTH))){
                if(failedAttempts == 4) {
                    newLockoutDate = new Date();
                    newFailedAttempts++;
                    getCommonHelper().generateFormException(MSG_ACOUNT_LOCKOUT, this, RESOURCE_BUNDLE);
                } else if (failedAttempts < 4) {
                    newFailedAttempts++;
                }
            } else {
                newFirstAttemptsDate = new Date();
                newFailedAttempts = 1;
            }
        } else {
            newFirstAttemptsDate = new Date();
            newFailedAttempts = 1;
        }
            
        MutableRepository mutableRepository = (MutableRepository)getProfileRepository();   
        MutableRepositoryItem mutableProfile;
        try {
            String profileId = repositoryItem.getRepositoryId();
            mutableProfile = mutableRepository.getItemForUpdate(profileId, DESCRIPTOR_NAME_PROFILE);
            mutableProfile.setPropertyValue(FAILED_ATTEMPTS_PROPERTY, newFailedAttempts);
            if(newFirstAttemptsDate != null)
                mutableProfile.setPropertyValue(FIRST_ATTEMPT_DATE, newFirstAttemptsDate);
            if(newLockoutDate != null)
                mutableProfile.setPropertyValue(LOCKOUT_DATE, newLockoutDate);
            mutableRepository.updateItem(mutableProfile);
        } catch (RepositoryException e) {
            if (isLoggingDebug()) {
                logDebug("It's impossible to update user: " + login);
            }
        }
    }
    
    private boolean isUserLocked(String login) {
        
        boolean result = false;
        RepositoryItem repositoryItem = getUserByEmail(login);
        int failedAttempts = (Integer)repositoryItem.getPropertyValue(FAILED_ATTEMPTS_PROPERTY);
        Date lockoutDate = (Date)repositoryItem.getPropertyValue(LOCKOUT_DATE);
        
        if(failedAttempts == 5) {
            long timeFromAccountLockout = new Date().getTime() - lockoutDate.getTime();
            if(timeFromAccountLockout <= getAccountLockoutDelay()) {
                getCommonHelper().generateFormException(MSG_ACOUNT_LOCKOUT, this, RESOURCE_BUNDLE);
                result = true;
            }
        }
        return result;
    }
    
    private RepositoryItem getUserByEmail(String email) {
        RepositoryItemDescriptor profileDescriptor;
        String profileId = null;
        RepositoryItem repositoryItem = null;
        try {
            profileDescriptor = getProfileRepository().getItemDescriptor(DESCRIPTOR_NAME_PROFILE);
            RepositoryView profileView = profileDescriptor.getRepositoryView();
            QueryBuilder profileBuilder = profileView.getQueryBuilder();
            Query profileQuery = profileBuilder.createComparisonQuery(
                    profileBuilder.createPropertyQueryExpression(LOGIN_PROFILE_PROP),
                    profileBuilder.createConstantQueryExpression(email),
                    QueryBuilder.EQUALS);
            RepositoryItem[] profileItems = profileView.executeQuery(profileQuery);
            if(profileItems != null) {
                repositoryItem = profileItems[0];
                profileId = repositoryItem.getRepositoryId();
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("Can't find repository item: " + profileId, e);
            }
        }
        return repositoryItem;
    }

    /**
     * Gets magasin id property - used when user's favorite store update is
     * required
     *
     * @return the magasinId
     */
    public String getMagasinId() {
        return mMagasinId;
    }

    /**
     * Sets magasin id property - used when user's favorite store update is
     * required
     *
     * @param pMagasinId the magasinId to set
     */
    public void setMagasinId(String pMagasinId) {
        this.mMagasinId = pMagasinId;
    }

    /**
     * Set forgot password handler.
     *
     * @return mForgotPasswordHandler
     */
    public ForgotPasswordHandler getForgotPasswordHandler() {
        return mForgotPasswordHandler;
    }

    /**
     * Sets ForgotPasswordHandler
     *
     * @param pForgotPasswordHandler
     */
    public void setForgotPasswordHandler(ForgotPasswordHandler pForgotPasswordHandler) {
        mForgotPasswordHandler = pForgotPasswordHandler;
    }

    public long getAccountLockoutDelay() {
        return accountLockoutDelay;
    }

    public void setAccountLockoutDelay(long accountLockoutDelay) {
        this.accountLockoutDelay = accountLockoutDelay;
    }

    public Repository getProfileRepository() {
        return profileRepository;
    }

    public void setProfileRepository(Repository profileRepository) {
        this.profileRepository = profileRepository;
    }
}
