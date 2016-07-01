package com.castorama.constantes;

/**
 * @author Florte J�r�my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe de constantes pour l'application Castorama
 */
public class CastoConstantes
{

    /*
     * ------------------------------------------------------------------------
     * Globales
     * ------------------------------------------------------------------------
     */

    /**
     * Descriptif pour l'entr�e dans une m�thode.
     */
    public static final String METHODE_ENTREE = "entrée : ";

    /**
     * Descriptif pour la sortie d'une m�thode.
     */
    public static final String METHODE_SORTIE = "sortie : ";

    /**
     * Identifiant de la propri�t� pour l'action de l'utilisateur.
     */
    public static final String PROPRIETE_ACTION_UTILISATEUR = "actionUtilisateur";

    /**
     * Emplacement du fichier bundle pour les messages d'erreur de la droplet
     * ErrorMessageForEach.
     */
    public static final String BUNDLE_FOREACH_ERREURS = "atg.droplet.ErrorMessageResources";

    /** . Constante statique * */
    public static final String QTE = "quantity";

    /** . Constante statique * */
    public static final String ITEM = "item";

    /**
     * . CONSTANTE statique
     */
    public static final String DISPONIBLE = "disponible";

    /**
     * . CONSTANTE statique
     */
    public static final String DISPONIBLE_PLD = "disponiblePLD";

    /**
     * . CONSTANTE qui définit le délai max retourné pour MAD
     */
    public static final String STOCK_PIVOT = "stockPivot";

    /**
     * . CONSTANTE qui définit le délai min retourné pour LAD
     */
    public static final String DELAI_MIN_LAD = "delaiMinLAD";

    /**
     * . CONSTANTE qui définit le délai min retourné pour MAD
     */
    public static final String DELAI_MIN_MAD = "delaiMinMAD";

    /**
     * . CONSTANTE qui définit le délai max retourné par défaut
     */
    public static final String DELAI_MAX = "Livr&eacute; chez vous sous 6 &agrave; 8 semaines";

    /** . Constante statique * */
    public static final int H24 = 24;

    /**
     * . CONSTANTE qui définit le délai max retourné pour LAD
     */
    public static final String DELAI_MAX_LAD = "delaiMaxLAD";

    /**
     * . CONSTANTE qui définit le délai max retourné pour MAD
     */
    public static final String DELAI_MAX_MAD = "delaiMaxMAD";

    /**
     * Libellé du délai maximum.
     */
    public static final String LIBELLE_DELAI_MAX = "libelleDelaiMax";

    /**
     * Repr�sente la valeur -1.
     */
    public static final int NEG = -1;

    /**
     * Repr�sente la valeur 0.
     */
    public static final int ZERO = 0;

    /**
     * Repr�sente la valeur 1.
     */
    public static final int UN = 1;

    /**
     * Repr�sente la valeur 2.
     */
    public static final int DEUX = 2;

    /**
     * Repr�sente la valeur 3.
     */
    public static final int TROIS = 3;

    /**
     * Représente la valeur 4.
     */
    public static final int QUATRE = 4;

    /**
     * Représente la valeur 5.
     */
    public static final int CINQ = 5;
    
    /**
     * Représente la valeur 6.
     */
    public static final int SIX = 6;
    
    /**
     * Représente la valeur 8.
     */
    public static final int HUIT = 8;
    
    /**
     * Représente la valeur 9.
     */
    public static final int NEUF = 9;

    /**
     * Représente la valeur 50.
     */
    public static final int CINQUANTE = 50;
    
    /**
     * Repr�sente la valeur 100.
     */
    public static final int CENT = 100;

    /**
     * Repr�sente la valeur 13.
     */
    public static final int TREIZE = 13;
    
    /**
     * Représente la valeur 255.
     */
    public static final int DEUX_CENT_CINQUANTE_CINQ = 255;
    
    /**
     * Repr�sente la valeur 1000.
     */
    public static final int MILLE = 1000;

    /**
     * Repr�sente la valeur 10.
     */
    public static final int DIX = 10;

    /**
     * Repr�sente la valeur 31.
     */
    public static final int TRENTEUN = 31;

    /**
     * Repr�sente la valeur 12.
     */
    public static final int DOUZE = 12;

    /**
     * Repr�sente la valeur 9999.
     */
    public static final int NEUFMILLENEUFCENTQUATREVINGTDIXNEUF = 9999;

    /**
     * Taux de conversion francs/euros.
     */
    public static final double TAUX_FRANC_EURO = 6.55957;

    /**
     * String pour la valeur bool�enne true.
     */
    public static final String TRUE = "true";

    /**
     * String pour la valeur bool�enne false.
     */
    public static final String FALSE = "false";

    /*
     * ------------------------------------------------------------------------
     * Repositories
     * ------------------------------------------------------------------------
     */

    /**
     * D�signe le descripteur utilisateur.
     */
    public static final String DESCRIPTEUR_UTILISATEUR = "user";

    /**
     * D�signe le descripteur pour les skus casto.
     */
    public static final String DESCRIPTEUR_CASTO_SKU = "casto_sku";

    /**
     * D�signe le descripteur pour les magasins casto.
     */
    public static final String DESCRIPTEUR_MAGASIN = "magasin";

    /**
     * D�signe le descripteur pour les produits casto.
     */
    public static final String DESCRIPTEUR_CASTO_PRODUCT = "casto_product";

    /**
     * D�signe le descripteur pour les produits casto.
     */
    public static final String DESCRIPTEUR_CASTO_CATEGORY = "casto_category";

    /**
     * D�signe le descripteur pour les price list.
     */
    public static final String DESCRIPTEUR_PRICE_LIST = "priceList";

    /**
     * D�signe le descripteur pour les codes postaux.
     */
    public static final String DESCRIPTEUR_CODE_POSTAL = "code_postal";

    /**
     * D�signe le descripteur pour les commandes.
     */
    public static final String DESCRIPTEUR_COMMANDE = "order";

    /*
     * ------------------------------------------------------------------------
     * R��criture
     * ------------------------------------------------------------------------
     */

    /**
     * Indique que la r��criture est en mode "dev".
     */
    public static final String REECRITURE_DEV = "dev";

    /**
     * Indique que la r��criture est en mode "prod".
     */
    public static final String REECRITURE_PROD = "prod";

    /*
     * ------------------------------------------------------------------------
     * Droplets
     * ------------------------------------------------------------------------
     */

    /**
     * Open parameter "output" d'une droplet.
     */
    public static final String OPEN_PARAMETER_OUTPUT = "output";

    /**
     * Open parameter "empty" d'une droplet.
     */
    public static final String OPEN_PARAMETER_EMPTY = "empty";

    /**
     * Open parameter "error" d'une droplet.
     */
    public static final String OPEN_PARAMETER_ERROR = "error";

    /**
     * Open parameter "true" d'une droplet.
     */
    public static final String OPEN_PARAMETER_TRUE = "true";

    /**
     * Open parameter "false" d'une droplet.
     */
    public static final String OPEN_PARAMETER_FALSE = "false";

    /*
     * ------------------------------------------------------------------------
     * Autres
     * ------------------------------------------------------------------------
     */

    /**
     * Encodage à employer.
     */
    public static final String ENCODING_SCHEME = "UTF-8";

    /**
     * Séparateur de ligne.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
