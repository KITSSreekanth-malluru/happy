package com.castorama.order.pricing;

import atg.commerce.pricing.AmountInfo;

/**
 * PreparationPriceInfo :.
 * 
 * @author DURIEZ Damien
 */
public class PreparationPriceInfo extends AmountInfo
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2988312387996566017L;

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    protected int m_nPoids;

    protected double m_dMontantAvantPromo;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Returns the Poids.
     * 
     * @return int Le poids.
     */
    public int getPoids()
    {
        return m_nPoids;
    }

    /**
     * Sets the Poids.
     * 
     * @param a_nPoids
     *            Le poids.
     */
    public void setPoids(int a_nPoids)
    {
        m_nPoids = a_nPoids;
    }

    /**
     * Returns the MontantAvantPromo.
     * 
     * @return double Le montant avant promo.
     */
    public double getMontantAvantPromo()
    {
        return m_dMontantAvantPromo;
    }

    /**
     * Sets the MontantAvantPromo.
     * 
     * @param a_dMontantAvantPromo
     *            Le nouveau montant avant promo.
     */
    public void setMontantAvantPromo(double a_dMontantAvantPromo)
    {
        m_dMontantAvantPromo = a_dMontantAvantPromo;
    }
}