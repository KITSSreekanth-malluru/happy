package com.castorama.webservices;

import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import atg.commerce.util.PlaceList.Place;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileServices;
import atg.userprofiling.ProfileTools;

import com.castorama.commerce.profile.CastAddressHelper;
import com.castorama.utils.ServerSetting;
import com.castorama.webservices.xsd.CreateUserRequest;
import com.castorama.webservices.xsd.NewUser;
import com.castorama.webservices.xsd.types.TypeAccesType;
import com.castorama.commerce.profile.Constants;

public class CastoCreateUserManager extends ApplicationLoggingImpl {

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------

	private static final String CANAL_NL = "Conception Cuisine";
	
	private CastoXMLOutputManager m_XMLManager;

	private Map m_messages;

	private String m_dateRegex;	

	private CastoWebServicesTools m_webServicesTools;
	
	private ProfileServices m_profileServices;
	
	private CastNewsletterManager m_newsletterManager;
	
	private ProfileTools mProfileTools;
	
	private CastAddressHelper addressHelper;

    private ServerSetting serverSetting;
    
    private String messageFrom;
    
    private String mUrlBaseImg;

    private static final String BUNDLE_TEMPLATE_MAIL = "com.castorama.webservices.TemplateMailResources";

	private static final String SUBJECT_KEY = "webservice.createuser.subject";

	private static final String TEXT_KEY = "webservice.createuser.texte";

	
	// ------------------------------------------------------------------------
	// CONSTANTES
	// ------------------------------------------------------------------------

	private static final String CODE_RETOUR_OK = "0";

	private static final String CODE_RETOUR_ERR_SAISIE = "1";

	private static final String CODE_RETOUR_ERR_EMAIL_INCORRECT = "2";

	private static final String CODE_RETOUR_ERR_CIVILITE_INCORRECTE = "3";

	private static final String CODE_RETOUR_ERR_PASSWORD_TROP_COURT = "4";

	private static final String CODE_RETOUR_ERR_CODE_POSTAL_INEXISTANT = "5";

	private static final String CODE_RETOUR_ERR_COMPTE_EXISTANT = "6";

	private static final String CODE_RETOUR_ERR_DDN_INCORRECTE = "7";

	private static final String CODE_RETOUR_ERR_CUSTOMER_EXISTS = "8";

	private static final String CODE_RETOUR_INCONNU = "999";

    private static final Integer TYPE_MAJ_PROFIL_CREATION = new Integer(0);
	
	// ------------------------------------------------------------------------
	// METHODES
	// ------------------------------------------------------------------------

	/**
	 * M�thode appel�e par le webservice. Cr�� un utilisateur avec les informations de la requ�te.
	 * 
	 * @param a_requestXML
	 *            la requ�te sous format XML envoy� par les clients
	 * @return
	 *            un XML contenant la r�ponse
	 */
	public String createUser(String a_requestXML) {
		if (isLoggingDebug()) {
			logDebug(a_requestXML);
		}

		String l_resultXML = null;

		try {
			CreateUserRequest l_request = CreateUserRequest.unmarshal(new StringReader(a_requestXML));
			
			TypeAccesType l_typeAcces = l_request.getTypeAcces();
			NewUser l_newUser = l_request.getNewUser();
			
			//check magasin reference
			if (!StringUtils.isEmpty(l_newUser.getMagasinReference())) {
				if (!getWebServicesTools().checkMagasinRefId(l_newUser.getMagasinReference())) {
					l_newUser.setMagasinReference(null);
				}
			}
			
			// en fonction du type acces, on aiguille vers la m�thode de v�rification et cr�ation appropri�e
			if (l_typeAcces == TypeAccesType.MAGASIN) {
				l_resultXML = createUserFromMagasin(l_newUser, l_typeAcces);
			}
			else
			{
				l_resultXML = createUserFromWeb(l_newUser, l_typeAcces);
			}

		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (ValidationException l_ve) {
			if (isLoggingError()) {
				logError(l_ve);
			}
		} catch (RepositoryException l_re) {
			if (isLoggingError()) {
				logError(l_re);
			}
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
			logDebug("OUT:" + l_resultXML);
		}
		return l_resultXML;
	}

	/**
	 * V�rifie les informations d'entr�e pour un utilisateur de magasin.
	 * 
	 * @param l_newUser objet XML utilisateur
	 * @param l_typeAcces type acces (web)
	 * @return un xml de r�ponse
	 * @throws RepositoryException
	 * @throws ServletException
	 */
	public String createUserFromMagasin(NewUser l_newUser, TypeAccesType l_typeAcces)
			throws RepositoryException, ServletException {
		String l_ret = null;

		// --------------------------------------------------------------------
		// REG_CC_6_2 + REG_CC_6_6
		// --------------------------------------------------------------------

		// CIVILITE
		if (StringUtils.isBlank(l_newUser.getCivilite())) {
			l_ret = getXMLErrSaisie("civilite");
		}
		// NOM
		if (l_ret == null && StringUtils.isBlank(l_newUser.getNom())) {
			l_ret = getXMLErrSaisie("nom");
		}
		
		// PRENOM
		if (l_ret == null && StringUtils.isBlank(l_newUser.getPrenom())) {
			l_ret = getXMLErrSaisie("prenom");
		}
		
		// CODE POSTAL
		if (l_ret == null && StringUtils.isBlank(l_newUser.getCodePostal())) {
			l_ret = getXMLErrSaisie("code postal");
		}

		// PASSWORD
		if (l_ret == null && StringUtils.isBlank(l_newUser.getPassword())) {
			l_ret = getXMLErrSaisie("mot de passe");
		}
		
		// --------------------------------------------------------------------
		// REG_CC_6_12
		// --------------------------------------------------------------------

		// INSCRIPTION NEWSLETTER
		if (l_ret == null && (l_newUser.getInscriptionNewsletter() || l_newUser.getInscriptionNewsletterPartenaires())) {
			if (StringUtils.isBlank(l_newUser.getEmail())) {
				l_ret = getXMLErrSaisie("adresse email");
			}
		}
		
		// EMAIL OU ADRESSE POSTALE
		if (l_ret == null && StringUtils.isBlank(l_newUser.getEmail())
				&& StringUtils.isBlank(l_newUser.getAdresse())) {
			l_ret = getXMLErrSaisie("adresse email ou postale");
		}
		
		// ------------------------
		// FIN CONTROLES EXISTENCES
		// ------------------------
		
		// ---------------------------
		// CONTROLES REGLES DE GESTION
		// ---------------------------		
		
		if (l_ret == null)
			l_ret = checkValueCivilite(l_newUser.getCivilite());
		if (l_ret == null)
			l_ret = checkValueCodePostal(l_newUser.getCodePostal());		
		if (l_ret == null && !StringUtils.isBlank(l_newUser.getEmail()))
			l_ret = checkValueEmail(l_newUser.getEmail());
		if (l_ret == null)
			l_ret = checkValuePassword(l_newUser.getPassword());
		if (l_ret == null && !StringUtils.isBlank(l_newUser.getDateNaissance()))
			l_ret = checkValueDateNaissance(l_newUser.getDateNaissance());
		
		// --------------------------------------------------------------------
		// REG_CC_6_8
		// --------------------------------------------------------------------
		if (l_ret == null) {
			String l_email = l_newUser.getEmail();
			if (!StringUtils.isBlank(l_email)) {
				RepositoryItem l_profile = getWebServicesTools().findByLoginIgnoreCase(l_newUser.getEmail());
				if (l_profile != null) {
					l_ret = getXML(CODE_RETOUR_ERR_COMPTE_EXISTANT, null);
				}
			} else {
				// Si l’email n’est pas spйcifiй, il faut vйrifier si un client
				// existe avec les mкmes informations : civilitй, nom, prйnom, code
				// postal et adresse postale
				
				String civilite = translateCiviliteFromIntegerStringToString(l_newUser.getCivilite());
				if (isLoggingDebug()) {
					logDebug("civilite: " + civilite);
					logDebug("lastName: " + l_newUser.getNom());
					logDebug("firstName: " + l_newUser.getPrenom());
				}
				RepositoryItem[] userItems = getWebServicesTools().findByParamsIgnoreCase(civilite,
						l_newUser.getNom(), l_newUser.getPrenom());
				
				if (userItems != null) {
					for (RepositoryItem item : userItems) {
						if (l_ret != null) {
							break;
						}
						if (isLoggingDebug()) {
							logDebug("user: " + item);
						}
	
						RepositoryItem l_billingAddress = (RepositoryItem) item.getPropertyValue("billingAddress");
						String l_postalCode = "";
						String l_address1 = "";
						if (l_billingAddress != null) {
							l_postalCode = (String) l_billingAddress.getPropertyValue("postalCode");
							l_address1 = (String) l_billingAddress.getPropertyValue("address1");
							
							if (isLoggingDebug()) {
								logDebug("--> comparaison ");
								logDebug("l_postalCode: " +l_postalCode);
								logDebug("l_address1: " + l_address1);
							}
						}
						
						if (l_billingAddress == null && l_newUser.getAdresse() == null 
								&& l_newUser.getCodePostal() == null) {
							l_ret = getXML(CODE_RETOUR_ERR_CUSTOMER_EXISTS, null);
						}
							
						if (l_ret == null && l_billingAddress != null) {								
							
							// le code postal correspond? (vide tous les deux ou rempli а la mкme chose tous les deux
							if ((l_postalCode == null && l_newUser.getCodePostal() == null) ||
								(l_postalCode != null && l_newUser.getCodePostal() != null && l_postalCode.equalsIgnoreCase(l_newUser.getCodePostal())))
							{
								if (isLoggingDebug()) {
									logDebug("code postal identique ...");
								}
								
								// l'adresse correspond? (vide toutes les deux ou rempli а la mкme chose toutes les deux
								if ((l_address1 == null && l_newUser.getAdresse() == null) ||
									(l_address1 != null && l_newUser.getAdresse() != null && l_address1.trim().equalsIgnoreCase(l_newUser.getAdresse().trim())))
								{
									if (isLoggingDebug()) {
										logDebug("adresse identique ...");
									}
									l_ret = getXML(CODE_RETOUR_ERR_CUSTOMER_EXISTS, null);
								}
							}
						}
					}
				}
			}
		}

		// --------------------------------------------------------------------
		// REG_CC_6_10
		// --------------------------------------------------------------------
		if (l_ret == null) {
			l_ret = createUser(l_newUser, l_typeAcces);
		}

		return l_ret;
	}

	/**
	 * V�rifie les informations d'entr�e pour un utilisateur du web.
	 * 
	 * @param l_newUser objet XML utilisateur
	 * @param l_typeAcces type acces (web)
	 * @return un xml de r�ponse
	 * @throws RepositoryException
	 * @throws ServletException
	 */
	public String createUserFromWeb(NewUser l_newUser, TypeAccesType l_typeAcces)
			throws RepositoryException, ServletException {
		String l_ret = null;

		// --------------------------------------------------------------------
		// REG_CC_6_1
		// --------------------------------------------------------------------

		// CIVILITE
		if (StringUtils.isBlank(l_newUser.getCivilite())) {
			l_ret = getXMLErrSaisie("civilite");
		}
		
		// NOM
		if (l_ret == null && StringUtils.isBlank(l_newUser.getNom())) {
			l_ret = getXMLErrSaisie("nom");
		}
		
		// PRENOM
		if (l_ret == null && StringUtils.isBlank(l_newUser.getPrenom())) {
			l_ret = getXMLErrSaisie("prenom");
		}
		
		// CODE POSTAL
		if (l_ret == null && StringUtils.isBlank(l_newUser.getCodePostal())) {
			l_ret = getXMLErrSaisie("code postal");
		}

		// EMAIL
		if (l_ret == null && StringUtils.isBlank(l_newUser.getEmail())) {
			l_ret = getXMLErrSaisie("adresse email");
		}
		
		// PASSWORD
		if (l_ret == null && StringUtils.isBlank(l_newUser.getPassword())) {
			l_ret = getXMLErrSaisie("mot de passe");
		}
		
		// ------------------------
		// FIN CONTROLES EXISTENCES
		// ------------------------
		
		// ---------------------------
		// CONTROLES REGLES DE GESTION
		// ---------------------------		
		
		if (l_ret == null)
			l_ret = checkValueCivilite(l_newUser.getCivilite());
		if (l_ret == null)
			l_ret = checkValueCodePostal(l_newUser.getCodePostal());
		if (l_ret == null)
			l_ret = checkValueEmail(l_newUser.getEmail());
		if (l_ret == null)
			l_ret = checkValuePassword(l_newUser.getPassword());		
		if (l_ret == null && !StringUtils.isBlank(l_newUser.getDateNaissance()))
			l_ret = checkValueDateNaissance(l_newUser.getDateNaissance());
		
		// --------------------------------------------------------------------
		// REG_CC_6_7
		// --------------------------------------------------------------------
		if (l_ret == null) {
			//String l_email = l_newUser.getEmail();
			//RepositoryItem l_profile = getProfileTools().getItemFromEmail(l_email);
			RepositoryItem l_profile = getWebServicesTools().findByLoginIgnoreCase(l_newUser.getEmail());
			if (l_profile != null) {
				l_ret = getXML(CODE_RETOUR_ERR_COMPTE_EXISTANT, null);
			}
		}

		// --------------------------------------------------------------------
		// REG_CC_6_10
		// --------------------------------------------------------------------
		if (l_ret == null) {
			l_ret = createUser(l_newUser, l_typeAcces);
		}

		return l_ret;
	}


	
	private String translateCiviliteFromIntegerStringToString(String a_civilite) {
		
		int l_civilite = 1;
		try {
			l_civilite = Integer.parseInt(a_civilite);
		}
		catch (NumberFormatException l_nfe)
		{
			if (isLoggingError()) {
				logError(l_nfe);
			}
		}
		
		switch (l_civilite)
		{
		case 1: return "mr";
		case 2: return "mrs";
		case 3: return "miss";
		default: return "organization";
		}		
	}
	
	private void ajoutChamps(StringBuffer profileAsXML, String champs, String valeur)
	{
		if (valeur != null && !"null".equals(valeur))
			profileAsXML.append("<user:user." + champs + ">" + valeur + "</user:user." + champs + ">");
	}
	
	/**
	 * Renvoie une requ�te XML de cr�ation d'utilisateur pour utiliser la m�thode interne ATG createUser(ProfileAsXML).
	 * champs g�r� : login, email, password, reminderPassword, reponseReminder, civilite, firstName, lastName, typeMAJProfil, dateMAJProfil, dateOfBirth, id_magasin_ref
	 */
	private String getATGXMLFromRequest(NewUser a_newUser)
	{
		StringBuffer profileAsXML = new StringBuffer("<user:user xmlns:user=\"http://www.atg.com/ns/userMapping/UserProfiles/user\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
			"xsi:schemaLocation=\"http://www.atg.com/ns/userMapping/UserProfiles/user userMapping+UserProfiles+user.xsd \">");

		// Si l'email n'est pas renseign�, on met un identifiant temporaire. On le renseignera avec l'ID ATG une fois cr�� (on ne le connait pas � l'avance!)
		if (!StringUtils.isBlank(a_newUser.getEmail()))
		{
			ajoutChamps(profileAsXML, "login", a_newUser.getEmail());
			ajoutChamps(profileAsXML, "email", a_newUser.getEmail());
		}
		else
		{
			ajoutChamps(profileAsXML, "login", (int) (Math.random() * 1000000) + "TMP" + (int) (Math.random() * 1000000));
		}
								
		ajoutChamps(profileAsXML, "password", a_newUser.getPassword());
		ajoutChamps(profileAsXML, "reminderPassword", " ");
		ajoutChamps(profileAsXML, "reponseReminder", " ");
		ajoutChamps(profileAsXML, "civilite", translateCiviliteFromIntegerStringToString(a_newUser.getCivilite()));
		ajoutChamps(profileAsXML, "firstName", a_newUser.getPrenom());
		ajoutChamps(profileAsXML, "lastName", a_newUser.getNom());
		Timestamp l_today = new Timestamp(new Date().getTime());		
		ajoutChamps(profileAsXML, "typeMAJprofil", "" + TYPE_MAJ_PROFIL_CREATION);
		ajoutChamps(profileAsXML, "dateMAJprofil", "" + l_today);
		ajoutChamps(profileAsXML, "utilisateurConceptionCuisine", "true");
		
		if (!StringUtils.isBlank(a_newUser.getDateNaissance()))
		{
			try 
			{
				SimpleDateFormat l_sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date l_dn = l_sdf.parse(a_newUser.getDateNaissance());
				ajoutChamps(profileAsXML, "dateOfBirth", new Timestamp(l_dn.getTime()).toString());
			}
			catch (ParseException l_e)
			{			
				if (isLoggingError()) {
					logError(l_e);
				}
			}
		}
		
		ajoutChamps(profileAsXML, "id__magasin__ref", a_newUser.getMagasinReference());
		profileAsXML.append("</user:user>");
		
		if (isLoggingDebug()) {
			logDebug(profileAsXML.toString());
		}
		
		return profileAsXML.toString();
	}
	
	private String createUser(NewUser a_newUser, TypeAccesType a_typeAccess) throws RepositoryException, ServletException {
		
		// --------------------------------------------------------------------
		// REG_CC_6_10 + REG_CC_6_11 (ProfileServices.createUser lance l'�v�nement register)
		// --------------------------------------------------------------------
		String profileAsXML = getATGXMLFromRequest(a_newUser);
		String id = getProfileServices().createUser(profileAsXML);		
		
		MutableRepository l_userRepository = (MutableRepository) getProfileTools().getProfileRepository();
		MutableRepositoryItem l_profile = (MutableRepositoryItem) l_userRepository.getItemForUpdate(id, "user");

		// apr�s cr�ation, on modifie le login si le mail n'a pas �t� renseign� � l'inscription
		if (StringUtils.isBlank(a_newUser.getEmail()))
		{
			l_profile.setPropertyValue("login", id);			
		}		
		
		// --------------------------------------------------------------------
		// Remplissage de la billingAddress (d�j� cr�� par ATG normalement)
		// --------------------------------------------------------------------
		MutableRepositoryItem l_billingAddress = (MutableRepositoryItem) l_profile.getPropertyValue("billingAddress");
		if (l_billingAddress != null)
		{
			if (isLoggingDebug()) {
				logDebug("remplissage de la billingAddress ...");
			}
			
			l_billingAddress.setPropertyValue("adresseActive", new Integer("1"));
			l_billingAddress.setPropertyValue("typeAdresse", new Integer("2"));
			
			if (a_newUser.getCivilite() != null) {
				String newCivilite = translateCiviliteFromIntegerStringToString(a_newUser.getCivilite());
				l_billingAddress.setPropertyValue("prefix", newCivilite);
				l_billingAddress.setPropertyValue("civilite", newCivilite);
			}
			if (a_newUser.getEmail() != null)
				l_billingAddress.setPropertyValue("email", a_newUser.getEmail());
			if (a_newUser.getPrenom() != null)
				l_billingAddress.setPropertyValue("firstName", a_newUser.getPrenom());
			if (a_newUser.getNom() != null)
				l_billingAddress.setPropertyValue("lastName", a_newUser.getNom());
			if (a_newUser.getSociete() != null)
				l_billingAddress.setPropertyValue("societe", a_newUser.getSociete());
			if (a_newUser.getCodePostal() != null)
				l_billingAddress.setPropertyValue("postalCode", a_newUser.getCodePostal());
			if (a_newUser.getAdresse() != null)
				l_billingAddress.setPropertyValue("address1", a_newUser.getAdresse());
			if (a_newUser.getAdresse() != null)
				l_billingAddress.setPropertyValue("address2", (a_newUser.getComplement() != null ? a_newUser.getComplement() : ""));
			if (a_newUser.getVille() != null)
				l_billingAddress.setPropertyValue("city", a_newUser.getVille());
			
			Place place = getWebServicesTools().getCountry(a_newUser.getPays());
			l_billingAddress.setPropertyValue(Constants.COUNTRY_ADDRESS_PROP, place.getDisplayName().trim());
			l_billingAddress.setPropertyValue(Constants.STATE_ADDRESS_PROP, place.getCode().trim());
			/*AP
			if (a_newUser.getPays() != null)
				l_billingAddress.setPropertyValue("country", a_newUser.getPays());
			*/
			Map l_secondaryBillingShippingAddresses = (Map) l_profile.getPropertyValue("secondaryBillingShippingAddresses");
			l_secondaryBillingShippingAddresses.put("Adresse de facturation", l_billingAddress);
		}
		
		l_userRepository.updateItem(l_profile);

		// Si opt-in ...
		int flagNewsletter = a_newUser.isInscriptionNewsletter() ? CastNewsletterManager.NEWSLETTER_1 : CastNewsletterManager.NEWSLETTER_0;
        int flagPartenaires = a_newUser.isInscriptionNewsletterPartenaires() ? CastNewsletterManager.NEWSLETTER_1 : CastNewsletterManager.NEWSLETTER_0;
        int flagRecontact = a_newUser.getAcceptRecontact();
		getNewsletterManager().saveNewsletter((String) l_profile.getPropertyValue("login"), CANAL_NL, l_profile, flagNewsletter, flagPartenaires, flagRecontact);
		
		// --------------------------------------------------------------------
		// REG_CC_6_14
		// --------------------------------------------------------------------		
		//getWebServicesTools().setUtilisateurConceptionCuisine(l_profile);
		
		try {
			sendEMail(a_newUser);			
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		
		return getXML(CODE_RETOUR_OK, id);
	}
	
	// --------------------------------------------------------------------
	// REG_CC_6_4
	// --------------------------------------------------------------------
	private String checkValueCivilite(String a_civilite)
	{
		if (!a_civilite.equals("1") && !a_civilite.equals("2") && !a_civilite.equals("3")) {
			return getXML(CODE_RETOUR_ERR_CIVILITE_INCORRECTE, null);
		}
		
		return null;
	}
	
	
	// --------------------------------------------------------------------
	// REG_CC_6_3
	// --------------------------------------------------------------------
	private String checkValueEmail(String a_email)
	{
		if (!StringUtils.isBlank(a_email) && !getWebServicesTools().isEmail(a_email)) {
			return getXML(CODE_RETOUR_ERR_EMAIL_INCORRECT, null);
		}
		return null;
	}
	
	// --------------------------------------------------------------------
	// REG_CC_6_5
	// --------------------------------------------------------------------
	private String checkValuePassword(String a_password)
	{
		if (a_password.length() < 4) {
			return getXML(CODE_RETOUR_ERR_PASSWORD_TROP_COURT, null);
		}
		return null;
	}
	
	
	// --------------------------------------------------------------------
	// REG_CC_6_15
	// --------------------------------------------------------------------		
	private String checkValueDateNaissance(String a_dateNaissance)
	{
		if (!StringUtils.isBlank(a_dateNaissance) && !a_dateNaissance.matches(getDateRegex()))
		{
			return getXML(CODE_RETOUR_ERR_DDN_INCORRECTE, null);
		}
		return null;
	}
	
	private String checkValueCodePostal(String a_cp) throws RepositoryException, ServletException
	{
		List villes = getAddressHelper().getCitiesViaCodePostal(a_cp);
		
		if (villes == null || villes.isEmpty()) {
			return getXML(CODE_RETOUR_ERR_CODE_POSTAL_INEXISTANT, null);
		}

		return null;
	}
	

	private String getXML(String a_codeRetour, String user) {
		return getXMLManager().getCreateUserOutput(a_codeRetour,
				(String) getMessages().get(a_codeRetour), user);
	}

	private String getXMLErrSaisie(String a_champs) {
		String msg = (String) getMessages().get(CODE_RETOUR_ERR_SAISIE);
		msg = msg.replaceAll("CHAMPS", a_champs);
		return getXMLManager().getCreateUserOutput(CODE_RETOUR_ERR_SAISIE, msg,
				null);
	}

	private void sendEMail(NewUser newUser) throws Exception
	{
		if (StringUtils.isBlank(newUser.getEmail())) {
			return;
		}
		
		String l_sujet = ResourceUtils
				.getUserMsgResource(
						SUBJECT_KEY, BUNDLE_TEMPLATE_MAIL, null);

		String l_message;
		String login = newUser.getEmail();
		
		String password = newUser.getPassword();
        String l_civilite = (String) newUser.getCivilite();
        if (l_civilite != null) {
        	if ("1".equals(l_civilite)) {
                l_civilite = "M.";
        	} else if ("3".equals(l_civilite)) {
                    l_civilite = "Mlle";
            } else if ("2".equals(l_civilite)) {
                    l_civilite = "Mme";
            }
        	
        } else {
        	l_civilite = "";
        }

		String firstName = newUser.getPrenom();
		String lastName = newUser.getNom();

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
						password,
						getServerSetting().getHost()
				}
		);

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

	public String getDateRegex() {
		return m_dateRegex;
	}

	public void setDateRegex(String regex) {
		m_dateRegex = regex;
	}
	
	public CastoWebServicesTools getWebServicesTools() {
		return m_webServicesTools;
	}

	public void setWebServicesTools(CastoWebServicesTools servicesTools) {
		m_webServicesTools = servicesTools;
	}
	
	public ProfileServices getProfileServices() {
		return m_profileServices;
	}

	public void setProfileServices(ProfileServices a_profileServices) {
		m_profileServices = a_profileServices;
	}
	
	public CastNewsletterManager getNewsletterManager() {
		return m_newsletterManager;
	}

	public void setNewsletterManager(CastNewsletterManager a_newsletterManager) {
		m_newsletterManager = a_newsletterManager;
	}
	
	public ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		mProfileTools = profileTools;
	}

	public CastAddressHelper getAddressHelper() {
		return addressHelper;
	}

	public void setAddressHelper(CastAddressHelper addressHelper) {
		this.addressHelper = addressHelper;
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
