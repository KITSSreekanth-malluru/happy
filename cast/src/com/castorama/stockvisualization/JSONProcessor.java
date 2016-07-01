/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.stockvisualization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import atg.core.util.StringUtils;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

/**
 * {@link DataProcessible} implementation intended for processing
 * of JSON data obtained from web service.
 *
 * @author Aliaksandr Belakurski
 */
public class JSONProcessor
        extends GenericService
        implements DataProcessible {
    // constants
    static final String STOCKS_JSON_NAME = "stocks";
    static final String PRODID_JSON_NAME = "prodId";
    static final String STOREID_JSON_NAME = "storeId";
    static final String STATUS_JSON_NAME = "status";
    static final String STOCK_AVAILABLE_JSON_NAME = "stockAvailable";
    static final String MAGSIN_NAME_PROPERTY_NAME = "nom";
    private StockVisualizationManager mStockVisualizationManager;

    public StockVisualizationManager getStockVisualizationManager() {
        return mStockVisualizationManager;
    }

    public void setStockVisualizationManager(StockVisualizationManager pStockVisualizationManager) {
        mStockVisualizationManager = pStockVisualizationManager;
    }

    /*
     * (non-Javadoc)
     * @see com.castorama.stockvisualization.DataProcessible#processData(java.lang.String)
     */
    public List<StockModel> processData(String pJSONData)
            throws StockVisualizationException {
        StockVisualizationManager svManager = getStockVisualizationManager();

        if (isLoggingDebug())
            logDebug("about to parse JSON data embedded in the following string " +
                    pJSONData);

        if (StringUtils.isEmpty(pJSONData))
            throw new StockVisualizationException("provided response data string is " +
                    "either empty or null");

        List<StockModel> stocks = new ArrayList<StockModel>(5);

        try {
            JSONObject stocksJSONObj = new JSONObject(pJSONData);
            JSONArray stocksArrayJSONObj = stocksJSONObj.getJSONArray(STOCKS_JSON_NAME);

            Object currObject = null;
            for (Iterator<JSONObject> arrIterator = stocksArrayJSONObj.iterator(); arrIterator.hasNext(); ) {

                if ((currObject = arrIterator.next()) instanceof JSONObject) {
                    String storeId = ((JSONObject) currObject).getString(STOREID_JSON_NAME);

                    RepositoryItem magasinItem = null;
                    try {
                        magasinItem = svManager.claimMagasinByStoreId(storeId);
                    } catch (StockVisualizationException svExc) {
                        if (isLoggingDebug())
                            logDebug("error occured while trying to claim magasin item, " +
                                    "skip processing current stock entity");
                    }

                    if (magasinItem == null)
                        continue;

                    String prodId = ((JSONObject) currObject).getString(PRODID_JSON_NAME);
                    String status = ((JSONObject) currObject).getString(STATUS_JSON_NAME);
                    String stockAvailable = ((JSONObject) currObject).getString(STOCK_AVAILABLE_JSON_NAME);
                    stocks.add(new StockModel(prodId, storeId, status, stockAvailable, magasinItem));
                } else if (isLoggingDebug())
                    logDebug("parsed JSON object is of inappropriate type - " + currObject);
            }
        } catch (JSONException jsonExc) {
            if (isLoggingError())
                logError("unable to process JSON data embedded in the following string " +
                        pJSONData);

            throw new StockVisualizationException(jsonExc);
        }

        if (isLoggingDebug())
            logDebug("the following list of StockModel entitties has been created - " +
                    stocks);

        return stocks;
    }
}
