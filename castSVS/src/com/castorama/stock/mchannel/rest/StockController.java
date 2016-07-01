/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.stock.mchannel.rest;

import com.castorama.stock.mchannel.Configuration;
import com.castorama.stock.mchannel.cache.Constants;

import com.castorama.stock.mchannel.rest.model.Stock;
import com.castorama.stock.mchannel.rest.model.StocksBag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.ModelAndView;


/**
 * Stock controller.
 *
 * @author EPAM team
 */
@Controller
@RequestMapping(value = "/stock")
public class StockController {
    /**
     * log property
     */
    private static final Log LOG = LogFactory.getLog(StockController.class);
    private static final String ERROR_LOG_PRODUCT = "PRODUCT ID SHOULD BE NOT EMPTY";
    private static final String ERROR_LOG_STORE = "STORE ID SHOULD BE NOT EMPTY";
    private static final String ERROR_LOG_STORE_INT = "STORE ID SHOULD BE AN INTEGER";
    private static final String ERROR_LOG_POSTAL_CODE = "POSTAL CODE SHOULD BE NOT EMPTY";
    private static final String ERROR_LOG_CHANNEL = "CHANNEL SHOULD BE WEB OR SVI";
    private static final String ERROR_LOG_QUANTITY = "QUANTITY SHOULD BE AN INTEGER";
    private static final String ERROR_LOG_SIZE = "SIZE SHOULD BE AN INTEGER";
    private static final String ERROR_LOG_PRODUCT_NUMBER = "NUMBER OF PRODUCTS IN URL PARAMETER EXCEEDED";
    private static int DEFAULT_QUANTITY = 1;
    private static int DEFAULT_NUMBER_OF_STORES = 5;
    private static final String PRODUCT_DELIMETER = ",";

    /**
     * mStockManager property
     */
    @Autowired
    private StockStatusProvider mStockStatusProvider;

    /** mConfiguration property */
    @Autowired
    @Qualifier("cacheConfig")
    private Configuration mConfiguration;

    /**
     *
     * @param prodId   parameter to set.
     * @param storeId  parameter to set.
     * @param channel  parameter to set.
     * @param quantity parameter to set.
     * @return model or view.
     */

    //@RequestMapping(method=RequestMethod.GET)
    @RequestMapping(params = {"prodId", "storeId"})
    public ModelAndView getProductStockByStore(
            @RequestParam("prodId") final String prodId, 
            @RequestParam("storeId") final String storeId, 
            @RequestParam("channel") final String channel, 
            @RequestParam(required = false, value = "quantity") final String quantity,
            @RequestParam(required = false, value = "reduce") final String reduce) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getProductStockByStore");
            LOG.debug("prodId: " + prodId);
            LOG.debug("storeId: " + storeId);
            LOG.debug("channel: " + channel);
            LOG.debug("quantity: " + quantity);
            LOG.debug("reduce: " + reduce);
        }

        String error = null;
        int qty = 0;
        int sid = 0;
        boolean rdc = false;

        if ((null == prodId) || (0 == prodId.length())) {
            error = ERROR_LOG_PRODUCT;
        } else if ((null == storeId) || (0 == storeId.length())) {
            error = ERROR_LOG_STORE;
        } else if (!("web".equalsIgnoreCase(channel) || "svi".equalsIgnoreCase(channel))) {
            error = ERROR_LOG_CHANNEL;
        } else {
            try {
                qty = ((null == quantity) || (0 == quantity.trim().length())) ? DEFAULT_QUANTITY : Integer.parseInt(quantity);
            } catch (Exception e) {
                error = ERROR_LOG_QUANTITY;
            }
        }

        if (null == error) {
            try {
                sid = Integer.parseInt(storeId);
            } catch (Exception e) {
                error = ERROR_LOG_STORE_INT;
            }
        }
        
        if (error == null) {
            if ("true".equals(reduce)) {
                rdc = true;
            }
        }

        final String fullProdId;
        final String[] fullProdIds;
        if (!prodId.contains(PRODUCT_DELIMETER)) {
            fullProdId = StockUtils.codeArticleToProductId(prodId);
            fullProdIds = null;
        } else {
            String[] prodIds = prodId.split(PRODUCT_DELIMETER);
            if (prodIds.length > getConfiguration().getMaxNumberOfProductsInParameter()) {
                if (error == null) {
                    error = ERROR_LOG_PRODUCT_NUMBER;
                }
                fullProdId = "products[0]:" + StockUtils.codeArticleToProductId(prodIds[0]);
                fullProdIds = null;
            } else {
                for (int i = 0; i < prodIds.length; i++) {
                    prodIds[i] = StockUtils.codeArticleToProductId(prodIds[i]);
                }
                fullProdId = "products[0]:" + prodIds[0];
                fullProdIds = prodIds;
            }
        }

        if (null == error) {
            if (fullProdIds == null) {
                final Stock stock = mStockStatusProvider.getStock(fullProdId, sid, qty, channel, rdc);
                return new ModelAndView("stockView", "stockStatus", new StocksBag(stock));
            } else {
                return new ModelAndView("stockView", "stockStatus",
                    mStockStatusProvider.getStocks(fullProdIds, sid, qty, channel));
            }
        } else {
            mStockStatusProvider.logErrorIntoDB(storeId, fullProdId, channel, Constants.STATUS_WRONG_REQUEST_PARAMETERS);

            if (LOG.isErrorEnabled()) {
                LOG.error(error);
            }

            final Stock stock = new Stock();
            stock.setStatus(Constants.STATUS_WRONG_REQUEST_PARAMETERS);

            return new ModelAndView("stockView", "stockStatus", new StocksBag(stock));
        }
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param prodId     ToDo: DOCUMENT ME!
     * @param postalCode ToDo: DOCUMENT ME!
     * @param storeId    ToDo: DOCUMENT ME!
     * @param channel    ToDo: DOCUMENT ME!
     * @param quantity   ToDo: DOCUMENT ME!
     * @param size       ToDo: DOCUMENT ME!
     * @return ToDo: DOCUMENT ME!
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getError(
            @RequestParam(required = false, value = "prodId") final String prodId,
            @RequestParam(required = false, value = "postalCode") final String postalCode,
            @RequestParam(required = false, value = "storeId") final String storeId,
            @RequestParam(required = false, value = "channel") final String channel,
            @RequestParam(required = false, value = "quantity") final String quantity,
            @RequestParam(required = false, value = "size") final String size) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getError!");
            LOG.debug("prodId: " + prodId);
            LOG.debug("postalCode: " + postalCode);
            LOG.debug("storeId: " + storeId);
            LOG.debug("channel: " + channel);
            LOG.debug("quantity: " + quantity);
            LOG.debug("size: " + size);
        }

        final String fullProdId = StockUtils.codeArticleToProductId(prodId);

        mStockStatusProvider.logErrorIntoDB(postalCode, Integer.parseInt(storeId), fullProdId, channel, Constants.STATUS_WRONG_REQUEST_PARAMETERS);

        if (LOG.isErrorEnabled()) {
            LOG.error("WRONG REQUEST PARAMETERS. STATUS = " + Constants.STATUS_WRONG_REQUEST_PARAMETERS);
        }

        final Stock stock = new Stock();
        stock.setStatus(Constants.STATUS_WRONG_REQUEST_PARAMETERS);

        return new ModelAndView("stockView", "stockStatus", new StocksBag(stock));
    }

    /**
     * Returns productStockByPostalCode property.
     *
     * @param prodId     parameter to set.
     * @param postalCode parameter to set.
     * @return productStockByPostalCode property.
     */
    @RequestMapping(params = {"prodId", "postalCode"})
    public ModelAndView getProductStockByPostalCode(
            @RequestParam("prodId") final String prodId,
            @RequestParam("postalCode") final String postalCode,
            @RequestParam("channel") final String channel,
            @RequestParam(required = false, value = "quantity") final String quantity,
            @RequestParam(required = false, value = "size") final String size) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getProductStockByPostalCode");
            LOG.debug("prodId: " + prodId);
            LOG.debug("postalCode: " + postalCode);
            LOG.debug("channel: " + channel);
            LOG.debug("quantity: " + quantity);
            LOG.debug("size: " + size);
        }

        String error = null;
        int qty = 0;
        int sz = 0;

        if ((null == prodId) || (0 == prodId.length())) {
            error = ERROR_LOG_PRODUCT;
        } else if ((null == postalCode) || (0 == postalCode.length())) {
            error = ERROR_LOG_POSTAL_CODE;
        } else if (!("web".equalsIgnoreCase(channel) || "svi".equalsIgnoreCase(channel))) {
            error = ERROR_LOG_CHANNEL;
        } else {
            try {
                qty = ((null == quantity) || (0 == quantity.trim().length())) ? DEFAULT_QUANTITY : Integer.parseInt(quantity);
            } catch (Exception e) {
                error = ERROR_LOG_QUANTITY;
            }

            if (null == error) {
                try {
                    sz = ((null == size) || (0 == size.trim().length())) ? DEFAULT_NUMBER_OF_STORES : Integer.parseInt(size);
                } catch (Exception e) {
                    error = ERROR_LOG_SIZE;
                }
            }
        }

        final String fullProdId = StockUtils.codeArticleToProductId(prodId);

        if (null == error) {
            return new ModelAndView("stockView", "stockStatus",
                    mStockStatusProvider.getStocks(fullProdId, postalCode, channel, qty, sz));
        } else {
            mStockStatusProvider.logErrorIntoDB(postalCode, fullProdId, channel, Constants.STATUS_WRONG_REQUEST_PARAMETERS);
            if (LOG.isErrorEnabled()) {
                LOG.error(error);
            }

            final Stock stock = new Stock();
            stock.setStatus(Constants.STATUS_WRONG_REQUEST_PARAMETERS);

            return new ModelAndView("stockView", "stockStatus", new StocksBag(stock));
        }
    }

    /**
     * Sets the value of the stockStatusProvider property.
     *
     * @param pStockStatusProvider parameter to set.
     */
    public void setStockStatusProvider(final StockStatusProvider pStockStatusProvider) {
        mStockStatusProvider = pStockStatusProvider;
    }

    /**
     * Returns configuration property.
     *
     * @return configuration property.
     */
    public Configuration getConfiguration() {
        return mConfiguration;
    }

    /**
     * Sets the value of the configuration property.
     *
     * @param pConfiguration parameter to set.
     */
    public void setConfiguration(final Configuration pConfiguration) {
        mConfiguration = pConfiguration;
    }
}
