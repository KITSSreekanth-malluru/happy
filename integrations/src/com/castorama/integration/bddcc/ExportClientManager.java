/**
 * Manager specifying the rules for managing the creation of XML files.
 */
package com.castorama.integration.bddcc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import atg.adapter.gsa.query.Builder;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.Constants;
import com.castorama.integration.backoffice.exp.UtilFormat;
import com.castorama.integration.util.MiscUtils;

public class ExportClientManager extends GenericService implements Schedulable {

    /**
     *
     */
    private static final int DAY_LENGTH = 1000 * 60 * 60 * 24;

    /**
     *
     */
    private static final String USER_ITEM_DESCRIPTOR_NAME = "user";

    private static final String MODE_NORMAL = "N";
    private static final String MODE_REPEAT = "R";
    /**
     * SQLs to count items to extract for defined Date
     */
    // For Normal mode LAST_BDDCC_EXTRACTION should be taken into account.     
    private static final String EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSEN = "((cu.datemajprofil >= ? AND cu.datemajprofil < ?) and ((TRUNC(ca.LAST_BDDCC_EXTRACTION, 'DDD') != TRUNC(cu.datemajprofil, 'DDD'))or (ca.LAST_BDDCC_EXTRACTION is NULL))) OR ((ca.datedernieremodification >= ? AND ca.datedernieremodification < ?) and ((TRUNC(ca.LAST_BDDCC_EXTRACTION, 'DDD') != TRUNC(ca.datedernieremodification, 'DDD')) or (ca.LAST_BDDCC_EXTRACTION is NULL)))";
    private static final String EXTRACT_NEWSLETTER_ITEMS_ALL_WHERE_CLAUSEN = "((cu.datemajprofil >= ? and ( (TRUNC(ca.LAST_BDDCC_EXTRACTION, 'DDD') != TRUNC(cu.datemajprofil, 'DDD')) or (ca.last_bddcc_extraction is NULL))) or (ca.datedernieremodification >=? and ((TRUNC(ca.LAST_BDDCC_EXTRACTION, 'DDD') != TRUNC(ca.datedernieremodification, 'DDD'))or(ca.last_bddcc_extraction is NULL))))";
    //For Repeat mode -	Exporting Clients of Castorama.fr which have been created/ updated on Castorama.fr from date specified in startDate property should be extracted
    private static final String EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSER = "((cu.datemajprofil >= ? AND cu.datemajprofil < ?) OR (ca.datedernieremodification >= ? AND ca.datedernieremodification < ?))";
    private static final String EXTRACT_NEWSLETTER_ITEMS_ALL_WHERE_CLAUSER = "(cu.datemajprofil >= ? OR ca.datedernieremodification >= ?)";
    //SELECT is the same for normal and repeat modes    
    private static final String COUNT_NEWSLETTER_ITEMS_SELECT_CLAUSE = "SELECT count (ca.email) FROM casto_abonnement_newsletter ca LEFT JOIN casto_user cu ON ca.profile_id = cu.id LEFT JOIN dps_user du on cu.id = du.id WHERE ";
    //COUNT for normal mode
    private static final String COUNT_NEWSLETTER_ITEMS_BY_DATEN = COUNT_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSEN;
    private static final String COUNT_NEWSLETTER_ITEMS_ALLN = COUNT_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_ALL_WHERE_CLAUSEN;
    //COUNT for repeat mode
    private static final String COUNT_NEWSLETTER_ITEMS_BY_DATER = COUNT_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSER;
    private static final String COUNT_NEWSLETTER_ITEMS_ALLR = COUNT_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_ALL_WHERE_CLAUSER;
    /**
     *SQLs to extract items for defined Date
     */
    private static final String GET_NEWSLETTER_ITEMS_SELECT_CLAUSE = "SELECT ca.email FROM casto_abonnement_newsletter ca LEFT JOIN casto_user cu ON ca.profile_id = cu.id LEFT JOIN dps_user du on cu.id = du.id WHERE ";
    private static final String GET_NEWSLETTER_ITEMS_ORDER_BY_CLAUSE = "ORDER BY ca.email";
    
    //Extraction of items performed only for specified date. The only difference is in mode 
    private static final String GET_NEWSLETTER_ITEMS_BY_DATEN = GET_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSEN + GET_NEWSLETTER_ITEMS_ORDER_BY_CLAUSE;    
    private static final String GET_NEWSLETTER_ITEMS_BY_DATER = GET_NEWSLETTER_ITEMS_SELECT_CLAUSE + EXTRACT_NEWSLETTER_ITEMS_BY_DATE_WHERE_CLAUSER + GET_NEWSLETTER_ITEMS_ORDER_BY_CLAUSE;

    private String COUNT_NEWSLETTER_ITEMS_ALL = COUNT_NEWSLETTER_ITEMS_ALLN;
    private String GET_NEWSLETTER_ITEMS_BY_DATE = GET_NEWSLETTER_ITEMS_BY_DATEN;
    
    
    private static final String PREFIX_FILE_NAME = "SCDXMUIY_MGN_0001_";

    private static final String START_EXPORT = "<exportEComClient xsi:noNamespaceSchemaLocation='exportEComClient.xsd'"
            + " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>";
    private static final String END_EXPORT = "</exportEComClient>";
    private static final String XML_VERSION = "<?xml version='1.0' encoding='ISO-8859-1' ?>";

    private static final String TAG_DATE = "<date>";
    private static final String END_TAG_DATE = "</date>";
    private static final String TAG_INCR = "<incr>";
    private static final String END_TAG_INCR = "</incr>";

    private static final String GET_LAST_EXECUTION_DATE = "ALL ORDER BY startDate SORT DESC RANGE 0+1";

    private static final String SUBSCRIPTION_ITEM_DESC = "abonnementNewsletter";
    private static final String BDDCC_ITEM_DESC = "bddccExecution";

    private int mPortionSize = 5000;

    private File workingDir;
    private File outputDir;
    private File errorDir;
    private File archiveDir;

    /* dataSource property */
    private DataSource dataSource;

    /**
     * in progress flag property
     */
    private boolean mInProgress = false;


    public ExportClientManager() {
    }

    // date repeat - format yyyyMMdd
    private String startDate;

    /**
     * @return string date repeat
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * set date repeat
     *
     * @param startDate the date repeat
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    // profile repository
    private Repository repository = null;

    /**
     * The ProfileAdapterRepository that holds the information
     *
     * @return the ProfileAdapterRepository that holds the information
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @param repository set the ProfileAdapterRepository that holds the
     *            information
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    private Repository subscriptionRepository;

    public Repository getSubscriptionRepository() {
        return subscriptionRepository;
    }

    public void setSubscriptionRepository(Repository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    private String rootDir;

    /**
     * The home directory where files will be processed
     *
     * @return home directory where files will be processed
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * @param rootDir set home directory where files will be processed
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
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
     * @param scheduler this service's scheduler
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
     * @param schedule the schedule this service will run on
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isInProgress() {
        return mInProgress;
    }

    /**
     * This method is invoked according to the schedule.
     *
     * @param scheduler Scheduler object to perform the task.
     * @param job ScheduledJob object to be performed as a task.
     */
    public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
        try {
            exportClientsModeNormal();
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
     * @throws ServiceException If a service exception occurs.
     */
    public void doStartService() throws ServiceException {
        ScheduledJob job = new ScheduledJob("ExportCustomersService", "Export Customers - BDDCC", getAbsoluteName(),
                getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
        jobId = getScheduler().addScheduledJob(job);
    }

    /**
     * Stops listening for messages and disconnects from the queue.
     */
    public void doStopService() throws ServiceException {
        getScheduler().removeScheduledJob(jobId);
    }

    /**
     * Export clients in Normal mode.
     */
    public void exportClientsModeNormal() throws ServiceException {
        if (!mInProgress) {
            if (isLoggingInfo()) {
                logInfo("start BDDCC - ExportCustomerManager - exportClientsModeNormal");
            }
            try {
                mInProgress = true;
                exportClients(true);
            } finally {
                mInProgress = false;
            }

            if (isLoggingInfo()) {
                logInfo("finish BDDCC - ExportCustomerManager - exportClientsModeNormal");
            }
        } else {
            throw new ServiceException("Unable to start exportClientsModeNormal()... The service is being executing. Please wait...");
        }
    }

    /**
     * Export clients in Repeat mode.
     */
    public void exportClientsModeRepeat() throws ServiceException {
        if (!mInProgress) {
            if (isLoggingInfo()) {
                logInfo("start BDDCC - ExportCustomerManager - exportClientsModeRepeat");
            }
            try {
                mInProgress = true;
                exportClients(false);
            } finally {
                mInProgress = false;
            }
            if (isLoggingInfo()) {
                logInfo("finish BDDCC - ExportCustomerManager - exportClientsModeRepeat");
            }
        } else {
            throw new ServiceException("Unable to start exportClientsModeRepeat... The service is being executing. Please wait...");
        }
    }

    private void exportClients(boolean isNormal) {
        String mode;
        checkFolders();
        Date dateOfExtraction = new Date();
        File tmpDir = null;
        File statusFile = null;
        PrintWriter statusWriter = null;
        try {
            Date startServiceDate = new Date();
            if (isLoggingDebug()) {
            	logDebug("Service started. startServiceDate is " + startServiceDate.toString());
            }
            Date lastBDDCexecutionDate = getStartDateNormal();
            if (isLoggingDebug()) {
            	logDebug("lastBDDCexecutionDate is " + lastBDDCexecutionDate.toString());
            }

            int nmbClients = 0;
            Date startDate = null;

            if (isNormal) {
                mode = MODE_NORMAL;
                if (isLoggingDebug()) {
                    logDebug("Service started. Mode normal");
                }
                startDate = lastBDDCexecutionDate;
                if (isLoggingDebug()) {
                    logDebug("Last BDDC Execution Date " + lastBDDCexecutionDate);
                }
                //Change SQLs for NORMAL mode
                GET_NEWSLETTER_ITEMS_BY_DATE = GET_NEWSLETTER_ITEMS_BY_DATEN;
                COUNT_NEWSLETTER_ITEMS_ALL = COUNT_NEWSLETTER_ITEMS_ALLN;
            } else {
                mode = MODE_REPEAT;
                if (isLoggingDebug()) {
                    logDebug("Service started. Mode repeat");
                }
                String extractSince = getStartDate();
                if (extractSince == null) {
                    if (isLoggingDebug()) {
                        logDebug("Repeat mode. startDate value is null");

                    }
                    //If startDate value is null retrieve all users Mantis 3139.
                    extractSince = "20000101";
                }
                startDate = Constants.DATE_FORMAT_YYYYMMDD.parse(extractSince);
                if (isLoggingDebug()) {
                    logDebug("Repeat mode. startDate value is " + startDate);
                }

                //Change SQLs for REPEAT mode
                GET_NEWSLETTER_ITEMS_BY_DATE = GET_NEWSLETTER_ITEMS_BY_DATER;
                COUNT_NEWSLETTER_ITEMS_ALL = COUNT_NEWSLETTER_ITEMS_ALLR;
            }

            nmbClients = countUsersAndNewsletters(COUNT_NEWSLETTER_ITEMS_ALL, getDates(startDate, true));

            if (isLoggingDebug()) {
                logDebug(nmbClients + " clients  selected");
            }

            tmpDir = new File(workingDir, Long.toString(new Date().getTime()));
            tmpDir.mkdir();
            if (isLoggingDebug()) {
                logDebug("Start writing export file");
            }
            statusFile = new File(tmpDir, getFileName(startServiceDate, mode, -1, ".log"));
            statusWriter = new PrintWriter(new BufferedWriter(new FileWriter(statusFile, false)));
            statusWriter.println("Process start at: " + Constants.DATE_FORMAT_LOG.format(startServiceDate));
            statusWriter.println("Selected clients: " + nmbClients);
            Object[] itemParams = new Object[4];
            itemParams[0] = startServiceDate;
            itemParams[2] = nmbClients;
            int countAll = 0;

            if (nmbClients != 0) {

                int nmbFiles = getNmbFiles(nmbClients);

                Integer startPositionUsers = 0;

                ExportClient exportClient = new ExportClient();

                List<Date> listDates;
                if (isNormal) {
                    listDates = generateListDates(lastBDDCexecutionDate, startServiceDate);
                } else {
                    listDates = generateListDates(startServiceDate);
                }
                if (isLoggingDebug()) {
                	logDebug("listDates size is " + listDates.size());
                }

                int nmbSelItems = 0;
                int nmbSelUserByDate = 0;
                int cntDates = 0;
                Date selectDate = listDates.get(cntDates);
                for (int i = 1; i <= nmbFiles; i++) {

                    String chunkFile = tmpDir.getAbsoluteFile() + File.separator
                            + getFileName(startServiceDate, mode, i, ".xml");
                    PrintWriter printWriter = new PrintWriter(chunkFile, "ISO-8859-1");
                    printWriter.write(getHeaderFile(new Date(), i));
                    nmbSelItems = 0;
                    try {                    	
                        while (selectDate.compareTo(startServiceDate) < 0) {
                        	Date[] datesAsParams = getDates(selectDate,false);
                        	 if (isLoggingDebug()) {
                        		 logDebug("selectDate is " + selectDate.toString());
                                 logDebug("startServiceDate is " + startServiceDate.toString());
                             }
                            if (startPositionUsers == 0) {      
                            	String query = COUNT_NEWSLETTER_ITEMS_BY_DATEN;
                                if (!isNormal){
                                	query = COUNT_NEWSLETTER_ITEMS_BY_DATER;
                                }
                                nmbSelUserByDate = countUsersAndNewsletters(query, datesAsParams);
                                if(isLoggingDebug()){
                                	logDebug("nmbSelUserByDate  " + nmbSelUserByDate);
                                }
                            }

                            if (nmbSelUserByDate > 0) {

                                int portion = mPortionSize - nmbSelItems;
                                if (isLoggingDebug()) {
                                    logDebug("portion " + portion + " and mPortionSize " + mPortionSize);
                                }
                                int res = exportNewslettersByDate(datesAsParams, startPositionUsers, portion,
                                        printWriter, exportClient, countAll, nmbClients, dateOfExtraction, isNormal);
                                if (isLoggingDebug()) {
                                    logDebug("users nmbSelUserByDate = " + nmbSelUserByDate + " res = " + res);
                                    logDebug("users countAll = " + countAll + " " + selectDate);
                                }
                                nmbSelItems += res;
                                countAll += res;
                                if (isLoggingDebug()) {
                                    logDebug("users countAll = " + countAll + " " + selectDate);
                                }
                                if (nmbSelItems == mPortionSize) {
                                    startPositionUsers += portion;
                                    break;
                                } else {
                                    startPositionUsers = 0;

                                    if (cntDates < listDates.size() - 1) {
                                        cntDates++;
                                        selectDate = listDates.get(cntDates);
                                        startPositionUsers = 0;
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                startPositionUsers = 0;

                                if (cntDates < listDates.size() - 1) {
                                    cntDates++;
                                    selectDate = listDates.get(cntDates);
                                } else {
                                    break;
                                }
                            }
                        }

                        printWriter.write(END_EXPORT);
                        printWriter.close();

                        File workingFile = new File(chunkFile);
                        File outFile = new File(outputDir, workingFile.getName());
                        MiscUtils.copyFile(workingFile, outFile);
                        if (isLoggingDebug()) {
                        	logDebug("Finish writing export file");
                        }
                    } finally {
                        if (printWriter != null) {
                            printWriter.close();
                        }
                    }
                }
            }

            statusWriter.println("Processed clients: " + countAll);
            statusWriter.println("Duplicated clients: " + (countAll - nmbClients));
            Date finishDate = new Date();
            statusWriter.println("Process end at: " + Constants.DATE_FORMAT_LOG.format(finishDate));
            statusWriter.close();
            itemParams[1] = finishDate;
            itemParams[3] = countAll;
            addLastBDDCCExecutionDate(itemParams);

            try {
                MiscUtils.copyFile(statusFile, new File(archiveDir, statusFile.getName()));                
            } catch (Exception e) {
                logError(e);
            }
            MiscUtils.deleteFolder(tmpDir);

        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
            try {
                if (statusWriter != null) {
                    e.printStackTrace(statusWriter);
                    statusWriter.println("Process end at: " + Constants.DATE_FORMAT_STATUS.format(new Date()));
                    statusWriter.close();
                    MiscUtils.copyFile(statusFile, new File(errorDir, statusFile.getName()));
                }
            } catch (Exception ex) {
                logError(ex);
            }
            if (tmpDir != null) {
                MiscUtils.deleteFolder(tmpDir);
            }
        } finally {
            if (statusWriter != null) {
                statusWriter.close();
            }
        }
    }

    private Date[] getDates(Date dateToExtract, boolean isByDate) {
        Date[] queryParameters;
        java.sql.Date firstDate = new java.sql.Date(getExtractTime(dateToExtract));
        if (isByDate) {
            queryParameters = new Date[]{firstDate, firstDate};
            if (isLoggingDebug()) {
                logDebug("By Date: " + queryParameters[0] + " and " + queryParameters[1]);
            }
        } else {
            java.sql.Date secondDate = new java.sql.Date(getExtractTime(dateToExtract) + DAY_LENGTH);
            queryParameters = new Date[]{firstDate, secondDate, firstDate, secondDate};
            if (isLoggingDebug()) {
                logDebug("By date is false: " + queryParameters[0] + " and " + queryParameters[1] + " and " + queryParameters[2] + " and " + queryParameters[3]);
            }
        }
        return queryParameters;
    }
    
    private long getExtractTime(Date dateToExtract){
        return dateToExtract.getTime();
       }

    private int exportNewslettersByDate(Date[] queryParameters, int startPosition, int portion,
                                        PrintWriter printWriter, ExportClient exportClient, int countAll, int nmbClients, Date dateOfExtraction, boolean isNormal)
            throws RepositoryException {
        int result = 0;

        RepositoryItem[] items = getNewsletterRepositoryItemsNew(queryParameters, startPosition, portion);
        if (items != null && items.length > 0) {
            if (isLoggingDebug()) {
                logDebug("Start exportNewLetterByDate: items size " + items.length + " and queryParameters length  " + queryParameters.length + " portion " + portion);
            }
            result = items.length;
            boolean lastItem = false;
            if (isLoggingDebug()) {
                logDebug("Start record to file: ");
            }
            for (int j = 0; j < items.length; j++) {
                if (countAll + j + 1 == nmbClients) {
                    if (isLoggingDebug()) {
                        logDebug(countAll + " + " + (j + 1) + " == " + nmbClients
                                + " (countAll + j == nmbClients)");
                    }
                    lastItem = true;
                }
                RepositoryItem subscrItem = items[j];
                String profileId = (String) subscrItem.getPropertyValue("profile");
                RepositoryItem item = getGetUserById(profileId);
                String xml = exportClient.parseClient(item, subscrItem, null, lastItem, dateOfExtraction);
                printWriter.write(xml);
                if (isNormal) {
                    addLastBDDCExtractionDate(subscrItem);
                }
                if (countAll + j + 1 == nmbClients) {
                    result = j + 1;
                    break;
                }
            }
        }
        return result;
    }

    private List<Date> generateListDates(Date currentDate) throws ParseException {
        Date start = Constants.DATE_FORMAT_YYYYMMDD.parse(getStartDate());
        return generateListDates(start, currentDate);
    }

    private List<Date> generateListDates(Date lastBDDCExecutionDate, Date currentDate) throws ParseException {
        List<Date> listDates = new ArrayList<Date>();
        if (lastBDDCExecutionDate.after(currentDate)) {
            throw new IllegalArgumentException("Start date is invalid: " + lastBDDCExecutionDate);
        }
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.setTime(currentDate);
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(lastBDDCExecutionDate);
        while (calStart.compareTo(calCurrent) < 0) {
            listDates.add(calStart.getTime());
            calStart.add(Calendar.DATE, 1);
          }
        if (isLoggingDebug()) {
            logDebug("List Dates size: " + listDates.size());
        }
        return listDates;
    }

    // subscribes
    private RepositoryView getNewsletterRepositoryView() throws RepositoryException {
        Repository repos = getSubscriptionRepository();
        return repos.getView(SUBSCRIPTION_ITEM_DESC);
    }

    private int countUsersAndNewsletters(String query, Date[] params) throws RepositoryException {
        Connection con = null;
        PreparedStatement countPreparedStatement = null;
        if (isLoggingDebug()) {
            logDebug("Start counting users and newsletters with query " + query);
        }
        try {
            con = getDataSource().getConnection();
            countPreparedStatement = con.prepareStatement(query);
            ResultSet rs = null;
            try {
                for (int i = 0; i < params.length; i++) {
                    countPreparedStatement.setDate(i + 1, (java.sql.Date) params[i]);
                    if (isLoggingDebug()) {
                        logDebug("Start countUsersAndNewsletters. Date Parameters " + params[i]);
                    }
                }

                rs = countPreparedStatement.executeQuery();

                int count = 0;
                if (rs.next()) {
                    count = rs.getInt(1);
                }
                return count;
            } catch (SQLException sqlException) {
                if (isLoggingError()) {
                    logError("SQLException during executing query: " + sqlException);
                }
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        if (isLoggingError()) {
                            logError("Unable to close result set." + e);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            if (isLoggingError()) {
                logError("SQLException during repare statement: " + e);
            }
        } finally {
            close(countPreparedStatement);
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    if (isLoggingError()) {
                        logError("Unable to close connection" + e);
                    }
                }
            }
            if (isLoggingDebug()) {
                logDebug("Finish counting users and newsletters");
            }
        }

        return 0;
    }

    private RepositoryItem[] getNewsletterRepositoryItemsNew(Object[] params, int start, int portion) {
        RepositoryView view;
        RepositoryItem[] temp = null;
        try {
            view = getNewsletterRepositoryView();
            Builder builder = (Builder) view.getQueryBuilder();
            temp = view.executeQuery(builder.createSqlPassthroughQuery(GET_NEWSLETTER_ITEMS_BY_DATE, params), start, start + portion);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("Can't return newsletters.", e);
            }
        }
        return temp;
    }

    private RepositoryItem getGetUserById(String userId) throws RepositoryException {
        if (userId == null || userId.length() == 0) {
            return null;
        }
        return getRepository().getItem(userId, USER_ITEM_DESCRIPTOR_NAME);
    }

    private int getNmbFiles(int nmbClients) {
        int nmbFiles = nmbClients / mPortionSize;
        if (isLoggingDebug()) {
            logDebug("getNmbFiles nmFiles" + nmbFiles + "nmbClients" + nmbClients);
        }
        if ((nmbClients % mPortionSize) > 0) {
            nmbFiles++;
        }
        if (isLoggingDebug()) {
            logDebug("Finally  nmbFiles " + nmbFiles);
        }
        return nmbFiles;
    }

    private void checkFolders() {
        File rootDir = new File(getRootDir());
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        workingDir = new File(rootDir, Constants.WORKING_FOLDER);
        if (!workingDir.exists()) {
            workingDir.mkdir();
        }

        outputDir = new File(rootDir, Constants.OUTPUT_FOLDER);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        errorDir = new File(rootDir, Constants.ERROR_FOLDER);
        if (!errorDir.exists()) {
            errorDir.mkdir();
        }

        archiveDir = new File(rootDir, Constants.ARCHIVE_FOLDER);
        if (!archiveDir.exists()) {
            archiveDir.mkdir();
        }
    }

    private String getFileName(Date date, String mode, int nmbChunk, String ext) {
        StringBuilder sb = new StringBuilder(PREFIX_FILE_NAME).append(mode);
        if (nmbChunk > 0) {
            sb.append("_").append(UtilFormat.formatString(Integer.toString(nmbChunk), 6, '0', false));
        }
        sb.append("_").append(Constants.DATE_FORMAT_YYYYMMDD.format(date)).append(ext);
        if (isLoggingDebug()) {
            logDebug("Start writing " + sb.toString() + " file");
        }
        return sb.toString();
    }

    private String getHeaderFile(Date date, int nmbFile) {
        return new StringBuilder().append(XML_VERSION).append(Constants.LINE_SEPARATOR).append(START_EXPORT)
                .append(Constants.LINE_SEPARATOR).append(TAG_DATE)
                .append(Constants.DATE_FORMAT_BDDCC_HEADER.format(date)).append(END_TAG_DATE)
                .append(Constants.LINE_SEPARATOR).append(TAG_INCR).append(nmbFile).append(END_TAG_INCR)
                .append(Constants.LINE_SEPARATOR).toString();
    }

    /**
     * @return portion size
     */
    public int getPortionSize() {
        return mPortionSize;
    }

    /**
     * set portion size
     *
     * @param pPortionSize the portion size
     */
    public void setPortionSize(int pPortionSize) {
        mPortionSize = pPortionSize;
    }

    /**
     * @return start date from last successful bddccExecution item if do not any
     *         bddccExecution items
     * @throws RepositoryException
     */
    private Date getStartDateNormal() throws RepositoryException {
        Date lastExecDate = getLastBDDCCExecutionDate();
        if (lastExecDate == null) {
            lastExecDate = getYesterdaysMidnightTime();
        }
        return lastExecDate;
    }

    /**
     * @return yesterday midnight time
     */
    private Date getYesterdaysMidnightTime() {
        Date midnightTime = null;
        Date currentDate = new Date();
        midnightTime = getMidnightTime(currentDate);
        midnightTime.setTime(midnightTime.getTime() - DAY_LENGTH);
        return midnightTime;
    }

    private Date getMidnightTime(Date date) {
        Date midnightTime = null;
        try {
            midnightTime = Constants.DATE_FORMAT_YYYYMMDD.parse(Constants.DATE_FORMAT_YYYYMMDD.format(date));
        } catch (ParseException e) {
            if (isLoggingError()) {
                logError("Can't parse date string.", e);
            }
        }
        return midnightTime;
    }

    /**
     * Returns startDate of bddccExecution item from newsletterRepository which
     * start date is the biggest
     *
     * @return start date
     * @throws RepositoryException
     */
    private Date getLastBDDCCExecutionDate() throws RepositoryException {
        RepositoryView view = getSubscriptionRepository().getView(BDDCC_ITEM_DESC);
        RqlStatement statment = RqlStatement.parseRqlStatement(GET_LAST_EXECUTION_DATE);
        RepositoryItem[] items = statment.executeQueryUncached(view, new Object[]{});
        if (items != null && items.length > 0) {
            RepositoryItem temp = items[0];
            Object objDate = temp.getPropertyValue("startDate");
            if (objDate instanceof Date) {
                return (Date) objDate;
            }
        }
        return null;
    }

    private void addLastBDDCCExecutionDate(Object[] params) throws RepositoryException {
        MutableRepository mutableRepos = (MutableRepository) getSubscriptionRepository();
        MutableRepositoryItem bddccItem = mutableRepos.createItem(BDDCC_ITEM_DESC);
        bddccItem.setPropertyValue("startDate", params[0]);
        bddccItem.setPropertyValue("endDate", params[1]);
        bddccItem.setPropertyValue("selectedClients", params[2]);
        bddccItem.setPropertyValue("processedClients", params[3]);
        mutableRepos.addItem(bddccItem);
    }
    
    
    private void addLastBDDCExtractionDate(RepositoryItem items) throws RepositoryException{
    	MutableRepository mutableRepos=(MutableRepository)getSubscriptionRepository();
    	MutableRepositoryItem subscrItem ;
    	if( items !=null){
    	  	  subscrItem= mutableRepos.getItemForUpdate(items.getRepositoryId(),SUBSCRIPTION_ITEM_DESC);
    		  subscrItem.setPropertyValue("lastBDDCCExtraction",new Date());
    		  mutableRepos.updateItem(subscrItem);
    	  
       }
    }

    /* */
    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }
        }
    }

}
