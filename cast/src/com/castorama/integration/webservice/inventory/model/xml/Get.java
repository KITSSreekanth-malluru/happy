package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "Get", namespace = XmlNamespaces.OA)
@XmlAccessorType(XmlAccessType.FIELD)
public class Get {

    @XmlElement(name = "Expression", namespace = XmlNamespaces.OA)
    private Expression expression;

    public Get() {
    }

    public Get(Expression expression) {
        this.expression = expression;
    }

    //region Getters\Setters
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
    //endregion
}
