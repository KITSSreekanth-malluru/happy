package com.castorama.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for repository cache invalidation service
 * 
 * @author Epam Team
 */
public interface RepoCacheInvalidationGlobalNotifier extends Remote {
	/**
	 * invalidate cache method
	 */
    public void invalidateRemoteRepoCaches(String repoPath) throws RemoteException;
    
}
