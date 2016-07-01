package com.castorama.droplet;

import com.castorama.CastoShoppingCartModifier;
import com.castorama.bean.BeanGetRemise;

import java.io.*;
import java.util.List;
import javax.servlet.*;

import atg.repository.RepositoryItem;
import atg.servlet.*;
import atg.commerce.order.*;
import atg.nucleus.naming.ParameterName;

/**
 * Cette servlet utilise le template d'affichage <i>output</i> pour acc�der au
 * param�tre de sortie dont le nom est stoqu� dans le param� d'entr�
 * <i>elementName</i> qui va contenir la promotion accord�e sur la commande
 * dont l'id est pass� dans le param�tre d'entr� <i>orderId</id>.
 * 
 * <p>
 * La description compl�te des param�tres de la droplet CalculPromo est:
 * 
 * <dl>
 * 
 * <dt>orderid
 * <dd>Le param�tre qui d�fini l'id de la commmande dont on veut calculer la
 * promotion.
 * 
 * <dt>elementName
 * <dd>Le param�tre facultatif qui doit �tre employ� comme nom pour le vector
 * dans le template de sortie output.
 * 
 * 
 * </dl>
 * 
 * @author Alain Monier
 * @version $Revision: 1.1 $ $Date: 2006/06/30 17:31:45 $ $Author:
 *          groupinfra\pereirag $
 */

public class CalculPromo extends DynamoServlet
{
    public static final String CLASS_VERSION = "$Id: CalculPromo.java,v 1.1 2006/06/30 17:31:45 user Exp $";

    public final static String ELEMENT = "element";

    public final static ParameterName OUTPUT = ParameterName.getParameterName("output");

    public final static ParameterName EMPTY = ParameterName.getParameterName("empty");

    // for backward compatibility
    public final static ParameterName ELEMENT_NAME = ParameterName.getParameterName("elementName");

    public final static String REMISEMONTANT = "remiseMontant";

    public final static ParameterName ORDER = ParameterName.getParameterName("order");

    public BeanGetRemise m_BeanGetRemise = null;

    /**
     * M�thode d'initialisation du BeanGetRemise.
     * 
     * @param BeanGetRemise -
     *            Le BeanGetRemise.
     * @return none.
     * @throws none.
     */
    public void setBeanGetRemise(BeanGetRemise a_BeanGetRemise)
    {
        m_BeanGetRemise = a_BeanGetRemise;
    }

    /**
     * M�thode de r�cup�ration du BeanGetRemise.
     * 
     * @param none.
     * @return BeanGetRemise - Le BeanGetRemise.
     * @throws none.
     */
    public BeanGetRemise getBeanGetRemise()
    {
        return m_BeanGetRemise;
    }

    /**
     * Charge dans la request, si la promotion est supperieur � 0.0, un template
     * d'affichage "output" pour acc�der � la promotion de la commmande, sinon
     * le template d'affichage "empty" est charg� dans la request.
     * 
     * @param DynamoHttpServletRequest -
     *            La request � trait�.
     * @param DynamoHttpServletResponse -
     *            L'objet reponse de cette request.
     * @return none.
     * @throws javax.servlet.ServletException -
     *             Si une erreur sp�cifique d'application est arriv�e traitant
     *             cette demande.
     * @throws java.io.IOException -
     *             Si une erreur est arriv�e lisant des donn�es de la demande ou
     *             �crivant des donn�es � la r�ponse.
     */
    public void service(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // Trace.logOpen(this,".service");
        try
        {

            Order l_Order = (Order) a_Request.getObjectParameter(ORDER);

            String l_strElement = "";
            l_strElement = a_Request.getParameter(ELEMENT_NAME);

            if (!(l_strElement != null && !l_strElement.equals("")))
            {
                l_strElement = ELEMENT;
            }

            double l_Promo = 0.0;
            double l_RemiseMontant = 0.0;

            if (l_Order != null)
            {
                l_Promo = Math.abs(m_BeanGetRemise.getRemise(l_Order));
                l_RemiseMontant = Math.abs(m_BeanGetRemise.getRemiseMontant(l_Order));
            }

            if (l_RemiseMontant > 0.0)
            {
                Double l_Promo_Result = new Double(l_Promo);
                Double l_RemiseMontant_Result = new Double(l_RemiseMontant);
                a_Request.setParameter(l_strElement, l_Promo_Result);
                a_Request.setParameter(REMISEMONTANT, l_RemiseMontant_Result);

                a_Request.serviceParameter(OUTPUT, a_Request, a_Response);
            }
            else
            {
                a_Request.serviceParameter(EMPTY, a_Request, a_Response);
            }
        }
        catch (Exception e)
        {
            logError("EXCEPTION ====> " + e);
            a_Request.serviceParameter(EMPTY, a_Request, a_Response);

            // Trace.logError(this,e,".service Exception : "+e.toString());
        }
        finally
        {
            // Trace.logClose(this,".service");
        }
    }
}
