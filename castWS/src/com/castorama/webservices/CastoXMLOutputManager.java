package com.castorama.webservices;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.castorama.webservices.xsd.Client;
import com.castorama.webservices.xsd.Clients;
import com.castorama.webservices.xsd.CreateUserResponse;
import com.castorama.webservices.xsd.LoggedUser;
import com.castorama.webservices.xsd.LoginUserResponse;
import com.castorama.webservices.xsd.MotDePassePerduResponse;
import com.castorama.webservices.xsd.RechercheClientsResponse;
import com.castorama.webservices.xsd.UpdateUserResponse;
import com.castorama.webservices.xsd.UserType;

/**
 * Regroupe les m�thodes utilis�s pour g�n�rer les XMLs de sortie des
 * webservices.
 * 
 * @author derosiauxs
 * 
 */
public class CastoXMLOutputManager extends ApplicationLoggingImpl {

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------
	
	private Repository m_profileRepository;

	private String m_emailRegex;
	
	private CastNewsletterManager m_newsletterManager;
	
	// ------------------------------------------------------------------------
	// METHODES
	// ------------------------------------------------------------------------

	/**
	 * G�re la sortie XML du webservice motDePassePerdu.
	 * 
	 * @param code
	 * @param description
	 * @return XML
	 */
	public String getMotDePassePerduOutput(String code, String description) {
		MotDePassePerduResponse r = new MotDePassePerduResponse();
		r.setCode(code);
		r.setDescription(description);
		StringWriter out = new StringWriter();

		try {
			r.marshal(out);
			out.close();
		} catch (ValidationException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (IOException l_ioe) {
			if (isLoggingError()) {
				logError(l_ioe);
			}
		}

		return out.toString();
	}

	
	/**
	 * G�re la sortie XML du webservice rechercheClients.
	 * 
	 * @param code
	 * @param description
	 * @param usersXML les ids des utilisateurs correspondant � la recherche
	 * @return XML
	 */
	public String getRechercheClientsOutput(String code, String description,
			List<String> usersXML) {
		RechercheClientsResponse r = new RechercheClientsResponse();
		r.setCode(code);
		r.setDescription(description);
		Clients clients = new Clients();

		if (usersXML != null)
		{
			for (String userId : usersXML) {
				Client c = new Client();				
				setUserType(c, userId);
				clients.addClient(c);
			}
		}
		r.setClients(clients);

		StringWriter out = new StringWriter();

		try {
			r.marshal(out);
			out.close();
		} catch (ValidationException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (IOException l_ioe) {
			if (isLoggingError()) {
				logError(l_ioe);
			}
		}

		return out.toString();
	}
	
	/**
	 * G�re la sortie XML du webservice loginUser.
	 * 
	 * @param code
	 * @param description
	 * @param userId l'id de l'utilisateur identifi�
	 * @return XML
	 */
	public String getLoginUserOutput(String code, String description, String userId)
	{
		LoginUserResponse r = new LoginUserResponse();
		r.setCode(code);
		r.setDescription(description);
		
		if (userId != null)
		{
			LoggedUser user = new LoggedUser();
			setUserType(user, userId);
			r.setLoggedUser(user);
		}		
		StringWriter out = new StringWriter();

		try {
			r.marshal(out);
			out.close();
		} catch (ValidationException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (IOException l_ioe) {
			if (isLoggingError()) {
				logError(l_ioe);
			}
		}

		return out.toString();
	}

	/**
	 * G�re la sortie XML du webservice updateUser.
	 * 
	 * @param code
	 * @param description
	 * @return XML
	 */
	public String getUpdateUserOutput(String code, String description) {
		UpdateUserResponse r = new UpdateUserResponse();
		r.setCode(code);
		r.setDescription(description);
		StringWriter out = new StringWriter();

		try {
			r.marshal(out);
			out.close();
		} catch (ValidationException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (IOException l_ioe) {
			if (isLoggingError()) {
				logError(l_ioe);
			}
		}

		return out.toString();
	}
	
	/**
	 * G�re la sortie XML du webservice createUser.
	 * 
	 * @param code
	 * @param description
	 * @param id l'id de l'utilisateur cr��
	 * @return XML
	 */
	public String getCreateUserOutput(String code, String description, String id) {
		CreateUserResponse r = new CreateUserResponse();
		r.setCode(code);
		r.setDescription(description);
		r.setId(id);
		StringWriter out = new StringWriter();

		try {
			r.marshal(out);
			out.close();
		} catch (ValidationException e) {
			if (isLoggingError()) {
				logError(e);
			}
		} catch (MarshalException l_me) {
			if (isLoggingError()) {
				logError(l_me);
			}
		} catch (IOException l_ioe) {
			if (isLoggingError()) {
				logError(l_ioe);
			}
		}

		return out.toString();
	}
	
	/**
	 * Renseigne les champs de userType avec les informations du profil d'id a_id.
	 * 
	 * @param userType un userType � remplir
	 * @param a_id un id de profil ATG
	 */
	private void setUserType(UserType userType, String a_id) {
		try {
			RepositoryItem user = getProfileRepository().getItem(a_id, "user");
			if (user != null)
			{
				userType.setId(user.getRepositoryId());
				String login = (String) user.getPropertyValue("login");
				if (login.matches(getEmailRegex()))
				{
					userType.setEmail((String) user.getPropertyValue("login"));
				}
				
				// -------------------------------------------------
				// Informations non modifiables sur le site lui-m�me
				
				userType.setCivilite((String) user.getPropertyValue("civilite"));
				userType.setNom((String) user.getPropertyValue("lastName"));
				userType.setPrenom((String) user.getPropertyValue("firstName"));				
				userType.setDateNaissance(getDateAsString((Date) user.getPropertyValue("dateOfBirth")));
				
				userType.setDateMAJProfil(getDateAsString((Date) user.getPropertyValue("dateMAJProfil")));
				
				// Informations non modifiables sur le site lui-m�me				
				// -------------------------------------------------
				
				userType.setMagasinReference((String) user.getPropertyValue("id_magasin_ref"));
				
				RepositoryItem newsletter = getNewsletterManager().getNewsletterItem(login);
				if (newsletter != null) {
					String rEmail = (String) newsletter.getPropertyValue("receiveEmail");
					userType.setInscriptionNewsletter("true".equals(rEmail));
					userType.setDateInscription(
							getDateAsString((Date) newsletter.getPropertyValue("dateInscription")));
                    rEmail = (String) newsletter.getPropertyValue("reseiveOffers");
                    userType.setInscriptionNewsletterPartenaires("true".equals(rEmail));
                    Integer acceptRecontact = (Integer) newsletter.getPropertyValue("acceptRecontact");
                    userType.setAcceptRecontact(acceptRecontact != null ? acceptRecontact : 0);
				} else {
					userType.setInscriptionNewsletter(false);
					userType.setDateInscription(null);
                    userType.setInscriptionNewsletterPartenaires(false);
                    userType.setAcceptRecontact(0);
				}
				
				
				RepositoryItem billingAddress = (RepositoryItem) user.getPropertyValue("billingAddress");
				if (billingAddress != null)
				{
					userType.setPays((String) billingAddress.getPropertyValue("country"));
					userType.setCodePostal((String) billingAddress.getPropertyValue("postalCode"));
					userType.setAdresse((String) billingAddress.getPropertyValue("address1"));
					userType.setComplement((String) billingAddress.getPropertyValue("address2"));
					userType.setSociete((String) billingAddress.getPropertyValue("societe"));
					userType.setVille((String) billingAddress.getPropertyValue("city"));
				}
			}
			
		} catch (RepositoryException l_re) {
			if (isLoggingError()) {
				logError(l_re);
			}
		}
	}
	
	/**
	 * Retourne true si l'utilisateur est abonn� � la newsletter, false sinon.
	 * 
	 * @param a_user l'utilisateur user ATG
	 * @return true | false
	 */
	public boolean getInscriptionNewsletter(RepositoryItem a_user)
	{
		boolean l_isValid = false;
		
		RepositoryItem l_an = (RepositoryItem) a_user.getPropertyValue("abonnementNewsletter");
		if (l_an != null)
		{
			Boolean l_isValidUser = (Boolean) l_an.getPropertyValue("isValid");
			if (l_isValidUser != null && l_isValidUser == Boolean.TRUE)
			{
				l_isValid = true;
			}
		}

		return l_isValid;
	}
	
	/**
	 * Retourne une date sous forme d'une cha�ne.
	 * 
	 * @param a_date
	 * @return
	 */
	private String getDateAsString(Date a_date)
	{
		if (a_date == null)
			return null;
		return a_date.toString();
	}
	
	// ------------------------------------------------------------------------
	// GETTERS/SETTERS
	// ------------------------------------------------------------------------
	
	public Repository getProfileRepository() {
		return m_profileRepository;
	}

	public void setProfileRepository(Repository repository) {
		m_profileRepository = repository;
	}
	
	public String getEmailRegex() {
		return m_emailRegex;
	}

	public void setEmailRegex(String regex) {
		m_emailRegex = regex;
	}

	public CastNewsletterManager getNewsletterManager() {
		return m_newsletterManager;
	}

	public void setNewsletterManager(CastNewsletterManager a_newsletterManager) {
		m_newsletterManager = a_newsletterManager;
	}

}
