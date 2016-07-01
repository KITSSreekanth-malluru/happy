package com.castorama.integration.bazaarvoice;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.seo.SEOUtils;
import com.castorama.sftp.SftpService;
import com.castorama.utils.ServerSetting;

/**
 * Export data feed service.
 * 
 * @author Andrew Logvinov
 */
public class SetupDataFeedManager extends GenericService implements Schedulable {

	private static final int DEFAULT_CHUNK_SIZE = 5000;

	/**
	 * private String rootDir; property wrapper
	 */
	private File rootFolderFile;

	/**
	 * Get root folder file system path.
	 * 
	 * @return managed property value.
	 */
	public String getRootDir() {
		return getRootFolderFile().getPath();
	}
	
	/**
	 * Set root folder file system path.
	 * 
	 * @param rootFolder the folder filename.
	 */
	public void setRootDir(String rootFolder) {
		this.rootFolderFile = new File(rootFolder);
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
	 * Global server settings
	 */
	private ServerSetting serverSetting;

	/**
	 * @return the serverSetting
	 */
	public ServerSetting getServerSetting() {
		return serverSetting;
	}

	/**
	 * @param serverSetting the serverSetting to set
	 */
	public void setServerSetting(ServerSetting serverSetting) {
		this.serverSetting = serverSetting;
	}

	
	/**
	 * @return
	 */
	private String getUrlPrefix() {
		return getServerSetting().getHost();
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
	
	/**
	 * 
	 */
	private String contextRoot = "";

	/**
	 * @return the contextRoot
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * @param contextRoot the contextRoot to set
	 */
	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	private int jobId;

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
	 * SFTP upload folder
	 */
	private String ftpDestDir;

	/**
	 * @return the ftpDestDir
	 */
	public String getFtpDestDir() {
		return ftpDestDir;
	}

	/**
	 * @param ftpDestDir the ftpDestDir to set
	 */
	public void setFtpDestDir(String ftpDestDir) {
		this.ftpDestDir = ftpDestDir;
	}

	/**
	 * 
	 */
	public void exportProducts(){
		
		try {
			DataFeedWriter writer = new DataFeedWriter(getWorkingFile());
			
			List<String> failures = new ArrayList<String>();
			int categories = 0;
			int products = 0;
			int documents = 0;
			
			FeedLoader loader = new FeedLoader(repository);
			File result = null;
			
			try {
				Iterator<CategoryProductFeed> it = loader.categoryIterator();
				while (it.hasNext()) {
					CategoryProductFeed itm = it.next();
					
					if (null != itm) {
						urlFilter(itm);
						
						try {
							writer.write(itm);
							categories ++;
						} catch (NullPointerException e) {
							failures.add(e.getMessage());
						}
					}
				}

				int startPosition = 0;
				int portion = DEFAULT_CHUNK_SIZE;
				int cntChunk = 1;
				int nmbChunk = 1;

				int countRecords = loader.countProducts();
				if (isLoggingDebug()) {
					logDebug("count Products=" + countRecords);
				}
				
				if (portion > 0) {
					cntChunk = countRecords / portion;
					if (cntChunk == 0) {
						cntChunk = 1;
					} else if ((countRecords % portion) > 0) {
						cntChunk++;
					}
				}
				if (isLoggingDebug()) {
					logDebug("count products Chunk=" + cntChunk);
				}
				
				for (; nmbChunk <= cntChunk; nmbChunk++) {
					Iterator<ProductFeed> itp = loader.productIterator(startPosition, portion);
					while (itp.hasNext()) {
						ProductFeed itm = itp.next();
						
						if (null != itm) {
							urlFilter(itm);
							
							try {
								writer.write(itm);
								products ++;
							} catch (NullPointerException e) {
								failures.add(e.getMessage());
							}
						}
					}

					startPosition += portion;
				}


				startPosition = 0;
				portion = DEFAULT_CHUNK_SIZE;
				cntChunk = 1;
				nmbChunk = 1;

				countRecords = loader.countDocuments();
				if (isLoggingDebug()) {
					logDebug("count Documents=" + countRecords);
				}
				
				if (portion > 0) {
					cntChunk = countRecords / portion;
					if (cntChunk == 0) {
						cntChunk = 1;
					} else if ((countRecords % portion) > 0) {
						cntChunk++;
					}
				}
				if (isLoggingDebug()) {
					logDebug("count documents Chunk=" + cntChunk);
				}

				for (; nmbChunk <= cntChunk; nmbChunk++) {

					Iterator<ProductFeed> itd = loader.documentIterator();
					while (itd.hasNext()) {
						ProductFeed itm = itd.next();
						
						if (null != itm) {
							urlFilter(itm);
							
							try {
								writer.write(itm);
								documents ++;
							} catch (NullPointerException e) {
								failures.add(e.getMessage());
							}
						}
					}

					startPosition += portion;
				}
				
				writer.close();
				
				result = writer.getResultFile();
				
				SchemaFactory jaxp = 
					SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				Schema schema = 
					jaxp.newSchema(new StreamSource(getClass().getResourceAsStream("productFeed.xml")));
				Validator validator = 
					schema.newValidator();
				
				Source source = 
					new SAXSource(new InputSource(new FileInputStream(result)));
				
				validator.validate(source);
				
				SftpService service = new SftpService();
				
				String dst = getFtpDestDir();
				if (dst.endsWith("/")) {
					dst = dst + result.getName();
				} else {
					dst = dst + "/" + result.getName();
				}

				File out = new File(getOutputFile(), result.getName());
				result.renameTo(out);
				result = out;
				
				service.putFile(getAccessCredentials().getSftpHost(),
						getAccessCredentials().getSftpUser(),
						getAccessCredentials().getSftpPassword(),
						result, dst);
				
				PrintWriter log = new PrintWriter(new File(getOutputFile(), result.getName().replaceAll("\\.xml", ".log")));
				logSuccess(log, failures, categories, products, documents);
			} catch (Exception e) {
				e.printStackTrace();
				
				if (null != result) {
					result.renameTo(new File(getErrorFile(), result.getName()));
				}
				
				PrintWriter log = new PrintWriter(new File(getErrorFile(), result.getName().replaceAll("\\.xml", ".log")));
				logFails(log, e.getMessage(), result.getPath(), e);
			}
		} catch (Throwable e) {
			logError(e.getMessage(), e);
		}
	}

	/**
	 * @param itm
	 * @throws UnsupportedEncodingException
	 */
	private void urlFilter(ProductFeed itm) throws UnsupportedEncodingException {
		if (itm.isDocument()) {
			String url = itm.getProductPageUrl();
			if (null != url) {
				itm.setProductPageUrl(getUrlPrefix() + '/' + getContextRoot() + URLEncoder.encode(itm.getProductPageUrl(), "UTF-8"));
			}			
		} else {
			String url = getUrlPrefix() + '/' + getContextRoot() + SEOUtils.getProductURL(itm.getItem());
			itm.setProductPageUrl(url);
		}
		
		if (null != itm.getImageUrl()) {
			itm.setImageUrl(getUrl(itm.getImageUrl()));
		}
	}

	/**
	 * @param itm
	 * @throws UnsupportedEncodingException
	 */
	private void urlFilter(CategoryProductFeed itm) throws UnsupportedEncodingException {
		String url = getUrlPrefix() + '/' + getContextRoot() + SEOUtils.getCategoryURL(itm.getItem());
		itm.setCategoryPageUrl(url);
	}

	/**
	 * @param url
	 * @return
	 */
	private String getUrl(String url) {
		if (null == url) return null;
		if ("".equals(url)) {
			return url;
		} else {
			if (url.startsWith("/")) {
				return getUrlPrefix() + url;
			} else {
				return getUrlPrefix() + '/' + url;
			}
		}		
	}

	/**
	 * @return
	 */
	private File getRootFolderFile() {
		if (!rootFolderFile.exists()) {
			rootFolderFile.mkdir();
		}
		
		return rootFolderFile;
	}
	
	/**
	 * @return
	 */
	private File getOutputFile() {
		File result = new File(getRootFolderFile(), "output");

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


	/* (non-Javadoc)
	 * @see atg.service.scheduler.Schedulable#performScheduledTask(atg.service.scheduler.Scheduler, atg.service.scheduler.ScheduledJob)
	 */
	public void performScheduledTask(Scheduler arg0, ScheduledJob arg1) {
		try {
			exportProducts();
		} catch (Throwable e) {
			logError(e);
		}
	}
	
	@Override
	public void doStartService() throws ServiceException {
		ScheduledJob job = new ScheduledJob(getClass().getSimpleName(),
				"Setup data feed service",
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
	
	private static void logFails(PrintWriter log, String message, String srcFile, Throwable t) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Export Date: " + df.format(new Date()));
		log.println("Status: Failed");
		log.println("File to export: " + srcFile);
		log.println(message);
		t.printStackTrace(log);
		
		log.flush();
		log.close();
	}
	
	private static void logSuccess(PrintWriter log, List<String> failures, int categories, 
			int products, int documents) {
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		log.println("Export Date: " + df.format(new Date()));
		if (failures.size() > 0) {
			log.println("Status: Failed");
		} else {
			log.println("Status: Success");
		}
		
		log.println("Exported categories: " + categories);
		log.println("Exported products: " + products);
		log.println("Exported documents: " + documents);

		if (failures.size() > 0) {
			log.println("Failed to export records: " + failures.size());
			for (String string : failures) {
				log.println(string);
			}
		}
		
		log.flush();
		log.close();
	}
}
