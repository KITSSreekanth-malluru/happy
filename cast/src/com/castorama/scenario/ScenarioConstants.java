package com.castorama.scenario;

import java.text.SimpleDateFormat;

/**
 * Common for all scenario classes constants
 *
 * @author EPAM team
 */
public interface ScenarioConstants {
    /** EMAIL_PARAM_ORDER_LIMIT constant. */
    String EMAIL_PARAM_ORDER_LIMIT = "orderLimit";

    /** EMAIL_PARAM_REFEREE constant. */
    String EMAIL_PARAM_REFEREE = "referee";

    /** SLASH constant. */
    String SLASH = "/";

    /** COLON constant. */
    String COLON = ":";

    /** CONTEXT_ROOT_WEB_APP_PROPERTY constant. */
    String CONTEXT_ROOT_WEB_APP_PROPERTY = "context-root";

    /** PARAM_MAILING_NAME constant. */
    String PARAM_MAILING_NAME = "mailingName";

    /** PARAM_MESSAGE_SUBJECT constant. */
    String PARAM_MESSAGE_SUBJECT = "messageSubject";

    /** PARAM_MESSAGE_FROM constant. */
    String PARAM_MESSAGE_FROM = "messageFrom";

    /** PARAM_MESSAGE_CC constant. */
    String PARAM_MESSAGE_CC = "messageCc";

    /** MESSAGE_PARAM_NAME constant. */
    String MESSAGE_PARAM_NAME = "message";

    /** PARAM_TEMPLATE constant. */
    String PARAM_TEMPLATE = "template";

    /** PARAM_REFERRER_TEMPLATE constant. */
    String PARAM_REFERRER_TEMPLATE = "referrerTemplate";

    /** EMAIL_PARAM_COUPON constant. */
    String EMAIL_PARAM_COUPON = "coupon";

    /** EMAIL_PARAM_REFERRER constant. */
    String EMAIL_PARAM_REFERRER = "referrer";

    /** PROPERTY_COUPONID constant. */
    String PROPERTY_COUPONID = "couponid";

    /** EMAIL_PARAM_PROMO_AMOUNT constant. */
    String EMAIL_PARAM_PROMO_AMOUNT = "promoAmount";

    /** EMAIL_PARAM_EXP_DATE constant. */
    String EMAIL_PARAM_EXP_DATE = "expDate";

    /** PROPERTY_END_USABLE constant. */
    String PROPERTY_END_USABLE = "endUsable";

    /** PROPERTY_ADJUSTER constant. */
    String PROPERTY_ADJUSTER = "adjuster";

    /** PROPERTY_EMAIL constant. */
    String PROPERTY_EMAIL = "email";

    /** PROPERTY_REFEREE constant. */
    String PROPERTY_REFEREE = "referee";

    /** ITEM_REFEREE constant. */
    String ITEM_REFEREE = "referee";

    /** FIND_REFEREES_BY_EMAIL_RQL constant. */
    String FIND_REFEREES_BY_EMAIL_RQL = "email EQUALS IGNORECASE ?0 and applied=true";

    /** GET_COUPON_ID_FOR_REFEREE_SQL constant. */
    /** OLD SELECT COUPON_ID FROM DCSPP_COUPON WHERE PROMOTION_ID=? AND ROWNUM <= 1 AND COUPON_ID NOT IN (SELECT COUPON_ID FROM CASTO_REFEREE)*/
    String GET_COUPON_ID_FOR_REFEREE_SQL =
        "SELECT CPN.COUPON_ID FROM DCSPP_COUPON CPN WHERE CPN.PROMOTION_ID=? AND ROWNUM <= 1 AND NOT EXISTS (SELECT NULL FROM CASTO_REFEREE CR WHERE CR.COUPON_ID=CPN.COUPON_ID)";

    /** GET_COUPON_ID_FOR_REFERRER_SQL constant. */
    /** OLD SELECT COUPON_ID FROM DCSPP_COUPON WHERE PROMOTION_ID=? AND ROWNUM <= 1 AND COUPON_ID NOT IN (SELECT REF_COUPON_ID FROM CASTO_REFEREE WHERE REF_COUPON_ID IS NOT NULL)*/
    String GET_COUPON_ID_FOR_REFERRER_SQL =
        "SELECT CPN.COUPON_ID FROM DCSPP_COUPON CPN WHERE CPN.PROMOTION_ID=? AND ROWNUM <= 1 AND NOT EXISTS (SELECT NULL FROM CASTO_REFEREE CR WHERE CR.REF_COUPON_ID=CPN.COUPON_ID)";

    /** DATE_FORMAT constant. */
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");

}
