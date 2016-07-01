package atg.adapter.gsa;

import java.util.ArrayList;
import java.util.List;

import atg.adapter.gsa.GSAItemDescriptor;

import atg.repository.Repository;
import atg.repository.RepositoryException;

import atg.service.scheduler.SchedulableService;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.nucleus.ServiceException;

/**
 * Given class invalidates caches for specified items on scheduled basis.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class ScheduledCacheInvalidator extends SchedulableService {
    /** mRepository property */
    private Repository mRepository;

    /** mItemsToInvalidate property */
    private List<String> mItemsToInvalidate = new ArrayList<String>();

    /** mCheckUsedRatio property */
    private boolean mCheckUsedRatio = false;

    /** mCriticalUsedRatio property */
    private double mCriticalUsedRatio = 0.85;

    /** mEnabled property */
    private boolean mEnabled = false;

    
    public void doStartService() throws ServiceException
    {
        if (isEnabled()) {
            startScheduledJob();
          }
          else {
            if (isLoggingDebug()) logDebug("Not starting up this service because it is not enabled.");
          }
    }        
    
    
    /**
     * ToDo: DOCUMENT ME!
     *
     * @param pScheduler ToDo: DOCUMENT ME!
     * @param pJob       ToDo: DOCUMENT ME!
     */
    public void performScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
        if (isLoggingDebug()) {
            logDebug("Entering performScheduledTask.");
        }
        if (!isEnabled()) {
            if (isLoggingDebug()) {
                logDebug("Service is not active. Exiting performScheduledTask.");
            }
            return;
        }
       
        if ((mItemsToInvalidate == null) || (mItemsToInvalidate.size() == 0)) {
            if (isLoggingDebug()) {
                logDebug("performScheduledTask: ItemsToInvalidate is empty. Exit.");
            }
            return;
        }

        for (String itemDescriptorName : mItemsToInvalidate) {
            try {
                GSAItemDescriptor descr = (GSAItemDescriptor) mRepository.getItemDescriptor(itemDescriptorName);
                ItemCache itemCache = descr.getItemCache();
                if (isLoggingDebug()) {
                    logDebug("performScheduledTask: " + itemDescriptorName + " : entry count=" +
                             itemCache.getEntryCount() + ", weak entry count=" + itemCache.getWeakEntryCount() +
                             ", size=" + itemCache.getItemCacheSize());
                }
                if (isCheckUsedRatio()) {
                    double ratio = itemCache.getEntryCount() / itemCache.getItemCacheSize();
                    if (isLoggingDebug()) {
                        logDebug("performScheduledTask: current used ratio=" + ratio);
                    }
                    if (ratio >= mCriticalUsedRatio) {
                        if (isLoggingDebug()) {
                            logDebug("performScheduledTask: used ratio is critical - need to invalidate cache");
                        }
                        descr.invalidateCaches();
                        if (isLoggingDebug()) {
                            logDebug("performScheduledTask: " + itemDescriptorName + " has been invalidated.");
                        }
                    } else {
                        if (isLoggingDebug()) {
                            logDebug("performScheduledTask: used ratio is not critical");
                        }
                    }
                } else {
                    descr.invalidateCaches();
                    if (isLoggingDebug()) {
                        logDebug("performScheduledTask: " + itemDescriptorName + " has been invalidated.");
                    }
                }  // end if-else

            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                continue;
            }  // end try-catch
        }  // end for
        if (isLoggingDebug()) {
            logDebug("Exiting performScheduledTask.");
        }
    }

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns itemsToInvalidate property.
     *
     * @return itemsToInvalidate property.
     */
    public List<String> getItemsToInvalidate() {
        return mItemsToInvalidate;
    }

    /**
     * Sets the value of the itemsToInvalidate property.
     *
     * @param pItemsToInvalidate parameter to set.
     */
    public void setItemsToInvalidate(List<String> pItemsToInvalidate) {
        mItemsToInvalidate = pItemsToInvalidate;
    }

    /**
     * Returns criticalUsedRatio property.
     *
     * @return criticalUsedRatio property.
     */
    public double getCriticalUsedRatio() {
        return mCriticalUsedRatio;
    }

    /**
     * Sets the value of the criticalUsedRatio property.
     *
     * @param pCriticalUsedRatio parameter to set.
     */
    public void setCriticalUsedRatio(double pCriticalUsedRatio) {
        mCriticalUsedRatio = pCriticalUsedRatio;
    }

    /**
     * Returns checkUsedRatio property.
     *
     * @return checkUsedRatio property.
     */
    public boolean isCheckUsedRatio() {
        return mCheckUsedRatio;
    }

    /**
     * Sets the value of the checkUsedRatio property.
     *
     * @param pCheckUsedRatio parameter to set.
     */
    public void setCheckUsedRatio(boolean pCheckUsedRatio) {
        mCheckUsedRatio = pCheckUsedRatio;
    }


    /**
     * Returns mEnabled property.
     *
     * @return mEnabled property.
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Sets the value of the mEnabled property.
     *
     * @param pEnabled parameter to set.
     */
    public void setEnabled(boolean pEnabled) {
        if (pEnabled && !mEnabled && isRunning()) {
            // start up the scheduled job if it isn't running
            startScheduledJob();
          }
          else if (!pEnabled && mEnabled) {
            stopScheduledJob();
          }
          mEnabled = pEnabled;
    }    
}
