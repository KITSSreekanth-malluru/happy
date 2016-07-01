package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.*;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "InventoryBalance", namespace = XmlNamespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryBalance {

    @XmlElementRef
    private ProductItem productItem;
    @XmlElement(name = "AvailableQuantity", namespace = XmlNamespaces.OA)
    private Integer availableQuantity;

    public InventoryBalance() {
    }

    public InventoryBalance(ProductItem productItem, Integer availableQuantity) {
        this.productItem = productItem;
        this.availableQuantity = availableQuantity;
    }

    public InventoryBalance(ProductItem productItem) {
        this.productItem = productItem;
    }

    public InventoryBalance(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    //region Getters\Setters
    public ProductItem getProductItem() {
        return productItem;
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem = productItem;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    //endregion
}
