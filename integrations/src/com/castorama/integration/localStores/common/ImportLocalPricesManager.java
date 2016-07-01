package com.castorama.integration.localStores.common;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.localStores.cleanup.LocalStoresCleanupManager;
import com.castorama.integration.localStores.discount.ImportLocalStoresDiscountManager;

public class ImportLocalPricesManager extends GenericService implements Schedulable {
    
    /**
     * jobId property
     */
    int mJobId;
    /**
     * scheduler property
     */
    private Scheduler mScheduler;
    /**
     * schedule property
     */
    private Schedule mSchedule;
    
    /** importLocalStoresInfoManager property */
    private LocalStoresCleanupManager localStoresCleanupManager;
    
    /** importLocalStoresInfoManager property */
    private ImportLocalStoresInfoManager importLocalStoresInfoManager;
    
    /** importLocalStoresInfoManager property */
    private ImportLocalStoresDiscountManager importLocalStoresDiscountManager;
    
    /**
     * Connects to the queue and starts listening for messages.
     *
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob("ImportLocalPricesManager",
                "Load local prices from stores", getAbsoluteName(),
                getSchedule(), this, ScheduledJob.REUSED_THREAD);
        mJobId = getScheduler().addScheduledJob(job);
        if (isLoggingDebug()) {
            vlogDebug("ImportLocalPricesManager service started. Job id : {0}", mJobId);
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     *
     * @throws ServiceException
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(mJobId);
        if (isLoggingDebug()) {
            vlogDebug("ImportLocalStoresInfoManager service stopped. Job id : " + mJobId);
        }
    }

    @Override
    public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
        if (isLoggingInfo()) {
            vlogInfo("Perform sheduled task. Job name : {0}", job.getJobName());
        }
        try {
            getLocalStoresCleanupManager().cleanup();
            getImportLocalStoresInfoManager().importLocalStoresInfo();
            getImportLocalStoresDiscountManager().importDiscount();
        } catch (Exception e) {
            if (isLoggingError()) {
                vlogError(e, "Error from performScheduledTask method {0}",
                        e.getMessage());
            }
        }
    }

    /**
     * @return the mScheduler
     */
    public Scheduler getScheduler() {
        return mScheduler;
    }

    /**
     * @param mScheduler the mScheduler to set
     */
    public void setScheduler(Scheduler mScheduler) {
        this.mScheduler = mScheduler;
    }

    /**
     * @return the mSchedule
     */
    public Schedule getSchedule() {
        return mSchedule;
    }

    /**
     * @param mSchedule the mSchedule to set
     */
    public void setSchedule(Schedule mSchedule) {
        this.mSchedule = mSchedule;
    }

    /**
     * @return the localStoresCleanupManager
     */
    public LocalStoresCleanupManager getLocalStoresCleanupManager() {
        return localStoresCleanupManager;
    }

    /**
     * @param localStoresCleanupManager the localStoresCleanupManager to set
     */
    public void setLocalStoresCleanupManager(LocalStoresCleanupManager localStoresCleanupManager) {
        this.localStoresCleanupManager = localStoresCleanupManager;
    }

    /**
     * @return the importLocalStoresInfoManager
     */
    public ImportLocalStoresInfoManager getImportLocalStoresInfoManager() {
        return importLocalStoresInfoManager;
    }

    /**
     * @param importLocalStoresInfoManager the importLocalStoresInfoManager to set
     */
    public void setImportLocalStoresInfoManager(ImportLocalStoresInfoManager importLocalStoresInfoManager) {
        this.importLocalStoresInfoManager = importLocalStoresInfoManager;
    }

    /**
     * @return the importLocalStoresDiscountManager
     */
    public ImportLocalStoresDiscountManager getImportLocalStoresDiscountManager() {
        return importLocalStoresDiscountManager;
    }

    /**
     * @param importLocalStoresDiscountManager the importLocalStoresDiscountManager to set
     */
    public void setImportLocalStoresDiscountManager(ImportLocalStoresDiscountManager importLocalStoresDiscountManager) {
        this.importLocalStoresDiscountManager = importLocalStoresDiscountManager;
    }

}
