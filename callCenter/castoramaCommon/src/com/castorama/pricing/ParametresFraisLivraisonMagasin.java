package com.castorama.pricing;

import atg.nucleus.logging.ApplicationLoggingImpl;

/**
 * Composant qui permet de mapper les paramètres pour les frais de livraison
 * appliqués dans les magasins.
 * 
 * @author Logica
 * @version 0.1
 */
public class ParametresFraisLivraisonMagasin extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    private double m_pourcentage;

    private double m_plancherMini;

    private double m_plancherMaxi;

    private double m_fraisLivraisonEtage;

    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    /**
     * Méthode qui renvoie le pourcentage appliqué.
     * 
     * @return Le pourcentage appliqué.
     */
    public double getPourcentage()
    {
        return m_pourcentage;
    }

    /**
     * Méthode qui fixe le pourcentage appliqué.
     * 
     * @param a_pourcentage
     *            Le nouveau pourcentage apppliqué.
     */
    public void setPourcentage(double a_pourcentage)
    {
        m_pourcentage = a_pourcentage;
    }

    /**
     * Méthode qui renvoie le montant du plancher mini.
     * 
     * @return Le montant du plancher mini.
     */
    public double getPlancherMini()
    {
        return m_plancherMini;
    }

    /**
     * Méthode qui fixe le montant du plancher midi.
     * 
     * @param a_plancherMini
     *            Le nouveau montant du plancher mini.
     */
    public void setPlancherMini(double a_plancherMini)
    {
        m_plancherMini = a_plancherMini;
    }

    /**
     * Méthode qui renvoie le montant du plancher maxi.
     * 
     * @return Le montant du plancher maxi.
     */
    public double getPlancherMaxi()
    {
        return m_plancherMaxi;
    }

    /**
     * Méthode qui fixe le montant du plancher maxi.
     * 
     * @param a_plancherMaxi
     *            Le nouveau montant du plancher maxi.
     */
    public void setPlancherMaxi(double a_plancherMaxi)
    {
        m_plancherMaxi = a_plancherMaxi;
    }

    /**
     * Méthode qui renvoie le montant des frais supplémentaires pour les
     * livraisons à l'étage.
     * 
     * @return Le montant des frais supplémentaires pour les livraisons à
     *         l'étage.
     */
    public double getFraisLivraisonEtage()
    {
        return m_fraisLivraisonEtage;
    }

    /**
     * Méthode qui fixe le montant des frais supplémentaires pour les livraisons
     * à l'étage.
     * 
     * @param a_fraisLivraisonEtage
     *            Le nouveau montant des frais supplémentaires pour les
     *            livraisons à l'étage.
     */
    public void setFraisLivraisonEtage(double a_fraisLivraisonEtage)
    {
        m_fraisLivraisonEtage = a_fraisLivraisonEtage;
    }
}