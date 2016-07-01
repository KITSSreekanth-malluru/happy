package com.castorama.catalog.custom;

import atg.commerce.catalog.CMSService;
import atg.commerce.catalog.custom.CatalogLoopException;
import atg.commerce.catalog.custom.CatalogProperties;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for classes that are called during maintenance service.
 *
 * @author EPAM team
 */
public abstract class BaseUpdateService extends CMSService {
    /**
     * catalogTools property.
     */
    private CustomCatalogTools mCatalogTools;

    /**
     * catalogProperties property.
     */
    private CatalogProperties mCatalogProperties;

    /**
     * maxItemsPerTransaction property.
     */
    private int mMaxItemsPerTransaction = 1000;

    /**
     * bookkeepingForThreads constant.
     */
    private Map mBookkeepingForThreads;

    /**
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CustomCatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CustomCatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets the value of the catalogProperties property.
     *
     * @param pCatalogProperties parameter to set.
     */
    public void setCatalogProperties(CatalogProperties pCatalogProperties) {
        mCatalogProperties = pCatalogProperties;
    }

    /**
     * Returns catalogProperties property.
     *
     * @return catalogProperties property.
     */
    public CatalogProperties getCatalogProperties() {
        return mCatalogProperties;
    }

    /**
     * Sets the value of the maxItemsPerTransaction property.
     *
     * @param pMaxItemsPerTransaction parameter to set.
     */
    public void setMaxItemsPerTransaction(int pMaxItemsPerTransaction) {
        mMaxItemsPerTransaction = pMaxItemsPerTransaction;
    }

    /**
     * Returns maxItemsPerTransaction property.
     *
     * @return maxItemsPerTransaction property.
     */
    public int getMaxItemsPerTransaction() {
        return mMaxItemsPerTransaction;
    }

    /**
     * This property maps repositories to information specific to the given
     * repository. The information in this class is used within a specific
     * thread.
     *
     * @param pRepository parameter
     * @return
     */
    protected BookkeepingForThread getBookkeepingForThread(Repository pRepository) {
        BookkeepingForThread info;
        if (mBookkeepingForThreads == null) {
            mBookkeepingForThreads = new HashMap();
        }
        if (mBookkeepingForThreads.containsKey(pRepository)) {
            info = (BookkeepingForThread) mBookkeepingForThreads.get(pRepository);
        } else {
            info = new BookkeepingForThread();
            mBookkeepingForThreads.put(pRepository, info);
        }
        return info;
    }

    protected void clearBookkeepingForThread(Repository pRepository) {
        mBookkeepingForThreads.remove(pRepository);
    }


    /**
     * Perform catalog update action.
     *
     * @param pRepository       parameter
     * @param pCatalogIds       parameter
     * @param pServiceFunctions parameter
     * @return true if all was passed successfully
     */
    public boolean performService(Repository pRepository, Collection pCatalogIds, List pServiceFunctions) {
        List serviceFunctions = pServiceFunctions;
        String[] functionsToPerformByDefault = getFunctionsToPerformByDefault();
        if (pServiceFunctions == null) {
            serviceFunctions = new java.util.ArrayList();
            if ((functionsToPerformByDefault != null) && (functionsToPerformByDefault.length > 0)) {
                for (int i = 0; i < functionsToPerformByDefault.length; i++) {
                    serviceFunctions.add(functionsToPerformByDefault[i]);
                }
            }
        }

        if (isLoggingDebug()) {
            logDebug(getServiceName() + " performService called with service functions " + stringIt(serviceFunctions));
        }

        if (serviceFunctions.contains(getServiceName())) {
            try {
                updateCatalog(pRepository);
            } catch (CatalogLoopException cle) {
                logError(cle);
            }
        }

        return true;
    }

    /**
     * This method checks to see if the calculated value is different from the
     * current value. If so, it sets the property to the new value
     *
     * @param pItem         parameter
     * @param pPropertyName parameter
     * @param pValue        parameter
     * @throws RepositoryException exception
     */
    protected void setPropertyValue(MutableRepositoryItem pItem, String pPropertyName, Object pValue)
            throws RepositoryException {
        Object currentValue = pItem.getPropertyValue(pPropertyName);
        if ((currentValue == null) || !currentValue.equals(pValue)) {
            pItem.setPropertyValue(pPropertyName, pValue);
        }
    }

    /**
     * Abstract method for catalog update.
     *
     * @param pCatalogRepository parameter
     * @throws CatalogLoopException exception
     */
    public abstract void updateCatalog(Repository pCatalogRepository) throws CatalogLoopException;

    protected abstract void calculatePrices(Repository pCatalogRepository) throws CatalogLoopException;
}
