package com.castorama.commerce.profile;

import static com.castorama.commerce.profile.Constants.*;
import static com.castorama.commerce.profile.ErrorCodeConstants.*;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_ILLEGAL_CHARACTERS;
import static com.castorama.commerce.profile.ErrorCodeConstants.MSG_INVALID_CODEPOSTAL;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;

import atg.commerce.profile.CommercePropertyManager;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;

import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.nucleus.GenericService;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.castorama.checkout.CastDeliveryFormHandler;

import com.castorama.utils.CommonHelper;
import com.castorama.utils.Validator;

/**
 * Class with address manipulation utilities.
 *
 * @author EPAM team
 */
public class CastAddressHelper extends GenericService {
    /** Primary address name. */
    private static final String PRIMARY_ADDRESS = "primary";

    /** RQL query for search by code postal. */
    private static final String SEARCH_BY_CODE_POSTAL_QUERY = "code_postal = ?0";

    /** Display name property. */
    private static final String DISPLAY_NAME_PROP = "displayName";

    /** exclusionLivraisonCorse param name. */
    private static final String EXCLUSION_LIVRAISON_CORSE = "exclusionLivraisonCorse";

    /** France country code. */
    private static final String FRANCE = "F";

    /** City column name. */
    private static final String VILLE = "ville";

    /** Postal Code column name. */
    private static final String CODE_POSTAL = "code_postal";

    /** Nickname field name. */
    private static final String NICKNAME = "nickname";

    /** Prefix for new address name. */
    private static final String NEW_ADDRESS_NAME_PREFIX = "Adresse ";

    /** Corse postal code. */
    public static final String CORSE_POSTAL_CODE = "20";

    private static final String NAME = "name";

    private static final String FIELD = "field";

    private static final String CITY = "city";

    /** property ProfileTools. */
    protected ProfileTools mProfileTools;

    /** property Profile. */
    protected Profile mProfile;

    /**
     * Error flag: set to true when incorrect postal code was inputed. In this
     * case drop down box with cities should be displayed.
     */
    private boolean mFlagErrorCp;

    /** Postal Code Repository. */
    private Repository mCodePostalRepository;

    /** Transaction Manager. */
    private TransactionManager mTransactionManager = null;

    /** List of Address (names + address) Properties. */
    private List<String> mNameAddressPropertyList = null;

    /** List of Names Properties. */
    private List<String> mNamePropertyList = null;

    /** List of Address Properties. */
    private List<String> mAddressPropertyList = null;

    /** Common helper component. */
    private CommonHelper commonHelper;

    /**
     * Gets CodePostal Repository.
     *
     * @return the mCodePostalRepository
     */
    public Repository getCodePostalRepository() {
        return mCodePostalRepository;
    }

    /**
     * Sets CodePostal Repository.
     *
     * @param codePostalRepository the mCodePostalRepository to set
     */
    public void setCodePostalRepository(Repository codePostalRepository) {
        mCodePostalRepository = codePostalRepository;
    }

    /**
     * Sets Address prop list.
     *
     * @param addressPropertyList the mAddressPropertyList to set
     */
    public void setAddressPropertyList(List<String> addressPropertyList) {
        mAddressPropertyList = addressPropertyList;
    }

    /**
     * Sets the Address property list, which is a list that mirrors the original
     * design of the AddressProperties property with the property names defined
     * in a configuration file. This List will be created by the
     * initializeAddressPropertyList method creating the appropriate list
     * containing the values from the property manager.
     *
     * @param pNameAddressPropertyList - the Address property list that needs to
     *                                 be set.
     */
    public void setNameAddressPropertyList(final List<String> pNameAddressPropertyList) {
        mNameAddressPropertyList = pNameAddressPropertyList;
    }

    /**
     * Sets the Name property list.
     *
     * @param pNamePropertyList - the name property list that needs to be set.
     */
    public void setNamePropertyList(final List<String> pNamePropertyList) {
        mNamePropertyList = pNamePropertyList;
    }

    /**
     * If the address property list is null, the
     * initializeNameAddressPropertyList method is called to create a list from
     * the appropriate property manager class.
     *
     * @return a List that contains the Address properties that are available
     */
    public List<String> getNameAddressPropertyList() {
        if (mNameAddressPropertyList == null) {
            setNameAddressPropertyList(initializeNameAddressPropertyList());
        }
        return mNameAddressPropertyList;
    }

    /**
     * If the name property list is null, the initializeNamePropertyList method
     * is called to create a list from the appropriate property manager class.
     *
     * @return a List that contains the Address properties that are available
     */
    public List<String> getNamePropertyList() {
        if (mNamePropertyList == null) {
            setNamePropertyList(initializeNamePropertyList());
        }
        return mNamePropertyList;
    }

    /**
     * If the address property list is null, the initAddressPropList method is
     * called to create a list from the appropriate property manager class.
     *
     * @return a List that contains the Address properties that are available
     */
    public List<String> getAddressPropertyList() {
        if (mAddressPropertyList == null) {
            setAddressPropertyList(initAddressPropList());
        }
        return mAddressPropertyList;
    }

    /**
     * This method initializes the list of properties that are used for the
     * Address (address + names) information.
     *
     * @return list of address properties
     */
    public List<String> initializeNameAddressPropertyList() {
        List<String> nl = new ArrayList<String>(10);
        nl.add(FIRST_NAME_ADDRESS_PROP);
        nl.add(PREFIX_ADDRESS_PROP);
        nl.add(LAST_NAME_ADDRESS_PROP);
        nl.addAll(initAddressPropList());
        return nl;
    }

    /**
     * This method initializes the list of properties that are used for the
     * Address information.
     *
     * @return list of address properties
     */
    private List<String> initAddressPropList() {
        List<String> nl = new ArrayList<String>(10);
        nl.add(ADDRESS_1_ADDRESS_PROP);
        nl.add(ADDRESS_2_ADDRESS_PROP);
        nl.add(ADDRESS_3_ADDRESS_PROP);
        nl.add(CITY_ADDRESS_PROP);
        nl.add(POSTAL_CODE_ADDRESS_PROP);
        nl.add(STATE_ADDRESS_PROP);
        nl.add(COUNTRY_ADDRESS_PROP);
        nl.add(PHONE_NUMBER_ADDRESS_PROP);
        nl.add(LOCALITY_ADDRESS_PROP);
        nl.add(PHONE_NUMBER_2_ADDRESS_PROP);
        return nl;
    }

    /**
     * This method initializes the list of properties that are used for the name
     * (profile name section) information.
     *
     * @return list of address properties
     */
    public List<String> initializeNamePropertyList() {
        List<String> nl = new ArrayList<String>(10);
        nl.add(FIRST_NAME_PROFILE_PROP);
        nl.add(TITLE_PROFILE_PROP);
        nl.add(LAST_NAME_PROFILE_PROP);
        return nl;
    }

    /**
     * A helper method to return the CommercePropertyManager.
     *
     * @return Property Manager
     */
    public CommercePropertyManager getCommercePropertyManager() {
        return (CommercePropertyManager) getProfileTools().getPropertyManager();
    }

    /**
     * Sets the transactionManager property, used to manage transactions in the
     * handler.
     *
     * @param pTransactionManager - transaction manager
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns the transactionManager property, used to manage transactions in
     * the handler.
     *
     * @return Transaction Manager
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the property Profile. This is set to the user Profile to edit.
     *
     * @param pProfile - profile
     */
    public void setProfile(Profile pProfile) {
        mProfile = pProfile;
    }

    /**
     * Returns the value of the property Profile.
     *
     * @return profile
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets the property ProfileTools.
     *
     * @param pProfileTools profile tools class instance
     */
    public void setProfileTools(ProfileTools pProfileTools) {
        mProfileTools = pProfileTools;
    }

    /**
     * Gets instance of class with profile manipulation utilities
     *
     * @return The value of the property ProfileTools.
     */
    public ProfileTools getProfileTools() {
        return mProfileTools;
    }

    /**
     * Gets postal code error flag.
     *
     * @return the mFlagErrorCp
     */
    public boolean isFlagErrorCp() {
        return mFlagErrorCp;
    }

    /**
     * Sets postal code error flag.
     *
     * @param flagErrorCp the mFlagErrorCp to set
     */
    public void setFlagErrorCp(boolean flagErrorCp) {
        mFlagErrorCp = flagErrorCp;
    }

    /**
     * Removes address by name
     *
     * @param  pAddressName - address name
     *
     * @return true if success, false otherwise
     */
    public boolean removeAddress(String pAddressName) {
        final CommercePropertyManager propertyManager =
            (CommercePropertyManager) getProfileTools().getPropertyManager();
        if ((pAddressName == null) || (pAddressName.trim().length() == 0)) {
            return false;
        }
        final Profile profile = getProfile();
        Map secondaryAddress = (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
        secondaryAddress.remove(pAddressName);
        Map newSecondaryAddress = new HashMap();

        /* Sort keys to prevent the change of address. */
        SortedSet<String> keySet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        keySet.addAll(secondaryAddress.keySet());
        final Iterator<String> it = keySet.iterator();
        int i = 1;
        while (it.hasNext()) {
            newSecondaryAddress.put(NEW_ADDRESS_NAME_PREFIX + i++, secondaryAddress.get(it.next()));
        }
        secondaryAddress.clear();
        secondaryAddress.putAll(newSecondaryAddress);
        return true;
    }

    /**
     * Add new address.
     *
     * @param  formHandler   - Form Handler to add address
     * @param  addressName   - Name of the Address
     * @param  addressValues - map with address
     *
     * @return <code>true</code> if address have added successfully
     *
     * @throws ServletException
     */
    public boolean addAddress(GenericFormHandler formHandler, String addressName, Map<String, String> addressValues)
                       throws ServletException {
        boolean result = false;

        if ((null != addressName) && (0 < addressName.length())) {
            final RepositoryItem profile = getProfile();
            TransactionManager tm = getTransactionManager();
            TransactionDemarcation td = new TransactionDemarcation();
            try {
                if (tm != null) {
                    td.begin(tm, TransactionDemarcation.REQUIRED);
                }
                final CommercePropertyManager propertyManager = getCommercePropertyManager();
                Map secondaryAddresses =
                    (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
                int secAddrCount = secondaryAddresses.size();
                if (secAddrCount < 3) {
                    MutableRepository repository = (MutableRepository) profile.getRepository();
                    if ((null == secondaryAddresses.get(addressName)) &&
                            validateContactInfo(formHandler, addressValues)) {
                        try {
                            MutableRepositoryItem address =
                                repository.createItem(propertyManager.getContactInfoItemDescriptorName());
                            String propertyName;
                            final Iterator<String> addressIterator = addressValues.keySet().iterator();
                            while (addressIterator.hasNext()) {
                                propertyName = addressIterator.next();
                                setAddressValue(propertyName, address, addressValues);
                            }
                            repository.addItem(address);
                            secondaryAddresses.put(addressName, address);
                            result = true;
                        } catch (RepositoryException e) {
                            logError("Impossible to add an address");
                        }
                    }
                }  // end if-else

            } catch (TransactionDemarcationException e) {
                throw new ServletException(e);
            } finally {
                try {
                    if (tm != null) {
                        td.end();
                    }
                } catch (TransactionDemarcationException e) {
                    if (isLoggingDebug()) {
                        logDebug("Ignoring exception", e);
                    }
                }
            }  // end try-catch-finally
        }  // end if
        return result;
    }

    /**
     * Set address property with name <code>name</code> to repositoryItem with
     * name <code>repositoryItem</code>.
     *
     * @param name           - name of address property
     * @param repositoryItem
     * @param addressValues  - map with address
     */
    private void setAddressValue(final String name, MutableRepositoryItem repositoryItem,
                                 final Map<String, String> addressValues) {
        String value = addressValues.get(name);
        if (value != null) {
            value = value.trim();
        }
        if (name.equalsIgnoreCase(COUNTRY_ADDRESS_PROP) &&
                ((value == null) || ((value != null) && (value.length() == 0)))) {
            value = DEFAULT_COUNTRY_NAME;
        }
        repositoryItem.setPropertyValue(name, value);
    }

    /**
     * Validates Contact Info: title, first name, last name, billing address
     * properties.
     *
     * @param  formHandler   - Form Handler
     * @param  addressValues
     *
     * @return <code>true</code> if address have validated successfully
     *
     * @throws ServletException
     */
    public boolean validateContactInfo(GenericFormHandler formHandler, Map<String, String> addressValues)
                                throws ServletException {
        return validateNameAddress(addressValues, formHandler) & validateAddress(addressValues, formHandler) &
               validatePhone(addressValues, formHandler);
    }

    /**
     * Checks if country is France
     *
     * @param  country     - country to check
     * @param  formHandler - Form Handler
     *
     * @return <code>true</code> if country is France
     */
    public boolean validateCountry(final String country, final GenericFormHandler formHandler) {
        boolean result = FRANCE.equals(country);
        if (!result) {
            getCommonHelper().generateFormException(MSG_NOT_DELIVERED_COUNTRY, formHandler, RESOURCE_BUNDLE);
        }
        return result;
    }

    /**
     * Validate address.
     *
     * @param  values      - map with address
     * @param  formHandler - parent Form Handler
     *
     * @return <code>true</code> if validation is successful
     *
     * @throws ServletException
     */
    public boolean validateAddress(final Map<String, String> values, final GenericFormHandler formHandler)
                            throws ServletException {
        boolean result = true;
        boolean isValidCity = true;

        if (values == null) {
            result = false;
        } else {
            if (StringUtils.isBlank(values.get(ADDRESS_1_ADDRESS_PROP))) {
                getCommonHelper().generateFormException(MSG_MISSED_STREET, formHandler, RESOURCE_BUNDLE);
                result = false;
            } else {
                result = validateAddressField(ADDRESS_1_LABEL, values.get(ADDRESS_1_ADDRESS_PROP), formHandler);
            }
            String city = values.get(CITY_ADDRESS_PROP);
            if (StringUtils.isBlank(city)) {
                getCommonHelper().generateFormException(MSG_MISSED_CITY, formHandler, RESOURCE_BUNDLE);
                isValidCity = false;
                result = false;
            } /*else {
                if (isValidCity = validateCity(city, formHandler)) {
                    city = city.trim();
                } else {
                    result = false;
                }
            }   */
            validateAddressField(ADDRESS_2_LABEL, values.get(ADDRESS_2_ADDRESS_PROP), formHandler);
            validateAddressField(ADDRESS_3_LABEL, values.get(ADDRESS_3_ADDRESS_PROP), formHandler);
            validateAddressField(LOCALITY_LABEL, values.get(LOCALITY_ADDRESS_PROP), formHandler);
            String pc = values.get(POSTAL_CODE_ADDRESS_PROP);
            if (StringUtils.isBlank(pc)) {
                getCommonHelper().generateFormException(MSG_MISSED_POSTAL_CODE, formHandler, RESOURCE_BUNDLE);
                result = false;
            } else {
                //if (validatePostCode(pc, formHandler) && isValidCity) {
                    pc = pc.trim();
                    if (FRANCE.equalsIgnoreCase(values.get(STATE_ADDRESS_PROP))) {
                        if (!existFrancePostalCode(pc)) {
                            getCommonHelper().generateFormException(MSG_INCORRECT_POSTAL_CODE, formHandler,
                                                                    RESOURCE_BUNDLE);
                            result = false;
                        } else if (!verifyCityViaCp(pc, city) && (city != null) && (city.length() > 0)) {
                            setFlagErrorCp(true);
                            getCommonHelper().generateFormException(MSG_INCORRECT_CITY, formHandler, RESOURCE_BUNDLE);
                            result = false;
                        } else {
                            setFlagErrorCp(false);
                        }
                    }
                /*} else {
                    result = false;
                } */
            }
        }  // end if-else
        return result;
    }

    /**
     * Validates value of a postcode. The postcode must include only numbers. If postcode isn't
     * valid adds form exception in form handler
     * @param postcode      a postcode to validate
     * @param formHandler
     * @return              true if postcode is valid and false in another case
     */
   /* private boolean validatePostCode(final String postcode, final GenericFormHandler formHandler) {
        boolean result = true;
        if ( !Validator.validateZipCode(postcode)) {
            getCommonHelper().generateFormException(MSG_INVALID_CODEPOSTAL, formHandler, RESOURCE_BUNDLE);
            result = false;
        }
        return result;
    }*/

    /**
     * Validates address fields such as '№, voie', 'Etage, appartment', 'Batiment',
     * 'Lieu-dit'.
     * @param fieldName     name of validating field in French. Using for error message
     * @param fieldValue    value to validate
     * @param formHandler
     * @return              true if fields value is value and false in another case
     */
    private boolean validateAddressField(final String fieldName, final String fieldValue, final GenericFormHandler formHandler) {
        boolean result = true;
        if (fieldValue.equals("")) {
            return result;
        } else {
            if (!Validator.validateField(fieldValue)) {
                String illegalSymbols = Validator.getIncludedSymbols(fieldValue, FIELD);
                generateIllegalCharactersException(MSG_ILLEGAL_CHARACTERS, fieldName, illegalSymbols, formHandler);
                result = false;
            }
        }
        return result;
    }

    /**
     * Validates city value.
     * @param fieldValue
     * @param formHandler
     * @return
     */
    /*private boolean validateCity(final String fieldValue, final GenericFormHandler formHandler) {
        boolean result = true;
        if (!Validator.validateCity2(fieldValue)) {
            String illegalSymbols = Validator.getIncludedSymbols(fieldValue, CITY);
            generateIllegalCharactersException(MSG_ILLEGAL_CHARACTERS, CITY_LABEL, illegalSymbols, formHandler);
            result = false;
        }
        return result;
    } */

    /**
     * Adds exception in form handler.
     * @param whatException
     * @param fieldName
     * @param illegalSymbols
     * @param formHandler
     */
    private void generateIllegalCharactersException (String whatException, String fieldName, String illegalSymbols,
                                                     GenericFormHandler formHandler) {
        ResourceBundle bundle = ResourceUtils.getBundle(RESOURCE_BUNDLE);
        String cleanErrorMessage = bundle.getString(whatException);

        MessageFormat messageFormat = new MessageFormat(cleanErrorMessage);
        String resultErrorMessage = messageFormat.format(new String[] {fieldName, illegalSymbols});
        formHandler.addFormException(new DropletFormException(resultErrorMessage, formHandler.getAbsoluteName(),
                whatException));
    }

    /**
     * Search postal code <code>postalCode</code> in CodePostalRepository
     *
     * @param  postalCode - postal code to search
     *
     * @return <code>true</code> if <code>postalCode</code> exist in repository
     *
     * @throws ServletException
     */
    public boolean existFrancePostalCode(final String postalCode) throws ServletException {
        boolean result = false;
        if ((getCodePostalRepItemViaPostalCode(postalCode) != null) &&
                (getCodePostalRepItemViaPostalCode(postalCode).length > 0)) {
            result = true;
        }
        return result;
    }

    /**
     * Verify postal code and city using CodePostalRepository
     *
     * @param  postalCode - postal code to verify
     * @param  city       - city to validate
     *
     * @return <code>true</code> if city valid for postal code. <code>
     *         false</code> otherwise
     *
     * @throws ServletException
     */
    public boolean verifyCityViaCp(final String postalCode, String city) throws ServletException {
        try {
            /*
             * if (postalCode != null && !postalCode.equals("") && postalCode.length() != 5) { return true; }
             */
            boolean result = false;
            List<String> validCities = getCitiesViaCodePostal(postalCode);

            if (!validCities.isEmpty()) {
                result = containsCity(validCities, city);
            }
            return result;
        } catch (RepositoryException e) {
            logError("" + e.toString());
            return false;
        }
    }

    /**
     * Checks if list of strings <code>citiesList</code> contains string <code>
     * city</code>
     *
     * @param  citiesList - list of cities
     * @param  city       - searching city
     *
     * @return <code>true</code> if <code>citiesList</code> contains <code>
     *         city</code>, <code>false</code> otherwise
     */
    private boolean containsCity(List<String> citiesList, String city) {
        boolean result = false;
        if ((city != null) && (citiesList != null)) {
            for (final Iterator<String> it = citiesList.iterator(); it.hasNext() && !result;) {
                result = city.equalsIgnoreCase(it.next());
            }
        }
        return result;
    }

    /**
     * Gets list of cities satisfied postal code <code>codePostal</code>
     *
     * @param  codePostal - postal code
     *
     * @return list of cities
     *
     * @throws RepositoryException
     * @throws ServletException
     */
    public List<String> getCitiesViaCodePostal(String codePostal) throws RepositoryException, ServletException {
        List<String> cities = new ArrayList<String>();

        RepositoryItem[] citiesList = getCodePostalRepItemViaPostalCode(codePostal);
        if (null != citiesList) {
            int size = citiesList.length;

            for (int i = 0; i < size; i++) {
                if (isLoggingDebug()) {
                    logDebug("|--> ville : " + citiesList[i].getPropertyValue(VILLE).toString());
                }
                cities.add(citiesList[i].getPropertyValue(VILLE).toString());
            }
        }
        return cities;
    }

    /**
     * Gets items from CodePostalRepository by postal code <code>
     * codePostal</code>.
     *
     * @param  codePostal - postal code
     *
     * @return the list of items from CodePostalRepository
     *
     * @throws ServletException
     */
    private RepositoryItem[] getCodePostalRepItemViaPostalCode(final String codePostal) throws ServletException {
        RepositoryItem[] citiesList = null;

        if (codePostal == null) {
            return citiesList;
        }

        Repository codePostalRepository = getCodePostalRepository();
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = new TransactionDemarcation();
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            try {
                RqlStatement findCity = RqlStatement.parseRqlStatement(SEARCH_BY_CODE_POSTAL_QUERY);
                RepositoryView codePostalView = codePostalRepository.getView(CODE_POSTAL);
                citiesList = findCity.executeQuery(codePostalView, new Object[] {codePostal});
            } catch (RepositoryException rpe) {
                if (isLoggingError()) {
                    logError("Impossible to check postal code.", rpe);
                }
            }
        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }  // end try-catch-finally
        return citiesList;
    }

    /**
     * Validates Corsika
     *
     * @param  shippingGroup
     * @param  formHandler
     * @param  postalCode
     *
     * @return
     *
     * @throws CommerceException
     */
    public boolean validateCorse(HardgoodShippingGroup shippingGroup, CastDeliveryFormHandler formHandler,
                                 String postalCode) throws CommerceException {
        boolean result = true;
        List<String> params = new ArrayList<String>();

        List relationships = shippingGroup.getCommerceItemRelationships();

        if (postalCode != null) {
            if (postalCode.startsWith(CORSE_POSTAL_CODE)) {
                if ((relationships != null) && (relationships.size() != 0)) {
                    ListIterator<CommerceItemRelationship> litr = relationships.listIterator();
                    while (litr.hasNext()) {
                        CommerceItem item = litr.next().getCommerceItem();
                        if (null != item) {
                            String productId = item.getAuxiliaryData().getProductId();
                            RepositoryItem sku = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
                            if (sku.getPropertyValue(EXCLUSION_LIVRAISON_CORSE) != null) {
                                if (((Boolean) sku.getPropertyValue(EXCLUSION_LIVRAISON_CORSE)).booleanValue()) {
                                    result = false;
                                    String productName = (String) sku.getPropertyValue(DISPLAY_NAME_PROP);
                                    String res =
                                        "<a href='../../castCatalog/productDetails.jsp?productId=" + productId + "'>" +
                                        productName + "</a><br/>";
                                    params.add(res);
                                }
                            }
                        }
                    }
                }  // end if
            } else {
                result = true;
            }  // end if-else
        }  // end if

        if (!result) {
            generateUndeliverableProductsException(MSG_UNDELIVERABLE_PRODUCTS, params, formHandler);
        }

        return result;
    }

    /**
     * Generates exception.
     *
     * @param whatException      error code
     * @param substitutionParams parameters for error
     * @param formHandler        form handler were error  occurs
     */
    private void generateUndeliverableProductsException(String whatException, List substitutionParams,
                                                        GenericFormHandler formHandler) {
        ResourceBundle bundle = ResourceUtils.getBundle(RESOURCE_BUNDLE);
        String rawErrorStr = bundle.getString(whatException);

        StringBuffer propertyAsText = new StringBuffer();
        ListIterator<String> litr = substitutionParams.listIterator();
        while (litr.hasNext()) {
            propertyAsText.append(litr.next());
        }
        String formattedErrorStr =
            MessageFormat.format(rawErrorStr, propertyAsText.toString(),
                                 atg.service.dynamo.LangLicense.getLicensedDefault());
        formHandler.addFormException(new DropletFormException(formattedErrorStr, formHandler.getAbsoluteName(),
                                                              whatException));
    }

    /**
     * Validates title, first and last name.
     *
     * @param  address     title - title for validation
     * @param  formHandler firstName - first name for validation
     *
     * @return <code>true</code> if fields are valid, <code>false</code>
     *         otherwise
     */
    public boolean validateNameAddress(final Map<String, String> address, final GenericFormHandler formHandler) {
        return validateName(address.get(PREFIX_ADDRESS_PROP), address.get(FIRST_NAME_ADDRESS_PROP),
                            address.get(LAST_NAME_ADDRESS_PROP), formHandler);
    }

    /**
     * Validates title, first and last name.
     *
     * @param  title       - title for validation
     * @param  firstName   - first name for validation
     * @param  lastName    - last name for validation
     * @param  formHandler - form handler
     *
     * @return <code>true</code> if fields are valid, <code>false</code>
     *         otherwise
     */
    public boolean validateName(final String title, final String firstName, final String lastName,
                                final GenericFormHandler formHandler) {
        boolean result = true;
        if (StringUtils.isBlank(title)) {
            getCommonHelper().generateFormException(MSG_MISSED_TITLE, formHandler, RESOURCE_BUNDLE);
            result = false;
        }
        if (StringUtils.isBlank(firstName)) {
            getCommonHelper().generateFormException(MSG_MISSED_FIRST_NAME, formHandler, RESOURCE_BUNDLE);
            result = false;
        } else {
            if (!isValidName(firstName, formHandler, FIRST_NAME_LABEL)) {
                result = false;
            }
        }
        if (StringUtils.isBlank(lastName)) {
            getCommonHelper().generateFormException(MSG_MISSED_LAST_NAME, formHandler, RESOURCE_BUNDLE);
            result = false;
        } else {
            if (!isValidName(lastName, formHandler, LAST_NAME_LABEL)) {
                result = false;
            }
        }
        return result;
    }

    private boolean isValidName(final String name, final GenericFormHandler formHandler, String nameType) {
        boolean result = true;
        if (!Validator.validateName(name)) {
            String illegalSymbols = Validator.getIncludedSymbols(name, NAME);
            generateIllegalCharactersException(MSG_ILLEGAL_CHARACTERS, nameType, illegalSymbols, formHandler);
            result = false;
        }
        return result;
    }

    /**
     * Prepare address for update: copy data in <code>editValues</code>
     *
     * @param  pNickname - nickname of the address
     * @param  pValues   - map with edited values
     *
     * @return <code>true</code> if address was edited successfully
     *
     * @throws ServletException
     */
    public boolean editAddress(final String pNickname, final Map pValues) throws ServletException {
        final TransactionManager tm = getTransactionManager();
        final TransactionDemarcation td = new TransactionDemarcation();
        final CommercePropertyManager propertyManager = getCommercePropertyManager();
        boolean result = false;
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            if ((pNickname == null) || (pNickname.trim().length() == 0)) {
                result = true;
            } else if (pNickname.equalsIgnoreCase(PRIMARY_ADDRESS)) {
                final Profile profile = getProfile();

                final Iterator<String> nameIterator = getNamePropertyList().iterator();
                Object propertyValue;
                String propertyName;
                while (nameIterator.hasNext()) {
                    propertyName = nameIterator.next();
                    propertyValue = profile.getPropertyValue(propertyName);
                    if (propertyValue != null) {
                        pValues.put(propertyName, propertyValue);
                    }
                }
                final Iterator<String> addressIterator = getAddressPropertyList().iterator();
                RepositoryItem billingAddress = (RepositoryItem) profile.getPropertyValue(BILLING_ADDRESS);
                while (addressIterator.hasNext()) {
                    propertyName = addressIterator.next();
                    propertyValue = billingAddress.getPropertyValue(propertyName);
                    if (propertyValue != null) {
                        pValues.put(propertyName, propertyValue);
                    }
                }
                result = true;
            } else {
                final Profile profile = getProfile();
                Map secondaryAddress =
                    (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
                MutableRepositoryItem editedAddress = (MutableRepositoryItem) secondaryAddress.get(pNickname);
                if (editedAddress != null) {
                    pValues.put(NICKNAME, pNickname);
                    final Iterator<String> addressIterator = getNameAddressPropertyList().iterator();
                    Object propertyValue;
                    String propertyName;
                    while (addressIterator.hasNext()) {
                        propertyName = addressIterator.next();
                        propertyValue = editedAddress.getPropertyValue(propertyName);
                        if (propertyValue != null) {
                            pValues.put(propertyName, propertyValue);
                        }
                    }
                    result = true;
                }
            }  // end if-else

        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }  // end try-catch-finally
        return result;
    }

    /**
     * Update existing address
     *
     * @param  pAddress     - new address
     * @param  pFormHandler - pRequest
     * @param  addressName  - name of the address to update
     *
     * @return <code>true</code> if address was updates, <code>false</code>
     *         otherwise
     *
     * @throws IOException
     * @throws ServletException
     */
    public boolean updateAddress(Map pAddress, final GenericFormHandler pFormHandler, final String addressName)
                          throws IOException, ServletException {
        final TransactionManager tm = getTransactionManager();
        final TransactionDemarcation td = new TransactionDemarcation();
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            final CommercePropertyManager propertyManager = getCommercePropertyManager();

            final Profile profile = getProfile();
            MutableRepository repository = (MutableRepository) profile.getRepository();
            Map secondaryAddress = (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());

            // String nickname = city + ", " + address1 + " " + address2 + " "
            // + address3;
            String nickname = addressName;
            if (StringUtils.isBlank(nickname)) {
                nickname = (String) pAddress.get(NICKNAME);
                if (nickname == null) {
                    return false;
                }
            }
            MutableRepositoryItem updatedAddress = (MutableRepositoryItem) secondaryAddress.get(nickname);

            // What address fields are we looking for
            final Iterator<String> addressIterator = getNameAddressPropertyList().iterator();

            // Copy each property
            String propertyName;
            while (addressIterator.hasNext()) {
                propertyName = addressIterator.next();
                setAddressValue(propertyName, updatedAddress, pAddress);
            }
            try {
                repository.updateItem(updatedAddress);
            } catch (RepositoryException repositoryExc) {
                if (isLoggingError()) {
                    logError(repositoryExc);
                }
                pFormHandler.addFormException(new DropletException("Repository Exception", repositoryExc, ""));

                return false;
            }

            pAddress.clear();

            return true;
        } catch (TransactionDemarcationException e) {
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (TransactionDemarcationException e) {
                if (isLoggingDebug()) {
                    logDebug("Ignoring exception", e);
                }
            }
        }  // end try-catch-finally
    }

    /**
     * Validates phone
     *
     * @param  address     phoneNumber - first phone number
     * @param  formHandler
     *
     * @return <code>true</code> if fields are valid, <code>false</code>
     *         otherwise
     */
    public boolean validatePhone(final Map<String, String> address, final GenericFormHandler formHandler) {
    	
        boolean result = true;
        
        boolean isFrance = false;
        if(DEFAULT_COUNTRY_NAME.equals(address.get(COUNTRY_ADDRESS_PROP)))
        	isFrance = true;
        
        if (StringUtils.isBlank(address.get(PHONE_NUMBER_ADDRESS_PROP))){
        	getCommonHelper().generateFormException(MSG_MISSED_PHONE_NUMBER, formHandler, RESOURCE_BUNDLE);
            result = false;
            if  (!StringUtils.isBlank(address.get(PHONE_NUMBER_2_ADDRESS_PROP)) &&
            		!Validator.validatePhone(address.get(PHONE_NUMBER_2_ADDRESS_PROP), isFrance)) {
            	getCommonHelper().generateFormException(MSG_INCORRECT_PHONE, formHandler, RESOURCE_BUNDLE);
                result = false;
            }
        } else if(!Validator.validatePhone(address.get(PHONE_NUMBER_ADDRESS_PROP), isFrance) ||
        		(!StringUtils.isBlank(address.get(PHONE_NUMBER_2_ADDRESS_PROP)) &&
                		!Validator.validatePhone(address.get(PHONE_NUMBER_2_ADDRESS_PROP), isFrance))) {
        	getCommonHelper().generateFormException(MSG_INCORRECT_PHONE, formHandler, RESOURCE_BUNDLE);
            result = false;
        }

        return result;
    }

    /**
     * Gets Common Helper instance.
     *
     * @return the commonHelper
     */
    public CommonHelper getCommonHelper() {
        return commonHelper;
    }

    /**
     * Sets Common Helper instance.
     *
     * @param commonHelper the commonHelper to set
     */
    public void setCommonHelper(CommonHelper commonHelper) {
        this.commonHelper = commonHelper;
    }

}