/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.purchasehistory;

import java.util.Date;


/**
 * Object holds ticket search criteria.
 *
 * @author Aliaksandr Surma
  */
public class TicketRequestData {
  private String mProductName;
  private Date mStartDate;
  private Date mEndDate;
  private String mStoreId;

  /**
   * Gets the productName property.
   *
   * @return the productName
   */
  public final String getProductName() {
    return mProductName;
  }

  /**
   * Sets the productName property.
   *
   * @param pProductName the productName to set
   */
  public final void setProductName(final String pProductName) {
    mProductName = pProductName;
  }

  /**
   * Gets the startDate property.
   *
   * @return the startDate
   */
  public final Date getStartDate() {
    return mStartDate;
  }

  /**
   * Sets the startDate property.
   *
   * @param pStartDate the startDate to set
   */
  public final void setStartDate(final Date pStartDate) {
    mStartDate = pStartDate;
  }

  /**
   * Gets the endDate property.
   *
   * @return the endDate
   */
  public final Date getEndDate() {
    return mEndDate;
  }

  /**
   * Sets the endDate property.
   *
   * @param pEndDate the endDate to set
   */
  public final void setEndDate(final Date pEndDate) {
    mEndDate = pEndDate;
  }

  /**
   * Gets the storeId property.
   *
   * @return the storeId
   */
  public final String getStoreId() {
    return mStoreId;
  }

  /**
   * Sets the storeId property.
   *
   * @param pStoreId the storeId to set
   */
  public final void setStoreId(final String pStoreId) {
    mStoreId = pStoreId;
  }
}
