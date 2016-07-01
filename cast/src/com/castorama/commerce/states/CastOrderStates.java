package com.castorama.commerce.states;

import atg.commerce.states.OrderStates;

/**
 * Add Castorama FO specific order states.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastOrderStates extends OrderStates {
    /** PENDING_CALL_CENTER constant. */
    public static final String PENDING_CALL_CENTER = "pending_call_center";

    /** PENDING_VIREMENT constant. */
    public static final String PENDING_VIREMENT = "pending_virement";

    /** PENDING_CHEQUE constant. */
    public static final String PENDING_CHEQUE = "pending_cheque";

    /** PENDING_PAYBOX constant. */
    public static final String PENDING_PAYBOX = "pending_paybox";

}
