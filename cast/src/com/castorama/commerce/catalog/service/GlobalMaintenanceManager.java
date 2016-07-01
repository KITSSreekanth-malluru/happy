package com.castorama.commerce.catalog.service;

import java.rmi.Naming;
import java.util.List;
import java.util.Set;

import atg.deployment.common.DeploymentException;
import atg.deployment.server.DeploymentServer;
import atg.deployment.server.topology.AgentDef;
import atg.deployment.server.topology.TargetDef;
import atg.deployment.server.topology.TopologyDef;
import atg.deployment.server.topology.TransportDef;
import atg.deployment.server.topology.TransportTypeEnum;
import atg.nucleus.GenericService;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.userdirectory.UserDirectoryUserAuthority;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class GlobalMaintenanceManager extends GenericService {
    /** CONTROLLER_NUCLEUS_PATH property */
    private static final String CONTROLLER_NUCLEUS_PATH =
        "/com/castorama/catalog/service/RemoteMaintenanceControllerService";

    /**
     * ToDo: DOCUMENT ME!
     *
     * @author  EPAM team
     * @version ToDo: INSERT VERSION NUMBER
     */
    public static enum MaintenanceState {
        ON, OFF
    }

    /** Logged in user with admin rights. */
    private User mCurrentUser;

    /** Previous logged in user (in running thread) */
    private User mPreviousUser;

    /** UserDirectoryUserAuthority */
    private UserDirectoryUserAuthority mUserAuthority;

    /** mPublishingAdmin property */
    private String mPublishingAdmin = "internalAdmin";

    /** mDeploymentServer property */
    private DeploymentServer mDeploymentServer;

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pState ToDo: DOCUMENT ME!
     *
     * @throws DeploymentException ToDo: DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    public void setMaintenanceState(MaintenanceState pState) throws DeploymentException {
        try {
            // need to login to access the topology info
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
                            RemoteMaintenanceControllerService mcs =
                                (RemoteMaintenanceControllerService) Naming.lookup(hostAddress +
                                                                                   CONTROLLER_NUCLEUS_PATH);

                            if (pState == MaintenanceState.ON) {
                                mcs.turnOn();
                                if (isLoggingDebug()) {
                                    logDebug("Maintenance has been turned on");
                                }
                            } else {
                                mcs.turnOff();
                                if (isLoggingDebug()) {
                                    logDebug("Maintenance has been turned off");
                                }
                            }
                        } catch (Exception e) {
                            if (isLoggingError()) {
                                logError(e);
                            }
                        }  // end try-catch
                    }  // end if
                }  // end for
            }  // end for
        } finally {
            unassumeUserIdentity();
        }  // end try-finally

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
        mCurrentUser = new User();

        Persona adminPersona = getUserAuthority().getPersona("Profile$login$" + pUser);

        if (adminPersona == null) {
            return;
        }

        mCurrentUser.addPersona(adminPersona);
        mPreviousUser = ThreadSecurityManager.currentUser();
        ThreadSecurityManager.setThreadUser(mCurrentUser);
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
}
