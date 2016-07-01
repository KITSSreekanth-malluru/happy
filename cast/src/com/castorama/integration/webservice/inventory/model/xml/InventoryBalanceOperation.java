package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;

/**
 * @author EPAM team
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class InventoryBalanceOperation {

    @XmlElementRef
    private ApplicationArea applicationArea;
    @XmlElementRef
    private DataArea dataArea;
    @XmlAttribute(name = "releaseID")
    private String releaseId = "1.0";
    @XmlAttribute(name = "versionID")
    private String versionID = "2.0";
    @XmlAttribute(name = "systemEnvironmentCode")
    private String systemEnvironmentCode = "Production";
    @XmlAttribute(name = "languageCode")
    private String languageCode = "en-US";

    public InventoryBalanceOperation() {
    }

    public InventoryBalanceOperation(ApplicationArea applicationArea, DataArea dataArea) {
        this.applicationArea = applicationArea;
        this.dataArea = dataArea;
    }

    //region Getters\Setters
    public ApplicationArea getApplicationArea() {
        return applicationArea;
    }

    public void setApplicationArea(ApplicationArea applicationArea) {
        this.applicationArea = applicationArea;
    }

    public DataArea getDataArea() {
        return dataArea;
    }

    public void setDataArea(DataArea dataArea) {
        this.dataArea = dataArea;
    }

    public String getReleaseID() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getVersionID() {
        return versionID;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public String getSystemEnvironmentCode() {
        return systemEnvironmentCode;
    }

    public void setSystemEnvironmentCode(String systemEnvironmentCode) {
        this.systemEnvironmentCode = systemEnvironmentCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    //endregion
}
