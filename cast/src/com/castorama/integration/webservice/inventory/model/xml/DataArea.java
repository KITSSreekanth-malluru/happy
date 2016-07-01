package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "DataArea", namespace = XmlNamespaces.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class DataArea {

    @XmlElement(name = "FulfilmentSiteID", namespace = XmlNamespaces.DEFAULT)
    private int fulfilmentSiteID;
    @XmlElementRef
    private List<InventoryBalance> inventoryBalances;
    @XmlElementRef
    private Get get;
    @XmlElementRef
    private Show show;

    public DataArea() {
    }

    public DataArea(int fulfilmentSiteID, Get get) {
        this.fulfilmentSiteID = fulfilmentSiteID;
        this.inventoryBalances = new ArrayList<InventoryBalance>();
        this.get = get;
        this.show = null;
    }

    public DataArea(int fulfilmentSiteID, Show show) {
        this.fulfilmentSiteID = fulfilmentSiteID;
        this.inventoryBalances = new ArrayList<InventoryBalance>();
        this.get = null;
        this.show = show;
    }

    //region Getters\Setters
    public void addInventoryBalance(InventoryBalance balance) {
        inventoryBalances.add(balance);
    }

    public int getFulfilmentSiteID() {
        return fulfilmentSiteID;
    }

    public void setFulfilmentSiteID(int fulfilmentSiteID) {
        this.fulfilmentSiteID = fulfilmentSiteID;
    }

    public List<InventoryBalance> getInventoryBalances() {
        return inventoryBalances;
    }

    public void setInventoryBalances(List<InventoryBalance> inventoryBalances) {
        this.inventoryBalances = inventoryBalances;
    }

    public Get getGet() {
        return get;
    }

    public void setGet(Get get) {
        this.get = get;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }
    //endregion
}
