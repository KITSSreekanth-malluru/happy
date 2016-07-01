package com.castorama.model;

import java.util.Date;

import com.castorama.commerce.profile.Constants;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Abonnement repository item wrapper. Very useful if you don't want to remember
 * each item property name and be happy with type casting.
 *
 * @author Andrew_Logvinov
 */
public class Abonnement extends RepositoryItemWrapper {
    /** DESCRIPTOR_NAME constant. */
    public static final String DESCRIPTOR_NAME = "abonnementNewsletter";

    /**
     * Creates a new Abonnement object.
     *
     * @param repositoryItem parameter
     */
    public Abonnement(RepositoryItem repositoryItem) {
        super(repositoryItem);
    }

    /**
     * Returns instance property.
     *
     * @param  repository parameter to set.
     * @param  email      parameter to set.
     *
     * @return instance property.
     *
     * @throws RepositoryException - exception
     */
    public static Abonnement getInstance(Repository repository, String email) throws RepositoryException {
    	if (null == email) return null;
    	
        RepositoryItem item = repository.getItem(email, DESCRIPTOR_NAME);

        return getInstance(item);
    }

    /**
     * Wrapped instance getter. Returns new object or null if RepositoryItem is
     * null. Does not throws exceptions like Constructor.
     *
     * @param  abonement
     *
     * @return
     */
    public static Abonnement getInstance(RepositoryItem abonement) {
        return (null != abonement) ? new Abonnement(abonement) : null;
    }

    /**
     * Sets the value of the id property.
     *
     * @param  id parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setId(String id) {
        throw new UnsupportedOperationException("updating item is not yet supported");
        //((MutableRepositoryItem) repositoryItem).setPropertyValue("id", id);
    }

    /**
     * Returns email property.
     *
     * @return email property.
     */
    public String getEmail() {
        return getId();
    }

    /**
     * Sets the value of the email property.
     *
     * @param  email parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setEmail(String email) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns subscribled property.
     *
     * @return subscribled property.
     */
    public Boolean getSubscribled() {
        return Boolean.valueOf((String) repositoryItem.getPropertyValue(Constants.RECEIVEEMAIL_NEWSLETTER_PROP));
    }

    /**
     * Sets the value of the subscribled property.
     *
     * @param  option parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setSubscribled(Boolean option) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns subscribledPartners property.
     *
     * @return subscribledPartners property.
     */
    public Boolean getSubscribledPartners() {
        return Boolean.valueOf((String) repositoryItem.getPropertyValue(Constants.RESEIVEOFFERS_NEWSLETTER_PROP));
    }

    /**
     * Sets the value of the subscribledPartners property.
     *
     * @param  optionPartners parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setSubscribledPartners(Boolean optionPartners) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns dateSubscription property.
     *
     * @return dateSubscription property.
     */
    public Date getDateSubscription() {
        return (Date) repositoryItem.getPropertyValue(Constants.DATE_SUBSCRIBE_NEWSLETTER_PROP);
    }

    /**
     * Sets the value of the dateSubscription property.
     *
     * @param  dateSubscription parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setDateSubscription(Date dateSubscription) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns dateSubscriptionPartners property.
     *
     * @return dateSubscriptionPartners property.
     */
    public Date getDateSubscriptionPartners() {
        return (Date) repositoryItem.getPropertyValue(Constants.DATE_OFFERS_SUBSCRIBE_NEWSLETTER_PROP);
    }

    /**
     * Sets the value of the dateSubscriptionPartners property.
     *
     * @param  dateSubscriptionPartners parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setDateSubscriptionPartners(Date dateSubscriptionPartners) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns dateUnSubscription property.
     *
     * @return dateUnSubscription property.
     */
    public Date getDateUnSubscription() {
        return (Date) repositoryItem.getPropertyValue(Constants.DATE_UNSUBSCRIBE_NEWSLETTER_PROP);
    }

    /**
     * Sets the value of the dateUnSubscription property.
     *
     * @param  dateUnSubscription parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setDateUnSubscription(Date dateUnSubscription) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns dateUnSubscriptionPartners property.
     *
     * @return dateUnSubscriptionPartners property.
     */
    public Date getDateUnSubscriptionPartners() {
        return (Date) repositoryItem.getPropertyValue(Constants.DATE_OFFERS_UNSUBSCRIBE_NEWSLETTER_PROP);
    }

    /**
     * Sets the value of the dateUnSubscriptionPartners property.
     *
     * @param  dateUnSubscriptionPartners parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setDateUnSubscriptionPartners(Date dateUnSubscriptionPartners) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }

    /**
     * Returns profile property.
     *
     * @return profile property.
     */
    public String getProfile() {
        return (String) repositoryItem.getPropertyValue(Constants.PROFILE_ID_FIELD_NEWSLETTER_PROP);
    }

    /**
     * Sets the value of the profile property.
     *
     * @param  profile parameter to set.
     *
     * @throws UnsupportedOperationException - exception.
     */
    public void setProfile(String profile) {
        throw new UnsupportedOperationException("updating item is not yet supported");
    }
}
