package com.castorama.cache;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
/**
 * Test class for RepositoryCacheInvalidationService 
 * 
 * @author Epam Team
 */
public class TestRMI {
	public static void main(String[] args) throws RemoteException, NotBoundException, IOException {
		RepositoryCacheInvalidationService bb = (RepositoryCacheInvalidationService) Naming.lookup("rmi://localhost:10160/com/castorama/cache/RepositoryCacheInvalidationService");
		bb.invalidateCache("");
	}
}
