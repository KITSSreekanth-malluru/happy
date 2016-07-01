package com.castorama.integration.experian;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import atg.nucleus.ServiceException;

import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.SMTPEmailSender;

import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryView;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;

import atg.repository.rql.RqlStatement;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import javax.transaction.SystemException;

import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.model.Abonnement;

/**
 * @author Mikalai_Khatsko
 */
public class ExportNewsletterManager extends IntegrationBase implements Schedulable {

    /* Newsletter repository information */
    private static final String TO_SEND_NEWSLETTER_ITEM_DESCRIPTOR = "experianRequest";
    private static final String FULL_GENERATION_ITEM_DESCRIPTOR = "abonnementNewsletter";
    private static final String SOURCE_FULL = "full";

    /* Newsletter status file information */
    private static final String STATUS_FILE_NAME = "Supervision_CSV_Experian.html";
    private static final String STATUS_FILE_HEADER = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><TITLE>Supervision generation CSV pour Experian</TITLE><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body>Supervision generation CSV pour Experian<br/>";
    private static final String STATUS_FILE_FOOTER = "</body></html>";
    private static final String STATUS_FILE_NEW_LINE = "<br/>";
    private static final String STATUS_FILE_DATE = "DATE : ";
    public static final SimpleDateFormat STATUS_FILE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final String STATUS_FILE_RESULTAT = "RESULTAT : ";
    private static final String STATUS_FILE_RESULTAT_OK = "OK";
    private static final String STATUS_FILE_RESULTAT_KO = "KO";
    /** Data file extension constant */
    public static final String DATA_FILE_EXTENSION = ".csv";

    /* Newsletter data file transfer information email */
    public static final SimpleDateFormat EMAIL_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat EMAIL_HOUR_FORMAT = new SimpleDateFormat("HH:mm");

    // TODO EMAIL_NUMBER_OF_RECORDS_MIN_DIGITS?
    private static final int EMAIL_NUMBER_OF_RECORDS_MIN_DIGITS = 2;




    
    /* keepInHistoryDays property */
    private int keepInHistoryDays;

    /* emailSender property */
    private SMTPEmailSender emailSender;

    /* emailRecipients property */
    private String[] emailRecipients;

    /* scheduler property */
    private Scheduler scheduler;

    /* schedule property */
    private Schedule schedule;

    /* jobId property */
    private int jobId;

    /* inProgress flag property */
    private boolean inProgress = false;

    /* inProgress flag property */
    private boolean inProgressFuul = false;

    /* emailFrom property */
    private String emailFrom;

    /* emailSubject property */
    private String emailSubject;

    /* profileRepository property. */
    private Repository mProfileRepository;

    /* emailMessage property */
    private String emailMessage;

    /* sendEmail property */
    private boolean sendEmail;
    
    /* batchSize property */
    private int batchSize;

    public int getKeepInHistoryDays() {
        return keepInHistoryDays;
    }

    public void setKeepInHistoryDays(int keepInHistoryDays) {
        this.keepInHistoryDays = keepInHistoryDays;
    }

    public SMTPEmailSender getEmailSender() {
        return emailSender;
    }

    public String[] getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(String[] emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    public void setEmailSender(SMTPEmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }
    
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Returns profileRepository property.
     * 
     * @return profileRepository property.
     */
    public Repository getProfileRepository() {
        return mProfileRepository;
    }

    /**
     * Sets the value of the profileRepository property.
     * 
     * @param pProfileRepository parameter to set.
     */
    public void setProfileRepository(Repository pProfileRepository) {
        mProfileRepository = pProfileRepository;
    }

    /**
     * Connects to the queue and starts listening for messages.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob("ExportNewsletterManager",
                "Exports data from NewsletterRepository to Experian", getAbsoluteName(), getSchedule(), this,
                ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);

        if (isLoggingDebug()) {
            logDebug("ExportNewsletterManager service started. Job id : " + jobId);
        }
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     * 
     * @throws ServiceException If a service exception occurs.
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(jobId);

        if (isLoggingDebug()) {
            logDebug("ExportNewsletterManager service stopped. Job id : " + jobId);
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     * 
     * @param scheduler ToDo: DOCUMENT ME!
     * @param job ToDo: DOCUMENT ME!
     */
    public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
        if (isLoggingInfo()) {
            logInfo("Perform sheduled task. Job name : " + job.getJobName());
        }
        try {
            exportNewsletters();
        } catch (ServiceException se) {
            if (isLoggingError()) {
                logError(se);
            }
        }
    }

    public void createRequestsForAllAccounts() {
        try {
            if (inProgressFuul) {
                throw new ServiceException(
                        "Unable to start creation requests for all accounts... The service is already started. Please wait...");
            }

            if (isLoggingDebug()) {
                logDebug("start - ExportNewsletterManager - createRequestsForAllAccounts");
            }

            int startIndex = 0; 
            int batchSize = getBatchSize();
            int counter = 0;

            try {
            	inProgressFuul = true;
	            TransactionDemarcation trd = new TransactionDemarcation();
	
	            MutableRepository newsletterRepository = (MutableRepository) getRepository();
	            RqlStatement newslettersRql = RqlStatement.parseRqlStatement("receiveEmail = true RANGE?0+?1");
	            RepositoryView newslettersView = newsletterRepository.getView(FULL_GENERATION_ITEM_DESCRIPTOR);
	           	
	            RepositoryItem[] newslettersList = null;
	                
	            do {
	            	try {
		            	trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);
		            	Object[] params = new Object[]{startIndex, batchSize};

		            	newslettersList = newslettersRql.executeQuery(newslettersView, params);
		            		
	                    if (newslettersList != null){
	                    	for (RepositoryItem newsletter : newslettersList) {
	                    		Abonnement abonnement = new Abonnement(newsletter);
	                            List<Object> properties = ExperianUtils.createValuesList(abonnement, SOURCE_FULL, getProfileRepository());
	                            ExperianUtils.createAndAddToRepository(properties, newsletterRepository);
	                            counter++;
	                        }
		                     startIndex += batchSize;
	                    }
	                 } catch (RepositoryException e) {
	                	 if (isLoggingError()) logError(e);
	                 } finally {
	                     trd.end();
	                 }           		
	            } while (newslettersList != null && newslettersList.length == batchSize);
	            
            } catch (TransactionDemarcationException trde) {
                if (isLoggingError()) logError(trde);
            } catch (RepositoryException e) {
           	 	if (isLoggingError()) logError(e);
            } finally {
            	if (isLoggingDebug()) logDebug("Were processed " + counter + " newsletter accounts.");
            	inProgressFuul = false;
            }
            if (isLoggingDebug()) logDebug("finish - ExportNewsletterManager - createRequestsForAllAccounts");
        } catch (ServiceException se) {
            if (isLoggingError()) {
                logError(se);
            }
        }
    }

    /** */
    public void exportNewsletters() throws ServiceException {
        if (inProgress) {
            throw new ServiceException(
                    "Unable to start newsletter export... The service is already started. Please wait...");
        }

        if (isLoggingDebug()) {
            logDebug("start - ExportNewsletterManager - exportNewsletters");
        }

        inProgress = true;

        try {
            generateDataFile();
            clearHistory();
        } finally {
            inProgress = false;
        }

        if (isLoggingDebug()) {
            logDebug("finish - ExportNewsletterManager - exportNewsletters");
        }
    }

    /**
     * generates data file, stores it in send directory and moves all data to
     * history table
     */
    public void generateDataFile() {

        if (isLoggingDebug()) {
            logDebug("Generating CSV file...");
        }

        int recordsExported = 0;

        String result = null;
        File csvFile = null;

        MutableRepository mutNewsletterRepository = (MutableRepository) getRepository();

        try {
            TransactionDemarcation trd = new TransactionDemarcation();
            trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

            try {
                File sendDir = getDir(Constants.OUTPUT_FOLDER);

                // get all records

                RqlStatement requestsRql = RqlStatement.parseRqlStatement("requestSource != 2");
                RepositoryView requestsView = mutNewsletterRepository.getView(TO_SEND_NEWSLETTER_ITEM_DESCRIPTOR);

                RepositoryItem[] requestsList = requestsRql.executeQuery(requestsView, new Object[] {});

                // loop through the list, add records to file and move records
                // to history

                if ((requestsList != null) && (requestsList.length > 0)) {

                    csvFile = new File(sendDir, Constants.DATA_FILE_NAME_PREFIX + Constants.DATA_FILE_NAME_DATE_FORMAT.format(new Date())
                            + DATA_FILE_EXTENSION);

                    if (csvFile.createNewFile()) {

                        result = STATUS_FILE_RESULTAT_KO;

                        if (isLoggingInfo()) {
                            logInfo("Start writing of csv file : " + csvFile.getName() + " . Records to write : "
                                    + requestsList.length);
                        }

                        recordsExported = ExperianUtils.printToDataFile(csvFile, requestsList);
                        if (isLoggingInfo()) {
                            logInfo("Writing of csv file finished : " + csvFile.getName() + " . Number of records: "
                                    + recordsExported);
                        }

                        result = STATUS_FILE_RESULTAT_OK;
                        sendTransferInformationEmail(new Date(), recordsExported);
                    }
                }
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }

                if (csvFile != null) {
                    csvFile.delete();
                }

                try {
                    getTransactionManager().setRollbackOnly();
                } catch (SystemException se) {
                    if (isLoggingError()) {
                        logError(se);
                    }
                }
            } finally {
                trd.end();
            }
        } catch (TransactionDemarcationException trde) {
            if (isLoggingError()) {
                logError(trde);
            }
        }

        // TODO result situations!!!
        if (result != null) {
            generateStatusFile(new Date(), result);
        }

        if (isLoggingDebug()) {
            logDebug("CSV file generated. Records number = " + recordsExported);
        }
    }

    /** clears history table from old records */
    public void clearHistory() {
        if (isLoggingDebug()) {
            logDebug("Cleaning history...");
        }

        int recordsCleaned = 0;

        MutableRepository mutNewsletterRepository = (MutableRepository) getRepository();

        try {
            // find all records older than keepInHistoryDays

            RqlStatement oldNewslettersRql = RqlStatement.parseRqlStatement(ExperianUtils.EXPORT_DATE_PROPERTY + " < ?0");
            RepositoryView newslettersView = mutNewsletterRepository.getView(ExperianUtils.HISTORY_NEWSLETTER_ITEM_DESCRIPTOR);

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.add(GregorianCalendar.DATE, -keepInHistoryDays);
            Object rqlparams[] = new Object[1];
            rqlparams[0] = calendar.getTime();

            RepositoryItem[] oldNewslettersList = oldNewslettersRql.executeQuery(newslettersView, rqlparams);

            // loop through the list, and delete the old records

            if (oldNewslettersList != null) {
                if (isLoggingDebug()) {
                    logDebug("Records to clean : " + oldNewslettersList.length);
                }

                for (int i = 0; i < oldNewslettersList.length; i++) {
                    mutNewsletterRepository.removeItem(oldNewslettersList[i].getRepositoryId(),
                            ExperianUtils.HISTORY_NEWSLETTER_ITEM_DESCRIPTOR);
                    recordsCleaned++;
                }
            }
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError(re);
            }
        }

        if (isLoggingDebug()) {
            logDebug("History cleaned. Cleaned records number = " + recordsCleaned);
        }
    }

    /**
     * @param date
     * @param result
     */
    private void generateStatusFile(Date date, String result) {

        if (isLoggingDebug()) {
            logDebug("Generating status file...");
        }

        PrintWriter statusFileWriter = null;
        try {
            File statusDir = getDir(Constants.STATUS_FOLDER);
            File statusFile = new File(statusDir, STATUS_FILE_NAME);
            statusFileWriter = new PrintWriter(statusFile);

            StringBuilder sb = new StringBuilder();
            sb.append(STATUS_FILE_HEADER);
            sb.append(STATUS_FILE_DATE).append(STATUS_FILE_DATE_FORMAT.format(date)).append(STATUS_FILE_NEW_LINE);
            sb.append(STATUS_FILE_RESULTAT).append(result).append(STATUS_FILE_NEW_LINE);
            sb.append(STATUS_FILE_FOOTER);
            statusFileWriter.println(sb.toString());
        } catch (IOException ioe) {
            if (isLoggingError()) {
                logError("Can't generate status file: ", ioe);
            }
        } finally {
            if (statusFileWriter != null) {
                statusFileWriter.close();
            }
        }

        if (isLoggingDebug()) {
            logDebug("Status file generated.");
        }
    }

    /* */
    private void sendTransferInformationEmail(Date date, int numberOfRecords) {

        if (isLoggingDebug()) {
            logDebug("Sending transfer information email...");
        }

        if ((sendEmail) && (emailRecipients != null) && (emailRecipients.length > 0)) {

            if (date == null) {
                date = new Date();
            }

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumIntegerDigits(EMAIL_NUMBER_OF_RECORDS_MIN_DIGITS);

            Object[] args = { EMAIL_DATE_FORMAT.format(date), EMAIL_HOUR_FORMAT.format(date),
                    nf.format(numberOfRecords) };

            String messageBody = null;
            try {
                messageBody = MessageFormat.format(emailMessage, args);
            } catch (IllegalArgumentException iae) {
                if (isLoggingError()) {
                    logError("Problem with email message body: " + emailMessage);
                }
            }

            if (messageBody != null) {

                String from = getEmailFrom();
                if (from == null || from.length() == 0) {
                    if (isLoggingWarning()) {
                        logWarning("Email field \"from\" is empty.\n Trying to send email with empty field \"from\"...");
                    }
                }
                String subject = getEmailSubject();
                if (subject == null || subject.length() == 0) {
                    if (isLoggingWarning()) {
                        logWarning("Email field \"subject\" is empty.\n Trying to send email with empty field \"subject\"...");
                    }
                }

                EmailEvent emailEvent = new EmailEvent(from, emailRecipients, subject, messageBody);
                try {
                    emailSender.sendEmailEvent(emailEvent);
                } catch (EmailException ee) {
                    if (isLoggingError()) {
                        logError("Can't send email: ", ee);
                    }
                }
            }
        }

        if (isLoggingDebug()) {
            logDebug("Transfer information email send.");
        }
    }
}
