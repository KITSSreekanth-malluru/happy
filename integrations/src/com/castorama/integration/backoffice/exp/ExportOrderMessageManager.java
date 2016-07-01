package com.castorama.integration.backoffice.exp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.transaction.TransactionManager;

import com.castorama.integration.backoffice.exp.BeanStringWriter;
import com.castorama.integration.backoffice.exp.COERecord;
import com.castorama.integration.backoffice.exp.COZRecord;
import com.castorama.integration.backoffice.exp.MessagesLoader;
import com.castorama.integration.backoffice.exp.RecordFactory;
import com.castorama.integration.journal.JournalItem;
import com.castorama.integration.journal.ProcessingJournalService;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

public class ExportOrderMessageManager extends GenericService  implements Schedulable {

	private int jobId = -1;
	
	private String filePrefix = "";

	/**
	 * @return the filePrefix
	 */
	public String getFilePrefix() {
		return filePrefix;
	}

	/**
	 * @param filePrefix the filePrefix to set
	 */
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

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

	private File rootFileFolder;

	/**
	 * @param rootFolder the rootFolder to set
	 */
	public void setRootDir(String rootFolder) {
		this.rootFileFolder = new File(rootFolder);
	}
	
	/**
	 * @return
	 */
	public String getRootDir() {
		return getRootDirFile().getPath();
	}
	
	private ProcessingJournalService journalService;
	
	/**
	 * @return the journalService
	 */
	public ProcessingJournalService getJournalService() {
		return journalService;
	}

	/**
	 * @param journalService the journalService to set
	 */
	public void setJournalService(ProcessingJournalService journalService) {
		this.journalService = journalService;
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
	
	
	public void exportMessages() throws IOException {
		RecordFactory rf = new RecordFactory();
		TransactionDemarcation trd = new TransactionDemarcation();

		List<String> success = new ArrayList<String>();
		int failures = 0;
		
		try {
			trd.begin(getTransactionManager());
			File file = null;
			JournalItem jr = null;
			
			try {
				jr = journalService.registerStarting("CONTACT");
				
				String fileId = "" + jr.getSequence();
				file = new File(getWorkFolder(), getNextFileName(jr));
				
				MessagesLoader loader = new MessagesLoader(getRepository());
				
				BeanStringWriter writer = new BeanStringWriter(file);
				writer.write(new COERecord(fileId));
				int num = 1;
				
				Iterator<RepositoryItem> it = loader.iterator(); // probably require transaction reference pass
				while (it.hasNext()) {
					RepositoryItem itm = it.next();
					
					if (null == itm) {
						continue;
					}
					
					COLRecord record = null;
					try {
						record = rf.getCOLRecord(itm);
						writer.write(record);
						num++;
						loader.registerExported(itm);
						
						success.add(record.getContactId());
					} catch (NullPointerException e) { // There we can caught any ours internal exceptions
						failures++;
					}
				}

				num++;
				writer.write(new COZRecord(fileId, num));		
				
				writer.close();
				
				File result = new File(getOutputFolder(), file.getName());
				file.renameTo(result);
				
				journalService.registerFinishing(jr);
				
				PrintWriter log = new PrintWriter(new File(getOutputFolder(), file.getName().replaceAll("\\.txt", ".log")));
				logSuccess(log, success, failures, result.getName());
			} catch (FileNotFoundException e) {
				if (null != file) {
					PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
					logFails(log, e.getMessage(), e);
				}
				
				logError(e.getMessage(), e);
				try {
					trd.getTransaction().setRollbackOnly();
				} catch (Throwable e1) {}
			} catch (Exception e) {
				if (null != file) {
					PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
					logFails(log, e.getMessage(), e);
				}
				
				PrintWriter log = new PrintWriter(new File(getErrorFolder(), file.getName().replaceAll("\\.txt", ".log")));
				
				logFails(log, e.getMessage(), e);
				logError(e.getMessage(), e);

				if (null != file) {
					try {
						file.renameTo(new File(getErrorFolder(), file.getName()));
					} catch (Throwable e1) { 	}
				}
				
				if (null != jr) {
					try {
						journalService.registerFails(jr);
					} catch (Throwable e1) { 	}
				}
				
				try {
					trd.getTransaction().setRollbackOnly();
				} catch (Throwable e1) {}
			} finally {
				trd.end();
			}
		} catch (TransactionDemarcationException e1) {
			logError(e1.getMessage(), e1);
		}
	}

	private String getNextFileName(JournalItem jr) {
		DateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmss");
		
		return getFilePrefix() + df.format(new Date()) + ".txt";
	}

	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#doStartService()
	 */
	@Override
	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob(getClass().getSimpleName(),
				"Export contact messages",
				getAbsoluteName(),
				getSchedule(),
				this,
				ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
		
		super.doStartService();
	}
	
	/* (non-Javadoc)
	 * @see atg.nucleus.GenericService#doStopService()
	 */
	@Override
	public void doStopService() throws ServiceException {
		if (jobId > 0) {
			getScheduler().removeScheduledJob(jobId);
		}
		
		super.doStopService();
	}

	/* (non-Javadoc)
	 * @see atg.service.scheduler.Schedulable#performScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
		try {
			exportMessages();
		} catch (Throwable e) {
			logError(e);
		} 
	}
	
	private File getErrorFolder() {
		File result = new File(getRootDirFile(), "error");
			
		if (!result.exists()) {
			result.mkdir();
		}
			
		return result;
	}

	private File getWorkFolder() {
		File result = new File(getRootDirFile(), "working");
			
		if (!result.exists()) {
			result.mkdir();
		}
			
		return result;
	}

	private File getRootDirFile() {
		if (!rootFileFolder.exists()) {
			rootFileFolder.mkdir();
		}
		
		return rootFileFolder;
	}
	
	private File getOutputFolder() {
		File result = new File(getRootDirFile(), "output");
		
		if (!result.exists()) {
			result.mkdir();
		}
		
		return result;
	}

	public static void logFails(PrintWriter log, String message, Throwable t) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Export Date: " + df.format(new Date()));
		log.println("Status: Failed");
		log.println("Successfully exported messages: 0");
		log.println(message);
		t.printStackTrace(log);
		
		log.flush();
		log.close();
	}
	
	private static void logSuccess(PrintWriter log, List<String> success, int failures, String resultFile) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Export Date: " + df.format(new Date()));
		if (failures > 0) {
			log.println("Status: Failed");
		} else {
			log.println("Status: Success");
		};
		
		log.println("Successfully exported records: " + success.size());
		if (failures > 0) {
			log.println("Exported id:");
			for (String string : success) {
				log.println("" + string);
			}
		}
		
		log.println("Number of not exported records: " + failures);
		log.println("File to Export: " + resultFile);
		
		log.flush();
		log.close();
	}
}
