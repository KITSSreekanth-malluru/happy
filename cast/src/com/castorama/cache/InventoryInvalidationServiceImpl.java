package com.castorama.cache;

import java.rmi.RemoteException;

import atg.commerce.inventory.CachingInventoryManager;

/**
 * Implementation of InventoryInvalidationService interface.
 * 
 * @author Epam Team
 */
public class InventoryInvalidationServiceImpl extends atg.nucleus.GenericRMIService implements
		InventoryInvalidationService {

	/**
	 * inventoryManager property
	 */
	private CachingInventoryManager mInventoryManager;

	/**
	 * Constructor
	 * 
	 * @throws RemoteException
	 */
	public InventoryInvalidationServiceImpl() throws RemoteException {
		super();
	}

	/**
	 * Method for invalidating inventory cache
	 */
	public void invalidateCache() throws RemoteException {
		if (isLoggingDebug()) {
			logDebug("Invalidate Cache...");
		}

		getInventoryManager().getInventoryCache().flush();

		if (isLoggingDebug()) {
			logDebug("Cache Invalidated");
		}
	}

	/**
	 * Returns inventoryManager property
	 * 
	 * @return inventoryManager property
	 */

	public CachingInventoryManager getInventoryManager() {
		return mInventoryManager;
	}

	/**
	 * Sets inventoryManager property
	 * 
	 * @param pInventoryManager
	 *            parameter to set
	 */
	public void setInventoryManager(CachingInventoryManager pInventoryManager) {
		mInventoryManager = pInventoryManager;
	}

}