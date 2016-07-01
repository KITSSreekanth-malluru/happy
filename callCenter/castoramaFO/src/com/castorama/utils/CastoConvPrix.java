package com.castorama.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**.
 * 
 * Droplet de convertion Euros francs  et francs euros
 *
 */
public class CastoConvPrix extends DynamoServlet 
{
   
    /**.
     * Fonction permettant de recuperer la categorie d'un sku. 
     * @param   a_oRequest          DynamoHttpServletRequest
     * @param   a_oResponse         DynamoHttpServletResponse
     * @throws  ServletException    If exception occurs
     * @throws  IOException         If exception occurs
     */
    public void service(DynamoHttpServletRequest a_oRequest, DynamoHttpServletResponse a_oResponse) throws ServletException, IOException 
            {
                if (isLoggingDebug()) 
                {
                    logDebug("CastoConvprix.servie  : " + this.getClass());
                }
                
                try 
                {
                     String l_sensConv = a_oRequest.getParameter("sensConv");
                     //AP Double l_montant = new Double (a_oRequest.getParameter("montant"));
                     String sMontant = a_oRequest.getParameter("montant");
                     Double l_montant = sMontant != null ? Double.valueOf(sMontant) : 0.00;
                     
                     if (l_sensConv!=null)
                     {
                        if (l_sensConv.equals("Fr-Eur"))
                        {
                            a_oRequest.setParameter("montantConverti", convFrancsEuros(l_montant).toString());
                            a_oRequest.setParameter("devise", "Eur");
                        }
                        else 
                        {
                            a_oRequest.setParameter("montantConverti", convEurosFrancs(l_montant).toString());
                            a_oRequest.setParameter("devise", "Frs");
                        }
                        a_oRequest.serviceParameter("output", a_oRequest, a_oResponse);
                     }
                     else 
                     {
                         if (isLoggingWarning())
                             
                         {
                             logWarning("Le sens de la convertion est inconnu");
                         }
                     }
                     
                }
                catch (Exception l_oException) 
                {
                    // En cas d'exception, on loggue l'erreur et on affiche sur la jsp un message
                    if (isLoggingError()) 
                    {
                        logError(l_oException);
                    }
                   
                }
        
                if (isLoggingDebug()) 
                {
                    logDebug("Fin de la fonction service du " + this.getClass());
                }
            }
    
    private static Double arrondi(double a_Number)
    {
        return new Double ((new Long(Math.round(a_Number*100.0)).doubleValue())/100.0);
    }

    private Double convEurosFrancs(Double a_montant)
    {
        try
        {           
            return arrondi(a_montant.doubleValue()*6.55957);           
        }
        catch(Exception e)
        {
            if (isLoggingError())
            {
                logError("exception pour convEurosFrancs de"+ this.getClass());
                logError("EXCEPTION : "+ e);
            }
            return Double.valueOf("9999999.99");
             
        }
    }
    private Double convFrancsEuros(Double a_montant)
    {
        try
        {            
            return arrondi(a_montant.doubleValue()/6.55957);            
        }
        catch(Exception e)
        {
            if (isLoggingError())
            {
                logError("exception pour convFrancsEuros de"+ this.getClass());
                logError("EXCEPTION : "+ e);
            }
            return Double.valueOf("9999999.99");
        }   
    }

   
}