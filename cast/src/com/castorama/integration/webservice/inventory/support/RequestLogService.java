package com.castorama.integration.webservice.inventory.support;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import com.castorama.integration.webservice.LogService;
import com.castorama.integration.webservice.inventory.model.LogMessage;

/**
 * @author EPAM team
 */
public class RequestLogService extends GenericService implements LogService<LogMessage> {

    //region Statics
    private static final String WEB_SERVICE_REQUESTS_DESCRIPTOR = "inventoryWebServiceRequests";
    private static final String REQUEST_DATE = "requestDate";
    private static final String STORE_ID = "storeId";
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_ITEMS = "orderItems";
    private static final String REQUEST_STATUS = "requestStatus";
    //endregion

    //region Dependencies
    private Repository webServicesLogRepository;
    //endregion

    @Override
    public void log(LogMessage logMessage) {
        try {
            addLogMessage(logMessage);
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError("Unable to add web service request information to repository.", e);
            }
        }

    }

    private void addLogMessage(LogMessage logMessage) throws RepositoryException {
        MutableRepository mutableRepository = (MutableRepository) webServicesLogRepository;
        MutableRepositoryItem mutableRepositoryItem = mutableRepository.createItem(WEB_SERVICE_REQUESTS_DESCRIPTOR);
        mutableRepositoryItem.setPropertyValue(REQUEST_DATE, logMessage.getRequestDate());
        mutableRepositoryItem.setPropertyValue(STORE_ID, logMessage.getStoreId());
        mutableRepositoryItem.setPropertyValue(ORDER_ID, logMessage.getOrderId());
        mutableRepositoryItem.setPropertyValue(ORDER_ITEMS, logMessage.getProductsIds());
        mutableRepositoryItem.setPropertyValue(REQUEST_STATUS, logMessage.getRequestStatus());
        mutableRepository.addItem(mutableRepositoryItem);
    }

    public Repository getWebServicesLogRepository() {
        return webServicesLogRepository;
    }

    public void setWebServicesLogRepository(Repository webServicesLogRepository) {
        this.webServicesLogRepository = webServicesLogRepository;
    }
}
