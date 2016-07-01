package com.castorama.order;

import atg.repository.RepositoryItem;


import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderUserMessage;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.PaymentGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import atg.repository.Repository;
import atg.repository.RepositoryException;



/**
 * Vérifie si une commande peut être annulée.
 * Cf doc ATG CSR pour les critères de vérification.
 */
public class CastoShippingGroupIsModifiable extends OrderComponentIsModifiable
{
    /*
     * Order Manager pour la gestion de la commande
     */
    private OrderManager m_OrderManager;
    private Repository m_repository;
    
    
    /**
     * Charge la commande concernée.
     * @return Order        La commande concernée
     */
    public Order getOrder()
    {
        if(super.mOrder == null && m_OrderManager != null && super.mId != null && super.mId.trim().length() != 0)
        {
            try
            {
                if(isLoggingDebug())
                    {
                    logDebug("Recherche de la commande #" + super.mId + "...");
                    }
                if(m_OrderManager.orderExists(super.mId))
                {
                    super.mOrder = m_OrderManager.loadOrder(super.mId);
                    
                        if(isLoggingDebug())
                        {
                        logDebug("...trouvée.");
                        }
                }
            }
            catch(CommerceException l_commerceexception)
            {
                if(isLoggingError())
                    {
                    logError(l_commerceexception);
                    }
            }
        }
        return super.mOrder;
    }

    /**
     * 
     * Chargement du OrderManager pour la gestion de la commande.
     * @return OrderManager     le manager de commandes
     */
    public OrderManager getOrderManager()
    {
        return m_OrderManager;
    }
    
    /**
     * 
     * fixe l'orderManger.
     * @param a_ordermanager      OrderManager - le manager de commande
     */
    public void setOrderManager(OrderManager a_ordermanager)
    {
        m_OrderManager = a_ordermanager;
    }

    
    /**
     * Méthode principale : vérification de la possibilité de supression de la commande.
     * @param   a_request             DynamoHttpServletRequest
     * @param   a_response            DynamoHttpServletResponse
     * @throws  ServletException      Erreur
     * @throws  IOException           Erreur
     */
    public void service(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+".service start");                
        }
         
        /*
         * On fixe le numero de la commande concernée et on récupère la commande sous la forme order
         */
        String l_orderId = a_request.getParameter(OrderComponentIsModifiable.ID);
        setId(l_orderId);
        Order l_order = getOrder();
                       
        /*
         * Si la commande n'est pas de la bonne forme, on part en exception
         */
        if(l_order == null || !(l_order instanceof Order))
        {
            a_request.serviceLocalParameter(OrderComponentIsModifiable.ERROR, a_request, a_response);
            String l_erreur = OrderUserMessage.format("noOrderToModify", getUserLocale(a_request, a_response));
            throw new ServletException(l_erreur);
        }
        
        
        try 
        {
            /*
             * on vérifie si la commande est éditable
             */
            if (orderIsEditable(l_orderId))
            {                                           
                a_request.serviceParameter("output", a_request, a_response);
                return;
            }
            else
            {
                a_request.serviceParameter("error", a_request, a_response);
                return;
            }
        }      
        catch (RepositoryException l_repoExc)
        {
            logError("EXCEPTION : "+l_repoExc);
            a_request.serviceLocalParameter("error", a_request, a_response);
            return;
        }
            
    }
       
    
    
    /**
     * Vérifie si une commande est éditable.
     * @param a_order       Order - la commande concernée
     * @return boolean      true = la commande est editable, false sinon
     */
    private boolean orderIsEditable(String a_orderId) throws RepositoryException
    {
        /*
         * Editable si statut <> REMOVED (4)
         *                       NO_PENDING_ACTION (5)
         *                       PENDING_REMOVE (3)
         */
        
        Repository repo = getRepository();
        RepositoryItem l_repoOrder = repo.getItem(a_orderId,"order");
        
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+".orderIsEditable start");                
        }

        if (l_repoOrder.getPropertyValue("state") != null 
            && (l_repoOrder.getPropertyValue("state").equals("REMOVED")
            || l_repoOrder.getPropertyValue("state").equals("PENDING_REMOVE")
            || l_repoOrder.getPropertyValue("state").equals("NO_PENDING_ACTION")))
            
        {
            if (isLoggingDebug())
            {
                logDebug(this.getClass()+".orderIsEditable : La commande n'est pas éditable, statut = "+ l_repoOrder.getPropertyValue("state").toString());
                logDebug(this.getClass()+".orderIsEditable.service stop");            
            }
            return false;
        }
        else 
        {
            if (isLoggingDebug())
            {
                logDebug(this.getClass()+".orderIsEditable : La commande est éditable, statut = "+ l_repoOrder.getPropertyValue("state").toString()); 
                logDebug(this.getClass()+".orderIsEditable.service stop");   
            }
            return true; 
        }       
    }
    
    

public Repository getRepository()
{
    return m_repository;
}

public void setRepository(Repository a_repository)
{
    m_repository = a_repository;
}
   
    
}
