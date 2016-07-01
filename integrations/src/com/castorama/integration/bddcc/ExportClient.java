/**
 * The class generate line of XML file.
 */
package com.castorama.integration.bddcc;

import static com.castorama.commerce.profile.Constants.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;


public class ExportClient extends ApplicationLoggingImpl {
    private static final String START_CLIENT = "<client>";
    private static final String END_CLIENT = "</client>";
    private static final String ID_CLIENT = "idClient";
    private static final String NOM = "nom";
    private static final String PRENOM = "prenom";
    private static final String DATE_OF_BIRTH = "dateOfBirth";
    private static final String CODE_POSTAL = "codePostal";
    private static final String VILLE = "ville";
    private static final String DATE_MAJ_PROFIL = "dateMajProfil";
    private static final String TYPE_MAJ_PROFIL = "typeMajProfil";
    private static final String REGISTRATION_DATE = "registrationDate";
    private static final String EMAIL = "email";
    private static final String CIVILITE = "civilite";
    private static final String EST_CLIENT_CASTO = "estClientCasto";
    private static final String CARTE_ATOUT = "carteAtout";
    private static final String MAGAZIN = "magasin";
    private static final String JARDIN = "jardin";
    private static final String MAISON = "maison";
    private static final String PROPRIETAIRE_LOCATAIRE = "proprietaireLocataire";
    private static final String MAISON_CAMPAGNE = "maisonCampagne";
    private static final String NB_PERSONNES = "nbPersonnes";
    private static final String DATE_INSCRIPTION = "dateInscription";
    private static final String DATE_DESINSCRIPTION = "dateDesinscription";
    private static final String DERNIER_CLIENT = "dernierClient";
    private static final String OPT_IN_FLAG = "optInFlag";
    private static final String CAST0_PART_IN_FLAG = "CastoPartOptInFlag";
    private static final String TELEPHONE1 = "Telephone1";
    private static final String TELEPHONE2 = "Telephone2";
    private static final String ADR3 = "adr3";
    private static final String ADR4 = "adr4";
    private static final String ADR5 = "adr5";
    private static final String ADR6 = "adr6";
    //Date of last e-mail extraction
    private static final String LAST_BDDCC_EXTRACTION = "lastBDDCCExtraction";

    private static String[] fields = { ID_CLIENT, NOM, PRENOM, DATE_OF_BIRTH, CODE_POSTAL, VILLE, DATE_MAJ_PROFIL,
            TYPE_MAJ_PROFIL, REGISTRATION_DATE, EMAIL, CIVILITE, EST_CLIENT_CASTO, CARTE_ATOUT, MAGAZIN, JARDIN,
            MAISON, PROPRIETAIRE_LOCATAIRE, MAISON_CAMPAGNE, NB_PERSONNES, DATE_INSCRIPTION, DATE_DESINSCRIPTION,
            OPT_IN_FLAG, CAST0_PART_IN_FLAG, TELEPHONE1, TELEPHONE2, DERNIER_CLIENT, ADR3, ADR4, ADR5, ADR6 };

    // properties
    public static final String PROP_DATE_MAJ_PROFIL = "dateMAJProfil";
    private static final String PROP_REGISTRATION_DATE = "registrationDate";

    public ExportClient() {
    }

    /**
     * Generate string for write in file.
     * @param profileItem the profile repository item
     * @param subscripItem the subscription repository item
     * @param startDate the date, null for normal mode
     * @param lastClient the boolean value indicate last client
     * @return string in xml format
     * @throws RepositoryException
     */
    public String parseClient(RepositoryItem profileItem, RepositoryItem subscripItem, Date startDate,
                              boolean lastClient, Date dateOfExtraction) throws RepositoryException {

        Map<String, Object> mapValues = new HashMap<String, Object>();
		Date dateProfile = new Date();
	    Date dateSubscription = new Date();
        if (profileItem != null) {
            mapValues.put(ID_CLIENT, profileItem.getRepositoryId());
            mapValues.put(NOM, encodeString(profileItem.getPropertyValue(LAST_NAME_PROFILE_PROP)));
            mapValues.put(PRENOM, encodeString(profileItem.getPropertyValue(FIRST_NAME_ADDRESS_PROP)));
			dateProfile = getDate(profileItem.getPropertyValue(PROP_DATE_MAJ_PROFIL));

            Object value = profileItem.getPropertyValue(BILLING_ADDRESS);
            if (value != null) {
                RepositoryItem address = (RepositoryItem) value;
                mapValues.put(CODE_POSTAL, encodeString(address.getPropertyValue(POSTAL_CODE_ADDRESS_PROP)));
                mapValues.put(VILLE, encodeString(address.getPropertyValue(CITY_ADDRESS_PROP)));
                mapValues.put(TELEPHONE1, encodeString(address.getPropertyValue(PHONE_NUMBER_ADDRESS_PROP)));
                mapValues.put(TELEPHONE2, encodeString(address.getPropertyValue(PHONE_NUMBER_2_ADDRESS_PROP)));
                mapValues.put(ADR3, encodeString(address.getPropertyValue(ADDRESS_1_PROP)));
                mapValues.put(ADR4, encodeString(address.getPropertyValue(ADDRESS_2_PROP)));
                mapValues.put(ADR5, encodeString(address.getPropertyValue(ADDRESS_3_PROP)));
                mapValues.put(ADR6, encodeString(address.getPropertyValue(LOCALITY_PROP)));
            } else {
                mapValues.put(CODE_POSTAL, null);
                mapValues.put(VILLE, null);
                mapValues.put(TELEPHONE1, null);
                mapValues.put(TELEPHONE2, null);
                mapValues.put(ADR3, null);
                mapValues.put(ADR4, null);
                mapValues.put(ADR5, null);
                mapValues.put(ADR6, null);
            }
			//mapValues.put(DATE_MAJ_PROFIL, parseDateToString(profileItem.getPropertyValue(PROP_DATE_MAJ_PROFIL)));
            mapValues.put(REGISTRATION_DATE, parseDateToString(profileItem.getPropertyValue(PROP_REGISTRATION_DATE)));
            mapValues.put(TYPE_MAJ_PROFIL, profileItem.getPropertyValue(UPDATE_TYPE_PROFILE_PROP));
            if (startDate != null) { // repeat mode
                value = profileItem.getPropertyValue(PROP_REGISTRATION_DATE);
                if (value != null) {
                    Date regDate = (Date) value;
                    if (startDate.compareTo(regDate) <= 0) {
                        mapValues.put(TYPE_MAJ_PROFIL, "0");
                    }
                }
            }

            mapValues.put(EMAIL, encodeString(profileItem.getPropertyValue(LOGIN_PROFILE_PROP)));
            mapValues.put(CIVILITE, parseCivility(profileItem.getPropertyValue(TITLE_PROFILE_PROP)));
        } else {
            mapValues.put(ID_CLIENT, null);
            mapValues.put(NOM, null);
            mapValues.put(PRENOM, null);

            mapValues.put(CODE_POSTAL, null);
            mapValues.put(VILLE, null);
            mapValues.put(TELEPHONE1, null);
            mapValues.put(TELEPHONE2, null);

			
            mapValues.put(REGISTRATION_DATE, null);
            mapValues.put(TYPE_MAJ_PROFIL, null);

            mapValues.put(EMAIL, null);
            mapValues.put(CIVILITE, null);
        }

        // subscription values
        if (subscripItem != null) {
			dateSubscription = getDate(subscripItem.getPropertyValue(DATE_DERNIERE_MODIF_NEWSLETTER_PROP));
            mapValues.put(DATE_OF_BIRTH, parseDateToString(subscripItem
                    .getPropertyValue(DATE_OF_BIRTH_NEWSLETTER_PROP)));
            mapValues.put(JARDIN, subscripItem.getPropertyValue(JARDIN_NEWSLETTER_PROP));

            mapValues.put(MAISON, parseMaison(subscripItem.getPropertyValue(MAISON_NEWSLETTER_PROP)));
            mapValues.put(MAISON_CAMPAGNE, subscripItem.getPropertyValue(MAISON_CAMPAGNE_NEWSLETTER_PROP));

            mapValues.put(PROPRIETAIRE_LOCATAIRE, parseProprietaireLocataire(subscripItem
                    .getPropertyValue(PROPRIETAIRE_NEWSLETTER_PROP)));
            mapValues.put(CARTE_ATOUT, subscripItem.getPropertyValue(CARTE_ATOUT_NEWSLETTER_PROP));
            mapValues.put(NB_PERSONNES, parseNmPerson(subscripItem.getPropertyValue(NB_PERSONNES_ATOUT_NEWSLETTER_PROP)));

            Object receiveEmail = subscripItem.getPropertyValue("receiveEmail");
            mapValues.put(OPT_IN_FLAG, receiveEmail);

            mapValues.put(CAST0_PART_IN_FLAG, subscripItem.getPropertyValue(RESEIVEOFFERS_NEWSLETTER_PROP));

            if (receiveEmail != null && !StringUtils.isEmpty((String) receiveEmail)) {
                mapValues.put(DATE_INSCRIPTION, parseDateToString(subscripItem
                        .getPropertyValue(DATE_SUBSCRIBE_NEWSLETTER_PROP)));
                mapValues.put(DATE_DESINSCRIPTION, parseDateToString(subscripItem
                        .getPropertyValue(DATE_UNSUBSCRIBE_NEWSLETTER_PROP)));
            } else {
                mapValues.put(DATE_INSCRIPTION, parseDateToString(subscripItem
                        .getPropertyValue(DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP)));
                mapValues.put(DATE_DESINSCRIPTION, parseDateToString(subscripItem
                        .getPropertyValue(DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP)));
            }

            Object magasinObj = subscripItem.getPropertyValue(PREF_STORE_NEWSLETTER_PROP);
            if (magasinObj != null) {
                RepositoryItem magasin = (RepositoryItem) magasinObj;
                mapValues.put(MAGAZIN, magasin.getRepositoryId());
            } else {
                mapValues.put(MAGAZIN, null);
            }
            
            Object estClientCasto = subscripItem.getPropertyValue(PREF_STORE_NEWSLETTER_PROP);
            if (estClientCasto == null) {
                mapValues.put(EST_CLIENT_CASTO, "false");
            } else {
                mapValues.put(EST_CLIENT_CASTO, "true");
            }
            
            mapValues.put(EMAIL, encodeString(subscripItem.getPropertyValue(EMAIL_NEWSLETTER_PROP)));
            //mapValues.put(DATE_MAJ_PROFIL, 
                   // parseDateToString(subscripItem.getPropertyValue(DATE_DERNIERE_MODIF_NEWSLETTER_PROP)));
        } else {
            mapValues.put(DATE_OF_BIRTH, null);
            mapValues.put(JARDIN, null);

            mapValues.put(MAISON, null);
            mapValues.put(MAISON_CAMPAGNE, null);

            mapValues.put(PROPRIETAIRE_LOCATAIRE, null);
            mapValues.put(CARTE_ATOUT, null);
            mapValues.put(NB_PERSONNES, null);

            mapValues.put(DATE_INSCRIPTION, null);
            mapValues.put(DATE_DESINSCRIPTION, null);

            mapValues.put(MAGAZIN, null);
            mapValues.put(EST_CLIENT_CASTO, null);
        }
        mapValues.put(DATE_MAJ_PROFIL, com.castorama.integration.Constants.DATE_FORMAT_BDDCC.format(dateOfExtraction));
        if (lastClient) {
            mapValues.put(DERNIER_CLIENT, "1");
        } else {
            mapValues.put(DERNIER_CLIENT, "0");
        }

        return generateXml(mapValues);
    }

    private String generateXml(Map<String, Object> mapValues) {
        StringBuilder sb = new StringBuilder();
        sb.append(START_CLIENT).append(com.castorama.integration.Constants.LINE_SEPARATOR);
        for (String key : fields) {
            sb.append(generateTag(key, mapValues.get(key)));
        }
        sb.append(END_CLIENT).append(com.castorama.integration.Constants.LINE_SEPARATOR);
        return sb.toString();
    }

    private String generateTag(String key, Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(key).append(">").append(value != null ? value : "").append("</").append(key)
                .append(">").append(com.castorama.integration.Constants.LINE_SEPARATOR);
        return sb.toString();
    }

    private String encodeString(Object value) {
        String result = null;
        if (value != null) {
            result = (String) value;
            result = result.replaceAll("&", "&amp;");
            result = result.replaceAll("<", "&lt;");
            result = result.replaceAll(">", "&gt;");
            result = result.replaceAll("\"", "&quot;");
        }
        return result;
    }

    private String parseDateToString(Object value) {
        String result = null;
        if (value != null) {
            try {
                Date date = (Date) value;
                result = com.castorama.integration.Constants.DATE_FORMAT_BDDCC.format(date);
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
        return result;
    }

private Date getDate(Object value){
	Date date=null;
	if(value != null){
		 try {
			 date = (Date) value;
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
	return date;
}
    private String parseCivility(Object value) {
        String result = null;
        if (value != null) {
            String val = (String) value;
            if (val.equals("mr"))
                result = "0";
            else if (val.equals("mrs"))
                result = "1";
            else if (val.equals("miss"))
                result = "2";
        }
        return result;
    }

    private Integer parseNmPerson(Object value) {
        Integer result = null;
        if (value != null) {
            String val = (String) value;
            if (val.equals("1"))
                result = 1;
            else if (val.equals("2"))
                result = 2;
            else if (val.equals("3"))
                result = 3;
            else if (val.equals("4 and more"))
                result = 4;
        }
        return result;
    }

    private Integer parseMaison(Object value) {
        Integer result = null;
        if (value != null) {
            Boolean val = (Boolean) value;
            if (val.booleanValue()) {
                result = 0;
            } else {
                result = 1;
            }
        }
        return result;
    }

    private Integer parseProprietaireLocataire(Object value) {
        Integer result = null;
        if (value != null) {
            String val = (String) value;
            if (val.equals("Owner"))
                result = 0;
            else if (val.equals("Tenant"))
                result = 1;
        }
        return result;
    }

	private Date getLastDate(Date dateProfile, Date dateSubscription){
		if(dateProfile==null && dateSubscription==null){
			if(isLoggingError()){
				logError("The date dernier modification is null. Writing current date");
			}
			return new Date();
		}
		if(dateProfile== null)return dateSubscription;
		if(dateSubscription==null)return dateProfile;
		return ((dateProfile.after(dateSubscription)) ? dateProfile : dateSubscription); 
	}
}
