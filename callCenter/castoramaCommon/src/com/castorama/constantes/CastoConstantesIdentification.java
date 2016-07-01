package com.castorama.constantes;

/**
 * @author Florte Jérémy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe de constantes pour l'U.C 001, identification. 
 */
/**
 * TODO commentaire Javadoc.
 *
 */
public class CastoConstantesIdentification
{
    /*
     * ------------------------------------------------------------------------ 
     * [ Constantes ] 
     * ------------------------------------------------------------------------
     */

    
    /**
     * Identifiant de la propriété pour le nombre de tentatives de connexion.
     */
    public static final String PROFILE_NB_IDENTIFICATION = "nbTentativesIdentification";
    
    /**
     * Emplacement du fichier bundle pour les interfaces de l'U.C identification.
     */
    public static final String BUNDLE_PROPRIETES_IDENTIFICATION = "com.castorama.identification.IdentificationMessageResources";
    
    /**
     * Emplacement du fichier de bundle pour le mail de rappel de mot de passe.
     */
    public static final String BUNDLE_PROPRIETES_IDENTIFICATION_RAPPEL_EMAIL = "com.castorama.identification.TemplateMailRappel";
    
    /**
     * Nom de la propriété qui désigne un login manquant dans le bundle des erreurs.
     */
    public static final String BUNDLE_LOGIN_MANQUANT = "identification.missingLogin";
    
    /**
     * Nom de la propriété qui désigne un mot de passe manquant dans le bundle des erreurs. 
     */
    public static final String BUNDLE_MOT_DE_PASSE_MANQUANT = "identification.missingPassword";
    
    /**
     * Nom de la propriété qui indique une civilité manquante lors de l'inscription.
     */
    public static final String BUNDLE_INSCRIPTION_CIVILITE_MANQUANTE = "inscription.missingCivilite";
    
    /**
     * Nom de la propriété qui indique un nom manquant lors de l'inscription.
     */
    public static final String BUNDLE_INSCRIPTION_NOM_MANQUANT = "inscription.missingLastName";
    
    /**
     * Nom de la propriété qui indique un prénom manquant lors de l'inscription.
     */
    public static final String BUNDLE_INSCRIPTION_PRENOM_MANQUANT = "inscription.missingFirstName";
    
    /**
     * Nom de la propriété qui indique un login manquant lors de l'inscription.
     */
    public static final String BUNDLE_INSCRIPTION_LOGIN_MANQUANT = "inscription.missingLogin";
    
    /**
     * Nom de la propriété qui indique une confirmation de login manquant lors de l'inscription.
     */
    public static final String BUNDLE_INSCRIPTION_CONFIRM_LOGIN_MANQUANT = "inscription.missingConfirmLogin";
    
    /**
     * Nom de la propriété qui indique que les adresses emails ne sont pas au bon format.
     */
    public static final String BUNDLE_INSCRIPTION_EMAIL_INCORRECT = "inscription.invalidLogin";
    
    /**
     * Nom de la propriété qui indique que deux emails différents ont été saisis.
     */
    public static final String BUNDLE_INSCRIPTION_EMAILS_DIFFERENTS = "inscription.differentEmails";
    
    /**
     * Nom de la propriété qui indique que le mot de passe est manquant. 
     */
    public static final String BUNDLE_INSCRIPTION_PASSWORD_MANQUANT = "inscription.missingPassword";
    
    /**
     * Nom de la propriété qui indique que la confirmation de mot de passe est manquante.
     */
    public static final String BUNDLE_INSCRIPTION_CONFIRM_PASSWORD_MANQUANT = "inscription.missingConfirmPassword";
    
    /**
     * Nom de la propriété qui indique que le mot de passe et la confirmation de mot de passe sont différents. 
     */
    public static final String BUNDLE_INSCRIPTION_PASSWORD_DIFFERENTS = "passwordsDoNotMatch";
    
    /**
     * Nom de la propriété qui indique que la carte atout est manquante.
     */
    public static final String BUNDLE_INSCRIPTION_CARTE_ATOUT_MANQUANTE = "inscription.missingCarteAtout";
    
    /**
     * Nom de la propriété qui indique que le mot de passe est trop court.
     */
    public static final String BUNDLE_INSCRIPTION_PASSWORD_TROP_COURT = "inscription.passwordToShort";
    
    /**
     * Nom de la propriété qui indique que la date de naissance est manquante.
     */
    public static final String BUNDLE_INSCRIPTION_DATE_MANQUANTE = "inscription.missingDateOfBirth";
    
    /**
     * Nom de la propriété qui indique qu'une erreur est survenue lors de l'inscription. 
     */
    public static final String BUNDLE_INSCRIPTION_ERREUR = "inscription.erreur";
    
    /**
     * Nom de la propriété qui indique que la date date doit être sur 8 caractères.
     */
    public static final String BUNDLE_NOMBRE_CARS_DATE_INCORRECT = "inscription.date8cars";
    
    /**
     * Nom de la propriété pour le texte de rappel complet.
     */
    public static final String BUNDLE_RAPPEL_EMAIL_TEXTE = "rappel.texte";
    
    /**
     * Nom de la prorpiété pour le texte de rappel sans les nom et prénom.
     */
    public static final String BUNDLE_RAPPEL_EMAIL_TEXTE_SANS_NOM = "rappel.texte.sans.nom";
    
    /**
     * Nom de la propriété pour le sujeu du mail. 
     */
    public static final String BUNDLE_RAPPEL_EMAIL_SUJET = "rappel.sujet";
    
    /**
     * Propriété qui indique la taille minimale du champ mot de passe.
     */
    public static final int LONGUEUR_MINIMALE_PASSWORD = 4;
    
    /**
     * Nombre de tentatives d'identification autorisées avant que l'internaute
     * ne soit redirigé vers la page d'oubli de mot de passe.
     */
    public static final int NOMBRE_IDENTIFICATION_AVANT_RAPPEL = 3;
    
    /**
     * Nombre de tentatives d'identification autorisées avant que l'internaute 
     * ne voit son compte bloqué.
     */
    public static final int NOMBRE_IDENTIFICATION_AVANT_COMPTE_BLOQUE = 10;
    
    /**
     * Nombre de caractères attendus pour la date de naissance.
     */
    public static final int NOMBRE_CARS_DATE_NAISSANCE = 8;
    
    /**
     * Valeur de la civilité M.
     */
    public static final String CIVILITE_MONSIEUR = "Monsieur";
    
    /**
     * Valeur de la civilité Mme.
     */
    public static final String CIVILITE_MADAME = "Madame";
    
    /**
     * Valeur de la civilité Mlle.
     */
    public static final String CIVILITE_MLLE = "Mlle";
}