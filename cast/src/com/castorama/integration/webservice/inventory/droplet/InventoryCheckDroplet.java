package com.castorama.integration.webservice.inventory.droplet;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.integration.webservice.LogService;
import com.castorama.integration.webservice.Processor;
import com.castorama.integration.webservice.inventory.model.*;
import com.castorama.utils.ContextState;
import com.castorama.utils.ContextTools;

import javax.servlet.ServletException;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author EPAM team
 */
public class InventoryCheckDroplet extends DynamoServlet {

    //region Statics
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    private static final String ORDER_ID_PARAMETER_NAME = "orderId";
    private static final String STORE_ID_PARAMETER_NAME = "storeId";
    private static final String TIME_PARAMETER_NAME = "time";
    private static final String PRODUCTS_PARAMETER_NAME = "products";
    private static final String STATUS_PARAMETER_NAME = "status";
    private static final String UNRESOLVED_ORDER_ID = "unresolved";
    private static final String ERROR_MESSAGE = "Error in web service request preparation stage.";
    private static final String ORDER_MISMATCH_ERROR_MESSAGE = "Order id from request does not match order id from current local order.";
    //endregion

    //region Dependencies
    private Processor<ProcessResponse, RequestParametersWrapper> inventoryProcessor;
    private ContextTools contextTools;
    private LogService<LogMessage> logService;
    //endregion

    @Override
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
            throws ServletException, IOException {
        ProcessResponse processResponse;
        try {
            ContextState contextState = contextTools.getContextState(request, response);
            CastOrderImpl currentLocal = contextState.getShoppingCart().getCurrentLocal();

            String orderId = request.getParameter(ORDER_ID_PARAMETER_NAME);
            String storeId = request.getParameter(STORE_ID_PARAMETER_NAME);
            String time = request.getParameter(TIME_PARAMETER_NAME);
            String[] productsIds = request.getParameter(PRODUCTS_PARAMETER_NAME).split(",");

            Date requestTime;
            try {
                requestTime = new SimpleDateFormat(WebServiceConstants.DATE_FORMAT).parse(time);
            } catch (ParseException e) {
                requestTime = Calendar.getInstance().getTime();
            }

            Set<Integer> codeArticles = new HashSet<Integer>();
            for (String productId : productsIds) {
                codeArticles.add(Integer.parseInt(productId));
            }

            RequestParametersWrapper parametersWrapper = new RequestParametersWrapper(
                    orderId, Integer.parseInt(storeId), requestTime, codeArticles, contextState
            );

            if (orderId.equals(currentLocal.getId())) {
                if (isLoggingDebug()) {
                    logDebug("Web service call will be performed for order:" + currentLocal.toString());
                }

                processResponse = inventoryProcessor.process(parametersWrapper);
            } else {
                if (isLoggingError()) {
                    logError(ORDER_MISMATCH_ERROR_MESSAGE);
                }

                processResponse = new ProcessResponse(ProcessStatus.CALL_PROCESS_ERROR, ORDER_MISMATCH_ERROR_MESSAGE);
            }

            LogMessage logMessage = createLogMessage(parametersWrapper, currentLocal, processResponse);
            logService.log(logMessage);

        } catch (Throwable throwable) {
            if (isLoggingError()) {
                logError(ERROR_MESSAGE, throwable);
            }
            String message = MessageFormat.format(
                    "Error in web service request preparation stage: {0}. Please, see application log for more information",
                    throwable.getMessage()
            );
            processResponse = new ProcessResponse(ProcessStatus.CALL_PROCESS_ERROR, message);
        }

        request.setParameter(STATUS_PARAMETER_NAME, processResponse);
        request.serviceLocalParameter(OUTPUT, request, response);
    }

    private LogMessage createLogMessage(RequestParametersWrapper parametersWrapper, CastOrderImpl currentLocal, ProcessResponse processResponse) {
        String localStoreId = Integer.toString(parametersWrapper.getStoreId());
        Date requestDate = parametersWrapper.getTime();
        Set<Integer> codeArticles = parametersWrapper.getCodeArticles();
        String orderId = parametersWrapper.getOrderId();
        String orderIdForLog = (orderId.equals(currentLocal.getId()))
                ? orderId
                : UNRESOLVED_ORDER_ID;
        return new LogMessage(requestDate, orderIdForLog, localStoreId, codeArticles, processResponse.getProcessStatus().toString());
    }

    //region Getters/Setters
    public ContextTools getContextTools() {
        return contextTools;
    }

    public void setContextTools(ContextTools contextTools) {
        this.contextTools = contextTools;
    }

    public Processor<ProcessResponse, RequestParametersWrapper> getInventoryProcessor() {
        return inventoryProcessor;
    }

    public void setInventoryProcessor(Processor<ProcessResponse, RequestParametersWrapper> inventoryProcessor) {
        this.inventoryProcessor = inventoryProcessor;
    }

    public LogService<LogMessage> getLogService() {
        return logService;
    }

    public void setLogService(LogService<LogMessage> logService) {
        this.logService = logService;
    }
    //endregion
}

