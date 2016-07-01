/**
 *
 */
package com.castorama.mobile.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.utils.StoreTools;
import static com.castorama.mobile.tools.CastoramaConstants.*;

/**
 * Calculate distance between current user location and store. Current user
 * location is pair of (longitude, latitude) - double values.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CalculateDistanceToStore extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");


    /** STORE_ID constant. */
    private static final String STORE_ID = "storeId";

    /** DISTANCE constant. */
    private static final String DISTANCE = "distance";

    /** mStoreTools property. */
    private StoreTools mStoreTools;

    /**
     * Returns storeTools property.
     *
     * @return storeTools property.
     */
    public StoreTools getStoreTools() {
        return mStoreTools;
    }

    /**
     * Sets the value of the storeTools property.
     *
     * @param pStoreTools parameter to set.
     */
    public void setStoreTools(StoreTools pStoreTools) {
        mStoreTools = pStoreTools;
    }

    /**
     * Calculate distance between current user location and store. Current user
     * location is pair of (longitude, latitude) - double values.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Double distance = null;
        try {
            String userLatitude = (String) pRequest.getParameter(LATITUDE);
            String userLongitude = (String) pRequest.getParameter(LONGITUDE);
            String storeId = (String) pRequest.getParameter(STORE_ID);
            Double userLatitudeD = null;
            Double userLongitudeD = null;
            try {
                if (!StringUtils.isBlank(userLatitude)) {
                    userLatitudeD = Double.parseDouble(userLatitude);
                }
                if (!StringUtils.isBlank(userLongitude)) {
                    userLongitudeD = Double.parseDouble(userLongitude);
                }
            } catch (NumberFormatException e) {
                if (isLoggingError()) {
                    logError("Error during parsing string to double in com.castorama.mobile.droplet.CalculateDistanceToStore: " +
                             e.getMessage());
                }
            }

            if ((getStoreTools() != null) && (userLatitudeD != null) && (userLongitudeD != null)) {
                RepositoryItem store = getStoreTools().getStore(storeId);
                if (store != null) {
                    Double storeLatitudeD = (Double) store.getPropertyValue(LATITUDE);
                    Double storeLongitudeD = (Double) store.getPropertyValue(LONGITUDE);
                    if ((storeLatitudeD != null) && (storeLongitudeD != null)) {
                        distance =
                            getStoreTools().calculateDistance(userLatitudeD, userLongitudeD, storeLatitudeD,
                                                              storeLongitudeD);
                    }
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error in com.castorama.mobile.droplet.CalculateDistanceToStore: ");
            }
        }  // end try-catch
        if (distance != null) {
            pRequest.setParameter(DISTANCE, distance);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }
}
