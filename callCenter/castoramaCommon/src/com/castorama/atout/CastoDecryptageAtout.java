package com.castorama.atout;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**.
 * 
 * Droplet qui décrypte le numéro carte l'Atout si nécessaire
 *
 */
public class CastoDecryptageAtout extends DynamoServlet 
{
    /**
     * Service.
     * On décode le numero carte l'Atout si nécessaire
     * @param    a_Request              DynamoHttpServletRequest - La request à traité.
     * @param    a_Response             DynamoHttpServletResponse - L'objet reponse de cette request.
     * 
     * @throws   ServletException       javax.servlet.ServletException
     * @throws   IOException            java.io.IOException
     */
    public void service (DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {

        try
        {

            String  l_sNumero   = a_Request.getParameter("numAtout") ;

            BeanChiffrement l_BeanChiffrement   = (BeanChiffrement) Nucleus.getGlobalNucleus().resolveName("/castorama/atout/BeanChiffrement");

            String l_sNumTraite = l_sNumero;

            if(l_sNumTraite != null && l_BeanChiffrement!=null)
            {
                l_sNumTraite = l_BeanChiffrement.decode(l_sNumTraite);

                if ("*** DES INPUT BUFFER NOT A MULTIPLE OF 8 BYTES - IGNORING EXCEPTION ***19"
                        .equalsIgnoreCase(l_sNumTraite))
                {
                    l_sNumTraite = l_sNumero;
                }

                a_Request.serviceLocalParameter("output", a_Request, a_Response);
                a_Request.setParameter("numero", l_sNumTraite);

            }

        }
        catch(Exception l_oException)
        {
            if (isLoggingError())
            {
                logError(l_oException);
            }
            a_Request.serviceLocalParameter("error", a_Request, a_Response);
        }

    }
}
