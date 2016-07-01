package com.castorama.integration.webservice.inventory.model.xml;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "ApplicationArea", namespace = XmlNamespaces.OA)
@XmlAccessorType(XmlAccessType.FIELD)
public class ApplicationArea {

    @XmlElement(name = "CreationDateTime", namespace = XmlNamespaces.OA)
    private String creationDateTime;
    @XmlElement(name = "BODID", namespace = XmlNamespaces.OA)
    private String bodId;

    public ApplicationArea() {
    }

    public ApplicationArea(String creationDateTime, String bodId) {
        this.creationDateTime = creationDateTime;
        this.bodId = bodId;
    }

    //region Getters\Setters
    public ApplicationArea(String creationDateTime) {
        this.creationDateTime = creationDateTime;
        this.bodId = null;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getBodId() {
        return bodId;
    }

    public void setBodId(String bodId) {
        this.bodId = bodId;
    }
    //endregion
}
