package com.castorama.webservices.rest;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.castorama.utils.CastPlaceList;

import atg.commerce.util.PlaceList.Place;
import atg.json.JSONObject;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * <p>This service returns the information about user profiles. To obtain it, the user must be authenticated by
 * its login and password. Both the user credentials are accepted and the user information returned as JSON structures
 * which are passed as request and response bodies, respectively.
 * Regardless of whether or not the result is successful, each response contains a status code that
 * indicates the result of the operation.</p>
 * 
 * <p><h2>The request message</h2>
 * <pre>
 *  {
 *      "login": {string},
 *      "password": {string},
 *  }
 * </pre>
 * <table border="1">
 *  <tr><th>Field Name</th>
 *      <th>Description</th>
 *      <th>Format</th></tr>
 *  <tr><td>login</td>
 *      <td>the user login, an email the user was registered with</td>
 *      <td>case-sensitive</td></tr>
 *  <tr><td>password</td>
 *      <td>the user password</td>
 *      <td>&nbsp;</td></tr>
 * </table>
 * </p>
 * 
 * <p><h2>The response message</h2>
 * <pre>
 *  {
 *      "civilite": {string},
 *      "lastName": {string},
 *      "firstName": {string},
 *      "dateOfBirth": {string},
 *      "inscription": {string},
 *      "eMail": {string},
 *      "postalCode": {string},
 *      "address1": {string},
 *      "address2": {string},
 *      "address3": {string},
 *      "locality": {string},
 *      "country": {string},
 *      "city": {string},
 *      "id_magasin_ref": {string},
 *      "phoneNumber": {string},
 *      "phoneNumber2": {string},
 *      "codeRetour": {int}
 *  }
 * </pre>
 * <table border="1">
 *  <tr><th>Field Name</th>
 *      <th>Description</th>
 *      <th>Format</th></tr>
 *  <tr><td>civilite</td>
 *      <td>honorific title</td>
 *      <td>possible values: MONSIEUR, MADAME, MLLE, SOCIETE. Refer to
 *          <tt>{@link com.castorama.webservices.rest.UserTitle}</tt> for the descriptions of the titles</td></tr>
 *  <tr><td>lastName</td>
 *      <td>surname</td>
 *      <td>length 1-64</td></tr>
 *  <tr><td>firstName</td>
 *      <td>given name</td>
 *      <td>length 1-64</td></tr>
 *  <tr><td>dateOfBirth</td>
 *      <td>user's date of birth</td>
 *      <td>optional, dd/MM/yyyy</td></tr>
 *  <tr><td>eMail</td>
 *      <td>user's e-mail</td>
 *      <td></td></tr>
 *  <tr><td>inscription</td>
 *      <td>the flag that indicates whether or not the user subscribed to receive e-mails from the site</td>
 *      <td>possible values: <tt><i>empty string</i></tt> (default), <tt>true</tt>, <tt>false</tt></td></tr>
 *  <tr><td>postalCode</td>
 *      <td>the postal code of user's delivery address</td>
 *      <td>required, length 1-16</td></tr>
 *  <tr><td>address1</td>
 *      <td>the address line no. 1 of user's delivery address (usually street)</td>
 *      <td>required, length 1-100</td></tr>
 *  <tr><td>address2</td>
 *      <td>the address line no. 2 of user's delivery address (usually floor and apartment)</td>
 *      <td>optional, max length 100</td></tr>
 *  <tr><td>address3</td>
 *      <td>the address line no. 3 of user's delivery address (usually building)</td>
 *      <td>optional, max length 100</td></tr>
 *  <tr><td>locality</td>
 *      <td>the locality of user's delivery address</td>
 *      <td>optional, max length 100</td></tr>
 *  <tr><td>country</td>
 *      <td>the country code of user's delivery address</td>
 *      <td>required, refer to <tt>/atg/commerce/util/CountryList_fr</tt> for the list of the valid country codes</td></tr>
 *  <tr><td>city</td>
 *      <td>the city name of user's delivery address</td>
 *      <td>required, length 1-64</td></tr>
 *  <tr><td>id_magasin_ref</td>
 *      <td>the repository ID of the shop preferred by the user</td>
 *      <td>required</td></tr>
 *  <tr><td>phoneNumber</td>
 *      <td>user's primary telephone number</td>
 *      <td>required, length 1-13</td></tr>
 *  <tr><td>phoneNumber2</td>
 *      <td>user's secondary telephone number</td>
 *      <td>required, length 1-13</td></tr>
 *  <tr><td>codeRetour</td>
 *      <td>the result code of the operation</td>
 *      <td>possible values:
 *          <ul>
 *              <li>{@value #RC_SUCCESS} - success</li>
 *              <li>{@value #RC_NO_SUCH_USER} - the login/password pair is incorrect</li>
 *              <li>{@value #RC_EXCEPTION} - an unexpected error has been encountered while processing the request</li>
 *          </ul>
 *      </td></tr>
 * </table>
 * Each field is necessarily present in a success response. An error response contains only the <tt>codeRetour</tt> field.
 * An empty string is returned to indicate that this field is undefined in the user profile.</p>
 */
public class GetUserInfoServlet extends AbstractUserInfoRESTServlet {
    /** The result code that indicates a successful 'get user info' request processing. */
    public static final int RC_SUCCESS = 101;
    /** The result code that indicates that the user is not authenticated by the given login/password. */
    public static final int RC_NO_SUCH_USER = 102;
    /** The result code that indicates that request processing has not succeed. */
    public static final int RC_EXCEPTION = 103;
    
    private static final String COUNTRY_LIST_NAME = "/atg/commerce/util/CountryList_fr";
    
    /**
     * Processes the request as per the class description.
     * 
     * @param requestBody the request message to get user information.
     * @param request the HTTP request that is being processed.
     * @param response the HTTP response for the HTTP request.
     */
    @Override
    protected JSONObject handleRequest(final JSONObject requestBody, final HttpServletRequest request,
            final HttpServletResponse response) {
        if (requestBody == null) {
            return createInitResult(RC_EXCEPTION);
        }
        final Object loginObj = requestBody.opt("login");
        final Object passwordObj = requestBody.opt("password");
        if (!(loginObj instanceof String) || !(passwordObj instanceof String)) {
            return createInitResult(RC_EXCEPTION);
        }
        final RepositoryItem userProfile = getProfile((String) loginObj, (String) passwordObj, request, response);
        if (userProfile != null) {
            final JSONObject result = createInitResult(RC_SUCCESS);
            putProfile(userProfile, result);
            return result;
        } else {
            return createInitResult(RC_NO_SUCH_USER);
        }
    }
    
    private void putProfile(final RepositoryItem profile, final JSONObject dest) {
        final String title = (String) profile.getPropertyValue("civilite");
        putValue(dest, "civilite", title == null ? "" : UserTitle.fromRepositoryCode(title));
        putValue(profile, "firstName", dest, "firstName");
        putValue(profile, "lastName", dest, "lastName");
        putValue(profile, "eMail", dest, "eMail");
        
        final RepositoryItem newsletter;
        try {
            newsletter = getNewsletterSubscription(profile);
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
        if (newsletter != null) {
            final Date dateOfBirth = (Date) newsletter.getPropertyValue("dateOfBirth");
            putValue(dest, "dateOfBirth", dateOfBirth == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(dateOfBirth));
            
            final RepositoryItem magasin = (RepositoryItem) newsletter.getPropertyValue("id_magasin_ref");            
            putValue(magasin, "id", dest, "id_magasin_ref");
            putValue(newsletter, "receiveEmail", dest, "inscription"); // "true", "false" or ""
        } else {
            putValue(dest, "id_magasin_ref", "");
            putValue(dest, "inscription", "");
        }
        
        final RepositoryItem primContactInfo = (RepositoryItem) profile.getPropertyValue("shippingAddress");
        putValue(primContactInfo, "address1", dest, "address1");
        putValue(primContactInfo, "address2", dest, "address2");
        putValue(primContactInfo, "address3", dest, "address3");
        putValue(primContactInfo, "locality", dest, "locality");
        putValue(dest, "country", countryCode(primContactInfo));
        putValue(primContactInfo, "city", dest, "city");
        putValue(primContactInfo, "postalCode", dest, "postalCode");
        putValue(primContactInfo, "phoneNumber", dest, "phoneNumber");
        putValue(primContactInfo, "phoneNumber2", dest, "phoneNumber2");
    }
    
    private static void putValue(final RepositoryItem item, final String itemPropertyName,
            final JSONObject dest, final String destPropertyName) {
        Object value;
        if (item == null) {
            value = "";
        } else {
            value = item.getPropertyValue(itemPropertyName);
            if (value == null) {
                value = "";
            }
        }
        putValue(dest, destPropertyName, value);
    }
    
    // never returns null
    private String countryCode(final RepositoryItem contactAddress) {
        if (contactAddress == null) {
            return "";
        }
        final String countryName = (String) contactAddress.getPropertyValue("country");
        String countryCode = "";
        if (countryName != null) {
            final Place country = ((CastPlaceList) resolveNucleusComponent(COUNTRY_LIST_NAME)).getPlaceForName(countryName);
            if (country != null && country.getCode() != null) {
                countryCode = country.getCode();
            }
        }
        return countryCode;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject responseBodyForBadRequest(final HttpServletRequest request) {
        return createInitResult(RC_EXCEPTION);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject responseBodyForInternalError(final HttpServletRequest request) {
        return createInitResult(RC_EXCEPTION);
    }
}
