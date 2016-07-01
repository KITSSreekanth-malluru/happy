package com.castorama.commerce.catalog.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for cache invalidation service
 * 
 * @author Epam Team
 */
public interface RemoteMaintenanceControllerService extends Remote {
	
	public void turnOn() throws RemoteException;
	
	public void turnOff() throws RemoteException;
	
	public boolean isActive() throws RemoteException;
}
