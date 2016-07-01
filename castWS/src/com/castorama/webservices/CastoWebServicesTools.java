package com.castorama.webservices;

import java.util.Locale;

import javax.mail.Message;
import javax.mail.MessagingException;

import atg.commerce.util.PlaceList.Place;
import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.EmailListener;
import atg.service.email.MimeMessageUtils;
import atg.userprofiling.PropertyManager;

import com.castorama.utils.CastPlaceList;


public class CastoWebServicesTools extends ApplicationLoggingImpl {
	private static final String COUNTRY_CODE = "F";
	private static final String COUNTRY_NAME = "France";

	// ------------------------------------------------------------------------
	// ATTRIBUTS
	// ------------------------------------------------------------------------

	private MutableRepository m_profileRepository;
	
	private String m_emailRegex;
	
	private String m_numericOnlyRegex;

	private String m_dateRegex;

    private String m_accents;

    private String m_sansAccents;
    
	private PropertyManager propertyManager;
	
    /**
     * Manager to send mail.
     */
    private EmailListener m_listener;
    
    private Repository magasinRepository;
    
    private CastPlaceList countryList;


	
	// ------------------------------------------------------------------------
	// METHODES
	// ------------------------------------------------------------------------

    /**
     * Methode pour l'envoi d'un mail au format HTML.
     * 
     * @param a_from
     *            L'expediteur du mail.
     * @param a_subject
     *            Le sujet du mail.
     * @param a_to
     *            Le destinataire du mail.
     * @param a_message
     *            Le contenu du mail au format HTML.
     * 
     * @throws Exception
     *             Si une erreur survient.
     */
    public void sendMailHTML(String a_from, String a_subject, String a_to, String a_message) throws Exception
    {
        if (isLoggingDebug())
        {
            logDebug(">>> com.castorama.utils.MailUtils.sendMailHTML().");
        }

        EmailEvent l_emailEvent = null;

        try
        {
            // Creation de l'objet mail
            Message l_msg = MimeMessageUtils.createMessage(a_from, a_subject);

            // On fixe le destinataire du mail
            MimeMessageUtils.setRecipient(l_msg, Message.RecipientType.TO, a_to);

            // On fixe le message
            ContentPart[] l_content =
            { new ContentPart(a_message, "text/html"), };

            MimeMessageUtils.setContent(l_msg, l_content);

            // Envoi du mail
            l_emailEvent = new EmailEvent(l_msg);
            l_emailEvent.setCharSet("UTF-8");

            getListener().sendEmailEvent(l_emailEvent);
        }
        catch (MessagingException l_exception)
        {
            logError("Impossible d'envoyer le mail : " + a_subject + ", " + l_exception.toString());

            throw l_exception;
        }
        catch (EmailException l_emailException)
        {
            logError("Impossible d'envoyer le mail : " + a_subject + ", " + l_emailException.toString());

            throw l_emailException;
        }

        if (isLoggingDebug())
        {
            logDebug("<<< com.castorama.utils.MailUtils.sendMailHTML().");
        }
    }
	
	/**
	 * Met le flag UtilisateurConceptionCuisine � true pour le user pass� en
	 * param�tre.
	 * 
	 * @param l_user
	 *            un repository item "user"
	 * @return true si sa propri�t� a bien �t� mise � true ou si elle l'�tait
	 *         d�j�
	 */
	public boolean setUtilisateurConceptionCuisine(MutableRepositoryItem l_user) {
		boolean l_ret = false;

		// on ne modifie rien si c'est d�j� true en base
		if ((Boolean) l_user.getPropertyValue("utilisateurConceptionCuisine") != Boolean.TRUE) {

			try {
				l_user.setPropertyValue("utilisateurConceptionCuisine",
						Boolean.TRUE);
				getProfileRepository().updateItem(l_user);
				l_ret = true;
			} catch (RepositoryException l_re) {
				if (isLoggingError()) {
					logError(l_re);
				}
			}

		} else {

			// on retourne vrai si la valeur �tait d�j� vrai
			l_ret = true;

		}

		return l_ret;
	}

	/**
	 * Met le flag UtilisateurConceptionCuisine � true pour le user dont l'id
	 * est pass� en param�tre.
	 * 
	 * @param l_userId
	 *            un id de repository item "user"
	 * @return true si sa propri�t� a bien �t� mise � true ou si elle l'�tait
	 *         d�j�
	 */
	public boolean setUtilisateurConceptionCuisine(String l_userId) {
		boolean l_ret = false;
		try {
			MutableRepositoryItem l_user = (MutableRepositoryItem) getProfileRepository()
					.getItem(l_userId, "user");
			l_ret = setUtilisateurConceptionCuisine(l_user);
		} catch (RepositoryException l_re) {
			if (isLoggingError()) {
				logError(l_re);
			}
		}

		return l_ret;
	}

	/**
	 * Renvoie une cha�ne sans accents et sans lettres capitales.
	 */
	public String removeAccentsAndCapitals(String value)
	{
		return org.apache.commons.lang.StringUtils.replaceChars(value, getAccents(), getSansAccents()).toLowerCase(Locale.FRANCE);
	}
	
	/**
	 * V�rifie si le param�tre est un email.
	 */
	public boolean isEmail(String l_email)
	{
		if (l_email != null)
		{
			return l_email.matches(getEmailRegex());
		}
		return false;
	}
	
	
	/**
	 * V�rifie si le param�tre est purement num�rique.
	 */
	public boolean isNumericOnly(String l_num)
	{
		if (l_num != null)
		{
			return l_num.matches(getNumericOnlyRegex());
		}
		return false;
	}
	
	public boolean isDifferntPassword(String login, String password, RepositoryItem profile) {
		String passwordHash = getPropertyManager().generatePassword(login, password);
		
		return !profile.getPropertyValue(getPropertyManager().getPasswordPropertyName()).equals(passwordHash);
	}
	
	public String generatePassword(String login, String password) {
		return getPropertyManager().generatePassword(login, password);
	}
	
	public boolean checkMagasinRefId(String magasinId) {
		boolean result = false;
		try {
			return getMagasinRepository().getItem(magasinId, "magasin") != null;
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
		return result;
	}
	
	public RepositoryItem findByLoginIgnoreCase(String email) throws RepositoryException {
		RepositoryItem item = null;
		if (email != null) {
			RepositoryView userView = getProfileRepository().getView("user");
			RqlStatement statment = RqlStatement.parseRqlStatement("login EQUALS IGNORECASE ?0");
			RepositoryItem[] items = statment.executeQuery(userView, new Object[] {email});
			if (items != null && items.length > 0) {
				item = items[0];
			}
		}
		return item;
	}
	
	public RepositoryItem[] findByParamsIgnoreCase(String civility, 
			String nom, String prenom) throws RepositoryException {
		RepositoryView userView = getProfileRepository().getView("user");
		RqlStatement statment = 
			RqlStatement.parseRqlStatement("civilite EQUALS ?0 AND lastName EQUALS IGNORECASE ?1 AND firstName EQUALS IGNORECASE ?2");
		RepositoryItem[] items = statment.executeQuery(userView, new Object[] {civility, nom, prenom});
		return items;
	}

	public Place getCountry(String countryName) {
		if (isLoggingDebug()) {
			logDebug("Country name: " + countryName);
		}
		
		if (!StringUtils.isBlank(countryName)) {
		    Place placeList[] = getCountryList().getPlaces();
		    for(int i=0; i<placeList.length; i++) {
		        Place place = placeList[i];
		        if(countryName.trim().equalsIgnoreCase(place.getDisplayName().trim())) {
			          return place;
		        }
		    }
		}
		return new Place(COUNTRY_CODE, COUNTRY_NAME);
	}
	
	// ------------------------------------------------------------------------
	// GETTERS/SETTERS
	// ------------------------------------------------------------------------

	public MutableRepository getProfileRepository() {
		return m_profileRepository;
	}

	public void setProfileRepository(MutableRepository repository) {
		m_profileRepository = repository;
	}

	public String getEmailRegex() {
		return m_emailRegex;
	}

	public void setEmailRegex(String regex) {
		m_emailRegex = regex;
	}
	
	public String getNumericOnlyRegex() {
		return m_numericOnlyRegex;
	}

	public void setNumericOnlyRegex(String onlyRegex) {
		m_numericOnlyRegex = onlyRegex;
	}

	public String getDateRegex() {
		return m_dateRegex;
	}

	public void setDateRegex(String dateRegex) {
		m_dateRegex = dateRegex;
	}
	
    public String getAccents()
    {
        return m_accents;
    }

    public void setAccents(String a_accents)
    {
        m_accents = a_accents;
    }

    public String getSansAccents()
    {
        return m_sansAccents;
    }

    public void setSansAccents(String a_sansAccents)
    {
        m_sansAccents = a_sansAccents;
    }
    
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	/**
     * Returns EmailListener attached to this component.
     * 
     * @return EmailListener The attached e EmailListener component.
     */
    public EmailListener getListener()
    {
        return m_listener;
    }

    /**
     * Sets EmailListener attached e component.
     * 
     * @param a_listener
     *            the EmailListener attached to this component.
     */
    public void setListener(EmailListener a_listener)
    {
        this.m_listener = a_listener;
    }

	public Repository getMagasinRepository() {
		return magasinRepository;
	}

	public void setMagasinRepository(Repository magasinRepository) {
		this.magasinRepository = magasinRepository;
	}

	public CastPlaceList getCountryList() {
		return countryList;
	}

	public void setCountryList(CastPlaceList countryList) {
		this.countryList = countryList;
	}
	
}
