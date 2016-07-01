package com.castorama.commande;

import com.castorama.constantes.CastoConstantesDefense;

import atg.commerce.order.HardgoodShippingGroup;

/**
 * Extension de la classe HardgoodShippingGroup (pour la gestion des propriétés
 * spécifiques).
 * 
 * @author Logica.
 * @version 0.1.
 */
public class CastoHardgoodShippingGroup extends HardgoodShippingGroup
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6765399694859701098L;

    /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */

    /**
     * Méthode qui renvoie le poids total du Shipping group.
     * 
     * @return Le poids total du Shipping group.
     */
    public int getPoidsTotal()
    {
        Object l_prop = getPropertyValue(CastoConstantesDefense.SHIPPPING_GROUP_POIDS_TOTAL);

        if (null != l_prop)
        {
            return ((Integer) l_prop).intValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le poids total du Shipping Group.
     * 
     * @param a_poidsTotal
     *            Le nouveau poids total du Shipping Group.
     */
    public void setPoidsTotal(int a_poidsTotal)
    {
        setPropertyValue(CastoConstantesDefense.SHIPPPING_GROUP_POIDS_TOTAL, new Integer(a_poidsTotal));
    }

    /**
     * Méthode qui renvoie le délai d'engagement du Shipping Group.
     * 
     * @return Le délai d'engagement du Shipping Group.
     */
    public int getDelaiEngagement()
    {
        Object l_prop = getPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_DELAI_ENGAGEMENT);

        if (null != l_prop)
        {
            return ((Integer) l_prop).intValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le délai d'engagement du Shipping Group.
     * 
     * @param a_delaiEngagement
     *            Le nouveau délai d'engagement du Shipping Group.
     */
    public void setDelaiEngagement(int a_delaiEngagement)
    {
        setPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_DELAI_ENGAGEMENT, new Integer(a_delaiEngagement));
    }

    /**
     * Méthode qui renvoie le délai d'engagement du Shipping Group.
     * 
     * @return Le délai d'engagement du Shipping Group.
     */
    public String getLibelleDelaiEngagement()
    {
        Object l_prop = getPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_LIBELLE_DELAI_ENGAGEMENT);

        if (null != l_prop)
        {
            return l_prop.toString();
        }
        return "";
    }

    /**
     * Méthode qui fixe le délai d'engagement du Shipping Group.
     * 
     * @param a_libelleDelaiEngagement
     *            Le nouveau délai d'engagement du Shipping Group.
     */
    public void setLibelleDelaiEngagement(String a_libelleDelaiEngagement)
    {
        setPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_LIBELLE_DELAI_ENGAGEMENT, a_libelleDelaiEngagement);
    }

    /**
     * Méthode qui renvoie la préférence de retrait du Shipping Group.
     * 
     * @return La préférence de retrait du Shipping Group.
     */
    public String getPreferenceClientRetraitMAD()
    {
        Object l_prop = getPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_PREFERENCE_CLIENT_RETRAIT);

        if (null != l_prop)
        {
            return l_prop.toString();
        }
        return "";
    }

    /**
     * Méthode qui fixe la préférence de retrait du Shipping Group.
     * 
     * @param a_preferenceClientRetraitMAD
     *            La préférence de retrait du Shipping Group.
     */
    public void setPreferenceClientRetraitMAD(String a_preferenceClientRetraitMAD)
    {
        setPropertyValue(CastoConstantesDefense.SHIPPING_GROUP_PREFERENCE_CLIENT_RETRAIT, a_preferenceClientRetraitMAD);
    }
}