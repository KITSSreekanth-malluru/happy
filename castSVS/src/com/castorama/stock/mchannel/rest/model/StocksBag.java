package com.castorama.stock.mchannel.rest.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Stock bag bean.
 *
 * @author  EPAM team
 */
@XStreamAlias("stocks")
@XmlRootElement(name="stocks", namespace= "http://castorama.fr/stocks")
@XmlAccessorType(XmlAccessType.FIELD)
public class StocksBag {

  /** stocks property */
  @XStreamImplicit(itemFieldName = "stock")
  @XmlElement(name="stock")
  private List<Stock> stocks;

  /**
   * Creates a new StocksBag object.
   */
  public StocksBag() {
  }

  /**
   * Creates a new StocksBag object.
   *
   * @param pRse stock bag.
   */
  public StocksBag(List<Stock> pRse) {
    stocks = pRse;
  }

  /**
   * Creates stock bag and add one stock.
   * @param pStock stock.
   */
  public StocksBag(Stock pStock) {
    stocks = new ArrayList<Stock>();
    stocks.add(pStock);
  }

  /**
   * Creates stock bag and add list of stocks.
   * @param pStock list of stocks.
   */
  public StocksBag(Stock... pS1) {
    stocks = new ArrayList<Stock>();
    for (Stock stock : pS1) {
      stocks.add(stock);
    }
  }

  /**
   * Returns stocks property.
   *
   * @return stocks property.
   */
  public List<Stock> getStocks() {
    return stocks;
  }

  /**
   * Sets the value of the stocks property.
   *
   * @param pStocks parameter to set.
   */
  public void setStocks(List<Stock> pStocks) {
    stocks = pStocks;
  }
}
