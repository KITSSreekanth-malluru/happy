package com.castorama;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;

import atg.commerce.profile.CommerceProfileFormHandler;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileUserMessage;

import com.castorama.config.Configuration;
import com.castorama.utils.CheckingTools;
import com.castorama.utils.MailTools;
import com.castorama.xml.XmlDocument;

/**
 * Extension du ProfileFormHandler de DPS. - surcharge de la m�thode
 * preCreateUser pour copier le Login dans le champ email
 * 
 * @author Damien DURIEZ
 * @version $Id: CastoProfileFormHandler.java,v 1.0 14/03/2001
 */

public class CastoProfileFormHandler extends CommerceProfileFormHandler
{

    public final static String ORIGINE = "CastoProfile";

    static final String MSG_MISSING_PASSWORD = "missingPassword";

    static final String MSG_INVALID_PASSWORD = "invalidPassword";

    static final String MSG_ERR_UPDATING_PROFILE = "errorUpdatingProfile";

    // Type de mise � jour du profil
    private static final Integer MODIFICATION = new Integer(1);

    String m_strOldPassword; // pour l'edition du login/password

    boolean m_bInscriptionNewsletter; // pour l'edition du login/password

    int m_nSecurityStatusCookie; // securityStatusCookie

    int m_nSecurityStatusLogin; // securityStatusLogin

    protected String m_strMailConfirmationInscriptionXslTemplateURL; // URL
                                                                        // du
                                                                        // template
                                                                        // xsl
                                                                        // pour
                                                                        // le
                                                                        // mail
                                                                        // de
                                                                        // confirmation
                                                                        // d'inscription

    /**
     * R�cup�ration du MailConfirmationInscriptionXslTemplateURL
     * 
     * @param none
     * @return String MailConfirmationInscriptionXslTemplateURL
     * @throws none
     */
    public String getMailConfirmationInscriptionXslTemplateURL()
    {
        return m_strMailConfirmationInscriptionXslTemplateURL;
    }

    /**
     * Modification du MailConfirmationInscriptionXslTemplateURL
     * 
     * @param String
     *            MailConfirmationInscriptionXslTemplateURL
     * @return none
     * @throws none
     */
    public void setMailConfirmationInscriptionXslTemplateURL(String a_strMailConfirmationInscriptionXslTemplateURL)
    {
        m_strMailConfirmationInscriptionXslTemplateURL = a_strMailConfirmationInscriptionXslTemplateURL;
    }

    /**
     * R�cup�ration de l'ancien mot de passe
     * 
     * @param none
     * @return String password
     * @throws none
     */
    public String getOldPassword()
    {

        return m_strOldPassword;
    }

    /**
     * Modification de l'ancien mot de passe
     * 
     * @param String -
     *            password
     * @return none
     * @throws none
     */
    public void setOldPassword(String a_strOldPassword)
    {

        m_strOldPassword = a_strOldPassword;

    }

    /**
     * inscription � la newsletter
     * 
     * @param none
     * @return boolean inscription � la newsletter
     * @throws none
     */
    public boolean getInscriptionNewsletter()
    {

        return m_bInscriptionNewsletter;
    }

    /**
     * Lancer l'inscription � la newsletter...
     * 
     * @param boolean
     *            inscription � la newsletter
     * @return none
     * @throws none
     */
    public void setInscriptionNewsletter(boolean a_bInscriptionNewsletter)
    {

        m_bInscriptionNewsletter = a_bInscriptionNewsletter;

    }

    /**
     * R�cup�ration du securityStatusCookie
     * 
     * @param none
     * @return int securityStatusCookie
     * @throws none
     */
    public int getSecurityStatusCookie()
    {

        return m_nSecurityStatusCookie;
    }

    /**
     * Modification du securityStatusCookie
     * 
     * @param int -
     *            securityStatusCookie
     * @return none
     * @throws none
     */
    public void setSecurityStatusCookie(int a_nSecurityStatusCookie)
    {

        m_nSecurityStatusCookie = a_nSecurityStatusCookie;

    }

    /**
     * R�cup�ration du securityStatusLogin
     * 
     * @param none
     * @return int securityStatusLogin
     * @throws none
     */
    public int getSecurityStatusLogin()
    {

        return m_nSecurityStatusLogin;
    }

    /**
     * Modification du securityStatusLogin
     * 
     * @param int -
     *            securityStatusLogin
     * @return none
     * @throws none
     */
    public void setSecurityStatusLogin(int a_nSecurityStatusLogin)
    {

        m_nSecurityStatusLogin = a_nSecurityStatusLogin;

    }

    /**
     * Op�ration appell� juste avant le d�marrage du processus de cr�ation du
     * User. Copie de la valeur du champ login dans le champ email du profil.
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
    protected void preCreateUser(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {

        // Copie de la valeur du champ login dans le champ email du profil
        // trace.logOpen(this,".preCreateUser");
        try
        {
            String l_strEmail = (String) this.getValue().get("login");
            if (l_strEmail != null)
            {
                this.getValue().put("email", l_strEmail);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".preCreateUser Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".preCreateUser");
        }
    }

    /**
     * Op�ration appell� juste avant le d�marrage du processus de mis � jour du
     * User. Copie de la valeur du champ login dans le champ email du profil.
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
    protected void preUpdateUser(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".preUpdateUser");
        try
        {
            String l_strEmail = (String) this.getValue().get("login");
            if (l_strEmail != null)
            {
                this.getValue().put("email", l_strEmail);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".preUpdateUser Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".preUpdateUser");
        }
    }

    /**
     * Op�ration appell� juste apres le processus de cr�ation du User. Copie de
     * la valeur des champs nom,pr�nom dans l'adresse de facturation du profil.
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
    /*
     * protected void postCreateUser(DynamoHttpServletRequest a_Request,
     * DynamoHttpServletResponse a_Response) throws ServletException,
     * IOException {
     * 
     * try { Profile l_Profile = getProfile(); String l_strFirstName = (String)
     * l_Profile.getPropertyValue("firstName"); String l_strLastName = (String)
     * l_Profile.getPropertyValue("lastName");
     * //trace.logDebug(this,".postCreateUser :
     * l_strFirstName="+l_strFirstName); //trace.logDebug(this,".postCreateUser :
     * l_strLastName="+l_strLastName);
     * 
     * MutableRepository l_MutableRepository = (MutableRepository)
     * l_Profile.getRepository(); MutableRepositoryItem l_AdresseFacturation =
     * (MutableRepositoryItem) l_Profile.getPropertyValue("adresseFacturation");
     * //trace.logDebug(this,".postCreateUser :
     * l_AdresseFacturation="+l_AdresseFacturation);
     * if(l_AdresseFacturation==null){ // pas d'adresse de facturation -->
     * creation l_AdresseFacturation =
     * l_MutableRepository.createItem("contactInfo");
     * l_AdresseFacturation.setPropertyValue("firstName", l_strFirstName);
     * l_AdresseFacturation.setPropertyValue("lastName", l_strLastName);
     * l_MutableRepository.addItem(l_AdresseFacturation); MutableRepositoryItem
     * ProfileItem =
     * l_MutableRepository.getItemForUpdate(l_Profile.getRepositoryId(),
     * "user"); ProfileItem.setPropertyValue ("adresseFacturation",
     * l_AdresseFacturation); }else{ // sinon mise � jour de l'adresse de
     * facturation l_AdresseFacturation.setPropertyValue("firstName",
     * l_strFirstName); l_AdresseFacturation.setPropertyValue("lastName",
     * l_strLastName); l_MutableRepository.updateItem(l_AdresseFacturation); }
     * 
     * l_MutableRepository.updateItem(l_AdresseFacturation); } catch
     * (RepositoryException e) { //trace.logError(this,e,".postCreateUser :
     * "+e.toString()); } }
     */

    // Profile Form overrides

    /**
     * Surcharge de la m�thode
     * <code> public boolean ProfileFormHandler.handleCreate(DynamoHttpServletRequest,DynamoHttpServletResponse pResponse)</code> -
     * Ajout de v�rification d'erreurs
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
    public boolean handleCreate(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleCreate");
        boolean l_bOk = checkErrors();
        try
        {
            if (l_bOk)
            {
                l_bOk = super.handleCreate(a_Request, a_Response);

                l_bOk = !getFormError();

                if (l_bOk)
                {
                    // envoi du mail de confirmation d'inscription
                    envoyerMailDeConfirmationInscription();
                    // envoi du mail newsletter
                    if (m_bInscriptionNewsletter)
                    {
                        String l_strNom = (String) this.getValue().get("LASTNAME");
                        String l_strPrenom = (String) this.getValue().get("FIRSTNAME");
                        String l_strEmail = (String) this.getValue().get("LOGIN");

                        NewsletterFormHandler l_NewsletterFormHandler = (NewsletterFormHandler) a_Request
                                .resolveName("/castorama/NewsletterFormHandler");
                        l_NewsletterFormHandler.setSubmitSuccessURL(this.getCreateSuccessURL());
                        l_NewsletterFormHandler.createDefaultOrigine(a_Request, a_Response, l_strNom, l_strPrenom,
                                l_strEmail, ORIGINE, "NR");
                    }
                }
                else
                {
                    Profile l_Profile = getProfile();
                    if (l_Profile != null)
                    {
                        l_Profile.setPropertyValue("securityStatus", new Integer(0));
                    }
                }
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleCreate Exception : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleCreate");
        }
        return true;
    }

    /**
     * Envoi d'un mail de confirmation d'inscription � moncasto
     * 
     * @param none
     * @return none
     * @exception none
     */
    public boolean envoyerMailDeConfirmationInscription()
    {
        try
        {
            String l_strEmail = (String) getValueProperty("login");

            XmlDocument l_XmlRepresentation = new XmlDocument();
            l_XmlRepresentation.addNode("middleName", (String) getValueProperty("middleName"));
            l_XmlRepresentation.addNode("firstName", (String) getValueProperty("firstName"));
            l_XmlRepresentation.addNode("lastName", (String) getValueProperty("lastName"));
            l_XmlRepresentation.addNode("password", (String) getValueProperty("password"));
            l_XmlRepresentation.addNode("email", l_strEmail);
            l_XmlRepresentation.addNode("societe", (String) getValueProperty("societe"));
            l_XmlRepresentation.addNode("reminderPassword", (String) getValueProperty("reminderPassword"));
            l_XmlRepresentation.addNode("reponseReminder", (String) getValueProperty("reponseReminder"));

            String l_strFrom = Configuration.getConfiguration().getMailDefaultFrom();
            MailTools.sendXSLMail(l_strFrom, l_strEmail,
                    "=?iso-8859-1?Q?Confirmation de l'inscription =E0 Mon Castorama?=",
                    m_strMailConfirmationInscriptionXslTemplateURL, l_XmlRepresentation.closeDocument());
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".envoyerMailDeConfirmationInscription :
            // "+e);
        }

        return true;
    }

    /**
     * Surcharge de la m�thode
     * <code> public boolean ProfileFormHandler.handleUpdate(DynamoHttpServletRequest,DynamoHttpServletResponse pResponse)</code> -
     * Ajout de v�rification d'erreurs
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
        try
        {
            boolean l_bOk = checkErrors();
            if (l_bOk)
            {
                l_bOk = super.handleUpdate(a_Request, a_Response);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdate Exception : "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdate");
        }
        return true;
    }

    /**
     * Surcharge de la m�thode pour mettre � jour la premi�re adresse de
     * livraison
     * <code> public boolean ProfileFormHandler.handleUpdate(DynamoHttpServletRequest,DynamoHttpServletResponse pResponse)</code> -
     * Ajout de v�rification d'erreurs: code postal
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
    public boolean handleUpdateLivraison1(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleUpdateLivraison1");
        try
        {
            super.handleUpdate(a_Request, a_Response);
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateLivraison1 Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateLivraison1");
        }
        return true;
    }

    /**
     * Surcharge de la m�thode pour mettre � jour la premi�re adresse de
     * livraison
     * <code> public boolean ProfileFormHandler.handleUpdate(DynamoHttpServletRequest,DynamoHttpServletResponse pResponse)</code> -
     * Ajout de v�rification d'erreurs: code postal
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
    public boolean handleUpdateLivraison2(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleUpdateLivraison2");
        try
        {
            super.handleUpdate(a_Request, a_Response);
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateLivraison2 Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateLivraison2");
        }
        return true;
    }

    /**
     * V�rification d'erreurs
     * 
     * @param none
     * @return boolean erreurs True/False
     * @exception none
     */
    public boolean checkErrors()
    {
        boolean l_bOk = true;
        // trace.logOpen(this,".handleUpdateLivraison2");
        try
        {
            Profile l_Profile = getProfile();
            if (l_bOk)
            {
                l_bOk = CheckingTools.checkEmail((String) getValueProperty("login"), "loginSyntaxError",
                        (GenericFormHandler) this);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateLivraison2 Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateLivraison2");
        }
        return l_bOk;
    }

    /**
     * V�rification du format du login, le login �tant une adresse email
     * 
     * @param String
     *            a_strLogin Login � v�rifier.
     * @return boolean erreur True/False
     * @exception none
     */
    public boolean checkLogin(String a_strLogin)
    {
        boolean l_bOk = true;
        if (a_strLogin != null)
        {
            int indexOfAt = a_strLogin.lastIndexOf("@");
            int indexOfPt = a_strLogin.lastIndexOf(".");
            l_bOk = ((0 < indexOfAt) && (indexOfAt < indexOfPt));
        }
        else
        {
            // trace.logDebug(this,".checkLogin: a_strLogin is null");
        }
        if (!l_bOk)
        {
            // trace.logDebug(this,".checkLogin a_strLogin="+a_strLogin);
            // addFormException(new DropletException("Erreur de syntaxe,
            // veuillez ressaisir votre email.", "loginSyntaxError"));
            addFormException(new DropletException("loginSyntaxError", "loginSyntaxError"));
        }
        return l_bOk;
    }

    /**
     * Pour la modification de login - mot de passe quaestion et r�ponse
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
    public boolean handleUpdateLogin(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleUpdateLogin");
        try
        {
            boolean l_bOk = checkErrors();
            l_bOk &= checkOldPassword();
            if (l_bOk)
            {
                handleUpdate(a_Request, a_Response);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateLogin Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateLogin");
        }
        return true;
    }

    /**
     * Pour la modification de login - mot de passe quaestion et r�ponse
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
    public boolean handleLogin(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {
        boolean l_bOk = false;
        boolean l_bReturn = true;
        // trace.logOpen(this,".handleUpdateLogin");
        try
        {
            Profile l_Profile = getProfile();
            if (((Integer) l_Profile.getPropertyValue("securityStatus")).intValue() == getSecurityStatusCookie())
            {
                l_bOk = l_Profile.getPropertyValue("login").equals((String) this.getValue().get("LOGIN"));
                if (l_bOk)
                {
                    this.setTestPassword((String) this.getValue().get("PASSWORD"));

                    // this.setVerifyPasswordSuccessURL(this.getLoginSuccessURL());

                    l_bOk &= !this.handleVerifyPassword(a_Request, a_Response);
                    if (l_bOk)
                    {
                        l_Profile.setPropertyValue("securityStatus", new Integer(getSecurityStatusLogin()));
                        l_bReturn = checkFormRedirect(this.getLoginSuccessURL(), getLoginErrorURL(), a_Request,
                                a_Response);
                    }
                    else
                    {
                        l_bReturn = checkFormRedirect(this.getLoginSuccessURL(), getLoginErrorURL(), a_Request,
                                a_Response);
                        ;
                    }
                }
                else
                {
                    super.handleLogin(a_Request, a_Response);
                }
            }
            else
            {
                super.handleLogin(a_Request, a_Response);
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateLogin Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateLogin");
        }
        return l_bReturn;
    }

    /**
     * Pour la modification de login - mot de passe quaestion et r�ponse
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
    public boolean handleUpdateCoordonnee(DynamoHttpServletRequest a_Request, DynamoHttpServletResponse a_Response)
            throws ServletException, IOException
    {

        /*
         * java.util.Enumeration e = this.getValue().keys(); while
         * (e.hasMoreElements()){ Object test = (Object) e.nextElement();
         *  }
         */
        // trace.logOpen(this,".handleUpdateCoordonnee");
        try
        {
            boolean l_bOk = super.handleUpdate(a_Request, a_Response);
            if (l_bOk)
            {
                // copie de nom, prenom, date de naissance dans l'adresse de
                // facturation
                Profile l_Profile = getProfile();
                RepositoryItem l_AdresseFacturation = (RepositoryItem) l_Profile.getPropertyValue("adresseFacturation");
                String l_strNom = (String) l_AdresseFacturation.getPropertyValue("lastName");
                String l_strPrenom = (String) l_AdresseFacturation.getPropertyValue("firstName");

                MutableRepository l_MutableRepository = (MutableRepository) l_Profile.getRepository();
                MutableRepositoryItem l_MutableUser = l_MutableRepository.getItemForUpdate(l_Profile.getRepositoryId(),
                        "user");
                l_MutableUser.setPropertyValue("lastName", l_strNom);
                l_MutableUser.setPropertyValue("firstName", l_strPrenom);

                //G.V --> BDD Client
                l_MutableUser.setPropertyValue("typeMAJprofil", MODIFICATION);
                l_MutableUser.setPropertyValue("dateMAJprofil", new Date());
                //
                l_MutableRepository.updateItem(l_MutableUser);
            }
        }
        catch (RepositoryException e)
        {
            // trace.logError(this,e,".handleUpdateCoordonnee
            // RepositoryException : "+e.toString());
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleUpdateCoordonnee Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleUpdateCoordonnee");
        }
        return true;
    }

    /**
     * V�rification du format du login, le login �tant une adresse email
     * 
     * @param none
     * @return boolean erreur True/False
     * @exception none
     */
    public boolean checkOldPassword()
    {
        boolean l_bOk = true;
        // trace.logOpen(this,".checkOldPassword");
        try
        {
            if (m_strOldPassword != null)
            {
                Profile l_Profile = getProfile();
                String l_strCurrentPassword = (String) l_Profile.getPropertyValue("password");
                if (!m_strOldPassword.equals(l_strCurrentPassword))
                {
                    l_bOk = false;
                    addFormException(new DropletException("oldPasswordError", "oldPasswordError"));
                }
            }
            else
            {
                l_bOk = false;
                addFormException(new DropletException("oldPasswordError", "oldPasswordError"));
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".checkOldPassword Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".checkOldPassword");
        }
        return l_bOk;
    }

    /**
     * Compares the value of testPassword to the profile's password and
     * redirects to the successUrl on success and gives messages on failure.
     */
    public boolean handleVerifyPassword(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException
    {
        // trace.logOpen(this,".handleVerifyPassword");
        try
        {
            if (getTestPassword() == null || getTestPassword().trim().length() == 0)
            {
                addFormException(new DropletException(formatUserMessage(MSG_MISSING_PASSWORD, pRequest)));
                return true;
            }
            String password = (String) getProfile().getPropertyValue(
                    getProfileTools().getPropertyManager().getPasswordPropertyName());
            String testPasswordHash = getProfileTools().getPropertyManager().generatePassword(getTestPassword().trim());

            if (!testPasswordHash.equals(password))
            {
                addFormException(new DropletException(formatUserMessage(MSG_INVALID_PASSWORD, pRequest)));
                return true;
            }

            // pResponse.sendLocalRedirect(getVerifyPasswordSuccessURL(),
            // pRequest);
            if (getProfileTools().isEnableSecurityStatus())
            {
                try
                {
                    getProfileTools().setLoginSecurityStatus(getProfile(), pRequest);
                }
                catch (RepositoryException e)
                {
                    addFormException(new DropletException(formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest)));
                    if (isLoggingError())
                    {
                        logError(e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".handleVerifyPassword Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".handleVerifyPassword");
        }
        return false;
    }

    /**
     * Utility method to format a message with no arguments using the Locale of
     * the user
     * 
     * @param pKey
     *            the identifier for the message to retrieve out of the
     *            ResourceBundle
     * @param pRequest
     *            the request object which can be used to extract the user's
     *            locale
     * @return the formatted message
     * @see ProfileUserMessage
     */
    public String formatUserMessage(String pKey, DynamoHttpServletRequest pRequest)
    {
        // trace.logOpen(this,".formatUserMessage");
        String l_strRetour = "";
        try
        {
            l_strRetour = ProfileUserMessage.format(pKey, getUserLocale(pRequest));
        }
        catch (Exception e)
        {
            // trace.logError(this,e,".formatUserMessage Exception :
            // "+e.toString());
        }
        finally
        {
            // trace.logClose(this,".formatUserMessage");
        }
        return l_strRetour;
    }

    /**
     * D�sactivation du chargement des anciens paniers car le mergeOrders a �t�
     * d�sactiv�
     * 
     * After logging in the user's session cached promotions are reloaded into
     * the PricingModelHolder. In addition any non-transient orders are made
     * persistent and old shopping carts are loaded from the database.
     * 
     * @param pRequest
     *            the servlet's request
     * @param pResponse
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    // TODO MIGRATION : this method does nothing more than the original method
    // --- probably rewrote the method just to call a CastoProfileTools class
    // --- but this is not usefull
    // protected void postLoginUser(DynamoHttpServletRequest pRequest,
    // DynamoHttpServletResponse pResponse)
    // throws ServletException, IOException
    // {
    //
    // superPostLoginUser(pRequest, pResponse);
    //		
    // Boolean isLoggedIn =
    // (Boolean)pRequest.getObjectParameter(HANDLE_LOGIN_PARAM);
    // if ((isLoggedIn != null) && (isLoggedIn.booleanValue())) {
    // if (getUserPricingModels() != null) {
    // getUserPricingModels().initializePricingModels();
    // }
    // if (getShoppingCart() != null) {
    // CastoProfileTools profileTools = (CastoProfileTools) getProfileTools();
    // Locale locale = profileTools.getUserLocale(pRequest, pResponse);
    // try {
    // profileTools.loadUserShoppingCartForLogin(getProfile(),
    // getShoppingCart(), getUserPricingModels(), locale);
    // }
    // catch (CommerceException exc) {
    // if (isLoggingError())
    // logError(exc);
    // }
    // }
    // }
    // }
    /**
     * Profile Cookies are rebroadcast as necessary and a Profile Event is fired
     * to indicate a user was logged in. Also, the securityStatus property gets
     * set. Operation called just after the user is found to be logged in
     * 
     * @param pRequest
     *            the servlet's request
     * @param pResponse
     *            the servlet's response
     * @exception ServletException
     *                if there was an error while executing the code
     * @exception IOException
     *                if there was an error with servlet io
     */
    // TODO MIGRATION : This method is a copy of the original method...
    // --- this is a bad JAVA coding
    // protected void superPostLoginUser(DynamoHttpServletRequest pRequest,
    // DynamoHttpServletResponse pResponse)
    // throws ServletException, IOException
    // {
    // Boolean isLoggedIn =
    // (Boolean)pRequest.getObjectParameter(HANDLE_LOGIN_PARAM);
    // if ((isLoggedIn != null) && (isLoggedIn.booleanValue())) {
    // if (isLoggingDebug())
    // logDebug("handleLogin: Sending profile cookies as needed");
    // atg.userprofiling.CookieManager cookieManager =
    // getProfileTools().getCookieManager();
    // if ((cookieManager != null) && (cookieManager.isSendProfileCookies()))
    // cookieManager.forceProfileCookies(getProfile(), pRequest, pResponse);
    //		
    // // login User with LoginUserAuthority
    // LoginUserAuthority userAuthority =
    // getProfileTools().getLoginUserAuthority();
    // User user = getUser();
    // ProfileTools ptools = getProfileTools();
    // PropertyManager pmgr = ptools.getPropertyManager();
    //		
    // if (userAuthority != null && user != null && pmgr != null) {
    // String userName = getStringValueProperty(pmgr.getLoginPropertyName());
    // String passwordPropertyName = pmgr.getPasswordPropertyName();
    // String password = (passwordPropertyName == null) ? null :
    // getStringValueProperty(passwordPropertyName);
    // PasswordHasher passwordHasher = (password == null) ? null :
    // userAuthority.getPasswordHasher();
    //		
    // //System.out.println("########## Login user = " + userName + " password =
    // " + password);
    // userAuthority.login
    // (user, userName,
    // //passwordHasher.hashPasswordForLogin(passwordHasher.encryptPassword(password)),
    // (password == null) ? null :
    // passwordHasher.hashPasswordForLogin(password),
    // (password == null) ? null : passwordHasher.getPasswordHashKey() );
    // }
    //		
    // if (isLoggingDebug())
    // logDebug("handleLogin: sending login event");
    // getProfileTools().getProfileEventTrigger().sendLoginEvent(getProfile(),
    // pRequest);
    //		
    // if (getProfileTools().isEnableSecurityStatus()) {
    // try {
    // getProfileTools().setLoginSecurityStatus(getProfile(), pRequest);
    // }
    // catch (RepositoryException e) {
    // addFormException(new DropletException("Unable to update security status",
    // e));
    // if (isLoggingError())
    // logError(e);
    // }
    // }
    // }
    // propagateLocale(pRequest,pResponse);
    // }
    //	

} // end of class

