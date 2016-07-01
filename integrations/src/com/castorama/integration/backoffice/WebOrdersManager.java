package com.castorama.integration.backoffice;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;

import atg.core.util.StringUtils;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import com.castorama.commerce.order.CastOrderManager;

/**
 * Extends GenericService to manage WebOrders.
 *
 * @author Vasili_Ivus
 */
public class WebOrdersManager extends GenericService {
    /** DATE_FORMAT constant. */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /** countOrdersByDate constant. */
    private static String countOrdersByDate = "state EQUALS IGNORECASE ?0 and lastModifiedDate < ?1";

    /** getOrdersByDate constant. */
    private static String getOrdersByDate = "state EQUALS IGNORECASE ?0 and lastModifiedDate < ?1 RANGE ?2+?3";

    /** STATE_PENDING_PAYBOX constant. */
    private static final String STATE_PENDING_PAYBOX = "PENDING_PAYBOX";

    /** STATE_PENDING_REMOVE constant. */
    private static final String STATE_PENDING_REMOVE = "PENDING_REMOVE";

    /** PORTION_SIZE constant. */
    private static final Integer PORTION_SIZE = 1000;

    /** DEFAULT_DELTA constant. */
    private static final Integer DEFAULT_DELTA = 14;

    /** ordersRepository property. */
    private Repository mOrdersRepository;

    /** sipsRepository property. */
    private Repository mSipsRepository;

    /** orderManager property. */
    private CastOrderManager mOrderManager;

    /** Transaction manager property. */
    private TransactionManager transactionManager;

    /** endDate property. */
    private String mEndDate;

    /** isStartManager property. */
    private boolean isStartManager = false;

    /** isStartManagerPendingRemove property. */
    private boolean isStartManagerPendingRemove = false;

    /** countRemoveOrder property. */
    private Integer countRemoveOrder = 0;

    /** endDateCalc property. */
    private String mEndDateCalc;

    /** delta property. */
    private Integer mDelta;

    /**
     * Removes orders with status PENDING_PAYBOX.
     */
    public void clearOrdersThatPendingPaybox() {
        if (isStartManager) {
            if (isLoggingInfo()) {
                logInfo("clearOrdersThatPendingPaybox is already working.");
            }
            return;
        }

        if (isLoggingInfo()) {
            logInfo("start - clearOrdersThatPendingPaybox");
        }
        try {
            isStartManager = true;

            removeOrdersThatPendingPaybox(true, STATE_PENDING_PAYBOX);

        } finally {
            isStartManager = false;
        }
        if (isLoggingInfo()) {
            logInfo("finish - clearOrdersThatPendingPaybox");
        }
    }

    /**
     * Removes orders with status PENDING_REMOVE.
     */
    public void clearOrdersThatPendingRemove() {
        if (isStartManagerPendingRemove) {
            if (isLoggingInfo()) {
                logInfo("clearOrdersThatPendingRemove is already working.");
            }
            return;
        }

        if (isLoggingInfo()) {
            logInfo("start - clearOrdersThatPendingRemove");
        }
        try {
            isStartManagerPendingRemove = true;

            removeOrdersThatPendingPaybox(true, STATE_PENDING_REMOVE);

        } finally {
            isStartManagerPendingRemove = false;
        }
        if (isLoggingInfo()) {
            logInfo("finish - clearOrdersThatPendingRemove");
        }
    }

    /**
     * check orders with status PENDING_PAYBOX before remove.
     */
    public void checkCleanOrdersThatPendingPaybox() {
        if (isLoggingInfo()) {
            logInfo("start - checkCleanOrdersThatPendingPaybox");
        }
        removeOrdersThatPendingPaybox(false, STATE_PENDING_PAYBOX);

        if (isLoggingInfo()) {
            logInfo("finish - checkCleanOrdersThatPendingPaybox");
        }
    }

    /**
     * check orders with status PENDING_REMOVE before remove.
     */
    public void checkCleanOrdersThatPendingRemove() {
        if (isLoggingInfo()) {
            logInfo("start - checkCleanOrdersThatPendingRemove");
        }
        removeOrdersThatPendingPaybox(false, STATE_PENDING_REMOVE);

        if (isLoggingInfo()) {
            logInfo("finish - checkCleanOrdersThatPendingRemove");
        }
    }

    /**
     * Remove orders with state = PENDING_PAYBOX
     *
     * @param isDelete parameter
     * @param state    parameter
     */
    private void removeOrdersThatPendingPaybox(boolean isDelete, String state) {
        if (isLoggingDebug()) {
            logDebug("isDelete=" + isDelete + "; state=" + state);
        }
        countRemoveOrder = 0;
        mEndDateCalc = "";
        CastOrderManager orderManager = getOrderManager();
        if (null != orderManager) {
            Repository ordersRepository = getOrdersRepository();
            if (null != ordersRepository) {
                Date endDate = null;
                try {
                    endDate = getEndDateAsDate();
                    if (endDate == null) {
                        if (isLoggingWarning()) {
                            logWarning("Cannot set endDate: " + getEndDate());
                        }
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                    return;
                }

                mEndDateCalc = DATE_FORMAT.format(endDate);

                try {
                    RepositoryView ordersView = ordersRepository.getView("order");
                    RqlStatement requestRQLCount = RqlStatement.parseRqlStatement(countOrdersByDate);
                    int cntOrders = requestRQLCount.executeCountQuery(ordersView, new Object[] {state, endDate});
                    countRemoveOrder = cntOrders;
                    if (isLoggingInfo()) {
                        logInfo("Found " + cntOrders + " orders with state = " + state +
                                " and lastModifiedDate less than " + endDate);
                    }

                    if (cntOrders > 0) {
                        int startPosition = 0;
                        int nmbChunk = 1;

                        int cntChunk = cntOrders / PORTION_SIZE;
                        if (cntChunk == 0) {
                            cntChunk = 1;
                        } else if ((cntOrders % PORTION_SIZE) > 0) {
                            cntChunk++;
                        }

                        TransactionDemarcation trd = new TransactionDemarcation();

                        for (; nmbChunk <= cntChunk; nmbChunk++) {
                            RqlStatement statment = RqlStatement.parseRqlStatement(getOrdersByDate);
                            RepositoryItem[] ordersItems =
                                statment.executeQueryUncached(ordersView,
                                                              new Object[] {state, endDate, startPosition, PORTION_SIZE});

                            List<String> ordersClearList = new LinkedList<String>();
                            if ((null != ordersItems) && (0 < ordersItems.length)) {
                                for (RepositoryItem orderItem : ordersItems) {
                                    ordersClearList.add(orderItem.getRepositoryId());
                                }
                            }

                            for (String ordersClearItem : ordersClearList) {
                                if (isDelete) {
                                    try {
                                        trd.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

                                        try {
                                            orderManager.removeOrder(ordersClearItem);
                                            if (isLoggingDebug()) {
                                                vlogDebug("Order with ID={0} removed.", ordersClearItem);
                                            }
                                        } catch (CommerceException e) {
                                            if (isLoggingError()) {
                                                vlogError(e, "Can't remove order with ID={0} .", ordersClearItem);
                                            }
                                            try {
                                                getTransactionManager().setRollbackOnly();

                                            } catch (Exception se) {
                                                if (isLoggingError()) {
                                                    logError("Unable to set rollback for transaction", se);
                                                }
                                            }
                                        }

                                    } finally {
                                        trd.end();
                                    }  // end try-finally
                                } else {
                                    if (isLoggingDebug()) {
                                        logDebug("Order ID=" + ordersClearItem);
                                    }
                                }  // end if-else
                            }  // end for
                            startPosition += PORTION_SIZE;
                        }  // end for

                        if (isDelete) {
                            cntOrders = requestRQLCount.executeCountQuery(ordersView, new Object[] {state, endDate});
                            countRemoveOrder = cntOrders;
                            if (isLoggingInfo()) {
                                logInfo("Check after remove. Found " + cntOrders + " orders with state = " + state +
                                        " and lastModifiedDate less than " + endDate);
                            }
                        }
                    }  // end if
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                } catch (TransactionDemarcationException e) {
                    if (isLoggingError()) {
                        logError("Creating transaction demarcation failed", e);
                    }
                }  // end try-catch
            } else {
                if (isLoggingDebug()) {
                    logDebug("ordersRepository does not properly setted.");
                }
            }  // end if-else
        } else {
            if (isLoggingDebug()) {
                logDebug("orderManager does not properly setted.");
            }
        }  // end if-else
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @throws IllegalArgumentException exception
     */
    private String checkDate() {
        String eDate = getEndDate();
        if (isLoggingInfo()) {
            logInfo("endDate: " + eDate);
        }

        if (!StringUtils.isBlank(eDate)) {
            eDate = eDate.trim();
            String[] arr = eDate.split("/");
            if ((arr == null) || (arr.length != 3)) {
                throw new IllegalArgumentException("Parameter 'endDate' is invalid.");
            }

            try {
                int day = Integer.parseInt(arr[0]);
                int month = Integer.parseInt(arr[1]);
                int year = Integer.parseInt(arr[2]);
                if ((day < 1) || (day > 31) || (month < 1) || (month > 12) || (year < 2000)) {
                    throw new IllegalArgumentException("Parameter 'endDate' is invalid.");
                }
            } catch (NumberFormatException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                throw new IllegalArgumentException("Parameter 'endDate' is invalid.");
            }
        }  // end if
        return eDate;
    }

    /**
     * Returns endDateAsDate property.
     *
     * @return endDateAsDate property.
     */
    private Date getEndDateAsDate() {
        Date date = null;
        if (!StringUtils.isBlank(checkDate())) {
            try {
                date = DATE_FORMAT.parse(getEndDate());
            } catch (ParseException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                date = null;
            }
        }

        if (date == null) {
            Date currDate = new Date();
            Integer delta = getDelta();
            try {
                date = DATE_FORMAT.parse(DATE_FORMAT.format(currDate));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, -delta);
                date = cal.getTime();
            } catch (ParseException e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }

        }
        if (isLoggingDebug()) {
            logDebug("End date: " + date);
        }
        return date;
    }

    /**
     * Returns ordersRepository property.
     *
     * @return ordersRepository property.
     */
    public Repository getOrdersRepository() {
        return mOrdersRepository;
    }

    /**
     * Sets the value of the ordersRepository property.
     *
     * @param pOrdersRepository parameter to set.
     */
    public void setOrdersRepository(Repository pOrdersRepository) {
        mOrdersRepository = pOrdersRepository;
    }

    /**
     * Returns sipsRepository property.
     *
     * @return sipsRepository property.
     */
    public Repository getSipsRepository() {
        return mSipsRepository;
    }

    /**
     * Sets the value of the sipsRepository property.
     *
     * @param pSipsRepository parameter to set.
     */
    public void setSipsRepository(Repository pSipsRepository) {
        mSipsRepository = pSipsRepository;
    }

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public CastOrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(CastOrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }

    /**
     * Returns transactionManager property.
     *
     * @return transactionManager property.
     */
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /**
     * Sets the value of the transactionManager property.
     *
     * @param transactionManager parameter to set.
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Return specified date
     *
     * @return ToDo: DOCUMENT ME!
     *
     * @beaninfo
     *   description: The test for end date
     */
    public String getEndDate() {
        return mEndDate;
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param endDate parameter to set.
     */
    public void setEndDate(String endDate) {
        this.mEndDate = endDate;
        this.mEndDateCalc = "";
        this.countRemoveOrder = 0;
    }

    /**
     * Returns countRemoveOrder property.
     *
     * @return countRemoveOrder property.
     */
    public Integer getCountRemoveOrder() {
        return countRemoveOrder;
    }

    /**
     * Returns endDateCalc property.
     *
     * @return endDateCalc property.
     */
    public String getEndDateCalc() {
        return mEndDateCalc;
    }

    /**
     * Returns delta property.
     *
     * @return delta property.
     */
    public Integer getDelta() {
        return mDelta;
    }

    /**
     * Sets the value of the delta property.
     *
     * @param pDelta parameter to set.
     */
    public void setDelta(Integer pDelta) {
        if ((pDelta == null) || (pDelta < 0)) {
            mDelta = DEFAULT_DELTA;
        } else {
            mDelta = pDelta;
        }  // end if-else
    }

}
