package com.castorama.commerce.states;

import atg.commerce.states.ObjectStates;

/**
 * Determine BO specific order states. 
 *
 * @author Katsiaryna Sharstsiuk
 */
public class BOOrderStates extends ObjectStates {
    /** PENDING_CALL_CENTER constant. */
    public static final String PENDING_CALL_CENTER = "pending_call_center";

    /** PENDING_VIREMENT constant. */
    public static final String PENDING_VIREMENT = "pending_virement";

    /** PENDING_CHEQUE constant. */
    public static final String PENDING_CHEQUE = "pending_cheque";

    /** VALIDE constant. */
    public static final String VALIDE = "valide";

    /** PENDING_REMOVE constant. */
    public static final String PENDING_REMOVE = "pending_remove";

    /** INCOMPLETE constant. */
    public static final String INCOMPLETE = "incomplete";

    /** EN_SUSPENS constant. */
    public static final String EN_SUSPENS = "en_suspens";

    /** A_CONTROLER constant. */
    public static final String A_CONTROLER = "a_controler";

    /** EN_ANOMALIE_STOCK constant. */
    public static final String EN_ANOMALIE_STOCK = "en_anomalie_stock";

    /** EN_ANOMALIE_FACTURE constant. */
    public static final String EN_ANOMALIE_FACTURE = "en_anomalie_facture";

    /** EN_PREPARATION constant. */
    public static final String EN_PREPARATION = "en_preparation";

    /** EN_PREPARATION_1 constant. */
    public static final String EN_PREPARATION_1 = "en_preparation_1";

    /** EN_PREPARATION_2 constant. */
    public static final String EN_PREPARATION_2 = "en_preparation_2";

    /** EXPEDIEE constant. */
    public static final String EXPEDIEE = "expediee";

    /** TERMINEE constant. */
    public static final String TERMINEE = "terminee";

    /** PENDING_PAYBOX constant. */
    public static final String PENDING_PAYBOX = "pending_paybox";
    
    /** FAILED_PAYBOX constant. */
    public static final String FAILED = "failed";

    /**
     * Creates a new BOOrderStates object.
     */
    public BOOrderStates() {
    }

}
