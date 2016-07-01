package com.castorama.droplet;

import static com.castorama.commerce.profile.Constants.ADDRESS_1_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.ADDRESS_3_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.CITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.COUNTRY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.DEFAULT_COUNTRY_NAME;
import static com.castorama.commerce.profile.Constants.FIRST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.LOCALITY_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_2_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PHONE_NUMBER_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.POSTAL_CODE_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.PREFIX_ADDRESS_PROP;
import static com.castorama.commerce.profile.Constants.STATE_ADDRESS_PROP;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userdirectory.droplet.Constants;

import com.castorama.commerce.profile.CastProfileFormHandler;

/**
 * Droplet for manipulations with addresses. Behaviour depends on input
 * parameters.
 * <dt>Input params:
 * <dd>
 * <li><code>type</code> - action that should be occured with address:
 *
 * <pre><code>
   uneAdresse</code> - read data from request and create new address for current
   profile<br>
   <code>modifierAdresse</code> - read data from request and update address with
   name from param <code>addressName</code><br>
   <code>modifierPrAdresse</code> - read data from request and update primary
   address for current profile<br>
   <code>edit</code> - gets address name <code>addressName</code>, reads this
   address from repository and fill in it into address bean<br></pre>
 * <dd>
 * <li><code>addressName</code> - name of non-primary address:
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> if oparam <code>type</code> is missed or if action
 * was processed with exceptions
 * <dd>
 * <li><code>empty</code> if action was processed without exceptions
 * <dt>Output params:
 * <dd>
 * <li><code>editValue</code> with address values if oparam <code>
 * type==edit</code>
 * <dd>
 * <li><code>formError</code> if action was processed with exceptions, <code>
 * formError==true</code>
 * <dd>
 * <li><code>formExceptions</code> if action was processed with exceptions,
 * contains the vector of form handler exceptions
 * <dd>
 * <li><code>flagErrorCp</code> if action was processed with exceptions, sets in
 * <code>true</code> if city value is incorrect for current postal code
 *
 * @author Katsiaryna Dmitrievich
 */
public class AddressDroplet extends DynamoServlet implements Constants {
    /** FLAG_ERROR_CP_OPARAM constant */
    private static final String FLAG_ERROR_CP_OPARAM = "flagErrorCp";

    /** FORM_EXCEPTIONS_OPARAM constant */
    private static final String FORM_EXCEPTIONS_OPARAM = "formExceptions";

    /** FORM_ERROR_OPARAM constant */
    private static final String FORM_ERROR_OPARAM = "formError";

    /** EDIT_TYPE constant */
    private static final String EDIT_TYPE = "edit";

    /** UPDATE_PRIMARY_ADDRESS_TYPE constant */
    private static final String UPDATE_PRIMARY_ADDRESS_TYPE = "modifierPrAdresse";

    /** UPDATE_ADDRESS_TYPE constant */
    private static final String UPDATE_ADDRESS_TYPE = "modifierAdresse";

    /** ADDRESS_NAME_IPARAM constant */
    private static final ParameterName ADDRESS_NAME_IPARAM = ParameterName.getParameterName("addressName");

    /** EDIT_VALUE_OPARAM constant */
    private static final String EDIT_VALUE_OPARAM = "editValue";

    /** ADD_ADDRESS_TYPE constant */
    private static final String ADD_ADDRESS_TYPE = "uneAdresse";

    /** Input parameter name for query string. */
    private static final ParameterName TYPE_IPARAM = ParameterName.getParameterName("type");

    /** profile handler property */
    private CastProfileFormHandler profileHandler;

    /**
     * Return the profileHandler property.
     *
     * @return profileHandler property.
     */
    public CastProfileFormHandler getProfileHandler() {
        return profileHandler;
    }

    /**
     * Set the profileHandler property.
     *
     * @param pProfileHandler
     */
    public void setProfileHandler(CastProfileFormHandler pProfileHandler) {
        profileHandler = pProfileHandler;
    }

    /**
     * Address manipulation servlet.<br>
     * If oparam type:
     *
     * <ul>
     * <li><code>uneAdresse</code> - read data from request and create new
     * address for current profile<br>
     *
     * <li><code>modifierAdresse</code> - read data from request and update
     * address with name from param <code>addressName</code><br>
     *
     * <li><code>modifierPrAdresse</code> - read data from request and update
     * primary address for current profile<br>
     *
     * <li><code>edit</code> - gets address name <code>addressName</code>, reads
     * this address from repository and fill in it into address bean<br>
     * </ul>
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @throws ServletException if servlet exception occures
     * @throws IOException      if servlet io exception occures
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String type = pRequest.getParameter(TYPE_IPARAM);
        String addressName = pRequest.getParameter(ADDRESS_NAME_IPARAM);
        CastProfileFormHandler profileHandler = getProfileHandler();
        profileHandler.resetFormExceptions();
        if (StringUtils.isBlank(type)) {
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        } else if (ADD_ADDRESS_TYPE.equalsIgnoreCase(type)) {
            profileHandler.newAddress(pRequest, pResponse, convertAddress(pRequest));
            setFormExceptions(pRequest, pResponse, profileHandler);
        } else if (UPDATE_ADDRESS_TYPE.equalsIgnoreCase(type) && !StringUtils.isBlank(addressName)) {
            profileHandler.updateAddress(pRequest, pResponse, convertAddress(pRequest), addressName);
            setFormExceptions(pRequest, pResponse, profileHandler);
        } else if (UPDATE_PRIMARY_ADDRESS_TYPE.equalsIgnoreCase(type)) {
            profileHandler.updatePrimaryAddress(pRequest, pResponse, convertAddress(pRequest));
            setFormExceptions(pRequest, pResponse, profileHandler);
        } else if (EDIT_TYPE.equalsIgnoreCase(type) && !StringUtils.isBlank(addressName)) {
            profileHandler.editAddress(pRequest, pResponse, addressName);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            pRequest.setParameter(EDIT_VALUE_OPARAM, profileHandler.getEditValue());
        }
    }

    /**
     * Sets output params if form errors were occured.
     *
     * @param  pRequest       the servlet's request
     * @param  pResponse      the servlet's response
     * @param  profileHandler profile form handler
     *
     * @throws ServletException if servlet exception occures
     * @throws IOException      if servlet io exception occures
     */
    private void setFormExceptions(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                   CastProfileFormHandler profileHandler) throws ServletException, IOException {
        if (profileHandler.getFormError()) {
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            pRequest.setParameter(FORM_ERROR_OPARAM, true);
            pRequest.setParameter(FORM_EXCEPTIONS_OPARAM, profileHandler.getFormExceptions());
            pRequest.setParameter(FLAG_ERROR_CP_OPARAM, profileHandler.isFlagErrorCp());
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * Create map with address properties from bean.
     *
     * @param  pRequest the servlet's request
     *
     * @return map with address properties
     */
    public Map<String, String> convertAddress(DynamoHttpServletRequest pRequest) {
        Map<String, String> address = new HashMap<String, String>();
        address.put(PREFIX_ADDRESS_PROP, pRequest.getParameter("prefix"));
        address.put(FIRST_NAME_ADDRESS_PROP, pRequest.getQueryParameter("firstname"));
        address.put(LAST_NAME_ADDRESS_PROP, pRequest.getQueryParameter("lastname"));


        address.put(ADDRESS_1_ADDRESS_PROP, pRequest.getQueryParameter("address1"));
        address.put(ADDRESS_2_ADDRESS_PROP, pRequest.getQueryParameter("address2"));
        address.put(ADDRESS_3_ADDRESS_PROP, pRequest.getQueryParameter("address3"));
        address.put(LOCALITY_ADDRESS_PROP, pRequest.getQueryParameter("locality"));
        address.put(POSTAL_CODE_ADDRESS_PROP, pRequest.getQueryParameter("postcode"));
        address.put(CITY_ADDRESS_PROP, pRequest.getQueryParameter("city"));
        address.put(STATE_ADDRESS_PROP, pRequest.getQueryParameter("state"));
        String country = pRequest.getQueryParameter("country");

        if ((country == null) || ((country != null) && (country.length() == 0))) {
            country = DEFAULT_COUNTRY_NAME;
        }

        address.put(COUNTRY_ADDRESS_PROP, country);
        address.put(PHONE_NUMBER_ADDRESS_PROP, pRequest.getQueryParameter("phone1"));
        address.put(PHONE_NUMBER_2_ADDRESS_PROP, pRequest.getQueryParameter("phone2"));

        return address;
    }
}
