package com.castorama.stock.mchannel.rest;

import com.castorama.stock.mchannel.rest.model.Stock;
import com.castorama.stock.mchannel.rest.model.StocksBag;

import java.util.Map;

/**
 * Stock provider.
 *
 * @author  EPAM team
 */
public interface StockStatusProvider {

  /**
   * Returns stock property.
   *
   * @param  prodId  parameter to set.
   * @param  storeId parameter to set.
   *
   * @return stock property.
   */
  Stock getStock(String prodId, int storeId, int quantity, String channel, boolean reduce);
  
  /**
   * Returns stocks property.
   *
   * @param  prodIds  parameter to set.
   * @param  storeId parameter to set.
   *
   * @return stocks property.
   */
  StocksBag getStocks(String[] prodIds, int storeId, int quantity, String channel);

    /**
     * Returns updated only stocks.
     *
     * @param  productQuantities  parameter to set.
     * @param  storeId parameter to set.
     *
     * @return stocks property.
     */
    StocksBag getStocksWithUpdate(int storeId, Map<String, Integer> productQuantities);

  /**
   * Returns stocks property.
   *
   * @param  pProdId    parameter to set.
   * @param  postalCode parameter to set.
   *
   * @return stocks property.
   */
  StocksBag getStocks(String pProdId, String postalCode, String channel, int quantity, Integer size);
  
  /**
   * Logs internal error.
   * 
   * @param pStoreId store id.
   * @param pProdId product id.
   * @param pChannel channel.
   * @param pCode code.
   */
  public void logErrorIntoDB(final int pStoreId,
                             final String pProdId,
                             final String pChannel,
                             final int pCode);

  /**
   * Logs internal error.
   * 
   * @param pPostalCode store id.
   * @param pProdId product id.
   * @param pChannel channel.
   * @param pCode code.
   */
  public void logErrorIntoDB(final String pPostalCode,
                             final String pProdId,
                             final String pChannel,
                             final int pCode);
  
  /**
   * Logs internal error.
   * 
   * @param pStoreId store id.
   * @param pProdId product id.
   * @param pChannel channel.
   * @param pCode code.
   */
  public void logErrorIntoDB(final String pPostalCode,
                             final int pStoreId,
                             final String pProdId,
                             final String pChannel,
                             final int pCode);


}
