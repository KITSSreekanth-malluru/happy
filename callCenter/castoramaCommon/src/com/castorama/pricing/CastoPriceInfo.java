package com.castorama.pricing;

/**
 * Classe qui permet de mapper un prix.<br />
 * 
 * @author Unilog
 * @version 0.1
 */
public class CastoPriceInfo
{
    /*
     * ------------------------------------------------------------------------
     * Atttributs
     * ------------------------------------------------------------------------
     */

    private double m_listPrice;

    private double m_salePrice;

    private boolean m_onSale;
    
    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    /**
     * Méthode qui renvoie le list price.
     * 
     * @return Le list price.
     */
    public double getListPrice()
    {
        return m_listPrice;
    }

    /**
     * Méthode qui fixe le list price.
     * 
     * @param a_listPrice
     *            Le nouveau list price.
     */
    public void setListPrice(double a_listPrice)
    {
        m_listPrice = a_listPrice;
    }

    /**
     * Méthode qui indique si le prix est barré ou non.
     * 
     * @return true si le prix est barré, false sinon.
     */
    public boolean isOnSale()
    {
        return m_onSale;
    }

    /**
     * Méthode qui fixe si le prix est barré ou non.
     * 
     * @param a_onSale
     *            true si le prix est barré, false sinon.
     */
    public void setOnSale(boolean a_onSale)
    {
        m_onSale = a_onSale;
    }

    /**
     * Méthode qui renvoie le sale price.
     * 
     * @return Le sale price.
     */
    public double getSalePrice()
    {
        return m_salePrice;
    }

    /**
     * Méthode qui fixe le sale price.
     * 
     * @param a_salePrice
     *            Le nouveau sale price.
     */
    public void setSalePrice(double a_salePrice)
    {
        m_salePrice = a_salePrice;
    }
}