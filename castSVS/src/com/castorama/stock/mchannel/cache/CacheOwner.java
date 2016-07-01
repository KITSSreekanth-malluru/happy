package com.castorama.stock.mchannel.cache;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheException;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.ConfigurationException;

/**
 * Cache Owner.
 *
 * @author EPAM team
 */
public class CacheOwner {

  private static final String CONFIGURATION_XML = "cache-configuration.xml";
  
  /** INSTANCE property */
  private static CacheOwner INSTANCE = new CacheOwner();

  /** started property */
  private boolean started = false;

  /** mCache property */
  @SuppressWarnings("unchecked")
  private Cache mCache;

  /**
   * Creates a new CacheController object. ToDo: DOCUMENT ME!
   */
  private CacheOwner() {
  }

  /**
   * Returns instance property.
   *
   * @return instance property.
   */
  public static final CacheOwner getInstance() {
    return INSTANCE;
  }

  /**
   * Starts cache owner.
   */
  @SuppressWarnings("unchecked")
  public void start() throws ConfigurationException, CacheException {
    if ( !started ) {
      started = true;
      CacheFactory factory = new DefaultCacheFactory();
      //      Configuration config = new Configuration();
      //      config.setTransactionManagerLookupClass( GenericTransactionManagerLookup.class.getName() );
      //      config.setIsolationLevel(IsolationLevel.READ_COMMITTED);
      //      config.setCacheMode(CacheMode.LOCAL);
      //      config.setLockAcquisitionTimeout(15000);
      mCache = factory.createCache(CacheOwner.class.getResourceAsStream(CONFIGURATION_XML));
      //Configuration conf = mCache.getConfiguration();
      mCache.create();
      mCache.start();
    }
  }

  /**
   * Returns cache property.
   *
   * @return cache property.
   */
  @SuppressWarnings("unchecked")
  public Cache getCache() {
    return mCache;
  }
}
