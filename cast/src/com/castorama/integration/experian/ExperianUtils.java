/**
 * 
 */
package com.castorama.integration.experian;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;

import com.castorama.integration.Constants;
import com.castorama.model.Abonnement;
import com.castorama.model.Profile;

/**
 * @author Mikalai_Khatsko
 * 
 */
public class ExperianUtils {

    /** Profile descriptor name constant. */
    public static final String DESCRIPTOR_NAME_PROFILE = "user";

    /** Experian request descriptor name constant. */
    public static final String EXPERIAN_REQUEST_ITEM_DESCRIPTOR_NAME = "experianRequest";

    /** Experian request history descriptor name constant. */
    public static final String HISTORY_NEWSLETTER_ITEM_DESCRIPTOR = "experianRequestHistory";

    /** Export date property name constant. */
    public static final String EXPORT_DATE_PROPERTY = "exportDate";

    /** Export date property name constant. */
    private static final String REQUEST_SOURCE_PROPERTY = "requestSource";

    /** EMAIL_LOGIN constant */
    private static final String EMAIL_LOGIN = "login";

    /** Apache Commons Logging instance. */
    private static final Log LOGGER = LogFactory.getLog(ExperianUtils.class);

    /** Property names to write in csv file */
    public static final String[] PROPERTY_NAMES = { "civilite", "firstName", "lastName", "login", "address1",
            "address2", "address3", "locality", "postalCode", "city", "country", "receiveEmail", "reseiveOffers",
            "sourceInscription", "dateInscription", "dateDesincrption", "dateOffersInscription",
            "dateOffersDesincrption", "phoneNumber", "phoneNumber2", "customerCasto", "storeReference", "carteAtout",
            "jardin", "maison", "proprietaire_locataire", "maisonCampagne", "nbPersonnes", "id", "dateOfBirth",
            "registrationDate", "DATEMAJPROFIL", "typeMAJprofil" };

    /** Column names to write in csv file */
    public static final String[] DATA_FILE_COLUMN_NAMES = { "Civilite", "Prenom", "Nom", "Email", "Adresse1",
            "Adresse2", "Adresse3", "Adresse4", "CodePostal", "Ville", "Pays", "OptinCastorama", "OptinPartenaire",
            "SourceInscription", "DateOptinCastorama", "DateOptoutCastorama", "DateOptinPartenaire",
            "DateOptoutPartenaire", "Telephone", "Telephone2", "ClientCasto", "MagasinPrefere", "CarteCastorama",
            "Jardin", "HabiteMaison", "ProprietaireLocataire", "ResidenceSecondaire", "NbPersonnesFoyer", "IdATG",
            "DateNaissance", "RegistrationDate", "DateMAJProfil", "TypeMAJProfil" };

    /** Data file extension constant */
    public static final String DATA_FILE_EXTENSION = ".csv";


    /** Data file delimeter constant */
    public static final String DATA_FILE_DELIMETER = ";";

    /* Newsletter enumerated properties */
    public static final Set<String> ENUM_PROPERTIES;
    static {
        Set<String> set = new HashSet<String>();

        set.add("civilite");
        set.add("receiveEmail");
        set.add("reseiveOffers");
        set.add("nbPersonnes");
        set.add("proprietaire_locataire");

        ENUM_PROPERTIES = Collections.unmodifiableSet(set);
    }

    /* Newsletter Boolean properties */
    public static final Set<String> BOOLEAN_PROPERTIES;
    static {
        Set<String> set = new HashSet<String>();
        set.add("customerCasto");
        set.add("carteAtout");
        set.add("jardin");
        set.add("maison");
        set.add("maisonCampagne");
        BOOLEAN_PROPERTIES = Collections.unmodifiableSet(set);
    }

    /** experianRequestItemProperties property. */
    public static final Map<String, Integer> experianRequestItemProperties;
    static {
        Map<String, Integer> tMap = new HashMap<String, Integer>();
        /*
         * 1 - profile 2 - contactInfo 3 - newsletter
         */
        tMap.put("civilite", 1);
        tMap.put("firstName", 1);
        tMap.put("lastName", 1);
        tMap.put(EMAIL_LOGIN, 1);
        tMap.put("address1", 2);
        tMap.put("address2", 2);
        tMap.put("address3", 2);
        tMap.put("locality", 2);
        tMap.put("postalCode", 2);
        tMap.put("city", 2);
        tMap.put("country", 2);
        tMap.put("receiveEmail", 3);
        tMap.put("reseiveOffers", 3);
        tMap.put("sourceInscription", 3);
        tMap.put("dateInscription", 3);
        tMap.put("dateDesincrption", 3);
        tMap.put("dateOffersInscription", 3);
        tMap.put("dateOffersDesincrption", 3);
        tMap.put("phoneNumber", 2);
        tMap.put("phoneNumber2", 2);
        tMap.put("customerCasto", 4);
        tMap.put("storeReference", 5);
        tMap.put("carteAtout", 3);
        tMap.put("jardin", 3);
        tMap.put("maison", 3);
        tMap.put("proprietaire_locataire", 3);
        tMap.put("maisonCampagne", 3);
        tMap.put("nbPersonnes", 3);
        tMap.put("id", 1);
        tMap.put("dateOfBirth", 3);
        tMap.put("registrationDate", 1);
        tMap.put("dateMAJProfil", 1);
        tMap.put("typeMAJProfil", 1);
        tMap.put("requestSource", 6);
        experianRequestItemProperties = Collections.unmodifiableMap(tMap);
    }

    /* Newsletter enumerated values to codes */
    private static final Map<String, Integer> VALUES_TO_CODES;
    static {
        Map<String, Integer> map = new HashMap<String, Integer>();
        // civilite
        map.put("miss", 3);
        map.put("mrs", 2);
        map.put("mr", 1);
        map.put("organization", 5);

        // receiveEmail, reseiveOffers
        map.put("", 8);
        map.put("true", 0);
        map.put("false", 1);

        // nbPersonnes
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        map.put("4 and more", 4);

        // proprietaire_locataire
        map.put("Owner", 0);
        map.put("Tenant", 1);

        VALUES_TO_CODES = Collections.unmodifiableMap(map);
    }

    /* Newsletter enumerated values to codes */
    private static final Map<String, String> BOOLEAN_TO_CODES;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("", "");
        map.put("true", "1");
        map.put("false", "0");
        BOOLEAN_TO_CODES = Collections.unmodifiableMap(map);
    }

    public static List<Object> createValuesList(Abonnement abonnement, String source, Repository profileRepository)
            throws RepositoryException {
        String profileId = abonnement.getProfile();
        Profile profile = Profile.getInstance(profileRepository, profileId, DESCRIPTOR_NAME_PROFILE);
        RepositoryItem contactItem = null;
        if (profile != null) {
            contactItem = (RepositoryItem) profile.getPropertyValue("billingAddress");
        }
        RepositoryItem newsletterItem = abonnement.getRepositoryItem();
        Iterator<String> it = experianRequestItemProperties.keySet().iterator();
        List<Object> propertyValuesList = new ArrayList<Object>(33);
        while (it.hasNext()) {
            boolean isSettedVar = false;
            String key = it.next();
            int type = experianRequestItemProperties.get(key);
            try {
                /*
                 * 1 - profile 2 - contactInfo 3 - newsletter
                 */
                switch (type) {
                    case 1: {
                        if (profile != null) {
                            propertyValuesList.add(profile.getPropertyValue(key));
                            isSettedVar = true;
                        } else {
                            if (abonnement.getEmail() != null && key.equalsIgnoreCase(EMAIL_LOGIN)) {
                                propertyValuesList.add(abonnement.getEmail());
                                isSettedVar = true;
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (contactItem != null) {
                            propertyValuesList.add(contactItem.getPropertyValue(key));
                            isSettedVar = true;
                        }
                        break;
                    }
                    case 3: {
                        propertyValuesList.add(newsletterItem.getPropertyValue(key));
                        isSettedVar = true;
                        break;
                    }
                    case 4: {
                        propertyValuesList.add(newsletterItem.getPropertyValue("id_magasin_ref") != null);
                        isSettedVar = true;
                        break;
                    }
                    case 5: {
                        Object referenceObj = newsletterItem.getPropertyValue("id_magasin_ref");
                        if (referenceObj == null) {
                            break;
                        }
                        RepositoryItem reference = null;
                        String temp = null;
                        if (referenceObj instanceof RepositoryItem) {
                            reference = (RepositoryItem) referenceObj;
                            temp = reference.getRepositoryId();
                        }
                        propertyValuesList.add(temp != null ? temp : new Object());
                        isSettedVar = true;
                        break;
                    }
                    case 6: {
                        propertyValuesList.add(source);
                        isSettedVar = true;
                        break;
                    }
                }
                if (!isSettedVar) {
                    propertyValuesList.add(null);
                }
            } catch (Throwable e) {
                // TODO delete before install to production
                // propertyValuesList.add(new Object());
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(e);
                }
            }
        }
        return propertyValuesList;
    }

    public static void createAndAddToRepository(List<Object> properties, Repository repository)
            throws RepositoryException {
        if (ExperianUtils.experianRequestItemProperties.size() != properties.size()) {
            StringBuffer sb = new StringBuffer("Incorrect size of list. List must have size = ");
            sb.append(ExperianUtils.experianRequestItemProperties.size());
            sb.append(", but not ");
            sb.append(properties.size());
            sb.append(".");
            throw new RuntimeException(sb.toString());
        }
        ;
        MutableRepositoryItem mri = ((MutableRepository) repository).createItem("experianRequest");
        Iterator<String> it = ExperianUtils.experianRequestItemProperties.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            try {
                String temp = it.next();
                mri.setPropertyValue(temp, properties.get(i));
                // throw new NullPointerException("Test exception");
            } catch (Exception ex) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error(ex);
                }
            }
            i++;
        }
        ((MutableRepository) repository).addItem(mri);
    }

    public static List<Object> getValuesList(RepositoryItem experianRequest) throws RepositoryException {
        RepositoryItemDescriptor itemDescriptor = getExperianRequestIremDescriptor(experianRequest);
        String[] propertyNames = itemDescriptor.getPropertyNames();
        List<Object> temp = new ArrayList<Object>();
        for (int i = 0; i < propertyNames.length; i++) {
            Object value = experianRequest.getPropertyValue(propertyNames[i]);
            temp.add(value);
        }
        return temp;
    }

    public static String repositoryItemToCSVLine(RepositoryItem experianRequest) throws RepositoryException {
        getExperianRequestIremDescriptor(experianRequest);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PROPERTY_NAMES.length; i++) {
            String propertyName = PROPERTY_NAMES[i];
            Object propertyValue = experianRequest.getPropertyValue(propertyName);
            String printValue = "";
            if (propertyValue != null) {

                if (ENUM_PROPERTIES.contains(propertyName)) {
                    printValue = VALUES_TO_CODES.get(propertyValue).toString();
                } else {
                    if (ExperianUtils.BOOLEAN_PROPERTIES.contains(propertyName)) {
                        printValue = BOOLEAN_TO_CODES.get(propertyValue.toString());
                    } else {
                        if (propertyValue instanceof Date) {
                            Date dateValue = (Date) propertyValue;
                            printValue = Constants.DATE_FORMAT_EXPERIAN.format(dateValue);
                        } else {
                            printValue = propertyValue.toString();
                        }
                    }
                }
            }

            if (i != 0) {
                sb.append(ExperianUtils.DATA_FILE_DELIMETER);
            }

            sb.append(printValue);
        }
        return sb.toString();
    }

    static public int printToDataFile(File file, RepositoryItem[] requestsList) throws RepositoryException,
            FileNotFoundException {
        if (file == null || requestsList == null || requestsList.length == 0) {
            // TODO don't forget replace REpository exception by
            // IllegalArgumentException and remove throws declaration
            throw new RepositoryException("Repository item must be a newsletter account type.");
        }

        int recordsExported = 0;
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file);
            printWriter.println(getColumnNamesLine());
            MutableRepository mutNewsletterRepository = (MutableRepository) requestsList[0].getRepository();

            for (int i = 0; i < requestsList.length; i++) {
                RepositoryItem request = requestsList[i];

                // write record to file
                printWriter.println(repositoryItemToCSVLine(request));

                // move record to history

                MutableRepositoryItem historyNewsletter = mutNewsletterRepository
                        .createItem(HISTORY_NEWSLETTER_ITEM_DESCRIPTOR);

                for (String propertyName : ExperianUtils.PROPERTY_NAMES) {
                    historyNewsletter.setPropertyValue(propertyName, request.getPropertyValue(propertyName));
                }
                historyNewsletter.setPropertyValue(EXPORT_DATE_PROPERTY, new Date());
                historyNewsletter.setPropertyValue(REQUEST_SOURCE_PROPERTY, request.getPropertyValue(REQUEST_SOURCE_PROPERTY));

                mutNewsletterRepository.addItem(historyNewsletter);

                mutNewsletterRepository.removeItem(requestsList[i].getRepositoryId(),
                        EXPERIAN_REQUEST_ITEM_DESCRIPTOR_NAME);

                recordsExported++;
            }
        } finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter = null;
            }
        }

        return recordsExported;
    }

    private static String getColumnNamesLine() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DATA_FILE_COLUMN_NAMES.length; i++) {
            String columnName = DATA_FILE_COLUMN_NAMES[i];
            if (i != 0) {
                sb.append(ExperianUtils.DATA_FILE_DELIMETER);
            }
            sb.append(columnName);
        }
        return sb.toString();
    }

    private static RepositoryItemDescriptor getExperianRequestIremDescriptor(RepositoryItem experianRequest)
            throws RepositoryException {
        RepositoryItemDescriptor itemDescriptor = null;
        if (experianRequest == null
                || !(itemDescriptor = experianRequest.getItemDescriptor()).getItemDescriptorName().equals(
                        EXPERIAN_REQUEST_ITEM_DESCRIPTOR_NAME)) {
            // TODO don't forget replace REpository exception by
            // IllegalArgumentException and remove throws declaration
            throw new RepositoryException("Repository item must be a newsletter account type.");
        }
        return itemDescriptor;
    }

}
