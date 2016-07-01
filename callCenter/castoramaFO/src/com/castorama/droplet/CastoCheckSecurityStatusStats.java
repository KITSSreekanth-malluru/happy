package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;


import atg.beans.PropertyNotFoundException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;



/**
 * @author Geoffrey Yusif 
 */



/**
 * 
 * CastoCheckSecurityStatus.
 * Vérifie si le client est connecté correctement pour pouvoir accéder aux pages
 * sensibles. Si ce n'est pas le cas, on redirige l'utilisateur vers la page de Login
 *
 */
public class CastoCheckSecurityStatusStats extends DynamoServlet
{
    /**
     * Constante statique.
     */
    private static final int DEUX = 2;
	
    
    
	/**
	 * méthode de principale de traitement.
     * @param a_request    DynamoHttpServletRequest  arg0
     * @param a_response   DynamoHttpServletResponse arg1
     * @throws   ServletException     erreur
     * @throws   IOException          erreur
	 */
	public void service(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response) 
	throws ServletException, IOException
	{
		if (isLoggingDebug())
        {
		    logDebug(":: CastoCheckSecurityStatus.service -> method is running ...");            
        }
        try
        {
            

                /*
                 * récupération du profile et du profileTools qui donne accès à 
                 * la propriété sécurityStatus 
                 */
                Profile l_user = (Profile)a_request.getObjectParameter("profilUtilisateur");
                ProfileTools l_tools = l_user.getProfileTools();
                
                /*
                 * Si l'utilisateur n'est pas connecté on le renvoie vers le login
                 */            
                Object l_transiente = l_user.getPropertyValue("login");
                if (l_transiente != null)
                {
                    /*
                     * récupération du securityStatus 
                     */
        
                    Integer l_securityStatus = l_tools.getSecurityStatus(l_user);
                    
                    
                    /*
                     * si security status vaut AUTO-SIGNIN (2) on doit rediriger vers 
                     * l'identification 
                     */
                    if (l_securityStatus.compareTo(new Integer(DEUX)) == 0)
                    {
                        if (isLoggingDebug())
                        {
                            logDebug(":: CastoCheckSecurityStatus -> AUTO_SIGNIN : redirection login");            
                        }                
                        a_response.sendLocalRedirect("./login.jsp", a_request);
                    }    
                }
                else 
                {
                    if (isLoggingDebug())
                    {
                        logDebug(":: CastoCheckSecurityStatus -> Non loggué : redirection login");            
                    } 
                    a_response.sendLocalRedirect("./login.jsp", a_request);
                }
        }
        catch (PropertyNotFoundException l_ex)
        {
            if (isLoggingError())
            {
                logError(":: CastoCheckSecurityStatus -> got en exception");
                logError("::details :");
                logError(l_ex);
            }
        }
        if (isLoggingDebug())
        {
            logDebug(":: CastoCheckSecurityStatus.service -> method is Terminating ...");            
        }   
	}
    
   
}