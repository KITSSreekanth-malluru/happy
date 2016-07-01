package com.castorama.droplet;

import static com.castorama.commerce.profile.Constants.*;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.ProfileTools;

/**
 *
 * @author EPAM team
 */
public class ProfileInfoDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** PROFILE_ID constant. */
    public static final String PROFILE_ID = "profileId";

    /** NAME constant. */
    public static final String NAME = "name";

    /** ADDRESS constant. */
    public static final String ADDRESS = "address";

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String name = null;
        RepositoryItem address = null;
        String profileId = pRequest.getParameter(PROFILE_ID);
        if ((null != profileId) && (0 < profileId.length())) {
            try {
                RepositoryItem profile =
                    getProfile(profileId, (ProfileTools) pRequest.resolveName("/atg/userprofiling/ProfileTools"));

                name = getProfileName(profile);
                address = getProfileAddress(profile);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        if (null != name) {
            pRequest.setParameter(NAME, name);
            pRequest.setParameter(ADDRESS, address);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * Returns profileName property.
     *
     * @param  profileId    parameter to set.
     * @param  profileTools parameter to set.
     *
     * @return profileName property.
     *
     * @throws RepositoryException - exception
     */
    public static String getProfileName(String profileId, ProfileTools profileTools) throws RepositoryException {
        StringBuffer result = null;
        RepositoryItem profile =
            profileTools.getProfileRepository().getItem(profileId, profileTools.getDefaultProfileType());
        String firstName = (String) profile.getPropertyValue(FIRST_NAME_PROFILE_PROP);
        String lastName = (String) profile.getPropertyValue(LAST_NAME_PROFILE_PROP);
        result = new StringBuffer();
        if ((null != firstName) && (0 < firstName.length())) {
            result.append(firstName);
        }
        if ((null != lastName) && (0 < firstName.length())) {
            if (0 < result.length()) {
                result.append(" ");
            }
            result.append(lastName);
        }
        return (null == result) ? null : result.toString();
    }

    /**
     * Returns profileName property.
     *
     * @param  profile parameter to set.
     *
     * @return profileName property.
     *
     * @throws RepositoryException - exception
     */
    private String getProfileName(RepositoryItem profile) throws RepositoryException {
        StringBuffer result = null;
        String firstName = (String) profile.getPropertyValue(FIRST_NAME_PROFILE_PROP);
        String lastName = (String) profile.getPropertyValue(LAST_NAME_PROFILE_PROP);
        result = new StringBuffer();
        if ((null != firstName) && (0 < firstName.length())) {
            result.append(firstName);
        }
        if ((null != lastName) && (0 < firstName.length())) {
            if (0 < result.length()) {
                result.append(" ");
            }
            result.append(lastName);
        }
        return (null == result) ? null : result.toString();
    }

    /**
     * Returns profileAddress property.
     *
     * @param  profile parameter to set.
     *
     * @return profileAddress property.
     *
     * @throws RepositoryException - exception
     */
    private RepositoryItem getProfileAddress(RepositoryItem profile) throws RepositoryException {
        return (RepositoryItem) profile.getPropertyValue(BILLING_ADDRESS);
    }

    /**
     * Returns profile property.
     *
     * @param  profileId    parameter to set.
     * @param  profileTools parameter to set.
     *
     * @return profile property.
     *
     * @throws RepositoryException - exception
     */
    private RepositoryItem getProfile(String profileId, ProfileTools profileTools) throws RepositoryException {
        return profileTools.getProfileRepository().getItem(profileId, profileTools.getDefaultProfileType());
    }

}
