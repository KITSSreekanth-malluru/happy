package com.castorama.integration.experian;

import java.util.List;

import com.castorama.model.Abonnement;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;

/**
 * Experian Request Helper
 * 
 * @author EPAM team
 */
public class ExperianRequestHelper extends GenericService {

    /** repository property. */
    private Repository mRepository;

    /** profileRepository property. */
    private Repository mProfileRepository;

    /**
     * Returns profileRepository property.
     * 
     * @return profileRepository property.
     */
    public Repository getProfileRepository() {
        return mProfileRepository;
    }

    /**
     * Sets the value of the profileRepository property.
     * 
     * @param pProfileRepository parameter to set.
     */
    public void setProfileRepository(Repository pProfileRepository) {
        mProfileRepository = pProfileRepository;
    }

    /**
     * Returns repository property.
     * 
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     * 
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Sends request about subscription creating.
     * 
     * @param email email
     * @param source source
     * 
     * @throws RepositoryException exception
     * @throws NullPointerException exception
     */
    public void addCreateSubscriptionRequest(String email, String source) throws RepositoryException, NullPointerException {
        Abonnement abonnement = Abonnement.getInstance(getRepository(), email);
        if (null == abonnement) {
            throw new NullPointerException("Abonnement " + email + " is not found.");
        }

        Boolean opt = abonnement.getSubscribled();
        Boolean optPartners = abonnement.getSubscribledPartners();

        if (((null == opt) || !opt) && ((null == optPartners) || !optPartners)) {
            // Silent exit
            return;
        }

        List<Object> values = ExperianUtils.createValuesList(abonnement, source, getProfileRepository());
        ExperianUtils.createAndAddToRepository(values, getRepository());
    }

    /**
     * Sends request about change of subscription.
     * 
     * @param email parameter
     * @param source source
     * 
     * @throws RepositoryException exception
     * @throws NullPointerException exception
     */
    public void addChangeSubscriptionRequest(String email, String source) throws RepositoryException, NullPointerException {
        Abonnement abonnement = Abonnement.getInstance(getRepository(), email);
        if (null == abonnement) {
            throw new NullPointerException("Abonnement " + email + " is not found.");
        }

        Boolean opt = abonnement.getSubscribled();
        Boolean optPartners = abonnement.getSubscribledPartners();

        if ((null == opt) && (null == optPartners)) {
            // Silent exit
            // no one subscription exists
            return;
        }

        List<Object> values = ExperianUtils.createValuesList(abonnement, source, getProfileRepository());
        ExperianUtils.createAndAddToRepository(values, getRepository());
    }

    /**
     * Send remove subscription request.
     * 
     * @param email parameter
     * @param source source
     * 
     * @throws RepositoryException exception
     * @throws NullPointerException exception
     */
    public void addRemoveSubscriptionRequest(String email, String source) throws RepositoryException, NullPointerException {
        Abonnement abonnement = Abonnement.getInstance(getRepository(), email);
        if (null == abonnement) {
            throw new NullPointerException("Abonnement " + email + " is not found.");
        }

        Boolean opt = abonnement.getSubscribled();
        Boolean optPartners = abonnement.getSubscribledPartners();

        if ((null != opt) && opt && (null != optPartners) && optPartners) {
            // Silent exit
            return;
        }
        List<Object> values = ExperianUtils.createValuesList(abonnement, source, getProfileRepository());
        ExperianUtils.createAndAddToRepository(values, getRepository());
    }

}
