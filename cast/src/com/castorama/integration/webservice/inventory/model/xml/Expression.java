package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.*;

/**
 * @author EPAM team
 */

@XmlRootElement(name = "Expression")
@XmlAccessorType(XmlAccessType.FIELD)
public class Expression {

    @XmlValue
    private String value;
    @XmlAttribute(name = "expressionLanguage")
    private String expressionLanguage;

    public Expression() {
    }

    public Expression(String value, String expressionLanguage) {
        this.value = value;
        this.expressionLanguage = expressionLanguage;
    }

    //region Getters\Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExpressionLanguage() {
        return expressionLanguage;
    }
    //endregion
}
