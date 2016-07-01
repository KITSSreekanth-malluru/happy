package com.castorama.droplet.commande.paiement;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.atosorigin.services.cad.apipayment.SIPSCallParm;
import com.atosorigin.services.cad.apipayment.web.SIPSApiWeb;
import com.atosorigin.services.cad.common.SIPSDataObject;
import com.castorama.constantes.CastoConstantes;
import com.castorama.jdbc.CastoJdbcTools;

/**
 * @author Florte Jï¿½rï¿½my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Droplet pour l'initialisation du paiement Mercanet.
 */
public class CastoMercanetFo extends DynamoServlet
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Paramï¿½tre "amount" de la droplet.
     */
    private static final String PARAMETER_MONTANT = "amount";

    /**
     * Paramï¿½tre "order" de la droplet.
     */
    private static final String PARAMETER_ORDER = "order";

    /**
     * Open parameter "output" de la droplet.
     */
    private static final String OPEN_PARAMETER_OUTPUT = "output";

    /**
     * Open parameter "open" de la droplet.
     */
    private static final String OPEN_PARAMETER_ERROR = "error";

    /**
     * Output parameter "sipsRequest" de la droplet.
     */
    private static final String OUTPUT_PARAMETER_REQUEST = "sipsRequest";
    
    /**
     * Valeur du paramètre SIPS CAPTURE_DAY en dev.
     */
    private static final String CAPTURE_DAY_DEV = "0";

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Emplacement du fichier pathfile Mercanet.
     */
    private String m_pathFile;

    /**
     * Identifiant du commerï¿½ant.
     */
    private String m_merchantId;

    /**
     * Pays du commerï¿½ant.
     */
    private String m_merchantCountry;

    /**
     * Devise.
     */
    private String m_currencyCode;

    /**
     * URL de rï¿½ponse automatique.
     */
    private String m_autoResponseUrl;

    /**
     * URL d'annulation.
     */
    private String m_cancelUrl;

    /**
     * URL de retour.
     */
    private String m_returnUrl;

    /**
     * Liste des moyens de paiement.
     */
    private String m_paymentMeans;

    /**
     * Utilitaire pour les accÃ¨s base direct.
     */
    private CastoJdbcTools m_castoJdbcTools;

    /**
     * Nom du template Ã  employer pour l'affichage cÃ´tÃ© serveur.
     */
    private String m_templateFile;
    
    /**
     * Délai d'envoi en banque.
     */
    private String m_captureDay;

    /**
     * Mode d'envoi en banque..
     */
    private String m_captureMode;
    
    /**
     * Liste des serveurs exclus pour la gestion du paramètre SIPS "captureDay".
     */
    private List m_captureExcludes;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Renvoit l'emplacement du fichier pathfile Mercanet.
     * 
     * @return String L'emplacement du fichier pathfile Mercanet.
     */
    public String getPathFile()
    {
        return m_pathFile;
    }

    /**
     * Fixe l'emplacement du fichier pathfile Mercanet.
     * 
     * @param a_pathFile
     *            Le nouvel emplacement du fichier pathfile Mercanet.
     */
    public void setPathFile(String a_pathFile)
    {
        m_pathFile = a_pathFile;
    }

    /**
     * Renvoit l'identifiant du commerï¿½ant.
     * 
     * @return String l'identifiant du commerï¿½ant.
     */
    public String getMerchantId()
    {
        return m_merchantId;
    }

    /**
     * Fixe l'identifiant du commerï¿½ant.
     * 
     * @param a_merchantId
     *            Le nouvel identifiant du commerï¿½ant.
     */
    public void setMerchantId(String a_merchantId)
    {
        m_merchantId = a_merchantId;
    }

    /**
     * Renvoit la devise en cours.
     * 
     * @return String La devise en cours.
     */
    public String getCurrencyCode()
    {
        return m_currencyCode;
    }

    /**
     * Fixe la devise en cours.
     * 
     * @param a_currencyCode
     *            La nouvelle devise.
     */
    public void setCurrencyCode(String a_currencyCode)
    {
        m_currencyCode = a_currencyCode;
    }

    /**
     * Renvoit le pays du commerï¿½ant.
     * 
     * @return String Le pays du commerï¿½ant.
     */
    public String getMerchantCountry()
    {
        return m_merchantCountry;
    }

    /**
     * Fixe le pays du commerï¿½ant.
     * 
     * @param a_merchantCountry
     *            Le nouveau pays du commerï¿½ant.
     */
    public void setMerchantCountry(String a_merchantCountry)
    {
        m_merchantCountry = a_merchantCountry;
    }

    /**
     * Renvoit l'url de rï¿½ponse automatique.
     * 
     * @return String L'url de rï¿½ponse automatique.
     */
    public String getAutoResponseUrl()
    {
        return m_autoResponseUrl;
    }

    /**
     * Fixe l'url de rï¿½ponse automatique.
     * 
     * @param a_autoResponseUrl
     *            La nouvelle url de rï¿½ponse automatique.
     */
    public void setAutoResponseUrl(String a_autoResponseUrl)
    {
        m_autoResponseUrl = a_autoResponseUrl;
    }

    /**
     * Renvoit une rï¿½fï¿½rence vers le composant d'utilitaires jdbc.
     * 
     * @return CastoJdbcTools Une rï¿½fï¿½rence vers le composant d'utilitaires
     *         jdbc.
     */
    public CastoJdbcTools getCastoJdbcTools()
    {
        return m_castoJdbcTools;
    }

    /**
     * Fixe la rï¿½fï¿½rence vers le composant d'utilitaires jdbc.
     * 
     * @param a_castoJdbcTools
     *            La nouvelle rï¿½fï¿½rence vers le composant d'utilitaires jdbc.
     */
    public void setCastoJdbcTools(CastoJdbcTools a_castoJdbcTools)
    {
        m_castoJdbcTools = a_castoJdbcTools;
    }

    /**
     * Renvoit l'URL d'annulation.
     * 
     * @return String L'URL d'annulation.
     */
    public String getCancelUrl()
    {
        return m_cancelUrl;
    }

    /**
     * Fixe l'URL d'annulation.
     * 
     * @param a_cancelUrl
     *            La nouvelle URL d'annulation.
     */
    public void setCancelUrl(String a_cancelUrl)
    {
        m_cancelUrl = a_cancelUrl;
    }

    /**
     * Renvoit la liste des moyens de paiement.
     * 
     * @return String La liste des moyens de paiement.
     */
    public String getPaymentMeans()
    {
        return m_paymentMeans;
    }

    /**
     * Fixe la liste des moyens de paiement.
     * 
     * @param a_paymentMeans
     *            La nouvelle liste des moyens de paiement.
     */
    public void setPaymentMeans(String a_paymentMeans)
    {
        m_paymentMeans = a_paymentMeans;
    }

    /**
     * Renvoit l'URL de retour.
     * 
     * @return String L'URL de retour.
     */
    public String getReturnUrl()
    {
        return m_returnUrl;
    }

    /**
     * Fixe l'URL de retour.
     * 
     * @param a_returnUrl
     *            La nouvelle URL de retour.
     */
    public void setReturnUrl(String a_returnUrl)
    {
        m_returnUrl = a_returnUrl;
    }

    /**
     * Renvoit le nom du template pour l'affichage cï¿½tï¿½ serveur.
     * 
     * @return String le nom du template pour l'affichage cï¿½tï¿½ serveur.
     */
    public String getTemplateFile()
    {
        return m_templateFile;
    }

    /**
     * Fixe le nom du template pour l'affichage cï¿½tï¿½ serveur.
     * 
     * @param a_templateFile
     *            Le nouveau nom du template pour l'affichage cï¿½tï¿½ serveur.
     */
    public void setTemplateFile(String a_templateFile)
    {
        m_templateFile = a_templateFile;
    }
    
    /**
     * Renvoie le délai d'envoi en banque.
     * 
     * @return String Le délai d'envoi en banque.
     */
    public String getCaptureDay()
    {
        return m_captureDay;
    }

    /**
     * Fixe le délai d'envoi en banque.
     * 
     * @param a_captureDay
     *            Le nouveau délai d'envoi en banque.
     */
    public void setCaptureDay(String a_captureDay)
    {
        m_captureDay = a_captureDay;
    }

    /**
     * Renvoie le mode d'envoi en banque.
     * 
     * @return String Le mode d'envoi en banque.
     */
    public String getCaptureMode()
    {
        return m_captureMode;
    }

    /**
     * Fixe le délai d'envoi en banque.
     * 
     * @param a_captureMode
     *            Le délai d'envoi en banque.
     */
    public void setCaptureMode(String a_captureMode)
    {
        m_captureMode = a_captureMode;
    }
    
    /**
     * Méthode qui renvoie la liste des serveurs exclus pour la gestion du
     * paramètre SIPS "captureDay".
     * 
     * @return List La liste des serveurs exclus pour la gestion du paramètre
     *         SIPS "captureDay".
     */
    public List getCaptureExcludes()
    {
        return m_captureExcludes;
    }

    /**
     * Méthode qui fixe la liste des serveurs exclus pour la gestion du
     * paramètre SIPS "captureDay".
     * 
     * @param a_captureExcludes
     *            La liste des serveurs à exclure.
     */
    public void setCaptureExcludes(List a_captureExcludes)
    {
        m_captureExcludes = a_captureExcludes;
    }

    /*
     * ------------------------------------------------------------------------ [
     * Mï¿½thodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_request
     *            La requï¿½te HTTP.
     * @param a_response
     *            La rï¿½ponse HTTP.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public void service(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.service().");
        }

        String l_idTransaction = null;
        StringBuffer l_result = null;

        try
        {
            l_result = new StringBuffer();

            /*
             * ------------------------------------------------------------------------------------
             * Rï¿½cupï¿½ration des paramï¿½tres de la droplet.
             * ------------------------------------------------------------------------------------
             */
            // Order l_order =
            // (Order)a_request.getObjectParameter(PARAMETER_ORDER);
            String l_orderId = a_request.getParameter("orderId");
            String l_profileId = a_request.getParameter("profileId");

            /*
             * ------------------------------------------------------------------------------------
             * Initialisations
             * ------------------------------------------------------------------------------------
             */
            SIPSApiWeb l_api = new SIPSApiWeb(getPathFile());
            SIPSDataObject l_call = (SIPSDataObject) new SIPSCallParm();

            /*
             * ------------------------------------------------------------------------------------
             * Affectation des paramï¿½tres obligatoires
             * ------------------------------------------------------------------------------------
             */

            l_call.setValue("merchant_id", getMerchantId());
            l_call.setValue("merchant_country", getMerchantCountry());
            // cf.code monétaire ISO 4217
            l_call.setValue("currency_code", getCurrencyCode());
            l_call.setValue("capture_mode", getCaptureMode());
            
            if (getCaptureExcludes().contains(a_request.getServerName()))
            {
                l_call.setValue("capture_day", CAPTURE_DAY_DEV);
            }
            else
            {
                l_call.setValue("capture_day", getCaptureDay());
            }

            /*
             * Affectation du montant de la transaction dans la plus petite
             * unité monétaire du pays Ex : 123.00 --> 12300
             */

            l_call.setValue("amount", getMontant(a_request));

            /*
             * ------------------------------------------------------------------------------------
             * Identifiant de la transaction
             * ------------------------------------------------------------------------------------
             */
            l_idTransaction = getIdTransaction();

            if (null == l_idTransaction)
            {
                throw new Exception("Impossible d'obtenir un identifiant de transaction.");
            }

            l_call.setValue("transaction_id", l_idTransaction);

            /*
             * ------------------------------------------------------------------------------------
             * On place le jsessionid dans l'url de retour automatique
             * ------------------------------------------------------------------------------------
             */
            l_call.setValue("automatic_response_url", getUrlReponseAuto(a_request, l_call));

            /*
             * ------------------------------------------------------------------------------------
             * URL's d'annulation et de retour
             * ------------------------------------------------------------------------------------
             */
            l_call.setValue("normal_return_url", getReturnUrl());
            l_call.setValue("cancel_return_url", getCancelUrl()+"?_requestid="+a_request.getParameter("_requestid"));

            /*
             * ------------------------------------------------------------------------------------
             * Liste des moyens de paiement
             * ------------------------------------------------------------------------------------
             */
            l_call.setValue("payment_means", getPaymentMeans());

            /*
             * ------------------------------------------------------------------------------------
             * Template
             * ------------------------------------------------------------------------------------
             */
            if (null != getTemplateFile() && !"".equals(getTemplateFile()))
            {
                l_call.setValue("templatefile", getTemplateFile());
            }

            /*
             * ------------------------------------------------------------------------------------
             * Autres paramï¿½tres
             * ------------------------------------------------------------------------------------
             */
            // Profile l_profile =
            // (Profile)a_request.getObjectParameter("Profile");
            l_call.setValue("header_flag", "no");
            l_call.setValue("customer_id", l_profileId);
            // l_call.setValue("order_id", l_order.getId());
            l_call.setValue("order_id", l_orderId);

            /*
             * ------------------------------------------------------------------------------------
             * Appel SIPS
             * ------------------------------------------------------------------------------------
             */
            l_result.append(l_api.sipsPaymentCallFunc(l_call));

            /*
             * ------------------------------------------------------------------------------------
             * On fixe les paramï¿½tres de sortie de la droplet
             * ------------------------------------------------------------------------------------
             */
            a_request.setParameter(OUTPUT_PARAMETER_REQUEST, l_result);

            a_request.serviceParameter(OPEN_PARAMETER_OUTPUT, a_request, a_response);
        }
        catch (Exception l_exception)
        {
            logError(l_exception.toString());

            a_request.serviceParameter(OPEN_PARAMETER_ERROR, a_request, a_response);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.service().");
        }
    }

    /**
     * Rï¿½cupï¿½re le montant du format 100.00 au format 10000.
     * 
     * @param a_request
     * 
     * @return String Le montant au bon format.
     */
    private String getMontant(DynamoHttpServletRequest a_request)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getMontant().");
        }

        String l_montant = null;

        try
        {
            double l_valeur = Double.valueOf(a_request.getParameter(PARAMETER_MONTANT)).doubleValue()
                    * CastoConstantes.CENT;
            int l_val = (int) Math.round(l_valeur);

            l_montant = (0 == l_val) ? "000" : "" + l_val;
        }
        catch (Exception l_exception)
        {
            logError(l_exception.toString());
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getMontant().");
        }
        return l_montant;
    }

    /**
     * Mï¿½thode qui ajoute ï¿½ l'url de rï¿½ponse automatique le jsessionid.
     * 
     * @param a_request
     *            La requï¿½te HTTP.
     * @param a_call
     *            L'appel SIPS.
     * 
     * @return String L'url de rï¿½ponse automatique suivie du jsessionid.
     */
    private String getUrlReponseAuto(DynamoHttpServletRequest a_request, SIPSDataObject a_call)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getUrlReponseAuto().");
        }

        String l_reponseAuto = null;

        l_reponseAuto = getAutoResponseUrl();

        if (isLoggingDebug())
        {
            logDebug("|--> reponse auto : " + l_reponseAuto);
        }

        l_reponseAuto += ";jsessionid=" + a_request.getSession().getId().toString();

        if (isLoggingDebug())
        {
            logDebug("|--> reponse auto + jsessionid: " + l_reponseAuto);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getUrlReponseAuto().");
        }

        return l_reponseAuto;
    }

    /**
     * Mï¿½thode qui renvoit l'identifiant de la transaction pour le paiement. Le
     * calcul de l'identifiant de transaction est basï¿½ sur une sï¿½quence Oracle.
     * 
     * @return String L'identifiant de la transaction pour le paiement.
     */
    private synchronized String getIdTransaction()
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getIdTransaction().");
        }

        Connection l_connection = null;
        PreparedStatement l_statement = null;
        ResultSet l_result = null;
        String l_id = null;

        try
        {
            l_connection = getCastoJdbcTools().getConnection();
            l_statement = l_connection.prepareStatement("select CASTO_TRANSACTION_SIPS_ID.nextval from DUAL");
            l_result = l_statement.executeQuery();

            if (null != l_result)
            {
                if (l_result.next())
                {
                    l_id = l_result.getString(1);
                }
            }
        }
        catch (Exception l_exception)
        {
            logError(l_exception.toString());
        }
        finally
        {
            getCastoJdbcTools().closeConnection(l_result, l_connection, l_statement);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.commande.paiement.CastoMercanet.getIdTransaction().");
        }
        return l_id;
    }
}