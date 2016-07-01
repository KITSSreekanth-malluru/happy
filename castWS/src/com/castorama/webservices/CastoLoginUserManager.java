package com.castorama.webservices;

import java.io.StringReader;
import java.util.Map;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileServices;
import atg.userprofiling.ProfileTools;

import com.castorama.webservices.xsd.LoginUserRequest;

public class CastoLoginUserManager extends GenericService {

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------

	private CastoXMLOutputManager m_XMLManager;

	private Map m_messages;

	private String m_emailRegex;
	
	private String m_numericOnlyRegex;

	private CastoWebServicesTools m_webServicesTools;
	
	private ProfileServices m_profileServices;
	
	private ProfileTools mProfileTools;
	
	// ------------------------------------------------------------------------
	// CONSTANTES
	// ------------------------------------------------------------------------

	private static final String CODE_RETOUR_OK = "0";

	private static final String CODE_RETOUR_IDENTIFIANT_VIDE = "1";

	private static final String CODE_RETOUR_MOT_DE_PASSE_VIDE = "2";

	private static final String CODE_RETOUR_IDENTIFIANT_INCORRECT = "3";

	private static final String CODE_RETOUR_IDENTIFIANT_INEXISTANT = "4";

	private static final String CODE_RETOUR_MOT_DE_PASSE_INCORRECT = "5";

	private static final String CODE_RETOUR_INCONNU = "999";

	// ------------------------------------------------------------------------
	// METHODES
	// ------------------------------------------------------------------------

	/**
	 * Méthode appelée par le webservice.
	 * 
	 * @param a_requestXML
	 *            la requête sous format XML envoyé par les clients
	 * @param retourne
	 *            un XML contenant la réponse
	 */
	public String loginUser(String a_requestXML) {
		if (isLoggingDebug()) {
			logDebug(a_requestXML);
		}

		String l_resultXML = null;

		try {
			LoginUserRequest l_request = LoginUserRequest
					.unmarshal(new StringReader(a_requestXML));
			String l_login = l_request.getLogin();
			String l_password = l_request.getPassword();

			// ----------------------------------------------------------------
			// REG_CC_3_1
			// ----------------------------------------------------------------
			
			if (StringUtils.isBlank(l_login)) {
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_VIDE, null);
				return l_resultXML;
			}

			if (StringUtils.isBlank(l_password)) {
				l_resultXML = getXML(CODE_RETOUR_MOT_DE_PASSE_VIDE, null);
				return l_resultXML;
			}

			// ----------------------------------------------------------------
			// REG_CC_3_2
			// ----------------------------------------------------------------
			
			if (!l_login.matches(getEmailRegex()) && !l_login.matches(getNumericOnlyRegex())) {
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_INCORRECT, null);
				return l_resultXML;
			}

			// ----------------------------------------------------------------
			// REG_CC_3_3
			// ----------------------------------------------------------------
			
			RepositoryItem l_profile = getProfileTools().getItem(l_login, null);

			if (null != l_profile) {
				
				// ------------------------------------------------------------
				// REG_CC_3_8
				// ------------------------------------------------------------
				
				if (getWebServicesTools().isDifferntPassword(l_login, l_password, l_profile)) {
					l_resultXML = getXML(CODE_RETOUR_MOT_DE_PASSE_INCORRECT,
							null);
					
				} else {
					
					// --------------------------------------------------------
					// REG_CC_3_6
					// --------------------------------------------------------
					
					String l_profileId = getProfileServices().loginUser(l_login, l_password, false);
					
					// --------------------------------------------------------					
					// REG_CC_3_7
					// --------------------------------------------------------
					
					if (l_profileId != null)
					{
						getWebServicesTools().setUtilisateurConceptionCuisine(
							(MutableRepositoryItem) l_profile);
						
						// --------------------------------------------------------					
						// REG_CC_3_4
						// --------------------------------------------------------

						l_resultXML = getXML(CODE_RETOUR_OK, l_profileId);
					}
				}
				
			} else {
				
				// ------------------------------------------------------------			
				// REG_CC_3_5
				// ------------------------------------------------------------
				
				l_resultXML = getXML(CODE_RETOUR_IDENTIFIANT_INEXISTANT, null);
				
			}

		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (ValidationException l_ve) {
			if (isLoggingError()) {
				logError(l_ve);
			}
		/* AP } catch (RepositoryException l_re) {
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
			l_resultXML = getXML(CODE_RETOUR_INCONNU, null);
		}

		if (isLoggingDebug()) {
			logDebug(l_resultXML);
		}
		return l_resultXML;
	}

	private String getXML(String a_codeRetour, String user) {
		return getXMLManager().getLoginUserOutput(a_codeRetour,
				(String) getMessages().get(a_codeRetour), user);
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

	public String getEmailRegex() {
		return m_emailRegex;
	}

	public void setEmailRegex(String regex) {
		m_emailRegex = regex;
	}

	public CastoWebServicesTools getWebServicesTools() {
		return m_webServicesTools;
	}

	public void setWebServicesTools(CastoWebServicesTools servicesTools) {
		m_webServicesTools = servicesTools;
	}

	public String getNumericOnlyRegex() {
		return m_numericOnlyRegex;
	}

	public void setNumericOnlyRegex(String numericOnlyRegex) {
		m_numericOnlyRegex = numericOnlyRegex;
	}

	public ProfileServices getProfileServices() {
		return m_profileServices;
	}

	public void setProfileServices(ProfileServices a_profileServices) {
		m_profileServices = a_profileServices;
	}

	public ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		mProfileTools = profileTools;
	}

}
