package com.castorama.cache;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Inventory invalidation service interface.
 * @author Epam Team
 */
public interface InventoryInvalidationService extends Remote {
	/**
	 *  Method for cache invalidation.
	 */
	public void invalidateCache() throws RemoteException;
}