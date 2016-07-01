package com.castorama.pricing;

import atg.nucleus.logging.ApplicationLoggingImpl;

/**
 * Composant qui permet de mapper les paramètres pour les frais de préparation
 * appliqués dans les magasins (en mode mise à disposition).
 * 
 * @author Logica
 * @version 0.1
 */
public class ParametresFraisPreparationMagasin extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------
     * Attributs
     * ------------------------------------------------------------------------
     */

    private double m_montantForfaitaire;

    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    
    /**
     * Méthode qui renvoie le montant forfaitaire appliqué.
     * 
     * @return Le montant forfaitaire appliqué.
     */
    public double getMontantForfaitaire()
    {
        return m_montantForfaitaire;
    }

    /**
     * Méthode qui fixe le montant forfaitaire appliqué.
     * 
     * @param a_montantForfaitaire
     *            Le nouveau montant forfaitaire apppliqué.
     */
    public void setMontantForfaitaire(double a_montantForfaitaire)
    {
        m_montantForfaitaire = a_montantForfaitaire;
    }
}