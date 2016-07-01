package com.castorama.scenario;

import static com.castorama.commerce.profile.Constants.FIRST_NAME_PROFILE_PROP;
import static com.castorama.commerce.profile.Constants.LAST_NAME_PROFILE_PROP;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.adapter.gsa.GSARepository;
import atg.commerce.promotion.PromotionConstants;
import atg.core.util.StringUtils;
import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;
import atg.process.action.ActionException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileTools;

import com.castorama.scenario.configuration.BaseSendEmailConfiguration;

/**
 * Given action is intended to handle user invitation. The action
 *
 * <ol>
 * <li>finds an available coupon and assigns it to referee</li>
 * <li>creates and store referee item</li>
 * <li>sends invitation email to referee and validation email to referrer</li>
<ol>
 *
 * @author Andrei_Raichonak
 */
public class SendCouponToRefereeAction extends BaseSendEmailAction {
    /** NUMBER_FORMAT constant. */
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    static {
        NUMBER_FORMAT.setMaximumFractionDigits(2);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    /** profileTools property. */
    private ProfileTools mProfileTools;

    /** fromEmail property. */
    private String mFromEmail;

    /** emailSubject property. */
    private String mEmailSubject;

    /**
     * @see com.castorama.scenario.BaseSendEmailAction#initialize(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public void initialize(Map pParameters) throws ProcessException {
        super.initialize(pParameters);
        storeRequiredParameter(pParameters, PARAM_TEMPLATE, java.lang.String.class);
        storeRequiredParameter(pParameters, PARAM_REFERRER_TEMPLATE, java.lang.String.class);
    }

    /**
     * @see com.castorama.scenario.BaseSendEmailAction#configure(java.lang.Object)
     */
    public void configure(Object pConfiguration) throws ProcessException {
        super.configure(pConfiguration);

        BaseSendEmailConfiguration config = (BaseSendEmailConfiguration) pConfiguration;

        mProfileTools = config.getProfileTools();

        if (mProfileTools == null) {
            throw new ProcessException(PromotionConstants.getStringResource(PromotionConstants.PROMOTION_TOOLS_NOT_FOUND));
        }

        mFromEmail = config.getFromEmail();
        mEmailSubject = config.getEmailSubject();

    }  // end method configure

    /**
     * Returns lockName property.
     *
     * @param  pContext parameter to set.
     *
     * @return lockName property.
     */
    protected Serializable getLockName(ProcessExecutionContext pContext) {
        InviteMessage inviteMessage = ((com.castorama.scenario.InviteMessage) pContext.getMessage());
        return inviteMessage.getProfileId() + ":" + inviteMessage.getReferee();
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

        InviteMessage inviteMessage = ((com.castorama.scenario.InviteMessage) pContext.getMessage());
        String email = inviteMessage.getReferee();

        if (StringUtils.isBlank(email)) {
            if (mLogger.isLoggingWarning()) {
                mLogger.logWarning("Empty email. Exiting.");
            }
            return;
        }

        if (mProfileTools.getItemFromEmail(email) != null) {
            if (mLogger.isLoggingWarning()) {
                mLogger.logWarning("User with email " + email + " already registered. Exiting.");
            }
            return;
        }

        RepositoryItem referrer = null;

        try {
            referrer = mProfileRepository.getItem(inviteMessage.getProfileId(), "user");
        } catch (RepositoryException e1) {
            throw new ProcessException("Can not find referrer with id=" + inviteMessage.getProfileId());
        }

        Set referees = (Set) referrer.getPropertyValue(PROPERTY_REFEREE);

        if (referees != null) {
            for (Iterator iterator = referees.iterator(); iterator.hasNext();) {
                RepositoryItem referee = (RepositoryItem) iterator.next();

                // check if the user is already invited
                if (((String) referee.getPropertyValue(PROPERTY_EMAIL)).equalsIgnoreCase(email)) {
                	if (mLogger.isLoggingWarning()) {
                        mLogger.logWarning(email + " is already invited by referrer. Exiting.");
                    }
                    return;
                }
            }
        }

        RepositoryItem refereePromo = mReferrerProgramConfig.getRefereePromotion();

        if (refereePromo == null) {
            throw new ProcessException("Referee promo is not set");
        }

        RepositoryItem referrerPromo = mReferrerProgramConfig.getReferrerPromotion();

        if (referrerPromo == null) {
            throw new ProcessException("Referrer promo is not set");
        }

        if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("refereePromo is " + refereePromo);
            mLogger.logDebug("referrerPromo is " + referrerPromo);
        }

        String couponId = null;

        // find first available coupon
        try {
            Connection connection = null;
            ResultSet resultSet = null;
            PreparedStatement ps = null;

            try {
                connection = ((GSARepository) mProfileRepository).getDataSource().getConnection();

                ps = connection.prepareStatement(GET_COUPON_ID_FOR_REFEREE_SQL);
                ps.setString(1, refereePromo.getRepositoryId());
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

            if (mLogger.isLoggingDebug()) {
                mLogger.logDebug("Coupon found");
            }

            // creates referrer item
            MutableRepositoryItem referee = ((MutableRepository) mProfileRepository).createItem(ITEM_REFEREE);
            referee.setPropertyValue(PROPERTY_EMAIL, email);
            referee.setPropertyValue(PROPERTY_COUPONID, couponId);
            ((MutableRepository) mProfileRepository).addItem(referee);
            referees.add(referee);
            ((MutableRepository) mProfileRepository).updateItem((MutableRepositoryItem) referrer);

            if (mLogger.isLoggingDebug()) {
                mLogger.logDebug("Coupon assigned");
            }
        } catch (RepositoryException e) {
            throw new ProcessException("Can not send email", e);
        }  // end try-catch

        String lastName = (String) referrer.getPropertyValue(LAST_NAME_PROFILE_PROP);
        String firstName = (String) referrer.getPropertyValue(FIRST_NAME_PROFILE_PROP);
        String referrerName = lastName + " " + firstName;

        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put(EMAIL_PARAM_COUPON, couponId);
            params.put(EMAIL_PARAM_REFERRER, referrerName);

            Double adjuster = (Double) refereePromo.getPropertyValue(PROPERTY_ADJUSTER);
            params.put(EMAIL_PARAM_PROMO_AMOUNT, NUMBER_FORMAT.format(adjuster));
            String subject = String.format(Locale.FRANCE, mEmailSubject, referrerName);

            // send invitation to referee
            sendEmail(pContext, subject, mFromEmail, email, (String) getParameterValue(PARAM_TEMPLATE, pContext),
                      params);

            params = new HashMap<String, String>();
            params.put(EMAIL_PARAM_REFERRER, referrerName);

            Date expDate = (Date) refereePromo.getPropertyValue(PROPERTY_END_USABLE);

            if (expDate != null) {
                params.put(EMAIL_PARAM_EXP_DATE, DATE_FORMAT.format(expDate));
            }

            adjuster = (Double) referrerPromo.getPropertyValue(PROPERTY_ADJUSTER);
            params.put(EMAIL_PARAM_PROMO_AMOUNT, NUMBER_FORMAT.format(adjuster));

            // send confirmation to referrer
            sendEmail(pContext, subject, mFromEmail, (String) referrer.getPropertyValue(PROPERTY_EMAIL),
                      (String) getParameterValue(PARAM_REFERRER_TEMPLATE, pContext), params);
        } catch (ActionException e) {
            throw new ProcessException("Can not send email", e);
        }  // end try-catch

    }

}
