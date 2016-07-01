package com.castorama.payment;

import atg.commerce.states.ObjectStates;

/**
 * Add Castorama specific Paybox Payment states.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class PayboxPaymentStates extends ObjectStates {
    /** VISA constant. */
    public static final String VISA = "visa";

    /** AMEX constant. */
    public static final String AMEX = "amex";

    /** EUROCARD_MASTERCARD constant. */
    public static final String EUROCARD_MASTERCARD = "eurocard_mastercard";

    /** CB constant. */
    public static final String CB = "cb";

    /** SVS constant. */
    public static final String SVS = "svs";

    /** SOFINCO constant. */
    public static final String SOFINCO = "sofinco";
}
