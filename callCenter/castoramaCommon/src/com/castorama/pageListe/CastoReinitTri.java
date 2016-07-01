package com.castorama.pageListe;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.castorama.constantes.CastoConstantesDefense;

/**
 * 
 * Reinitialisation du tri par prix et filtre marque.
 *
 */
public class CastoReinitTri extends DynamoServlet 
{
   /** 
     * 
     * @param   a_Request             La requ�te HTTP.
     * @param   a_Response            La r�ponse HTTP.
     * @throws  ServletException      si erreur
     * @throws  IOException           si erreur
     */
    public void service(DynamoHttpServletRequest a_Request,DynamoHttpServletResponse a_Response) throws ServletException, IOException 
    {
        Profile l_profile = (Profile) a_Request.getObjectParameter("profile");
        String l_nouveauMode = null;
        String l_nouvelleCategorie = a_Request.getParameter("nouvelleCategorie");
        String l_ancienMode = a_Request.getParameter("ancienMode");
        String l_ancienneCategorie = a_Request.getParameter("ancienneCategorie");       
        
        
        int l_idMagasin;
        if (null != l_profile)
        {
            Object l_obj = l_profile.getPropertyValue(CastoConstantesDefense.PROFILE_PROPERTY_SESSION_MAGASIN);
            if (null != l_obj)
            {
                RepositoryItem l_magasin = (RepositoryItem) l_obj;
                l_idMagasin = new Integer(l_magasin.getRepositoryId()).intValue();
                if (CastoConstantesDefense.CASTORAMA_DIRECT_INT == l_idMagasin)
                {
                    l_nouveauMode="CD";
                }
                else
                {
                    l_nouveauMode="PLD";
                }    
            }
        }
        
        if (l_nouvelleCategorie==null || l_ancienneCategorie==null || l_ancienMode==null || l_nouveauMode==null) 
        {
            if (isLoggingDebug())
            {
                logDebug("Reinit : true (un des parametres est null)");
            }  
            a_Request.serviceParameter("true", a_Request, a_Response);
        }
        else
        {
            if (!l_nouvelleCategorie.equals(l_ancienneCategorie) || !l_nouveauMode.equals(l_ancienMode))
            {
                if (isLoggingDebug())
                {
                    logDebug("Reinit : true");
                }  
                a_Request.serviceParameter("true", a_Request, a_Response);
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("Reinit : false");
                }  
                a_Request.serviceParameter("false", a_Request, a_Response);
            }      
        }    
    }
}