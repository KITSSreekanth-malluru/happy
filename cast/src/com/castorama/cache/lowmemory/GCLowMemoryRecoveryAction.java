package com.castorama.cache.lowmemory;

import atg.nucleus.GenericService;

/**
 * Given recovery action simply calls System.gc()
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class GCLowMemoryRecoveryAction extends GenericService implements LowMemoryRecoveryAction {
    /**
     * Calls System.gc()
     */
    public void runAction() {
    	System.gc();
    }
}
