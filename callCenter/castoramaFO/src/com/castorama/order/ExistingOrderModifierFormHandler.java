package com.castorama.order;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.fulfillment.GenericAdd;
import atg.commerce.fulfillment.GenericRemove;
import atg.commerce.fulfillment.GenericUpdate;
import atg.commerce.fulfillment.IdTargetModification;
import atg.commerce.fulfillment.PaymentGroupUpdate;
import atg.commerce.fulfillment.ShippingGroupUpdate;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.GiftCertificate;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidTypeException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderModifierFormHandler;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupNotFoundException;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.Relationship;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.promotion.PromotionTools;
import com.castorama.commerce.states.CastOrderStates;
import atg.droplet.DropletFormException;
import atg.payment.creditcard.CreditCardTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

//Referenced classes of package atg.commerce.csr:
//MessageDispatcher, OrderUpdateNotification

public class ExistingOrderModifierFormHandler extends OrderModifierFormHandler
{

    public ExistingOrderModifierFormHandler()
    {
        mOrderValidationError = false;
        mNotificationMessage = new OrderUpdateNotification();
        mCurrentTransaction = null;
        mShippingMethod = null;
        mCreditCardType = null;
        mCreditCardNumber = null;
        mExpirationMonth = null;
        mExpirationDayOfMonth = null;
        mExpirationYear = null;
    }

    public boolean afterSet(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    {
        Transaction transaction = getCurrentTransaction();
        if(isLoggingDebug())
        {
            logDebug("Calling afterSet: " + transaction);
        }
        dynamohttpservletresponse.setHeader("Expires", "-1");
        if(transaction != null)
        {
            commitTransaction(transaction);
            setCurrentTransaction(null);
        }
        return true;
    }

    public boolean beforeSet(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    {
        Transaction transaction = ensureTransaction();
        if(transaction != null)
        {
            setCurrentTransaction(transaction);
        }
        if(isLoggingDebug())
        {
            logDebug("Calling beforeSet: " + transaction);
        }
        return true;
    }

    protected void computeRemainingAmountDue(Order order)
    {
        List list = getAllPaymentGroupRelationships();
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        double d3 = 0.0D;
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            PaymentGroupRelationship paymentgrouprelationship = (PaymentGroupRelationship)iterator.next();
            PaymentGroup paymentgroup = paymentgrouprelationship.getPaymentGroup();
            int i = paymentgrouprelationship.getRelationshipType();
            if(i == 200)
            {
                d1 += paymentgrouprelationship.getAmount();
            }
            if(i == 301)
            {
                d3 += paymentgrouprelationship.getAmount();
            }
            if(i == 403)
            {
                d += paymentgrouprelationship.getAmount();
            }
            if(i == 401)
            {
                d2 += paymentgrouprelationship.getAmount();
            }
            if(i == 203)
            {
                flag1 = true;
            }
            if(i == 405)
            {
                flag = true;
            }
            if(i == 404)
            {
                flag2 = true;
            }
            if(i == 302)
            {
                flag3 = true;
            }
        }

        if(flag2 || flag && flag3 && flag1)
        {
            setOrderDue(0.0D);
            setItemDue(0.0D);
            setTaxDue(0.0D);
            setShippingDue(0.0D);
            return;
        }
        OrderPriceInfo orderpriceinfo = order.getPriceInfo();
        double d4 = 0.0D;
        if(isLoggingDebug())
        {
            logDebug(flag1 + " ItemAmount: " + orderpriceinfo.getAmount() + " / Computed: " + d1);
            logDebug(flag3 + " ShipAmount: " + orderpriceinfo.getShipping() + " / Computed: " + d3);
            logDebug(flag + " TaxAmount: " + orderpriceinfo.getTax() + " / Computed: " + d);
            logDebug(flag2 + " OrderAmount: " + orderpriceinfo.getTotal() + " / Computed: " + d2);
        }
        d4 = orderpriceinfo.getTotal() - d2;
        if(d4 > 0.0D)
        {
            setOrderDue(d4);
        } else
        {
            if(flag1)
            {
                setItemDue(0.0D);
            } else
            {
                double d5 = orderpriceinfo.getAmount() - d1;
                if(d5 <= 0.0D)
                {
                    d5 = 0.0D;
                }
                setItemDue(d5);
            }
            if(flag)
            {
                setTaxDue(0.0D);
            } else
            {
                double d6 = orderpriceinfo.getTax() - d;
                if(d6 <= 0.0D)
                {
                    d6 = 0.0D;
                }
                setTaxDue(d6);
            }
            if(flag3)
            {
                setShippingDue(0.0D);
            } else
            {
                double d7 = orderpriceinfo.getTax() - d;
                if(d7 <= 0.0D)
                {
                    d7 = 0.0D;
                }
                setShippingDue(d7);
            }
        }
    }

    protected boolean createPayment(String s, DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws IOException, ServletException
    {
        Order order = getOrder();
        if(order == null)
        {
            String s1 = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
            throw new ServletException(s1);
        }
        SimpleOrderManager simpleordermanager = getOrderManager();
        if(simpleordermanager == null)
        {
            String s2 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
            throw new ServletException(s2);
        }
        int i = -1;
        String s3 = getRelationshipTypeAsString();
        if(s3 != null && s3.trim().length() != 0)
        {
            i = RelationshipTypes.stringToType(s3);
        }
        if(i < 0)
        {
            String s4 = formatUserMessage("unknownRelationshipType", dynamohttpservletrequest, dynamohttpservletresponse);
            String s6 = generatePropertyPath("relationshipType");
            addFormException(new DropletFormException(s4, s6, "unknownRelationshipType"));
            return false;
        }
        String s5 = getCatalogRefId();
        if(i == 200 && (s5 == null || s5.trim().length() == 0))
        {
            String s7 = formatUserMessage("noItemsToAdd", dynamohttpservletrequest, dynamohttpservletresponse);
            String s9 = generatePropertyPath("catalogRefId");
            addFormException(new DropletFormException(s7, s9, "noItemsToAdd"));
            return false;
        }
        String s8 = getShippingGroupId();
        if(i == 301 && (s8 == null || s8.trim().length() == 0))
        {
            String s10 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
            String s11 = generatePropertyPath("shippingGroupId");
            addFormException(new DropletFormException(s10, s11, "noGroupId"));
            return false;
        }
        double d = 0.0D;
        if(i == 200 || i == 401 || i == 403)
        {
            d = getAmount();
            if(d <= 0.0D)
            {
                String s12 = formatUserMessage("amountLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s14 = generatePropertyPath("amount");
                addFormException(new DropletFormException(s12, s14, "amountLessThanOrEqualToZero"));
                return false;
            }
        }
        try
        {
            if(i == 200)
            {
                simpleordermanager.addItemAmountToPaymentGroup(order, s5, s, d);
            }
            else
                if(i == 403)
                {
                    simpleordermanager.addTaxAmountToPaymentGroup(order, s, d);
                }
                else
                    if(i == 301)
                    {
                        simpleordermanager.addShippingCostAmountToPaymentGroup(order, s8, s, d);
                    }
                    else
                        if(i == 401)
                        {
                            simpleordermanager.addOrderAmountToPaymentGroup(order, s, d);
                        }
                        else
                            if(i == 203)
                            {
                                simpleordermanager.addRemainingItemAmountToPaymentGroup(order, s5, s);
                            }
                            else
                                if(i == 405)
                                {
                                    simpleordermanager.addRemainingTaxAmountToPaymentGroup(order, s);
                                }
                                else
                                    if(i == 302)
                                    {
                                        simpleordermanager.addRemainingShippingCostToPaymentGroup(order, s8, s);
                                    }
                                    else
                                        if(i == 404)
                                        {
                                            simpleordermanager.addRemainingOrderAmountToPaymentGroup(order, s);
                                        } else
                                        {
                                            String s13 = formatUserMessage("unknownRelationshipType", dynamohttpservletrequest, dynamohttpservletresponse);
                                            String s15 = generatePropertyPath("relationshipType");
                                            addFormException(new DropletFormException(s13, s15, "unknownRelationshipType"));
                                            return false;
                                        }
        }
        catch(CommerceException commerceexception)
        {
            if(isLoggingError())
            {
                logError(commerceexception);
            }
            String s16 = formatUserMessage("noPaymentRelationshipCreated", dynamohttpservletrequest, dynamohttpservletresponse);
            String s17 = generatePropertyPath("payment");
            addFormException(new DropletFormException(s16, s17, "noPaymentRelationshipCreated"));
            return false;
        }
        return true;
    }

    protected PaymentGroup createPaymentGroup(SimpleOrderManager simpleordermanager, Order order, String s)
    {
        PaymentGroup paymentgroup = null;
        try
        {
            paymentgroup = simpleordermanager.createPaymentGroup(s);
            simpleordermanager.addPaymentGroupToOrder(order, paymentgroup);
        }
        catch(CommerceException _ex)
        {
            if(isLoggingDebug())
            {
                logDebug("General Commerce Exception");
            }
            return null;
        }
        return paymentgroup;
    }

    protected ShippingGroup createShippingGroup(String s)
    {
        ShippingGroup shippinggroup = null;
        try
        {
            shippinggroup = getOrderManager().createShippingGroup(s);
            getOrderManager().addShippingGroupToOrder(getOrder(), shippinggroup);
        }
        catch(CommerceException _ex)
        {
            if(isLoggingDebug())
            {
                logDebug("General Commerce Exception");
            }
            shippinggroup = null;
        }
        return shippinggroup;
    }

    protected boolean deletePaymentGroup(String s)
    {
        try
        {
            getOrderManager().removePaymentGroupFromOrder(getOrder(), s);
        }
        catch(CommerceException commerceexception)
        {
            if(isLoggingError())
            {
                logError(commerceexception);
            }
            return false;
        }
        return true;
    }

    protected boolean deleteShippingGroup(String s)
    {
        try
        {
            getOrderManager().removeShippingGroupFromOrder(getOrder(), s);
        }
        catch(CommerceException _ex)
        {
            if(isLoggingError())
            {
                logError("Could not Remove: CommerceException " + s);
            }
            return false;
        }
        return true;
    }

    public String getAddItemFailureURL()
    {
        return mAddItemFailureURL;
    }

    public String getAddItemSuccessURL()
    {
        return mAddItemSuccessURL;
    }

    public List getAllCommerceItemRelationships()
    {
        if(getOrderManager() != null && getOrder() != null)
        {
            try
            {
                return getOrderManager().getAllCommerceItemRelationships(getOrder());
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
        }
        return new ArrayList(0);
    }

    public List getAllOrderRelationships()
    {
        if(getOrderManager() != null && getOrder() != null)
        {
            try
            {
                return getOrderManager().getAllOrderRelationships(getOrder());
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
        }
        return new ArrayList(0);
    }

    public List getAllPaymentGroupRelationships()
    {
        if(getOrderManager() != null && getOrder() != null)
        {
            try
            {
                return getOrderManager().getAllPaymentGroupRelationships(getOrder());
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
        }
        return new ArrayList(0);
    }

    public List getAllShippingGroupRelationships()
    {
        if(getOrderManager() != null && getOrder() != null)
        {
            try
            {
                return getOrderManager().getAllShippingGroupRelationships(getOrder());
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
        }
        return new ArrayList(0);
    }

    public double getAmount()
    {
        return mAmount;
    }

    public String getCancelCancelFailureURL()
    {
        return mCancelCancelFailureURL;
    }

    public String getCancelCancelSuccessURL()
    {
        return mCancelCancelSuccessURL;
    }

    public String getCancelOrderFailureURL()
    {
        return mCancelOrderFailureURL;
    }

    public String getCancelOrderSuccessURL()
    {
        return mCancelOrderSuccessURL;
    }

    public String getCatalogRefId()
    {
        return mCatalogRefId;
    }

    public ClaimableManager getClaimableManager()
    {
        return mClaimableManager;
    }

    public String getCommerceItemId()
    {
        return mCommerceItemId;
    }

    public String getConfirmCancelFailureURL()
    {
        return mConfirmCancelFailureURL;
    }

    public String getConfirmCancelSuccessURL()
    {
        return mConfirmCancelSuccessURL;
    }

    public String getCreatePaymentGroupFailureURL()
    {
        return mCreatePaymentGroupFailureURL;
    }

    public String getCreatePaymentGroupSuccessURL()
    {
        return mCreatePaymentGroupSuccessURL;
    }

    public String getCreateShippingGroupFailureURL()
    {
        return mCreateShippingGroupFailureURL;
    }

    public String getCreateShippingGroupSuccessURL()
    {
        return mCreateShippingGroupSuccessURL;
    }

    public String getCreditCardNumber()
    {
        return mCreditCardNumber;
    }

    public String getCreditCardType()
    {
        return mCreditCardType;
    }

    public Transaction getCurrentTransaction()
    {
        return mCurrentTransaction;
    }

    public String getDoNotSaveOrderFailureURL()
    {
        return mDoNotSaveOrderFailureURL;
    }

    public String getDoNotSaveOrderSuccessURL()
    {
        return mDoNotSaveOrderSuccessURL;
    }

    public String getExpirationDayOfMonth()
    {
        return mExpirationDayOfMonth;
    }

    public String getExpirationMonth()
    {
        return mExpirationMonth;
    }

    public String getExpirationYear()
    {
        return mExpirationYear;
    }

    public String getForwardFailureURL()
    {
        return mForwardFailureURL;
    }

    public String getForwardSuccessURL()
    {
        return mForwardSuccessURL;
    }

    public String getGiftCertificateNumber()
    {
        return mGiftCertificateNumber;
    }

    public double getItemDue()
    {
        return mItemDue;
    }

    public String getLoadOrderFailureURL()
    {
        return mLoadOrderFailureURL;
    }

    public String getLoadOrderSuccessURL()
    {
        return mLoadOrderSuccessURL;
    }

    public MessageDispatcher getMessageDispatcher()
    {
        return mMessageDispatcher;
    }

    public CreditCard getNewCreditCard()
    {
        CreditCard creditcard = null;
        try
        {
            creditcard = (CreditCard)getOrderManager().createPaymentGroup("creditCard");
        }
        catch(CommerceException commerceexception)
        {
            creditcard = null;
            if(isLoggingError())
            {
                logError(commerceexception);
            }
        }
        return creditcard;
    }

    public GiftCertificate getNewGiftCertificate()
    {
        GiftCertificate giftcertificate = null;
        try
        {
            giftcertificate = (GiftCertificate)getOrderManager().createPaymentGroup("giftCertificate");
        }
        catch(CommerceException commerceexception)
        {
            giftcertificate = null;
            if(isLoggingError())
            {
                logError(commerceexception);
            }
        }
        return giftcertificate;
    }

    public HardgoodShippingGroup getNewHardgoodShippingGroup()
    {
        HardgoodShippingGroup hardgoodshippinggroup = null;
        try
        {
            hardgoodshippinggroup = (HardgoodShippingGroup)getOrderManager().createShippingGroup("hardgoodShippingGroup");
        }
        catch(CommerceException commerceexception)
        {
            hardgoodshippinggroup = null;
            if(isLoggingError())
            {
                logError(commerceexception);
            }
        }
        return hardgoodshippinggroup;
    }

    public OrderUpdateNotification getNotificationMessage()
    {
        return mNotificationMessage;
    }

    protected List getOrderComponentsIds(Order order)
    throws CommerceException
    {
        if(order == null)
        {
            throw new CommerceException();
        }
        Vector vector = new Vector(3, 3);
        Vector vector1 = new Vector(order.getPaymentGroups());
        Vector vector2 = new Vector(order.getShippingGroups());
        Vector vector3 = new Vector(order.getCommerceItems());
        for(int i = 0; i < vector1.size(); i++)
        {
            vector.add(((PaymentGroup)vector1.elementAt(i)).getId());
        }

        for(int j = 0; j < vector2.size(); j++)
        {
            vector.add(((ShippingGroup)vector2.elementAt(j)).getId());
        }

        for(int k = 0; k < vector3.size(); k++)
        {
            vector.add(((CommerceItem)vector3.elementAt(k)).getId());
        }

        vector.add(order.getId());
        if(isLoggingDebug())
        {
            logDebug("Complete Set of Component Ids: " + vector);
        }
        return vector;
    }

    public double getOrderDue()
    {
        return mOrderDue;
    }

    public String getOrderId()
    {
        return mOrderId;
    }

    public String getOrderLockedURL()
    {
        return mOrderLockedURL;
    }

    public Vector getOrderValidationErrors()
    {
        if(mOrderValidationErrors == null)
        {
            mOrderValidationErrors = new Vector();
        }
        return mOrderValidationErrors;
    }

    public String[] getOrderValidationErrorsAsStringArray()
    {
        if(mOrderValidationErrors == null)
        {
            mOrderValidationErrors = new Vector();
        }
        return (String[])mOrderValidationErrors.toArray(new String[0]);
    }

    public String getPaymentGroupId()
    {
        return mPaymentGroupId;
    }

    public PaymentGroupStates getPaymentGroupStates()
    {
        return mPaymentGroupStates;
    }

    public PaymentManager getPaymentManager()
    {
        return mPaymentManager;
    }

    public PricingTools getPricingTools()
    {
        return mPricingTools;
    }

    public ProfileTools getProfileTools()
    {
        return mProfileTools;
    }

    public PromotionTools getPromotionTools()
    {
        return mPromotionTools;
    }

    public String getRelationshipId()
    {
        return mRelationshipId;
    }

    public String getRelationshipTypeAsString()
    {
        return mRelationshipTypeAsString;
    }

    public String getSaveOrderFailureURL()
    {
        return mSaveOrderFailureURL;
    }

    public String getSaveOrderSuccessURL()
    {
        return mSaveOrderSuccessURL;
    }

    public double getShippingDue()
    {
        return mShippingDue;
    }

    public String getShippingGroupId()
    {
        return mShippingGroupId;
    }

    public ShippingGroupStates getShippingGroupStates()
    {
        return mShippingGroupStates;
    }

    public String getShippingMethod()
    {
        return mShippingMethod;
    }

    public double getTaxDue()
    {
        return mTaxDue;
    }

    public String getUpdatePaymentGroupFailureURL()
    {
        return mUpdatePaymentGroupFailureURL;
    }

    public String getUpdateShippingGroupFailureURL()
    {
        return mUpdateShippingGroupFailureURL;
    }

    public boolean handleAddItemToOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getAddItemFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            Order order = getOrder();
            if(order == null)
            {
                String s1 = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            String s2 = getCatalogRefId();
            if(s2 == null || s2.trim().length() == 0)
            {
                String s3 = formatUserMessage("noItemsToAdd", dynamohttpservletrequest, dynamohttpservletresponse);
                String s5 = generatePropertyPath("catalogRefId");
                addFormException(new DropletFormException(s3, s5, "noItemsToAdd"));
            }
            String s4 = getProductId();
            if(s4 == null || s4.trim().length() == 0)
            {
                String s6 = formatUserMessage("noItemsToAdd", dynamohttpservletrequest, dynamohttpservletresponse);
                String s8 = generatePropertyPath("productId");
                addFormException(new DropletFormException(s6, s8, "noItemsToAdd"));
            }
            String s7 = getShippingGroupId();
            if(s7 == null || s7.trim().length() == 0)
            {
                String s9 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s10 = generatePropertyPath("shippingId");
                addFormException(new DropletFormException(s9, s10, "noGroupId"));
            }
            long l = getQuantity();
            if(l <= 0L)
            {
                String s11 = formatUserMessage("quantityLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s12 = generatePropertyPath("quantity");
                addFormException(new DropletFormException(s11, s12, "quantityLessThanOrEqualToZero"));
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s13 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s13);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s14 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s14);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s15 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s15);
            }
            if(!checkFormRedirect(null, getAddItemFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            try
            {
                if(isLoggingDebug())
                {
                    logDebug("Adding item to order [" + order.getId() + "] " + l + " of " + s2 + "to ship to " + s7);
                }
                CommerceItem commerceitem = simpleordermanager.addItemToShippingGroup(order, s2, s4, l, s7);
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s18 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s21 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s18, s21, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    if(commerceitem.getQuantity() > l)
                    {
                        orderupdatenotification.addItemQuantityUpdate(commerceitem.getId(), commerceitem.getQuantity());
                    }
                    else
                    {
                        orderupdatenotification.addItemAdd(order.getId(), commerceitem.getId());
                    }
                    orderupdatenotification.addShippingGroupUpdate(s7);
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception)
                {
                    logError("===>"+this.getClass()+".handleAddItemToOrder : ERROR4"+commerceexception.toString());
                    String s22 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
//                  commerceexception.printStackTrace();
                    throw new ServletException(s22 + commerceexception.getMessage());
                }
            }
            catch(CommerceItemNotFoundException _ex)
            {
                logError("===>"+this.getClass()+".handleAddItemToOrder : ERROR5"+_ex.toString());
                String s16 = formatUserMessage("badCatalogRefId", s2, s4, dynamohttpservletrequest, dynamohttpservletresponse);
                String s19 = generatePropertyPath(".catalogRefId");
                addFormException(new DropletFormException(s16, s19, "errorAddingToOrder"));
            }
            catch(CommerceException _ex)
            {
                logError("===>"+this.getClass()+".handleAddItemToOrder : ERROR6"+_ex.toString());
                String s17 = formatUserMessage("errorAddingToOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s20 = generatePropertyPath(".catalogRefId");
                addFormException(new DropletFormException(s17, s20, "errorAddingToOrder"));
            }
            boolean flag2 = checkFormRedirect(getAddItemSuccessURL(), getAddItemFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag2;
        }
        catch (Exception ex)
        {
            logError("===>"+this.getClass()+".handleAddItemToOrder : ERROR3"+ex.toString());
            return false;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    /**.
     * Modification G.P. 06/08/07
     * Envoie un message au fulfillment a priori pour dire
     * que la commande est à supprimer/supprimée...
     * @param       a_request           Dynamohttpservletrequest
     * @param       a_response          Dynamohttpservletresponse
     * @return      boolean             Retour
     * @throws      ServletException    ServletException
     * @throws      IOException         IOException
     */
    public boolean handleCancelOrder(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
    throws ServletException, IOException
    {
        Transaction l_transaction = null;
        try
        {
            l_transaction = ensureTransaction();
            Order l_order = getOrder();
            if (l_order == null)
            {
                String l_noOrder = formatUserMessage("noOrderToModify", a_request, a_response);
                throw new ServletException(l_noOrder);
            }
            if (!checkFormRedirect(getCancelOrderSuccessURL(), getCancelOrderFailureURL(), a_request, a_response))
            {
                if (isLoggingError() && getFormExceptions() != null && getFormExceptions().size() > 0)
                {
                    logError((Exception) getFormExceptions().get(0));
                }
                
                return false;
            }
            if (isLoggingDebug())
            {
                logDebug("Canceling Order " + l_order + " ...");
            }
            if (mMessageDispatcher != null)
            {
                mMessageDispatcher.sendCancelOrder(l_order.getId());
            }
            else
            {
                String l_s1 = formatUserMessage("errorCancelingOrder", l_order.getId(), a_request, a_response);
                String l_s2 = generatePropertyPath("messageDispatcher");
                addFormException(new DropletFormException(l_s1, l_s2, "errorCancelingOrder"));
                
                if (isLoggingError())
                {
                    logError(new DropletFormException(l_s1, l_s2, "errorCancelingOrder"));
                }
            }

            return checkFormRedirect(getCancelOrderSuccessURL(), getCancelOrderFailureURL(), a_request, a_response);
        }
        catch (Exception l_exception)
        {
            if (isLoggingError())
            {
                logError(l_exception);
            }
            return false;
        }
        finally
        {
            if(l_transaction != null)
            {
                commitTransaction(l_transaction);
            }
        }
    }

    public boolean handleCreateCreditCard(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getCreatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            CreditCard creditcard = getCreditCard();
            if(creditcard == null)
            {
                String s5 = formatUserMessage("noCreditCardObject", dynamohttpservletrequest, dynamohttpservletresponse);
                String s8 = generatePropertyPath("creditCard");
                addFormException(new DropletFormException(s5, s8, "errorCreatingPaymentGroup"));
            } else
            {
                int i = CreditCardTools.verifyCreditCard(creditcard);
                if(i != 0)
                {
                    String s9 = CreditCardTools.getStatusCodeMessage(i, getDefaultLocale());
                    String s13 = generatePropertyPath("creditCardNumber");
                    addFormException(new DropletFormException(s9, s13, "errorCreatingPaymentGroup"));
                }
            }
            if(!checkFormRedirect(null, getCreatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            if(creditcard != null)
            {
                try
                {
                    simpleordermanager.addPaymentGroupToOrder(getOrder(), creditcard);
                }
                catch(CommerceException _ex)
                {
                    String s6 = formatUserMessage("errorCreatingPaymentGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s10 = generatePropertyPath("creditCard");
                    addFormException(new DropletFormException(s6, s10, "errorCreatingPaymentGroup"));
                }
                if(!createPayment(creditcard.getId(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    try
                    {
                        simpleordermanager.removePaymentGroupFromOrder(order, creditcard.getId());
                    }
                    catch(CommerceException commerceexception)
                    {
                        String s11 = generatePropertyPath("creditCard");
                        addFormException(new DropletFormException(commerceexception.getMessage(), s11, "errorCreatingPaymentGroup"));
                    }
                    if(!checkFormRedirect(null, getCreatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                    {
                        boolean flag2 = false;
                        return flag2;
                    }
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s7 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s12 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s7, s12, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupAdd(order.getId(), creditcard.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception1)
                {
                    String s14 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception1.printStackTrace();
                    throw new ServletException(s14 + commerceexception1.getMessage());
                }
            }
            setCreditCard(null);
            boolean flag3 = checkFormRedirect(getCreatePaymentGroupSuccessURL(), getCreatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag3;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleCreateGiftCertificate(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            ClaimableManager claimablemanager = getClaimableManager();
            if(claimablemanager == null)
            {
                String s1 = formatUserMessage("noClaimableManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s2 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s3 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s4 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s5 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            String s6 = getGiftCertificateNumber();
            if(s6 == null)
            {
                String s7 = formatUserMessage("noGiftCertificateNumber", dynamohttpservletrequest, dynamohttpservletresponse);
                String s8 = generatePropertyPath("giftCertificateNumber");
                addFormException(new DropletFormException(s7, s8, "errorCreatingPaymentGroup"));
            }
            GiftCertificate giftcertificate = getGiftCertificate();
            if(giftcertificate == null)
            {
                String s9 = formatUserMessage("noGiftCertificateFound", dynamohttpservletrequest, dynamohttpservletresponse);
                String s11 = generatePropertyPath("giftCertificate");
                addFormException(new DropletFormException(s9, s11, "errorCreatingPaymentGroup"));
            } else
            {
                giftcertificate.setGiftCertificateNumber(s6);
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            if(giftcertificate != null)
            {
                RepositoryItem repositoryitem = null;
                try
                {
                    repositoryitem = claimablemanager.claimItem(giftcertificate.getGiftCertificateNumber());
                }
                catch(ClaimableException claimableexception)
                {
                    if(isLoggingError())
                    {
                        logError(claimableexception);
                    }
                    String s14 = formatUserMessage("noGiftCertificateFound", giftcertificate.getGiftCertificateNumber(), dynamohttpservletrequest, dynamohttpservletresponse);
                    String s18 = generatePropertyPath("giftCertificateNumber");
                    addFormException(new DropletFormException(s14, s18, "errorCreatingPaymentGroup"));
                }
                try
                {
                    simpleordermanager.initializeGiftCertificate(giftcertificate, order.getProfileId(), giftcertificate.getGiftCertificateNumber());
                    simpleordermanager.addPaymentGroupToOrder(order, giftcertificate);
                    setAmount(((Double)repositoryitem.getPropertyValue("amountRemaining")).doubleValue());
                }
                catch(CommerceException commerceexception)
                {
                    if(isLoggingError())
                    {
                        logError(commerceexception);
                    }
                    String s15 = formatUserMessage("errorCreatingPaymentGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s19 = generatePropertyPath("GiftCertificatePaymentGroup");
                    addFormException(new DropletFormException(s15, s19, "errorCreatingPaymentGroup"));
                }
                if(!createPayment(giftcertificate.getId(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    try
                    {
                        simpleordermanager.removePaymentGroupFromOrder(order, giftcertificate.getId());
                    }
                    catch(CommerceException commerceexception1)
                    {
                        String s16 = generatePropertyPath("giftCertificate");
                        addFormException(new DropletFormException(commerceexception1.getMessage(), s16, "errorCreatingPaymentGroup"));
                    }
                    if(!checkFormRedirect(null, getCreatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                    {
                        boolean flag2 = false;
                        return flag2;
                    }
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s12 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s17 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s12, s17, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupAdd(order.getId(), giftcertificate.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception2)
                {
                    String s20 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception2.printStackTrace();
                    throw new ServletException(s20 + commerceexception2.getMessage());
                }
            } else
            {
                String s10 = formatUserMessage("noGiftCertificateFound", giftcertificate.getGiftCertificateNumber(), dynamohttpservletrequest, dynamohttpservletresponse);
                String s13 = generatePropertyPath("giftCertificateNumber");
                addFormException(new DropletFormException(s10, s13, "errorCreatingPaymentGroup"));
            }
            setGiftCertificate(null);
            setGiftCertificateNumber(null);
            boolean flag3 = checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag3;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleCreateHardgoodShippingGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getCreateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            HardgoodShippingGroup hardgoodshippinggroup = getHardgoodShippingGroup();
            if(hardgoodshippinggroup == null)
            {
                String s2 = formatUserMessage("noShippingGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                String s3 = generatePropertyPath("hardgoodShippingGroup");
                addFormException(new DropletFormException(s2, s3, "errorCreatingShippingGroup"));
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s4 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s5 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s6 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s6);
            }
            if(!checkFormRedirect(null, getCreateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            if(hardgoodshippinggroup != null)
            {
                hardgoodshippinggroup.setShippingMethod(getShippingMethod());
                try
                {
                    simpleordermanager.addShippingGroupToOrder(getOrder(), hardgoodshippinggroup);
                }
                catch(CommerceException _ex)
                {
                    String s7 = formatUserMessage("errorCreatingShippingGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s9 = generatePropertyPath("hardgoodShippingGroup");
                    addFormException(new DropletFormException(s7, s9, "errorCreatingShippingGroup"));
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s8 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s10 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s8, s10, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addShippingGroupAdd(getOrder().getId(), hardgoodshippinggroup.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception)
                {
                    String s11 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    // commerceexception.printStackTrace();
                    throw new ServletException(s11 + commerceexception.getMessage());
                }
            }
            setHardgoodShippingGroup(null);
            boolean flag2 = checkFormRedirect(getCreateShippingGroupSuccessURL(), getCreateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag2;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleCreateShippingGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getCreateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            ShippingGroup shippinggroup = createShippingGroup("shippingGroup");
            if(shippinggroup == null)
            {
                String s5 = formatUserMessage("errorCreatingShippingGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                String s7 = generatePropertyPath("shippingGroup");
                addFormException(new DropletFormException(s5, s7, "errorCreatingShippingGroup"));
            } else
            {
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s6 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s8 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s6, s8, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addShippingGroupAdd(getOrder().getId(), shippinggroup.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception)
                {
                    String s9 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    // commerceexception.printStackTrace();
                    throw new ServletException(s9 + commerceexception.getMessage());
                }
            }
            setShippingGroup(null);
            boolean flag1 = checkFormRedirect(getCreateShippingGroupSuccessURL(), getCreateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag1;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleCreditPaymentGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PaymentManager paymentmanager = getPaymentManager();
            if(simpleordermanager == null)
            {
                String s2 = formatUserMessage("noPaymentManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s3 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s4 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s5 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            String s6 = getPaymentGroupId();
            if(s6 == null)
            {
                String s7 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s8 = generatePropertyPath("paymentGroupId");
                addFormException(new DropletFormException(s7, s8, "noGroupId"));
            }
            double d = getAmount();
            if(d <= 0.0D)
            {
                String s9 = formatUserMessage("amountLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s10 = generatePropertyPath("amount");
                addFormException(new DropletFormException(s9, s10, "amountLessThanOrEqualToZero"));
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            try
            {
                PaymentGroup paymentgroup = order.getPaymentGroup(s6);
                List list = (List)((ArrayList)paymentgroup.getCreditStatus()).clone();
                try
                {
                    paymentmanager.credit(order, paymentgroup, d);
                }
                catch(PaymentException paymentexception)
                {
                    if(isLoggingInfo())
                    {
                        logInfo(paymentexception.getMessage());
                    }
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupCredit(paymentgroup.getId(), list, paymentgroup.getCreditStatus());
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s16 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s17 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s16, s17, "errorRepricingOrder"));
                }
                updateOrder(order, simpleordermanager);
                if(mMessageDispatcher != null && orderupdatenotification != null)
                {
                    mMessageDispatcher.sendOrderUpdateNotification(orderupdatenotification);
                }
            }
            catch(PaymentGroupNotFoundException paymentgroupnotfoundexception)
            {
                if(isLoggingError())
                {
                    logError(paymentgroupnotfoundexception);
                }
                String s11 = formatUserMessage("noPaymentGroup", s6, dynamohttpservletrequest, dynamohttpservletresponse);
                String s14 = generatePropertyPath("creditCard");
                addFormException(new DropletFormException(s11, s14, "noPaymentGroup"));
            }
            catch(InvalidParameterException invalidparameterexception)
            {
                if(isLoggingError())
                {
                    logError(invalidparameterexception);
                }
                String s12 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s15 = generatePropertyPath("creditCardId");
                addFormException(new DropletFormException(s12, s15, "noGroupId"));
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                String s13 = generatePropertyPath("credit");
                addFormException(new DropletFormException(commerceexception.getMessage(), s13, "errorCrediting"));
            }
            boolean flag1 = checkFormRedirect(getForwardSuccessURL(), getForwardFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag1;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleDeletePaymentGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            String s5 = getPaymentGroupId();
            if(s5 == null)
            {
                String s6 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s9 = generatePropertyPath("paymentGroupId");
                addFormException(new DropletFormException(s6, s9, "noGroupId"));
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            if(deletePaymentGroup(s5))
            {
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s7 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s10 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s7, s10, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupDelete(order.getId(), s5);
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception)
                {
                    String s12 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception.printStackTrace();
                    throw new ServletException(s12 + commerceexception.getMessage());
                }
            } else
            {
                String s8 = formatUserMessage("errorDeletingPaymentGroup", s5, dynamohttpservletrequest, dynamohttpservletresponse);
                String s11 = generatePropertyPath("paymentGroup");
                addFormException(new DropletFormException(s8, s11, "errorDeletingPaymentGroup"));
            }
            setPaymentGroupId(null);
            boolean flag1 = checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag1;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }

        }
    }

    public boolean handleDeleteShippingGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            String s2 = getShippingGroupId();
            if(s2 == null || s2.trim().length() == 0)
            {
                String s3 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s4 = generatePropertyPath("shippingGroupId");
                addFormException(new DropletFormException(s3, s4, "noGroupId"));
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s5 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s6 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s6);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s7 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s7);
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            if(deleteShippingGroup(s2))
            {
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s8 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s10 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s8, s10, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addShippingGroupDelete(order.getId(), s2);
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception)
                {
                    String s12 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception.printStackTrace();
                    throw new ServletException(s12 + commerceexception.getMessage());
                }
            } else
            {
                String s9 = formatUserMessage("errorDeletingShippingGroup", s2, dynamohttpservletrequest, dynamohttpservletresponse);
                String s11 = generatePropertyPath("shippingGroupId");
                addFormException(new DropletFormException(s9, s11, "errorDeletingShippingGroup"));
            }
            setShippingGroupId(null);
            boolean flag2 = checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag2;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleDoNotSaveOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getDoNotSaveOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            if(isLoggingDebug())
            {
                logDebug("Unloading the Order w/o Saving " + getOrder());
            }
            setOrder(null);
            boolean flag1 = checkFormRedirect(getDoNotSaveOrderSuccessURL(), null, dynamohttpservletrequest, dynamohttpservletresponse);
            return flag1;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleForward(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        return checkFormRedirect(getForwardSuccessURL(), getForwardFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
    }

    public boolean handleLoadOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            String s = getOrderId();
            if(s == null || s.trim().length() == 0)
            {
                String s1 = formatUserMessage("noOrderId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s3 = generatePropertyPath("orderId");
                addFormException(new DropletFormException(s1, s3, "noOrderId"));
            }
            if(!checkFormRedirect(null, getLoadOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            if(isLoggingDebug())
            {
                logDebug("Attempting to Load Order #" + s);
            }
            if(!loadOrder(s))
            {
                String s2 = formatUserMessage("noOrderToModify", s, dynamohttpservletrequest, dynamohttpservletresponse);
                String s4 = generatePropertyPath("order");
                addFormException(new DropletFormException(s2, s4, "noOrderToModify"));
            }
            boolean flag1 = checkFormRedirect(getLoadOrderSuccessURL(), getLoadOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag1;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handlePriceOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
            {
                String s5 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s6 = generatePropertyPath("price:priceOrder");
                addFormException(new DropletFormException(s5, s6, "errorRepricingOrder"));
            }
            try
            {
                PipelineResult pipelineresult = simpleordermanager.validateOrder(order);
                setOrderValidationErrors(pipelineresult);
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                String s7 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s9 = generatePropertyPath("validateOrder");
                addFormException(new DropletFormException(s7, s9, "errorRepricingOrder"));
            }
            try
            {
                updateOrder(order, simpleordermanager);
            }
            catch(CommerceException commerceexception1)
            {
                String s8 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                //commerceexception1.printStackTrace();
                throw new ServletException(s8 + commerceexception1.getMessage());
            }
            boolean flag = checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            String s2 = getCommerceItemId();
            if(s2 == null || s2.trim().length() == 0)
            {
                String s3 = formatUserMessage("noItemsChosen", dynamohttpservletrequest, dynamohttpservletresponse);
                String s4 = generatePropertyPath("commerceItemId");
                addFormException(new DropletFormException(s3, s4, "noItemsToAdd"));
            }
            long l = getQuantity();
            if(l <= 0L)
            {
                String s5 = formatUserMessage("quantityLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s7 = generatePropertyPath("quantity");
                addFormException(new DropletFormException(s5, s7, "quantityLessThanOrEqualToZero"));
            }
            String s6 = getShippingGroupId();
            if(s6 == null || s6.trim().length() == 0)
            {
                String s8 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s9 = generatePropertyPath("shippingId");
                addFormException(new DropletFormException(s8, s9, "noGroupId"));
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s10 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s10);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s11 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s11);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s12 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s12);
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            try
            {
                simpleordermanager.removeItemFromShippingGroup(order, s2, s6, l);
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s13 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s14 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s13, s14, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    try
                    {
                        CommerceItem commerceitem = order.getCommerceItem(s2);
                        orderupdatenotification.addItemQuantityUpdate(s2, commerceitem.getQuantity());
                    }
                    catch(CommerceException _ex)
                    {
                        orderupdatenotification.addItemDelete(order.getId(), s2);
                    }
                    orderupdatenotification.addShippingGroupUpdate(s6);
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception1)
                {
                    String s16 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    // commerceexception1.printStackTrace();
                    throw new ServletException(s16 + commerceexception1.getMessage());
                }
            }
            catch(CommerceException commerceexception)
            {
                String s15 = formatUserMessage("errorRemovingItemFromOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s17 = generatePropertyPath("catalogRefId");
                addFormException(new DropletFormException(s15, s17, "errorRemovingItemFromOrder"));
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
            boolean flag2 = checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag2;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleResetFormErrors(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        if(isLoggingDebug())
        {
            logDebug("Resetting Form Errors");
        }
        resetFormExceptions();
        return true;
    }

    public boolean handleResetOrderValidationErrors(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        if(isLoggingDebug())
        {
            logDebug("Resetting Order Validation Errors");
        }
        resetOrderValidationErrors();
        return true;
    }

    public boolean handleSaveOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getSaveOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
            {
                String s5 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s8 = generatePropertyPath("price:saveOrder");
                addFormException(new DropletFormException(s5, s8, "errorRepricingOrder"));
            }
            try
            {
                PipelineResult pipelineresult = simpleordermanager.validateOrder(order);
                setOrderValidationErrors(pipelineresult);
            }
            catch(CommerceException _ex)
            {
                String s6 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s9 = generatePropertyPath("orderValidation");
                addFormException(new DropletFormException(s6, s9, "errorRepricingOrder"));
            }
            if(isOrderValidationError())
            {
                String s7 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s10 = generatePropertyPath("orderValidation");
                addFormException(new DropletFormException(s7, s10, "errorUpdatingOrder"));
                boolean flag1 = checkFormRedirect(getSaveOrderSuccessURL(), getSaveOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
                return flag1;
            }
            if(!checkFormRedirect(null, getSaveOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag2 = false;
                return flag2;
            }
            try
            {
                updateOrder(order, simpleordermanager);
                if(mMessageDispatcher != null)
                {
                    OrderUpdateNotification orderupdatenotification = removeUnrelatedMessages();
                    if(orderupdatenotification != null && !orderupdatenotification.isEmpty())
                    {
                        mMessageDispatcher.sendOrderUpdateNotification(orderupdatenotification);
                    }
                }
                setOrder(null);
            }
            catch(CommerceException commerceexception)
            {
                String s11 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                String s12 = generatePropertyPath("order");
                addFormException(new DropletFormException(s11, s12, "errorUpdatingOrder"));
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
            }
            //boolean flag3 = checkFormRedirect(getSaveOrderSuccessURL(), getSaveOrderFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return true;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleSetOrder(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        return false;
    }

    public boolean handleUpdateCreditCard(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            String s1 = getPaymentGroupId();
            if(s1 == null)
            {
                String s2 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s4 = generatePropertyPath("paymentGroupId");
                addFormException(new DropletFormException(s2, s4, "noGroupId"));
            }
            String s3 = getRelationshipId();
            if(s3 == null || s3.trim().length() == 0)
            {
                String s5 = formatUserMessage("noRelationshipId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s6 = generatePropertyPath("relationshipId");
                addFormException(new DropletFormException(s5, s6, "noRelationshipId"));
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s7 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s7);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s8 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s8);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s9 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s9);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s10 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s10);
            }
            if(!checkFormRedirect(null, getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            try
            {
                CreditCard creditcard = (CreditCard)order.getPaymentGroup(s1);
                if(creditcard == null)
                {
                    String s11 = formatUserMessage("noPaymentGroup", s1, dynamohttpservletrequest, dynamohttpservletresponse);
                    String s16 = generatePropertyPath("creditCard");
                    addFormException(new DropletFormException(s11, s16, "errorUpdatingPaymentGroup"));
                }
                int i = CreditCardTools.verifyCreditCard(creditcard);
                if(i != 0)
                {
                    String s17 = CreditCardTools.getStatusCodeMessage(i, getDefaultLocale());
                    String s22 = generatePropertyPath("creditCardNumber");
                    addFormException(new DropletFormException(s17, s22, "errorUpdatingPaymentGroup"));
                }
                if(!checkFormRedirect(null, getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    boolean flag1 = false;
                    return flag1;
                }
                Relationship relationship = order.getRelationship(s3);
                if(relationship instanceof PaymentGroupRelationship)
                {
                    if(((PaymentGroupRelationship)relationship).getPaymentGroup().getId() == creditcard.getId())
                    {
                        updatePaymentGroupRelationship((PaymentGroupRelationship)relationship, dynamohttpservletrequest, dynamohttpservletresponse);
                    }
                    else
                    {
                        throw new ServletException("Wrong Payment Group");
                    }
                }
                PipelineResult pipelineresult = simpleordermanager.validateOrder(order);
                setOrderValidationErrors(pipelineresult);
                if(pipelineresult.hasErrors())
                {
                    try
                    {
                        setTransactionToRollbackOnly();
                    }
                    catch(SystemException systemexception)
                    {
                        String s24 = generatePropertyPath("creditCard");
                        addFormException(new DropletFormException(systemexception.getMessage(), s24, "errorUpdatingOrder"));
                        boolean flag2 = false;
                        return flag2;
                    }
                }
                if(!checkFormRedirect(null, getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    boolean flag3 = false;
                    return flag3;
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s23 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s25 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s23, s25, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupUpdate(creditcard.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception1)
                {
                    String s26 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception1.printStackTrace();
                    throw new ServletException(s26 + commerceexception1.getMessage());
                }
                if(!checkFormRedirect(null, getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    boolean flag4 = false;
                    return flag4;
                }
            }
            catch(PaymentGroupNotFoundException paymentgroupnotfoundexception)
            {
                if(isLoggingError())
                {
                    logError(paymentgroupnotfoundexception);
                }
                String s12 = formatUserMessage("noPaymentGroup", s1, dynamohttpservletrequest, dynamohttpservletresponse);
                String s18 = generatePropertyPath("creditCard");
                addFormException(new DropletFormException(s12, s18, "noPaymentGroup"));
            }
            catch(InvalidParameterException invalidparameterexception)
            {
                if(isLoggingError())
                {
                    logError(invalidparameterexception);
                }
                String s13 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s19 = generatePropertyPath("creditCardId");
                addFormException(new DropletFormException(s13, s19, "noGroupId"));
            }
            catch(RelationshipNotFoundException relationshipnotfoundexception)
            {
                if(isLoggingError())
                {
                    logError(relationshipnotfoundexception);
                }
                String s14 = formatUserMessage("noRelationship", dynamohttpservletrequest, dynamohttpservletresponse);
                String s20 = generatePropertyPath("relationship");
                addFormException(new DropletFormException(s14, s20, "noRelationship"));
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                String s15 = formatUserMessage("errorUpdatingPaymentGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                String s21 = generatePropertyPath("creditCard");
                addFormException(new DropletFormException(s15, s21, "errorUpdatingPaymentGroup"));
            }
            setPaymentGroupId(null);
            setRelationshipId(null);
            boolean flag5 = checkFormRedirect(getGeneralSuccessURL(), getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag5;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleUpdateGiftCertificate(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            String s1 = getPaymentGroupId();
            if(s1 == null)
            {
                String s2 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s3 = generatePropertyPath("paymentGroupId");
                addFormException(new DropletFormException(s2, s3, "noGroupId"));
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s4 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s5 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s6 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s6);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s7 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s7);
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            try
            {
                GiftCertificate giftcertificate = (GiftCertificate)getOrder().getPaymentGroup(s1);
                PipelineResult pipelineresult = simpleordermanager.validateOrder(order);
                setOrderValidationErrors(pipelineresult);
                if(pipelineresult.hasErrors())
                {
                    try
                    {
                        setTransactionToRollbackOnly();
                    }
                    catch(SystemException systemexception)
                    {
                        String s15 = generatePropertyPath("giftCertificate");
                        addFormException(new DropletFormException(systemexception.getMessage(), s15, "errorUpdatingOrder"));
                        boolean flag1 = false;
                        return flag1;
                    }
                }
                if(!checkFormRedirect(null, getUpdatePaymentGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    boolean flag2 = false;
                    return flag2;
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s11 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s16 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s11, s16, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addPaymentGroupUpdate(s1);
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception1)
                {
                    String s17 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    //commerceexception1.printStackTrace();
                    throw new ServletException(s17 + commerceexception1.getMessage());
                }
            }
            catch(PaymentGroupNotFoundException paymentgroupnotfoundexception)
            {
                if(isLoggingError())
                {
                    logError(paymentgroupnotfoundexception);
                }
                String s8 = formatUserMessage("noPaymentGroup", s1, dynamohttpservletrequest, dynamohttpservletresponse);
                String s12 = generatePropertyPath("paymentGroup");
                addFormException(new DropletFormException(s8, s12, "noPaymentGroup"));
            }
            catch(InvalidParameterException invalidparameterexception)
            {
                if(isLoggingError())
                {
                    logError(invalidparameterexception);
                }
                String s9 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s13 = generatePropertyPath("paymentGroupId");
                addFormException(new DropletFormException(s9, s13, "noGroupId"));
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                String s10 = formatUserMessage("errorUpdatingPaymentGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                String s14 = generatePropertyPath("giftCertificate");
                addFormException(new DropletFormException(s10, s14, "errorUpdatingPaymentGroup"));
            }
            setPaymentGroupId(null);
            boolean flag3 = checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag3;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleUpdateRelationship(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s2 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s2);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s3 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s3);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s4 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s4);
            }
            String s5 = getRelationshipId();
            if(s5 == null || s5.trim().length() == 0)
            {
                String s6 = formatUserMessage("noRelationshipId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s7 = generatePropertyPath("relationshipId");
                addFormException(new DropletFormException(s6, s7, "noRelationshipId"));
            }
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            try
            {
                Relationship relationship = order.getRelationship(s5);
                if(relationship instanceof PaymentGroupRelationship)
                {
                    if(updatePaymentGroupRelationship((PaymentGroupRelationship)relationship, dynamohttpservletrequest, dynamohttpservletresponse))
                    {
                        if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                        {
                            String s8 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                            String s13 = generatePropertyPath("price:saveOrder");
                            addFormException(new DropletFormException(s8, s13, "errorRepricingOrder"));
                        }
                        OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                        if(orderupdatenotification != null)
                        {
                            orderupdatenotification.addPaymentGroupUpdate(((PaymentGroupRelationship)relationship).getPaymentGroup().getId());
                        }
                        try
                        {
                            updateOrder(order, simpleordermanager);
                        }
                        catch(CommerceException commerceexception)
                        {
                            String s18 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                            //commerceexception.printStackTrace();
                            throw new ServletException(s18 + commerceexception.getMessage());
                        }
                    }
                } else
                    if(relationship instanceof ShippingGroupCommerceItemRelationship)
                    {
                        if(updateShippingGroupCommerceItemRelationship((ShippingGroupCommerceItemRelationship)relationship, getShippingGroupId(), dynamohttpservletrequest, dynamohttpservletresponse))
                        {
                            if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                            {
                                String s9 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                                String s14 = generatePropertyPath("price:saveOrder");
                                addFormException(new DropletFormException(s9, s14, "errorRepricingOrder"));
                            }
                            OrderUpdateNotification orderupdatenotification1 = getNotificationMessage();
                            if(orderupdatenotification1 != null)
                            {
                                orderupdatenotification1.addShippingGroupUpdate(((ShippingGroupRelationship)relationship).getShippingGroup().getId());
                                orderupdatenotification1.addShippingGroupUpdate(getShippingGroupId());
                            }
                            try
                            {
                                updateOrder(order, simpleordermanager);
                            }
                            catch(CommerceException commerceexception1)
                            {
                                String s19 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                                // commerceexception1.printStackTrace();
                                throw new ServletException(s19 + commerceexception1.getMessage());
                            }
                        }
                    } else
                    {
                        String s10 = formatUserMessage("unknownRelationshipType", dynamohttpservletrequest, dynamohttpservletresponse);
                        String s15 = generatePropertyPath("relationshipType");
                        addFormException(new DropletFormException(s10, s15, "unknownRelationshipType"));
                    }
            }
            /*catch(RelationshipNotFoundException relationshipnotfoundexception)
            {
                if(isLoggingError())
                    {
                    logError(relationshipnotfoundexception.toString());
                    }
                String s11 = formatUserMessage("noRelationship", s5, dynamohttpservletrequest, dynamohttpservletresponse);
                String s16 = generatePropertyPath("relationshipId");
                addFormException(new DropletFormException(s11, s16, "noRelationship"));
            }
            catch(InvalidParameterException invalidparameterexception)
            {
                if(isLoggingError())
                {
                    logError(invalidparameterexception.toString());
                }
                String s12 = formatUserMessage("noRelationshipId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s17 = generatePropertyPath("relationshipId");
                addFormException(new DropletFormException(s12, s17, "noRelationshipId"));
            }*/
            catch (Exception e)
            {
                if(isLoggingError())
                {
                    logError(e.toString());
                }
            }
            boolean flag2 = checkFormRedirect(getGeneralSuccessURL(), getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag2;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    public boolean handleUpdateShippingGroup(DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws ServletException, IOException
    {
        Transaction transaction = null;
        try
        {
            transaction = ensureTransaction();
            if(!checkFormRedirect(null, getGeneralFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag = false;
                return flag;
            }
            Order order = getOrder();
            if(order == null)
            {
                String s = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s);
            }
            SimpleOrderManager simpleordermanager = getOrderManager();
            if(simpleordermanager == null)
            {
                String s1 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s1);
            }
            String s2 = getShippingGroupId();
            if(s2 == null || s2.trim().length() == 0)
            {
                String s3 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s4 = generatePropertyPath("shippingGroupId");
                addFormException(new DropletFormException(s3, s4, "noGroupId"));
            }
            PricingTools pricingtools = getPricingTools();
            if(pricingtools == null)
            {
                String s5 = formatUserMessage("noPricingTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s5);
            }
            ProfileTools profiletools = getProfileTools();
            if(profiletools == null)
            {
                String s6 = formatUserMessage("noProfileTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s6);
            }
            PromotionTools promotiontools = getPromotionTools();
            if(promotiontools == null)
            {
                String s7 = formatUserMessage("noPromoTools", dynamohttpservletrequest, dynamohttpservletresponse);
                throw new ServletException(s7);
            }
            if(!checkFormRedirect(null, getUpdateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
            {
                boolean flag1 = false;
                return flag1;
            }
            try
            {
                ShippingGroup shippinggroup = order.getShippingGroup(s2);
                PipelineResult pipelineresult = simpleordermanager.validateOrder(order);
                setOrderValidationErrors(pipelineresult);
                if(pipelineresult.hasErrors())
                {
                    try
                    {
                        setTransactionToRollbackOnly();
                    }
                    catch(SystemException systemexception)
                    {
                        String s15 = generatePropertyPath("shippingGroup");
                        addFormException(new DropletFormException(systemexception.getMessage(), s15, "errorUpdatingOrder"));
                        boolean flag2 = false;
                        return flag2;
                    }
                }
                if(!checkFormRedirect(null, getUpdateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse))
                {
                    boolean flag3 = false;
                    return flag3;
                }
                if(!repriceOrder(order, pricingtools, profiletools, promotiontools))
                {
                    String s11 = formatUserMessage("errorRepricingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s16 = generatePropertyPath("price:saveOrder");
                    addFormException(new DropletFormException(s11, s16, "errorRepricingOrder"));
                }
                OrderUpdateNotification orderupdatenotification = getNotificationMessage();
                if(orderupdatenotification != null)
                {
                    orderupdatenotification.addShippingGroupUpdate(shippinggroup.getId());
                }
                try
                {
                    updateOrder(order, simpleordermanager);
                }
                catch(CommerceException commerceexception1)
                {
                    String s17 = formatUserMessage("errorUpdatingOrder", dynamohttpservletrequest, dynamohttpservletresponse);
                    // commerceexception1.printStackTrace();
                    throw new ServletException(s17 + commerceexception1.getMessage());
                }
            }
            catch(ShippingGroupNotFoundException shippinggroupnotfoundexception)
            {
                if(isLoggingError())
                {
                    logError(shippinggroupnotfoundexception);
                }
                String s8 = formatUserMessage("noShippingGroup", s2, dynamohttpservletrequest, dynamohttpservletresponse);
                String s12 = generatePropertyPath("shippingGroup");
                addFormException(new DropletFormException(s8, s12, "noShippingGroup"));
            }
            catch(InvalidParameterException invalidparameterexception)
            {
                if(isLoggingError())
                {
                    logError(invalidparameterexception);
                }
                String s9 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s13 = generatePropertyPath("shippingGroupId");
                addFormException(new DropletFormException(s9, s13, "noGroupId"));
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                String s10 = formatUserMessage("errorUpdatingShippingGroup", dynamohttpservletrequest, dynamohttpservletresponse);
                String s14 = generatePropertyPath("shippingGroup");
                addFormException(new DropletFormException(s10, s14, "errorUpdatingShippingGroup"));
            }
            setShippingGroupId(null);
            boolean flag4 = checkFormRedirect(getGeneralSuccessURL(), getUpdateShippingGroupFailureURL(), dynamohttpservletrequest, dynamohttpservletresponse);
            return flag4;
        }
        finally
        {
            if(transaction != null)
            {
                commitTransaction(transaction);
            }
        }
    }

    final boolean haveId(String s, String as[])
    {
        if(as != null && s != null)
        {
            int i = as.length;
            for(int j = 0; j < i; j++)
            {
                if(s.equals(as[j]))
                {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean isOrderValidationError()
    {
        return mOrderValidationError;
    }

    protected boolean loadOrder(String s)
    {
        Object obj = null;
        if(getOrderManager() != null && s != null)
        {
            Order order;
            try
            {
                order = getOrderManager().loadOrder(s);
            }
            catch(CommerceException commerceexception)
            {
                if(isLoggingError())
                {
                    logError(commerceexception);
                }
                return false;
            }
            setOrder(order);
            if(mMessageDispatcher != null)
            {
                mNotificationMessage = new OrderUpdateNotification();
                mNotificationMessage.setOrderId(order.getId());
            }
        }
        return true;
    }

    protected String[] makePerfectSublist(String as[], List list)
    {
        Vector vector = new Vector(1, 1);
        for(int i = 0; i < as.length; i++)
        {
            if(!list.contains(as[i]))
            {
                vector.add(as[i]);
            }
        }

        if(isLoggingDebug())
        {
            logDebug("Removing " + vector + " from " + as);
        }
        if(vector.size() > 0)
        {
            Vector vector1 = new Vector(as.length);
            for(int j = 0; j < as.length; j++)
            {
                if(!vector.contains(as[j]))
                {
                    vector1.add(as[j]);
                }
            }

            return (String[])vector1.toArray(new String[0]);
        } else
        {
            return as;
        }
    }

    protected OrderUpdateNotification removeUnrelatedMessages()
    {
        OrderUpdateNotification orderupdatenotification = getNotificationMessage();
        if(orderupdatenotification == null || orderupdatenotification.isEmpty())
        {
            return orderupdatenotification;
        }
        atg.commerce.fulfillment.Modification amodification[] = orderupdatenotification.getModifications();
        if(isLoggingDebug())
        {
            logDebug("Length of the Initial List of Modifications: " + amodification.length);
        }
        List list = null;
        try
        {
            list = getOrderComponentsIds(getOrder());
        }
        catch(CommerceException _ex)
        {
            if(isLoggingWarning())
            {
                logWarning("Cannot Remove Unrelation Modificaiton Messages, no Order Found");
            }
            return orderupdatenotification;
        }
        Vector vector = new Vector(1, 1);
        for(int i = 0; i < amodification.length; i++)
        {
            if((amodification[i] instanceof GenericAdd) || (amodification[i] instanceof GenericUpdate))
            {
                if(isLoggingDebug())
                {
                    logDebug("Generic Mod " + amodification[i]);
                }
                if(!list.contains(((IdTargetModification)amodification[i]).getTargetId()))
                {
                    vector.add(new Integer(i));
                }
            } else
                if(amodification[i] instanceof ShippingGroupUpdate)
                {
                    if(isLoggingDebug())
                    {
                        logDebug("Shipping Group Update " + amodification[i]);
                    }
                    String as[] = makePerfectSublist(((ShippingGroupUpdate)amodification[i]).getShippingGroupIds(), list);
                    if(as.length > 0)
                    {
                        ((ShippingGroupUpdate)amodification[i]).setShippingGroupIds(as);
                    }
                    else
                    {
                        vector.add(new Integer(i));
                    }
                } else
                    if(amodification[i] instanceof PaymentGroupUpdate)
                    {
                        if(isLoggingDebug())
                        {
                            logDebug("Payment Group Update " + amodification[i]);
                        }
                        String as1[] = makePerfectSublist(((PaymentGroupUpdate)amodification[i]).getPaymentGroupIds(), list);
                        if(as1.length > 0)
                        {
                            ((PaymentGroupUpdate)amodification[i]).setPaymentGroupIds(as1);
                        }
                        else
                        {
                            vector.add(new Integer(i));
                        }
                    } else
                        if(amodification[i] instanceof GenericRemove)
                        {
                            if(isLoggingDebug())
                            {
                                logDebug("Ignoring a GenericRemove: " + amodification[i]);
                            }
                        } else
                            if(isLoggingWarning())
                            {
                                logWarning("Unknown Modificaiton: " + amodification[i]);
                            }
        }

        if(isLoggingDebug())
        {
            logDebug("List of Modifications to be discarded: " + vector);
        }
        if(vector.size() > 0)
        {
            orderupdatenotification.removeModifications(vector);
        }
        if(isLoggingDebug())
        {
            logDebug("Length of the Updated List of Modifications: " + orderupdatenotification.getModifications().length);
        }
        return orderupdatenotification;
    }

    protected boolean repriceOrder(Order order, PricingTools pricingtools, ProfileTools profiletools, PromotionTools promotiontools)
    {
        try
        {
            ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList();
            ArrayList arraylist2 = new ArrayList();
            ArrayList arraylist3 = new ArrayList();
            promotiontools.getOrderPromotions(order, arraylist, arraylist1, arraylist2, arraylist3);
            Profile profile = new Profile();
            if(!profiletools.locateUserFromId(order.getProfileId(), profile))
            {
                if(isLoggingDebug())
                {
                    logDebug("Repricing without a Profile");
                }
                profile = null;
            }
            OrderPriceInfo orderpriceinfo = pricingtools.priceOrderTotal(order, arraylist2, arraylist3, arraylist, arraylist1, getDefaultLocale(), profile, null);

            logDebug("|--------------------------------------------------------|");
            logDebug("|-raw subTotal : "+ orderpriceinfo.getRawSubtotal());
            logDebug("|-shipping cost : "+ orderpriceinfo.getShipping());
            logDebug("|-remise : "+ orderpriceinfo.getDiscountAmount());
            double prepa = orderpriceinfo.getTotal() - (orderpriceinfo.getRawSubtotal() + orderpriceinfo.getShipping());
            logDebug("|-preparation : "+ prepa);
            logDebug("|-total : "+ orderpriceinfo.getTotal());        
            logDebug("|--------------------------------------------------------|");

            /*
             * Mise Ã  jour de la commande
             */
            setOrderPriceInfo(orderpriceinfo);



            if(isLoggingDebug())
            {
                logDebug("Finished repricing the Order");
            }
            PipelineResult pipelineresult = getOrderManager().recalculatePaymentGroupAmounts(order);
            if(isLoggingDebug())
            {
                logDebug("Finished calculating the PaymentGroupAmounts");
            }




        }
        catch(PricingException pricingexception)
        {
            if(isLoggingError())
            {
                logError(pricingexception);
            }
            return false;
        }
        catch(CommerceException commerceexception)
        {
            if(isLoggingError())
            {
                logError(commerceexception);
            }
            return false;
        }
        return true;
    }

    public void resetOrderValidationErrors()
    {
        if(mOrderValidationErrors != null)
        {
            mOrderValidationErrors.removeAllElements();
        }
    }

    public void setAddItemFailureURL(String s)
    {
        mAddItemFailureURL = s;
    }

    public void setAddItemSuccessURL(String s)
    {
        mAddItemSuccessURL = s;
    }

    public void setAmount(double d)
    {
        mAmount = d;
    }

    public void setCancelCancelFailureURL(String s)
    {
        mCancelCancelFailureURL = s;
    }

    public void setCancelCancelSuccessURL(String s)
    {
        mCancelCancelSuccessURL = s;
    }

    public void setCancelOrderFailureURL(String s)
    {
        mCancelOrderFailureURL = s;
    }

    public void setCancelOrderSuccessURL(String s)
    {
        mCancelOrderSuccessURL = s;
    }

    public void setCatalogRefId(String s)
    {
        mCatalogRefId = s;
    }

    public void setClaimableManager(ClaimableManager claimablemanager)
    {
        mClaimableManager = claimablemanager;
    }

    public void setCommerceItemId(String s)
    {
        mCommerceItemId = s;
    }

    public void setConfirmCancelFailureURL(String s)
    {
        mConfirmCancelFailureURL = s;
    }

    public void setConfirmCancelSuccessURL(String s)
    {
        mConfirmCancelSuccessURL = s;
    }

    public void setCreatePaymentGroupFailureURL(String s)
    {
        mCreatePaymentGroupFailureURL = s;
    }

    public void setCreatePaymentGroupSuccessURL(String s)
    {
        mCreatePaymentGroupSuccessURL = s;
    }

    public void setCreateShippingGroupFailureURL(String s)
    {
        mCreateShippingGroupFailureURL = s;
    }

    public void setCreateShippingGroupSuccessURL(String s)
    {
        mCreateShippingGroupSuccessURL = s;
    }

    public void setCreditCardNumber(String s)
    {
        mCreditCardNumber = s;
    }

    public void setCreditCardType(String s)
    {
        mCreditCardType = s;
    }

    public void setCurrentTransaction(Transaction transaction)
    {
        mCurrentTransaction = transaction;
    }

    public void setDoNotSaveOrderFailureURL(String s)
    {
        mDoNotSaveOrderFailureURL = s;
    }

    public void setDoNotSaveOrderSuccessURL(String s)
    {
        mDoNotSaveOrderSuccessURL = s;
    }

    public void setExpirationDayOfMonth(String s)
    {
        mExpirationDayOfMonth = s;
    }

    public void setExpirationMonth(String s)
    {
        mExpirationMonth = s;
    }

    public void setExpirationYear(String s)
    {
        mExpirationYear = s;
    }

    public void setForwardFailureURL(String s)
    {
        mForwardFailureURL = s;
    }

    public void setForwardSuccessURL(String s)
    {
        mForwardSuccessURL = s;
    }

    public void setGiftCertificateNumber(String s)
    {
        mGiftCertificateNumber = s;
    }

    public void setItemDue(double d)
    {
        mItemDue = d;
    }

    public void setLoadOrderFailureURL(String s)
    {
        mLoadOrderFailureURL = s;
    }

    public void setLoadOrderSuccessURL(String s)
    {
        mLoadOrderSuccessURL = s;
    }

    public void setMessageDispatcher(MessageDispatcher messagedispatcher)
    {
        mMessageDispatcher = messagedispatcher;
    }

    public void setNotificationMessage(OrderUpdateNotification orderupdatenotification)
    {
        mNotificationMessage = orderupdatenotification;
    }

    public void setOrderDue(double d)
    {
        mOrderDue = d;
    }

    public void setOrderId(String s)
    {
        mOrderId = s;
    }

    public void setOrderLockedURL(String s)
    {
        mOrderLockedURL = s;
    }

    public void setOrderValidationError(boolean flag)
    {
        mOrderValidationError = flag;
    }

    public void setOrderValidationErrors(PipelineResult pipelineresult)
    {
        if(pipelineresult == null || !pipelineresult.hasErrors())
        {
            setOrderValidationError(false);
            return;
        }
        boolean flag = false;
        Object aobj[] = pipelineresult.getErrorKeys();
        int i = aobj.length;
        for(int j = 0; j < i; j++)
        {
            Object obj = pipelineresult.getError(aobj[j]);
            if(isLoggingDebug())
            {
                logDebug("PipelineError: key=" + aobj[j] + "; error=" + obj);
            }
            if(obj != null)
            {
                String s = aobj[j].toString();
                if(obj instanceof Map)
                {
                    for(Iterator iterator = ((Map)obj).values().iterator(); iterator.hasNext();)
                    {
                        getOrderValidationErrors().add(iterator.next().toString());
                        flag = true;
                    }

                } else
                {
                    if(obj instanceof Collection)
                    {
                        for(Iterator iterator1 = ((Collection)obj).iterator(); iterator1.hasNext();)
                        {
                            getOrderValidationErrors().add(iterator1.next().toString());
                            flag = true;
                        }

                        setOrderValidationError(true);
                        return;
                    }
                    getOrderValidationErrors().add(obj.toString());
                }
                flag = true;
            }
        }

        setOrderValidationError(flag);
    }

    public void setPaymentGroupId(String s)
    {
        mPaymentGroupId = s;
    }

    public void setPaymentGroupStates(PaymentGroupStates paymentgroupstates)
    {
        mPaymentGroupStates = paymentgroupstates;
    }

    public void setPaymentManager(PaymentManager paymentmanager)
    {
        mPaymentManager = paymentmanager;
    }

    public void setPricingTools(PricingTools pricingtools)
    {
        mPricingTools = pricingtools;
    }

    public void setProfileTools(ProfileTools profiletools)
    {
        mProfileTools = profiletools;
    }

    public void setPromotionTools(PromotionTools promotiontools)
    {
        mPromotionTools = promotiontools;
    }

    public void setRelationshipId(String s)
    {
        mRelationshipId = s;
    }

    public void setRelationshipTypeAsString(String s)
    {
        mRelationshipTypeAsString = s;
    }

    public void setSaveOrderFailureURL(String s)
    {
        mSaveOrderFailureURL = s;
    }

    public void setSaveOrderSuccessURL(String s)
    {
        mSaveOrderSuccessURL = s;
    }

    public void setShippingDue(double d)
    {
        mShippingDue = d;
    }

    public void setShippingGroupId(String s)
    {
        mShippingGroupId = s;
    }

    public void setShippingGroupStates(ShippingGroupStates shippinggroupstates)
    {
        mShippingGroupStates = shippinggroupstates;
    }

    public void setShippingMethod(String s)
    {
        mShippingMethod = s;
    }

    public void setTaxDue(double d)
    {
        mTaxDue = d;
    }

    public void setUpdatePaymentGroupFailureURL(String s)
    {
        mUpdatePaymentGroupFailureURL = s;
    }

    public void setUpdateShippingGroupFailureURL(String s)
    {
        mUpdateShippingGroupFailureURL = s;
    }

    /* protected void updateOrder(Order order, SimpleOrderManager simpleordermanager)
        throws CommerceException
    {
        if(order == null)
            {
            throw new InvalidParameterException();
            }
        if(simpleordermanager == null)
            {
            throw new InvalidParameterException();
            }
        getOrderStates();
        if(order.getState() == getOrderStates().getStateValue("pending_merchant_action"))
        {
            getOrderStates();
            order.setState(getOrderStates().getStateValue("processing"));
            OrderUpdateNotification orderupdatenotification = getNotificationMessage();
            if(orderupdatenotification != null)
            {
                orderupdatenotification.addOrderStateUpdate(order.getId(), order.getState());
            }
        }
        List list = order.getPaymentGroups();
        for(Iterator iterator = list.iterator(); iterator.hasNext();)
        {
            PaymentGroup paymentgroup = (PaymentGroup)iterator.next();
            if(paymentgroup.getState() == getPaymentGroupStates().getStateValue(getPaymentGroupStates().AUTHORIZE_FAILED) || paymentgroup.getState() == getPaymentGroupStates().getStateValue(getPaymentGroupStates().SETTLE_FAILED))
            {
                paymentgroup.setState(getPaymentGroupStates().getStateValue(getPaymentGroupStates().INITIAL));
                OrderUpdateNotification orderupdatenotification1 = getNotificationMessage();
                if(orderupdatenotification1 != null)
                {
                    orderupdatenotification1.addPaymentGroupUpdate(paymentgroup.getId());
                }
            }
        }*/

    protected void updateOrder(Order order, SimpleOrderManager simpleordermanager)
    throws CommerceException
    {
        RepositoryItem commande = null;
        Repository repo = getRepository();

        if(order == null)
        {
            throw new InvalidParameterException();
        }
        if(simpleordermanager == null)
        {
            throw new InvalidParameterException();
        }

        //recuperation de la commande en repositoryItem
        try
        {

            commande = repo.getItem(order.getId(),"order");

        }
        catch (RepositoryException e)
        {
            if (isLoggingError())
            {
                logError("ERROR : "+e);
            }
        }



        if(commande.getPropertyValue("state")!= null && commande.getPropertyValue("state").equals("PENDING_MERCHANT_ACTION"))
        {        
            order.setState(2); //set du statut Ã  PROCCESSING
            OrderUpdateNotification orderupdatenotification = getNotificationMessage();
            if(orderupdatenotification != null)
            {
                orderupdatenotification.addOrderStateUpdate(order.getId(), order.getState());
            }
        }

        try {
            List list = order.getPaymentGroups();
            for(Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                PaymentGroup paymentgroup = (PaymentGroup)iterator.next();
                RepositoryItem l_paymentGroup = repo.getItem(paymentgroup.getId(),"paymentGroup");
                if(l_paymentGroup.getPropertyValue("state") != null 
                        && (l_paymentGroup.getPropertyValue("state").equals("AUTHORIZE_FAILED") 
                                || l_paymentGroup.getPropertyValue("state").equals("SETTLE_FAILED")))
                {


                    paymentgroup.setState(0);     //set du statut Ã  INITIAL
                    OrderUpdateNotification orderupdatenotification1 = getNotificationMessage();
                    if(orderupdatenotification1 != null)
                    {
                        orderupdatenotification1.addPaymentGroupUpdate(paymentgroup.getId());
                    }


                }
            }
        }
        catch (RepositoryException repoEx)
        {
            if (isLoggingError())
            {
                logError("ERROR :"+repoEx);
            }
        }


        try {

            List list1 = order.getShippingGroups();
            for(Iterator iterator1 = list1.iterator(); iterator1.hasNext();)
            {
                ShippingGroup shippinggroup = (ShippingGroup)iterator1.next();
                RepositoryItem l_shippinggroup = repo.getItem(shippinggroup.getId(),"shippingGroup");
                if(l_shippinggroup.getPropertyValue("state") != null 
                        && l_shippinggroup.getPropertyValue("state").equals("PENDING_MERCHANT_ACTION"))
                {                
                    shippinggroup.setState(0);   //set du statut Ã  INITIAL
                    OrderUpdateNotification orderupdatenotification2 = getNotificationMessage();
                    if(orderupdatenotification2 != null)
                    {
                        orderupdatenotification2.addShippingGroupUpdate(shippinggroup.getId());
                    }
                }
            }
        }
        catch (RepositoryException repoEx)
        {
            if (isLoggingError())
            {
                logError("ERROR : "+repoEx);
            }
        }
        simpleordermanager.updateOrder(order);
    }

    protected boolean updatePaymentGroupRelationship(PaymentGroupRelationship paymentgrouprelationship, DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws IOException, ServletException
    {
        int i = paymentgrouprelationship.getRelationshipType();
        int j = -1;
        String s = getRelationshipTypeAsString();
        if(s != null && s.trim().length() != 0)
        {
            j = RelationshipTypes.stringToType(s);
        }
        if(j < 0)
        {
            j = i;
        }
        if(j != i)
        {
            try
            {
                paymentgrouprelationship.setRelationshipType(j);
            }
            catch(InvalidTypeException _ex)
            {
                String s1 = formatUserMessage("unknownRelationshipType", dynamohttpservletrequest, dynamohttpservletresponse);
                String s2 = generatePropertyPath("relationshipType");
                addFormException(new DropletFormException(s1, s2, "unknownRelationshipType"));
                return false;
            }
        }
        if(j == 200 || j == 401 || j == 403)
        {
            double d = getAmount();
            if(d < 0.0D)
            {
                String s3 = formatUserMessage("amountLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s6 = generatePropertyPath("amount");
                addFormException(new DropletFormException(s3, s6, "amountLessThanOrEqualToZero"));
                return false;
            }
            if(d == 0.0D)
            {    try
            {
                Order order = getOrder();
                if(order == null)
                {
                    String s7 = formatUserMessage("noOrderToModify", dynamohttpservletrequest, dynamohttpservletresponse);
                    throw new ServletException(s7);
                }
                order.removePaymentGroupRelationship(paymentgrouprelationship.getId());
            }
            /* catch(RelationshipNotFoundException _ex)
                {
                    String s4 = formatUserMessage("noRelationship", paymentgrouprelationship.getId(), dynamohttpservletrequest, dynamohttpservletresponse);
                    String s8 = generatePropertyPath("paymentGroupId");
                    addFormException(new DropletFormException(s4, s8, "noPaymentGroup"));
                    return false;
                }
                catch(InvalidParameterException _ex)
                {
                    String s5 = formatUserMessage("noRelationshipId", dynamohttpservletrequest, dynamohttpservletresponse);
                    String s9 = generatePropertyPath("relationshipId");
                    addFormException(new DropletFormException(s5, s9, "noRelationshipId"));
                    return false;
                }*/
            catch (Exception e)
            {
                if(isLoggingError())
                {
                    logError(e.toString());
                }
            }
            }
            else
            {
                paymentgrouprelationship.setAmount(d);
            }

        } else
        {
            paymentgrouprelationship.setAmount(0.0D);
        }
        return true;
    }

    protected boolean updateShippingGroupCommerceItemRelationship(ShippingGroupCommerceItemRelationship shippinggroupcommerceitemrelationship, String s, DynamoHttpServletRequest dynamohttpservletrequest, DynamoHttpServletResponse dynamohttpservletresponse)
    throws IOException, ServletException
    {
        ShippingGroup shippinggroup = shippinggroupcommerceitemrelationship.getShippingGroup();
        int i = shippinggroupcommerceitemrelationship.getRelationshipType();
        CommerceItem commerceitem = shippinggroupcommerceitemrelationship.getCommerceItem();
        if(s == null)
        {
            String s1 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
            String s2 = generatePropertyPath("shippingId");
            addFormException(new DropletFormException(s1, s2, "noGroupId"));
            return false;
        }
        SimpleOrderManager simpleordermanager = getOrderManager();
        if(simpleordermanager == null)
        {
            String s3 = formatUserMessage("noOrderManager", dynamohttpservletrequest, dynamohttpservletresponse);
            throw new ServletException(s3);
        }
        if(i == 100)
        {
            long l = getQuantity();
            if(l <= 0L)
            {
                String s8 = formatUserMessage("quantityLessThanOrEqualToZero", dynamohttpservletrequest, dynamohttpservletresponse);
                String s10 = generatePropertyPath("quantity");
                addFormException(new DropletFormException(s8, s10, "quantityLessThanOrEqualToZero"));
                return false;
            }
            if(!shippinggroup.getId().equals(s))
            {
                try
                {
                    simpleordermanager.moveItemToShippingGroup(getOrder(), commerceitem.getCatalogRefId(), l, shippinggroup.getId(), s);
                }
                catch(CommerceException _ex)
                {
                    String s9 = formatUserMessage("errorMovingToShippingGroup", shippinggroup.getId(), s, dynamohttpservletrequest, dynamohttpservletresponse);
                    String s11 = generatePropertyPath("shippingGroupRelationship");
                    addFormException(new DropletFormException(s9, s11, "errorMovingToShippingGroup"));
                    return false;
                }
            }
            return true;
        }
        if(i == 101)
        {
            try
            {
                shippinggroupcommerceitemrelationship.setShippingGroup(getOrder().getShippingGroup(s));
            }
            /* catch(ShippingGroupNotFoundException _ex)
            {
                String s4 = formatUserMessage("noShippingGroup", s, dynamohttpservletrequest, dynamohttpservletresponse);
                String s6 = generatePropertyPath("shippingGroup");
                addFormException(new DropletFormException(s4, s6, "noShippingGroup"));
                return false;
            }
            catch(InvalidParameterException _ex)
            {
                String s5 = formatUserMessage("noGroupId", dynamohttpservletrequest, dynamohttpservletresponse);
                String s7 = generatePropertyPath("shippingGroup");
                addFormException(new DropletFormException(s5, s7, "noGroupId"));
                return false;
            }*/

            catch (Exception e)
            {
                if(isLoggingError())
                {
                    logError(e.toString());
                }
            }
            return true;
        }
        if(isLoggingWarning())
        {
            logWarning("Unknown Relationship Type " + shippinggroupcommerceitemrelationship.getRelationshipTypeAsString());
        }
        return false;
    }


    public void overrideUpdateOrder(String o)
    {
        MutableRepositoryItem commande = null;
        MutableRepository repo = (MutableRepository)getRepository();

        //recuperation de la commande en repositoryItem
        try
        {        
            commande = (MutableRepositoryItem)repo.getItem(o,"order");
            MutableRepositoryItem priceInfo = (MutableRepositoryItem)commande.getPropertyValue("priceInfo");
            priceInfo.setPropertyValue("rawSubtotal", new Double(getOrderPriceInfo().getRawSubtotal()));
            priceInfo.setPropertyValue("shipping",new Double(getOrderPriceInfo().getShipping()));
            priceInfo.setPropertyValue("amount",new Double(getOrderPriceInfo().getAmount()));
            priceInfo.setPropertyValue("fraisDePreparation",new Double(getOrderPriceInfo().getTotal() - (getOrderPriceInfo().getRawSubtotal() + getOrderPriceInfo().getShipping())));            
            commande.setPropertyValue("priceInfo", priceInfo);

            priceInfo = (MutableRepositoryItem)commande.getPropertyValue("preparationPriceInfo");
            priceInfo.setPropertyValue("amount",new Double(getOrderPriceInfo().getTotal() - (getOrderPriceInfo().getRawSubtotal() + getOrderPriceInfo().getShipping())));
            commande.setPropertyValue("preparationPriceInfo", priceInfo);

            List l_ShippingGroups = (List)commande.getPropertyValue("shippingGroups");
            if (l_ShippingGroups != null)
            {
                MutableRepositoryItem l_ShippingGroup = (MutableRepositoryItem) l_ShippingGroups.get(0);
                priceInfo = (MutableRepositoryItem)l_ShippingGroup.getPropertyValue("priceInfo");                
                priceInfo.setPropertyValue("amount", new Double(getOrderPriceInfo().getShipping()));
                priceInfo.setPropertyValue("rawShipping", new Double(getOrderPriceInfo().getShipping()));
                l_ShippingGroup.setPropertyValue("priceInfo", priceInfo);
                l_ShippingGroups.set(0,l_ShippingGroup);
                commande.setPropertyValue("shippingGroups", l_ShippingGroups);
            }


            repo.updateItem(commande);


        }
        catch (Exception e)        
        {   
            if (isLoggingError())
            {
                logError(this.getClass() + ".overrideUpdateOrder  :ERROR ->" +e);
            }
        }
    }

    public static final String CLASS_VERSION = "$Id: ExistingOrderModifierFormHandler.java,v 1.53.6.16 2000/12/08 23:00:25 sperman Exp $";
    public static final String MSG_NO_ORDER_MANAGER = "noOrderManager";
    public static final String MSG_NO_PAYMENT_MANAGER = "noPaymentManager";
    public static final String MSG_NO_PRICING_TOOLS = "noPricingTools";
    public static final String MSG_NO_PROFILE_TOOLS = "noProfileTools";
    public static final String MSG_NO_PROMO_TOOLS = "noPromoTools";
    public static final String MSG_NO_USER_PROFILE = "noUserProfile";
    public static final String MSG_NO_CLAIMABLE_MANAGER = "noClaimableManager";
    public static final String MSG_NO_ORDER_ID = "noOrderId";
    public static final String MSG_ORDER_LOCKED = "orderLocked";
    public static final String MSG_ERROR_REPRICING = "errorRepricingOrder";
    public static final String MSG_ERROR_UPDATING_ORDER = "errorUpdatingOrder";
    public static final String MSG_ERROR_CANCELING_ORDER = "errorCancelingOrder";
    public static final String MSG_NO_ITEMS_CHOSEN = "noItemsChosen";
    public static final String MSG_ERROR_ADDING_ITEMS = "errorAddingToOrder";
    public static final String MSG_ERROR_REMOVING_ITEMS = "errorRemovingItemFromOrder";
    public static final String MSG_NO_GROUP_ID = "noGroupId";
    public static final String MSG_NO_ADDRESS = "noAddress";
    public static final String MSG_NO_PAYMENTGROUP_FOUND = "noPaymentGroup";
    public static final String MSG_NO_CREDITCARD_FOUND = "noCreditCardObject";
    public static final String MSG_ERROR_CREATING_PAYMENTGROUP = "errorCreatingPaymentGroup";
    public static final String MSG_ERROR_DELETING_PAYMENTGROUP = "errorDeletingPaymentGroup";
    public static final String MSG_ERROR_UPDATING_PAYMENTGROUP = "errorUpdatingPaymentGroup";
    public static final String MSG_NO_GIFTCERTIFICATE_NUMBER = "noGiftCertificateNumber";
    public static final String MSG_NO_GIFTCERTIFICATE_FOUND = "noGiftCertificateFound";
    public static final String MSG_NO_CREDITCARD_NUMBER = "noCreditCardNumber";
    public static final String MSG_UNABLE_TO_CREDIT = "errorCrediting";
    public static final String MSG_NO_SHIPPINGGROUP_FOUND = "noShippingGroup";
    public static final String MSG_ERROR_CREATING_SHIPPINGGROUP = "errorCreatingShippingGroup";
    public static final String MSG_ERROR_DELETING_SHIPPINGGROUP = "errorDeletingShippingGroup";
    public static final String MSG_ERROR_UPDATING_SHIPPINGGROUP = "errorUpdatingShippingGroup";
    public static final String MSG_NO_CARRIER = "noCarrier";
    public static final String MSG_AMOUNT_LESSTHAN_OR_EQUALTO_ZERO = "amountLessThanOrEqualToZero";
    public static final String MSG_NO_RELATIONSHIP_ID = "noRelationshipId";
    public static final String MSG_UNKNOWN_RELATIONSHIP_TYPE = "unknownRelationshipType";
    public static final String MSG_NO_RELATIONSHIP_FOUND = "noRelationship";
    public static final String MSG_PAYMENT_RELATIONSHIP_NOT_CREATED = "noPaymentRelationshipCreated";
    public static final String MSG_ITEM_NOT_FOUND_IN_CATALOG = "badCatalogRefId";
    public static final String MSG_ERROR_MOVING_ITEMS_TO_PAYMENTGROUP = "errorMovingToPaymentGroup";
    public static final String MSG_ERROR_MOVING_ITEMS_TO_SHIPPINGGROUP = "errorMovingToShippingGroup";
    public static final String MSG_SHIPPING_BALANCE_DUE = "shippingBalanceDue";
    public static final String MSG_TAX_BALANCE_DUE = "taxBalanceDue";
    public static final String MSG_ORDER_BALANCE_DUE = "orderBalanceDue";
    public static final String MSG_ITEM_BALANCE_DUE = "itemBalanceDue";
    ProfileTools mProfileTools;
    PricingTools mPricingTools;
    PromotionTools mPromotionTools;
    PaymentManager mPaymentManager;
    ClaimableManager mClaimableManager;
    PaymentGroupStates mPaymentGroupStates;
    ShippingGroupStates mShippingGroupStates;
    String mOrderId;
    String mCommerceItemId;
    String mShippingGroupId;
    String mPaymentGroupId;
    String mRelationshipId;
    String mCatalogRefId;
    String mGiftCertificateNumber;
    double mAmount;
    double mTaxDue;
    double mItemDue;
    double mShippingDue;
    double mOrderDue;
    boolean mOrderValidationError;
    Vector mOrderValidationErrors;
    String mRelationshipTypeAsString;
    MessageDispatcher mMessageDispatcher;
    OrderUpdateNotification mNotificationMessage;
    String mDoNotSaveOrderSuccessURL;
    String mDoNotSaveOrderFailureURL;
    String mSaveOrderSuccessURL;
    String mSaveOrderFailureURL;
    String mLoadOrderSuccessURL;
    String mLoadOrderFailureURL;
    String mOrderLockedURL;
    String mConfirmCancelSuccessURL;
    String mConfirmCancelFailureURL;
    String mCancelOrderSuccessURL;
    String mCancelOrderFailureURL;
    String mCancelCancelFailureURL;
    String mCancelCancelSuccessURL;
    String mForwardSuccessURL;
    String mForwardFailureURL;
    String mCreatePaymentGroupSuccessURL;
    String mCreatePaymentGroupFailureURL;
    String mUpdatePaymentGroupFailureURL;
    String mCreateShippingGroupSuccessURL;
    String mCreateShippingGroupFailureURL;
    String mUpdateShippingGroupFailureURL;
    String mAddItemSuccessURL;
    String mAddItemFailureURL;
    Transaction mCurrentTransaction;
    String mShippingMethod;
    String mCreditCardType;
    String mCreditCardNumber;
    String mExpirationMonth;
    String mExpirationDayOfMonth;
    String mExpirationYear;

    OrderPriceInfo m_OrderPriceInfo;

    private Repository mRepository;

    public Repository getRepository()
    {
        return mRepository;
    }

    public void setRepository(Repository a_repository)
    {
        mRepository = a_repository;
    }

    public OrderPriceInfo getOrderPriceInfo()
    {
        return m_OrderPriceInfo;
    }

    public void setOrderPriceInfo(OrderPriceInfo a_orderPriceInfo)
    {
        m_OrderPriceInfo = a_orderPriceInfo;
    }

}