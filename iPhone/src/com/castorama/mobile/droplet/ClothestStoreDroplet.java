package com.castorama.mobile.droplet;

import java.io.IOException;

import java.util.SortedSet;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.utils.StoreTools;
import com.castorama.utils.StoreTools.StoreToDistance;

/**
 * Find a closest store.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ClothestStoreDroplet extends DynamoServlet {
    /** NUMBER_OF_STORES_IN_LIST constant. */
    private static final String NUMBER_OF_STORES_IN_LIST = "numberOfStoresInList";

    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** PROXIMITY constant. */
    public static final String PROXIMITY = "proximity";

    /** LATITUDE constant. */
    private static final String LATITUDE = "latitude";

    /** LONGITUDE constant. */
    private static final String LONGITUDE = "longitude";

    /** STORE_ID constant. */
    private static final String STORE_LIST = "storeList";

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
     * Find a closest store.
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        SortedSet<StoreToDistance> storeList = null;
        boolean result = true;
        try {
            String latitude = (String) pRequest.getParameter(LATITUDE);
            String longitude = (String) pRequest.getParameter(LONGITUDE);
            String numberOfStoresInList = (String) pRequest.getParameter(NUMBER_OF_STORES_IN_LIST);
            Double latitudeD = null;
            Double longitudeD = null;
            Integer numberOfStoresInListInt = null;
            if (!StringUtils.isBlank(latitude)) {
                latitudeD = Double.parseDouble(latitude);
            }
            if (!StringUtils.isBlank(longitude)) {
                longitudeD = Double.parseDouble(longitude);
            }

            if (!StringUtils.isBlank(numberOfStoresInList)) {
                numberOfStoresInListInt = Integer.parseInt(numberOfStoresInList);
            }
            if ((getStoreTools() != null) && (latitudeD != null) && (longitudeD != null)) {
                if ((numberOfStoresInListInt == null) || (numberOfStoresInListInt <= 0)) {
                    storeList = getStoreTools().findClothestStores(latitudeD, longitudeD);
                } else {
                    storeList = getStoreTools().findNClothestStores(latitudeD, longitudeD, numberOfStoresInListInt);
                }
            }
        } catch (Exception e) {
            if (isLoggingError()) {
                logError("Error in com.castorama.mobile.droplet.ClothestStoreDroplet: " + e.getMessage());
            }
            e.printStackTrace();
            result = false;
        }  // end try-catch
        if ((storeList == null) || storeList.isEmpty()) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        } else if (!result) {
            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        } else {
            pRequest.setParameter(STORE_LIST, storeList);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
    }
}
