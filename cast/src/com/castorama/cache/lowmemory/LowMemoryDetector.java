package com.castorama.cache.lowmemory;

import atg.nucleus.ServiceException;
import atg.service.scheduler.SchedulableService;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

/**
 * Given scheduled service checks ratio of used heap size to max heap size.
 * If the ratio is greater or equal to specified <code>criticalRatio</code> then
 * the service will try to call configured recovery actions in way to try free up some
 * heap memory.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class LowMemoryDetector extends SchedulableService {
    /** MEGABYTE property */
    private static final int MEGABYTE = 1024 * 1024;

    /** CriticalRatio property */
    private double mCriticalRatio = 0.85;

    /** LowMemoryDetectorListeners property */
    private LowMemoryRecoveryAction[] mLowMemoryDetectorListeners;

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
     * Checks memory consumption for critical ratio.
     *
     * @param pScheduler ToDo: DOCUMENT ME!
     * @param pJob       ToDo: DOCUMENT ME!
     */
    public void performScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
        if (!isEnabled()) {
            if (isLoggingDebug()) {
                logDebug("Service is not active. Exiting performScheduledTask.");
            }
            return;
        }
    	long max = Runtime.getRuntime().maxMemory();
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (isLoggingDebug()) {
            logDebug("Max Heap: " + (max / MEGABYTE) + "M");
            logDebug("Current Total Heap: " + (Runtime.getRuntime().totalMemory() / MEGABYTE) + "M");
            logDebug("Current Free Heap: " + (Runtime.getRuntime().freeMemory() / MEGABYTE) + "M");
            logDebug("Used Heap: " + (used / MEGABYTE) + "M");
        }
        double ratio = ((double) used / (double) max);
        if (ratio >= getCriticalRatio()) {
            if (isLoggingDebug()) {
                logDebug("Ratio " + ratio + " is critical. Recovery actions will be performed.");
            }
            performLowMemoryRecovery();
        } else {
            if (isLoggingDebug()) {
                logDebug("Ratio " + ratio + " is NOT critical. Recovery actions are skipped.");
            }
        }
    }

    /**
     * Calls recovery actions
     */
    protected void performLowMemoryRecovery() {
        if (mLowMemoryDetectorListeners != null) {
            for (int i = 0; i < mLowMemoryDetectorListeners.length; i++) {
                try {
                    mLowMemoryDetectorListeners[i].runAction();
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError("Error occurred while executing recovery action", e);
                    }
                }
            }
        }
    }

    /**
     * Returns criticalRatio property.
     *
     * @return criticalRatio property.
     */
    public double getCriticalRatio() {
        return mCriticalRatio;
    }

    /**
     * Sets the value of the criticalRatio property.
     *
     * @param pCriticalRatio parameter to set.
     */
    public void setCriticalRatio(double pCriticalRatio) {
        mCriticalRatio = pCriticalRatio;
    }

    /**
     * Returns lowMemoryDetectorListeners property.
     *
     * @return lowMemoryDetectorListeners property.
     */
    public LowMemoryRecoveryAction[] getLowMemoryDetectorListeners() {
        return mLowMemoryDetectorListeners;
    }

    /**
     * Sets the value of the lowMemoryDetectorListeners property.
     *
     * @param pLowMemoryDetectorListeners parameter to set.
     */
    public void setLowMemoryDetectorListeners(LowMemoryRecoveryAction[] pLowMemoryDetectorListeners) {
        mLowMemoryDetectorListeners = pLowMemoryDetectorListeners;
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
