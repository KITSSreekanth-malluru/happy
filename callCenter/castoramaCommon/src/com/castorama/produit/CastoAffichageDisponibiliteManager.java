package com.castorama.produit;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.MissingInventoryItemException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.produit.CastoProductTools;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.i18n.I18nTools;
import com.castorama.inventory.InventoryAdapter;

/**
 * .
 * 
 * Manager a appeler exclusivement quand on desire connaitre la disponibilite
 * d'un article
 * 
 */
public class CastoAffichageDisponibiliteManager extends /*Code Review*/ApplicationLoggingImpl
{
    /*
     * 
     * CONSTANTES
     * 
     */
    /** . Constante statique * */
    public static final String BUNDLE = "com.castorama.ficheproduit.FicheProduitResources";

    /** . Constante statique * */
    public static final String TYPE_SKU = "casto_sku";

    /** . Constante statique * */
    public static final int PAR_NOS_SOINS = 1;

    /** . Constante statique * */
    public static final int LIVRAISON_DIRECTE_FOURNISSEUR = 2;

    /** . Constante statique * */
    public static final int LIVRAISON_DIRECTE_MAGASIN = 3;

    /** . Constante statique * */
    public static final String COLISSIMO = "COLISSIMO";

    /** . Constante statique * */
    public static final String SERNAM = "SERNAM";

    /** . Constante statique * */
    public static final String HEPPNER = "HEPPNER";

    /** . Constante statique * */
    public static final String PRESTONNE = "PRESTONNE";
    
    /** . Constante statique * */
    public static final String DISPONIBLE = "disponible";

    /** . Constante statique * */
    public static final int NEG = -1;

    /** . Constante statique * */
    public static final double UNE_JOURNEE = 24;

    /** . Constante statique * */
    public static final int UN = 1;

    /** . Constante statique * */
    public static final int DEUX = 2;

    /** . Constante statique * */
    public static final int TROIS = 3;

    /** . Constante statique * */
    public static final int CINQ = 5;

    /** . Constante statique * */
    public static final int QUATRE = 4;

    /** . Constante statique * */
    public static final int SEPT = 7;

    /** . Constante statique * */
    public static final int SIX = 6;

    /** . Constante statique * */
    public static final int UNE_SEMAINES_EN_HEURES = 168;

    /** . Constante statique * */
    public static final int DEUX_SEMAINES_EN_HEURES = 336;

    /** . Constante statique * */
    public static final int TROIS_SEMAINES_EN_HEURES = 504;

    /** . Constante statique * */
    public static final int QUATRE_SEMAINES_EN_HEURES = 672;

    /** . Constante statique * */
    public static final int CINQ_SEMAINES_EN_HEURES = 840;

    /** . Constante statique * */
    public static final int SIX_SEMAINES_EN_HEURES = 1008;

    /** . Constante statique * */
    public static final int UN_JOUR_EN_HEURES = 24;

    /** . Constante statique * */
    public static final int DEUX_JOURS_EN_HEURES = 48;

    /** . Constante statique * */
    public static final int TROIS_JOURS_EN_HEURES = 72;

    /** . Constante statique * */
    public static final int QUATRE_JOURS_EN_HEURES = 96;

    /** . Constante statique * */
    public static final int CINQ_JOURS_EN_HEURES = 120;

    /** . Constante statique * */
    public static final int SIX_JOURS_EN_HEURES = 144;

    /** . Constante statique * */
    public static final int HUIT = 8;

    /** . Constante statique * */
    public static final int H24 = 24;

    /** . Constante statique * */
    public static final int INFINI = 1000;

    /** . Constante statique * */
    public static final int HUIT_SEMAINES = 35;

    /** . Constante statique * */
    public static final int CINQ_SEMAINES = 28;

    /** . Constante statique * */
    public static final int QUATRE_SEMAINES = 21;

    /** . Constante statique * */
    public static final int TROIS_SEMAINES = 14;

    /** . Constante statique * */
    public static final int QUINZE_JOURS = 7;

    /** . Constante statique * */
    public static final int HUIT_JOURS = 0;
    
    /** . Constante statique * */
    public static final int DESTOCKAGE = -2;    

    /** . Constante statique * */
    public static final int PLUS_VENDU = 1005;

    /** . Constante statique * */
    public static final String BUNDLE_LINKS = "bundleLinks";
    
    /** . Constante statique * */
    public static final int     MOINS_SIX  = -6;

    /** . Constante statique * */
    public static final String QTE = "quantity";

    /** . Constante statique * */
    public static final String ITEM = "item";

    /** . Constante statique * */
    public static final String ID = "id";
    
    /** . Constante statique * */
    public static final String PANIER = "panier";
    
    /**. Constante statique **/
    public static final int     RUPTURE_MOMENTANEE_FOURNISSEUR  = 3;

    /**
     * . CONSTANTE pour le coefficient de fiabilité du stock.
     */
    public static final String COEFFICIENT_FIABILITE_STOCK = "coef_stock";

    /**
     * . CONSTANTE pour le delai de preparation du magasin
     */
    public static final String DELAI_PREPARATION = "delai_preparation";

    /**
     * . CONSTANTE pour le delai de livraison du magasin
     */
    public static final String DELAI_LIVRAISON = "delai_livraison";

    /**
     * . CONSTANTE pour le délai de réapprovissionement Pour le moment, c'est
     * une variable en dur : 0 heure
     */
    public static final int DELAI_REAPPROVISSIONEMENT = 0;

    private static final String DELAI_APPRO_FOURNISSEUR = "DelaiApproFournisseur";

    private static final String QUATRE_STR = ".quatre";

    private static final String CINQ_STR = ".cinq";

    private static final String HUIT_EXP = ".huitExp";

    private static final String SKU_INEXISTANT_STR = "L'inventaire n'existe pas pour le SKU : ";

    /*
     * 
     * ATTRIBUTS
     * 
     */

    /**
     * . Référence vers l'inventoryAdapter : Puiser les stocks
     */
    private InventoryAdapter m_inventoryAdapter;

    /** . Repository * */
    private Repository m_oRepository;

    /**
     * Renvoie une référence vers la classe utilitaire InventoryAdapter.
     * 
     * @return Une instance de InventoryAdapter.
     */
    public InventoryAdapter getInventoryAdapter()
    {
        return m_inventoryAdapter;
    }

    /**
     * Fixe la référence à InventoryAdapter.
     * 
     * @param a_inventoryAdapter
     *            une instance de InventoryAdapter.
     */
    public void setInventoryAdapter(InventoryAdapter a_inventoryAdapter)
    {
        m_inventoryAdapter = a_inventoryAdapter;
    }

    /**
     * Fait tous les tests qu'il faut pour calculer le delai.
     * 
     * @param a_oSku
     *            Sku concerne
     * @param a_quantite
     *            Quantité d'articles à tester.
     * 
     * @return double Delai calcule
     * 
     * @throws Exception
     *             Exception
     */
    private static double calculSelonRegles(RepositoryItem a_oSku, double a_quantite)
    {
        String l_sTransporteur = (String) a_oSku.getPropertyValue("transporteur");
        Integer l_nTypeExpedition = (Integer) a_oSku.getPropertyValue("typeExpedition");
        Double l_oApproFournisseur = (Double) a_oSku.getPropertyValue(DELAI_APPRO_FOURNISSEUR);

        double l_nApproFournisseur = 0;
        if (l_oApproFournisseur != null)
        {
            l_nApproFournisseur = l_oApproFournisseur.doubleValue();
        }

        // Le delai livraison est celui affiche a l'utilisateur
        double l_nDelaiLivraison = NEG;

        // Calcul du delai de livraison
        if (l_nTypeExpedition != null)
        {
            if (l_nTypeExpedition.intValue() == PAR_NOS_SOINS)
            {
                if (a_quantite >= 0)
                {
                    if (COLISSIMO.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = TROIS;
                    }
                    else if (SERNAM.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    else if (PRESTONNE.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    /** F-D Evol Heppner * */
                    else if (HEPPNER.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    /** F-D Evol Heppner * */
                }
                else
                {
                    if (COLISSIMO.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ + l_nApproFournisseur;
                    }
                    else if (SERNAM.equalsIgnoreCase(l_sTransporteur))
                    {
                        // Correction FICHE 803 J.R & F.Dl_nDelaiLivraison =
                        // CINQ + l_nApproFournisseur;
                        l_nDelaiLivraison = SEPT + l_nApproFournisseur;
                    }
                    else if (PRESTONNE.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = SEPT + l_nApproFournisseur;
                    }
                    /** F-D Evol Heppner * */
                    else if (HEPPNER.equalsIgnoreCase(l_sTransporteur))
                    {
                        // Correction FICHE 803 J.R & F.Dl_nDelaiLivraison =
                        // CINQ + l_nApproFournisseur;
                        l_nDelaiLivraison = SEPT + l_nApproFournisseur;
                    }
                    /** F-D Evol Heppner * */
                }
            }
            else if (l_nTypeExpedition.intValue() == LIVRAISON_DIRECTE_FOURNISSEUR)
            {
                l_nDelaiLivraison = DEUX + l_nApproFournisseur;
            }
            else if (l_nTypeExpedition.intValue() == LIVRAISON_DIRECTE_MAGASIN)
            {
                l_nDelaiLivraison = NEG;
            }
        }

        return l_nDelaiLivraison;
    }

    /**
     * .
     * 
     * En fonction du delai calcule, on cherche le libelle correspondant
     * 
     * @param a_nDelai
     *            Delai calcule
     * @param a_sProvenance
     *            Provenance
     * @return Libelle Libelle correspondant au delai calcule
     */
    public static String calculLibelle(double a_nDelai, String a_sProvenance)
    {

        String l_sDisponibilite = null;

        if (a_nDelai > HUIT_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".huit", BUNDLE);
        }
        else if (a_nDelai > CINQ_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + CINQ_STR, BUNDLE);
        }
        else if (a_nDelai > QUATRE_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + QUATRE_STR, BUNDLE);
        }
        else if (a_nDelai > TROIS_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".trois", BUNDLE);
        }
        else if (a_nDelai > QUINZE_JOURS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".deux", BUNDLE);
        }
        else if (a_nDelai > HUIT_JOURS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".un", BUNDLE);
        }
        else if (a_nDelai == DESTOCKAGE)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".destockage", BUNDLE);
        }
        else
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".huit", BUNDLE);
        }

        return l_sDisponibilite;
    }

    /**
     * .
     * 
     * FD - 20/11/2007 - traitement des retours espace client En fonction du
     * delai calcule, on cherche le libelle correspondant
     * 
     * @param a_nDelai
     *            Delai calcule
     * @param a_sProvenance
     *            Provenance
     * @return Libelle Libelle correspondant au delai calcule
     */
    public static String calculLibelleDelaiExpedition(double a_nDelai, String a_sProvenance)
    {

        String l_sDisponibilite = null;

        if (a_nDelai == HUIT)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + HUIT_EXP, BUNDLE);
        }
        else if (a_nDelai == CINQ)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".cinqExp", BUNDLE);
        }
        else if (a_nDelai == QUATRE)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".quatreExp", BUNDLE);
        }
        else if (a_nDelai == TROIS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".troisExp", BUNDLE);
        }
        else if (a_nDelai == DEUX)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".deuxExp", BUNDLE);
        }
        else if (a_nDelai == UN)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".unExp", BUNDLE);
        }
        else if (a_nDelai == DESTOCKAGE)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".destockageExp", BUNDLE);
        }
        else
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + HUIT_EXP, BUNDLE);
        }

        return l_sDisponibilite;
    }

    /**
     * . LOGICA - La Défense - calcul du délai en heures/jours/semaines à partir
     * d'un a_nDelai en jours.
     * 
     * @param a_nDelai
     *            Delai calcule
     * @param a_sProvenance
     *            Provenance
     * @return Libelle Libelle correspondant au delai calcule
     */
    public static String calculLibellePLD(double a_nDelai, String a_sProvenance)
    {

        String l_sDisponibilite = null;

        /* Les semaines */
        /*
         * if( a_nDelai > SIX_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".huitSemaines", BUNDLE); } else
         * if( a_nDelai > CINQ_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".sixSemaines", BUNDLE); } else
         * if( a_nDelai > QUATRE_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".cinqSemaines", BUNDLE); } else
         * if( a_nDelai > TROIS_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".quatreSemaines", BUNDLE); } else
         * if( a_nDelai > DEUX_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".troisSemaines", BUNDLE); } else
         * if( a_nDelai > UNE_SEMAINES_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".deuxSemaines", BUNDLE); }
         */
        /* Les jours */
        /*
         * else if( a_nDelai > SIX_JOURS_EN_HEURES ) { l_sDisponibilite =
         * I18nTools.getMessage(a_sProvenance+".uneSemaine", BUNDLE); }
         */
        if (a_nDelai > SIX_JOURS_EN_HEURES)
        {
            int l_d = new Double(Math.ceil(a_nDelai / H24)).intValue();
            l_sDisponibilite = "dans " + l_d + " jours";
        }
        else if (a_nDelai > CINQ_JOURS_EN_HEURES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".sixJours", BUNDLE);
        }
        else if (a_nDelai > QUATRE_JOURS_EN_HEURES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".cinqJours", BUNDLE);
        }
        else if (a_nDelai > TROIS_JOURS_EN_HEURES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".quatreJours", BUNDLE);
        }
        else if (a_nDelai > DEUX_JOURS_EN_HEURES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".troisJours", BUNDLE);
        }
        else if (a_nDelai > UN_JOUR_EN_HEURES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".deuxJours", BUNDLE);
        }
        else if (UN_JOUR_EN_HEURES >= a_nDelai)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".uneJournee", BUNDLE);
        }
        /* Les heures */
        else if (a_nDelai == SEPT)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + CINQ_STR, BUNDLE);
        }
        else if (a_nDelai == SIX)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + QUATRE_STR, BUNDLE);
        }
        else if (a_nDelai == CINQ)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + CINQ_STR, BUNDLE);
        }
        else if (a_nDelai == QUATRE)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + QUATRE_STR, BUNDLE);
        }
        else if (a_nDelai == TROIS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".trois", BUNDLE);
        }
        else if (a_nDelai == DEUX)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".deux", BUNDLE);
        }
        else if (a_nDelai == UN)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".un", BUNDLE);
        }
        else
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".huitSemaines", BUNDLE);
        }

        return l_sDisponibilite;
    }

    /**
     * . LOGICA - La Défense
     * 
     * @param a_nDelai
     *            Delai calcule
     * @param a_sProvenance
     *            Provenance
     * @return Libelle Libelle correspondant au delai calcule
     */
    public static String calculLibelleLAD(double a_nDelai, String a_sProvenance)
    {

        String l_sDisponibilite = null;

        if (a_nDelai > HUIT_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + HUIT_EXP, BUNDLE);
        }
        else if (a_nDelai > CINQ_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".cinqExp", BUNDLE);
        }
        else if (a_nDelai > QUATRE_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".quatreExp", BUNDLE);
        }
        else if (a_nDelai > TROIS_SEMAINES)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".troisExp", BUNDLE);
        }
        else if (a_nDelai > QUINZE_JOURS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".deuxExp", BUNDLE);
        }
        else if (a_nDelai >= HUIT_JOURS)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".unExp", BUNDLE);
        }
        else if (a_nDelai == DESTOCKAGE)
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".destockageExp", BUNDLE);
        }
        else
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + HUIT_EXP, BUNDLE);
        }

        return l_sDisponibilite;
    }

    /**
     * Calcule le délai de livraison.
     * 
     * @param a_oSku
     *            Sku en question
     * @param a_sProvenance
     *            Texte à choisir
     * @param a_quantite
     *            Quantité d'articles à tester
     * @param a_sProvenance2
     *            Non documenté.
     * 
     * @return String Disponibilite.
     * 
     */
    public String calculDelaiLibelle(RepositoryItem a_oSku, String a_sProvenance, double a_quantite, String a_sProvenance2)
    {
        String l_sDisponibilite = null;

        double l_nDelaiLivraison = calculDelai(a_oSku, a_quantite, a_sProvenance2);
        
        // Projet Destockage
        if(CastoProductTools.isInDestockage(a_oSku))
        {
            l_sDisponibilite = I18nTools.getMessage(a_sProvenance + ".destockage", BUNDLE);
        }
        else
        {
            l_sDisponibilite = calculLibelle(l_nDelaiLivraison, a_sProvenance);
        }

        return l_sDisponibilite;
    }

    /**
     * . Calcule la quantité restante commandable
     * 
     * @param a_oSku
     *            Sku en question
     * @param a_quantite
     *            Quantit� d'articles � tester
     * @param a_profile
     *            a_profile
     * @return String Disponibilite
     */
    public double quantiteRestante(RepositoryItem a_oSku, double a_quantite, RepositoryItem a_profile)
    {
        String l_sIdSku = a_oSku.getRepositoryId();
        double l_nStock = NEG;
        double l_quantiteRestante = NEG;
        try
        {
            InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager(a_profile, false);
            
            if (null != l_inventoryManager)
            {
                l_nStock = l_inventoryManager.queryStockLevel(l_sIdSku);
            }
            
            if (l_nStock == NEG)
            {
                // On assigne 100 a la variable quantite restante
                // sachant que ca pourrait etre nimporte quoi d'autre...
                l_quantiteRestante = INFINI;
            }
            else
            {
                l_quantiteRestante = l_nStock - a_quantite;
            }
        }
        catch (MissingInventoryItemException l_oException)
        {
            if (isLoggingInfo())
            {
                logInfo(SKU_INEXISTANT_STR + l_sIdSku);
            }
            l_nStock = NEG;
        }
        catch (InventoryException l_ex)
        {
            logError(l_ex);
            l_nStock = NEG;
        }
        return l_quantiteRestante;
    }

    /**
     * .
     * 
     * Calcule le délai de livraison.
     * 
     * @param a_oSku
     *            Sku en question
     * @param a_quantite
     *            Quantité d'articles à choisir
     * @param a_provenance
     *            Non documenté.
     * 
     * @return double Delai.
     * 
     */
    public double calculDelai(RepositoryItem a_oSku, double a_quantite, String a_provenance)
    {
        if(isLoggingDebug())
            logDebug("Debut traitement des codes lots - delai de livraison");
            
        if(isLoggingDebug())
            logDebug("Recuperation de l article maitre et des articles esclaves");
        
        String l_sSkuId =(String) a_oSku.getPropertyValue(ID) ;

        RepositoryItem[] l_aArticleMaitre = new RepositoryItem[1];
        l_aArticleMaitre[0] = a_oSku;
        
        List l_aArticlesEsclaves = null;
        
        if(l_aArticleMaitre[0] != null) /* Est-on dans un pack ? oui, si bundleLinks != 0*/
            l_aArticlesEsclaves = (List) l_aArticleMaitre[0].getPropertyValue(BUNDLE_LINKS);
        
        if(isLoggingDebug())
            logDebug("article maitre : " + l_aArticleMaitre[0].getPropertyValue("CodeArticle"));
        
        if(isLoggingDebug())
            logDebug("article consulte : " + l_sSkuId);
        
        if(isLoggingDebug() && l_aArticlesEsclaves != null)
            logDebug("nombre d article esclaves : " + l_aArticlesEsclaves.size());
        
        
        double l_nDelaiLivraison=0;
        
        if(CastoProductTools.isInDestockage(a_oSku))
        {
            if(isLoggingDebug())
                logDebug("produit en destokage");
            l_nDelaiLivraison = DESTOCKAGE;
        }
        else
        {
            if(l_aArticlesEsclaves == null || (l_aArticlesEsclaves != null && l_aArticlesEsclaves.size() == 0) )
            {
                if(isLoggingDebug())
                    logDebug("nous n etions pas dans un pack - traitement classique du delai");
            
                l_nDelaiLivraison = calculSelonRegles(a_oSku, quantiteRestante(a_oSku, a_quantite,a_provenance));
            }
            else
            {
                if(isLoggingDebug())
                    logDebug("nous sommes dans un pack");
           
                double l_tmp=0;
                //l_nDelaiLivraison=calculSelonRegles(l_aArticleMaitre[0], l_quantiteRestante);
                l_nDelaiLivraison=0;
           
                if(isLoggingDebug())
                    logDebug("delai livraison de l article maitre : " + l_nDelaiLivraison);
           
                for(Iterator l_it = l_aArticlesEsclaves.iterator();l_it.hasNext();)
                {
                    RepositoryItem l_SkuLink = (RepositoryItem)l_it.next();
                    double l_quantiteDemande = a_quantite*((Long)l_SkuLink.getPropertyValue(QTE)).doubleValue();
                    RepositoryItem l_Sku = (RepositoryItem)l_SkuLink.getPropertyValue(ITEM);
               
                    l_tmp = calculSelonRegles(l_Sku, quantiteRestante(l_Sku, l_quantiteDemande,a_provenance));
                    if(isLoggingDebug())
                        logDebug("le delai de livraison de l article esclave "+ l_Sku.getPropertyValue(ID) + " est " + l_tmp);
                    if(l_tmp > l_nDelaiLivraison)
                        l_nDelaiLivraison = l_tmp;
                }
            }
        }
        if(isLoggingDebug())
            logDebug("delai max retenu : " + l_nDelaiLivraison);
        
        if(isLoggingDebug())
            logDebug("Fin traitement des codes lots - delai de livraison");

        return l_nDelaiLivraison;
    }

    /**
     * .
     * quantiteRestante
     * 
     * @param a_oSku
     *            Sku en question.
     * @param a_quantite
     *            double.
     * @param a_provenance
     *            Provenance de l'internaute.
     * 
     * @return double .
     * 
     */
    public double quantiteRestante(RepositoryItem a_oSku, double a_quantite, String a_provenance)
    {
        String l_sIdSku = a_oSku.getRepositoryId();
        double l_nStock = NEG;
        double l_quantiteRestante = NEG;
        try
        {
            if (a_provenance.equals(PANIER))
            {
                l_nStock = getInventoryAdapter().getInventoryManager().queryStockLevel(l_sIdSku);
            }
            else
            {
                l_nStock = getInventoryAdapter().getCachedInventoryManager().queryStockLevel(l_sIdSku);
            }

            if (l_nStock == NEG)
            {
                // On assigne 100 a la variable quantite restante
                // sachant que ca pourrait etre nimporte quoi d'autre...
                l_quantiteRestante = INFINI;
            }
            else
            {
                l_quantiteRestante = l_nStock - a_quantite;
            }
        }
        catch (MissingInventoryItemException l_oException)
        {
            if (isLoggingInfo())
            {
                logInfo(SKU_INEXISTANT_STR + l_sIdSku);
            }
            l_nStock = NEG;
        }
        catch (InventoryException l_exception2)
        {
            if (isLoggingInfo())
            {
                logInfo(SKU_INEXISTANT_STR +
                        l_sIdSku);
            }
            l_nStock = CastoConstantes.NEG;
        }
        
        return l_quantiteRestante;
    }
    
    /**
     * Is plus vendu.
     * 
     * @param a_oSku
     *            Sku en question.
     * @param a_profile
     *            Profile.
     * @param a_provenance
     *            Provenance de l'internaute.
     * 
     * @return boolean is plus vendu.
     * 
     * @throws InventoryException
     *             InventoryException
     */
    public boolean isPlusVendu(RepositoryItem a_oSku, RepositoryItem a_profile, String a_provenance)
            throws InventoryException
    {

        String l_sIdProduit = a_oSku.getRepositoryId();
        boolean l_plusVendu;

        int l_nStatus = NEG;
        try
        {
            InventoryManager l_inventoryManager;
            
            // MC 080226 : si on vient du pré-panier alors on utilise
            // l'InventoryRepository
            if (a_provenance.equals(PANIER))
            {
                l_inventoryManager = getInventoryAdapter().getInventoryManager(a_profile, false);
            }
            else
            {
                l_inventoryManager = getInventoryAdapter().getCachedInventoryManager(a_profile);
            }
            
            if (null != l_inventoryManager)
            {
                l_nStatus = l_inventoryManager.queryAvailabilityStatus(l_sIdProduit);    
            }
        }
        catch (MissingInventoryItemException l_oException)
        {
            if (isLoggingInfo())
            {
                logInfo(SKU_INEXISTANT_STR + l_sIdProduit);
            }
            l_nStatus = NEG;
        }

        // si 1005 article plus vendu
        if (l_nStatus == PLUS_VENDU)
        {
            l_plusVendu = true;
        }
        else
        {
            l_plusVendu = false;
        }

        return l_plusVendu;
    }

    /**
     * .
     * 
     * Fait tous les tests qu'il faut pour calculer le delai
     * 
     * @param a_oSku
     *            Sku concerne
     * @param a_quantite
     *            quantit� d'articles � tester
     * @return double Delai calcule
     * @throws Exception
     *             Exception
     */
    public static double calculSelonReglesPanier(RepositoryItem a_oSku, double a_quantite) throws Exception
    {

        String l_sTransporteur = (String) a_oSku.getPropertyValue("transporteur");
        Integer l_nTypeExpedition = (Integer) a_oSku.getPropertyValue("typeExpedition");
        Double l_oApproFournisseur = (Double) a_oSku.getPropertyValue(DELAI_APPRO_FOURNISSEUR);

        double l_nApproFournisseur = 0;
        if (l_oApproFournisseur != null)
        {
            l_nApproFournisseur = l_oApproFournisseur.doubleValue();
        }

        // Le delai livraison est celui affiche a l'utilisateur
        double l_nDelaiLivraison = NEG;

        // Calcul du delai de livraison
        if (l_nTypeExpedition != null)
        {
            if (l_nTypeExpedition.intValue() == PAR_NOS_SOINS)
            {
                if (a_quantite >= 0)
                {
                    if (COLISSIMO.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = TROIS;
                    }
                    else if (SERNAM.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    else if (PRESTONNE.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    /** F-D Evol Heppner* */
                    else if (HEPPNER.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    /** F-D Evol Heppner* */
                }
                else
                {
                    if (COLISSIMO.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ + l_nApproFournisseur;
                    }
                    else if (SERNAM.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ + l_nApproFournisseur;
                    }
                    else if (PRESTONNE.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = SEPT + l_nApproFournisseur;
                    }
                    /** F-D Evol Heppner* */
                    else if (HEPPNER.equalsIgnoreCase(l_sTransporteur))
                    {
                        l_nDelaiLivraison = CINQ;
                    }
                    /** F-D Evol Heppner* */
                }
            }
            else if (l_nTypeExpedition.intValue() == LIVRAISON_DIRECTE_FOURNISSEUR)
            {
                l_nDelaiLivraison = DEUX + l_nApproFournisseur;
            }
            else if (l_nTypeExpedition.intValue() == LIVRAISON_DIRECTE_MAGASIN)
            {
                l_nDelaiLivraison = NEG;
            }
        }

        return l_nDelaiLivraison;
    }

    /**
     * . En fonction du delai calcule, on cherche le libelle correspondant
     * 
     * @param a_nDelai
     *            Delai calcule
     * @return Libelle Libelle correspondant au delai calcule
     */
    public static double calculDelaiEnSemaine(double a_nDelai)
    {

        String l_sDisponibilite = null;

        if (a_nDelai > HUIT_SEMAINES)
        {
            l_sDisponibilite = "8";
        }
        else if (a_nDelai > CINQ_SEMAINES)
        {
            l_sDisponibilite = "5";
        }
        else if (a_nDelai > QUATRE_SEMAINES)
        {
            l_sDisponibilite = "4";
        }
        else if (a_nDelai > TROIS_SEMAINES)
        {
            l_sDisponibilite = "3";
        }
        else if (a_nDelai > QUINZE_JOURS)
        {
            l_sDisponibilite = "2";
        }
        else if (a_nDelai > HUIT_JOURS)
        {
            l_sDisponibilite = "1";
        }
        else if (a_nDelai > DESTOCKAGE)
        {
            l_sDisponibilite = DESTOCKAGE+"";
        }
        else
        {
            l_sDisponibilite = "8";
        }

        return new Double(l_sDisponibilite).doubleValue();
    }
   
    /**
     * Repository.
     * 
     * @return Repository
     */
    public Repository getRepository()
    {
        return m_oRepository;
    }

    /**
     * .
     * 
     * Repository.
     * 
     * @param a_oRepository
     *            Repository
     */
    public void setRepository(Repository a_oRepository)
    {
        this.m_oRepository = a_oRepository;
    }

    /**
     * . Logica - La Défense Calcule le delail de livraison LAD
     * 
     * @param a_oSku
     *            Sku en question
     * @param a_res
     *            Stock*Coeff Fiabilité
     * @param a_profile
     *            Profile
     * @return double Delai
     */
    public double getDelaiLAD(RepositoryItem a_oSku, double a_res, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.CastoAffichageDisponibiliteManager.getDelaiLAD()");
        }

        RepositoryItem[] l_aArticleMaitre = new RepositoryItem[1];
        l_aArticleMaitre[0] = a_oSku;

        List l_aArticlesEsclaves = null;

        if (l_aArticleMaitre[0] != null) /*
                                             * Est-on dans un pack ? oui, si
                                             * bundleLinks != 0
                                             */
            l_aArticlesEsclaves = (List) l_aArticleMaitre[0].getPropertyValue(BUNDLE_LINKS);

        double l_nDelaiLivraison = 0;
        if (l_aArticlesEsclaves == null || (l_aArticlesEsclaves != null && l_aArticlesEsclaves.size() == 0))
        {
            l_nDelaiLivraison = calculSelonRegles(a_oSku, quantiteRestante(a_oSku, a_res, a_profile));
        }
        else
        {
            double l_tmp = 0;
            l_nDelaiLivraison = 0;
            for (Iterator l_it = l_aArticlesEsclaves.iterator(); l_it.hasNext();)
            {
                RepositoryItem l_SkuLink = (RepositoryItem) l_it.next();
                double l_quantiteDemande = a_res * ((Long) l_SkuLink.getPropertyValue(QTE)).doubleValue();
                RepositoryItem l_Sku = (RepositoryItem) l_SkuLink.getPropertyValue(ITEM);
                l_tmp = calculSelonRegles(l_Sku, quantiteRestante(l_Sku, l_quantiteDemande, a_profile));
                if (l_tmp > l_nDelaiLivraison)
                    l_nDelaiLivraison = l_tmp;
            }
        }
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.produit.CastoAffichageDisponibiliteManager.getDelaiLAD()");
        }

        return l_nDelaiLivraison;
    }

    /**
     * . Renvoie le coefficient de fiabilité du stock
     * 
     * @param a_sIdCategorie
     *            a_sIdCategorie
     * @return double Delai
     */
    public double getCoefficientFiabilite(String a_sIdCategorie)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.CastoAffichageDisponibiliteManager.getCoefficientFiabilite()");
        }
        try
        {
            if (null != a_sIdCategorie && !"".equals(a_sIdCategorie.trim()))
            {

                RepositoryItem l_cat = (RepositoryItem) getRepository().getItem(a_sIdCategorie,
                        CastoConstantes.DESCRIPTEUR_CASTO_CATEGORY);
                if (null != l_cat)
                {
                    Double l_Coef = (Double) l_cat.getPropertyValue("CoefFiabilite");
                    if (null != l_Coef)
                    {
                        return l_Coef.doubleValue();
                    }
                }
            }
            else
            {
                logError("a_sIdCategorie == null");
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.produit.CastoAffichageDisponibiliteManager.getCoefficientFiabilite()");
        }
        return 0;
    }

    /**
     * . Renvoie le coefficient de fiabilité du stock
     * 
     * @param a_profile
     *            a_profile
     * @return double Delai
     */
    public double getDelaiPreparation(RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiPreparation()");
        }
        if (null != a_profile)
        {
            if (isLoggingDebug())
            {
                logDebug(CastoConstantes.METHODE_SORTIE
                        + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiPreparation()");
            }
            RepositoryItem l_sSessionMagasin = (RepositoryItem) a_profile.getPropertyValue("sessionMagasin");
            if (null != l_sSessionMagasin)
            {
                Double l_delai = (Double) l_sSessionMagasin.getPropertyValue(DELAI_PREPARATION);
                if (null != l_delai)
                {
                    /*
                     * PLD_REG_FP_6b Le délai standard de màd est paramétré pour
                     * le magasin PLD à J+1.
                     */
                    return l_delai.doubleValue(); /* + UNE_JOURNEE; */
                }
                else
                {
                    logError("Ce magasin n a pas de delai de preparation.");
                }
            }
            else
            {
                logError("sessionMagasin == null");
            }
        }
        else
        {
            logError("Profile == null");
        }
        return 0;
    }

    /**
     * . Renvoie le coefficient de fiabilité du stock
     * 
     * @param a_profile
     *            a_profile
     * @return double Delai
     */
    public double getDelaiLivraison(RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiLivraison()");
        }
        if (null != a_profile)
        {
            if (isLoggingDebug())
            {
                logDebug(CastoConstantes.METHODE_SORTIE
                        + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiLivraison()");
            }
            RepositoryItem l_sSessionMagasin = (RepositoryItem) a_profile.getPropertyValue("sessionMagasin");
            if (null != l_sSessionMagasin)
            {
                Double l_delai = (Double) l_sSessionMagasin.getPropertyValue(DELAI_LIVRAISON);
                if (null != l_delai)
                {
                    /*
                     * PLD_REG_FP_6b Le délai standard de màd est paramétré pour
                     * le magasin PLD à J+1.
                     */
                    return l_delai.doubleValue(); /* + UNE_JOURNEE; */
                }
                else
                {
                    logError("Ce magasin n a pas de delai de livraison.");
                }
            }
            else
            {
                logError("sessionMagasin == null");
            }
        }
        else
        {
            logError("Profile == null");
        }
        return 0;
    }

    /**
     * . Renvoie le stock du produit
     * 
     * @param a_Sku
     *            RepositoryItem
     * @param a_profile
     *            RepositoryItem
     * @return double Delai
     */
    public double getStock(RepositoryItem a_Sku, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getStock()");
        }

        try
        {
            if (null != a_Sku)
            {
                InventoryAdapter l_inventoryAdapter = getInventoryAdapter();
                if (null != l_inventoryAdapter)
                {
                    InventoryManager l_inventoryManager = l_inventoryAdapter.getInventoryManager(a_profile, false);
                    
                    if (null != l_inventoryManager)
                    {
                        return l_inventoryManager.queryStockLevel(a_Sku.getPropertyValue(ID).toString());
                    }
                }
            }
        }
        catch (InventoryException l_ex)
        {
            logError("la demande queryStockLevel a echoue.");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getStock()");
        }
        return 0;
    }

    /**
     * . Renvoie le délai de réapprovisionnement du produit
     * 
     * @param a_Sku
     *            RepositoryItem
     * @param a_profile
     *            RepositoryItem
     * @return double Delai
     */
    public double getDelaiReappro(RepositoryItem a_Sku, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiReappro()");
        }

        if (null != a_profile)
        {
            RepositoryItem l_sSessionMagasin = (RepositoryItem) a_profile
                    .getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
            if (null != l_sSessionMagasin)
            {
                Integer l_idMagasinSession = (Integer) l_sSessionMagasin.getPropertyValue(ID);
                if (null != a_Sku && null != l_idMagasinSession)
                {
                    Map l_infos = (Map) a_Sku.getPropertyValue(CastoConstantesDefense.INFOS);
                    if (l_infos.containsKey(l_idMagasinSession.toString()))
                    {
                        RepositoryItem l_donnees = (RepositoryItem) l_infos.get(l_idMagasinSession.toString());
                        if (null != l_donnees)
                        {
                            Double l_delai = (Double) l_donnees.getPropertyValue(DELAI_APPRO_FOURNISSEUR);
                            if (null != l_delai)
                            {
                                return l_delai.doubleValue();
                            }
                        }
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.produit.DropletAffichageDisponibilitePLD.getDelaiReappro()");
        }
        return DELAI_REAPPROVISSIONEMENT;
    }
    
    /**.
     * 
     * Méthode permettant de contrôler la règle de gestion REG_1_1 de la fiche produit
     * 
     * @param       a_nStatus                   Statut du stock
     * @param       a_oSku                      Sku à tester
     * @return      String                      Chaîne de caractère indiquant si
     *                                          le sku est commande et/ou sélectionnable
     */
    public boolean isCommandableQuantiteLimitee (int a_nStatus, RepositoryItem a_oSku)
    {

        List l_aArticlesEsclaves = null;
        
        if(a_oSku != null) /* Est-on dans un pack ? oui, si bundleLinks != 0*/
            l_aArticlesEsclaves = (List) a_oSku.getPropertyValue(BUNDLE_LINKS);
        
        if(l_aArticlesEsclaves != null && l_aArticlesEsclaves.size() != 0)
        {
            boolean l_tmp = false;
            boolean l_Retour = false;
            
            for(Iterator l_it = l_aArticlesEsclaves.iterator();l_it.hasNext();)
            {
                RepositoryItem l_SkuLink = (RepositoryItem)l_it.next();
                RepositoryItem l_sku = (RepositoryItem) l_SkuLink.getPropertyValue(ITEM);
                l_tmp = isCommandableQuantiteLimiteeUnArticle(a_nStatus,l_sku);
                // true = quantité limitée
                // false = quantité non limité : nouvelle règle
                if(l_tmp)
                    l_Retour = l_tmp;
            }
            // true = quantité limitée
            // false = quantité non limité : nouvelle règle
            return l_Retour;
        }
        else
        {
            // true = quantité limitée
            // false = quantité non limité : nouvelle règle
            return isCommandableQuantiteLimiteeUnArticle(a_nStatus,a_oSku);
        }
    }
    
    /**.
     * 
     * Méthode permettant de contrôler la règle de gestion REG_1_1 de la fiche produit
     * 
     * @param       a_nStatus                   Statut du stock
     * @param       a_oSku                      Sku à tester
     * @return      String                      Chaîne de caractère indiquant si
     *                                          le sku est commande et/ou sélectionnable
     */
    public boolean isCommandableQuantiteLimiteeUnArticle (int a_nStatus, RepositoryItem a_oSku) 
    {
        
        Integer l_nRetraitCodifie = (Integer) a_oSku.getPropertyValue("retraitMomentaneMotifsCodifies");
        Date l_dDateCourante = new Date();
        Date l_dDateFinVie = (Date) a_oSku.getPropertyValue("dateFinVie");
        String l_limitationDeQuantite = null;
        boolean l_quantiteLimitee = false;
        if(a_oSku != null)
        {
            if(a_oSku.getPropertyValue("quantiteLimitee") != null)
            {
                l_limitationDeQuantite = a_oSku.getPropertyValue("quantiteLimitee").toString();
                l_quantiteLimitee = false;
                if(l_limitationDeQuantite != null)
                {
                    l_quantiteLimitee = (Boolean.valueOf(l_limitationDeQuantite)).booleanValue();
                }
            }
            
        }
       
        // On utilise deux variables en Calendar car cet objet permet de manipuler les mois plus facilement
        Calendar l_dFinVie = Calendar.getInstance();
        if (l_dDateFinVie != null)
        {
            l_dFinVie.setTime(l_dDateFinVie);
            l_dFinVie.add(Calendar.MONTH, MOINS_SIX);
        }
        else
        {
            l_dFinVie = null;
        }
        Calendar l_dCourante = Calendar.getInstance();
        l_dCourante.setTime(l_dDateCourante);

        boolean l_ret= false;
        if(l_dFinVie != null && ((l_dCourante.getTime()).after(l_dFinVie.getTime())))
        {
            // je veux limiter la quantité
            l_ret = true;
        }
        else if(l_nRetraitCodifie != null && RUPTURE_MOMENTANEE_FOURNISSEUR == l_nRetraitCodifie.intValue())
        {
            // je veux limiter la quantité
            l_ret = true;
        }
        else if(l_quantiteLimitee)
        {
            // je veux limiter la quantité
            l_ret = true;
        }
        //je ne veux pas  limiter la quantité
        return l_ret;
    }
    
    /**.
     * @param       a_shippingGroups            list des shipping groups
     * @param       a_oSku                      Sku visité
     * @param       a_StockMini                 Stock pivot changeant le délai
     * @param       a_sProvenance               a_sProvenance
     * @return      double                      double                      
     */
    public double getQteEncoreCommandable(List a_shippingGroups, RepositoryItem a_oSku, double a_StockMini, String a_sProvenance)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoAffichageDisponibiliteManager.getQteEncoreCommandable().");
        }
        
        String l_IdSku = (String)a_oSku.getPropertyValue(ID);
        
        int l_size=0;
        if(a_shippingGroups != null )
            l_size= a_shippingGroups.size();
        
        // l_qteCommandable est la quantité de l'objet consulté qui sera commandable
        // valeur que l'on retournera après la boucle
        // On l'initialise avec la valeur de base commandable suite aux contraintes des packs.
        double l_qteCommandable = a_StockMini;
        
        //récupération des CommerceItemRelationships du ShippingGroup en cours
        for (int l_i = 0; l_i < l_size; l_i++)
        {
            List l_CommerceItemsRelationShips = ((ShippingGroup) a_shippingGroups.get(l_i)).getCommerceItemRelationships();
            for (int l_j = 0 ; l_j < l_CommerceItemsRelationShips.size() ; l_j++)
            {
                // récupération du CommerceItemRelationShip en cours
                CommerceItemRelationship l_commerceItemRelationShip = (CommerceItemRelationship)l_CommerceItemsRelationShips.get(l_j);
                
                // récupération du CommerceItem en cours
                CommerceItem l_commerceItem = l_commerceItemRelationShip.getCommerceItem();
                
                // récupération du SKU du CommerceItem en cours et dispatchage en faction du type d'expédition      
                try
                {
                    // Avertissement : On peut être dans le Cas 1 ou 2 mais dans le Cas 1 et 2
                    RepositoryItem l_aArticleMaitre = getRepository().getItem(l_commerceItem.getCatalogRefId(),"casto_sku");
                    String l_tmpSku = (String)l_aArticleMaitre.getPropertyValue(ID);

                    List l_aArticlesEsclaves = (List) a_oSku.getPropertyValue(BUNDLE_LINKS);
                    
                    // Cas 1 : l'objet consulté "non pack" est contenu dans le panier (Pack ou non pack)
                    // L'id de l'objet consulté est le même l'objet du panier que l'on étudie est un pack. 
                    if(l_IdSku.equals(l_tmpSku))
                    {
                        l_qteCommandable -= l_commerceItem.getQuantity();
                    }
                    // Cas 3 : L'objet consulte est un pack
                    else if( l_aArticlesEsclaves!= null && l_aArticlesEsclaves.size() > 0)
                    {
                        
                        for(Iterator l_it = l_aArticlesEsclaves.iterator();l_it.hasNext();)
                        {
                            RepositoryItem l_SkuLink = (RepositoryItem)l_it.next();
                            RepositoryItem l_Sku = (RepositoryItem)l_SkuLink.getPropertyValue(ITEM);
                            double l_quantiteDemandePourUnPack = ((Long)l_SkuLink.getPropertyValue(QTE)).doubleValue();
                            // Le pack contenu dans le panier contient l'objet consulté
                            
                            // MC 080226 : si on vient du pré-panier alors on utilise l'InventoryRepository
                            int l_nStatus = 0;
                            if(a_sProvenance.equals(PANIER))
                            {
                                l_nStatus = getInventoryAdapter().getInventoryManager().queryAvailabilityStatus(l_Sku.getPropertyValue(ID).toString());
                            }
                            else
                            {
                                l_nStatus = getInventoryAdapter().getCachedInventoryManager().queryAvailabilityStatus(l_Sku.getPropertyValue(ID).toString());
                            }
                            
                            if((l_Sku.getPropertyValue(ID)).equals(l_commerceItem.getCatalogRefId()) && isCommandableQuantiteLimitee(l_nStatus, l_Sku)) 
                                //isCommandableQuantiteLimiteeUnArticle
                            {
                                l_qteCommandable -= Math.ceil(l_commerceItem.getQuantity()/l_quantiteDemandePourUnPack) ;
                            }
                        }
                    }
                   
                    // Cas 2 : l'objet consulté n'est pas un pack mais un pack contenu dans le panier le contient
                    // (En gros, les objets sur lesquels on travaille n'ont pas le même id mais pourtant la qté de l'un
                    // impacte l'autre.
                    l_aArticlesEsclaves = null;
                    // Ici on regarde si on a un pack.
                    if(l_aArticleMaitre != null) /* Est-on dans un pack ? oui, si bundleLinks !=null ou si bundlelinks.size() != 0*/
                        l_aArticlesEsclaves = (List) l_aArticleMaitre.getPropertyValue(BUNDLE_LINKS);
                        
                    // L'objet du panier que l'on étudie est un pack. 
                    if(l_aArticlesEsclaves != null && l_aArticlesEsclaves.size() > 0)
                    {
                        // Il faut donc balayer ce pack et voir s'il contient l'objet consulté
                        for(Iterator l_it = l_aArticlesEsclaves.iterator();l_it.hasNext();)
                        {
                            RepositoryItem l_SkuLink = (RepositoryItem)l_it.next();
                            RepositoryItem l_Sku = (RepositoryItem)l_SkuLink.getPropertyValue(ITEM);
                            double l_quantiteDemandePourUnPack = ((Long)l_SkuLink.getPropertyValue(QTE)).doubleValue();
                            l_tmpSku = (String)l_Sku.getPropertyValue(ID);
                            
                            // Le pack contenu dans le panier contient l'objet consulté
                            
                            // MC 080226 : si on vient du pré-panier alors on utilise l'InventoryRepository
                            int l_nStatus = 0;
                            if(a_sProvenance.equals(PANIER))
                            {
                                l_nStatus = getInventoryAdapter().getInventoryManager()
                                    .queryAvailabilityStatus(l_aArticleMaitre.getPropertyValue(ID).toString());
                            }
                            else
                            {
                                l_nStatus = getInventoryAdapter().getCachedInventoryManager()
                                    .queryAvailabilityStatus(l_aArticleMaitre.getPropertyValue(ID).toString());
                            }

                            if(l_IdSku.equals(l_tmpSku) && isCommandableQuantiteLimitee(l_nStatus, l_aArticleMaitre))
                            {
                                l_qteCommandable -= l_commerceItem.getQuantity()*l_quantiteDemandePourUnPack ;
                            }
                        }
                    }
                }
                catch (RepositoryException l_repositoryException)
                {
                    if (isLoggingError())
                    {
                        logError("ERROR : erreur lors de la récupération d'un sku : ");
                        logError("DETAILS : "+l_repositoryException.getMessage());
                    }
                }
                catch (InventoryException l_repositoryException)
                {
                    if (isLoggingError())
                    {
                        logError("ERROR : erreur lors de la récupération d'un sku : ");
                        logError("DETAILS : "+l_repositoryException.getMessage());
                    }
                }
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoAffichageDisponibiliteManager.getQteEncoreCommandable().");
        }

        if(l_qteCommandable < 0)
            l_qteCommandable = 0.0;
        
        return l_qteCommandable;
    }
    
    /**.
     * 
     * Calcul des delais a l'avance pour switcher en javascript sur 
     * l'un ou l'autre des libelles
     * @param       a_sku           Sku en question
     * @param       a_stock         Stock de l'article en question
     * @param       a_StockMini     Stock faisant changer le délai
     * @param       a_sProvenance   FP, Panier, etc..
     * @param       a_shippingGroups List
     * @param       a_oRequest      DynamoHttpServletRequest
     * @param       a_oResponse     DynamoHttpServletRequest
     */
    public void preCalculDelais (List a_shippingGroups, RepositoryItem a_sku, double a_stock, double a_StockMini, String a_sProvenance,
            DynamoHttpServletRequest a_oRequest, DynamoHttpServletResponse a_oResponse)
    {
        if (isLoggingDebug())
        {
            logDebug("Debut de la fonction preCalculDelais");
        }

        a_oRequest.setParameter("libelleDispoMin", calculDelaiLibelle(
                a_sku, DISPONIBLE, 0, a_sProvenance));

        a_oRequest.setParameter("libelleDispoMax", calculDelaiLibelle(
                a_sku, DISPONIBLE, a_stock + 1, a_sProvenance));
        
        a_oRequest.setParameter("stock", ""+getQteEncoreCommandable(a_shippingGroups, a_sku, a_StockMini, a_sProvenance));    

        if (isLoggingDebug())
        {
            logDebug("Fin de la fonction preCalculDelais");
        }
    }
    
    /**
     * Détermine la provenance.
     * 
     * @param a_oRequest
     *            DynamoHttpServletRequest
     * @return provenance String
     */
    public String recuperationProvenance(DynamoHttpServletRequest a_oRequest)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoAffichageDisponibiliteManager.recuperationProvenance(DynamoHttpServletRequest)");
        }

        String l_ret;

        if (a_oRequest.getParameter("provenance") != null)
        {
            // MC 080226 : si on vient du pré-panier alors on utilise
            // l'InventoryRepository
            if (a_oRequest.getParameter("provenance").equals(PANIER))
            {
                l_ret = PANIER;
            }
            else
            {
                l_ret = "livrer";
            }
        }
        else
        {
            l_ret = DISPONIBLE;
        }

        if (isLoggingInfo())
        {
            logInfo("CastoAffichageDisponibiliteManager.recuperationProvenance(DynamoHttpServletRequest) : provenance : "
                    + l_ret);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoAffichageDisponibiliteManager.recuperationProvenance(DynamoHttpServletRequest)");
        }

        return l_ret;
    }
    
    /**
     * Détermine la quantité.
     * 
     * @param l_quantite
     *            l_quantite
     * @return double double
     */
    public double parseQuantite(String l_quantite)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.DropletAffichageDisponibilite.parseQuantite(DynamoHttpServletRequest)");
        }
        double l_nQuantite = 1;
        if (l_quantite != null && !"".equals(l_quantite))
        {
            try
            {
                l_nQuantite = Double.parseDouble(l_quantite);
                if (isLoggingInfo())
                {
                    logInfo("DropletAffichageDisponibilite.calculDelaiLivraison(...) : quantité : " + l_nQuantite);
                }
            }
            catch (NumberFormatException l_exception)
            {
                if (isLoggingError())
                {
                    logError(l_exception);
                }
            }
        }
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.DropletAffichageDisponibilite.parseQuantite(DynamoHttpServletRequest)");
        }
        
        return l_nQuantite;
    }
}