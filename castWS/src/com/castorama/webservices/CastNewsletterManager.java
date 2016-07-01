package com.castorama.webservices;

import java.util.Date;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileTools;

import com.castorama.commerce.clientspace.CastAdminNewsletterFormHandler;
import com.castorama.commerce.profile.Constants;
import com.castorama.integration.experian.ExperianRequestHelper;

public class CastNewsletterManager extends ApplicationLoggingImpl {
	
    public static final int NEWSLETTER_0 = 0;
	public static final int NEWSLETTER_1 = 1;
	public static final int NEWSLETTER_2 = 2;
	public static final String SOURCE_WEB_SERVICE = "web_serv";
	
	private Repository newsletterRepository;
	private ProfileTools profileTools;
	private ExperianRequestHelper experianRequestHelper;

	/**
	 * Save newsletter
	 * @param email
	 * @param canalInscription
	 * @param user
	 * @param flagNewsletter - 1 - subscribe; 2 - unsubscribe
     * @param flagPartenaires - 1 - subscribe; 2 - unsubscribe
     * @param flagRecontact - 1 or 2 - yes; 0 - no
	 * @throws RepositoryException
	 */
	public void saveNewsletter(String email, String canalInscription, RepositoryItem user, int flagNewsletter, int flagPartenaires, int flagRecontact) throws RepositoryException {
		if (StringUtils.isBlank(email)) {
			if (isLoggingDebug()) {
				logDebug("Email is blank.");
			}
			return;
		}
		MutableRepository mutableRopo = (MutableRepository) getNewsletterRepository();
		MutableRepositoryItem newsletter = getNewsletter(email);

		Date currentDate = new Date();
		String subscrube = null;
		boolean sendEditas = false;
		Date dateOfBirth = null;
		if (user.getPropertyValue(Constants.DATE_OF_BIRTH_NEWSLETTER_PROP) != null) {
			dateOfBirth = (Date) user.getPropertyValue(Constants.DATE_OF_BIRTH_NEWSLETTER_PROP);
		}

		if (newsletter != null) {
			switch (flagNewsletter) {
			case NEWSLETTER_1: //subscribe
				subscrube = "true";
				newsletter.setPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP, subscrube);
				newsletter.setPropertyValue(Constants.DATE_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
				break;
			case NEWSLETTER_2: 
				subscrube = "false";
				newsletter.setPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP, subscrube);
				newsletter.setPropertyValue(Constants.DATE_UNSUBSCRIBE_NEWSLETTER_PROP, currentDate);
				break;
			} 
            switch (flagPartenaires) {
            case NEWSLETTER_1: //subscribe
                subscrube = "true";
                newsletter.setPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP, subscrube);
                newsletter.setPropertyValue(Constants.DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
                break;
            case NEWSLETTER_2: 
                subscrube = "false";
                newsletter.setPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP, subscrube);
                newsletter.setPropertyValue(Constants.DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP, currentDate);
                break;
            } 
            newsletter.setPropertyValue(Constants.ACCEPTRECONTACT_NEWSLETTER_PROP, flagRecontact);
			newsletter.setPropertyValue(Constants.DATE_OF_BIRTH_NEWSLETTER_PROP, dateOfBirth);
			newsletter.setPropertyValue(Constants.DATE_DERNIERE_MODIF_NEWSLETTER_PROP, currentDate);
			mutableRopo.updateItem(newsletter);
			sendEditas = true;
		} else {
			newsletter = mutableRopo.createItem(email, CastAdminNewsletterFormHandler.ABONNEMENT_NEWSLETTER);

			newsletter.setPropertyValue("profile", user.getRepositoryId());
			newsletter.setPropertyValue("isValid", Boolean.TRUE);
			newsletter.setPropertyValue("origine", canalInscription);
			newsletter.setPropertyValue("format", "html");
			//newsletter.setPropertyValue("nom", user.getPropertyValue("lastName"));
			//newsletter.setPropertyValue("prenom", user.getPropertyValue("firstName"));
			newsletter.setPropertyValue("isTest", Boolean.FALSE);
			newsletter.setPropertyValue("accesPartenairesCasto", Boolean.FALSE);
			newsletter.setPropertyValue("emailStatus", "valid");
			switch (flagNewsletter) {
			case NEWSLETTER_1: //subscribe
				sendEditas = true;
				subscrube = "true";
				newsletter.setPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP, subscrube);
				newsletter.setPropertyValue(Constants.DATE_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
				break;
			}
            switch (flagPartenaires) {
            case NEWSLETTER_1: //subscribe
                sendEditas = true;
                subscrube = "true";
                newsletter.setPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP, subscrube);
                newsletter.setPropertyValue(Constants.DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP, currentDate);
                break;
            }
            newsletter.setPropertyValue(Constants.ACCEPTRECONTACT_NEWSLETTER_PROP, flagRecontact);
			newsletter.setPropertyValue(Constants.DATE_OF_BIRTH_NEWSLETTER_PROP, dateOfBirth);
			newsletter.setPropertyValue(Constants.DATE_DERNIERE_MODIF_NEWSLETTER_PROP, currentDate);
			mutableRopo.addItem(newsletter);
		}

		int flag = flagNewsletter != NEWSLETTER_0 ? flagNewsletter : flagPartenaires;
		switch (flag) {
		case NEWSLETTER_1: //subscribe
			getExperianRequestHelper().addCreateSubscriptionRequest(email, SOURCE_WEB_SERVICE);
			break;
		case NEWSLETTER_2: 
			if (sendEditas) {
				getExperianRequestHelper().addRemoveSubscriptionRequest(email, SOURCE_WEB_SERVICE);
			}
			break;
		}
	}

	public MutableRepositoryItem getNewsletter(String a_email) throws RepositoryException {
		return ((MutableRepository) getNewsletterRepository()).getItemForUpdate(a_email,
				CastAdminNewsletterFormHandler.ABONNEMENT_NEWSLETTER);
	}

	public RepositoryItem getNewsletterItem(String a_email) throws RepositoryException {
		return getNewsletterRepository().getItem(a_email,
				CastAdminNewsletterFormHandler.ABONNEMENT_NEWSLETTER);
	}

	/**
	 * The Newsletter repository that holds the information
	 * 
	 * @return the repository that holds the information
	 */
	public Repository getNewsletterRepository() {
		return newsletterRepository;
	}

	/**
	 * @param repository
	 *            set the Newsletter repository that holds the information
	 */
	public void setNewsletterRepository(Repository newsletterRepository) {
		this.newsletterRepository = newsletterRepository;
	}

	/**
	 * Get class ProfileTools
	 * 
	 * @return class ProfileTools
	 */
	public ProfileTools getProfileTools() {
		return profileTools;
	}

	/**
	 * Set class ProfileTools
	 * 
	 * @param profileTools
	 */
	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * Get class ExperianRequestHelper
	 * 
	 * @return class ExperianRequestHelper
	 */
	public ExperianRequestHelper getExperianRequestHelper() {
		return experianRequestHelper;
	}

	/**
	 * Set class ExperianRequestHelper
	 * 
	 * @param experianRequestHelper
	 */
	public void setExperianRequestHelper(ExperianRequestHelper experianRequestHelper) {
		this.experianRequestHelper = experianRequestHelper;
	}
	
}
