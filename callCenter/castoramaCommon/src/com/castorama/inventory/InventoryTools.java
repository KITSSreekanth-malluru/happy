package com.castorama.inventory;

import java.util.ArrayList;
import java.util.List;

import atg.nucleus.Nucleus;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

/**
 * Manipulation d'informations de quantit� - stock.
 * 
 * @author ??.
 */
public class InventoryTools
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    /**
     * Constante pour désigner un article en quantité illimitée.
     */
    public static final int STOCK_INFINI = -1;

    /**
     * Constante pour désigner un article disponible.
     */
    public static final int DISPONIBLE = 0;

    /**
     * Constante pour désigner un article momentanément indisponible.
     */
    public static final int MOMENTANEMENT_INDISPONIBLE = 1;

    /**
     * Constante pour désigner un article épuisé.
     */
    public static final int EPUISE = 2;

    /**
     * Constante pour ??.
     */
    public static final String CHILD_SKUS = "childSKUs";

    /**
     * Constante pour ??.
     */
    public static final String PRODUCT = "product";

    /**
     * Constante pour l'instance unique de la classe.
     */
    public static final InventoryTools INSTANCE = (InventoryTools) Nucleus.getGlobalNucleus().resolveName(
            "/castorama/inventory/InventoryTools");

    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    protected Repository m_catalogRepository; // Catalog Repository

    protected OrderManager m_orderManager; // OrderManager

    private InventoryAdapter m_inventoryAdapter;

    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    /**
     * R�cup�ration de CatalogRepository.
     * 
     * @return Repository CatalogRepository
     */
    public Repository getCatalogRepository()
    {
        return m_catalogRepository;
    }

    /**
     * Modification de CatalogRepository.
     * 
     * @param a_catalogRepository
     *            ??.
     */
    public void setCatalogRepository(Repository a_catalogRepository)
    {
        m_catalogRepository = a_catalogRepository;
    }

    /**
     * R�cup�ration de OrderManager.
     * 
     * @return OrderManager OrderManager
     */
    public OrderManager getOrderManager()
    {
        return m_orderManager;
    }

    /**
     * Modification de OrderManager.
     * 
     * @param a_OrderManager
     *            OrderManager
     */
    public void setOrderManager(OrderManager a_OrderManager)
    {
        m_orderManager = a_OrderManager;
    }

    /**
     * Méthode qui renvoie une référence vers le gestionnaire d'inventaires.
     * 
     * @return Une référence vers le gestionnaire d'inventaires.
     */
    public InventoryAdapter getInventoryAdapter()
    {
        return m_inventoryAdapter;
    }

    /**
     * Méthode qui fixe la référence vers le gestionnaire d'inventaires.
     * 
     * @param a_inventoryAdapter
     *            La nouvelle référence vers le gestionnaire d'inventaires.
     */
    public void setInventoryAdapter(InventoryAdapter a_inventoryAdapter)
    {
        m_inventoryAdapter = a_inventoryAdapter;
    }

    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * R�cup�ration du singleton InventoryTools.
     * 
     * @return InventoryTools
     */
    public static InventoryTools getInstance()
    {
        return INSTANCE;
    }

    /**
     * R�cup�ration de la disponibilit� d'un article suivant sont �tat de
     * dispponibilit� en stock et sa propri�t� "enVente".
     * 
     * @param a_strSkuId
     *            Identifiant du sku.
     * 
     * @return int disponibilit�
     */
    public int checkAvailability(String a_strSkuId)
    {
        InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager();

        int l_bDisponibilite = DISPONIBLE; // stock infini par d�faut
        try
        {
            RepositoryItem l_Sku = (RepositoryItem) m_catalogRepository.getItem(a_strSkuId, "casto_sku");
            boolean l_bEnVente = ((Boolean) l_Sku.getPropertyValue("enVente")).booleanValue();
            boolean l_bAvailability = l_inventoryManager.queryAvailabilityStatus(a_strSkuId) != InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK;
            long l_lStockLevel = l_inventoryManager.queryStockLevel(a_strSkuId);

            if (!l_bAvailability)
            {
                l_bDisponibilite = EPUISE;
            }
            else
            {
                if (!l_bEnVente && l_lStockLevel == STOCK_INFINI)
                {
                    l_bDisponibilite = MOMENTANEMENT_INDISPONIBLE;
                }
                else
                {
                    l_bDisponibilite = DISPONIBLE;
                }
            }
        }
        catch (InventoryException l_exception)
        {
            // Trace.logError(this,e,".checkAvailibility SkuId="+a_strSkuId+" :
            // "+e.toString());
        }
        catch (RepositoryException l_repositoryException)
        {
            //
        }
        return l_bDisponibilite;
    }

    /**
     * Teste si la commande contient au moins un article contraint a une limite
     * de stock.
     * 
     * @param a_Order
     *            commande
     * @return boolean contient un article avec un stock insuffisant
     * @throws none
     */
    public boolean checkOrderAvecReservationStock(Order a_Order)
    {
        try
        {
            List l_SgCiRels = getShippingGroupCommerceItemRelationships(a_Order);
            int l_size = l_SgCiRels.size();
            InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager();

            for (int l_i = 0; l_i < l_size; l_i++)
            {
                ShippingGroupCommerceItemRelationship l_SgCiRel = (ShippingGroupCommerceItemRelationship) l_SgCiRels
                        .get(l_i);
                String l_strSkuId = l_SgCiRel.getCommerceItem().getCatalogRefId();
                long l_CiStockLevel = l_inventoryManager.queryStockLevel(l_strSkuId); // quantit�
                // en
                // stock
                // pour
                // l'article
                if (l_CiStockLevel >= 0)
                { // si le stock n'est pas infini
                    return true;
                }
            }
        }
        catch (InventoryException l_exception)
        {
            // Trace.logError(this,e,".checkOrderAvecReservationStock :
            // "+e.toString());
        }
        catch (CommerceException l_commerceException)
        {
            //
        }
        return false;
    }

    /**
     * Teste si tous les articles d'une commande(panier) sont disponibles pour
     * les quantit�s renseign�es.
     * 
     * @param a_Order
     *            commande
     * 
     * @return boolean contient un article avec un stock insuffisant
     */
    public boolean checkOrderCommerceItemsAvailability(Order a_Order)
    {
        try
        {
            List l_SgCiRels = getShippingGroupCommerceItemRelationships(a_Order);
            int l_size = l_SgCiRels.size();
            InventoryManager l_inventoryManager = getInventoryAdapter().getInventoryManager();

            for (int l_i = 0; l_i < l_size; l_i++)
            {
                ShippingGroupCommerceItemRelationship l_SgCiRel = (ShippingGroupCommerceItemRelationship) l_SgCiRels
                        .get(l_i);
                String l_strSkuId = l_SgCiRel.getCommerceItem().getCatalogRefId();
                long l_CiQuantity = l_SgCiRel.getCommerceItem().getQuantity(); // quantit�
                // demand�e
                // pour
                // l'article
                long l_CiStockLevel = l_inventoryManager.queryStockLevel(l_strSkuId); // quantit�
                // en
                // stock
                // pour
                // l'article
                if (l_CiStockLevel >= 0 && l_CiQuantity > l_CiStockLevel)
                { // si le stock n'est pas infini et insuffisant
                    return true;
                }
            }
        }
        catch (InventoryException l_exception)
        {
            // Trace.logError(this,e,".checkOrderCommerceItemsAvailability :
            // "+e.toString());
        }
        catch (CommerceException l_commerceException)
        {
            //
        }
        return false;
    }

    /**
     * Get a list of all the shippingGroupCommerceItemRelationships that are
     * contained within a particular order. This is done by getting the list of
     * commerce item relationships and shipping group relationships and then
     * taking the intersection of the two.
     * 
     * @param a_order
     *            the order whose relationships will be obtained
     * @return the list of shipping group commerce item relationships in the
     *         order
     * @exception CommerceException
     *                if an error occurs
     */
    protected List getShippingGroupCommerceItemRelationships(Order a_order) throws CommerceException
    {
        List l_ciRels = getOrderManager().getCommerceItemManager().getAllCommerceItemRelationships(a_order);
        List l_sgRels = getOrderManager().getShippingGroupManager().getAllShippingGroupRelationships(a_order);

        l_ciRels.retainAll(l_sgRels);

        return l_ciRels;
    }

    /**
     * R�cup�ration de la liste des skus dont le stock est disponible pour le
     * productId pass� en param�tre.
     * 
     * @param a_strProductId
     *            productId
     * @return Object[] list des skus
     * @throws none
     */
    public Object[] getListeSkusInStock(String a_strProductId)
    {

        List l_SkuInStockList = new ArrayList();

        try
        {
            RepositoryItem l_Product = getCatalogRepository().getItem(a_strProductId, PRODUCT);
            List l_SkuList = (List) l_Product.getPropertyValue(CHILD_SKUS);
            int l_size = l_SkuList.size();

            for (int l_i = 0; l_i < l_size; l_i++)
            {
                RepositoryItem l_Sku = (RepositoryItem) l_SkuList.get(l_i);
                int l_nAvailability = checkAvailability(l_Sku.getRepositoryId());
                if (l_nAvailability == InventoryTools.DISPONIBLE)
                {
                    l_SkuInStockList.add(l_Sku);
                }
            }
        }
        catch (RepositoryException l_exception)
        {
            // Trace.logError(this,e,".getListeSkusInStock : "+e.toString());
        }

        return (Object[]) l_SkuInStockList.toArray();
    }
}