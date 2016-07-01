package com.castorama.scenario.configuration;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;

import atg.repository.Repository;

/**
 *
 * @author Andrei_Raichonak
 */
public class SearchMessageHandlerConfiguration {
    /** Transaction manager. */
    private TransactionManager mTransactionManager;

    /** Type Ahead repository. */
    private Repository mTypeAhead;

    /** Transaction demarcation. */
    private TransactionDemarcation mTransactionDemarcation;

    /**
     * Returns transactionManager property.
     *
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     *
     * @param pTransactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns typeAheadRepository property.
     *
     * @return typeAheadRepository property.
     */
    public Repository getTypeAheadRepository() {
        return mTypeAhead;
    }

    /**
     * Sets the value of the typeAhead property.
     *
     * @param typeAhead parameter to set.
     */
    public void setTypeAhead(Repository typeAhead) {
        this.mTypeAhead = typeAhead;
    }

    /**
     * Returns transactionDemarcation property.
     *
     * @return transactionDemarcation property.
     */
    public TransactionDemarcation getTransactionDemarcation() {
        return mTransactionDemarcation;
    }

    /**
     * Sets the value of the transactionDemarcation property.
     *
     * @param td parameter to set.
     */
    public void setTransactionDemarcation(TransactionDemarcation td) {
        this.mTransactionDemarcation = td;
    }

}
