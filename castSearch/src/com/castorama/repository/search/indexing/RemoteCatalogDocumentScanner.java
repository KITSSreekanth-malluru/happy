package com.castorama.repository.search.indexing;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.ArrayList;

/**
 * Interface for document scanner
 *
 * @author Katsiaryna Sharstsiuk
 */
public interface RemoteCatalogDocumentScanner extends Remote {
    /**
     * Interface for getting list of documents for indexing
     *
     * @return list of documents for indexing
     *
     * @throws RemoteException
     */
    public ArrayList<CastoramaDocument> getDocumentsForIndexing() throws RemoteException;

}
