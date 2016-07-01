package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.ServletException;

import javax.transaction.Transaction;

import atg.adapter.gsa.GSAItem;
import atg.commerce.order.OrderManager;

import atg.droplet.ForEach;

import atg.repository.RepositoryItem;

import atg.service.filter.ItemFilter;
import atg.service.filter.ItemHistory;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.targeting.Targeter;
import atg.targeting.TargetingArray;
import atg.targeting.TargetingException;

import com.castorama.commerce.order.CastOrderManager;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Retrieves orders and its messages to show in order history.
 * Configured by /atg/registry/RepositoryTargeters/Orders/WebOrders
 *
 * @author Vasili Ivus.
 */
public class WebOrdersDroplet extends TargetingArray {

	/** TARGETER constant. */
    public static final String TARGETER = "targeter";

    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** ORDERS constant. */
    public static final String ORDERS = "orders";

    /** MESSAGES constant. */
    public static final String MESSAGES = "messages";
    
    /** HISTORY_YEARS constant. */
    private static final int HISTORY_YEARS = -3;

    /** orderManager property. */
    private OrderManager mOrderManager;

    /**
     * Return orders and its messages in output. 
     *
     * @param     pRequest         dynamo http servlet request.
     * @param     pResponse        dynamo http servlet response.
     * @exception ServletException logic errors.
     * @exception IOException      IO error.
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
    		throws ServletException, IOException {

    	Transaction tr = null;
        try {
            tr = ensureTransaction();
            Object[] targets = null;
            ArrayList<RepositoryItem> messagesList = new ArrayList<RepositoryItem>();
            ArrayList<GSAItem> ordersList = new ArrayList<GSAItem>();
            ItemFilter filter = null;
            try {
                targets = getTargetArray(pRequest, Targeter.NO_MAX_NUMBER);
                filter = getFilter(pRequest);
                targets = (Object[]) ForEach.getSortedArray(this, targets, pRequest);
            } catch (TargetingException e) {
                if (isLoggingError()) {
                    logError(e);
                }
                return;
            }
            if ((targets == null) || (targets.length == 0)) {
                pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            } else {
                if (filter instanceof ItemHistory) {
                    ((ItemHistory) filter).recordItems(targets);
                }
                OrderManager om = getOrderManager();
                if (om instanceof CastOrderManager) {
                    CastOrderManager com = (CastOrderManager) om;
                    Calendar historyYears = Calendar.getInstance();
                    historyYears.add(Calendar.YEAR, HISTORY_YEARS);
                    for (int i = 0; i < targets.length; i++) {
                        if (targets[i] instanceof GSAItem) {
                            GSAItem order = (GSAItem) targets[i];
                            if ( addOrderToList(order, ordersList, historyYears) ) {
                            	String orderId = order.getRepositoryId();
                            	RepositoryItem obo = com.getOrderBO(orderId);
                            	if (null != obo) {
                            		int status = (int) com.getStateBO(obo.getRepositoryId());
                            		java.util.Date cancelledDate = com.getCancelledBO(obo.getRepositoryId());
                            		order.setPropertyValue("datesuppression", cancelledDate);
                            		RepositoryItem[] messages =
                            			com.getMessages(obo.getRepositoryId(), order.getRepositoryId(), false);
                            		if (0 < messages.length) {
                            			Collections.addAll(messagesList, messages);
                            		}
                            	}
                            }
                        }
                    }
                }
                pRequest.setParameter(ORDERS, ordersList.toArray(new RepositoryItem[ordersList.size()]));
                Collections.sort(messagesList, new Comparator() {
                                     public int compare(Object o1, Object o2) {
                                         Date d1 = null;
                                         Date d2 = null;
                                         if (o1 instanceof RepositoryItem) {
                                             d1 = (Date) ((RepositoryItem) o1).getPropertyValue("DCONTACT");
                                         }
                                         if (o2 instanceof RepositoryItem) {
                                             d2 = (Date) ((RepositoryItem) o2).getPropertyValue("DCONTACT");
                                         }
                                         if ((null == d1) && (null == d2)) {
                                             return 0;
                                         } else if (null == d1) {
                                             return 1;
                                         } else if (null == d2) {
                                             return -1;
                                         } else {
                                             return (d1.equals(d2)) ? 0 : ((d1.before(d2)) ? 1 : -1);
                                         }
                                     }
                                 });
                pRequest.setParameter(MESSAGES, messagesList.toArray(new RepositoryItem[messagesList.size()]));
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
        } finally {
            commitTransaction(tr);
        }
    }
    
    /**
     * Add order to list, check additional conditions here.
     * 
     * @param order order to add.
     * @param ordersList orders list.
     * @param threeYearsOld three years old.
     * @return true if order added else false. 
     */
    private boolean addOrderToList(GSAItem order, ArrayList<GSAItem> ordersList, Calendar history) {
    	boolean result = false;
    	if ( !"PENDING_PAYBOX".equalsIgnoreCase((String) order.getPropertyValue("state")) ) {
    		java.util.Date submittedDate = (java.util.Date) order.getPropertyValue(CastoConstantesCommande.SUBMITTED_DATE);  
    		if ( null == history || null == submittedDate || submittedDate.after(history.getTime()) ) {
    			result = ordersList.add(order);
    		}
    	}
    	return result;
	}


	/**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param pOrderManager parameter to set.
     */
    public void setOrderManager(OrderManager pOrderManager) {
        this.mOrderManager = pOrderManager;
    }
}
