package com.castorama.profile;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoServlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.userprofiling.Profile;


/**
 * Récupération des privileges utilisateur et autorisation d'acces aux pages.
 *
 * @author Geoffrey Yusif
 */


public class CastoGetPrivileges extends DynamoServlet
{
	
	
	

	
	/**
     * Méthode principale.
     * @param   a_Request         DynamoHttpServletRequest
     * @param   a_Response        DynamoHttpServletResponse
     * @throws  ServletException  erreur
     * @throws  IOException       erreur
	 */
	public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
	     throws ServletException, IOException
	{
        
        if (isLoggingDebug())
        {
            logDebug(this.getClass() + ": debut");
        }
        
        try
        {
            
            /*
             * login utilisateur
             */
    	    Profile l_user = (Profile)a_Request.getObjectParameter("profile");
            
            /*
             * liste des privileges de l'utilisateur
             */
            List l_aPrivileges = null;
            
            
            /*
             * Stabilisation
             */
            //r�cup�ration des privil�ges n�cessaires
            List l_privAutorises = new ArrayList();
            
            String[] l_listeGroupesRequis = a_Request.getParameter("requis").split(",");
            for (int l_i = 0 ; l_i < l_listeGroupesRequis.length ; l_i++)
            {
                l_privAutorises.add(l_listeGroupesRequis[l_i]);
            }
            
            
            
            /*
             * récuperation des groupes de l'utilisateur
             */
            RepositoryItem[] l_aListeDesGroupes = recuperationListe("groups", l_user);
            
            /*
             * init de laliste des privileges de l'utilisateur avant consolidation de cette derniere
             */
            if (l_aListeDesGroupes != null && l_aListeDesGroupes.length != 0)
            {
                l_aPrivileges = new ArrayList();
            
            
                /*
                 * récupération des privileges de l'utilisateur
                 */
                for (int l_i = 0; l_i < l_aListeDesGroupes.length ; l_i++)
                {
                    RepositoryItem l_oGroupeEnCours = (RepositoryItem)l_aListeDesGroupes[l_i];
                
                    RepositoryItem[] l_oPrivilegesPourGroupeEnCours = recuperationListe("privileges", l_oGroupeEnCours);
                
                    if (l_oPrivilegesPourGroupeEnCours != null && l_oPrivilegesPourGroupeEnCours.length != 0)
                    {
                        for (int l_j = 0; l_j < l_oPrivilegesPourGroupeEnCours.length ; l_j++)
                        {
                            l_aPrivileges.add(l_oPrivilegesPourGroupeEnCours[l_j].getPropertyValue("libelle"));
                        }
                    }            
                }
            
            }
            
            if (l_aPrivileges != null && l_aPrivileges.size() != 0)
            {
                a_Request.setParameter("privileges", l_aPrivileges);
            }
            else 
            {
                a_Request.setParameter("privileges", null);
            }
            
            
            String l_autorise = "0";
            if (l_aPrivileges != null && l_aPrivileges.size() != 0)
            {
                for (int l_j = 0; l_j < l_aPrivileges.size(); l_j++)
                {
                    /*OLDif (l_aPrivileges.get(l_j).equals(a_Request.getParameter("requis")))
                    {
                        l_autorise = "1";                        
                    }*/
                    for (int l_k = 0 ; l_k < l_privAutorises.size(); l_k ++)
                    {
                        if (l_aPrivileges.get(l_j).equals(l_privAutorises.get(l_k)))
                        {
                            l_autorise = "1";
                        }
                    }
                }
            }
            
           
            if (l_autorise.equals("1"))
            {
                a_Request.serviceParameter("accesAutorise", a_Request, a_Response);
            }
            else 
            {
                a_Request.serviceParameter("accesRefuse", a_Request, a_Response);
            }
            
            if (isLoggingDebug())
            {
                logDebug(this.getClass() + ": fin");
            }
        }
        catch (Exception l_e)
        {
            if (isLoggingError())
            {
                logError(this.getClass() + " ERROR : ");
                logError(l_e);
            }
        }
		
	}
    
    /**
     * 
     * TODO commentaire Javadoc.
     * @param a_sPropertyValue
     * @param a_oProfile
     * @return
     */
    private RepositoryItem[] recuperationListe(String a_sPropertyValue, Profile a_oProfile)
    {
        // R�cup�ration de la liste de contenu.
        RepositoryItem[] l_riReposItem = null;

        Set l_sListeEnSet = new HashSet();
        l_sListeEnSet = (Set) a_oProfile.getPropertyValue(a_sPropertyValue);

        if ((l_sListeEnSet != null) && (l_sListeEnSet.size() != 0))
        {
            l_riReposItem = new RepositoryItem[l_sListeEnSet.size()];
            int l_iCpt = 0;

            // pour chaque �lmt de la liste, on cast l'objet en repositoryItem
            // qu'on stock dans la liste RItem.
            for (Iterator l_iterator = l_sListeEnSet.iterator(); l_iterator.hasNext();)
            {
                RepositoryItem l_item = (RepositoryItem) l_iterator.next();
                l_riReposItem[l_iCpt] = l_item;
                l_iCpt++;
            }
        }

        return l_riReposItem;
    }
    
    /**
     * 
     * TODO commentaire Javadoc.
     * @param a_sPropertyValue
     * @param a_oRepositoryItem
     * @return
     */
    private RepositoryItem[] recuperationListe(String a_sPropertyValue, RepositoryItem a_oRepositoryItem)
    {
        // R�cup�ration de la liste de contenu.
        RepositoryItem[] l_riReposItem = null;

        Set l_sListeEnSet = new HashSet();
        l_sListeEnSet = (Set) a_oRepositoryItem.getPropertyValue(a_sPropertyValue);

        if ((l_sListeEnSet != null) && (l_sListeEnSet.size() != 0))
        {
            l_riReposItem = new RepositoryItem[l_sListeEnSet.size()];
            int l_iCpt = 0;

            // pour chaque �lmt de la liste, on cast l'objet en repositoryItem
            // qu'on stock dans la liste RItem.
            for (Iterator l_iterator = l_sListeEnSet.iterator(); l_iterator.hasNext();)
            {
                RepositoryItem l_item = (RepositoryItem) l_iterator.next();
                l_riReposItem[l_iCpt] = l_item;
                l_iCpt++;
            }
        }

        return l_riReposItem;
    }
	
	
	
}
