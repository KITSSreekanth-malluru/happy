/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.stock.quartz;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * Quartz scheduled job to invalidate Stock cache.
 *
 * @author Aliaksandr Surma
  */

public class ClearCacheJob implements Job, MessageDrivenBean, MessageListener {
  //-------------------------------------
  //Constants
  private static final String INVALIDATE_CACHE = "invalidateCache";
  private static final String CACHE_MBEAN_NAME = "com.castorama.stock.cache:type=CacheService";
  private static final Logger LOG = Logger.getLogger(ClearCacheJob.class);

  /**
   * Scheduled job.
   *
   * @param jobExecutionContext JobExecutionContext instance
   *
   * @throws JobExecutionException if error occurs
   */
  public void execute(final JobExecutionContext jobExecutionContext)
    throws JobExecutionException {
    LOG.info("Executing ClearCache scheduled job...");

    try {
      final MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();
      final ObjectName bean = new ObjectName(CACHE_MBEAN_NAME);
      mbeanServer.invoke(bean, INVALIDATE_CACHE, null, null);
    } catch(Exception exc) {
      final String message = "Unable to execute scheduled job...";
      LOG.error(message,exc);
    }
  }
  
  /**
   * *
   * 
   * @see javax.ejb.MessageDrivenBean#ejbRemove() *
   * @throws EJBException
   */
  public void ejbRemove() throws EJBException {
  }

  /**
   * *
   * 
   * @see javax.ejb.MessageDrivenBean#setMessageDrivenContext(javax.ejb.MessageDrivenContext) *
   * @param context *
   * @throws EJBException
   */
  public void setMessageDrivenContext(MessageDrivenContext context)
      throws EJBException {
  }

  /**
   * *
   * 
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message) *
   * @param message
   */
  public void onMessage(Message message) {
  }

  public void ejbCreate() {
    LOG.info("ClearCache scheduled job created");
  }
  
}
