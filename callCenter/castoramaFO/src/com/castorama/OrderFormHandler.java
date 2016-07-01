package com.castorama;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.OrderImpl;
import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.servlet.RepositoryFormHandler;
import atg.service.lockmanager.DeadlockException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.castorama.atout.BeanChiffrement;
import com.castorama.commande.CastoOrderTools;
import com.castorama.commerce.states.BOOrderStates;
import com.castorama.config.Configuration;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.utils.CheckingTools;
import com.castorama.utils.MailUtils;
import com.castorama.utils.ServerSetting;

/**
 * OrderFormHandler : Castorama 2001 Ce composant est utilisé pour l'edition
 * des commandes é partir de formulaires (call_center).
 * 
 * @version 1.0
 * @author Damien DURIEZ - INTERNENCE (Juillet 2001)
 */
public class OrderFormHandler extends RepositoryFormHandler
{

    /*
     * public static String CODEBIN = "504526" ; public static String[]
     * COEMETTEURS = { "014" , "015" , "016" };
     */

    private String m_strCodeBin; // Code bin

    private String[] m_strCoemetteurs; // coemetteurs

    private Profile m_Profile; // Profil é mettre é jour lors d'une commande
                                // par carte atout

    private boolean m_bCommit; // Commit de l'order lors de l'updateAtout ?
                                // true/false

    private boolean m_bValidAtout; // La commande payée par carte atout
                                    // est-elle valide ? true/false

    protected String m_strMailReceptionChequeXslTemplateURL; // URL du
                                                                // template xsl
                                                                // pour le mail
                                                                // de
                                                                // confirmation
                                                                // de
                                                                // receptionCheque

    protected Repository m_ProfileRepository;
    
    
    private String m_urlSite;
    
    private String m_urlBaseImg;
    
    private String m_castoMail;

    private MailUtils m_mailUtils;
    
    private MutableRepository m_journalisationRepository;
    
    private ServerSetting serverSetting;
    
	private BOOrderStates boOrderStates;

	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

	public BOOrderStates getBoOrderStates() {
		return boOrderStates;
	}

	public void setBoOrderStates(BOOrderStates boOrderStates) {
		this.boOrderStates = boOrderStates;
	}
   
    public MutableRepository getJournalisationRepository()
    {
        return m_journalisationRepository;
    }

    public void setJournalisationRepository(MutableRepository a_journalisationRepository)
    {
        m_journalisationRepository = a_journalisationRepository;
    }

    public String getUrlSite()
    {
        return m_urlSite;
    }
    
    public void setUrlSite(String a_urlSite)
    {
        m_urlSite = a_urlSite;
    }
    
    
    public String getUrlBaseImg()
    {
        return m_urlBaseImg;
    }
    
    public void setUrlBaseImg(String a_urlBaseImg)
    {
        m_urlBaseImg = a_urlBaseImg;
    }
    
    public String getCastoMail()
    {
        return m_castoMail;
    }
    
    public void setCastoMail(String a_castoMail)
    {
        m_castoMail = a_castoMail;
    }
    
    public MailUtils getMailUtils()
    {
        return m_mailUtils;
    }

  
    public void setMailUtils(MailUtils a_mailUtils)
    {
        m_mailUtils = a_mailUtils;
    }
    
    

    /**
     * R�f�rence vers le composant d'utilitaire pour les commandes.
     */
    private CastoOrderTools m_orderTools;

    /**
     * Récupération du ProfileRepository
     * 
     * @param none
     * @return Repository ProfileRepository
     * @throws none
     */
    public Repository getProfileRepository()
    {
        return m_ProfileRepository;
    }

    /**
     * Modification du ProfileRepository
     * 
     * @param Repository
     *            ProfileRepository
     * @return none
     * @throws none
     */
    public void setProfileRepository(Repository a_ProfileRepository)
    {
        m_ProfileRepository = a_ProfileRepository;
    }

    /**
     * Récupération du MailReceptionChequeXslTemplateURL
     * 
     * @param none
     * @return String MailReceptionChequeXslTemplateURL
     * @throws none
     */
    public String getMailReceptionChequeXslTemplateURL()
    {
        return m_strMailReceptionChequeXslTemplateURL;
    }

    /**
     * Modification du MailReceptionChequeXslTemplateURL
     * 
     * @param String
     *            MailReceptionChequeXslTemplateURL
     * @return none
     * @throws none
     */
    public void setMailReceptionChequeXslTemplateURL(String a_strMailReceptionChequeXslTemplateURL)
    {
        m_strMailReceptionChequeXslTemplateURL = a_strMailReceptionChequeXslTemplateURL;
    }

    /**
     * Récupération du CodeBin
     * 
     * @param none
     * @return none
     * @throws none
     */
    public String getCodeBin()
    {

        return m_strCodeBin;
    }

    /**
     * Modification du CodeBin
     * 
     * @param String
     *            CodeBin
     * @return none
     * @throws none
     */
    public void setCodeBin(String a_strCodeBin)
    {

        m_strCodeBin = a_strCodeBin;

    }

    /**
     * Récupération des Coemetteurs
     * 
     * @param none
     * @return none
     * @throws none
     */
    public String[] getCoemetteurs()
    {

        return m_strCoemetteurs;
    }

    /**
     * Modification des Coemetteurs
     * 
     * @param String[]
     *            Coemetteurs
     * @return none
     * @throws none
     */
    public void setCoemetteurs(String[] a_strCoemetteurs)
    {

        m_strCoemetteurs = a_strCoemetteurs;

    }

    /**
     * Récupération du profil
     * 
     * @param none
     * @return Profil user
     * @throws none
     */
    public Profile getProfile()
    {

        return m_Profile;
    }

    /**
     * Modification du profil
     * 
     * @param Profil
     *            user
     * @return none
     * @throws none
     */
    public void setProfile(Profile a_Profile)
    {

        m_Profile = a_Profile;

    }

    /**
     * Récupération du Commit
     * 
     * @param none
     * @return boolean Commit
     * @throws none
     */
    public boolean getCommit()
    {

        return m_bCommit;
    }

    /**
     * Modification du Commit
     * 
     * @param boolean
     *            Commit
     * @return none
     * @throws none
     */
    public void setCommit(boolean a_bCommit)
    {

        m_bCommit = a_bCommit;

    }

    /**
     * Récupération du ValidAtout
     * 
     * @param none
     * @return boolean ValidAtout
     * @throws none
     */
    public boolean getValidAtout()
    {

        return m_bValidAtout;
    }

    /**
     * Modification du ValidAtout
     * 
     * @param boolean
     *            ValidAtout
     * @return none
     * @throws none
     */
    public void setValidAtout(boolean a_bValidAtout)
    {

        m_bValidAtout = a_bValidAtout;

    }

    /**
     * Renvoie une r�f�rence vers le composant d'utilitaires des commandes.
     * 
     * @return CastoOrderTools Une r�f�rence vers les composant d'utilitaires
     *         des commandes.
     */
    public CastoOrderTools getOrderTools()
    {
        return m_orderTools;
    }

    /**
     * Fixe la r�f�rence vers le composant d'utilitaires des commandes.
     * 
     * @param a_orderTools
     *            La nouvelle r�f�rence vers le composant d'utilitaires des
     *            commandes.
     */
    public void setOrderTools(CastoOrderTools a_orderTools)
    {
        m_orderTools = a_orderTools;
    }

    /**
     * Mise é jour d'une commande dans le cas du paiement par SIPS dans le
     * call_center: <br>
     * La date d'export doit étre mise é null, ainsi que les champs concernant
     * <br>
     * les autres types de paiement. Cette faéon de mise é jour est
     * particuliére puisque appelelée dans le callback de SIPS: cette méthode
     * est invoquée par un setvalue.
     * 
     * @param String
     *            a_strOrderId
     * @return none
     * @throws none
     */
    public void setUpdateOrderAfterSIPSCallCenter(String a_strOrderId)
    {
        // trace.logOpen(this,".setUpdateOrderAfterSIPSCallCenter");
        try
        {
            setUpdateOrderAfterSIPS(a_strOrderId, "Call-Center");
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".setUpdateOrderAfterSIPSCallCenter
            // Exception : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".setUpdateOrderAfterSIPSCallCenter");
        }
    }

    /**
     * Mise é jour d'une commande dans le cas du paiement par SIPS dans le
     * site: <br>
     * La date d'export doit étre mise é null, ainsi que les champs concernant
     * <br>
     * les autres types de paiement. Cette faéon de mise é jour est
     * particuliére puisque appelelée dans le callback de SIPS: cette méthode
     * est invoquée par un setvalue.
     * 
     * @param String
     *            a_strOrderId
     * @return none
     * @throws none
     */
    public void setUpdateOrderAfterSIPSSite(String a_strOrderId)
    {
        // trace.logOpen(this,".setUpdateOrderAfterSIPSSite");
        try
        {
            setUpdateOrderAfterSIPS(a_strOrderId, "creditCard");
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".setUpdateOrderAfterSIPSSite Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".setUpdateOrderAfterSIPSSite");
        }
    }

    /**
     * Mise é jour d'une commande dans le cas du paiement par SIPS: <br>
     * La date d'export doit étre mise é null, ainsi que les champs concernant
     * <br>
     * les autres types de paiement. Cette faéon de mise é jour est
     * particuliére puisque appelelée dans le callback de SIPS: cette méthode
     * est invoquée par un setvalue.
     * 
     * @param String
     *            a_strOrderId
     * @param String
     *            a_strPaiementMethod
     * @return none
     * @throws none
     */
    public void setUpdateOrderAfterSIPS(String a_strOrderId, String a_strPaiementMethod)
    {
        // trace.logOpen(this,".setUpdateOrderAfterSIPS");

        Configuration l_Configuration = Configuration.getConfiguration();
        try
        {

            l_Configuration.getClientLockManager().acquireWriteLock(a_strOrderId);

            MutableRepository l_OrderRepository = (MutableRepository) getRepository();
            MutableRepositoryItem l_order = (MutableRepositoryItem) l_OrderRepository.getItemForUpdate(a_strOrderId,
                    "order");

		/* CR: Modification mod�le de donn�es */
            l_order.setPropertyValue("libelleBanque_avtCodeReview", null);
            l_order.setPropertyValue("numcheque_avtCodeReview", null);
            l_order.setPropertyValue("montantChequeFrancs_avtCodeReview", null);
            l_order.setPropertyValue("montantChequeEuros_avtCodeReview", null);
            l_order.setPropertyValue("numCarteAtout_avtCodeReview", null);
            l_order.setPropertyValue("dateValidAtout_avtCodeReview", null);
		/* !CR: Modification mod�le de donn�es */
            l_order.setPropertyValue("exportdate", null);
            l_order.setPropertyValue("lastModifiedDate", new Date());
            l_OrderRepository.updateItem(l_order);

            List l_PaymentGroups = (List) l_order.getPropertyValue("paymentGroups");
            MutableRepositoryItem l_PaymentGroup = (MutableRepositoryItem) l_PaymentGroups.get(0);
            if (l_PaymentGroup != null)
            {
                /* CR:20/08/2008:Modification du mod�le de donn�es */
                l_PaymentGroup.setPropertyValue("numcheque", null);
                l_PaymentGroup.setPropertyValue("libelleBanque", null);
                l_PaymentGroup.setPropertyValue("dateValidAtout", null);
                l_PaymentGroup.setPropertyValue("montantChequeFrancs", null);
                l_PaymentGroup.setPropertyValue("montantChequeEuros", null);
                /* !CR:20/08/2008:Modification du mod�le de donn�es */
                
                l_PaymentGroup.setPropertyValue("paymentMethod", a_strPaiementMethod);
                l_OrderRepository.updateItem(l_PaymentGroup);

                // Modification G.P. 29/01/2007
                // On doit mettre � jour le stock si la commande est pay�e...
                getOrderTools().updateInventory(a_strOrderId);

            }

        }
        catch (Exception e)
        {
            // trace.logError(this,e,".setUpdateOrderAfterSIPS
            // orderId="+a_strOrderId+" : "+e.toString());
        }
        finally
        {
            try
            {
                l_Configuration.getClientLockManager().releaseWriteLock(a_strOrderId);
            }
            catch (Exception e)
            {
                // trace.logError(this,e,".setUpdateOrderAfterSIPS
                // orderId="+a_strOrderId+" : "+e);
            }
            // trace.logClose(this,".setUpdateOrderAfterSIPS");
        }
    }

    /**
     * Récupération du Numero de carte atout décodé <br>
     * Renvoi une chaine vide si le numero de carte atout de l'item courant est
     * null ou ne commence pas par un coemetteur valide <br>
     * Cette méthode est utilisée pour préremplir le champ numéro de carte
     * atout dans le call center
     * 
     * @param none
     * @return String Numero de carte atout décodé
     * @throws none
     */
    public String getNumeroCarteAtout()
    {

        // trace.logOpen(this,".getNumeroCarteAtout");
        String l_strRetour = "";
        try
        {
            BeanChiffrement l_BeanChiffrement = (BeanChiffrement) Nucleus.getGlobalNucleus().resolveName(
                    "/castorama/atout/BeanChiffrement");
            String l_strNumAtout = (String) getItemProperty("numCarteAtout");
            if (l_strNumAtout != null && l_BeanChiffrement != null)
            {
                l_strRetour = l_BeanChiffrement.decode(l_strNumAtout);

                // Modification temporaire (?) G.P. 26/01/2007
                // Si la variable n'�tait pas encod�e, on reprend l'ancienne
                // valeur
                if ("*** DES INPUT BUFFER NOT A MULTIPLE OF 8 BYTES - IGNORING EXCEPTION ***19"
                        .equalsIgnoreCase(l_strRetour))
                {
                    l_strRetour = l_strNumAtout;
                }
            }
        }
        catch (Exception e)
        {
            l_strRetour = "";
            // trace.logError(this,e,".getNumeroCarteAtout Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".getNumeroCarteAtout");
        }
        return l_strRetour;
    }

    /**
     * Récupération du Numero de carte atout décodé <br>
     * Renvoi une chaine vide si le numero de carte atout de l'item courant est
     * null ou ne commence pas par un coemetteur valide <br>
     * Cette méthode est utilisée pour préremplir le champ numéro de carte
     * atout dans le call center
     * 
     * @param none
     * @return String Numero de carte atout décodé
     * @throws none
     */
    public String getNumeroCarteAtoutWithoutCodeBin()
    {

        // trace.logOpen(this,".getNumeroCarteAtout");
        String l_strRetour = "";
        try
        {
            BeanChiffrement l_BeanChiffrement = (BeanChiffrement) Nucleus.getGlobalNucleus().resolveName(
                    "/castorama/atout/BeanChiffrement");
            String l_strNumAtout = (String) getItemProperty("numCarteAtout");
            if (l_strNumAtout != null && l_BeanChiffrement != null)
            {
                l_strRetour = l_BeanChiffrement.decode(l_strNumAtout);

                // Modification temporaire (?) G.P. 26/01/2007
                // Si la variable n'�tait pas encod�e, on reprend l'ancienne
                // valeur
                if ("*** DES INPUT BUFFER NOT A MULTIPLE OF 8 BYTES - IGNORING EXCEPTION ***19"
                        .equalsIgnoreCase(l_strRetour))
                {
                    l_strRetour = l_strNumAtout;
                }

            }

            if (l_strRetour != null && m_strCodeBin != null && l_strRetour.startsWith(m_strCodeBin))
            {
                l_strRetour = l_strRetour.substring(m_strCodeBin.length());
            }
        }
        catch (Exception e)
        {
            l_strRetour = "";
            // trace.logError(this,e,".getNumeroCarteAtout Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".getNumeroCarteAtout");
        }
        return l_strRetour;
    }

    /**
     * Mise é jour d'une commande: <br>
     * La date d'export doit étre mise é null<br>
     * pour que la commande soit prise en compte lors du prochain export
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleUpdate(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {

        // trace.logOpen(this,".handleUpdate");
        boolean l_bOk = true;
        Configuration l_Configuration = Configuration.getConfiguration();
        try
        {

            l_Configuration.getClientLockManager().acquireWriteLock(getRepositoryId());

            setValueProperty("exportdate", null);
            setValueProperty("LastModifiedDate", new Date());
            // ****** Log *****
            BeanCreateLogAdmin.createLog(a_Request, a_Response, "CallCenter", "Modification",
                    "Mise a jour de la commande " + getRepositoryId());
            l_bOk = super.handleUpdate(a_Request, a_Response);

        }
        catch (Exception e)
        {
            l_bOk = false;
            // trace.logError(this,e,".handleUpdate
            // orderId="+getRepositoryId()+" : "+e.toString());
        }
        finally
        {
            try
            {
                l_Configuration.getClientLockManager().releaseWriteLock(getRepositoryId());
            }
            catch (Exception e)
            {
                // trace.logError(this,e,".handleUpdate
                // orderId="+getRepositoryId()+" : "+e);
            }
            // trace.logClose(this,".handleUpdate");
        }
        return true;

    }

    /**
     * Mise é jour d'une commande pour réception d'un chéque
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * 
     * @return false si une redirection est nécessaire, true sinon.
     * 
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleUpdateCheque(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleUpdateCheque");
        boolean l_bOk = true;
        Configuration l_Configuration = Configuration.getConfiguration();

        try
        {
            l_Configuration.getClientLockManager().acquireWriteLock(getRepositoryId());
            
            if (isLoggingInfo())
            {
                logInfo("OrderFormHandler.handleUpdateCheque() : client lock manager ok.");
            }
            
            l_bOk &= CheckingTools.checkString((String) getValueProperty("paymentGroups[0].numcheque"), "numchequeSyntaxError",
                    (GenericFormHandler) this);
            
            if (isLoggingInfo())
            {
                logInfo("OrderFormHandler.handleUpdateCheque() : num cheque : " + l_bOk);
            }
            
            l_bOk &= CheckingTools.checkString((String) getValueProperty("paymentGroups[0].libelleBanque"), "libelleBanqueSyntaxError",
                    (GenericFormHandler) this);
            
            if (isLoggingInfo())
            {
                logInfo("OrderFormHandler.handleUpdateCheque() : libelle banque : " + l_bOk);
            }
            
            l_bOk &= CheckingTools.checkDouble(new Double(getValueProperty("paymentGroups[0].montantChequeEuros").toString()), "montantChequeError",
                    (GenericFormHandler) this);
            
            if (isLoggingInfo())
            {
                logInfo("OrderFormHandler.handleUpdateCheque() : montant cheque euro : " + l_bOk);
            }
            
            if (l_bOk)
            {
                l_Configuration.getClientLockManager().acquireWriteLock(getRepositoryId());
                
                if (isLoggingInfo())
                {
                    logInfo("OrderFormHandler.handleUpdateCheque() : lock ok : " + l_bOk);
                }
                
                Boolean l_ChequeNonValide = Boolean.FALSE;
                
                try
                {
                    l_ChequeNonValide = new Boolean(getValueProperty("paymentGroups[0].chequeNonValide").toString());
                    
                    if (isLoggingInfo())
                    {
                        logInfo("OrderFormHandler.handleUpdateCheque() : cheque non valide : " + l_ChequeNonValide);
                    }
                    
                    if (l_ChequeNonValide != null && !l_ChequeNonValide.booleanValue())
                    {
                        setValueProperty("BOState", "VALIDE");
                        setValueProperty("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.VALIDE));
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : commande fixee a valide.");
                        }
                        
                        setValueProperty("BOStateDetail", "La commande est valid�e");
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : detail bo state mis a jour.");
                        }
                        
                        setDateExportNull();
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : date d'export a null.");
                        }
                        
                        //envoyerMailConfirmationReceptionCheque();
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : envoi mail de reception cheque.");
                        }

                        // Modification G.P. 29/01/2007
                        // On doit mettre � jour le stock si la commande est
                        // pay�e...
                        getOrderTools().updateInventory(getRepositoryId());
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : stock mis a jour.");
                        }
                        setValueProperty("state", "SUBMITTED");
                    }
                    else
                    {
                        setValueProperty("BOState", "PENDING_CHEQUE");
                        setValueProperty("BOStateNum", getBoOrderStates().getStateValue(BOOrderStates.PENDING_CHEQUE));
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : etat fixe a pending cheque.");
                        }
                        
                        setValueProperty("BOStateDetail", "En attente de r&eacute;ception ch&egrave;que.");
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : detail bo state mis a jour.");
                        }
                    }
                    setValueProperty("lastModifiedDate", new Date());
                    
                    if (isLoggingInfo())
                    {
                        logInfo("OrderFormHandler.handleUpdateCheque() : date de derniere mise a jour fixee.");
                    }
                    
                    // ****** Log de la réception du chéque *****
                    BeanCreateLogAdmin.createLog(a_Request, a_Response, "CallCenter", "Modification",
                            "R�ception du ch�que " + (String) getValueProperty("paymentGroups[0].numcheque") + " pour la commande "
                                    + getRepositoryId());
                    setValueProperty("LastModifiedDate", new Date());
                    
                    if (isLoggingInfo())
                    {
                        logInfo("OrderFormHandler.handleUpdateCheque() : date de derniere mise a jour fixée.");
                    }
                }
                finally
                {
                    try
                    {
                        l_Configuration.getClientLockManager().releaseWriteLock(getRepositoryId());
                        
                        if (isLoggingInfo())
                        {
                            logInfo("OrderFormHandler.handleUpdateCheque() : lock ote.");
                        }
                    }
                    catch (Exception l_e)
                    {
                        logError("OrderFormHandler.handleUpdateCheque() : " + l_e.toString());
                    }
                }
                
                
                try 
                {
                    MutableRepository l_mutRep = getJournalisationRepository();
                    MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                    l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                    l_journalisation.setPropertyValue("action","validation_CHEQUE");
                    l_journalisation.setPropertyValue("userId",a_Request.getParameter("userId"));
                    l_journalisation.setPropertyValue("login",a_Request.getParameter("userLogin"));
                    l_journalisation.setPropertyValue("orderId",a_Request.getParameter("numeroCommande"));
                    l_mutRep.addItem(l_journalisation);
                }
                catch (Exception l_e)
                {
                    logError(l_e);
                }
                                
                l_bOk = super.handleUpdate(a_Request, a_Response);

            }
            else
            {
                logError("OrderFormHandler.handleUpdateCheque() 2 : ok non valide.");
            }
        }
        catch (Exception l_e)
        {
            logError("OrderFormHandler.handleUpdateCheque() 3 : ", l_e);
        }
        return l_bOk;
    }

    /**
     * Envoi d'un mail é l'internaute, lui confirmant la réception de son
     * chéque
     * 
     * @param none
     * @return none
     * @exception none
     */
    public void envoyerMailConfirmationReceptionCheque()
    {
        try
        {
            RepositoryItem l_Order = getRepositoryItem();
            
            /*
             * r�cup�ration des informations � placer dans le mail
             */
            RepositoryItem l_User = m_ProfileRepository.getItem((String) l_Order.getPropertyValue("profileId"), "user");
            
            //si la civilit� est "Mlle", on met "Mademoiselle"
            String l_cher = null;
            String l_strCivilite = (String) l_User.getPropertyValue("civilite");
            if (l_strCivilite != null) {
            	if ("mr".equals(l_strCivilite)) {
                    l_cher = "Cher";
                    l_strCivilite = "monsieur";
            	} else if ("miss".equals(l_strCivilite)) {
                        l_cher = "Ch&egrave;re";
                        l_strCivilite = "Mademoiselle";
                } else if ("mrs".equals(l_strCivilite)) {
                        l_cher = "Ch&egrave;re";
                        l_strCivilite = "Madame";
                } else {
                	l_cher = "";
                }
            	
            } else {
            	l_strCivilite = "";
            }

            // On met une majuscule au pr�nom
            String l_strFirstName = (String) l_User.getPropertyValue("firstName");
            l_strFirstName = l_strFirstName.substring(0,1).toUpperCase() + l_strFirstName.substring(1);
            
            // on met le nom en majuscule
            String l_strLastName = (String) l_User.getPropertyValue("lastName");
            l_strLastName = l_strLastName.toUpperCase();
            
            /*
             * r�cup�ration du code client � placer dans le mail
             */ 
            String l_codeClient = l_Order.getPropertyValue("profileId").toString();

            // r�cup�ration de "averti" au f�minin ou pluriel en fonction de
            // civilite
            String l_strAverti = (l_strCivilite.equalsIgnoreCase("Madame") || l_strCivilite
                    .equalsIgnoreCase("Mademoiselle")) ? "avertie" : "averti";

            /*
             * construction du corps du mail
             */
            String l_corpsMessage = ResourceUtils.getUserMsgResource("commande.job.confirmation.reception.texte",
                    CastoConstantesCommande.BUNDLE_COMMANDES, null, new Object[]
                    {
                        l_Order.getRepositoryId(),
                        getServerSetting().getHost() + getUrlBaseImg(),
                        l_codeClient,
                        l_strCivilite,
                        l_strFirstName,
                        l_strLastName,
                        l_cher,
                        l_strAverti,
                        getServerSetting().getHost()
                        });
            /*
             * construction du sujet du mail
             */    
            String l_subject = ResourceUtils.getUserMsgResource("commande.job.confirmation.reception.titre",
                    CastoConstantesCommande.BUNDLE_COMMANDES, null, new String[]
                    { l_Order.getRepositoryId()});
            
            /*
             * Obtention du destinataire
             */
            String l_destinataire = (String) l_User.getPropertyValue("email");
            
            
            
            /*
             * Envoi du mail
             */
            if (null != l_destinataire)
            {
                getMailUtils().sendMailHTML(getCastoMail(), l_subject, l_destinataire,
                        l_corpsMessage);
            }
            
        }
        catch (Exception e)
        {
            if(isLoggingError())
            {
                logError("ERREUR LORS DE L'ENVOID U MAIL DE CONFIRMATION DE RECEPTION DU CHEQUE : "+e);
            }
        }
    }

    /**
     * Mise é jour d'une commande pour paiement par carte Atout <br>
     * le numéro de carte léAtout est composé du code bin 504526 et du
     * numéro de carte é 13 chiffres Les 13 chiffres du numéro de carte
     * doivent étre tous saisis (et étre numériques) <br>
     * Les 3 premiers des 13 chiffres doivent correspondre aux numéros de
     * coémetteurs 014, 015 et 016 <br>
     * Le numéro de carte léAtout sera stocké dans les données de la
     * commande (mais jamais affiché au client) et crypté par algorithme DES
     * 56 bits fourni par un package du noyau Oracle (la clé sera renseignée
     * é la compilation) sur le site MGN <br>
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleUpdateAtout(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleUpdateAtout");
        boolean l_bOk = true;

        try
        {
            SessionBeanOrder l_SessionBeanOrder = (SessionBeanOrder) a_Request
                    .resolveName("/castorama/SessionBeanOrder");
            l_SessionBeanOrder.setErreurNumOrDateAtout(false);
            String l_strNumero = (String) getValueProperty("numCarteAtout");
            Date l_DateValid = (Date) getValueProperty("dateValidAtout");

            String profId = a_Request.getParameter("profId");

            l_bOk = CheckingTools.checkLong(l_strNumero, "numCarteAtoutError", (GenericFormHandler) this); // numCarteAtoutNonNumeric
            l_bOk &= checkNbChiffres(l_strNumero, "numCarteAtoutError"); // numCarteAtoutNbChiffresError
            l_bOk &= checkCoemetteurs(l_strNumero, "numCarteAtoutError"); // numCarteAtoutCoemetteursError
            l_bOk &= CheckingTools.checkDateValid(l_DateValid, "dateValidAtoutCoemetteursError",
                    (GenericFormHandler) this);
            if (l_bOk)
            {

                updateProfilPossedeCarteAtout(profId);

                if (m_bCommit)
                {
                    CastoShoppingCartModifier l_CastoShoppingCartModifier = (CastoShoppingCartModifier) a_Request
                            .resolveName("/castorama/CastoShoppingCartModifier");
                    l_CastoShoppingCartModifier.handleMoveToOrderCommit(a_Request, a_Response);
                }

                BeanChiffrement l_BeanChiffrement = (BeanChiffrement) a_Request
                        .resolveName("/castorama/atout/BeanChiffrement");
                String l_strNumeroEncode = l_BeanChiffrement.encode(m_strCodeBin + l_strNumero);

                setValueProperty("numCarteAtout", l_strNumeroEncode);
                setDateExportNull();

                if (m_bValidAtout)
                {
                    setValueProperty("BOState", "VALIDE");
                    setValueProperty("BOStateDetail", "La commande est valid�e");

                    // Modification G.P. 29/01/2007
                    // On doit mettre � jour le stock si la commande est
                    // pay�e...
                    getOrderTools().updateInventory(getRepositoryId());

                }
                setValueProperty("lastModifiedDate", new Date());

                // ****** Log de la validation carte atout *****
                if (!m_bCommit)
                {
                    BeanCreateLogAdmin.createLog(a_Request, a_Response, "CallCenter", "Modification",
                            "Validation de la carte l'Atout " + l_strNumeroEncode + " pour la commande "
                                    + getRepositoryId());
                }
                setValueProperty("LastModifiedDate", new Date());

                l_bOk = super.handleUpdate(a_Request, a_Response);

                Repository l_Repository = getRepository();
                RepositoryItem l_OrderItem = l_Repository.getItem(getRepositoryId(), "order");
                if (l_OrderItem.getPropertyValue("numCarteAtout") == null
                        || l_OrderItem.getPropertyValue("dateValidAtout") == null
                        || ((String) (l_OrderItem.getPropertyValue("numCarteAtout"))).equals(""))
                {
                    SimpleDateFormat l_SimpleDateFormater = new SimpleDateFormat("dd/MM/yyyy");
                    // BeanCreateLogAdmin l_BeanCreateLogAdmin =
                    // (BeanCreateLogAdmin)
                    // a_Request.resolveName("/castorama/BeanCreateLogAdmin");
                    // l_BeanCreateLogAdmin.enregistrement("Commande","ErreurNumOrDateAtout","Pour
                    // la commande "+getRepositoryId()+" :
                    // numCarteAtout="+l_strNumero+" ---
                    // dateValidAtout="+l_SimpleDateFormater.format(l_DateValid),a_Request.getRemoteAddr(),(String)m_Profile.getPropertyValue("login"));
                    // trace.logError(this,".handleUpdateAtout Erreur de mise a
                    // jour des donnees du paiement atout pour la commande
                    // "+getRepositoryId());
                    l_SessionBeanOrder.setErreurNumOrDateAtout(true);
                }

            }
        }
        catch (Exception e)
        {
            l_bOk = false;
            // trace.logError(this,e,".handleUpdateAtout
            // orderId="+getRepositoryId()+" : "+e.toString());
        }
        finally
        {
            try 
            {
                MutableRepository l_mutRep = getJournalisationRepository();
                MutableRepositoryItem l_journalisation = l_mutRep.createItem("journalisationCC");
                l_journalisation.setPropertyValue("dateAction", new java.util.Date());
                l_journalisation.setPropertyValue("action","validation_ATOUT");
                l_journalisation.setPropertyValue("userId",a_Request.getParameter("userId"));
                l_journalisation.setPropertyValue("login",a_Request.getParameter("userLogin"));
                l_journalisation.setPropertyValue("orderId",a_Request.getParameter("numeroCommande"));
                l_mutRep.addItem(l_journalisation);
            }
            catch (Exception l_e)
            {
                logError(l_e);
            }
        }
        return true;
    }

    /**
     * Commit de la commande pour paiement par carte Atout <br>
     * le numéro de carte léAtout est composé du code bin 504526 et du
     * numéro de carte é 13 chiffres Les 13 chiffres du numéro de carte
     * doivent étre tous saisis (et étre numériques) <br>
     * Les 3 premiers des 13 chiffres doivent correspondre aux numéros de
     * coémetteurs 014, 015 et 016 <br>
     * Le numéro de carte léAtout sera stocké dans les données de la
     * commande (mais jamais affiché au client) et crypté par algorithme DES
     * 56 bits fourni par un package du noyau Oracle (la clé sera renseignée
     * é la compilation) sur le site MGN <br>
     * 
     * @param a_Request
     *            the servlet's request
     * @param a_Response
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    public boolean handleCommitAtout(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleCommitAtout");
        boolean l_bOk = true;

        try
        {
            SessionBeanOrder l_SessionBeanOrder = (SessionBeanOrder) a_Request
                    .resolveName("/castorama/SessionBeanOrder");
            l_SessionBeanOrder.setErreurNumOrDateAtout(false);
            String l_strNumero = (String) getValueProperty("numCarteAtout");
            Date l_DateValid = (Date) getValueProperty("dateValidAtout");
            String l_strOptionPaiementAtout = (String) getValueProperty("optionPaiementAtout");

            /*
             * ICI
             */
            String profId = a_Request.getParameter("profId");

            l_bOk = CheckingTools.checkLong(l_strNumero, "numCarteAtoutError", (GenericFormHandler) this); // numCarteAtoutNonNumeric
            l_bOk &= checkNbChiffres(l_strNumero, "numCarteAtoutError"); // numCarteAtoutNbChiffresError
            l_bOk &= checkCoemetteurs(l_strNumero, "numCarteAtoutError"); // numCarteAtoutCoemetteursError
            l_bOk &= CheckingTools.checkDateValid(l_DateValid, "dateValidAtoutCoemetteursError",
                    (GenericFormHandler) this);
            if (l_bOk)
            {

                updateProfilPossedeCarteAtout(profId);

                CastoShoppingCartModifier l_CastoShoppingCartModifier = (CastoShoppingCartModifier) a_Request
                        .resolveName("/castorama/CastoShoppingCartModifier");
                OrderImpl l_Order = (OrderImpl) l_CastoShoppingCartModifier.getOrder();

                BeanChiffrement l_BeanChiffrement = (BeanChiffrement) a_Request
                        .resolveName("/castorama/atout/BeanChiffrement");
                String l_strNumeroEncode = l_BeanChiffrement.encode(m_strCodeBin + l_strNumero);

                l_Order.setPropertyValue("numCarteAtout", l_strNumeroEncode);
                l_Order.setPropertyValue("dateValidAtout", l_DateValid);
                l_Order.setPropertyValue("optionPaiementAtout", l_strOptionPaiementAtout);
                l_Order.setPropertyValue("exportdate", null);

                l_CastoShoppingCartModifier.handleMoveToOrderCommit(a_Request, a_Response);

                l_bOk = !l_CastoShoppingCartModifier.getFormError();
            }
            /*
             * Repository l_Repository = getRepository(); RepositoryItem
             * l_OrderItem = l_Repository.getItem(getRepositoryId(),"order");
             * if(l_OrderItem.getPropertyValue("numCarteAtout")==null ||
             * l_OrderItem.getPropertyValue("dateValidAtout")==null ||
             * ((String)(l_OrderItem.getPropertyValue("numCarteAtout"))).equals("")) {
             * SimpleDateFormat l_SimpleDateFormater = new
             * SimpleDateFormat("dd/MM/yyyy"); BeanCreateLogAdmin
             * l_BeanCreateLogAdmin = (BeanCreateLogAdmin)
             * a_Request.resolveName("/castorama/BeanCreateLogAdmin");
             * l_BeanCreateLogAdmin.enregistrement("Commande","ErreurNumOrDateAtout","Pour
             * la commande "+getRepositoryId()+" : numCarteAtout="+l_strNumero+"
             * ---
             * dateValidAtout="+l_SimpleDateFormater.format(l_DateValid),a_Request.getRemoteAddr(),(String)m_Profile.getPropertyValue("login"));
             * //trace.logError(this,".handleUpdateAtout Erreur de mise a jour
             * des donnees du paiement atout pour la commande
             * "+getRepositoryId());
             * l_SessionBeanOrder.setErreurNumOrDateAtout(true); }
             */
        }
        catch (Exception e)
        {
            l_bOk = false;
            // trace.logError(this,e,".handleCommitAtout
            // orderId="+getRepositoryId()+" : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleCommitAtout");
        }
        return true;
    }

    /**
     * Mise é jour du Profil d'une commande par carte atout: flag
     * carteAtout=true
     * 
     * @param none
     * @return boolean erreur True/False
     * @exception none
     */
    private void updateProfilPossedeCarteAtout(String a_profId)
    {
        // trace.logOpen(this,".updateProfilPossedeCarteAtout");
        try
        {
            MutableRepository l_MutableRepository = (MutableRepository) m_Profile.getRepository();
            MutableRepositoryItem l_MutableUser = l_MutableRepository.getItemForUpdate(a_profId, "user");
            l_MutableUser.setPropertyValue("carteAtout", new Boolean(true));
            l_MutableRepository.updateItem(l_MutableUser);
        }
        catch (RepositoryException e)
        {
            // trace.logError(this,e,".updateProfilPossedeCarteAtout
            // RepositoryException : "+e.toString());
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".updateProfilPossedeCarteAtout Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".updateProfilPossedeCarteAtout");
        }
    }

    /**
     * Vérification : le numéro de carte a bien 13 chiffres
     * 
     * @param String
     *            a_strNumero : chaine é tester
     * @return boolean erreur True/False
     * @exception none
     */
    private boolean checkNbChiffres(String a_strNumero, String a_strExceptionName)
    {
        boolean l_bOk = false;
        try
        {
            l_bOk = (a_strNumero.length() == 13);
            if (!l_bOk)
            {
                addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
            }
        }
        catch (Exception e)
        {
            addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
            // trace.logError(this,e,".checkNbChiffres Exception :
            // "+e.toString());
        }
        return l_bOk;
    }

    /**
     * Vérification : la chaine est-elle numérique
     * 
     * @param String
     *            a_strNumero : chaine é tester
     * @return boolean erreur True/False
     * @exception none
     */
    private boolean checkCoemetteurs(String a_strNumero, String a_strExceptionName)
    {
        boolean l_bOk = false;
        try
        {
            if (a_strNumero == null || a_strNumero.length() <= 3)
            {
                l_bOk = false;
            }
            else
            {
                for (int i = 0; i < m_strCoemetteurs.length; i++)
                {
                    l_bOk |= a_strNumero.startsWith(m_strCoemetteurs[i]);
                }
            }
            if (!l_bOk)
            {
                addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
            }
        }
        catch (Exception e)
        {
            addFormException(new DropletException(a_strExceptionName, a_strExceptionName));
            // trace.logError(this,e,".checkCoemetteurs Exception :
            // "+e.toString());
        }
        return l_bOk;
    }

    /**
     * Mise é null de la date d'export de la commande pour qu'elle soit prise
     * en compte au prochain export
     * 
     * @param none
     * @return none
     * @exception none
     */
    private void setDateExportNull()
    {

        // trace.logOpen(this,".setDateExportNull");

        try
        {

            MutableRepository l_OrderRepository = (MutableRepository) getRepository();
            MutableRepositoryItem l_order = (MutableRepositoryItem) l_OrderRepository.getItemForUpdate(
                    getRepositoryId(), "order");
            l_order.setPropertyValue("exportdate", null);
            l_OrderRepository.updateItem(l_order);

        }
        catch (Exception e)
        {
            // trace.logError(this,e,".setDateExportNull
            // orderId="+getRepositoryId()+" : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".setDateExportNull");
        }

    }

}