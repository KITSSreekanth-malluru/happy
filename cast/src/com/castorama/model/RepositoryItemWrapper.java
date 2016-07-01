package com.castorama.model;

import atg.repository.RepositoryItem;

/**
 *
 * @author Andrew_Logvinov
 */
public class RepositoryItemWrapper {
    /** repositoryItem property. */
    protected final RepositoryItem repositoryItem;

    /**
     * Creates a new RepositoryItemWrapper object.
     *
     * @param  repositoryItem parameter
     *
     * @throws NullPointerException exception
     */
    public RepositoryItemWrapper(RepositoryItem repositoryItem) {
        if (null == repositoryItem) {
            throw new NullPointerException();
        }

        this.repositoryItem = repositoryItem;
    }

    /**
     * Returns repositoryItem property.
     *
     * @return repositoryItem property.
     */
    public RepositoryItem getRepositoryItem() {
        return repositoryItem;
    }

    /**
     * Returns id property.
     *
     * @return id property.
     */
    public String getId() {
        return repositoryItem.getRepositoryId();
    }

    /**
     * Wrapper method for quick refactoring purposes. You can simple exchange
     * repository item to wrapped object without compilation errors on get
     * values. Try to not use this method. Make wrapped methods for get and set
     * values.
     *
     * @param  propertyName
     *
     * @return wrapped {@link RepositoryItem#getPropertyValue(String)} call.
     */
    public Object getPropertyValue(String propertyName) {
        return repositoryItem.getPropertyValue(propertyName);
    }
}
