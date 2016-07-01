package com.castorama;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileAdminFormHandler;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.utils.MailUtils;
import com.castorama.utils.ServerSetting;

public class CastoProfileAdminFormHandler extends ProfileAdminFormHandler
{

    /**. Désigne le descripteur de l'objet newsletter **/
    public static final String ABONNEMENT_NEWSLETTER = "abonnementNewsletter";
    
    /**. Désigne le descripteur de l'objet contactInfo **/
    public static final String SECONDARY_BILLING_ADDRESSES = "secondaryBillingAddresses";
    
    /**. Désigne le descripteur de l'objet contactInfo **/
    public static final String SECONDARY_BILLING_SHIPPING_ADDRESSES = "secondaryBillingShippingAddresses";
    
    /**. Désigne le descripteur de l'objet contactInfo **/
    public static final String BILLING_ADDRESS = "BillingAddress";
    
    /**. Désigne le descripteur de l'objet contactInfo **/
    public static final String SHIPPING_ADDRESS = "ShippingAddress";
    
    /**. Désigne le descripteur de l'objet contactInfo **/
    public static final String HOME_ADDRESS = "HomeAddress";
    
    /**. Désigne le descripteur de la propriété email  **/
    public static final String EMAIL = "email";
    
    /**. Désigne le descripteur de la propriété email  **/
    public static final String CONTACT_INFO_EMAIL = "eMail";
    
    /**. Désigne le descripteur de la propriété emailHTML  **/
    public static final String EMAIL_HTML ="emailHTML";
    
    /**. Désigne le descripteur de la propriété emailStatus  **/
    public static final String EMAIL_STATUS ="emailStatus";
    
    /**. Désigne le descripteur de la propriété receiveEmail  **/
    public static final String RECEIVE_EMAIL = "receiveEmail";
    public static final String RESEIVE_OFFERS = "reseiveOffers";

    /** Type de mise à jour du profil.**/
    private static final Integer MODIFICATION = new Integer(1);
    
    /**. newsletterRepository **/
    private MutableRepository m_newsletterRepository;
    
    // parametres pour les logs
    private String m_strType = "";
    

    private MailUtils m_mailUtils ;
    private String m_urlSite;
    private String m_urlBaseImg;
    private String m_castoMail;
    
    private ServerSetting serverSetting;
    
	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

	public void setType(String a_strType)
    {
        m_strType = a_strType;
    }

    public String getType()
    {
        return m_strType;
    }

    private String m_strAction = "";

    public void setAction(String a_strAction)
    {
        m_strAction = a_strAction;
    }

    public String getAction()
    {
        return m_strAction;
    }

    private String m_strCommentaire = "";

    public void setCommentaire(String a_strCommentaire)
    {
        m_strCommentaire = a_strCommentaire;
    }

    public String getCommentaire()
    {
        return m_strCommentaire;
    }

    private String m_strIp = "";

    public void setIp(String a_strIp)
    {
        m_strIp = a_strIp;
    }

    public String getIp()
    {
        return m_strIp;
    }

    private String m_strNomLogin = "";

    public void setNomLogin(String a_strNomLogin)
    {
        m_strNomLogin = a_strNomLogin;
    }

    public String getNomLogin()
    {
        return m_strNomLogin;
    }
    
    /**
     * @return the mailUtils
     */
    public MailUtils getMailUtils()
    {
        return m_mailUtils;
    }

    /**
     * @param a_mailUtils the mailUtils to set
     */
    public void setMailUtils(MailUtils a_mailUtils)
    {
        m_mailUtils = a_mailUtils;
    }

    /**
     * @return the urlBaseImg
     */
    public String getUrlBaseImg()
    {
        return m_urlBaseImg;
    }

    /**
     * @param a_urlBaseImg the urlBaseImg to set
     */
    public void setUrlBaseImg(String a_urlBaseImg)
    {
        m_urlBaseImg = a_urlBaseImg;
    }
 
    /**
     * @return the urlSite
     */
    public String getUrlSite()
    {
        return m_urlSite;
    }
 
    /**
     * @param a_urlSite the urlSite to set
     */
    public void setUrlSite(String a_urlSite)
    {
        m_urlSite = a_urlSite;
    }
    
    /**
     * @return the castoMail
     */
    public String getCastoMail()
    {
        return m_castoMail;
    }

    /**
     * @param a_castoMail the castoMail to set
     */
    public void setCastoMail(String a_castoMail)
    {
        m_castoMail = a_castoMail;
    }
       
    /**
     * GY : stabilisation : envoi de mail mot de passe.
     * @param pRequest  L'objet Request de la session
     * @param pResponse L'objet Response de la session
     * @return boolean un booléen
     * @throws ServletException L'objet ServletException
     * @throws IOException L'objet IOException
     */
    public boolean handleSendMail(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        try
        {            
            String l_login = pRequest.getParameter("loginDeBase");
            Repository repository = getProfileTools().getProfileRepository();
            RepositoryItem l_profile = null;  
            String l_civilite = null;
            String l_cher = null;
            String newPassword = null;
            
            RepositoryView l_oView = repository.getView("user");
            RqlStatement l_oUsr = RqlStatement.parseRqlStatement("login = ?0");
            RepositoryItem[] l_oUser = l_oUsr.executeQuery(l_oView, new Object[]{ l_login });
            if (l_oUser != null)
            {
                l_profile = l_oUser[0];
        		newPassword = getProfileTools().generateNewPasswordForProfile(l_profile);
                
                l_civilite = (String) l_profile.getPropertyValue("civilite");
                if (l_civilite != null) {
                	if ("mr".equals(l_civilite)) {
                        l_cher = "Cher";
                        l_civilite = "monsieur";
                	} else if ("miss".equals(l_civilite)) {
                            l_cher = "Ch&egrave;re";
                            l_civilite = "Mademoiselle";
                    } else if ("mrs".equals(l_civilite)) {
                            l_cher = "Ch&egrave;re";
                            l_civilite = "Madame";
                    } else {
                    	l_cher = "";
                    }
                	
                } else {
                	l_civilite = "";
                }
                
            }
            
            
            String l_prenom = (String)l_profile.getPropertyValue("firstName");
            String l_nom = (String)l_profile.getPropertyValue("lastName");
            l_prenom = l_prenom.substring(0, 1).toUpperCase() + l_prenom.substring(2);
            l_nom = l_nom.toUpperCase();
            
           
            /*
             * construction du corps du mail
             */
    	
            String l_corpsMessage = ResourceUtils.getUserMsgResource("commande.motdepasse.perdu.texte",
                    CastoConstantesCommande.BUNDLE_COMMANDES, null, new Object[]
                    {
            			getServerSetting().getHost() + getUrlBaseImg(),
                        l_profile.getRepositoryId(),
                        l_civilite,
                        l_prenom,
                        l_nom,
                        l_cher,
                        l_profile.getPropertyValue("login"),
                        newPassword,
                        getServerSetting().getHost(),
                        });
                       
            /*
             * construction du sujet du mail
             */   
    	
            String l_subject = ResourceUtils.getUserMsgResource("commande.motdepasse.perdu.titre",
                    CastoConstantesCommande.BUNDLE_COMMANDES, null, new String[]{});
           
            /*
             * Obtention du destinataire
             */
    	
            String l_destinataire = l_login;
            
            
            
            /*
             * Envoi du mail
             */
    	
            if (null != l_destinataire)
            {
                getMailUtils().sendMailHTML(getCastoMail(), l_subject, l_destinataire,
                        l_corpsMessage);
            }
            
            pResponse.sendLocalRedirect(getUpdateSuccessURL()+"&success=true", pRequest);
            return false;
        }
        catch (Exception l_e)
        {
            logError(l_e);
            pResponse.sendLocalRedirect(getUpdateErrorURL()+"&error=true;", pRequest);
            return true;
        }
        
    }

    /*
     * public boolean handleUpdateProfile(DynamoHttpServletRequest
     * pRequest,DynamoHttpServletResponse pResponse) throws ServletException,
     * IOException { //trace.logOpen(this,".handleUpdateProfile"); boolean
     * l_bOk=false; try{
     * createLog(pRequest,pResponse,m_strType,m_strAction,m_strCommentaire,m_strIp,m_strNomLogin);
     * 
     * l_bOk=handleUpdate(pRequest,pResponse);
     * 
     * String phone = this.getRepositoryId().toString(); String a ="1";
     * 
     * 
     * }catch(Exception e){ //trace.logError(this,e,".handleUpdateProfile
     * Exception : "+e.toString()); l_bOk=false; }finally{
     * //trace.logClose(this,".handleUpdateProfile"); } return true; }
     */

    public boolean handleUpdateProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException
    {
        try
        {
            MutableRepository repository = getProfileTools().getProfileRepository();
            MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId());

            updateProfileAttributes(mutableItem, getValue(), pRequest, pResponse);
            //BDDCC G.V.
            mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
            mutableItem.setPropertyValue("dateMAJprofil",new Date());
            
            repository.updateItem(mutableItem);
            if (isUsingLdapProfile())

            {
                updateLDAPProfileAttributes(mutableItem);
            }
            if (getCheckForRequiredPropertiesAfterUpdate())
            {
                checkForRequiredProperties(mutableItem, pRequest, pResponse);
            }
            /*
             * if(!getFormError()) { if(isLoggingDebug()) { logDebug("updating
             * the item in the repository"); }
             * repository.updateItem(mutableItem); }
             */
            
            String provenance = pRequest.getParameter("provenance");
            
            boolean e = false;
            if (provenance == null || (provenance != null && provenance.equals(""))) {
                Map l_secondaryAddresses = (Map) mutableItem.getPropertyValue("secondaryAddresses");
                Map l_secondaryBillingAddresses = (Map) mutableItem.getPropertyValue("secondaryBillingAddresses");
                Map l_secondaryBillingShippingAddresses = (Map) mutableItem.getPropertyValue("secondaryBillingShippingAddresses");
    
                
            
                for (int i = 0; i < l_secondaryAddresses.size(); i++)
                {
                    String newTel = pRequest.getParameter("telLiv" + i);
                    MutableRepositoryItem adresseEnCours = (MutableRepositoryItem) l_secondaryAddresses.get(pRequest
                            .getParameter("telLivKey" + i));
                    adresseEnCours.setPropertyValue("phoneNumber", newTel);
                    l_secondaryAddresses.put(pRequest.getParameter("telLivKey" + i), adresseEnCours);
                }
    
                for (int i = 0; i < l_secondaryBillingAddresses.size(); i++)
                {
                    String newTel = pRequest.getParameter("telFac" + i);
                    MutableRepositoryItem adresseEnCours = (MutableRepositoryItem) l_secondaryBillingAddresses.get(pRequest
                            .getParameter("telFacKey" + i));
                    adresseEnCours.setPropertyValue("phoneNumber", newTel);
                    l_secondaryBillingAddresses.put(pRequest.getParameter("telFacKey" + i), adresseEnCours);
                }
    
                for (int i = 0; i < l_secondaryBillingShippingAddresses.size(); i++)
                {
                    String newTel = pRequest.getParameter("telFacLiv" + i);
                    MutableRepositoryItem adresseEnCours = (MutableRepositoryItem) l_secondaryBillingShippingAddresses
                            .get(pRequest.getParameter("telFacLivKey" + i));
                    adresseEnCours.setPropertyValue("phoneNumber", newTel);
                    l_secondaryBillingShippingAddresses.put(pRequest.getParameter("telFacLivKey" + i), adresseEnCours);
                }
    
                mutableItem.setPropertyValue("secondaryAddresses", l_secondaryAddresses);
                mutableItem.setPropertyValue("secondaryBillingAddresses", l_secondaryBillingAddresses);
                mutableItem.setPropertyValue("secondaryBillingShippingAddresses", l_secondaryBillingShippingAddresses);
      
                repository.updateItem(mutableItem);
            }
                pResponse.sendLocalRedirect(getUpdateSuccessURL(), pRequest);

            return false;

        }
        catch (RepositoryException exc)
        {
            if (isLoggingError())
            {
                logError("exception" + exc);
                pResponse.sendLocalRedirect(getUpdateErrorURL(), pRequest);
            }
            return true;
        }
    }

    public boolean handleUpdateProfileLogin(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException
    {
        logDebug("*************ESSAI********************");
        boolean erreur = false;
        try
        {
            MutableRepository repository = getProfileTools().getProfileRepository();
            MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId());

            String login = pRequest.getParameter("formLogin");
            String pwd = pRequest.getParameter("formPwd");
            
            logDebug("*************valur pwd : "+pwd +"**********************");
            
            String question = pRequest.getParameter("formQtn");
            String reponse = pRequest.getParameter("formRep");

            // verif formulaire
            if (!estDefini(login))
            {
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=1", pRequest);
                erreur = true;
            }
            else if (!estDefini(pwd))
            {
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=2", pRequest);
                erreur = true;

            }
            else if (!estDefini(question))
            {
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=3", pRequest);
                erreur = true;
            }
            /*else if (!estDefini(reponse))
            {
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=4", pRequest);
                erreur = true;
            }*/

            if (!erreur)
            {
                // verif si profil n'existe pas dejï¿½
                if (!pRequest.getParameter("loginDeBase").equalsIgnoreCase(login))
                {
                    RepositoryView l_oView = repository.getView("user");
                    RqlStatement l_oUsr = RqlStatement.parseRqlStatement("login = ?0");
                    RepositoryItem[] l_oUser = l_oUsr.executeQuery(l_oView, new Object[]
                    { login });
                    if (l_oUser != null)
                    {
                        pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=5", pRequest);
                        erreur = true;
                    }
                    else
                    {
                        mutableItem.setPropertyValue("login", login);
                        mutableItem.setPropertyValue("password", pwd);
                        mutableItem.setPropertyValue("reminderPassword", question);
                        mutableItem.setPropertyValue("reponseReminder", reponse);
                        
                        //BDDCC G.V.
                        mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
                        mutableItem.setPropertyValue("dateMAJprofil",new Date());
                        
                        repository.updateItem(mutableItem);
                    }
                }
                else
                {
                    mutableItem.setPropertyValue("login", login);
                    mutableItem.setPropertyValue("password", pwd);
                    mutableItem.setPropertyValue("reminderPassword", question);
                    mutableItem.setPropertyValue("reponseReminder", reponse);
                    
                    //BDDCC G.V.
                    mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
                    mutableItem.setPropertyValue("dateMAJprofil",new Date());
                    
                    repository.updateItem(mutableItem);
                }
            }

        }
        catch (RepositoryException e)
        {
            if (isLoggingError())
            {
                logError(this.getClass() + "EXCEPTION  :" + e);
                pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=6", pRequest);
                erreur = true;
            }
        }

        if (erreur)
        {
            return true;
        }
        else
        {
            pResponse.sendLocalRedirect(getUpdateSuccessURL(), pRequest);
            return false;
        }

    }
    
    
    public boolean handleUpdateProfileContactInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
        int numErreur = 0;
        int numErreurLiv = 0;
        int numErreurFac = 0;
        boolean erreur = false;
        
        try
        {
            
            MutableRepository repository = getProfileTools().getProfileRepository();
            MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId());
            
            /*
             *------------------------------------------------------------ 
             * adresse de livraison active
             *------------------------------------------------------------ 
             */
            String adresse1Liv = pRequest.getParameter("adresse1Liv");
            String adresse2Liv = pRequest.getParameter("adresse2Liv");
            String cpLiv = pRequest.getParameter("cpLiv");
            String villeLiv = pRequest.getParameter("villeLiv");
            
            String adresse1Fac = pRequest.getParameter("adresse1Fac");
            String adresse2Fac = pRequest.getParameter("adresse2Fac");
            String cpFac = pRequest.getParameter("cpFac");
            String villeFac = pRequest.getParameter("villeFac");
            
            numErreurLiv = checkAdresse(adresse1Liv,cpLiv,villeLiv);

            numErreurFac = checkAdresse(adresse1Fac,cpFac,villeFac);
            
            if (numErreurLiv != 0)
            {
                if (numErreurFac != 0)
                {
                    pResponse.sendLocalRedirect(getUpdateErrorURL()+"&erreur="+numErreurLiv, pRequest);
                    return true;
                }
            }
            else
            {
                MutableRepositoryItem adresseLiv;
                if (null == mutableItem.getPropertyValue("shippingAddress"))
                {
                    adresseLiv = repository.createItem("contactInfo");
                    mutableItem.setPropertyValue("shippingAddress", adresseLiv);
                }
                else
                {               
                    adresseLiv = (MutableRepositoryItem)mutableItem.getPropertyValue("shippingAddress");
                }
                adresseLiv.setPropertyValue("address1", adresse1Liv);
                adresseLiv.setPropertyValue("address2", adresse2Liv);
                adresseLiv.setPropertyValue("postalCode", cpLiv);
                adresseLiv.setPropertyValue("city", villeLiv);
                
                mutableItem.setPropertyValue("shippingAddress", adresseLiv);
            }
            
            
            /*
             *------------------------------------------------------------
             * adresse de facturation active
             *------------------------------------------------------------ 
             */

            if (numErreurFac == 0)
            {
                MutableRepositoryItem adresseFac;
                if (null == mutableItem.getPropertyValue("billingAddress"))
                {
                    adresseFac = repository.createItem("contactInfo");
                    mutableItem.setPropertyValue("billingAddress", adresseFac);
                }
                else
                {
                    adresseFac = (MutableRepositoryItem)mutableItem.getPropertyValue("billingAddress");
                }
                adresseFac.setPropertyValue("address1", adresse1Fac);
                adresseFac.setPropertyValue("address2", adresse2Fac);
                adresseFac.setPropertyValue("postalCode", cpFac);
                adresseFac.setPropertyValue("city", villeFac);
                
                mutableItem.setPropertyValue("billingAddress", adresseFac);
            }
            
            
            /*
             *------------------------------------------------------------
             * adresse de livraisons secondaires
             *------------------------------------------------------------ 
             */
                    Map l_secondaryAddresses = (Map) mutableItem.getPropertyValue("secondaryAddresses");
                    for (int i = 0 ; i < l_secondaryAddresses.size() ; i++)
                    {                                               
                        String adresse1LivSec = pRequest.getParameter("adresse1Liv"+i);
                        String adresse2LivSec = pRequest.getParameter("adresse2Liv"+i);
                        String cpLivSec = pRequest.getParameter("cpLiv"+i);
                        String villeLivSec = pRequest.getParameter("villeLiv"+i);
                        
                        numErreur = checkAdresse(adresse1LivSec, cpLivSec, villeLivSec);
                                                
                        if (numErreur != 0)
                        {
                            pResponse.sendLocalRedirect(getUpdateErrorURL()+"&erreur="+numErreur, pRequest);
                            erreur = true;
                        }
                        else
                        {
                            String clef = pRequest.getParameter("keyLiv"+i);
                            MutableRepositoryItem adresseLiv = (MutableRepositoryItem)l_secondaryAddresses.get(clef);
                            adresseLiv.setPropertyValue("address1", adresse1LivSec);
                            adresseLiv.setPropertyValue("address2", adresse2LivSec);
                            adresseLiv.setPropertyValue("postalCode", cpLivSec);
                            adresseLiv.setPropertyValue("city", villeLivSec);
                            
                            l_secondaryAddresses.put(clef, adresseLiv);
                        }                       
                    }
                    
                    mutableItem.setPropertyValue("secondaryAddresses", l_secondaryAddresses);
           
                    
                    /*
                     *------------------------------------------------------------
                     * adresse de facturation secondaires
                     *------------------------------------------------------------ 
                     */
                            Map l_secondaryBillingAddresses = (Map) mutableItem.getPropertyValue("secondaryBillingAddresses");
                            for (int i = 0 ; i < l_secondaryBillingAddresses.size() ; i++)
                            {                                                       
                                String adresse1FacSec = pRequest.getParameter("adresse1Fac"+i);
                                String adresse2FacSec = pRequest.getParameter("adresse2Fac"+i);
                                String cpFacSec = pRequest.getParameter("cpFac"+i);
                                String villeFacSec = pRequest.getParameter("villeFac"+i);
                                
                                numErreur = checkAdresse(adresse1FacSec, cpFacSec, villeFacSec);
                                                        
                                if (numErreur != 0)
                                {
                                    pResponse.sendLocalRedirect(getUpdateErrorURL()+"&erreur="+numErreur, pRequest);
                                    erreur = true;
                                }
                                else
                                {
                                    String clef = pRequest.getParameter("keyFac"+i);
                                    MutableRepositoryItem adresseFac = (MutableRepositoryItem)l_secondaryBillingAddresses.get(clef);
                                    adresseFac.setPropertyValue("address1", adresse1FacSec);
                                    adresseFac.setPropertyValue("address2", adresse2FacSec);
                                    adresseFac.setPropertyValue("postalCode", cpFacSec);
                                    adresseFac.setPropertyValue("city", villeFacSec);
                                    
                                    l_secondaryBillingAddresses.put(clef, adresseFac);
                                }                       
                            }
                            
                            mutableItem.setPropertyValue("secondaryBillingAddresses", l_secondaryBillingAddresses);
            
                            
                            /*
                             *------------------------------------------------------------
                             * adresse de facturation / livraison secondaires
                             *------------------------------------------------------------ 
                             */
                                    Map l_secondaryBillingShippingAddresses = (Map) mutableItem.getPropertyValue("secondaryBillingShippingAddresses");
                                    for (int i = 0 ; i < l_secondaryBillingShippingAddresses.size() ; i++)
                                    {                                                               
                                        String adresse1FacLivSec = pRequest.getParameter("adresse1FacLiv"+i);
                                        String adresse2FacLivSec = pRequest.getParameter("adresse2FacLiv"+i);
                                        String cpFacLivSec = pRequest.getParameter("cpFacLiv"+i);
                                        String villeFacLivSec = pRequest.getParameter("villeFacLiv"+i);
                                        
                                        numErreur = checkAdresse(adresse1FacLivSec, cpFacLivSec, villeFacLivSec);
                                                                
                                        if (numErreur != 0)
                                        {
                                            pResponse.sendLocalRedirect(getUpdateErrorURL()+"&erreur="+numErreur, pRequest);
                                            erreur = true;
                                        }
                                        else
                                        {
                                            String clef = pRequest.getParameter("keyFacLiv"+i);
                                            MutableRepositoryItem adresseFacLiv = (MutableRepositoryItem)l_secondaryBillingShippingAddresses.get(clef);
                                            adresseFacLiv.setPropertyValue("address1", adresse1FacLivSec);
                                            adresseFacLiv.setPropertyValue("address2", adresse2FacLivSec);
                                            adresseFacLiv.setPropertyValue("postalCode", cpFacLivSec);
                                            adresseFacLiv.setPropertyValue("city", villeFacLivSec);
                                            
                                            l_secondaryBillingShippingAddresses.put(clef, adresseFacLiv);
                                        }                       
                                    }
                                    
                                    mutableItem.setPropertyValue("secondaryBillingShippingAddresses", l_secondaryBillingShippingAddresses);
                                    
                                    
                                    
                              /*
                               * SI ON ARRIVE ICI, ALORS PAS D'ERREURS ON MET A JOUR LE PROFILE
                               */
                                    
                                    
                               //BDDCC G.V.
                               mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
                               mutableItem.setPropertyValue("dateMAJprofil",new Date());
                                   
                               repository.updateItem(mutableItem);
                               pResponse.sendLocalRedirect(getUpdateSuccessURL(), pRequest);
                               
        }
        catch (Exception e)
        {
            if (isLoggingError())
            {
                logError(this.getClass() + "EXCEPTION :" + e);              
            }
            pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=4", pRequest);
            
            erreur = true;
        }
        return erreur;
    }
    
    
    private int checkAdresse(String adr, String cp, String vil)
    {
        if (!estDefini(adr))
        {
            return 1;
        }
        else if(!estDefini(cp))
        {
            return 2;
        }
        else if (!estDefini(vil))
        {
            return 3;
        }
            return 0;
    }
    
    
    public boolean handleUpdateProfileMailingAndContact(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
    {
    boolean erreur = false;
    try
    {
        MutableRepository repository = getProfileTools().getProfileRepository();
        MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId());    
        updateProfileAttributes(mutableItem, getValue(), pRequest, pResponse);
    }
    catch (RepositoryException e)
        {
            if (isLoggingError())
                {
                    logError(this.getClass() + "EXCEPTION  :" + e);
                    pResponse.sendLocalRedirect(getUpdateErrorURL() + "&erreur=6", pRequest);
                    erreur = true;
                }
        }

        if (erreur)
        {
            return true;
        }
        else
        {
            pResponse.sendLocalRedirect(getUpdateSuccessURL(), pRequest);
            return false;
        }

       }
    

    // creation des logs
    public void createLog(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String a_strType,
            String a_strAction, String a_strCommentaire, String a_strIp, String a_strNomLogin)
    {
        // trace.logOpen(this,".createLog");
        boolean l_bOk = false;
        try
        {
            BeanCreateLogAdmin l_BeanCreateLogAdmin = (BeanCreateLogAdmin) pRequest
                    .resolveName("/castorama/BeanCreateLogAdmin");
            l_BeanCreateLogAdmin.enregistrement(a_strType, a_strAction, a_strCommentaire, a_strIp, a_strNomLogin);
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".createLog Exception : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".createLog");
        }
    }

    private boolean estDefini(String s)
    {
        if (s == null || (s != null && s.equals("")) || (s != null && s.equals(" ")))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /*
     * Fiche Mantis 1015 - FD - 04/03/2008
     */
    /**
     * .
     * Modification du login user
     * @param a_request a_request
     * @param a_response a_response
     * @throws ServletException ServletException
     * @throws IOException IOException
     * @return boolean boolean
     */
    public boolean handleUpdateProfileMailing(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {
        if(isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + " CastoProfileAdminFormHandler.handleUpdateProfileMailing()");
        }
        
        boolean l_erreur = false;
        try
        {
            if(null!=getValue().get(EMAIL))
            {
                String l_email = getValue().get(EMAIL).toString();
                if(!"".equals(l_email))
                {
                    if(null!=getProfileTools())
                    {
                        if(null!=getProfileTools().getProfileRepository())
                        {
                            /* On récupère le profile de l'utilisateur */
                            MutableRepository l_repository = getProfileTools().getProfileRepository();
                            MutableRepositoryItem l_mutableItem = l_repository.getItemForUpdate(getRepositoryId(), CastoConstantes.DESCRIPTEUR_UTILISATEUR);
                            
                            /* Mise à jour du compte newsletter */
                            MutableRepository l_newsletterRepo = getNewsletterRepository();
                            MutableRepositoryItem l_newsletter = (MutableRepositoryItem)l_mutableItem.getPropertyValue(ABONNEMENT_NEWSLETTER);
                            if(null!=l_newsletter)
                            {
                                if(null!=l_newsletterRepo)
                                {
                                    /* Propriété EMAIL du repository newsletter */
                                    //je récupère l'id du repository item newsletter
                                    String l_newletterId = l_newsletter.getRepositoryId();
                                    //pdt la modif je met à nul le lien qui va du profil vers la newsletter
                                    l_mutableItem.setPropertyValue(ABONNEMENT_NEWSLETTER, null);
                                    
                                    
                                    l_repository.updateItem(l_mutableItem);
                                    //je récupère l'objet existant avec grace à l'id
                                    MutableRepositoryItem l_mutableNewsLetter = l_newsletterRepo.getItemForUpdate(l_newletterId, ABONNEMENT_NEWSLETTER);
                                    //je clone l'objet existant avec le nouvelle id
                                    MutableRepositoryItem l_mutableNouvelleNewsLetter = 
                                        atg.repository.RepositoryUtils.cloneItem(l_mutableNewsLetter, true, null, null, null, l_email);
                                    //j'update le repository le contenant
                                    l_newsletterRepo.updateItem(l_mutableNouvelleNewsLetter);
                                    //je vire l'ancien
                                    //l_newsletterRepo.removeItem(l_newletterId, ABONNEMENT_NEWSLETTER);
                                    //je remet le lien dans le profile
                                    l_mutableItem.setPropertyValue(ABONNEMENT_NEWSLETTER, l_mutableNouvelleNewsLetter);

                                    //BDDCC G.V.
                                    l_mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
                                    l_mutableItem.setPropertyValue("dateMAJprofil",new Date());
                                    
                                    l_repository.updateItem(l_mutableItem);
                                }  
                            }
                            
                            /* Propriété emailHTML */
                            if(null!=getValue().get(EMAIL_HTML))
                            {
                                String l_emailHTML = getValue().get(EMAIL_HTML).toString();
                                if(!"".equals(l_emailHTML))
                                {
                                    Boolean l_bool = Boolean.valueOf(l_emailHTML);
                                    l_mutableItem.setPropertyValue(EMAIL_HTML,l_bool);
                                }
                            }
                            /* Propriété emailstatus */
                            if(null!=getValue().get(EMAIL_STATUS))
                            {
                                String l_emailStatus = getValue().get(EMAIL_STATUS).toString();
                                if(!"".equals(l_emailStatus))
                                {
                                    l_mutableItem.setPropertyValue(EMAIL_STATUS, l_emailStatus);
                                }
                            }
                            /* Propriété receiveEmail */
                            if(null!=getValue().get(RECEIVE_EMAIL))
                            {
                                String l_receiveEmail = getValue().get(RECEIVE_EMAIL).toString();
                                if(!"".equals(l_receiveEmail))
                                {
                                    l_mutableItem.setPropertyValue(RECEIVE_EMAIL, l_receiveEmail);
                                }
                            }
                            
                            /* MAJ du Home Address */
                            MutableRepositoryItem l_homeAddress = (MutableRepositoryItem)l_mutableItem.getPropertyValue(HOME_ADDRESS);
                            l_homeAddress.setPropertyValue(CONTACT_INFO_EMAIL, l_email);
                            
                            /* MAJ du billing address */
                            MutableRepositoryItem l_billingAddress = (MutableRepositoryItem)l_mutableItem.getPropertyValue(BILLING_ADDRESS);
                            l_billingAddress.setPropertyValue(CONTACT_INFO_EMAIL, l_email);
                            
                            /* MAJ du shipping address */
                            MutableRepositoryItem l_shippingAddress = (MutableRepositoryItem)l_mutableItem.getPropertyValue(SHIPPING_ADDRESS);
                            l_shippingAddress.setPropertyValue(CONTACT_INFO_EMAIL, l_email);
                            
                            /* MAJ du SECONDARY_BILLING_ADDRESSES - compléments d'informations pour la livraison*/
                            Map l_ContactInfo =(Map)l_mutableItem.getPropertyValue(SECONDARY_BILLING_ADDRESSES);
                            if(null != l_ContactInfo)
                            {
                                if(l_ContactInfo.containsKey(CONTACT_INFO_EMAIL))
                                {
                                    l_ContactInfo.put(CONTACT_INFO_EMAIL, l_email);
                                    l_mutableItem.setPropertyValue(SECONDARY_BILLING_ADDRESSES,l_ContactInfo);
                                }
                            }
                            else
                            {
                                if(isLoggingDebug())
                                {
                                    logDebug("l_ContactInfo == null");
                                }
                            }
                            
                            /* MAJ du SECONDARY_BILLING_SHIPPING_ADDRESSES - compléments d'informations pour la livraison*/
                            l_ContactInfo =(Map)l_mutableItem.getPropertyValue(SECONDARY_BILLING_SHIPPING_ADDRESSES);
                            if(null != l_ContactInfo)
                            {
                                if(l_ContactInfo.containsKey(CONTACT_INFO_EMAIL))
                                {
                                    l_ContactInfo.put(CONTACT_INFO_EMAIL, l_email);
                                    l_mutableItem.setPropertyValue(SECONDARY_BILLING_SHIPPING_ADDRESSES,l_ContactInfo);
                                }
                            }
                            else
                            {
                                if(isLoggingDebug())
                                {
                                    logDebug("l_ContactInfo == null");
                                }
                            }
                            
                            /* Mise à jour du profile */
                            /* Propriété login */
                            l_mutableItem.setPropertyValue("login", l_email);
                            
                            //BDDCC G.V.
                            l_mutableItem.setPropertyValue("typeMAJprofil",MODIFICATION);
                            l_mutableItem.setPropertyValue("dateMAJprofil",new Date());
                            
                            l_repository.updateItem(l_mutableItem);
                        }  
                        else
                        {
                            logError("getProfileTools().getProfileRepository() == null");
                        }
                    }
                }
                else
                {
                    logError("getProfileTools() == null");
                }
            }
        }
        catch (RepositoryException l_e)
        {
            if (isLoggingError())
            {
                logError(l_e);
                a_response.sendLocalRedirect(getUpdateErrorURL() + "&erreur=6", a_request);
                l_erreur = true;
            }
        }

        if(isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + " CastoProfileAdminFormHandler.handleUpdateProfileMailing()");
        }
        
        if (l_erreur)
        {
            return true;
        }
        else
        {
            a_response.sendLocalRedirect(getUpdateSuccessURL(), a_request);
            return false;
        }    
    }
    
    /*
     * Fiche Mantis 1015 - FD - 04/03/2008
     */
    
    /**
     * @return the newsletterRepository
     */
    public MutableRepository getNewsletterRepository()
    {
        return m_newsletterRepository;
    }

    /**
     * @param a_newsletterRepository the newsletterRepository to set
     */
    public void setNewsletterRepository(MutableRepository a_newsletterRepository)
    {
        m_newsletterRepository = a_newsletterRepository;
    }
}// end class
