package com.castorama.commerce.pricing;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Cast resources constant
 *
 * @author EPAM team
 */
public class Constants extends atg.commerce.pricing.Constants {
    /** castResources constant. */
    static ResourceBundle castResources =
        ResourceBundle.getBundle("com.castorama.commerce.pricing.Resources",
                                 atg.service.dynamo.LangLicense.getLicensedDefault());

    /** HANDLING_PRICE_ADJUSTMENT_DESCRIPTION constant. */
    public static final String HANDLING_PRICE_ADJUSTMENT_DESCRIPTION =
        getCASTStringResource("handlingPriceAdjustmentDescription");

    /**
     * Return bundled resource property for passed pResourceName parameter
     *
     * @param  pResourceName parameter
     *
     * @return bundled resource property for passed pResourceName parameter
     *
     * @throws MissingResourceException exception
     */
    public static String getCASTStringResource(String pResourceName) throws MissingResourceException {
        try {
            String result = castResources.getString(pResourceName);
            if (result == null) {
                String str = "ERROR: Unable to load resource " + pResourceName;
                throw new MissingResourceException(str, "atg.commerce.pricing.Constants", pResourceName);
            } else {
                return result;
            }
        } catch (MissingResourceException exc) {
            throw exc;
        }
    }
}
