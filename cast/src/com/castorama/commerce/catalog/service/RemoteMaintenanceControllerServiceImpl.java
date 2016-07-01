package com.castorama.commerce.catalog.service;

import java.rmi.RemoteException;

import atg.deployment.agent.CatalogMaintenanceHelper;
import atg.deployment.agent.CatalogUpdateServiceInvoker;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class RemoteMaintenanceControllerServiceImpl extends atg.nucleus.GenericRMIService
    implements RemoteMaintenanceControllerService {
    
    /**
	 *  serialVersionUID
	 */
	private static final long serialVersionUID = 2794861591955228121L;

	/** mCatalogMaintenanceHelper property */
    private CatalogMaintenanceHelper mCatalogMaintenanceHelper;

    /** mCatalogUpdateServiceInvoker property */
    private CatalogUpdateServiceInvoker mCatalogUpdateServiceInvoker;

    /**
     * Creates a new RemoteMaintenanceControllerServiceImpl object. ToDo:
     * DOCUMENT ME!
     *
     * @throws RemoteException ToDo: DOCUMENT ME!
     */
    public RemoteMaintenanceControllerServiceImpl() throws RemoteException {
        super();
    }

    /**
     * Returns active property.
     *
     * @return active property.
     *
     * @throws RemoteException - exception
     */
    public boolean isActive() throws RemoteException {
        return mCatalogMaintenanceHelper.isActive();
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws RemoteException ToDo: DOCUMENT ME!
     */
    public void turnOff() throws RemoteException {
        mCatalogMaintenanceHelper.setActive(false);
        mCatalogUpdateServiceInvoker.setActive(false);
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws RemoteException ToDo: DOCUMENT ME!
     */
    public void turnOn() throws RemoteException {
        mCatalogMaintenanceHelper.setActive(true);
        mCatalogUpdateServiceInvoker.setActive(true);
    }

    /**
     * Returns catalogMaintenanceHelper property.
     *
     * @return catalogMaintenanceHelper property.
     */
    public CatalogMaintenanceHelper getCatalogMaintenanceHelper() {
        return mCatalogMaintenanceHelper;
    }

    /**
     * Sets the value of the catalogMaintenanceHelper property.
     *
     * @param pCatalogMaintenanceHelper parameter to set.
     */
    public void setCatalogMaintenanceHelper(CatalogMaintenanceHelper pCatalogMaintenanceHelper) {
        mCatalogMaintenanceHelper = pCatalogMaintenanceHelper;
    }

    /**
     * Returns catalogUpdateServiceInvoker property.
     *
     * @return catalogUpdateServiceInvoker property.
     */
    public CatalogUpdateServiceInvoker getCatalogUpdateServiceInvoker() {
        return mCatalogUpdateServiceInvoker;
    }

    /**
     * Sets the value of the catalogUpdateServiceInvoker property.
     *
     * @param pCatalogUpdateServiceInvoker parameter to set.
     */
    public void setCatalogUpdateServiceInvoker(CatalogUpdateServiceInvoker pCatalogUpdateServiceInvoker) {
        mCatalogUpdateServiceInvoker = pCatalogUpdateServiceInvoker;
    }

}
