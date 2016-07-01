package com.castorama.scenario;

import static com.castorama.commerce.profile.Constants.EMAIL_PROFILE_PROP;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.adapter.gsa.GSARepository;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;
import atg.process.action.ActionException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.scenario.configuration.BaseSendEmailConfiguration;

/**
 * Given action is intended to handle order submit action. The action
 *
 * <ol>
 * <li>checks if the user has been invited</li>
 * <li>checks if the order has referee coupon applied</li>
 * <li>finds an available coupon for referrer</li>
 * <li>sends email with coupon for referrer</li>
<ol>
 *
 * @author Andrei_Raichonak
 */
public class SendCouponToReferrerAction extends BaseSendEmailAction {
    /** NUMBER_FORMAT constant. */
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    /** fromEmail property. */
    private String mFromEmail;

    /** emailSubject property. */
    private String mEmailSubject;

    /**
     * @see com.castorama.scenario.BaseSendEmailAction#configure(java.lang.Object)
     */
    public void configure(Object pConfiguration) throws ProcessException {
        super.configure(pConfiguration);

        BaseSendEmailConfiguration config = (BaseSendEmailConfiguration) pConfiguration;

        mFromEmail = config.getFromEmail();
        mEmailSubject = config.getEmailSubject();
    }  // end method configure

    /**
     * @see com.castorama.scenario.BaseSendEmailAction#initialize(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public void initialize(Map pParameters) throws ProcessException {
        super.initialize(pParameters);
        storeRequiredParameter(pParameters, PARAM_TEMPLATE, java.lang.String.class);
    }

    /**
     * Returns lockName property.
     *
     * @param  pContext parameter to set.
     *
     * @return lockName property.
     */
    protected Serializable getLockName(ProcessExecutionContext pContext) {
        String lockName = "SendCouponToReferrerAction" + ((CastoOrderMessage) pContext.getMessage()).getOrderId();
        return lockName;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pContext ToDo: DOCUMENT ME!
     *
     * @throws ProcessException ToDo: DOCUMENT ME!
     */
    protected void doExecuteAction(ProcessExecutionContext pContext) throws ProcessException {
        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("Entering doExecuteAction");
            mLogger.logDebug("Message is " + pContext.getMessage());
        }

        RepositoryItem refereePromo = mReferrerProgramConfig.getRefereePromotion();

        if (refereePromo == null) {
            if (mLogger.isLoggingWarning()) {
                mLogger.logWarning("Referee promotion is null. Exiting.");
            }
            return;
        }

        RepositoryItem referrerPromo = mReferrerProgramConfig.getReferrerPromotion();

        if (referrerPromo == null) {
            if (mLogger.isLoggingWarning()) {
                mLogger.logWarning("Referrer promotion is null. Exiting.");
            }
            return;
        }

        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("refereePromo is " + refereePromo);
            mLogger.logDebug("referrerPromo is " + referrerPromo);
        }

        Order order = ((CastoOrderMessage) pContext.getMessage()).getOrder();
        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("order is " + order);
        }

        Set<String> ids = getAllPromosAppliedToOrder(order);

        if (!ids.contains(refereePromo.getRepositoryId())) {
            if (mLogger.isLoggingWarning()) {
                mLogger.logWarning("No referee promotion applied. Exiting.");
            }
            return;
        }

        ClaimableTools claimableTools = mClaimableManager.getClaimableTools();
        MutableRepository claimableRepository = (MutableRepository) claimableTools.getClaimableRepository();
        String couponId = null;
        RepositoryItem referrer = null;
        String email = null;
        RepositoryItem profile = null;

        try {
            String profId = order.getProfileId();
            profile = mPromotionTools.getProfile(profId);
            email = (String) profile.getPropertyValue(EMAIL_PROFILE_PROP);

            // find all referees which email equals to email from order profile and which referre's coupon is
            // applied
            RqlStatement statement = RqlStatement.parseRqlStatement(FIND_REFEREES_BY_EMAIL_RQL);
            RepositoryView refreeRepositoryView =
                mProfileRepository.getItemDescriptor(EMAIL_PARAM_REFEREE).getRepositoryView();
            RepositoryItem[] referees = statement.executeQuery(refreeRepositoryView, new Object[] {email});
            RepositoryItem referee = null;

            if ((referees != null) && (referees.length > 0)) {
                // iterate to find out a coupon which corresponds to referee promotion
                for (int i = 0; i < referees.length; i++) {
                    String cid = (String) referees[i].getPropertyValue("couponid");
                    RepositoryItem coupon =
                        claimableRepository.getItem(cid, claimableTools.getClaimableItemDescriptorName());
                    RepositoryItem promotion = (RepositoryItem) coupon.getPropertyValue("promotion");

                    if (promotion.getRepositoryId().equals(refereePromo.getRepositoryId())) {
                        referee = referees[i];

                        break;
                    }
                }
            }

            if (referee == null) {
                if (mLogger.isLoggingWarning()) {
                    mLogger.logWarning("Strange - no referee. Exiting.");
                }
                return;
            }

            referrer = (RepositoryItem) referee.getPropertyValue("referrer");

            Connection connection = null;
            ResultSet resultSet = null;
            PreparedStatement ps = null;

            try {
                connection = ((GSARepository) mProfileRepository).getDataSource().getConnection();

                ps = connection.prepareStatement(GET_COUPON_ID_FOR_REFERRER_SQL);
                ps.setString(1, referrerPromo.getRepositoryId());
                resultSet = ps.executeQuery();

                if (resultSet.next()) {
                    couponId = resultSet.getString("COUPON_ID");
                }
            } catch (Exception e) {
                throw new ProcessException("Can not retrieve coupon", e);
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (Exception e) {
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (Exception e) {
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) {
                    }
                }
            }  // end try-catch-finally

            if (couponId == null) {
                throw new ProcessException("No coupons available");
            }

            MutableRepositoryItem mutRef =
                ((MutableRepository) mProfileRepository).getItemForUpdate(referee.getRepositoryId(),
                                                                          EMAIL_PARAM_REFEREE);
            mutRef.setPropertyValue("referrerCouponId", couponId);
            ((MutableRepository) mProfileRepository).updateItem(mutRef);
        } catch (RepositoryException e) {
            throw new ProcessException("Can not send email", e);
        }  // end try-catch

        try {
            Double adjuster = (Double) referrerPromo.getPropertyValue("adjuster");
            Map<String, String> params = new HashMap<String, String>();
            params.put(EMAIL_PARAM_COUPON, couponId);
            String formattedAdjuster = NUMBER_FORMAT.format(adjuster);
            params.put(EMAIL_PARAM_PROMO_AMOUNT, formattedAdjuster);
            params.put(EMAIL_PARAM_REFERRER,
                       referrer.getPropertyValue("firstName") + " " + referrer.getPropertyValue("lastName"));
            params.put(EMAIL_PARAM_REFEREE,
                       profile.getPropertyValue("firstName") + " " + profile.getPropertyValue("lastName"));
            params.put(EMAIL_PARAM_ORDER_LIMIT, String.valueOf(mReferrerProgramConfig.getOrderLimit()));
            sendEmail(pContext, String.format(mEmailSubject, formattedAdjuster), mFromEmail,
                      (String) referrer.getPropertyValue("email"), (String) getParameterValue(PARAM_TEMPLATE, pContext),
                      params);
        } catch (ActionException e) {
            throw new ProcessException(e);
        }

    }

    /**
     * Returns all promotions applied to the order
     *
     * @param  pOrder order to get promotions from
     *
     * @return a set of promotions id
     */
    @SuppressWarnings("unchecked")
    protected Set<String> getAllPromosAppliedToOrder(Order pOrder) {
        Set<String> ids = new HashSet<String>();

        List shippinggroups = pOrder.getShippingGroups();

        for (Iterator iterator = shippinggroups.iterator(); iterator.hasNext();) {
            ShippingGroup shippingGroup = (ShippingGroup) iterator.next();
            ShippingPriceInfo spi = shippingGroup.getPriceInfo();

            if (spi != null) {
                List adjustments = spi.getAdjustments();

                for (Iterator it = adjustments.iterator(); it.hasNext();) {
                    PricingAdjustment adj = (PricingAdjustment) it.next();

                    if (adj.getPricingModel() != null) {
                        String promoId = adj.getPricingModel().getRepositoryId();
                        ids.add(promoId);
                    }
                }
            }
        }

        OrderPriceInfo opi = pOrder.getPriceInfo();

        if (opi != null) {
            List adjustments = opi.getAdjustments();

            for (Iterator it = adjustments.iterator(); it.hasNext();) {
                PricingAdjustment adj = (PricingAdjustment) it.next();

                if (adj.getPricingModel() != null) {
                    String promoId = adj.getPricingModel().getRepositoryId();
                    ids.add(promoId);
                }
            }
        }

        List commerceItems = pOrder.getCommerceItems();

        for (Iterator iterator = commerceItems.iterator(); iterator.hasNext();) {
            CommerceItem commerceItem = (CommerceItem) iterator.next();
            ItemPriceInfo ipi = commerceItem.getPriceInfo();

            if (ipi != null) {
                List adjustments = ipi.getAdjustments();

                for (Iterator it = adjustments.iterator(); it.hasNext();) {
                    PricingAdjustment adj = (PricingAdjustment) it.next();

                    if (adj.getPricingModel() != null) {
                        String promoId = adj.getPricingModel().getRepositoryId();
                        ids.add(promoId);
                    }
                }
            }
        }

        return ids;
    }  // end method getAllPromosAppliedToOrder

}
