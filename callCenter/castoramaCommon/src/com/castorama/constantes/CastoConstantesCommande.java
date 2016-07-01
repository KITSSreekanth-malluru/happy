package com.castorama.constantes;

/**
 * @author Florte Jérémy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe de constantes pour l'application Castorama, pour la partie commande.
 */
public class CastoConstantesCommande
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Emplacement du fichier de resources pour les commandes.
     */
    public static final String BUNDLE_COMMANDES = "com.castorama.commande.CommandeResources";

    /**
     * Emplacement du fichier de resources pour les mails de suivi niveau
     * CastoramaBO .
     */
    public static final String BUNDLE_COMMANDES_MAILS = "com.castorama.commande.MailsResources";

    /**
     * . Message coupon expire
     */
    public static final String BUNDLE_COUPON_EXPIRE = "coupon.expire";

    /**
     * . Message coupon expire
     */
    public static final String BUNDLE_COUPON_NON_CUMULABLE = "coupon.non.cumulable";
    
    /**
     * . Message coupon non applicable CD/PLD/CD&PLD
     */
    public static final String BUNDLE_COUPON_MAUVAIS_TYPAGE = "coupon.non.typage";

    /**
     * . Message coupon pas encore actif
     */
    public static final String BUNDLE_COUPON_PAS_ENCORE = "coupon.pasencore";

    /**
     * . Message coupon d�j� utilis�
     */
    public static final String BUNDLE_COUPON_DEJA_UTILISE = "coupon.deja.utilise";

    /**
     * D�signe une civilit� manquante sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_CIVILITE_FACT = "creation.compte.facturation.civiliteManquante";

    /**
     * D�signe un nom manquant sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_NOM_FACT = "creation.compte.facturation.nomManquant";

    /**
     * D�signe un pr�nom manquant sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_PRENOM_FACT = "creation.compte.facturation.prenomManquant";

    /**
     * D�signe une adresse manquante sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADRESSE_FACT = "creation.compte.facturation.adresseManquante";

    /**
     * D�signe un code postal manquant sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_CP_FACT = "creation.compte.facturation.cpManquant";

    /**
     * D�signe une ville manquante sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_VILLE_FACT = "creation.compte.facturation.villeManquante";

    /**
     * D�signe un n� de t�l�phone manquant sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_TEL_FACT = "creation.compte.facturation.telManquant";

    /**
     * D�signe une adresse email inccorecte sur l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_EMAIL_FACT = "creation.compte.facturation.emailIncorrect";

    /**
     * D�signe un n� de carte atout manquant.
     */
    public static final String BUNDLE_CREATION_COMPTE_CARTE_FACT = "creation.compte.facturation.numCarteAtoutManquant";

    /**
     * Désigne une n° de téléphone d'alerte manquant.
     */
    public static final String BUNDLE_CREATION_COMPTE_ALERTE_FACT = "creation.compte.facturation.numAlertemanquant";

    /**
     * D�signe une civilit� manquante sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_CIVILITE_LIVR = "creation.compte.livraison.civiliteManquante";

    /**
     * D�signe un nom manquant sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_NOM_LIVR = "creation.compte.livraison.nomManquant";

    /**
     * D�signe un pr�nom manquant sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_PRENOM_LIVR = "creation.compte.livraison.prenomManquant";

    /**
     * D�signe une adresse manquante sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADRESSE_LIVR = "creation.compte.livraison.adresseManquante";

    /**
     * D�signe un code postal manquant sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_CP_LIVR = "creation.compte.livraison.cpManquant";

    /**
     * D�signe une ville manquante sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_VILLE_LIVR = "creation.compte.livraison.villeManquante";

    /**
     * D�signe un n� de t�l�phone manquant sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_TEL_LIVR = "creation.compte.livraison.telManquant";

    /**
     * D�signe une adresse email inccorecte sur l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_EMAIL_LIVR = "creation.compte.livraison.emailIncorrect";

    /**
     * D�signe le surco�t pour la corse.
     */
    public static final String BUNDLE_SURCOUT_CORSE = "creation.compte.livraison.surcout.corse";

    /**
     * D�signe le message pour les livraisons dans les �les m�tropolitaines.
     */
    public static final String BUNDLE_ILES_METROPOLITAINES = "creation.compte.livraison.iles.metropolitaines";

    /**
     * D�signe le message pour les livraisons dans les DOM-TOM.
     */
    public static final String BUNDLE_DOM_TOM = "creation.compte.livraison.dom.tom";

    /**
     * D�signe le message pour indiquer que la ville ne correspond pas au code
     * postal saisi.
     */
    public static final String BUNDLE_VILLE_INTROUVABLE_FACTURATION = "creation.compte.facturation.ville.introuvable";

    /**
     * D�signe le message pour indiquer que la ville ne correspond pas au code
     * postal saisi.
     */
    public static final String BUNDLE_VILLE_INTROUVABLE_FACTURATION_BIS = "creation.compte.facturation.ville.introuvable.bis";

    /**
     * D�signe le message pour indiquer que la ville ne correspond pas au code
     * postal saisi.
     */
    public static final String BUNDLE_VILLE_INTROUVABLE_LIVRAISON = "creation.compte.livraison.ville.introuvable";

    /**
     * D�signe le message pour indiquer que la ville ne correspond pas au code
     * postal saisi.
     */
    public static final String BUNDLE_VILLE_INTROUVABLE_LIVRAISON_BIS = "creation.compte.livraison.ville.introuvable.bis";

    /**
     * D�signe le message pour indiquer qu'on ne peut pas livrer dans le pays
     * indiqu�.
     */
    public static final String BUNDLE_PAYS_NON_LIVRABLE = "creation.compte.livraison.pays.non.livrable";

    /**
     * Repr�sente le d�but d'un code postal corse.
     */
    public static final String CP_CORSE = "20";

    /**
     * Repr�sente les codes postaux pour les �les m�tropolitaines.
     */
    public static final String[] CP_ILES_METROPOLITAINES =
    { "17123", "22560", "22870", "29242", "29253", "29259", "29900", "29980", "29990", "50100", "50400", "50550",
            "56170", "56360", "56590", "56780", "56840", "83400", "85350", };

    /**
     * Repr�sente les codes postaux pour les DOM-TOM.
     */
    public static final String[] CP_DOM_TOM =
    { "00", "96", "97", "99", "981", "982", "983", "984", "985", "986", "987", "988", "989" };

    /**
     * Propri�t� "civilite" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_CIVILITE = "civilite";

    /**
     * Propri�t� "lastName" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_NOM = "lastName";

    /**
     * Propri�t� "firstName" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_PRENOM = "firstName";

    /**
     * Propri�t� "societe" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_SOCIETE = "societe";

    /**
     * Propri�t� "address1" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_ADDRESS_1 = "address1";

    /**
     * Propri�t� "address2" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_ADDRESS_2 = "address2";

    /**
     * Propri�t� "address3" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_ADDRESS_3 = "address3";

    /**
     * Propri�t� "postalCode" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_CODE_POSTAL = "postalCode";

    /**
     * Propri�t� "city" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_VILLE = "city";

    /**
     * Propri�t� "country" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_PAYS = "country";

    /**
     * Propri�t� "phoneNumber" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_TEL = "phoneNumber";

    /**
     * Propri�t� "telephonePro" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_TEL_PRO = "telProfessionnel";

    /**
     * Propri�t� "gsm" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_TEL_PORTABLE = "telPortable";

    /**
     * Propri�t� "faxNumber" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_FAX = "faxNumber";

    /**
     * Propri�t� "email" pour les adresse de facturation/livraison.
     */
    public static final String PROPERTY_EMAIL = "email";

    /**
     * Propri�t� "tvaIntracommunautaire" pour les adresse de
     * facturation/livraison.
     */
    public static final String PROPERTY_TVA_INTRACOMMUNAUTAIRE = "tvaIntracommunautaire";

    /**
     * Propri�t� "messageTransporteur" pour la commande.
     */
    public static final String PROPERTY_MESSAGE_TRANSPORTEUR = "messageTransporteur";

    /* 1327: vérification longueur des champs dans les formulaires du cycle de commande */
    /**
     * nom trop long dans l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_NOM_FACT_LENGTH = "creation.compte.facturation.nomTropLong";

    /**
     * prenom trop long dans l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_PRENOM_FACT_LENGTH = "creation.compte.facturation.prenomTropLong";
    
    /**
     * societe trop long dans l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_SOCIETE_FACT_LENGTH = "creation.compte.facturation.societeTropLong";

    /**
     * adresse 1 trop long dans l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADDRESS_1_FACT_LENGTH = "creation.compte.facturation.adresse1TropLong";
    
    /**
     * adresse 2 trop long dans l'adresse de facturation.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADDRESS_2_FACT_LENGTH = "creation.compte.facturation.adresse2TropLong";

    /**
     * nom trop long dans l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_NOM_LIVR_LENGTH = "creation.compte.livraison.nomTropLong";

    /**
     * prenom trop long dans l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_PRENOM_LIVR_LENGTH = "creation.compte.livraison.prenomTropLong";
    
    /**
     * societe trop long dans l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_SOCIETE_LIVR_LENGTH = "creation.compte.livraison.societeTropLong";

    /**
     * adresse 1 trop long dans l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADDRESS_1_LIVR_LENGTH = "creation.compte.livraison.adresse1TropLong";

    /**
     * adresse 2 trop long dans l'adresse de livraison.
     */
    public static final String BUNDLE_CREATION_COMPTE_ADDRESS_2_LIVR_LENGTH = "creation.compte.livraison.adresse2TropLong";

    /**
     * commentaire(cadeau) trop long.
     */
    public static final String BUNDLE_CREATION_COMPTE_COMMENTAIRE = "creation.compte.commentaireCadeauTropLong";

    /**
     * message transporteur trop long.
     */
    public static final String BUNDLE_CREATION_COMPTE_MESSAGE_TRANSPORTEUR = "creation.compte.messageTransporteurTropLong";

    /**
     * Longueur maximale de la propriete 'nom'.
     */
    public static int LENGTH_NOM = 40;

    /**
     * Longueur maximale de la propriete 'prenom'.
     */
    public static int LENGTH_PRENOM = 40;

    /**
     * Longueur maximale de la propriete 'societe'.
     */
    public static int LENGTH_SOCIETE = 40;
    /**
     * Longueur maximale de la propriete 'address1'.
     */
    public static int LENGTH_ADDRESS_1 = 100;
    
    /**
     * Longueur maximale de la propriete 'address2'.
     */
    public static int LENGTH_ADDRESS_2 = 100;
    
    /**
     * Longueur maximale de la propriete commentaire (cadeau).
     */
    public static final int LENGTH_COMMENTAIRE = 200;
    
    /**
     * Longueur maximale de la propriete message transporteur.
     */
    public static final int LENGTH_MESSAGE_TRANSPORTEUR = 500;
    /* fin 1327 */
    
    /**
     * Propri�t� "optionPaiementAtout" pour la commande.
     */
    public static final String PROPERTY_OPTION_PAIEMENT_ATOUT = "optionPaiementAtout";

    /**
     * D�signe propri�t� "commandecadeau" pour la commande.
     */
    public static final String PROPERTY_COMMANDE_CADEAU = "commandecadeau";
    
    
    /* Projet Castorama - La Défense *** logica */
    /**
     * D�signe propri�t� "CarteAtoutIndetermine" pour la commande.
     */
    public static final String PROPERTY_CARTE_ATOUT_INDETERMINE = "CarteAtoutIndetermine";
    /* Fin Projet Castorama - La Défense */
    

    /**
     * D�signe propri�t� "Commentaire" pour la commande.
     */
    public static final String PROPERTY_COMMENTAIRE = "Commentaire";

    /**
     * D�signe propri�t� "Commentaire" pour la commande.
     */
    public static final String NUMERO_CARTE_ATOUT = "numCarteAtout";

    /**
     * D�signe propri�t� "submittedDate" pour la commande.
     */
    public static final String SUBMITTED_DATE = "submittedDate";

    /**
     * D�signe propri�t� "PayeCarteAtout" pour la commande.
     */
    public static final String PROPERTY_PAYE_CARTE_ATOUT = "PayeCarteAtout";

    /**
     * Désigne un ajout d'article principal.
     */
    public static final int TYPE_ARTICLE_PRINCIPAL = 1;

    /**
     * Désigne un ajout d'article secondaire.
     */
    public static final int TYPE_ARTICLE_SECONDAIRE = 2;

    /**
     * Désigne un ajout de pack.
     */
    public static final int TYPE_ARTICLE_PACK = 3;

    /**
     * Propriété "alerteAutomatique" d'un PaymentGroup.
     */
    public static final String PROPERTY_ALERTE_AUTOMATIQUE = "alerteAutomatique";

    /**
     * Propriété "moyenContact" d'un PaymentGroup.
     */
    public static final String PROPERTY_MOYEN_CONTACT = "moyenContact";

    /**
     * Propriété "JSessionID" de la commande.
     */
    public static final String PROPERTY_JSESSION_ID = "JSessionID";

    /**
     * Propriété "idTransaction" de la commande.
     */
    public static final String ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX = "idTransaction";

    /**
     * Propriété "numAutorisation" de la commande.
     */
    public static final String ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX = "numAutorisation";

    /**
     * Propriété "numTransaction" de la commande.
     */
    public static final String ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX = "numTransaction";

    /**
     * Propriété "dateTransaction" de la commande.
     */
    public static final String ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX = "dateTransaction";

    /**
     * Propriété "datePaiement" de la commande.
     */
    public static final String ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX = "datePaiement";

    /**
     * Propriété "dateValidAtout" de la commande.
     */
    public static final String ORDER_PROPERTY_DATE_EXPIRATION_ATOUT = "dateValidAtout";

    /**
     * Délai de paiement paybox.
     */
    public static final int DELAI_PAIEMENT_PAYBOX = 7;

    /**
     * Propriété "paymentMethod" d'un groupe de paiement.
     */
    public static final String PAYMENT_GROUP_PAYMENT_METHOD = "paymentMethod";

    /**
     * Propriété "montantTotalCommandeTTC" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_TOTAL_COMMANDE_TTC = "montantTotalCommandeTTC";

    /**
     * Propriété "montantFraisLivraisonNonRemise" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_FRAIS_LIVRAISON_NON_REMISE = "montantFraisLivraisonNonRemise";

    /**
     * Propriété "montantFraisMonteeEtageNonRemise" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_FRAIS_MONTEE_ETAGE_NON_REMISE = "montantFraisMonteeEtageNonRemise";

    /**
     * Propriété "montantRemiseLivraisonEtMonteeEtage" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_REMISE_LIVRAISON_ET_MONTEE_ETAGE = "montantRemiseLivraisonEtMonteeEtage";

    /**
     * Propriété "montantFraisDePreparationNonRemise" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_NON_REMISE = "montantFraisDePreparationNonRemise";
    
    /**
     * Propriété "montantFraisDePreparationRemise" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_REMISE = "montantFraisDePreparationRemise";
    
    /**
     * Propriété "montantRemiseTotal" de la commande.
     */
    public static final String ORDER_PROPERTY_MONTANT_REMISE_TOTAL = "montantRemiseTotal";
    
    /**
     * D�signe propri�t� "montantPFL" pour la commande.
     */
    public static final String MONTANT_PFL = "montantPFL"; 
    
    /**
     * D�signe propri�t� "montantPFT" pour la commande.
     */
    public static final String MONTANT_PFT = "montantPFT";
}