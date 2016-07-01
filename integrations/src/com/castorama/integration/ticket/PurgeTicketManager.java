package com.castorama.integration.ticket;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.IntegrationBase;
import com.castorama.purchasehistory.PurchaseHistoryConfiguration;

/**
 * Service for purge order history.
 * 
 * @author Vasili_Ivus
 *
 */
public class PurgeTicketManager extends IntegrationBase implements Schedulable {

  public static final String TICKET_HEADER = "ticketHeader";
  
  public static final String PURGE_CONDITION = "ticketDate < ?0";

  private boolean mInProgress = false;

  public void performScheduledTask(Scheduler pScheduler, ScheduledJob pScheduledJob) {
    if ( mConfiguration.isPurgeEnabled() ) {
      try {
        purgeTickets();
      } catch (Exception e) {
        if (isLoggingError()) {
          logError(e);
        }
      }
    } else {
      if (isLoggingWarning()) {
        logWarning("Purge order history job hasn't started, because it is disabled.");
      }
    }
  }

  /**
   * Purge Tickets.
   * 
   * @throws ServiceException
   */
  public void purgeTickets() throws ServiceException {
    if (mInProgress) {
      throw new ServiceException("Unable to start purge... The service is being executing. Please wait...");
    }
    if(isLoggingInfo()) {
      logInfo("Purge tickets started...");
    }
    try {
      mInProgress = true;
      Repository r = getRepository();
      Calendar calendar = GregorianCalendar.getInstance();
      long nowMillis = calendar.getTimeInMillis();
      calendar.add(Calendar.YEAR, -mConfiguration.getHoldsTicketsYears());
      Timestamp ticketDate = new Timestamp(calendar.getTimeInMillis());
      calendar.setTimeInMillis(nowMillis);
      RepositoryView view = r.getView(TICKET_HEADER);
      RqlStatement statement = RqlStatement.parseRqlStatement(PURGE_CONDITION);
      Object params[] = new Object[2];
      params[0] = ticketDate;
      RepositoryItem [] items =statement.executeQuery (view, params);
      if ( null != items ) {
        for ( int index = 0; index < items.length; index++ ) {
          ((MutableRepository) r).removeItem(items[index].getRepositoryId(), TICKET_HEADER);
        }
      }
    } catch (RepositoryException e) {
      if ( isLoggingError() ) {
        logError("Purge tickets error.", e);
      }
    } finally {
      mInProgress = false;
    }
    if(isLoggingInfo()) {
      logInfo("Purge tickets finished...");
    }
  }
  
  private PurchaseHistoryConfiguration mConfiguration = null;

  /**
   * @return the configuration
   */
  public PurchaseHistoryConfiguration getConfiguration() {
    return mConfiguration;
  }

  /**
   * @param pConfiguration the configuration to set
   */
  public void setConfiguration(PurchaseHistoryConfiguration pConfiguration) {
    this.mConfiguration = pConfiguration;
  }

  
  // Scheduler property
  private Scheduler scheduler;

  /**
   * This service's scheduler
   * 
   * @return this service's scheduler
   */
  public Scheduler getScheduler() {
    return this.scheduler;
  }

  /**
   * This service's scheduler
   * 
   * @return this service's scheduler
   */
  public void setScheduler(Scheduler scheduler) {
    this.scheduler = scheduler;
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


  //Start service
  int jobId;
  
  /**
   * Connects to the queue and starts listening for messages.
   * 
   * @throws ServiceException
   *             If a service exception occurs.
   */
  public void doStartService() throws ServiceException {
    ScheduledJob job = new ScheduledJob("PurgeTicketManager",
        "Purge users order history",
        getAbsoluteName(),
        getSchedule(),
        this,
        ScheduledJob.SEPARATE_THREAD);
    jobId = getScheduler().addScheduledJob(job);
  }

  /**
   * Stop method.
   *
   * @throws ServiceException service exception.
   */
  public void doStopService() throws ServiceException {
    getScheduler().removeScheduledJob(jobId);
  }

}
