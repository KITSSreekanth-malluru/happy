package com.castorama.integration.ticket;

import java.util.Date;

public class TicketLineObject {

  private String productId;
  private double productQuantity;
  private String productLabel;
  private double unitPriceHT;
  private double unitPriceTTC;
  private double rateTVA;
  private double discountAmount;
  private String discountLabel;
  private Date productCreationDate;
  private int lineNumber;

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append("TICKET LINE[");
    result.append("lineNumber=").append(lineNumber);
    result.append(",productId=").append(productId);
    result.append(",productQuantity=").append(productQuantity);
    result.append(",productLabel=").append(productLabel);
    result.append(",productCreationDate=").append(productCreationDate);
    result.append(",unitPriceHT=").append(unitPriceHT);
    result.append(",unitPriceTTC=").append(unitPriceTTC);
    result.append(",rateTVA=").append(rateTVA);
    result.append(",discountAmount=").append(discountAmount);
    result.append(",discountLabel=").append(discountLabel);
    result.append("]");
    return result.toString();
  }

  /**
   * @return the productId
   */
  public String getProductId() {
    return productId;
  }

  /**
   * @param productId the productId to set
   */
  public void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   * @return the productQuantity
   */
  public double getProductQuantity() {
    return productQuantity;
  }

  /**
   * @param productQuantity the productQuantity to set
   */
  public void setProductQuantity(double productQuantity) {
    this.productQuantity = productQuantity;
  }

  /**
   * @return the productLabel
   */
  public String getProductLabel() {
    return productLabel;
  }

  /**
   * @param productLabel the productLabel to set
   */
  public void setProductLabel(String productLabel) {
    this.productLabel = productLabel;
  }

  /**
   * @return the unitPriceHT
   */
  public double getUnitPriceHT() {
    return unitPriceHT;
  }

  /**
   * @param unitPriceHT the unitPriceHT to set
   */
  public void setUnitPriceHT(double unitPriceHT) {
    this.unitPriceHT = unitPriceHT;
  }

  /**
   * @return the unitPriceTTC
   */
  public double getUnitPriceTTC() {
    return unitPriceTTC;
  }

  /**
   * @param unitPriceTTC the unitPriceTTC to set
   */
  public void setUnitPriceTTC(double unitPriceTTC) {
    this.unitPriceTTC = unitPriceTTC;
  }

  /**
   * @return the rateTVA
   */
  public double getRateTVA() {
    return rateTVA;
  }

  /**
   * @param rateTVA the rateTVA to set
   */
  public void setRateTVA(double rateTVA) {
    this.rateTVA = rateTVA;
  }

  /**
   * @return the discountAmount
   */
  public double getDiscountAmount() {
    return discountAmount;
  }

  /**
   * @param discountAmount the discountAmount to set
   */
  public void setDiscountAmount(double discountAmount) {
    this.discountAmount = discountAmount;
  }

  /**
   * @return the discountLabel
   */
  public String getDiscountLabel() {
    return discountLabel;
  }

  /**
   * @param discountLabel the discountLabel to set
   */
  public void setDiscountLabel(String discountLabel) {
    this.discountLabel = discountLabel;
  }

  /**
   * @return the productCreationDate
   */
  public Date getProductCreationDate() {
    return productCreationDate;
  }

  /**
   * @param productCreationDate the productCreationDate to set
   */
  public void setProductCreationDate(Date productCreationDate) {
    this.productCreationDate = productCreationDate;
  }

  /**
   * @return the lineNumber
   */
  public int getLineNumber() {
    return lineNumber;
  }

  /**
   * @param lineNumber the lineNumber to set
   */
  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

}
