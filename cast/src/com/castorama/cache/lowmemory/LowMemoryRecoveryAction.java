package com.castorama.cache.lowmemory;

/**
 * Interface defining a simple recovery action
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBE
 */
public interface LowMemoryRecoveryAction {
    /**
     * Implementing class should provide some logic here for the low memory
     * recovery.
     */
    void runAction();
}
