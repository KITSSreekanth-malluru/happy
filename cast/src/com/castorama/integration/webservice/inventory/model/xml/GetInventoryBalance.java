package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "GetInventoryBalance", namespace = XmlNamespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInventoryBalance extends InventoryBalanceOperation {

    public GetInventoryBalance() {
    }

    public GetInventoryBalance(ApplicationArea applicationArea, DataArea dataArea) {
        super(applicationArea, dataArea);
    }
}
