
package com.castorama.commerce.order;

import atg.commerce.order.HardgoodShippingGroup;

import com.castorama.constantes.CastoConstantesCommande;

/**
 * Castorama extension of HardgoodShippingGroup for adding Castorama specific
 * properties to the HardgoodShippingGroup
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastHardgoodShippingGroup extends HardgoodShippingGroup {
    /**
     * Returns adresseLibelle property.
     *
     * @return adresseLibelle property.
     */
    public String getAdresseLibelle() {
        Object prop = getPropertyValue(CastoConstantesCommande.ADRESSE_LIBELLE);

        if (prop != null) {
            return prop.toString();
        }
        return "";
    }

    /**
     * Sets the value of the adresseLibelle property.
     *
     * @param pAdresseLibelle parameter to set.
     */
    public void setAdresseLibelle(String pAdresseLibelle) {
        setPropertyValue(CastoConstantesCommande.ADRESSE_LIBELLE, pAdresseLibelle);
    }

}
