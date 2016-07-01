package com.castorama.commerce.profile;

/**
 * Helper class: contains profile and newsletters repositories property names
 * and message codes.
 *
 * @author Katsiaryna_Dmitrievich
 */
public class Constants {
    /** Profile Resource bundle name. */
    public static final String RESOURCE_BUNDLE = "com.castorama.commerce.profile.CastProfileResources";

    /** Profile repository property names. */

    /** First Name Profile field name. */
    public static final String FIRST_NAME_PROFILE_PROP = "firstName";

    /** Last Name Profile field name. */
    public static final String LAST_NAME_PROFILE_PROP = "lastName";

    /** Login field name. */
    public static final String LOGIN_PROFILE_PROP = "login";

    /** Email field name. */
    public static final String EMAIL_PROFILE_PROP = "email";

    /** Password field name. */
    public static final String PASSWORD_PROFILE_PROP = "password";

    /** Confirmation password field name. */
    public static final String CONFIRM_PASSWORD_PROFILE_PROP = "confirmPassword";

    /** Title field name. */
    public static final String TITLE_PROFILE_PROP = "civilite";

    /** Castorama Card Number field name. */
    public static final String CAST_CARD_NUMBER_PROFILE_PROP = "numeroCarteAtout";
    
    /** Castorama Card field name. */
    public static final String CARTE_ATOUT_PROFILE_PROP = "carteAtout";

    /** Profile update type property. */
    public static final String UPDATE_TYPE_PROFILE_PROP = "typeMAJprofil";

    /** Flag for bespoke user. */
    public static final String FO_USER_PROFILE_PROP = "estUtilisateurFo";

    /** Profile's Group property. */
    public static final String LIBELLE_GROUP_PROFILE_PROP = "libelle";

    /** Profiles Groups property name. */
    public static final String GROUPS_PROFILE_PROP = "groups";

    /** Profile descriptor name constant. */
    public static final String DESCRIPTOR_NAME_PROFILE = "user";

    /** AUTO_LOGIN_PROFILE_PROP property */
    public static final String AUTO_LOGIN_PROFILE_PROP = "autoLogin";

    /** Home Id property */
    public static final String HOME_ID = "homeId";

    /** Active property (Flag to determinate the validity of the Castorama card). */
    public static final String IS_ACTIVE = "active";

    /** Sofinco Account property */
    public static final String SOFINCO_ACCOUNT = "sofincoAccount";

    /** Sofinco Name1 property */
    public static final String SOFINCO_NAME1 = "sofincoName1";

    /** Sofinco Name2 property */
    public static final String SOFINCO_NAME2 = "sofincoName2";

    /** Current Local Store field name. */
    public static final String CURRENT_LOCAL_STORE_PROFILE_PROP = "currentLocalStore";

    /** Current Local Store field name. */
    public static final String WRAPPED_LOCAL_STORE_PROFILE_PROP = "wrappedCurrentLocalStore";

    /** Contact Info property names. */

    /** Billing Address field name. */
    public static final String BILLING_ADDRESS = "billingAddress";

    /** Shipping Address field name. */
    public static final String SHIPPING_ADDRESS = "shippingAddress";

    /** First Name Address field name. */
    public static final String FIRST_NAME_ADDRESS_PROP = "firstName";

    /** Last Name Address field name. */
    public static final String LAST_NAME_ADDRESS_PROP = "lastName";

    /** Locality field name. */
    public static final String LOCALITY_ADDRESS_PROP = "locality";

    /** Address1 field name. */
    public static final String ADDRESS_1_ADDRESS_PROP = "address1";

    /** Address2 field name. */
    public static final String ADDRESS_2_ADDRESS_PROP = "address2";

    /** Address3 field name. */
    public static final String ADDRESS_3_ADDRESS_PROP = "address3";

    /** City field name. */
    public static final String CITY_ADDRESS_PROP = "city";

    /** Postal Code field name. */
    public static final String POSTAL_CODE_ADDRESS_PROP = "postalCode";

    /** Country field name. */
    public static final String COUNTRY_ADDRESS_PROP = "country";

    /** State field name. */
    public static final String STATE_ADDRESS_PROP = "state";
    
    /** Telephone fixe field name. */
    public static final String TELEPHONE_FIXE_ADDRESS_PROP = "telephone_telform";
    
    /** Phone number field name. */
    public static final String PHONE_NUMBER_ADDRESS_PROP = "phoneNumber";

    /** Phone number 2 field name. */
    public static final String PHONE_NUMBER_2_ADDRESS_PROP = "phoneNumber2";

    /** address 1 field name. */
    public static final String ADDRESS_1_PROP = "address1";

    /** address 2 field name. */
    public static final String ADDRESS_2_PROP = "address2";

    /** address 3 field name. */
    public static final String ADDRESS_3_PROP = "address3";

    /** locality field name. */
    public static final String LOCALITY_PROP = "locality";

    /** Prefix field name. */
    public static final String PREFIX_ADDRESS_PROP = "prefix";

    /** Defaoul country name. */
    public static final String DEFAULT_COUNTRY_NAME = "France";

    /** Newsletter repository property names. */

    /** Newsletter item descriptor name. */
    public static final String NEWSLETTER_ITEM_DESC = "abonnementNewsletter";
    
    /** Receive offers field name. */
    public static final String RESEIVEOFFERS_NEWSLETTER_PROP = "RESEIVEOFFERS";

    /** Receive newsletters field name. */
    public static final String RECEIVEEMAIL_NEWSLETTER_PROP = "RECEIVEEMAIL";

    /** Accept recontact field name. */
    public static final String ACCEPTRECONTACT_NEWSLETTER_PROP = "ACCEPTRECONTACT";

    /** Email field name. */
    public static final String EMAIL_NEWSLETTER_PROP = "EMAIL";

    /** accesPartenairesCasto property name. */
    public static final String ACCES_PARTENAIRES_CASTO_NEWSLETTER_PROP = "accesPartenairesCasto";

    /** Preffered store property. */
    public static final String PREF_STORE_NEWSLETTER_PROP = "id_magasin_ref";

    /** Date modification field name. */
    public static final String DATE_DERNIERE_MODIF_NEWSLETTER_PROP = "dateDerniereModification";

    /** Profile field name. */
    public static final String PROFILE_ID_FIELD_NEWSLETTER_PROP = "profile";

    /** Date of Birth field name. */
    public static final String DATE_OF_BIRTH_NEWSLETTER_PROP = "dateOfBirth";

    /** Date subscription field name. */
    public static final String DATE_SUBSCRIBE_NEWSLETTER_PROP = "dateInscription";

    /** Date subscription field name. */
    public static final String DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP = "dateOffersInscription";

    /** Date on unsubscribe on offers from Castorama partners. */
    public static final String DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP = "dateOffersDesincrption";

    /** Date on unsubscribe on newsletters from Castorama. */
    public static final String DATE_UNSUBSCRIBE_NEWSLETTER_PROP = "dateDesincrption";

    /** Appartament field property name. */
    public static final String APPARTEMENT_NEWSLETTER_PROP = "appartement";

    /** Maison field property name. */
    public static final String MAISON_NEWSLETTER_PROP = "maison";

    /** Jardin field property name. */
    public static final String JARDIN_NEWSLETTER_PROP = "jardin";

    /** Owner status field property name. */
    public static final String PROPRIETAIRE_NEWSLETTER_PROP = "proprietaire_locataire";

    /** Maison Campagne field property name. */
    public static final String MAISON_CAMPAGNE_NEWSLETTER_PROP = "maisonCampagne";

    /** Carte Atout field property name. */
    public static final String CARTE_ATOUT_NEWSLETTER_PROP = "carteAtout";

    /** Number of person in house field property name. */
    public static final String NB_PERSONNES_ATOUT_NEWSLETTER_PROP = "nbPersonnes";

    /** Catalog Reservation Repository Field Names. */

    /** Phone 2 field name in Catalogue Reservation Repository. */
    public static final String PHONE_NUMBER_2_RESERVATION_FIELD = "phoneNumber2";

    /** Phone field name in Catalogue Reservation Repository. */
    public static final String PHONE_NUMBER_RESERVATION_FIELD = "phoneNumber1";

    /** Country field name in Catalogue Reservation Repository. */
    public static final String COUNTRY_RESERVATION_FIELD = "pays";

    /** City field name in Catalogue Reservation Repository. */
    public static final String CITY_RESERVATION_FIELD = "ville";

    /** Postal Code field name in Catalogue Reservation Repository. */
    public static final String POSTAL_CODE_RESERVATION_FIELD = "codePostal";

    /** Address 4 field name in Catalogue Reservation Repository. */
    public static final String ADDRESS_4_RESERVATION_FIELD = "adresse4";

    /** Address 3 field name in Catalogue Reservation Repository. */
    public static final String ADDRESS_3_RESERVATION_FIELD = "adresse3";

    /** Address 2 field name in Catalogue Reservation Repository. */
    public static final String ADDRESS_2_RESERVATION_FIELD = "adresse2";

    /** Address field name in Catalogue Reservation Repository. */
    public static final String ADDRESS_1_RESERVATION_FIELD = "adresse";

    /** Last Name field name in Catalogue Reservation Repository. */
    public static final String LAST_NAME_RESERVATION_FIELD = "nom";

    /** First Name field name in Catalogue Reservation Repository. */
    public static final String FIRST_NAME_RESERVATION_FIELD = "prenom";

    /** Title field name in Catalogue Reservation Repository. */
    public static final String TITLE_RESERVATION_FIELD = "civilite";

    /** Reservation date field name in Catalogue Reservation Repository. */
    public static final String DATE_RESERVATION_FIELD = "DATERESERVATION";

    /** Email field name in Catalogue Reservation Repository. */
    public static final String EMAIL_RESERVATION_FIELD = "EMAIL";

    /** Reservation catalog descriptor name. */
    public static final String DESCRIPTOR_RESERVATION_NAME = "reservationCatalogue";

    /** Session properties. */

    /** Session property name for onBefalf functionality. */
    public static final String ADMIN_PASSWORD_SESSION_PARAM = "adminPassword";

    /** Session property name adminLogin. */
    public static final String ADMIN_LOGIN_SESSION_PARAM = "adminLogin";

    /** Role names. */

    /** Admin role name. */
    public static final String ADMIN_ROLE = "Admin_Habilitations_Utilisateurs";

    /** Statistics Repository properties. */

    /** CONNEXION_STATISTICS_PROP property */
    public static final String CONNEXION_STATISTICS_PROP = "connexion";

    /** NAVIGATEUR_STATISTICS_PROP property */
    public static final String NAVIGATEUR_STATISTICS_PROP = "navigateur";

    /** SYSTEME_EXPLOITATION_STATISTICS_PROP property */
    public static final String SYSTEME_EXPLOITATION_STATISTICS_PROP = "systemeExploitation";

    /** MESSAGE_STATISTICS_PROP property */
    public static final String MESSAGE_STATISTICS_PROP = "message";

    /** QUESTION_STATISTICS_PROP property */
    public static final String QUESTION_STATISTICS_PROP = "question";

    /** MAGASIN_STATISTICS_PROP property */
    public static final String MAGASIN_STATISTICS_PROP = "magasin";

    /** REFERENCE_PRODUIT_STATISTICS_PROP property */
    public static final String REFERENCE_PRODUIT_STATISTICS_PROP = "referenceProduit";

    /** DATE_ENREGISTREMENT_STATISTICS_PROP property */
    public static final String DATE_ENREGISTREMENT_STATISTICS_PROP = "dateEnregistrement";

    /** TELEPHONE_STATISTICS_PROP property */
    public static final String TELEPHONE_STATISTICS_PROP = "telephone";

    /** CODE_POSTAL_STATISTICS_PROP property */
    public static final String CODE_POSTAL_STATISTICS_PROP = "codePostal";

    /** VILLE_STATISTICS_PROP property */
    public static final String VILLE_STATISTICS_PROP = "ville";

    /** ADRESSE_STATISTICS_PROP property */
    public static final String ADRESSE_STATISTICS_PROP = "adresse";

    /** EMAIL_STATISTICS_PROP property */
    public static final String EMAIL_STATISTICS_PROP = "email";

    /** PRENOM_STATISTICS_PROP property */
    public static final String PRENOM_STATISTICS_PROP = "prenom";

    /** NOM_STATISTICS_PROP property */
    public static final String NOM_STATISTICS_PROP = "nom";

    /** CIVILITE_STATISTICS_PROP property */
    public static final String CIVILITE_STATISTICS_PROP = "civilite";

    /** PROFILE_ID_STATISTICS_PROP property */
    public static final String PROFILE_ID_STATISTICS_PROP = "profileId";

    /** MOTIF_STATISTICS_PROP property */
    public static final String MOTIF_STATISTICS_PROP = "motif";

    /** TYPE_STATISTICS_PROP property */
    public static final String TYPE_STATISTICS_PROP = "type";

    /** STATISTICS_REPOSITORY_DESCRIPTOR_NAME property */
    public static final String STATISTICS_REPOSITORY_DESCRIPTOR_NAME = "StatisticsFAQRepository";

    /** Motif repository properties. */

    /** FORM_TYPE_MOTIF_PROP property */
    public static final String FORM_TYPE_MOTIF_PROP = "formType";

    /** CONTACT_US_MOTIF_ITEM constant */
    public static final String MOTIF_DESCRIPTOR_NAME = "ContactUsMotif";

    /** Local price list profile property name. */
    public static final String LOCAL_PRICE_LIST = "localPriceList";

    /**First name property using on registration page**/
    public static final String FIRST_NAME_FORM_PROPERTY = "firstname";

    /**Last name property using on registration page**/
    public static final String LAST_NAME_FORM_PROPERTY = "lastname";

    /**Last name property using on registration page**/
    public static final String POST_CODE_FORM_PROPERTY = "postcode";

    /**Address1 label using for error messages**/
    public static final String ADDRESS_1_LABEL = "N°, voie";

    /**Address2 label using for error messages**/
    public static final String ADDRESS_2_LABEL = "Étage, appartement";

    /**Address3 label using for error messages**/
    public static final String ADDRESS_3_LABEL = "Bâtiment";

    /**Locality label using for error messages**/
    public static final String LOCALITY_LABEL = "Lieu-dit";

    /**City label using for error messages**/
    public static final String CITY_LABEL = "Ville";

    /**First name label using for error messages**/
    public static final String LAST_NAME_LABEL = "Nom";

    /**Last name label using for error messages**/
    public static final String FIRST_NAME_LABEL = "Prénom";

}