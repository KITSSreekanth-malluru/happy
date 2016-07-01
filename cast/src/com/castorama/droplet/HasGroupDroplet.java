package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;
import atg.userdirectory.droplet.Utils;

import atg.userprofiling.ProfileTools;

import com.castorama.commerce.profile.CastProfileTools;

/**
 * Droplet that checks if user with login <code>userLogin</code> has group with
 * name <code>group</code>.<br />
 * Input params:
 * <li> <code>USER_LOGIN</code> - user login
 * <li> <code>GROUP</code> - searching group name<br />
 * Oparams:
 * <li> <code>true</code> if user has such group
 * <li> <code>false</code> otherwise
 *
 * @author Katsiaryna_Dmitrievich
 * @see    ProfileHelper#checkRole
 * @see    #USER_LOGIN
 * @see    #GROUP
 */

public class HasGroupDroplet extends DynamoServlet implements Constants {
    /** User login - input param name. */
    private static final ParameterName USER_LOGIN = ParameterName.getParameterName("userLogin");

    /** Group - input param name. */
    private static final ParameterName GROUP = ParameterName.getParameterName("group");

    /** Output param name. */
    public static final ParameterName TRUE = ParameterName.getParameterName("true");

    /** Output param name. */
    public static final ParameterName FALSE = ParameterName.getParameterName("false");

    /** Profile Tools. */
    private ProfileTools mProfileTools;

    /**
     * Checks if user with login <code>userLogin</code> has group with name
     * <code>group</code>. If yes set 'true' serviceLocalParameter in request,
     * otherwise set 'false'
     *
     * @param     pRequest  the servlet's request
     * @param     pResponse the servlet's response
     *
     * @exception ServletException if there was an error while executing the
     *                             code
     * @exception IOException      if there was an error with servlet io
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String userLogin = pRequest.getParameter(USER_LOGIN);
        String searchGroupName = pRequest.getParameter(GROUP);

        if (isLoggingDebug()) {
            logDebug("Got userId: " + userLogin + "\nGot groupName: " + searchGroupName);
        }

        if (isMissingParameters(userLogin, searchGroupName)) {
            pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
            return;
        }

        if (((CastProfileTools) getProfileTools()).checkRole(userLogin, searchGroupName)) {
            pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
        }

    }

    /**
     * Gets ProfileTools.
     *
     * @return
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Sets ProfileTools.
     *
     * @param pProfileTools
     */
    public void setProfileTools(ProfileTools pProfileTools) {
        this.mProfileTools = pProfileTools;
    }

    /**
     * This method will determine if there are any missing parameters passed to
     * this droplet. If there are then an error is logged and this method will
     * return true. This method will look for the following parameters:<br>
     *
     * <ul>
     * <li>pUserid - this is the id that is used to look up the user. If it is
     * missing, then the droplet will consider this an error condition.
     * <li>pGroupName - this is the group name that is used to look up the
     * group. If it is missing, then the droplet will consider this an error
     * condition.
     * </ul>
     *
     * @param  pUserId    a <code>String</code> value
     * @param  pGroupName a <code>String</code> value
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(String pUserId, String pGroupName) {
        if (StringUtils.isBlank(pUserId)) {
            if (isLoggingError()) {
                Object[] args = {USER_LOGIN.getName()};
                logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
            }
            return true;
        }

        if (StringUtils.isBlank(pGroupName)) {
            if (isLoggingError()) {
                Object[] args = {GROUP.getName()};
                logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
            }
            return true;
        }

        return false;
    }
}
