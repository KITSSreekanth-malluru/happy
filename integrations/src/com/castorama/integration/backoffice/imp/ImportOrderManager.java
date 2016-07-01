package com.castorama.integration.backoffice.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

import javax.transaction.SystemException;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.order.PropertyNameConstants;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
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
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;
import atg.xml.tools.DefaultXMLToolsFactory;
import atg.xml.tools.XMLToDOMParser;
import atg.xml.tools.XMLToolsFactory;

import com.castorama.commerce.states.BOOrderStates;
import com.castorama.core.RepositoryInvalidationService;
import com.castorama.integration.Constants;
import com.castorama.integration.IntegrationBase;

public class ImportOrderManager extends IntegrationBase implements Schedulable {

	protected static final String PREFIX_FILE = "SCDMXUIL";

	private static final int CASTO_CONTACT = 1;
	private static final String PROP_CVENTE = "CVENTE"; 
	private static final String PROP_BO_SCD_VENTE_WEB = "BO_SCD_VENTE_WEB"; 
	private static final String PROP_CCLIENT_WEB = "CCLIENT_WEB"; 
	private static final String PROP_FREPONSE_CLIENT_ATTENDUE = "FREPONSE_CLIENT_ATTENDUE"; 
	private static final String PROP_DCREATION = "DCREATION";
	private static final String PROP_EMAILED = "EMAILED";
	private static final String PROP_NCOMMANDE_CLIENT = "NCOMMANDE_CLIENT";
	private static final String PROP_LCONTACT = "LCONTACT";

	private static final String PROP_BO_STATE = "BOState";
	private static final String PROP_BO_STATE_NUM = "BOStateNum";
	private static final String PROP_BO_STATE_DETAIL = "BOStateDetail";
	private static final String PROP_FETAT_INTEGRATION = "fEtatIntegration";
	
	private static final String SUBJECT_NEW_MESSAGE = "CASTORAMA - Message au sujet de votre commande n° ";
	private static final String SUBJECT_NEEDS_ANSWER = "CASTORAMA - Attente de votre réponse - commande n° ";
	
	private static final int DEFAULT_TRANSACTION_TIMEOUT = 300;

	private File workingDir;
	private File errorDir;
	private File archiveDir;
	
	private TemplateEmailInfo mDefaultEmailInfo;
	private TemplateEmailSender mEmailSender = null;
	private String templateUrlReceived = null;
	private String templateUrlNeedsAnswer = null;
	
	private Repository profileRepository;
	private Repository orderRepository;
	
	private BOOrderStates orderStates;
    private RepositoryInvalidationService invalidationService;


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
	 * Sets the email send component. This is configured in the component property file.
	 * 
	 * @param pEmailSender -
	 *            the email send component
	 */
	public void setEmailSender(TemplateEmailSender pEmailSender) {
		mEmailSender = pEmailSender;
	}

	public TemplateEmailSender getEmailSender() {
		return this.mEmailSender;
	}
	
	/**
	 * Gets the email send component. This is configured in the component property file.
	 * 
	 * @return the email send component
	 */
	public String getTemplateUrlReceived() {
		return templateUrlReceived;
	}

	/**
	 * Sets the URL for the email template used to send the email. This is configured in the component property file.
	 * 
	 * @param pTemplateUrl -
	 *            the URL
	 */
	public void setTemplateUrlReceived(String templateUrlReceived) {
		this.templateUrlReceived = templateUrlReceived;
	}

	/**
	 * Gets the email send component. This is configured in the component property file.
	 * 
	 * @return the email send component
	 */
	public String getTemplateUrlNeedsAnswer() {
		return templateUrlNeedsAnswer;
	}

	/**
	 * Sets the URL for the email template used to send the email. This is configured in the component property file.
	 * 
	 * @param pTemplateUrl -
	 *            the URL
	 */
	public void setTemplateUrlNeedsAnswer(String templateUrlNeedsAnswer) {
		this.templateUrlNeedsAnswer = templateUrlNeedsAnswer;
	}

	
	public Repository getProfileRepository() {
		return profileRepository;
	}

	public void setProfileRepository(Repository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public Repository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public BOOrderStates getOrderStates() {
		return orderStates;
	}

	public void setOrderStates(BOOrderStates orderStates) {
		this.orderStates = orderStates;
	}

	public ImportOrderManager() {
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
	 * This method is invoked according to the schedule.
	 * 
	 * @param scheduler
	 *            Scheduler object to perform the task.
	 * @param job
	 *            ScheduledJob object to be performed as a task.
	 */
	public void performScheduledTask(Scheduler scheduler, ScheduledJob job) {
		try {
			importOrders ();
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(e);
			}
		}
	}

	public void importOrders () throws FileNotFoundException {
		if (isLoggingDebug()) {
			logDebug("start - ImportOrderManager - importOrders. ");
		}

		checkMandatoryFolders();
		checkProperties();
		try {
			
			workingDir = getWorkingDir();
			errorDir = getErrorDir();
			archiveDir = getDir(Constants.ARCHIVE_FOLDER);

			File[] boFiles = getUploadFiles(null);
			if (boFiles == null || boFiles.length == 0) {
				if (isLoggingDebug()) {
					logDebug("Files for upload not found.  " + getRootDir() + File.separator
							+ Constants.INPUT_FOLDER);
				}
				return;
			}

			PrintWriter logWriter = null;

			for (File file : boFiles) {
				File statusFile = new File(workingDir, getFileName(file) + ".log");
				logWriter = new PrintWriter(statusFile);
				logWriter.println("Import Date: " + Constants.DATE_FORMAT_LOG.format(new Date()));

				try {
					File workingFile = getWorkingFile(file);
					if (isLoggingDebug()) {
						logDebug("processing: " + workingFile);
					}
					if (workingFile == null) {
						continue;
					}

					int countSuccessOrder = 0;
					int countFailOrder = 0;
					StringBuilder listFailedOrders = new StringBuilder();
					try {
						List<String> tmpNames = parseFluxXml(workingFile);
						if (tmpNames == null || tmpNames.isEmpty()) {
							if (isLoggingDebug()) {
								logDebug("Temp Files not found.");
							}
							return;
						}

						for (String tmpName : tmpNames) {
							File tmpFile = new File(workingDir, tmpName);
							if (uploadXml(tmpFile)) {
								countSuccessOrder++;
							} else {
								countFailOrder++;
								listFailedOrders.append(getOrderIdFromName(tmpFile.getName())).append("\n");
							}
							tmpFile.delete();
						}


						((GSARepository)getRepository()).invalidateCaches();
						
						String status = countFailOrder > 0 ? "Failed" : "Success";
						logWriter.println("Status: " + status);
						logWriter.println("Number of orders imported successfully: " + countSuccessOrder);
						logWriter.println("Number of failed orders: " + countFailOrder);
						if (countFailOrder > 0) {
							logWriter.println("List of failed orders:");
							logWriter.println(listFailedOrders.toString());
						}
						
						logWriter.flush();
						logWriter.close();

						if (countFailOrder > 0) {
							moveFileIn(workingFile, errorDir);
							moveFileIn(statusFile, errorDir);
						} else {
							workingFile.delete();
							moveFileIn(statusFile, archiveDir);
							
						}
/* 						
						try {
							sendEmailToCustomer();						
						} catch (Exception e) {
							if (isLoggingError()) {
								logError("sendEmailToCustomer: ", e);
							}
						}
*/
					} catch (Exception e) {
						if (isLoggingError()) {
							logError("Exception occured: ", e);
						}
						if (logWriter != null) {
							logWriter.println("Status: Failed");
							e.printStackTrace(logWriter);
							logWriter.close();
							moveFileIn(statusFile, errorDir);
						}
					}
				} catch (Exception e) {
					if (isLoggingError()) {
						logError("Exception occured: ", e);
					}
					if (logWriter != null) {
						logWriter.println("Status: Failed");
						e.printStackTrace(logWriter);
						logWriter.close();
						moveFileIn(statusFile, errorDir);
					}
				}
			}

		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish - ImportOrderManager - importOrders ");
		}

        invalidateCaches();

	}

	private boolean uploadXml(File tmpFile) {
		if (isLoggingDebug()) {
			logDebug("start - uploadXml: " + tmpFile.getAbsolutePath());
		}

		XMLToolsFactory factory = DefaultXMLToolsFactory.getInstance();
		XMLToDOMParser domParser = getXMLToDOMParser(factory);

		boolean rollback = true;
		boolean result = false;
		TransactionDemarcation trd = new TransactionDemarcation();
		try {
			InputStream inputStream = new FileInputStream(tmpFile);
			String orderId = getOrderIdFromName(tmpFile.getName());
			
			try {
				getTransactionManager().setTransactionTimeout(getTransactionTimeout());
				trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRES_NEW);

				TemplateParser.addToTemplate(inputStream, null, domParser,
						(atg.adapter.gsa.GSARepository) getRepository(), TemplateParser.PARSE_NORMAL,
						false, new PrintWriter(System.out), null);

				rollback = false;
				result = true;
			} catch (Exception e) {
				if (isLoggingError()) {
					logError("Exception occured trying to add/update items", e);
				}
			} finally {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					if (isLoggingError()) {
						logError("IOException occured: ", ioe);
					}
				}
				try {
					trd.end(rollback);
				} catch (TransactionDemarcationException e) {
					logError("TransactionDemarcationException occured: ", e);
				}
				getTransactionManager().setTransactionTimeout(0);
			}

			if (result) {
				updateOrderFO(orderId);
				
			}
		} catch (SystemException e) {
			if (isLoggingError()) {
				logError("setTransactionTimeout failed. ", e);
			}
		} catch (Exception e) {
			if (isLoggingError()) {
				logError("Exception occured: ", e);
			}
		}
		if (isLoggingDebug()) {
			logDebug("finish -  uploadXml: result="+result);
		}
		return result;
	}
	
	private String getOrderIdFromName(String fileName) {
		return fileName.substring(0, fileName.indexOf("_"));
	}

	private void updateOrderFO(String orderId) {
		if (isLoggingDebug()) {
			logDebug("updateOrderFO: orderId=" + orderId);
		}
		if (orderId == null) {
			return;
		}

		try{
			String query = "NCOMMANDE_CLIENT = ?0";
			RepositoryView venteView = getRepository().getView("BO_SCD_VENTE_WEB");
			RqlStatement statment = RqlStatement.parseRqlStatement(query);
			RepositoryItem[] items =  statment.executeQueryUncached(venteView, new Object[] {orderId});
			if (items == null || items.length == 0) {
				if (isLoggingDebug()) {
					logDebug("items not found by id=" + orderId);
				}
				return;
			}
			
			RepositoryItem boItem = items[0];
			if (boItem != null) {
				short boState = (Short) boItem.getPropertyValue("TETAT_COMMANDE_C602");
				int state = new Integer(boState);

				MutableRepository orderMutableRepository = (MutableRepository) getOrderRepository();
				MutableRepositoryItem orderItem = orderMutableRepository.getItemForUpdate(orderId, "order");
				if (orderItem != null) {
					orderItem.setPropertyValue(PROP_FETAT_INTEGRATION, true);
					orderItem.setPropertyValue(PROP_BO_STATE, getOrderStates().getStateString(state));
					orderItem.setPropertyValue(PROP_BO_STATE_NUM, state);
					orderItem.setPropertyValue(PROP_BO_STATE_DETAIL, getOrderStates().getStateDescription(state));

					orderItem.setPropertyValue(PropertyNameConstants.LASTMODIFIEDDATE, new Date());

					orderMutableRepository.updateItem(orderItem);
					
				} else {
					if (isLoggingDebug()) {
						logDebug("updateOrderFO: Order not found by id=" + orderId);
					}
				}
				
			} else {
				if (isLoggingDebug()) {
					logDebug("updateOrderFO: BOItem is null");
				}
			}
		}catch(Exception e){
			if (isLoggingError()) {
				logError(e);
			}
		}
	}

	private void checkProperties() {
		if (getTransactionTimeout() == null) {
			setTransactionTimeout(DEFAULT_TRANSACTION_TIMEOUT);
		}

		if (isLoggingDebug()) {
			logDebug("transactionTimeout=" + getTransactionTimeout());
		}
	}

	private void sendEmailToCustomer() throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("Start sendEmailToCustomer");
		}
		String query = "EMAILED is null and CSENS_CONTACT_C659 = ?0";
		MutableRepository boRepository = (MutableRepository) getRepository();
		RepositoryView contactView = boRepository.getView("BO_SCD_CONTACT");
		RqlStatement statment = RqlStatement.parseRqlStatement(query);
		RepositoryItem[] items =  statment.executeQueryUncached(contactView, new Object[] {CASTO_CONTACT});
		if (items == null || items.length == 0) {
			if (isLoggingDebug()) {
				logDebug("No selected items.  ");
			}
			return;
		}
		
		if (isLoggingDebug()) {
			logDebug("Selected records: " + items.length);
		}

		String paramOrderId = "orderId";
		String paramOrderdate = "orderDate";
		String paramMessage = "message";
		
		for (RepositoryItem item : items) {
			Long webId = (Long) item.getPropertyValue(PROP_CVENTE);
			// logDebug("Contact Property CVENTE=" + webId);

			RepositoryItem webItem = getRepository().getItem(webId.toString(), PROP_BO_SCD_VENTE_WEB);
			String userId = (String) webItem.getPropertyValue(PROP_CCLIENT_WEB);

			RepositoryItem user = getProfileRepository().getItem(userId, Constants.ITEM_USER);
			if (user != null) {
				boolean needAnswer = (Boolean) item.getPropertyValue(PROP_FREPONSE_CLIENT_ATTENDUE);
				
				try {
					Map params = new HashMap();
					params.put("user", user);
					String orderId = (String)webItem.getPropertyValue(PROP_NCOMMANDE_CLIENT);
					params.put(paramOrderId, orderId);
					params.put(paramOrderdate, (Date)item.getPropertyValue(PROP_DCREATION));
					params.put(paramMessage, (String)item.getPropertyValue(PROP_LCONTACT));
					
					String messageTo = (String)user.getPropertyValue(Constants.PROPERTY_LOGIN);
					String firstname = (String) user.getPropertyValue(Constants.PROPERTY_FIRST_NAME);
					String lastname = (String) user.getPropertyValue(Constants.PROPERTY_LAST_NAME);
					if (!StringUtils.isEmpty(firstname) && !StringUtils.isEmpty(lastname)) {
						messageTo = new StringBuilder().append(firstname).append(" ")
						.append(lastname).append("<").append(messageTo).append(">").toString();
					}

					sendEmail(messageTo, params, orderId, needAnswer);

					((MutableRepositoryItem)item).setPropertyValue(PROP_EMAILED, new Date());
					boRepository.updateItem((MutableRepositoryItem)item);
				} catch (Exception e) {
					if (isLoggingError()) {
						logError(e);
					}
				}
			} else {
				if (isLoggingDebug()) {
					logDebug("User not found by Id="+userId);
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("End sendEmailToCustomer");
		}
	}
	
	private void sendEmail(String messageTo, Map params, String orderId, boolean needAnswer) throws TemplateEmailException {
		TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();
		String subject;
		if (needAnswer) {
			emailInfo.setTemplateURL(getTemplateUrlNeedsAnswer());
			subject = SUBJECT_NEEDS_ANSWER + orderId;
		} else {
			emailInfo.setTemplateURL(getTemplateUrlReceived());
			subject = SUBJECT_NEW_MESSAGE + orderId;
		}
		
		emailInfo.setMessageFrom(emailInfo.getMessageFrom());

		if (isLoggingDebug()) {
			logDebug("MesssageTo="+messageTo);
		}
		if (messageTo != null) {
			emailInfo.setTemplateParameters(params);
			emailInfo.setMessageTo(messageTo);
			emailInfo.setMessageSubject(subject);
			List<String> recipents = new ArrayList<String>();
			recipents.add(messageTo);
			getEmailSender().sendEmailMessage(emailInfo, recipents, true, false);
		} else {
			throw new TemplateEmailException("Email of user is null.");
		}
	}

    private void invalidateCaches() {
        try {
            getInvalidationService().invalidateRepository(getRepository());
        } catch (Throwable e) {
            String messagePrefix = "Exception during cache invalidation!: ";
            if (isLoggingDebug()) {
                logError(messagePrefix, e);
            } else if (isLoggingError()) {
                logError(messagePrefix + e.getMessage());
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
		ScheduledJob job = new ScheduledJob("OrderSchedulableService", "Upload Files to BO Repository",
				getAbsoluteName(), getSchedule(), this, ScheduledJob.SCHEDULER_THREAD);
		jobId = getScheduler().addScheduledJob(job);
	}

	/**
	 * Stops listening for messages and disconnects from the queue.
	 */
	public void doStopService() throws ServiceException {
		getScheduler().removeScheduledJob(jobId);
	}

    public RepositoryInvalidationService getInvalidationService() {
        return invalidationService;
    }

    public void setInvalidationService(RepositoryInvalidationService invalidationService) {
        this.invalidationService = invalidationService;
    }
}
