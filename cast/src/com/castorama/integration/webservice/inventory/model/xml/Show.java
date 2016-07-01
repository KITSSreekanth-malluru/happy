package com.castorama.integration.webservice.inventory.model.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author EPAM team
 */
@XmlRootElement(name = "Show", namespace = XmlNamespaces.OA)
@XmlAccessorType(XmlAccessType.FIELD)
public class Show {

    @XmlElement(name = "Expression", namespace = XmlNamespaces.OA)
    private Expression expression;
    @XmlElement(name = "ActionExpression", namespace = XmlNamespaces.OA)
    private Expression actionExpression;

    public Show() {
    }

    public Show(Expression expression, Expression actionExpression) {
        this.expression = expression;
        this.actionExpression = actionExpression;
    }

    //region Getters\Setters
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getActionExpression() {
        return actionExpression;
    }

    public void setActionExpression(Expression actionExpression) {
        this.actionExpression = actionExpression;
    }
    //endregion
}
