package com.castorama.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantes;

/**
 * .
 * Logica
 * 18/09/2008
 * Fiche Mantis 1256 - Droplet qui liste les commandes d'un user par submitted date
 */
public class CastoListeCommandesParDate extends DynamoServlet
{
    /*
     * ------------------------------------------------------------------------
     * Contantes
     * ------------------------------------------------------------------------
     */
    private static final String DESCRIPTEUR_ORDER = "order";
    
    private static final String LISTE_DES_IDS_COMMANDES = "listeOrderIds";
    
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */
    private Repository m_orderRepository;
    
    /*
     * ------------------------------------------------------------------------
     * Getters / Setters
     * ------------------------------------------------------------------------
     */
    /**
     * Retourne une reference vers le orderRepository.
     * 
     * @return Repository Une reference vers le orderRepository.
     */
    public Repository getOrderRepository()
    {
        return m_orderRepository;
    }

    /**
     * Fixe la reference vers le orderRepository.
     * 
     * @param a_orderRepository
     *            La nouvelle reference vers le orderRepository.
     */
    public void setOrderRepository(Repository a_orderRepository)
    {
        m_orderRepository = a_orderRepository;
    }
    
    /*
     * ------------------------------------------------------------------------
     * Méthode
     * ------------------------------------------------------------------------
     */
    /*
     * .
     * Logica
     * 18/09/2008
     * Fiche Mantis 1256 - Droplet qui liste les commandes d'un user par submitted date
     */
    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_oRequest
     *            La requête HTTP.
     * @param a_oResponse
     *            La réponse HTTP.
     * 
     * @throws ServletException
     *             Si une exception survient.
     * @throws IOException
     *             Si une exception survient.
     */
    public void service(DynamoHttpServletRequest a_oRequest, DynamoHttpServletResponse a_oResponse)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.CastoListeCommandesParDate.service()");
        }

        List l_orderIds = (List) a_oRequest.getObjectParameter(LISTE_DES_IDS_COMMANDES);

        if (isLoggingDebug())
        {
            logDebug("Nombre de commandes a trier : "+  l_orderIds.size());
        }
        
        Repository l_orderRepo = getOrderRepository();
        List l_lesCommandesTriees = new ArrayList();
        if(null!=l_orderRepo)
        {
            if(null!=l_orderIds && l_orderIds.size()>1)
            {
                RepositoryItem l_order = null;
                String l_orderId = null;
                for(Iterator l_it=l_orderIds.iterator();l_it.hasNext();)
                {
                    try
                    {
                        l_orderId = (String)l_it.next();
                        l_order = (RepositoryItem) l_orderRepo.getItem(l_orderId,DESCRIPTEUR_ORDER);
                        if (isLoggingDebug())
                        {
                            logDebug(l_order.getRepositoryId());
                        }
                        l_lesCommandesTriees.add(l_order);
                    }
                    catch (RepositoryException l_ex) 
                    {
                        logError(l_ex);
                    }
                }
                
                if(null!=l_lesCommandesTriees)
                {
                    Collections.sort(l_lesCommandesTriees, new OrderComparator());
                }
                else
                {
                    if (isLoggingDebug())
                    {
                        logDebug("lesCommandesTriees==null");
                    }
                }
                
                a_oRequest.setParameter("lesCommandesTriees", l_lesCommandesTriees);
                a_oRequest.serviceParameter("output", a_oRequest, a_oResponse);
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("l_orderIds.size() <= 1 :  pas besoin de tri.");
                }
            }
        }
        else
        {
            if (isLoggingDebug())
            {
                logDebug("null==getOrderRepository()");
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.commande.CastoListeCommandesParDate.service()");
        }
    }
}
