package com.castorama.droplet.commande;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.order.OrderImpl;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesDefense;

/**
 * droplet qui determine si une commande est une commande magasin.
 * 
 */
public class CastoIsCommandeMagasin extends DynamoServlet
{
    
    /*
     * constantes
     */
    private static final String FALSE = "false";

    /*
     * attributs
     */
    private Repository m_OrderRepository;

    /*
     * accesseurs
     */
    /**
     * .
     * 
     * Repository
     * 
     * @return Repository
     */
    public Repository getOrderRepository()
    {
        return m_OrderRepository;
    }

    /**
     * .
     * 
     * Repository
     * 
     * @param a_OrderRepository
     *            Repository
     */
    public void setOrderRepository(Repository a_OrderRepository)
    {
        this.m_OrderRepository = a_OrderRepository;
    }

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
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.commande.CastoIsCommandeMagasin.service()");
        }

        RepositoryItem l_origineMagasin;
        String l_magasinId;
        OrderImpl l_commande = (OrderImpl) a_oRequest.getObjectParameter("order");
        //my test atg.commerce.order.OrderImpl l_commande = (atg.commerce.order.OrderImpl) a_oRequest.getObjectParameter("order");
        
        if (null == l_commande)
        {
            a_oRequest.serviceParameter(FALSE, a_oRequest, a_oResponse);
        }
        else
        {
            /* Code Review : synchronized (l_commande)
            {*/
                l_origineMagasin = (RepositoryItem) l_commande.getPropertyValue("origineMagasin");
                if (null == l_origineMagasin)
                {
                    a_oRequest.serviceParameter(FALSE, a_oRequest, a_oResponse);
                }
                else
                {
                    l_magasinId = l_origineMagasin.getRepositoryId();
                    if (null == l_magasinId || "".equals(l_magasinId)
                            || CastoConstantesDefense.CASTORAMA_DIRECT.equals(l_magasinId))
                    {
                        a_oRequest.serviceParameter(FALSE, a_oRequest, a_oResponse);
                    }
                    else
                    {
                        a_oRequest.serviceParameter("true", a_oRequest, a_oResponse);
                    }
                }
        }
        /* Code Review : }*/

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.commande.CastoIsCommandeMagasin.service()");
        }

    }

}
