package com.castorama.invite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.ejb.CreateException;

import javax.transaction.TransactionManager;

import atg.adapter.gsa.GSARepository;

import atg.adapter.version.VersionRepository;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.claimable.ClaimableTools;

import atg.deployment.server.DeploymentServer;
import atg.deployment.server.topology.TopologyDef;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.ProjectConstants;

import atg.process.action.ActionConstants;
import atg.process.action.ActionException;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;

import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.scheduler.SingletonSchedulableService;

import atg.userdirectory.UserDirectoryUserAuthority;

import atg.versionmanager.Branch;
import atg.versionmanager.VersionManager;
import atg.versionmanager.WorkingContext;
import atg.versionmanager.Workspace;

import atg.versionmanager.exceptions.VersionException;

import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;

import com.castorama.scenario.CouponIDGenerator;

/**
 * Given job is responsible for generating coupons for both referrer and referee
 * promotions. The job generates coupons only if certain conditions are met. The
 * job performs the following steps:
 *
 * <ol>
 * <li>Creates a project over OneStepDeploymentWorkflow</li>
 * <li>Generates coupons</li>
 * <li>Advances the workflow</li>
 * </ol>
 *
 * @author Andrei_Raichonak
 */
public class CouponPoolObserverJob extends SingletonSchedulableService {
    /** SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFEREE constant. */
    private static final String SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFEREE =
        "SELECT count(*) FROM DCSPP_COUPON WHERE PROMOTION_ID=? AND COUPON_ID NOT IN (SELECT COUPON_ID FROM CASTO_REFEREE)";

    /** SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFERRER constant. */
    private static final String SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFERRER =
        "SELECT count(*) FROM DCSPP_COUPON WHERE PROMOTION_ID=? AND COUPON_ID NOT IN (SELECT REF_COUPON_ID FROM CASTO_REFEREE WHERE REF_COUPON_ID IS NOT NULL)";

    /** MAIN_BRANCH constant. */
    private static final String MAIN_BRANCH = "main";

    /** QUERY_COUPONS_FOR_PROMOTION constant. */
    private static final String QUERY_COUPONS_FOR_PROMOTION = "promotion = ?0";

    /** Process name prefix. */
    public static final String PROCESS_NAME_PREFIX = "Coupons For Referrer Program ";

    /** PROJECT_SUFFIX constant. */
    public static final SimpleDateFormat PROJECT_SUFFIX = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    /** Default publishing user. */
    public static String DEFAULT_PUBLISHING_USER = "internalAdmin";

    /** Referrer Program Configuration component */
    private ReferrerProgramConfig mReferrerProgramConfig;

    /** Version ClaimableRepository where coupons will be created */
    private VersionRepository mClaimableRepository;

    /** ClaimableRepository where coupons will be deployed to */
    private Repository mProductionClaimableRepository;

    /** ClaimableTools */
    private ClaimableTools mClaimableTools;

    /** ClaimableManager */
    private ClaimableManager mClaimableManager;

    /** Transaction Manager */
    private TransactionManager mTransactionManager;

    /** Project Workflow to use for coupon deployment */
    private String mProjectWorkflow = "/Commerce/castorama/OneStepDeploymentWorkflow.wdl";

    /** Logged in user with admin rights. */
    private User mCurrentUser;

    /** Previous logged in user (in running thread) */
    private User mPreviousUser;

    /** UserDirectoryUserAuthority */
    private UserDirectoryUserAuthority mUserAuthority;

    /** WorkflowManager */
    private WorkflowManager mWorkflowManager;

    /** Default publishing user. */
    private String mTaskOutcomeId = "4.1.1";

    /** User with admin rights. */
    private String mPublishingUser = DEFAULT_PUBLISHING_USER;
    
    /** DeploymentServer property */
    private DeploymentServer mDeploymentServer;
    /**
     * Returns workflowManager property.
     *
     * @return workflowManager property.
     */
    public WorkflowManager getWorkflowManager() {
        return mWorkflowManager;
    }

    /**
     * Sets the value of the workflowManager property.
     *
     * @param pWorkflowManager parameter to set.
     */
    public void setWorkflowManager(WorkflowManager pWorkflowManager) {
        mWorkflowManager = pWorkflowManager;
    }

    /**
     * Returns referrerProgramConfig property.
     *
     * @return referrerProgramConfig property.
     */
    public ReferrerProgramConfig getReferrerProgramConfig() {
        return mReferrerProgramConfig;
    }

    /**
     * Sets the value of the referrerProgramConfig property.
     *
     * @param pReferrerProgramConfig parameter to set.
     */
    public void setReferrerProgramConfig(ReferrerProgramConfig pReferrerProgramConfig) {
        mReferrerProgramConfig = pReferrerProgramConfig;
    }

    /**
     * Returns claimableRepository property.
     *
     * @return claimableRepository property.
     */
    public VersionRepository getClaimableRepository() {
        return mClaimableRepository;
    }

    /**
     * Sets the value of the claimableRepository property.
     *
     * @param pClaimableRepository parameter to set.
     */
    public void setClaimableRepository(VersionRepository pClaimableRepository) {
        mClaimableRepository = pClaimableRepository;
    }

    /**
     * Returns claimableTools property.
     *
     * @return claimableTools property.
     */
    public ClaimableTools getClaimableTools() {
        return mClaimableTools;
    }

    /**
     * Sets the value of the claimableTools property.
     *
     * @param pClaimableTools parameter to set.
     */
    public void setClaimableTools(ClaimableTools pClaimableTools) {
        mClaimableTools = pClaimableTools;
    }

    /**
     * Returns claimableManager property.
     *
     * @return claimableManager property.
     */
    public ClaimableManager getClaimableManager() {
        return mClaimableManager;
    }

    /**
     * Sets the value of the claimableManager property.
     *
     * @param pClaimableManager parameter to set.
     */
    public void setClaimableManager(ClaimableManager pClaimableManager) {
        mClaimableManager = pClaimableManager;
    }

    /**
     * Returns transactionManager property.
     *
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return mTransactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     *
     * @param pTransactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager pTransactionManager) {
        mTransactionManager = pTransactionManager;
    }

    /**
     * Returns projectWorkflow property.
     *
     * @return projectWorkflow property.
     */
    public String getProjectWorkflow() {
        return mProjectWorkflow;
    }

    /**
     * Sets the value of the projectWorkflow property.
     *
     * @param pProjectWorkflow parameter to set.
     */
    public void setProjectWorkflow(String pProjectWorkflow) {
        mProjectWorkflow = pProjectWorkflow;
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
     * Returns productionClaimableRepository property.
     *
     * @return productionClaimableRepository property.
     */
    public Repository getProductionClaimableRepository() {
        return mProductionClaimableRepository;
    }

    /**
     * Sets the value of the productionClaimableRepository property.
     *
     * @param pProductionClaimableRepository parameter to set.
     */
    public void setProductionClaimableRepository(Repository pProductionClaimableRepository) {
        mProductionClaimableRepository = pProductionClaimableRepository;
    }

    /**
     * Returns taskOutcomeId property.
     *
     * @return taskOutcomeId property.
     */
    public String getTaskOutcomeId() {
        return mTaskOutcomeId;
    }

    /**
     * Sets the value of the taskOutcomeId property.
     *
     * @param pTaskOutcomeId parameter to set.
     */
    public void setTaskOutcomeId(String pTaskOutcomeId) {
        mTaskOutcomeId = pTaskOutcomeId;
    }

    /**
     * Performs the job.
     *
     * @param pScheduler parameter
     * @param pJob       parameter
     */
    public void doScheduledTask(Scheduler pScheduler, ScheduledJob pJob) {
        if (isLoggingDebug()) {
            logDebug("Entering doScheduledTask");
        }
       
        try {
        	//need to login to access the topology info
        	assumeUserIdentity(getPublishingUser());
        	TopologyDef topology = getDeploymentServer().getTopologyManager().getPrimaryTopology();
            if (topology == null) {
            	if (isLoggingError()) {
                    logError("No Primary Topology configured. Exit.");
                }
                return;
            }
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Error getting Primary Topology. Perhaps initial deployment has not been completed yet. Exit.", e);
			}
			return;
		} finally {
			//don't forget to log off
			unassumeUserIdentity();
		}
        
        
        RepositoryItem refereePromo = getReferrerProgramConfig().getRefereePromotion();
        RepositoryItem referrerPromo = getReferrerProgramConfig().getReferrerPromotion();

        if ((refereePromo == null) || (referrerPromo == null)) {
            if (isLoggingDebug()) {
                logDebug("Exiting doScheduledTask. No promotions configured.");
            }
            return;
        }

        int refereeNumberToGenerate = 0;
        int referrerNumberToGenerate = 0;

        try {
            refereeNumberToGenerate = checkPromotion(refereePromo, SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFEREE);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (ClaimableException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }

        try {
            referrerNumberToGenerate = checkPromotion(referrerPromo, SQL_GET_NUMBER_OF_NOT_ASSIGNED_COUPONS_REFERRER);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (ClaimableException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }

        if ((referrerNumberToGenerate == 0) && (refereeNumberToGenerate == 0)) {
            if (isLoggingDebug()) {
                logDebug("Exiting doScheduledTask. No need to generate coupons.");
            }
            return;
        }

        boolean success = false;
        String workspaceId = null;
        TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
        Process process = null;

        try {
            process = createProcess();
            workspaceId = process.getProject().getWorkspace();

            Workspace ws = getWorkspace(workspaceId);
            WorkingContext.pushDevelopmentLine(ws);

            if (isLoggingDebug()) {
                logDebug("Beginning generating");
            }

            transactionDemarcation.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

            if (refereeNumberToGenerate > 0) {
                generatesCoupons(refereePromo, refereeNumberToGenerate);
            }

            if (referrerNumberToGenerate > 0) {
                generatesCoupons(referrerPromo, referrerNumberToGenerate);
            }

            success = true;
        } catch (TransactionDemarcationException e) {
            if (isLoggingError()) {
                logError(e);
            }
        } catch (Exception ex) {
            try {
                transactionDemarcation.getTransaction().setRollbackOnly();
            } catch (Exception e) {
            }

            if (isLoggingError()) {
                logError(ex);
            }
        } finally {
            try {
                transactionDemarcation.end();
            } catch (TransactionDemarcationException e1) {
            }

            if (success) {
                if (process != null) {
                    try {
                        advanceWorkflow(process);
                    } catch (WorkflowException e) {
                        if (isLoggingError()) {
                            logError(e);
                        }

                        if (isLoggingDebug()) {
                            logDebug("Deleting project");
                        }

                        deleteProject(process);
                    } catch (ActionException e) {
                        if (isLoggingError()) {
                            logError(e);
                        }

                        if (isLoggingDebug()) {
                            logDebug("Deleting project");
                        }

                        deleteProject(process);
                    }  // end try-catch
                }  // end if
            } else {
                if (process != null) {
                    if (isLoggingDebug()) {
                        logDebug("Deleting project");
                    }

                    deleteProject(process);
                }
            }  // end if-else

            WorkingContext.popDevelopmentLine();
            unassumeUserIdentity();
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Exiting doScheduledTask");
        }
    }

    /**
     * Returns number of coupons to generate
     *
     * @param  pPromo a promotion to generate coupons for
     * @param  pSql   SQL to execute
     *
     * @return number of coupons to generate
     *
     * @throws RepositoryException propagate up if any
     * @throws ClaimableException  propagate up if any
     */
    protected int checkPromotion(RepositoryItem pPromo, String pSql) throws RepositoryException, ClaimableException {
        if (isLoggingDebug()) {
            logDebug("Checking promotion " + pPromo);
        }

        RepositoryItemDescriptor itemDescriptor =
            mProductionClaimableRepository.getItemDescriptor("PromotionClaimable");
        RepositoryView repositoryView = itemDescriptor.getRepositoryView();
        RqlStatement statement = null;

        // get total number of coupons
        statement = RqlStatement.parseRqlStatement(QUERY_COUPONS_FOR_PROMOTION);

        int totalCount = statement.executeCountQuery(repositoryView, new Object[] {pPromo});

        if (isLoggingDebug()) {
            logDebug("Total number of promotion " + totalCount);
        }

        if ((mReferrerProgramConfig.getNumberOfCouponsToGeneratePerPromotion() != -1) &&
                (totalCount >= mReferrerProgramConfig.getNumberOfCouponsToGeneratePerPromotion())) {
            if (isLoggingDebug()) {
                logDebug("NumberOfCouponsToGeneratePerPromotion exceeded. Skiping generation.");
            }

            return 0;
        }
        // get total number of not assigned coupons

        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        int count = 0;

        try {
            connection = ((GSARepository) mProductionClaimableRepository).getDataSource().getConnection();

            ps = connection.prepareStatement(pSql);
            ps.setString(1, pPromo.getRepositoryId());
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (Exception e) {
            throw new RepositoryException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }  // end try-catch-finally

        if (isLoggingDebug()) {
            logDebug("Total number of not assigned promotion " + count);
        }

        if (count <= mReferrerProgramConfig.getThreshold()) {
            int toGenerate =
                Math.min(mReferrerProgramConfig.getNumberOfCouponsToGeneratePerJob(),
                         mReferrerProgramConfig.getNumberOfCouponsToGeneratePerPromotion() - totalCount);

            return toGenerate;
        }

        return 0;
    }

    /**
     * Generates coupons
     *
     * @param  pPromo           a promotion to generate coupons for
     * @param  pNumberOfCoupons number of coupons to generate
     *
     * @throws ClaimableException propagate up if any
     */
    protected void generatesCoupons(RepositoryItem pPromo, int pNumberOfCoupons) throws ClaimableException {
        if (isLoggingDebug()) {
            logDebug("Generating " + pNumberOfCoupons + " coupon(s) for " + pPromo);
        }

        try {
            for (int i = 0; i < pNumberOfCoupons; i++) {
                String couponId = CouponIDGenerator.generate();
                MutableRepositoryItem coupon =
                    ((MutableRepository) mClaimableManager.getClaimableTools().getClaimableRepository()).createItem(couponId,
                                                                                                                    mClaimableManager
                                                                                                                    .getClaimableTools()
                                                                                                                    .getCouponItemDescriptorName());
                DynamicBeans.setPropertyValue(coupon, mClaimableManager.getClaimableTools().getPromotionPropertyName(),
                                              pPromo);
                ((MutableRepository) mClaimableManager.getClaimableTools().getClaimableRepository()).addItem(coupon);
            }

        } catch (RepositoryException e) {
            throw new ClaimableException(e);
        } catch (PropertyNotFoundException e) {
            throw new ClaimableException(e);
        }
    }

    /**
     * Create a process.
     *
     * @return instance of Process
     *
     * @throws ActionException                 propagate if an error occurs
     * @throws CreateException                 propagate if an error occurs
     * @throws TransactionDemarcationException propagate if an error occurs
     * @throws WorkflowException               propagate if an error occurs
     */
    protected Process createProcess() throws ActionException, CreateException, TransactionDemarcationException,
                                             WorkflowException {
        assumeUserIdentity(getPublishingUser());

        ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();

        String processName = PROCESS_NAME_PREFIX + PROJECT_SUFFIX.format(new Date());

        if (isLoggingDebug()) {
            logDebug("Creating process: " + processName);
        }

        return processHome.createProcessForImport(processName, getProjectWorkflow());
    }

    /**
     * Find a workspace by id
     *
     * @param  pWorkspaceId workspace id
     *
     * @throws RepositoryException propagate if an error occurs
     *
     * @return instance of Workspace
     */
    protected Workspace getWorkspace(String pWorkspaceId) throws RepositoryException {
        if (pWorkspaceId == null) {
            throw new RepositoryException("Error: workspaceId is missed");
        }

        VersionManager vm = getClaimableRepository().getVersionManager();

        if (vm == null) {
            throw new RepositoryException("Error: VersionManager is not specified");
        }

        Workspace ws = null;

        try {
            ws = vm.getWorkspaceByName(pWorkspaceId);
        } catch (VersionException ve) {
            throw new RepositoryException("Cannot retrieve workspace with id " + pWorkspaceId, ve);
        }

        if ((ws != null) && ws.isCheckedIn()) {
            throw new RepositoryException("Cannot use checked-in workspace: " + ws.getName());
        }

        if (ws != null) {
            return ws;
        }

        Branch parent = null;

        try {
            parent = vm.getBranchByName(MAIN_BRANCH);
        } catch (VersionException ve) {
            throw new RepositoryException("Error: invalid branch" + ve);
        }

        if (parent == null) {
            throw new RepositoryException("Branch is not specified");
        }

        try {
            ws = vm.createWorkspace(pWorkspaceId, parent);
        } catch (VersionException ve) {
            throw new RepositoryException(ve);
        }

        return ws;
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
     * Advances workflow
     *
     * @param  pProcess DOCUMENT ME!
     *
     * @throws WorkflowException DOCUMENT ME!
     * @throws ActionException   DOCUMENT ME!
     */
    protected void advanceWorkflow(Process pProcess) throws WorkflowException, ActionException {
        RepositoryItem processWorkflow = pProcess.getProject().getWorkflow();
        String workflowProcessName = processWorkflow.getPropertyValue("processName").toString();
        String subjectId = pProcess.getId();

        try {
            WorkflowView wv = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());

            wv.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT, subjectId,
                               getTaskOutcomeId(), ActionConstants.ERROR_RESPONSE_DEFAULT);

        } catch (MissingWorkflowDescriptionException e) {
            if (isLoggingError()) {
                logError("Advance Workflow Failed");
            }

            throw e;
        } catch (ActorAccessException e) {
            if (isLoggingError()) {
                logError("Advance Workflow Failed");
            }

            throw e;
        } catch (ActionException e) {
            if (isLoggingError()) {
                logError("Advance Workflow Failed");
            }

            throw e;
        } catch (UnsupportedOperationException e) {
            if (isLoggingError()) {
                logError("Advance Workflow Failed");
            }

            throw e;
        }  // end try-catch
    }

    /**
     * Delete the specifies project
     *
     * @param pProcess DOCUMENT ME!
     */
    protected void deleteProject(Process pProcess) {
        if (pProcess.getProject() != null) {
            try {
                // pProcess.getProject().delete(mCurrentUser.toString());
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns publishingUser property.
     *
     * @return publishingUser property.
     */
    public String getPublishingUser() {
        return mPublishingUser;
    }

    /**
     * Sets the value of the publishingUser property.
     *
     * @param pPublishingUser parameter to set.
     */
    public void setPublishingUser(String pPublishingUser) {
        mPublishingUser = pPublishingUser;
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
