package com.castorama.integration.webservice.inventory.message;

import java.util.Date;
import java.util.List;

/**
 * @author EPAM team
 */
public class InventoryResponseMessage extends InventoryMessageBase {

    private String bodId;
    List<InventoryUnit> unitList;

    public InventoryResponseMessage(String languageCode,
                                    Date creationDateTime,
                                    int storeId,
                                    String bodId,
                                    List<InventoryUnit> unitList) {
        super(languageCode, creationDateTime, storeId);
        this.bodId = bodId;
        this.unitList = unitList;
    }

    public String getBodId() {
        return bodId;
    }

    public void setBodId(String bodId) {
        this.bodId = bodId;
    }

    public List<InventoryUnit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<InventoryUnit> unitList) {
        this.unitList = unitList;
    }

    @Override
    public String toString() {
        return "InventoryResponseMessage{" +
                "bodId='" + bodId + '\'' +
                "} " + super.toString();
    }
}
