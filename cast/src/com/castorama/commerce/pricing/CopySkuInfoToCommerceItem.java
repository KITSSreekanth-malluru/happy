package com.castorama.commerce.pricing;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;

/**
 * Copying information from a sku to a trade item. The purpose of this copy is
 * to get this information to control the descent in the back office. Once this
 * information in order copies, they are no longer subject to evolving Catalog
 *
 * @author Epam Team
 */

public class CopySkuInfoToCommerceItem {
    /**
     * Prevents instantiation of the class
     */
    private CopySkuInfoToCommerceItem() {
    }

    /**
     * Copy sku info to commerce item.
     *
     * @param  pItem The commerceItem copy.
     *
     * @throws PropertyNotFoundException exception
     */
    public static void copySkuInfoToCommerceItem(CommerceItem pItem) throws PropertyNotFoundException {
        // Trace.logOpen("CopySkuInfoToCommerceItem.copySkuInfoToCommerceItem() item="+a_Item);

        CommerceItemImpl Item = (CommerceItemImpl) pItem;

        Object catalogRef = Item.getAuxiliaryData().getCatalogRef();
        Object poids = DynamicBeans.getPropertyValue(catalogRef, "PoidsUV");
        Object horsNormes = DynamicBeans.getPropertyValue(catalogRef, "horsNormes");
        Object exonerationPFE = DynamicBeans.getPropertyValue(catalogRef, "exonerationPFE");
        Object exonerationPFT = DynamicBeans.getPropertyValue(catalogRef, "exonerationPFT");
        Object sommeForf = DynamicBeans.getPropertyValue(catalogRef, "sommeForfaitaire");
        Object estUnCadeau = DynamicBeans.getPropertyValue(catalogRef, "cadeau");
        Object codeArticle = DynamicBeans.getPropertyValue(catalogRef, "codeArticle");
        Object enVente = DynamicBeans.getPropertyValue(catalogRef, "enVente");
        Object dateFinVie = DynamicBeans.getPropertyValue(catalogRef, "dateFinVie");
        Object coefficientDeSatisfaction = DynamicBeans.getPropertyValue(catalogRef, "CoefficientDeSatisfaction");

        // info copy of the sku in the commerceItem
        synchronized (Item) {
            if (poids != null) {
                Item.setPropertyValue("poids", poids);
            }
            if (horsNormes != null) {
                Item.setPropertyValue("horsNormes", horsNormes);
            }

            if (exonerationPFE != null) {
                Item.setPropertyValue("exonerationPFE", exonerationPFE);
            }

            if (exonerationPFT != null) {
                Item.setPropertyValue("exonerationPFT", exonerationPFT);
            }

            if (sommeForf != null) {
                Item.setPropertyValue("sommeForfaitaire", sommeForf);
            }

            if (estUnCadeau != null) {
                Item.setPropertyValue("cadeau", estUnCadeau);
            }

            if (codeArticle != null) {
                Item.setPropertyValue("codeArticle", codeArticle);
            }

            if (enVente != null) {
                Item.setPropertyValue("enVente", enVente);
            }

            if (dateFinVie != null) {
                Item.setPropertyValue("dateFinVie", dateFinVie);
            }

            if (coefficientDeSatisfaction != null) {
                Item.setPropertyValue("CoefficientDeSatisfaction", coefficientDeSatisfaction);
            }

        }

    }
}
