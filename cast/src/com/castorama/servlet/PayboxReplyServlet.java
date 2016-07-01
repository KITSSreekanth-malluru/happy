package com.castorama.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import atg.adapter.gsa.GSAItem;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderManager;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.states.CastOrderStates;

/**
 *
 * Extends PayboxServlet to wait order submisison.
 * Configured by /com/castorama/droplet/PayboxReplyCommerceDroplet
 *               /castorama/droplet/PayboxReplyCallCenterDroplet 
 *
 * @author EPAM team
 */
public class PayboxReplyServlet extends PayboxServlet {

    /** shoppingCart parameter */
    private static final String SHOPPING_CART_PARAMETER = "shoppingCart";

    /** orderId parameter */
    private static final String ORDER_ID_PARAMETER = "orderId";

    /** callback parameter */
    private static final String CALLBACK_PARAMETER = "callback";

    /** cancel */
    private static final String CANCEL_VALUE = "cancel";

    /** refuse */
    private static final String REFUSE_VALUE = "refuse";

    /** errorParam property. */
    private String mErrorParam;

    /** successURL property. */
    private String mSuccessURL;

    /** errorURL property. */
    private String mErrorURL;

    /** pendingURL property. */
    private String mPendingURL;
    
    /** orderStates property. */
    private CastOrderStates mOrderStates;
    
    /** orderIdParam property. */
    private String mOrderIdParam;
    
    /** attemptCount property. */
    private String mAttemptCount;
    
    /** protocolSwitchServlet property. */
    private CastoProtocolSwitchServlet mProtocolSwitchServlet; 
    
    /**
     * Checks when result code is ok and order is submitted.
     * 
     * @param  request  dynamo http servlet request.
     * @param  response dynamo http servlet response.
     *
     * @throws IOException      exception, when io error.
     * @throws ServletException exception, when logic error. 
     */
    protected void serviceAction(DynamoHttpServletRequest request, DynamoHttpServletResponse response)
                          throws IOException, ServletException {

        if ( isLoggingInfo() ) {
            logInfo("serviceAction(request) - start: " + request.getQueryString());
        }
        
        String error = request.getParameter(getErrorParam());
        String callback = request.getParameter(CALLBACK_PARAMETER);

        if ( isLoggingInfo() ) {
            logInfo("serviceAction(request) - callback: " + callback + " - " + request.getQueryString());
        }
        
        if ( CANCEL_VALUE.equalsIgnoreCase(callback) 
                || REFUSE_VALUE.equalsIgnoreCase(callback) ) {
            
            String orderId = getOrderId(request);
            getOrderManager().markOrderAsHandledPaybox(orderId, Boolean.FALSE, null, null);
            redirect(request, response, getErrorURL());
        } else {
            String redirectTo = getErrorURL();
            //
            // To prevent unnecessary order processing when user double-click on "Retoure ecommerce".
            //
            String ref = request.getParameter(getOrderIdParam());
            HttpSession session = request.getSession();
            Map<String, String> reply_routing;
            synchronized (session) {
                reply_routing = (Map<String, String>)session.getAttribute("CASTO_REPLY_ROUTING"); 
                if ( null == reply_routing ) {
                    reply_routing = new HashMap<String, String>();
                    session.setAttribute("CASTO_REPLY_ROUTING", reply_routing);

                    if ( isLoggingInfo() ) {
                        logInfo("serviceAction(request) - reply_routing is created - " + request.getQueryString());
                    }
                }
            }
            
            if ( isLoggingInfo() ) {
                logInfo("serviceAction(request) - reply_routing: " + reply_routing.hashCode()+ " - " + request.getQueryString());
            }

            synchronized (reply_routing) {
                if ( isLoggingInfo() ) {
                    logInfo("serviceAction(request) - reply_routing contains ref: " + 
                            reply_routing.containsKey(ref) + " - " + request.getQueryString());
                }

                if ( !reply_routing.containsKey(ref) ) {
                    Object shoppingCartParameter = request.getObjectParameter(SHOPPING_CART_PARAMETER);

                    if ( isLoggingInfo() ) {
                        logInfo("serviceAction(request) - shoppingCartParameter exists: " + 
                                shoppingCartParameter + " - " + request.getQueryString());
                    }

                    CastOrderHolder orderHolder = null;
                    Order current = null;
                    String orderId = null;
                    if (shoppingCartParameter instanceof CastOrderHolder) {
                        orderHolder = (CastOrderHolder) shoppingCartParameter;
                        current = orderHolder.getCurrentlySelected();
                        orderId = current.getId(); 
                    } else {
                        orderId = request.getParameter(ORDER_ID_PARAMETER);
                    }
                    
                    if ( isLoggingInfo() ) {
                        logInfo("serviceAction(request) - orderHolder: " + orderHolder + " - " + request.getQueryString());
                        logInfo("serviceAction(request) - current: " + current + " - " + request.getQueryString());
                        logInfo("serviceAction(request) - orderId: " + orderId + " - " + request.getQueryString());
                        logInfo("serviceAction(request) - getOrderIdParam(): " + getOrderIdParam() + " - " + request.getQueryString());
                    }

                    if (null != orderId) {
                        if (orderId.equalsIgnoreCase(request.getParameter(getOrderIdParam()))) {
                            if (checkSubmittedOrderStatus(orderId)) {
                                redirectTo = getSuccessURL();
                            } else {
                                if (!checkFailedOrderStatus(orderId)) {
                                    if (getOrderManager().setPendingPayboxStateFO(orderId)) {
                                        if (orderHolder != null) {
                                            if (orderHolder.getSelectedOrder() == 2) {
                                                orderHolder.setCurrentLocal(null);
                                            } else {
                                                orderHolder.setCurrent(null);
                                            }
                                        }
                                    }
                                    boolean isOk = getCodeOk().equals(error);
                                    boolean isSign = isCorrectSign(request);

                                    if (isLoggingInfo()) {
                                        logInfo("serviceAction(request) - isOk: " + isOk + " - " + request.getQueryString());
                                        logInfo("serviceAction(request) - isSign: " + isSign + " - " + request.getQueryString());
                                    }

                                    if (isOk && isSign) {
                                        int attempCount = getAttempCountInt();
                                        boolean isSubmitted = false;
                                        boolean isFailed = false;
                                        for (int i = 0; i < attempCount; i++) {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                if (isLoggingError()) {
                                                    logError("Wait order " + orderId + " has been interrapted.");
                                                }
                                            }
                                            if (isLoggingInfo()) {
                                                logInfo("serviceAction(request) - try to check order status: " + i);
                                            }

                                            if (checkSubmittedOrderStatus(orderId)) {
                                                isSubmitted = true;
                                                break;
                                            } else if (checkFailedOrderStatus(orderId)) {
                                                isFailed = true;
                                                break;
                                            }
                                        }

                                        if (isLoggingInfo()) {
                                            if (isFailed) {
                                                logInfo("serviceAction(request) - isFailed: " + isFailed + " - " + request.getQueryString());
                                            } else {
                                                logInfo("serviceAction(request) - isSubmitted: " + isSubmitted + " - " + request.getQueryString());
                                            }
                                        }

                                        if (isSubmitted) {
                                            if (null != orderHolder) {
                                                orderHolder.setLast(current);
                                            }
                                            redirectTo = getSuccessURL();
                                        } else if (isFailed) {
                                            redirectTo = getErrorURL();
                                        } else {
                                            getOrderManager().setSubmittedDateFO(orderId);
                                            redirectTo = getPendingURL();
                                        }
                                    } else {
                                        redirectTo = getErrorURL();
                                    }

                                    if (isLoggingInfo()) {
                                        logInfo("serviceAction(request) - redirectTo: " + redirectTo + " - " + request.getQueryString());
                                    }
                                } else {
                                    redirectTo = getErrorURL();
                                }
                            }
                        }
                        removeAffectedOrderFromShoppingCart(orderHolder, current);
                    }
                    reply_routing.put(ref, redirectTo);

                    if ( isLoggingInfo() ) {
                        logInfo("serviceAction(request) - ref is added to reply_routing");
                    }
                }
            }
            
            String redir = reply_routing.get(ref);

            if ( isLoggingInfo() ) {
                logInfo("serviceAction(request) - finish: " + redir + " - " + request.getQueryString());
            }
           
            if ( null != redir ) {
                redirect(request, response, redir);
            } else {
                if ( isLoggingError() ) {
                    logError("No way to redirect: " + ref);
                }
                redirect(request, response, getPendingURL());
            }
        }
    }
    
    private void redirect(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String pLocation) throws IOException {
        CastoProtocolSwitchServlet protocolSwitchServlet = getProtocolSwitchServlet();
        if ( null != protocolSwitchServlet ) {
            String newLocation = protocolSwitchServlet.getNonSecureUrl(pRequest.getContextPath() + pLocation);
            pResponse.sendRedirect(pResponse.encodeRedirectURL(newLocation));
        } else {
            pResponse.sendLocalRedirect(pLocation, pRequest);
        }
    }

    /**
     * Checks when order status is submitted.
     *
     * @param orderId order id.
     * @return true when order status is submitted, otherwise false.
     */
    private boolean checkSubmittedOrderStatus(String orderId) {
        boolean result = false;
        CastOrderManager com = getOrderManager();
        if (null != com) {
            result = com.checkSubmittedOrderStatus(orderId);
        }
        return result;
    }

    /**
     * Checks when order status is failed.
     *
     * @param orderId order id.
     * @return true when order status is failed, otherwise false.
     */
    private boolean checkFailedOrderStatus(String orderId) {
        boolean result = false;
        CastOrderManager com = getOrderManager();
        if (null != com) {
            result = com.checkFailedOrderStatus(orderId);
        }
        return result;
    }

    /**
     * Returns int value of the mAttemptCount property.
     *  
     * @return int value of the mAttemptCount property.
     */
    private int getAttempCountInt() {
        int result = 30;
        if ( null != mAttemptCount ) {
            try {
                result = Integer.parseInt(mAttemptCount);
            } catch(NumberFormatException e) {
                if ( isLoggingError() ) {
                    logError("attemptCount has wrong int format: " + mAttemptCount);
                }
            }
        }
        return result;
    }
    
    /**
     * Returns order id from request.
     * @param request request.
     * @return order id from request.
     */
    private String getOrderId(DynamoHttpServletRequest request) {
        String result = request.getParameter(ORDER_ID_PARAMETER);
        if ( null == result || 0 == result.trim().length() ) {
            Object shoppingCartParameter = request.getObjectParameter(SHOPPING_CART_PARAMETER);
            if (shoppingCartParameter instanceof CastOrderHolder) {
                try {
                    result = ((CastOrderHolder) shoppingCartParameter).getCurrentlySelected().getId();
                } catch (Exception e) {
                    if ( isLoggingDebug() ) {
                        logDebug("Can't get order id from shopping cart.");
                    }
                    result = null;
                }
            } else {
                result = null;
            }
        }
        return result;
    }

    private void removePayedItems(CastOrderImpl order1, CastOrderImpl order2) {
        OrderTools orderTools = getOrderManager().getOrderTools();
        if (orderTools instanceof CastOrderTools ){
            CastOrderTools castTools = (CastOrderTools) orderTools;
            castTools.removePayedItems(order1, order2);
        }
    }

    private void removeAffectedOrderFromShoppingCart(CastOrderHolder orderHolder, Order current) {
        if (null != orderHolder) {
            orderHolder.setLast(current);//TODO check that last don't override last;
            if (orderHolder.getSelectedOrder() == 2) {
                removePayedItems((CastOrderImpl) orderHolder.getCurrent(), (CastOrderImpl) current);
                orderHolder.setCurrentLocal(null);
            } else {
                removePayedItems(orderHolder.getCurrentLocal(), (CastOrderImpl) current);
                orderHolder.setCurrent(null);
            }
        }
    }

    /**
     * Returns errorParam property.
     *
     * @return errorParam property.
     */
    public String getErrorParam() {
        return mErrorParam;
    }

    /**
     * Sets the value of the errorParam property.
     *
     * @param errorParam parameter to set.
     */
    public void setErrorParam(String errorParam) {
        this.mErrorParam = errorParam;
    }

    /**
     * Returns successURL property.
     *
     * @return successURL property.
     */
    public String getSuccessURL() {
        return mSuccessURL;
    }

    /**
     * Sets the value of the successURL property.
     *
     * @param successURL parameter to set.
     */
    public void setSuccessURL(String successURL) {
        this.mSuccessURL = successURL;
    }

    /**
     * Returns errorURL property.
     *
     * @return errorURL property.
     */
    public String getErrorURL() {
        return mErrorURL;
    }

    /**
     * Sets the value of the errorURL property.
     *
     * @param errorURL parameter to set.
     */
    public void setErrorURL(String errorURL) {
        this.mErrorURL = errorURL;
    }

    /**
     * Return OrderStates
     * @return OrderStates property
     */
    public CastOrderStates getOrderStates() {
        return mOrderStates;
    }

    /**
     * @param orderStates parameter to set
     */
    public void setOrderStates(CastOrderStates orderStates) {
        this.mOrderStates = orderStates;
    }

    /**
     * @return the mOrderIdParam
     */
    public String getOrderIdParam() {
        return (null == mOrderIdParam || 0 == mOrderIdParam.trim().length()) ? "ref" : mOrderIdParam;
    }

    /**
     * @param pOrderIdParam the mOrderIdParam to set
     */
    public void setOrderIdParam(String pOrderIdParam) {
        mOrderIdParam = pOrderIdParam;
    }

    /**
     * @return the attemptCount
     */
    public String getAttemptCount() {
        return mAttemptCount;
    }

    /**
     * @param pAttemptCount the attemptCount to set
     */
    public void setAttemptCount(String pAttemptCount) {
        this.mAttemptCount = pAttemptCount;
    }

    /**
     * @return the pendingURL
     */
    public String getPendingURL() {
        return mPendingURL;
    }

    /**
     * @param pPendingURL the pendingURL to set
     */
    public void setPendingURL(String pPendingURL) {
        this.mPendingURL = pPendingURL;
    }

    /**
     * @return the protocolSwitchServlet
     */
    public CastoProtocolSwitchServlet getProtocolSwitchServlet() {
        return mProtocolSwitchServlet;
    }

    /**
     * @param pProtocolSwitchServlet the protocolSwitchServlet to set
     */
    public void setProtocolSwitchServlet(CastoProtocolSwitchServlet pProtocolSwitchServlet) {
        this.mProtocolSwitchServlet = pProtocolSwitchServlet;
    }
    
    
}
