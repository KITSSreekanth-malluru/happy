package com.castorama.inventory;

import atg.commerce.inventory.InventoryManager;
import atg.nucleus.ServiceMap;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;

import com.castorama.commande.CastoOrder;
import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesDefense;

/**
 * Adpater pour les différentes instances d'inventory manager.
 * 
 * @author Logica.
 * @version 0.1.
 */
public class InventoryAdapter extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    private static final String MANAGER_STRING = ", manager : ";

    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    private ServiceMap m_listInventoryManagers;

    private ServiceMap m_listCachedInventoryManagers;

    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    /**
     * Renvoie la liste des objets InventoryManager disponibles.
     * 
     * @return La liste des objets InventoryManager disponibles.
     */
    public ServiceMap getListInventoryManagers()
    {
        return m_listInventoryManagers;
    }

    /**
     * Fixe la liste des objets InventoryManager disponibles.
     * 
     * @param a_listInventoryManagers
     *            La nouvelle liste des objets InventoryManager disponibles.
     */
    public void setListInventoryManagers(ServiceMap a_listInventoryManagers)
    {
        m_listInventoryManagers = a_listInventoryManagers;
    }

    /**
     * Méthode qui renvoie la liste des CachedInventorymanager disponibles.
     * 
     * @return La liste des CachedInventorymanager disponibles.
     */
    public ServiceMap getListCachedInventoryManagers()
    {
        return m_listCachedInventoryManagers;
    }

    /**
     * Méthode qui fixe la liste des CachedInventorymanager disponibles..
     * 
     * @param a_listCachedInventoryManagers
     *            La nouvelle liste des CachedInventorymanager disponibles.
     */
    public void setListCachedInventoryManagers(ServiceMap a_listCachedInventoryManagers)
    {
        m_listCachedInventoryManagers = a_listCachedInventoryManagers;
    }

    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * Méthode qui renvoie l'inventory manager approprié en foncion des données
     * contenues dans le profil.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getInventoryManager()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.inventory.InventoryAdapter.getInventoryManager().");
        }

        String l_id = String.valueOf(CastoConstantesDefense.CASTORAMA_DIRECT_INT);

        InventoryManager l_manager = (InventoryManager) getListInventoryManagers().get(l_id);

        if (isLoggingDebug())
        {
            logDebug("InventoryAdapter.getInventoryManager() : id : " + l_id + MANAGER_STRING + l_manager);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.inventory.InventoryAdapter.getInventoryManager().");
        }

        return l_manager;
    }

    /**
     * Méthode qui renvoie l'inventory manager approprié en foncion des données
     * contenues dans le profil.
     * 
     * @param a_profile a_profile
     *            La commmande en cours.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getInventoryManager(RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(Profile).");
        }

        InventoryManager l_manager;

        if (null != a_profile)
        {
            Object l_magasin = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

            if (null != l_magasin)
            {
                String l_id = ((RepositoryItem) l_magasin).getRepositoryId();

                l_manager = (InventoryManager) getListInventoryManagers().get(l_id);

                if (isLoggingDebug())
                {
                    logDebug("InventoryAdapter.getInventoryManager(Profile) : id : " + l_id + MANAGER_STRING
                            + l_manager);
                }
            }
            else
            {
                logError("InventoryAdapter.getInventoryManager(Profile) : pas de magasin pour l'objet Profile fourni.");

                l_manager = null;
            }
        }
        else
        {
            logError("InventoryAdapter.getInventoryManager(Profile) : l'objet Profile fourni est null.");

            l_manager = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(Profile).");
        }

        return l_manager;
    }
    
    /**
     * Méthode qui renvoie l'inventory manager approprié en foncion des données
     * contenues dans le profil.
     * 
     * @param a_item
     *            La commmande en cours.
     * @param a_isOrder
     *            Indique si l'item passé en paramètre est de type profile ou
     *            order.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getInventoryManager(RepositoryItem a_item, boolean a_isOrder)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(RepositoryItem, boolean).");
        }

        InventoryManager l_manager;

        if (null != a_item)
        {
            try
            {
                if (a_isOrder)
                {
                    Object l_magasin = a_item.getPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ORIGINE_MAGASIN);

                    if (null != l_magasin)
                    {
                        String l_id = ((RepositoryItem) l_magasin).getRepositoryId();

                        l_manager = (InventoryManager) getListInventoryManagers().get(l_id);

                        if (isLoggingDebug())
                        {
                            logDebug("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : id : " + l_id
                                    + MANAGER_STRING + l_manager);
                        }
                    }
                    else
                    {
                        logError("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : pas de magasin pour l'objet Order fourni.");

                        l_manager = null;
                    }
                }
                else
                {
                    Object l_magasin = a_item.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

                    if (null != l_magasin)
                    {
                        String l_id = ((RepositoryItem) l_magasin).getRepositoryId();

                        l_manager = (InventoryManager) getListInventoryManagers().get(l_id);

                        if (isLoggingDebug())
                        {
                            logDebug("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : id : " + l_id
                                    + MANAGER_STRING + l_manager);
                        }
                    }
                    else
                    {
                        logError("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : pas de magasin pour l'objet Profile fourni.");

                        l_manager = null;
                    }
                }
            }
            catch (Exception l_exception)
            {
                logError("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : Erreur inattendue : "
                        + l_exception.toString(), l_exception);

                l_manager = null;
            }
        }
        else
        {
            logError("InventoryAdapter.getInventoryManager(RepositoryItem, boolean) : l'objet Profile fourni est null.");

            l_manager = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(RepositoryItem, boolean).");
        }

        return l_manager;
    }

    /**
     * Méthode qui renvoie l'inventory manager approprié en foncion des données
     * contenues dans la commande.
     * 
     * @param a_order
     *            La commande.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getInventoryManager(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(CastoOrder).");
        }

        InventoryManager l_manager;

        if (null != a_order)
        {
            RepositoryItem l_magasin = a_order.getOrigineMagasin();

            if (null != l_magasin)
            {
                l_manager = (InventoryManager) getListInventoryManagers().get(l_magasin.getRepositoryId());
            }
            else
            {
                logError("InventoryAdapter.getInventoryManager(CastoOrder) : l'item origine magasin est null (" + a_order + ")");

                l_manager = null;
            }
        }
        else
        {
            logError("InventoryAdapter.getInventoryManager(CastoOrder) : l'objet order fourni est null.");

            l_manager = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getInventoryManager(CastoOrder).");
        }

        return l_manager;
    }

    /**
     * Méthode qui renvoie l'inventory manager "caché" Castorama Direct.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getCachedInventoryManager()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager().");
        }

        String l_id = String.valueOf(CastoConstantesDefense.CASTORAMA_DIRECT_INT);

        InventoryManager l_manager = (InventoryManager) getListCachedInventoryManagers().get(l_id);

        if (isLoggingDebug())
        {
            logDebug("InventoryAdapter.getCachedInventoryManager() : id : " + l_id + MANAGER_STRING + l_manager);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager().");
        }

        return l_manager;
    }

    /**
     * Méthode qui renvoie l'inventory manager "caché" approprié en foncion des
     * données contenues dans le profil.
     * 
     * @param a_profile
     *            La commmande en cours.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getCachedInventoryManager(RepositoryItem a_profile)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager(Profile).");
        }

        InventoryManager l_manager;

        if (null != a_profile)
        {
            Object l_magasin = a_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);

            if (null != l_magasin)
            {
                String l_id = ((RepositoryItem) l_magasin).getRepositoryId();

                l_manager = (InventoryManager) getListCachedInventoryManagers().get(l_id);

                if (isLoggingDebug())
                {
                    logDebug("InventoryAdapter.getCachedInventoryManager(Profile) : id : " + l_id + MANAGER_STRING
                            + l_manager);
                }
            }
            else
            {
                logError("InventoryAdapter.getCachedInventoryManager(Profile) : pas de magasin pour l'objet Profile fourni.");

                l_manager = null;
            }
        }
        else
        {
            logError("InventoryAdapter.getCachedInventoryManager(Profile) : l'objet Profile fourni est null.");

            l_manager = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager(Profile).");
        }

        return l_manager;
    }

    /**
     * Méthode qui renvoie l'inventory manager "caché" approprié en foncion des
     * données contenues dans la commande.
     * 
     * @param a_order
     *            La commande.
     * 
     * @return Un objet inventory manager ou null en cas d'échec.
     */
    public InventoryManager getCachedInventoryManager(CastoOrder a_order)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager(CastoOrder).");
        }

        InventoryManager l_manager;

        if (null != a_order)
        {
            RepositoryItem l_magasin = a_order.getOrigineMagasin();

            if (null != l_magasin)
            {
                l_manager = (InventoryManager) getListInventoryManagers().get(l_magasin.getRepositoryId());
            }
            else
            {
                logError("InventoryAdapter.getCachedInventoryManager(CastoOrder) : l'item origine magasin est null.");

                l_manager = null;
            }
        }
        else
        {
            logError("InventoryAdapter.getCachedInventoryManager(CastoOrder) : l'objet order fourni est null.");

            l_manager = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.inventory.InventoryAdapter.getCachedInventoryManager(CastoOrder).");
        }

        return l_manager;
    }
}