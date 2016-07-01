/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.stock.mchannel.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.CacheException;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.Region;
import org.jboss.cache.config.ConfigurationException;
import org.jboss.cache.config.EvictionRegionConfig;
import org.jboss.cache.eviction.LRUAlgorithmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.castorama.stock.mchannel.Configuration;


/**
 * Cache Starter
 *
 * @author EPAM TEAM
 */
@Service
public class CacheStarter {
  /** SELECT_STORES property */
  private static final String SELECT_STORES = "select distinct STORE_ID from ms_inventory order by STORE_ID";

  /** Apache Log4j property. */
  private static final Log LOG = LogFactory.getLog(CacheStarter.class);

  /** mConfiguration property */
  @Autowired
  @Qualifier("cacheConfig")
  private Configuration mConfiguration;

  /** mMultiThreadCacheLoader property */
  @Autowired
  private MultiThreadCacheLoader mMultiThreadCacheLoader;

  /** mCacheOwner property */
  @Autowired
  private CacheOwner mCacheOwner;

  /**
   * Initialize service.
   */
  @SuppressWarnings("unchecked")
  public void init() {
    if(LOG.isInfoEnabled()) {
      LOG.info("Cache inititialization...START");

      LOG.info("Initial settings [");
      LOG.info("   isLoadOnStartUp: " + mConfiguration.isLoadOnStartUp());
      LOG.info("   productsToLoad: " + mConfiguration.getProductsToLoad());
      LOG.info("   sqlFetchSize: " + mConfiguration.getSqlFetchSize());
      LOG.info("   numberOfThreads: " + mConfiguration.getNumberOfThreads());
      LOG.info("   lruMaxNodes: " + mConfiguration.getLruMaxNodes());
      LOG.info("   evictionRegionQueueSize: " + mConfiguration.getEvictionRegionQueueSize());
      LOG.info("]");
    }

    try {
      CacheOwner.getInstance().start();
    } catch(ConfigurationException exc) {
      if(LOG.isErrorEnabled()) {
        LOG.error(exc);
      }
    } catch(CacheException exc) {
      if(LOG.isErrorEnabled()) {
        LOG.error(exc);
      }
    }

    initializeStoreCache();
    initializePostalCodeCache();

    if(LOG.isInfoEnabled()) {
      LOG.info("Cache inititialization...END");
    }
  }

  /**
   * ToDo: DOCUMENT ME!
   */
  private void initializePostalCodeCache() {
    long start = 0;

    if(LOG.isInfoEnabled()) {
      LOG.info("Loading postal codes in cache ...START");
      start = System.currentTimeMillis();
    }

    final Node root = mCacheOwner.getCache().getRoot();
    loadPostalCodesInCache(root);

    if(LOG.isInfoEnabled()) {
      LOG.info("Loading postal codes in cache...END (completed in " + ((System.currentTimeMillis() - start) / 1000) +
        " sec)");
    }
  }

  /**
   * ToDo: DOCUMENT ME!
   */
  private void initializeStoreCache() {
    long start = 0;

    if(LOG.isInfoEnabled()) {
      LOG.info("Loading stores in cache...START");
      start = System.currentTimeMillis();
    }

    final Node root = mCacheOwner.getCache().getRoot();
    final List<Integer> storeIds = createStoreNodes(root);
    createEvictionPolicies(storeIds);
    loadCacheForStores(storeIds);

    if(LOG.isInfoEnabled()) {
      LOG.info("Loading stores in cache...END (completed in " + ((System.currentTimeMillis() - start) / 1000) +
        " sec)");
    }
  }

  /**
   * Loads store ids into teh cache.
   */
  private List<Integer> createStoreNodes(final Node root) {
    if(LOG.isDebugEnabled()) {
      LOG.debug("Creating store nodes in cache...START");
    }

    final List<Integer> storeIds = new ArrayList<Integer>();
    JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());

    try {
      template.query(SELECT_STORES,
        new RowCallbackHandler() {
          @SuppressWarnings("unchecked")
          public void processRow(final ResultSet pResultSet)
            throws SQLException {
            int storeId = pResultSet.getInt(1);
            Fqn storeFqn = Fqn.fromElements(storeId);
            root.addChild(storeFqn);

            if(LOG.isDebugEnabled()) {
              LOG.debug("Added Store Cache Node : " + storeFqn);
            }

            storeIds.add(storeId);
          }
        });
    } catch(Exception exc) {
      if(LOG.isErrorEnabled()) {
        LOG.error(exc);
      }
    }

    if(LOG.isDebugEnabled()) {
      LOG.debug("Creating store nodes in cache...END");
    }

    return storeIds;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeIds ToDo: DOCUMENT ME!
   */
  private void createEvictionPolicies(final List<Integer> storeIds) {
    // create eviction config
    if(LOG.isDebugEnabled()) {
      LOG.debug("Creating eviction policies...START");
      LOG.debug("storeIds size: " + storeIds.size());
    }

    for(int storeId : storeIds) {
      LRUAlgorithmConfig lruAlgorithm = new LRUAlgorithmConfig(-1, -1, mConfiguration.getLruMaxNodes());
      Fqn storeFqn = Fqn.fromElements(storeId);

      if(LOG.isDebugEnabled()) {
        LOG.debug("storeFqn: " + storeFqn);
      }

      EvictionRegionConfig erc = new EvictionRegionConfig(storeFqn, lruAlgorithm, mConfiguration.getEvictionRegionQueueSize());
      Region region = mCacheOwner.getCache().getRegion(storeFqn, true);

        if(LOG.isDebugEnabled()) {
          LOG.debug("$$$$$$$$$$$$$$ " + region);
        LOG.debug("Refion active: " + region.isActive());
        LOG.debug("Refion status: " + region.getStatus());
      }

      region.setEvictionRegionConfig(erc);

      if(LOG.isDebugEnabled()) {
        LOG.debug("Creating eviction policies...END");
      }
    }
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param stores ToDo: DOCUMENT ME!
   */
  private void loadCacheForStores(final List<Integer> pStoreIds) {
    if(mConfiguration.isLoadOnStartUp()) {
      try {
        if(LOG.isDebugEnabled()) {
          LOG.debug("Pre-loading cache for stores...START");
        }

        mMultiThreadCacheLoader.loadCacheForStores(pStoreIds, true);

        if(LOG.isDebugEnabled()) {
          LOG.debug("Pre-loading cache for stores...END");
        }

        System.gc();
      } catch(Exception exc) {
        if(LOG.isErrorEnabled()) {
          LOG.error(exc);
        }
      }
    } else {
      if(LOG.isInfoEnabled()) {
        LOG.info("Skipping pre-loading cache for stores. isLoadOnStartUp=false");
      }
    }
  }

  /**
   * Loads postal codes into teh cache.
   */
  private void loadPostalCodesInCache(final Node pRoot) {
    final Node postalCodesRoot = pRoot.addChild(Fqn.fromString(Constants.POSTAL_CODES_NODE_NAME));

    if(mConfiguration.isLoadOnStartUp()) {
      mMultiThreadCacheLoader.loadPostalCodesCache(postalCodesRoot); 
    } else {
      if(LOG.isInfoEnabled()) {
        LOG.info("Skipping pre-loading cache for postal codes. isLoadOnStartUp=false");
      }
    }
  }

  /**
   * Returns cache configuration.
   *
   * @return return
   */
  public Configuration getCacheConfiguration() {
    return mConfiguration;
  }

  /**
   * Sets cache configuration.
   *
   * @param pCacheConfiguration param
   */
  public void setCacheConfiguration(final Configuration pCacheConfiguration) {
    mConfiguration = pCacheConfiguration;
  }

  /**
   * Returns cache loader.
   *
   * @return return
   */
  public MultiThreadCacheLoader getMultiThreadCacheLoader() {
    return mMultiThreadCacheLoader;
  }

  /**
   * Sets cache loader.
   *
   * @param pMultiThreadCacheLoader param
   */
  public void setMultiThreadCacheLoader(final MultiThreadCacheLoader pMultiThreadCacheLoader) {
    mMultiThreadCacheLoader = pMultiThreadCacheLoader;
  }

  /**
   * Returns cache owner.
   *
   * @return return
   */
  public CacheOwner getCacheOwner() {
    return mCacheOwner;
  }

  /**
   * Sets cache owner.
   *
   * @param pCacheOwner param
   */
  public void setCacheOwner(final CacheOwner pCacheOwner) {
    mCacheOwner = pCacheOwner;
  }

  /**
   * @return the configuration
   */
  public Configuration getConfiguration() {
    return mConfiguration;
  }

  /**
   * @param pConfiguration the configuration to set
   */
  public void setConfiguration(final Configuration pConfiguration) {
    mConfiguration = pConfiguration;
  }
}
