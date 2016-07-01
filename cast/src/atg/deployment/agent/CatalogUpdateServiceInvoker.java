package atg.deployment.agent;

import java.rmi.Naming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import atg.adapter.gsa.GSARepository;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.catalog.custom.CatalogUpdateService;

import atg.deployment.common.Status;
import atg.deployment.common.event.DeploymentEvent;

import atg.deployment.manifest.ManifestException;

import atg.nucleus.Nucleus;

import atg.repository.Repository;

import com.castorama.cache.RepoCacheInvalidationGlobalNotifier;

/**
 * Castorama extension of CatalogMaintenanceHelper class.
 *
 * @author EPAM team
 */
public class CatalogUpdateServiceInvoker extends CatalogMaintenanceHelper {
    
    private static final String RMI_PREFIX = "rmi://";
    
    /** defaultAffectedDescriptorNames property. */
    private String[] mDefaultAffectedDescriptorNames;

    /** catalogUpdateServiceName property. */
    private String mCatalogUpdateServiceName = "/atg/commerce/catalog/custom/CatalogUpdateService";
    
    /** active property. */
    private boolean active = true;
    
    /** repoCacheInvalidationGlobalNotifierName property. */
    private String repoCacheInvalidationGlobalNotifierName;
    
    /** publishingServer property. */
    private String publishingServer;
    
    /** publishingServerRmiPort property. */
    private int publishingServerRmiPort = 10160;

    /** catalogUpdateService property. */
    private CatalogUpdateService mCatalogUpdateService;
    
    /**
     * Returns defaultAffectedDescriptorNames property.
     *
     * @return defaultAffectedDescriptorNames property.
     */
    public String[] getDefaultAffectedDescriptorNames() {
        return mDefaultAffectedDescriptorNames;
    }

    /**
     * Sets the value of the defaultAffectedDescriptorNames property.
     *
     * @param pDefaultAffectedDescriptorNames parameter to set.
     */
    public void setDefaultAffectedDescriptorNames(String[] pDefaultAffectedDescriptorNames) {
        mDefaultAffectedDescriptorNames = pDefaultAffectedDescriptorNames;
    }

    /**
     * Sets the value of the catalogUpdateServiceName property.
     *
     * @param pCatalogUpdateServiceName parameter to set.
     */
    public void setCatalogUpdateServiceName(final String pCatalogUpdateServiceName) {
        if ((pCatalogUpdateServiceName != null) && (pCatalogUpdateServiceName.length() > 0)) {
            mCatalogUpdateServiceName = pCatalogUpdateServiceName;
        }
    }

    /**
     * Returns catalogUpdateServiceName property.
     *
     * @return catalogUpdateServiceName property.
     */
    public String getCatalogUpdateServiceName() {
        return mCatalogUpdateServiceName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRepoCacheInvalidationGlobalNotifierName() {
        return repoCacheInvalidationGlobalNotifierName;
    }

    public void setRepoCacheInvalidationGlobalNotifierName(
            String repoCacheInvalidationGlobalNotifierName) {
        this.repoCacheInvalidationGlobalNotifierName = repoCacheInvalidationGlobalNotifierName;
    }

    public String getPublishingServer() {
        return publishingServer;
    }

    public void setPublishingServer(String publishingServer) {
        this.publishingServer = publishingServer;
    }

    public int getPublishingServerRmiPort() {
        return publishingServerRmiPort;
    }

    public void setPublishingServerRmiPort(int publishingServerRmiPort) {
        this.publishingServerRmiPort = publishingServerRmiPort;
    }

    /**
     * Returns catalogUpdateService property.
     *
     * @return catalogUpdateService property.
     */
    public CatalogUpdateService getCatalogUpdateService() {
        if (mCatalogUpdateService == null) {
            // Lookup the maintenance service in nucleus.
            Nucleus nuke = Nucleus.getGlobalNucleus();
            mCatalogUpdateService = (CatalogUpdateService) nuke.resolveName(getCatalogUpdateServiceName(), true);
        }

        return mCatalogUpdateService;
    }

    /**
     * DeploymentEventListener callback.
     *
     * @param pEvent event to handle
     */
    public void deploymentEvent(final DeploymentEvent pEvent) {
        DeploymentAgent agent = getDeploymentAgent();
        if (agent.isLoggingDebug()) {
            agent.logDebug("Start deploymentEvent with event:" + pEvent);
        }
        if (isActive() && (getDeploymentAgent() != null) && (getCatalogUpdateService() != null) &&
                (pEvent.getNewState() == DeploymentEvent.DONE_APPLY)) {
            try {
                runCatalogUpdateService(pEvent);
            } catch (Exception e){
                if (agent.isLoggingError()) {
                    agent.logError(e);
                }
            }
        }
        if (agent.isLoggingDebug()) {
            agent.logDebug("Stop deploymentEvent with event:" + pEvent);
        }
    }

    /**
     * Run CatalogUpdateService#performService
     *
     * @param pEvent parameter
     */
    protected void runCatalogUpdateService(final DeploymentEvent pEvent) {
        DeploymentAgent agent = getDeploymentAgent();
        if (agent.isLoggingDebug()) {
            agent.logDebug("Start runCatalogUpdateService with event:" + pEvent);
        }

        try {
            CatalogUpdateService cms = getCatalogUpdateService();
            CatalogTools tools = ((CatalogUpdateService) cms).getCatalogTools();
    
            Repository rawCatalog = tools.getCatalog();
    
            if ((rawCatalog instanceof GSARepository) == false) {
                return;
            }
    
            GSARepository gsaCatalog = (GSARepository) rawCatalog;
            GSARepository catalog = gsaCatalog;
    
            try {
                catalog = agent.mShadows.resolveRepository(catalog.getAbsoluteName(), true);
    
                if (catalog == null) {
                    if (agent.isLoggingDebug()) {
                        agent.logDebug("Catalog is null");
                    }
                    return;
                }
            } catch (ManifestException me) {
                if (agent.isLoggingError()) {
                    agent.logError(me);
                }
                return;
            }
    
            Status agentStatus = agent.getStatus();
            if (agent.isLoggingDebug()) {
                if (agent != null) {
                    agent.logDebug("agentStatus is " + agentStatus);
                }
            }
    
            if (agentStatus.getAffectedItemTypes() == null) {
                if (agent.isLoggingDebug()) {
                    if (agent != null) {
                        agent.logDebug("agentStatus.getAffectedItemTypes() returns null");
                    }
                }
                return;
            }
    
            Set repositories = agentStatus.getAffectedItemTypes().keySet();
            Iterator i = repositories.iterator();
    
            while (i.hasNext()) {
                String gsaPath = (String) i.next();
                Object r = Nucleus.getGlobalNucleus().resolveName(gsaPath);
    
                if (!(r instanceof GSARepository)) {
                    continue;
                }
    
                GSARepository destinationShadow = null;
    
                try {
                    destinationShadow = agent.mShadows.resolveRepository(((GSARepository) r).getAbsoluteName(), true);
    
                    if (destinationShadow == null) {
                        if (agent.isLoggingDebug()) {
                            if (agent != null) {
                                agent.logDebug("destinationShadow is null");
                            }
                        }
                        return;
                    }
                } catch (ManifestException me) {
                    if (agent.isLoggingError()) {
                        agent.logError(me);
                    }
                    return;
                }
    
                if (catalog.getAbsoluteName().equals(destinationShadow.getAbsoluteName())) {
                    if (agent.isLoggingDebug()) {
                        agent.logDebug("found catalog : " + catalog.getAbsoluteName() + " = " +
                                       (destinationShadow.getAbsoluteName()));
                    }
    
                    Map m = agentStatus.getAffectedItemTypes();
                    Set names = (Set) m.get(gsaCatalog.getAbsoluteName());
                    Set extraPlusDefaults = new HashSet();
                    extraPlusDefaults.addAll(Arrays.asList(getDefaultAffectedDescriptorNames()));
    
                    if (getExtraTriggeringAffectedItemDescriptors() != null) {
                        extraPlusDefaults.addAll(Arrays.asList(getExtraTriggeringAffectedItemDescriptors()));
                    }
    
                    if (extraPlusDefaults.removeAll(names)) {
                        if (agent.isLoggingDebug()) {
                            agent.logDebug("Update will be run");
                        }
    
                        if (pEvent.getDeploymentMode() == MODE_ONLINE) {
                            ((GSARepository) r).invalidateCaches();
                        }
    
                        ((CatalogUpdateService) cms).performService(catalog, null, null);
                        
                        if (active) {
                            if (agent.isLoggingDebug()) {
                                agent.logDebug("runCatalogUpdateService active = true ");
                            }
                            try {
                                
                                String server = getPublishingServer();
                                if ((server == null) || server.length() == 0) {
                                    server = pEvent.getDeploymentServer();
                                    server = server.substring(0, server.indexOf(":"));
                                }
                                server += ":" + publishingServerRmiPort;
                                
                                RepoCacheInvalidationGlobalNotifier gn =
                                    (RepoCacheInvalidationGlobalNotifier) Naming.lookup(
                                            RMI_PREFIX + server + repoCacheInvalidationGlobalNotifierName);
                                if (agent.isLoggingDebug()) {
                                    agent.logDebug("gn.invalidateRemoteRepoCaches(gsaPath) started. Server = " + server);
                                }
                                gn.invalidateRemoteRepoCaches(gsaPath);
                                if (agent.isLoggingDebug()) {
                                    agent.logDebug("gn.invalidateRemoteRepoCaches(gsaPath) completed");
                                }
                            } catch (Exception e) {
                                if (agent.isLoggingError()) {
                                    agent.logError(e);
                                }
                            }
                        }
                        
                        return;
                    }
                }  // end if
            }
        } finally {
            if (agent.isLoggingDebug()) {
                if (agent != null) {
                    agent.logDebug("Stop runCatalogUpdateService with event:" + pEvent);
                }
            }
        }
    }
}
