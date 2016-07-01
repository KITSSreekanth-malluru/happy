/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.integration.ticket;

import atg.core.util.ResourceUtils;

import atg.nucleus.ServiceException;

import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import atg.service.email.SMTPEmailSender;

import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.castorama.integration.IntegrationBase;

import com.castorama.purchasehistory.PurchaseHistoryConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.mail.Message;
import javax.mail.MessagingException;


/**
 * Notify users before they thickets will be purged.
 *
 * @author Vasili_Ivus
 */
public class NotificationPurgeTicketsManager extends IntegrationBase implements Schedulable {
  /**
   * ToDo: DOCUMENT ME!
   */
  public static final String RESOURCE_PURGE_NOTIFY = "going_to_purge_tickets";
  private static final String FILE_TEMPLATE = "com.castorama.integration.ticket.MailTickets_fr";
  private boolean mInProgress = false;

  //--------------------------------
  // Properties

  //--------------------------------
  // property: configuration
  private PurchaseHistoryConfiguration mConfiguration;

  /**
   * @return the configuration
   */
  public PurchaseHistoryConfiguration getConfiguration() {
    return mConfiguration;
  }

  /**
   * @param pConfiguration the configuration to set
   */
  public void setConfiguration(final PurchaseHistoryConfiguration pConfiguration) {
    this.mConfiguration = pConfiguration;
  }

  private TemplateEmailInfo mDefaultEmailInfo;

  /**
   * Sets the default email information. This is configured in the component property file.
   *
   * @param pDefaultEmailInfo -
   *            the default email information
   */
  public void setDefaultEmailInfo(final TemplateEmailInfo pDefaultEmailInfo) {
    mDefaultEmailInfo = pDefaultEmailInfo;
  }

  /**
   * Gets the default email information. This is configured in the component property file.
   *
   * @return the default email information
   */
  public TemplateEmailInfo getDefaultEmailInfo() {
    return mDefaultEmailInfo;
  }

  private String mTemplateBodyMessage;

  /**
   * @return the templateBodyMessage
   */
  public String getTemplateBodyMessage() {
    return mTemplateBodyMessage;
  }

  /**
   * @param pTemplateBodyMessage the templateBodyMessage to set
   */
  public void setTemplateBodyMessage(final String pTemplateBodyMessage) {
    this.mTemplateBodyMessage = pTemplateBodyMessage;
  }

  private SMTPEmailSender mEmailSender = null;

  /**
   * Sets the email send component. This is configured in the component property file.
   *
   * @param pEmailSender -
   *            the email send component
   */
  public void setEmailSender(final SMTPEmailSender pEmailSender) {
    mEmailSender = pEmailSender;
  }

  /**
   * Email Sender.
   *
   * @return Email Sender
   */
  public SMTPEmailSender getEmailSender() {
    return this.mEmailSender;
  }

  private PurgeTicketManager mPurgeTicketManager = null;

  /**
   * @return the purgeTicketManager
   */
  public PurgeTicketManager getPurgeTicketManager() {
    return mPurgeTicketManager;
  }

  /**
   * @param pPurgeTicketManager the purgeTicketManager to set
   */
  public void setPurgeTicketManager(final PurgeTicketManager pPurgeTicketManager) {
    this.mPurgeTicketManager = pPurgeTicketManager;
  }

  // Scheduler property
  private Scheduler mScheduler;

  /**
   * This service's scheduler
   *
   * @return this service's scheduler
   */
  public Scheduler getScheduler() {
    return this.mScheduler;
  }

  /**
   * @param pScheduler
   *            this service's scheduler
   */
  public void setScheduler(final Scheduler pScheduler) {
    this.mScheduler = pScheduler;
  }

  // Schedule property
  private Schedule mSchedule;

  /**
   * The schedule this service will run on
   *
   * @return the schedule this service will run on
   */
  public Schedule getSchedule() {
    return this.mSchedule;
  }

  /**
   * @param pSchedule
   *            the schedule this service will run on
   */
  public void setSchedule(final Schedule pSchedule) {
    this.mSchedule = pSchedule;
  }
  
  /**
   * This is called when a scheduled job tied to this object occurs.
   *
   * @param pScheduler - scheduler calling the job
   * @param pScheduledJob - called the job
   */
  public void performScheduledTask(final Scheduler pScheduler, final ScheduledJob pScheduledJob) {
    if(mConfiguration.isPurgeEnabled()) {
      try {
        notifyUsers();
      } catch(Exception e) {
        if(isLoggingError()) {
          logError(e);
        }
      }
    } else {
      if(isLoggingWarning()) {
        logWarning("User notification job hasn't started, because purge is disabled.");
      }
    }
  }

  /**
   * Notify users before they thickets will be purged.
   */
  public void notifyUsers() throws ServiceException {
    if(mInProgress) {
      throw new ServiceException("Unable to start users notification... The service is being executing. Please wait...");
    }

    if(isLoggingInfo()) {
      logInfo("Users notification started...");
    }

    try {
      mInProgress = true;

      Date purgeRunDateAsDate = mConfiguration.getPurgeRunDateAsDate();
      Calendar current = GregorianCalendar.getInstance();
      current.add(Calendar.DAY_OF_YEAR, mConfiguration.getDaysSendAlertBeforePurge());
      
      if(purgeRunDateAsDate.before(current.getTime())) {
        sendEmail();
      }
    } finally {
      mInProgress = false;
    }

    if(isLoggingInfo()) {
      logInfo("Users notification finished...");
    }
  }

  /**
   * Send email.
   *
   * @throws EmailException
   */
  private boolean sendEmail() {
    boolean result = false;

    try {
      TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();

      String from = emailInfo.getMessageFrom();
      String to = mConfiguration.getPurgeNotificationSendTo();
      String subject = emailInfo.getMessageSubject();
      String message = ResourceUtils.getUserMsgResource(RESOURCE_PURGE_NOTIFY, FILE_TEMPLATE, null);

      EmailEvent emailEvent = null;
      Message jMessage = MimeMessageUtils.createMessage(from, subject);
      MimeMessageUtils.setRecipient(jMessage, Message.RecipientType.TO, to);

      ContentPart[] l_content = { new ContentPart(message, "text/html"), };
      MimeMessageUtils.setContent(jMessage, l_content);
      emailEvent = new EmailEvent(jMessage);
      emailEvent.setCharSet("UTF-8");
      getEmailSender().sendEmailEvent(emailEvent);
    } catch(EmailException e) {
      if(isLoggingError()) {
        logError(e);
      }
    } catch(MessagingException me) {
      if(isLoggingError()) {
        logError(me);
      }
    }

    return result;
  }



  //Start service
  int jobId;

  /**
   * Connects to the queue and starts listening for messages.
   *
   * @throws ServiceException
   *             If a service exception occurs.
   */
  public void doStartService() throws ServiceException {
    ScheduledJob job = new ScheduledJob("NotificationPurgeTicketsService",
        "Notify users before purge they order history", getAbsoluteName(), getSchedule(), this,
        ScheduledJob.SCHEDULER_THREAD);
    jobId = getScheduler().addScheduledJob(job);
  }

  // Stop method
  /**
   * Stop method.
   *
   * @throws ServiceException service exception.
   */
  public void doStopService() throws ServiceException {
    getScheduler().removeScheduledJob(jobId);
  }
}
