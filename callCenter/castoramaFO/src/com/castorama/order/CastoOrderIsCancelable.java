package com.castorama.order;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderUserMessage;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.commerce.states.BOOrderStates;

/** 
 * @author Geoffrey Yusif (LogicaCMG)
 */

/**
 * Vérifie si une commande peut être annulée.
 * Cf doc ATG CSR pour les critères de vérification.
 */
public class CastoOrderIsCancelable extends OrderComponentIsModifiable
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
            if (orderIsCancelable(l_orderId))
            {            
                //on vérifie les contraintes sur les commerceItems
                
                List l_commerceItems = l_order.getCommerceItems();
                for(int l_i = 0; l_i < l_commerceItems.size(); l_i++)
                {                
                   if(!commerceItemIsEditable(((CommerceItem)l_commerceItems.get(l_i)).getId()))               
                   {
                        a_request.serviceLocalParameter(OrderComponentIsModifiable.ERROR, a_request, a_response);
                        return;               
                   }
                }
                
                //on vérifie les contraintes sur les paymentGroups
               List l_paymentGroups = l_order.getPaymentGroups();
                for(int l_i = 0; l_i < l_paymentGroups.size(); l_i++)
                {
                    if(!paymentGroupIsEditable(((PaymentGroup)l_paymentGroups.get(l_i)).getId()))
                    {
                        a_request.serviceLocalParameter(OrderComponentIsModifiable.ERROR, a_request, a_response);
                        return;
                    }
                }                               
                
                //on vérifie les contraintes sur les ShippingGroups
                List l_shippingGroups = l_order.getShippingGroups();
                for(int l_i = 0; l_i < l_shippingGroups.size(); l_i++)
                {
                    if(!shippingGroupIsEditable(((ShippingGroup)l_shippingGroups.get(l_i)).getId()))
                    {
                        a_request.serviceLocalParameter(OrderComponentIsModifiable.ERROR, a_request, a_response);
                        return;
                    }
                }
                
                a_request.serviceParameter("output", a_request, a_response);                                                       
            }
        }      
        catch (RepositoryException l_repoExc)
        {
            logError("EXCEPTION : "+l_repoExc);
            a_request.serviceLocalParameter(OrderComponentIsModifiable.ERROR, a_request, a_response);
            return;
        }
            
    }
       
 
    private boolean orderIsCancelable(String pOrderId) throws RepositoryException {
    	boolean result = false;
        RepositoryItem order = getRepository().getItem(pOrderId,"order");
        
        if (order != null) {
        	Object boState = order.getPropertyValue("BOState");
        	if (boState != null && (
        			BOOrderStates.PENDING_CALL_CENTER.equalsIgnoreCase((String)boState) ||
        			BOOrderStates.PENDING_PAYBOX.equalsIgnoreCase((String)boState) ||
        			BOOrderStates.PENDING_VIREMENT.equalsIgnoreCase((String)boState) ||
        			BOOrderStates.PENDING_CHEQUE.equalsIgnoreCase((String)boState))) {

        		result = true;
        		
        	}
        }
        
    	return result;
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
    
    /**
     * Vérifie si un commerceItem est Editable.
     * @param a_commerItemId        String - identifiant du commerceItem
     * @param a_order               Order - la commande
     * @throws CommerceException    Erreur
     */
   private boolean commerceItemIsEditable (String a_commerItemId) throws RepositoryException
    {
       /*
        * commerceItem editable si statut <> DELIVERED (0)
        */
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+".commerceItemIsEditable start");                
        }
        
        Repository repo = getRepository();
        RepositoryItem l_commerceItem = repo.getItem(a_commerItemId,"commerceItem"); 
        
        
        
        if (l_commerceItem.getPropertyValue("state") != null
            && l_commerceItem.getPropertyValue("state").equals("DELIVERED"))            
        {
            if (isLoggingDebug())
            {
                logDebug(this.getClass()+".commerceItemIsEditable : commerceItem "+ a_commerItemId +" non editable");                
            } 
            return false;            
        }
        else
        {
            if (isLoggingDebug())
            {
                logDebug(this.getClass()+".commerceItemIsEditable : commerceItem "+ a_commerItemId +" editable");                
            }
            return true;
        }
    }
   
   /**
    * Vérifie si un PaymentGroup est Editable.
    * @param a_paymentGroupId        String - identifiant du paymentGroup
    * @param a_order               Order - la commande
    * @throws CommerceException    Erreur
    */
  private boolean paymentGroupIsEditable (String a_paymentGroupId) throws RepositoryException
   {
      /*
       * PaymentGroup editable si statut <> SETTLED ()
       */
       if (isLoggingDebug())
       {
           logDebug(this.getClass()+".paymentGroupIsEditable start");                
       }
       
       Repository repo = getRepository();
       RepositoryItem l_paymentGroup = repo.getItem(a_paymentGroupId,"paymentGroup");
       
       if (l_paymentGroup.getPropertyValue("state") != null
               && l_paymentGroup.getPropertyValue("state").equals("SETTLED"))
       {
           if (isLoggingDebug())
           {
               logDebug(this.getClass()+".paymentGroupIsEditable : paymentGroup "+a_paymentGroupId+" non editable");                
           } 
           return false;            
       }
       else
       {
           if (isLoggingDebug())
           {
               logDebug(this.getClass()+".paymentGroupIsEditable : paymentGroup "+a_paymentGroupId+" editable");                
           }
           return true;
       }
   }
  
  /**
   * Vérifie si un ShippingGroup est Editable.
   * @param a_shippingGroupId        String - identifiant du shippingGroup
   * @param a_order               Order - la commande
   * @throws CommerceException    Erreur
   */
 private boolean shippingGroupIsEditable (String a_shippingtGroupId) throws RepositoryException
  {
     /*
      * ShippingGroup editable si statut <> SETTLED ()
      */
      if (isLoggingDebug())
      {
          logDebug(this.getClass()+".shippingGroupIsEditable start");                
      }
      
      Repository repo = getRepository();
      RepositoryItem l_shippingGroup = repo.getItem(a_shippingtGroupId,"shippingGroup");
      
   
      if (l_shippingGroup.getPropertyValue("state") != null
              && l_shippingGroup.getPropertyValue("state").equals("NO_PENDING_ACTION"))
      {
          if (isLoggingDebug())
          {
              logDebug(this.getClass()+".shippingGroupIsEditable : shippingGroup "+a_shippingtGroupId+" non editable");                
          } 
          return false;            
      }
      else
      {
          if (isLoggingDebug())
          {
              logDebug(this.getClass()+".shippingGroupIsEditable : shippingGroup  "+a_shippingtGroupId+" editable");                
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
