package com.castorama.integration.order;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.backoffice.WebOrdersManager;

/**
 * Job for removing orders with states PENDING_PAYBOX/PENDING_REMOVE
 * Removes orders older than 14 days (default delta value for /com/castorama/integration/backoffice/WebOrdersManager component).
 * delta is used when endDate property isn't defined in /com/castorama/integration/backoffice/WebOrdersManager.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class RemoveOrdersScheduler extends GenericService implements Schedulable {
    /** mWebOrdersManager constant. */
    private WebOrdersManager mWebOrdersManager;

    /** mScheduler property. */
    private Scheduler mScheduler;

    /** mSchedule constant. */
    private Schedule mSchedule;

    /** mJobId constant. */
    private int mJobId;

    /**
     * Returns webOrdersManager property.
     *
     * @return webOrdersManager property.
     */
    public WebOrdersManager getWebOrdersManager() {
        return mWebOrdersManager;
    }

    /**
     * Sets the value of the webOrdersManager property.
     *
     * @param pWebOrdersManager parameter to set.
     */
    public void setWebOrdersManager(WebOrdersManager pWebOrdersManager) {
        mWebOrdersManager = pWebOrdersManager;
    }

    /**
     * Returns scheduler property.
     *
     * @return scheduler property.
     */
    public Scheduler getScheduler() {
        return mScheduler;
    }

    /**
     * Sets the value of the scheduler property.
     *
     * @param pScheduler parameter to set.
     */
    public void setScheduler(Scheduler pScheduler) {
        mScheduler = pScheduler;
    }

    /**
     * Returns schedule property.
     *
     * @return schedule property.
     */
    public Schedule getSchedule() {
        return mSchedule;
    }

    /**
     * Sets the value of the schedule property.
     *
     * @param pSchedule parameter to set.
     */
    public void setSchedule(Schedule pSchedule) {
        mSchedule = pSchedule;
    }

    /* (non-Javadoc)
     * @see atg.service.scheduler.Schedulable#performScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
     */
    public void performScheduledTask(Scheduler pArg0, ScheduledJob pArg1) {
        try {
            removeOrders();
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }

    }

    /**
     * Removes order with states PENDING_PAYBOX/PENDING_REMOVE
     */
    private void removeOrders() {
        WebOrdersManager webOrdersManager = getWebOrdersManager();
        if (getWebOrdersManager() != null) {
            webOrdersManager.clearOrdersThatPendingPaybox();
            webOrdersManager.clearOrdersThatPendingRemove();
        }
    }

    /**
     * Connects to the queue and starts listening for messages.
     *
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job =
            new ScheduledJob("RemoveOrdersScheduler", "Remove orders in states pending paybox and pending remove", getAbsoluteName(),
                             getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
        mJobId = getScheduler().addScheduledJob(job);
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     *
     * @throws ServiceException exception
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(mJobId);
    }
}
