package com.castorama.payment;

import java.util.Map;

import atg.commerce.states.ObjectStates;

/**
 * Add Castorama specific Payment states.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class PaymentStates extends ObjectStates {
    /** ATOUT constant. */
    public static final String ATOUT = "atout";

    /** MIX constant. */
    public static final String MIX = "mix";

    /** CALL_CENTER constant. */
    public static final String CALL_CENTER = "call_center";

    /** CHEQUE constant. */
    public static final String CHEQUE = "cheque";

    /** VIREMENT constant. */
    public static final String VIREMENT = "virement";

    /** CADEAU constant. */
    public static final String CADEAU = "cadeau";

    /** CREDIT_CARD constant. */
    public static final String CREDIT_CARD = "creditCard";

    /** fOToPayboxStatesMap property */
    Map<String, String> mFOToPayboxStatesMap;

    /**
     * Returns FOToPayboxStatesMap property.
     *
     * @return FOToPayboxStatesMap property.
     */
    public Map<String, String> getFOToPayboxStatesMap() {
        return mFOToPayboxStatesMap;
    }

    /**
     * Sets the value of the FOToPayboxStatesMap property.
     *
     * @param pToPayboxStatesMap parameter to set.
     */
    public void setFOToPayboxStatesMap(Map<String, String> pToPayboxStatesMap) {
        mFOToPayboxStatesMap = pToPayboxStatesMap;
    }

}
