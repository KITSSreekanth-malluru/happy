/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.integration.ticket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.mail.Message;
import javax.mail.MessagingException;

import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.MimeMessageUtils;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.castorama.commerce.pricing.CastVATManager;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;
import com.castorama.purchasehistory.PurchaseHistoryConfiguration;


/**
 * Service for import "Purchase History".
 *
 * @author Vasili Ivus
  */
public class ImportTicketManager extends IntegrationBase implements Schedulable {

  public static final String ALERT_MISSING_IMPORT = "You have not any import for {0} days.";
  
  private boolean mInProgress = false;

  private StringBuffer mStatusAlertInfo = null;
  private boolean mStatusOfImport = true;
    private CastVATManager mCastVATManager;
  
  public static final String SERVICE_NAME = "ImportTicketManagerService";
  
  public static final String SERVICE_DESCRIPTION = "Import users order history";

  /**
   * Creates a new ImportTicketManager object.
   */
  public ImportTicketManager() {
  }

  /**
   * This is called when a scheduled job tied to this object occurs.
   *
   * @param pScheduler - scheduler calling the job
   * @param pScheduledJob - called the job
   */
  public void performScheduledTask(final Scheduler pScheduler, final ScheduledJob pScheduledJob) {
    if ( mConfiguration.isEnabled() ) {
      try {
        checkLastImport(importTickets());
      } catch(Exception e) {
        if( isLoggingError() ) {
          logError(e);
        }
      }
    } else {
      if ( isLoggingWarning() ) {
        logWarning("Import order history job hasn't started, because it disabled.");
      }
    }
  }

  /**
   * Write start service date in statistic.
   */
  private void writeServiceStartStatistic() {
    new ParseTicketObject(ParseTicketable.START, SERVICE_NAME, getRepository(), this).finish();
  }

  /**
   * Write start service date in statistic.
   */
  private void checkLastImport(boolean isImported) {
    if ( !isImported ) {
      ParseTicketObject checkParseObject = new ParseTicketObject(ParseTicketable.START, SERVICE_NAME, getRepository(), this);
      Integer daysBeforeNotify = mConfiguration.getDaysBeforeNotifyMissingImport();
      if ( !checkParseObject.checkLastImport(daysBeforeNotify) ) {
        sendAlert(ALERT_MISSING_IMPORT.replace("{0}", daysBeforeNotify.toString()));
      }
    } 
  }
  
  /**
   * Imports order history.
   * 
   * @throws ServiceException - throws if trying to start import while previous import hasn't finished.
   */
  public boolean importTickets() throws ServiceException {
    boolean result = false;
    if (mInProgress) {
      throw new ServiceException("Unable to start import... The service is being executing. Please wait...");
    }
    
    if(isLoggingInfo()) {
      logInfo("Import tickets started...");
    }
    
    try {
      mInProgress = true;
      mStatusAlertInfo = new StringBuffer();
      mStatusOfImport = true;
      
      Map<String, File> folders = getFolders();

      if ( null != folders ) {
        File[] archFiles = getArchiveFromInputFolder(folders);

        if ( null != archFiles ) {
          List<File> inputFiles = getInputFiles(archFiles, folders);

          if ( null != inputFiles ) {
            result = true;
            // Mapping files should be imported first.
            int type = ParseTicketable.MAPPING;
            List<File> mappingFiles = getSpecificFiles(inputFiles, type);
            for(File file : mappingFiles) {
              parseFile(file, new File(folders.get(Constants.ERROR_FOLDER), file.getName()), type);
            }
            // Header files should be imported after mappings.
            type = ParseTicketable.HEADER;
            List<File> headerFiles = getSpecificFiles(inputFiles, type);
            for(File file : headerFiles) {
              parseFile(file, new File(folders.get(Constants.ERROR_FOLDER), file.getName()), type);
            }

            //No import errors - delete files from input folder
            if (mStatusOfImport) {
              for(File file : inputFiles) {
                file.delete();
              }
            }

          }
        }
      } else {
        if(isLoggingWarning()) {
          logWarning("Can't find root directory " + getRootDir() + " with content \n..\nerror\ninput\nworking");
        }
      }
    } finally {
      String statusOfImport = mStatusAlertInfo.toString();
      if ( 0 < statusOfImport.length() ) {
        sendAlert(statusOfImport);
      }
      mInProgress = false;
    }

    if(isLoggingInfo()) {
      logInfo("Import tickets finished...");
    }
    return result;
  }
  
  /**
   * Gets specific type files.
   * It is needed to import mapping at first.
   * 
   * @param pList - files list for import.
   * @param pType - type
   * @return list of type specific files.
   */
  protected List<File> getSpecificFiles(List<File> pList, int pType) {
    List<File> result = new ArrayList<File>();
    for (File file: pList ) {
      if ( checkFile(file, pType) ) {
        result.add(file);
      } else {
      } 
    }
    return result;
  }
  
  
  /**
   * Checks if file contains header info.
   * 
   * @param line record for check
   * @return true if it is header record, false otherwise.
   */
  private boolean isParseHeader(String line) {
    return (6 < line.split(ParseTicketable.PARSE_DELIMITER).length);
  }

  /**
   * Checks if file contains mapping info.
   * 
   * @param line record for check
   * @return true if it is header record, false otherwise.
   */
  private boolean isParseMapping(String line) {
    return (6 == line.split(ParseTicketable.PARSE_DELIMITER).length);
  }

  /**
   * Checks if file contains type specific info.
   * 
   * @param pFile - file.
   * @param pType - specific type.
   *   @see ParseTicketable.MAPPING
   *   @see ParseTicketable.HEADER
   * @return true if file contains specific info, otherwise false.
   */
  protected boolean checkFile(File pFile, int pType) {
    boolean result = false;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(pFile));
      String line;
      line = reader.readLine();
      if (null != line ) {
        
        boolean headerFile = isParseHeader(line) && (ParseTicketable.HEADER == pType);
        boolean mappingFile = isParseMapping(line) && (ParseTicketable.MAPPING == pType);
        
        result = headerFile || mappingFile;
        
        if (!isParseHeader(line) && !isParseMapping(line)) {
          mStatusOfImport = false;
          if (mStatusAlertInfo.length() > 0) {
            mStatusAlertInfo.append("<br/>");
            mStatusAlertInfo.append("<br/>");
          }
          // Prevent duplicate messages in the email body 
          if (mStatusAlertInfo.indexOf("Parsing error. Incorrect file format."+pFile.getAbsolutePath()) < 0) {
            mStatusAlertInfo.append("Parsing error. Incorrect file format.");
            mStatusAlertInfo.append(pFile.getAbsolutePath());
            mStatusAlertInfo.append("<br/>");
          }
        }
        
      }
    } catch (Exception e) {
      if ( isLoggingError() ) {
        logError(e);
      }
    } finally {
      closeReader(reader);
    }
    return result;
  }

  /**
   * Parse file.
   *
   * @param pFile source file.
   * @param pErrorFile file for wrong recordes.
   * @param pType specific file type.
   */
  private void parseFile(final File pFile, final File pErrorFile, final int pType) {
    if(null != pFile) {
      if(isLoggingInfo()) {
        logInfo("Start parsing: " + pFile.getAbsolutePath());
      }

      BufferedReader reader = null;
      BufferedWriter writer = null;

      try {
        reader = new BufferedReader(new FileReader(pFile));
        writer = new BufferedWriter(new FileWriter(pErrorFile));

        String line;
        line = reader.readLine();

        if(null != line) {
          ParseTicketable parseTicket = ParseTicketObjectFactory.create(pType, pFile.getName(), 
              this, writer, getRepository(), getProfileRepository(), getTransactionManager(), getCastVATManager());

          do {
            parseTicket.addLine(line);
          } while(null != (line = reader.readLine()));

          parseTicket.finish();
        }
      } catch(IOException e) {
        if(isLoggingError()) {
          logError("Cant parse file " + pFile.getAbsolutePath() + ", cause: " + e.getMessage());
        }
        mStatusOfImport = false;

        if (mStatusAlertInfo.length() > 0) {
          mStatusAlertInfo.append("<br/>");
          mStatusAlertInfo.append("<br/>");
        }
        
        mStatusAlertInfo.append("Can't parse file ");
        mStatusAlertInfo.append(pFile.getAbsolutePath());
        mStatusAlertInfo.append("<br/>");
        mStatusAlertInfo.append(" Exception: ");
        mStatusAlertInfo.append("<br/>");
        mStatusAlertInfo.append(MiscUtils.excetionAsString(e));
      } finally {
        if(null != writer) {
          closeWriter(writer);
          writer = null;
        }
        if(null != reader) {
          closeReader(reader);
          reader = null;
        }
      }

    } else {
      if(isLoggingError()) {
        logError("Cant parse null file");
      }
    }
    if(isLoggingInfo()) {
      logInfo("End parsing: " + pFile.getAbsolutePath());
    }

  }

  /**
   * Unzip and gets input files.
   *
   * @param archFiles zipped unput files.
   * @param folders working folders map.
   *
   * @return unzipped files list.
   */
  private List<File> getInputFiles(final File[] archFiles, 
                                  final Map<String, File> folders) {
    List<File> result = new ArrayList<File>();

    for(int i = 0; i < archFiles.length; i++) {
      List<File> wfiles = unzipFile(archFiles[i], folders);

      if(null != wfiles) {
        result.addAll(wfiles);
      }
      if (mStatusOfImport) {
        archFiles[i].delete();
      }
    }

    return result;
  }

  /**
   * Gets imports files.
   *
   * @param folders working folders map.
   *
   * @return files for import.
   */
  private File[] getArchiveFromInputFolder(final Map<String, File> folders) {
    
    File inputFolder = folders.get(Constants.INPUT_FOLDER);
    List<File> result = getFilesFromFolder(inputFolder);

    if(null == result) {
      if(isLoggingWarning()) {
        logWarning(inputFolder.getAbsolutePath() + " is empty or not directory.");
      }
    }

    return (null != result) ? result.toArray(new File[result.size()]) : null;
  }

  /**
   * Gets files list from folder.
   *
   * @param folder source folder.
   *
   * @return files list.
   */
  private List<File> getFilesFromFolder(final File folder) {
    File[] list = folder.listFiles();

    return (null == list) ? null : Arrays.asList(list);
  }

  /**
   * Unzip file.
   *
   * @param file for unzip.
   * @param folders working folders map.
   *
   * @return unzipped files list.
   */
  private List<File> unzipFile(final File file,
                              final Map<String, File> folders) {
    List<File> result = new ArrayList<File>();
    int BUFFER = 2048;

    try {
      BufferedOutputStream dest = null;
      BufferedInputStream bis = null;
      ZipEntry entry;
      ZipFile zipfile = new ZipFile(file);
      Enumeration e = zipfile.entries();

      while(e.hasMoreElements()) {
        entry = (ZipEntry) e.nextElement();
        InputStream is = zipfile.getInputStream(entry);
        bis = new BufferedInputStream(is);

        int count;
        byte[] data = new byte[BUFFER];
        File wfile = new File(folders.get(Constants.WORKING_FOLDER), entry.getName());
        FileOutputStream fos = new FileOutputStream(wfile);
        dest = new BufferedOutputStream(fos, BUFFER);

        while((count = bis.read(data, 0, BUFFER)) != -1) {
          dest.write(data, 0, count);
        }

        dest.flush();
        dest.close();
        dest = null;
        is.close();
        is = null;
        bis.close();
        bis = null;
        result.add(wfile);
      }
      zipfile.close();
    } catch(Exception e) {
      if(isLoggingWarning()) {
        logWarning("Can't unzip file " + file.getAbsolutePath());
      }
      mStatusOfImport = false;

      if (mStatusAlertInfo.length() > 0) {
        mStatusAlertInfo.append("<br/>");
        mStatusAlertInfo.append("<br/>");
      }

      mStatusAlertInfo.append("Can't unzip file ");
      mStatusAlertInfo.append(file.getAbsolutePath());
      mStatusAlertInfo.append("<br/>");
      mStatusAlertInfo.append(" Exception: ");
      mStatusAlertInfo.append("<br/>");
      mStatusAlertInfo.append(MiscUtils.excetionAsString(e));
      result = null;
    }

    return result;
  }


  /**
   * Send email.
   *
   * @throws EmailException
   */
  private boolean sendAlert(String message) {
    boolean result = false;
    try {
      TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getAlertEmailInfo().copy();

      String from = emailInfo.getMessageFrom();
      String to = mConfiguration.getImportFailedNotificationSendTo();
      String subject = emailInfo.getMessageSubject();

      EmailEvent emailEvent = null;
      Message jMessage = MimeMessageUtils.createMessage(from, subject);
      MimeMessageUtils.setRecipient(jMessage, Message.RecipientType.TO, to);

      ContentPart[] l_content = { new ContentPart(message, "text/html"), };
      MimeMessageUtils.setContent(jMessage, l_content);
      emailEvent = new EmailEvent(jMessage);
      emailEvent.setCharSet("UTF-8");
      getAlertEmailSender().sendEmailEvent(emailEvent);
    } catch(EmailException e) {
      if(isLoggingError()) {
        logError(e);
      }
    } catch(MessagingException me) {
      if(isLoggingError()) {
        logError(me);
      }
    }

    return result;
  }

  /**
   * Close reader.
   *
   * @param reader reader.
   */
  private void closeReader(final Reader reader) {
    if ( null != reader ) {
      try {
        reader.close();
      } catch(Exception e) {
        if(isLoggingDebug()) {
          logDebug("Can't close reader: ", e);
        }
      }
    }
  }

  /**
   * Close writer.
   *
   * @param writer writer.
   */
  private void closeWriter(final Writer writer) {
    try {
      writer.flush();
      writer.close();
    } catch(Exception e) {
      if(isLoggingDebug()) {
        logDebug("Can't close writer: ", e);
      }
    }
  }

  /**
   * Check mandatory folder - root folder.
   * 
   * @throws FileNotFoundException
   *             if mandatory folders not exists
   */
  protected Map<String, File> getFolders() {
    Map<String, File> result = null;
    File rootDir = null;
    try {
      String rootFolder = getRootDir();
      rootDir = new File(rootFolder);
      checkDirectory(rootDir);
    } catch (FileNotFoundException e) {
      mStatusAlertInfo.append("Can't find root dir.<br/>");
      mStatusAlertInfo.append("Exception:<br/>");
      mStatusAlertInfo.append(MiscUtils.excetionAsString(e));
    }
    
    result = getMandatoryFolders();
    if ( null == result ) {
      mStatusAlertInfo.append("Wrong root dir structure");
    }
    return result;
  }

  /**
   * Gets default TVA rate.
   * @return default TVA rate.
   */
  public double getDefaultTVARate() {
    return mConfiguration.getDefaultTVARate();
  }
  
  /**
   * Gets default discount amount.
   * @return default discount amount.
   */
  public double getDefaultDiscountAmount() {
    return mConfiguration.getDefaultDiscountAmount();
  }
  
  
  private Repository mProfileRepository;
  
  /**
   * @return the mProfileRepository
   */
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param pProfileRepository the mProfileRepository to set
   */
  public void setProfileRepository(final Repository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  
  private PurchaseHistoryConfiguration mConfiguration = null;

  /**
   * @return the configuration
   */
  public PurchaseHistoryConfiguration getConfiguration() {
    return mConfiguration;
  }

  /**
   * @param pConfiguration the configuration to set
   */
  public void setConfiguration(PurchaseHistoryConfiguration pConfiguration) {
    this.mConfiguration = pConfiguration;
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
  
  //-------------------------------------
  // property: alertEmailInfo
  private TemplateEmailInfo mAlertEmailInfo;

  /**
   * Sets the alert email information. This is configured in the component property file.
   *
   * @param pAlertEmailInfo -
   *            the alert email information
   */
  public void setAlertEmailInfo(final TemplateEmailInfo pAlertEmailInfo) {
    mAlertEmailInfo = pAlertEmailInfo;
  }

  /**
   * Gets the alert email information. This is configured in the component property file.
   *
   * @return the alert email information
   */
  public TemplateEmailInfo getAlertEmailInfo() {
    return mAlertEmailInfo;
  }

  //-------------------------------------
  // property: alertEmailSender
  private SMTPEmailSender mAlertEmailSender = null;

  /**
   * Sets the email send component. This is configured in the component property file.
   *
   * @param pAlertEmailSender -
   *            the email send component
   */
  public void setAlertEmailSender(final SMTPEmailSender pAlertEmailSender) {
    mAlertEmailSender = pAlertEmailSender;
  }

  /**
   * Email Sender.
   *
   * @return Email Sender
   */
  public SMTPEmailSender getAlertEmailSender() {
    return this.mAlertEmailSender;
  }


  //Start service
  int jobId;
  
  /**
   * Connects to the queue and starts listening for messages.
   * 
   * @throws ServiceException
   *             If a service exception occurs.
   */
  public void doStartService() throws ServiceException {
    ScheduledJob job = new ScheduledJob (SERVICE_NAME,
        SERVICE_DESCRIPTION,
        getAbsoluteName(),
        getSchedule(),
        this,
        ScheduledJob.SEPARATE_THREAD);
    jobId = getScheduler().addScheduledJob(job);
    writeServiceStartStatistic();
  }

  // Stop method
  public void doStopService() throws ServiceException {
    getScheduler().removeScheduledJob (jobId);
  }

    public CastVATManager getCastVATManager() {
        return mCastVATManager;
    }

    public void setCastVATManager(CastVATManager mCastVATManager) {
        this.mCastVATManager = mCastVATManager;
    }
}
