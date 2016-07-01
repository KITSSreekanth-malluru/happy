package com.castorama.produit;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.MissingInventoryItemException;
import atg.commerce.inventory.RepositoryInventoryManager;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.castorama.category.CastoCategoryManager;
import com.castorama.commande.CastoHardgoodShippingGroup;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.constantes.CastoConstantesFicheProduit;
import com.castorama.config.Configuration;
import com.castorama.inventory.InventoryAdapter;
import com.castorama.pricing.CastoPriceInfo;
import com.castorama.profil.CastoProfileManager;

/**
 * Composant d'utilitaire pour la gestion des produits.
 * 
 * @author Florte Jérémy
 * @version 0.1
 * 
 */
public class CastoProductTools extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    /** . Constante statique * */
    public static final int NEG = -1;

    /** . Constante statique * */
    public static final int SIX = 6;

    /** . Constante statique * */
    public static final int PRODUIT_RETIRE_DE_LA_VENTE = 2;
    
    /** . Constante statique * */
    public static final int PRODUIT_DANGEREUX = 1;

    /** . Constante statique * */
    public static final int RUPTURE_MOMENTANEE_FOURNISSEUR = 3;

    /** . Constante statique * */
    public static final int TYPE_VUM = 3;

    /** . Constante statique * */
    private static final int TROIS = 3;

    /** . Constante statique * */
    private static final int CENT = 100;

    /** . Constante statique * */
    private static final int QUARANTE_HUIT = 48;

    /** . Constante statique * */
    private static final String PROPERTY_SERVICES_SKU = "services";

    /** . Constante statique * */
    private static final String BUNDLE_LINKS = "bundleLinks";

    /** . Constante statique * */
    private static final String ID = "id";

    /** . Constante statique * */
    private static final int QUATRE = 4;

    /** . Constante statique * */
    private static final int HUIT = 8;

    /** . Constante statique * */
    private static final int CINQ = 5;

    /** . Constante statique * */
    private static final int SEPT = 7;

    /** . Constante statique * */
    private static final int DIX = 10;

    /** . Constante statique * */
    private static final int MILLE_NEUF_CENT = 1900;

    /** . Constante statique * */
    private static final String PRDM = "PRDm";
    
    /**
     * CODE_ERREUR.
     */
    private static final int CODE_ERREUR = NEG;

    /*
     * ------------------------------------------------------------------------
     * Atttributs
     * ------------------------------------------------------------------------
     */

    private Repository m_catalogRepository;

    private Repository m_magasinRepository;

    private Locale m_locale;

    private DecimalFormat m_decimalFormat;

    private PriceListManager m_priceListManager;

    private CastoProfileManager m_profileManager;

    private CastoAffichageDisponibiliteManager m_dispoManager;

    private InventoryAdapter m_inventoryAdapter;
    
    private Configuration m_configuration;

    /** . Inventory manager * */
    // private InventoryManager m_oInventoryManager;
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /**
     * @return the dispoManager
     */
    public CastoAffichageDisponibiliteManager getDispoManager()
    {
        return m_dispoManager;
    }

    /**
     * @param a_dispoManager
     *            the dispoManager to set
     */
    public void setDispoManager(CastoAffichageDisponibiliteManager a_dispoManager)
    {
        m_dispoManager = a_dispoManager;
    }

    /**
     * Renvoie une référence vers le repository magasin.
     * 
     * @return Une référence vers le repository magasin.
     */
    public Repository getMagasinRepository()
    {
        return m_magasinRepository;
    }

    /**
     * Fixe la référence vers le repository magasin.
     * 
     * @param a_magasinRepository
     *            référence vers le repository magasin.
     */
    public void setMagasinRepository(Repository a_magasinRepository)
    {
        m_magasinRepository = a_magasinRepository;
    }

    /**
     * Renvoie une référence vers le repository du catalogue.
     * 
     * @return Une référence vers le repository du catalogue.
     */
    public Repository getCatalogRepository()
    {
        return m_catalogRepository;
    }

    /**
     * Fixe la référence vers le repository du catalogue.
     * 
     * @param a_catalogRepository
     *            référence vers le repository du catalogue.
     */
    public void setCatalogRepository(Repository a_catalogRepository)
    {
        m_catalogRepository = a_catalogRepository;
    }

    /**
     * . Renvoie une référence vers la classe utilitaire InventoryAdapter
     * 
     * @return Une instance de InventoryAdapter.
     */
    public InventoryAdapter getInventoryAdapter()
    {
        return m_inventoryAdapter;
    }

    /**
     * . Fixe la référence à InventoryAdapter
     * 
     * @param a_inventoryAdapter
     *            une instance de InventoryAdapter.
     */
    public void setInventoryAdapter(InventoryAdapter a_inventoryAdapter)
    {
        m_inventoryAdapter = a_inventoryAdapter;
    }

    /**
     * Méthode qui renvoie une référence vers la locale en cours.
     * 
     * @return Un objet Locale.
     */
    public Locale getLocale()
    {
        return m_locale;
    }

    /**
     * Méthode qui fixe la référence vers la locale en cours.
     * 
     * @param a_locale
     *            la nouvelle locale.
     */
    public void setLocale(Locale a_locale)
    {
        m_locale = a_locale;
    }

    /**
     * Renvoie une référence vers le format employé.
     * 
     * @return Une instance de DecimalFormat.
     */
    public DecimalFormat getDecimalFormat()
    {
        if (null == m_decimalFormat)
        {
            m_decimalFormat = new DecimalFormat("0.00", new DecimalFormatSymbols(getLocale()));

            m_decimalFormat.setGroupingSize(TROIS);
            m_decimalFormat.setGroupingUsed(true);
        }
        return m_decimalFormat;
    }

    /**
     * Fixe le format employé.
     * 
     * @param a_decimalFormat
     *            une instance de DecimalFormat.
     */
    public void setDecimalFormat(DecimalFormat a_decimalFormat)
    {
        m_decimalFormat = a_decimalFormat;
    }

    /**
     * Renvoie une référence vers le composant de gestion des price lists.
     * 
     * @return une référence vers le composant de gestion des price lists.
     */
    public PriceListManager getPriceListManager()
    {
        return m_priceListManager;
    }

    /**
     * Fixe la référence vers le composant de gestion des price lists..
     * 
     * @param a_priceListManager
     *            La nouvelle référence vers le composant de gestion des price
     *            lists.
     */
    public void setPriceListManager(PriceListManager a_priceListManager)
    {
        m_priceListManager = a_priceListManager;
    }

    /**
     * Méthode qui renvoie une référence vers le gestionnaire de profil.
     * 
     * @return Une référence vers le gestionnaire de profil.
     */
    public CastoProfileManager getProfileManager()
    {
        return m_profileManager;
    }

    /**
     * Méthode qui fixe la référence vers le gestionnaire de projet.
     * 
     * @param a_profileManager
     *            Le nouveau gestionnaire de projet.
     */
    public void setProfileManager(CastoProfileManager a_profileManager)
    {
        m_profileManager = a_profileManager;
    }
    
    /**
     * Méthode qui renvoie une référence vers le gestionnaire de configuration.
     * @return m_configuration 
     *                 Une référence vers le gestionnaire de configuration
     */
    public Configuration getConfiguration()
    {
        return m_configuration;
    }

    /**
     * Méthode qui fixe une référence vers le gestionnaire de configuration.
     * @param a_configuration a_configuration
     *                  La référence vers le gestionnaire de configuration
     */
    public void setConfiguration(Configuration a_configuration)
    {
        m_configuration = a_configuration;
    }
    

    /*
     * ------------------------------------------------------------------------ [
     * Méthodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Méthode qui renvoie les informations de prix pour un sku d'un produit
     * donné.
     * 
     * @param a_profile
     *            Profil de l'internaute.
     * @param a_product
     *            Produit.
     * @param a_sku
     *            Sku.
     * 
     * @return un objet CastoPriceInfo ou null en cas d'échec.
     */
    public CastoPriceInfo getPrix(Profile a_profile, RepositoryItem a_product, RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getPrix(Profile, RepositoryItem, RepositoryItem)");
        }

        CastoPriceInfo l_priceInfo;

        if (null != a_profile)
        {
            l_priceInfo = new CastoPriceInfo();

            // mantis 1334: récupération de la priceList
            l_priceInfo.setListPrice(getPrixPriceList(a_profile, a_product, a_sku, false));

            /*
             * On vérifie si session Castorama Direct ou magasin
             */
            if (!getProfileManager().isSessionCastoramaDirect(a_profile))
            {
                // Session magasin, pas de gestion des prix barrés
                l_priceInfo.setOnSale(false);
            }
            else
            {
                // Session castorama direct

                // On vérifie les prix barrés
                l_priceInfo.setOnSale(hasPrixBarre(a_sku));

                if (l_priceInfo.isOnSale())
                {
                    //mantis 1334: récupération du saleprice dans la priceList
                    l_priceInfo.setSalePrice(getPrixPriceList(a_profile, a_product, a_sku, true));
                }
            }
        }
        else
        {
            l_priceInfo = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getPrix(Profile, RepositoryItem, RepositoryItem)");
        }

        return l_priceInfo;
    }

    /**
     * Méthode qui calcule le prix d'un sku en se basant sur les price list ATG.
     * 
     * @param a_profile
     *            Profil de l'internaute.
     * @param a_product
     *            Produit concerné.
     * @param a_sku
     *            Sku concerné.
     * 
     * @return le prix du sku.
     */
//  mantis 1334: ajout du booléen pour récupérer la PriceList prix soldés ou non
    public double getPrixPriceList(Profile a_profile, RepositoryItem a_product, RepositoryItem a_sku, boolean a_sale)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getPrixPriceList(Profile, RepositoryItem, RepositoryItem)");
        }

        double l_montant = 0;

        if (null != a_profile)
        {
            RepositoryItem l_priceList;
            RepositoryItem l_montantPriceList;

//          mantis 1334: récupération de la PriceList prix soldés ou non
            if (a_sale)
                l_priceList = (RepositoryItem) a_profile.getPropertyValue(CastoConstantesDefense.PROPERTY_SALE_PRICE_LIST);
            else
                l_priceList = (RepositoryItem) a_profile.getPropertyValue(CastoConstantesDefense.PROPERTY_PRICE_LIST);
            
            if (null != l_priceList)
            {
                try
                {
                    // Appel composants ATG pour récupérer le prix associé
                    l_montantPriceList = getPriceListManager().getPrice(l_priceList, a_product, a_sku, true);

                    if (null != l_montantPriceList)
                    {
                        l_montant = ((Double) l_montantPriceList
                                .getPropertyValue(CastoConstantesFicheProduit.LIST_PRICE)).doubleValue();

                    }
                    else
                    {
                        if (isLoggingError())
                        {
                            logError("Pas de prix dans la priceList " + l_priceList + " pour : produit=" + a_product + ", sku=" + a_sku);
                        }
                    }
                }
                catch (PriceListException l_ple)
                {
                    logError("CastoProductTools.getPrixPriceList(Profile, RepositoryItem, RepositoryItem) : "
                            + l_ple.toString());
                }
            }
            else
            {
                logError("CastoProductTools.getPrixPriceList(Profile, RepositoryItem, RepositoryItem) : pas de price lists défini.");
            }
        }
        else
        {
            logError("CastoProductTools.getPrixPriceList(Profile, RepositoryItem, RepositoryItem) : l'objet Profile fourni est null.");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getPrixPriceList(Profile, RepositoryItem, RepositoryItem)");
        }

        return l_montant;
    }

    /**
     * Méthode qui calcule le prix d'un sku en se basant sur la price list.
     * passee en parametre
     * 
     * @param a_idPriceList
     *            l'identifiant de la price list a utiliser
     * @param a_sku
     *            Sku concerné.
     * 
     * @return le prix du sku ou null s'il y a eu une erreur
     * @exception IllegalArgumentException
     *                si les parametres sont vides
     */
    public CastoPriceInfo getPrixPriceList(String a_idPriceList, RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getPrixPriceList(String a_idPriceList, RepositoryItem a_sku)");
        }

        // -------------------------------------------------
        // Tests des parametres
        // -------------------------------------------------
        if (a_sku == null)
        {
            throw new IllegalArgumentException("a_sku vide");
        }

        if (a_idPriceList == null || "".equals(a_idPriceList))
        {
            throw new IllegalArgumentException("a_idPriceList vide");
        }

        // -------------------------------------------------

        CastoPriceInfo l_priceInfo = null;

        try
        {
            // -------------------------------------------------
            // On recupere la priceList demandee
            // -------------------------------------------------
            RepositoryItem l_priceList = getPriceListManager().getPriceList(a_idPriceList);

            if (l_priceList == null)
            {
                if (isLoggingDebug())
                {
                    logDebug("ERR: priceList '" + a_idPriceList + "' inexistante.");
                }
                return null;
            }

            // -------------------------------------------------
            // On recupere le prix du sku de la priceList
            // -------------------------------------------------
            RepositoryItem l_montantPriceList = null;

            // On recupere le produit du sku (pour utiliser
            // getPriceListManager().getPrice()
            Collection l_produits = (Collection) a_sku.getPropertyValue("parentProducts");
            RepositoryItem l_product;

            for (Iterator l_it = l_produits.iterator(); l_it.hasNext();)
            {
                l_product = (RepositoryItem) l_it.next();

                if (isLoggingDebug())
                {
                    logDebug("# Product associe (un seul normalement) : " + l_product);
                }

                l_montantPriceList = getPriceListManager().getPrice(l_priceList, l_product, a_sku, true);
            }

            if (null == l_montantPriceList)
            {
                if (isLoggingDebug())
                {
                    logDebug("ERR: montantPriceList == null");
                }
                return null;
            }

            // On recupere le listPrice en double
            Object l_oMontantPriceList = l_montantPriceList.getPropertyValue(CastoConstantesFicheProduit.LIST_PRICE);
            if (l_oMontantPriceList == null)
            {
                if (isLoggingDebug())
                {
                    logDebug("ERR: l_oMontantPriceList == null, pas de priceList de la listPrice");
                }

                return null;
            }

            double l_montant = ((Double) l_oMontantPriceList).doubleValue();

            l_priceInfo = new CastoPriceInfo();
            l_priceInfo.setListPrice(l_montant);

            // -------------------------------------------------
            // On vérifie les prix barrés
            // -------------------------------------------------
            if (a_sku.getPropertyValue(CastoConstantesFicheProduit.ON_SALE) != null
                    && ((Boolean) a_sku.getPropertyValue(CastoConstantesFicheProduit.ON_SALE)).booleanValue())
            {
                l_priceInfo.setOnSale(true);
                if (a_sku.getPropertyValue(CastoConstantesFicheProduit.SALE_PRICE) != null)
                {
                    l_priceInfo.setSalePrice(((Double) a_sku.getPropertyValue(CastoConstantesFicheProduit.SALE_PRICE))
                            .doubleValue());
                }
                else
                {
                    if (isLoggingDebug())
                    {
                        logDebug("ERR: onSale actif, mais pas de salePrice");
                    }
                    return null;
                }
            }

        }
        catch (PriceListException l_e)
        {
            if (isLoggingDebug())
            {
                logDebug(l_e.getMessage());
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getPrixPriceList(String a_idPriceList, RepositoryItem a_sku)");
        }

        return l_priceInfo;
    }

    /**
     * Méthode qui recherche le plux petit prix parmi les skus d'un produit
     * donné.
     * 
     * @param a_profile
     *            Profil de l'internaute.
     * @param a_product
     *            produit concerné.
     * 
     * @return un montant, ou 0 en cas d'échec.
     */
    public double getPlusPetitPrixProduit(Profile a_profile, RepositoryItem a_product)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem)");
        }

        double l_prix = 0;

        if (null != a_product)
        {
            try
            {
                Object l_prop = a_product.getPropertyValue(CastoConstantesFicheProduit.CHILD_SKUS);

                if (null != l_prop)
                {
                    List l_skus = (List) l_prop;

                    double l_montant = 0;

                    // On parcourt les skus du produit
                    for (Iterator l_iterator = l_skus.iterator(); l_iterator.hasNext();)
                    {
                        l_montant = getPrixPriceList(a_profile, a_product, (RepositoryItem) l_iterator.next(),false);

                        if (isLoggingDebug())
                        {
                            logDebug("CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem) : produit : "
                                    + a_product.getRepositoryId() + ", montant : " + l_montant);
                        }

                        if (0 == l_prix || l_montant < l_prix)
                        {
                            l_prix = l_montant;
                        }
                    }
                }
            }
            catch (NullPointerException l_npe)
            {
                logError("CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem) : " + l_npe.toString());

                l_prix = 0;
            }
            catch (ClassCastException l_cce)
            {
                logError("CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem) : " + l_cce.toString());

                l_prix = 0;
            }
        }
        else
        {
            logError("CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem) : le produit est null.");

            l_prix = 0;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getPlusPetitPrixProduit(Profile, RepositoryItem)");
        }

        return l_prix;
    }

    /**
     * Méthode qui indique si une date est comprise dans les bornes passées en
     * paramètre. Le paramètre a_borneSup peut être null, dans ce cas, on
     * considère que la borne supérieure est indéfinie.
     * 
     * @param a_date
     *            La date à tester.
     * @param a_borneInf
     *            La borne inférieure.
     * @param a_borneSup
     *            La borne supérieure.
     * 
     * @return True si la date est comprise dans les bornes.
     */
    public boolean dateEstCompriseEntre(Date a_date, Date a_borneInf, Date a_borneSup)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.produit.CastoProductTools.dateEstCompriseEntre().");
        }

        boolean l_ret;

        if (null != a_date && null != a_borneInf)
        {
            l_ret = a_date.equals(a_borneInf) || a_date.after(a_borneInf);

            if (null != a_borneSup)
            {
                l_ret &= a_date.equals(a_borneSup) || a_date.before(a_borneSup);
            }
        }
        else
        {
            // Les paramètres ne sont pas convenablement renseignés.
            throw new IllegalArgumentException();
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.dateEstCompriseEntre().");
        }

        return l_ret;
    }

    /**
     * Méthode qui indique si un sku est en promotion ou non (via listePrice,
     * salePrice).
     * 
     * @param a_sku
     *            Sku concerné.
     * 
     * @return Renvoie true si une promotion est trouvée, false dans le cas
     *         contraire.
     */
    public boolean hasPrixBarre(RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.hasPrixBarre(RepositoryItem).");
        }

        boolean l_ret = null != a_sku.getPropertyValue(CastoConstantesFicheProduit.SALE_PRICE)
                && null != a_sku.getPropertyValue(CastoConstantesFicheProduit.ON_SALE)
                && ((Boolean) a_sku.getPropertyValue(CastoConstantesFicheProduit.ON_SALE)).booleanValue();

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.hasPrixBarre(RepositoryItem).");
        }

        return l_ret;
    }

    /**
     * Méthode qui indique si un CommerceItem a un prix barré ou non.
     * 
     * @param a_item
     *            CommerceItem.
     * 
     * @return Renvoie true si le CommerceItem a un prix barré, false dans le
     *         cas contraire.
     */
    public boolean hasPrixBarre(CommerceItem a_item)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.hasPrixBarre(CommerceItem).");
        }

        boolean l_ret;

        if (null == a_item)
        {
            logError("CastoProductTools.hasPrixBarre(CommerceItem) : CommerceItem est null.");

            l_ret = false;
        }
        else
        {
            AuxiliaryData l_auxiliaryData = a_item.getAuxiliaryData();

            if (null == l_auxiliaryData)
            {
                logError("CastoProductTools.hasPrixBarre(CommerceItem) : AuxiliaryData est null.");

                l_ret = false;
            }
            else
            {
                Object l_catalogRef = l_auxiliaryData.getCatalogRef();

                try
                {
                    Boolean l_onSale = (Boolean) DynamicBeans.getPropertyValue(l_catalogRef,
                            CastoConstantesFicheProduit.ON_SALE);

                    Double l_salePrice = (Double) DynamicBeans.getPropertyValue(l_catalogRef,
                            CastoConstantesFicheProduit.SALE_PRICE);

                    l_ret = null != l_salePrice && null != l_onSale && l_onSale.booleanValue();
                }
                catch (PropertyNotFoundException l_pnfe)
                {
                    logError("CastoProductTools.hasPrixBarre(CommerceItem) : " + l_pnfe);

                    l_ret = false;
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.hasPrixBarre(CommerceItem).");
        }

        return l_ret;
    }

    /**
     * Méthode qui renvoie le prix barré pour un CommerceItem.
     * 
     * @param a_item
     *            CommerceItem.
     * 
     * @return Renvoie Le prix barré ou 0 en cas d'échec.
     */
    public double getPrixBarre(CommerceItem a_item)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.produit.CastoProductTools.getPrixBarre(Object).");
        }

        double l_prix;

        if (null == a_item)
        {
            logError("CastoProductTools.getPrixBarre(Object) : CommerceItem est null.");

            l_prix = 0;
        }
        else
        {
            AuxiliaryData l_auxiliaryData = a_item.getAuxiliaryData();

            if (null == l_auxiliaryData)
            {
                logError("CastoProductTools.getPrixBarre(Object) : AuxiliaryData est null.");

                l_prix = 0;
            }
            else
            {
                try
                {
                    Double l_salePrice = (Double) DynamicBeans.getPropertyValue(l_auxiliaryData.getCatalogRef(),
                            CastoConstantesFicheProduit.SALE_PRICE);

                    if (null == l_salePrice)
                    {
                        l_prix = 0;
                    }
                    else
                    {
                        l_prix = l_salePrice.doubleValue();
                    }
                }
                catch (PropertyNotFoundException l_pnfe)
                {
                    logError("CastoProductTools.getPrixBarre(Object) : " + l_pnfe);

                    l_prix = 0;
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.getPrixBarre(Object).");
        }

        return l_prix;
    }

    /**
     * Méthode qui permet de formater un prix.
     * 
     * @param a_prix
     *            Le prix à formater.
     * 
     * @return Un prix formaté ou une chaîne vide en cas d'échec.
     */
    public String formaterPrix(String a_prix)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.produit.CastoProductTools.formaterPrix(String).");
        }

        String l_formate;
        double l_value;

        try
        {
            // Conversion de la valeur passée en paramètre en double puis
            // formatage.
            l_value = new Double((null == a_prix) ? "0" : a_prix).doubleValue();

            l_formate = formaterPrix(l_value);
        }
        catch (NumberFormatException l_nfe)
        {
            logError("com.castorama.produit.CastoProductTools.formaterPrix(double) : Impossible de convertir en nombre la chaîne passée en entrée : "
                    + a_prix);

            l_formate = "";
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.formaterPrix(String).");
        }

        return l_formate;
    }

    /**
     * Méthode qui permet de formater un prix.
     * 
     * @param a_prix
     *            Le prix à formater.
     * 
     * @return Un prix formaté ou une chaîne vide en cas d'échec.
     */
    public String formaterPrix(double a_prix)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.produit.CastoProductTools.formaterPrix(double).");
        }

        String l_formate;

        try
        {
            l_formate = getDecimalFormat().format(
                    new Double((new Long(Math.round(a_prix * CENT)).doubleValue()) / CENT));
        }
        catch (IllegalArgumentException l_iae)
        {
            logError("com.castorama.produit.CastoProductTools.formaterPrix(double) : " + l_iae.toString());

            l_formate = "";
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.formaterPrix(double).");
        }

        return l_formate;
    }

    /**
     * Méthode qui renvoie la référence produit en fonction de l'identifiant de
     * sku ou de produit fourni. Si l'identifiant fourni est un identifiant de
     * produit, renvoie l'identifiant de produit. Si l'identifiant fourni est un
     * identifiant de sku, renvoie l'identifiant du premier produit parent.
     * Sinon, renvoie null.
     * 
     * @param a_id
     *            Identifiant de produit ou de sku.
     * 
     * @return Un identifiant de produit ou null en cas d'échec.
     */
    public String getReferenceProduit(String a_id)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.produit.CastoProductTools.getReferenceProduit().");
        }

        String l_id;

        try
        {
            if (isLoggingDebug())
            {
                logDebug("--> cherche la référence produit pour le n°: " + a_id);
            }

            RepositoryItem l_item = getCatalogRepository().getItem(a_id, CastoConstantes.DESCRIPTEUR_CASTO_PRODUCT);

            if (null != l_item)
            {
                l_id = a_id;
            }
            else
            {
                l_item = getCatalogRepository().getItem(a_id, CastoConstantes.DESCRIPTEUR_CASTO_SKU);

                if (null != l_item)
                {
                    Set l_parents = (Set) l_item.getPropertyValue(CastoConstantesFicheProduit.PARENT_PRODUCTS);

                    if (null != l_parents && !l_parents.isEmpty())
                    {
                        l_id = ((RepositoryItem) l_parents.iterator().next()).getRepositoryId();
                    }
                    else
                    {
                        l_id = null;
                    }
                }
                else
                {
                    l_id = null;
                }
            }
        }
        catch (RepositoryException l_re)
        {
            logError("CastoProductTools.getReferenceProduit() : " + l_re.toString());

            l_id = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.getReferenceProduit().");
        }

        return l_id;
    }

    /**
     * Méthode qui renvoie la liste des vignettes pour une liste de produits
     * donnée.
     * 
     * @param a_listeProduits
     *            La liste de produits.
     * 
     * @return Une liste de vignettes.
     */
    public List getListeVignettes(List a_listeProduits)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getListeVignettes(List).");
        }

        List l_vignettes = new ArrayList();

        for (Iterator l_iterator = a_listeProduits.iterator(); l_iterator.hasNext();)
        {
            RepositoryItem l_product = (RepositoryItem) l_iterator.next();
            RepositoryItem l_image = (RepositoryItem) l_product.getPropertyValue("thumbnailImage");

            if (l_image != null)
            {
                l_vignettes.add(l_image.getPropertyValue("url").toString());
            }
            else
            {
                l_vignettes.add("/images/img-produit-blank.jpg");
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getListeVignettes(List).");
        }

        return l_vignettes;
    }

    /**
     * Méthode qui renvoie la liste des descriptions pour une liste de produits
     * donnée.
     * 
     * @param a_listeProduits
     *            La liste de produits.
     * 
     * @return Une liste de descriptions.
     */
    public List getListeDescriptions(List a_listeProduits)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getListeDescriptions(List).");
        }

        List l_listeDescription = new ArrayList();

        for (Iterator l_iterator = a_listeProduits.iterator(); l_iterator.hasNext();)
        {
            RepositoryItem l_item = (RepositoryItem) l_iterator.next();
            Object l_desc = l_item.getPropertyValue(CastoConstantesFicheProduit.DESCRIPTION);

            if (null != l_desc)
            {
                String l_description = (String) l_desc;

                // Formatage de la description si trop longue
                if (l_description.length() > QUARANTE_HUIT)
                {
                    String l_sDescriptionFormatte = l_description.substring(0, QUARANTE_HUIT);

                    l_listeDescription.add(l_sDescriptionFormatte);
                }
                else
                {
                    l_listeDescription.add(l_description);
                }
            }
            else
            {
                l_listeDescription.add(new String(""));
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getListeDescriptions(List).");
        }

        return l_listeDescription;
    }

    /**
     * Méthode qui renvoie la liste des produits dont les dates de début et de
     * fin sont correctes.
     * 
     * @param a_listeProduits
     *            Liste des produits.
     * @param a_dateDuJour
     * 
     * @return Une liste de produits.
     */
    public List getListeProduitsEnCours(List a_listeProduits)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getListeProduitsEnCours(List).");
        }

        List l_listeProdTg = new ArrayList();
        Date l_dateDuJour = new Date();

        for (Iterator l_iterator = a_listeProduits.iterator(); l_iterator.hasNext();)
        {
            RepositoryItem l_oProdEnCours = (RepositoryItem) l_iterator.next();

            if (null == l_oProdEnCours.getPropertyValue("startDate")
                    || null == l_oProdEnCours.getPropertyValue("endDate"))
            {
                l_listeProdTg.add(l_oProdEnCours);
            }
            else
            {
                Date l_dateDebut = (Date) l_oProdEnCours.getPropertyValue("startDate");
                Date l_dateFin = (Date) l_oProdEnCours.getPropertyValue("endDate");

                /*
                 * on teste si la date du jour est comprise entre les date de
                 * debut et fin
                 */
                if (l_dateDebut == null || l_dateFin == null
                        || dateEstCompriseEntre(l_dateDuJour, l_dateDebut, l_dateFin))
                {
                    l_listeProdTg.add(l_oProdEnCours);
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getListeProduitsEnCours(List).");
        }

        return l_listeProdTg;
    }

    /**
     * Méthode qui renvoie le sku contenant les données à afficher (celui
     * contenant la promotion la plus avantageuse).
     * 
     * @param a_product
     *            Le produit parent.
     * 
     * @return Un objet de type RepositoryItem ou null si aucune promotion n'est
     *         active pour les skus de ce produit ou en cas d'échec.
     */
    public RepositoryItem getSkuPromotion(RepositoryItem a_product)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getSkuPromotion(RepositoryItem).");
        }

        RepositoryItem l_item = null;

        if (null != a_product)
        {
            try
            {
                Object l_prop = a_product.getPropertyValue(CastoConstantesFicheProduit.CHILD_SKUS);

                if (null != l_prop)
                {
                    List l_skus = (List) l_prop;

                    double l_montantPromo = 0;

                    // On parcourt les skus du produit
                    for (Iterator l_iterator = l_skus.iterator(); l_iterator.hasNext();)
                    {
                        RepositoryItem l_sku = (RepositoryItem) l_iterator.next();

                        // On vérifie si le sku dispose ou non d'une promotion
                        if (hasPrixBarre(l_sku))
                        {
                            // Le sku dispose d'une promotion, on vérifie s'il
                            // s'agit ou non de la plus avantageuse.
                            double l_montant = ((Double) l_sku.getPropertyValue(CastoConstantesFicheProduit.SALE_PRICE))
                                    .doubleValue();

                            if (0 == l_montantPromo || l_montant < l_montantPromo)
                            {
                                l_item = l_sku;
                            }
                        }
                    }
                }
            }
            catch (NullPointerException l_npe)
            {
                logError("CastoProductTools.getSkuPromotion(RepositoryItem) : " + l_npe.toString());

                l_item = null;
            }
            catch (ClassCastException l_cce)
            {
                logError("CastoProductTools.getSkuPromotion(RepositoryItem) : " + l_cce.toString());

                l_item = null;
            }
        }
        else
        {
            l_item = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getSkuPromotion(RepositoryItem).");
        }

        return l_item;
    }

    /**
     * Méthode qui indique si un sku est détenu ou non dans le magasin passé en
     * paramètre.
     * 
     * @param a_sku
     *            RepositoryItem du sku.
     * @param a_idMagasin
     *            Identifiant du magasin.
     * 
     * @return True si le sku est détenu dans le magasin passé en paramètres,
     *         false sinon.
     */
    public boolean skuEstDetenu(RepositoryItem a_sku, int a_idMagasin)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.services.CastoServicesTools.skuEstDetenu(RepositoryItem, int).");
        }

        boolean l_estDetenu = false;

        RepositoryItem l_sku = a_sku;

        if (a_idMagasin == CastoConstantesDefense.CASTORAMA_DIRECT_INT)
        {
            l_estDetenu = true;
        }
        else
        {
            if (null != l_sku)
            {

                if (null != l_sku)
                {
                    Map l_infos = (Map) l_sku.getPropertyValue(CastoConstantesDefense.INFOS);
                    String l_idMag = String.valueOf(a_idMagasin);
                    if (l_infos.containsKey(l_idMag))
                    {
                        RepositoryItem l_donnees = (RepositoryItem) l_infos.get(l_idMag);
                        if (null != l_donnees)
                        {
                            Boolean l_Detenu = (Boolean) l_donnees.getPropertyValue("Articledetenus");
                            l_estDetenu = l_Detenu.booleanValue();
                        }
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.services.CastoServicesTools.skuEstDetenu(RepositoryItem, int).");
        }

        return l_estDetenu;
    }

    /**
     * Méthode qui liste pour un produit d’un magasin 
     * tous les services disponibles pour au moins un de ses skus.
     * 
     * 
     * @param a_IdProduit
     *            Le produit parent.
     * @param a_IdMagasin
     *            Le magasin.
     * @return Un objet de type RepositoryItem ou null si aucune promotion n'est
     *         active pour les skus de ce produit ou en cas d'échec.
     */
    public List getServicesProduit(String a_IdProduit, int a_IdMagasin)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getServicesProduit(String a_IdProduit,String a_IdMagasin).");
        }

        /* Partie 1 - Récupération des ids de services du produit */

        List l_LesServicesDuProduit = new ArrayList();
        List l_LesIdDesServicesDuProduit = new ArrayList();
        try
        {
            if (a_IdProduit != null && !a_IdProduit.equals("") && new Integer(a_IdMagasin) != null)
            {
                /* Récupération du repository catalogue */
                if (getCatalogRepository() != null)
                {
                    /*
                     * Récupération de notre objet produit à partir du
                     * repository catalogue
                     */
                    RepositoryItem l_produit = (RepositoryItem) getCatalogRepository().getItem(
                            a_IdProduit, CastoConstantes.DESCRIPTEUR_CASTO_PRODUCT);

                    if (l_produit != null)
                    {
                        /*
                         * Récupération des couples services-magasins liés au
                         * produit
                         */
                        //l_services = (Set) l_produit.getPropertyValue(PROPERTY_SERVICES);
                        // Nouveau traitement
                        List l_listeSkus = (List) l_produit.getPropertyValue("childSKUs");
                        for (Iterator l_iteratorSku = l_listeSkus.iterator(); l_iteratorSku.hasNext();)
                        {
                            RepositoryItem l_sku = (RepositoryItem) l_iteratorSku.next();
                            List l_services = (List) getServicesSku(l_sku.getRepositoryId(), a_IdMagasin);
                            
                            for (Iterator l_iteratorService = l_services.iterator(); l_iteratorService.hasNext();)
                            {
                                RepositoryItem l_service = (RepositoryItem) l_iteratorService.next();
                                boolean a_ajouter=true;
                                int l_parcours=0;
                                while (a_ajouter && l_parcours<l_LesIdDesServicesDuProduit.size()) 
                                {
                                    if (l_service.getRepositoryId().equals(l_LesIdDesServicesDuProduit.get(l_parcours))) 
                                    {
                                        a_ajouter = false;
                                    }
                                    l_parcours++;
                                }
                                if (a_ajouter)
                                {
                                    l_LesServicesDuProduit.add(l_service);
                                    l_LesIdDesServicesDuProduit.add(l_service.getRepositoryId());
                                }
                            }  
                        }
                    }
                    else
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("---> l_produit == null ");
                        }
                    }
                }
                else
                {
                    if (isLoggingDebug())
                    {
                        logDebug("---> getCatalogRepository() == null");
                    }
                }
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("---> getServicesProduit : a_IdProduit ou a_IdMagasin null.");
                }
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getServicesProduit(String a_IdProduit,String a_IdMagasin).");
        }

        return l_LesServicesDuProduit;
    }

    /**
     * Méthode qui indique, pour un magasin donné, si le prix d'un sku est
     * donnée est un prix qui comprend déjà les frais de livraison ou non.
     * 
     * @param a_profile
     *            Profil de l'internaute (contient un lien vers le magasin de
     *            session).
     * @param a_sku
     *            RepositoryItem du sku concerné.
     * 
     * @return true si le prix du sku comprend déjà les frais de livraison,
     *         false sinon (ou en cas d'échec).
     */
    public boolean estPrixLivre(Profile a_profile, RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(Profile, RepositoryItem).");
        }

        boolean l_estLivre;

        if (null == a_profile)
        {
            logError("CastoProductTools.estPrixLivre(Profile, RepositoryItem) : Le profil fourni est null.");

            l_estLivre = false;
        }
        else
        {
            // Récupération du magasin de l'internaute
            Object l_obj = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

            if (null == l_obj)
            {
                logError("CastoProductTools.estPrixLivre(Profile, RepositoryItem) : Le magasin du profil est null.");

                l_estLivre = false;
            }
            else
            {
                l_estLivre = estPrixLivre(((RepositoryItem) l_obj).getRepositoryId(), a_sku);
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(Profile, RepositoryItem).");
        }

        return l_estLivre;
    }

    /**
     * Méthode qui indique, pour un magasin donné, si le prix d'un sku est
     * donnée est un prix qui comprend déjà les frais de livraison ou non.
     * 
     * @param a_idMagasin
     *            Identifiant du magasin.
     * @param a_item
     *            CommerceItem concerné.
     * 
     * @return true si le prix du sku comprend déjà les frais de livraison,
     *         false sinon (ou en cas d'échec).
     */
    public boolean estPrixLivre(String a_idMagasin, CommerceItem a_item)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(String, CommerceItem).");
        }

        boolean l_estLivre;

        AuxiliaryData l_auxiliaryData = a_item.getAuxiliaryData();

        if (null != l_auxiliaryData)
        {
            l_estLivre = estPrixLivre(a_idMagasin, (RepositoryItem) l_auxiliaryData.getCatalogRef());
        }
        else
        {
            l_estLivre = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(String, CommerceItem).");
        }

        return l_estLivre;
    }

    /**
     * Méthode qui indique, pour un magasin donné, si le prix d'un sku est
     * donnée est un prix qui comprend déjà les frais de livraison ou non.
     * 
     * @param a_idMagasin
     *            Identifiant du magasin.
     * @param a_sku
     *            RepositoryItem du sku concerné.
     * 
     * @return true si le prix du sku comprend déjà les frais de livraison,
     *         false sinon (ou en cas d'échec).
     */
    public boolean estPrixLivre(String a_idMagasin, RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(String, RepositoryItem).");
        }

        boolean l_estLivre = false;

        if (null == a_sku)
        {
            logError("CastoProductTools.estPrixLivre(String, RepositoryItem) : Le sku fourni est null.");
        }
        else
        {
            if (null != a_sku && null != a_idMagasin)
            {
                Map l_infos = (Map) a_sku.getPropertyValue(CastoConstantesDefense.INFOS);
                if (l_infos.containsKey(a_idMagasin.toString()))
                {
                    RepositoryItem l_donnees = (RepositoryItem) l_infos.get(a_idMagasin.toString());
                    if (null != l_donnees)
                    {
                        Boolean l_pxlivre = (Boolean) l_donnees
                                .getPropertyValue(CastoConstantesDefense.CASTO_SKU_PROPERTY_PRIX_LIVRE);
                        if (null != l_pxlivre)
                        {
                            l_estLivre = l_pxlivre.booleanValue();
                        }
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.estPrixLivre(String, RepositoryItem).");
        }
        return l_estLivre;
    }

    /**
     * . Méthode qui indique si un sku est un pack
     * 
     * @param a_idProduit
     *            a_idProduit
     * 
     * @param a_sTypeProduit
     *            a_sTypeProduit
     * 
     * @return true si c'est un pack, fase sinon
     */
    public boolean estUnPack(String a_idProduit, String a_sTypeProduit)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.estUnPack(String a_idProduit,String a_sTypeProduit).");
        }

        try
        {
            RepositoryItem l_oSku = getCatalogRepository().getItem(a_idProduit, a_sTypeProduit);

            if (null == l_oSku)
            {
                if (isLoggingDebug())
                {
                    logDebug("Le sku fourni est null.");
                }
            }
            else
            {
                List l_aArticlesEsclaves = (List) l_oSku.getPropertyValue(BUNDLE_LINKS);
                if (l_aArticlesEsclaves == null || (l_aArticlesEsclaves != null && l_aArticlesEsclaves.size() == 0))
                {
                    return false;
                }
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.estUnPack(String a_idProduit,String a_sTypeProduit).");
        }

        return true;
    }

    /**
     * . Retourne le chapo de promotion si la date actuelle est comprise dans la
     * periode de la promotion
     * 
     * @param a_childSkus
     *            liste des enfants du produit
     * @param a_profile
     *            a_profile
     * @return l_chapo
     */
    public boolean recupChapoPLD(List a_childSkus, RepositoryItem a_profile)
    {
        boolean l_chapo = false;
        if (null != a_profile)
        {
            RepositoryItem l_sSessionMagasin = (RepositoryItem) a_profile
                    .getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
            if (null != l_sSessionMagasin)
            {
                Integer l_idMagasinSession = (Integer) l_sSessionMagasin.getPropertyValue(ID);
                if (null != a_childSkus)
                {
                    RepositoryItem l_sku;
                    for (Iterator l_iterator = a_childSkus.iterator(); l_iterator.hasNext();)
                    {
                        l_sku = (RepositoryItem) l_iterator.next();
                        if (null != l_sku && null != l_idMagasinSession)
                        {
                            Map l_infos = (Map) l_sku.getPropertyValue(CastoConstantesDefense.INFOS);
                            if (l_infos.containsKey(l_idMagasinSession.toString()))
                            {
                                RepositoryItem l_donnees = (RepositoryItem) l_infos.get(l_idMagasinSession.toString());
                                if (null != l_donnees)
                                {
                                    Date l_dateDebut = (Date) l_donnees.getPropertyValue("DdebPromo");
                                    Date l_dateFin = (Date) l_donnees.getPropertyValue("DfinPromo");
                                    if (l_dateDebut != null && l_dateFin != null
                                            && dateEstCompriseEntre(new Date(), l_dateDebut, l_dateFin))
                                    {
                                        l_chapo = true;
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        return l_chapo;
    }

    /**
     * . Droplet spécifique permettant de voir si un produit a les frais de
     * livraison gratuit.
     * 
     * @param a_idMagasin
     *            identifiant du magasin de session
     * @param a_sIdProduit
     *            identifiant du produit consulté
     * @param a_sTypeProduit
     *            type du produit consulté : casto_sku ou casto_product
     * @return l_chapo
     */
    public boolean isPrixLivre(String a_idMagasin, String a_sIdProduit, String a_sTypeProduit)
    {
        try
        {
            if (CastoConstantes.DESCRIPTEUR_CASTO_SKU.compareToIgnoreCase(a_sTypeProduit) == 0)
            {
                RepositoryItem l_oSku = getCatalogRepository().getItem(a_sIdProduit, a_sTypeProduit);
                if (null != l_oSku)
                {
                    return estPrixLivre(a_idMagasin, l_oSku);
                }
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }
        return false;
    }

    // Ancien filtre sur les produits dangereux
    // /**
    // * PARIS LA DEFENSE filtre sur produits dangereux.
    // *
    // * @param a_listeProduits
    // * a_listeProduits
    // * @return List de produits non dangereux
    // */
    // public List filtrageDangereux(List a_listeProduits)
    // {
    // if (isLoggingDebug())
    // {
    // logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtrageDangereux
    // ==> method is running...");
    // }
    //
    // // liste des produits non dangereux
    // List l_listeProduitsNonDangereux = new ArrayList();
    //
    // // pour chaque produit, on regarde le flag dangereux et en ajoute ou pas
    // // dans la liste finale
    // for (int l_i = 0; l_i < a_listeProduits.size(); l_i++)
    // {
    // RepositoryItem l_product = (RepositoryItem) a_listeProduits.get(l_i);
    //
    // if ((l_product.getPropertyValue("dangereux") == null)
    // || (!((Boolean) l_product.getPropertyValue("dangereux")).booleanValue()))
    // {
    // l_listeProduitsNonDangereux.add(l_product);
    // }
    // }
    //
    // if (isLoggingDebug())
    // {
    // logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtrageDangereux
    // ==> method is plus running...");
    // }
    //
    // return l_listeProduitsNonDangereux;
    // }

    /**
     * MANTIS 1155 filtre sur les produits de prix nul.
     * 
     * @param a_listeProduits
     *            une liste de produits
     * @param a_profil
     *            un profil
     * 
     * @return List de produits de prix non nul
     */
    public List filtrePrixNul(List a_listeProduits, Profile a_profil)
    {
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtrePrixNul ==> method is running...");
        }

        // liste des produits de prix non nul
        List l_listeProduitsPrixNonNul = new ArrayList();

        // pour chaque produit, on regarde s'il existe un sku de prix non null
        // et on l'ajoute ou pas
        // dans la liste finale
        for (int l_i = 0; l_i < a_listeProduits.size(); l_i++)
        {
            RepositoryItem l_product = (RepositoryItem) a_listeProduits.get(l_i);

            if ((getPrix(l_product, a_profil) != null) && (!getPrix(l_product, a_profil).equals(new Double(0.0))))
            {
                l_listeProduitsPrixNonNul.add(l_product);
            }
        }

        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtrePrixNul ==> method is plus running...");
        }

        return l_listeProduitsPrixNonNul;
    }

    /**
     * . Calcule le delail de livraison en String
     * 
     * @param a_sIdProduit
     *            Id du produit
     * @param a_sTypeProduit
     *            Type du produit
     * @param a_profile
     *            Session Profile
     * @param a_sIdCategorie
     *            Categorie Id
     * @param a_quantite
     *            Quantité demandée
     * 
     * @return double Delai calcule
     */
    public Map calculDelaiLivraison(String a_sIdProduit, String a_sTypeProduit, String a_sIdCategorie,
            RepositoryItem a_profile, double a_quantite)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "ccom.castorama.produit.CastoProductTools.calculDelaiLivraison()");
        }

        /* Variables qui vont contenir le delai de livraison */
        Map l_DonneesRetours = new HashMap();

        try
        {

            /* LAD */
            double l_delaiMinLAD = 0;
            double l_delaiMaxLAD = CastoConstantes.MILLE;
            String l_delaiLMinLAD = null;
            String l_delaiLMaxLAD = null;

            /* MAD */
            double l_delaiMinMAD = 0;
            double l_delaiMaxMAD = CastoConstantes.MILLE;
            String l_delaiLMinMAD = null;
            String l_delaiLMaxMAD = null;

            /* Le Sku */
            RepositoryItem l_oSku = getCatalogRepository().getItem(a_sIdProduit, a_sTypeProduit);

            /*
             * Récupérération du coef de fiabilité du stock du magasin en
             * session
             */
            double l_CoefStockMagasin = getDispoManager().getCoefficientFiabilite(a_sIdCategorie);

            /* Récupérération du stock du produit */
            double l_Stock = getDispoManager().getStock(l_oSku, a_profile);
            double l_res = l_CoefStockMagasin * l_Stock;

            /* Récupération du délai de réapprovisionnement */
            double l_delaiReappro = getDispoManager().getDelaiReappro(l_oSku, a_profile);
            /*
             * -=MAD/LAD=- Si le client est en navigation PLD, le délai de
             * livraison est calculé en fonction d'un coefficient de fiabilité
             * du stock. Ce coefficient est paramétré au niveau de la
             * sous-famille du produit.
             */
            if (l_res >= 1 && a_quantite <= l_res)
            {
                /*
                 * -=LAD=-
                 * 
                 * Cas 1 : Si le stock fiabilisé (stock théorique X coefficient)
                 * est supérieur ou égal à 1 alors le produit est disponible et
                 * le délai de livraison est iso CD.
                 */
                // l_delaiMinLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
                // a_profile);
                l_delaiMinLAD = getDispoManager().getDelaiLivraison(a_profile);
                l_delaiMinMAD = getDispoManager().getDelaiPreparation(a_profile);
            }
            else
            {
                /*
                 * - -=MAD/LAD=- Cas 3 Si le stock fiabilisé est inférieur à 1
                 * alors le délai de MAD/LAD devient le délai minimum de
                 * préparation + un délai de réapprovisionnement. Ce délai de
                 * réapprovisionnement est une information du produit et est
                 * propre à chaque magasin.
                 */
                // l_delaiMinLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
                // a_profile) + l_delaiReappro;// (l_delaiReappro
                // /
                // CastoConstantes.H24);//
                // +CastoConstantes.UN);
                l_delaiMinLAD = getDispoManager().getDelaiLivraison(a_profile) + l_delaiReappro;
                l_delaiMinMAD = getDispoManager().getDelaiPreparation(a_profile) + l_delaiReappro * CastoConstantes.H24;
            }

            // l_delaiMaxLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
            // a_profile) + l_delaiReappro;// (l_delaiReappro
            // /
            // CastoConstantes.H24);//
            // +CastoConstantes.UN);
            l_delaiMaxLAD = getDispoManager().getDelaiLivraison(a_profile) + l_delaiReappro;
            l_delaiMaxMAD = getDispoManager().getDelaiPreparation(a_profile) + l_delaiReappro * CastoConstantes.H24;

            if (l_delaiMaxLAD < l_delaiMinLAD || (l_delaiMinLAD == CastoConstantes.NEG))
            {
                l_delaiMaxLAD = l_delaiMinLAD;
            }

            if (l_delaiMaxMAD < l_delaiMinMAD || (l_delaiMinMAD == CastoConstantes.NEG))
            {
                l_delaiMaxMAD = l_delaiMinMAD;
            }

            if (isLoggingInfo())
            {
                StringBuffer l_buffer = new StringBuffer();

                l_buffer.append("l_delaiMinLAD : ").append(l_delaiMinLAD).append(CastoConstantes.LINE_SEPARATOR);
                l_buffer.append("l_delaiMaxLAD : ").append(l_delaiMaxLAD).append(CastoConstantes.LINE_SEPARATOR);
                l_buffer.append("l_delaiMinMAD : ").append(l_delaiMinMAD).append(CastoConstantes.LINE_SEPARATOR);
                l_buffer.append("l_delaiMaxMAD : ").append(l_delaiMaxMAD);

                logInfo(l_buffer.toString());
            }

            /* LAD */
            l_delaiLMinLAD = CastoAffichageDisponibiliteManager.calculLibelleLAD(l_delaiMinLAD,
                    CastoConstantes.DISPONIBLE);
            l_delaiLMaxLAD = CastoAffichageDisponibiliteManager.calculLibelleLAD(l_delaiMaxLAD,
                    CastoConstantes.DISPONIBLE);
            /* MAD */
            l_delaiLMinMAD = CastoAffichageDisponibiliteManager.calculLibellePLD(l_delaiMinMAD,
                    CastoConstantes.DISPONIBLE_PLD);
            l_delaiLMaxMAD = CastoAffichageDisponibiliteManager.calculLibellePLD(l_delaiMaxMAD,
                    CastoConstantes.DISPONIBLE_PLD);

            /* On renvoie nos données */
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_LAD, l_delaiLMinLAD);
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_LAD, l_delaiLMaxLAD);
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_MAD, l_delaiLMinMAD);
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_MAD, l_delaiLMaxMAD);
            l_DonneesRetours.put(CastoConstantes.STOCK_PIVOT, new Double(l_res));
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.produit.CastoProductTools.calculDelaiLivraison()");
        }
        return l_DonneesRetours;
    }

    /**
     * . Calcule le delail de livraison en double
     * 
     * @param a_sIdProduit
     *            Id du produit
     * @param a_sTypeProduit
     *            Type du produit
     * @param a_magasin
     *            Session Magasin du Profile
     * @param a_oRequest
     *            DynamoHttpServletRequest
     * @param a_oResponse
     *            DynamoHttpServletResponse
     * @param a_sProvenance
     *            Provenance
     * @param a_sIdCategorie
     *            Id catégorie
     * @param a_quantite
     *            Quantité demandée
     * 
     * @return double Delai calcule
     * 
     * @throws Exception
     *             Exception
     */
    private Map calculDelaiLivraisonDouble(String a_sIdProduit, String a_sTypeProduit, String a_sIdCategorie,
            RepositoryItem a_profile, double a_quantite)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.calculDelaiLivraisonDouble()");
        }

        /* Variables qui vont contenir le delai de livraison */
        Map l_DonneesRetours = new HashMap();

        try
        {

            /* LAD */
            double l_delaiMinLAD = 0;
            double l_delaiMaxLAD = CastoConstantes.MILLE;

            /* MAD */
            double l_delaiMinMAD = 0;
            double l_delaiMaxMAD = CastoConstantes.MILLE;

            /* Le Sku */
            RepositoryItem l_oSku = getCatalogRepository().getItem(a_sIdProduit, a_sTypeProduit);

            /* Récupéréation du coef de fiabilité du stock du magasin en session */
            double l_CoefStockMagasin = getDispoManager().getCoefficientFiabilite(a_sIdCategorie);

            /* Récupéréation du stock du produit */
            double l_Stock = getDispoManager().getStock(l_oSku, a_profile);
            double l_res = l_CoefStockMagasin * l_Stock;

            /* Récupération du délai de réapprovisionnement */
            double l_delaiReappro = getDispoManager().getDelaiReappro(l_oSku, a_profile);

            /*
             * -=MAD/LAD=- Si le client est en navigation PLD, le délai de
             * livraison est calculé en fonction d'un coefficient de fiabilité
             * du stock. Ce coefficient est paramétré au niveau de la
             * sous-famille du produit.
             */
            if (l_res >= 1 && a_quantite <= l_res)
            {
                /*
                 * -=LAD=-
                 * 
                 * Cas 1 : Si le stock fiabilisé (stock théorique X coefficient)
                 * est supérieur ou égal à 1 alors le produit est disponible et
                 * le délai de livraison est iso CD.
                 */
                // l_delaiMinLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
                // a_profile);
                l_delaiMinLAD = getDispoManager().getDelaiLivraison(a_profile);
                l_delaiMinMAD = getDispoManager().getDelaiPreparation(a_profile);
            }
            else
            {
                /*
                 * - -=MAD/LAD=- Cas 3 Si le stock fiabilisé est inférieur à 1
                 * alors le délai de MAD/LAD devient le délai minimum de
                 * préparation + un délai de réapprovisionnement. Ce délai de
                 * réapprovisionnement est une information du produit et est
                 * propre à chaque magasin.
                 */
                // l_delaiMinLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
                // a_profile) + l_delaiReappro;// (l_delaiReappro
                l_delaiMinLAD = getDispoManager().getDelaiLivraison(a_profile) + l_delaiReappro;
                // /
                // CastoConstantes.H24
                // +
                // CastoConstantes.UN);
                l_delaiMinMAD = getDispoManager().getDelaiPreparation(a_profile) + l_delaiReappro * CastoConstantes.H24;
            }
            // l_delaiMaxLAD = getDispoManager().getDelaiLAD(l_oSku, l_res,
            // a_profile) + l_delaiReappro;// (l_delaiReappro
            l_delaiMaxLAD = getDispoManager().getDelaiLivraison(a_profile) + l_delaiReappro;
            // /
            // CastoConstantes.H24
            // +
            // CastoConstantes.UN);
            l_delaiMaxMAD = getDispoManager().getDelaiPreparation(a_profile) + l_delaiReappro * CastoConstantes.H24;

            if (l_delaiMaxLAD < l_delaiMinLAD || l_delaiMinLAD == CastoConstantes.NEG)
            {
                l_delaiMaxLAD = l_delaiMinLAD;
            }

            if (l_delaiMaxMAD < l_delaiMinMAD || l_delaiMinMAD == CastoConstantes.NEG)
            {
                l_delaiMaxMAD = l_delaiMinMAD;
            }

            /* On renvoie nos données */
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_LAD, new Double(l_delaiMinLAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_LAD, new Double(l_delaiMaxLAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_MAD, new Double(l_delaiMinMAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_MAD, new Double(l_delaiMaxMAD));
            l_DonneesRetours.put(CastoConstantes.STOCK_PIVOT, new Double(l_res));
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.calculDelaiLivraisonDouble()");
        }
        return l_DonneesRetours;
    }

    /**
     * . Calcule le delail de livraison du pack
     * 
     * @param a_sIdProduit
     *            Id du produit
     * @param a_sTypeProduit
     *            Type du produit
     * @param a_profile
     *            Session Profile
     * @param a_sIdCategorie
     *            Id de catégorie
     * @param a_quantite
     *            Quantité
     * @return double Delai calcule
     */
    public Map calculDelaiLivraisonPack(String a_sIdProduit, String a_sTypeProduit, String a_sIdCategorie,
            RepositoryItem a_profile, double a_quantite)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.calculDelaiLivraisonPack()");
        }

        /* Variables qui vont contenir le delai de livraison */
        Map l_DonneesRetours = new HashMap();

        try
        {
            /* Stock Pivot */
            double l_stockPivot = CastoConstantes.NEG;

            /* LAD */
            double l_delaiMinLAD = 0;
            double l_delaiMaxLAD = 0;

            /* MAD */
            double l_delaiMinMAD = 0;
            double l_delaiMaxMAD = 0;

            /* Le Sku */
            RepositoryItem l_oSkuMaitre = getCatalogRepository().getItem(a_sIdProduit, a_sTypeProduit);
            /* Ses articles de pack */
            List l_aArticlesEsclaves = null;

            /* Propriété d'un composant */
            RepositoryItem l_SkuLink;
            double l_quantiteDemandePourUnPack;
            RepositoryItem l_Sku;
            double l_stockPackPivot;

            /* valeurs de retour temporaires */
            Map l_tmp;

            if (l_oSkuMaitre != null)
            {
                /*
                 * Est-on dans un pack ? oui, si bundleLinks !=null ou si
                 * bundlelinks.size() != 0
                 */
                l_aArticlesEsclaves = (List) l_oSkuMaitre.getPropertyValue(BUNDLE_LINKS);
            }

            if (null != l_aArticlesEsclaves)
            {
                for (Iterator l_it = l_aArticlesEsclaves.iterator(); l_it.hasNext();)
                {
                    l_SkuLink = (RepositoryItem) l_it.next();
                    l_quantiteDemandePourUnPack = ((Long) l_SkuLink.getPropertyValue(CastoConstantes.QTE))
                            .doubleValue();
                    l_Sku = (RepositoryItem) l_SkuLink.getPropertyValue(CastoConstantes.ITEM);
                    l_tmp = calculDelaiLivraisonDouble(l_Sku.getPropertyValue(ID).toString(),
                            CastoConstantes.DESCRIPTEUR_CASTO_SKU, a_sIdCategorie, a_profile, a_quantite);

                    if (l_delaiMinLAD < ((Double) l_tmp.get(CastoConstantes.DELAI_MIN_LAD)).doubleValue())
                    {
                        l_delaiMinLAD = ((Double) l_tmp.get(CastoConstantes.DELAI_MIN_LAD)).doubleValue();
                    }

                    if (l_delaiMinMAD < ((Double) l_tmp.get(CastoConstantes.DELAI_MIN_MAD)).doubleValue())
                    {
                        l_delaiMinMAD = ((Double) l_tmp.get(CastoConstantes.DELAI_MIN_MAD)).doubleValue();
                    }

                    if (l_delaiMaxLAD < ((Double) l_tmp.get(CastoConstantes.DELAI_MAX_LAD)).doubleValue())
                    {
                        l_delaiMaxLAD = ((Double) l_tmp.get(CastoConstantes.DELAI_MAX_LAD)).doubleValue();
                    }

                    if (l_delaiMaxMAD < ((Double) l_tmp.get(CastoConstantes.DELAI_MAX_MAD)).doubleValue())
                    {
                        l_delaiMaxMAD = ((Double) l_tmp.get(CastoConstantes.DELAI_MAX_MAD)).doubleValue();
                    }

                    l_stockPackPivot = ((Double) l_tmp.get(CastoConstantes.STOCK_PIVOT)).doubleValue()
                            / l_quantiteDemandePourUnPack;

                    if (l_stockPivot == CastoConstantes.NEG || l_stockPivot > l_stockPackPivot)
                    {
                        l_stockPivot = Math.floor(l_stockPackPivot);
                    }
                }
            }

            /* On renvoie nos données */
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_LAD, new Double(l_delaiMinLAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_LAD, new Double(l_delaiMaxLAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MIN_MAD, new Double(l_delaiMinMAD));
            l_DonneesRetours.put(CastoConstantes.DELAI_MAX_MAD, new Double(l_delaiMaxMAD));
            l_DonneesRetours.put(CastoConstantes.STOCK_PIVOT, new Double(l_stockPivot));
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.calculDelaiLivraisonPack()");
        }

        return l_DonneesRetours;
    }

    /**
     * Méthode qui dans le cadre d'une session PLD renvoie le délai maximum de .
     * 
     * @param a_shippingGroup
     *            Le servide à considérer.
     * @param a_profile
     *            Données de l'internaute.
     * 
     * @return Une map contenant les informations pour le service passé en
     *         paramètre, null en cas d'échec.
     */
    public Map getDelaiMaximumService(ShippingGroup a_shippingGroup, Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile)");
        }

        Map l_infos;

        if (null == a_shippingGroup)
        {
            logError("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : Le service est null");

            l_infos = null;
        }
        else
        {
            boolean l_lad = CastoConstantesDefense.LIVRAISON_A_DOMICILE.equals(a_shippingGroup.getShippingMethod());

            if (isLoggingInfo())
            {
                logInfo("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : LAD : " + l_lad);
            }

            CommerceItemRelationship l_relation;
            CommerceItem l_commerceItem;
            String l_category = null;
            Set l_categories;
            Object l_product;
            Map l_delais;
            Double l_delai;
            Double l_delaiMax = new Double(0);
            Double l_pivot;
            String l_libelle;
            long l_quantite;

            for (Iterator l_relations = a_shippingGroup.getCommerceItemRelationships().iterator(); l_relations
                    .hasNext();)
            {
                l_relation = (CommerceItemRelationship) l_relations.next();

                l_commerceItem = l_relation.getCommerceItem();

                l_product = l_commerceItem.getAuxiliaryData().getProductRef();

                l_quantite = l_commerceItem.getQuantity();

                if (isLoggingInfo())
                {
                    StringBuffer l_buffer = new StringBuffer();

                    l_buffer.append("product : ").append(l_product).append(CastoConstantes.LINE_SEPARATOR);
                    l_buffer.append("quantite : ").append(l_quantite);

                    logInfo(l_buffer.toString());
                }

                try
                {
                    RepositoryItem l_cat = (RepositoryItem) DynamicBeans.getPropertyValue(l_product, "parentCategory");

                    if (null != l_cat)
                    {
                        l_category = l_cat.getRepositoryId();

                        if (isLoggingInfo())
                        {
                            logInfo("parentCategory : " + l_category);
                        }
                    }

                    if (null == l_category)
                    {
                        l_categories = (Set) DynamicBeans.getPropertyValue(l_product, "parentCategories");

                        if (null != l_categories && !l_categories.isEmpty())
                        {
                            l_category = ((RepositoryItem) l_categories.iterator().next()).getRepositoryId();
                        }
                        else
                        {
                            logError("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) "
                                    + ": catégories parentes non renseignées pour le produit : " + l_product.toString());

                            l_category = null;
                        }

                        if (isLoggingInfo())
                        {
                            logInfo("parentCategories (1ère) : " + l_category);
                        }
                    }
                }
                catch (PropertyNotFoundException l_pnfe)
                {
                    l_category = null;

                    logError("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : " + l_pnfe.toString(),
                            l_pnfe);
                }

                if (null != l_category)
                {
                    // On teste s'il s'agit d'un pack

                    if (estUnPack(l_commerceItem.getCatalogRefId(), CastoConstantes.DESCRIPTEUR_CASTO_SKU))
                    {
                        l_delais = calculDelaiLivraisonPack(l_commerceItem.getCatalogRefId(),
                                CastoConstantes.DESCRIPTEUR_CASTO_SKU, l_category, a_profile, l_quantite);
                    }
                    else
                    {
                        l_delais = calculDelaiLivraisonDouble(l_commerceItem.getCatalogRefId(),
                                CastoConstantes.DESCRIPTEUR_CASTO_SKU, l_category, a_profile, l_quantite);
                    }

                    if (null != l_delais)
                    {
                        l_pivot = (Double) l_delais.get(CastoConstantes.STOCK_PIVOT);

                        if (l_quantite >= l_pivot.doubleValue())
                        {
                            l_delai = l_lad ? (Double) l_delais.get(CastoConstantes.DELAI_MAX_LAD) : (Double) l_delais
                                    .get(CastoConstantes.DELAI_MAX_MAD);
                        }
                        else
                        {
                            l_delai = l_lad ? (Double) l_delais.get(CastoConstantes.DELAI_MIN_LAD) : (Double) l_delais
                                    .get(CastoConstantes.DELAI_MIN_MAD);
                        }

                        if (isLoggingInfo())
                        {
                            logInfo("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : Delai : "
                                    + l_delai + ", max : " + l_delaiMax);
                        }

                        if (null != l_delai)
                        {
                            if (0 > l_delaiMax.compareTo(l_delai))
                            {
                                l_delaiMax = l_delai;
                            }
                        }

                        if (isLoggingInfo())
                        {
                            logInfo("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : Delai : "
                                    + l_delai + ", max : " + l_delaiMax + ", pivot : " + l_pivot);
                        }
                    }
                }
                else
                {
                    if (isLoggingWarning())
                    {
                        logWarning("CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile) : impossible de trouver une catégorie parente.");
                    }
                }

                // Remise à null de la catégorie en cours
                l_category = null;
            }

            // Calcul du libellé

            l_libelle = l_lad ? CastoAffichageDisponibiliteManager.calculLibelleLAD(l_delaiMax.doubleValue(),
                    CastoConstantes.DISPONIBLE) : CastoAffichageDisponibiliteManager.calculLibellePLD(l_delaiMax
                    .doubleValue(), CastoConstantes.DISPONIBLE_PLD);

            // Affectation du délai d'engagement

            ((CastoHardgoodShippingGroup) a_shippingGroup).setDelaiEngagement(l_delaiMax.intValue());

            // Affectation du libellé du délai d'engagement

            ((CastoHardgoodShippingGroup) a_shippingGroup).setLibelleDelaiEngagement(l_libelle);

            // Création de la map de retour

            l_infos = new HashMap();

            l_infos.put(CastoConstantes.DELAI_MAX, l_delaiMax);
            l_infos.put(CastoConstantes.LIBELLE_DELAI_MAX, l_libelle);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getDelaiMaximumService(ShippingGroup, Profile)");
        }

        return l_infos;
    }

    /**
     * methode qui renvoie le produit pere d'un sku.
     * 
     * @param a_idSku
     *            le skuId.
     * @return Le produit pere.
     */
    public RepositoryItem getProduitPere(String a_idSku)
    {

        RepositoryItem l_product = null;

        try
        {
            if (null != a_idSku)
            {
                RepositoryItem l_sku = getCatalogRepository().getItem(a_idSku, CastoConstantes.DESCRIPTEUR_CASTO_SKU);
                if (null != l_sku)
                {
                    Set l_products = (Set) l_sku.getPropertyValue("parentProducts");
                    if (null != l_products)
                    {
                        Object[] l_tabProducts = l_products.toArray();
                        if (null != l_tabProducts && l_tabProducts.length > 0)
                        {
                            l_product = (RepositoryItem) l_tabProducts[0];
                        }
                    }
                }
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        return l_product;

    }

    /**
     * .
     * 
     * On enleve les produits destockes
     * 
     * @param a_products
     *            Liste des produits a filtrer
     * @return List Liste des produits filtres
     */
    public List filtreDestockage(List a_products)
    {
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreDestockage ==> method is running...");
        }

        List l_listeFiltree = new ArrayList();

        for (int l_i = 0; l_i < a_products.size(); l_i++)
        {
            boolean l_affichageProduit = false;

            RepositoryItem l_product = (RepositoryItem) a_products.get(l_i);

            if (CastoProductTools.isInDestockageProduit(l_product))
            {
                Collection l_skus = (Collection) l_product.getPropertyValue("childSKUs");

                Iterator l_iterator = l_skus.iterator();

                while (l_iterator.hasNext() && !l_affichageProduit)
                {

                    RepositoryItem l_sku = (RepositoryItem) l_iterator.next();

                    int l_nStatus = NEG;
                    
                    Integer l_nRetraitCodifie;

                    try
                    {
                        l_nRetraitCodifie = (Integer) l_sku.getPropertyValue("retraitMomentaneMotifsCodifies");
                        
                        l_nStatus = getInventoryAdapter().getCachedInventoryManager().queryAvailabilityStatus(
                                l_sku.getRepositoryId());
                        
                        if(l_nRetraitCodifie != null && PRODUIT_DANGEREUX == l_nRetraitCodifie.intValue())
                        {
                            l_affichageProduit = false;
                        }
                        else if(l_nStatus == RepositoryInventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK)
                        {
                            l_affichageProduit = false;
                        }
                        else
                        {
                            l_affichageProduit = true;
                        }
                    }
                    catch (MissingInventoryItemException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }
                    catch (InventoryException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }
                    // MODIF 890
                    /*if (CastoCategoryManager.isInDestockage(l_sku) || CastoCategoryManager.isInBonneAffaire(l_sku))
                    {
                        l_affichageProduit = true;
                    }*/
                }

                if (l_affichageProduit)
                {
                    l_listeFiltree.add(l_product);
                }

            }
            else
            {
                l_listeFiltree.add(l_product);
            }
        }

        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreDestockage ==> method is plus running...");
        }

        return l_listeFiltree;
    }
    
    /**
     * .
     * 
     * On enleve les produits destockes dans le moteur de recherche
     * 
     * @param a_products
     *            Liste des produits a filtrer
     * @return List Liste des produits filtres
     */
    public List filtreDestockageRecherche(List a_products)
    {
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreDestockage ==> method is running...");
        }

        List l_listeFiltree = new ArrayList();

        for (int l_i = 0; l_i < a_products.size(); l_i++)
        {
            boolean l_affichageProduit = false;

            RepositoryItem l_product = (RepositoryItem) a_products.get(l_i);

            if (CastoProductTools.isInDestockageProduit(l_product))
            {
                Collection l_skus = (Collection) l_product.getPropertyValue("childSKUs");

                Iterator l_iterator = l_skus.iterator();

                while (l_iterator.hasNext() && !l_affichageProduit)
                {

                    RepositoryItem l_sku = (RepositoryItem) l_iterator.next();

                    int l_nStatus = NEG;
                    
                    Integer l_nRetraitCodifie;

                    try
                    {
                        l_nRetraitCodifie = (Integer) l_sku.getPropertyValue("retraitMomentaneMotifsCodifies");
                        
                        l_nStatus = getInventoryAdapter().getCachedInventoryManager().queryAvailabilityStatus(
                                l_sku.getRepositoryId());
                        
                        if(l_nRetraitCodifie != null && PRODUIT_DANGEREUX == l_nRetraitCodifie.intValue())
                        {
                            l_affichageProduit = false;
                        }
                        else if(l_nStatus == RepositoryInventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK)
                        {
                            l_affichageProduit = false;
                        }
                        else
                        {
                            l_affichageProduit = true;
                        }
                    }
                    catch (MissingInventoryItemException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }
                    catch (InventoryException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }
                    // MODIF 890
                    /*if (CastoCategoryManager.isInDestockage(l_sku) || CastoCategoryManager.isInBonneAffaire(l_sku))
                    {
                        l_affichageProduit = true;
                    }*/
                }

                if (l_affichageProduit && !getConfiguration().getDestockExcluRecherche())
                {
                    l_listeFiltree.add(l_product);
                }

            }
            else
            {
                l_listeFiltree.add(l_product);
            }
        }

        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreDestockage ==> method is plus running...");
        }

        return l_listeFiltree;
    }

    
    /**
     * pour les soldes.
     * 
     * On enleve les produits epuises
     * 
     * @param a_products
     *            Liste des produits a filtrer
     * @return List Liste des produits filtres
     */
    public List filtreEpuisement(List a_products)
    {
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreDestockage ==> method is running...");
        }

        List l_listeFiltree = new ArrayList();

        for (int l_i = 0; l_i < a_products.size(); l_i++)
        {
            boolean l_affichageProduit = false;

            RepositoryItem l_product = (RepositoryItem) a_products.get(l_i);

            if (CastoProductTools.isInDestockageProduit(l_product))
            {
                Collection l_skus = (Collection) l_product.getPropertyValue("childSKUs");

                Iterator l_iterator = l_skus.iterator();

                while (l_iterator.hasNext() && !l_affichageProduit)
                {

                    RepositoryItem l_sku = (RepositoryItem) l_iterator.next();

                    int l_nStatus = NEG;

                    try
                    {
                        l_nStatus = getInventoryAdapter().getCachedInventoryManager().queryAvailabilityStatus(
                                l_sku.getRepositoryId());
                    }
                    catch (MissingInventoryItemException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }
                    catch (InventoryException l_oException)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("L'inventaire n'existe pas pour le SKU : " + l_sku.getRepositoryId());
                        }
                        l_nStatus = NEG;
                    }

                    
                    if (l_nStatus == InventoryManager.AVAILABILITY_STATUS_IN_STOCK)
                    {
                        l_affichageProduit = true;
                    }
                }

                if (l_affichageProduit)
                {
                    l_listeFiltree.add(l_product);
                }

            }
            else
            {
                l_listeFiltree.add(l_product);
            }
        }

        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.filtreEpuisement ==> method is finish...");
        }

        return l_listeFiltree;
    }

    
    
    
    
    /**
     * Méthode qui filtre les articles selon la règle suivante :<br/> Article
     * avec date de déref atteinte ou dépassée (sauf rangement specifique type
     * Solde) et article avec retraitmomentanMotifscodifies = 2.
     * 
     * @param a_list
     *            la liste d'objets
     * @param a_typeListe
     *            le type de la liste
     * @return List
     */
    public List filtreDeref(List a_list, String a_typeListe)
    {
        List l_listeFiltree = new ArrayList();

        if (a_typeListe.equals("product"))
        {
            for (int l_i = 0; l_i < a_list.size(); l_i++)
            {
                boolean l_affichageProduit = true;
                RepositoryItem l_product = (RepositoryItem) a_list.get(l_i);
                Collection l_skus = (Collection) l_product.getPropertyValue("childSkus");
                Iterator l_iterator = l_skus.iterator();

                while (l_iterator.hasNext() && l_affichageProduit)
                {
                    RepositoryItem l_sku = (RepositoryItem) l_iterator.next();
                    l_affichageProduit = regleDeref(l_sku);
                }

                if (l_affichageProduit)
                {
                    l_listeFiltree.add(l_product);
                }
            }
        }
        else
        {
            if (a_typeListe.equals("sku"))
            {
                Iterator l_iterator = a_list.iterator();
                boolean l_affichageProduit = true;

                while (l_iterator.hasNext())
                {
                    RepositoryItem l_sku = (RepositoryItem) l_iterator.next();
                    l_affichageProduit = regleDeref(l_sku);

                    if (l_affichageProduit)
                    {
                        l_listeFiltree.add(l_sku);
                    }
                }
            }
            else
            {
                if (a_typeListe.equals("crossSelling"))
                {
                    Iterator l_iterator = a_list.iterator();
                    boolean l_affichageProduit = true;

                    while (l_iterator.hasNext())
                    {

                        RepositoryItem l_skuCross = (RepositoryItem) l_iterator.next();
                        RepositoryItem l_sku = (RepositoryItem) l_skuCross.getPropertyValue("sku");

                        l_affichageProduit = regleDeref(l_sku);

                        if (l_affichageProduit)
                        {
                            l_listeFiltree.add(l_skuCross);
                        }
                    }
                }
            }
        }

        return l_listeFiltree;

    }

    /**
     * .
     * 
     * @param a_sku
     *            un sku
     * @return boolean
     */
    protected boolean regleDeref(RepositoryItem a_sku)
    {
        boolean l_affichageProduit = false;
        String l_retrait = null;
        Boolean l_enSolde = null;

        if (CastoProductTools.isInDestockage(a_sku))
        {
            l_affichageProduit = true;
        }
        else if (a_sku.getPropertyValue("retraitmomentanemotifscodifies") == null
                || a_sku.getPropertyValue("datefinvie") == null)
        {
            l_affichageProduit = true;
        }
        else
        {
            /* Les soldes*/
            if (a_sku.getPropertyValue("onSale") != null)
            {
                l_enSolde = (Boolean) a_sku.getPropertyValue("onSale");
            }
            else
            {
                l_enSolde = new Boolean(false);
            }
            //l_enSolde = new Boolean(false);
            l_retrait = a_sku.getPropertyValue("retraitmomentanemotifscodifies").toString();

            Date l_dateDujour = Calendar.getInstance().getTime();
            String l_dateDeref = a_sku.getPropertyValue("datefinvie").toString();
            String l_anneeDeref = l_dateDeref.substring(0, QUATRE);
            String l_moisDeref = l_dateDeref.substring(CINQ, SEPT);
            String l_jourDeref = l_dateDeref.substring(HUIT, DIX);
            int l_nAnnee = new Integer(l_anneeDeref).intValue() - MILLE_NEUF_CENT;
            int l_nMois = new Integer(l_moisDeref).intValue() - 1;
            int l_nJour = new Integer(l_jourDeref).intValue();
            Calendar l_cal = Calendar.getInstance();
            l_cal.set(l_nAnnee, l_nMois, l_nJour);
            Date l_dateDeDeref = l_cal.getTime();

            if (l_dateDujour.compareTo(l_dateDeDeref) >= 0 && l_retrait.equals("2") && !l_enSolde.booleanValue())
            {
                l_affichageProduit = false;
            }
            else
            {
                l_affichageProduit = true;
            }
        }

        return l_affichageProduit;
    }

    /**
     * retourne le prix � associ� � un produit.
     * 
     * @param a_oProduct
     *            le produit
     * @param a_profileUtilisateur
     *            Profile
     * @return Double le prix
     */

    public Double getPrix(RepositoryItem a_oProduct, Profile a_profileUtilisateur)
    {

        double l_nPrixCata = 0;
        double l_nPrixPromo = 0;
        Double l_ret = null;

        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoListerProduitParCategorie.getPrixCata ==> method is running...");
        }

        try
        {
            // obtention des skus du produit en cours
            List l_listeSkusBrute = (List) a_oProduct.getPropertyValue("childSKUs");

            RepositoryItem[] l_aListeSkus = new RepositoryItem[l_listeSkusBrute.size()];
            int l_compteur = 0;

            for (Iterator l_iterator = l_listeSkusBrute.iterator(); l_iterator.hasNext();)
            {
                RepositoryItem l_item = (RepositoryItem) l_iterator.next();
                l_aListeSkus[l_compteur] = l_item;
                l_compteur++;
            }

            // PARIS LA DEFENSE on recupere maintenant le prix catalogue depuis
            // les price list
            // on recupere toujours le sale price depuis les attributs produits
            // si produit mono sku, on a le prix cata et potentiellement le prix
            // promo si il est en promo et que l'on est en mode CD
            if (l_aListeSkus.length == 1)
            {
                CastoPriceInfo l_prixCataEtPrixPromo = getPrix(a_profileUtilisateur, a_oProduct, l_aListeSkus[0]);
                l_nPrixPromo = l_prixCataEtPrixPromo.getSalePrice();
                l_nPrixCata = l_prixCataEtPrixPromo.getListPrice();
            }
            else
            {
                // init de la taille des liste de prix cata et promo
                List l_aListePrixCata = new ArrayList();
                List l_aListePrixPromo = new ArrayList();

                for (int l_i = 0; l_i < l_aListeSkus.length; l_i++)
                {
                    CastoPriceInfo l_prixCataEtPrixPromo = getPrix(a_profileUtilisateur, a_oProduct, l_aListeSkus[l_i]);
                    if (l_prixCataEtPrixPromo.getSalePrice() != 0)
                    {
                        l_aListePrixPromo.add(new Double(l_prixCataEtPrixPromo.getSalePrice()));
                    }
                    if (l_prixCataEtPrixPromo.getListPrice() != 0)
                    {
                        l_aListePrixCata.add(new Double(l_prixCataEtPrixPromo.getListPrice()));
                    }
                }

                // recherche du prix cata � renvoyer
                // parcours de la liste des prix cata et d�finition du prix le
                // plus petit
                if (l_aListePrixCata.size() != 0)
                {
                    l_nPrixCata = ((Double) l_aListePrixCata.get(0)).doubleValue();
                    for (int l_i = 1; l_i < l_aListePrixCata.size(); l_i++)
                    {
                        if (((Double) l_aListePrixCata.get(l_i)).doubleValue() <= l_nPrixCata)
                        {
                            l_nPrixCata = ((Double) l_aListePrixCata.get(l_i)).doubleValue();
                        }
                    }
                }

                // recherche du prix promo � renvoyer
                // parcours de la liste des prix promo et d�finition du prix le
                // plus petit
                if (l_aListePrixPromo.size() != 0)
                {
                    l_nPrixPromo = ((Double) l_aListePrixPromo.get(0)).doubleValue();
                    for (int l_i = 1; l_i < l_aListePrixPromo.size(); l_i++)
                    {
                        if (((Double) l_aListePrixPromo.get(l_i)).doubleValue() <= l_nPrixPromo
                                && ((Double) l_aListePrixPromo.get(l_i)).doubleValue() != CODE_ERREUR)
                        {
                            l_nPrixPromo = ((Double) l_aListePrixPromo.get(l_i)).doubleValue();
                        }
                    }
                }
            }

            if (l_nPrixPromo != 0)
            {
                l_ret = new Double(l_nPrixPromo);
            }
            else
            {
                l_ret = new Double(l_nPrixCata);
            }
        }
        catch (Exception l_exception)
        {
            if (isLoggingError())
            {
                logError("<<Droplet>> : CastoListerProduitParCategorie.getPrixCata ==> " + l_exception);
            }
            l_ret = new Double("0");
        }

        return l_ret;
    }

    /**
     * . Permet de voir si un produit est commandable ou non
     * 
     * @param a_oSku
     *            a_oSku
     * @return boolean ce sku est il commandable?
     */
    public static boolean isCommandable(RepositoryItem a_oSku)
    {

        Date l_dDateCourante = new Date();
        Integer l_nRetraitCodifie = (Integer) a_oSku.getPropertyValue("retraitMomentaneMotifsCodifies");
        Date l_dDateFinVie = (Date) a_oSku.getPropertyValue("dateFinVie");
        // Integer l_nTypeArticle = (Integer)
        // a_oSku.getPropertyValue("typeArticle");
        boolean l_commandable = false;

        // On utilise deux variables en Calendar car cet objet permet de
        // manipuler les mois plus facilement
        Calendar l_dFinVie = Calendar.getInstance();

        if (l_dDateFinVie != null)
        {
            l_dFinVie.setTime(l_dDateFinVie);
            // mantis 1330: date de deref des produits LDF == date de fin de vie (avt: date de deref == dateFinVie - 6 mois)
            if (null == a_oSku.getPropertyValue("LivraisonDirecteFournisseur") ||
                    ("false".equals(a_oSku.getPropertyValue("LivraisonDirecteFournisseur").toString())))
            {
                l_dFinVie.roll(Calendar.MONTH, SIX);
            }
        }
        else
        {
            l_dFinVie = null;
        }
        Calendar l_dCourante = Calendar.getInstance();
        l_dCourante.setTime(l_dDateCourante);

        if(CastoProductTools.isInDestockage(a_oSku))
        {
            if(l_nRetraitCodifie != null && PRODUIT_DANGEREUX == l_nRetraitCodifie.intValue())
            {
                l_commandable = false;
            }
            else
            {
                l_commandable = true;
            }
        }
        else
        {
            if (l_nRetraitCodifie != null && PRODUIT_RETIRE_DE_LA_VENTE == l_nRetraitCodifie.intValue())
            {
                l_commandable = false;
            }
            //mantis 1330
            else if (l_dFinVie != null && l_dCourante.after(l_dFinVie) == true)
            {
                l_commandable = false;
            }
            /*
             * FM 1113 else if (l_nTypeArticle != null && l_nTypeArticle.intValue() ==
             * TYPE_VUM) { l_commandable = false; }
             */
            else if (l_nRetraitCodifie != null && RUPTURE_MOMENTANEE_FOURNISSEUR == l_nRetraitCodifie.intValue())
            {
                l_commandable = false;
            }
            else
            {
                l_commandable = true;
            }
        }
        return l_commandable;
    }
    
    /**
     * Méthode qui listera pour un sku d’un magasin les services
     * disponibles.
     * 
     * 
     * @param a_IdSku
     *            Le psku.
     * @param a_IdMagasin
     *            Le magasin.
     * @return List des services disponibles pour le sku dans le magasin
     */
    public List getServicesSku(String a_IdSku, int a_IdMagasin)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.getServicesProduit(String a_IdProduit,String a_IdMagasin).");
        }

        /* Partie 1 - Récupération des ids de services du produit */

        /* Variables locales */
        List l_LesServicesDuSku = new ArrayList();
        Set l_services = null;
        int l_IdMagasin = CastoConstantesDefense.CASTORAMA_DIRECT_INT;
        int l_IdService = 0;
        RepositoryItem l_Couple = null;
        Repository l_MagasinRepository = getMagasinRepository();
        try
        {
            if (a_IdSku != null && !a_IdSku.equals("") && new Integer(a_IdMagasin) != null)
            {
                /* Récupération du repository catalogue */
                if (getCatalogRepository() != null)
                {
                    /*
                     * Récupération de notre objet produit à partir du
                     * repository catalogue
                     */
                    RepositoryItem l_sku = (RepositoryItem) getCatalogRepository().getItem(
                            a_IdSku, CastoConstantes.DESCRIPTEUR_CASTO_SKU);

                    if (l_sku != null)
                    {
                        /*
                         * Récupération des couples services-magasins liés au
                         * produit
                         */
                        l_services = (Set) l_sku.getPropertyValue(PROPERTY_SERVICES_SKU);
                        if (l_services != null && l_services.size() > 0)
                        {
                            /*
                             * Récupération des services liés au produit en
                             * filtrant le couple idService/idMagasin à partir
                             * du paramètre idMagasin
                             */
                            for (Iterator l_it = l_services.iterator(); l_it.hasNext();)
                            {
                                l_Couple = (RepositoryItem) l_it.next();
                                l_IdMagasin = ((Integer) l_Couple.getPropertyValue("idMagasin")).intValue();
                                if (l_IdMagasin == a_IdMagasin)
                                {
                                    l_IdService = ((Integer) l_Couple.getPropertyValue("idService")).intValue();
                                    /*
                                     * Partie 2 - On traduit la liste d'id de
                                     * services récupérés en liste d'objets
                                     * services
                                     */
                                    if (l_MagasinRepository != null)
                                    {
                                        MutableRepositoryItem l_service = (MutableRepositoryItem) l_MagasinRepository
                                                .getItem(new Integer(l_IdService).toString(),
                                                        CastoConstantesDefense.DESCRIPTEUR_SERVICE_COMMANDE);
                                        if (l_service != null)
                                        {
                                            l_LesServicesDuSku.add(l_service);
                                        }
                                    }
                                    else
                                    {
                                        if (isLoggingDebug())
                                        {
                                            logDebug("---> getMagasinRepository() null");
                                        }
                                    }
                                }// Fin if permettant de savoir si ce couple
                                // est celui du mag recherche
                            }// Fin FOR
                        }
                        else
                        {
                            if (isLoggingDebug())
                            {
                                logDebug("---> (getCatalogRepository() == null ");
                            }
                        }
                    }
                    else
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("---> (Set) l_sku.getPropertyValue(PROPERTY_SERVICES) == null ");
                        }
                    }
                }
                else
                {
                    if (isLoggingDebug())
                    {
                        logDebug("---> getCatalogRepository() == null");
                    }
                }
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("---> getServicesSku : a_IdSku ou a_IdMagasin null.");
                }
            }
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.getServicesSku(String a_IdSku,String a_IdMagasin).");
        }

        return l_LesServicesDuSku;
    }

    /**
     * . Permet de voir si un produit est de type article ou pas
     * 
     * @param a_listeProduits
     *            une liste de produits
     * 
     * @return List de produits de prix non nul
     */
    
    /* Debut : 26/11/08 : Mantis 1229 */
    public List triTypeArticle(List a_listeProduits)
    {
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoProductTools.triTypeArticle ==> method is running...");
        }

        // liste des produits de prix non nul
        List l_listeProduitsTriee = new ArrayList();

        // On séparer en 3 listes les articles remontées par la bdd (en fonction du type article
        List l_aListeTypeArticleUnOuDeux = new ArrayList();
        List l_aListeTypeArticleTrois = new ArrayList();
        RepositoryItem l_product;
        Collection l_childSkus;
        for (int l_i = 0; l_i < a_listeProduits.size(); l_i++)
        {
            l_product = (RepositoryItem) a_listeProduits.get(l_i);
            // obtention des skus du produit en cours
            l_childSkus = (Collection) l_product.getPropertyValue("childSKUs");
            // Test du type artile
            if(null!= l_product && isTypeArticleTrois(l_childSkus))
            {
                l_aListeTypeArticleTrois.add(l_product);
            }
            else
            {
                l_aListeTypeArticleUnOuDeux.add(l_product);
            }
        }
        
        // Puis on concatène les 2 listes
        if(null!=l_aListeTypeArticleUnOuDeux && l_aListeTypeArticleUnOuDeux.size()>0)
        {
            l_listeProduitsTriee = l_aListeTypeArticleUnOuDeux;
        }
        if(null!=l_aListeTypeArticleTrois && l_aListeTypeArticleTrois.size()>0)
        {
            l_listeProduitsTriee.addAll(l_aListeTypeArticleTrois);
        }
        
        if (isLoggingDebug())
        {
            logDebug("<<Droplet>> : CastoProductTools.triTypeArticle ==> method is plus running...");
        }
        
        return l_listeProduitsTriee;
    }/* Fin Mantis 1229 */
    
    /**
     * . Permet de voir si un produit est de type article ou pas
     * 
     * @param a_childSkus
     *            a_childSkus
     * @return boolean ce produit est il de type article 3 ou non?
     */
    /* Debut : 26/11/08 : Mantis 1229 */
    public boolean isTypeArticleTrois(Collection a_childSkus)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.produit.CastoProductTools.isTypeArticleTrois(Collection a_childSkus).");
        }
        
        Iterator l_iterator = a_childSkus.iterator();
        RepositoryItem l_sku;
        Integer l_typeArticle;
        boolean l_typeArticleTrois = false;
        while (l_iterator.hasNext())
        {
            l_sku = (RepositoryItem) l_iterator.next();
            Object l_typeA = l_sku.getPropertyValue("typeArticle");
            if(null!=l_typeA)
            {
                l_typeArticle = (Integer) l_typeA;
                /*
                 * Logica - 17/09/2008 - Fiche mantis 1229
                 * ajout de la condition : null!= l_typeArticle 
                 */
                if(null!= l_typeArticle && !"".equals(l_typeArticle.toString()) && l_typeArticle.intValue() == CastoConstantes.TROIS)
                {
                    l_typeArticleTrois = true;
                }
                /*
                 * Logica - 17/09/2008 - Fiche mantis 1259
                 */
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.produit.CastoProductTools.isTypeArticleTrois(Collection a_childSkus).");
        }
        
        return l_typeArticleTrois;
    }/* Fin Mantis 1229 */
    
    
    /* Debut Quick Win Lot 2*/
    /**
     * Permet de voir si un produit a les frais de livraison gratuit ou 
     * les frais de livraison forfaitaire.
     *
     * @param a_repoItem
     *            a_repoItem
     * @return boolean.
     */
    public boolean hasForfaitTransport(RepositoryItem a_repoItem, Profile a_profile)
    {
        String l_sSommeForf;
        boolean l_exoPFE;
        if(null != a_repoItem.getPropertyValue("sommeForfaitaire"))
        {
            l_sSommeForf = a_repoItem.getPropertyValue("sommeForfaitaire").toString();
        }
        else
        {
            l_sSommeForf="0.0"; 
        }
        
        if(null != a_repoItem.getPropertyValue("exonerationPFE"))
        {
            l_exoPFE = ((Boolean) a_repoItem.getPropertyValue("exonerationPFE")).booleanValue();
        }
        else
        {
            l_exoPFE=false; 
        }

        RepositoryItem l_magasin    = (RepositoryItem)a_profile.getPropertyValue("sessionMagasin");
        
        boolean l_hasForfaitTransport =false;

        if( null != l_magasin)
        {
            int l_idMagasin = new Integer(l_magasin.getRepositoryId()).intValue();
            if(CastoConstantesDefense.CASTORAMA_DIRECT_INT == l_idMagasin)
            {
                if(!l_sSommeForf.equals("0.0") && !l_exoPFE)
                {
                    l_hasForfaitTransport = true;
                }
            }
        }      
        return l_hasForfaitTransport;
    }/* Fin Quick Win Lot 2 */
    
    
    /* Projet Destockage */
    /**
     * Permet de savoir si un article est en destockage.
     * @param a_sku
     *          l'article concerné.
     * @return boolean.
     */
    public static boolean isInDestockage(RepositoryItem a_sku)
    {
        boolean l_isDestock = false;
        Date l_dDateDebDestock  = null;
        Date l_dDateFinDestock  = null;
        Date l_dDateCourante = new Date();
        Calendar l_dateDebDestock = Calendar.getInstance();
        Calendar l_dateFinDestock = Calendar.getInstance();
        Calendar l_dateCourante = Calendar.getInstance();
        l_dateCourante.setTime(l_dDateCourante);
        
        if(null != a_sku.getPropertyValue("dateDebDestock") && null != a_sku.getPropertyValue("dateFinDestock"))
        {
            l_dDateDebDestock = (Date) a_sku.getPropertyValue("dateDebDestock");
            l_dDateFinDestock = (Date) a_sku.getPropertyValue("dateFinDestock");
        
            l_dateDebDestock.setTime(l_dDateDebDestock);
            l_dateFinDestock.setTime(l_dDateFinDestock);
            l_dateFinDestock.add(Calendar.DATE, 1);
            
            if(l_dateCourante.after(l_dateDebDestock) && l_dateCourante.before(l_dateFinDestock) && CastoCategoryManager.isInCategorieDestockage(a_sku))
            {
                l_isDestock = true;
            }
        }
    
        return l_isDestock;
    }
    
    /* Projet Destockage */
    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_produit   Produit en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public static boolean isInDestockageProduit (RepositoryItem a_produit)
    {
        boolean l_isDestock = false;
        
        RepositoryItem l_sku = null;
        Iterator l_iterator;
        
        Collection l_skus = (Collection) a_produit.getPropertyValue("childSkus");
        
        if (l_skus != null && l_skus.size() > 0)
        {
            l_iterator = l_skus.iterator();

            while(l_iterator.hasNext() && !l_isDestock)
            {
                l_sku = (RepositoryItem) l_iterator.next();
                l_isDestock = isInDestockage(l_sku);
            }
        }
        return l_isDestock;
    }
    
    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_skuId     Id du sku en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public boolean isInDestockage (String a_skuId)
    {
        boolean l_retour = false;      
        RepositoryItem l_sku = null;
        RepositoryItem l_oParent = null;
        
        try
        {
            if (a_skuId != null)
            {
                if (a_skuId.startsWith(PRDM))
                {
                    l_oParent = getCatalogRepository().getItem(a_skuId, "casto_product");            
                    l_retour = isInDestockageProduit(l_oParent);
                }
                else
                {
                    l_sku = getCatalogRepository().getItem(a_skuId, "casto_sku");
                    l_retour = isInDestockage(l_sku);
                }
            }
        }
        catch (RepositoryException l_exception)
        {
            if (isLoggingError())
            {
                logError("CastoProductTools.isInDestockage('"+a_skuId+"') : "+l_exception.toString());
            }
        }
        return l_retour;
    }
}