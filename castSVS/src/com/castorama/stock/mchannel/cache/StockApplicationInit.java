package com.castorama.stock.mchannel.cache;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoader;

/**
 * Stock initalizator.
 *
 * @author  EPAM team
 */
public class StockApplicationInit implements ServletContextListener {

  /**
   * Context destroyed.
   *
   * @param pEvent servelt context event.
   */
  public void contextDestroyed(ServletContextEvent pEvent) {
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pEvent servelt context event.
   */
  public void contextInitialized(ServletContextEvent pEvent) {
    try {
      CacheStarter sacheStarter =
        (CacheStarter) ContextLoader.getCurrentWebApplicationContext().getBean("cacheStarter");
      sacheStarter.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
