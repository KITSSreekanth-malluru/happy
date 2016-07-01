package com.castorama.integration.webservice.inventory.support;

import static com.castorama.utils.CastUtils.buildSVSProductId;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.ShippingGroup;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;

import com.castorama.commerce.order.CastCommerceItemImpl;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.profile.CastProfileTools;
import com.castorama.integration.webservice.HttpService;
import com.castorama.integration.webservice.Processor;
import com.castorama.integration.webservice.inventory.message.InventoryRequestMessage;
import com.castorama.integration.webservice.inventory.message.InventoryResponseMessage;
import com.castorama.integration.webservice.inventory.message.InventoryUnit;
import com.castorama.integration.webservice.inventory.model.ProcessResponse;
import com.castorama.integration.webservice.inventory.model.ProcessStatus;
import com.castorama.integration.webservice.inventory.model.RequestParametersWrapper;
import com.castorama.integration.webservice.inventory.model.WebServiceConfiguration;
import com.castorama.stockvisualization.RemoteStockCacheAccessor;
import com.castorama.stockvisualization.StockModel;
import com.castorama.stockvisualization.StockVisualizationManager;
import com.castorama.stockvisualization.Stocks;
import com.castorama.utils.ContextState;

import javax.transaction.TransactionManager;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author EPAM team
 */
public class InventoryCheckProcessor extends GenericService implements Processor<ProcessResponse, RequestParametersWrapper> {

    //region Statics
    private static final String REQUEST_LANGUAGE = "en";
    private static final String CODE_ARTICLE_PROPERTY = "codeArticle";
    private static final String QUANTITY_PROPERTY = "quantity";
    private static final String ERROR_MESSAGE = "Web service call error";
    //endregion

    //region Dependencies
    private CastOrderManager orderManager;
    private HttpService<String> requestService;
    private WebServiceConfiguration configuration;
    private StockVisualizationManager stockVisualizationManager;
    private RequestConverter requestConverter;
    private ResponseConverter responseConverter;
    private ClientLockManager lockManager;
    private TransactionManager transactionManager;
    //endregion
    
    /** remoteStockCacheAccessor property */
    private RemoteStockCacheAccessor remoteStockCacheAccessor;

    @Override
    public ProcessResponse process(RequestParametersWrapper requestWrapper) {
        ProcessResponse processResponse;
        try {
            ContextState contextState = requestWrapper.getContextState();
            CastOrderImpl currentLocal = contextState.getShoppingCart().getCurrentLocal();
            Set<Integer> codeArticles = requestWrapper.getCodeArticles();

            String profileId = contextState.getProfile().getRepositoryId();
            int storeId = requestWrapper.getStoreId();
            if (isLoggingDebug()) {
                vlogDebug("Web service caller information: profileId - {0}, storeId - {1}", profileId, storeId);
            }

            InventoryRequestMessage requestMessage = buildRequestMessage(storeId, codeArticles);
            String requestWebserviceMessage = requestConverter.convertFromMessage(requestMessage);
            if (isLoggingDebug()) {
                vlogDebug("Request web service xml message:\n{0}", requestWebserviceMessage.trim());
            }

            URI requestUri = new URI(configuration.getWebServiceRequestUrl());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            WebServiceRequestWorker requestWorker = new WebServiceRequestWorker(requestService, requestUri, requestWebserviceMessage);
            Future<String> responseWebserviceFuture = executor.submit(requestWorker);
            String responseWebserviceMessage;
            boolean responseTimeoutReached = false;
            try {
                responseWebserviceMessage = responseWebserviceFuture.get(configuration.getWaitingForWebserviceResponse(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                responseTimeoutReached = true;
                if (isLoggingWarning()) {
                    logWarning("Web service response timeout was reached.");
                }
                responseWebserviceMessage = responseWebserviceFuture.get();
            }
            if (isLoggingDebug()) {
                vlogDebug("Response web service xml message:\n{0}", responseWebserviceMessage.trim());
            }

            InventoryResponseMessage responseMessage = responseConverter.convertToMessage(responseWebserviceMessage);
            if (isLoggingDebug()) {
                vlogDebug("Response web service message after converting:{0}", responseMessage.toString());
            }

            List<StockModel> stockModelList;
            Stocks stocks = getStocksObject(storeId, responseMessage);
            
            stockModelList = stockVisualizationManager.verifyStockLevelWithUpdate(stocks);
            if (isLoggingDebug()) {
                vlogDebug("Verifying stock level with update - stage I");
            }
            
            List<String> codeArticlesToInvalidate = new ArrayList<String>();
            for(Integer article : codeArticles)
            	codeArticlesToInvalidate.add(buildSVSProductId(article.toString()));
            getRemoteStockCacheAccessor().invalidateCache(storeId, codeArticlesToInvalidate);
            if (isLoggingDebug()) {
                vlogDebug("Invalidating cache for stockVis instances - stage II");
            }
            
            logStockVisualisationResponseStocks(stockModelList);

            if (!responseTimeoutReached) {
                processResponse = checkInventoryWithItemsUpdate(currentLocal, stockModelList);
            } else {
                processResponse = new ProcessResponse(ProcessStatus.CALL_PROCESS_ERROR, "Web service response timeout was reached. Inventory has been updated.");
            }
        } catch (Throwable throwable) {
            if (isLoggingError()) {
                logError(ERROR_MESSAGE, throwable);
            }
            String message = MessageFormat.format(
                    "Web service call error: {0}. Please, see application log for more information",
                    throwable.getMessage()
            );
            processResponse = new ProcessResponse(ProcessStatus.CALL_PROCESS_ERROR, message);
        }

        return processResponse;
    }

    private InventoryRequestMessage buildRequestMessage(int storeId, Set<Integer> codeArticles) {
        Calendar calendar = Calendar.getInstance();

        return new InventoryRequestMessage(REQUEST_LANGUAGE, calendar.getTime(), storeId, codeArticles);
    }

    private Stocks getStocksObject(int storeId, InventoryResponseMessage responseMessage) {
        Map<String, Long> map = new HashMap<String, Long>();
        List<InventoryUnit> inventoryUnits = responseMessage.getUnitList();
        for (InventoryUnit unit : inventoryUnits) {
            map.put(Integer.toString(unit.getCodeArticle()), unit.getQuantity());
        }

        return new Stocks(storeId, map);
    }

    private ProcessResponse checkInventoryWithItemsUpdate(CastOrderImpl order, List<StockModel> stockModelList) throws Exception {

        String profileId = order.getProfileId();
        Map<Integer, Object> quantityMap = new HashMap<Integer, Object>();
        List commerceItems = order.getCommerceItems();
        ShippingGroup shippingGroup = (ShippingGroup) order.getShippingGroups().get(0);
        CommerceItemManager commerceItemManager = getOrderManager().getCommerceItemManager();
        int stockIsNotEnoughCount = 0;

        for (Object item : commerceItems) {
            CommerceItemImpl commerceItem = (CommerceItemImpl) item;
            Integer codeArticle = (Integer) commerceItem.getPropertyValue(CODE_ARTICLE_PROPERTY);
            quantityMap.put(codeArticle, item);
        }

        ClientLockManager lockManager = getLockManager();
        boolean acquireLock = false;
        try {
            acquireLock = !lockManager.hasWriteLock(profileId, Thread.currentThread());
            if (acquireLock){
                lockManager.acquireWriteLock(profileId, Thread.currentThread());
            }

            TransactionDemarcation td = new TransactionDemarcation();
            td.begin(transactionManager);
            boolean shouldRollback = false;
            try {
                synchronized (order) {

                    for (StockModel model : stockModelList) {
                        CastCommerceItemImpl commerceItem = (CastCommerceItemImpl) quantityMap.get(Integer.parseInt(model.getProdId()));
                        if (commerceItem != null) {
                            Long quantity = (Long) commerceItem.getPropertyValue(QUANTITY_PROPERTY);
                            Long backOfficeQuantity = Long.parseLong(model.getStockAvailable());
                            if (backOfficeQuantity < quantity) {
                                if (backOfficeQuantity > 0) {
                                    commerceItem.setQuantityWasDecreased(true);
                                    commerceItem.setCurrentInventoryValue(backOfficeQuantity);
                                    commerceItem.setQuantity(backOfficeQuantity);
                                    commerceItemManager.removeItemQuantityFromShippingGroup(
                                            order, commerceItem.getId(), shippingGroup.getId(), quantity - backOfficeQuantity
                                    );
                                } else {
                                    order.getRemovedItemsIds().add(commerceItem.getCatalogRefId());
                                    commerceItemManager.removeItemFromOrder(order, commerceItem.getId());
                                }
                                stockIsNotEnoughCount++;
                            }
                        }
                    }

                    getOrderManager().updateOrder(order);
                }
            } catch (CommerceException e) {
                shouldRollback = true;
                throw e;
            } finally {
                td.end(shouldRollback);
            }
        } finally {
            if (acquireLock){
                lockManager.releaseWriteLock(profileId, Thread.currentThread(), true);
            }
        }

        if (stockIsNotEnoughCount > 0) {
            String message = MessageFormat.format("There is/are {0} item(s) with 'not enough' stock status", stockIsNotEnoughCount);
            if (isLoggingDebug()) {
                vlogDebug(message);
            }

            return new ProcessResponse(ProcessStatus.STOCK_IS_NOT_ENOUGH, message);
        } else {
            return new ProcessResponse(ProcessStatus.STOCK_IS_ENOUGH, null);
        }

    }

    private void logStockVisualisationResponseStocks(List<StockModel> stockModelList) {
        if (isLoggingDebug()) {
            if (stockModelList != null) {
                StringBuilder stringBuffer = new StringBuilder("Stock visualisation response stocks:");
                for (StockModel model : stockModelList) {
                    stringBuffer.append("\n").append(model.toString());
                }
                vlogDebug(stringBuffer.toString());
            } else {
                logDebug("Stock visualisation response is empty.");
            }
        }
    }

    private class WebServiceRequestWorker implements Callable<String> {

        private HttpService<String> requestService;
        private URI requestUri;
        private String requestWebserviceMessage;

        private WebServiceRequestWorker(HttpService<String> requestService, URI requestUri, String requestWebserviceMessage) {
            this.requestService = requestService;
            this.requestUri = requestUri;
            this.requestWebserviceMessage = requestWebserviceMessage;
        }

        @Override
        public String call() throws Exception {
            return requestService.doPostRequest(requestUri, requestWebserviceMessage);
        }
    }

    //region Getters/Setters
    public CastOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(CastOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public HttpService<String> getRequestService() {
        return requestService;
    }

    public void setRequestService(HttpService<String> requestService) {
        this.requestService = requestService;
    }

    public WebServiceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WebServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public StockVisualizationManager getStockVisualizationManager() {
        return stockVisualizationManager;
    }

    public void setStockVisualizationManager(StockVisualizationManager stockVisualizationManager) {
        this.stockVisualizationManager = stockVisualizationManager;
    }

    public RequestConverter getRequestConverter() {
        return requestConverter;
    }

    public void setRequestConverter(RequestConverter requestConverter) {
        this.requestConverter = requestConverter;
    }

    public ResponseConverter getResponseConverter() {
        return responseConverter;
    }

    public void setResponseConverter(ResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
    }

    public ClientLockManager getLockManager() {
        return lockManager;
    }

    public void setLockManager(ClientLockManager lockManager) {
        this.lockManager = lockManager;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    //endregion

	public RemoteStockCacheAccessor getRemoteStockCacheAccessor() {
		return remoteStockCacheAccessor;
	}

	public void setRemoteStockCacheAccessor(
			RemoteStockCacheAccessor remoteStockCacheAccessor) {
		this.remoteStockCacheAccessor = remoteStockCacheAccessor;
	}

}
