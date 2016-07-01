package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "Body", namespace = XmlNamespaces.SOAPENV)
@XmlAccessorType(XmlAccessType.FIELD)
public class Body {

    @XmlElementRef
    GetInventoryBalance getInventoryBalance;
    @XmlElementRef
    ShowInventoryBalance showInventoryBalance;

    public Body() {
    }

    public Body(GetInventoryBalance getInventoryBalance, ShowInventoryBalance showInventoryBalance) {
        this.getInventoryBalance = getInventoryBalance;
        this.showInventoryBalance = showInventoryBalance;
    }

    public Body(GetInventoryBalance getInventoryBalance) {
        this.getInventoryBalance = getInventoryBalance;
        this.showInventoryBalance = null;
    }

    public Body(ShowInventoryBalance showInventoryBalance) {
        this.getInventoryBalance = null;
        this.showInventoryBalance = showInventoryBalance;
    }

    //region Getters\Setters
    public GetInventoryBalance getGetInventoryBalance() {
        return getInventoryBalance;
    }

    public void setGetInventoryBalance(GetInventoryBalance getInventoryBalance) {
        this.getInventoryBalance = getInventoryBalance;
    }

    public ShowInventoryBalance getShowInventoryBalance() {
        return showInventoryBalance;
    }

    public void setShowInventoryBalance(ShowInventoryBalance showInventoryBalance) {
        this.showInventoryBalance = showInventoryBalance;
    }
    //endregion
}
