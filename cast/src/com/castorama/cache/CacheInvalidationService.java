package com.castorama.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for cache invalidation service
 * 
 * @author Epam Team
 */
public interface CacheInvalidationService extends Remote {
	/**
	 * Method for cache invalidation.
	 */
	public void invalidateCache(String pCacheServicePath) throws RemoteException;
}
