package com.castorama.stock.mchannel.rest;

import com.castorama.stock.mchannel.cache.Constants;
import com.castorama.stock.mchannel.rest.model.Stock;
import com.castorama.stock.mchannel.rest.model.Stocks;
import com.castorama.stock.mchannel.rest.model.StocksBag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  EPAM team
 */
@Controller
@RequestMapping(value = "/stockVerify")
public class StockVerifyController {

    //region Statics
    private static final Log logger = LogFactory.getLog(StockController.class);
    //endregion

    //region Dependencies
    @Autowired
    private StockStatusProvider stockStatusProvider;
    //endregion

    //region Public methods
    @RequestMapping(params = "jsonQuery")
    public ModelAndView stockVerifyWithUpdate(@RequestParam("jsonQuery") final String jsonQuery) {

        String errorMessage = null;

        if (logger.isDebugEnabled()) {
            logger.debug("Processing json query: " + jsonQuery);
        }

        //there is no mention of Gson's thread safety
        Gson gson = new GsonBuilder().create();
        Stocks stocks = gson.fromJson(jsonQuery, Stocks.class);

        if (logger.isDebugEnabled()) {
            logger.debug("Parsed json query: " + stocks.toString());
        }

        if (stocks.getStoreId() == 0) {
            errorMessage = "Incorrect store id";
        } else if (stocks.getProductsQuantityMap().size() == 0) {
            errorMessage = "Incorrect products quantity";
        }

        StocksBag stocksBag;
        if (errorMessage == null) {
            stocksBag = stockStatusProvider.getStocksWithUpdate(stocks.getStoreId(),
                    productsQuantityMapToStockFormat(stocks.getProductsQuantityMap()));
            return new ModelAndView("stockVerify", "stockStatus", stocksBag);

        } else {
            if (logger.isErrorEnabled()) {
                logger.error(errorMessage);
            }

            final Stock stock = new Stock();
            stock.setStatus(Constants.STATUS_WRONG_REQUEST_PARAMETERS);
            stocksBag = new StocksBag(stock);
        }

        return new ModelAndView("stockVerify", "stockStatus", stocksBag);

    }
    //endregion

    //region Private methods
    private Map<String, Integer> productsQuantityMapToStockFormat(Map<String, Integer> productsQuantityMap){
        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : productsQuantityMap.entrySet()) {
            resultMap.put(StockUtils.codeArticleToProductId(entry.getKey()), entry.getValue());

        }
        return resultMap;
    }
    //endregion

}
