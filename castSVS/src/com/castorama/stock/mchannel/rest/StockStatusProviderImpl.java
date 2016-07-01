/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.stock.mchannel.rest;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.castorama.stock.mchannel.Configuration;
import com.castorama.stock.mchannel.cache.CacheOwner;
import com.castorama.stock.mchannel.cache.Constants;
import com.castorama.stock.mchannel.rest.model.Stock;
import com.castorama.stock.mchannel.rest.model.StocksBag;
import com.thoughtworks.xstream.XStream;


/**
 * Stock status provider.
 *
 * @author EPAM team
 */
@Service
public class StockStatusProviderImpl implements StockStatusProvider {
    /**
     * Apache Log4j property.
     */
    private static final Log LOG = LogFactory.getLog(StockStatusProviderImpl.class);
    //-------------------------------------
    // Constants
    private static final String LOOKUP_BY_PROD_AND_STORE_QUERY = "SELECT STOCK_AVAILABLE FROM MS_INVENTORY WHERE PRODUCT_ID=? AND STORE_ID=?";
    private static final String REDUCE_BY_PROD_AND_STORE_QUERY = "UPDATE MS_INVENTORY SET STOCK_AVAILABLE = CASE WHEN STOCK_AVAILABLE > ? THEN (STOCK_AVAILABLE - ?) ELSE 0 END WHERE PRODUCT_ID = ? AND STORE_ID = ?";
    private static final String LOOKUP_BY_PROD_AND_STORE_MULT_QUERY = "SELECT PRODUCT_ID, STOCK_AVAILABLE FROM MS_INVENTORY WHERE PRODUCT_ID IN ( :prodIds ) AND STORE_ID = :storeId";
    private static final String LOOKUP_STORE_PRODUCT = "SELECT PRODUCT_ID FROM MS_INVENTORY WHERE STORE_ID=? AND rownum < 2";
    private static final String LOOKUP_BY_POSTAL_CODE = "SELECT STORE_1, STORE_2, STORE_3, STORE_4, STORE_5, STORE_6, STORE_7, STORE_8, STORE_9, STORE_10 FROM MS_PCODE_TO_STORES WHERE POSTAL_CODE=?";
    private static final String INSERT_INTO_ERROR_LOG = "INSERT INTO MS_STOCK_SERVICE_ERR_LOG (POSTAL_CODE, STORE_ID, PROD_ID, CHANNEL, ERROR_CODE) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_BY_PROD_AND_STORE_QUERY = "UPDATE MS_INVENTORY SET STOCK_AVAILABLE = CASE WHEN STOCK_AVAILABLE > ? THEN ? ELSE STOCK_AVAILABLE END WHERE PRODUCT_ID = ? AND STORE_ID = ?";

    static {
        XStream xs = new XStream();
        xs.processAnnotations(Stock.class);
        xs.processAnnotations(StocksBag.class);
    }

    @Autowired
    @Qualifier("cacheConfig")
    private Configuration mConfiguration;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public StocksBag getStocks(final String pProdId, final String pPostalCode,
                               final String pChannel, final int pQuantity,
                               final Integer pSize) {
        StocksBag result = null;
        Node node = CacheOwner.getInstance().getCache()
                .getNode(Fqn.fromElements(Constants.POSTAL_CODES_NODE_NAME, pPostalCode));

        if (null != node) {
            Object ostores = node.get("stores");

            if (ostores instanceof List) {
                List stores = (List) ostores;

                if (0 < stores.size()) {
                    int size = stores.size();

                    if (null != pSize) {
                        if ((0 < pSize) && (pSize < size)) {
                            size = pSize;
                        }
                    }

                    List<Stock> stocks = new ArrayList<Stock>(size);

                    for (int i = 0; i < size; i++) {
                        BigDecimal storeid = (BigDecimal) stores.get(i);

                        if (null != storeid) {
                            stocks.add(getStock(pProdId, storeid.intValue(), pQuantity, pChannel, false));
                        } else {
                            break;
                        }
                    }

                    result = new StocksBag(stocks);
                }
            }
        } else {
            // Get from DB and store into cache

            final JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
            List resultObject = null;

            try {
                resultObject = template.query(LOOKUP_BY_POSTAL_CODE, new Object[]{pPostalCode},
                        new RowMapper() {
                            public List mapRow(final ResultSet pResultSet, final int pRowNum) throws SQLException {
                                final List storeIds = new ArrayList(10);
                                storeIds.add(pResultSet.getObject(1));
                                storeIds.add(pResultSet.getObject(2));
                                storeIds.add(pResultSet.getObject(3));
                                storeIds.add(pResultSet.getObject(4));
                                storeIds.add(pResultSet.getObject(5));
                                storeIds.add(pResultSet.getObject(6));
                                storeIds.add(pResultSet.getObject(7));
                                storeIds.add(pResultSet.getObject(8));
                                storeIds.add(pResultSet.getObject(9));
                                storeIds.add(pResultSet.getObject(10));
                                return storeIds;
                            }
                        });
                if (resultObject == null || resultObject.size() == 0) {
                    result = new StocksBag(createStock(pProdId, null, Constants.STATUS_STORE_OR_POSTAL_CODE_NOT_FOUND, 0));
                    return result;
                }

                List storeIds = (ArrayList) resultObject.get(0);


                final Node postalCodesRoot = CacheOwner.getInstance().getCache().getNode(Fqn.fromElements(Constants.POSTAL_CODES_NODE_NAME));
                final Node postalCodeNode = postalCodesRoot.addChild(Fqn.fromElements(pPostalCode));

                postalCodeNode.put("stores", storeIds);
                int size = storeIds.size();

                if (null != pSize) {
                    if ((0 < pSize) && (pSize < size)) {
                        size = pSize;
                    }
                }

                final List<Stock> stocks = new ArrayList<Stock>(size);

                for (int i = 0; i < size; i++) {
                    final BigDecimal storeId = (BigDecimal) storeIds.get(i);

                    if (storeId != null) {
                        stocks.add(getStock(pProdId, storeId.intValue(), pQuantity, pChannel, false));
                    }
                }

                result = new StocksBag(stocks);

            } catch (Exception exc) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(exc);
                }
            }
        }


        if (result == null) {
            logErrorIntoDB(pPostalCode, pProdId, pChannel, Constants.STATUS_INTERNAL_ERROR);

            if (LOG.isErrorEnabled()) {
                LOG.error("CACHE ERROR. STATUS = " + Constants.STATUS_INTERNAL_ERROR);
            }

            result = new StocksBag(createStock(pProdId, null, Constants.STATUS_INTERNAL_ERROR, 0));
        }

        return result;
    }

    /**
     * Returns stock property.
     *
     * @param pProdId  parameter to set.
     * @param pStoreId parameter to set.
     * @return stock property.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Stock getStock(final String pProdId, final int pStoreId,
                          final int quantity, final String channel, final boolean reduce) {
        final Cache cache = CacheOwner.getInstance().getCache();
        Node node = cache.getNode(Fqn.fromElements(pStoreId, pProdId));

        if (reduce) {
            if (node != null) {
                cache.removeNode(Fqn.fromElements(pStoreId, pProdId));
                node = null;
            }

            JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());

            try {
                int rowsAffected = template.update(REDUCE_BY_PROD_AND_STORE_QUERY, new Object[]{quantity, quantity, pProdId, pStoreId});
                if (rowsAffected > 0) {
                    return createStock(pProdId, pStoreId, Constants.STATUS_REDUCE_SUCCESS, 0);
                }
            } catch (DataAccessException dae) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(dae);
                }
            }

            return createStock(pProdId, pStoreId, Constants.STATUS_INTERNAL_ERROR, 0);
        } else {
            if (node == null) {
                Stock stock = null;

                if (isStore(pStoreId)) {
                    JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());

                    final Float stockLevel;
                    try {
                        stockLevel = template.queryForObject(LOOKUP_BY_PROD_AND_STORE_QUERY,
                                new Object[]{pProdId, pStoreId}, new RowMapper<Float>() {
                            public Float mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                                return rs.getFloat(1);
                            }

                        });
                    } catch (IncorrectResultSizeDataAccessException e) {
                        return createStock(pProdId, pStoreId, Constants.STATUS_PRODUCT_NOT_FOUND, 0);
                    }

                    if (stockLevel != null) {
                        float stockTestValue = stockLevel - quantity;

                        stockTestValue = new BigDecimal(stockTestValue).setScale(2, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros()
                                .floatValue();

                        int status = Constants.STATUS_NOT_AVAILABLE;

                        int topLimetedAvailability = mConfiguration.getLimitedAvailabilityTop();
                        int buttomLimmetedAvailability = mConfiguration.getLimitedAvailabilityButtom();

                        if (stockTestValue > topLimetedAvailability) {
                            status = Constants.STATUS_AVAILABLE;
                        } else if ((stockTestValue <= topLimetedAvailability) && (stockTestValue >= buttomLimmetedAvailability)) {
                            status = Constants.STATUS_LIMITED_AVAILABILITY;
                        }
                        stock = createStock(pProdId, pStoreId, status, stockLevel.intValue());
                    } else {
                        logErrorIntoDB(pStoreId, pProdId, channel, Constants.STATUS_INTERNAL_ERROR);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Error during stock status retrieval. Status = " + Constants.STATUS_INTERNAL_ERROR);
                        }
                    }

                    Node childStore = cache.getRoot().getChild(Fqn.fromElements(pStoreId));

                    if (childStore == null) {
                        childStore = cache.getRoot().addChild(Fqn.fromElements(pStoreId));
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("childStore.dataSize(): " + childStore.getChildren().size());
                        }
                    }

                    Node prodNode = childStore.addChild(Fqn.fromElements(pProdId));

                    if (stock != null) {
                        prodNode.put(Constants.STATUS, stockLevel);
                    }
                } else {
                    stock = createStock(pProdId, pStoreId, Constants.STATUS_STORE_OR_POSTAL_CODE_NOT_FOUND, 0);
                }

                return stock;
            } else {
                final Float stockLevel = (Float) node.get(Constants.STATUS);

                if (stockLevel == null) {
                    logErrorIntoDB(pStoreId, pProdId, channel, Constants.STATUS_INTERNAL_ERROR);
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Internal error. Status = " + Constants.STATUS_INTERNAL_ERROR);
                    }
                    return createStock(pProdId, pStoreId, Constants.STATUS_INTERNAL_ERROR, 0);
                } else {
                    float stockTestValue = stockLevel - quantity;

                    stockTestValue = new BigDecimal(stockTestValue).setScale(2, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros()
                            .floatValue();

                    int status = Constants.STATUS_NOT_AVAILABLE;

                    int topLimetedAvailability = mConfiguration.getLimitedAvailabilityTop();
                    int buttomLimmetedAvailability = mConfiguration.getLimitedAvailabilityButtom();

                    if (stockTestValue > topLimetedAvailability) {
                        status = Constants.STATUS_AVAILABLE;
                    } else if ((stockTestValue <= topLimetedAvailability) && (stockTestValue >= buttomLimmetedAvailability)) {
                        status = Constants.STATUS_LIMITED_AVAILABILITY;
                    }

                    return createStock(pProdId, pStoreId, status, stockLevel.intValue());
                }
            }
        }
    }

    /**
     * Returns stocks property.
     *
     * @param pProdIds parameter to set.
     * @param pStoreId parameter to set.
     * @return stock property.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public StocksBag getStocks(final String[] pProdIds, final int pStoreId, final int quantity, final String channel) {
        final List<Stock> stocks = new ArrayList<Stock>(pProdIds.length);
        final List<String> notCachedProdIds = new ArrayList<String>();

        final Cache cache = CacheOwner.getInstance().getCache();

        for (String pProdId : pProdIds) {
            Node node = cache.getNode(Fqn.fromElements(pStoreId, pProdId));

            if (node == null) {
                notCachedProdIds.add(pProdId);
            } else {
                final Float stockLevel = (Float) node.get(Constants.STATUS);

                if (stockLevel == null) {
                    logErrorIntoDB(pStoreId, pProdId, channel, Constants.STATUS_INTERNAL_ERROR);
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Internal error. Status = " + Constants.STATUS_INTERNAL_ERROR);
                    }
                    stocks.add(createStock(pProdId, pStoreId, Constants.STATUS_INTERNAL_ERROR, 0));
                } else {
                    float stockTestValue = stockLevel - quantity;

                    stockTestValue = new BigDecimal(stockTestValue)
                            .setScale(2, BigDecimal.ROUND_HALF_DOWN)
                            .stripTrailingZeros().floatValue();

                    int status = Constants.STATUS_NOT_AVAILABLE;

                    int topLimetedAvailability = mConfiguration.getLimitedAvailabilityTop();
                    int buttomLimmetedAvailability = mConfiguration.getLimitedAvailabilityButtom();

                    if (stockTestValue > topLimetedAvailability) {
                        status = Constants.STATUS_AVAILABLE;
                    } else if ((stockTestValue <= topLimetedAvailability) && (stockTestValue >= buttomLimmetedAvailability)) {
                        status = Constants.STATUS_LIMITED_AVAILABILITY;
                    }

                    stocks.add(createStock(pProdId, pStoreId, status, stockLevel.intValue()));
                }
            }
        }

        if (!notCachedProdIds.isEmpty()) {
            if (isStore(pStoreId)) {
                NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(getConfiguration().getDataSource());
                Map<String, Float> stockLevelsMap = new HashMap<String, Float>();

                Map<String, Object> params = new HashMap<String, Object>(2);
                params.put("storeId", pStoreId);

                boolean wasException = false;
                try {
                    int i = 0;
                    while (i < notCachedProdIds.size()) {
                        params.put("prodIds", notCachedProdIds.subList(i, i + 1000 < notCachedProdIds.size() ? i + 1000 : notCachedProdIds.size()));
                        i += 1000;
                        stockLevelsMap.putAll(template.query(LOOKUP_BY_PROD_AND_STORE_MULT_QUERY, params, new ResultSetExtractor<Map<String, Float>>() {
                            public Map<String, Float> extractData(final ResultSet rs) throws SQLException, DataAccessException {
                                final Map<String, Float> result = new HashMap<String, Float>();
                                while (rs.next()) {
                                    result.put(rs.getString(1), rs.getFloat(2));
                                }
                                return result;
                            }
                        }));
                    }
                } catch (DataAccessException dae) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(dae);
                    }
                    wasException = true;
                }

                for (String notCachedProdId : notCachedProdIds) {
                    if (!stockLevelsMap.containsKey(notCachedProdId)) {
                        if (wasException) {
                            stocks.add(createStock(notCachedProdId, pStoreId, Constants.STATUS_INTERNAL_ERROR, 0));
                        } else {
                            stocks.add(createStock(notCachedProdId, pStoreId, Constants.STATUS_PRODUCT_NOT_FOUND, 0));
                        }
                    } else {
                        Float stockLevel = stockLevelsMap.get(notCachedProdId);
                        if (stockLevel != null) {
                            float stockTestValue = stockLevel - quantity;

                            stockTestValue = new BigDecimal(stockTestValue)
                                    .setScale(2, BigDecimal.ROUND_HALF_DOWN)
                                    .stripTrailingZeros().floatValue();

                            int status = Constants.STATUS_NOT_AVAILABLE;

                            int topLimetedAvailability = mConfiguration.getLimitedAvailabilityTop();
                            int buttomLimmetedAvailability = mConfiguration.getLimitedAvailabilityButtom();

                            if (stockTestValue > topLimetedAvailability) {
                                status = Constants.STATUS_AVAILABLE;
                            } else if ((stockTestValue <= topLimetedAvailability)
                                    && (stockTestValue >= buttomLimmetedAvailability)) {
                                status = Constants.STATUS_LIMITED_AVAILABILITY;
                            }
                            stocks.add(createStock(notCachedProdId, pStoreId, status, stockLevel.intValue()));

                            Node childStore = cache.getRoot().getChild(Fqn.fromElements(pStoreId));

                            if (childStore == null) {
                                childStore = cache.getRoot().addChild(Fqn.fromElements(pStoreId));
                            } else {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("childStore.dataSize(): " + childStore.getChildren().size());
                                }
                            }
                            Node prodNode = childStore.addChild(Fqn.fromElements(notCachedProdId));
                            prodNode.put(Constants.STATUS, stockLevel);
                        } else {
                            logErrorIntoDB(pStoreId, notCachedProdId, channel, Constants.STATUS_INTERNAL_ERROR);
                            if (LOG.isErrorEnabled()) {
                                LOG.error("Error during stock status retrieval. Status = " + Constants.STATUS_INTERNAL_ERROR);
                            }
                            stocks.add(createStock(notCachedProdId, pStoreId, Constants.STATUS_INTERNAL_ERROR, 0));
                        }
                    }
                }
            } else {
                for (String notCachedProdId : notCachedProdIds) {
                    stocks.add(createStock(notCachedProdId, pStoreId, Constants.STATUS_STORE_OR_POSTAL_CODE_NOT_FOUND, 0));
                }
            }
        }

        return new StocksBag(stocks);
    }

    /**
     * Returns stocks property.
     *
     * @param storeId parameter to set.
     * @param productQuantities parameter to set.
     * @return stock property.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public StocksBag getStocksWithUpdate(final int storeId, Map<String, Integer> productQuantities) {
        final Cache cache = CacheOwner.getInstance().getCache();
        final List<Stock> stocks = new ArrayList<Stock>();

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            String prodId = entry.getKey();
            int quantity = entry.getValue();
            Node node = cache.getNode(Fqn.fromElements(storeId, prodId));

            JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());

            try {
                int rowsAffected = template.update(UPDATE_BY_PROD_AND_STORE_QUERY, quantity, quantity, prodId, storeId);
                if (rowsAffected > 0) {
                    stocks.add(createStock(prodId, storeId, Constants.STATUS_REDUCE_SUCCESS, quantity));
                    if (node != null) {
                        cache.removeNode(Fqn.fromElements(storeId, prodId));
                    }
                }
            } catch (DataAccessException dae) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(dae);
                    stocks.add(createStock(prodId, storeId, Constants.STATUS_INTERNAL_ERROR, 0));
                }
            }
        }

        return new StocksBag(stocks);

    }

    /**
     * Checks if store exists in DB.
     *
     * @param pStoreId store id.
     * @return true if store exists, otherwise false.
     */
    private boolean isStore(final int pStoreId) {
        boolean result = false;

        JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
        List<Map<String, Object>> rows = template.queryForList(LOOKUP_STORE_PRODUCT, new Object[]{pStoreId});
        if (rows.size() > 0) {
            result = true;
        }

        return result;
    }

    /**
     * Creates stock object.
     *
     * @param pProdId  - product id.
     * @param pStoreId - store id.
     * @param pStatus  - status.
     * @return stock object.
     */
    private Stock createStock(final String pProdId, final Integer pStoreId, final int pStatus, final int pStockAvailable) {
        Stock result = new Stock();
        result.setProdId(pProdId);

        if (null != pStoreId) {
            result.setStoreId(pStoreId);
        }

        result.setStatus(pStatus);
        result.setStockAvailable(pStockAvailable);

        return result;
    }

    /**
     * Logs internal error.
     *
     * @param pProdId  product id.
     * @param pStoreId store id.
     * @param pChannel channel.
     * @param pCode    code.
     */
    public void logErrorIntoDB(final int pStoreId, final String pProdId,
                               final String pChannel, final int pCode) {
        try {
            JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
            template.update(INSERT_INTO_ERROR_LOG, new Object[]{null, pStoreId, pProdId, pChannel, pCode});
        } catch (Exception exc) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't log error into database.", exc);
            }
        }
    }

    /**
     * Logs internal error.
     *
     * @param pProdId     product id.
     * @param pPostalCode store id.
     * @param pChannel    channel.
     * @param pCode       code.
     */
    public void logErrorIntoDB(final String pPostalCode, final String pProdId,
                               final String pChannel, final int pCode) {
        try {
            JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
            template.update(INSERT_INTO_ERROR_LOG, new Object[]{pPostalCode, null, pProdId, pChannel, pCode});
        } catch (Exception exc) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't log error into database.", exc);
            }
        }
    }

    /**
     * Logs internal error.
     *
     * @param pProdId  product id.
     * @param pStoreId store id.
     * @param pChannel channel.
     * @param pCode    code.
     */
    public void logErrorIntoDB(final String pPostalCode, final int pStoreId, final String pProdId,
                               final String pChannel, final int pCode) {
        try {
            JdbcTemplate template = new JdbcTemplate(getConfiguration().getDataSource());
            template.update(INSERT_INTO_ERROR_LOG, new Object[]{pPostalCode, pStoreId, pProdId, pChannel, pCode});
        } catch (Exception exc) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Can't log error into database.", exc);
            }
        }
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public void setConfiguration(final Configuration pConfiguration) {
        mConfiguration = pConfiguration;
  }
}
