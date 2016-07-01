package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantes;

/**
 * .
 * Logica
 * Fiche Mantis 1294 Lister les commandes des administrateurs
 */
public class CastoListerCommandesAdmin extends DynamoServlet
{

    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
    
    /**
     * VINGT_CING.
     */
    private static final int VINGT_CINQ = 25;
    
    /*
     * ------------------------------------------------------------------------ [
     * Attributs
     * ------------------------------------------------------------------------
     */
    private Repository m_orderRepository;
    
    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /**.
     * Recuperation du Repository
     * @param none
     * @return Repository Repository
     * @throws none
     */
    public Repository getOrderRepository()
    {
        return m_orderRepository;
    }

    /**.
     * Modification du Order Repository
     * @param        a_orderRepository       a_orderRepository
     * @throws       none
     */
    public void setOrderRepository(Repository a_orderRepository)
    {
        m_orderRepository = a_orderRepository;
    }
    
    /*
     * ------------------------------------------------------------------------
     * Méthodes
     * ------------------------------------------------------------------------
     */

    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_Request
     *            Requête HTTP.
     * @param a_Response
     *            Réponse HTTP.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoListerCommandesAdmin.service().");
        }
        
        //Etape 1 : On recupere le numero de la page a afficher
        int l_nStart = 1;
        Object l_pageEnCours = a_Request.getParameter("pageEnCours");
        int l_nNbTotalProduits = 0;
        Object l_nombreTotalProduit = a_Request.getParameter("nombreTotalProduit");
        if(null != l_pageEnCours)
        {
            l_nStart = new Integer((String)l_pageEnCours).intValue();
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("l_pageEnCours==null");
            }
        }
        
        // Etape 1 bis : On recupere le nombre de produits total a afficher
        if(null != l_nombreTotalProduit)
        {
            l_nNbTotalProduits =  new Integer((String)l_nombreTotalProduit).intValue();
        }
        else
        {
            if(isLoggingDebug())
            {
                logDebug("l_nombreTotalProduit==null");
            }
        }
        
        if(isLoggingDebug())
        {
            logDebug("l_pageEnCours : "+l_pageEnCours.toString() + " l_nombreTotalProduit : "+l_nombreTotalProduit);
        }
        
        //Etape 2 : On calcule à quelle nouvelle position le numero de page demandee correspond
        int l_nIndicePremierProduitALister = calculIndicePremierProduitALister(l_nStart);
        
        if(isLoggingDebug())
        {
            logDebug("l_nIndicePremierProduitALister : "+l_nombreTotalProduit);
        }
        
        //Etape 3 : On recupere donc les nouvelles 25 cdes a afficher
        //RepositoryItem[] l_cdes = new atg.repository.RepositoryItem[VINGT_CINQ];
        RepositoryItem[] l_cdes = lesProduitsAAfficher(l_nStart, l_nIndicePremierProduitALister, l_nNbTotalProduits);

        //Etape 4 : On definit les params de sortie
        a_Request.setParameter("lesProduits", l_cdes);
        a_Request.serviceParameter("output", a_Request, a_Response);
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoListerCommandesAdmin.service().");
        }
    }
    
    /**
     * Remplissage des trois listes de produits (correspondant aux 3 rangee de 4
     * produits a afficher).
     * 
     * @param l_nStart l_nStart
     * @param l_nIndicePremierProduitALister l_nIndicePremierProduitALister
     * @apram a_nombreTotalProduit a_nombreTotalProduit
     */
    private RepositoryItem[] lesProduitsAAfficher(int a_nStart, int a_nIndicePremierProduitALister, int a_nombreTotalProduit)
    {

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoListerCommandesAdmin.lesProduitsAAfficher().");
        }
        
        int l_tailleListeCdes = 0;
        if (isLoggingDebug())
        {
            logDebug("a_nStart : "+a_nStart);
            logDebug("a_nIndicePremierProduitALister : "+a_nIndicePremierProduitALister);
            logDebug("a_nombreTotalProduit : "+a_nombreTotalProduit);
        }
        if(a_nombreTotalProduit - a_nIndicePremierProduitALister < VINGT_CINQ)
        {
            l_tailleListeCdes = a_nombreTotalProduit - a_nIndicePremierProduitALister + 1;
        }
        else
        {
            l_tailleListeCdes = VINGT_CINQ;
        }
        
        RepositoryItem[] l_list = new RepositoryItem[l_tailleListeCdes];
        RqlStatement l_oRequeteRQL;
        Repository l_orderRepo = getOrderRepository();
        RepositoryItem [] l_aList = null;
        Object[] l_aRqlparams = null;
        RepositoryView l_oView = null;
        if(null!=l_orderRepo)
        {
            try
            {
                l_oView = l_orderRepo.getView("order");
                l_oRequeteRQL = RqlStatement.parseRqlStatement("cdeAdmin=true ORDER BY exportdate SORT DESC");
                // On execute la requete
                l_aList = l_oRequeteRQL.executeQueryUncached (l_oView, l_aRqlparams);
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
                logDebug("getOrderRepository()==null");
            }
            l_aList = null;
        }
        
        int l_nCompteurProduitAffiche = 1;
        int l_indiceListe = 0;
        if (l_aList != null) {
            if(isLoggingDebug())
            {
                logDebug("-->a_nIndicePremierProduitALister="+a_nIndicePremierProduitALister);
                logDebug("-->l_aList.length="+l_aList.length);
            }
            for (int l_i = a_nIndicePremierProduitALister; l_i <= l_aList.length; l_i++)
            {
                if (l_nCompteurProduitAffiche <= VINGT_CINQ)
                {
                    l_list[l_indiceListe] = l_aList[l_i - 1];
                    if(isLoggingDebug())
                    {
                        logDebug(l_list[l_indiceListe].toString());
                    }
                    l_indiceListe++;
                    l_nCompteurProduitAffiche++;
                }
                else
                {
                    break;
                }
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoListerCommandesAdmin.lesProduitsAAfficher().");
        }
        
        return l_list;
    }
    
    /**
     * Calcul l indice du premier produit lister en fonction de la page
     * demandee la formule pour le calcul est : [(n page demandee * 12)+1]-12 =
     * indice recherche.
     * 
     * @param a_nStart -
     *            la page demandee
     * @return int - indice du premier produit a lister
     */
    private int calculIndicePremierProduitALister(int a_nStart)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.commande.CastoListerCommandesAdmin.calculIndicePremierProduitALister().");
        }

        int l_indicePremierProduitALister = ((a_nStart * VINGT_CINQ) + 1) - VINGT_CINQ;

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.commande.CastoListerCommandesAdmin.calculIndicePremierProduitALister().");
        }

        return l_indicePremierProduitALister;
    }
}
