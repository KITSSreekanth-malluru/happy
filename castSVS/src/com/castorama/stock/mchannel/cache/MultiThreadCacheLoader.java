/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.stock.mchannel.cache;

import com.castorama.stock.mchannel.Configuration;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.cache.Fqn;
import org.jboss.cache.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;


/**
 * Cache loader.
 *
 * @author  EPAM team
 */
public class MultiThreadCacheLoader {
  /** Apache Log4j property */
  private static final Log LOG = LogFactory.getLog(MultiThreadCacheLoader.class);

  /** INSTANCE property */
  private static final MultiThreadCacheLoader INSTANCE = new MultiThreadCacheLoader();

  /** mThreadPool property */
  private ExecutorService mThreadPool;

  /** mConfiguration property */
  @Autowired
  @Qualifier("cacheConfig")
  private Configuration mConfiguration;

  /** mCacheOwner property */
  @Autowired
  private CacheOwner mCacheOwner;

  /**
   * Creates a new MultiThreadCacheLoader object. ToDo: DOCUMENT ME!
   */
  private MultiThreadCacheLoader() {
  }

  /**
   * Returns instance property.
   *
   * @return instance property.
   */
  public static MultiThreadCacheLoader getInstance() {
    return INSTANCE;
  }

  /**
   * Returns executorService property.
   *
   * @return executorService property.
   */
  private ExecutorService getExecutorService() {
    if(mThreadPool == null) {
      mThreadPool = Executors.newFixedThreadPool(mConfiguration.getNumberOfThreads());
    }

    return mThreadPool;
  }

  /**
   * Loads cache for store.
   *
   * @param storeIds store id.
   * @param block is block.
   */
  public void loadCacheForStores(final List<Integer> storeIds, final boolean block) {
    List<Callable<String>> calalables = new ArrayList<Callable<String>>();

    for(final Integer storeId : storeIds) {
      calalables.add(new Callable<String>() {
          public String call() {
            doLoadCacheForStore(storeId);

            return "OK";
          }
        });
    }

    if(block) {
      try {
        getExecutorService().invokeAll(calalables);
      } catch(InterruptedException e) {
        LOG.error(e);
      }
    } else {
      for(Callable<String> callable : calalables) {
        getExecutorService().submit(callable);
      }
    }
  }

  /**
   * Loads cache for store.
   *
   * @param storeId store id.
   */
  public void loadCacheForStore(final int storeId) {
    getExecutorService().submit(new Callable<String>() {
        public String call() {
          doLoadCacheForStore(storeId);

          return "OK";
        }
      });
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param postalCodesRoot ToDo: DOCUMENT ME!
   */
  public void loadPostalCodesCache(final Node pPostalCodesRoot) {
    if(LOG.isDebugEnabled()) {
      LOG.debug("Pre-loading cache for postal codes...START");
    }

    JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
    final int[] postalCodes = new int[]{ 0 };

    try {
      template.query("select postal_code, store_1, store_2, store_3, store_4, store_5, store_6, store_7, store_8, store_9, store_10 from MS_PCODE_TO_STORES",
        new RowCallbackHandler() {
          @SuppressWarnings("unchecked")
          public void processRow(final ResultSet pResultSet)
            throws SQLException {
            String postalCode = pResultSet.getString(1);
            Node postalCodeNode = pPostalCodesRoot.addChild(Fqn.fromElements(postalCode));
            List stores = new ArrayList(10);
            stores.add(pResultSet.getObject(2));
            stores.add(pResultSet.getObject(3));
            stores.add(pResultSet.getObject(4));
            stores.add(pResultSet.getObject(5));
            stores.add(pResultSet.getObject(6));
            stores.add(pResultSet.getObject(7));
            stores.add(pResultSet.getObject(8));
            stores.add(pResultSet.getObject(9));
            stores.add(pResultSet.getObject(10));
            stores.add(pResultSet.getObject(11));
            postalCodeNode.put("stores", stores);
            postalCodes[0]++;
          }
        });

      if(LOG.isDebugEnabled()) {
        LOG.debug("Added " + postalCodes[0] + " postal codes.");
        LOG.debug("Pre-loading cache for postal codes...END");
      }
    } catch(Exception exc) {
      if(LOG.isErrorEnabled()) {
        LOG.error(exc);
      }
    }
  }

  /**
   * Load cache for store.
   *
   * @param pStoreId store id.
   */
  @SuppressWarnings("unchecked")
  private void doLoadCacheForStore(final int pStoreId) {
    long start = 0;

    if(LOG.isTraceEnabled()) {
      start = System.currentTimeMillis();
      LOG.trace("Enter doLoadCacheForStore for store id=[" + pStoreId + "].");
    }

    JdbcTemplate template2 = new JdbcTemplate(getConfiguration().getDataSource());
    template2.setFetchSize(mConfiguration.getSqlFetchSize());
    template2.setMaxRows(mConfiguration.getProductsToLoad());

    final int[] productCount = new int[]{ 0 };

    Node storeNodeTemp = mCacheOwner.getCache().getRoot().getChild(Fqn.fromElements(pStoreId));

    if(storeNodeTemp == null) {
      if(LOG.isWarnEnabled()) {
        LOG.warn("Store node [" + pStoreId + "] not found. Creating...");
      }
      Fqn storeFqn = Fqn.fromElements(pStoreId);
      storeNodeTemp = mCacheOwner.getCache().getRoot().addChild(storeFqn);
    }
    
    final Node storeNode = storeNodeTemp;
    try {
      template2.query("SELECT PRODUCT_ID, STOCK_AVAILABLE FROM MS_INVENTORY WHERE STORE_ID=" +
        pStoreId,
        new RowCallbackHandler() {
          public void processRow(final ResultSet pResultSet)
            throws SQLException {
            String productId = pResultSet.getString(1);
            final Node prodNode = storeNode.addChild(Fqn.fromElements(productId));
            Float stockStatus = pResultSet.getFloat(2);
            prodNode.put(Constants.STATUS, stockStatus);
            productCount[0]++;
          }
        });
    } catch(Exception exc) {
      if(LOG.isErrorEnabled()) {
        LOG.error(exc);
      }
    }

    if(LOG.isTraceEnabled()) {
      LOG.trace("Added " + productCount[0] + " products.");
      LOG.trace("Exit doLoadCacheForStore for store id=[" + pStoreId + "] (completed in " +
        ((System.currentTimeMillis() - start) / 1000) + " sec).");
    }
  }

  /**
   * Returns configuration property.
   *
   * @return configuration property.
   */
  public Configuration getConfiguration() {
    return mConfiguration;
  }

  /**
   * Sets the value of the configuration property.
   *
   * @param pConfiguration parameter to set.
   */
  public void setConfiguration(final Configuration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  /**
   * Returns cacheOwner property.
   *
   * @return cacheOwner property.
   */
  public CacheOwner getCacheOwner() {
    return mCacheOwner;
  }

  /**
   * Sets the value of the cacheOwner property.
   *
   * @param pCacheOwner parameter to set.
   */
  public void setCacheOwner(final CacheOwner pCacheOwner) {
    mCacheOwner = pCacheOwner;
  }
}
