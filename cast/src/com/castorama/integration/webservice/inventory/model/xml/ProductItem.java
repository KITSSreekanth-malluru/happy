package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "ProductItem", namespace = XmlNamespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductItem {

    @XmlElement(name = "UPCID", namespace = XmlNamespaces.OA)
    private Integer id;

    public ProductItem() {
    }

    public ProductItem(Integer id) {
        this.id = id;
    }

    //region Getters\Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    //endregion
}
