package com.castorama;

import atg.nucleus.GenericService;

/**
 * Class for rollback functionality
 *
 * @author Katsiaryna Sharstsiuk
 */
public class RollbackFunctionalityConfiguration extends GenericService {
    /**
     * mRollbackOrderSynchronization  - property for rollback Mantis 1777
     * functionality.
     */
    private boolean mRollbackOrderSynchronization = false;

    /**
     * Returns rollbackOrderSynchronization property.
     *
     * @return rollbackOrderSynchronization property.
     */
    public boolean isRollbackOrderSynchronization() {
        return mRollbackOrderSynchronization;
    }

    /**
     * Sets the value of the rollbackOrderSynchronization property.
     *
     * @param pRollbackOrderSynchronization parameter to set.
     */
    public void setRollbackOrderSynchronization(boolean pRollbackOrderSynchronization) {
        mRollbackOrderSynchronization = pRollbackOrderSynchronization;
    }

}
