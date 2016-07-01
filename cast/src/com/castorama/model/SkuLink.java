package com.castorama.model;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/**
 *
 * @author Andrew_Logvinov
 */
public class SkuLink extends RepositoryItemWrapper {
    /** ITEM constant. */
    private static final String ITEM = "item";

    /** QUANTITY constant. */
    private static final String QUANTITY = "quantity";

    /**
     * Creates a new SkuLink object.
     *
     * @param repositoryItem
     */
    public SkuLink(RepositoryItem repositoryItem) {
        super(repositoryItem);
    }

    /**
     * Returns instance property.
     *
     * @param  linkItem parameter to set.
     *
     * @return instance property.
     */
    public static SkuLink getInstance(RepositoryItem linkItem) {
        return (null == linkItem) ? null : new SkuLink(linkItem);
    }

    /**
     * Returns item property.
     *
     * @return item property.
     */
    public CastoSku getItem() {
        return CastoSku.getInstance((RepositoryItem) repositoryItem.getPropertyValue(ITEM));
    }

    /**
     * Sets the value of the item property.
     *
     * @param item parameter to set.
     */
    public void setItem(CastoSku item) {
        ((MutableRepositoryItem) repositoryItem).setPropertyValue(ITEM, item.getRepositoryItem());
    }

    /**
     * Returns quantity property.
     *
     * @return quantity property.
     */
    public Long getQuantity() {
        return (Long) repositoryItem.getPropertyValue(QUANTITY);
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param quantity parameter to set.
     */
    public void setQuantity(Long quantity) {
        ((MutableRepositoryItem) repositoryItem).setPropertyValue(QUANTITY, quantity);
    }
}
