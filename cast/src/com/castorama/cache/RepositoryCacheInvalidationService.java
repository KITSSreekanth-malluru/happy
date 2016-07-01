package com.castorama.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for repository cache invalidation service
 * 
 * @author Epam Team
 */
public interface RepositoryCacheInvalidationService extends Remote {
	/**
	 * invalidate cache method
	 */
	public void invalidateCache(String pRepositoryPath) throws RemoteException;
}
