/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.stock.mchannel;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class holds basic configuration for 'Stock Web Services'.
 *
 * @author EPAM team.
  */
public class Configuration {
  
  /** Apache Log4j property. */
  private static final Log LOG = LogFactory.getLog(Configuration.class);
  
  /** mSqlFetchSize property */
  private int mSqlFetchSize = 5000;

  /** mProductsToLoad property */
  private int mProductsToLoad = 5000;

  /** mLoadOnStart property */
  private boolean mLoadOnStartUp = true;

  /** mSqlFetchSize property */
  private int mNumberOfThreads = 5;

  /** dataSourceName property */
  private String mDataSourceName;
  
  /** Top line of limited availability of the product */
  private int limitedAvailabilityTop;

  /** Buttom line of limited availability of the product */
  private int limitedAvailabilityButtom;
  
  /** Maximum number of products in prodId URL parameter */
  private int maxNumberOfProductsInParameter;

  /** Max nodes for LRU algoritm */
  private int mLruMaxNodes = 1000;

  /** Eviction region queue size */
  private int mEvictionRegionQueueSize = 400000;

  public int getLimitedAvailabilityTop() {
	return limitedAvailabilityTop;
  }

  public void setLimitedAvailabilityTop(int limitedAvailabilityTop) {
	this.limitedAvailabilityTop = limitedAvailabilityTop;
  }

  public int getLimitedAvailabilityButtom() {
	return limitedAvailabilityButtom;
  }

  public void setLimitedAvailabilityButtom(int limitedAvailabilityButtom) {
	this.limitedAvailabilityButtom = limitedAvailabilityButtom;
  }

/**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public int getSqlFetchSize() {
    return mSqlFetchSize;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pSqlFetchSize ToDo: DOCUMENT ME!
   */
  public void setSqlFetchSize(final int pSqlFetchSize) {
    mSqlFetchSize = pSqlFetchSize;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public int getProductsToLoad() {
    return mProductsToLoad;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pProductsToLoad ToDo: DOCUMENT ME!
   */
  public void setProductsToLoad(final int pProductsToLoad) {
    mProductsToLoad = pProductsToLoad;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public boolean isLoadOnStartUp() {
    return mLoadOnStartUp;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pLoadOnStart ToDo: DOCUMENT ME!
   */
  public void setLoadOnStartUp(final boolean pLoadOnStartUp) {
    mLoadOnStartUp = pLoadOnStartUp;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public int getNumberOfThreads() {
    return mNumberOfThreads;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pNumberOfThreads ToDo: DOCUMENT ME!
   */
  public void setNumberOfThreads(final int pNumberOfThreads) {
    mNumberOfThreads = pNumberOfThreads;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public String getDataSourceName() {
    return mDataSourceName;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pDataSourceName ToDo: DOCUMENT ME!
   */
  public void setDataSourceName(final String pDataSourceName) {
    mDataSourceName = pDataSourceName;
  }
  
  /** mDataSource property */
  private DataSource mDataSource;

  /**
   * After service constructed.
   */
  @PostConstruct
  public void postConstruct() {
    if(mDataSource == null) {
      mDataSource = lookupDataSource();
    }
  }
  
  /**
   * Looks up data source.
   *
   * @return looked up data source.
   */
  private DataSource lookupDataSource() {
    DataSource result = null;
    if(LOG.isInfoEnabled()) {
      LOG.info("Trying to initialize '" + getDataSourceName() + "' data source...");
    }

    try {
      Context initContext = new InitialContext();
      Context envContext = (Context) initContext.lookup("java:");
      result = (DataSource) envContext.lookup(getDataSourceName());
    } catch(Exception e) {
      if(LOG.isErrorEnabled()) {
        LOG.error(e);
      }
    }
    if(result != null) {
      if(LOG.isInfoEnabled()) {
        LOG.info("Data source '" + getDataSourceName() + "' has been found.");
      }
    }

    return result;
  }

  /**
   * Returns dataSource property.
   *
   * @return dataSource property.
   */
  public DataSource getDataSource() {
    if(null == mDataSource) {
      mDataSource = lookupDataSource();
    }

    return mDataSource;
  }

  /**
   * @return the maxNumberOfProductsInParameter
   */
  public int getMaxNumberOfProductsInParameter() {
      return maxNumberOfProductsInParameter;
  }

  /**
   * @param maxNumberOfProductsInParameter the maxNumberOfProductsInParameter to set
   */
  public void setMaxNumberOfProductsInParameter(int maxNumberOfProductsInParameter) {
      this.maxNumberOfProductsInParameter = maxNumberOfProductsInParameter;
  }

  /**
   * @return the lruMaxNodes
   */
  public int getLruMaxNodes() {
      return mLruMaxNodes;
  }
  
  /**
   * @param pLruMaxNodes the lruMaxNodes to set
   */
  public void setLruMaxNodes(int pLruMaxNodes) {
      this.mLruMaxNodes = pLruMaxNodes;
  }

  /**
   * @return the evictionRegionQueueSize
   */
  public int getEvictionRegionQueueSize() {
      return mEvictionRegionQueueSize;
  }

  /**
   * @param pEvictionRegionQueueSize the evictionRegionQueueSize to set
   */
  public void setEvictionRegionQueueSize(int pEvictionRegionQueueSize) {
      this.mEvictionRegionQueueSize = pEvictionRegionQueueSize;
  }

}
