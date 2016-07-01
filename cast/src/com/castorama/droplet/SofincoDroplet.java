package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.castorama.payment.PaymentConfiguration;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Generates Sofinco url
 *
 * @author Vasili_Ivus
 */
public class SofincoDroplet extends DynamoServlet {
    /** name of oparam when not empty output */
    public static final String OUTPUT = "output";

    /** name of oparam when empty output */
    public static final String EMPTY = "empty";

    /** name of expected param for order amount */
    public static final String ORDER_AMOUNT = "orderAmount";

    /** name of expected param for order reference */
    public static final String ORDER_REFERENCE = "orderReference";

    /** name of out param for sofinco url string */
    public static final String URL_STRING = "urlString";

    /** payment configuration bean */
    private PaymentConfiguration mPaymentConfiguration;

    /**
     * Writes sofinco url
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String result = null;

        try {
            String orderAmount = pRequest.getParameter(ORDER_AMOUNT);
            String orderReference = pRequest.getParameter(ORDER_REFERENCE);

            PaymentConfiguration paymentConfiguration = getPaymentConfiguration();

            String extQuery = paymentConfiguration.getQueryAmount() + "=" + orderAmount;
            extQuery += "&" + paymentConfiguration.getQueryReference() + "=" + orderReference;

            String sofincoURL = paymentConfiguration.getSofincoURL();
            int i = sofincoURL.indexOf("?");
            if (0 > i) {
                result = sofincoURL + "?" + extQuery;
            } else {
                result = sofincoURL + "&" + extQuery;
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError(e);
            }
        }  // end try-catch

        if ((null != result) && (0 < result.trim().length())) {
            pRequest.setParameter(URL_STRING, result);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }

    }

    /**
     * Gets payment configuration
     *
     * @return payment configuration
     */
    public PaymentConfiguration getPaymentConfiguration() {
        return mPaymentConfiguration;
    }

    /**
     * Sets payment configuration
     *
     * @param pPaymentConfiguration payment configuration
     */
    public void setPaymentConfiguration(PaymentConfiguration pPaymentConfiguration) {
        this.mPaymentConfiguration = pPaymentConfiguration;
    }

}
