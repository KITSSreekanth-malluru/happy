package com.castorama.scenario.configuration;

import atg.commerce.order.OrderManager;

import atg.nucleus.GenericService;

/**
 *
 * @author Andrei_Raichonak
 */
public class FirstOrderFilterConfiguration extends GenericService {
    /** orderManager property. */
    private OrderManager mOrderManager;

    /**
     * Returns orderManager property.
     *
     * @return orderManager property.
     */
    public OrderManager getOrderManager() {
        return mOrderManager;
    }

    /**
     * Sets the value of the orderManager property.
     *
     * @param orderManager parameter to set.
     */
    public void setOrderManager(OrderManager orderManager) {
        mOrderManager = orderManager;
    }
}
