package com.castorama.searchadmin.adapter.content.impl;

import java.io.IOException;

import java.rmi.ConnectIOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.List;
import java.util.Locale;
import java.util.Properties;

import atg.nucleus.GenericContext;

import atg.search.adapter.content.ConfigurationStatus;
import atg.search.adapter.content.ContentItemSource;
import atg.search.adapter.exception.AdapterException;
import atg.search.adapter.loader.AcquireSession;
import atg.search.adapter.loader.AcquireSessionStatus;
import atg.search.adapter.loader.command.ContentItemCommand;

import atg.search.routing.command.indexing.DocType;
import atg.search.routing.command.indexing.DocumentCommandProperties;

import atg.searchadmin.adapter.content.impl.BasicContentItem;

import atg.searchadmin.utils.SearchFileTyperService;

import com.castorama.repository.search.indexing.CastIndexDocument;
import com.castorama.repository.search.indexing.CastoramaDocument;
import com.castorama.repository.search.indexing.RemoteCatalogDocumentScanner;

import com.castorama.search.adapter.loader.command.CastAddContentItem;

/**
 * CastoramaDocumentSource for support castorama document items indexing
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaDocumentSource extends GenericContext implements ContentItemSource, Runnable {
    /** HOST_MACHINE constant. */
    public static final String HOST_MACHINE = "HostMachine";

    /** PORT constant. */
    public static final String PORT = "Port";

    /** PATH constant. */
    public static final String PATH = "Path";

    /** CastoramaCatalogDocumentScanner constant. */
    public static final String CDS_PATH = "CatalogDocumentScanner";

    /** adapterExtraConfig property */
    private Properties mAdapterExtraConfig;

    /** acquireSession property */
    private AcquireSession mAcquireSession;

    /** acquireThread property */
    private Thread mAcquireThread;

    /** searchFileTyperService property */
    private SearchFileTyperService mSearchFileTyperService = new SearchFileTyperService();

    /**
     * Constructor
     *
     * @param pProps parameter
     */
    public CastoramaDocumentSource(Properties pProps) {
        mAdapterExtraConfig = pProps;
    }

    /**
     * Returns searchFileTyperService property.
     *
     * @return searchFileTyperService property.
     */
    public SearchFileTyperService getSearchFileTyperService() {
        return mSearchFileTyperService;
    }

    /**
     * Sets the value of the searchFileTyperService property.
     *
     * @param pSearchFileTyperService parameter to set.
     */
    public void setSearchFileTyperService(SearchFileTyperService pSearchFileTyperService) {
        mSearchFileTyperService = pSearchFileTyperService;
    }

    /**
     * Begin acquire method. Perform indexing work.
     *
     * @param  pAcquireSession parameter
     *
     * @return exception
     *
     * @throws AdapterException exception
     */
    public boolean beginAcquire(AcquireSession pAcquireSession) throws AdapterException {
        mAcquireSession = pAcquireSession;
        mAcquireThread = new Thread(this);
        mAcquireThread.start();

        return true;
    }

    /**
     * Cancel acquire
     *
     * @return false in any cases
     */
    public boolean cancelAcquire() {
        return false;
    }

    /**
     * Returns extended status
     *
     * @param pCallback parameter
     * @param pLocale   parameter
     */
    public void getExtendedStatus(StatusCallback pCallback, Locale pLocale) {
    }

    /**
     * Returns simple status
     *
     * @param  pLocale parameter
     *
     * @return null
     */
    public ConfigurationStatus getSimpleStatus(Locale pLocale) {
        return null;
    }

    /**
     * Process document submission.
     */
    public void run() {
        try {
            RemoteCatalogDocumentScanner remoteCDS = getRemoteCatalogDocumentScannerBySession(mAcquireSession);

            if (remoteCDS != null) {
                List docsForIndexing = remoteCDS.getDocumentsForIndexing();
                for (CastoramaDocument castDoc : (List<CastoramaDocument>) docsForIndexing) {
                    if ((castDoc.getFile() != null) && (castDoc.getFile().length > 0)) {
                        BasicContentItem contentItem =
                            new com.castorama.searchadmin.adapter.content.impl.CastDocContentItem(castDoc.getFile());

                        contentItem.setWebsiteUrl("/" + castDoc.getCategoryId() + "/" + castDoc.getRepositoryId() +
                                                  castDoc.getRelativeURL());
                        contentItem.setDocUrl("/" + castDoc.getCategoryId() + "/" + castDoc.getRepositoryId() +
                                              castDoc.getRelativeURL());
                        contentItem.setId("/" + castDoc.getCategoryId() + "/" + castDoc.getRepositoryId() +
                                          castDoc.getRelativeURL());

                        contentItem.setDocType(DocType.HTMLDOC);
                        ContentItemCommand command = new CastAddContentItem();
                        command.setContentItem(contentItem);
                        command.setCharSet("UTF-8");
                        command.setStreaming(false);

                        DocumentCommandProperties dcp = command.getDocumentCommandProperties();
                        if ((castDoc.getDocumentMetadataList() != null) &&
                                !castDoc.getDocumentMetadataList().isEmpty()) {
                            ((CastIndexDocument) dcp).setCastMetadata(castDoc.getDocumentMetadataList());
                        }
                        mAcquireSession.processCommand(command);

                    }  // end if
                }  // end for
                mAcquireSession.notify(atg.search.adapter.loader.AcquireSessionStatus.EDone);
            }  // end if
        } catch (ConnectIOException e) {
            mAcquireSession.notifyError(e, true, null);
            mAcquireSession.notify(AcquireSessionStatus.EError);
        } catch (RemoteException e) {
            mAcquireSession.notify(AcquireSessionStatus.EError);
        } catch (NotBoundException e) {
            mAcquireSession.notify(AcquireSessionStatus.EError);
        } catch (IOException e) {
            mAcquireSession.notify(AcquireSessionStatus.EError);
        }  // end try-catch

    }

    /**
     * Returns document type. If searchFileTyperService isn't set then UNKNOWN
     * document type is returned.
     *
     * @param  pCastDoc parameter
     *
     * @return document type
     */
    private DocType getDocType(CastoramaDocument pCastDoc) {
        if (getSearchFileTyperService() != null) {
            return getSearchFileTyperService().getSearchDocumentType(pCastDoc.getRelativeURL());
        }
        return DocType.UNKNOWN;
    }

    /**
     * Returns remoteCatalogDocumentScanner object. Get it through RMI.
     *
     * @param  pAcquireSession parameter
     *
     * @return remoteCatalogDocumentScanner object
     *
     * @throws RemoteException    exception
     * @throws NotBoundException  exception
     * @throws IOException        exception
     * @throws ConnectIOException excption
     */
    private RemoteCatalogDocumentScanner getRemoteCatalogDocumentScannerBySession(AcquireSession pAcquireSession)
                                                                           throws RemoteException, NotBoundException,
                                                                                  IOException {
        String host = mAdapterExtraConfig.getProperty(HOST_MACHINE);
        String port = mAdapterExtraConfig.getProperty(PORT);
        String cdsPath = mAdapterExtraConfig.getProperty(CDS_PATH);

        RemoteCatalogDocumentScanner remoteCDS = null;

        String cdsLocation = null;

        try {
            if ((host != null) && (port != null)) {
                cdsLocation = "rmi://" + host + ":" + port + cdsPath;
                remoteCDS = (RemoteCatalogDocumentScanner) Naming.lookup(cdsLocation);
            }
        } catch (ConnectIOException e) {
            throw new ConnectIOException("Cannot find remote CatalogDocumentScanner at the following location: " +
                                         cdsLocation, e);
        } finally {
            if (remoteCDS == null) {
                if (isLoggingError()) {
                    logError("Cannot find remote CatalogDocumentScanner at the following location: " + cdsLocation);
                }
            }
        }

        return remoteCDS;
    }
}
