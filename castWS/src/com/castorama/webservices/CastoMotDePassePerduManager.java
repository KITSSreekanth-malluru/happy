package com.castorama.webservices;

import java.io.StringReader;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileTools;

import com.castorama.utils.ServerSetting;
import com.castorama.webservices.xsd.MotDePassePerduRequest;

/**
 * Utilisée par le webservice motDePassePerdu.
 * 
 * @author derosiauxs
 * 
 */
public class CastoMotDePassePerduManager extends ApplicationLoggingImpl {

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------	
	
	private ProfileTools mProfileTools;

	private CastoXMLOutputManager m_XMLManager;

	private Map m_messages;
	
	private CastoWebServicesTools m_webServicesTools;
	
	private String m_emailRegex;
	
	private String emailSubject;

    private ServerSetting serverSetting;
    
    private String messageFrom;
    
    private String mUrlBaseImg;
    
    
	// ------------------------------------------------------------------------
	// CONSTANTES
	// ------------------------------------------------------------------------	
		
	private static final String CODE_RETOUR_OK = "0";

	private static final String CODE_RETOUR_IDENTIFIANT_VIDE = "1";

	private static final String CODE_RETOUR_IDENTIFIANT_INCORRECT = "2";

	private static final String CODE_RETOUR_IDENTIFIANT_INEXISTANT = "3";

	private static final String CODE_RETOUR_INCONNU = "999";
	
	private static final String BUNDLE_TEMPLATE_MAIL = "com.castorama.webservices.TemplateMailResources";

	private static final String SUBJECT_KEY = "webservice.motdepasse.perdu.subject";

	private static final String TEXT_KEY = "rappel.texte";

	private static final String TEXT_KEY_WITHOUT_NAME = "webservice.texte.sans.nom";
	// ------------------------------------------------------------------------	
	// METHODES
	// ------------------------------------------------------------------------
	
	/**
	 * Méthode appelée par le webservice.
	 *
	 * @param a_requestXML la requête sous format XML envoyé par les clients
	 * @param retourne un XML contenant la réponse
	 */
	public String motDePassePerdu(String a_requestXML) {
		if (isLoggingDebug()) {
			logDebug(a_requestXML);
		}

		String l_resultXML = null;

		try {
			MotDePassePerduRequest l_request = MotDePassePerduRequest.unmarshal(new StringReader(a_requestXML));
			String l_email = l_request.getEmail();

			if (StringUtils.isBlank(l_email)) {
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_VIDE);
				return l_resultXML;
			}

			if (!l_email.matches(getEmailRegex())) {
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_INCORRECT);
				return l_resultXML;
			}

			RepositoryItem l_profile = getProfileTools().getItemFromEmail(l_email);


			if (null != l_profile)
			{
				// Envoi du mail
				envoiMail(l_profile);
				getWebServicesTools().setUtilisateurConceptionCuisine((MutableRepositoryItem) l_profile);
				l_resultXML = getXML(CODE_RETOUR_OK);
			}
			else
			{
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_INEXISTANT);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (ValidationException l_ve) {
			if (isLoggingError()) {
				logError(l_ve);
			}
/*		} catch (RepositoryException l_re) {
			if (isLoggingError()) {
				logError(l_re);
			}*/
		} catch (Exception l_e) {
			if (isLoggingError()) {
				logError(l_e);
			}
		}

		if (l_resultXML == null) {
			if (isLoggingDebug()) {
				logDebug("Internal error. See ATG logs for more information.");
			}
			l_resultXML = getXML(CODE_RETOUR_INCONNU);
		}

		if (isLoggingDebug()) {
			logDebug("OUT:" + l_resultXML);
		}
		return l_resultXML;
	}
	

	private String getXML(String a_codeRetour) {
		return getXMLManager().getMotDePassePerduOutput(a_codeRetour,
				(String) getMessages().get(a_codeRetour));
	}

	private void envoiMail(RepositoryItem a_profile) throws Exception
	{
		// Rйcupйration du sujet du mail
		String l_sujet = ResourceUtils
				.getUserMsgResource(
						SUBJECT_KEY, BUNDLE_TEMPLATE_MAIL, null);

		String l_message;
		String login = (String) a_profile.getPropertyValue("login");
		
		String newPassword = getProfileTools().generateNewPasswordForProfile(a_profile);
        String l_civilite = (String) a_profile.getPropertyValue("civilite");
        if (l_civilite != null) {
        	if ("mr".equals(l_civilite)) {
                l_civilite = "M.";
        	} else if ("miss".equals(l_civilite)) {
                    l_civilite = "Mlle";
            } else if ("mrs".equals(l_civilite)) {
                    l_civilite = "Mme";
            }
        	
        } else {
        	l_civilite = "";
        }

		String firstName = (String) a_profile.getPropertyValue(getWebServicesTools().getPropertyManager()
				.getFirstNamePropertyName());
		String lastName = (String) a_profile.getPropertyValue(getWebServicesTools().getPropertyManager()
				.getLastNamePropertyName());

		if (null == firstName || null == lastName) {
			l_message = ResourceUtils
					.getUserMsgResource(
							TEXT_KEY_WITHOUT_NAME,
							BUNDLE_TEMPLATE_MAIL,
							null,
							new String[] {
									getServerSetting().getHost() + getUrlBaseImg(),
									login,
									newPassword,
									getServerSetting().getHost()
							}
					);
		} else {
 
            l_message = ResourceUtils
					.getUserMsgResource(
							TEXT_KEY,
							BUNDLE_TEMPLATE_MAIL,
							null,
							new String[] {
									getServerSetting().getHost() + getUrlBaseImg(),
									l_civilite,
									firstName,
									lastName,
									login,
									newPassword,
									getServerSetting().getHost()
							}
					);
		}

		// Envoi du mail
		getWebServicesTools().sendMailHTML(getMessageFrom(),
				l_sujet, login, l_message);
	}
	
	// ------------------------------------------------------------------------
	// GETTERS/SETTERS
	// ------------------------------------------------------------------------	
	
	public Map getMessages() {
		return m_messages;
	}

	public void setMessages(Map a_messages) {
		this.m_messages = a_messages;
	}

	public CastoXMLOutputManager getXMLManager() {
		return m_XMLManager;
	}

	public void setXMLManager(CastoXMLOutputManager a_manager) {
		m_XMLManager = a_manager;
	}

	public CastoWebServicesTools getWebServicesTools() {
		return m_webServicesTools;
	}

	public void setWebServicesTools(CastoWebServicesTools servicesTools) {
		m_webServicesTools = servicesTools;
	}

	public String getEmailRegex() {
		return m_emailRegex;
	}

	public void setEmailRegex(String regex) {
		m_emailRegex = regex;
	}
	
	public ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		mProfileTools = profileTools;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

	public String getMessageFrom() {
		return messageFrom;
	}

	public void setMessageFrom(String messageFrom) {
		this.messageFrom = messageFrom;
	}

	public String getUrlBaseImg() {
		return mUrlBaseImg;
	}

	public void setUrlBaseImg(String urlBaseImg) {
		mUrlBaseImg = urlBaseImg;
	}
	
	
}
