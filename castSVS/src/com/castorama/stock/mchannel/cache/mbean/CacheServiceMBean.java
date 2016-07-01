/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.stock.mchannel.cache.mbean;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.castorama.stock.mchannel.Configuration;
import com.castorama.stock.mchannel.cache.CacheOwner;
import com.castorama.stock.mchannel.cache.Constants;
import com.castorama.stock.mchannel.cache.MultiThreadCacheLoader;


/**
 * ToDo: DOCUMENT ME!
 *
 * @author EPAM Team
  */
@ManagedResource(objectName = "com.castorama.stock.cache:type=CacheService", description = "CacheService Managed Bean")
public class CacheServiceMBean {
  /** Apache Log4j property. */
  private static final Log LOG = LogFactory.getLog(Configuration.class);
  @Autowired
  private MultiThreadCacheLoader mMultiThreadCacheLoader;
  @Autowired
  private CacheOwner mCacheOwner;

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Clears cache for the specific store")
  @ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id")
  })
  public void clearCache(final Integer pStoreId) {
    if (LOG.isDebugEnabled()){
      LOG.debug("ClearCache for store id=[" + pStoreId+"]");
    }

    Fqn fqn = Fqn.fromElements(pStoreId);

    Node root = mCacheOwner.getCache().getRoot();
    if(root.hasChild(fqn)) {
      Node storeNode = root.getChild(fqn);
      Set children = mCacheOwner.getCache().getChildrenNames(fqn);
      if (LOG.isDebugEnabled()){
        LOG.debug("Items to remove:"+children.size());
      }

      for(Object object : children) {
        storeNode.removeChild(object);
      }
      
      if (LOG.isDebugEnabled()){
        LOG.debug("Store Cache has been successfully cleared.");
      }
    }
  }
  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Clears cache for the specific store")
  @ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id"),@ManagedOperationParameter(name="codeArticles", description="list of code articles to delete from cache")
  })
  public void clearCache(final Integer pStoreId, final List<String> codeArticles) {
    if (LOG.isDebugEnabled()){
      LOG.debug("ClearCache for store id=[" + pStoreId+"] and skus " + codeArticles);
    }
    if (codeArticles != null && codeArticles.size() > 0){
        Fqn storeFqn = Fqn.fromElements(pStoreId);
    
        Node root = mCacheOwner.getCache().getRoot();
        if(root.hasChild(storeFqn)) {
          Node storeNode = root.getChild(storeFqn);
          if (LOG.isDebugEnabled()){
              LOG.debug("Items to remove: " + codeArticles.size());
          }
          for (String codeArticle : codeArticles){
              
              Fqn codeArticleFqn = Fqn.fromElements(codeArticle);
              if (storeNode.hasChild(codeArticleFqn)){
                  storeNode.removeChild(codeArticleFqn);
              }
              if (LOG.isDebugEnabled()){
                LOG.debug("Store Cache has been successfully cleared.");
              }
          }
        }
    }
  }
  /**
   * ToDo: DOCUMENT ME!
   *
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Clears postal codes cache")
  public void clearPostalCodesCache() {

    if (LOG.isDebugEnabled()){
      LOG.debug("ClearPostalCodesCache");
    }

    Fqn fqn = Fqn.fromString(Constants.POSTAL_CODES_NODE_NAME);

    if(mCacheOwner.getCache().getRoot().hasChild(fqn)) {
      Node postalCodesNode = CacheOwner.getInstance().getCache().getRoot().getChild(fqn);
      Set children = CacheOwner.getInstance().getCache().getChildrenNames(fqn);
      if (LOG.isDebugEnabled()){
        LOG.debug("Items to remove:"+children.size());
      }

      for(Object object : children) {
        postalCodesNode.removeChild(object);
      }

      if (LOG.isDebugEnabled()){
        LOG.debug("Postal Code Cache has been successfully cleared.");
      }
    }
  }
  
  /**
   * ToDo: DOCUMENT ME!
   *
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Loads postal codes cache")
  public void loadPostalCodesCache() {
    if (LOG.isDebugEnabled()){
      LOG.debug("LoadPostalCodesCache");
    }

    Fqn fqn = Fqn.fromString(Constants.POSTAL_CODES_NODE_NAME);
    Node root = mCacheOwner.getCache().getRoot();
    Node postalCodesRoot = null;
    
    if(root.hasChild(fqn)) {
      postalCodesRoot = root.getChild(fqn);
    } else {
      postalCodesRoot = root.addChild(fqn);
    }
    
    mMultiThreadCacheLoader.loadPostalCodesCache(postalCodesRoot);

    if (LOG.isDebugEnabled()){
      LOG.debug("Postal Code Cache has been successfully loaded. Size = "
        + postalCodesRoot.getChildren().size());
    }
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   */
  @ManagedOperation(description = "Clears and Reloads cache for the specific store")
  @ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id")
  })
  public void reloadCache(final Integer pStoreId) {
    if (LOG.isInfoEnabled()){
      LOG.info("Reload Store Cache for store id=[" + pStoreId+"]...START");
    }
    clearCache(pStoreId);
    mMultiThreadCacheLoader.loadCacheForStore(pStoreId);

    if (LOG.isInfoEnabled()){
      LOG.info("Reload Store Cache for store id=[" + pStoreId+"]...END");
    }

  }
  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   */
  @ManagedOperation(description = "Clear cache for the specific store")
  @ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id"),@ManagedOperationParameter(name="codeArticles", description="list of code articles to delete from cache")
  })
  public void removeItemsFromCache(final Integer pStoreId, final List<String> codeArticles) {
    if (LOG.isInfoEnabled()){
      LOG.info("Reload Store Cache for store id=[" + pStoreId+"]...START");
    }
    clearCache(pStoreId, codeArticles);
    //mMultiThreadCacheLoader.loadCacheForStore(pStoreId);

    if (LOG.isInfoEnabled()){
      LOG.info("Reload Store Cache for store id=[" + pStoreId+"]...END");
    }

  }
  /**
   * ToDo: DOCUMENT ME!
   *
   */
  @ManagedOperation(description = "Clears and reloads postal codes cache")
  public void reloadPostalCodesCache() {
    if (LOG.isInfoEnabled()){
      LOG.info("Reload PostalCode Cache...START");
    }

    clearPostalCodesCache();
	  loadPostalCodesCache();

	  if (LOG.isInfoEnabled()){
      LOG.info("Reload PostalCode Cache...END");
    }

  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Get size of cache for the specific store")
  @ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id")
  })
  public int getCacheSize(final Integer pStoreId) {
    if (LOG.isDebugEnabled()){
      LOG.debug("Get Cache Size for store id=[" + pStoreId+"]");
    }

    Fqn fqn = Fqn.fromElements(pStoreId);

    if(mCacheOwner.getCache().getRoot().hasChild(fqn)) {
      Node storeNode = mCacheOwner.getCache().getRoot().getChild(fqn);

      //Set children = CacheOwner.getInstance().getCache().getChildrenNames(fqn);
      return storeNode.getChildren().size();
    }

    return 0;
  }

  /**
   * ToDo: DOCUMENT ME!
   */
  @SuppressWarnings("unchecked")
  @ManagedOperation(description = "Invalidates all cache data")
  @ManagedOperationParameters({})
  public void invalidateCache() {
    if (LOG.isInfoEnabled()){
      LOG.info("Invalidating store cache...START");
    }

    final Node root = CacheOwner.getInstance().getCache().getRoot();
    final Set children = root.getChildrenNames();
    
    int count = 0;
    for(Object name : children) {
      
      if (name instanceof String && ((String)name).equals(Constants.POSTAL_CODES_NODE_NAME)) {
        // Skipping "Postal Codes" node...
        continue;
      }
      root.removeChild(name);
      count++;
    }
    
    if (LOG.isInfoEnabled()){
      LOG.info(count + " store items have been removed from cache.");
      LOG.info("Invalidating store cache...END");
    }

  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public MultiThreadCacheLoader getMultiThreadCacheLoader() {
    return mMultiThreadCacheLoader;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pMultiThreadCacheLoader ToDo: DOCUMENT ME!
   */
  public void setMultiThreadCacheLoader(final MultiThreadCacheLoader pMultiThreadCacheLoader) {
    mMultiThreadCacheLoader = pMultiThreadCacheLoader;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public CacheOwner getCacheOwner() {
    return mCacheOwner;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pCacheOwner ToDo: DOCUMENT ME!
   */
  public void setCacheOwner(final CacheOwner pCacheOwner) {
    mCacheOwner = pCacheOwner;
  }
  
//@SuppressWarnings("unchecked")
//@ManagedOperation(description = "Evict cache for the specific store")
//@ManagedOperationParameters({@ManagedOperationParameter(name = "storeId", description = "Store id")
//})
//public void evictStore(final Integer storeId) {
//  System.out.println("Evict ...");
//
//  Fqn fqn = Fqn.fromElements(storeId);
//  //mCacheOwner.getCache().evict(fqn, true);
//  System.out.println("Evict ..." + Fqn.fromElements(storeId));
//
//  final Region region = mCacheOwner.getCache().getRegion(Fqn.fromElements(storeId), false);
//
//  if(region != null) {
//    region.processEvictionQueues();
//  } else {
//    System.out.println("Region was not found. ");
//    mCacheOwner.getCache().evict(fqn, true);
//  }
//}

}
