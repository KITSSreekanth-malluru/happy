package com.castorama.constantes;

/**
 * Classe de constantes pour le projet la Défense<br />.
 *
 * @author Florte Jérémy.
 * 
 * TODO : prototype
 */
/**
 * TODO commentaire Javadoc.
 *
 */
public class CastoConstantesDefense
{
    /*
     * ------------------------------------------------------------------------ 
     * [ Constantes ] 
     * ------------------------------------------------------------------------
     */
    
    /**
     * .
     * infos -> Champ d'un casto_sku pointant sur les données le concernant pour un magasin
     */
    public static final String INFOS = "infos";
    
    /**
     * ShippingMethod "Mise à disposition magasin".
     */
    public static final String MISE_A_DISPOSITION = "MAD";
    
    /**
     * ShippingMethod "Livraison a domicile".
     */
    public static final String LIVRAISON_A_DOMICILE = "LAD";
    
    /**
     * Identifiant du magasin "Castorama direct".
     */
    public static final String CASTORAMA_DIRECT = "999";
    
    /**
     * Identifiant du magasin "Castorama direct".
     */
    public static final int CASTORAMA_DIRECT_INT = 999;
    
    /**
     * Identifiant du magasin "PLD" (pour l'instant = au magasin Englos).
     */
    public static final int ID_MAGASIN_PLD = 2400007;
    
    /**
     * Identifiant du magasin "PLD" (pour l'instant = au magasin Englos).
     */
    public static final String MAGASIN_PLD = "2400007";
    
    /**
     * Propriété "sessionMagasin" du repository profile.
     */
    public static final String PROFILE_PROPERTY_SESSION_MAGASIN = "sessionMagasin";
    
    /**
     * Identifiant du repository pour les services.
     */
//    public static final String DESCRIPTEUR_SERVICE = "CommandeService";
    
    /**
     * Identifiant du repository pour les affectations produits/magasins/services.
     */
//    public static final String DESCRIPTEUR_PRODUIT_SERVICE_MAGASIN = "ProduitServiceMagasin";
    
    /**
     * Identifiant du descripteur pour la disponibilité en magasin. 
     */
    public static final String DESCRIPTEUR_DISPONIBILITE = "dispoMagasin";
    
    /**
     * Identifiant du service de livraison à domicile.
     */
    public static final int ID_SERVICE_LIVRAISON_A_DOMICILE = 2; 
    
    /**
     * Identifiant de la propriété "priceList" du descripteur "user" (cf. userProfile.xml).
     */
    public static final String PROPERTY_PRICE_LIST = "priceList";
    
    /**
     * Identifiant de la propriété "salePriceList" du descripteur "user" (cf. userProfile.xml).
     */
    public static final String PROPERTY_SALE_PRICE_LIST = "salePriceList";
    
    /**
     * Propriété "services" d'un repository item produit.
     */
     
    /**
     * Propriété "magasins" d'un repository item produit.
     */
    public static final String PROPERTY_MAGASINS_PRODUIT = "magasins";
    
    /**
     * Propriété "services" d'un repository item sku.
     */
    public static final String PROPERTY_SERVICES_SKU = "services";
    
    /**
     * Propriété "idMagasin" d'une association service/magasin.
     */
    public static final String PROPERTY_ASSOCIATION_ID_MAGASIN = "idMagasin";
    
    /**
     * Propriété "idService" d'une association service/magasin.
     */
    public static final String PROPERTY_ASSOCIATION_ID_SERVICE = "idService";
    
    /**
     * Descripteur pour les services.
     */
    public static final String DESCRIPTEUR_SERVICE_COMMANDE = "serviceL";
    
    /**
     * Propriété "origineMagasin" d'un repository item order.
     */
    public static final String ORDER_PROPERTY_ORIGINE_MAGASIN = "origineMagasin";
    
    /**
     * Propriété "magasins_prix_livres" d'un repository item de type "casto_sku".
     */
    public static final String CASTO_SKU_PROPERTY_PRIX_LIVRE = "PrixLivre";
    
    /**
     * Identifiant de la price list Castorama Direct.
     */
    public static final String PRICE_LIST_CASTORAMA_DIRECT = "PL999";
    
    /**
     * Identifiant de la sale price list Castorama Direct.
     */
    public static final String SALE_PRICE_LIST_CASTORAMA_DIRECT = "SPL999";
    
    /**
     * Propriété "poidsTotal" d'un Shipping Group.
     */
    public static final String SHIPPPING_GROUP_POIDS_TOTAL = "poidsTotal";
    
    /**
     * Propriété "poidsPFT" d'un Shipping Group.
     */
    public static final String SHIPPPING_GROUP_POIDS_PFT = "poidsPFT";
    
    /**
     * Propriété "poidsPFL" d'un Shipping Group.
     */
    public static final String SHIPPPING_GROUP_POIDS_PFL = "poidsPFL";
    
    /**
     * Propriété "delaiEngagement" d'un Shipping Group.
     */
    public static final String SHIPPING_GROUP_DELAI_ENGAGEMENT = "delaiEngagement";
    
    /**
     * Propriété "libelleDelaiEngagement" d'un Shipping Group.
     */
    public static final String SHIPPING_GROUP_LIBELLE_DELAI_ENGAGEMENT = "libelleDelaiEngagement";
    
    /**
     * Propriété "preferenceClientRetraitMAD" d'un Shipping Group.
     */
    public static final String SHIPPING_GROUP_PREFERENCE_CLIENT_RETRAIT = "preferenceClientRetraitMAD";
    
    /**
     * Identifiant du descripteur pour les affectations codes postaux/PFE.
     */
    public static final String DESCRIPTEUR_CP_PFE = "codespostaux_PFE";
    
    /**
     * Propriété "etageLivraison" d'une commande.
     */
    public static final String ORDER_PROPERTY_ETAGE_LIVRAISON = "etageLivraison"; 
    
    /**
     * Propriété typagePromotion d'une promotion.
     */
    public static final String PROPERTY_TYPE_PROMOTION = "typagePromotion";

    /**
     * Indique qu'une promotion n'est applicable que pour CD.
     */
    public static final int PROMOTION_APPLICABLE_CD = 0;

    /**
     * Indique qu'une promotion n'est applicable que pour PLD.
     */
    public static final int PROMOTION_APPLICABLE_PLD = 1;

    /**
     * Indique qu'une promotion est applicable pour CD et PLD.
     */
    public static final int PROMOTION_APPLICABLE_CD_PLD = 2;
}