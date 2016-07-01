package com.castorama.mail;

import atg.nucleus.ServiceException;

import atg.repository.RepositoryException;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

/**
 * MailSenderManager sends e-mails according to specified conditions and
 * time-table.
 *
 * @author EPAM team
 */
public class MailSenderManager extends SingletonSchedulableService {
    
    /** mMailSender property */
    private MailSender mMailSender;

    /**
     * Creates a new MailSenderManager object.
     */
    public MailSenderManager() {
    }

    /**
     * Returns mailSender property.
     *
     * @return mailSender property.
     */
    public MailSender getMailSender() {
        return mMailSender;
    }

    /**
     * Sets the value of the mailSender property.
     *
     * @param pMailSender parameter to set.
     */
    public void setMailSender(MailSender pMailSender) {
        this.mMailSender = pMailSender;
    }

    /**
     * This method is invoked according to the schedule.
     *
     * @param pScheduler Scheduler object to perform the task.
     * @param pJob       ScheduledJob object to be performed as a task.
     */
    public void doScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
        try {
            getMailSender().sendEmails();
        } catch (RepositoryException re) {
            logError("Error occured while sending emails.");
            logError(re.getCause());
        }
    }

    /**
     * Start service.
     *
     * @throws ServiceException
     */
    public void doStartService() throws ServiceException {
        super.doStartService();
        logInfo("Service started");
    }

}
