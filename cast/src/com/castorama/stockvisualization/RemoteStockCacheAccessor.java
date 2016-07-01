/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.stockvisualization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;


/**
 * Class to access remote JBoss MBean and invoke method to clear Stock Cache for specific store Id.
 *
 * @author Mikalai Khatsko, Aliaksandr Surma
  */
public class RemoteStockCacheAccessor extends GenericService {
  //--------------------------------
  //Constants
  private static final String REMOTE_METHOD_NAME = "removeItemsFromCache";
  private static final String RELOAD_CACHE_METHOD_NAME = "reloadCache";
  private static final String CACHE_SERVICE_NAME = "com.castorama.stock.cache:type=CacheService";
  private static final String RMIADAPTOR = "jmx/invoker/RMIAdaptor";
  
  private static final String RELOAD_POSTAL_CODES_CACHE_RMN = "reloadPostalCodesCache";

  //--------------------------------
  // Properties

  //--------------------------------
  // property: servers
  private ArrayList<String> mServers;

  /**
   * Returns mServers property.
   *
   * @return mServers property.
   */
  public ArrayList<String> getServers() {
    return mServers;
  }

  /**
   * Sets the value of the mServers property.
   *
   * @param pServers
   *            parameter to set.
   */
  public void setServers(final ArrayList<String> pServers) {
    mServers = pServers;
  }

  //--------------------------------
  // property: JMXConsoleUserName
  private String mJMXConsoleUserName;

  /**
   * Gets the jMXConsoleUserName property.
   *
   * @return the jMXConsoleUserName
   */
  public final String getJMXConsoleUserName() {
    return mJMXConsoleUserName;
  }

  /**
   * Sets the jMXConsoleUserName property.
   *
   * @param pConsoleUserName the jMXConsoleUserName to set
   */
  public final void setJMXConsoleUserName(final String pConsoleUserName) {
    mJMXConsoleUserName = pConsoleUserName;
  }

  //--------------------------------
  // property: JMXConsolePassword
  private String mJMXConsolePassword;

  /**
   * Gets the jMXConsolePassword property.
   *
   * @return the jMXConsolePassword
   */
  public final String getJMXConsolePassword() {
    return mJMXConsolePassword;
  }

  /**
   * Sets the jMXConsolePassword property.
   *
   * @param pConsolePassword the jMXConsolePassword to set
   */
  public final void setJMXConsolePassword(final String pConsolePassword) {
    mJMXConsolePassword = pConsolePassword;
  }

  //--------------------------------
  // property: storeId
  private Integer mStoreId;

  /**
   * Returns mStoreId property.
   *
   * @return mStoreId property.
   */
  public Integer getStoreId() {
    return mStoreId;
  }

  /**
   * Sets the value of the mStoreId property.
   *
   * @param pStoreId
   *            parameter to set.
   */
  public void setStoreId(final Integer pStoreId) {
    mStoreId = pStoreId;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @throws ServiceException ToDo: DOCUMENT ME!
   */
  public void invalidateCache() {
    if(isLoggingDebug()) {
      logDebug("Executing clearCache method with storeId=" + getStoreId());
    }
    invalidateCache(getStoreId());
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param storeId ToDo: DOCUMENT ME!
   *
   * @throws ServiceException ToDo: DOCUMENT ME!
   */
  public void invalidateCache(final Integer storeId) {
    if((null == storeId) || (storeId < 0)) {
      if(isLoggingError()) {
        logError("Parameter store id is invalid");
      }
      return;
    }

    InitialContext ic = null;
    int size = getServers().size();

    if(size == 0) {
      if(isLoggingError()) {
        logError("Remote servers list is empty");
      }
    }

    for(int i = 0; i < size; i++) {
      try {
        ic = getInitialContext(getServers().get(i));

        MBeanServerConnection server = (MBeanServerConnection) ic.lookup(RMIADAPTOR);
        ObjectName bean = new ObjectName(CACHE_SERVICE_NAME);
        Object[] arg1 = new Object[]{ storeId };
        String[] arg2 = new String[]{ Integer.class.getName() };
        server.invoke(bean, RELOAD_CACHE_METHOD_NAME, arg1, arg2);
      } catch(SecurityException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(NamingException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(MalformedObjectNameException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(NullPointerException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(InstanceNotFoundException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(MBeanException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(ReflectionException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } catch(IOException e) {
        if(isLoggingError()) {
          logError(e);
        }
      } finally {
        if(null != ic) {
          try {
            ic.close();
          } catch(NamingException e) {
            if(isLoggingError()) {
              logError("Can't close initial context", e);
            }
          }
        }
      }
    }
  }

    public void invalidateCache(final Integer storeId, final List<String> codeArticles) {
        if ((null == storeId) || (storeId < 0)) {
            if (isLoggingError()) {
                logError("Parameter store id is invalid");
            }
            return;
        }
        if ((codeArticles == null) || (codeArticles.size() <= 0)) {
            if (isLoggingError()) {
                logError("No items to invalidate");
            }
            return;
        }
        InitialContext ic = null;

        if (getServers() != null) {

            int size = getServers().size();

            if (size == 0) {
                if (isLoggingError()) {
                    logError("Remote servers list is empty");
                }
            }

            for (int i = 0; i < size; i++) {
                try {
                    ic = getInitialContext(getServers().get(i));

                    MBeanServerConnection server = (MBeanServerConnection) ic.lookup(RMIADAPTOR);
                    ObjectName bean = new ObjectName(CACHE_SERVICE_NAME);
                    Object[] arg1 = new Object[]{storeId, codeArticles};
                    String[] arg2 = new String[]{Integer.class.getName(), List.class.getName()};
                    server.invoke(bean, REMOTE_METHOD_NAME, arg1, arg2);
                } catch (SecurityException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (NamingException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (MalformedObjectNameException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (NullPointerException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (InstanceNotFoundException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (MBeanException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (ReflectionException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } finally {
                    if (null != ic) {
                        try {
                            ic.close();
                        } catch (NamingException e) {
                            if (isLoggingError()) {
                                logError("Can't close initial context", e);
                            }
                        }
                    }
                }
            }
        } else {
            if (isLoggingError()) {
                logError("Remote servers list is null");
            }
        }
    }

    /**
     * * ToDo: DOCUMENT ME!
     */
    public void reloadPostalCodesCache() {
        InitialContext ic = null;
        int size = getServers().size();

        if (size == 0) {
            if (isLoggingError()) {
                logError("Remote servers list is empty");
            }
        }

        for (int i = 0; i < size; i++) {
            try {
                ic = getInitialContext(getServers().get(i));

                MBeanServerConnection server = (MBeanServerConnection) ic
                        .lookup(RMIADAPTOR);
                ObjectName bean = new ObjectName(CACHE_SERVICE_NAME);
                Object[] arg1 = new Object[]{};
                String[] arg2 = new String[]{};
                server.invoke(bean, RELOAD_POSTAL_CODES_CACHE_RMN, arg1, arg2);
            } catch (SecurityException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (NamingException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (MalformedObjectNameException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (NullPointerException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (InstanceNotFoundException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (MBeanException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (ReflectionException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            } finally {
                if (null != ic) {
                    try {
                        ic.close();
                    } catch (NamingException e) {
                        if (isLoggingError()) {
                            logError("Can't close initial context", e);
                        }
                    }
                }
            }
        }
    }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param server ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws NamingException ToDo: DOCUMENT ME!
   */
  private InitialContext getInitialContext(final String server)
    throws NamingException {
    if(null == server) {
      throw new NamingException("Server name is null.");
    }

    Hashtable<String, String> props = new Hashtable<String, String>();
    props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.JndiLoginInitialContextFactory");
    props.put(Context.PROVIDER_URL, "jnp://" + server);
    props.put(Context.SECURITY_CREDENTIALS, getJMXConsolePassword());
    props.put(Context.SECURITY_PRINCIPAL, getJMXConsoleUserName());

    InitialContext ctx = new InitialContext(props);

    return ctx;
  }
}
