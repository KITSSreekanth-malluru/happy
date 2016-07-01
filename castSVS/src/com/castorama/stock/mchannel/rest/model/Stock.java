package com.castorama.stock.mchannel.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Stock bean.
 * 
 * @author EPAM team
 */
@XStreamAlias("stock")
@XmlRootElement(name="stock", namespace= "http://castorama.fr/stocks")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stock {

  /** prodId property */
  @XStreamAlias("product")
  @XStreamAsAttribute
  @XmlAttribute(name="product")
  private String prodId;

  /** storeId property */
  @XStreamAlias("store")
  @XStreamAsAttribute
  @XmlAttribute(name="store")
  private int storeId;
  // TODO: storeId = 0 when bean initialized.

  /** stockStatus property */
  @XStreamAlias("status")
  @XStreamAsAttribute
  @XmlAttribute(name="status")
  private int status;
  
  /** stockAvailable property */
  @XStreamAlias("stockAvailable")
  @XStreamAsAttribute
  @XmlAttribute(name="stockAvailable")
  private int stockAvailable;

  /**
   * Returns prodId property.
   * 
   * @return prodId property.
   */
  public String getProdId() {
    return prodId;
  }

  /**
   * Sets the value of the prodId property.
   * 
   * @param pProdId parameter to set.
   */
  public void setProdId(String pProdId) {
    prodId = pProdId;
  }

  /**
   * Returns storeId property.
   * 
   * @return storeId property.
   */
  public int getStoreId() {
    return storeId;
  }

  /**
   * Sets the value of the storeId property.
   * 
   * @param pStoreId parameter to set.
   */
  public void setStoreId(int pStoreId) {
    storeId = pStoreId;
  }

  /**
   * Returns status.
   * 
   * @return status.
   */
  public int getStatus() {
    return status;
  }

  /**
   * Sets status.
   * @param pStatus status.
   */
  public void setStatus(int pStatus) {
    status = pStatus;
  }

  /**
   * Returns stockAvailable.
   * 
   * @return stockAvailable.
   */
  public int getStockAvailable() {
    return stockAvailable;
  }

  /**
   * Sets stockAvailable.
   * @param pStockAvailable stockAvailable.
   */
  public void setStockAvailable(int pStockAvailable) {
    stockAvailable = pStockAvailable;
  }
}
