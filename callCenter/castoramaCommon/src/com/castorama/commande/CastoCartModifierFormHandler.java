package com.castorama.commande;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.SoftGoodFormHandler;
import atg.commerce.promotion.PromotionTools;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.castorama.catalog.CastoCatalogManager;
import com.castorama.category.CastoCategoryManager;
import com.castorama.produit.CastoProductTools;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.inventory.InventoryAdapter;
import com.castorama.profil.CastoProfileManager;

/**
 * @author Florte J�r�my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Extension du CartModifierFormHandler standard pour les besoins sp�cifiques de
 * Castorama.
 */
public class CastoCartModifierFormHandler extends SoftGoodFormHandler
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
     
    /**
     * Constante pour la session utilisateur.
     */
    public static final String TDUID = "tduid";

    /**
     * D�signe un paiement par virement.
     */
    public static final String PAIEMENT_PAR_VIREMENT = "Virement";

    /**
     * D�signe un paiement par t�l�phone.
     */
    public static final String PAIEMENT_PAR_TELEPHONE = "Call-Center";

    /**
     * D�signe un paiement par ch�que cadeau.
     */
    public static final String PAIEMENT_PAR_CHEQUE_CADEAU = "Cheque cadeau";

    /**
     * D�signe un paiement par ch�que bancaire.
     */
    public static final String PAIEMENT_PAR_CHEQUE = "Cheque";

    /**
     * D�signe un paiement via la carte l'atout.
     */
    public static final String PAIEMENT_CARTE_ATOUT = "Atout";

    /**
     * Internationalisation pour le message n� carte atout invalide.
     */
    //private static final String BUNDLE_NUM_CARTE_ATOUT_INVALIDE = "panier.paiement.atout.num.carte.invalide";

    /**
     * Internationalisation pour le message n� carte atout invalide.
     */
    private static final String ACTIVE_PROMOTION = "activePromotions";

    /**
     * Internationalisation pour le message n� carte atout invalide.
     */
    private static final String USED_PROMOTION = "usedPromotions";

    /** Type de mise à jour du profil. **/
    private static final Integer MODIFICATION = new Integer(1);

    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    private ShippingGroupManager m_shippingGroupManager;

    /** . CategoryManager * */
    private CastoCategoryManager m_manager;

    /** . ProductTools * */
    private CastoProductTools m_productTools;
    
    /**
     * Commande en cours.
     */
    private Order m_tmpOrder;

    /**
     * DataSource pour les acc�s base.
     */
    private DataSource m_DataSource = null;

    /**
     * Catalog Repository.
     */
    private Repository m_CatalogRepository;

    /**
     * Catalog Repository.
     */
    private MutableRepository m_couponRepository;

    /**
     * R�f�rence vers le CastoProfileManager.
     */
    private CastoProfileManager m_profileManager;

    /**
     * Type d'url pour le rewriting.
     */
    private String m_typeUrl;

    /**
     * R�f�rence vers le manager du CatalogRepository.
     */
    private CastoCatalogManager m_catalogManager;

    /**
     * R�f�rence vers le composant d'utilitaire pour les commandes.
     */
    private CastoOrderTools m_orderTools;

    /**
     * URL de succès pour l'enregistrement des chèques cadeaux.
     */
    private String m_enregistrerChequeCadeauSuccessURL;

    /**
     * Nombre de lignes maximum pour le panier.
     */
    private int m_nbLignesArticle;

    /** . Inventory manager * */
    private InventoryManager m_oInventoryManager;

    /** . Repository * */
    private Repository m_oRepository;

    private boolean m_blocagePourDestockage;

    /* MODIF 890 */
    private boolean m_blocagePourBonneAffaire;

    /**
     * Adapter pour la gestion des stocks.
     */
    private InventoryAdapter m_inventoryAdapter;

    private String m_shippingGroupName;

    private String m_etageLivraison;

    private String m_RetourPayboxSuccessUrl;
    
    private String m_RetourPayboxErrorUrl;
    
    private String m_PreSofincoSuccessUrl;
    
    private String m_PreSofincoErrorUrl;

    /* Code Review */private Locale m_defaultLocale;/* Code Review */
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /**
     * @return the successUrl
     */
    public String getRetourPayboxSuccessUrl()
    {
        return m_RetourPayboxSuccessUrl;
    }

    /**
     * @param a_RetourPayboxSuccessUrl
     *            the successUrl to set
     */
    public void setRetourPayboxSuccessUrl(String a_RetourPayboxSuccessUrl)
    {
        m_RetourPayboxSuccessUrl = a_RetourPayboxSuccessUrl;
    }
    
    /**
     * @return the errorUrl
     */
    public String getRetourPayboxErrorUrl()
    {
        return m_RetourPayboxErrorUrl;
    }

    /**
     * @param a_RetourPayboxErrorUrl
     *            the errorUrl to set
     */
    public void setRetourPayboxErrorUrl(String a_RetourPayboxErrorUrl)
    {
        m_RetourPayboxErrorUrl = a_RetourPayboxErrorUrl;
    }
    
    /**
     * @return the successUrl
     */
    public String getPreSofincoSuccessUrl()
    {
        return m_PreSofincoSuccessUrl;
    }

    /**
     * @param a_PreSofincoSuccessUrl
     *            the successUrl to set
     */
    public void setPreSofincoSuccessUrl(String a_PreSofincoSuccessUrl)
    {
        m_PreSofincoSuccessUrl = a_PreSofincoSuccessUrl;
    }

    /**
     * @return the errorUrl
     */
    public String getPreSofincoErrorUrl()
    {
        return m_PreSofincoErrorUrl;
    }

    /**
     * @param a_PreSofincoErrorUrl
     *            the errorUrl to set
     */
    public void setPreSofincoErrorUrl(String a_PreSofincoErrorUrl)
    {
        m_PreSofincoErrorUrl = a_PreSofincoErrorUrl;
    }
    
    /**
     * Méthode qui renvoie le shipping group employé pour l'ajout d'un article
     * au panier.
     * 
     * @return Le shipping group employé pour l'ajout d'un article au panier.
     */
    public String getShippingGroupName()
    {
        return m_shippingGroupName;
    }

    /**
     * Méthode qui fixe le shipping group employé pour l'ajout d'un article au
     * panier.
     * 
     * @param a_shippingGroupName
     *            Le shipping group employé pour l'ajout d'un article au panier.
     */
    public void setShippingGroupName(String a_shippingGroupName)
    {
        m_shippingGroupName = a_shippingGroupName;
    }

    /**
     * Méthode qui renvoie une référence vers le composant de gestion de
     * l'inventaire.
     * 
     * @return InventoryManager Une référence vers le composant de gestion de
     *         l'inventaire.
     */
    public InventoryManager getInventoryManager()
    {
        return m_oInventoryManager;
    }

    /**
     * Méthode qui fixe la référence vers le composant de gestion de
     * l'inventaire.
     * 
     * @param a_oInventoryManager
     *            La nouvelle référence vers le composant de gestion de
     *            l'inventaire.
     */
    public void setInventoryManager(InventoryManager a_oInventoryManager)
    {
        this.m_oInventoryManager = a_oInventoryManager;
    }

    /**
     * @return Repository
     */
    public Repository getCatalogRepository()
    {
        return m_CatalogRepository;
    }

    /**
     * @param a_CatalogRepository
     *            Repository
     */
    public void setCatalogRepository(Repository a_CatalogRepository)
    {
        m_CatalogRepository = a_CatalogRepository;
    }

    /**
     * @return the tmpOrder
     */
    public Order getTmpOrder()
    {
        return m_tmpOrder;
    }

    /**
     * @param a_tmpOrder
     *            the tmpOrder to set
     */
    public void setTmpOrder(Order a_tmpOrder)
    {
        m_tmpOrder = a_tmpOrder;
    }

    /**
     * @return DataSource
     */
    public DataSource getDataSource()
    {
        return m_DataSource;
    }

    /**
     * @param a_dataSource
     *            DataSource
     */
    public void setDataSource(DataSource a_dataSource)
    {
        m_DataSource = a_dataSource;
    }

    /**
     * Renvoit une r�f�rence vers le CastoProfileManager.
     * 
     * @return CastoProfileManager Une r�f�rence vers le CastoProfileManager.
     */
    public CastoProfileManager getProfileManager()
    {
        return m_profileManager;
    }

    /**
     * Fixe la r�f�rence vers le CastoProfileManager.
     * 
     * @param a_profileManager
     *            La nouvelle r�f�rence vers le CastoProfileManager.
     */
    public void setProfileManager(CastoProfileManager a_profileManager)
    {
        m_profileManager = a_profileManager;
    }

    /**
     * Renvoit le type d'url � g�n�rer pour le rewriting.
     * 
     * @return String Le type d'url � g�n�rer pour le rewriting.
     */
    public String getTypeUrl()
    {
        return m_typeUrl;
    }

    /**
     * Fixe le type d'url � g�n�rer pour le rewriting.
     * 
     * @param a_typeUrl
     *            le nouveau type d'url � g�n�rer pour le rewriting.
     */
    public void setTypeUrl(String a_typeUrl)
    {
        m_typeUrl = a_typeUrl;
    }

    /**
     * Renvoie la r�f�rence vers le manager du CatalogRepository.
     * 
     * @return CastoCatalogManager La r�f�rence vers le manager du
     *         CatalogRepository.
     */
    public CastoCatalogManager getCatalogManager()
    {
        return m_catalogManager;
    }

    /**
     * Fixe la r�f�rence vers le manager du CatalogRepository.
     * 
     * @param a_catalogManager
     *            La nouvelle r�f�rence vers le manager du CatalogRepository.
     */
    public void setCatalogManager(CastoCatalogManager a_catalogManager)
    {
        m_catalogManager = a_catalogManager;
    }

    /**
     * Renvoie une r�f�rence vers le composant d'utilitaires des commandes.
     * 
     * @return CastoOrderTools Une r�f�rence vers les composant d'utilitaires
     *         des commandes.
     */
    public CastoOrderTools getOrderTools()
    {
        return m_orderTools;
    }

    /**
     * Fixe la r�f�rence vers le composant d'utilitaires des commandes.
     * 
     * @param a_orderTools
     *            La nouvelle r�f�rence vers le composant d'utilitaires des
     *            commandes.
     */
    public void setOrderTools(CastoOrderTools a_orderTools)
    {
        m_orderTools = a_orderTools;
    }

    /**
     * Renvoie l'URL de succès pour l'enregistrement des chèques cadeaux.
     * 
     * @return String L'URL de succès pour l'enregistrement des chèques cadeaux.
     */
    public String getEnregistrerChequeCadeauSuccessURL()
    {
        return m_enregistrerChequeCadeauSuccessURL;
    }

    /**
     * Fixe l'URL de succès pour l'enregistrement des chèques cadeaux..
     * 
     * @param a_enregistrerChequeCadeauSuccessURL
     *            La nouvelle URL de succès pour l'enregistrement des chèques
     *            cadeaux.
     */
    public void setEnregistrerChequeCadeauSuccessURL(String a_enregistrerChequeCadeauSuccessURL)
    {
        m_enregistrerChequeCadeauSuccessURL = a_enregistrerChequeCadeauSuccessURL;
    }

    /**
     * Renvoie le nombre de lignes maximum pour le panier.
     * 
     * @return int le nombre de lignes maximum pour le panier.
     */
    public int getNbLignesArticle()
    {
        return m_nbLignesArticle;
    }

    /**
     * Fixe le nombre de lignes maximum pour le panier.
     * 
     * @param a_nbLignesArticle
     *            Le nouveau nombre de lignes maximum pour le panier.
     */
    public void setNbLignesArticle(int a_nbLignesArticle)
    {
        m_nbLignesArticle = a_nbLignesArticle;
    }

    /**
     * .
     * 
     * Repository
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
     * Repository
     * 
     * @param a_oRepository
     *            Repository
     */
    public void setRepository(Repository a_oRepository)
    {
        this.m_oRepository = a_oRepository;
    }

    /**
     * @return the couponRepository
     */
    public MutableRepository getCouponRepository()
    {
        return m_couponRepository;
    }

    /**
     * @param a_couponRepository
     *            the couponRepository to set
     */
    public void setCouponRepository(MutableRepository a_couponRepository)
    {
        m_couponRepository = a_couponRepository;
    }

    /**
     * @return the manager
     */
    public CastoCategoryManager getManager()
    {
        return m_manager;
    }

    /**
     * @param a_manager
     *            the manager to set
     */
    public void setManager(CastoCategoryManager a_manager)
    {
        m_manager = a_manager;
    }
   
    /**
     * .
     * @return m_productTools
     */
    public CastoProductTools getProductTools()
    {
        return m_productTools;
    }

    /**
     * .
     * @param a_productTools
     */
    public void setProductTools(CastoProductTools a_productTools)
    {
        m_productTools = a_productTools;
    }

    /**
     * Méthode qui renvoie la référence vers l'adpater d'inventory manager.
     * 
     * @return La référence vers l'adpater d'inventory manager.
     */
    public InventoryAdapter getInventoryAdapter()
    {
        return m_inventoryAdapter;
    }

    /**
     * Méthode qui fixe la référence vers l'adpater d'inventory manager.
     * 
     * @param a_inventoryAdapter
     *            La nouvelle référence vers l'adpater d'inventory manager.
     */
    public void setInventoryAdapter(InventoryAdapter a_inventoryAdapter)
    {
        m_inventoryAdapter = a_inventoryAdapter;
    }

    /**
     * Référence vers le gestionnaire des shipping groups.
     * 
     * @return une référence vers le gestionnaire des shipping groups.
     */
    public ShippingGroupManager getShippingGroupManager()
    {
        return m_shippingGroupManager;
    }

    /**
     * Fixe la référence vers le gestionnaire des shipping groups.
     * 
     * @param a_shippingGroupManager
     *            La nouvelle référence vers le gestionnaire des shipping
     *            groups.
     */
    public void setShippingGroupManager(ShippingGroupManager a_shippingGroupManager)
    {
        m_shippingGroupManager = a_shippingGroupManager;
    }

    /**
     * Accesseur pour l'étage de livraison.
     * 
     * @return L'étage de livraison.
     */
    public String getEtageLivraison()
    {
        return m_etageLivraison;
    }

    /**
     * Accesseur pour l'étage de livraison.
     * 
     * @param a_etageLivraison
     *            le nouvel étage de livraison.
     */
    public void setEtageLivraison(String a_etageLivraison)
    {
        m_etageLivraison = a_etageLivraison;

        CastoOrder l_order = (CastoOrder) getOrder();

        synchronized (l_order)
        {
            try
            {
                l_order.setEtageLivraison(new Integer(m_etageLivraison).intValue());
            }
            catch (NumberFormatException l_nfe)
            {
                logError("CastoCartModifierFormHandler.handleChangerEtageLivraison() : impossible de transformer la valeur "
                        + getEtageLivraison() + " en entier.");
            }
        }
    }

    /* 890 DEBUT */
    /**
     * @return the blocagePourBonneAffaire
     */
    public boolean isBlocagePourBonneAffaire()
    {
        return m_blocagePourBonneAffaire;
    }

    /**
     * @param a_blocagePourBonneAffaire
     *            the blocagePourBonneAffaire to set
     */
    public void setBlocagePourBonneAffaire(boolean a_blocagePourBonneAffaire)
    {
        m_blocagePourBonneAffaire = a_blocagePourBonneAffaire;
    }

    /* 890 FIN */

    /**
     * @return the blocagePourDestockage
     */
    public boolean isBlocagePourDestockage()
    {
        return m_blocagePourDestockage;
    }

    /**
     * @param a_blocagePourDestockage
     *            the blocagePourDestockage to set
     */
    public void setBlocagePourDestockage(boolean a_blocagePourDestockage)
    {
        m_blocagePourDestockage = a_blocagePourDestockage;
    }
    /* Code Review */    
    /**
     * .
     * Renvoie le currency code fr_FR_EURO
     * @return String fr_FR_EURO
     */
    public Locale getDefaultLocale()
    {
        return m_defaultLocale;
    }
    
    /**
     * .
     * Fixe la reference du currency code fr_FR_EURO
     * 
     * @param a_defaultLocale
     *            la reference du currency code fr_FR_EURO
     */
    public void setDefaultLocale(Locale a_defaultLocale)
    {
        m_defaultLocale = a_defaultLocale;
    }
    /* Code Review */
    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * 
     * @see atg.commerce.order.purchase.CartModifierFormHandler#addItemToOrder(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            La réponse HTTP.
     * 
     * @throws ServletException
     *             Si une exception survient.
     * @throws IOException
     *             Si une exception survient.
     * 
     */
    public void preAddItemToOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.preAddItemToOrder().");
        }

        boolean l_declinaison = a_request.getRequestURI().endsWith("declinaison_style.jsp");

        if (a_request.getRequestURI().endsWith("fiche-produit.jsp") || l_declinaison)
        {
            /*
             * Booléen pour savoir si on est en dev ou en prod
             */
            boolean l_prod = l_declinaison ? false : CastoConstantes.REECRITURE_PROD.equals(getTypeUrl());

            /*
             * On récupère la liste des items qui ont été ajoutés au panier.
             * Pour chaque item, on l'ajoute à une chaîne afin de générer une
             * url du type :
             * 
             * <success_url>&catalogRefIds=Casto24004,Casto25002&type=1|2|3&quantite=1
             */

            // Obtention des ids des skus ajoutés au panier
            String[] l_catalogRefIds = getCatalogRefIds();

            // Utilisation d'un StringBuffer pour la concatenation de chaine
            StringBuffer l_urlSuffix = null;

            // Variable pour l'url de redirection
            StringBuffer l_url = null;

            if (null != l_catalogRefIds)
            {
                int l_size = l_catalogRefIds.length;

                // Si on a au moins article ajoute au panier
                if (0 != l_size)
                {
                    String l_addItemToOrder = getAddItemToOrderSuccessURL();

                    // Url dans StringBuffer
                    l_url = new StringBuffer(l_addItemToOrder);

                    /*
                     * On teste si article principal ou secondaire, ou pack
                     */
                    int l_type = 0;

                    if (null != a_request.getPostParameter("articleSecondaire"))
                    {
                        l_type = CastoConstantesCommande.TYPE_ARTICLE_SECONDAIRE;
                    }
                    else
                    {
                        l_type = (null != a_request.getPostParameter("pack")) ? CastoConstantesCommande.TYPE_ARTICLE_PACK
                                : CastoConstantesCommande.TYPE_ARTICLE_PRINCIPAL;
                    }

                    /*
                     * On complète l'url avec l'identifiant des articles ajoutés
                     */

            //Pour Referencement : Mise en commentaire / * * /

            /*if (l_prod)
            {
                l_urlSuffix = new StringBuffer("-" + l_catalogRefIds[0]);
            }
            else
            {
                Enumeration l_enum = a_request.getParameterNames();
                l_urlSuffix = (l_enum.hasMoreElements()) ? new StringBuffer("&catalogRefIds=")
                        .append(l_catalogRefIds[0]) : new StringBuffer("?catalogRefIds=")
                        .append(l_catalogRefIds[0]);
            // Pour RÃ©fÃ©rencement : Mise en commentaire / * * /
            } */
 
            if(null != l_url && l_url.indexOf("?")>CastoConstantes.NEG)
            {
                l_urlSuffix= new StringBuffer("&catalogRefIds=");             
            }
            else
            {
                l_urlSuffix= new StringBuffer("?catalogRefIds=");
            }
            l_urlSuffix.append(l_catalogRefIds[0]);

            /*
             * Rem : la boucle commence Ã  1 car on dÃ©jÃ  ajoutÃ©
             * l'identifiant du premier article.
             * 
             * Cela Ã©vite les tests du genre : if (0 != l_i) {
             * l_urlSuffix.append(","); }
             */
            for (int l_i = 1; l_i < l_size; l_i++)
            {
                // Ajout de l'identifiant de l'article ajoutÃ©
                l_urlSuffix.append(",").append(l_catalogRefIds[l_i]);
            }

            /*
             * Si on a bien eu au moins un article ajoutÃ©, on modifie
             * l'url de succes.
             */
            // Pour Referencement : Mise en commentaire / * * /
            /*if (l_prod)
            {
                l_url = new StringBuffer(l_url.toString().replaceAll(".htm", "")).append(l_urlSuffix).append(
                        "-").append(l_type).append(".htm");
                if (CastoConstantesCommande.TYPE_ARTICLE_PRINCIPAL == l_type)
                {
                    l_url.append("?quantite=").append(getQuantity());
                }
            }
            else
            {*/
                l_url.append(l_urlSuffix).append("&type=").append(l_type);
                if (CastoConstantesCommande.TYPE_ARTICLE_PRINCIPAL == l_type)
                {
                    l_url.append("&quantite=").append(getQuantity());
                }
           // Pour Referencement : Mise en commentaire / * * /
           /* } */

                    if (isLoggingDebug())
                    {
                        logDebug("|--> url : " + l_url);
                    }

                    setAddItemToOrderSuccessURL(l_url.toString().replaceAll("&amp;", "&"));
                }
                // Fin du if (0 != l_size)
            }
            // Fin du if (null != l_catalogRefIds)
        }
        else if (a_request.getRequestURI().endsWith("page-liste-recherche.jsp")
                || a_request.getRequestURI().endsWith("pageListe.jsp"))
        {
            String l_addItemToOrder = getAddItemToOrderSuccessURL();

            // Suppression popup=compare + compare=true
            setAddItemToOrderSuccessURL(l_addItemToOrder.replaceAll("&popup=compare", "").replaceAll("&compare=true",
                    ""));
        }
        // Fin du if (a_request.getRequestURI().endsWith("fiche-produit.jsp"))

        /*
         * Appel a la methode parent, traitement par defaut d'ATG.
         */
        super.preAddItemToOrder(a_request, a_response);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.preAddItemToOrder().");
        }
    }

    /**
     * Méthode qui supprime les Shipping Group ayant des shippingMethod autres
     * que LAD ou MAD.
     * 
     * @param a_order
     *            La commande à modifier.
     */
    private void clearShippingGroups(Order a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.clearShippingGroups(Order).");
        }

        if (null != a_order)
        {
    /* Code Review : transaction pattern */
            ShippingGroupManager l_manager = getShippingGroupManager();
            if (null != l_manager)
            {
                // Suppression du hardgoodShippingGroup (créé par défaut par
                // ATG)
                ShippingGroup l_shippingGroup;

                List l_shippingGroups = a_order.getShippingGroups();
                List l_removals = new ArrayList();
                String l_id;

                if (null != l_shippingGroups)
                {
                    for (Iterator l_iterator = l_shippingGroups.iterator(); l_iterator.hasNext();)
                    {
                        l_shippingGroup = (ShippingGroup) l_iterator.next();

                        if (isLoggingInfo())
                        {
                            logInfo("CastoCartModifierFormHandler.clearShippingGroups(Order) : shipping method : "
                                    + l_shippingGroup.getShippingMethod() + ", nb cir : "
                                    + l_shippingGroup.getCommerceItemRelationshipCount());
                        }

                        if ("hardgoodShippingGroup".equals(l_shippingGroup.getShippingMethod())
                                || 0 == l_shippingGroup.getCommerceItemRelationshipCount())
                        {
                            if (isLoggingInfo())
                            {
                                logInfo("CastoCartModifierFormHandler.clearShippingGroups(Order) : ajout à removals de : "
                                        + l_shippingGroup.getId());
                            }

                            l_removals.add(l_shippingGroup.getId());
                        }
                    }
                    try
                    {
                        for (Iterator l_iterator = l_removals.iterator(); l_iterator.hasNext();)
                        {
                            l_id = (String) l_iterator.next();
                            if (isLoggingInfo())
                            {
                                logInfo("CastoCartModifierFormHandler.clearShippingGroups(Order) : suppression du shipping group : "
                                        + l_id);
                            }
                            l_manager.removeAllRelationshipsFromShippingGroup(a_order, l_id);
                            l_manager.removeShippingGroupFromOrder(a_order, l_id);
                        }
                    }
                    catch(CommerceException l_cce)
                    {
                        logError(l_cce);
                    }
                }
        /* Code Review : transaction pattern */

            }        
        }    
            
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.clearShippingGroups(Order).");
        }
    }

    /**
     * @see atg.commerce.order.ShoppingCartFormHandler#handleRemoveItemFromOrderByRelationshipId(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse)
     * 
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            La réponse HTTP.
     * 
     * @return Un booléen indiquant si une redirection est nécessaire ou non.
     * 
     * @throws IOException
     *             Si une erreur survient.
     * @throws ServletException
     *             Si une erreur survient.
     */
    public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws IOException, ServletException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleRemoveItemFromOrder().");
        }

        boolean l_ret = super.handleRemoveItemFromOrder(a_request, a_response);
        Order l_order = getOrder();
    /* Code Review */        
        if (null != l_order)
        {
            /* Transaction */
            boolean l_rollback = false;
            TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
            synchronized (l_order)
            {
                try
                {
                    l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                    try
                    {
                        clearShippingGroups(l_order);
                        getOrderManager().updateOrder(l_order);
                    }
                    catch (Exception l_ex)
                    {
                        logError("CastoCartModifierFormHandler.handleRemoveItemFromOrder : " + l_ex.toString());
                        l_rollback = true;
                    }
                    finally
                    {
                        l_transactionDemarcation.end(l_rollback);
                    }
                }
                catch (TransactionDemarcationException l_tde)
                {
                    logError("CastoOrderTools.handleRemoveItemFromOrder(Order)  : erreur transaction : "
                            + l_tde.toString());
                }
            }//Fin Synchronisation
        }
    /* Code Review */        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleRemoveItemFromOrder().");
        }

        return l_ret;
    }

    /**
     * Méthode qui renvoie le Shipping Group correspondant à la shipping method
     * adéquate (stockée dans m_shippingGroupName).
     * 
     * @param a_order
     *            La commande à considérer.
     * 
     * @return Le shipping Group avec la bonne méthode de shipping ou null.
     */
    private ShippingGroup getShippingGroup(Order a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.GetShippingGroup().");
        }

        ShippingGroup l_shippingGroup = null;
        String l_shippingGroupName = getShippingGroupName();

        if (isLoggingDebug())
        {
            logDebug("--> CastoCartModifierFormHandler.GetShippingGroup() : ShippingGroup name : "
                    + l_shippingGroupName);
        }

        if (null != a_order)
        {
            for (Iterator l_iterator = a_order.getShippingGroups().iterator(); l_iterator.hasNext();)
            {
                l_shippingGroup = (ShippingGroup) l_iterator.next();

                if (isLoggingDebug())
                {
                    logDebug("Shipping group : " + l_shippingGroup.getId() + ", method :"
                            + l_shippingGroup.getShippingMethod());
                }

                if (null != l_shippingGroup && null != l_shippingGroupName && l_shippingGroupName.equals(l_shippingGroup.getShippingMethod()))
                {
                    break;
                }
                else
                {
                    l_shippingGroup = null;
                }
            }

            // Si le shipping group requis n'existe pas encore dans la commande
            // en cours, on le crée
            if (null == l_shippingGroup)
            {
                if (isLoggingDebug())
                {
                    logDebug("CastoCartModifierFormHandler.GetShippingGroup() : création d'un nouveau ShippingGroup : "
                            + l_shippingGroupName);
                }

                ShippingGroupManager l_manager = getShippingGroupManager();

                if (null != l_manager)
                {
                    try
                    {
                        l_shippingGroup = l_manager.createShippingGroup();

                        l_shippingGroup.setShippingMethod(l_shippingGroupName);

                        l_manager.addShippingGroupToOrder(a_order, l_shippingGroup);
                    }
                    catch (CommerceException l_ce)
                    {
                        logError("--> CastoCartModifierFormHandler.GetShippingGroup() : " + l_ce.toString());
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.GetShippingGroup().");
        }

        return l_shippingGroup;
    }

    /**
     * @see atg.commerce.order.ShoppingCartFormHandler#addItemToOrder(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse, boolean)
     * 
     * Source : \DCS\src\Java\atg\commerce\order\ShoppingCartFormHandler.java.
     * 
     * @param a_request
     *            the servlet's request
     * @param a_response
     *            the servlet's response
     * @param a_IsGift
     *            If this is true, the added items are gifts. see the giftlist
     *            property.
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    protected void addItemToOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response,
            boolean a_IsGift, Order a_order) throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.addItemToOrder().");
        }

        if (a_order == null)
        {
            String l_msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, a_request, a_response);
            throw new ServletException(l_msg);
        }

        String[] l_skuIds = getCatalogRefIds();
        ShippingGroup l_sg = null;

        if ((l_skuIds == null) || (l_skuIds.length == 0))
        {
            String l_msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, a_request, a_response);
            String l_propertyPath = generatePropertyPath("catalogRefIds");
            addFormException(new DropletFormException(l_msg, l_propertyPath, MSG_NO_ITEMS_TO_ADD));
        }
        else
        {
            /*
             * synchronized (l_order) {
             */
            // PLD : Nettoyage des shipping groups
            clearShippingGroups(a_order);

            l_sg = getShippingGroup(a_order);

            if (isLoggingInfo())
            {
                logInfo("CastoCartModifierFormHandler.addItemToOrder() : Obtention du ShippingGroup : [ "
                        + l_sg.getId() + ", " + l_sg.getShippingMethod() + "]");
            }
            /* } */
        }

        if (l_sg == null)
        {
            String l_msg = formatUserMessage(MSG_ERROR_ADDING_ITEM, a_request, a_response);
            addFormException(new DropletFormException(l_msg, MSG_ERROR_ADDING_ITEM));

            return;
        }

        try
        {
            /*
             * synchronized (l_order) {
             */
            // For each of the parts, check if the QTY is > 0.
            // If not, generate a form error.
            // Otherwise, add it to the order:
            for (int l_c = 0; l_c < l_skuIds.length; l_c++)
            {
                long l_quantity = getQuantity(l_skuIds[l_c], a_request, a_response);

                if (l_quantity <= 0)
                {
                    // If the quantity is 0 it is possible this is due
                    // to the fact that the item was
                    // just removed by the deleteItems call. Consult the
                    // deletedSkus list and
                    // determine if that sku was a part of the deleted skus.
                    // If it was we will skip this error.

                    List l_deletedSkus = getDeletedSkus();

                    if (l_deletedSkus != null && l_deletedSkus.contains(l_skuIds[l_c]))
                    {
                        continue;
                    }

                    String l_msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, a_request, a_response);
                    String l_propertyPath = generatePropertyPath("quantity");

                    addFormException(new DropletFormException(l_msg, l_propertyPath,
                            MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO));
                    continue;

                    // NOTE: Somewhere the QTY gets reset to 1. Not sure
                    // we should show an error about QTY being less then 0,
                    // and change the QTY. It is confusing...
                }
                // Fin du if (l_quantity <= 0)

                // Modification G.P. pour sfd soldes
                // la quantite ne doit pas depasser le stock
                // pour les produits de destockage
                long l_stock = CastoConstantes.NEG;
                boolean l_pasAjout = false;
                try
                {
                    // l_stock =
                    // getInventoryManager().queryStockLevel(l_skuIds[l_c]);
                    
                    InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager((CastoOrder) a_order);
                    
                    if (null != l_inventoryManager)
                    {
                        l_stock = l_inventoryManager.queryStockLevel(l_skuIds[l_c]);
                    }
                    
                    List l_commerces = getOrder().getCommerceItemsByCatalogRefId(l_skuIds[l_c]);
                    long l_quantite = ((CommerceItem) l_commerces.get(0)).getQuantity();

                    /* 890 MODIF */
                    if ((getProductTools().isInDestockage(l_skuIds[l_c]) || getManager().isInBonneAffaire(l_skuIds[l_c]))
                            && l_stock != CastoConstantes.NEG && l_quantite + l_quantity >= l_stock)
                    {
                        l_quantity = l_stock - l_quantite;

                        if (l_quantity <= 0)
                            l_pasAjout = true;

                    }
                }
                catch (InventoryException l_exception)
                {
                    l_stock = CastoConstantes.NEG;
                }
                catch (CommerceItemNotFoundException l_exception2)
                {
                    // l_stock = -1;

                    // Si on a pas trouve l'item dans la commande
                    // il faut verifier quand meme la limite
                    if (l_quantity > l_stock && l_stock > 0 && getProductTools().isInDestockage(l_skuIds[l_c]))
                    {
                        l_quantity = l_stock;
                    }
                    // logError(l_exception2);
                }

                if (!l_pasAjout
                        && getOrderTools().ajoutAuServiceValide(getProfile(), getOrder(), getShippingGroup(a_order),
                                l_skuIds[l_c]))
                {
                    CommerceItemManager l_cim = getOrderManager().getCommerceItemManager();
                    CommerceItem l_ci = null;

                    if (null == getProductId())
                    {
                        /*
                         * J.F, si le productId n'est pas renseigné, on le
                         * recalcule.
                         */
            /*Code Review */
                        l_ci = l_cim.createCommerceItem(l_skuIds[l_c], getCatalogManager().getIdProduit(l_skuIds[l_c]),
                                l_quantity, getDefaultLocale().toString());
            /* Code Review */
                    }
                    else
                    {
            /* Code Review */
                        l_ci = l_cim.createCommerceItem(l_skuIds[l_c], getProductId(), l_quantity, getDefaultLocale().toString());
            /* Code Review */
                    }

                    l_ci = l_cim.addItemToOrder(getOrder(), l_ci);
                    setCommerceItemProperties(l_ci, l_c);

                    l_cim.addItemQuantityToShippingGroup(getOrder(), l_ci.getId(), l_sg.getId(), l_quantity);

                    // gifthandling
                    if (a_IsGift)
                    {
                        getGiftlistManager().addGiftToOrder(getProfile(), getOrder(), l_ci.getId(), l_sg, l_quantity,
                                getGiftlistId(), getGiftlistItemId());
                    }

                    runProcessRepriceOrder(getAddItemToOrderPricingOp(), a_order, getUserPricingModels(),
                            getUserLocale(a_request, a_response), getProfile(), null);

                    runProcessAddItemToOrder(a_order, l_ci, getUserPricingModels(),
                            getUserLocale(a_request, a_response), getProfile(), null);

                    if (isTransactionMarkedAsRollBack())
                    {
                        return;
                    }

                    runProcessSendScenarioEvent(a_order, l_ci, l_quantity, getItemAddedToOrderEventType());
                }
                else
                {
                    addFormException(new DropletFormException(
                            "Vous avez d&eacute;j&agrave; cet article pour un autre service.", "errorAjoutAuPanier"));

                }
            } // for
            /* } */// synchronized
        }
        catch (NumberFormatException l_nfe)
        {
            // invalid number given for quantity of item to add
            String l_msg = formatUserMessage(MSG_INVALID_QUANTITY, a_request, a_response);
            addFormException(new DropletException(l_msg, l_nfe, MSG_INVALID_QUANTITY));
        }
        catch (Exception l_exc)
        {
            processException(l_exc, MSG_ERROR_ADDING_TO_ORDER, a_request, a_response);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.addItemToOrder().");
        }
    }

    /**
     * This method is used to add items to the order. It will first make a call
     * to preAddItemToOrder, then it will call addItemToOrder and finally it
     * will call postAddItemToOrder.
     * 
     * @param a_request
     *            the servlet's request
     * @param a_response
     *            the servlet's response
     * 
     * @return boolean False si une redirection est nécessaire, false sinon.
     * 
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleAddItemToOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug("CastoCartModifierFormHandler addItemToOrderSuccessURL : " + getAddItemToOrderSuccessURL()
                    + " addItemToOrderSuccessURL : " + getAddItemToOrderErrorURL());
        }

        Transaction l_transaction = null;
        boolean l_ret = true;
        Order l_order = getOrder();
        
        synchronized (l_order)
        {
            if (isLoggingDebug())
            {
                logDebug("shippingGroupName => " + getShippingGroupName());
            }

            if (compterNbArticlesPanier() < m_nbLignesArticle)
            {
                try
                {
                    l_transaction = ensureTransaction();
                    // If any form errors found, redirect to error URL:
                    if (!checkFormRedirect(null, getAddItemToOrderErrorURL(), a_request, a_response))
                    {
                        l_ret = false;
                    }

                    preAddItemToOrder(a_request, a_response);
                    addItemToOrder(a_request, a_response, false, l_order);
                    postAddItemToOrder(a_request, a_response);

                    try
                    {
            /* Code Review */
                        getOrderTools().affecterMagasinOrigineCommande((CastoOrder)l_order,(RepositoryItem)getProfile());
            /* Code Review */
                        getOrderManager().updateOrder(l_order);
                    }
                    catch (CommerceException l_exception)
                    {
                        processException(l_exception, MSG_ERROR_UPDATE_ORDER, a_request, a_response);
                    }

                    // If NO form errors are found, redirect to the success URL.
                    // If form errors are found, redirect to the error URL.
                    if (isLoggingDebug())
                    {
                        logDebug("CastoCartModifierFormHandler addItemToOrderSuccessURL : "
                                + getAddItemToOrderSuccessURL() + " addItemToOrderErrorURL : "
                                + getAddItemToOrderErrorURL());
                    }

                    l_ret = checkFormRedirect(getAddItemToOrderSuccessURL(), getAddItemToOrderErrorURL(), a_request,
                            a_response);
                }
                finally
                {
                    if (l_transaction != null)
                    {
                        commitTransaction(l_transaction);
                    }
                }
            }
            else
            {
                l_ret = checkFormRedirect(getAddItemToOrderSuccessURL(), getAddItemToOrderErrorURL(), a_request,
                        a_response);
            }
        }//Fin Synchronisation

        return l_ret;
    }

    /**
     * Méthode qui compte le nombre de lignes du panier.
     * 
     * @return int Le nombre de lignes du panier.
     */
    private int compterNbArticlesPanier()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.compterNbArticlesPanier().");
        }

        int l_nbLignes = 0;
        Order l_order = getOrder();

        /*synchronized (l_order)
        {*/
            List l_shippingGroups = l_order.getShippingGroups();
            ShippingGroup l_shippingGroup;

            if (null != l_shippingGroups)
            {
                for (Iterator l_iterator = l_shippingGroups.iterator(); l_iterator.hasNext();)
                {
                    l_shippingGroup = (ShippingGroup) l_iterator.next();

                    l_nbLignes += l_shippingGroup.getCommerceItemRelationshipCount();
                }
            }
        /*}*/

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.compterNbArticlesPanier().");
        }
        return l_nbLignes;
    }

    /**
     * 
     * true si le sku est un cadeau, false sinon.
     * 
     * @param a_Sku
     *            le sku
     * @return boolean
     */
    public boolean isGift(String a_Sku)
    {
        boolean l_bEstCadeau = false;
        try
        {
            RepositoryItem l_leSku = getCatalogRepository().getItem(a_Sku, "casto_sku");
            if (l_leSku.getPropertyValue("cadeau") != null
                    && l_leSku.getPropertyValue("cadeau").toString().equals("true"))
            {
                l_bEstCadeau = true;
            }
            return l_bEstCadeau;
        }
        catch (RepositoryException l_e)
        {
            return false;
        }

    }

    /**
     * M�thode qui sauvegarde le contenu du panier dans les s�lections de
     * l'internaute.
     * 
     * @param a_request
     *            La requ�te HTTP.
     * @param a_response
     *            La r�ponse HTTP.
     * 
     * @return boolean True si pas de redirection n�cessaire, false sinon.
     * 
     * @throws IOException
     *             Si une erreur survient.
     * @throws ServletException
     *             Si une erreur survient.
     */
    public boolean handleSaveSelection(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws IOException, ServletException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleSaveSelection().");
        }

        boolean l_ret = getProfileManager().sauvegarderSelection(getOrder(), (Profile) getProfile());

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleSaveSelection().");
        }
        return l_ret;
    }

    /**
     * . Méthode qui sauvegarde le contenu du panier dans la liste de courses
     * d'un profile
     * 
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            La réponse HTTP.
     * 
     * @return boolean True si pas de redirection n�cessaire, false sinon.
     * 
     * @throws IOException
     *             Si une erreur survient.
     * @throws ServletException
     *             Si une erreur survient.
     */
    public boolean handleSaveListeDeCourses(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws IOException, ServletException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleSaveListeDeCourses().");
        }

        boolean l_ret = getProfileManager().sauvegarderListeDeCourses(getOrder(), (Profile) getProfile());

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleSaveListeDeCourses().");
        }
        return l_ret;
    }

    /*
     * ==========================================================================================================================
     * Partie paiement/enregistrement de la commande
     * ==========================================================================================================================
     */


    /**
     * .
     * 
     * M�thode qui transfert les activePromotion vers les usedPromotions
     */
    private void transfertPromotion()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.transfertPromotion().");
        }

        // On recupere le profil et ses promotions actives
        try
        {
            // MutableRepositoryItem l_profile =
            // (MutableRepositoryItem) getProfile();
            MutableRepositoryItem l_profile = (MutableRepositoryItem) getProfileManager().getUserRepository().getItem(
                    getProfile().getRepositoryId(), "user");

            Collection l_activePromotions = (Collection) l_profile.getPropertyValue(ACTIVE_PROMOTION);

            if (l_activePromotions != null)
            {
                // S'il a des promotions actives non nulles,
                // on les transfert vers les promotions utilis�es

                Collection l_usedPromotions = (Collection) l_profile.getPropertyValue(USED_PROMOTION);

                if (l_usedPromotions == null)
                {
                    l_usedPromotions = new ArrayList();
                }

                // l_usedPromotions.addAll(l_activePromotions);
                Iterator l_iterator = l_activePromotions.iterator();

                while (l_iterator.hasNext())
                {
                    RepositoryItem l_status = (RepositoryItem) l_iterator.next();

                    l_usedPromotions.add(l_status.getPropertyValue("promotion"));
                }

                // On met a null les promotions actives et
                // on enregistre le tout...

                l_activePromotions = null;

                l_profile.setPropertyValue(ACTIVE_PROMOTION, l_activePromotions);

                l_profile.setPropertyValue(USED_PROMOTION, l_usedPromotions);

                setProfile(l_profile);

                ((MutableRepository) getProfileManager().getUserRepository()).updateItem(l_profile);

                // Si on ne vide pas le UserPricingModels
                // la promotion sera repass�e � la commande suivante...
                getUserPricingModels().setProfile(l_profile);
                getUserPricingModels().initializeAllPromotions();
                getUserPricingModels().initializePricingModels();

            }
        }
        catch (RepositoryException l_exception)
        {
            if (isLoggingError())
            {
                logError(l_exception);
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.transfertPromotion().");
        }
    }

    /** FD-12/11/2007-Coupons * */
    /**
     * . fournit des fonctions de gestion des promotions
     */
    private PromotionTools m_promotionTools;

    /**
     * .
     * 
     * @return promotionTools
     */
    public PromotionTools getPromotionTools()
    {
        return m_promotionTools;
    }

    /**
     * .
     * 
     * @param a_promotionTools
     *            promotionTools
     */
    public void setPromotionTools(PromotionTools a_promotionTools)
    {
        m_promotionTools = a_promotionTools;
    }

    /** FD-12/11/2007-Coupons * */

    /**
     * .
     * 
     * M�thode qui enregistre les coupons utilis�s pour cette commande
     * 
     * @param a_orderId
     *            ID de la commande
     */
    private void enregistrementCoupon(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.enregistrementCoupon().");
        }

        // Correction coll�giale
        Collection l_coupons = (Collection) getProfile().getPropertyValue("activeCoupons");
        //

        Iterator l_iterator = l_coupons.iterator();
        while (l_iterator.hasNext())
        {
            try
            {
                String l_couponId = (String) l_iterator.next();

                MutableRepositoryItem l_coupon = getCouponRepository().createItem("casto_coupons");

                l_coupon.setPropertyValue("order_id", a_orderId);
                l_coupon.setPropertyValue("code_coupon", l_couponId);
                l_coupon.setPropertyValue("date_usage", new Date());

                getCouponRepository().addItem(l_coupon);
            }
            catch (RepositoryException l_exception)
            {
                if (isLoggingError())
                {
                    logError(l_exception);
                }
            }
        }

        ((MutableRepositoryItem) getProfile()).setPropertyValue("couponsUtilises", null);

        ((MutableRepositoryItem) getProfile()).setPropertyValue("activeCoupons", null);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.enregistrementCoupon().");
        }
    }

    /**
     * @see atg.commerce.order.ShoppingCartFormHandler#handleMoveToOrderCommit(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_request
     *            La requ�te HTTP.
     * @param a_response
     *            La r�ponse HTTP.
     * 
     * @return boolean True ou false si une redirection est n�cessaire ou non.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public boolean handleMoveToOrderCommit(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleMoveToOrderCommit().");
        }
    /* Code Review */
        boolean l_redirect = true;
        Order l_order = null;
        OrderManager l_om = getOrderManager();
        Order l_lastOrder = null;
        if(null!=l_om)
        {
            l_order = getOrder();
            if(null!=l_order)
            {
                synchronized (l_order)
                {
                    if (!getFormError())
                    {
                        // make sure they are not trying to double submit an order
                        l_lastOrder = getShoppingCart().getLast();
                        if (getOrderId() != null && l_lastOrder != null && getOrderId().equals(l_lastOrder.getId()))
                        {
                            // invalid number given for quantity of item to add
                            String l_msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, a_request, a_response);
                            addFormException(new DropletException(l_msg, MSG_ORDER_ALREADY_SUBMITTED));
                        }
                        else
                        {
                            boolean l_rollback = false;
                            TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                            try
                            {
                                l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                                try
                                {
                                    PipelineResult l_resultPipeline;
                                    String l_paymentMethod = a_request.getParameter("payment_method");
                                    Map l_infos = new HashMap();
                                    

                                    if(null!=l_paymentMethod && !"".equals(l_paymentMethod))
                                    {
                                        l_infos.put("paymentMethod", l_paymentMethod);
                                    }
                                    if(null!=getProfile())
                                    {
                                        l_infos.put("profile",getProfile().getRepositoryId());
                                    }
                                                
                                    l_resultPipeline = getOrderManager().processOrder(l_order,(HashMap)l_infos);
                                    
                                    if (!processPipelineErrors(l_resultPipeline))
                                    {
                                        setTmpOrder(l_order);
                                        if (getShoppingCart() != null)
                                        {
                                            getShoppingCart().setLast(getTmpOrder());
                                            getShoppingCart().setCurrent(null);
                                            setOrderId(getTmpOrder().getId());
                                        }
                                    }
                                    else
                                    {
                                        logError("Erreurs dans le pipeline de cdes CB et autres : ");
                                        Object[] l_keys = l_resultPipeline.getErrorKeys();
                                        for (int l_i = 0; l_i < l_keys.length; l_i++)
                                        {
                                            logError(l_resultPipeline.getError(l_keys[l_i]).toString());
                                        }        
                                    }
                                    setMoveToOrderCommitSuccessURL(getMoveToOrderCommitSuccessURL() + "?order_id=" + l_order.getId());
                                    l_redirect = checkFormRedirect(getMoveToOrderCommitSuccessURL(),getMoveToOrderCommitErrorURL(), a_request, a_response);
                                }
                                catch (CommerceException l_exceptionCCE)
                                {
                                    logError(l_exceptionCCE);
                                    processException(l_exceptionCCE, MSG_ERROR_MOVE_TO_ORDER_COMMIT, a_request, a_response);
                                }
                                finally
                                {
                                    l_transactionDemarcation.end(l_rollback);
                                }
                            }
                            catch (TransactionDemarcationException l_tde)
                            {
                                logError(l_tde);
                            }
                            catch (ServletException l_exceptionServletException)
                            {
                                logError(l_exceptionServletException);
                            }
                            catch (IOException  l_exceptionIOException)
                            {
                                logError(l_exceptionIOException);
                            }
                        }
                    }
                    else
                    {
                        logError("Error dans le formulaire de commit de la cde - getFormError : " + getFormError());
                    }        
                }//fin synchronized
            }
            else
            {
                logError("getOrder()==null");
            }
        }
        else
        {
            logError("getOrderManager()==null");
        }
        
        // On enregistre les coupons utilises...
        if(!getFormError() && null!=l_order)
        {
            enregistrementCoupon(l_order.getId());
            // on passe les promotions de active � used et on vide le UserPriceEngine
            transfertPromotion();
            // maj du profil avec les infos suivantes : dateMAJprofil, typeMAJprofil
            getOrderTools().majProfile(getProfileManager(),getProfile());
        }
        else
        {
            for (Iterator l_exceptions = getFormExceptions().iterator(); l_exceptions.hasNext();)
            {
                logError(l_exceptions.next().toString());
            }        
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleMoveToOrderCommit().");
        }
        return l_redirect;
    /* Code Review */
    }

    /**
     * Méthode qui enregistre un paiement de type chèque cadeau.
     * 
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            La réponse HTTP.
     * 
     * @return boolean False si une redirection doit avoir lieu, true sinon.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public boolean handleEnregistrerChequeCadeau(DynamoHttpServletRequest a_request,
            DynamoHttpServletResponse a_response) throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleEnregistrerChequeCadeau().");
        }

        boolean l_result = true;

        /*
         * Récupération du montant saisi pour les chèques cadeaux
         */
        double l_montant = new Double((String) a_request.getParameter("montant_chq_cadeau")).doubleValue();

        /*
         * Affectation du montant des chèques cadeaux à la commande en cours.
         */
        CastoOrder l_order = (CastoOrder) getOrder();

        l_order.setPaiementChequecadeau(true);
        l_order.setMontantChequeCadeau(l_montant);

        if (l_montant >= l_order.getPriceInfo().getTotal())
        {
            /*
             * Les chèques cadeaux payent la totalité de la commande. On
             * enregistre la commande.
             */
            l_result = handleMoveToOrderCommit(a_request, a_response);
        }
        else
        {
            /*
             * Les chèques cadeaux ne payent qu'une partie de la commande. On
             * redirige vers l'interface de sélection des moyens de paiement.
             */
            a_response.sendLocalRedirect(getEnregistrerChequeCadeauSuccessURL(), a_request);

            l_result = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handleEnregistrerChequeCadeau().");
        }

        return l_result;
    }
    
    /**
     * @see atg.commerce.order.ShoppingCartFormHandler#handlePreSofinco(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_request
     *            La requete HTTP.
     * @param a_response
     *            La reponse HTTP.
     * 
     * @return boolean True ou false si une redirection est necessaire ou non.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public boolean handlePreSofinco(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handlePreSofinco().");
        }
    /*Code Review*/
        HttpSession l_session = a_request.getSession();
        if(null!=a_request.getSession())
        {
            String l_sessionId = l_session.getId();
            if(null!=l_sessionId)
            {
                getOrderTools().updateOrderPreSofinco(getOrder(), getProfile(), l_sessionId);
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("a_request.getSession().getId()==null");
                }
            }
        }
        else
        {
            if (isLoggingDebug())
            {
                logDebug("a_request.getSession()==null");
            }
        }
    /*Code Review*/    
        checkFormRedirect(getPreSofincoSuccessUrl(),getPreSofincoErrorUrl(), a_request, a_response);
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.handlePreSofinco().");
        }
        return true;
    }
}