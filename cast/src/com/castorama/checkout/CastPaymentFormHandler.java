package com.castorama.checkout;
import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_DELIVERY_TO_HOME;
import static com.castorama.constantes.CastoConstantesOrders.DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

import com.castorama.CastConfiguration;
import com.castorama.RollbackFunctionalityConfiguration;
import com.castorama.commerce.order.CastOrderHolder;
import com.castorama.commerce.order.CastOrderImpl;
import com.castorama.commerce.order.CastOrderTools;
import com.castorama.commerce.pricing.CastoShippingTools;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.payment.PayboxPaymentStates;
import com.castorama.payment.PaymentStates;
import com.castorama.utils.Validator;

/**
 * Customize order submission for castorama synch/asynch payment methods.
 *
 * @author Epam Team
 */
public class CastPaymentFormHandler extends CommitOrderFormHandler {
    /** ORDER constant. */
    private static final String ORDER = "order";

    /** USER constant. */
    private static final String USER = "user";

    /** PAYMENT_METHOD constant. */
    private static final String PAYMENT_METHOD = "paymentMethod";
    
    /** NEED_UPDATE_INVENTORY constant. */
    private static final String NEED_UPDATE_INVENTORY = "needUpdateInventory";
    
    private static final String CAST_CONFIGURATION_PATH = "/com/castorama/CastConfiguration";
    
    /** EML_RXTENSION constant */
    public static final String EML_EXTENSION = ".eml";

    /** payboxPaymentStates property */
    private PayboxPaymentStates mPayboxPaymentStates;

    /** paymentStates property */
    private PaymentStates mPaymentStates;

    /** cancelURL property */
    private String mCancelURL;

    /** cartURL property */
    private String mCartURL;

    /** successURL property */
    private String mSuccessURL;

    /** templateEmailConfirmCheck property */
    private TemplateEmailInfoImpl mTemplateEmailConfirmCheck = null;

    /** templateEmailConfirmPhone property */
    private TemplateEmailInfoImpl mTemplateEmailConfirmPhone = null;

    /** templateEmailConfirmClickAndCollect property */
    private TemplateEmailInfoImpl mTemplateEmailConfirmClickAndCollect = null;

    /** templateEmailConfirmTransfer property */
    private TemplateEmailInfoImpl mTemplateEmailConfirmTransfer = null;

    /** subjectEmailConfirmation property */
    private String mSubjectEmailConfirmation;

    /** templateEmailSender property */
    private TemplateEmailSender mTemplateEmailSender = null;
    
    /** shippingTools property  */
    private CastoShippingTools mShippingTools = null;

    /** mRollbackFunctionalityConfiguration property. */
    private RollbackFunctionalityConfiguration mRollbackFunctionalityConfiguration;

    /** clientLockManager property */
    protected ClientLockManager mClientLockManager;

    /**
     * Returns payboxPaymentStates property.
     *
     * @return payboxPaymentStates property.
     */
    public PayboxPaymentStates getPayboxPaymentStates() {
        return mPayboxPaymentStates;
    }

    /**
     * Sets the value of the payboxPaymentStates property.
     *
     * @param pPayboxPaymentStates parameter to set.
     */
    public void setPayboxPaymentStates(PayboxPaymentStates pPayboxPaymentStates) {
        mPayboxPaymentStates = pPayboxPaymentStates;
    }

    /**
     * Returns paymentStates property.
     *
     * @return paymentStates property.
     */
    public PaymentStates getPaymentStates() {
        return mPaymentStates;
    }

    /**
     * Sets the value of the paymentStates property.
     *
     * @param pPaymentStates parameter to set.
     */
    public void setPaymentStates(PaymentStates pPaymentStates) {
        mPaymentStates = pPaymentStates;
    }

    /**
     * Sets the value of the templateEmailSender property.
     *
     * @param pTemplateEmailSender parameter to set.
     */
    public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
        mTemplateEmailSender = pTemplateEmailSender;
    }

    /**
     * Returns templateEmailSender property.
     *
     * @return templateEmailSender property.
     */
    public TemplateEmailSender getTemplateEmailSender() {
        return mTemplateEmailSender;
    }

    /**
     * Sets the value of the templateEmailConfirmCheck property.
     *
     * @param pTemplateEmailConfirmCheck parameter to set.
     */
    public void setTemplateEmailConfirmCheck(TemplateEmailInfoImpl pTemplateEmailConfirmCheck) {
        mTemplateEmailConfirmCheck = pTemplateEmailConfirmCheck;
    }
    
    /**
     * Returns templateEmailConfirmPhone property.
     *
     * @return templateEmailConfirmPhone property.
     */
    public TemplateEmailInfoImpl getTemplateEmailConfirmPhone() {
        return mTemplateEmailConfirmPhone;
    }

    /**
     * Sets the value of the templateEmailConfirmPhone property.
     *
     * @param pTemplateEmailConfirmPhone parameter to set.
     */
    public void setTemplateEmailConfirmPhone(TemplateEmailInfoImpl pTemplateEmailConfirmPhone) {
        mTemplateEmailConfirmPhone = pTemplateEmailConfirmPhone;
    }

    /**
     * Returns templateEmailConfirmClickAndCollect property.
     *
     * @return templateEmailConfirmClickAndCollect property.
     */
    public TemplateEmailInfoImpl getTemplateEmailConfirmClickAndCollect() {
        return mTemplateEmailConfirmClickAndCollect;
    }
    
    /**
     * Sets the value of the templateEmailConfirmClickAndCollect property.
     *
     * @param pTemplateEmailConfirmClickAndCollect parameter to set.
     */
    public void setTemplateEmailConfirmClickAndCollect(TemplateEmailInfoImpl pTemplateEmailConfirmClickAndCollect) {
        mTemplateEmailConfirmClickAndCollect = pTemplateEmailConfirmClickAndCollect;
    }

    /**
     * Returns templateEmailConfirmTransfer property.
     *
     * @return templateEmailConfirmTransfer property.
     */
    public TemplateEmailInfoImpl getTemplateEmailConfirmTransfer() {
        return mTemplateEmailConfirmTransfer;
    }

    /**
     * Sets the value of the templateEmailConfirmTransfer property.
     *
     * @param pTemplateEmailConfirmTransfer parameter to set.
     */
    public void setTemplateEmailConfirmTransfer(TemplateEmailInfoImpl pTemplateEmailConfirmTransfer) {
        mTemplateEmailConfirmTransfer = pTemplateEmailConfirmTransfer;
    }

    /**
     * Returns templateEmailConfirmCheck property.
     *
     * @return templateEmailConfirmCheck property.
     */
    public TemplateEmailInfoImpl getTemplateEmailConfirmCheck() {
        return mTemplateEmailConfirmCheck;
    }

    /**
     * Sets the value of the cartURL property.
     *
     * @param pCartURL parameter to set.
     */
    public void setCartURL(String pCartURL) {
        mCartURL = pCartURL;
    }

    /**
     * Returns cartURL property.
     *
     * @return cartURL property.
     */
    public String getCartURL() {
        return mCartURL;
    }

    /**
     * Sets the value of the cancelURL property.
     *
     * @param pCancelURL parameter to set.
     */
    public void setCancelURL(String pCancelURL) {
        mCancelURL = pCancelURL;
    }

    /**
     * Returns cancelURL property.
     *
     * @return cancelURL property.
     */
    public String getCancelURL() {
        return mCancelURL;
    }

    /**
     * Sets the value of the successURL property.
     *
     * @param pSuccessURL parameter to set.
     */
    public void setSuccessURL(String pSuccessURL) {
        mSuccessURL = pSuccessURL;
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
     * Returns subjectEmailConfirmation property.
     *
     * @return subjectEmailConfirmation property.
     */
    public String getSubjectEmailConfirmation() {
        return mSubjectEmailConfirmation;
    }

    /**
     * Sets the value of the subjectEmailConfirmation property.
     *
     * @param pSubjectEmailConfirmation parameter to set.
     */
    public void setSubjectEmailConfirmation(String pSubjectEmailConfirmation) {
        mSubjectEmailConfirmation = pSubjectEmailConfirmation;
    }

    /**
     * Returns rollbackFunctionalityConfiguration property.
     *
     * @return rollbackFunctionalityConfiguration property.
     */
    public RollbackFunctionalityConfiguration getRollbackFunctionalityConfiguration() {
        return mRollbackFunctionalityConfiguration;
    }

    /**
     * Sets the value of the rollbackFunctionalityConfiguration property.
     *
     * @param pRollbackFunctionalityConfiguration parameter to set.
     */
    public void setRollbackFunctionalityConfiguration(RollbackFunctionalityConfiguration pRollbackFunctionalityConfiguration) {
        mRollbackFunctionalityConfiguration = pRollbackFunctionalityConfiguration;
    }

    /**
     * Returns clientLockManager property.
     *
     * @return clientLockManager property.
     */
    public ClientLockManager getClientLockManager() {
        return mClientLockManager;
    }

    /**
     * Sets the value of the clientLockManager property.
     *
     * @param pClientLockManager parameter to set.
     */
    public void setClientLockManager(ClientLockManager pClientLockManager) {
        mClientLockManager = pClientLockManager;
    }

    /**
     * Redirects to the cancelURL, if cancelURL is non-null value.
     *
     * @param  pRequest  - http request
     * @param  pResponse - http response
     *
     * @return true if success, false - otherwise
     *
     * @throws ServletException if servlet error occurs
     * @throws IOException      if IO error occurs
     */
    public boolean handleCancel(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                         throws ServletException, IOException {
        String cancel = getCancelURL();
        if (!StringUtils.isBlank(cancel)) {
            redirectOrForward(pRequest, pResponse, cancel);
            return false;
        }
        return true;
    }

    /**
     * Redirects to the cartURL, if cartURL is non-null value.
     *
     * @param  pRequest  - http request
     * @param  pResponse - http response
     *
     * @return true if success, false - otherwise
     *
     * @throws ServletException if servlet error occurs
     * @throws IOException      if IO error occurs
     */
    public boolean handleCart(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                       throws ServletException, IOException {
        String url = getCartURL();

        if (!StringUtils.isBlank(url)) {
            redirectOrForward(pRequest, pResponse, url);
            return false;
        }
        return true;
    }

    /**
     * Add email sending after order's completion.
     *
     * @param  pRequest  - http request
     * @param  pResponse - http response
     *
     * @throws ServletException if servlet error occurs
     * @throws IOException      if IO error occurs
     */
    @Override public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                   throws ServletException, IOException {
        String paymentMethod = pRequest.getParameter(PAYMENT_METHOD);
        PaymentStates paymentStates = getPaymentStates();
        if (!StringUtils.isBlank(paymentMethod) && (paymentStates != null)) {
            if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.CHEQUE)))) {
                sendConfirmationEmail(getTemplateEmailConfirmCheck());
            } else if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.CALL_CENTER)))) {
                if (!DELIVERY_TYPE_OPTION_CLICK_AND_COLLECT.equals(((CastOrderImpl)getShoppingCart().getLast()).getDeliveryType()))
                sendConfirmationEmail(getTemplateEmailConfirmPhone());
                else
                    sendConfirmationEmail(getTemplateEmailConfirmClickAndCollect());
            } else if ((paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.VIREMENT)))) {
                sendConfirmationEmail(getTemplateEmailConfirmTransfer());
            }
        }
        super.postCommitOrder(pRequest, pResponse);
    }

    /**
     * Method for sending order confirmation email
     *
     * @param pTemplate template to use
     */
    private void sendConfirmationEmail(TemplateEmailInfoImpl pTemplate) {
        RepositoryItem user = getProfile();

        Map params = new HashMap();

        params.put(USER, user);

        params.put(ORDER, getShoppingCart().getLast());

        List recipents = new ArrayList();
        recipents.add(user.getPropertyValue(EMAIL_PROFILE_PROP));
        
        StringBuffer bccField = new StringBuffer();
        CastConfiguration castConfiguration =
                (CastConfiguration) Nucleus.getGlobalNucleus().resolveName(CAST_CONFIGURATION_PATH);
        ArrayList<String> bccEmails = castConfiguration.getBccListForConfirmationEmails();
        for(String email : bccEmails) {
        	if (Validator.validateEmail(email)) {
            	bccField.append(email+",");
        	}
        }
        if(!StringUtils.isBlank(bccField.toString())) {
        	pTemplate.setMessageBcc(bccField.toString());
        }
        
        CastOrderImpl order = (CastOrderImpl)getShoppingCart().getLast();
        File[] filesToAttach = ((CastOrderTools)getOrderManager().getOrderTools()).createEmailAttachments(order);
        pTemplate.setMessageAttachments(filesToAttach);
        
        pTemplate.setMessageSubject(getSubjectEmailConfirmation() + getShoppingCart().getLast().getId());
        pTemplate.setTemplateParameters(params);
        
        String tempFilesDestFolder = castConfiguration.getConfirmEmailsDestFolder();
        StringBuffer emlFilePath = new StringBuffer();
        
        if (!StringUtils.isEmpty(tempFilesDestFolder)) {       	        	
        	emlFilePath.append(tempFilesDestFolder);    		
        	if(!tempFilesDestFolder.endsWith("/")){
        		emlFilePath.append("/");
        	}

            emlFilePath.append(order.getId());
            emlFilePath.append(EML_EXTENSION);            

            Message message;
			try {
				message = getTemplateEmailSender().createMessage(pTemplate, user.getPropertyValue(EMAIL_PROFILE_PROP));
				try {
					message.writeTo(new FileOutputStream(new File(emlFilePath.toString())));
				} catch (FileNotFoundException e) {
					if(isLoggingError()){
						logError("It's impossible to create new EML file: " + emlFilePath, e);
					}
				} catch (IOException e) {
					if(isLoggingError()){
						logError("Unable to open FileOutputStream", e);
					}
				} catch (MessagingException e) {
					if(isLoggingError()){
						logError("Error while creating EML file for order: " + order.getId(), e);
					}
				}
			} catch (TemplateEmailException e) {
				if(isLoggingError()){
					logError("It's impossible to create confirmation email for order: " + order.getId(), e);
				}
			}	
        }
        
        try {
            getTemplateEmailSender().sendEmailMessage(pTemplate, recipents, true, false);
        } catch (TemplateEmailException e) {
        }

    }

    /**
     * Method for updating order for money transfer, telephone & etc payment
     * methods
     *
     * @param  pOrder    - Order
     * @param  pRequest  - http request
     * @param  pResponse - http response
     *
     * @throws ServletException if servlet error occurs
     * @throws IOException      if IO error occurs
     */
    public void commitOrder(Order pOrder, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                     throws ServletException, IOException {
        String paymentMethod = pRequest.getParameter(PAYMENT_METHOD);
        PaymentStates paymentStates = getPaymentStates();
        if (!StringUtils.isBlank(paymentMethod)) {
            if (
                (paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.CALL_CENTER))) ||
                    (paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.CHEQUE))) ||
                    (paymentMethod.trim()).equalsIgnoreCase(paymentStates.getStateString(paymentStates.getStateValue(paymentStates.VIREMENT)))) {
//                Order tmpOrder = getOrder();
//                if (tmpOrder instanceof CastOrderImpl) {
                  if (pOrder instanceof CastOrderImpl) {
                    CastOrderImpl order = (CastOrderImpl) pOrder;
                    for (Object o : order.getPaymentGroups()) {
                        PaymentGroup group = (PaymentGroup) o;
                        group.setPaymentMethod(paymentMethod);
                    }
                    try {
                        Map map = new HashMap();
                        map.put(PAYMENT_METHOD, paymentMethod);
                        map.put(NEED_UPDATE_INVENTORY, false);
                        map.put(CastoConstantesCommande.PIPELINE_CASTO_SHIPPING_TOOLS, getShippingTools());
                        order.setSubmittedTime(System.currentTimeMillis());
                        PipelineResult result =
                            getOrderManager().processOrder(order, "payboxProcessOrder2",
                                                           getOrderManager().getProcessOrderMap(null, map));

                        if (getShoppingCart() instanceof CastOrderHolder){
                            CastOrderHolder shoppingCart = (CastOrderHolder)getShoppingCart();
                            shoppingCart.setLast(order);
                            if (order.getDeliveryType() != null && order.getDeliveryType().equals(DELIVERY_TYPE_OPTION_DELIVERY_TO_HOME)){
                                removePayedItems(shoppingCart.getCurrentLocal(), order);
                                shoppingCart.setCurrent(null);
                            } else {
                                removePayedItems((CastOrderImpl) shoppingCart.getCurrent(), order);
                                shoppingCart.setCurrentLocal(null);
                            }
                        } else {
                            getShoppingCart().setLast(order);
                        }
                        getOrderManager().updateOrder(order);

                    } catch (CommerceException e) {
                        processException(e, MSG_COMMIT_ERROR, pRequest, pResponse);
                    }
                }  // end if
            }  // end if
        }  // end if
    }

    /**
     * @return the shippingTools
     */
    public CastoShippingTools getShippingTools() {
        return mShippingTools;
    }

    /**
     * @param pShippingTools the shippingTools to set
     */
    public void setShippingTools(CastoShippingTools pShippingTools) {
        this.mShippingTools = pShippingTools;
    }

    /* (non-Javadoc)
    * @see atg.commerce.order.purchase.PurchaseProcessFormHandler#afterSet(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
    */
    @Override public boolean afterSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                               throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        Order currentOrder = null;
        if (curShCart instanceof CastOrderHolder){
            currentOrder = ((CastOrderHolder) curShCart).getCurrentlySelected();
        } else {
            currentOrder = curShCart.getCurrent();
        }
        if ((profileItem != null) && (curShCart != null) && (currentOrder != null)) {
            Order curOrder = currentOrder;
            if (curOrder != null) {
                if (isLoggingDebug()) {
                    logDebug("CastPaymentFormHandler release lock : Profile ID : " + profileItem.getRepositoryId() +
                             "; Order ID  : " + curOrder.getId());

                }
            }
        }
        return super.afterSet(pRequest, pResponse);
    }

    /* (non-Javadoc)
     * @see atg.commerce.order.purchase.PurchaseProcessFormHandler#beforeSet(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
     */
    @Override public boolean beforeSet(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                                throws DropletFormException {
        RepositoryItem profileItem = getProfile();
        OrderHolder curShCart = getShoppingCart();
        Order currentOrder = null;
        if (curShCart instanceof CastOrderHolder){
            currentOrder = ((CastOrderHolder) curShCart).getCurrentlySelected();
        } else {
            currentOrder = curShCart.getCurrent();
        }
        if ((profileItem != null) && (curShCart != null) && (currentOrder != null)) {
            Order curOrder = currentOrder;
            if (curOrder != null) {
                if (isLoggingInfo()) {
                    logInfo("CastPaymentFormHandler get lock : Profile ID : " + profileItem.getRepositoryId() +
                            "; Order ID : " + curOrder.getId());

                }
            }
        }
        return super.beforeSet(pRequest, pResponse);
    }


    /**
     *
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *
     *
     * @param  pRequest parameter
     *
     * @throws DeadlockException exception
     */
    protected void acquireTransactionLock(DynamoHttpServletRequest pRequest) throws DeadlockException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    RepositoryItem profileItem = getProfile();
                    if (profileItem != null) {
                        String profileId = profileItem.getRepositoryId();
                        pRequest.setAttribute(LOCK_NAME_ATTRIBUTE_NAME, profileId);
                        service.acquireTransactionLock(profileId);
                    } else {
                        service.acquireTransactionLock();
                    }
                }
            } catch (NoLockNameException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.acquireTransactionLock(pRequest);
        }  // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *
     *
     * @param  pRequest parameter
     *
     * @throws LockManagerException exception
     */
    protected void releaseTransactionLock(DynamoHttpServletRequest pRequest) throws LockManagerException {
        RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
        if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
            try {
                TransactionLockService service = getLockService();
                if (service != null) {
                    String lockName = (String) pRequest.getAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    if (lockName != null) {
                        service.releaseTransactionLock(lockName);
                        pRequest.removeAttribute(LOCK_NAME_ATTRIBUTE_NAME);
                    } else {
                        service.releaseTransactionLock();
                    }
                }
            } catch (LockManagerException exc) {
                if (isLoggingError()) {
                    logError(exc);
                }
            }
        } else {
            super.releaseTransactionLock(pRequest);
        }  // end if-else
    }

    /**
     * Update to maintain centralized ClientLockManager if
     * rollbackFunctionalityConfiguration.isRollbackOrderSynchronization() false
     * then ClientLockManager, otherwise LocalLockManager is used
     *
     *
     * @return lockService property.
     */
    protected TransactionLockService getLockService() {
        TransactionLockFactory factory = getConfiguration().getTransactionLockFactory();
        TransactionLockService tls = null;
        if (factory != null) {
            tls = factory.getServiceInstance(this);
            RollbackFunctionalityConfiguration rfc = getRollbackFunctionalityConfiguration();
            if (!((rfc != null) && rfc.isRollbackOrderSynchronization() && (getClientLockManager() != null))) {
                tls.setClientLockManager(getClientLockManager());
            }
        }
        return tls;
    }
    
    /**
     * Return the Order property.
     * @return an <code>Order</code> value
     */
    public Order getOrder() {
      if (getShoppingCart() instanceof CastOrderHolder){
          return ((CastOrderHolder)getShoppingCart()).getCurrentlySelected();
      } else {
          return getShoppingCart().getCurrent();
      }
    }
    
    
    /**
     * Override method from CommitOrderFormHandler to use currently selected order 
     * from CastOrderHolder instead of current order
     *
     * @param pRequest
     * @param pResponse 
     * @return a <code>boolean</code> value
     * @exception ServletException
     * @exception IOException
     */
    public boolean handleCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        RepeatingRequestMonitor monitor = getRepeatingRequestMonitor();
        String handlerName = "CastPaymentFormHandler.handleCommit";
        if (monitor == null || monitor.isUniqueRequestEntry(handlerName)) {
            Transaction transaction = null;
            try {
                transaction = ensureTransaction();
                if (getUserLocale() == null)
                    setUserLocale(getUserLocale(pRequest, pResponse));
                if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
                    return false;
                OrderHolder castShoppingCart = getShoppingCart();
                Order currentOrder = null;
                boolean currentlySelectedEmpty;
                if (castShoppingCart instanceof CastOrderHolder) {
                    CastOrderHolder castOrderHolder = (CastOrderHolder) castShoppingCart;
                    currentOrder = castOrderHolder.getCurrentlySelected();
                    currentlySelectedEmpty = castOrderHolder.isCurrentlySelectedEmpty();
                } else {
                    currentOrder = castShoppingCart.getCurrent();
                    currentlySelectedEmpty = castShoppingCart.isCurrentEmpty();
                }
                synchronized (currentOrder) {
                    Order lastOrder = castShoppingCart.getLast();
                    String orderId = getOrderId();
                    if (lastOrder != null && orderId != null && orderId.equals(lastOrder.getId())) {
                        String path = generatePropertyPath("orderId");
                        String messsage = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
                        addFormException(new DropletFormException(messsage, path, MSG_ORDER_ALREADY_SUBMITTED));
                    } else if (orderId != null && !orderId.equals(currentOrder.getId())) {
                        String path = generatePropertyPath("orderId");
                        String message = formatUserMessage(MSG_ORDER_NOT_CURRENT, pRequest, pResponse);
                        addFormException(new DropletFormException(message, path, MSG_ORDER_NOT_CURRENT));
                    } else {
                        if (!getAllowEmptyOrders() && currentlySelectedEmpty) {
                            String path = generatePropertyPath("allowEmptyOrders");
                            String message = formatUserMessage(MSG_ORDER_EMPTY, pRequest, pResponse);
                            addFormException(new DropletFormException(message, path, MSG_ORDER_EMPTY));
                        }
                    }
                    if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
                        return false;
                    preCommitOrder(pRequest, pResponse);
                    if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
                        return false;
                    commitOrder(currentOrder, pRequest, pResponse);
                    if (!checkFormRedirect(null, getCommitOrderErrorURL(), pRequest, pResponse))
                        return false;
                    postCommitOrder(pRequest, pResponse);
                }
                return checkFormRedirect(getCommitOrderSuccessURL(), getCommitOrderErrorURL(), pRequest, pResponse);
            } finally {
                if (transaction != null)
                    commitTransaction(transaction);
                if (monitor != null)
                    monitor.removeRequestEntry(handlerName);
            }
        } else {
            return false;
        }
    }

    private void removePayedItems(CastOrderImpl order1, CastOrderImpl order2) {
        OrderTools orderTools = getOrderManager().getOrderTools();
        if (orderTools instanceof CastOrderTools ){
            CastOrderTools castTools = (CastOrderTools) orderTools;
            castTools.removePayedItems(order1, order2);
        }
        return;
    }

}
