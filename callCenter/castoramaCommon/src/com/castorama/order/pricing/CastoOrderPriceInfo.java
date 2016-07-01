package com.castorama.order.pricing;

import atg.commerce.pricing.OrderPriceInfo;

/**
 * CastoOrderPriceInfo.
 * 
 * Ajout du montant de frais de preparation
 * 
 * @author Damien DURIEZ
 */

public class CastoOrderPriceInfo extends OrderPriceInfo
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2160906141409814986L;

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    protected double m_dFraisDePreparation;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Returns the FraisDePreparation.
     * 
     * @return double Le montant des frais de préparation.
     */
    public double getFraisDePreparation()
    {
        return m_dFraisDePreparation;
    }

    /**
     * Sets the FraisDePreparation.
     * 
     * @param a_dFraisDePreparation
     *            Le montant des frais de préparation.
     */
    public void setFraisDePreparation(double a_dFraisDePreparation)
    {
        m_dFraisDePreparation = a_dFraisDePreparation;
    }

    /*
     * ------------------------------------------------------------------------ [
     * Méthodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Ajout des Frais De Preparation dans le total de la commande.
     * 
     * @return double Le montant des frais de preparation dans le total de la
     *         commande.
     */
    public double getTotal()
    {
        return getAmount() + getShipping() + getTax() + getFraisDePreparation();
    }

} // end of class
