package com.castorama.cache;

import java.rmi.RemoteException;

import atg.adapter.gsa.GSARepository;
import atg.repository.Repository;

/**
 * Implementation of RepositoryCacheInvalidationService interface.
 * 
 * @author Epam Team
 */
public class RepositoryCacheInvalidationServiceImpl extends atg.nucleus.GenericRMIService implements
		RepositoryCacheInvalidationService {
	/**
	 * Constructor
	 * 
	 * @throws RemoteException
	 */
	public RepositoryCacheInvalidationServiceImpl() throws RemoteException {
		super();
	}

	/**
	 * Invalidate cache of pRepositoryPath repository.
	 * 
	 * @param pRepositoryPath -
	 *            determines repository to flush cache
	 */
	public void invalidateCache(String pRepositoryPath) throws RemoteException {
		if (isLoggingDebug()) {
			logDebug("Invalidate Cache for " + pRepositoryPath);
		}
		Object component = resolveName(pRepositoryPath);
		if (component != null && component instanceof GSARepository) {
			((GSARepository) component).invalidateCaches();

			if (isLoggingDebug()) {
				logDebug("Cache Invalidated");
			}
		}
	}

}
