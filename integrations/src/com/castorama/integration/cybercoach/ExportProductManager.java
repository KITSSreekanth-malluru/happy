package com.castorama.integration.cybercoach;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import atg.service.scheduler.Schedulable;
import atg.service.scheduler.Schedule;
import atg.service.scheduler.ScheduledJob;
import atg.service.scheduler.Scheduler;

import com.castorama.integration.Constants;
import com.castorama.integration.ExportManagerBase;
import com.castorama.integration.util.MiscUtils;

public class ExportProductManager extends ExportManagerBase implements Schedulable {

	private static final String PREFIX_FILE_NAME = "SPC_";
	
	private static final String FILTER_CYBERCOACH = "cybercoach=1";	
	
	private static final String TAG_PRODUCTS = "<Products>";
	private static final String END_TAG_PRODUCTS = "</Products>";
	private static final String TAG_PRODUCT = "<Product>";
	private static final String END_TAG_PRODUCT = "</Product>";
	private static final String TAG_PRODUCT_REF = "<ProductRef>";
	private static final String END_TAG_PRODUCT_REF = "</ProductRef>";
	private static final String TAG_PRODUCT_PRICE = "<ProductPrice>";
	private static final String END_TAG_PRODUCT_PRICE = "</ProductPrice>";
	
	private static NumberFormat formatter = new DecimalFormat("#0.00");

	private PriceListManager mPriceListManager;

	// Scheduler property
	private Scheduler scheduler;
	// Schedule property
	private Schedule schedule;


	public void setPriceListManager(PriceListManager pPriceListManager) {
		mPriceListManager = pPriceListManager;
	}

	public PriceListManager getPriceListManager() {
		return mPriceListManager;
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
		ScheduledJob job = new ScheduledJob("CybercoachSchedulableService", "Export products from Repository",
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
	 * Export products
	 */
	public void exportProducts() {
		if (isLoggingInfo()) {
			logInfo("start - cybercoach.ExportProductManager - exportProducts");
		}
		
		checkFolders();
		
		int countSuccessItems = 0;
		int countFailItems = 0;

		File logFile = null;
		PrintWriter logWriter = null;
		File workingFile = null;
		PrintWriter printWriter = null;
		StringBuilder warningBuilder = new StringBuilder();
		try {
			logFile = new File(workingDir, genFileName(PREFIX_FILE_NAME, ".log"));
			logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile, false)));
			logWriter.println(LOG_EXPORT_DATE + Constants.DATE_FORMAT_LOG.format(new Date()));
			
			workingFile = new File(workingDir, genFileName(PREFIX_FILE_NAME, ".xml"));
			printWriter = new PrintWriter(workingFile, "UTF-8");
			printWriter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			printWriter.println(TAG_PRODUCTS);

			int countSkus = countCastoSku(FILTER_CYBERCOACH);
			if (isLoggingDebug()) {
				logDebug("count Sku=" + countSkus);
			}
			
			if (countSkus > 0) {
				int nmbChunk = 1;
				int startPosition = 0;
				
				int cntChunk = countSkus / DEFAULT_CHUNK_SIZE;
				if (cntChunk == 0) {
					cntChunk = 1;
				} else if ((countSkus % DEFAULT_CHUNK_SIZE) > 0) {
					cntChunk++;
				}

				if (isLoggingDebug()) {
					logDebug("Number Chunk=" + cntChunk);
				}


				for (; nmbChunk <= cntChunk; nmbChunk++) {

					RepositoryItem[] skuItems = getChunkProducts(FILTER_CYBERCOACH, startPosition, DEFAULT_CHUNK_SIZE);
					if (skuItems != null && skuItems.length > 0) {
						if (isLoggingDebug()) {
							logDebug("Selected Sku=" + skuItems.length);
						}
						String line;
						for (RepositoryItem item : skuItems) {
							line = generateXmlProduct(item, warningBuilder);
							if (line != null) {
								printWriter.print(line);
								countSuccessItems++;
							} else {
								countFailItems++;
							}
						}
					} else {
						if (isLoggingDebug()) {
							logInfo("No sku selected!");
						}
					}
					
					startPosition += DEFAULT_CHUNK_SIZE;
				}
			}

			printWriter.println(END_TAG_PRODUCTS);
			printWriter.close();

			MiscUtils.copyFile(workingFile, new File(outputDir, workingFile.getName()));
			
			File statusDir = null;
			if (countFailItems > 0) {
				logWriter.println("Status: Failed");
				statusDir = errorDir;
			} else {
				logWriter.println("Status: Success");
				statusDir = archiveDir;
			}
			logWriter.println("File to Export: " + workingFile.getName());
			logWriter.println(LOG_NUMBER_SKUS_SUCCESS + countSuccessItems);
			logWriter.println(LOG_NUMBER_SKUS_FAIL + countFailItems);
			if (countFailItems > 0) {
				logWriter.println(LOG_LIST_SKUS_FAIL);
				logWriter.println(warningBuilder.toString());
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
			try {
				if (logWriter != null) {
					logWriter.println("Status: Failed");
					e.printStackTrace(logWriter);
					logWriter.close();
					MiscUtils.copyFile(logFile, new File(errorDir, logFile.getName()));
				}
			} catch (Exception ex) {
				logError(ex);
			}
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
			if (logWriter != null) {
				logWriter.close();
			}
			if (workingFile != null) {
				workingFile.delete();
			}
			if (logFile != null) {
				logFile.delete();
			}
			
		}

		if (isLoggingInfo()) {
			logInfo("finish - cybercoach.ExportProductManager - exportProducts");
		}
	}
	
	private String generateXmlProduct(RepositoryItem skuItem, StringBuilder warning) {
		String skuId = skuItem.getRepositoryId();
		String priceList = getPrice(skuItem);
		if (priceList == null) {
			warning.append(skuId).append("\n");
			return null;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(TAG_PRODUCT).append(Constants.LINE_SEPARATOR);
			
			sb.append(TAG_PRODUCT_REF).append(skuId.substring(PREFIX_CAST0.length())).append(END_TAG_PRODUCT_REF).append(Constants.LINE_SEPARATOR);
			sb.append(TAG_PRODUCT_PRICE).append(priceList).append(END_TAG_PRODUCT_PRICE).append(Constants.LINE_SEPARATOR);

			sb.append(END_TAG_PRODUCT).append(Constants.LINE_SEPARATOR);
			return sb.toString();
		}
	}
	
	private String getPrice(RepositoryItem skuItem) {
		String result = null;
		try {
			RepositoryItem priceList = mPriceListManager.getPriceList(null, "priceList");
			if (priceList != null) {
				
				RepositoryItem price = mPriceListManager.getPrice(priceList, null, skuItem);
	            
	            if (price != null) {
	                Object listPrice = price.getPropertyValue("listPrice");
	                if (listPrice == null) {
	                    if (isLoggingDebug()) {
	                        logDebug("ERR: listPrice == null, no pricelist of ListPrice");
	                    }

	                    return null;
	                }

	                double productPrice = (Double)listPrice;
	                
	                Object ratio = skuItem.getPropertyValue("ratio");
	                if (ratio != null) {
	                	productPrice *= (Double) ratio;
	                }
	    			result = formatter.format(productPrice).replace('.', ',');
                    if (isLoggingDebug()) {
                        logDebug("# listPrice =" + listPrice + "; Ratio =" + ratio + " = " + result);
                    }
	            	
	            } else {
	                if (isLoggingDebug()) {
	                    logDebug("ERR: amountPriceList == null");
	                }
	            }
				
			} else {
	            if (isLoggingDebug()) {
	                logDebug("ERR: priceList == null");
	            }
			}
			
		} catch (PriceListException e) {
            if (isLoggingError()) {
                logError(e);
            }
            result = null;
		}

		return result;
	}
	
	
	
	private String getPriceList(RepositoryItem skuItem) {
		String result = null;
		try {
			RepositoryItem priceList = mPriceListManager.getPriceList(null, "priceList");
			if (priceList != null) {
				
				RepositoryItem amountPriceList = null;

				Collection produits = (Collection) skuItem.getPropertyValue("parentProducts");
	            RepositoryItem product;
	            for (Iterator iter = produits.iterator(); iter.hasNext();) {
	                product = (RepositoryItem) iter.next();

	                if (isLoggingDebug()) {
	                    logDebug("# Product associate (one normally) : " + product);
	                }

	                amountPriceList = getPriceListManager().getPrice(priceList, product, skuItem, true);
	                
	            }
	            
	            if (amountPriceList != null) {
	                Object propAmountPriceList = amountPriceList.getPropertyValue("listPrice");
	                if (propAmountPriceList == null) {
	                    if (isLoggingDebug()) {
	                        logDebug("ERR: propAmountPriceList == null, no pricelist of ListPrice");
	                    }

	                    return null;
	                }

	                result = Double.toString((Double) propAmountPriceList);
	            	
	            } else {
	                if (isLoggingDebug()) {
	                    logDebug("ERR: amountPriceList == null");
	                }
	            }
				
			} else {
	            if (isLoggingDebug()) {
	                logDebug("ERR: priceList == null");
	            }
			}
			
		} catch (PriceListException e) {
            if (isLoggingError()) {
                logError(e);
            }
            result = null;
		}

		return result;
	}

	protected void checkFolders() {
		super.checkFolders();

		outputDir = new File(getRootDir(), Constants.OUTPUT_FOLDER);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}
	
}
