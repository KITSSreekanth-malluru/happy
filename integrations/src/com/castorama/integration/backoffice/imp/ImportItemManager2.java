package com.castorama.integration.backoffice.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.transaction.SystemException;

import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.inventory.InventoryManager;
import atg.core.util.StringUtils;
import atg.deployment.common.DeploymentException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.epub.project.Process;
import atg.epub.project.ProcessEnumStatus;
import atg.epub.project.ProcessHome;
import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectEnumStatus;
import atg.nucleus.DynamoEnv;
import atg.nucleus.ServiceException;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.security.Persona;
import atg.security.ThreadSecurityManager;
import atg.security.User;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.userdirectory.UserDirectoryUserAuthority;
import atg.versionmanager.VersionManagerURI;
import atg.versionmanager.WorkingVersion;
import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.cache.InventoryCacheInvalidationGlobalNotifier;
import com.castorama.commerce.catalog.service.GlobalMaintenanceManager;
import com.castorama.commerce.catalog.service.GlobalMaintenanceManager.MaintenanceState;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import com.castorama.integration.xml.ImportFileParser;
import com.castorama.integration.xml.ImportItem;

public class ImportItemManager2 extends IntegrationBase implements Schedulable {
	private static int CODE_SUCCESS = 0;
	private static int CODE_NOT_UPLOAD = -200;
	private static int CODE_INVENTORY_FAILED = -100;
	private static int CODE_DEPLOY_FAILED = -500;
	
	private static final String PROJECT_STATE_SUCCESS = "Success";
	private static final String PROJECT_STATE_INVENTORY_FAILED = "Inventory Upload failed";
	private static final String PROJECT_STATE_DEPLOY_FAILED = "Deployment failed";
	private static final String PROJECT_STATE_UPLOAD_FAILED = "Upload in BCC failed";
	
	private static final String DEFAULT_PROJECT_NAME = "Item Flow";
	private static final String DEFAULT_USER_NAME = "internalAdmin";
	private static final String DEFAULT_COMMENT = "Item Flow";
	private static final int DEFAULT_TRANSACTION_TIMEOUT = 10800;
	private static final String DEFAULT_DEPLOYMENT_WORKFLOW = "/Commerce/castorama/OneStepDeploymentWorkflow.wdl";

	private static final String PRICE_LIST_REPOSITORY = "/atg/commerce/pricing/priceLists/PriceLists";
	private static final String PROP_DATE_INIT_DESTOCK = "dateInitDestock";
	private static final String PROP_DATE_DEB_DESTOCK = "dateDebDestock";
	private static final String PROP_DATE_FIN_DESTOCK = "dateFinDestock";

	private static final String PROP_LIST_PRICE = "listPrice";
	private static final String PROP_PRIX_SOLDE = "PrixSolde";
	private static final String PROP_FSOLDE = "FSolde";

	private static final String PREFIX_LIST_PRICE = "lpx";
	private static final String PREFIX_SALE_PRICE = "spx";
	private static final String PREFIX_INVENTORY = "inv";

	private static SimpleDateFormat dateFormatDestock = new SimpleDateFormat("MM/dd/yyyy");

	private static final String TAG_ADD_ITEM = "<" + TemplateParser.TAG_ADD_ITEM;
	private static final String UPDATE_INVETORY_NAME = "UPDATE_SKUS_INVENTORY_";
	
	private static final int MAX_ITEMS = 5000;
	//private static final int MAX_PROPS_IN_PROJECT = 3;

	private static final String DESCRIPTION_NOT_FOUND = "Description not found SKU_ID = ";

	private File workingDir;
	private boolean isStartManager = false;

	private String projectName;
	private String userName;
	private String comment;
	private WorkflowManager mWorkflowManager;
	private String deploymentWorkflow;
	private Repository[] inventoryRepositories;
	private InventoryManager inventoryManager;
	private Properties availabilityStatusMap;
	private InventoryCacheInvalidationGlobalNotifier mInventoryCacheInvalidationGlobalNotifier;
	private boolean mInvalidateInventoryCache;

	private String workingProjectName;
	
	private boolean filterData;

	private Repository productProductionRepository;

	/** UserDirectoryUserAuthority */
	private UserDirectoryUserAuthority mUserAuthority;

	/** Logged in user with admin rights. */
	private User mCurrentUser;

	/** Previous logged in user (in running thread) */
	private User mPreviousUser;

	/** list of item descriptors to auto-merge */
	private List<String> mConflictSensitiveItems = new ArrayList<String>();

	/** seconds to wait for deployment to complete */
	private int mWaitForCompleteTimeoutInSeconds = 60 * 60 * 4; // 4 hours

	/** mGlobalMaintenanceManager property */
	private GlobalMaintenanceManager mGlobalMaintenanceManager;

	public List<String> getConflictSensitiveItems() {
		return mConflictSensitiveItems;
	}

	public void setConflictSensitiveItems(List<String> pConflictSensitiveItems) {
		mConflictSensitiveItems = pConflictSensitiveItems;
	}

	public int getWaitForCompleteTimeoutInSeconds() {
		return mWaitForCompleteTimeoutInSeconds;
	}

	public void setWaitForCompleteTimeoutInSeconds(int pWaitForCompleteTimeoutInSeconds) {
		mWaitForCompleteTimeoutInSeconds = pWaitForCompleteTimeoutInSeconds;
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
	 * @param pUserAuthority
	 *            parameter to set.
	 */
	public void setUserAuthority(UserDirectoryUserAuthority pUserAuthority) {
		mUserAuthority = pUserAuthority;
	}

	public Properties getAvailabilityStatusMap() {
		return availabilityStatusMap;
	}

	public void setAvailabilityStatusMap(Properties availabilityStatusMap) {
		this.availabilityStatusMap = availabilityStatusMap;
	}

	/**
	 * Returns globalMaintenanceManager property.
	 * 
	 * @return globalMaintenanceManager property.
	 */
	public GlobalMaintenanceManager getGlobalMaintenanceManager() {
		return mGlobalMaintenanceManager;
	}

	/**
	 * Sets the value of the globalMaintenanceManager property.
	 * 
	 * @param pGlobalMaintenanceManager
	 *            parameter to set.
	 */
	public void setGlobalMaintenanceManager(GlobalMaintenanceManager pGlobalMaintenanceManager) {
		mGlobalMaintenanceManager = pGlobalMaintenanceManager;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public Repository getProductProductionRepository() {
		return productProductionRepository;
	}

	public void setProductProductionRepository(Repository productProductionRepository) {
		this.productProductionRepository = productProductionRepository;
	}

	/**
	 * Get Inventory repository
	 * 
	 * @return inventory production repository
	 */
	public Repository[] getInventoryRepositories() {
		return inventoryRepositories;
	}

	/**
	 * Set inventory repository
	 * 
	 * @param inventoryRepository
	 */
	public void setInventoryRepositories(Repository[] inventoryRepositories) {
		this.inventoryRepositories = inventoryRepositories;
	}

	/**
	 * The BCC project name
	 * 
	 * @return project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Set BCC project name
	 * 
	 * @param projectName
	 *            the project name
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * The user name
	 * 
	 * @return user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set user name
	 * 
	 * @param userName
	 *            user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * The comment
	 * 
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Set comment
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public WorkflowManager getWorkflowManager() {
		return mWorkflowManager;
	}

	public void setWorkflowManager(WorkflowManager pWorkflowManager) {
		mWorkflowManager = pWorkflowManager;
	}

	public String getDeploymentWorkflow() {
		return deploymentWorkflow;
	}

	public void setDeploymentWorkflow(String deploymentWorkflow) {
		this.deploymentWorkflow = deploymentWorkflow;
	}

	public boolean isFilterData() {
		return filterData;
	}

	public void setFilterData(boolean filterData) {
		this.filterData = filterData;
	}
	
	// Scheduler property
	private Scheduler scheduler;

	/**
	 * This service's scheduler
	 * 
	 * @return this service's scheduler
	 */
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	/**
	 * @param scheduler
	 *            this service's scheduler
	 */
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	// Schedule property
	private Schedule schedule;

	/**
	 * The schedule this service will run on
	 * 
	 * @return the schedule this service will run on
	 */
	public Schedule getSchedule() {
		return this.schedule;
	}

	/**
	 * @param schedule
	 *            the schedule this service will run on
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * This method is invoked according to the schedule.
	 * 
	 * @param scheduler
	 *            Scheduler object to perform the task.
	 * @param job
	 *            ScheduledJob object to be performed as a task.
	 */
	public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
		try {
			uploadFiles();
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
	}

	public ImportItemManager2() {
	}

	/**
	 * Upload files in repository
	 */
	public void uploadFiles() throws FileNotFoundException {
		if (isStartManager) {
			if (isLoggingInfo()) {
				logInfo("ImportItemManager2 is already working.");
			}
			return;
		}

		if (isLoggingInfo()) {
			logInfo("start - ImportItemManager2 - uploadFiles");
		}
		try {
			isStartManager = true;
			
			uploadFlowFiles();
			
		} finally {
			isStartManager = false;
		}
		if (isLoggingInfo()) {
			logInfo("finish - ImportItemManager2 - uploadFiles");
		}
	}

	private void uploadFlowFiles() throws FileNotFoundException {
		File[] sourceFiles = null;
		try {
			sourceFiles = getUploadFiles(null);
			if (sourceFiles == null || sourceFiles.length == 0) {
				if (isLoggingDebug()) {
					logDebug("Files for upload not found. " + getRootDir() + File.separator + Constants.INPUT_FOLDER);
				}
				return;
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
				return;
			}
		}

		long start = System.currentTimeMillis();

		File workBase = getWorkingDir();
		workingDir = new File(workBase, Long.toString(new Date().getTime()));
		workingDir.mkdir();

		checkMandatoryFolders();

		checkProperties();

		File tmpDir = null;
		PrintWriter logWriter = null;
		File statusFile = null;

		try {

			for (File file : sourceFiles) {
				File workingFile = getWorkingFile(file, workingDir);
				if (isLoggingDebug()) {
					logDebug("processing: " + workingFile);
				}
				if (workingFile == null) {
					continue;
				}

				statusFile = new File(workingDir, getFileName(workingFile) + ".log");
				logWriter = new PrintWriter(statusFile);
				logWriter.println("Data file: " + workingFile.getName());

				try {
					List<List<ChunkProperties>> chunks = parseXml(workingFile);

					List<ChunkProperties> updateSkuItems = chunks.get(0);
					List<ChunkProperties> addSkuItems = chunks.get(1);;

					prepareUploadFiles(updateSkuItems, true, true);
					prepareUploadFiles(addSkuItems, false, false);

					List<ProjectProperties> updateProjects = generateProjects(updateSkuItems);
					List<ProjectProperties> addProjects = generateProjects(addSkuItems);
					
					assumeUserIdentity(getUserName());
					
					int res1 = uploadItems(updateProjects, logWriter, true);
					int res2 = uploadItems(addProjects, logWriter, false);

					unassumeUserIdentity();

					long elapsed = System.currentTimeMillis() - start;
					logWriter.println("Import items: " + (elapsed / 60000) + " min(s)");
					
					if ((res1 == CODE_SUCCESS || res1 == CODE_NOT_UPLOAD) 
							&& (res2 == CODE_SUCCESS || res2 == CODE_NOT_UPLOAD)) {
						logWriter.flush();
						logWriter.close();
						workingFile.delete();
						tmpDir = getArchiveDir();
					} else {
						logWriter.flush();
						logWriter.close();
						tmpDir = getErrorDir();
						moveFileIn(workingFile, tmpDir);
					}
					moveFileIn(statusFile, tmpDir);

				} catch (Exception e) {
					if (isLoggingError()) {
						logError("Exception occured: ", e);
					}
					e.printStackTrace(logWriter);
					logWriter.flush();
					logWriter.close();
					moveFileIn(workingFile, getErrorDir());
					moveFileIn(statusFile, getErrorDir());
				}
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		} finally {
			if (tmpDir == null && logWriter != null) {
				logWriter.flush();
				logWriter.close();
				try {
					moveFileIn(statusFile, getErrorDir());
				} catch (Exception e) {
				}
			}
			MiscUtils.deleteFolder(workingDir);
		}
	}

	private List<ProjectProperties> generateProjects(List<ChunkProperties> chunks) {
		List<ProjectProperties> projects = new ArrayList<ProjectProperties>();

		ProjectProperties project = null;
		for (ChunkProperties chunk : chunks) {
			project = new ProjectProperties();
			if (chunk.isOnlyInventory()) {
				project.setOnlyInventory(true);
			}
			project.addChunk(chunk);
			projects.add(project);
		}

		return projects;
	}

	private void prepareUploadFiles(List<ChunkProperties> chunks, boolean isUpdate, boolean checkProps) throws Exception {
		if (isLoggingDebug()) {
			logDebug(">> prepareUploadFiles: update: " + isUpdate + " checkProps=" + checkProps);
		}
		String prefix = isUpdate ? "update_" : "add_";
		
		ImportFileParser parser = null;
		ImportItem[] impItems = null;
		int idx = 1;
		for (ChunkProperties chunk : chunks) {
			parser = new ImportFileParser(ImportFileParser.PHASE_ADD_UPDATE,
					new File(workingDir, chunk.getSkuFileName()));

			impItems = parser.parseFile();
			if (impItems != null && impItems.length > 0) {
				String prx = prefix + idx;
				chunk.setCountItems(impItems.length);
				chunk.setInventoryFileName(generateInventoryXml(impItems, prx, checkProps));
				if (!chunk.isOnlyInventory()) {
					chunk.setPriceFileName(generatePriceListXml(impItems, prx, checkProps));
					if (!isUpdate) {
						chunk.setProductFileName(generateProductListXml(impItems, prx));
					}
				}
			} else {
				chunk.setCountItems(0);
			}
			idx++;
		}

		parser = null;
		impItems = null;
		System.gc();

		if (isLoggingDebug()) {
			logDebug("<< prepareUploadFiles");
		}
	}

	private int uploadItems(List<ProjectProperties> projects, PrintWriter log, boolean isUpdate) {
		int result = 0;
		if (projects.isEmpty()) {
			if (isLoggingDebug()) {
				logDebug("No projects for upload");
			}

			return result;
		}

		int idx = 1;
		Process mProcess = null; 
		String mDeployProjectName = null;
		
		for (ProjectProperties project : projects) {

			if (mProcess != null && mDeployProjectName != null) {
				String status = isProjectDeployed(mProcess) ? PROJECT_STATE_SUCCESS : PROJECT_STATE_DEPLOY_FAILED;
				log.println("Deploy project " + mDeployProjectName + ", status: " + status);
				log.flush();
			}

			int res = uploadProjectItems(project, log, isUpdate, idx);
			mDeployProjectName = null;
			mProcess = null;
			if (res != 0) {
				result = res;
			} else {
				if (!project.isOnlyInventory() && project.getState() == CODE_SUCCESS) {
					try {
						mProcess = find(project.getProjectName());
						deployProject(project, mProcess);
						mDeployProjectName = project.getProjectName();
						try {
							waitForProjectToComplete(mProcess);
						} catch (InterruptedException e) {
							if (isLoggingError()) {
								logError(e);
							}
							e.printStackTrace(log);
						} catch (atg.core.exception.TimeoutException e) {
							if (isLoggingError()) {
								logError(e);
							}
							e.printStackTrace(log);
						}
					} catch (Exception e) {
						if (isLoggingError()) {
							logError("Deploy Project Failed:" + e);
						}
					}
				}
			}
			idx++;
		}
		
		if (mProcess != null && mDeployProjectName != null) {
			String status = isProjectDeployed(mProcess) ? PROJECT_STATE_SUCCESS : PROJECT_STATE_DEPLOY_FAILED;
			log.println("Deploy project " + mDeployProjectName + ", status: " + status);
			log.flush();
		}
		
		return result;
	}

	private int uploadProjectItems(ProjectProperties project, PrintWriter log, boolean isUpdate, int index) {
		if (isLoggingDebug()) {
			logDebug(">> uploadProjectItems " );
		}
		long start = System.currentTimeMillis();
		int result = CODE_NOT_UPLOAD;
		String mProjectName = null;
		try {
			List<String> listInv = new ArrayList<String>();
			if (!project.isOnlyInventory()) {
				mProjectName = new StringBuilder().append(this.workingProjectName).append(
						isUpdate ? " (Modify " : " (New ").append(index).append(") ").append(
						Constants.DATE_FORMAT_PROJECT.format(new Date())).toString();

				project.setProjectName(mProjectName);
				log.println();
				log.println("Project name: " + mProjectName);

				int countSkus = 0;
				List<ChunkProperties> chunks = project.getChunks();
				List<String> list = new ArrayList<String>();
				for (ChunkProperties chunk : chunks) {
					countSkus += chunk.getCountItems();
					if (chunk.getSkuFileName() != null) {
						list.add(chunk.getSkuFileName());
					}
					if (!isUpdate) {
						if (chunk.getProductFileName() != null) {
							list.add(chunk.getProductFileName());
						}
					}
					if (chunk.getPriceFileName() != null) {
						list.add(chunk.getPriceFileName());
					}
					if (chunk.getInventoryFileName() != null) {
						listInv.add(chunk.getInventoryFileName());
					}

					if (isUpdate) {
						log.println("Selected skus for update: " + countSkus);
					} else {
						log.println("Selected skus for addition: " + countSkus);
					}
					if (countSkus > 0) {
						result = uploadXml(list.toArray(new String[0]), log, mProjectName);
					}
				}
			} else {
				if (project.getChunks() != null && project.getChunks().get(0) != null) {
					listInv.add(project.getChunks().get(0).getInventoryFileName());
					log.println("Upload inventory: " + project.getChunks().get(0).getCountItems());
				}
			}

			boolean isInv = true;
			if (result == CODE_SUCCESS || result == CODE_NOT_UPLOAD) {
				for (String invName : listInv) {
					if (invName != null) {
						isInv = uploadInventoryXml(invName, log);
						if (!isInv) {
							break;
						}
					}
				}
			}
			if (!isInv) {
				result = CODE_INVENTORY_FAILED;
				project.setState(result);
			} else {
				project.setState(result);
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
				e.printStackTrace(log);
			}
		}
		long elapsed = System.currentTimeMillis() - start;
		if (!project.isOnlyInventory()) {
			log.println("Upload project: " + (elapsed / 60000) + " min(s); status: " + getStatusProject(result));
		} else {
			log.println("Upload inventory: " + (elapsed / 60000) + " min(s); status: " + getStatusProject(result));
		}
		log.flush();

		System.gc();

		if (isLoggingDebug()) {
			logDebug("<< uploadProjectItems: result =" + result);
		}
		return result;
	}

	private int uploadXml(String[] tmpNames, PrintWriter log, String pProjectName) {
		long start = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug("start - uploadXml: projectName=" + pProjectName);
		}

		List<String> list = new ArrayList<String>();

		String dataDirProp = DynamoEnv.getProperty("atg.dynamo.data-dir");
		String serverNameProp = DynamoEnv.getProperty("atg.dynamo.server.name");
		if (isLoggingDebug()) {
			logDebug("atg.dynamo.data-dir: " + dataDirProp);
			logDebug("atg.dynamo.server.name: " + serverNameProp);
		}

		String configPath = getNucleus().getConfigPath(serverNameProp);

		if (!StringUtils.isBlank(dataDirProp) && !StringUtils.isBlank(serverNameProp)) {
			configPath += File.pathSeparator + dataDirProp + "/servers/" + serverNameProp + "/localconfig";
		}

		String database = ((atg.adapter.gsa.GSARepository) getRepository()).getDatabaseName();
		String repository = ((atg.adapter.gsa.GSARepository) getRepository()).getAbsoluteName();
		if (isLoggingDebug()) {
			logDebug("database: " + database);
			logDebug("repository: " + repository);
			logDebug("Server Name: " + serverNameProp);
		}

		list.add("-database");
		list.add(database);

		list.add("-configPath");
		list.add(configPath);

		list.add("-repository");
		list.add(repository);

		list.add("-project");
		list.add(pProjectName);

		list.add("-user");
		list.add(getUserName());

		list.add("-comment");
		list.add(getComment());

		list.add("-workflow");
		list.add(getDeploymentWorkflow());

		list.add("-startDir");
		list.add(workingDir.getAbsolutePath());

		for (String name : tmpNames) {
			if (name != null) {
				list.add("-import");
				list.add(name);
			}
		}

		if (isLoggingDebug()) {
			logDebug("arguments: " + list);
		}

		int result = -1;
		boolean rollback = true;
		if (isLoggingDebug()) {
			logDebug("getTransactionManager(): " + getTransactionManager());
		}
		TransactionDemarcation trd = new TransactionDemarcation();

		try {
			if (isLoggingDebug()) {
				logDebug("!! getTransaction(): " + trd.getTransaction());
			}
			try {
				getTransactionManager().setTransactionTimeout(getTransactionTimeout());
				trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
				if (isLoggingDebug()) {
					logDebug("isTransactionCreated(): " + trd.isTransactionCreated());
					logDebug("!! getTransaction(): " + trd.getTransaction());
				}

				result = TemplateParser.runParser(list.toArray(new String[0]), new PrintWriter(System.out, true));

				if (isLoggingDebug()) {
					logDebug("TemplateParser.runParser - result=" + result);
				}

				if (result == CODE_SUCCESS) {
					rollback = false;
				}

			} finally {
				try {
					trd.end(rollback);
				} catch (TransactionDemarcationException e) {
					logError("TransactionDemarcationException occured: ", e);
				}
				getTransactionManager().setTransactionTimeout(0);
			}
		} catch (SystemException e) {
			if (isLoggingError()) {
				logError("setTransactionTimeout failed", e);
			}
		} catch (TransactionDemarcationException e) {
			if (isLoggingError()) {
				logError("Creating transaction demarcation failed", e);
			}
		}

		long elapsed = System.currentTimeMillis() - start;
		if (isLoggingDebug()) {
			logDebug("<< finish -  uploadXml: " + (elapsed / 60000) + " min(s)");
		}
		return result;
	}

	protected void turnOn() {
		try {
			getGlobalMaintenanceManager().setMaintenanceState(MaintenanceState.ON);
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	protected void turnOff() {
		try {
			getGlobalMaintenanceManager().setMaintenanceState(MaintenanceState.OFF);
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
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
	 * @param pUser
	 *            user to authenticate
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

	protected void deployProject(ProjectProperties project, Process process) {
		System.gc();

		if (project.getState() == CODE_SUCCESS) {
			if (isLoggingDebug()) {
				logDebug("deployProjects: projectName=" + project.getProjectName());
				logDebug("deployProjects: process=" + process);
			}

			try {
				if (!process.getProject().assetsUpToDate()) {
					Set<WorkingVersion> assets = process.getProject().getAssets();
					for (WorkingVersion workingVersion : assets) {
						if (!workingVersion.isUpToDate()) {
							String itemName = workingVersion.getURI().getURIPart(
									VersionManagerURI.REPOSITORY_ITEM_DESCRIPTOR);
							if (isLoggingDebug()) {
								logDebug("++ itemName: " + itemName);
							}
							if (getConflictSensitiveItems().contains(itemName)) {
								if (isLoggingDebug()) {
									logDebug("++ Found: " + itemName);
								}
								workingVersion.resetParentVersion();
							}
						}
					}
				}
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(e);
				}
			}

			try {
				advanceWorkflow(process);
			} catch (WorkflowException e1) {
				if (isLoggingError()) {
					logError(e1);
				}
			} catch (ActionException e1) {
				if (isLoggingError()) {
					logError(e1);
				}
			}
		}
	}

	protected Process find(String pName) throws WorkflowException, ActionException, EJBException, FinderException {
		if (isLoggingDebug()) {
			logDebug("find process: dispaly name=" + pName);
		}
		ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
		Collection coll = processHome.findByDisplayName(pName);
		if (coll != null && coll.size() > 0) {
			if (isLoggingDebug()) {
				logDebug("find: Process.size()=" + coll.size());
			}
			return (Process) coll.iterator().next();
		}
		return null;
	}

	protected void advanceWorkflow(Process pProcess) throws WorkflowException, ActionException {
		if (isLoggingDebug()) {
			logDebug("advanceWorkflow: Process=" + pProcess);
		}
		RepositoryItem processWorkflow = pProcess.getProject().getWorkflow();
		String workflowProcessName = processWorkflow.getPropertyValue("processName").toString();
		String subjectId = pProcess.getId();

		try {
			WorkflowView wv = getWorkflowManager().getWorkflowView(ThreadSecurityManager.currentUser());

			wv.fireTaskOutcome(workflowProcessName, WorkflowConstants.DEFAULT_WORKFLOW_SEGMENT, subjectId, "4.1.1",
					ActionConstants.ERROR_RESPONSE_DEFAULT);

		} catch (MissingWorkflowDescriptionException e) {
			if (isLoggingError()) {
				logError("SERVICE: Advance Workflow Failed: MissingWorkflowDescriptionException: " + e.getMessage());
			}
			throw e;
		} catch (ActorAccessException e) {
			if (isLoggingError()) {
				logError("SERVICE: Advance Workflow Failed: ActorAccessException:  " + e.getMessage());
			}
			throw e;
		} catch (ActionException e) {
			if (isLoggingError()) {
				logError("SERVICE: Advance Workflow Failed: ActionException:  " + e.getMessage());
			}
			throw e;
		} catch (UnsupportedOperationException e) {
			if (isLoggingError()) {
				logError("SERVICE: Advance Workflow Failed: UnsupportedOperationException:  " + e.getMessage());
			}
			throw e;
		}
	}

	/**
	 * Return true if project is deployed, otherwise - false
	 * 
	 * @param pProcess
	 *            process to examine
	 * @return
	 */
	protected boolean isProjectDeployed(Process pProcess) {
		return pProcess.getProject().getStatus() == ProjectEnumStatus.getCompleted()
				&& !pProcess.getProject().isEditable();
	}

	protected boolean isProcessCompleted(Process pProcess) {
		return pProcess.getStatus().toString().equals(ProcessEnumStatus.getCompleted().toString());
	}

	/**
	 * Return true if project is in error state, otherwise - false
	 * 
	 * @param pProcess
	 *            process to examine
	 * @return
	 */
	protected boolean isProjectFailed(Process pProcess) {
		return pProcess.getProject().getStatus() == ProjectEnumStatus.getError();
	}

	/**
	 * Wait for project deployment to complete.
	 * 
	 * @param pProcess
	 * @throws InterruptedException
	 * @throws atg.core.exception.TimeoutException
	 *             if waiting timeout has been reached
	 */
	protected synchronized void waitForProjectToComplete(Process pProcess) throws InterruptedException,
			atg.core.exception.TimeoutException {
		if (pProcess == null) {
			return;
		}
		
		long startWaiting = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug("waitForProjectToComplete: Entered at " + startWaiting);
			logDebug("pProcess.getStatus()=" + pProcess.getStatus().toString());
			logDebug("equals: " + pProcess.getStatus().toString().equals(ProcessEnumStatus.getCompleted().toString()));
			logDebug("pProcess.getProject().getStatus()=" + pProcess.getProject().getStatus());
		}
		while (!(isProcessCompleted(pProcess) || isProjectFailed(pProcess))) {
			if (((System.currentTimeMillis() - startWaiting) / 1000) > mWaitForCompleteTimeoutInSeconds) {
				if (isLoggingDebug()) {
					logDebug("waitForProjectToComplete: timeout reached at " + System.currentTimeMillis());
				}
				throw new atg.core.exception.TimeoutException();
			}
			wait(5000);
		}
		if (isLoggingDebug()) {
			logDebug("waitForProjectToComplete: end " );
			logDebug("pProcess.getStatus()=" + pProcess.getStatus().toString());
			logDebug("equals: " + pProcess.getStatus().toString().equals(ProcessEnumStatus.getCompleted().toString()));
			logDebug("pProcess.getProject().getStatus()=" + pProcess.getProject().getStatus());
		}
		wait(2000);
	}

	private boolean uploadInventoryXml(String invTmpName, PrintWriter log) {
		if (isLoggingDebug()) {
			logDebug("start - Inventory ");
		}

		if (getInventoryRepositories() == null || getInventoryRepositories().length == 0) {
			if (isLoggingError()) {
				logError("No inventory repositories specified");
			}
			return true;
		}

		boolean result = true;
		File tmpFile = null;
		try {
			tmpFile = new File(workingDir, invTmpName);

			if (isLoggingDebug()) {
				logDebug("upload Inventory: " + tmpFile);
			}

			Repository[] inventoryRepositories = getInventoryRepositories();
			for (Repository repository : inventoryRepositories) {
				result &= uploadInventory(repository, tmpFile, log);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
			result = false;
		}

		if (isLoggingDebug()) {
			logDebug("finish -  uploadInventoryXml: result=" + result);
		}

		if (result && isInvalidateInventoryCache()) {
			try {
				getInventoryCacheInvalidationGlobalNotifier().invalidateRemoteInventoryCaches();
			} catch (Exception e) {
				if (isLoggingError()) {
					logError("Exception occured during inventory cache invalidation: ", e);
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish - uploadInventoryXml: result= " + result);
		}
		return result;
	}

	protected boolean uploadInventory(Repository pInventoryRepository, File pXMLFile, PrintWriter pLog)
			throws FileNotFoundException {
		if (isLoggingDebug()) {
			logDebug("Start uploading inventory for repository - " + pInventoryRepository.getRepositoryName());
		}
		XMLToolsFactory factory = DefaultXMLToolsFactory.getInstance();
		XMLToDOMParser domParser = getXMLToDOMParser(factory);
		TransactionDemarcation trd = new TransactionDemarcation();

		InputStream inputStream = new FileInputStream(pXMLFile);
		boolean rollback = true;
		boolean result = false;
		try {
			getTransactionManager().setTransactionTimeout(getTransactionTimeout());
			trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

			TemplateParser.addToTemplate(inputStream, null, domParser,
					(atg.adapter.gsa.GSARepository) pInventoryRepository, TemplateParser.PARSE_NORMAL, false,
					new PrintWriter(System.out, true), null);

			rollback = false;
			result = true;
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured trying to add/update items", e);
			}
			e.printStackTrace(pLog);
		} finally {
			try {
				inputStream.close();
			} catch (IOException ioe) {
				if (isLoggingError()) {
					logError("IOException occured: ", ioe);
				}
			}
			try {
				trd.end(rollback);
			} catch (TransactionDemarcationException e) {
				logError("TransactionDemarcationException occured: ", e);
			}
			try {
				getTransactionManager().setTransactionTimeout(0);
			} catch (SystemException e) {
				if (isLoggingError()) {
					logError("setTransactionTimeout failed. ", e);
				}
			}
		}

		if (isLoggingDebug()) {
			logDebug("Finished uploading inventory for repository - " + pInventoryRepository.getRepositoryName());
		}

		return result;
	}

	int jobId;

	/**
	 * Connects to the queue and starts listening for messages.
	 * 
	 * @throws ServiceException
	 *             If a service exception occurs.
	 */
	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob("FlowItemsSchedulableService2", "Upload Flow Items Files to Repository",
				getAbsoluteName(), getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
	}

	/**
	 * Stops listening for messages and disconnects from the queue.
	 */
	public void doStopService() throws ServiceException {
		getScheduler().removeScheduledJob(jobId);
	}

	private String generatePriceListXml(ImportItem[] importItems, String prf, boolean checkProps) throws Exception {
		File tmpFile = null;
		PrintWriter writer = null;
		String prefix = prf + "_priceList_";
		String tmpName = prefix + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";

		Date currentDate = dateFormatDestock.parse(dateFormatDestock.format(new Date()));

		try {

			tmpFile = new File(workingDir, tmpName);

			writer = new PrintWriter(tmpFile, "UTF-8");
			writer.write(XML_HEADER);

			StringBuilder sbRemoveItems = new StringBuilder();

			if (importItems != null) {

				for (ImportItem item : importItems) {
					HashMap<String, String> props = item.getProperties();

					if (checkProps) {
						String strInitDestock = props.get(PROP_DATE_INIT_DESTOCK);
						String strDebDestock = props.get(PROP_DATE_DEB_DESTOCK);
						String strFinDestock = props.get(PROP_DATE_FIN_DESTOCK);

						if (!StringUtils.isEmpty(strInitDestock) && !StringUtils.isEmpty(strDebDestock)
								&& !StringUtils.isEmpty(strFinDestock)) {

							Date dateInitDestock = dateFormatDestock.parse(strInitDestock);
							Date dateDebDestock = dateFormatDestock.parse(strDebDestock);
							Date dateFinDestock = dateFormatDestock.parse(strFinDestock);
							Calendar calFinDestock = Calendar.getInstance();
							calFinDestock.setTime(dateFinDestock);
							calFinDestock.add(Calendar.DATE, 1);
							dateFinDestock = calFinDestock.getTime();

							if ((currentDate.compareTo(dateInitDestock) != 0) && currentDate.after(dateDebDestock)
									&& currentDate.before(dateFinDestock)) {
								continue;

							}
						}
					}

					writer.write(generatePriceList(item.getItemId(), props.get(PROP_LIST_PRICE)));

					if (Boolean.parseBoolean(props.get(PROP_FSOLDE))) {
						writer.write(generateSalesPriceList(item.getItemId(), props.get(PROP_PRIX_SOLDE), true));
					} else {
						sbRemoveItems
								.append(generateSalesPriceList(item.getItemId(), props.get(PROP_PRIX_SOLDE), false));

					}

				}
			}

			writer.println("</" + TemplateParser.TAG_IMPORT_ITEMS + ">\n");
			writer.println(sbRemoveItems.toString());
			writer.println(TemplateParser.XML_FILE_FOOTER);
			writer.flush();
			writer.close();
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
		return tmpName;
	}

	private String generatePriceList(String skuId, String listPrice) {
		String listPriceId = PREFIX_LIST_PRICE + skuId;
		StringBuilder sb = new StringBuilder();
		sb.append("<add-item item-descriptor=\"price\" repository=\"").append(PRICE_LIST_REPOSITORY).append("\" id=\"")
				.append(listPriceId).append("\" >\n");

		sb.append("<set-property name=\"priceList\"><![CDATA[listPrices]]></set-property>\n");

		sb.append("<set-property name=\"listPrice\"><![CDATA[").append(listPrice).append("]]></set-property>\n");

		sb.append("<set-property name=\"skuId\"><![CDATA[").append(skuId).append("]]></set-property>\n");

		sb.append("</add-item>\n");

		return sb.toString();
	}

	private String generateSalesPriceList(String skuId, String salePrice, boolean isImport) {
		String salePriceId = PREFIX_SALE_PRICE + skuId;
		StringBuilder sb = new StringBuilder();
		if (isImport) {
			sb.append("<add-item item-descriptor=\"price\" repository=\"").append(PRICE_LIST_REPOSITORY).append(
					"\" id=\"").append(salePriceId).append("\" >\n");

			sb.append("<set-property name=\"priceList\"><![CDATA[salePrices]]></set-property>\n");

			sb.append("<set-property name=\"listPrice\"><![CDATA[").append(salePrice).append("]]></set-property>\n");

			sb.append("<set-property name=\"skuId\"><![CDATA[").append(skuId).append("]]></set-property>\n");

			sb.append("</add-item>\n");
		} else {
			sb.append("<remove-item item-descriptor=\"price\"  id=\"").append(salePriceId).append("\" />\n");
		}

		return sb.toString();
	}

	private String generateInventoryXml(ImportItem[] importItems, String prx, boolean checkProps) throws Exception {
		File tmpFile = null;
		PrintWriter writer = null;
		String prefix = prx + "_inventory_";
		String tmpName = prefix + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";

		Date currentDate = dateFormatDestock.parse(dateFormatDestock.format(new Date()));

		try {

			tmpFile = new File(workingDir, tmpName);

			writer = new PrintWriter(tmpFile, "UTF-8");
			writer.write(XML_HEADER);

			if (importItems != null) {

				for (ImportItem item : importItems) {
					HashMap<String, String> props = item.getProperties();

					String strInitDestock = props.get(PROP_DATE_INIT_DESTOCK);
					String strDebDestock = props.get(PROP_DATE_DEB_DESTOCK);
					String strFinDestock = props.get(PROP_DATE_FIN_DESTOCK);

					if (checkProps) {
						if (!StringUtils.isEmpty(strInitDestock) && !StringUtils.isEmpty(strDebDestock)
								&& !StringUtils.isEmpty(strFinDestock)) {

							Date dateInitDestock = dateFormatDestock.parse(strInitDestock);
							Date dateDebDestock = dateFormatDestock.parse(strDebDestock);
							Date dateFinDestock = dateFormatDestock.parse(strFinDestock);
							Calendar calFinDestock = Calendar.getInstance();
							calFinDestock.setTime(dateFinDestock);
							calFinDestock.add(Calendar.DATE, 1);
							dateFinDestock = calFinDestock.getTime();

							if ((currentDate.compareTo(dateInitDestock) != 0) && currentDate.after(dateDebDestock)
									&& currentDate.before(dateFinDestock)) {
								continue;

							}
						}
					}

					Double stock = null;

					String cacheSKUCompteur = props.get("CACHESKUCOMPTEUR");
					if (!StringUtils.isEmpty(cacheSKUCompteur)) {
						stock = Double.valueOf(cacheSKUCompteur);
					}
					if (checkProps) {
						if (!StringUtils.isEmpty(strInitDestock)) {
							Date dateInitDestock = dateFormatDestock.parse(strInitDestock);
							if (currentDate.compareTo(dateInitDestock) == 0) {
								String valStockDestock = props.get("valStockDestock");
								if (!StringUtils.isEmpty(valStockDestock)) {
									stock = Double.valueOf(valStockDestock);
								}
							}
						}
					} else {
						String valStockDestock = props.get("valStockDestock");
						if (!StringUtils.isEmpty(valStockDestock)) {
							stock = Double.valueOf(valStockDestock);
						}
					}

					int status = 0;
					String skuId = item.getItemId();
					if (stock != null) {
						status = getInventoryManager().setAvailabilityStatus(skuId,
								InventoryManager.AVAILABILITY_STATUS_DERIVED);
						if (status == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
							writer.write(generateInventoryItem(item, stock));

						} else if (status == InventoryManager.INVENTORY_STATUS_FAIL) {
							if (isLoggingError()) {
								logError("An error in setting date on the status occurred on sku: " + skuId);
							}
						} else {
							status = getInventoryManager().setStockLevel(skuId, stock.longValue());
							if (status == InventoryManager.INVENTORY_STATUS_FAIL) {
								if (isLoggingError()) {
									logError("An error in setting date on the status occurred on sku: " + skuId);
								}
							} else {
								if (isLoggingDebug()) {
									logDebug("Update to " + skuId + " with value=" + stock);
								}
							}
						}
					}
				}
			}

			writer.println(XML_FOOTER);
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
		return tmpName;
	}

	private String generateInventoryItem(ImportItem item, Double stock) {
		String skuId = item.getItemId();

		String invId = PREFIX_INVENTORY + skuId;
		StringBuilder sb = new StringBuilder();
		sb.append("<add-item item-descriptor=\"inventory\" id=\"").append(invId).append("\" >\n");

		sb.append("<set-property name=\"displayName\"><![CDATA[").append(skuId).append("]]></set-property>\n");

		sb.append("<set-property name=\"catalogRefId\"><![CDATA[").append(skuId).append("]]></set-property>\n");

		sb.append("<set-property name=\"description\"><![CDATA[").append(skuId).append("]]></set-property>\n");
		// DERIVED is the String value for the InventoryManager.AVAILABILITY_STATUS_DERIVED
		sb.append("<set-property name=\"availabilityStatus\"><![CDATA[").append(
				getAvailabilityStatusMap().getProperty(Integer.toString(InventoryManager.AVAILABILITY_STATUS_DERIVED)))
				.append("]]></set-property>\n");

		sb.append("<set-property name=\"stockLevel\"><![CDATA[").append(stock.longValue()).append(
				"]]></set-property>\n");

		sb.append("</add-item>\n");

		return sb.toString();
	}

	private void checkProperties() {
		if (StringUtils.isEmpty(getProjectName())) {
			this.workingProjectName = DEFAULT_PROJECT_NAME;
		} else {
			this.workingProjectName = this.projectName;
		}

		if (StringUtils.isEmpty(getUserName())) {
			setUserName(DEFAULT_USER_NAME);
		}
		if (StringUtils.isEmpty(getComment())) {
			setComment(DEFAULT_COMMENT);
		}

		if (getTransactionTimeout() == null) {
			setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
		}

		if (StringUtils.isEmpty(getDeploymentWorkflow())) {
			setDeploymentWorkflow(DEFAULT_DEPLOYMENT_WORKFLOW);
		}

		if (isLoggingDebug()) {
			logDebug("projectName=" + workingProjectName);
			logDebug("userName=" + getUserName());
			logDebug("comment=" + getComment());
			logDebug("deploymentWorkflow=" + getDeploymentWorkflow());
			logDebug("transactionTimeout=" + getTransactionTimeout());
		}

	}

	public InventoryCacheInvalidationGlobalNotifier getInventoryCacheInvalidationGlobalNotifier() {
		return mInventoryCacheInvalidationGlobalNotifier;
	}

	public void setInventoryCacheInvalidationGlobalNotifier(
			InventoryCacheInvalidationGlobalNotifier pInventoryCacheInvalidationGlobalNotifier) {
		mInventoryCacheInvalidationGlobalNotifier = pInventoryCacheInvalidationGlobalNotifier;
	}

	public boolean isInvalidateInventoryCache() {
		return mInvalidateInventoryCache;
	}

	public void setInvalidateInventoryCache(boolean pInvalidateInventoryCache) {
		mInvalidateInventoryCache = pInvalidateInventoryCache;
	}

	/**
	 * @param workingFile
	 * @return array file names, 0 - name file on update, 1 - name file on add
	 * @throws Exception
	 */
	private List<List<ChunkProperties>> parseXml(File workingFile) throws Exception {
		if (workingFile == null) {
			if (isLoggingDebug()) {
				logDebug("Source file is null.");
			}
			return null;
		}

		List<List<ChunkProperties>> result = new ArrayList<List<ChunkProperties>>();
		
		List<ChunkProperties> addChunks = new ArrayList<ChunkProperties>();
		List<ChunkProperties> updateChunks = new ArrayList<ChunkProperties>();

		int cntAddSkus = 0;
		int cntUpdateSkus = 0;
		int nmbFileAdd = 1;
		int nmbFileUpdate = 1;
		
		String tmpAddFileName = new StringBuilder().append("Add_SKU_").append(nmbFileAdd).append("_").append(
				new Date().getTime()).append(".xml").toString();
		ChunkProperties addChunk = new ChunkProperties(tmpAddFileName);
		addChunks.add(addChunk);
		File addSkus = new File(workingFile.getParentFile(), tmpAddFileName);

		String tmpUpdateFileName = new StringBuilder().append("Update_SKU_").append(nmbFileUpdate).append("_").append(
				new Date().getTime()).append(".xml").toString();
		ChunkProperties updateChunk = new ChunkProperties(tmpUpdateFileName);
		updateChunks.add(updateChunk);
		File updateSkus = new File(workingFile.getParentFile(), tmpUpdateFileName);

		String tmpUpdateInvFileName = UPDATE_INVETORY_NAME + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";
		File updateInvSkus = new File(workingFile.getParentFile(), tmpUpdateInvFileName);
		ChunkProperties invChunk = new ChunkProperties(tmpUpdateInvFileName);
		invChunk.setOnlyInventory(true);

		PrintWriter addWriter = null;
		PrintWriter updateWriter = null;
		PrintWriter updateInvWriter = null;

		BufferedReader reader = null;
		try {
			addWriter = new PrintWriter(addSkus, "UTF-8");
			addWriter.write(XML_HEADER);

			updateWriter = new PrintWriter(updateSkus, "UTF-8");
			updateWriter.write(XML_HEADER);

			updateInvWriter = new PrintWriter(updateInvSkus, "UTF-8");
			updateInvWriter.write(XML_HEADER);

			reader = new BufferedReader(new FileReader(workingFile));
			String line;
			boolean isItem = false;
			boolean isUpdate = true;
			SkuComparator skuComparator = null;
			StringBuilder sbUpdate = null;
            StringBuilder sbUpdateTmp = null;
            Date currentDate = dateFormatDestock.parse(dateFormatDestock.format(new Date()));
            Date dateDebDestock = null;
            Date dateFinDestock = null;
			
			while ((line = reader.readLine()) != null) {
				if (cntAddSkus == MAX_ITEMS) {
					cntAddSkus = 0;
					nmbFileAdd++;

					addWriter.println(XML_FOOTER);
					addWriter.flush();
					addWriter.close();
					tmpAddFileName = new StringBuilder().append("Add_SKU_").append(nmbFileAdd).append("_").append(
							new Date().getTime()).append(".xml").toString();

					addChunk = new ChunkProperties(tmpAddFileName);
					addChunks.add(addChunk);

					addSkus = new File(workingFile.getParentFile(), tmpAddFileName);
					addWriter = new PrintWriter(addSkus, "UTF-8");
					addWriter.write(XML_HEADER);

				}
				
				if (cntUpdateSkus == MAX_ITEMS) {
					cntUpdateSkus = 0;
					nmbFileUpdate++;

					updateWriter.println(XML_FOOTER);
					updateWriter.flush();
					updateWriter.close();
					tmpUpdateFileName = new StringBuilder().append("Update_SKU_").append(nmbFileUpdate).append("_").append(
							new Date().getTime()).append(".xml").toString();

					updateChunk = new ChunkProperties(tmpUpdateFileName);
					updateChunks.add(updateChunk);

					updateSkus = new File(workingFile.getParentFile(), tmpUpdateFileName);
					updateWriter = new PrintWriter(updateSkus, "UTF-8");
					updateWriter.write(XML_HEADER);

				}
				
				if (line.indexOf(TAG_ADD_ITEM) != -1) {
					isItem = true;
					String skuId = getSkuId(line);
					RepositoryItem item = getProductProductionRepository().getItem(skuId, Constants.ITEM_CASTO_SKU);
					if (item == null) {
						isUpdate = false;
					} else {
						isUpdate = true;
						skuComparator = new SkuComparator(isFilterData());
						skuComparator.setSkuItem(item);
						sbUpdate = new StringBuilder();
						sbUpdateTmp = new StringBuilder();
			            dateDebDestock = null;
			            dateFinDestock = null;
					}
				}

				if (isItem) {
					line = line.replaceAll(SING_NULL, "");
					if (isUpdate) {
					    if (line.indexOf(END_TAG_ADD_ITEM) != -1 &&
					            (dateDebDestock == null || dateFinDestock == null ||
					             currentDate.before(dateDebDestock) || currentDate.after(dateFinDestock))) {
					        sbUpdate.append(sbUpdateTmp);
	                    }
						sbUpdate.append(line).append(Constants.LINE_SEPARATOR);
						if (line.indexOf(PROP_PRIX_SOLDE) != -1) {
						    sbUpdateTmp.append(START_TAG_SALE_PRICE);
						    sbUpdateTmp.append(getCDataValue(line));
						    sbUpdateTmp.append(END_SET_PROPERTY).append(Constants.LINE_SEPARATOR);
						}
						if (line.indexOf(PROP_FSOLDE) != -1) {
						    sbUpdateTmp.append(START_TAG_ON_SALE);
						    sbUpdateTmp.append(getCDataValue(line));
						    sbUpdateTmp.append(END_SET_PROPERTY).append(Constants.LINE_SEPARATOR);
						}
                        if (line.indexOf(PROP_DATE_DEB_DESTOCK) != -1) {
                            try {
                                dateDebDestock = dateFormatDestock.parse(getCDataValue(line));
                            } catch (ParseException e) {
                                dateDebDestock = null;
                            }
                        }
                        if (line.indexOf(PROP_DATE_FIN_DESTOCK) != -1) {
                            try {
                                dateFinDestock = dateFormatDestock.parse(getCDataValue(line));
                            } catch (ParseException e) {
                                dateFinDestock = null;
                            }
						}
					} else {
						addWriter.println(line);
						if (line.indexOf(PROP_PRIX_SOLDE) != -1) {
							addWriter.write(START_TAG_SALE_PRICE);
							addWriter.write(getCDataValue(line));
							addWriter.println(END_SET_PROPERTY);
						}
						if (line.indexOf(PROP_FSOLDE) != -1) {
							addWriter.write(START_TAG_ON_SALE);
							addWriter.write(getCDataValue(line));
							addWriter.println(END_SET_PROPERTY);
						}
					}
				}

				if (line.indexOf(END_TAG_ADD_ITEM) != -1) {
					isItem = false;
					if (isUpdate) {
						if (!skuComparator.isEqualsSku(sbUpdate.toString())) {
							updateWriter.write(sbUpdate.toString());
							cntUpdateSkus++;
						} else {
							updateInvWriter.write(sbUpdate.toString());
						}
					} else {
						cntAddSkus++;
					}
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}

			if (addWriter != null) {
				addWriter.println(XML_FOOTER);
				addWriter.flush();
				addWriter.close();
			}
			if (updateWriter != null) {
				updateWriter.println(XML_FOOTER);
				updateWriter.flush();
				updateWriter.close();
			}
			if (updateInvWriter != null) {
				updateInvWriter.println(XML_FOOTER);
				updateInvWriter.flush();
				updateInvWriter.close();
			}

			updateChunks.add(invChunk);
			result.add(updateChunks);
			result.add(addChunks);
		}
		

		return result;
	}

	private static final String START_TAG_SALE_PRICE = "<set-property name=\"salePrice\"><![CDATA[";
	private static final String START_TAG_ON_SALE = "<set-property name=\"onSale\"><![CDATA[";
	private static final String KEY_CDATA = "[CDATA[";
	private static final String END_CDATA = "]]";

	private String getSkuId(String line) {
		if (line.indexOf("id") != -1) {
			String mLine = line.substring(line.indexOf("id"));
			return mLine.substring(mLine.indexOf("\"") + 1, mLine.lastIndexOf("\""));
		}
		return "";
	}

	private String getCDataValue(String line) {
		if (line.indexOf(KEY_CDATA) != -1) {
			String mLine = line.substring(line.indexOf(KEY_CDATA));
			return mLine.substring(KEY_CDATA.length(), mLine.lastIndexOf(END_CDATA));
		}
		return "";
	}

	private static final String ADD_PRODUCT_ITEM = "<add-item item-descriptor=\"casto_product\" repository=\"/atg/commerce/catalog/ProductCatalog\">";
	private static final String ADD_PRODUCT_ITEM_END = "</add-item>";
	private static final String START_SET_PROPERTY_DISPLAY_NAME = "<set-property name=\"displayName\"><![CDATA[";
	private static final String END_SET_PROPERTY = "]]></set-property>";
	private static final String START_SETP_PROPERTY_CHILD_SKUS = "<set-property name=\"childSKUs\"><![CDATA[";
	private static final String PROP_DESCRIPTION = "Description";

	private String generateProductListXml(ImportItem[] importItems, String prx) throws Exception {
		File tmpFile = null;
		PrintWriter writer = null;
		String prefix = prx + "_productList_";
		String tmpName = prefix + Constants.DATE_FORMAT_ARCHIVE.format(new Date()) + ".xml";

		try {

			tmpFile = new File(workingDir, tmpName);

			writer = new PrintWriter(tmpFile, "UTF-8");
			writer.write(XML_HEADER);

			if (importItems != null) {

				for (ImportItem item : importItems) {
					HashMap<String, String> props = item.getProperties();
					writer.println(ADD_PRODUCT_ITEM);

					writer.write(START_SET_PROPERTY_DISPLAY_NAME);
					if (props.get(PROP_DESCRIPTION) == null) {
						writer.write(DESCRIPTION_NOT_FOUND + item.getItemId());
					} else {
						writer.write(props.get(PROP_DESCRIPTION));
					}
					writer.println(END_SET_PROPERTY);

					writer.write(START_SETP_PROPERTY_CHILD_SKUS);
					writer.write(item.getItemId());
					writer.println(END_SET_PROPERTY);

					writer.println(ADD_PRODUCT_ITEM_END);

				}
			}

			writer.println("</" + TemplateParser.TAG_IMPORT_ITEMS + ">\n");
			writer.println(TemplateParser.XML_FILE_FOOTER);
			writer.flush();
			writer.close();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		return tmpName;
	}

	private String getLastDeployProjectName(List<ProjectProperties> updateProjects,
			List<ProjectProperties> addProjects) {
		String lastProjectName = null;
		int cnt = 0;
		if (updateProjects != null) {
			for(ProjectProperties project : updateProjects) {
				if (project.getState() == 0) {
					lastProjectName = project.getProjectName();
					cnt++;
				}
			}
		}

		if (addProjects != null) {
			for(ProjectProperties project : addProjects) {
				if (project.getState() == 0) {
					lastProjectName = project.getProjectName();
					cnt++;
				}
			}
		}
		
		if (cnt < 2) {
			lastProjectName = null;
		}
		return lastProjectName;
	}
	
	private String getStatusProject(int code) {
		if (code == CODE_SUCCESS || code == CODE_NOT_UPLOAD) {
			return PROJECT_STATE_SUCCESS;
		} else if (code == CODE_INVENTORY_FAILED) {
			return PROJECT_STATE_INVENTORY_FAILED;
		} else if (code == CODE_DEPLOY_FAILED) {
			return PROJECT_STATE_DEPLOY_FAILED;
		} else {
			return PROJECT_STATE_UPLOAD_FAILED;
		}
	}
	
}
