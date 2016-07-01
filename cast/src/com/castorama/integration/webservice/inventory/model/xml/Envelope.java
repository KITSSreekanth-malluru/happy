package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "Envelope", namespace = XmlNamespaces.SOAPENV)
@XmlAccessorType(XmlAccessType.FIELD)
public class Envelope {

    @XmlElementRef
    private Header header;
    @XmlElementRef
    private Body body;

    public Envelope() {
    }

    public Envelope(Header header, Body body) {
        this.header = header;
        this.body = body;
    }

    //region Getters\Setters
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
    //endregion
}
