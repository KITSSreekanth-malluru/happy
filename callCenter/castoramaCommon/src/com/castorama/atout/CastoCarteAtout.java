package com.castorama.atout;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.constantes.CastoConstantes;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**.
 * 
 * Droplet qui cherche l'ensemble des paiements carte l'Atout disponibles
 *
 */
public class CastoCarteAtout extends DynamoServlet 
{
    /**. Variable statique */
    public static final String CODE                                 = "code";

    /**. Variable statique */
    public static final String OUTPUT                               = "output";

    /**. Variable statique */
    public static final String NOT_FOUND                            = "notFound";

    /**. Variable statique */
    public static final String ELEMENT_NAME                         = "elementName";

    /**. Variable statique */
    public static final String ELEMENT                              = "element";

    /**. Variable statique */
    public static final String PAIEMENT_EN_UNE_FOIS                 = "Paiement en une fois";

    /**. Variable statique */
    public static final String PAIEMENT_EN_PETITES_MENSUALITES      = "Paiement en petites mensualites";

    /**. Variable statique */
    public static final String CODE_PAIEMENT_EN_UNE_FOIS            = "1";

    /**. Variable statique */
    public static final String CODE_PAIEMENT_EN_PETITES_MENSUALITES = "3";

    protected MoyenDePaiement[] m_Liste;    // Liste des moyens de paiement par carte l'atout


    /**.
     * Récupération de Liste
     * @param    none
     * @return   MoyenDePaiement[] Liste
     * @throws   none
     */
    public MoyenDePaiement[] getListe()
    {
        return m_Liste;
    }


    /**.
     * Modification de Liste
     * @param    a_Liste        MoyenDePaiement[] Liste
     */
    public void setListe(MoyenDePaiement[] a_Liste)
    {
        m_Liste = a_Liste;
    }


    /**.
     * Récupération d'un moyen de paiement
     * @param    a_strCodePaiement      CodePaiement
     * @return   MoyenDePaiement
     * @throws   none
     */
    public MoyenDePaiement getMoyenDePaiement(String a_strCodePaiement)
    {

        MoyenDePaiement l_MoyenDePaiement = null;
        for(int l_nI=0;l_nI<m_Liste.length;l_nI++)
        {
            l_MoyenDePaiement = m_Liste[l_nI];
            if(a_strCodePaiement.equals(l_MoyenDePaiement.getCode()))
            {
                return l_MoyenDePaiement;
            }
        }

        return null;
    }





    /**
     * Service.
     * Un mode de paiement est renvoyé en paramètre de sortie pour un code de paiement passé en paramètre d'entrée
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

            String  l_strCode   = a_Request.getParameter(CODE) ;
            if(l_strCode!=null)
            {
                serviceDetail(a_Request,a_Response);
            }
            else
            {
                serviceList(a_Request,a_Response);
            }

        }
        catch(Exception l_oException)
        {
            if (isLoggingError())
            {
                logError(l_oException);
            }

        }

    }


    /**
     * Service Detail.
     * Un mode de paiement est renvoyé en paramètre de sortie pour un code de paiement passé en paramètre d'entrée
     * @param    a_Request                  DynamoHttpServletRequest - La request à traité.
     * @param    a_Response                 DynamoHttpServletResponse - L'objet reponse de cette request.
     * 
     * @throws   ServletException           javax.servlet.ServletException
     * @throws   IOException                java.io.IOException
     */
    public void serviceDetail (DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {

        try
        {

            String  l_strCode           = a_Request.getParameter(CODE) ;
            String  l_strElementName    = a_Request.getParameter(ELEMENT_NAME) ;


            if(l_strElementName==null) l_strElementName = ELEMENT;

            // Modification G.P. 19/02/2007
            // Correction bug 609 il faut gerer cas code = 1 ou code = 3

            MoyenDePaiement l_MoyenDePaiement = null;

            if (String.valueOf(CastoConstantes.UN).equalsIgnoreCase(l_strCode) ||
                    String.valueOf(CastoConstantes.TROIS).equalsIgnoreCase(l_strCode))
            {
                l_MoyenDePaiement = gestionCasParticuliers(l_strCode);
            }
            else
            {
                l_MoyenDePaiement = getMoyenDePaiement(l_strCode);
            }

            if(l_MoyenDePaiement!=null)
            {
                a_Request.setParameter(l_strElementName+"Name", l_MoyenDePaiement.getLibelle());
                a_Request.setParameter(l_strElementName,l_MoyenDePaiement);
                a_Request.serviceLocalParameter(OUTPUT, a_Request, a_Response);
            }
            else
            {
                a_Request.serviceLocalParameter(NOT_FOUND, a_Request, a_Response);
            }


        }
        catch(Exception l_oException)
        {
            if (isLoggingError())
            {
                logError(l_oException);
            }

        }

    }


    /**
     * Service List.
     * Une liste de String presentant les nucleus names de chaque mode de paiement est renvoyé en paramètre de sortie
     * @param    a_Request               DynamoHttpServletRequest - La request à traité.
     * @param    a_Response              DynamoHttpServletResponse - L'objet reponse de cette request.
     * 
     * @throws   ServletException        javax.servlet.ServletException
     * @throws   IOException             java.io.IOException
     */
    public void serviceList (DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
    throws ServletException, IOException
    {

        try
        {

            String  l_strElementName    = a_Request.getParameter(ELEMENT_NAME) ;

            if(l_strElementName==null) l_strElementName = ELEMENT;

            for(int l_nI=0;l_nI<m_Liste.length;l_nI++)
            {
                MoyenDePaiement l_MoyenDePaiement = m_Liste[l_nI];
                a_Request.setParameter(l_strElementName+"Name", l_MoyenDePaiement.getName());
                a_Request.setParameter(l_strElementName,l_MoyenDePaiement);
                a_Request.serviceLocalParameter(OUTPUT, a_Request, a_Response);
            }
        }
        catch(Exception l_oException)
        {
            if (isLoggingError())
            {
                logError(l_oException);
            }

        }
    }

    /**.
     * 
     * Gére les cas particuliers (ex : code = 1 ou code = 3)s
     * @param       a_Code              Code du poyen de paiement recherché
     * @return      MoyenDePaiement     Moyen de paiement avec le libellé renseigné
     */
    private MoyenDePaiement gestionCasParticuliers (String a_Code)
    {
        MoyenDePaiement l_MoyenDePaiement = new MoyenDePaiement();

        // Si code = 1
        if (String.valueOf(CastoConstantes.UN).equalsIgnoreCase(a_Code))
        {
            l_MoyenDePaiement.setLibelle(PAIEMENT_EN_UNE_FOIS);
        }
        // Si code = 3
        else if (String.valueOf(CastoConstantes.TROIS).equalsIgnoreCase(a_Code))
        {
            l_MoyenDePaiement.setLibelle(PAIEMENT_EN_PETITES_MENSUALITES);
        }
        // Sinon on renvoie null
        else
        {
            l_MoyenDePaiement = null;
        }

        return l_MoyenDePaiement;
    }
}
