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
import com.castorama.commerce.profile.Constants;
import com.castorama.utils.ServerSetting;
import com.castorama.webservices.xsd.UpdateUserRequest;
import com.castorama.webservices.xsd.UpdatedUser;
import com.castorama.webservices.xsd.UserType;
import com.castorama.webservices.xsd.types.TypeAccesType;

public class CastoUpdateUserManager extends ApplicationLoggingImpl {

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

    // ------------------------------------------------------------------------
	// CONSTANTES
	// ------------------------------------------------------------------------

	private static final String CODE_RETOUR_OK = "0";

	private static final String CODE_RETOUR_ERR_SAISIE = "1";

	private static final String CODE_RETOUR_ERR_EMAIL_INCORRECT = "2";

	private static final String CODE_RETOUR_ERR_CIVILITE_INCORRECTE = "3";

	private static final String CODE_RETOUR_ERR_PASSWORD_TROP_COURT = "4";

	private static final String CODE_RETOUR_ERR_CODE_POSTAL_INEXISTANT = "5";

	private static final String CODE_RETOUR_ERR_COMPTE_INEXISTANT = "6";
	
	private static final String CODE_RETOUR_ERR_ADRESSE_VIDE = "7";
	
	private static final String CODE_RETOUR_ERR_CONSERVATION_MAIL = "8";
	
	private static final String CODE_RETOUR_ERR_CONSERVATION_PASSWORD = "9";

	private static final String CODE_RETOUR_ERR_DDN_INCORRECTE = "10";
	
	private static final String CODE_RETOUR_ERR_EMAIL_DEJA_EXISTANT = "11";
	
	private static final String CODE_RETOUR_ERR_EMAIL_OBLIGATOIRE= "12";

	private static final String CODE_RETOUR_ERR_CUSTOMER_EXISTS = "13";

	private static final String CODE_RETOUR_INCONNU = "999";

    private static final Integer TYPE_MAJ_PROFIL_MODIFICATION = new Integer(1);
    
    private static final String BUNDLE_TEMPLATE_MAIL = "com.castorama.webservices.TemplateMailResources";

	private static final String SUBJECT_KEY = "webservice.updateuser.subject";

	private static final String TEXT_KEY = "webservice.updateuser.texte";

	// ------------------------------------------------------------------------	
	// METHODES
	// ------------------------------------------------------------------------
	
	/**
	 * M�thode appel�e par le webservice.
	 *
	 * @param a_requestXML la requ�te sous format XML envoy� par les clients
	 * @param retourne un XML contenant la r�ponse
	 */
	public String updateUser(String a_requestXML)
	{
		if (isLoggingDebug()) {
			logDebug(a_requestXML);
		}

		String l_resultXML = null;

		try {
			UpdateUserRequest l_request = UpdateUserRequest.unmarshal(new StringReader(a_requestXML));
			
			TypeAccesType l_typeAcces = l_request.getTypeAcces();
			UpdatedUser l_updatedUser = l_request.getUpdatedUser();
			
			// on v�rifie directement si l'utilisateur existe (on a l'id !)
			if (StringUtils.isBlank(l_updatedUser.getId()))
			{
				l_resultXML = getXMLErrSaisie("numero client");
			}
			
			if (l_resultXML == null)
			{
				//check magasin reference
				if (!StringUtils.isEmpty(l_updatedUser.getMagasinReference())) {
					if (!getWebServicesTools().checkMagasinRefId(l_updatedUser.getMagasinReference())) {
						l_updatedUser.setMagasinReference(null);
					}
				}
				
				// en fonction du type acces, on aiguille vers la m�thode de v�rification et cr�ation appropri�e
				if (l_typeAcces == TypeAccesType.MAGASIN) {
					l_resultXML = checkUserFromMagasin(l_updatedUser, l_typeAcces);
				}
				else
				{
					l_resultXML = checkUserFromWeb(l_updatedUser, l_typeAcces);
				}
				
				// existence de l'utilisateur � modifier?
				if (l_resultXML == null)
				{
					MutableRepositoryItem l_profile = ((MutableRepository) getProfileTools().getProfileRepository()).getItemForUpdate(l_updatedUser.getId(), "user");
					
					if (l_profile == null)
					{
						// --------------------------------------------------------------------				
						// REG_CC_7_7
						// --------------------------------------------------------------------				
						l_resultXML = getXML(CODE_RETOUR_ERR_COMPTE_INEXISTANT);
					}
					else
					{

						l_resultXML = checkValues(l_updatedUser, l_typeAcces, l_profile);
						
						// --------------------------------------------------------------------
						// REG_CC_6_10
						// --------------------------------------------------------------------							
						if (l_resultXML == null) 
						{
							l_resultXML = updateUser(l_updatedUser, l_profile, l_typeAcces);
						}
					}							
				}
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
			l_resultXML = getXML(CODE_RETOUR_INCONNU);
		}

		if (isLoggingDebug()) {
			logDebug("OUT:" + l_resultXML);
		}
		return l_resultXML;
	}
	
	/**
	 * V�rifie les informations d'entr�e pour un utilisateur de magasin.
	 * 
	 * @param a_updatedUser objet XML utilisateur
	 * @param l_typeAcces type acces (web)
	 * @return un xml de r�ponse
	 * @throws RepositoryException
	 * @throws ServletException
	 */
	public String checkUserFromMagasin(UserType a_user, TypeAccesType l_typeAcces)
			throws RepositoryException, ServletException {
		String l_ret = null;

		// --------------------------------------------------------------------
		// REG_CC_6_2 + REG_CC_6_6
		// --------------------------------------------------------------------

		// CIVILITE
		if (StringUtils.isBlank(a_user.getCivilite())) {
			l_ret = getXMLErrSaisie("civilite");
		}

		// NOM
		if (l_ret == null && StringUtils.isBlank(a_user.getNom())) {
			l_ret = getXMLErrSaisie("nom");
		}
		
		// PRENOM
		if (l_ret == null && StringUtils.isBlank(a_user.getPrenom())) {
			l_ret = getXMLErrSaisie("prenom");
		}
		
		// CODE POSTAL
		if (l_ret == null && StringUtils.isBlank(a_user.getCodePostal())) {
			l_ret = getXMLErrSaisie("code postal");
		}
		
		// PASSWORD
		if (l_ret == null && StringUtils.isBlank(a_user.getPassword())) {
			l_ret = getXMLErrSaisie("mot de passe");
		}
		
		// --------------------------------------------------------------------
		// REG_CC_6_12
		// --------------------------------------------------------------------

		// si inscription newsletter est coch�, il faut absolument un email!
		if (l_ret == null && StringUtils.isBlank(a_user.getEmail()) && (a_user.isInscriptionNewsletter() || a_user.isInscriptionNewsletterPartenaires()))
		{
			l_ret = getXML(CODE_RETOUR_ERR_EMAIL_OBLIGATOIRE);
		}
		
		if (l_ret == null && StringUtils.isBlank(a_user.getEmail())
				&& StringUtils.isBlank(a_user.getAdresse())) {
			l_ret = getXMLErrSaisie("adresse email ou postale");
		}

		return l_ret;
	}

	/**
	 * V�rifie les informations d'entr�e pour un utilisateur du web.
	 * 
	 * @param a_updatedUser objet XML utilisateur
	 * @param l_typeAcces type acces (web)
	 * @return un xml de r�ponse
	 * @throws RepositoryException
	 * @throws ServletException
	 */
	public String checkUserFromWeb(UserType a_user, TypeAccesType l_typeAcces)
			throws RepositoryException, ServletException {
		String l_ret = null;

		// --------------------------------------------------------------------
		// REG_CC_6_1
		// --------------------------------------------------------------------

		// CIVILITE
		if (StringUtils.isBlank(a_user.getCivilite())) {
			l_ret = getXMLErrSaisie("civilite");
		}
		
		// NOM
		if (l_ret == null && StringUtils.isBlank(a_user.getNom())) {
			l_ret = getXMLErrSaisie("nom");
		}
		
		// PRENOM
		if (l_ret == null && StringUtils.isBlank(a_user.getPrenom())) {
			l_ret = getXMLErrSaisie("prenom");
		}
		
		// CODE POSTAL
		if (l_ret == null && StringUtils.isBlank(a_user.getCodePostal())) {
			l_ret = getXMLErrSaisie("code postal");
		}

		// EMAIL
		if (l_ret == null && StringUtils.isBlank(a_user.getEmail())) {
			l_ret = getXMLErrSaisie("adresse email");
		}
		
		// PASSWORD
		if (l_ret == null && StringUtils.isBlank(a_user.getPassword())) {
			l_ret = getXMLErrSaisie("mot de passe");
		}

		return l_ret;
	}

	private String checkValues(UpdatedUser a_user, TypeAccesType l_typeAcces, RepositoryItem a_profile) 
		throws RepositoryException, ServletException
	{
		String l_ret = null;
		
		if (l_ret == null)
			l_ret = checkValueCivilite(a_user.getCivilite());
		if (l_ret == null)
			l_ret = checkValueCodePostal(a_user.getCodePostal());		
		if (l_ret == null && !StringUtils.isBlank(a_user.getEmail()))
			l_ret = checkValueEmail(a_user.getEmail());
		
		// on r�cup�re le mail du profile au cas o� l'utilisateur a modifi� son email (a_updatedUser.getEmail() != profile.getPropertyValue("email")
		String l_oldEmail = (String) a_profile.getPropertyValue("email");
		String l_newEmail = a_user.getEmail();
		boolean l_emailChanged = isDifferent(l_oldEmail, l_newEmail);
		
		if (l_ret == null && l_emailChanged)
		{
			// il faut conserver un email si on en avait un 
			if (!StringUtils.isBlank(l_oldEmail) && StringUtils.isBlank(l_newEmail))
			{
				l_ret = getXML(CODE_RETOUR_ERR_CONSERVATION_MAIL);
			}
			
			// le nouveau email ne doit pas deja exister
			if (l_ret == null && getWebServicesTools().findByLoginIgnoreCase(l_newEmail) != null)
			{
				l_ret = getXML(CODE_RETOUR_ERR_EMAIL_DEJA_EXISTANT);
				
			}
		}
		
		if (l_ret == null && StringUtils.isBlank(l_newEmail) && l_typeAcces == TypeAccesType.MAGASIN) {
			String civilite = translateCiviliteFromIntegerStringToString(a_user.getCivilite());
			if (isLoggingDebug()) {
				logDebug("civilite: " + civilite);
				logDebug("lastName: " + a_user.getNom());
				logDebug("firstName: " + a_user.getPrenom());
			}
			RepositoryItem[] userItems = getWebServicesTools().findByParamsIgnoreCase(civilite,
					a_user.getNom(), a_user.getPrenom());
			
			if (userItems != null) {
				for (RepositoryItem item : userItems) {
					if (isLoggingDebug()) {
						logDebug("user: " + item);
					}
					
					if (item.getRepositoryId().equals(a_user.getId())) {
						continue;
					}

					if (l_ret != null) {
						break;
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
					
					if (l_billingAddress == null && a_user.getAdresse() == null 
							&& a_user.getCodePostal() == null) {
						l_ret = getXML(CODE_RETOUR_ERR_CUSTOMER_EXISTS);
					}
						
					if (l_ret == null && l_billingAddress != null) {								
						
						// le code postal correspond? (vide tous les deux ou rempli а la mкme chose tous les deux
						if ((l_postalCode == null && a_user.getCodePostal() == null) ||
							(l_postalCode != null && a_user.getCodePostal() != null 
									&& l_postalCode.equalsIgnoreCase(a_user.getCodePostal())))
						{
							if (isLoggingDebug()) {
								logDebug("code postal identique ...");
							}
							
							// l'adresse correspond? (vide toutes les deux ou rempli а la mкme chose toutes les deux
							if ((l_address1 == null && a_user.getAdresse() == null) ||
								(l_address1 != null && a_user.getAdresse() != null 
										&& l_address1.trim().equalsIgnoreCase(a_user.getAdresse().trim())))
							{
								if (isLoggingDebug()) {
									logDebug("adresse identique ...");
								}
								l_ret = getXML(CODE_RETOUR_ERR_CUSTOMER_EXISTS);
							}
						}
					}
				}
			}
		}
		
		if (l_ret == null)
			l_ret = checkValuePassword(a_user.getPassword());
		
		// DATE DE NAISSANCE
		if (l_ret == null && !StringUtils.isBlank(a_user.getDateNaissance()))
			l_ret = checkValueDateNaissance(a_user.getDateNaissance());

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
		else
			profileAsXML.append("<user:user." + champs + "></user:user." + champs + ">");
	}
	
	private boolean isDifferent(String a_old, String a_new)
	{
		if (StringUtils.isBlank(a_old) && StringUtils.isBlank(a_new))
			return false;
		if (!StringUtils.isBlank(a_old) && !StringUtils.isBlank(a_new))
			return !a_old.equalsIgnoreCase(a_new);
		return true;
	}
	
	/**
	 * Renvoie une requ�te XML de cr�ation d'utilisateur pour utiliser la m�thode interne ATG createUser(ProfileAsXML).
	 * champs g�r� : login, email, password, reminderPassword, reponseReminder, civilite, firstName, lastName, typeMAJProfil, dateMAJProfil, dateOfBirth, id_magasin_ref
	 */
	private String getATGXMLFromRequest(UpdatedUser a_updatedUser, RepositoryItem a_profile)
	{
		StringBuffer profileAsXML = new StringBuffer("<user:user xmlns:user=\"http://www.atg.com/ns/userMapping/UserProfiles/user\" " +
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
			"xsi:schemaLocation=\"http://www.atg.com/ns/userMapping/UserProfiles/user userMapping+UserProfiles+user.xsd\" " +
			">");

		// id de l'utilisateur � modifier
		ajoutChamps(profileAsXML, "id", a_updatedUser.getId());
		
		// on met dans le XML uniquement les champs qui ont �t� modifi�
		if (!StringUtils.isBlank(a_updatedUser.getEmail()) && isDifferent((String) a_profile.getPropertyValue("login"), a_updatedUser.getEmail()))
		{
			ajoutChamps(profileAsXML, "login", a_updatedUser.getEmail());
			ajoutChamps(profileAsXML, "email", a_updatedUser.getEmail());
		}
		
		if (getWebServicesTools().isDifferntPassword(a_updatedUser.getEmail(), a_updatedUser.getPassword(), a_profile)) {
			String nPassword = getWebServicesTools().generatePassword(a_updatedUser.getEmail(), a_updatedUser.getPassword());
			ajoutChamps(profileAsXML, "password", nPassword);
		}
		
		if (isDifferent((String) a_profile.getPropertyValue("civilite"), translateCiviliteFromIntegerStringToString(a_updatedUser.getCivilite())))
			ajoutChamps(profileAsXML, "civilite", translateCiviliteFromIntegerStringToString(a_updatedUser.getCivilite()));
		
		if (isDifferent((String) a_profile.getPropertyValue("firstName"), a_updatedUser.getPrenom()))
			ajoutChamps(profileAsXML, "firstName", a_updatedUser.getPrenom());
		
		if (isDifferent((String) a_profile.getPropertyValue("lastName"), a_updatedUser.getNom()))
			ajoutChamps(profileAsXML, "lastName", a_updatedUser.getNom());

		// s'il n'y avait pas de date de naissance avant ou qu'elle a chang�
		try 
		{
			if (!StringUtils.isBlank(a_updatedUser.getDateNaissance()))
			{
				SimpleDateFormat l_sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date l_dn = l_sdf.parse(a_updatedUser.getDateNaissance());
				ajoutChamps(profileAsXML, "dateOfBirth", new Timestamp(l_dn.getTime()).toString());
			}
			else
			{
				ajoutChamps(profileAsXML, "dateOfBirth", "");
			}
		}
		catch (ParseException l_e)
		{
			if (isLoggingError()) {
				logError(l_e);
			}
		}			
		
		if (isDifferent((String) a_profile.getPropertyValue("id_magasin_ref"), a_updatedUser.getMagasinReference()))
			ajoutChamps(profileAsXML, "id__magasin__ref", a_updatedUser.getMagasinReference());

		// on modifie un champs qu'on utilise pas pour qu'ATG lance l'�v�nement updateProfile m�me si aucune modification
		// n'a �t� apport� sur le "user" (genre on modifie uniquement la billingAddress)
		if (a_profile.getPropertyValue("middleName") == null)
		{
			ajoutChamps(profileAsXML, "middleName", " ");
		}
		else
		{
			ajoutChamps(profileAsXML, "middleName", null);
		}
		
		// type et date de mise � jour du profil
		ajoutChamps(profileAsXML, "typeMAJprofil", "" + TYPE_MAJ_PROFIL_MODIFICATION);		
		Date l_today = new java.util.Date();
        SimpleDateFormat l_sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date l_d = java.sql.Date.valueOf(l_sdf.format(l_today));                	
		ajoutChamps(profileAsXML, "dateMAJprofil", "" + l_d);
		

		profileAsXML.append("</user:user>");
		
		if (isLoggingDebug()) {
			logDebug(profileAsXML.toString());
		}
		
		return profileAsXML.toString();
	}
	
	private String updateUser(UpdatedUser a_updatedUser, MutableRepositoryItem a_profile, TypeAccesType a_typeAccess) throws RepositoryException, ServletException {
		
		String l_newEmail = a_updatedUser.getEmail();
		
		// --------------------------------------------------------------------
		// REG_CC_6_10 + REG_CC_6_11 (ProfileServices.updateUser launch event updateUser)
		// --------------------------------------------------------------------
		String profileAsXML = getATGXMLFromRequest(a_updatedUser, a_profile);
		getProfileServices().updateUser(profileAsXML);
		
		// --------------------------------------------------------------------
		// NEWSLETTER
		// --------------------------------------------------------------------
		
		// If there is an email (if there was one before, there is necessarily an even REG_CC_7_7)
		if (!StringUtils.isBlank(l_newEmail)) {
	        int flagNewsletter = a_updatedUser.isInscriptionNewsletter() ? CastNewsletterManager.NEWSLETTER_1 : CastNewsletterManager.NEWSLETTER_2;
	        int flagPartenaires = a_updatedUser.isInscriptionNewsletterPartenaires() ? CastNewsletterManager.NEWSLETTER_1 : CastNewsletterManager.NEWSLETTER_2;
	        int flagRecontact = a_updatedUser.getAcceptRecontact();
	        if (isLoggingDebug()) {
	            logDebug("Opt-in is " + a_updatedUser.isInscriptionNewsletter() + " for newEmail=" + l_newEmail);
	        }
	        getNewsletterManager().saveNewsletter(l_newEmail, CANAL_NL, a_profile, flagNewsletter, flagPartenaires, flagRecontact);
		}
		else
		{
			if (isLoggingDebug()) {
				logDebug("No newEmail set for user=" + a_profile);
			}
		}
		
		// --------------------------------------------------------------------
		// BILLING ADDRESS
		// --------------------------------------------------------------------
		
		MutableRepositoryItem l_billingAddress = (MutableRepositoryItem) a_profile.getPropertyValue("billingAddress");
		if (l_billingAddress != null)
		{
			if (isLoggingDebug()) {
				logDebug("billingAddress found for " + a_profile + " (" + a_profile.getPropertyValue("login") + "), updating ...");
			}
			boolean l_changes = false;
			String newCivilite = translateCiviliteFromIntegerStringToString(a_updatedUser.getCivilite());
			if (isDifferent(newCivilite, (String) l_billingAddress.getPropertyValue("civilite")))
			{
				if (isLoggingDebug()) {
					logDebug("updating civilite ... (from " + (String) l_billingAddress.getPropertyValue("civilite") + " to " + newCivilite + ")");
				}
				l_billingAddress.setPropertyValue("civilite", newCivilite);
				l_billingAddress.setPropertyValue("prefix", newCivilite);
				l_changes = true;
			}				
			if (isDifferent(a_updatedUser.getEmail(), (String) l_billingAddress.getPropertyValue("email")))
			{
				if (isLoggingDebug()) {
					logDebug("updating email ... (from " + (String) l_billingAddress.getPropertyValue("email") + " to " + a_updatedUser.getEmail() + ")");
				}
				l_billingAddress.setPropertyValue("email", a_updatedUser.getEmail());
				l_changes = true;				
			}				
			if (isDifferent(a_updatedUser.getPrenom(), (String) l_billingAddress.getPropertyValue("firstName")))
			{
				if (isLoggingDebug()) {
					logDebug("updating firstName ... (from " + (String) l_billingAddress.getPropertyValue("firstName") + " to " + a_updatedUser.getPrenom() + ")");
				}
				l_billingAddress.setPropertyValue("firstName", a_updatedUser.getPrenom());
				l_changes = true;				
			}
			if (isDifferent(a_updatedUser.getNom(), (String) l_billingAddress.getPropertyValue("lastName")))
			{
				if (isLoggingDebug()) {
					logDebug("updating lastName ... (from " + (String) l_billingAddress.getPropertyValue("lastName") + " to " + a_updatedUser.getNom() + ")");
				}
				l_billingAddress.setPropertyValue("lastName", a_updatedUser.getNom());
				l_changes = true;
			}
			if (a_updatedUser.getSociete() != null && isDifferent(a_updatedUser.getSociete(), (String) l_billingAddress.getPropertyValue("societe")))
			{
				if (isLoggingDebug()) {
					logDebug("updating societe ... (from " + (String) l_billingAddress.getPropertyValue("societe") + " to " + a_updatedUser.getSociete() + ")");
				}
				l_billingAddress.setPropertyValue("societe", a_updatedUser.getSociete());
				l_changes = true;
			}
			if (isDifferent(a_updatedUser.getCodePostal(), (String) l_billingAddress.getPropertyValue("postalCode")))
			{
				if (isLoggingDebug()) {
					logDebug("updating postalCode ... (from " + (String) l_billingAddress.getPropertyValue("postalCode") + " to " + a_updatedUser.getCodePostal() + ")");
				}
				l_billingAddress.setPropertyValue("postalCode", a_updatedUser.getCodePostal());
				l_changes = true;
			}
			if (isDifferent(((a_updatedUser.getAdresse() != null ? a_updatedUser.getAdresse() : "")).trim(), (String) l_billingAddress.getPropertyValue("address1")))
			{
				if (isLoggingDebug()) {
					logDebug("updating address1 ... (from " + (String) l_billingAddress.getPropertyValue("address1") + " to " + ((a_updatedUser.getAdresse() != null ? a_updatedUser.getAdresse() : "")).trim());
				}
				l_billingAddress.setPropertyValue("address1", ((a_updatedUser.getAdresse() != null ? a_updatedUser.getAdresse() : "")).trim());
				l_changes = true;
			}
			if (isDifferent(((a_updatedUser.getComplement() != null ? a_updatedUser.getComplement() : "")).trim(), (String) l_billingAddress.getPropertyValue("address2")))
			{
				if (isLoggingDebug()) {
					logDebug("updating address2 ... (from " + (String) l_billingAddress.getPropertyValue("address2") + " to " + ((a_updatedUser.getComplement() != null ? a_updatedUser.getComplement() : "")).trim());
				}
				l_billingAddress.setPropertyValue("address2", ((a_updatedUser.getComplement() != null ? a_updatedUser.getComplement() : "")).trim());
				l_changes = true;
			}
			if (isDifferent(a_updatedUser.getVille(), (String) l_billingAddress.getPropertyValue("city")))
			{
				if (isLoggingDebug()) {
					logDebug("updating city ... (from " + (String) l_billingAddress.getPropertyValue("city") + " to " + a_updatedUser.getVille() + ")");
				}
				l_billingAddress.setPropertyValue("city", a_updatedUser.getVille());
				l_changes = true;
			}
			if (isDifferent(a_updatedUser.getPays(), (String) l_billingAddress.getPropertyValue("country")))
			{
				if (isLoggingDebug()) {
					logDebug("updating country ... (from " + (String) l_billingAddress.getPropertyValue("country") + " to " + a_updatedUser.getPays() + ")");
				}
				Place place = getWebServicesTools().getCountry(a_updatedUser.getPays());
				l_billingAddress.setPropertyValue(Constants.COUNTRY_ADDRESS_PROP, place.getDisplayName().trim());
				l_billingAddress.setPropertyValue(Constants.STATE_ADDRESS_PROP, place.getCode().trim());
				//AP l_billingAddress.setPropertyValue("country", a_updatedUser.getPays());
				l_changes = true;
			}
			
			if (!l_changes)
			{
				if (isLoggingDebug()) {
					logDebug("no changes to billingAddress");
				}
			}
		}
		else
		{
			if (isLoggingDebug()) {
				logDebug("No billingAddress found for " + a_profile + " (" + a_profile.getPropertyValue("login") + "), creating ...");
			}
			l_billingAddress = ((MutableRepository) getProfileTools().getProfileRepository())
				.createItem("contactInfo");
			
			l_billingAddress.setPropertyValue("adresseActive", new Integer("1"));
			l_billingAddress.setPropertyValue("typeAdresse", new Integer("2"));
			
			if (!StringUtils.isBlank(a_updatedUser.getCivilite())){
				String newCivilite = translateCiviliteFromIntegerStringToString(a_updatedUser.getCivilite());
				l_billingAddress.setPropertyValue("civilite", newCivilite);
			    l_billingAddress.setPropertyValue("prefix", newCivilite);
			}
			if (!StringUtils.isBlank(a_updatedUser.getEmail()))
				l_billingAddress.setPropertyValue("email", a_updatedUser.getEmail());
			if (!StringUtils.isBlank(a_updatedUser.getPrenom()))
				l_billingAddress.setPropertyValue("firstName", a_updatedUser.getPrenom());
			if (!StringUtils.isBlank(a_updatedUser.getNom()))
				l_billingAddress.setPropertyValue("lastName", a_updatedUser.getNom());
			if (!StringUtils.isBlank(a_updatedUser.getSociete()))
				l_billingAddress.setPropertyValue("societe", a_updatedUser.getSociete());
			if (!StringUtils.isBlank(a_updatedUser.getCodePostal()))
				l_billingAddress.setPropertyValue("postalCode", a_updatedUser.getCodePostal());
			if (!StringUtils.isBlank(a_updatedUser.getAdresse()))
				l_billingAddress.setPropertyValue("address1", a_updatedUser.getAdresse());
			if (!StringUtils.isBlank(a_updatedUser.getComplement()))
				l_billingAddress.setPropertyValue("address2", a_updatedUser.getComplement());
			if (!StringUtils.isBlank(a_updatedUser.getVille()))
				l_billingAddress.setPropertyValue("city", a_updatedUser.getVille());

			Place place = getWebServicesTools().getCountry(a_updatedUser.getPays());
			l_billingAddress.setPropertyValue(Constants.COUNTRY_ADDRESS_PROP, place.getDisplayName().trim());
			l_billingAddress.setPropertyValue(Constants.STATE_ADDRESS_PROP, place.getCode().trim());
			/*
			if (!StringUtils.isBlank(a_updatedUser.getPays()))
				l_billingAddress.setPropertyValue("country", a_updatedUser.getPays());
				*/
			((MutableRepository) getProfileTools().getProfileRepository()).addItem(l_billingAddress);
			
			if (isLoggingDebug()) {
				logDebug("adding new billingAddress to secondaryBillingShippingAddresses as '"  + "Adresse de facturation"  + "' ...");
			}
			Map l_secondaryBillingShippingAddresses = (Map) a_profile.getPropertyValue("secondaryBillingShippingAddresses");
			l_secondaryBillingShippingAddresses.put("Adresse de facturation", l_billingAddress);
			((MutableRepository) getProfileTools().getProfileRepository()).updateItem(a_profile);
		}
		
		// --------------------------------------------------------------------
		// REG_CC_6_14
		// --------------------------------------------------------------------		
		getWebServicesTools().setUtilisateurConceptionCuisine(a_profile);
		
		try {
			sendEMail(a_updatedUser);			
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}

		
		return getXML(CODE_RETOUR_OK);
	}

	private String getXML(String a_codeRetour) {
		return getXMLManager().getUpdateUserOutput(a_codeRetour,
				(String) getMessages().get(a_codeRetour));
	}

	private String getXMLErrSaisie(String a_champs) {
		String msg = (String) getMessages().get(CODE_RETOUR_ERR_SAISIE);
		msg = msg.replaceAll("CHAMPS", a_champs);
		return getXMLManager().getUpdateUserOutput(CODE_RETOUR_ERR_SAISIE, msg);
	}
	
		// --------------------------------------------------------------------
	// REG_CC_6_4
	// --------------------------------------------------------------------
	private String checkValueCivilite(String a_civilite)
	{
		if (!a_civilite.equals("1") && !a_civilite.equals("2") && !a_civilite.equals("3")) {
			return getXML(CODE_RETOUR_ERR_CIVILITE_INCORRECTE);
		}
		
		return null;
	}
	
	
	// --------------------------------------------------------------------
	// REG_CC_6_3
	// --------------------------------------------------------------------
	private String checkValueEmail(String a_email)
	{
		if (!StringUtils.isBlank(a_email) && !getWebServicesTools().isEmail(a_email)) {
			return getXML(CODE_RETOUR_ERR_EMAIL_INCORRECT);
		}
		return null;
	}
	
	// --------------------------------------------------------------------
	// REG_CC_6_5
	// --------------------------------------------------------------------
	private String checkValuePassword(String a_password)
	{
		if (a_password.length() < 4) {
			return getXML(CODE_RETOUR_ERR_PASSWORD_TROP_COURT);
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
			return getXML(CODE_RETOUR_ERR_DDN_INCORRECTE);
		}
		return null;
	}
	
	private String checkValueCodePostal(String a_cp) throws RepositoryException, ServletException
	{
		List villes = getAddressHelper().getCitiesViaCodePostal(a_cp);
		
		if (villes == null || villes.isEmpty()) {
			return getXML(CODE_RETOUR_ERR_CODE_POSTAL_INEXISTANT);
		}

		return null;
	}
	
	private void sendEMail(UpdatedUser updatedUser) throws Exception
	{
		if (StringUtils.isBlank(updatedUser.getEmail())) {
			return;
		}
		
		String l_sujet = ResourceUtils
				.getUserMsgResource(
						SUBJECT_KEY, BUNDLE_TEMPLATE_MAIL, null);

		String l_message;
        String l_civilite = (String) updatedUser.getCivilite();
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

		String firstName = updatedUser.getPrenom();
		String lastName = updatedUser.getNom();

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
						getServerSetting().getHost()
				}
		);

		getWebServicesTools().sendMailHTML(getMessageFrom(),
				l_sujet, updatedUser.getEmail(), l_message);
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
	
	public ProfileTools getProfileTools() {
		return mProfileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		mProfileTools = profileTools;
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
