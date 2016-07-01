package com.castorama.webservices.rest;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;

import atg.adapter.gsa.GSARepository;
import atg.commerce.util.PlaceList;
import atg.commerce.util.PlaceList.Place;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.json.JSONObject;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.rql.RqlStatement;

/**
 * <p>This service updates the information stored in a user profile. The information is sent in a request along with the
 * user login/password. If the user is authenticated by its login and password and the profile fields are valid according
 * to the format requirements then the user profile is updated with the information passed. The result of the update
 * operation is then returned to the caller. Both the request and response messages are JSON structures
 * which are passed as request and response bodies, respectively.</p>
 * 
 * <p>All the fields are updated with the new values. There is no special value for a field that indicates
 * &quot;do not update the value of the field&quot;.</p>
 * 
 * <p><h2>The request message</h2>
 * <pre>
 *  {
 *      "login": {string},
 *      "password": {string},
 *      "civilite": {string},
 *      "firstName": {string},
 *      "lastName": {string},
 *      "dateOfBirth": {string},
 *      "inscription": {string},
 *      "postalCode": {string},
 *      "address1": {string},
 *      "address2": {string},
 *      "address3": {string},
 *      "locality": {string},
 *      "country": {string},
 *      "city": {string},
 *      "id_magasin_ref": {string},
 *      "phoneNumber": {string},
 *      "phoneNumber2": {string}
 *  }
 * </pre>
 * <table border="1">
 *  <tr><th>Field Name</th>
 *      <th>Description</th>
 *      <th>Format</th></tr>
 *  <tr><td>login</td>
 *      <td>the user login, an email the user is registered with</td>
 *      <td>case-sensitive</td></tr>
 *  <tr><td>password</td>
 *      <td>the user password</td>
 *      <td></td></tr>
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
 *  <tr><td>inscription</td>
 *      <td>the flag that indicates whether or not the user subscribed to receive e-mails from the site</td>
 *      <td>possible values: <tt><i>empty string</i></tt> (default), <tt>true</tt>, <tt>false</tt></td></tr>
 *  <tr><td>postalCode</td>
 *      <td>the postal code of user's delivery address</td>
 *      <td>required, length 1-16. It must be a valid postal code from the database
 *          (repository /atg/registry/Repository/CodePostalRepository). The <i>postalCode</i> and <i>city</i> values
 *          must conform to each other.</td></tr>
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
 *      <td>required, length 1-64. It must be a valid city name from the database (ignoring case,
 *          repository /atg/registry/Repository/CodePostalRepository). The <i>postalCode</i> and <i>city</i> values
 *          must conform to each other</td></tr>
 *  <tr><td>id_magasin_ref</td>
 *      <td>the repository ID of the shop preferred by the user</td>
 *      <td>required</td></tr>
 *  <tr><td>phoneNumber</td>
 *      <td>user's primary telephone number</td>
 *      <td>required, length 1-13</td></tr>
 *  <tr><td>phoneNumber2</td>
 *      <td>user's secondary telephone number</td>
 *      <td>required, length 1-13</td></tr>
 * </table>
 * </p>
 * 
 * <p><h2>The response message</h2>
 * <pre>
 *  {
 *      "codeRetour": {int},
 *      "errorField": {string}
 *  }
 * </pre>
 * <table border="1">
 *  <tr><th>Field Name</th>
 *      <th>Description</th>
 *      <th>Format</th></tr>
 *  <tr><td>codeRetour</td>
 *      <td>the result code of the operation</td>
 *      <td>possible values:
 *          <ul>
 *              <li>{@value #RC_SUCCESS} - success</li>
 *              <li>{@value #RC_NO_SUCH_USER} - the login/password pair is incorrect</li>
 *              <li>{@value #RC_EXCEPTION} - an unexpected error has been encountered while processing the request</li>
 *          </ul>
 *      </td></tr>
 *  <tr><td>errorField</td>
 *      <td>the list of input fields that are invalid. The service returns <i>errorField</i> if and only if
 *          any of the input field values is invalid</td>
 *      <td>&nbsp;</td></tr>
 * </table>
 * </p>
 */
public class UpdateUserInfoServlet extends AbstractUserInfoRESTServlet {
    /** The result code that indicates a successful 'update user info' request processing. */
    public static final int RC_SUCCESS = 201;
    /** The result code that indicates that the user is not authenticated by the given login/password. */
    public static final int RC_NO_SUCH_USER = 202;
    /** The result code that indicates that request processing has not succeed. */
    public static final int RC_EXCEPTION = 203;
    
    private static final String COUNTRY_LIST_NAME = "/atg/commerce/util/CountryList_fr";
    private static final String POSTAL_CODE_REPOSITORY_NAME = "/atg/registry/Repository/CodePostalRepository";
    private static final String MAGASING_REPOSITORY_NAME = "/atg/registry/Repository/MagasinGSARepository";
    
    private static final RqlStatement postalCodeCityQuery;
    
    private static final String DOB_FORMAT = "dd/MM/yyyy";
    
    static {
        try {
            postalCodeCityQuery = RqlStatement.parseRqlStatement("code_postal = ?0 and ville equals ignorecase ?1");
        } catch (RepositoryException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Processes the request as per the class description.
     * 
     * @param updateRequest the request message to update user profile.
     * @param request the HTTP request that is being processed.
     * @param response the HTTP response for the HTTP request.
     */
    @Override
    protected JSONObject handleRequest(final JSONObject updateRequest, final HttpServletRequest request,
            final HttpServletResponse response) {
        if (updateRequest == null) {
            return createInitResult(RC_EXCEPTION);
        }
        final TreeSet<String> errorFields = new TreeSet<String>();
        if (!validate(updateRequest, errorFields)) {
            final JSONObject result = createInitResult(RC_EXCEPTION);
            putValue(result, "errorField", errorFields.toString());
            return result;
        }
        final String login = (String) updateRequest.opt("login");
        final String password = (String) updateRequest.opt("password");
        if (StringUtils.isEmpty(login) || password == null) {
            return createInitResult(RC_NO_SUCH_USER);
        }
        final RepositoryItem userProfile = getProfile(login, password, request, response);
        if (userProfile != null) {
            try {
                updateProfile(userProfile, updateRequest);
                return createInitResult(RC_SUCCESS);
            } catch (RepositoryException ex) {
                if (logger().isLoggingError()) {
                    logger().logError("unable to update user info", ex);
                }
                return createInitResult(RC_EXCEPTION);
            }
        } else {
            return createInitResult(RC_NO_SUCH_USER);
        }
    }
    
    // empty or true/false
    private static final Pattern FLD_INSCRIPTION_PATTERN = Pattern.compile("|true|false");
    
    private void updateProfile(final RepositoryItem profile, final JSONObject updateRequest) throws RepositoryException {
        final GSARepository profileRep = (GSARepository) profile.getRepository();
        final GSARepository newsletterRep = (GSARepository) resolveNucleusComponent(NEWSLETTER_REPOSITORY_NAME);
        boolean finished = false;
        final MultiTMTransactionDemarcation td = new MultiTMTransactionDemarcation();
        try {
            try {
                final TransactionManager[] tms = new TransactionManager[]{
                        profileRep.getTransactionManager(), newsletterRep.getTransactionManager()};
                td.begin(tms, TransactionDemarcation.REQUIRES_NEW);

                final MutableRepositoryItem newProfile = profileRep.
                        getItemForUpdate(profile.getRepositoryId(), profile.getItemDescriptor().getRepositoryView().getViewName());
                
                if (newProfile == null) { // someone has deleted the user in the mid of the update operation
                    throw new RepositoryException("There is no such user in the repository: " + profile.getRepositoryId());
                }
                
                final String rawTitle = (String) updateRequest.opt("civilite");
                newProfile.setPropertyValue("civilite", UserTitle.valueOf(rawTitle).repositoryCode());
                newProfile.setPropertyValue("firstName", (String) updateRequest.opt("firstName"));
                newProfile.setPropertyValue("lastName", (String) updateRequest.opt("lastName"));
                final Date dateOfBirth = new SimpleDateFormat(DOB_FORMAT).parse(
                        (String) updateRequest.opt("dateOfBirth"), new ParsePosition(0));
                newProfile.setPropertyValue("dateOfBirth", dateOfBirth);
                
                final RepositoryItem primContactInfo = (RepositoryItem) newProfile.getPropertyValue("shippingAddress");
                final MutableRepositoryItem newPrimContactInfo;
                final boolean contactInfoCreated;
                if (primContactInfo == null) {
                    newPrimContactInfo = profileRep.createItem("contactInfo");
                    newProfile.setPropertyValue("shippingAddress", newPrimContactInfo);
                    contactInfoCreated = true;
                } else {
                    newPrimContactInfo = profileRep.getItemForUpdate(primContactInfo.getRepositoryId(), "contactInfo");
                    contactInfoCreated = false;
                }
                newPrimContactInfo.setPropertyValue("address1", (String) updateRequest.opt("address1"));
                newPrimContactInfo.setPropertyValue("address2", (String) updateRequest.opt("address2"));
                newPrimContactInfo.setPropertyValue("address3", (String) updateRequest.opt("address3"));
                newPrimContactInfo.setPropertyValue("locality", (String) updateRequest.opt("locality"));
                
                final String countryCode = (String) updateRequest.opt("country");
                String countryName = "";
                if (countryCode != null) {
                    final PlaceList countryList = (PlaceList) resolveNucleusComponent(COUNTRY_LIST_NAME);
                    final Place country = countryList.getPlaceForCode(countryCode);
                    if (country != null && country.getDisplayName() != null) {
                        // PlaceList does not trim trim whitespaces while parsing the config file. Have to do this here
                        countryName = country.getDisplayName().trim();
                    }
                }
                newPrimContactInfo.setPropertyValue("country", countryName);
                newPrimContactInfo.setPropertyValue("city", (String) updateRequest.opt("city"));
                newPrimContactInfo.setPropertyValue("postalCode", (String) updateRequest.opt("postalCode"));
                newPrimContactInfo.setPropertyValue("phoneNumber", (String) updateRequest.opt("phoneNumber"));
                newPrimContactInfo.setPropertyValue("phoneNumber2", (String) updateRequest.opt("phoneNumber2"));
                
                final RepositoryItem subscription = getNewsletterSubscription(profile);
                final MutableRepositoryItem newSubscription;
                final boolean subscriptionCreated;
                if (subscription == null) {
                    newSubscription = newsletterRep.createItem("abonnementNewsletter");
                    newSubscription.setPropertyValue("profile", profile.getRepositoryId());
                    subscriptionCreated = true;
                } else {
                    newSubscription = newsletterRep.getItemForUpdate(subscription.getRepositoryId(), "abonnementNewsletter");
                    subscriptionCreated = false;
                }
                newSubscription.setPropertyValue("dateOfBirth", dateOfBirth);
                newSubscription.setPropertyValue("receiveEmail", (String) updateRequest.opt("inscription"));
                final String magasinRefId = (String) updateRequest.opt("id_magasin_ref");
                if (magasinRefId.length() == 0) {
                    newSubscription.setPropertyValue("id_magasin_ref", null);
                } else {
                    final GSARepository magasinRep = (GSARepository) resolveNucleusComponent(MAGASING_REPOSITORY_NAME);
                    final RepositoryItem magasin = magasinRep.getItem((String) updateRequest.opt("id_magasin_ref"), "magasin");
                    if (magasin == null) { // if someone has removed the magasin during the update procedure
                        // the user info is not updated
                        throw new RepositoryException("There is no such magasin in the repository: " + magasinRefId);
                    }
                    newSubscription.setPropertyValue("id_magasin_ref", magasin);
                }
                
                if (subscriptionCreated) {
                    newsletterRep.addItem(newSubscription);
                } else {
                    newsletterRep.updateItem(newSubscription);
                }
                if (contactInfoCreated) {
                    profileRep.addItem(newPrimContactInfo);
                } else {
                    profileRep.updateItem(newPrimContactInfo);
                }
                profileRep.updateItem(newProfile);
                
                finished = true;
            } finally {
                td.end(!finished);
            }
        } catch (TransactionDemarcationException ex) {
            throw new RepositoryException(ex);
        }
    }
    
    /* Validates the 'update user' request object. Returns 'true' if the request is valid; 'false' is returned otherwise.
     * The list of name of invalid or missing fields is passed via errorFields */
    private boolean validate(final JSONObject request, final TreeSet<String> errorFields) {
        validateTitle(request, errorFields);
        validateDateOfBirth(request, errorFields);
        validateRequiredAndMaxLength(request, "firstName", 64, errorFields);
        validateRequiredAndMaxLength(request, "lastName", 64, errorFields);
        validateRequiredAndMaxLength(request, "address1", 100, errorFields);
        validateOptionalAndMaxLength(request, "address2", 100, errorFields);
        validateOptionalAndMaxLength(request, "address3", 100, errorFields);
        validateOptionalAndMaxLength(request, "locality", 100, errorFields);
        validateRequiredAndMaxLength(request, "phoneNumber", 13, errorFields);
        validateOptionalAndMaxLength(request, "phoneNumber2", 13, errorFields);
        validatePattern(request, "inscription", FLD_INSCRIPTION_PATTERN, errorFields);
        validateCityPostalCode(request, errorFields);
        validateCountry(request, errorFields);
        validateMagasinRef(request, errorFields);
        return errorFields.isEmpty();
    }
    
    private static boolean validateOptionalAndMaxLength(final JSONObject request, final String fieldName, int maxLength,
            final TreeSet<String> errorFields) {
        final Object value = request.opt(fieldName);
        if (value instanceof String && ((String) value).length() <= maxLength) {
            return true;
        }
        errorFields.add(fieldName);
        return false;
    }
    
    private static boolean validateRequiredAndMaxLength(final JSONObject request, final String fieldName, int maxLength,
            final TreeSet<String> errorFields) {
        final Object value = request.opt(fieldName);
        if (value instanceof String) {
            final String str = (String) value;
            if (str.length() > 0 && str.length() <= maxLength) {
                return true;
            }
        }
        errorFields.add(fieldName);
        return false;
    }
    
    private static boolean validatePattern(final JSONObject request, final String fieldName, Pattern pattern,
            final TreeSet<String> errorFields) {
        final Object value = request.opt(fieldName);
        if (value instanceof String && pattern.matcher((String) value).matches()) {
            return true;
        }
        errorFields.add(fieldName);
        return false;
    }

    private static void validateTitle(final JSONObject request, final TreeSet<String> errorFields) {
        final Object rawTitle = request.opt("civilite");
        if (rawTitle instanceof String) {
            final String title = (String) rawTitle;
            try {
                UserTitle.valueOf(title);
                return;
            } catch (IllegalArgumentException ex) {
                // the errorFields is updated with the field name in the end of the function
            }
        }
        errorFields.add("civilite");
    }
    
    // validates that request.dateOfBirth is either empty or a valid date in the 'dd/MM/yyyy' format
    private static void validateDateOfBirth(final JSONObject request, final TreeSet<String> errorFields) {
        final Object dobObj = request.opt("dateOfBirth");
        if (dobObj instanceof String) {
            final String dob = (String) dobObj;
            if (dob.length() == 0) {
                return;
            }
            final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            format.setLenient(false);
            if (format.parse(dob, new java.text.ParsePosition(0)) != null) {
                return;
            }
        }
        errorFields.add("dateOfBirth");
    }
    
    private void validateCityPostalCode(final JSONObject request, final TreeSet<String> errorFields) {
        boolean bothValid = true;
        if (validateRequiredAndMaxLength(request, "postalCode", 16, errorFields)) {
            bothValid &= true;
        } else {
            bothValid &= false;
            errorFields.add("postalCode");
        }
        if (validateRequiredAndMaxLength(request, "city", 64, errorFields)) {
            bothValid &= true;
        } else {
            bothValid &= false;
            errorFields.add("city");
        }
        if (!bothValid) {
            return;
        }
        try {
            // checks that the postal code conforms to the city name. If it does not then both fields are the suspects to be invalid
            final Repository pcRep = (Repository) resolveNucleusComponent(POSTAL_CODE_REPOSITORY_NAME);
            final int count = postalCodeCityQuery.executeCountQuery(pcRep.getView("code_postal"),
                    new Object[]{request.opt("postalCode"), request.opt("city")});
            if (count == 0) {
                errorFields.add("postalCode");
                errorFields.add("city");
            }
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private void validateCountry(final JSONObject request, final TreeSet<String> errorFields) {
        final Object countryObj = request.opt("country");
        if (countryObj instanceof String) {
            final String countryCode = (String) countryObj;
            if (countryCode.length() > 0) {
                final PlaceList countryList = (PlaceList) resolveNucleusComponent(COUNTRY_LIST_NAME);
                if (countryList.getPlaceForCode(countryCode) != null) {
                    return;
                }
            }
        }
        errorFields.add("country");
    }
    
    private void validateMagasinRef(final JSONObject request, final TreeSet<String> errorFields) {
        try {
            final Object magasinRefObj = request.opt("id_magasin_ref");
            if (magasinRefObj instanceof String) {
                final String magasinRef = (String) magasinRefObj;
                if (magasinRef.length() > 0) {
                    final Repository magasinRepository = (Repository) resolveNucleusComponent(MAGASING_REPOSITORY_NAME);
                    if (magasinRepository.getItem(magasinRef, "magasin") != null) {
                        return;
                    }
                }
            }
            errorFields.add("id_magasin_ref");
        } catch (RepositoryException ex) {
            throw new IllegalStateException(ex);
        }
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
