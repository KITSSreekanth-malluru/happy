package com.castorama.servlet;

import java.io.IOException;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.constantes.CastoConstantes;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extends PayboxServlet to serve paybox log request (server to server).
 * It could be called before PayboxReplyServlet while return to commerce
 * @see com/castorama/payment/PayboxParametersConfiguration.testMode
 * 
 * @author  Vasili Ivus
 */
public class PayboxLogServlet extends PayboxServlet {
    
    /** ORDER_ID_MESS */
    protected static final String ORDER_ID_MESS = "orderId = ";
    
    /** repository property */
    private Repository mRepository;

    /** orderRepository property */
    private Repository mOrderRepository;

    /** fieldsMap property */
    private Properties mFieldsMap;

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        this.mRepository = pRepository;
    }

    /**
     * Returns orderRepository property.
     *
     * @return orderRepository property
     */
    public Repository getOrderRepository() {
        return mOrderRepository;
    }

    /**
     * Sets the value of the orderRepository property.
     *
     * @param pOrderRepository parameter to set.
     */
    public void setOrderRepository(Repository pOrderRepository) {
        this.mOrderRepository = pOrderRepository;
    }

    /**
     * Returns fieldsMap property.
     *
     * @return fieldsMap property
     */
    public Properties getFieldsMap() {
        return mFieldsMap;
    }

    /**
     * Sets the value of the fieldsMap property.
     *
     * @param pFieldsMap parameter to set.;
     */
    public void setFieldsMap(Properties pFieldsMap) {
        this.mFieldsMap = pFieldsMap;
    }

    /**
     * Here is the place to do something before log writting in log repository. 
     */
    protected boolean preWriteReport(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        return false;
    }

    /**
     * Here is the place to do something after log writting in log repository. 
     */
    protected boolean postWriteReport(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        return false;
    }

    /**
     * Here is the place to do something after log writting in log repository. 
     */
    protected boolean postServiceAction(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
        return false;
    }

    /**
     * Extends PayboxServlet serviceAction to write in log repository. 
     */
    protected void serviceAction(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                          throws IOException, ServletException {

        if ( isLoggingInfo() ) {
            logInfo("serviceAction(request) - start: " + request.getQueryString());
        }
        
        CastOrderManager com = getOrderManager();
        
        if ( isLoggingInfo() ) {
            logInfo("serviceAction(request) - OrderManager is set: " + com);
        }
        
        if ( null != com ) {
            boolean postWriteReportSuccess = false;
            String queryString = request.getQueryString();
            Map<String, String> parameters = parseQueryString(queryString);
            String order_id = getParameterValue(parameters, CastoConstantes.PAYBOX_LOG_ORDER_ID);
            
            boolean correctIP = isCorrectIP(request);
            boolean correctSign = isCorrectSign(request);
             if ( isLoggingInfo() ) {
                logInfo("serviceAction(request) - isCorrectIP(): " + correctIP);
                logInfo("serviceAction(request) - isCorrectSign(): " + correctSign);
            }

               if (correctIP && correctSign) {
                preWriteReport(request, response);
                
                if ( isLoggingInfo() ) {
                    logInfo("serviceAction(request) - isCorrectIP() & isCorrectSign() passed");
                }
              
                boolean writeReport = false;
               
                
                try {
                	
                    writeReport = writeReport(request, queryString, parameters, false,com);
                } catch (Exception e) {
                    if ( isLoggingWarning() ) {
                        logWarning("serviceAction(request) - Cannot write transaction into CASTO_SIPS_LOG table.");
                    }

                    response.sendError(DynamoHttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Cannot write transaction into CASTO_SIPS_LOG table." );
                    return;
                }
                
                if ( writeReport ) {
                    boolean gotLock = false;
                
                    try {
                        gotLock = com.acquireGlobalLock(order_id, 5000);
                    
                        if ( isLoggingInfo() ) {
                            logInfo("serviceAction(request) - lock is acquired: " + gotLock + " - " + request.getQueryString());
                        }
                    
                        if (gotLock) {
                            postWriteReportSuccess = postWriteReport(request, response);
                        } else {
                            if (isLoggingError()) {
                                logError("Couldn't acquire lock for order with id = " + order_id);
                            }
                        }
                    } catch (Exception e) {
                        if (isLoggingError()) {
                            logError(ORDER_ID_MESS + order_id, e);
                        }
                    } finally {
                        if (gotLock) {
                            com.releaseLock(order_id);
                        }

                        if ( isLoggingInfo() ) {
                            logInfo("serviceAction(request) - lock is released: " + request.getQueryString());
                        }
                    }
                }
               } else {
                if ( isLoggingInfo() ) {
                    logInfo("serviceAction(request) - isCorrectIP() & isCorrectSign() didn't pass");
                }
                try {
                    writeReport(request, queryString, parameters, true,com);    
                   } catch (Exception e) {
                    if ( isLoggingWarning() ) {
                        logWarning("serviceAction(request) - Cannot write transaction into CASTO_SIPS_LOG table.");
                    }

                    response.sendError(DynamoHttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Cannot write transaction into CASTO_SIPS_LOG table." );
                    return;
                }
            }
            
        /*
            boolean gotLock = false;
            try {
                gotLock = com.acquireLock(order_id, 5000);
                
                if ( isLoggingInfo() ) {
                    logInfo("serviceAction(request) - lock is acquired: " + gotLock + " - " + request.getQueryString());
                }
                
                
                if (gotLock) {
                    if (isCorrectIP(request) && isCorrectSign(request)) {
                        if ( isLoggingInfo() ) {
                            logInfo("serviceAction(request) - isCorrectIP() & isCorrectSign() passed");
                        }

                        preWriteReport(request, response);
                        if ( writeReport(request, queryString, parameters, false) ) {
                            postWriteReportSuccess = postWriteReport(request, response);
                        }
                    } else {
                        if ( isLoggingInfo() ) {
                            logInfo("serviceAction(request) - isCorrectIP() & isCorrectSign() didn't pass");
                        }

                        writeReport(request, queryString, parameters, true);
                    }
                } else if (isLoggingError()) {
                    logError("Couldn't acquire lock for order with id = " + order_id);
                }
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(ORDER_ID_MESS + order_id, e);
                }
            } finally {
                if (gotLock) {
                    com.releaseLock(order_id);
                }

                if ( isLoggingInfo() ) {
                    logInfo("serviceAction(request) - lock is released: " + request.getQueryString());
                }
            }
            */
            if ( isLoggingInfo() ) {
                logInfo("serviceAction(request) - postWriteReportSuccess: " + postWriteReportSuccess + 
                            " - " + request.getQueryString());
            }

            if (postWriteReportSuccess) {
                postServiceAction(request, response);
            }
        }
        
        if ( isLoggingInfo() ) {
            logInfo("serviceAction(request) - finish: " + request.getQueryString());
        }
    }

    /**
     * Writes report in repository.
     *
     * @return true when request does not duplicated and false otherwise.
     */
    
    protected boolean writeReport(DynamoHttpServletRequest request, 
                                    String queryString, 
                                    Map<String, String> parameters,
                                    boolean isNotSigned,CastOrderManager com) throws Exception {
        boolean result = false;
        String order_id = getParameterValue(parameters, CastoConstantes.PAYBOX_LOG_ORDER_ID);
        if ( isLoggingInfo() ) {
            logInfo("writeReport(request) - start: " + queryString);
        }
        
        try {
            String request_url = request.getRequestURL() + "?" + queryString;
            String index = request.getParameter(CastoConstantes.PAYBOX_LOG_INDEX);
            
            String errorCode = null;
            result = checkUnique(parameters, request_url);
            if ( result ) {
                errorCode = isNotSigned ? CastoConstantes.LOGSIPS_ERROR : null;
            } else {
                errorCode = CastoConstantes.LOGSIPS_DUPLICATE;
            }
            //Call check in the amount of the order (Payment issue)
            
            
            if ( isLoggingInfo() ) {
                logInfo("writeReport(request) - request_url: " + request_url);
                logInfo("writeReport(request) - index: " + index);
                logInfo("writeReport(request) - errorCode: " + errorCode);
            }
         
            writeReport(parameters, request_url, index, errorCode);
          
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(ORDER_ID_MESS+order_id,e);
            }
            
            throw e; 
        }

        if ( isLoggingInfo() ) {
            logInfo("writeReport(request) - finish: " + result + " - " + queryString);
        }
        
        return result;
    }
    
    /**
     * Writes report in repository.
     *
     * @return true when request does not duplicated and false otherwise.
     * @throws RepositoryException 
     */
    private void writeReport(Map<String, String> parameters, String request_url, String index, String errorCode) throws RepositoryException {

        if ( isLoggingInfo() ) {
            logInfo("writeReport(" + request_url + ") - start");
        }
        
        MutableRepository r = (MutableRepository) getRepository();
        MutableRepositoryItem ri = r.createItem(CastoConstantes.LOGSIPS_ITEM_NAME);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_PAYMENT_TIME, now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE));
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_PAYMENT_DATE,
                    now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" +
                    now.get(Calendar.DAY_OF_MONTH));
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_DATE_TRANS_EXPIRE,
                    now.get(Calendar.YEAR) + "" + (now.get(Calendar.MONTH) + 1) + "" +
                    now.get(Calendar.DAY_OF_MONTH));
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_DATE_DEVISE, CastoConstantes.LOGSIPS_DEFAULT_CURRANCY_CODE);
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_PAYMENT_SYSTEM, CastoConstantes.LOGSIPS_PAYBOX);
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_REQUEST_URL, request_url);
        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_TRANSACTION_INDEX, index);
    
        Iterator<String> keys = parameters.keySet().iterator();
        while ( keys.hasNext() ) {
            String name = keys.next();
            String key = getFieldsMap().getProperty(name);
            if ( (null != key) && (0 < key.trim().length()) ) {
                String value = parameters.get(name);
                if ( CastoConstantes.PAYBOX_LOG_AMOUNT.equals(key) ) {
                    try {
                        ri.setPropertyValue(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        if (isLoggingError()) {
                            logError(e);
                        }
                    }
                } else if ( CastoConstantes.PAYBOX_LOG_ORDER_ID.equals(key) ) {
                    ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_ORDER_ID, value);
                    RepositoryItem order = null;
                    try {
                        order = getOrderRepository().getItem(value, CastoConstantes.ORDER_ITEM_NAME);
                    } catch (RepositoryException e) {
                        if (isLoggingError()) {
                            logError(ORDER_ID_MESS+value,e);
                        }
                    }
                    if (null != order) {
                        ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_PROFILE_ID, order.getPropertyValue(CastoConstantes.ORDER_PROPERTY_PROFILE_ID));
                    }
                } else {
                    ri.setPropertyValue(key, value);
                }
            }
        }
        if ( null != errorCode ) {
            ri.setPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_RESPONSE_CODE, errorCode);
        }
        r.addItem(ri);
        
        if ( isLoggingInfo() ) {
            logInfo("writeReport(" + request_url + ") - finish");
        }
    }
    
    /**
     * Parse request query string.
     */
    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> result = new TreeMap<String, String>();
        String[] couples = queryString.split("&");
        if ( (null != couples) && (0 < couples.length) ) {
            for (int i = 0; i < couples.length; i++) {
                if ((null != couples[i]) && (0 < couples[i].trim().length())) {
                    String[] couple = couples[i].split("=");
                    if ((null != couple) && (0 < couple.length)) {
                        String key = couple[0];
                        String value = (1 < couple.length) ? couple[1] : "";
                        result.put(key, value);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Gets parameter value.
     * 
     * Name=Name_In_Query_String -> ...&Name_In_Query_String=Value&... -> Value 
     * 
     */
    private String getParameterValue(Map<String, String> parameters, String name) {
        String result = null;
        if ( null != parameters ) {
            String parameter = getKeyOfValue(getFieldsMap(), name);
            if ( null != parameter ) {
                result = parameters.get(parameter);
            }
        }
        return result;
    }
    
    /**
     * Gets key of properties value.
     */
    private String getKeyOfValue(Properties properties, String value) {
        String result = null;
        if ( null != value && null != properties ) {
            Iterator keys = properties.keySet().iterator();
            while ( keys.hasNext() ) {
                String key = (String) keys.next();
                String val = properties.getProperty(key);
                if ( value.equals(val)) {
                    result = key;
                    break;
                }
            }
        }
        return result;
    }
    
    /**
     * Checks when request unique.
     */
    private boolean checkUnique(Map<String, String> parameters, String request_url) throws RepositoryException {
        boolean result = true;

        if ( isLoggingInfo() ) {
            logInfo("checkUnique(" + request_url + ") - start");
        }

        Repository repository = getRepository();
        RepositoryView logsipsView = repository.getView(CastoConstantes.LOGSIPS_ITEM_NAME);
        QueryBuilder queryBuilder = logsipsView.getQueryBuilder();
        QueryExpression propertyQuery = queryBuilder.createPropertyQueryExpression(CastoConstantes.LOGSIPS_PROPERTY_ORDER_ID);
        QueryExpression valueQuery = queryBuilder.createConstantQueryExpression(getParameterValue(parameters, CastoConstantes.PAYBOX_LOG_ORDER_ID));
        Query query = queryBuilder.createComparisonQuery(propertyQuery, valueQuery, QueryBuilder.EQUALS);
        RepositoryItem[] logsipsItems = logsipsView.executeQuery(query);
        if ( null != logsipsItems && 0 < logsipsItems.length ) {
            for (RepositoryItem logsipsItem : logsipsItems) {
                String request_url_value = (String) logsipsItem.getPropertyValue(CastoConstantes.LOGSIPS_PROPERTY_REQUEST_URL);
                if ( request_url.equals(request_url_value) ) {
                    result = false;
                    break;
                }
            }
        }

        if ( isLoggingInfo() ) {
            logInfo("checkUnique(" + request_url + ") - finish: " + result);
        }

        return result;
    }
}
