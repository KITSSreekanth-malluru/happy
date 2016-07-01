package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "ShowInventoryBalance", namespace = XmlNamespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class ShowInventoryBalance extends InventoryBalanceOperation {

    public ShowInventoryBalance() {
    }

    public ShowInventoryBalance(ApplicationArea applicationArea, DataArea dataArea) {
        super(applicationArea, dataArea);
    }
}
