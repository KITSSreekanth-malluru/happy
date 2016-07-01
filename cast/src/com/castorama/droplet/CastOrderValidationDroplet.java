package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.commerce.order.CastOrderHolder;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class CastOrderValidationDroplet extends DynamoServlet {
    /** CHANGED constant. */
    public static final String CHANGED = "changed";

    /** shoppingCart property. */
    private CastOrderHolder shoppingCart;

    /**
     *
     * @param  req parameter
     * @param  res parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) throws ServletException,
                                                                                                      IOException {
        if (getShoppingCart().validateCurrentOrder()) {
            req.serviceLocalParameter(CHANGED, req, res);
        }
    }

    /**
     * Returns shoppingCart property.
     *
     * @return shoppingCart property.
     */
    public CastOrderHolder getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Sets the value of the shoppingCart property.
     *
     * @param shoppingCart parameter to set.
     */
    public void setShoppingCart(CastOrderHolder shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

}
