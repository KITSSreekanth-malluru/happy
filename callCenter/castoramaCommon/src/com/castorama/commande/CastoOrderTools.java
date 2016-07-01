package com.castorama.commande;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupImpl;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceMap;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.atosorigin.services.cad.apipayment.SIPSResponseParm;
import com.atosorigin.services.cad.apipayment.web.SIPSApiWeb;
import com.atosorigin.services.cad.common.SIPSDataObject;
import com.atosorigin.services.cad.common.SIPSException;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.constantes.CastoConstantesPaybox;
import com.castorama.inventory.InventoryAdapter;
import com.castorama.order.CastoPaymentGroupImpl;
import com.castorama.pricing.ParametresFraisLivraisonMagasin;
import com.castorama.pricing.ParametresFraisPreparationMagasin;
import com.castorama.produit.CastoAffichageDisponibiliteManager;
import com.castorama.profil.CastoProfileManager;

/**
 * @author Florte J�r�my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe d'utilitaires pour la gestion des commandes
 */
public class CastoOrderTools extends GenericService
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
    /**
     * D�signe la propri�t� "state" d'une commande.
     */
    public static final String PROPERTY_STATE = "state";

    /**
     * D�signe la propri�t� "boState" d'une commande.
     */
    public static final String PROPERTY_BO_STATE = "BOState";
    
    /**
     * D�signe la propri�t� "boState" d'une commande.
     */
    /* Code Review */public static final String PROPERTY_BO_STATE_DETAIL = "BOStateDetail";

    /**
     * D�signe la propri�t� "amount" d'un PaymentGroup.
     */
    public static final String PROPERTY_AMOUNT = "amount";

    /**
     * D�signe la propri�t� "amountAuthorized" d'un PaymentGroup.
     */
    public static final String PROPERTY_AMOUNT_AUTHORIZED = "amountAuthorized";

    /**
     * D�signe une commande avec un statut en attente de ch�que.
     */
    public static final String PENDING_CHEQUE = "PENDING_CHEQUE";

    /**
     * D�signe une commande avec un statut en attente de paiement carte atout.
     */
    public static final String PENDING_ATOUT = "PENDING_ATOUT";

    /**
     * D�signe une commande avec un statut en attente de paiement par t�l�phone.
     */
    public static final String PENDING_CALL_CENTER = "PENDING_CALL_CENTER";

    /**
     * D�signe une commande avec un statut en attente de virement.
     */
    public static final String PENDING_VIREMENT = "PENDING_VIREMENT";

    /**
     * D�signe une commande avec un statut en attente de chèque cadeaux.
     */
    public static final String PENDING_CHEQUE_CADEAU = "PENDING_CHEQUE_CADEAU";

    /**
     * D�signe une commande avec un statut d�j� relanc�.
     */
    public static final String PENDING_RELANCE = "PENDING_RELANCE";

    /**
     * D�signe un statut de commande valid�e.
     */
    public static final String VALIDE = "VALIDE";

    /**
     * . Num�ro de carte bleue
     */
    public static final String NUMERO_DEFAUT = " ";
    /* Code Review */  
    /**
     * Code retour SIPS : service indisponible.
     */
    public static final int CODE_RETOUR_SERVICE_INDISPONIBLE = 90;

    /**
     * Code retour SIPS : transaction d�j� trait�e.
     */
    public static final int CODE_RETOUR_TRANSACTION_DEJA_TRAITEE = 94;
    

    /**
     * Code retour SIPS : paiement accept�.
     */
    public static final int CODE_RETOUR_PAIEMENT_ACCEPTE = 0;

    /**
     * Code retour SIPS : paiement refus�.
     */
    public static final int CODE_RETOUR_PAIEMENT_REFUSE = 2;

    /**
     * Code retour SIPS : identifiant commer�ant invalide.
     */
    public static final int CODE_RETOUR_MERCHANT_ID_INVALIDE = 3;

    /**
     * Code retour SIPS : refus de paiement.
     */
    public static final int CODE_RETOUR_REFUS_PAIEMENT = 5;

    /**
     * Code retour SIPS : transaction invalide.
     */
    public static final int CODE_RETOUR_TRANSACTION_INVALIDE = 12;

    /**
     * Code retour SIPS : montant invalide.
     */
    public static final int CODE_RETOUR_MONTANT_INVALIDE = 13;

    /**
     * Code retour SIPS : identifiant client invalide.
     */
    public static final int CODE_RETOUR_ID_CLIENT_INVALIDE = 14;

    /**
     * Code retour SIPS : erreur de format.
     */
    public static final int CODE_RETOUR_ERREUR_FORMAT = 30;

    /**
     * Code retour SIPS : r�gles de s�curit� non respect�e.
     */
    public static final int CODE_RETOUR_ERREUR_SECURITE = 63;
    /* Code Review */
    /**
     * Chemin du composant CastoCartModifierFormHandler.
     */
    protected static final ComponentName CASTO_CART_MODIFIER_FORM_HANDLER_NAME = ComponentName
            .getComponentName("/castorama/commande/CastoCartModifierFormHandler");

    /**
     * Chemin du composant BeanOrderTransaction.
     */
    protected static final ComponentName BEAN_ORDER_TRANSACTION_NAME = ComponentName
            .getComponentName("/castorama/order/BeanOrderTransaction");

    /**
     * Chemin du composant BeanLogSips.
     */
    protected static final ComponentName BEAN_LOGS_SIPS = ComponentName
            .getComponentName("/castorama/order/BeanLogSips");

    /* Code Review */
    /**
     * Repr�sente la fin d'une ann�e dans le substring d'une date.
     */
    private static final int INDEX_FIN_ANNEE = 4;

    /**
     * Repr�sente la fin d'un mois dans le substring d'une date.
     */
    private static final int INDEX_FIN_MOIS = 6;

    /**
     * Repr�sente la fin d'un jour dans le substring d'une date.
     */
    private static final int INDEX_FIN_JOUR = 8;
    
    /**
     * .
     * donn�e SIPS
     */
    private static final String AUTORISATION_ID = "authorisation_id";
    
    /**
     * .
     * donn�e SIPS
     */
    private static final String PAYMENT_DATE = "payment_date";

    /**
     * Code retour SIPS : annulation client.
     */
    private static final int CODE_RETOUR_ANNULATION_CLIENT = 17;

    /**
     * Code retour SIPS : nombre maximum d'essais atteint.
     */
    private static final int CODE_RETOUR_NB_ESSAIS_MAX = 75;
    

    /**
     * Libell� retour SIPS : paiement accept�.
     */
    private static final String LIBELLE_PAIEMENT_ACCEPTE = "accepte";

    /**
     * Libell� retour SIPS : paiement refus�.
     */
    private static final String LIBELLE_PAIEMENT_REFUSE = "refus de paiement";

    /**
     * Libell� retour SIPS : identifiant commer�ant invalide.
     */
    private static final String LIBELLE_MERCHANT_ID_INVALIDE = "MERCHANT_ID invalide";

    /**
     * Libell� retour SIPS : refus de paiement.
     */
    private static final String LIBELLE_REFUS_PAIEMENT = "refus du paiement";

    /**
     * Libell� retour SIPS : transaction invalide.
     */
    private static final String LIBELLE_TRANSACTION_INVALIDE = "transaction invalide";

    /**
     * Libell� retour SIPS : montant invalide.
     */
    private static final String LIBELLE_MONTANT_INVALIDE = "montant invalide";

    /**
     * Libell� retour SIPS : identifiant client invalide.
     */
    private static final String LIBELLE_ID_CLIENT_INVALIDE = "CUSTOMER_ID invalide";

    /**
     * Libell� retour SIPS : annulation client.
     */
    private static final String LIBELLE_ANNULATION_CLIENT = "annulation de l'internaute";

    /**
     * Libell� retour SIPS : erreur de format.
     */
    private static final String LIBELLE_ERREUR_FORMAT = "erreur de format";

    /**
     * Libell� retour SIPS : r�gles de s�curit� non respect�e.
     */
    private static final String LIBELLE_ERREUR_SECURITE = "regles de securite non respectees";

    /**
     * Libell� retour SIPS : nombre maximum d'essais atteint.
     */
    private static final String LIBELLE_NB_ESSAIS_MAX = "nombre maximum essais";

    /**
     * Libell� retour SIPS : service indisponible.
     */
    private static final String LIBELLE_SERVICE_INDISPONIBLE = "service indisponible";

    /**
     * Libell� retour SIPS : transaction d�j� trait�e.
     */
    private static final String LIBELLE_TRANSACTION_DEJA_TRAITEE = "transaction deja traitee";
    /* Code Review */
    
    /**
     * Descripteur pour la gestion de la PFT.
     */
    private static final String DESCRIPTEUR_PFT = "grillePoidsPreparationDeCommande";

    /**
     * Descripteur pour la gestion de la PFL.
     */
    private static final String DESCRIPTEUR_PFL = "poids";

    /**
     * D�signe la propri�t� "montant" de l'item descripteur
     * "grillePoidsPreparationDeCommande".
     */
    private static final String PROPERTY_PFT_MONTANT = "montant";

    /**
     * D�signe la propri�t� "prixAuKg" de l'item descripteur "poids".
     */
    private static final String PROPERTY_PFL_PRIX_AU_KG = "prixAuKg";

    /**
     * Code retour de la banque pour une transaction approuv�e.
     */
    private static final String TRANSACTION_APPROUVEE = "00";

    /* Code Review */
    /**
     * .
     * Internationalisation pour le message n� carte atout invalide.
     */
    private static final String ACTIVE_PROMOTION = "activePromotions";

    /**
     * .
     * Internationalisation pour le message n� carte atout invalide.
     */
    private static final String USED_PROMOTION = "usedPromotions";
    /* Code Review */
    
    /**
     * Param�tre code retour bancaire.
     */
    private static final String BANK_RESPONSE_CODE = "bank_response_code";

    /**
     * D�signe la propri�t� "payment_means" du retour sips.
     */
    private static final String PROPERTY_PAYMENT_MEANS = "payment_means";

    /**
     * D�signe la propri�t� "card_number" du retour sips.
     */
    private static final String PROPERTY_CARD_NUMBER = "card_number";

    private static final String CREDIT_CARD = "creditCard";
    
    private static final String CATALOG_REF_ID = "catalogRefId";
    
    /* Code Review */private static final String TIRET = " - ";/* Code Review */
    
    private static final String VERSION = "version";
    
    private static final String PAYMENT_METHOD = "paymentMethod";
    
    private static final String CUSTOMER_ID = "customer_id";
    
    private static final String PAYEMENT_GROUPS = "paymentGroups";
    
    /* Code Review */private static final String PROFILE_ID =  "profileId";/* Code Review */
    
    /* Code Review */private static final String TRANSACTION_ID =  "transaction_id";/* Code Review */
    
    /* Code Review */private static final String ORDER_ID =  "order_id";/* Code Review */
    
    /* Code Review */private static final String PAIMENT_CHEQUE_CADEAU =  "paiementChequeCadeau";/* Code Review */
    
    /* Code Review */private static final String TYPE_MAJ_PROFIL =  "typeMAJprofil";/* Code Review */
    
    /* Code Review */private static final String EXP = "exp";/* Code Review */
    
    /**
     * .
     * pour BDDCC, le temps qui permet de séparer la creation d'un profil via une commande par rapport aux autres creations de profil 
     */
    private static final int CONVERSION_DOUZE_HEURES_MILLISECONDE = 12*60*60*1000;
    
    /** Type de mise à jour du profil. **/
    private static final Integer MODIFICATION = new Integer(1);
    
    
    private static final String COMMERCE_ITEM_BACKUP = "commerce_item_backup";
    
    private static final String ORDER_BACKUP = "order_backup";
    
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    /**
     * . Manager permettant de d�cr�menter le stock (uniquement sur carte
     * l'Atout)
     */
    protected InventoryManager m_oInventoryManager;

    private Repository m_orderRepository;

    private Repository m_catalogRepository;

    /** . dispo manager * */
    private CastoAffichageDisponibiliteManager m_dispoManager;

    private Repository m_magasinRepository;

    private TransactionManager m_transactionManager;

    private ServiceMap m_parametresLivraisons;

    private ServiceMap m_fraisMAD;

    private double m_pourcentageAtout;

    private InventoryAdapter m_inventoryAdapter;

    private Repository m_profileRepository;

    private Repository m_orderBackupRepository;
    
    private OrderManager m_orderManager;
    
    /* Code Review */
    private Locale m_defaultLocale;

    private Repository m_OrderTransactionRepository;
    
    private Repository m_OrderLogsSIPSRepository;
    
    /**
     * .
     * Catalog Repository.
     */
    private MutableRepository m_couponRepository;
    /* Code Review */
    /*
     * ===========================================================================
     * ATTRIBUTS =================================================================
     * ===========================================================================
     */
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
     
    /**
     * Renvoie une référence vers le repository des order backup.
     * @return Une référence vers le repository des order backup.
     */
    public Repository getOrderBackupRepository()
    {
        return m_orderBackupRepository;
    }

    /**
     * Fixe la référence du repository des order backup.
     * 
     * @param a_orderBackupRepository
     *            La nouvelle référence du repository des order backup.
     */
    public void setOrderBackupRepository(Repository a_orderBackupRepository)
    {
        m_orderBackupRepository = a_orderBackupRepository;
    }
    
    /* Code Review */
    /**
     * .
     * @return the couponRepository
     */
    public MutableRepository getCouponRepository()
    {
        return m_couponRepository;
    }

    /**
     * .
     * @param a_couponRepository
     *            the couponRepository to set
     */
    public void setCouponRepository(MutableRepository a_couponRepository)
    {
        m_couponRepository = a_couponRepository;
    }
    
    /**
     * Renvoie une reference vers le repository des logs SIPS.
     * 
     * @return Une reference vers le repository des logs SIPS.
     */
    public Repository getOrderLogsSIPSRepository()
    {
        return m_OrderLogsSIPSRepository;
    }

    /**
     * Fixe la reference du repository des logs SIPS.
     * 
     * @param a_OrderLogsSIPSRepository
     *            La nouvelle reference du repository des logs SIPS.
     */
    public void setOrderLogsSIPSRepository(Repository a_OrderLogsSIPSRepository)
    {
        m_OrderLogsSIPSRepository = a_OrderLogsSIPSRepository;
    }
    
    /**
     * Renvoie une reference vers le repository des transactions.
     * 
     * @return Une reference vers le repository des transactions.
     */
    public Repository getOrderTransactionRepository()
    {
        return m_OrderTransactionRepository;
    }

    /**
     * Fixe la reference du repository des transactions.
     * 
     * @param a_OrderTransactionRepository
     *            La nouvelle reference du repository des transactions.
     */
    public void setOrderTransactionRepository(Repository a_OrderTransactionRepository)
    {
        m_OrderTransactionRepository = a_OrderTransactionRepository;
    }
    
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
    
    /**
     * Renvoie une référence vers le repository des magasins.
     * 
     * @return Une référence vers le repository des magasins.
     */
    public Repository getMagasinRepository()
    {
        return m_magasinRepository;
    }

    /**
     * Fixe la référence du repository des magasins.
     * 
     * @param a_magasinRepository
     *            La nouvelle référence du repository des magasins.
     */
    public void setMagasinRepository(Repository a_magasinRepository)
    {
        m_magasinRepository = a_magasinRepository;
    }

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
     * Retourne une r�f�rence vers le CatalogRepository.
     * 
     * @return Repository Une r�f�rence vers le CatalogRepository.
     */
    public Repository getOrderRepository()
    {
        return m_orderRepository;
    }

    /**
     * Fixe la r�f�rence vers le CatalogRepository.
     * 
     * @param a_catalogRepository
     *            La nouvelle r�f�rence vers le CatalogRepository.
     */
    public void setOrderRepository(Repository a_catalogRepository)
    {
        m_orderRepository = a_catalogRepository;
    }

    /**
     * .
     * 
     * InventoryManager
     * 
     * @return InventoryManager
     */
    public InventoryManager getInventoryManager()
    {
        return m_oInventoryManager;
    }

    /**
     * .
     * 
     * InventoryManager
     * 
     * @param a_oInventoryManager
     *            Inventory manager
     */
    public void setInventoryManager(InventoryManager a_oInventoryManager)
    {
        this.m_oInventoryManager = a_oInventoryManager;
    }

    /**
     * Renvoie une r�f�rence vers le CatalogRepository.
     * 
     * @return Repository Une r�f�rence vers le CatalogRepository.
     */
    public Repository getCatalogRepository()
    {
        return m_catalogRepository;
    }

    /**
     * Fixe la r�f�rence vers le CatalogRepository.
     * 
     * @param a_catalogRepository
     *            La nouvelle r�f�rence vers le CatalogRepository.
     */
    public void setCatalogRepository(Repository a_catalogRepository)
    {
        m_catalogRepository = a_catalogRepository;
    }

    /**
     * Méthode qui renvoie une référence vers l'Order Manager.
     * 
     * @return une référence vers l'Order Manager.
     */
    public OrderManager getOrderManager()
    {
        return m_orderManager;
    }

    /**
     * Méthode qui fixe la référence vers l'Order Manager.
     * 
     * @param a_orderManager
     *            La nouvelle référence vers l'Order Manager.
     */
    public void setOrderManager(OrderManager a_orderManager)
    {
        m_orderManager = a_orderManager;
    }

    /**
     * Méthode qui renvoie une référence vers le gestionnaire de transaction.
     * 
     * @return Une référence vers le gestionnaire de transaction.
     */
    public TransactionManager getTransactionManager()
    {
        return m_transactionManager;
    }

    /**
     * Méthode qui fixe la référence vers le gestionnaire de transaction.
     * 
     * @param a_transactionManager
     *            La nouvelle référence vers le gestionnaire de transaction.
     */
    public void setTransactionManager(TransactionManager a_transactionManager)
    {
        m_transactionManager = a_transactionManager;
    }

    /**
     * Accesseurs pour la liste des paramètres pour le calcul des frais de
     * livraison PLD.
     * 
     * @return La liste des paramètres pour le calcul des frais de livraison
     *         PLD.
     */
    public ServiceMap getParametresLivraisons()
    {
        return m_parametresLivraisons;
    }

    /**
     * Méthode qui fixe la liste des paramètres pour le calcul des frais de
     * livraison PLD.
     * 
     * @param a_parametresLivraisons
     *            La novelle liste des paramètres pour le calcul des frais de
     *            livraison PLD.
     */
    public void setParametresLivraisons(ServiceMap a_parametresLivraisons)
    {
        m_parametresLivraisons = a_parametresLivraisons;
    }

    /**
     * Méthode qui renvoie la liste des frais de mise à disposition par magasin.
     * 
     * @return La liste des frais de mise à disposition par magasin.
     */
    public ServiceMap getFraisMAD()
    {
        return m_fraisMAD;
    }

    /**
     * Méthode qui fixe la liste des frais de mise à disposition par magasin.
     * 
     * @param a_fraisMAD
     *            La nouvelle liste des frais de mise à disposition par magasin.
     */
    public void setFraisMAD(ServiceMap a_fraisMAD)
    {
        m_fraisMAD = a_fraisMAD;
    }

    /**
     * Méthode qui renvoie le pourcentage de remise appliqué sur les commandes
     * payées avec la carte l'atout.
     * 
     * @return Le pourcentage de remise appliqué sur les commandes payées avec
     *         la carte l'atout.
     */
    public double getPourcentageAtout()
    {
        return m_pourcentageAtout;
    }

    /**
     * Méthode qui fixe le pourcentage de remise appliqué sur les commandes
     * payées avec la carte l'atout.
     * 
     * @param a_pourcentageAtout
     *            Le nouveau pourcentage de remise appliqué sur les commandes
     *            payées avec la carte l'atout.
     */
    public void setPourcentageAtout(double a_pourcentageAtout)
    {
        m_pourcentageAtout = a_pourcentageAtout;
    }

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
     * Méthode qui renvoie une référence vers le repository des utilisateurs.
     * 
     * @return Une référence vers le repository des utilisateurs.
     */
    public Repository getProfileRepository()
    {
        return m_profileRepository;
    }

    /**
     * Méthode qui fixe la référence vers le repository des utilisateurs.
     * 
     * @param a_profileRepository
     *            La nouvelle référence vers le repository des utilisateurs.
     */
    public void setProfileRepository(Repository a_profileRepository)
    {
        m_profileRepository = a_profileRepository;
    }

    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * . Objectif : Un panier ne peut pas contenir 2 fois le même article avec
     * le service qui diffère. la Défense - Logica - PLD_REG_FP_12 &&
     * PLD_REG_FP_13
     * 
     * @param a_profile
     *            Profile de l'utilisateur
     * 
     * @param a_order
     *            La commande en cours de "construction"
     * 
     * @param a_sg
     *            Le shipping group auquel on souhaite ajouter notre sku
     * 
     * @param a_idSku
     *            Sku que l'on souhaite ajouter shipping group a_sg de notre
     *            panier contenu dans a_order
     * 
     * @return boolean - true si on peut ajouter ce produit dans ce shipping
     *         group là. false sinon.
     * 
     * @throws RepositoryException
     *             RepositoryException
     * 
     */
    public boolean ajoutAuServiceValide(RepositoryItem a_profile, Order a_order, ShippingGroup a_sg, String a_idSku)
            throws RepositoryException
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoCartModifierFormHandler.ajoutAuServiceValide().");
        }

        if (isLoggingDebug())
        {
            logDebug("---> Sku à trouver dans les autres groups : " + a_idSku);
        }

        int l_idMagasin;
        if (null != a_profile)
        {
            Object l_obj = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
            if (null != l_obj)
            {
                RepositoryItem l_magasin = (RepositoryItem) l_obj;
                l_idMagasin = new Integer(l_magasin.getRepositoryId()).intValue();
            }
            else
            {
                logError("CastoIsSessionPLD.service() : le magasin de l'objet profile n'est pas renseigné.");
                l_idMagasin = CastoConstantesDefense.CASTORAMA_DIRECT_INT;
            }
            if (CastoConstantesDefense.CASTORAMA_DIRECT_INT != l_idMagasin)
            {
                if (null != a_order)
                {
                        List l_shippingGroups = a_order.getShippingGroups();
                        if (null != l_shippingGroups)
                        {
                            // Parcours des shipping groups
                            for (Iterator l_it = l_shippingGroups.iterator(); l_it.hasNext();)
                            {

                                HardgoodShippingGroup l_item = (HardgoodShippingGroup) l_it.next();
                                // On ne parcourt que les
                                // CommerceItemRelationships
                                // des shippings groups différents
                                // à la recherche d'un même sku déjà présent.
                                if (!l_item.getId().equals(a_sg.getId()))
                                {
                                    if (isLoggingDebug())
                                    {
                                        String l_shippingMethod = (String) l_item.getPropertyValue("shippingMethod");
                                        logDebug("--->Shipping method : " + l_shippingMethod);
                                    }
                                    List l_CommerceItemRelationships = l_item.getCommerceItemRelationships();
                                    if (null != l_CommerceItemRelationships)
                                    {

                                        CommerceItemRelationship l_ComItemRelationship;
                                        CommerceItem l_ComItem;

                                        // Parcours des
                                        // CommerceItemRelationships
                                        for (Iterator l_CommeItemRel = l_CommerceItemRelationships.iterator(); l_CommeItemRel
                                                .hasNext();)
                                        {
                                            l_ComItemRelationship = (CommerceItemRelationship) l_CommeItemRel.next();
                                            l_ComItem = l_ComItemRelationship.getCommerceItem();
                                            RepositoryItem l_ComItemRepo = (RepositoryItem) getOrderRepository()
                                                    .getItem(l_ComItem.getId(), "commerceItem");

                                            if (null != l_ComItemRepo)
                                            {
                                                String l_Code = (String) l_ComItemRepo.getPropertyValue(CATALOG_REF_ID);
                                                if (null != l_Code && !"".equals(l_Code))
                                                {
                                                    if (l_Code.equals(a_idSku))
                                                    {
                                                        return false;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoCartModifierFormHandler.ajoutAuServiceValide().");
        }
        return true;
    }

    /**
     * M�thode qui renvoit le montant de la PFT en fonction du poids pass� en
     * param�tre.
     * 
     * @param a_poids
     *            Le poids total de la commande.
     * 
     * @return double Le montant de la PFT pour la commande en cours.
     * 
     * @throws RepositoryException
     *             Si une erreur survient.
     */
    public double obtenirMontantPFT(double a_poids) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.obtenirMontantPFT().");
        }

        Double l_poids = new Double(a_poids);
        double l_montant = 0;
        Repository l_catalogRepository = getOrderRepository();

        try
        {
            RqlStatement l_findMontant = RqlStatement.parseRqlStatement("POIDSINF <= ?0 and ?1 < POIDSSUP");
            RepositoryView l_view = l_catalogRepository.getView(DESCRIPTEUR_PFT);
            RepositoryItem[] l_items = l_findMontant.executeQuery(l_view, new Object[]
            { l_poids, l_poids, });

            if (null != l_items && 0 != l_items.length)
            {
                l_montant = ((Double) l_items[0].getPropertyValue(PROPERTY_PFT_MONTANT)).doubleValue();
            }
        }
        catch (RepositoryException l_exception)
        {
            logError(l_exception.toString());

            throw l_exception;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.obtenirMontantPFT().");
        }

        return l_montant;
    }

    /**
     * M�thode qui renvoit le montant de la PFL en fonction du poids pass� en
     * param�tre.
     * 
     * @param a_poids
     *            Le poids total de la commande.
     * 
     * @return double Le montant de la PFL pour la commande en cours.
     * 
     * @throws RepositoryException
     *             Si une exception survient.
     */
    public double obtenirMontantPFL(double a_poids) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.obtenirMontantPFL().");
        }

        Double l_poids = new Double(a_poids);
        double l_montant = 0;
        double l_prixKg = 0;
        Repository l_catalogRepository = getOrderRepository();

        try
        {
            RqlStatement l_findMontant = RqlStatement
                    .parseRqlStatement("POIDSINF <= ?0 and ?1 < POIDSSUP and GRILLEPFEID = 1");
            RepositoryView l_view = l_catalogRepository.getView(DESCRIPTEUR_PFL);
            RepositoryItem[] l_items = l_findMontant.executeQuery(l_view, new Object[]
            { l_poids, l_poids, });

            if (null != l_items && 0 != l_items.length)
            {
                l_prixKg = ((Double) l_items[0].getPropertyValue(PROPERTY_PFL_PRIX_AU_KG)).doubleValue();

                l_montant = l_prixKg * a_poids;
            }
        }
        catch (RepositoryException l_exception)
        {
            logError(l_exception.toString());

            throw l_exception;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.obtenirMontantPFL().");
        }

        return l_montant;
    }

    /**
     * M�thode appel�e pour le traitement d'un retour de paiement par CB.
     * 
     * @param a_request
     *            La requ�te HTTP.
     * @param a_response
     *            La r�ponse HTTP.
     * @param a_pathFile
     *            Chemin du fichier pathfile SIPS.
     * @return String
     *            url de redirection
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public /* Code Review */ String /* Code Review */ traiterRetourPaiementCB(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response,
            String a_pathFile) throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementCB().");
        }
        /* Code Review */ String l_retour=null; /* Code Review */
        try
        {
            StringBuffer l_result = new StringBuffer();

            // Initialisation du chemin du fichier pathfile
            SIPSApiWeb l_api = new SIPSApiWeb(a_pathFile);

            // Initialisation de l'objet r�ponse
            SIPSDataObject l_response = (SIPSDataObject) new SIPSResponseParm();

            // R�cup�ration de la variable crypt�e post�e
            String l_cypheredtxt = a_request.getParameter("DATA");

            // D�cryptage de la r�ponse
            l_response = l_api.sipsPaymentResponseFunc(l_cypheredtxt);

            /*
             * Mise � jour du statut de la commande
             */
    /* Code Review */
            l_retour = validerCommande(l_response, a_request, a_response);
    /* Code Review */

            /*
             * Log des informations
             */
            if (isLoggingDebug())
            {
                String l_key = null;

                // Affichage des donn�es de la r�ponse du serveur
                for (Enumeration l_enum = l_response.getKeyEnum(); l_enum.hasMoreElements();)
                {
                    l_key = l_enum.nextElement().toString();

                    l_result.append(l_key + " = ").append(l_response.getValue(l_key)).append("\n");
                }

                // Affichage dans la log
                logDebug(l_result.toString());
            }
        }
        catch (SIPSException l_exception)
        {
            logError(l_exception.toString());
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementCB().");
        }
        return l_retour;
    }/* Code Review*/
    
    /**
     * Méthode qui traite le retour de paiement Paybox.
     * 
     * @param a_referenceCommande
     *            La commande.
     * @param a_erreur
     *            a_erreur.
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            Response
     * 
     */
    /* Code Review*/public void traiterRetourPaiementPaybox(String a_referenceCommande, String a_erreur,
            DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response) 
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementPaybox");
        }
        
        OrderManager l_om = getOrderManager();
        if(null!=l_om)
        {
            if(null!=a_referenceCommande && !"".equals(a_referenceCommande))
            {
                String l_urlErreur = null;
                try
                {
                    CastoOrder l_order=null;
                    try
                    {
                        l_order = (CastoOrder)l_om.loadOrder(a_referenceCommande);
                    }
                    catch (CommerceException l_exceptionCCE)
                    {
                        logError(l_exceptionCCE);
                        l_order=null;
                    }
                    
                    if(null!=l_order)
                    {
                        TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                        boolean l_rollback = false;
                        synchronized (l_order)
                        {
                            try
                            {
                                l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                                PipelineResult l_resultPipeline;
                                Map l_paymentInfosAtout = new HashMap();
                                Map l_infos = new HashMap();
                                
                                l_paymentInfosAtout.put(CastoConstantesPaybox.PARAMETRE_IDENTIFIANT_TRANSACTION, 
                                        a_request.getParameter(CastoConstantesPaybox.PARAMETRE_IDENTIFIANT_TRANSACTION) );
                                
                                l_paymentInfosAtout.put(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION, 
                                        a_request.getParameter(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION) );
                                
                                l_paymentInfosAtout.put(CastoConstantesPaybox.PARAMETRE_NUMERO_AUTORISATION, 
                                        a_request.getParameter(CastoConstantesPaybox.PARAMETRE_NUMERO_AUTORISATION) );
                                
                                l_paymentInfosAtout.put(EXP, a_request.getParameter(EXP) );
                                l_infos.put("paymentInfosAtout",l_paymentInfosAtout);
                                l_infos.put(PAYMENT_METHOD,"Atout");
                                
                                try
                                {
                                   
                                    l_resultPipeline = getOrderManager().processOrderWithReprice(
                                            l_order,getDefaultLocale().toString(),new HashMap(l_infos));
                                    if (!l_resultPipeline.hasErrors())
                                    {
                                       //redirection
                                        if(isLoggingDebug())
                                        {
                                            logDebug("process order atout == ok");
                                        }
                                        
                                        String l_urlSuccess = null;
                                        if(null!=l_order.getJSessionID())
                                        {
                                        
                                            l_urlSuccess = "/store/html/commande/panier-confirmation-commande.jsp;jsessionid=" 
                                                + l_order.getJSessionID() + "?order_id=" + a_referenceCommande;
                                        }
                                        else
                                        {
                                            l_urlSuccess = "/store/html/commande/panier-confirmation-commande.jsp?order_id=" + a_referenceCommande;
                                        }
                                        
                                        String l_pId = l_order.getProfileId();
                                        if(null != l_pId && !"".equals(l_pId.toString()))
                                        {
                                            RepositoryItem l_origineMagasin = ((CastoOrder)l_order).getOrigineMagasin();
                                            gestionNouvelleCde(l_pId,a_request,l_origineMagasin);
                                        }
                                        else
                                        {
                                            logError("l_order.getProfileId()==null");
                                        }
                                        a_response.sendRedirect(l_urlSuccess);
                                    }
                                    else
                                    {
                                        logError("Erreurs dans le pipeline de cdes Atout : ");
                                        Object[] l_keys = l_resultPipeline.getErrorKeys();
                                        for (int l_i = 0; l_i < l_keys.length; l_i++)
                                        {
                                            logError(l_resultPipeline.getError(l_keys[l_i]).toString());
                                        }   
                                        if(null!=a_erreur && !"".equals(a_erreur))
                                        {
                                            l_urlErreur = "/store/html/commande/paybox/erreur-paiement.jsp?erreur=" 
                                                + a_erreur + "&order_id=" + a_referenceCommande;
                                        }
                                        else
                                        {
                                            l_urlErreur = "/store/html/commande/paybox/erreur-paiement.jsp?erreur=CCMFH_URL" 
                                                + "&order_id=" + a_referenceCommande;
                                        }
                                        a_response.sendRedirect(l_urlErreur);
                                    }
                                }
                                catch (CommerceException l_exceptionCCE)
                                {
                                    logError(l_exceptionCCE);
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
                        }//fin synchronized    
                    }
                    else
                    {
                        logError("l_order==null || l_jsessionId==null");
                    }    
                }
                catch(IOException l_ioe)
                {
                    logError(l_ioe);
                }
            }
            else
            {
                logError("a_referenceCommande == NULL");
            }
        }
        else
        {
            logError("getOrderManager() =  = null");
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementPaybox");
        }
    }/* Code Review*/
    
    /**
     * Methode qui traite le retour de paiement Paybox.
     * 
     * @param a_order
     *            La commande.
     * @param a_infos
     *            Les informations de paiement
     */
    /* Code Review*/public void updateOrderAtout(CastoOrder a_order, Map a_infos) 
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.updateOrderAtout");
        }

        /*
         * Preparation des calendars pour la gestion des dates de : transation, de paiement, d expiration
         */
        Calendar l_now = Calendar.getInstance();
        l_now.add(Calendar.DAY_OF_MONTH, CastoConstantesCommande.DELAI_PAIEMENT_PAYBOX);
        String l_exp = (String)a_infos.get(EXP);
        Calendar l_dateExpiration = Calendar.getInstance();
        l_dateExpiration.set(Calendar.DATE, CastoConstantes.UN);
        int l_month = Integer.valueOf(l_exp.substring(CastoConstantes.DEUX, CastoConstantes.QUATRE))
                .intValue() + 1;
        l_dateExpiration.set(Calendar.MONTH, l_month);
        int l_year = Integer.valueOf("20" + l_exp.substring(0, CastoConstantes.DEUX)).intValue();
        l_dateExpiration.set(Calendar.YEAR, l_year);
        
        a_order.setPayeCarteAtout(Boolean.TRUE);
        a_order.setStateDetail("paiementValide");
        a_order.setBOState(VALIDE);
        a_order.setBOStateDetail("La commande a ete validee");
        /*
         * On set les objets paymentGroup de la commande : Moyen de paiement
         */
        List l_paymentsGroups = (List) a_order.getPaymentGroups();
        for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
        {
            CastoPaymentGroupImpl l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
            l_paymentGroup.setPaymentMethod(CastoCartModifierFormHandler.PAIEMENT_CARTE_ATOUT);
            l_paymentGroup.setCreditCardNumber("4111111111111111");
            l_paymentGroup.setCreditCardType("Visa");
            l_paymentGroup.setExpirationMonth(new Integer(l_month).toString());
            l_paymentGroup.setExpirationYear(new Integer(l_year).toString());
            l_paymentGroup.setNumeroAutorisationPaybox(
                    (String)a_infos.get(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION));
            l_paymentGroup.setIdTransactionPaybox(
                    Integer.valueOf((String)a_infos.get(CastoConstantesPaybox.PARAMETRE_IDENTIFIANT_TRANSACTION)));
            l_paymentGroup.setNumTransactionPaybox(
                    Integer.valueOf((String)a_infos.get(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION)));                    
            
            //l_paymentGroup.setIdTransactionPaybox(
            //        (Integer)a_infos.get(CastoConstantesPaybox.PARAMETRE_IDENTIFIANT_TRANSACTION));
            //l_paymentGroup.setNumTransactionPaybox(
            //       (Integer)a_infos.get(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION));
            l_paymentGroup.setDateTransactionPaybox(
                    new Timestamp(l_now.getTimeInMillis()));
            l_paymentGroup.setDatePaiementPaybox( 
                    new Timestamp(l_now.getTimeInMillis()));
            l_paymentGroup.setDateExpirationPaybox( 
                    new Timestamp(l_dateExpiration.getTimeInMillis()));
            l_paymentGroup.setDateValidAtout(new Timestamp(l_dateExpiration.getTimeInMillis()));
            break;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.updateOrderAtout");
        }
    }/* Code Review*/

    /**
     * Méthode qui traite le retour de paiement Paybox.
     * 
     * @param a_referenceCommande
     *            La commande.
     * @param a_request
     *            La requête HTTP.
     * @param a_response
     *            Response
     * 
     * @return Le jsessionid de la commande traitée, ou null en cas d'échec.
     */
    /* Code Review*/public String traiterRetourPaiementPaybox(String a_referenceCommande, 
            DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response) 
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest).");
        }

        String l_jsessionId = null;
        /*
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         * 1 - On verifie que l'on recupere bien un numero d'autorisation.
         * Si c'est le cas, on enregistre les informations de paiement de la
         * commande.
         * Sinon, on ne fait rien.
         * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         */

        String l_numAutorisation = a_request.getParameter(CastoConstantesPaybox.PARAMETRE_NUMERO_AUTORISATION);
        if (isLoggingInfo())
        {
            logInfo("CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest) : num autorisation : "
                    + l_numAutorisation);
        }
        if (null != l_numAutorisation && !"".equals(l_numAutorisation))
        {
            CastoOrder l_order=null;
            OrderManager l_orderManager = getOrderManager();
            if (null != l_orderManager)
            {
                try
                {
                    l_order = (CastoOrder)l_orderManager.loadOrder(a_referenceCommande);
                    if(null!=l_order)
                    {
                        synchronized (l_order)
                        {
                            boolean l_rollback = false;
                            TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                            try
                            {
                                l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                                try
                                {
                                    /*
                                     * S il y a une erreur durant la transaction java, alors on redirige vers la page
                                     * d erreur de paiement avec l_jsessionId==null
                                     */ 
                                    l_jsessionId = (String) l_order.getSessionId();
                                    
                                    /*
                                     * Preparation des calendars pour la gestion des dates de : transation, de paiement, d expiration
                                     */
                                    Calendar l_now = Calendar.getInstance();
                                    l_now.add(Calendar.DAY_OF_MONTH, CastoConstantesCommande.DELAI_PAIEMENT_PAYBOX);
                                    String l_exp = a_request.getParameter(EXP);
                                    Calendar l_dateExpiration = Calendar.getInstance();
                                    l_dateExpiration.set(Calendar.DATE, CastoConstantes.UN);
                                    int l_month = Integer.valueOf(l_exp.substring(CastoConstantes.DEUX, CastoConstantes.QUATRE))
                                            .intValue() + 1;
                                    l_dateExpiration.set(Calendar.MONTH, l_month);
                                    int l_year = Integer.valueOf("20" + l_exp.substring(0, CastoConstantes.DEUX)).intValue();
                                    l_dateExpiration.set(Calendar.YEAR, l_year);
                                    
                                    l_order.setPayeCarteAtout(Boolean.TRUE);
                                    l_order.setStateAsString(PENDING_ATOUT);
                                    l_order.setStateDetail("paiementValide");
                                    l_order.setBOState(VALIDE);
                                    l_order.setBOStateDetail("La commande a ete validee.");
                                    /*
                                     * On set les objets paymentGroup de la commande : Moyen de paiement
                                     */
                                    List l_paymentsGroups = (List) l_order.getPaymentGroups();
                                    for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
                                    {
                                        CastoPaymentGroupImpl l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
                                        l_paymentGroup.setPaymentMethod(CastoCartModifierFormHandler.PAIEMENT_CARTE_ATOUT);
                                        l_paymentGroup.setCreditCardNumber("4111111111111111");
                                        l_paymentGroup.setCreditCardType("Visa");
                                        l_paymentGroup.setExpirationMonth(new Integer(l_month).toString());
                                        l_paymentGroup.setExpirationYear(new Integer(l_year).toString());
                                        l_paymentGroup.setNumeroAutorisationPaybox(l_numAutorisation);
                                        l_paymentGroup.setIdTransactionPaybox(
                                                new Integer(a_request.getParameter(CastoConstantesPaybox.PARAMETRE_IDENTIFIANT_TRANSACTION)));
                                        l_paymentGroup.setNumTransactionPaybox(
                                                new Integer(a_request.getParameter(CastoConstantesPaybox.PARAMETRE_NUMERO_TRANSACTION)));
                                        l_paymentGroup.setDateTransactionPaybox(
                                                new Timestamp(l_now.getTimeInMillis()));
                                        l_paymentGroup.setDatePaiementPaybox( 
                                                new Timestamp(l_now.getTimeInMillis()));
                                        l_paymentGroup.setDateExpirationPaybox( 
                                                new Timestamp(l_dateExpiration.getTimeInMillis()));
                                        l_paymentGroup.setDateValidAtout(new Timestamp(l_dateExpiration.getTimeInMillis()));
                                        break;
                                    }
                                    /*
                                     * On met à jour la commande
                                     */
                                    getOrderManager().updateOrder(l_order);
                                }
                                /* Code Review*/catch (CommerceException l_e)/* Code Review*/
                                {
                                    logError(l_e);
                                    l_rollback = true;
                                    l_jsessionId = null;
                                }
                                finally
                                {
                                    if (isLoggingInfo())
                                    {
                                        logInfo("CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest) " +
                                                ": commit des modifications, rollback = " + l_rollback);
                                    }
                                    l_transactionDemarcation.end(l_rollback);
                                }
                            }
                            catch (TransactionDemarcationException l_tde)
                            {
                                logError(l_tde);
                            }
                        }
                    }
                }
                /* Code Review*/catch (CommerceException l_exception)/* Code Review*/
                {
                    logError(l_exception);
                }
            }
            else
            {
                logError("CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest) : OrderManager == NULL");
            }
        }
        else
        {
            logError("CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest) : l_numAutorisation == NULL");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.traiterRetourPaiementPaybox(String, DynamoHttpServletRequest).");
        }

        return l_jsessionId;
    }/* Code Review*/

    /**
     * M�thode qui renvoie le libell� correspondant au code retour SIPS.
     * 
     * @param a_responseCode
     *            Le code retour SIPS
     * 
     * @return String Le libell� correspondant au code retour SIPS.
     */
    /* Code Review*/private String getLibelle(int a_responseCode)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.getLibelle().");
        }
        
        String l_strLibelle;

        switch (a_responseCode)
        {
            case CODE_RETOUR_PAIEMENT_ACCEPTE:
            {
                l_strLibelle = LIBELLE_PAIEMENT_ACCEPTE;
                break;
            }
            case CODE_RETOUR_PAIEMENT_REFUSE:
            {
                l_strLibelle = LIBELLE_PAIEMENT_REFUSE;
                break;
            }
            case CODE_RETOUR_MERCHANT_ID_INVALIDE:
            {
                l_strLibelle = LIBELLE_MERCHANT_ID_INVALIDE;
                break;
            }
            case CODE_RETOUR_REFUS_PAIEMENT:
            {
                l_strLibelle = LIBELLE_REFUS_PAIEMENT;
                break;
            }
            case CODE_RETOUR_TRANSACTION_INVALIDE:
            {
                l_strLibelle = LIBELLE_TRANSACTION_INVALIDE;
                break;
            }
            case CODE_RETOUR_MONTANT_INVALIDE:
            {
                l_strLibelle = LIBELLE_MONTANT_INVALIDE;
                break;
            }
            case CODE_RETOUR_ID_CLIENT_INVALIDE:
            {
                l_strLibelle = LIBELLE_ID_CLIENT_INVALIDE;
                break;
            }
            case CODE_RETOUR_ANNULATION_CLIENT:
            {
                l_strLibelle = LIBELLE_ANNULATION_CLIENT;
                break;
            }
            case CODE_RETOUR_ERREUR_FORMAT:
            {
                l_strLibelle = LIBELLE_ERREUR_FORMAT;
                break;
            }
            case CODE_RETOUR_ERREUR_SECURITE:
            {
                l_strLibelle = LIBELLE_ERREUR_SECURITE;
                break;
            }
            case CODE_RETOUR_NB_ESSAIS_MAX:
            {
                l_strLibelle = LIBELLE_NB_ESSAIS_MAX;
                break;
            }
            case CODE_RETOUR_SERVICE_INDISPONIBLE:
            {
                l_strLibelle = LIBELLE_SERVICE_INDISPONIBLE;
                break;
            }
            case CODE_RETOUR_TRANSACTION_DEJA_TRAITEE:
            {
                l_strLibelle = LIBELLE_TRANSACTION_DEJA_TRAITEE;
                break;
            }
            default:
            {
                l_strLibelle = "";
                break;
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.getLibelle().");
        }
        
        return l_strLibelle;
    }/* Code Review*/
    
    /**
     * Methode qui enregistre les informations de paiement SIPS.
     * 
     * @param a_sipsData
     *            Les donnees renvoyees par SIPS.
     */
    /* Code Review*/private void creationInformationPaiement(SIPSDataObject a_sipsData, String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.creationInformationPaiement().");
        }
        try
        {
            // On recupere le repository gerant la confirmation des commandes CB et
            // qui enregistrent en base la trace de la confirmation de paiement CB
            MutableRepository l_orderRepo = (MutableRepository) getOrderTransactionRepository();
            String l_DescriptorName = "order_transaction";
            Object l_autoId = a_sipsData.getValue(AUTORISATION_ID);
            String l_autorisationId;
            //Order Transaction
            if(null!=l_orderRepo)
            {
                Object l_DateTransExpiree = a_sipsData.getValue(PAYMENT_DATE);
                if(null != l_autoId && null != l_DateTransExpiree)
                {
                    l_autorisationId = l_autoId.toString();
                    String l_date = l_DateTransExpiree.toString();
                    Calendar l_Cal = Calendar.getInstance();
                    String l_stryyyy = l_date.substring(0, INDEX_FIN_ANNEE);
                    String l_strmm = l_date.substring(INDEX_FIN_ANNEE, INDEX_FIN_MOIS);
                    String l_strdd = l_date.substring(INDEX_FIN_MOIS, INDEX_FIN_JOUR);
                    int l_nyear = Integer.parseInt(l_stryyyy);
                    int l_nmonth = Integer.parseInt(l_strmm);
                    int l_nday = Integer.parseInt(l_strdd);
                    l_Cal.set(Calendar.YEAR, l_nyear);
                    l_Cal.set(Calendar.MONTH, l_nmonth - 1);
                    l_Cal.set(Calendar.DAY_OF_MONTH, l_nday);
                    Date l_dateExpiration = new java.sql.Date(l_Cal.getTime().getTime());
                    // On verifie que l'enregistrement n'etait pas deja� present
                    RepositoryView l_view = l_orderRepo.getView(l_orderRepo.getRepositoryName());
                    RqlStatement l_statement = RqlStatement.parseRqlStatement("order_id = ?0");
                    Object []l_params = new Object[CastoConstantes.UN];
                    l_params[0] = a_orderId;
                    RepositoryItem [] l_items = l_statement.executeQuery (l_view, l_params);
                    MutableRepositoryItem l_logOrderTransaction;
                    l_logOrderTransaction = l_orderRepo.createItem(l_DescriptorName);
                    if(l_items != null)
                    {
                            if(isLoggingDebug())
                            {
                                logDebug("CASTO_ORDER_TRANSACTION contient deja cet enregistrement.");
                            }
                    }
                    else
                    {
                        // On prepare les valeurs a� inserer en base
                        l_logOrderTransaction.setPropertyValue(ORDER_ID, a_orderId);
                        l_logOrderTransaction.setPropertyValue("date_trans_expire", l_dateExpiration);
                        l_logOrderTransaction.setPropertyValue(AUTORISATION_ID, l_autorisationId);
                        // Insertion en base de l'enregistrement
                        l_orderRepo.addItem(l_logOrderTransaction);
                    }
                }
                else
                {
                    logError("null == l_autoId || l_DateTransExpiree==null");
                }
            }
            else
            {
                logError("getOrderTransactionRepository()==null");
            }
            
            //SIPS Log
            l_orderRepo = (MutableRepository) getOrderLogsSIPSRepository();
            if(null!=l_orderRepo)
            {
                if(null != l_autoId)
                {
                    l_autorisationId = l_autoId.toString();
                    MutableRepositoryItem l_logSips;
                    l_DescriptorName = "logsips";
                    l_logSips = l_orderRepo.createItem(l_DescriptorName);
                    l_logSips.setPropertyValue(ORDER_ID, a_orderId);
                    l_logSips.setPropertyValue("response_code", a_sipsData.getValue(BANK_RESPONSE_CODE));
                    l_logSips.setPropertyValue("libelle", getLibelle(Integer.parseInt(a_sipsData.getValue(BANK_RESPONSE_CODE))));
                    l_logSips.setPropertyValue("montant", new Integer(a_sipsData.getValue(PROPERTY_AMOUNT)));
                    l_logSips.setPropertyValue(TRANSACTION_ID, a_sipsData.getValue(TRANSACTION_ID));
                    l_logSips.setPropertyValue("authorisation_id", l_autorisationId);
                    l_logSips.setPropertyValue(PROPERTY_PAYMENT_MEANS, a_sipsData.getValue(PROPERTY_PAYMENT_MEANS));
                    l_logSips.setPropertyValue(PROPERTY_CARD_NUMBER, a_sipsData.getValue(PROPERTY_CARD_NUMBER));
                    l_logSips.setPropertyValue("payment_certificate", a_sipsData.getValue("payment_certificate"));
                    l_logSips.setPropertyValue("payment_time", a_sipsData.getValue("payment_time"));
                    l_logSips.setPropertyValue("payment_date", a_sipsData.getValue(PAYMENT_DATE));
                    l_logSips.setPropertyValue("devise", a_sipsData.getValue("currency_code"));
                    l_logSips.setPropertyValue("profile_id", a_sipsData.getValue(CUSTOMER_ID));
                    // D�coupage de la date
                    String l_strAnneeTransaction = a_sipsData.getValue(PAYMENT_DATE).substring(0, INDEX_FIN_ANNEE);
                    String l_strMoisTransaction = a_sipsData.getValue(PAYMENT_DATE).substring(INDEX_FIN_ANNEE, INDEX_FIN_MOIS);
                    String l_strJourTransaction = a_sipsData.getValue(PAYMENT_DATE).substring(INDEX_FIN_MOIS, INDEX_FIN_JOUR);
                    // D�claration des entiers correspondant au jour, mois, ann�e de
                    // la transaction
                    int l_nAnneeTransaction = Integer.parseInt(l_strAnneeTransaction);
                    int l_nMoisTransaction = Integer.parseInt(l_strMoisTransaction);
                    int l_nJourTransaction = Integer.parseInt(l_strJourTransaction);
                    // Cr�ation d'une date correspondant � ces valeurs
                    Calendar l_cal = Calendar.getInstance();
                    l_cal.set(Calendar.DAY_OF_MONTH, l_nJourTransaction);
                    l_cal.set(Calendar.MONTH, l_nMoisTransaction - 1);
                    l_cal.set(Calendar.YEAR, l_nAnneeTransaction);
                    String l_strNewMonth = "";
                    String l_strNewDay = "";
                    int l_nNewMonth = l_cal.get(Calendar.MONTH) + 1;
                    int l_nNewDay = l_cal.get(Calendar.DAY_OF_MONTH);
                    l_strNewMonth = (l_nNewMonth < CastoConstantes.DIX) ? "0" + l_nNewMonth : "" + l_nNewMonth;
                    l_strNewDay = (l_nNewDay < CastoConstantes.DIX) ? "0" + l_nNewDay : "" + l_nNewDay;
                    String l_strDateTransExpire = l_cal.get(Calendar.YEAR) + l_strNewMonth + l_strNewDay;
                    l_logSips.setPropertyValue("date_trans_expire", l_strDateTransExpire);
                    // Insertion en base de l'enregistrement
                    l_orderRepo.addItem(l_logSips);
                }
                else
                {
                    logError("null == l_autoId || l_DateTransExpiree==null");
                }
            }
            else
            {
                logError("getOrderLogsSIPSRepository()==null");
            }
        }
        catch (SIPSException l_ex)
        {
            logError(l_ex);
        }
        catch (RepositoryException l_ex)
        {
            logError(l_ex);
        }
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.creationInformationPaiement().");
        }
    }/* Code Review*/
    
    /**
     * Methode qui enregistre les informations de paiement SIPS.
     * 
     * @param a_sipsData
     *            Les donnees renvoyees par SIPS.
     */
    /* Code Review*/private void insertionConfirmationPaiementSIPS(SIPSDataObject a_sipsData, String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.insertionConfirmationPaiementSIPS().");
        }

        try
        {
            Object l_resCode = a_sipsData.getValue(BANK_RESPONSE_CODE);
            if(null!=l_resCode)
            {
                String l_rc = l_resCode.toString();
                if(!"".equals(l_rc))
                {
                    int l_responseCode = Integer.valueOf(l_rc).intValue();
                    switch (l_responseCode)
                    {
                        case CODE_RETOUR_PAIEMENT_ACCEPTE   : creationInformationPaiement(a_sipsData, a_orderId); break;
                        case CODE_RETOUR_PAIEMENT_REFUSE    : creationInformationPaiement(a_sipsData, a_orderId); break;
                        case CODE_RETOUR_REFUS_PAIEMENT     : creationInformationPaiement(a_sipsData, a_orderId); break;
                        case CODE_RETOUR_ANNULATION_CLIENT  : creationInformationPaiement(a_sipsData, a_orderId); break;
                        case CODE_RETOUR_NB_ESSAIS_MAX      : creationInformationPaiement(a_sipsData, a_orderId); break;
                        default:
                            logError("erreur dans le code retour");
                    }
                }
                else
                {
                    logError("erreur dans le type de variable du code retour");
                }
            }    
            else
            {
                logError("code retour == null");
            }
        }
        catch (SIPSException l_ex)
        {
            logError(l_ex);
        }
    
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.insertionConfirmationPaiementSIPS().");
        }
    }
    
    /**
     * .
     * Methode qui valide la commande dans ATG.
     */
    private String processOrder(String a_orderId, Map a_infos, DynamoHttpServletRequest a_request)
        throws CommerceException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.processOrder().");
        }
        
        String l_retour = null;
        OrderManager l_om = getOrderManager();
        Order l_order = null;
        if(null!=l_om)
        {
            l_order = l_om.loadOrder(a_orderId);
            if(null!=l_order)
            {
                CastoOrder l_castoOrder = (CastoOrder) l_order;
                int l_etat = l_castoOrder.getState();
                if(getOrderManager().isOrderStateValidForProcessing(l_etat))
                {
                    synchronized (l_order)
                    {
                        boolean l_rollback = false;
                        TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                        try
                        {
                            l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                            try
                            {
                                PipelineResult l_resultPipeline;
                                Map l_paymentInfos = new HashMap();
                                if(null!=a_infos)
                                {
                                    l_paymentInfos.put("paymentInfos",a_infos);
                                }
                                if(null!=a_infos)
                                {
                                    if(null!=getDefaultLocale() && isLoggingDebug())
                                    {
                                        logDebug("Locale : "+getDefaultLocale().toString());
                                    }
                                    
                                    l_resultPipeline = getOrderManager().processOrderWithReprice(
                                            l_order,getDefaultLocale().toString(),new HashMap(l_paymentInfos));
                                    if (l_resultPipeline.hasErrors())
                                    {
                                        logError("Erreurs dans le pipeline de cdes CB et autres : ");
                                        Object[] l_keys = l_resultPipeline.getErrorKeys();
                                        for (int l_i = 0; l_i < l_keys.length; l_i++)
                                        {
                                            logError(l_resultPipeline.getError(l_keys[l_i]).toString());
                                        }        
                                        changeState((CastoOrder)l_order, "ERREUR SIPS", "ERREUR SIPS", CREDIT_CARD);
                                    }
                                    else
                                    {
                                        l_retour= "/store/html/commande/panier-confirmation-commande.jsp?order_id=" + l_order.getId();
                                        Object l_pId = a_infos.get(PROFILE_ID);
                                        if(null != l_pId && !"".equals(l_pId.toString()))
                                        {
                                            RepositoryItem l_origineMagasin = ((CastoOrder)l_order).getOrigineMagasin();
                                            gestionNouvelleCde(l_pId.toString(),a_request,l_origineMagasin);
                                        }
                                        else
                                        {
                                            logError("_infos.get(profileId)==null");
                                        }
                                    }
                                }
                                else
                                {
                                    logError("l_infos==null");
                                }
                            }
                            catch (CommerceException l_exceptionCCE)
                            {
                                logError(l_exceptionCCE);
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
                    }
                }
                else
                {
                    if(isLoggingDebug())
                    {
                        logDebug("Commande deja en traitement par ATG");
                    }
                }    
            }
            else
            {
                logError("l_order==null");
            }        
        }
        else
        {
            logError("getOrderManager() = = null");
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.processOrder().");
        }
        return l_retour;
    }/* Code Review*/
    
    /**
     * .
     * @param a_pId a_pId
     * @param a_request a_request
     * @param a_origineMagasin a_origineMagasin
     */
    /* Code Review*/private void gestionNouvelleCde(String a_pId, DynamoHttpServletRequest a_request, RepositoryItem a_origineMagasin)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.gestionNouvelleCde().");
        }
        
        OrderManager l_om = getOrderManager();
        if(null!=l_om)
        {
            try
            {
                Order l_newOrder = l_om.createOrder(a_pId);
                if(isLoggingDebug())
                {
                    logDebug("createOrder : "+a_pId.toString());
                }
                OrderHolder l_oh   = (OrderHolder)a_request.resolveName("/atg/commerce/ShoppingCart");
                CastoOrder l_nOrder = (CastoOrder)l_newOrder;
                l_nOrder.setOrigineMagasin(a_origineMagasin);
                if(isLoggingDebug())
                {
                    logDebug("Nouvelle Cde : "+l_nOrder.getId()+" - Origine Magasin Cde : "
                            +a_origineMagasin.getRepositoryId());
                }                
                l_newOrder.removeAllCommerceItems();
                l_om.updateOrder(l_newOrder);
                l_oh.setCurrent(l_newOrder);
            }
            catch(CommerceException l_ce)
            {
                logError(l_ce);
            }
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("getOrderManager()= =null");
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.gestionNouvelleCde().");
        }
    }/* Code Review*/
    
    /**
     * .
     * 
     * Methode qui transfert les activePromotion vers les usedPromotions
     * @param a_customerId a_customerId
     */
    /* Code Review*/private void transfertPromotion(String a_customerId, DynamoHttpServletRequest a_request)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.transfertPromotion().");
        }

        // On recupere le profil et ses promotions actives
        try
        {
            OrderManager l_om = getOrderManager();
            if(null!=l_om)
            {
                OrderTools l_ot = l_om.getOrderTools();
                if(null!=l_ot)
                {
                    Repository l_pr = l_ot.getProfileRepository();
                    if(null!=l_ot)
                    {
                        MutableRepositoryItem l_profile = (MutableRepositoryItem) l_pr.getItem(a_customerId, CastoConstantes.DESCRIPTEUR_UTILISATEUR);
                        if(null!=l_profile)
                        {
                            Collection l_activePromotions = (Collection) l_profile.getPropertyValue(ACTIVE_PROMOTION);
                            if (null != l_activePromotions)
                            {
                                Collection l_usedPromotions = (Collection) l_profile.getPropertyValue(USED_PROMOTION);
                                if (l_usedPromotions == null)
                                {
                                    l_usedPromotions = new ArrayList();
                                }
                                // l_usedPromotions.addAll(l_activePromotions);
                                Iterator l_iterator = l_activePromotions.iterator();
                                RepositoryItem l_status=null;
                                while (l_iterator.hasNext())
                                {
                                    l_status = (RepositoryItem) l_iterator.next();
                                    l_usedPromotions.add(l_status.getPropertyValue("promotion"));
                                }
                                // On met a null les promotions actives et
                                // on enregistre le tout...
                                l_activePromotions = null;
                                l_profile.setPropertyValue(ACTIVE_PROMOTION, l_activePromotions);
                                l_profile.setPropertyValue(USED_PROMOTION, l_usedPromotions);
                                ((MutableRepository)l_pr).updateItem(l_profile);

                                // Si on ne vide pas le UserPricingModels
                                // la promotion sera repass�e � la commande suivante...
                                Object l_objPH = a_request.resolveName("/atg/commerce/pricing/UserPricingModels");
                                if(null!=l_objPH)
                                {
                                    try
                                    {
                                        PricingModelHolder l_pmh = (PricingModelHolder)l_objPH;
                                        if(isLoggingDebug())
                                        {
                                            logDebug("l_pmh!=null");
                                            
                                        }
                                        l_pmh.setProfile(l_profile);
                                        l_pmh.initializeAllPromotions();
                                        l_pmh.initializePricingModels();    
                                    }
                                    catch(ClassCastException l_cce)
                                    {
                                        logError(l_cce);
                                    }
                                }
                                else
                                {
                                    logError("a_request.resolveName(/atg/commerce/pricing/UserPricingModels)==null");
                                }     
                            }
                            else
                            {
                                if(isLoggingDebug())
                                {
                                    logDebug("Aucune promotion utilisee pour cette commande.");
                                }
                            }
                        }
                        else
                        {
                            logError("getProfileRepository().getItem() == null");
                        }
                    }
                    else
                    {
                        logError("getProfileRepository() == null");
                    }
                }
                else
                {
                    logError("getOrderTools() == null");
                }
            }
            else
            {
                logError("getOrderManager() = = null");
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
                    + "com.castorama.commande.CastoOrderTools.transfertPromotion().");
        }
    }/* Code Review*/
    
    /**
     * .
     * 
     * M�thode qui enregistre les coupons utilis�s pour cette commande
     * 
     * @param a_orderId
     *            ID de la commande
     * @param a_customerId
     *            ID du user
     */
   /* Code Review*/private void enregistrementCoupon(String a_orderId,String a_customerId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.enregistrementCoupon().");
        }
        
        //  On recupere le profil et ses promotions actives
        try
        {
            OrderManager l_om = getOrderManager();
            if(null!=l_om)
            {
                OrderTools l_ot = l_om.getOrderTools();
                if(null!=l_ot)
                {
                    Repository l_pr = l_ot.getProfileRepository();
                    if(null!=l_ot)
                    {
                        MutableRepositoryItem l_profile = (MutableRepositoryItem) l_pr.getItem(a_customerId, CastoConstantes.DESCRIPTEUR_UTILISATEUR);
                        if(null!=l_profile)
                        {
                            Collection l_coupons = (Collection) l_profile.getPropertyValue("activeCoupons");
                            Iterator l_iterator = l_coupons.iterator();
                            String l_couponId =null;
                            MutableRepositoryItem l_coupon = null;
                            while (l_iterator.hasNext())
                            {
                                l_couponId = (String) l_iterator.next();
                                l_coupon = getCouponRepository().createItem("casto_coupons");
                                l_coupon.setPropertyValue("order_id", a_orderId);
                                l_coupon.setPropertyValue("code_coupon", l_couponId);
                                l_coupon.setPropertyValue("date_usage", new Date());
                                getCouponRepository().addItem(l_coupon);        
                            }
                            l_profile.setPropertyValue("couponsUtilises", null);
                            l_profile.setPropertyValue("activeCoupons", null);
                            ((MutableRepository)l_pr).updateItem(l_profile);
                        }
                        else
                        {
                            logError("getProfileRepository().getItem()==null");
                        }
                    }
                    else
                    {
                        logError("getProfileRepository()==null");
                    }
                }
                else
                {
                    logError("getOrderTools()==null");
                }
            }
            else
            {
                logError("getOrderManager()==null");
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
                    + "com.castorama.commande.CastoOrderTools.enregistrementCoupon().");
        }
    }/* Code Review*/
    
    /**
     * M�thode qui valide la commande dans ATG.
     * 
     * @param a_sipsData
     *            Les donn�es renvoy�es par SIPS.
     */
    /* Code Review*/private String validerCommande(SIPSDataObject a_sipsData, DynamoHttpServletRequest a_request,
            DynamoHttpServletResponse a_response)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.validerCommande().");
        }
        
        String l_retour = null;
        try
        {
            if (null!= a_sipsData)
            {
                Object l_transaction = a_sipsData.getValue(BANK_RESPONSE_CODE);
                if(null!=l_transaction && TRANSACTION_APPROUVEE.equals(l_transaction.toString()))
                {
                    Object l_orderIdentifiant = a_sipsData.getValue(ORDER_ID);
                    if(null!=l_orderIdentifiant)
                    {
                        String l_orderId = a_sipsData.getValue(ORDER_ID);
                        Object l_custId = a_sipsData.getValue(CUSTOMER_ID);
                        if (isLoggingDebug())
                        {
                            if(null!=l_custId)
                            {
                                logDebug("| traitement de la commande n : " + l_orderId + ", pour le profil n : " + a_sipsData.getValue(CUSTOMER_ID));
                            }
                            Object l_bankRespCode = a_sipsData.getValue(BANK_RESPONSE_CODE);
                            if(null!=l_bankRespCode)
                            {
                                logDebug("| statut : " + a_sipsData.getValue(BANK_RESPONSE_CODE));
                            }
                        }
                        /*
                         * Insertion dans la table CASTO_ORDER_TRANSACTION et CASTO_SIPS_LOG :
                         * Tra�abilit� des donn�es de paiement
                         */
                        insertionConfirmationPaiementSIPS(a_sipsData,l_orderId);
                        
                        Map l_infos = new java.util.HashMap();
                        l_infos.put(PROPERTY_BO_STATE, VALIDE);
                        l_infos.put(PROPERTY_BO_STATE_DETAIL, "La commande a ete validee.");
                        l_infos.put(PROFILE_ID, a_sipsData.getValue(CUSTOMER_ID));
                        l_infos.put(PROPERTY_PAYMENT_MEANS, a_sipsData.getValue(PROPERTY_PAYMENT_MEANS));
                        l_infos.put(PROPERTY_CARD_NUMBER, a_sipsData.getValue(PROPERTY_CARD_NUMBER));

                        // M�thode maj via le call center
                        if (checkOrderCallCenter(l_orderId))
                        {
                            updateOrderCB(l_orderId, l_infos);
                        }
                        else
                        {
                            if(isLoggingDebug())
                            {
                                logDebug("avant l_retour = processOrder(l_orderId, l_infos,a_request);");
                            }
                            l_retour = processOrder(l_orderId, l_infos,a_request);
                            if(isLoggingDebug())
                            {
                                logDebug("apres l_retour = processOrder(l_orderId, l_infos,a_request);");
                            }
                            if(null!=l_custId)
                            {
                                String l_customerIdentifiant = l_custId.toString();
                                // on passe les promotions de active et used et on vide le UserPriceEngine
                                transfertPromotion(l_customerIdentifiant,a_request);
                                // On enregistre les coupons utilise...
                                enregistrementCoupon(l_orderId,l_customerIdentifiant);
                            }
                            else
                            {
                                if(isLoggingDebug())
                                {
                                    logDebug("l_custId==null");
                                }
                            }
                        }
                    }
                    else
                    {
                        logError("a_sipsData.getValue(ORDER_ID) == null");
                    }
                }
                else
                {
                    logError("a_sipsData.getValue(BANK_RESPONSE_CODE) == null");
                }
            }
            else
            {
                logError("a_sipsData == null");
            }
        }
        catch (SIPSException l_exception)
        {
            logError(l_exception.toString());
        }
        catch(CommerceException l_ce)
        {
            logError(l_ce);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.validerCommande().");
        }
        
        return l_retour;
    }/* Code Review*/

    /**
     * M�thode qui v�rifie si la commande existe d�j� en base et est dans un
     * �tat du CallCenter (Call-Center, Virement, etc...).
     * 
     * @param a_orderId
     *            Identifiant de la commande � tester.
     * 
     * @return boolean true si la commande existe et se trouve dans le bon �tat,
     *         false sinon.
     */
    private boolean checkOrderCallCenter(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.checkOrderCallCenter().");
        }

        boolean l_ret;

        try
        {
            RepositoryItem l_order = getOrderRepository().getItem(a_orderId, "order");

            if (null != l_order)
            {
                String l_state = (String) l_order.getPropertyValue(PROPERTY_STATE);

                if (PENDING_CHEQUE.equals(l_state) || PENDING_CALL_CENTER.equals(l_state)
                        || PENDING_VIREMENT.equals(l_state))
                {
                    l_ret = true;
                }
                else
                {
                    l_ret = false;
                }
            }
            else
            {
                l_ret = false;
            }
        }
        catch (RepositoryException l_repositoryException)
        {
            logError("--> checkOrderCallCenter : " + l_repositoryException);

            l_ret = false;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.checkOrderCallCenter().");
        }
        return l_ret;
    }

    /**
     * Changement d'�tat d'une commande.
     * 
     * @param a_order
     *            La commande.
     * @param a_state
     *            L'�tat de la commande.
     * @param a_detail
     *            D�tail de la modification d'�tat.
     * @param a_paiementMethod
     *            Moyen de paiement
     */
    /* Code Review*/private void changeState(CastoOrder a_order, String a_state, String a_detail, String a_paiementMethod)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.changeState().");
        }

        try
        {
            if (null != a_state)
            {
                a_order.setStateAsString(a_state);
            }

            if (null != a_detail)
            {
                a_order.setBOStateDetail(a_detail);
            }

            a_order.setExportDate(null);
            a_order.setLastModifiedDate(new Date());
            List l_paymentsGroups = (List) a_order.getPaymentGroups();
            for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
            {
                CastoPaymentGroupImpl l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
                l_paymentGroup.setLibelleBanque("");
                l_paymentGroup.setNumCheque("");
                l_paymentGroup.setMontantChequeFrancs("");
                l_paymentGroup.setMontantChequeEuros("");
                l_paymentGroup.setDateValidAtout(null);
                break;
            }
            
            getOrderManager().updateOrder(a_order);
        }
        catch (CommerceException l_ce)
        {
            logError("CastoOrderTools.changeState() : " + l_ce.toString());
        }    

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.changeState().");
        }
    }/* Code Review*/

    /**
     * Met à jour certaines infos de la commande pour les paiements de type CB.
     * 
     * @param a_orderId
     *            Identifiant de la commande à mettre à jour.
     * @param a_infos
     *            Informations à ajouter.
     */
    /* Code Review*/public void updateOrderCB(String a_orderId, Map a_infos)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.updateOrderCB()");
        }

        /*
         * Logging des param�tres de la m�thode.
         */
        if (isLoggingDebug())
        {
            logDebug("| order : " + a_orderId);
            if (null != a_infos)
            {
                String l_key;
                for (Iterator l_keys = a_infos.keySet().iterator(); l_keys.hasNext();)
                {
                    l_key = (String) l_keys.next();
                    logDebug("| " + l_key + " : " + a_infos.get(l_key));
                }
            }
        }

        OrderManager l_om = getOrderManager();
        Order l_order = null;
        if(null!=l_om)
        {
            try
            {
                l_order = l_om.loadOrder(a_orderId);
                if(null!=l_order)
                {
                    CastoOrder l_castoOrder = (CastoOrder) l_order;
                    int l_etat = l_castoOrder.getState();
                    if(getOrderManager().isOrderStateValidForProcessing(l_etat))
                    {
                        synchronized (l_order)
                        {
                            boolean l_rollback = false;
                            TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                            try
                            {
                                l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                                try
                                {
                                    l_castoOrder.setBOState((String) a_infos.get(PROPERTY_BO_STATE));
                                    l_castoOrder.setBOStateDetail((String) a_infos.get(PROPERTY_BO_STATE_DETAIL));
                                    l_castoOrder.setProfileId((String) a_infos.get(PROFILE_ID));
                                    
                                    // Mise a� jour des informations
                                    List l_paymentsGroups = (List) l_castoOrder.getPaymentGroups();
                                    CastoPaymentGroupImpl l_paymentGroup = null;
                                    String l_paymentMethod = null;
                                    for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
                                    {
                                        l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
                                        l_paymentMethod = (String) l_paymentGroup.getPropertyValue(PAYMENT_METHOD);
    
                                        if (CREDIT_CARD.equals(l_paymentMethod)
                                                || CastoCartModifierFormHandler.PAIEMENT_PAR_TELEPHONE.equals(l_paymentMethod)
                                                || CastoCartModifierFormHandler.PAIEMENT_PAR_VIREMENT.equals(l_paymentMethod))
                                        {
                                            //updateMontantPaymentGroupChequeCadeau(l_castoOrder, l_paymentGroup);
    
                                            /*
                                             * On met a jour le statut de la commande en fonction du
                                             * moyen de paiement (seulement pour la gestion des cheques
                                             * cadeaux).
                                             */
                                            updateOrderState("", l_castoOrder, null);
                                            
                                            Object l_cbType = a_infos.get(PROPERTY_PAYMENT_MEANS);
                                            if(null!=l_cbType)
                                            {
                                                l_paymentGroup.setCreditCardType(l_cbType.toString());
                                            }
                                            else
                                            {
                                                if(isLoggingDebug())
                                                {
                                                    logDebug("PROPERTY_PAYMENT_MEANS==null");
                                                }
                                            }
                                            
                                            Object l_cbNumber = a_infos.get(PROPERTY_CARD_NUMBER);
                                            if(null!=l_cbNumber)
                                            {
                                                l_paymentGroup.setCreditCardNumber((l_cbNumber.toString()).replaceAll("\\.", "XXXXXXXXXX"));
                                            }
                                            else
                                            {
                                                if(isLoggingDebug())
                                                {
                                                    logDebug("PROPERTY_CARD_NUMBER==null");
                                                }
                                            }
                                            
                                            break;
                                        }
                                    }
                                    
                                    /*
                                     * On met a jour la commande
                                     */
                                    getOrderManager().updateOrder(l_order);
                                }
                                catch (CommerceException l_exceptionCCE)
                                {
                                    logError(l_exceptionCCE);
                                    l_rollback = true;
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
                        }
                    }
                    else
                    {
                        if(isLoggingDebug())
                        {
                            logDebug("Commande deja en traitement par ATG");
                        }
                    } 
                }
                else
                {
                    logError("l_order==null");
                }
            }
            catch (CommerceException l_ce) 
            {
                logError(l_ce);
            }
        }
        else
        {
            logError("getOrderManager() == null");
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.updateOrderCB()");
        }
    }/* Code Review*/


    /**
     * Met à jour certaines infos de la commande pour les paiements de type CB.
     * 
     * @param a_orderId
     *            Identifiant de la commande à mettre à jour.
     */
    public void updateInventoryCB(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.updateInventoryCB().");
        }

        if (null != a_orderId)
        {
            /*
             * R�cup�ration de la commande enregistr�e en base
             */
            if (isLoggingDebug())
            {
                logDebug("Modification de la commande n� : " + a_orderId);
            }

            try
            {
                // R�cup�ration de la commande
                RepositoryItem l_order = getOrderRepository().getItem(a_orderId, CastoConstantes.DESCRIPTEUR_COMMANDE);

                // On r�cup�re les commerces items
                List l_aItems = (List) l_order.getPropertyValue("commerceItems");

                for (int l_nI = 0; l_nI < l_aItems.size(); l_nI++)
                {
                    RepositoryItem l_oCommerce = (RepositoryItem) l_aItems.get(l_nI);
                    String l_sSkuId = (String) l_oCommerce.getPropertyValue(CATALOG_REF_ID);
                    Long l_oQuantite = (Long) l_oCommerce.getPropertyValue("quantity");

                    if (l_oQuantite != null)
                    {
                        long l_nQuantite = l_oQuantite.longValue();

                        try
                        {
                            long l_nQuantiteStock = getInventoryManager().queryStockLevel(l_sSkuId);

                            if (l_nQuantite > l_nQuantiteStock)
                            {
                                getInventoryManager().setStockLevel(l_sSkuId, 0);
                            }
                        }
                        catch (InventoryException l_oException)
                        {
                            if (isLoggingError())
                            {
                                logError(l_oException);
                            }
                        }

                    }
                }

            }
            catch (RepositoryException l_repositoryException)
            {
                logError("|--> RepositoryException : " + l_repositoryException.toString());
            }
        }
        // fin du if (null != a_orderId)

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.updateInventoryCB().");
        }
    }

    /**
     * Met à jour certaines infos de la commande pour les paiements de type
     * a_paymentMethod.
     * 
     * @param a_orderId
     *            Identifiant de la commande à mettre à jour.
     */
    /* Code Review*/public void updateInventory(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.updateInventory().");
        }

        if (null != a_orderId)
        {
            // Récupération de la commande enregistrée en base

            if (isLoggingDebug())
            {
                logDebug("Modification de la commande n° : " + a_orderId);
            }

            try
            {
                // Récupération de la commande
                RepositoryItem l_order = getOrderRepository().getItem(a_orderId, CastoConstantes.DESCRIPTEUR_COMMANDE);

                if (isLoggingDebug())
                {
                    logDebug("CastoOrderTools.updateInventory() : order récupéré.");
                }

                // Récupération de l'inventoryManager
                InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager(l_order, true);

                if (isLoggingDebug())
                {
                    logDebug("CastoOrderTools.updateInventory() : inventory récupéré : " + l_inventoryManager);
                }

                if (null != l_inventoryManager)
                {
                    // On récupère les commerces items
                    List l_aItems = (List) l_order.getPropertyValue("commerceItems");

                    if (isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.updateInventory() : liste des produits récupérés.");
                    }

                    for (int l_nI = 0; l_nI < l_aItems.size(); l_nI++)
                    {
                        RepositoryItem l_oCommerce = (RepositoryItem) l_aItems.get(l_nI);
                        String l_sSkuId = (String) l_oCommerce.getPropertyValue(CATALOG_REF_ID);
                        Long l_oQuantite = (Long) l_oCommerce.getPropertyValue("quantity");

                        if (isLoggingDebug())
                        {
                            logDebug("CastoOrderTools.updateInventory() : commerceItem : "
                                    + l_oCommerce.getRepositoryId());
                            logDebug("CastoOrderTools.updateInventory() : sku id : " + l_sSkuId);
                            logDebug("CastoOrderTools.updateInventory() : quantité : " + l_oQuantite);
                        }

                        if (l_oQuantite != null)
                        {
                            if (isLoggingDebug())
                            {
                                logDebug("CastoOrderTools.updateInventory() : quantité non nulle.");
                            }

                            long l_nQuantite = l_oQuantite.longValue();

                            if (isLoggingDebug())
                            {
                                logDebug("CastoOrderTools.updateInventory() : quantité : " + l_nQuantite);
                            }

                            try
                            {
                                int l_nRetour = l_inventoryManager.purchase(l_sSkuId, l_nQuantite);

                                if (isLoggingDebug())
                                {
                                    logDebug("CastoOrderTools.updateInventory() : retour : " + l_nRetour);
                                }

                                if (l_nRetour == InventoryManager.INVENTORY_STATUS_FAIL
                                        || l_nRetour == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND)
                                {
                                    logError("Impossible de modifier l'inventaire : Code retour = " + l_nRetour);
                                    // getInventoryManager().setStockLevel(l_sSkuId,
                                    // 0);
                                }
                                else if (l_nRetour == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY)
                                {
                                    if (isLoggingDebug())
                                    {
                                        logDebug("CastoOrderTools.updateInventory() : avant setStockLevel");
                                    }

                                    l_inventoryManager.setStockLevel(l_sSkuId, 0);

                                    if (isLoggingDebug())
                                    {
                                        logDebug("CastoOrderTools.updateInventory() : après setStockLevel");
                                    }
                                }
                            }
                            catch (InventoryException l_oException)
                            {
                                logError(l_oException);
                            }
                        }
                    }
                }
            }
            catch (RepositoryException l_repositoryException)
            {
                logError(l_repositoryException);
            }
            catch (ClassCastException l_cce)
            {
                logError(l_cce);
            }

        }
        // fin du if (null != a_orderId)

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.updateInventory().");
        }
    }/* Code Review*/

    /**
     * Renvoie la liste des commandes sujettes à relance.
     * 
     * @param a_dateReference
     *            La date de référence.
     * 
     * @return RepositoryItem[] La liste des commandes sujettes à relance.
     * 
     * @throws RepositoryException
     *             Si une erreur survient.
     */
    public RepositoryItem[] getCommandesRelance(GregorianCalendar a_dateReference) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.getCommandesRelance");
        }

        // R�cup�ration de l'order repository
        MutableRepository l_orderRepository = (MutableRepository) getOrderRepository();

        /*
         * Requ�te de s�lection des commandes concern�es par la relance :
         * 
         * Commandes pay�es avec des ch�ques.
         */
        RqlStatement l_findCommandes = RqlStatement.parseRqlStatement(PROPERTY_BO_STATE
                + " EQUALS \"PENDING_CHEQUE\" and submittedDate < ?0");

        // Ex�cution de la requ�te
        RepositoryView l_view = l_orderRepository.getView(CastoConstantes.DESCRIPTEUR_COMMANDE);
        RepositoryItem[] l_commandes = l_findCommandes.executeQuery(l_view, new Object[]
        { a_dateReference.getTime(), });

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.getCommandesRelance");
        }

        return l_commandes;
    }

    /**
     * M�thode pour la suppression d'une commande.
     * 
     * @param a_orderId
     *            Identifiant de la commande à supprimer.
     */
    public void supprimerCommande(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.supprimerCommande");
        }

        try
        {
            if (null != a_orderId)
            {
                ((MutableRepository) getOrderRepository()).removeItem(a_orderId, CastoConstantes.DESCRIPTEUR_COMMANDE);
            }
        }
        catch (RepositoryException l_repoException)
        {
            logError("supprimerCommande : " + l_repoException);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.supprimerCommande");
        }
    }

    /* Code Review*/
    /**
     * Méthode qui marque une commande en tant que relancée.
     * 
     * @param a_orderId
     *            L'identifiant de la commande.
     */
    public void marquerCommandeRelance(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.marquerCommandeRelance");
        }

        OrderManager l_om = getOrderManager();
        Order l_order = null;
        if(null!=l_om)
        {
            try
            {
                l_order = l_om.loadOrder(a_orderId);
            }
            catch(CommerceException l_ce)
            {
                logError(l_ce);
                l_order = null;
            }
            
            if(null!=l_order)
            {
                CastoOrder l_castoOrder = (CastoOrder) l_order;
                synchronized (l_order)
                {
                    boolean l_rollback = false;
                    TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                    try
                    {
                        l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                        try
                        {
                            // Modification de l'etat de la commande
                            l_castoOrder.setBOState(PENDING_RELANCE);
                            
                            /*
                             * On met a� jour la commande
                             */
                            getOrderManager().updateOrder(l_castoOrder);
                        }
                        catch (CommerceException l_exceptionCCE)
                        {
                            logError(l_exceptionCCE);
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
                }
            }
            else
            {
                logError("l_order == null");
            }        
        }
        else
        {
            logError("getOrderManager() == null");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.marquerCommandeRelance");
        }
    }/* Code Review*/

    /**
     * .
     * 
     * Enregistre le délai dans le champ GeneralementLivreEnHeures
     * 
     * @param a_order
     *            Commande en question
     * @param a_profile
     *            Profil de l'internaute.
     */
    public void enregistrerDelaisLivraison(Order a_order, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.enregistrerDelaisLivraison");
        }

        if (null != a_order)
        {
            List l_commerceItems = a_order.getCommerceItems();
            RepositoryItem l_sku;

            String l_idSku;
            int l_delai;

            for (Iterator l_iterator = l_commerceItems.iterator(); l_iterator.hasNext();)
            {
                CommerceItemImpl l_commerceItem = (CommerceItemImpl) l_iterator.next();

                l_idSku = l_commerceItem.getCatalogRefId();

                /*
                 * On récupère le délai de livraison
                 */
                try
                {
                    l_sku = getCatalogRepository().getItem(l_idSku, CastoConstantes.DESCRIPTEUR_CASTO_SKU);

                    // MC 080226 : si on vient du pré-panier alors on
                    // utilise
                    // l'InventoryRepository

                    l_delai = (int) getDispoManager().calculDelai(l_sku, l_commerceItem.getQuantity(), "panier");
                }
                catch (RepositoryException l_repositoryException)
                {
                    l_delai = 0;

                    logError("CastoOrderTools.enregistrerDelaisLivraison : prob getItem : " + l_idSku + ", "
                            + l_repositoryException);
                }

                /*
                 * On met à jour le commerceItem
                 */
                l_commerceItem.setPropertyValue("GeneralementLivreEnHeures", new Integer(l_delai));
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.enregistrerDelaisLivraison");
        }
    }
    /* Code Review*/
    /**
     * Méthode qui permet d'affecter le magasin d'origine de la commande.
     * 
     * @param a_order
     *            La command en cours.
     * @param a_profile
     *            Le profil utilisateur.
     * @param a_request
     *            La requête HTTP.
     */
    public void affecterMagasinProfile(CastoOrder a_order, Profile a_profile, DynamoHttpServletRequest a_request)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile).");
        }

        if (null == a_profile || null == a_order)
        {
            logError("CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile) : profil ou commande null.");
        }
        else
        {
            Object l_obj = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
            RepositoryItem l_magasinProfile;

            if (null == l_obj)
            {
                /*
                 * Note : S'il n'y a pas de magasin dans l'objet Profile, on lui
                 * affecte.
                 */
                if (isLoggingInfo())
                {
                    logInfo("Propriete 'magasin' du profil est nulle, Castorama Direct va lui être affecte.");
                }

                try
                {
                    l_magasinProfile = getMagasinRepository().getItem(CastoConstantesDefense.CASTORAMA_DIRECT,
                            "magasin");

                    if (null != l_magasinProfile)
                    {
                        a_profile.setPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN,
                                l_magasinProfile);
                        if(isLoggingDebug())
                        {
                            logDebug("CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile) " + l_magasinProfile.getRepositoryId());
                        }
                    }
                    else
                    {
                        logError("Impossible d'affecter un magasin au profil passé en parametre.");
                    }
                }
                catch (RepositoryException l_re)
                {
                    logError("CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile) : " + l_re.toString());

                    l_magasinProfile = null;
                }
            }
            else
            {
                l_magasinProfile = (RepositoryItem) l_obj;
                if(isLoggingDebug())
                {
                    logDebug("CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile) " + l_magasinProfile.getRepositoryId());
                }
            }
        }


        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMagasinProfile(CastoOrder, Profile).");
        }
    }/* Code Review*/

    /**
     * Méthode qui indique si une commande possède ou non un Shipping Group doté
     * la méthode de shipping demandée.
     * 
     * @param a_order
     *            La commande.
     * @param a_shippingMethod
     *            La méthode de shipping recherchée.
     * 
     * @return Le Shipping group doté de la bonne méthode de Shipping s'il
     *         existe, null sinon.
     */
    public ShippingGroup containsShippingMethod(Order a_order, String a_shippingMethod)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.containsShippingMethod(Order, String).");
        }

        ShippingGroup l_ret = null;
        ShippingGroup l_shippingGroup;

        if (null != a_order)
        {
            synchronized (a_order)
            {
                for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups.hasNext();)
                {
                    l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

                    if (l_shippingGroup.getShippingMethod().equals(a_shippingMethod))
                    {
                        l_ret = l_shippingGroup;
                        break;
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.containsShippingMethod(Order, String).");
        }

        return l_ret;
    }
    
    /*Code Review*/    
    /**
     * .
     * Méthode qui met à jour avec les infos suivantes : dateMAJprofil, typeMAJprofil
     * @param a_manager a_manager
     * @param a_profile a_profile
     * @return MutableRepositoryItem l_profile
     */
    public void majProfile(CastoProfileManager a_manager, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.majProfile");
        }
        
            // BDDCC G.V
            try
            {
                if ((MutableRepositoryItem) a_manager.getUserRepository().getItem(a_profile.getRepositoryId(), "user") != null)
                {
                    MutableRepositoryItem l_profile = (MutableRepositoryItem) a_manager.getUserRepository().getItem(
                            a_profile.getRepositoryId(), "user");

                    /*
                     * si le compte est modifié le jour de sa creation, alors on
                     * laisse typeMAJprofil = 0, sinon typeMAJprofil =1
                     */
                    if (l_profile.getPropertyValue("registrationDate") != null)
                    {
                        Date l_registrationDate = (Date) l_profile.getPropertyValue("registrationDate");
                        SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date l_d = java.sql.Date.valueOf(l_sdf.format(l_registrationDate));
                        if (l_d.compareTo(java.sql.Date.valueOf(l_sdf.format(new Date()))) < 0)
                            l_profile.setPropertyValue("typeMAJprofil", MODIFICATION);
                    }
                    else
                    {
                        l_profile.setPropertyValue("typeMAJprofil", MODIFICATION);
                        if (isLoggingError())
                        {
                            logError("**** CastoCartModifierFormHandler.handleMoveToOrderCommit() --> RegistrationDate nul ****");
                        }
                    }
                    l_profile.setPropertyValue("dateMAJprofil", new Date());

                    ((MutableRepository) a_manager.getUserRepository()).updateItem(l_profile);
                }
                else
                {
                    if (isLoggingError())
                    {
                        logError("**** CastoCartModifierFormHandler.handleMoveToOrderCommit() --> RepositoryItem nul ****");
                    }
                }
            }
            catch (RepositoryException l_re)
            {
                if (isLoggingError())
                {
                    logError("erreur lors de mise a jour profil BDDCC", l_re);
                }
            }
   
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.majProfile");
        }
        
    }
    /*Code Review*/

    /**
     * .
     * Méthode qui met à jour l'identifiant de profil d'une commande, de l'état de la commande pour la faire persister avant le paiement,
     * et un libellé d'attente de paiement.
     * 
     * @param a_order
     *            a_order
     * @param a_profile
     *           a_profile
     * @param a_sessionId 
     *           a_sessionId
     * 
     */
    /* Code Review */public void updateOrderPreSofinco(Order a_order, RepositoryItem a_profile, String a_sessionId)    /* Code Review */
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.updateOrderPreSofinco(String, String).");
        }
            
        if(null!=a_order)
        {
            synchronized (a_order)
            {
                boolean l_rollback = false;
                TransactionDemarcation l_transactionDemarcation = new TransactionDemarcation();
                try
                {
                    l_transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);
                    try
                    {
            /* Code Review */
                        CastoOrder l_castoOrder = (CastoOrder)a_order;
                        l_castoOrder.setProfileId(a_profile.getRepositoryId());
                        l_castoOrder.setStateDetail("waitingForPaymentInfo");
                        l_castoOrder.setJSessionID(a_sessionId);
                        getOrderManager().updateOrder(a_order);
            /* Code Review */
                    }
                    catch (CommerceException l_e)
                    {
                        logError("CastoOrderTools.updateOrderPreSofinco : " + l_e.toString());
                        logError(l_e);
                        l_rollback = true;
                    }
                    finally
                    {
                        if (isLoggingInfo())
                        {
                            logInfo("CastoOrderTools.updateOrderPreSofinco(String, String) :  "+ l_rollback);
                        }
    
                        l_transactionDemarcation.end(l_rollback);
                    }
                }
                catch (TransactionDemarcationException l_tde)
                {
                    logError("CastoOrderTools.updateOrderPreSofinco(String, String) : " + l_tde.toString());
                    logError(l_tde);
                }
            }//Fin Synchronisation
        }  

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.updateOrderPreSofinco(String, String).");
        }
    }

    /**
     * Méthode qui renvoie le n° de version d'une command en base.
     * 
     * @param a_orderId
     *            Identifiant de la commande.
     * 
     * @return Le n° de version de la commande, -1 en cas d'échec.
     */
    public int getOrderRepositoryItemVersion(String a_orderId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.getOrderRepositoryItemVersion(String)");
        }

        int l_idVersion;

        if (null == a_orderId)
        {
            logError("CastoOrderTools.getOrderRepositoryItemVersion(String) : l'identifiant de commande est null.");

            l_idVersion = CastoConstantes.NEG;
        }
        else
        {
            Repository l_orderRepository = getOrderRepository();

            if (null == l_orderRepository)
            {
                logError("CastoOrderTools.getOrderRepositoryItemVersion(String) : le repository des commandes est null.");

                l_idVersion = CastoConstantes.NEG;
            }
            else
            {
                try
                {
                    RepositoryItem l_commande = l_orderRepository.getItem(a_orderId,
                            CastoConstantes.DESCRIPTEUR_COMMANDE);

                    if (null == l_commande)
                    {
                        logError("CastoOrderTools.getOrderRepositoryItemVersion(String) : la commande n° " + a_orderId
                                + " est introuvable.");

                        l_idVersion = CastoConstantes.NEG;
                    }
                    else
                    {
                        Object l_version = l_commande.getPropertyValue(VERSION);
                        if (null == l_version)
                        {
                            logError("CastoOrderTools.getOrderRepositoryItemVersion(String) : propriété 'version' introuvable.");

                            l_idVersion = CastoConstantes.NEG;
                        }
                        else
                        {
                            l_idVersion = Integer.valueOf(l_version.toString()).intValue();
                        }
                    }
                }
                catch (RepositoryException l_re)
                {
                    logError("CastoOrderTools.getOrderRepositoryItemVersion(String) : erreur lors de la récupération de la commande n° : "
                            + a_orderId);

                    l_idVersion = CastoConstantes.NEG;
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.getOrderRepositoryItemVersion(String)");
        }

        return l_idVersion;
    }


    /**
     * Méthode qui affecte les montants de PFL et PFT d'une commande CD.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passée, false sinon.
     */
    public boolean affecterMontantsPFLPFT(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantsPFLPFT(CastoOrder)");
        }

        boolean l_ret;

        if (null == a_order)
        {
            logError("CastoOrderTools.affecterMontantsPFLPFT(CastoOrder) : objet order null.");

            l_ret = false;
        }
        else
        {
        /* Code Review */
            // On récupère le coefficient de remise appliqué

            ShippingGroup l_shippingGroup;
            ShippingPriceInfo l_priceInfo;

            for (Iterator l_iterator = a_order.getShippingGroups().iterator(); l_iterator.hasNext();)
            {
                l_shippingGroup = (ShippingGroup) l_iterator.next();

                if (CastoConstantesDefense.LIVRAISON_A_DOMICILE.equals(l_shippingGroup.getShippingMethod()))
                {
                    // On obtient le price info
                    l_priceInfo = l_shippingGroup.getPriceInfo();

                    double l_rawShipping = l_priceInfo.getRawShipping();
                    double l_amount = l_priceInfo.getAmount();

                    if (l_rawShipping != l_amount)
                    {
                        // On calcule le pourcentage de remise
                        double l_pourcentageRemise = (l_amount * CastoConstantes.CENT / l_rawShipping)
                                / CastoConstantes.CENT;

                        // On applique ce pourcentage

                        a_order.setMontantPFLTotal(a_order.getMontantPFLTotal() * l_pourcentageRemise);
                        a_order.setMontantPFTTotal(a_order.getMontantPFTTotal() * l_pourcentageRemise);
                    }

                    // Fin de boucle
                    break;
                }
            }                
            l_ret = true;
        /* Code Review */
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantsPFLPFT(CastoOrder)");
        }

        return l_ret;
    }

    /**
     * Méthode qui affecte le montant des frais de livraison non remisé.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passée, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantFraisLivraisonNonRemise(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisLivraisonNonRemise(CastoOrder)");
        }

        boolean l_ret = true;

        // 1 - On parcourt les shipping group de la commande afin d'en trouver 1
        // qui soit en LAD

        ShippingGroup l_shippingGroup;

        for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups.hasNext();)
        {
            l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

            if (CastoConstantesDefense.LIVRAISON_A_DOMICILE.equals(l_shippingGroup.getShippingMethod()))
            {
                // Obtention du priceInfo...

                ShippingPriceInfo l_priceInfo = l_shippingGroup.getPriceInfo();

                if (null == l_priceInfo)
                {
                    logError("CastoOrderTools.affecterMontantFraisLivraisonNonRemise(CastoOrder) : price info null.");

                    l_ret = false;
                }
                else
                {
                    // ...puis on prend le 1er ajustement (i.e celui du
                    // montant initial des frais de livraison).
                    List l_adjustments = l_priceInfo.getAdjustments();

                    if (null == l_adjustments)
                    {
                        logError("CastoOrderTools.affecterMontantFraisLivraisonNonRemise(CastoOrder) : pas d'ajustements trouvés.");
                    }
                    else
                    {
                        if (0 != l_adjustments.size())
                        {
                            PricingAdjustment l_ajustement = (PricingAdjustment) l_adjustments.iterator().next();

                            double l_montant = l_ajustement.getTotalAdjustment();

                            if (a_order.isPayeCarteAtout())
                            {
                                // Si paiement par carte l'atout, on rajoute les
                                // 30% déjà enlevés

                                double l_coef = (CastoConstantes.CENT - getPourcentageAtout()) / CastoConstantes.CENT;

                                a_order.setMontantFraisLivraisonNonRemise(l_montant / l_coef);
                            }
                            else
                            {
                                a_order.setMontantFraisLivraisonNonRemise(l_montant);
                            }
                        }
                    }
                }

                // Fin de la boucle
                break;
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisLivraisonNonRemise(CastoOrder)");
        }

        return l_ret;
    }

    /**
     * Méthode qui affecte le montant, non remisé, des frais de livraison à
     * l'étage.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantFraisMonteeEtageNonRemise(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisMonteeEtageNonRemise(CastoOrder)");
        }

        boolean l_ret;

        ParametresFraisLivraisonMagasin l_prm = (ParametresFraisLivraisonMagasin) getParametresLivraisons().get(
                a_order.getOrigineMagasin().getRepositoryId());

        if (null == l_prm)
        {
            if (isLoggingError())
            {
                logError("CastoOrderTools.affecterMontantFraisMonteeEtageNonRemise(CastoOrder) : "
                    + "aucun paramètre pour les frais de livraison pour ce magasin.");
            }

            l_ret = false;
        }
        else
        {
            a_order.setMontantFraisMonteeEtageNonRemise(l_prm.getFraisLivraisonEtage());

            l_ret = true;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisMonteeEtageNonRemise(CastoOrder)");
        }

        return l_ret;
    }

    /* Code Review *//**
     * M�thode qui va affecter le montant total de la commande.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour se passe bien, false sinon.
     */
    public boolean affecterTotalCommande(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.processor.CastoAffecterMontants.affecterTotalCommande(CastoOrder)");
        }

        OrderPriceInfo l_priceInfo = a_order.getPriceInfo();
        boolean l_ret = true;
        if (null == l_priceInfo)
        {
            logError("CastoAffecterMontants.CastoAffecterMontants(CastoOrder) : price info null.");
            l_ret =  false;
        }
        else
        {
            a_order.setMontantTotalCommandeTTC(l_priceInfo.getTotal());
        }
        
        if (isLoggingDebug())
        {
            logDebug("CastoAffecterMontants(CastoOrder)"+l_ret);
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.processor.CastoAffecterMontants(CastoOrder)");
        }
        return l_ret;
    }/* Code Review */
    
    /**
     * Méthode qui affecte le montant des remises sur les frais de livraison.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantRemiseLivraisonEtMonteeEtage(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantRemiseLivraisonEtMonteeEtage(CastoOrder)");
        }

        boolean l_ret = true;

        ShippingGroup l_shippingGroup;

        for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups.hasNext();)
        {
            l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

            if (CastoConstantesDefense.LIVRAISON_A_DOMICILE.equals(l_shippingGroup.getShippingMethod()))
            {
                // Obtention du priceInfo...

                ShippingPriceInfo l_priceInfo = l_shippingGroup.getPriceInfo();

                if (null == l_priceInfo)
                {
                    logError("CastoOrderTools.affecterMontantRemiseLivraisonEtMonteeEtage(CastoOrder) : price info null.");

                    l_ret = false;
                }
                else
                {
                    a_order.setMontantRemiseLivraisonEtMonteeEtage(a_order.getMontantFraisLivraisonNonRemise()
                            - l_priceInfo.getAmount());
                }

                // Fin de la boucle
                break;
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantRemiseLivraisonEtMonteeEtage(CastoOrder)");
        }

        return l_ret;
    }

    /**
     * Méthode qui affecte le montant des frais de préparation non remisé.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantFraisDePreparationNonRemise(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisDePreparationNonRemise(CastoOrder)");
        }

        boolean l_ret = true;

        ShippingGroup l_shippingGroup;

        for (Iterator l_shippingGroups = a_order.getShippingGroups().iterator(); l_shippingGroups.hasNext();)
        {
            l_shippingGroup = (ShippingGroup) l_shippingGroups.next();

            if (CastoConstantesDefense.MISE_A_DISPOSITION.equals(l_shippingGroup.getShippingMethod()))
            {
                // Obtention des frais

                ParametresFraisPreparationMagasin l_prm = (ParametresFraisPreparationMagasin) getFraisMAD().get(
                        a_order.getOrigineMagasin().getRepositoryId());

                if (null == l_prm)
                {
                    logError("CastoOrderTools.affecterMontantFraisDePreparationNonRemise(CastoOrder) : "
                            + "aucun frais de préparation configuré pour ce magasin.");

                    l_ret = false;
                }
                else
                {
                    a_order.setMontantFraisDePreparationNonRemise(l_prm.getMontantForfaitaire());
                }

                // Fin de la boucle
                break;
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisDePreparationNonRemise(CastoOrder)");
        }

        return l_ret;
    }

    /**
     * Méthode qui affecte le montant remisé des frais de préparation de la
     * commande.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantFraisDePreparationRemise(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisDePreparationRemise(CastoOrder)");
        }

        boolean l_ret = true;

        // Pour l'instant, tjrs 0. Aucune règle de gestion n'est appliquée sur
        // ces frais.

        a_order.setMontantFraisDePreparationRemise(0);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantFraisDePreparationRemise(CastoOrder)");
        }

        return l_ret;
    }

    /**
     * Méthode qui affecte le montant total des remises appliquées sur la
     * commande.
     * 
     * @param a_order
     *            La commande à modifier.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    /* Code Review */public/* Code Review */ boolean affecterMontantRemiseTotal(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantRemiseTotal(CastoOrder)");
        }

        boolean l_ret = true;
        double l_montant;

        // 1 - Montant de la remise au niveau des shipping group

        l_montant = a_order.getMontantRemiseLivraisonEtMonteeEtage();

        // 2 - Montant des remises au niveau des commerce items

        List l_commerceItems = a_order.getCommerceItems();
        CommerceItem l_item;
        ItemPriceInfo l_priceInfo;
        double l_remise;

        if (null != l_commerceItems)
        {
            for (Iterator l_iterator = l_commerceItems.iterator(); l_iterator.hasNext();)
            {
                // Obtention du commerce item

                l_item = (CommerceItem) l_iterator.next();

                // Obtention du price info

                l_priceInfo = l_item.getPriceInfo();

                // Calcul de la remise

                l_remise = l_priceInfo.getRawTotalPrice() - l_priceInfo.getAmount();

                // Ajout de la remise au montant total des remises

                l_montant += l_remise;
            }
        }

        // 3 - Montant des remises au niveau de la commande

        OrderPriceInfo l_orderPriceInfo = a_order.getPriceInfo();

        if (null == l_orderPriceInfo)
        {
            logError("CastoOrderTools.affecterMontantRemiseTotal(CastoOrder) : price info de la commande null.");

            l_ret = false;
        }
        else
        {
            // On parcourt les ajustements

            PricingAdjustment l_ajustement;
            boolean l_isFirst = true;

            for (Iterator l_iterator = l_orderPriceInfo.getAdjustments().iterator(); l_iterator.hasNext();)
            {
                l_ajustement = (PricingAdjustment) l_iterator.next();

                if (l_isFirst)
                {
                    l_isFirst = false;
                }
                else
                {
                    l_montant += Math.abs(l_ajustement.getTotalAdjustment());
                }
            }
        }

        // On fixe le montant total des remises

        a_order.setMontantRemiseTotal(l_montant);

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantRemiseTotal(CastoOrder)");
        }

        return l_ret;
    }
    /* Code Review */    
    /**
     * Methode qui permet d'affecter le magasin d'origine de la commande.
     * 
     * @param a_order
     *            La commande en cours.
     * @param a_profile
     *            a_profile           
     *            
     */
    public void affecterMagasinOrigineCommande(CastoOrder a_order, RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMagasinOrigineCommande(CastoOrder).");
        }

        RepositoryItem l_magasin;
        RepositoryItem l_magasinCommande;
        
        if (null == a_order)
        {
            logError("CastoOrderTools.affecterMagasinOrigineCommande(CastoOrder) : commande nulle.");
        }
        else if(null != a_order)
        {
            try
            {
                l_magasinCommande = a_order.getOrigineMagasin();
                if(null==l_magasinCommande)
                {
                    Repository l_magRepo = getMagasinRepository();
                    if(null!=l_magRepo)
                    {
                        if(null!=a_profile)
                        {
                            RepositoryItem l_magPro = (RepositoryItem)a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
                            if(null!=l_magPro)
                            {
                                l_magasin = l_magRepo.getItem(l_magPro.getRepositoryId(), CastoConstantes.DESCRIPTEUR_MAGASIN);
                            }
                            else
                            {
                                l_magasin = l_magRepo.getItem(CastoConstantesDefense.CASTORAMA_DIRECT, CastoConstantes.DESCRIPTEUR_MAGASIN);
                            }
                        }
                        else
                        {
                            l_magasin = l_magRepo.getItem(CastoConstantesDefense.CASTORAMA_DIRECT, CastoConstantes.DESCRIPTEUR_MAGASIN);
                        }
                        if (null != l_magasin)
                        {
                            if (isLoggingDebug())
                            {
                                logDebug("order != null, Castorama Direct va etre affecte a la cde.");
                            }
                            a_order.setOrigineMagasin(l_magasin);
                        }
                        else
                        {
                            if (isLoggingDebug())
                            {
                                logDebug("getMagasinRepository().getItem(999,magasin)==null");
                            }
                        }
                    }
                    else
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("getMagasinRepository()==null");
                        }
                    }
                }
                else
                {
                    if (isLoggingDebug())
                    {
                        logDebug("La commande a deja un magasin d origine : "+l_magasinCommande.getRepositoryId());
                    }
                }
            }
            catch (RepositoryException l_re)
            {
                logError("CastoOrderTools.affecterMagasinOrigineCommande(CastoOrder) : " + l_re.toString());
                logError(l_re);
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMagasinOrigineCommande(CastoOrder).");
        }
    }
    
    /**
     * Met à jour certaines infos de la commande pour les paiements de type CB.
     * 
     * @param a_order
     *            Ia_order
     * @param a_infos
     *            Informations à ajouter.
     */
    public void updateOrderCB(CastoOrder a_order, Map a_infos)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.updateOrderCB().");
        }

        /*
         * Logging des param�tres de la m�thode.
         */
        if (isLoggingDebug())
        {
            logDebug("====================================================================================");
            logDebug("| order : " + a_order.getId());
            if (null != a_infos)
            {
                String l_key;
                for (Iterator l_keys = a_infos.keySet().iterator(); l_keys.hasNext();)
                {
                    l_key = (String) l_keys.next();

                    logDebug("| " + l_key + " : " + a_infos.get(l_key));
                }
            }
            logDebug("====================================================================================");
        }
        
        if(null!=a_order)
        {
            // Mise a jour des informations de la commande
            a_order.setBOState((String) a_infos.get(PROPERTY_BO_STATE));
            a_order.setBOStateDetail((String) a_infos.get(PROPERTY_BO_STATE_DETAIL));
            a_order.setProfileId((String) a_infos.get(PROFILE_ID));
            
            // Mise a� jour des informations
            List l_paymentsGroups = (List) a_order.getPaymentGroups();
            CastoPaymentGroupImpl l_paymentGroup = null;
            String l_paymentMethod = null;
            Object l_cbType = null;
            Object l_cbNumber = null;
            for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
            {
                l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
                l_paymentMethod = (String) l_paymentGroup.getPropertyValue(PAYMENT_METHOD);
                if (CREDIT_CARD.equals(l_paymentMethod)
                        || CastoCartModifierFormHandler.PAIEMENT_PAR_TELEPHONE.equals(l_paymentMethod)
                        || CastoCartModifierFormHandler.PAIEMENT_PAR_VIREMENT.equals(l_paymentMethod))
                {
                    //updateMontantPaymentGroupChequeCadeau(a_order, l_paymentGroup);
                    
                    l_cbType = a_infos.get(PROPERTY_PAYMENT_MEANS);
                    if(null!=l_cbType)
                    {
                        l_paymentGroup.setCreditCardType(l_cbType.toString());
                    }
                    else
                    {
                        if(isLoggingDebug())
                        {
                            logDebug("PROPERTY_PAYMENT_MEANS==null");
                        }
                    }
                    
                    l_cbNumber = a_infos.get(PROPERTY_CARD_NUMBER);
                    if(null!=l_cbNumber)
                    {
                        l_paymentGroup.setCreditCardNumber((l_cbNumber.toString()).replaceAll("\\.", "XXXXXXXXXX"));
                    }
                    else
                    {
                        if(isLoggingDebug())
                        {
                            logDebug("PROPERTY_CARD_NUMBER==null");
                        }
                    }
                    
                    break;
                }
            }            
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.updateOrderCB().");
        }
    }
    
    /**
     * Met a jour certaines infos de la commande pour les paiements de type
     * a_paymentMethod.
     * 
     * @param a_order
     *            Identifiant de la commande a mettre a jour.
     * @param a_paymentMethod
     *            methode de paiement           
     */
    public void updateOrderPaymentMethod(CastoOrder a_order, String a_paymentMethod)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.updateOrderPaymentMethod().");
        }

        if (isLoggingDebug())
        {
            logDebug("Modification de la commande n : " + a_order.getId());
        }

        // Mise a jour du moyen de paiement
        List l_paymentsGroups = (List)  a_order.getPaymentGroups();
        for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
        {
            //MutableRepositoryItem l_paymentGroup = (MutableRepositoryItem) l_iterator.next();
            CastoPaymentGroupImpl l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();
            if (isLoggingDebug())
            {
                logDebug("Commande n : " + a_order.getId()+TIRET+l_paymentGroup.getId());
            }
            l_paymentGroup.setPaymentMethod(a_paymentMethod);
            updateOrderState(a_paymentMethod, a_order, l_paymentGroup);
            //updateMontantPaymentGroupChequeCadeau(a_order, l_paymentGroup);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.updateOrderPaymentMethod().");
        }
    }
    
    /**
     * M�thode qui met � jour le statut d'une commande en fonction du moyen de
     * paiement employ�.
     * 
     * (J.F : met �galement � vide certains champs qui ne concernent que les
     * paiements par CB).
     * 
     * @param a_paymentMethod
     *            Le moyen de paiement employ�.
     * @param a_order
     *            La commande � mettre � jour.
     * @param a_paymentGroup
     *            Le PaymentGroup associ� � la commande.
     */
    private void updateOrderState(String a_paymentMethod, CastoOrder a_order, CastoPaymentGroupImpl a_paymentGroup)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.commande.CastoOrderTools.updateOrderState().");
        }

        boolean l_supInfos = false;
        if (isLoggingDebug())
        {
            logDebug("CastoOrderTools.updateOrderState() : "+a_paymentMethod+ TIRET+ a_order+TIRET+ a_paymentGroup);
        }
        if (CastoCartModifierFormHandler.PAIEMENT_PAR_CHEQUE.equals(a_paymentMethod))
        {
            a_order.setStateAsString(PENDING_CHEQUE);
            a_order.setBOState(PENDING_CHEQUE);
            l_supInfos = true;
        }
        else if (CastoCartModifierFormHandler.PAIEMENT_PAR_TELEPHONE.equals(a_paymentMethod))
        {
            a_order.setStateAsString(PENDING_CALL_CENTER);
            a_order.setBOState(PENDING_CALL_CENTER);
            l_supInfos = true;
        }
        else if (CastoCartModifierFormHandler.PAIEMENT_PAR_VIREMENT.equals(a_paymentMethod))
        {
            a_order.setStateAsString(PENDING_VIREMENT);
            a_order.setBOState(PENDING_VIREMENT);
            l_supInfos = true;
        }
        else if (Boolean.valueOf(a_order.getPropertyValue(PAIMENT_CHEQUE_CADEAU).toString()).booleanValue())
        {
            a_order.setStateAsString(PENDING_CHEQUE_CADEAU);
            a_order.setBOState(PENDING_CHEQUE_CADEAU);
            l_supInfos = true;
        }

        if (l_supInfos)
        {
            a_paymentGroup.setCreditCardNumber("");
            a_paymentGroup.setCreditCardType("");
            a_paymentGroup.setExpirationMonth("");
            a_paymentGroup.setExpirationYear("");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.commande.CastoOrderTools.updateOrderState().");
        }
    }

    /**
     * 
     * @param a_order
     *            La commande.
     * @param a_paymentGroup
     *            Le PaymentGroup.
     */
    private void updateMontantPaymentGroupChequeCadeau(CastoOrder a_order,
            CastoPaymentGroupImpl a_paymentGroup)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.updateMontantPaymentGroupChequeCadeau().");
        }

        if (Boolean.valueOf(a_order.getPropertyValue(PAIMENT_CHEQUE_CADEAU).toString()).booleanValue())
        {
            double l_montant = ((Double) a_paymentGroup.getPropertyValue(PROPERTY_AMOUNT)).doubleValue();
            double l_montantChq = ((Double) a_order.getPropertyValue("montantChequeCadeau")).doubleValue();

            if (l_montantChq > l_montant)
            {
                a_paymentGroup.setAmount(new Double(0));
                a_paymentGroup.setAmountAuthorized(new Double(0));
            }
            else
            {
                a_paymentGroup.setAmount(new Double(l_montant - l_montantChq));
                a_paymentGroup.setAmountAuthorized(new Double(l_montant - l_montantChq));
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.updateMontantPaymentGroupChequeCadeau().");
        }
    }/* Code Review */
    
        /**
     * .
     * Méthode qui sauvegarde la commande sur le panier recap
     * 
     * @param a_order
     *            La commande à sauvegarder.
     * @param a_profile
     *            Le profile contenant des informations.
     * 
     * @return True si la mise à jour s'est bien passé, false sinon.
     */
    public boolean sauvegarderCommande(CastoOrder a_order, Profile a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile)");
        }

        boolean l_ret = true;
        if(null!=a_order)
        {
            if(null!=a_profile)
            {
                /*try
                {*/
                    if(isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile) : Debut de la sauvegarde");
                    }
                    
                    /* Save des cce items */
                    Set l_cceItemsBackUp = backupCceItems(a_order);
                    
                    /* Save de la cde */
                    backupCde(a_order, a_profile, l_cceItemsBackUp);
                    
                    if(isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile) : Fin de la sauvegarde");
                    }
                    
                    /* On enregistre en base la cde -> test pour les cdes inexistantes */
                    /*if(isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile) : Debut de la methodeOrderManager.addOrder()");
                    }
                    a_order.setStateAsString("waitingForPaymentInfos");
                    getOrderManager().addOrder(a_order);
                    if(isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile) : Fin de la methodeOrderManager.addOrder()");
                    }
                }
                catch (CommerceException l_cceex)
                {
                    logError(l_cceex);
                    l_ret=false;
                }*/
            }
            else
            {
                if(isLoggingDebug())
                {
                    logDebug("null==a_profile");
                }
            }
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("null==a_order");
            }
        }    
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.affecterMontantsBackup(CastoOrder, Profile)");
        }

        return l_ret;
    }
    
    /**
     * .
     * @param a_order cde à saver
     * @return lister cce à saver
     */
    private Set backupCceItems(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.backupCceItems(CastoOrder, Profile)");
        }
        
        Set l_cceItemsBackUp = null;
        MutableRepository l_orderBackupRepo = (MutableRepository)getOrderBackupRepository();
        if(null!=l_orderBackupRepo)
        {
            List l_cceItems = a_order.getCommerceItems();
            if(null!=l_cceItems && l_cceItems.size()>0)
            {
                MutableRepositoryItem l_cceBackup = null;
                boolean l_creationCce = false;
                CommerceItemImpl l_cceItem = null;
                try
                {
                    l_cceItemsBackUp = new HashSet();
                    for(Iterator l_it=l_cceItems.iterator(); l_it.hasNext();)
                    {
                        l_cceItem = (CommerceItemImpl)l_it.next();
                        l_cceBackup = (MutableRepositoryItem)l_orderBackupRepo.getItem(l_cceItem.getId(), COMMERCE_ITEM_BACKUP);
                        if(null==l_cceBackup)
                        {
                            l_cceBackup = l_orderBackupRepo.createItem(l_cceItem.getId(), COMMERCE_ITEM_BACKUP);
                            l_creationCce = true;
                        }
                        else
                        {
                            if(isLoggingDebug())
                            {
                                logDebug("CastoOrderTools.backupCde(CastoOrder, Profile, Set a_cceItemsBackUp)(CastoOrder, Profile) " +
                                        ": "+l_cceItem.getId()+" deja cree");
                            }
                        }
                        
                        /* Set du code article du cce item*/
                        String l_codeArticle = (String)l_cceItem.getCatalogRefId();
                        if(null!=l_codeArticle)
                            l_cceBackup.setPropertyValue("codeArticle", l_codeArticle);
                        
                        /* Set du cdeRef du cce item*/
                        String l_cdeRef = (String)a_order.getId();
                        if(null!=l_cdeRef)
                            l_cceBackup.setPropertyValue("cdeRef", l_cdeRef);
                        
                        /* Set de la qte du cce item*/
                        Long l_qte = new Long(l_cceItem.getQuantity());
                        if(null!=l_qte)
                            l_cceBackup.setPropertyValue("quantite", l_qte);
                        
                        /* Set du prix de la ligne article du cce item*/
                        Double l_pxLigne = new Double(l_cceItem.getPriceInfo().getAmount());
                        if(null!=l_pxLigne)
                            l_cceBackup.setPropertyValue("prixLigneArticle", l_pxLigne);
                        
                        /* si c est une creation de cce item alors addItem sinon updateItem */
                        if(l_creationCce)
                            l_orderBackupRepo.addItem(l_cceBackup);
                        else
                            l_orderBackupRepo.updateItem(l_cceBackup);
                        
                        /* Ajout du cce item backupe dans la liste des cce items de la cde backupe */
                        l_cceItemsBackUp.add(l_cceBackup);
                        
                        /* On set la creation a false pour le prochain cce item a sauvegarder */
                        l_creationCce = false;
                    }
                    
                }
                catch(RepositoryException l_rex)
                {
                    logError(l_rex);
                }
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("l_cceItems==null  || l_cceItems.size()==0");
                }  
            }        
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("null==getOrderBackupRepository()");
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.backupCceItems(CastoOrder, Profile)");
        }
        
        return l_cceItemsBackUp;
    }
    
    /**
     * .
     * @param a_order cde à saver
     * @param a_profile profil de la cde
     * @param a_cceItemsBackUp cces items de la cde
     * @return lister cce à saver
     */
    private void backupCde(CastoOrder a_order, Profile a_profile, Set a_cceItemsBackUp)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoOrderTools.backupCde(CastoOrder, Profile, Set a_cceItemsBackUp)");
        }
        
        MutableRepository l_orderBackupRepo = (MutableRepository)getOrderBackupRepository();
        if(null!=l_orderBackupRepo)
        {
            try
            {
                MutableRepositoryItem l_orderBackup = null;
                boolean l_creationBackup = false;
                l_orderBackup = (MutableRepositoryItem)l_orderBackupRepo.getItem(a_order.getId(), ORDER_BACKUP);
                if(null==l_orderBackup)
                {
                    l_orderBackup = l_orderBackupRepo.createItem(a_order.getId(), ORDER_BACKUP);
                    l_creationBackup = true;
                }
                else
                {
                    if(isLoggingDebug())
                    {
                        logDebug("CastoOrderTools.backupCde(CastoOrder, Profile, Set a_cceItemsBackUp) : "+a_order.getId()+" deja cree");
                    }
                }
                
                /* Date de passage de la commande*/
                l_orderBackup.setPropertyValue("dateCommande", new Date());
                
                /* set du total article de la commande */
                Double l_totalArticles = new Double(a_order.getPriceInfo().getAmount());
                if(null!=l_totalArticles)
                    l_orderBackup.setPropertyValue("totalArticles", l_totalArticles);
                
                /* set des frais de livraison de la commande */
                Double l_totalPFL = new Double(a_order.getMontantPFLTotal());
                if(null!=l_totalPFL)
                    l_orderBackup.setPropertyValue("FraisLivraison", l_totalPFL);
                
                /* set des frais de traitement de la commande */
                Double l_totalPFT = new Double(a_order.getMontantPFTTotal());
                if(null!=l_totalPFT)
                    l_orderBackup.setPropertyValue("FraisTraitement", l_totalPFT);
                
                /* set du total de la commande */
                Double l_total = new Double(a_order.getPriceInfo().getTotal());
                if(null!=l_total)
                    l_orderBackup.setPropertyValue("totalCommande", l_total);
                
                /* set du magasin d'origine de la commande */
                String l_origineMag = (String) a_order.getOrigineMagasin().getPropertyValue("nom");
                if(null!=l_origineMag)
                    l_orderBackup.setPropertyValue("origineMagasin", l_origineMag);
                
                /* set du login du client */
                String l_login = (String)a_profile.getPropertyValue("login");
                if(null!=l_login)
                    l_orderBackup.setPropertyValue("login", l_login);
                
                /* set du nom du client */
                String l_nom = (String)a_profile.getPropertyValue("lastName");
                if(null!=l_nom)
                    l_orderBackup.setPropertyValue("nom", l_nom);
                
                /* set du nom du client */
                String l_prenom = (String)a_profile.getPropertyValue("firstName");
                if(null!=l_prenom)
                    l_orderBackup.setPropertyValue("prenom", l_prenom);
                
                List l_paymentsGroups = (List) a_order.getPropertyValue(PAYEMENT_GROUPS);
                for (Iterator l_iterator = l_paymentsGroups.iterator(); l_iterator.hasNext();)
                {
                    MutableRepositoryItem l_paymentGroup = (MutableRepositoryItem) l_iterator.next();
                    
                    /* Set du moyen de paiement*/
                    String l_moyenPaiement = (String)l_paymentGroup.getPropertyValue("paymentMethod");
                    if(null!=l_moyenPaiement)
                        l_orderBackup.setPropertyValue("moyenPaiement", l_moyenPaiement);
                    
                    /* Set du telephone portable de l adresse de paiement*/
                    String l_telPortable = (String)l_paymentGroup.getPropertyValue("telPortable");
                    if(null!=l_telPortable)
                        l_orderBackup.setPropertyValue("telPortable", l_telPortable);
                    
                    /* Set du telephone fixe de l adresse de paiement*/
                    String l_telFixe = (String)l_paymentGroup.getPropertyValue("phoneNumber");
                    if(null!=l_telFixe)
                        l_orderBackup.setPropertyValue("telFixe", l_telFixe);
                }
                
                List l_hardGoodSg = (List) a_order.getShippingGroups();
                for (Iterator l_iterator = l_hardGoodSg.iterator(); l_iterator.hasNext();)
                {
                    ShippingGroupImpl l_hgGroup = (ShippingGroupImpl) l_iterator.next();
                    /* Sauvegarde Adresse Livraison */
                    String l_addressShippingAddress = (String)l_hgGroup.getPropertyValue("address1");
                    String l_postalCodeShippingAddress = (String)l_hgGroup.getPropertyValue("postalCode");
                    String l_cityShippingAddress = (String)l_hgGroup.getPropertyValue("city");
                    String l_countryShippingAddress = (String)l_hgGroup.getPropertyValue("country");
                    
                    if(null!=l_addressShippingAddress
                        && null!=l_postalCodeShippingAddress
                        && null!=l_cityShippingAddress
                        && null!=l_countryShippingAddress)
                    {
                        String l_res = l_addressShippingAddress
                            +", "+l_postalCodeShippingAddress
                            +" "+l_cityShippingAddress
                            +" "+l_countryShippingAddress;
                        l_orderBackup.setPropertyValue("adresseLibelleLiv",l_res );
                    }
                    break;
                }
                
                
                /* Sauvegarde Adresse Facturation */
                RepositoryItem l_billingAddress = (RepositoryItem) a_profile.getPropertyValue("billingAddress");
                String l_addressBillingAddress = (String)l_billingAddress.getPropertyValue("address1");
                String l_postalCodeBillingAddress = (String)l_billingAddress.getPropertyValue("postalCode");
                String l_cityBillingAddress = (String)l_billingAddress.getPropertyValue("city");
                String l_countryBillingAddress = (String)l_billingAddress.getPropertyValue("country");
                
                if(null!=l_addressBillingAddress
                    && null!=l_postalCodeBillingAddress
                    && null!=l_cityBillingAddress
                    && null!=l_countryBillingAddress)
                {
                    String l_res = l_addressBillingAddress
                        +", "+l_postalCodeBillingAddress
                        +" "+l_cityBillingAddress
                        +" "+l_countryBillingAddress;
                    l_orderBackup.setPropertyValue("adresseLibelleFactu",l_res );
                }
                
                if(null!=a_cceItemsBackUp && a_cceItemsBackUp.size()>0)
                    l_orderBackup.setPropertyValue("listCceItems", a_cceItemsBackUp);
                
                /* si c est une creation de cce item alors addItem sinon updateItem */
                if(l_creationBackup)
                    l_orderBackupRepo.addItem(l_orderBackup);
                else
                    l_orderBackupRepo.updateItem(l_orderBackup);
            }
            catch(RepositoryException l_rex)
            {
                logError(l_rex);
            }           
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("null==getOrderBackupRepository()");
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoOrderTools.backupCde(CastoOrder, Profile, Set a_cceItemsBackUp)");
        }
    }
}