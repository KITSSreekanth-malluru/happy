package com.castorama.integration.backoffice.exp;

import atg.adapter.gsa.GSARepository;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import com.castorama.constantes.CastoConstantesOrders;
import com.castorama.payment.PayboxParametersConfiguration;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * User: EPAM Systems
 * Date: 12/6/13
 * Time: 4:53 PM
 */
public class PayboxDirectPlusManager extends GenericService {

    //region Statics
    /*Repository item descriptors*/
    private static final String STORE_ITEM_DESCRIPTOR = "magasin";
    private static final String ORDER_ITEM_DESCRIPTOR = "order";
    private static final String LOG_SIPS_ITEM_DESCRIPTOR = "logsips";
    /*Repository item names*/
    private static final String LOG_SIPS_TRANSACTION_ID_PROPERTY_MANE = "transaction_id";
    private static final String PBX_SITE_PROPERTY_MANE = "pbxSite";
    private static final String PBX_RANG_PROPERTY_MANE = "pbxRang";
    private static final String PBX_DIRECT_PLUS_KEY_PROPERTY_MANE = "pbxDirectPlusKey";
    private static final String LOG_SIPS_RESPONSE_CODE_PROPERTY_MANE = "response_code";
    /*RQL queries*/
    private static final String RQL_STORE = "id = ?0";
    private static final String RQL_ORDER = "id=?0 RANGE 0 + 10";
    private static final String RQL_LOG_SIPS = "order_id = ?0";
    /*Success transaction code*/
    private static final String RESPONSE_VALID_CODE = "00000";
    private static final String LOG_SIPS_VALID_CODE = "00000";
    private static final String DATEQ_FORMAT = "ddMMyyyy";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DATEQ_FORMAT);
    /*Request template for Paybox Direct Plus*/
    private static final String REQUEST_TEMPLATE = "VERSION={0}&TYPE={1}&SITE={2}&RANG={3}&CLE={4}&NUMQUESTION={5}&NUMTRANS={6}&DATEQ={7}";
    private static final String SQL_SELECT_QUESTION_NUMBER = "SELECT NUMQUESTION_DATE, NUMQUESTION FROM CASTO_PAYBOX_NUMQUESTION";
    private static final String SQL_UPDATE_QUESTION_NUMBER = "UPDATE CASTO_PAYBOX_NUMQUESTION SET NUMQUESTION_DATE = ?, NUMQUESTION = ?";
    private static final String CODE_RESPONSE = "CODEREPONSE";
    private static final HttpClient httpClient = HttpClients.createDefault();
    private static final String STRING_ENCODING = "UTF-8";
    private static final String WEB_STORE_ID="999";
    //endregion

    //region Dependensies
    /* Paybox Direct Plus URL For preproduction & tests environnements*/
    private String payboxDirectPlusPreprodURL;
    /*Paybox Direct Plus URLs for production environnement*/
    private String payboxDirectPlusMainProdURL;
    private String payboxDirectPlusBackupProdURL;
    /*Enable/Disable Paybox Direct Plus*/
    private boolean payboxDirectPlusIsActive;
    /*Enable/Disable test mode for Paybox Direct Plus*/
    private boolean payboxDirectPlusTestMode;
    /*The Paybox direct version*/
    private String payboxDirectPlusVersion;
    /*Transaction type*/
    private String payboxDirectPlusTransactionType;
    private Repository storeRepository;
    private Repository ordersRepository;
    private Repository logSipsRepository;
    /*dataSource property*/
    private DataSource dataSource;
    private PayboxParametersConfiguration payboxConfiguration;
    //endregion

    public RepositoryItem getLocalStoreItem(String castoramaStoreId) throws RepositoryException {
        Repository storeRep = getStoreRepository();
        ((GSARepository) storeRep).invalidateCaches();
        RepositoryView repView;
        repView = storeRep.getView(STORE_ITEM_DESCRIPTOR);
        RqlStatement storesRQL = RqlStatement.parseRqlStatement(RQL_STORE);
        RepositoryItem[] stores = storesRQL.executeQuery(repView, new Object[]{castoramaStoreId});
        if (stores != null) {
            return stores[0];
        }
        return null;
    }

    public RepositoryItem getOrderItem(String orderId) throws RepositoryException {
        Repository storeRep = getStoreRepository();
        ((GSARepository) storeRep).invalidateCaches();
        RepositoryView view = ordersRepository.getView(ORDER_ITEM_DESCRIPTOR);
        RqlStatement statement = RqlStatement.parseRqlStatement(RQL_ORDER);
        RepositoryItem[] items = statement.executeQuery(view, new Object[]{orderId});
        if (items != null) {
            return items[0];
        }
        return null;
    }

    public RepositoryItem[] getLogSipsItemsByOrderId(String orderId) throws RepositoryException {
        Repository storeRep = getStoreRepository();
        ((GSARepository) storeRep).invalidateCaches();
        RepositoryView view = logSipsRepository.getView(LOG_SIPS_ITEM_DESCRIPTOR);
        RqlStatement statement = RqlStatement.parseRqlStatement(RQL_LOG_SIPS);
        return statement.executeQuery(view, new Object[]{orderId});
    }

    public boolean isOrderTransactionsValid(String orderId) {
        try {
            RepositoryItem order = getOrderItem(orderId);
            String castoramaStoreId = (String) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_MAGASIN_ID);
            String deliveryType = (String) order.getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_DELIVERY_TYPE);
            RepositoryItem[] orderLogSipsRecords = getLogSipsItemsByOrderId(orderId);
            String site, rang, cle;

            if (CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT.equals(deliveryType)) {
                RepositoryItem store = getLocalStoreItem(castoramaStoreId);
                site = (String) store.getPropertyValue(PBX_SITE_PROPERTY_MANE);
                rang = (String) store.getPropertyValue(PBX_RANG_PROPERTY_MANE);
                cle = (String) store.getPropertyValue(PBX_DIRECT_PLUS_KEY_PROPERTY_MANE);
            } else {
                site = payboxConfiguration.getPbx_site();
                rang = payboxConfiguration.getPbx_rang();
                cle = payboxConfiguration.getDirectPlusKey();
            }
            if (cle != null) {
                if (rang != null && site != null) {
                    for (RepositoryItem orderLogSipsRecord : orderLogSipsRecords) {
                        String transactionId = (String) orderLogSipsRecord.getPropertyValue(LOG_SIPS_TRANSACTION_ID_PROPERTY_MANE);
                        String logSipsResponseCode = (String) orderLogSipsRecord.getPropertyValue(LOG_SIPS_RESPONSE_CODE_PROPERTY_MANE);
                        if (!LOG_SIPS_VALID_CODE.equals(logSipsResponseCode)) {
                            return false;
                        }
                        Object arguments[] = {
                                payboxDirectPlusVersion, payboxDirectPlusTransactionType,
                                site, rang, cle, getPayboxQuestionNumberAsString(), transactionId,
                                getCurrentDateAsString()
                        };
                        String request = MessageFormat.format(REQUEST_TEMPLATE, arguments);
                        String response = sendRequestToPaymentSystem(request);
                        boolean isResponseValid = checkResponse(response);
                        if (!isResponseValid) {
                            if (isLoggingError()) {
                                vlogError("Paybox transaction validation failed for request: {0}. Paybox server response: {1}",
                                        request, response);
                            }
                            return false;
                        } else {
                            if (isLoggingDebug()) {
                                vlogDebug("Paybox transaction validation success for request: {0}. Paybox server response: {1}",
                                        request, response);
                            }
                        }
                    }
                } else {
                    if (isLoggingError()) {
                        vlogError("Some parameters are not initialized: pbxSite = {0}, pbxRang = {1}." +
                                " Returning failed validation status for order with id = {2}.", site, rang, orderId);
                    }
                    return false;
                }
            } else {
                if (isLoggingDebug()) {
                    vlogDebug("directPlusKey is null. Returning success validation status for order with id = {0}.", orderId);
                }
            }
        } catch (RepositoryException repositoryException) {
            if (isLoggingError()) {
                vlogError(repositoryException, "Repository exception during transaction check for order with id {0}", orderId);
            }
            return false;
        }
        return true;
    }

    private String sendRequestToPaymentSystem(String requestParametersString) {
        if (!isPayboxDirectPlusTestMode()) {
            try {
                logUrlRequestInfo(payboxDirectPlusMainProdURL);
                return doPost(payboxDirectPlusMainProdURL, requestParametersString);
            } catch (Exception mainException) {
                if (isLoggingError()) {
                    vlogError(mainException, "Unable to send request to main server {0}. Trying to send request to backup server.",
                            payboxDirectPlusMainProdURL);
                }
                try {
                    logUrlRequestInfo(payboxDirectPlusBackupProdURL);
                    return doPost(payboxDirectPlusBackupProdURL, requestParametersString);
                } catch (Exception backupException) {
                    if (isLoggingError()) {
                        vlogError(backupException, "Unable to send request to backup server {0}.", payboxDirectPlusBackupProdURL);
                    }
                }
            }
        } else {
            try {
                logUrlRequestInfo(payboxDirectPlusPreprodURL);
                return doPost(payboxDirectPlusPreprodURL, requestParametersString);
            } catch (Exception exception) {
                if (isLoggingError()) {
                    vlogError(exception, "Unable to send request to test server {0}.", payboxDirectPlusPreprodURL);
                }
            }
        }
        return null;
    }

    public String doPost(String urlString, String data) throws Exception {
        StringEntity stringentity = new StringEntity(data, STRING_ENCODING);

        HttpPost httpPost = new HttpPost(urlString);
        //set connection keep-alive for better request performance
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.setEntity(stringentity);
        logRequestHeaders(httpPost);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        logResponseHeaders(httpResponse);
        HttpEntity entity = httpResponse.getEntity();

        return EntityUtils.toString(entity);
    }

    private void logRequestHeaders(HttpRequestBase request) {
        if (isLoggingDebug()) {
            Header[] headers = request.getAllHeaders();
            for (Header header : headers) {
                vlogDebug("[Request header]: [{0}] - [{1}]", header.getName(), header.getValue());
            }
        }
    }

    private void logResponseHeaders(HttpMessage requestBase) {
        if (isLoggingDebug()) {
            Header[] headers = requestBase.getAllHeaders();
            for (Header header : headers) {
                vlogDebug("[Response header]: [{0}] - [{1}]", header.getName(), header.getValue());
            }
        }
    }

    private boolean checkResponse(String response) {
        if (response != null) {
            StringTokenizer tokenizer = new StringTokenizer(response, "=&");
            while (tokenizer.hasMoreTokens()) {
                if (CODE_RESPONSE.equals(tokenizer.nextToken())) {
                    return RESPONSE_VALID_CODE.equals(tokenizer.nextToken());
                }
            }
        }
        return false;
    }

    private int getPayboxQuestionNumber() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        java.sql.Date questionDate;
        java.sql.Date dateToSet;
        int questionNumber;
        int questionNumberToSet = 1;
        ResultSet resultSet = null;
        try {
            connection = getDataSource().getConnection();
            preparedStatement = connection.prepareStatement(SQL_SELECT_QUESTION_NUMBER);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                questionDate = resultSet.getDate(1);
                questionNumber = resultSet.getInt(2);
                if (questionDate.before(getCurrentDateWithoutTime())) {
                    questionNumberToSet = 1;
                } else {
                    questionNumberToSet = questionNumber + 1;
                }
            } else {
                if (isLoggingError()) {
                    logError("There is no data in table CASTO_PAYBOX_NUMQUESTION for NUMQUESTION generation.");
                }
            }
            dateToSet = new java.sql.Date(getCurrentDateWithoutTime().getTime());
            preparedStatement = connection.prepareStatement(SQL_UPDATE_QUESTION_NUMBER);
            preparedStatement.setDate(1, dateToSet);
            preparedStatement.setInt(2, questionNumberToSet);
            preparedStatement.execute();
            return questionNumberToSet;
        } catch (SQLException sqlException) {
            if (isLoggingError()) {
                logError("SQLException during executing query.", sqlException);
            }
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException sqlException) {
                    if (isLoggingError()) {
                        logError("Unable to close result set.", sqlException);
                    }
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlException) {
                    if (isLoggingError()) {
                        logError("Unable to close prepared statement.", sqlException);
                    }
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException sqlException) {
                    if (isLoggingError()) {
                        logError("Unable to close connection set.", sqlException);
                    }
                }
            }
        }
        return 1;
    }

    private String getPayboxQuestionNumberAsString() {
        String payboxQuestionNumber = Integer.toString(getPayboxQuestionNumber());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10 - payboxQuestionNumber.length(); i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.append(payboxQuestionNumber).toString();
    }

    private Date getCurrentDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    private void logUrlRequestInfo(String url) {
        if (isLoggingDebug()) {
            vlogDebug("Sending Paybox direct plus request to: {0}", url);
        }
    }

    private String getCurrentDateAsString() {
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    public Repository getLogSipsRepository() {
        return logSipsRepository;
    }

    public void setLogSipsRepository(Repository logSipsRepository) {
        this.logSipsRepository = logSipsRepository;
    }

    public Repository getOrdersRepository() {
        return ordersRepository;
    }

    public void setOrdersRepository(Repository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public Repository getStoreRepository() {
        return storeRepository;
    }

    public void setStoreRepository(Repository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public String getPayboxDirectPlusTransactionType() {
        return payboxDirectPlusTransactionType;
    }

    public void setPayboxDirectPlusTransactionType(String payboxDirectPlusTransactionType) {
        this.payboxDirectPlusTransactionType = payboxDirectPlusTransactionType;
    }

    public String getPayboxDirectPlusVersion() {
        return payboxDirectPlusVersion;
    }

    public void setPayboxDirectPlusVersion(String payboxDirectPlusVersion) {
        this.payboxDirectPlusVersion = payboxDirectPlusVersion;
    }

    public boolean isPayboxDirectPlusTestMode() {
        return payboxDirectPlusTestMode;
    }

    public void setPayboxDirectPlusTestMode(boolean payboxDirectPlusTestMode) {
        this.payboxDirectPlusTestMode = payboxDirectPlusTestMode;
    }

    public String getPayboxDirectPlusBackupProdURL() {
        return payboxDirectPlusBackupProdURL;
    }

    public void setPayboxDirectPlusBackupProdURL(String payboxDirectPlusBackupProdURL) {
        this.payboxDirectPlusBackupProdURL = payboxDirectPlusBackupProdURL;
    }

    public String getPayboxDirectPlusMainProdURL() {
        return payboxDirectPlusMainProdURL;
    }

    public void setPayboxDirectPlusMainProdURL(String payboxDirectPlusMainProdURL) {
        this.payboxDirectPlusMainProdURL = payboxDirectPlusMainProdURL;
    }

    public String getPayboxDirectPlusPreprodURL() {
        return payboxDirectPlusPreprodURL;
    }

    public void setPayboxDirectPlusPreprodURL(String payboxDirectPlusPreprodURL) {
        this.payboxDirectPlusPreprodURL = payboxDirectPlusPreprodURL;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PayboxParametersConfiguration getPayboxConfiguration() {
        return payboxConfiguration;
    }

    public void setPayboxConfiguration(PayboxParametersConfiguration payboxConfiguration) {
        this.payboxConfiguration = payboxConfiguration;
    }

    public boolean isPayboxDirectPlusIsActive() {
        return payboxDirectPlusIsActive;
    }

    public void setPayboxDirectPlusIsActive(boolean payboxDirectPlusIsActive) {
        this.payboxDirectPlusIsActive = payboxDirectPlusIsActive;
    }
}