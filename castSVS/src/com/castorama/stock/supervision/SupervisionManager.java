package com.castorama.stock.supervision;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public class SupervisionManager implements ServletContextAware {

    private static final String JNDI_NAME_PREFIX = "java:/";

    /* ATTRIBUTES */
    private static final String ATTRIB_SUPERVISION_MANAGER = "supervisionManager";

    private static final String ATTRIB_MONTHES = "monthes";
    private static final String ATTRIB_MONTH_TO_VIEW = "monthToView";
    private static final String ATTRIB_DAY_TO_VIEW = "dayToView";
    private static final String ATTRIB_SUPERVISION_RECORDS = "supervisionRecords";
    private static final String ATTRIB_SYNC_JOURNAL_RECORDS = "syncJournalRecords";
    private static final String ATTRIB_STOCK_AVERAGE_OK = "averageStockOK";
    private static final String ATTRIB_STOCK_AVERAGE_KO = "averageStockKO";
    private static final String ATTRIB_PRICE_AVERAGE_OK = "averagePriceOK";
    private static final String ATTRIB_PRICE_AVERAGE_KO = "averagePriceKO";

    /* PARAMS */
    private static final String PARAM_CHOSEN_MONTH = "chosenMonth";
    private static final String PARAM_CHOSEN_DAY = "chosenDay";

    /* SQL QUERIES */

    private static final String SYNC_JOURNAL_LIST_BY_DAY_FOR_PRICE = "SELECT STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS "
            + "FROM MS_LOCAL_PRICES_SYNC_JOURNAL WHERE TRUNC(LAST_UPDATE) = TRUNC(?) ORDER BY STORE_ID";

    private static final String SYNC_JOURNAL_LIST_BY_DAY_FOR_STOCK = "SELECT STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS "
            + "FROM MS_INVENTORY_SYNC_JOURNAL WHERE TRUNC(LAST_UPDATE) = TRUNC(?) ORDER BY STORE_ID";
    
    private static final String SYNC_JOURNAL_LIST_BY_DAY_FOR_ORDER = "SELECT STORE_ID, QUANTITY, LAST_UPDATE, STATUS "
            + "FROM MS_ORDERS_SYNC_JOURNAL WHERE TRUNC(LAST_UPDATE) = TRUNC(?) ORDER BY STORE_ID";
    
    private static final String SYNC_JOURNAL_LIST_BY_MONTH_FOR_PRICE = "SELECT STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS "
            + "FROM MS_LOCAL_PRICES_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'MM/YYYY') = TO_CHAR(?, 'MM/YYYY')";

    private static final String SYNC_JOURNAL_LIST_BY_MONTH_FOR_STOCK = "SELECT STORE_ID, ENTITY_NUMBER, FILE_SEQ, LAST_UPDATE, STATUS "
            + "FROM MS_INVENTORY_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'MM/YYYY') = TO_CHAR(?, 'MM/YYYY')";

    private static final String SYNC_JOURNAL_LIST_BY_MONTH_FOR_ORDER = "SELECT STORE_ID, QUANTITY, LAST_UPDATE, STATUS "
            + "FROM MS_ORDERS_SYNC_JOURNAL WHERE TO_CHAR(LAST_UPDATE, 'MM/YYYY') = TO_CHAR(?, 'MM/YYYY')";

    /* UPDATE STATUSES */
    private static final int STATUS_OK = 0;
    private static final int STATUS_KO = 1;

    /* logger property */
    private static final Log logger = LogFactory.getLog(SupervisionManager.class);

    @Autowired
    private HttpServletRequest request;

    private ServletContext mServletContext;

    /**
     * mDataSourceName property
     */
    private String mDataSourceName;

    /**
     * mDataSource property
     */
    private DataSource mDataSource;

    public void setServletContext(ServletContext pServletContext) {
        mServletContext = pServletContext;
    }

    public String getDataSourceName() {
        return mDataSourceName;
    }

    public void setDataSourceName(String pDataSourceName) {
        mDataSourceName = pDataSourceName;
    }

    private DataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource pDataSource) {
        mDataSource = pDataSource;
    }

    @PostConstruct
    public void postConstruct() {
        try {
            Context initialContext = new InitialContext();
            Context envContext = (Context) initialContext
                    .lookup(JNDI_NAME_PREFIX);
            mDataSource = (DataSource) envContext.lookup(mDataSourceName);

            mServletContext.setAttribute(ATTRIB_SUPERVISION_MANAGER, this);
        } catch (NamingException exception) {
            if (logger.isErrorEnabled()) {
                logger.error(exception);
            }
        }
    }

    public boolean getDayDataPrice() {
        Date dayToView = getDayToView();
        request.setAttribute(ATTRIB_DAY_TO_VIEW, dayToView);

        List<SyncJournalRecord> syncJournalRecords = getPriceSyncJournalRecords(SYNC_JOURNAL_LIST_BY_DAY_FOR_PRICE, dayToView);
        request.setAttribute(ATTRIB_SYNC_JOURNAL_RECORDS, syncJournalRecords);

        return true;
    }

    public boolean getDayDataStock() {
        Date dayToView = getDayToView();
        request.setAttribute(ATTRIB_DAY_TO_VIEW, dayToView);

        List<SyncJournalRecord> syncJournalRecords = getStockSyncJournalRecords(SYNC_JOURNAL_LIST_BY_DAY_FOR_STOCK, dayToView);
        request.setAttribute(ATTRIB_SYNC_JOURNAL_RECORDS, syncJournalRecords);

        return true;
    }

    public boolean getDayDataOrder() {
        Date dayToView = getDayToView();
        request.setAttribute(ATTRIB_DAY_TO_VIEW, dayToView);
        
        List<SyncJournalRecord> syncJournalRecords = getOrderSyncJournalRecords(SYNC_JOURNAL_LIST_BY_DAY_FOR_ORDER, dayToView);
        request.setAttribute(ATTRIB_SYNC_JOURNAL_RECORDS, syncJournalRecords);
        
        return true;
    }

    public boolean getMonthData() {
        List<Date> monthes = getMonthes();
        request.setAttribute(ATTRIB_MONTHES, monthes);

        Date monthToView = getMonthToView();
        request.setAttribute(ATTRIB_MONTH_TO_VIEW, monthToView);

        List<SyncJournalRecord> syncJournalRecordsStock = getStockSyncJournalRecords(SYNC_JOURNAL_LIST_BY_MONTH_FOR_STOCK, monthToView);
        List<SyncJournalRecord> syncJournalRecordsPrice = getPriceSyncJournalRecords(SYNC_JOURNAL_LIST_BY_MONTH_FOR_PRICE, monthToView);
        List<SyncJournalRecord> syncJournalRecordsOrder = getOrderSyncJournalRecords(SYNC_JOURNAL_LIST_BY_MONTH_FOR_ORDER, monthToView);


        LinkedHashMap<String, SupervisionRecord> supervisionMap = new LinkedHashMap<String, SupervisionRecord>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar now = new GregorianCalendar();
        Calendar month = new GregorianCalendar();
        month.setTime(monthToView);
        int maxDaysNumber = month.getActualMaximum(Calendar.DAY_OF_MONTH);
        int daysNumber = maxDaysNumber;
        if ((month.get(Calendar.YEAR) == now.get(Calendar.YEAR)) &&
                (month.get(Calendar.MONTH) == now.get(Calendar.MONTH))) {
            daysNumber = now.get(Calendar.DAY_OF_MONTH);
        }

        for (int i = 1; i <= maxDaysNumber; i++) {
            month.set(Calendar.DATE, i);
            SupervisionRecord record = new SupervisionRecord();
            record.setDate(month.getTime());
            supervisionMap.put(sdf.format(month.getTime()), record);
        }

        for (SyncJournalRecord record : syncJournalRecordsOrder) {
            String dateString = sdf.format(record.getLastUpdate());
            int currentOrdersCount = supervisionMap.get(dateString).getOrderCount();
            int storeOrdersCount = record.getOrdersCount();
            supervisionMap.get(dateString).setOrderCount(currentOrdersCount + storeOrdersCount);
        }

        for (SyncJournalRecord record : syncJournalRecordsStock) {
            String dateString = sdf.format(record.getLastUpdate());
            int stockStatus = record.getStockStatus();
            if (stockStatus == STATUS_OK) {
                int value = supervisionMap.get(dateString).getStockOkCount();
                supervisionMap.get(dateString).setStockOkCount(value + 1);
            } else if (stockStatus == STATUS_KO) {
                int value = supervisionMap.get(dateString).getStockKoCount();
                supervisionMap.get(dateString).setStockKoCount(value + 1);
            }
        }

        for (SyncJournalRecord record : syncJournalRecordsPrice) {
            String dateString = sdf.format(record.getLastUpdate());
            int priceStatus = record.getPriceStatus();
            if (priceStatus == STATUS_OK) {
                int value = supervisionMap.get(dateString).getPriceOkCount();
                supervisionMap.get(dateString).setPriceOkCount(value + 1);
            } else if (priceStatus == STATUS_KO) {
                int value = supervisionMap.get(dateString).getPriceKoCount();
                supervisionMap.get(dateString).setPriceKoCount(value + 1);
            }
        }

        List<SupervisionRecord> supervisionRecords =
                new ArrayList<SupervisionRecord>(supervisionMap.values());

        int totalStockOK = 0;
        int totalStockKO = 0;
        int totalPriceOK = 0;
        int totalPriceKO = 0;
        for (SupervisionRecord record : supervisionRecords) {
            totalPriceOK += record.getPriceOkCount();
            totalPriceKO += record.getPriceKoCount();
            totalStockOK += record.getStockOkCount();
            totalStockKO += record.getStockKoCount();
        }

        int totalStore = totalStockKO + totalStockOK;
        int totalPrice = totalPriceKO + totalPriceOK;

        double averageStockOK = -1;
        double averageStockKO = -1;
        double averagePriceOK = -1;
        double averagePriceKO = -1;
        if (totalStore != 0) {
            averageStockOK = totalStockOK / (double) totalStore;
            averageStockKO = totalStockKO / (double) totalStore;
        }
        if (totalPrice != 0) {
            averagePriceOK = totalPriceOK / (double) totalPrice;
            averagePriceKO = totalPriceKO / (double) totalPrice;
        }
        request.setAttribute(ATTRIB_STOCK_AVERAGE_OK, averageStockOK);
        request.setAttribute(ATTRIB_STOCK_AVERAGE_KO, averageStockKO);
        request.setAttribute(ATTRIB_PRICE_AVERAGE_OK, averagePriceOK);
        request.setAttribute(ATTRIB_PRICE_AVERAGE_KO, averagePriceKO);

        for (int i = daysNumber; i < maxDaysNumber; i++) {
            supervisionRecords.get(i).setStockOkCount(-1);
            supervisionRecords.get(i).setStockKoCount(-1);
            supervisionRecords.get(i).setPriceOkCount(-1);
            supervisionRecords.get(i).setPriceKoCount(-1);
        }

        request.setAttribute(ATTRIB_SUPERVISION_RECORDS, supervisionRecords);

        return true;
    }

    private List<Date> getMonthes() {
        List<Date> monthes = new ArrayList<Date>();

        Calendar now = new GregorianCalendar();

        int currentYear = now.get(Calendar.YEAR);
        int currentMonthIndex = now.get(Calendar.MONTH);
        int monthesNumber = now.getActualMaximum(Calendar.MONTH);

        Calendar month = new GregorianCalendar();
        month.set(Calendar.DAY_OF_MONTH, 1);
        month.set(Calendar.YEAR, currentYear - 1);
        for (int i = currentMonthIndex + 1; i <= monthesNumber; i++) {
            month.set(Calendar.MONTH, i);
            monthes.add(month.getTime());
        }
        month.set(Calendar.YEAR, currentYear);
        for (int i = 0; i <= currentMonthIndex; i++) {
            month.set(Calendar.MONTH, i);
            monthes.add(month.getTime());
        }

        return monthes;
    }

    private Date getMonthToView() {
        String chosenMonth = request.getParameter(PARAM_CHOSEN_MONTH);
        Date chosenMonthDate = null;

        if (chosenMonth != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            try {
                chosenMonthDate = sdf.parse(chosenMonth);
            } catch (ParseException pe) {
                if (logger.isErrorEnabled()) {
                    logger.equals(pe);
                }
            }

            if (chosenMonthDate != null) {
                Calendar chosenMonthCalendar = new GregorianCalendar();
                chosenMonthCalendar.setTime(chosenMonthDate);
                boolean validChosenMonth = false;
                for (Date month : getMonthes()) {
                    Calendar cMonth = new GregorianCalendar();
                    cMonth.setTime(month);
                    if ((cMonth.get(Calendar.YEAR) == chosenMonthCalendar.get(Calendar.YEAR)) &&
                            (cMonth.get(Calendar.MONTH) == chosenMonthCalendar.get(Calendar.MONTH))) {
                        validChosenMonth = true;
                        break;
                    }
                }
                if (validChosenMonth == false) {
                    chosenMonthDate = new Date();
                }
            } else {
                chosenMonthDate = new Date();
            }
        } else {
            chosenMonthDate = new Date();
        }

        return chosenMonthDate;
    }

    private Date getDayToView() {
        String chosenDay = request.getParameter(PARAM_CHOSEN_DAY);
        Date chosenDayDate = null;

        if (chosenDay != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                chosenDayDate = sdf.parse(chosenDay);
            } catch (ParseException pe) {
                if (logger.isErrorEnabled()) {
                    logger.equals(pe);
                }
            }

            if (chosenDayDate != null) {
                Calendar chosenDayCalendar = new GregorianCalendar();
                chosenDayCalendar.setTime(chosenDayDate);
                boolean validChosenDay = false;
                for (Date month : getMonthes()) {
                    Calendar cMonth = new GregorianCalendar();
                    cMonth.setTime(month);
                    if ((cMonth.get(Calendar.YEAR) == chosenDayCalendar.get(Calendar.YEAR)) &&
                            (cMonth.get(Calendar.MONTH) == chosenDayCalendar.get(Calendar.MONTH))) {
                        validChosenDay = true;
                        break;
                    }
                }
                if (validChosenDay) {
                    if (chosenDayCalendar.after(new GregorianCalendar())) {
                        chosenDayDate = new Date();
                    }
                } else {
                    chosenDayDate = new Date();
                }
            } else {
                chosenDayDate = new Date();
            }
        } else {
            chosenDayDate = new Date();
        }

        return chosenDayDate;
    }

    private List<SyncJournalRecord> getPriceSyncJournalRecords(String query, Date periodToView) {
        List<SyncJournalRecord> records = null;
        Connection connection = null;
        PreparedStatement listStatement = null;
        ResultSet listSet = null;

        try {
            connection = mDataSource.getConnection();
            listStatement = connection.prepareStatement(query);
            listStatement.setTimestamp(1, new Timestamp(periodToView.getTime()));
            listSet = listStatement.executeQuery();

            records = new ArrayList<SyncJournalRecord>();
            while (listSet.next()) {
                SyncJournalRecord record = readPriceSyncJournalRecord(listSet);
                records.add(record);
            }
        } catch (SQLException exception) {
            if (logger.isErrorEnabled()) {
                logger.error(exception);
            }
        } finally {
            close(listSet);
            close(listStatement);
            close(connection);
        }

        return records;
    }

    private List<SyncJournalRecord> getOrderSyncJournalRecords(String query, Date periodToView) {
        List<SyncJournalRecord> records = null;
        Connection connection = null;
        PreparedStatement listStatement = null;
        ResultSet listSet = null;

        try {
            connection = mDataSource.getConnection();
            listStatement = connection.prepareStatement(query);
            listStatement.setTimestamp(1, new Timestamp(periodToView.getTime()));
            listSet = listStatement.executeQuery();

            records = new ArrayList<SyncJournalRecord>();
            while (listSet.next()) {
                SyncJournalRecord record = readOrderSyncJournalRecord(listSet);
                records.add(record);
            }
        } catch (SQLException exception) {
            if (logger.isErrorEnabled()) {
                logger.error(exception);
            }
        } finally {
            close(listSet);
            close(listStatement);
            close(connection);
        }

        return records;
    }

    private List<SyncJournalRecord> getStockSyncJournalRecords(String query, Date periodToView) {
        List<SyncJournalRecord> records = null;
        Connection connection = null;
        PreparedStatement listStatement = null;
        ResultSet listSet = null;

        try {
            connection = mDataSource.getConnection();
            listStatement = connection.prepareStatement(query);
            listStatement.setTimestamp(1, new Timestamp(periodToView.getTime()));
            listSet = listStatement.executeQuery();

            records = new ArrayList<SyncJournalRecord>();
            while (listSet.next()) {
                SyncJournalRecord record = readStockSyncJournalRecord(listSet);
                records.add(record);
            }
        } catch (SQLException exception) {
            if (logger.isErrorEnabled()) {
                logger.error(exception);
            }
        } finally {
            close(listSet);
            close(listStatement);
            close(connection);
        }

        return records;
    }

    private SyncJournalRecord readPriceSyncJournalRecord(ResultSet listSet)
            throws SQLException {
        SyncJournalRecord record = new SyncJournalRecord();
        record.setStoreId(listSet.getInt(1));
        record.setPriceEntityNumber(listSet.getInt(2));
        record.setPriceFileSequence(listSet.getInt(3));
        record.setLastUpdate(new Date(listSet.getTimestamp(4).getTime()));
        record.setPriceStatus(listSet.getInt(5));
        return record;
    }

    private SyncJournalRecord readOrderSyncJournalRecord(ResultSet listSet)
            throws SQLException {
        SyncJournalRecord record = new SyncJournalRecord();
        record.setStoreId(listSet.getInt(1));
        record.setOrdersCount(listSet.getInt(2));
        record.setLastUpdate(new Date(listSet.getTimestamp(3).getTime()));
        record.setOrderStatus(listSet.getInt(4));
        return record;
    }

    private SyncJournalRecord readStockSyncJournalRecord(ResultSet listSet)
            throws SQLException {
        SyncJournalRecord record = new SyncJournalRecord();
        record.setStoreId(listSet.getInt(1));
        record.setStockEntityNumber(listSet.getInt(2));
        record.setStockFileSequence(listSet.getInt(3));
        record.setLastUpdate(new Date(listSet.getTimestamp(4).getTime()));
        record.setStockStatus(listSet.getInt(5));
        return record;
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException exception) {
            }
        }
    }

    private void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
            }
        }
    }

    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
            }
        }
    }
}
