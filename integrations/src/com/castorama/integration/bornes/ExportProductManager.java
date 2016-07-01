package com.castorama.integration.bornes;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.Constants;
import com.castorama.integration.ExportManagerBase;
import com.castorama.integration.backoffice.exp.UtilFormat;
import com.castorama.integration.journal.JournalItem;
import com.castorama.integration.journal.ProcessingJournalService;
import com.castorama.integration.util.MiscUtils;
import com.castorama.utils.ServerSetting;

public class ExportProductManager extends ExportManagerBase implements Schedulable {
	private static final String PREFIX_FILE_NAME = "SCDXMUIL_MGN_2500_N";
	private static final String PREFIX_IMG_FILE_NAME = "SCDXMUIN_MGN_2500_N";

	private static final String FIELD_SEPARATOR = "|";
	private static final char CHAR_ZERO = '0';
	private static final char CHAR_GAP = ' ';
	private static final String COMMA = ",";
	private static final String IMAGES_FOLDER = "/images/products/h/";
	private static final String JPG_EXT = ".jpg";
	

	private static final String ITEM_DESC_EXPORT_SKU_JOURNAL = "BORNES_EXPORT_JOURNALISATION";
	// size
	private static final int SIZE_FIELD_6 = 6;
	private static final int SIZE_FIELD_1000 = 1000;
	private static final int SIZE_FIELD_300 = 300;
	private static final int SIZE_FIELD_254 = 254;
	private static final int SIZE_FIELD_200 = 200;
	private static final int SIZE_FIELD_100 = 100;
	private static final int SIZE_FIELD_50 = 50;
	private static final int SIZE_FIELD_22 = 22;

	private static final String FILTER_BORNES = "CodeArticle>0 and bornes=1 ORDER BY CodeArticle";
	private static final String COUNT_BORNES = "CodeArticle>0 and bornes=1";
	
	// properties
	private Integer startFrom;
	private Integer chunkSize;
	private Boolean modifiedImagesOnly;
	private Boolean needCompressing;
	// the server setting
	private ServerSetting serverSetting;
	// Journal SKU Repository
	private Repository journalSkuRepository;
	// Scheduler property
	private Scheduler scheduler;
	// Schedule property
	private Schedule schedule;
	
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

	public ExportProductManager() {
	}

	public Integer getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(Integer startFrom) {
		this.startFrom = startFrom;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Boolean getModifiedImagesOnly() {
		return modifiedImagesOnly;
	}

	public void setModifiedImagesOnly(Boolean modifiedImagesOnly) {
		this.modifiedImagesOnly = modifiedImagesOnly;
	}

	public Boolean getNeedCompressing() {
		return needCompressing;
	}

	public void setNeedCompressing(Boolean needCompressing) {
		this.needCompressing = needCompressing;
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
		ScheduledJob job = new ScheduledJob("ExportBornesSchedulableService", "Export bornes from Repository",
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
	 * export products
	 */
	public void exportProducts() {
		if (isLoggingInfo()) {
			logInfo("start - ExportProductManager - exportProducts");
		}

		checkFolders();
		
		if (isLoggingDebug()) {
			logDebug("startFrom=" + getStartFrom());
			logDebug("chunkSize=" + getChunkSize());
			logDebug("modifiedImagesOnly=" + getModifiedImagesOnly());
			logDebug("needCompressing=" + getNeedCompressing());
		}
		//
		
		int startPosition = getStartFrom() != null ? getStartFrom() : DEFAULT_START_FROM;
		int portion = getChunkSize() != null ? getChunkSize() : DEFAULT_CHUNK_SIZE;

		if (isLoggingDebug()) {
			logDebug("Parameter: start position: " + startPosition);
			logDebug("Parameter: portion : " + portion);
		}

		int cntChunk = 1;
		int nmbChunk = 1;
		long sequence = 0;
		JournalItem jitem = null;

		File tmpDir = null;
		File imagesDir;
		File logFile = null;
		PrintWriter logWriter = null;
		StringBuilder logBuilder = new StringBuilder();

		int countSuccessItems = 0;
		int countFailItems = 0;
		boolean isFailed = false;

		try {
			
			int countSkus = countCastoSku(COUNT_BORNES);
			countSkus -= startPosition;
			if (isLoggingDebug()) {
				logDebug("countSku=" + countSkus);
			}
			
			if (countSkus > 0) {
				sequence = getJournalService().getSequence("BORNES");
				if (isLoggingDebug()) {
					logDebug("sequence=" + sequence);
				}
			}

			if (portion > 0) {
				cntChunk = countSkus / portion;
				if (cntChunk == 0) {
					cntChunk = 1;
				} else if ((countSkus % portion) > 0) {
					cntChunk++;
				}
			}
			if (isLoggingDebug()) {
				logDebug("count Chunk=" + cntChunk);
			}

			logFile = new File(workingDir, getFileName(PREFIX_FILE_NAME, 0, -1) + ".log");
			logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, false)));
			logWriter.println(LOG_EXPORT_DATE + Constants.DATE_FORMAT_LOG.format(new Date()));
			logBuilder.append(LOG_FILE_LIST_EXPORT).append(Constants.LINE_SEPARATOR);
			

			for (; nmbChunk <= cntChunk; nmbChunk++) {

				countSuccessItems = 0;
				countFailItems = 0;
				StringBuilder failedSkuIds = new StringBuilder();

				RepositoryItem[] skuItems = getChunkProducts(FILTER_BORNES, startPosition, portion);
				PrintWriter printWriter = null;
				if (skuItems != null && skuItems.length > 0) {
					tmpDir = new File(workingDir, Long.toString(new Date().getTime()));
					tmpDir.mkdir();

					imagesDir = new File(tmpDir, "images");
					imagesDir.mkdir();

					String pathChunk = tmpDir.getAbsoluteFile() + File.separator 
						+ getFileName(PREFIX_FILE_NAME, sequence, nmbChunk);

					try {
						printWriter = new PrintWriter(new BufferedWriter(new FileWriter(pathChunk + ".txt", false)));;
						String line;
						for (RepositoryItem skuItem : skuItems) {
							try {
								line = generateLine(skuItem);
								printWriter.write(line);
								countSuccessItems++;
							} catch (Exception e) {
								if (isLoggingError()) {
									logError(e);
								}
								countFailItems++;
								failedSkuIds.append(skuItem.getRepositoryId()).append(Constants.LINE_SEPARATOR);
							}
						}
						printWriter.close();
						zipChunkTxt(pathChunk, logBuilder);

						logBuilder.append(LOG_NUMBER_SKUS_SUCCESS).append(countSuccessItems).append(Constants.LINE_SEPARATOR)
						.append(LOG_LIST_SKUS_FAIL).append(countFailItems).append(Constants.LINE_SEPARATOR);
						if (countFailItems > 0) {
							logBuilder.append(LOG_LIST_SKUS_FAIL).append(Constants.LINE_SEPARATOR);
							logBuilder.append(failedSkuIds.toString()).append(Constants.LINE_SEPARATOR);
							isFailed = true;
						}

						// export images
						List<MutableRepositoryItem> journalItems = new ArrayList<MutableRepositoryItem>();
						String errorImgMes = null;
						StringBuilder errorImgMessages = new StringBuilder();
						for (RepositoryItem skuItem : skuItems) {
							errorImgMes = exportImages(skuItem, imagesDir.getAbsolutePath(), journalItems);
							if (errorImgMes != null) {
								errorImgMessages.append(errorImgMes).append(Constants.LINE_SEPARATOR);
							}
						}
						
						if (!journalItems.isEmpty()) {
							String imgZipName = imagesDir.getAbsolutePath() + File.separator
							+ getFileName(PREFIX_IMG_FILE_NAME, sequence, nmbChunk);

							zipChunkImgs(imgZipName, imagesDir, logBuilder);
						}
						
						if (!StringUtils.isBlank(errorImgMessages.toString())) {
							logBuilder.append(errorImgMessages.toString());
							isFailed = true;
						}
						
						MiscUtils.deleteFolder(tmpDir);

						saveJournalItems(journalItems);

					} finally {
						if (printWriter != null) {
							printWriter.close();
						}
					}
				} else {
					if (isLoggingDebug()) {
						logInfo("No sku selected!");
					}
				}
				
				startPosition += portion;
			}
			
			File statusDir = null;
			if (isFailed) {
				logWriter.println("Status: Failed");
				statusDir = errorDir;
			} else {
				logWriter.println("Status: Success");
				statusDir = archiveDir;
			}
			// items not found
			if (logBuilder.toString().indexOf(LOG_NUMBER_SKUS_SUCCESS) == -1) {
				logBuilder.append(LOG_NUMBER_SKUS_SUCCESS).append(countSuccessItems)
				.append(Constants.LINE_SEPARATOR)
				.append(LOG_LIST_SKUS_FAIL).append(countFailItems).append(Constants.LINE_SEPARATOR);
			} else {
				getJournalService().saveSequance("BORNES", sequence + cntChunk);
			}
			logWriter.print(logBuilder.toString());
			logWriter.flush();
			logWriter.close();
			
			try {
				MiscUtils.copyFile(logFile, new File(statusDir, logFile.getName()));
				logFile.delete();
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(e);
				}
			}


		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
			try {
				if (logWriter != null) {
					logWriter.println("Status: Failed");
					logWriter.println(logBuilder.toString());
					e.printStackTrace(logWriter);
					logWriter.flush();
					logWriter.close();
					MiscUtils.copyFile(logFile, new File(errorDir, logFile.getName()));
					logFile.delete();
				}
				getJournalService().registerFails(jitem);
			} catch (Exception ex) {
				if (isLoggingError()) {
					logError(e);
				}
			}
			if (tmpDir != null) {
				MiscUtils.deleteFolder(tmpDir);
			}
		}

		if (isLoggingInfo()) {
			logInfo("finish - ExportProductManager - exportProducts");
		}
	}

	private void zipChunkTxt(String txtFile, StringBuilder log) throws Exception {
		File workingFile = new File(txtFile + ".txt");
		if (getNeedCompressing()) {
		    MiscUtils.TarCompressFile(workingFile, txtFile);
			
			File tarFile = new File(txtFile);
			File tarOutFile = new File(outputDir, tarFile.getName());
			MiscUtils.copyFile(tarFile, tarOutFile);
			log.append(tarOutFile.getName()).append(Constants.LINE_SEPARATOR);
		} else {
			File outFile = new File(outputDir, workingFile.getName());
			MiscUtils.copyFile(workingFile, outFile);
			log.append(outFile.getName()).append(Constants.LINE_SEPARATOR);
		}
	}
	
	private void zipChunkImgs(String imgZipFile, File imagesDir, StringBuilder log) throws Exception {
		MiscUtils.TarCompressFiles(imagesDir, imgZipFile);
	    
		File tarImgFile = new File(imgZipFile);
		File tarImgOutFile = new File(outputDir, tarImgFile.getName());
		if (tarImgFile.exists()) {
			MiscUtils.copyFile(tarImgFile, tarImgOutFile);
			String zName = tarImgOutFile.getName();
			log.append(zName).append(Constants.LINE_SEPARATOR);
		}
	}

	private void saveJournalItems(List<MutableRepositoryItem> journalItems) {
		if (journalItems == null || journalItems.isEmpty()) {
			return;
		}
		
		TransactionDemarcation trd = new TransactionDemarcation();
		boolean rollback = false;
		try {
			MutableRepository mutJournalRepository = (MutableRepository) getJournalSkuRepository();
			for (MutableRepositoryItem item : journalItems) {
				rollback = false;
				try {
					trd.begin(getTransactionManager());
					if (item.isTransient()) {
						mutJournalRepository.addItem(item);
					} else {
						mutJournalRepository.updateItem(item);
					}
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}
					rollback = true;
				} finally {
					trd.end(rollback);
				}
			}
		} catch (TransactionDemarcationException e) {
			if (isLoggingError()) {
				logError("TransactionDemarcationException occured: ", e);
			}
		}
	}

	private String getFileName(String prefix, long sequance, int nmbChunk) {
		String suffix = "";
		if (nmbChunk != -1) {
			long nmb = sequance + nmbChunk;
			suffix = "_" + UtilFormat.formatString(Long.toString(nmb), 6, '0', false) + "_";
		}
		return prefix + suffix + Constants.DATE_FORMAT_YYYYMMDD.format(new Date());
	}

	protected void checkFolders() {
		super.checkFolders();

		outputDir = new File(getRootDir(), Constants.OUTPUT_FOLDER);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}
	
	private String generateLine(RepositoryItem skuItem) throws Exception {
		Integer codeArticle = (Integer) skuItem.getPropertyValue(Constants.PROPERTY_CODE_ARTICLE);
		String plusProduit = (String) skuItem.getPropertyValue(Constants.PROPERTY_PLUS_DU_PRODUIT);
		String displayName = (String) skuItem.getPropertyValue(Constants.PROPERTY_DISPLAY_NAME);
		String clientLong = (String) skuItem.getPropertyValue(Constants.PROPERTY_LIBELLE_CLIENT_LONG);
		String description = (String) skuItem.getPropertyValue(Constants.PROPERTY_DESCRIPTION);
		String garantie = (String) skuItem.getPropertyValue(Constants.PROPERTY_GARANTIE);
		Integer poidsUV = (Integer) skuItem.getPropertyValue(Constants.PROPERTY_POIDSUV);

		StringBuilder sb = new StringBuilder(3000);
		String emptyString = "";

		sb.append(UtilFormat.formatString(codeArticle.toString(), SIZE_FIELD_6, CHAR_ZERO, false)).append(
				FIELD_SEPARATOR);

		sb.append(removeLineTerminators(UtilFormat.formatString(plusProduit, SIZE_FIELD_1000, CHAR_GAP, true))).append(
				FIELD_SEPARATOR);

		sb.append(removeLineTerminators(UtilFormat.formatString(displayName, SIZE_FIELD_254, CHAR_GAP, true))).append(
				FIELD_SEPARATOR);

		sb.append(removeLineTerminators(UtilFormat.formatString(description, SIZE_FIELD_1000, CHAR_GAP, true))).append(
				FIELD_SEPARATOR);

		sb.append(removeLineTerminators(UtilFormat.formatString(clientLong, SIZE_FIELD_300, CHAR_GAP, true))).append(
				FIELD_SEPARATOR);

		sb.append(removeLineTerminators(UtilFormat.formatString(garantie, SIZE_FIELD_50, CHAR_GAP, true))).append(
				FIELD_SEPARATOR);

		if (poidsUV != null) {
			sb.append(UtilFormat.formatString(poidsUV.toString(), SIZE_FIELD_22, CHAR_ZERO, false)).append(
					FIELD_SEPARATOR);
		} else {
			sb.append(UtilFormat.formatString(emptyString, SIZE_FIELD_22, CHAR_ZERO, false)).append(FIELD_SEPARATOR);
		}

		sb.append(generateFieldImages(skuItem));

		sb.append(Constants.LINE_SEPARATOR);

		return sb.toString();
	}
	
	private StringBuilder generateFieldImages(RepositoryItem skuItem) throws RepositoryException {

		StringBuilder line = new StringBuilder(500);

		int idx = -1;
		RepositoryItem largeImageItem = (RepositoryItem) skuItem.getPropertyValue(Constants.PROPERTY_LARGE_IMAGE);
		StringBuilder images = new StringBuilder(200);
		if (null != largeImageItem) {
//			String largeImageurl = (String) largeImageItem.getPropertyValue(Constants.PROPERTY_URL);
//			if (null != largeImageurl && !"".equals(largeImageurl.toString())) {
				Integer codeArticle = (Integer) skuItem.getPropertyValue(Constants.PROPERTY_CODE_ARTICLE);
				String largeImageName = "g_" + codeArticle.toString() + JPG_EXT;
				images.append(largeImageName);
//			}
		}

		String[] urlPictos = (String[]) skuItem.getPropertyValue(Constants.PROPERTY_URL_PICTO);

		StringBuilder sbPictos = new StringBuilder(200);

		idx = -1;
		for (int i = 0; i < urlPictos.length; i++) {
			String res = urlPictos[i];
			if ((idx = res.lastIndexOf(Constants.SEPARATOR)) != -1) {
				sbPictos.append(res.substring(idx + 1, res.length()));
			} else {
				sbPictos.append(res);
			}
			if (i < urlPictos.length - 1) {
				sbPictos.append(COMMA);
			}
		}

		line.append(UtilFormat.formatString(sbPictos.toString(), SIZE_FIELD_200, CHAR_GAP, true)).append(
				FIELD_SEPARATOR);

		line.append(UtilFormat.formatString(images.toString(), SIZE_FIELD_200, CHAR_GAP, true)).append(FIELD_SEPARATOR);

		idx = -1;
		String marqueUrl = getMarqueUrl(skuItem);
		if (marqueUrl != null) {
			if ((idx = marqueUrl.lastIndexOf(Constants.SEPARATOR)) != -1) {
				marqueUrl = marqueUrl.substring(idx + 1, marqueUrl.length());
			}
		} else {
			marqueUrl = "";
		}
		
		line.append(UtilFormat.formatString(marqueUrl, SIZE_FIELD_100, CHAR_GAP, true)).append(FIELD_SEPARATOR);

//		line.append(UtilFormat.formatString(getImageName(skuItem), SIZE_FIELD_100, CHAR_GAP, true)).append(FIELD_SEPARATOR);
		
		return line;
	}

	private String exportImages(RepositoryItem skuItem, String imgDir, List<MutableRepositoryItem> journalItems) throws RepositoryException, MalformedURLException {
		String errMess = null;
		MutableRepository mutJournalRepository = (MutableRepository) getJournalSkuRepository();
		MutableRepositoryItem journalItem = (MutableRepositoryItem) mutJournalRepository.getItem(skuItem
				.getRepositoryId(), ITEM_DESC_EXPORT_SKU_JOURNAL);

		boolean isDownload = false;
		Date mExportDate = null;

		String largeImageName = getImageName(skuItem);
		String mLargeImageUrl = IMAGES_FOLDER + largeImageName;
		if (journalItem != null) {
			String jUrl = (String) journalItem.getPropertyValue(Constants.PROPERTY_URL);
			mExportDate = (Date) journalItem.getPropertyValue(Constants.PROPERTY_DATE_EXPORT);
			if (!mLargeImageUrl.equals(jUrl)) {
				mExportDate = null;
			}
		}

		errMess = downloadImage(largeImageName, mLargeImageUrl, mExportDate, imgDir);
		if (SUCCESS.equals(errMess)) {
			errMess = null;
			TransactionDemarcation trd = new TransactionDemarcation();
			boolean rollback = false;
			isDownload = true;
			try {
				try {
					trd.begin(getTransactionManager());
					if (journalItem == null) {
						journalItem = mutJournalRepository.createItem(ITEM_DESC_EXPORT_SKU_JOURNAL);
						journalItem.setPropertyValue(Constants.PROPERTY_SKU_ID, skuItem.getRepositoryId());
					}
					journalItem.setPropertyValue(Constants.PROPERTY_URL, mLargeImageUrl);
					journalItem.setPropertyValue(Constants.PROPERTY_DATE_EXPORT, new Date());
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
		}
		if (isDownload) {
			journalItems.add(journalItem);
		}
		
		return errMess;
	}

	private String downloadImage(String mImageName, String mImageUrl, Date mExportDate, String imgDir) throws MalformedURLException {

		String result = null;
		String imageUrl = mImageUrl;

		Object expDate = mExportDate;
		long expTime = 0;
		if (expDate != null) {
			expTime = ((Date) expDate).getTime();
		}

		OutputStream outStream = null;
		InputStream is = null;
		URL url = null;
		URLConnection urlConn = null;

		try {
			String urlImg = getServerSetting().getHost() + imageUrl; 
			url = new URL(urlImg);
			urlConn = url.openConnection();
			long lastModified = urlConn.getLastModified();
			if (lastModified == 0) {
				if (isLoggingError()) {
					logError("URL " + urlImg + " is invalid.");
				}
				result = "URL " + urlImg + " is invalid.";
				return result;
			}

			if (!getModifiedImagesOnly() || expTime > lastModified) {
				is = urlConn.getInputStream();

				outStream = new BufferedOutputStream(new FileOutputStream(imgDir + File.separator + mImageName));

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
			result = imageUrl + "; IOException: " + e.getMessage();
		} finally {
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

//	private String getExtension(String path) {
//		if (path != null && (path.lastIndexOf(".") != -1)) {
//			return path.substring(path.lastIndexOf("."), path.length());
//		}
//		return "";
//	}

	private String getImageName(RepositoryItem skuItem) {
		Integer codeArticle = (Integer) skuItem.getPropertyValue(Constants.PROPERTY_CODE_ARTICLE);
		String imageName = "h_" + codeArticle.toString() + JPG_EXT;
		return imageName;
	}
	
}
