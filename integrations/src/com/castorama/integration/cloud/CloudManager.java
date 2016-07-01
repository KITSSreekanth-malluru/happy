package com.castorama.integration.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;

import atg.adapter.gsa.xml.TemplateParser;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;

public class CloudManager extends IntegrationBase implements Schedulable {

	private static final String CLOUD_ITEM_DESC_NAME = "cloud";
	private static final String CLOUD_TERM_ITEM_DESC_NAME = "cloud_term";

	private File inputDir;
	private File workingDir;
	private File archiveDir;
	private File errorDir;

	public CloudManager() {

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
	 * Upload files from home directory
	 * 
	 * @throws FileNotFoundException
	 *             if home directory not exists
	 */
	public void uploadClouds() throws FileNotFoundException {
		// check folders
		if (isLoggingDebug()) {
			logDebug("start - CloudManager - uploadClouds");
		}
		checkFolders();

		XMLToolsFactory factory = DefaultXMLToolsFactory.getInstance();
		XMLToDOMParser domParser = getXMLToDOMParser(factory);

		try {
			File workingFile = getWorkingFile();
			if (workingFile == null) {
				if (isLoggingDebug()) {
					logDebug("Folder " + inputDir.getAbsolutePath() + " is empty.");
				}
				if (isLoggingDebug()) {
					logDebug("finish - CloudManager - uploadClouds");
				}
				return;
			} else {
				if (isLoggingDebug()) {
					logDebug(" working file: " + workingFile);
				}
			}

			InputStream inputStream = new FileInputStream(workingFile);
			File statusFile = getStatusFile(workingFile);
			if (isLoggingDebug()) {
				logDebug("status file: " + statusFile);
			}

			TransactionDemarcation trd = new TransactionDemarcation();
			File dest = archiveDir;
			try {
				PrintWriter logWriter = null;
				try {
					trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

					logWriter = new PrintWriter(statusFile);
					logWriter.println("Import Date: " + Constants.DATE_FORMAT_LOG.format(new Date()));

					removeAllItems(CLOUD_TERM_ITEM_DESC_NAME);

					removeAllItems(CLOUD_ITEM_DESC_NAME);

					TemplateParser.addToTemplate(inputStream, null, domParser,
							(atg.adapter.gsa.GSARepository) getRepository(), TemplateParser.PARSE_NORMAL, true, // without
							// transaction
							new PrintWriter(System.out), null);

					logWriter.println("Status: Success");
					logWriter.println("Number of Clouds imported successfully: " + countItems(CLOUD_ITEM_DESC_NAME));

					logWriter.println("Number of Terms Clouds imported successfully: " + countItems(CLOUD_TERM_ITEM_DESC_NAME));

				} catch (Exception e) {
					if (isLoggingError()) {
						logError("Exception occured trying to remove/add items", e);
					}

					logWriter.println("Status: Failed");

					dest = errorDir;
					e.printStackTrace(logWriter);
					try {
						getTransactionManager().setRollbackOnly();

					} catch (Exception se) {
						if (isLoggingError()) {
							logError("Unable to set rollback for transaction", se);
						}
					}
				} finally {
					trd.end();

					logWriter.flush();
					logWriter.close();

					moveFileIn(workingFile, dest);
					moveFileIn(statusFile, dest);
				}
			} catch (TransactionDemarcationException e) {
				if (isLoggingError()) {
					logError("Creating transaction demarcation failed", e);
				}
			}

		} catch (FileNotFoundException e) {
			if (isLoggingError()) {
				logError("Xml file not found", e);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish - CloudManager - uploadClouds");
		}

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
			uploadClouds();
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
	}

	int jobId;

	/**
	 * Connects to the queue and starts listening for messages.
	 * 
	 * @throws ServiceException
	 *             If a service exception occurs.
	 */
	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob("CloudSchedulableService", "Upload Terms Clouds to Cloud Repository",
				getAbsoluteName(), getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
	}

	/**
	 * Stops listening for messages and disconnects from the queue.
	 */
	public void doStopService() throws ServiceException {
		getScheduler().removeScheduledJob(jobId);
	}

	private RepositoryView getRepositoryView(String itemDescName) throws RepositoryException {
		RepositoryView view = getRepository().getView(itemDescName);
		if (view == null) {
			throw new RepositoryException("RepositoryView not found for " + itemDescName);
		}
		return view;
	}

	private void removeAllItems(String itemDescName) throws RepositoryException {
		RepositoryView view = getRepositoryView(itemDescName);
		RepositoryItem[] items = view.executeQuery(view.getQueryBuilder().createUnconstrainedQuery());
		if (items == null) {
			if (isLoggingDebug()) {
				logDebug("No items to remove for type: " + itemDescName);
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				atg.adapter.util.RepositoryUtils.removeReferencesToItem(items[i]);
				((MutableRepository) getRepository()).removeItem(items[i].getRepositoryId(), itemDescName);
			}
		}
	}

	private int countItems(String itemDescName) throws RepositoryException {
		RepositoryView view = getRepositoryView(itemDescName);
		return view.executeCountQuery(view.getQueryBuilder().createUnconstrainedQuery());
	}

	private void checkFolders() throws FileNotFoundException {
		File rootDir = getRootFolder();

		inputDir = new File(rootDir, Constants.INPUT_FOLDER);
		checkDirectory(inputDir);

		workingDir = new File(rootDir, Constants.WORKING_FOLDER);
		if (!workingDir.exists()) {
			workingDir.mkdir();
		}
		checkDirectory(workingDir);

		archiveDir = new File(rootDir, Constants.ARCHIVE_FOLDER);
		if (!archiveDir.exists()) {
			archiveDir.mkdir();
		}
		checkDirectory(archiveDir);

		errorDir = new File(rootDir, Constants.ERROR_FOLDER);
		if (!errorDir.exists()) {
			errorDir.mkdir();
		}
		checkDirectory(errorDir);
	}

	private File getWorkingFile() throws Exception {
		File[] inpFiles = inputDir.listFiles();

		File inpFile = null;
		if (inpFiles == null || inpFiles.length == 0) {
			return null;
		} else {
			for (File f : inpFiles) {
				if (f.isFile() && f.exists()) {
					inpFile = f;
					break;
				}
			}
		}

		String name = getFileName(inpFile);
		String workName = name + "_" + Constants.DATE_FORMAT_ARCHIVE.format(new Date())
				+ inpFile.getName().substring(name.length(), inpFile.getName().length());
		File workFile = new File(workingDir, workName);

		MiscUtils.copyFile(inpFile, workFile);

		inpFile.delete();

		return workFile;
	}

	private File getStatusFile(File workFile) {
		String name = getFileName(workFile);
		return new File(workingDir, name + ".log");
	}

}
