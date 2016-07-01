package com.castorama.order.pricing;

import java.util.Locale;
import java.util.Map;

import atg.commerce.order.Order;
import atg.commerce.pricing.PricingException;
import atg.repository.RepositoryItem;

/**
 * PreparationPricingCalculator :.
 * 
 * @author DURIEZ Damien
 */
public interface PreparationPricingCalculator
{

    /**
     * Méthode pour le princing des frais de préparation.
     * 
     * @param a_reparationpriceinfo
     *            ??.
     * @param a_order
     *            La commande.
     * @param a_repositoryitem
     *            ??.
     * @param a_locale
     *            La locale.
     * @param a_repositoryitem1
     *            ??.
     * @param a_map
     *            ??.
     * 
     * @throws PricingException
     *             Si une exception survient.
     */
    void pricePreparation(PreparationPriceInfo a_reparationpriceinfo, Order a_order, RepositoryItem a_repositoryitem,
            Locale a_locale, RepositoryItem a_repositoryitem1, Map a_map) throws PricingException;
}