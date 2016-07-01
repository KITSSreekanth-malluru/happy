package com.castorama.scenario.configuration;

import atg.nucleus.GenericService;

import atg.repository.Repository;

/**
 *
 * @author Andrei_Raichonak
 */
public class GodFatherFilterConfiguration extends GenericService {
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
     * Sets the value of the profileRepositiry property.
     *
     * @param profileRepository parameter to set.
     */
    public void setProfileRepositiry(Repository profileRepository) {
        mProfileRepository = profileRepository;
    }
}
