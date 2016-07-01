package com.castorama.cache;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.util.List;
import java.util.Set;

import atg.deployment.common.DeploymentException;

import atg.deployment.server.DeploymentServer;
import atg.deployment.server.topology.AgentDef;
import atg.deployment.server.topology.TargetDef;
import atg.deployment.server.topology.TopologyDef;
import atg.deployment.server.topology.TransportDef;
import atg.deployment.server.topology.TransportTypeEnum;

import atg.nucleus.GenericRMIService;

import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;

import atg.userdirectory.UserDirectoryUserAuthority;

/**
 * Given class is intended to initialize repo cache invalidation on servers for which
 * primary topology has publishing agents specified.
 *
 * @author EPAM team
 */
public class RepoCacheInvalidationGlobalNotifierImpl extends GenericRMIService 
        implements RepoCacheInvalidationGlobalNotifier {

    /** mRepoCacheInvalidationServicePath property */
    private String mRepoCacheInvalidationServicePath;

    /** mDeploymentServer property */
    private DeploymentServer mDeploymentServer;

    /** Logged in user with admin rights. */
    private User mCurrentUser;

    /** Previous logged in user (in running thread) */
    private User mPreviousUser;

    /** UserDirectoryUserAuthority */
    private UserDirectoryUserAuthority mUserAuthority;

    /** mPublishingAdmin property */
    private String mPublishingAdmin;

    public RepoCacheInvalidationGlobalNotifierImpl() throws RemoteException { }
    
    /**
     * ToDo: DOCUMENT ME!
     *
     * @throws DeploymentException ToDo: DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    public void invalidateRemoteRepoCaches(String repoPath) throws RemoteException {
        try {
            if (isLoggingDebug()) {
                logDebug("Start invalidateRemoteRepoCaches for : " + repoPath!=null?repoPath:"null");
            }
            //need to login to access the topology info
            assumeUserIdentity(getPublishingAdmin());
            
            TopologyDef topology = getDeploymentServer().getTopologyManager().getPrimaryTopology();
            if (topology == null) {
                if (isLoggingError()) {
                    logError("No Primary Topology. Exit.");
                }
                return;
            }
            List<TargetDef> targets = topology.getTargets();
            for (TargetDef targetDef : targets) {
                Set<AgentDef> agents = targetDef.getAgents();
                for (AgentDef agentDef : agents) {
                    TransportDef transport = agentDef.getTransport();
                    if (transport.getTransportType() == TransportTypeEnum.RMI) {
                        String rmiURI = transport.getRmiURI();
                        String hostAddress = rmiURI.substring(0, rmiURI.indexOf('/', 7));
                        try {
                            RepositoryCacheInvalidationService bb =
                                (RepositoryCacheInvalidationService) Naming.lookup(hostAddress + mRepoCacheInvalidationServicePath);
                            bb.invalidateCache(repoPath);
                            if (isLoggingDebug()) {
                                logDebug("Cache at " + hostAddress + " has been invalidated.");
                            }
                        } catch (Exception e) {
                            if (isLoggingError()) {
                                logError(e);
                            }
                        }
                    }
                }
            }  // end for
        } catch (DeploymentException de) {
            if (isLoggingError()) {
                logError(de);
            }
        } finally {
            unassumeUserIdentity();
            if (isLoggingDebug()) {
                logDebug("Stop invalidateRemoteRepoCaches for : " + repoPath!=null?repoPath:"null");
            }
        }  // end try-finally

    }

    /**
     * Returns deploymentServer property.
     *
     * @return deploymentServer property.
     */
    public DeploymentServer getDeploymentServer() {
        return mDeploymentServer;
    }

    /**
     * Sets the value of the deploymentServer property.
     *
     * @param pDeploymentServer parameter to set.
     */
    public void setDeploymentServer(DeploymentServer pDeploymentServer) {
        mDeploymentServer = pDeploymentServer;
    }

    /**
     * Authenticate the previous user
     */
    private void unassumeUserIdentity() {
        ThreadSecurityManager.setThreadUser(mPreviousUser);
        mPreviousUser = null;
        mCurrentUser = null;
    }

    /**
     * Authenticate the specified user in current session
     *
     * @param pUser user to authenticate
     */
    protected void assumeUserIdentity(String pUser) {
        if (isLoggingDebug()) {
            logDebug("Start assumeUserIdentity for : " + pUser!=null?pUser:"null");
        }
        mCurrentUser = new User();

        Persona adminPersona = getUserAuthority().getPersona("Profile$login$" + pUser);

        if (adminPersona == null) {
            if (isLoggingDebug()) {
                logDebug("adminPersona is null");
            }
            return;
        }

        mCurrentUser.addPersona(adminPersona);
        mPreviousUser = ThreadSecurityManager.currentUser();
        ThreadSecurityManager.setThreadUser(mCurrentUser);
        if (isLoggingDebug()) {
            logDebug("Stop assumeUserIdentity for : " + pUser!=null?pUser:"null");
        }
    }

    /**
     * Returns userAuthority property.
     *
     * @return userAuthority property.
     */
    public UserDirectoryUserAuthority getUserAuthority() {
        return mUserAuthority;
    }

    /**
     * Sets the value of the userAuthority property.
     *
     * @param pUserAuthority parameter to set.
     */
    public void setUserAuthority(UserDirectoryUserAuthority pUserAuthority) {
        mUserAuthority = pUserAuthority;
    }

    /**
     * Returns publishingAdmin property.
     *
     * @return publishingAdmin property.
     */
    public String getPublishingAdmin() {
        return mPublishingAdmin;
    }

    /**
     * Sets the value of the publishingAdmin property.
     *
     * @param pPublishingAdmin parameter to set.
     */
    public void setPublishingAdmin(String pPublishingAdmin) {
        mPublishingAdmin = pPublishingAdmin;
    }

    public String getRepoCacheInvalidationServicePath() {
        return mRepoCacheInvalidationServicePath;
    }

    public void setRepoCacheInvalidationServicePath(
            String pRepoCacheInvalidationServicePath) {
        mRepoCacheInvalidationServicePath = pRepoCacheInvalidationServicePath;
    }
}
