package com.castorama.cache;

import java.rmi.RemoteException;

import atg.service.cache.Cache;
/**
 * Implementation of CacheInvalidationService interface.
 * @author Epam Team
 */
public class CacheInvalidationServiceImpl extends atg.nucleus.GenericRMIService implements CacheInvalidationService {
	/**
	 * Constructor
	 * @throws RemoteException
	 */
	public CacheInvalidationServiceImpl() throws RemoteException {
		super();		
	}
	/**
	 * Method for cache invalidation
	 */
	public void invalidateCache(String pCachePath) throws RemoteException {	
		if (isLoggingDebug()) {
			logDebug("Invalidate Cache for " + pCachePath);
		}	
		
		Object component = resolveName(pCachePath);
		if (component != null && component instanceof Cache) {
			((Cache) component).flush();
			
			if (isLoggingDebug()) {
				logDebug("Cache Invalidated");
			}
		}
	}
}
