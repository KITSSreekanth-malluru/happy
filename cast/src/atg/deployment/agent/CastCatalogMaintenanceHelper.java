package atg.deployment.agent;

import atg.deployment.common.event.DeploymentEvent;
import atg.deployment.agent.CatalogMaintenanceHelper;

/**
 * Castorama extension of CatalogMaintenanceHelper class.
 *
 * @author EPAM team
 */
public class CastCatalogMaintenanceHelper extends CatalogMaintenanceHelper {
    /**
     * DeploymentEventListener callback.
     *
     * @param pEvent event to handle
     */
    public void deploymentEvent(DeploymentEvent pEvent) {
        if ((getDeploymentAgent() != null) && getDeploymentAgent().isLoggingDebug()) {
            getDeploymentAgent().logDebug(">> DeploymentEvent:" + pEvent.getNewState() +
                                          (pEvent.getNewState() == DeploymentEvent.DONE_APPLY) + " " +
                                          (pEvent.getNewState() == DeploymentEvent.DEPLOYMENT_COMPLETE));
        }
        if (isActive() && (getDeploymentAgent() != null) && (getCatalogMaintenanceService() != null) &&
                (pEvent.getNewState() == DeploymentEvent.DONE_APPLY)) {
            runMaintenanceService(pEvent);
        }
    }
}
