package com.castorama.integration.bazaarvoice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import atg.adapter.gsa.xml.TemplateParser;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.epub.project.Process;
import atg.epub.project.ProcessHome;
import atg.epub.project.ProjectConstants;
import atg.nucleus.DynamoEnv;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.process.action.ActionConstants;
import atg.process.action.ActionException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.security.ThreadSecurityManager;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.workflow.ActorAccessException;
import atg.workflow.MissingWorkflowDescriptionException;
import atg.workflow.WorkflowConstants;
import atg.workflow.WorkflowException;
import atg.workflow.WorkflowManager;
import atg.workflow.WorkflowView;

import com.castorama.integration.util.MiscUtils;
import com.castorama.sftp.SftpService;

/**
 * Service makes Rating review import to version repository. 
 * 
 * @author Andrew_Logvinov
 */
public class RatingReviewFeedManager extends GenericService implements Schedulable {

	private static final String DEFAULT_DEPLOYMENT_WORKFLOW = 
		"/Commerce/castorama/OneStepDeploymentWorkflow.wdl";
	/**
	 * 
	 */
	private static DateFormat archFileFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

	/**
	 * Stores export root dir as file object.
	 */
	private File rootDirFile;
	
	// rootDir property wrapper	
	/**
	 * Get export root folder file system path.
	 * 
	 * @return managed property value.
	 */
	public String getRootDir() {
		return getRootFolderFile().getPath();
	}

	/**
	 * Set export root folder file system path.
	 * 
	 * @param rootFolder the folder filename.
	 */
	public void setRootDir(String rootFolder) {
		this.rootDirFile = new File(rootFolder);
	}
	
	
	/**
	 * SFTP access credentials
	 */
	private AccessCredentials accessCredentials;

	/**
	 * @return the accessCredentials
	 */
	public AccessCredentials getAccessCredentials() {
		return accessCredentials;
	}

	/**
	 * @param accessCredentials the accessCredentials to set
	 */
	public void setAccessCredentials(AccessCredentials accessCredentials) {
		this.accessCredentials = accessCredentials;
	}
	
	
	/**
	 * 
	 */
	private String ftpSourceFile;

	/**
	 * @return the ftpSourceFile
	 */
	public String getFtpSourceFile() {
		return ftpSourceFile;
	}

	/**
	 * @param ftpSourceFile the ftpSourceFile to set
	 */
	public void setFtpSourceFile(String ftpSourceFile) {
		this.ftpSourceFile = ftpSourceFile;
	}
	
	/**
	 * 
	 */
	private Repository repository;
	
	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	
	/**
	 * 
	 */
	private TransactionManager transactionManager;
	
	/**
	 * @return the transactionManager
	 */
	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	/**
	 * @param transactionManager the transactionManager to set
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	/**
	 * 
	 */
	private int transactionTimeout;

	/**
	 * @return the transactionTimeout
	 */
	public int getTransactionTimeout() {
		return transactionTimeout;
	}

	/**
	 * @param transactionTimeout the transactionTimeout to set
	 */
	public void setTransactionTimeout(int transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}


	/**
	 * 
	 */
	private String projectName; 

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private String deploymentWorkflow;
	
	public String getDeploymentWorkflow() {
		return deploymentWorkflow;
	}

	public void setDeploymentWorkflow(String deploymentWorkflow) {
		this.deploymentWorkflow = deploymentWorkflow;
	}

	private WorkflowManager mWorkflowManager;

	public WorkflowManager getWorkflowManager() {
		return mWorkflowManager;
	}

	public void setWorkflowManager(WorkflowManager pWorkflowManager) {
		mWorkflowManager = pWorkflowManager;
	}
	
	/**
	 * 
	 */
	private String userName;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 
	 */
	private String comment;

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	// Scheduler property
	/**
	 * 
	 */
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

	// schedule property
	/**
	 * 
	 */
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
	
	private int jobId = -1;	

	/**
	 * @throws IOException 
	 * 
	 */
	public void importRating() throws IOException {
		SftpService service = new SftpService();
		File zipfile;
		try {
			zipfile = service.getFile(accessCredentials.getSftpHost(),
					accessCredentials.getSftpUser(),
					accessCredentials.getSftpPassword(),
					getFtpSourceFile(),
					getInputFile());
			
		} catch (IOException e) {
			logError(e.getMessage(), e);
			
			return;
		}
		
		GZIPInputStream zis = null;
		
		File input = new File(getInputFile(), "bv_castorama_ratings_" + archFileFormat.format(new Date()) + ".xml");
		
		try {
			zis = new GZIPInputStream(new FileInputStream(zipfile));

			FileOutputStream fos = new FileOutputStream(input);
			byte[] buff = new byte[1024*1024];
			
			try {
				int av = 1;
				while (av > 0) {
					av = zis.read(buff);
					if (av > 0) {
						fos.write(buff, 0, av);
					}
				}
				
				fos.flush();
			} finally {
				buff = null;
				fos.close();
			}
		} catch (IOException e) {
			logError(e.getMessage(), e);
			
			return;
		} finally {
			try {
				zis.close();
			} catch (Exception e) {
				// do nothing
			}
			
			zipfile.delete();
		}

		File importFolder = getImportFile();
			
		File working = new File(getWorkingFile(), input.getName());
		input.renameTo(working);
		input = working;
		
		RatingReviewWriter writer = new RatingReviewWriter(importFolder);			
		RatingReviewXMLHandler1 handler = new RatingReviewXMLHandler1(writer);
		
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();				
			
			parser.parse(new InputSource(new FileInputStream(input)), handler);				
			input.renameTo(new File(getArchiveFile(), input.getName()));

			int documents = handler.getDocumentsCount();
			int products = handler.getProductsCount();
			
			try {
				String[] lst = importFolder.list(new FilenameFilter(){
					public boolean accept(File dir, String name) {
						return name.endsWith(".xml");
					}});
				
				logInfo("" + lst.length + " files was found for import");
				
				int res = doTemplateImport(lst, importFolder);
				if (res == 0) {
					PrintWriter log = new PrintWriter(new File(getArchiveFile(), input.getName().replaceAll("\\.xml", ".log")));
					logSuccess(log, products, documents);
				} else {
					importFolder.renameTo(new File(getErrorFile(), archFileFormat.format(new Date()) + "_out"));
					PrintWriter log = new PrintWriter(new File(getErrorFile(), input.getName().replaceAll("\\.xml", ".log")));
					logFails(log, input.getPath());
				}

			} catch (Throwable e) {
				importFolder.renameTo(new File(getErrorFile(), archFileFormat.format(new Date()) + "_out"));
			}
				
		} catch (ParserConfigurationException e) {
			logError("File " + input.getPath() + " has wrong xml format. Skipping.");
			logError(e.getMessage());
				
			input.renameTo(new File(getErrorFile(), archFileFormat.format(new Date()) + input.getName()));

			PrintWriter log = new PrintWriter(new File(getErrorFile(), input.getName().replaceAll("\\.xml", ".log")));
			logFails(log, e.getMessage(), input.getPath(), e);
		} catch (SAXException e) {
			logError("File " + input.getPath() + " has wrong xml format. Skipping.");
			logError(e.getMessage());
				
			input.renameTo(new File(getErrorFile(), archFileFormat.format(new Date()) + input.getName()));

			PrintWriter log = new PrintWriter(new File(getErrorFile(), input.getName().replaceAll("\\.xml", ".log")));
			logFails(log, e.getMessage(), input.getPath(), e);
		} catch (FactoryConfigurationError e) {
			logError(e);
		} catch (IOException e) {
			logError("File " + input.getPath() + " could not be read. Skipping.");
			logError(e.getMessage());
				
			input.renameTo(new File(getErrorFile(), archFileFormat.format(new Date()) + input.getName()));
			PrintWriter log = new PrintWriter(new File(getErrorFile(), input.getName().replaceAll("\\.xml", ".log")));
			logFails(log, e.getMessage(), input.getPath(), e);
		} finally {
			if (importFolder != null && importFolder.exists()) {
				MiscUtils.deleteFolder(importFolder);
			}
		}
	}


	private int doTemplateImport(String[] tmpNames, File startDir){
		long start = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug("start - doTemplateImport: ");
		}

		if (StringUtils.isEmpty(getDeploymentWorkflow())) {
			setDeploymentWorkflow(DEFAULT_DEPLOYMENT_WORKFLOW);
		}

		String workingProjectName = getUniqueProjectName();
			
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
		}

		list.add("-database");
		list.add(database);

		list.add("-configPath");
		list.add(configPath);

		list.add("-repository");
		list.add(repository);

		list.add("-project");
		list.add(workingProjectName);

		list.add("-user");
		list.add(getUserName());

		list.add("-comment");
		list.add(getComment());

		list.add("-workflow");
		list.add(getDeploymentWorkflow());

		list.add("-startDir");
		list.add(startDir.getAbsolutePath());

		for (String name : tmpNames) {
			list.add("-import");
			list.add(name);
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

				result = TemplateParser.runParser(list.toArray(new String[0]), new PrintWriter(System.out));

				if (isLoggingDebug()) {
					logDebug("TemplateParser.runParser - result=" + result);
				}

				if (result == 0) {
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
			logDebug("finish -  doTemplateImport: " + (elapsed / 60000) + " min(s)");
		}
		
		if (!rollback) {
			try {
				Process pr = find(workingProjectName);
				advanceWorkflow(pr);
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(e);
				}
				result = -100;
			}
		}
		
		return result;
		
	}

	protected Process find(String pName) throws WorkflowException, ActionException, EJBException, FinderException {
		ProcessHome processHome = ProjectConstants.getPersistentHomes().getProcessHome();
		Collection coll = processHome.findByDisplayName(pName);
		if (coll != null && coll.size() > 0) {
			return (Process) coll.iterator().next();
		}
		return null;
	}
	
	protected void advanceWorkflow(Process pProcess) throws WorkflowException, ActionException {
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

	@Override
	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob(getClass().getSimpleName(),
				"Rating Review import service",
				getAbsoluteName(),
				getSchedule(),
				this,
				ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
		
		super.doStartService();
	}
	
	@Override
	public void doStopService() throws ServiceException {
		if (jobId > 0) {
			getScheduler().removeScheduledJob(jobId);
		}
		
		super.doStopService();
	}


	private File getImportFile() {
		File result = new File(getWorkingFile(), new SimpleDateFormat("yyyyMMdd").format(new Date()));
		
		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}
	
	private File getArchiveFile() {
		File result = new File(getRootFolderFile(), "archive");
		
		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}

	private File getErrorFile() {
		File result = new File(getRootFolderFile(), "error");
		
		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}

	public void performScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		try {
			importRating();
		} catch (Throwable e) {
			logError(e);
		}
	}
	
	private String getUniqueProjectName(){
		return getProjectName() + " " + archFileFormat.format(new Date());
	}

	/**
	 * Internal getter for get File for root folder. If folder was not exist will be created.
	 *  
	 * @return
	 */
	private File getRootFolderFile() {
		if (!rootDirFile.exists()) {
			rootDirFile.mkdir();
		}
		
		return rootDirFile;
	}

	/**
	 * @return
	 */
	private File getInputFile() {
		File result = new File(getRootFolderFile(), "input");

		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}

	/**
	 * @return the workingFile
	 */
	private File getWorkingFile() {
		File result = new File(getRootFolderFile(), "working");
		
		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}
	
	private static void logFails(PrintWriter log, String message, String srcFile, Throwable t) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Import Date: " + df.format(new Date()));
		log.println("Status: Failed");
		log.println("File to import: " + srcFile);
		log.println(message);
		if (t != null) {
			t.printStackTrace(log);
		}
		
		log.flush();
		log.close();
	}
	
	private static void logFails(PrintWriter log, String srcFile) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Import Date: " + df.format(new Date()));
		log.println("Status: Failed");
		log.println("File to import: " + srcFile);
		
		log.flush();
		log.close();
	}

	private static void logSuccess(PrintWriter log, int products, int documents) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Import Date: " + df.format(new Date()));
		log.println("Status: Success");
		log.println("Products: " + products);
		log.println("Documents: " + documents);
		
		log.flush();
		log.close();
	}
	
}
