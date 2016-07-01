package com.castorama.integration.planner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;

import atg.adapter.gsa.GSARepository;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.email.ContentPart;
import atg.service.email.EmailEvent;
import atg.service.email.EmailException;
import atg.service.email.EmailListener;
import atg.service.email.MimeMessageUtils;
import atg.service.email.SMTPEmailSender;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;
import atg.userprofiling.email.HtmlToTextConverter;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.castorama.integration.Constants;
import com.castorama.integration.ExportManagerBase;
import com.castorama.integration.backoffice.exp.UtilFormat;
import com.castorama.integration.util.MiscUtils;
import com.castorama.sftp.SftpService;
import com.castorama.utils.ServerSetting;

public class ExportProductManager extends ExportManagerBase implements Schedulable {

	private static final String ITEM_DESC_PLANNER_EXPORT_JOURNAL = "PLANNER_EXPORT_JOURNALISATION";

	private static final String FILTER_KITCHEN_PLANNER = "kitchen_planner=1";
	
    private static final String ATTR_CODE_ARTICLE = "codeArticle";
	
	private static final String TAG_PRODUCT = "<Produit>";
	private static final String END_TAG_PRODUCT = "</Produit>";
	private static final String TAG_REF = "<ref>";
	private static final String END_TAG_REF = "</ref>";
	private static final String TAG_LIB = "<lib>";
	private static final String END_TAG_LIB = "</lib>";
	private static final String TAG_DESC = "<desc>";
	private static final String END_TAG_DESC = "</desc>";
	private static final String TAG_NORNE = "<norme>";
	private static final String END_TAG_NORME = "</norme>";
	private static final String TAG_PLUS_PRD = "<plusPrd>";
	private static final String END_TAG_PLUS_PRD = "</plusPrd>";
	private static final String TAG_RESTR = "<restr>";
	private static final String END_TAG_RESTR = "</restr>";
	private static final String TAG_CONSEILS = "<conseils>";
	private static final String END_TAG_CONSEILS = "</conseils>";
	private static final String TAG_LOGO_MARQUE = "<logoMarque>";
	private static final String END_TAG_LOGO_MARQUE = "</logoMarque>";
	private static final String TAG_GARANTIE = "<garantie>";
	private static final String END_TAG_GARANTIE = "</garantie>";
	private static final String TAG_PHOTO = "<photo>";
	private static final String END_TAG_PHOTO = "</photo>";

	private static final String PREFIX_CAST0 = "Casto";

	private static final String MAIL_OK_TITRE = "cuisine.depot.ok.titre";
	private static final String MAIL_OK_CORPS = "cuisine.depot.ok.corps";
	private static final String MAIL_KO_TITRE = "cuisine.depot.ko.titre";
	private static final String MAIL_KO_CORPS = "cuisine.depot.ko.corps";
	private static final String MAIL_IMAGES_NOT_FOUND = "cuisine.images.not.found";
	
	private static final String FILE_TEMPLATE = "com.castorama.integration.planner.MailConceptionCuisineResources_fr";

	private static String prefix_xml_file = "CUISINE_SQUARECLOCK_";
	private static String prefix_images_file = "CC_produits_SQUARECLOCK_";
	private static String prefix_brands_file = "CC_marques_SQUARECLOCK_";

	private List<RepositoryItem> journalItems;

	// properties
	private Boolean modifiedImagesOnly;
	
	// the server setting
	private ServerSetting serverSetting;
	
	// Journal SKU Repository
	private Repository journalSkuRepository;
	// Scheduler property
	private Scheduler scheduler;
	// Schedule property
	private Schedule schedule;

	// FTP server name or IP address
	private String ftpserver;
	// FTP server port
	private int ftpport;
	// FTP login name
	private String ftplogin;
	// FTP password
	private String ftpmdp;
	// folder name on FTP server to upload feed files
	private String ftpfolder;

	private Boolean usePrivateKey;
	
	private String pathPrivateKey;

	private String mailTo;
	private EmailListener emailListener;
	private TemplateEmailInfo mDefaultEmailInfo;
	private SMTPEmailSender emailSender;
	
	private String filePrefix;
	private String fileImagesPrefix;
	private String fileBrandsPrefix;
	
	public ExportProductManager() {
	}

	
	public Boolean getUsePrivateKey() {
		return usePrivateKey;
	}


	public void setUsePrivateKey(Boolean usePrivateKey) {
		this.usePrivateKey = usePrivateKey;
	}


	public String getPathPrivateKey() {
		return pathPrivateKey;
	}


	public void setPathPrivateKey(String pathPrivateKey) {
		this.pathPrivateKey = pathPrivateKey;
	}


	/**
	 * Sets the email send component. This is configured in the component property file.
	 * 
	 * @param pEmailSender -
	 *            the email send component
	 */
	public void setEmailSender(SMTPEmailSender pEmailSender) {
		emailSender = pEmailSender;
	}

	public SMTPEmailSender getEmailSender() {
		return this.emailSender;
	}

	/**
	 * Sets the default email information. This is configured in the component property file.
	 * 
	 * @param pDefaultEmailInfo -
	 *            the default email information
	 */
	public void setDefaultEmailInfo(TemplateEmailInfo pDefaultEmailInfo) {
		mDefaultEmailInfo = pDefaultEmailInfo;
	}

	/**
	 * Gets the default email information. This is configured in the component property file.
	 * 
	 * @return the default email information
	 */
	public TemplateEmailInfo getDefaultEmailInfo() {
		return mDefaultEmailInfo;
	}

	/**
	 * Defines what images should be included into the feed. If true than the images modified since the previous export
	 * will be included into the feed only, otherwise all images will be included into the feed
	 * 
	 * @return boolean value
	 */
	public Boolean getModifiedImagesOnly() {
		return modifiedImagesOnly;
	}

	/**
	 * @param modifiedImagesOnly
	 *            boolean value
	 */
	public void setModifiedImagesOnly(Boolean modifiedImagesOnly) {
		this.modifiedImagesOnly = modifiedImagesOnly;
	}

	/**
	 * Get server setting object
	 * @return server setting object
	 */
	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	/**
	 * Set server setting object
	 * @param serverSetting server setting object
	 */
	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

	/**
	 * The repository that holds the information
	 * 
	 * @return the journal repository that holds the information
	 */
	public Repository getJournalSkuRepository() {
		return journalSkuRepository;
	}

	/**
	 * @param journalSkuRepository
	 *            set the journal repository that holds the information
	 */
	public void setJournalSkuRepository(Repository journalSkuRepository) {
		this.journalSkuRepository = journalSkuRepository;
	}

	/**
	 * Get FTP server name or IP address
	 * 
	 * @return FTP server name or IP address
	 */
	public String getFtpserver() {
		return ftpserver;
	}

	/**
	 * Set FTP server name or IP address
	 * 
	 * @param ftpserver
	 *            the FTP server name or IP address
	 */
	public void setFtpserver(String ftpserver) {
		this.ftpserver = ftpserver;
	}

	/**
	 * Get FTP server port
	 * 
	 * @return FTP server port
	 */
	public int getFtpport() {
		return ftpport;
	}

	/**
	 * Set FTP server port
	 * 
	 * @param ftpport
	 *            FTP server port
	 */
	public void setFtpport(int ftpport) {
		this.ftpport = ftpport;
	}

	/**
	 * Get FTP login name
	 * 
	 * @return FTP login name
	 */
	public String getFtplogin() {
		return ftplogin;
	}

	/**
	 * Set FTP login name
	 * 
	 * @param ftplogin
	 *            FTP login name
	 */
	public void setFtplogin(String ftplogin) {
		this.ftplogin = ftplogin;
	}

	/**
	 * Get FTP password
	 * 
	 * @return FTP password
	 */
	public String getFtpmdp() {
		return ftpmdp;
	}

	/**
	 * @param ftpmdp
	 *            FTP password
	 */
	public void setFtpmdp(String ftpmdp) {
		this.ftpmdp = ftpmdp;
	}

	/**
	 * Get folder name on FTP server to upload feed files
	 * 
	 * @return folder name
	 */
	public String getFtpfolder() {
		return ftpfolder;
	}

	/**
	 * Set folder name on FTP server to upload feed files
	 * 
	 * @param ftpfolder
	 *            folder name
	 */
	public void setFtpfolder(String ftpfolder) {
		this.ftpfolder = ftpfolder;
	}

	/**
	 * Get email address to send notification about process results
	 * 
	 * @return email address
	 */
	public String getMailTo() {
		return mailTo;
	}

	/**
	 * Set email address to send notification about process results
	 * 
	 * @param mailTo
	 *            email address
	 */
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	/**
	 * Get email listener
	 * 
	 * @return email listener
	 */
	public EmailListener getEmailListener() {
		return emailListener;
	}

	/**
	 * Set email listener
	 * 
	 * @param emaillistener
	 *            the email listener
	 */
	public void setEmailListener(EmailListener emaillistener) {
		this.emailListener = emaillistener;
	}

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
	 * Get prefix file
	 * @return prefix file
	 */
	public String getFilePrefix() {
		return filePrefix;
	}

	/**
	 * @param filePrefix prefix file
	 */
	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	/**
	 * Get prefix file for export images
	 * @return prefix file
	 */
	public String getFileImagesPrefix() {
		return fileImagesPrefix;
	}

	/**
	 * @param fileImagesPrefix prefix file 
	 */
	public void setFileImagesPrefix(String fileImagesPrefix) {
		this.fileImagesPrefix = fileImagesPrefix;
	}

	/**
	 * Get prefix file for export brands
	 * @return prefix file
	 */
	public String getFileBrandsPrefix() {
		return fileBrandsPrefix;
	}

	/**
	 * @param fileBrandsPrefix the prefix file for export brands
	 */
	public void setFileBrandsPrefix(String fileBrandsPrefix) {
		this.fileBrandsPrefix = fileBrandsPrefix;
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
			exportProducts();
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
		ScheduledJob job = new ScheduledJob("KitchenPlannerSchedulableService", "Export products from Repository",
				getAbsoluteName(), getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
	}

	/**
	 * Stops listening for messages and disconnects from the queue.
	 */
	public void doStopService() throws ServiceException {
		getScheduler().removeScheduledJob(jobId);
	}

	/**
	 * Export products for Kitchen Planner Integration
	 */
	public void exportProducts() {
		if (isLoggingInfo()) {
			logInfo("start - ExportProductManager - exportProducts");
		}

		checkFolders();
		checkProperties();

		int countSuccessItems = 0;
		int countFailItems = 0;

		File tmpDir = null;
		File imagesDir = null;
		File brandDir = null;
		File backupDir = null;
		File logFile = null;
		PrintWriter logWriter = null;
		StringBuilder logBuilder = new StringBuilder();
		//StringBuilder logWarn = new StringBuilder();
		StringBuilder error = new StringBuilder();

		try {
			// invalidate caches
		    ((GSARepository)getRepository()).invalidateCaches();
			
			tmpDir = new File(workingDir, Long.toString(new Date().getTime()));
			tmpDir.mkdir();
			
			backupDir = new File(getRootDir(), "backup");
	        if (!backupDir.exists()) {
	            backupDir.mkdir();
	        }

			logFile = new File(tmpDir, genFileName(prefix_xml_file, ".log"));
			logWriter = new PrintWriter(logFile, "UTF-8");
			logWriter.println(LOG_EXPORT_DATE + Constants.DATE_FORMAT_LOG.format(new Date()));
			logBuilder.append(LOG_FILE_LIST_EXPORT).append(Constants.LINE_SEPARATOR);

			File workingFile = new File(tmpDir, genFileName(prefix_xml_file, ".xml"));
			PrintWriter printWriter = new PrintWriter(workingFile, "UTF-8");
			printWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			printWriter.println("<Produits>");

			StringBuilder warningBuilder = new StringBuilder();
			StringBuilder errorImgMessages = new StringBuilder();
			
			boolean isFailed = false;
			int nmbChunk = 1;
			int startPosition = 0;
			int countSkus = countCastoSku(FILTER_KITCHEN_PLANNER);
			if (isLoggingDebug()) {
				logDebug("count Sku=" + countSkus);
			}

			if (countSkus > 0) {
				int cntChunk = countSkus / DEFAULT_CHUNK_SIZE;
				if (cntChunk == 0) {
					cntChunk = 1;
				} else if ((countSkus % DEFAULT_CHUNK_SIZE) > 0) {
					cntChunk++;
				}

				if (isLoggingDebug()) {
					logDebug("Number Chunk=" + cntChunk);
				}

				imagesDir = new File(tmpDir, "images");
				imagesDir.mkdir();

				brandDir = new File(tmpDir, "brands");
				brandDir.mkdir();


				journalItems = new ArrayList<RepositoryItem>();
				
				for (; nmbChunk <= cntChunk; nmbChunk++) {

					RepositoryItem[] skuItems = getChunkProducts(FILTER_KITCHEN_PLANNER,
							startPosition, DEFAULT_CHUNK_SIZE);
					if (skuItems != null && skuItems.length > 0) {
						if (isLoggingDebug()) {
							logDebug("Selected Sku=" + skuItems.length);
						}
						String errorImgMes = null;
						String line;
						for (RepositoryItem item : skuItems) {
							try {
								line = generateXmlProduct(item);
								printWriter.print(line);
								// images
								RepositoryItem journalItem = getJournalSkuRepository().getItem(item.getRepositoryId(),
										ITEM_DESC_PLANNER_EXPORT_JOURNAL);

								errorImgMes = exportImages(item, imagesDir.getAbsolutePath(), brandDir.getAbsolutePath(), journalItem);
								if (errorImgMes != null) {
									errorImgMessages.append(errorImgMes).append(Constants.LINE_SEPARATOR);
								}
								countSuccessItems++;
							} catch (Exception e) {
								if (isLoggingError()) {
									logError(e);
								}
								countFailItems++;
								warningBuilder.append(item.getRepositoryId()).append(Constants.LINE_SEPARATOR);
							}
						}
					}
				}
			}

			printWriter.println("</Produits>");
			printWriter.close();
			
			//copy working file in output 
			File outputXmlFile = new File(outputDir, workingFile.getName());
			MiscUtils.copyFile(workingFile, outputXmlFile);
			logBuilder.append(outputXmlFile.getName()).append(Constants.LINE_SEPARATOR);
			File outputImgZipFie = null;
			File outputBrandZipFie = null;
			
			if (countSkus > 0) {
				String imgZipName = null;
				String brandZipName = null;
				if (imagesDir != null) {
					imgZipName = outputDir.getAbsolutePath() + File.separator + genFileName(prefix_images_file, ".zip");
					MiscUtils.zippingFiles(imagesDir, imgZipName);
					outputImgZipFie = new File(imgZipName);
					if (outputImgZipFie.exists()) {
						logBuilder.append(outputImgZipFie.getName()).append(Constants.LINE_SEPARATOR);
					}
				}

				if (brandDir != null) {
					brandZipName = outputDir.getAbsolutePath() + File.separator + genFileName(prefix_brands_file, ".zip");
					MiscUtils.zippingFiles(brandDir, brandZipName);
					outputBrandZipFie = new File(brandZipName);
					if (outputBrandZipFie.exists()) {
						logBuilder.append(outputBrandZipFie.getName()).append(Constants.LINE_SEPARATOR);
					}
				}
	

				saveJournalItems();
			}
			
			writeLogFile(logBuilder, countSuccessItems, countFailItems, warningBuilder);
			if (countFailItems > 0) {
				isFailed = true;
			}

			if (!StringUtils.isBlank(errorImgMessages.toString())) {
				logBuilder.append(errorImgMessages.toString());
			}

			File statusDir = null;
			try {
				if (isFailed) {
					error.append(ResourceUtils.getUserMsgResource(MAIL_IMAGES_NOT_FOUND, FILE_TEMPLATE, null))
					.append(Constants.LINE_SEPARATOR);
				}
				transferToFTP(outputXmlFile, outputImgZipFie, outputBrandZipFie);
				MiscUtils.copyFile(outputXmlFile, new File(backupDir, outputXmlFile.getName()));
				outputXmlFile.delete();
				
				if (outputImgZipFie != null && outputImgZipFie.exists()) {
				    MiscUtils.copyFile(outputImgZipFie, new File(backupDir, outputImgZipFie.getName()));
					outputImgZipFie.delete();
				}
				if (outputBrandZipFie != null && outputBrandZipFie.exists()) {
				    MiscUtils.copyFile(outputBrandZipFie, new File(backupDir, outputBrandZipFie.getName()));
					outputBrandZipFie.delete();
				}
				if (!isFailed) {
					// send email
					sendEmailHtml(null);
				}

				if (isFailed) {
					logWriter.println("Status: Failed");
					statusDir = errorDir;
				} else {
					logWriter.println("Status: Success");
					statusDir = archiveDir;
				}
				logWriter.print(logBuilder.toString());
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(e);
				}
				error.append(e.getMessage());
				
				logWriter.println("Status: Failed");
				logWriter.print(logBuilder.toString());
				e.printStackTrace(logWriter);
				statusDir = errorDir;
			}

			logWriter.close();
			try {
				MiscUtils.copyFile(logFile, new File(statusDir, logFile.getName()));
			} catch (Exception e) {
				logError(e);
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
			error = new StringBuilder();
			error.append(e.toString());
			try {
				if (logWriter != null) {
					logWriter.println("Status: Failed");
					logWriter.println(error.toString());
					e.printStackTrace(logWriter);
					logWriter.close();
					MiscUtils.copyFile(logFile, new File(errorDir, logFile.getName()));
				}
			} catch (Exception ex) {
				logError(ex);
			}
		} finally {
			if (logWriter != null) {
				logWriter.close();
			}

			if (tmpDir != null) {
				MiscUtils.deleteFolder(tmpDir);
			}
			if (!StringUtils.isEmpty(error.toString())) {
				try {
					sendEmailHtml(error.toString());
				}catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
				
			}
		}

		if (isLoggingInfo()) {
			logInfo("finish - ExportProductManager - exportProducts");
		}
	}

	private void writeLogFile(StringBuilder log, int cntSuccess, int cntFail, StringBuilder warning) {
		log.append(LOG_NUMBER_SKUS_SUCCESS).append(cntSuccess).append(Constants.LINE_SEPARATOR);
		log.append(LOG_NUMBER_SKUS_FAIL).append(cntFail).append(Constants.LINE_SEPARATOR);
		if (cntFail > 0) {
			log.append(LOG_LIST_SKUS_FAIL).append(Constants.LINE_SEPARATOR);
			log.append(warning.toString()).append(Constants.LINE_SEPARATOR);
		}
	}
	
	private void transferToFTP(File workingFile, File largeImgFile, File brandFile) throws Exception {
		  String dest;
		  String forlder;
		  if (getFtpfolder() != null) {
			  forlder = getFtpfolder().endsWith("/") ? getFtpfolder() : getFtpfolder() + "/";
			  
		  } else {
			  forlder = "/";
		  }
		  dest = forlder + workingFile.getName(); 
		  
		  SftpService service = new SftpService();
		  
		  if (getUsePrivateKey()) {
			  service.putSSHFile(getFtpserver(), getFtpport(), getFtplogin(), getPathPrivateKey(), workingFile, dest);
			  
			  if (largeImgFile != null && largeImgFile.exists()) {
				  dest = forlder + largeImgFile.getName();  
				  service.putSSHFile(getFtpserver(), getFtpport(), getFtplogin(), getPathPrivateKey(), largeImgFile, dest);
			  }
			
			  if (brandFile != null && brandFile.exists()){
				  dest = forlder + brandFile.getName();  
				  service.putSSHFile(getFtpserver(), getFtpport(), getFtplogin(), getPathPrivateKey(), brandFile, dest);
			  }
		  } else {
			  service.putFile(getFtpserver(), getFtpport(), getFtplogin(), getFtpmdp(), workingFile, dest);
			  
			  if (largeImgFile != null && largeImgFile.exists()) {
				  dest = forlder + largeImgFile.getName();  
				  service.putFile(getFtpserver(), getFtpport(), getFtplogin(), getFtpmdp(), largeImgFile, dest);
			  }
			
			  if (brandFile != null && brandFile.exists()){
				  dest = forlder + brandFile.getName();  
				  service.putFile(getFtpserver(), getFtpport(), getFtplogin(), getFtpmdp(), brandFile, dest);
			  }
		  }
	}
	
	private void sendEmailHtml(String error) throws MessagingException,
			EmailException {

		TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();
		String from = emailInfo.getMessageFrom();
		String to = getMailTo() != null ? getMailTo() : emailInfo.getMessageTo();

		String subject;
		String message;
		if (error == null) {
			subject = ResourceUtils.getUserMsgResource(MAIL_OK_TITRE, FILE_TEMPLATE, null);
			message = ResourceUtils.getUserMsgResource(MAIL_OK_CORPS, FILE_TEMPLATE, null);
		} else {
			subject = ResourceUtils.getUserMsgResource(MAIL_KO_TITRE, FILE_TEMPLATE, null);
	        message = ResourceUtils.getUserMsgResource(MAIL_KO_CORPS, FILE_TEMPLATE, null, new String[]
	        { error });
		}


		EmailEvent emailEvent = null;
		Message jMessage = MimeMessageUtils.createMessage(from, subject);
		MimeMessageUtils.setRecipient(jMessage, Message.RecipientType.TO, to);

		ContentPart[] l_content = { new ContentPart(message, "text/html"), };

		MimeMessageUtils.setContent(jMessage, l_content);

		emailEvent = new EmailEvent(jMessage);
		emailEvent.setCharSet("UTF-8");

		getEmailSender().sendEmailEvent(emailEvent);
	}

	private String generateXmlProduct(RepositoryItem skuItem) throws RepositoryException {
		StringBuilder sb = new StringBuilder();
		String skuId = skuItem.getRepositoryId();
		sb.append(TAG_PRODUCT).append(Constants.LINE_SEPARATOR);

		sb.append(TAG_REF).append(skuId.substring(PREFIX_CAST0.length()))
			.append(END_TAG_REF).append(Constants.LINE_SEPARATOR);
		sb.append(getValuesFromProduct(skuItem));
		sb.append(TAG_NORNE).append(normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_NORMES_TEXT))).append(
				END_TAG_NORME).append(Constants.LINE_SEPARATOR);

		sb.append(TAG_PLUS_PRD).append(normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_PLUS_DU_PRODUIT)))
				.append(END_TAG_PLUS_PRD).append(Constants.LINE_SEPARATOR);

		sb.append(TAG_RESTR).append(normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_RESTRICTION_USAGE)))
				.append(END_TAG_RESTR).append(Constants.LINE_SEPARATOR);

		sb.append(TAG_CONSEILS).append(
				normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_CONTRAINTES_UTILISATION))).append(
				END_TAG_CONSEILS).append(Constants.LINE_SEPARATOR);

		sb.append(generateImage(skuItem));

		sb.append(END_TAG_PRODUCT).append(Constants.LINE_SEPARATOR);
		return sb.toString();
	}

	private String getValuesFromProduct(RepositoryItem skuItem) throws RepositoryException {
		RepositoryItem product = null;
		String libel = null;
		String description = null;
		Object value = skuItem.getPropertyValue(Constants.PROPERTY_PARENT_PRODUCTS);
		if (value != null) {
			Set productSet = (Set) value;
			if (productSet != null && productSet.size() > 0) {
				Iterator it = productSet.iterator();
				if (it.hasNext()) {
					product = (RepositoryItem) it.next();
				}
			}
		}

		if (product != null) {
			libel = normalizeString(product.getPropertyValue(Constants.PROPERTY_DISPLAY_NAME));
			description = normalizeString(product.getPropertyValue(Constants.PROPERTY_LONG_DESCRIPTION));
		}

		if (StringUtils.isEmpty(libel)) {
			libel = normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_DISPLAY_NAME));
		}

		if (StringUtils.isEmpty(description)) {
			description = normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_LIBELLE_CLIENT_LONG));
		}

		StringBuilder sb = new StringBuilder();
		sb.append(TAG_LIB).append(libel).append(END_TAG_LIB).append(Constants.LINE_SEPARATOR);
		sb.append(TAG_DESC).append(description).append(END_TAG_DESC).append(Constants.LINE_SEPARATOR);
		return sb.toString();
	}

	private String generateImage(RepositoryItem skuItem) throws RepositoryException {
		String logoMarque = "";
		String largeImageUrl = "";

		largeImageUrl = getImageName((Integer)skuItem.getPropertyValue(ATTR_CODE_ARTICLE));
		logoMarque = getImageName(getMarqueUrl(skuItem));

		StringBuilder sb = new StringBuilder();
		sb.append(TAG_LOGO_MARQUE).append(logoMarque).append(END_TAG_LOGO_MARQUE).append(Constants.LINE_SEPARATOR);
		sb.append(TAG_GARANTIE).append(normalizeString(skuItem.getPropertyValue(Constants.PROPERTY_GARANTIE))).append(
				END_TAG_GARANTIE).append(Constants.LINE_SEPARATOR);
		sb.append(TAG_PHOTO).append(largeImageUrl).append(END_TAG_PHOTO).append(Constants.LINE_SEPARATOR);
		return sb.toString();
	}

	private String exportImages(RepositoryItem skuItem, String imgDir, String brandDir, RepositoryItem journalItem)
			throws RepositoryException, MalformedURLException, IOException {
		boolean isDownload = false;
		String errMess = null;
		
		RepositoryItem largeImageItem = (RepositoryItem) skuItem.getPropertyValue(Constants.PROPERTY_LARGE_IMAGE);

		Date exportDate = null;
		if (largeImageItem != null) {
			Integer type = (Integer) largeImageItem.getPropertyValue("type");
			if (type == 1) {
				String largeImageUrl = "/images/products/h/" + getImageName((Integer)skuItem.getPropertyValue(ATTR_CODE_ARTICLE));
				if (journalItem != null) {
					String jUrl = (String) journalItem.getPropertyValue(Constants.PROPERTY_IMAGE_URL);
					if (largeImageUrl.equals(jUrl)) {
						exportDate = (Date) journalItem.getPropertyValue(Constants.PROPERTY_IMAGE_DATE_EXPORT);
					}
				}
				String ret = downloadImage(largeImageUrl, exportDate, imgDir);
				if (SUCCESS.equals(ret)) {
					TransactionDemarcation trd = new TransactionDemarcation();
					boolean rollback = false;
					isDownload = true;
					try {
						try {
							trd.begin(getTransactionManager());
							if (journalItem == null) {
								MutableRepository mutJournalRepository = (MutableRepository) getJournalSkuRepository();
								journalItem = mutJournalRepository.createItem(ITEM_DESC_PLANNER_EXPORT_JOURNAL);
							}
							((MutableRepositoryItem) journalItem).setPropertyValue(Constants.PROPERTY_SKU_ID, skuItem
									.getRepositoryId());
							((MutableRepositoryItem) journalItem).setPropertyValue(Constants.PROPERTY_IMAGE_URL,
									largeImageUrl);
							((MutableRepositoryItem) journalItem).setPropertyValue(Constants.PROPERTY_IMAGE_DATE_EXPORT,
									new Date());
						} catch (Exception e) {
							if (isLoggingError()) {
								logError(e);
							}
							rollback = true;
							isDownload = false;
						} finally {
							trd.end(rollback);
						}
						
					} catch (TransactionDemarcationException e) {
						if (isLoggingError()) {
							logError("TransactionDemarcationException occured: ", e);
						}
						isDownload = false;
					}
				} else {
					errMess = ret;
				}
			}
		}

		String marqueUrl = getMarqueUrl(skuItem);
		exportDate = null;
		if (marqueUrl != null) {
			if (journalItem != null) {
				String jUrl = (String) journalItem.getPropertyValue(Constants.PROPERTY_BRAND_URL);
				if (marqueUrl.equals(jUrl)) {
					exportDate = (Date) journalItem.getPropertyValue(Constants.PROPERTY_BRAND_DATE_EXPORT);
				}
			}
			String ret1 = downloadImage(marqueUrl, exportDate, brandDir);
			if (SUCCESS.equals(ret1)) {
				TransactionDemarcation trd = new TransactionDemarcation();
				boolean rollback = false;
				isDownload = true;
				try {
					try {
						trd.begin(getTransactionManager());
						if (journalItem == null) {
							MutableRepository mutJournalRepository = (MutableRepository) getJournalSkuRepository();
							journalItem = mutJournalRepository.createItem(ITEM_DESC_PLANNER_EXPORT_JOURNAL);
						}
						((MutableRepositoryItem) journalItem).setPropertyValue(Constants.PROPERTY_SKU_ID, skuItem
								.getRepositoryId());
						((MutableRepositoryItem) journalItem).setPropertyValue(Constants.PROPERTY_BRAND_URL, marqueUrl);
						((MutableRepositoryItem) journalItem)
								.setPropertyValue(Constants.PROPERTY_BRAND_DATE_EXPORT, new Date());
					} catch (Exception e) {
						if (isLoggingError()) {
							logError(e);
						}
						rollback = true;
						isDownload = false;
					} finally {
						trd.end(rollback);
					}
					
				} catch (TransactionDemarcationException e) {
					if (isLoggingError()) {
						logError("TransactionDemarcationException occured: ", e);
					}
					isDownload = false;
				}
			} else {
				if (errMess == null) {
					errMess = ret1;
				} else if (ret1 != null) {
					errMess += Constants.LINE_SEPARATOR + ret1;
				}
			}
		}

		if (isDownload) {
			journalItems.add(journalItem);
		}
		
		return errMess;
	}

	private String downloadImage(String imageUrl, Object dateExport, String imgDir) throws MalformedURLException {
		String result = null;
		long expTime = 0;
		if (dateExport != null) {
			expTime = ((Date) dateExport).getTime();
		}

		OutputStream outStream = null;
		InputStream is = null;
		URL url = null;
		HttpURLConnection urlConn = null;

		String urlImg = null;
		try {
			String imageUri = imageUrl.replaceAll(" ", "%20");
			
			urlImg = getServerSetting().getHost() + imageUri; 
			url = new URL(urlImg);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput(true);
			urlConn.connect();
			long lastModified = urlConn.getLastModified();
			if (lastModified == 0) {
				if (isLoggingError()) {
					logError("URL " + urlImg + " is invalid.");
				}
				result = "URL " + urlImg + " is invalid.";
			}

			if (!getModifiedImagesOnly() || expTime < lastModified) {
				is = urlConn.getInputStream();

				String imgName = getImageName(imageUrl);

				outStream = new BufferedOutputStream(new FileOutputStream(imgDir + File.separator + imgName));

				byte buf[] = new byte[32768];
				int bytesRead;
				while ((bytesRead = is.read(buf, 0, buf.length)) > 0) {
					outStream.write(buf, 0, bytesRead);
				}
				result = SUCCESS;
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			if (isLoggingError()) {
				logError(e);
			}
			result = urlImg + "; IOException: " + e.getMessage();
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
			try {
				if (is != null)
					is.close();
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				if (isLoggingError()) {
					logError(e);
				}
			}
		}
		return result;
	}

	private void saveJournalItems() {
		if (isLoggingDebug()) {
			logDebug("Save journal Items: " + journalItems.size());
		}
		if (journalItems == null || journalItems.isEmpty()) {
			return;
		}

		try {
			MutableRepository mutJournalRepository = (MutableRepository) getJournalSkuRepository();
			for (RepositoryItem item : journalItems) {
				MutableRepositoryItem mItem = (MutableRepositoryItem) item;
				if (mItem.isTransient()) {
					mutJournalRepository.addItem(mItem);
				} else {
					mutJournalRepository.updateItem(mItem);
				}
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}

	}

	private String normalizeString(Object value) {
		String result = "";
		if (value != null) {
			result = (String) value;
			
			result = result.replaceAll("<br>", "!%#");
            result = result.replaceAll("<br */>", "!%#");
            result = result.replaceAll("<li>", "!%# -");
            HtmlToTextConverter converter = new HtmlToTextConverter();
            try {
                result = converter.htmlToText(result);
            } catch (TemplateEmailException tee) {
                if (isLoggingError()) {
                    logError(tee);
                }
            }
			
			result = UtilFormat.deleteCRLF(result);
			result = removeLineTerminators(result);
			
			result = result.replaceAll("!%#", "\n");
			
			result = replaceSpecialChars(result);
		}
		return result;
	}

	private String replaceSpecialChars(String value) {
		if (!StringUtils.isEmpty(value)) {
			//value = value.replaceAll("<", "&lt;");
			//value = value.replaceAll(">", "&gt;");
			value = MiscUtils.encodeString(value);
		}
		return value;
	}
	
	private String getImageName(String imageUrl) {
        imageUrl = normalizeString(imageUrl);
        if (imageUrl != null) {
            int idx = -1;
            if ((idx = imageUrl.lastIndexOf(Constants.SEPARATOR)) != -1) {
                imageUrl = imageUrl.substring(idx + 1, imageUrl.length());
            }
        }
        return imageUrl;
    }

	private String getImageName(Integer codeArticle) {
        return "h_" + codeArticle.toString() + ".jpg";
    }

	protected void checkFolders() {
		super.checkFolders();

		outputDir = new File(getRootDir(), Constants.OUTPUT_FOLDER);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}
	
	private void checkProperties() {
		if (!StringUtils.isBlank(getFilePrefix())) {
			prefix_xml_file = getFilePrefix();
		}
		
		if (!StringUtils.isBlank(getFileImagesPrefix())) {
			prefix_images_file = getFileImagesPrefix();
		}

		if (!StringUtils.isBlank(getFileBrandsPrefix())) {
			prefix_brands_file = getFileBrandsPrefix();
		}
	}
}
