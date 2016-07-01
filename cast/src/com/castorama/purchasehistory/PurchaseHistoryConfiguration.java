/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory;

import atg.nucleus.GenericService;

import atg.service.email.SMTPEmailSender;

import atg.userprofiling.email.TemplateEmailInfo;
import com.castorama.commerce.pricing.CastVATManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;


/**
 * Class holds basic configuration properties for "Purchase History" functionality.
 *
 * @author Aliaksandr Surma
 */
public class PurchaseHistoryConfiguration extends GenericService {
  //--------------------------------
  //Constants
  private static final String DATE_FORMAT = "dd/MM/yyyy";

  //--------------------------------
  // Properties

  //--------------------------------
  // property: enabled
  private boolean mEnabled = true;

  /**
   * Gets the enabled property.
   *
   * @return the enabled
   */
  public final boolean isEnabled() {
    return mEnabled;
  }

  /**
   * Sets the enabled property.
   *
   * @param pEnabled the enabled to set
   */
  public final void setEnabled(final boolean pEnabled) {
    mEnabled = pEnabled;
  }

  //--------------------------------
  // property: historyStartDate
  private String mHistoryStartDate;

  /**
   * Gets the historyStartDate property.
   *
   * @return the historyStartDate
   */
  public final String getHistoryStartDate() {
    return mHistoryStartDate;
  }

  /**
   * Sets the historyStartDate property.
   *
   * @param pHistoryStartDate the historyStartDate to set
   */
  public final void setHistoryStartDate(final String pHistoryStartDate) {
    mHistoryStartDate = pHistoryStartDate;
  }

  /**
   * Gets the historyStartDate property.
   *
   * @return the historyStartDate
   */
  public Date getHistoryStartDateAsDate() {
    final Date date;
    final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    try {
      dateFormat.setLenient(false);
      date = dateFormat.parse(getHistoryStartDate());
    } catch(ParseException e) {
      if(isLoggingError()) {
        logError("Unable to parse history start date " + getHistoryStartDate() +
          " Returns default value  - 01/03/2010");
      }

      Date dateDefault = null;

      try {
        dateDefault = dateFormat.parse("01/03/2010");
      } catch(ParseException exc) {
      }

      return dateDefault;
    }

    return date;
  }

  //--------------------------------
  // property: purgeStartDate
  private String mPurgeRunDate;

  /**
  * Gets the purgeRunDate property.
  *
  * @return the purgeRunDate
  */
  public final String getPurgeRunDate() {
    return mPurgeRunDate;
  }

    /**
     *
     */
     private CastVATManager mCastVATManager;

    /**
   * Sets the purgeRunDate property.
   *
   * @param pPurgeRunDate the purgeRunDate to set
   */
  public final void setPurgeRunDate(final String pPurgeRunDate) {
    mPurgeRunDate = pPurgeRunDate;
  }


  /**
   * Gets the historyStartDate property.
   *
   * @return the historyStartDate
   */
  public Date getPurgeRunDateAsDate() {
    final Date date;
    final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    try {
      dateFormat.setLenient(false);
      date = dateFormat.parse(getPurgeRunDate());
    } catch(ParseException e) {
      if(isLoggingError()) {
        logError("Unable to parse purge run date " + getPurgeRunDate() + " Returns default value  - 01/03/2013");
      }

      Date dateDefault = null;

      try {
        dateDefault = dateFormat.parse("01/03/2013");
      } catch(ParseException exc) {
      }

      return dateDefault;
    }

    return date;
  }

  //-------------------------------------
  // property: paymentMethodsCodeToLabelMap
  private Properties mPaymentMethodsCodeToLabelMap = null;

  /**
   * Gets the paymentMethodsCodeToLabelMap property.
   *
   * @return the paymentMethodsCodeToLabelMap
   */
  public final Properties getPaymentMethodsCodeToLabelMap() {
    return mPaymentMethodsCodeToLabelMap;
  }

  /**
   * Sets the paymentMethodsCodeToLabelMap property.
   *
   * @param pPaymentMethodsCodeToLabelMap the paymentMethodsCodeToLabelMap to set
   */
  public final void setPaymentMethodsCodeToLabelMap(final Properties pPaymentMethodsCodeToLabelMap) {
    mPaymentMethodsCodeToLabelMap = pPaymentMethodsCodeToLabelMap;
  }

  //-------------------------------------
  // property: defaultPaymentMethodLabel
  private String mDefaultPaymentMethodLabel = "CARTE CASTORAMA";

  /**
   * Gets the defaultPaymentMethodLabel property.
   *
   * @return the defaultPaymentMethodLabel
   */
  public final String getDefaultPaymentMethodLabel() {
    return mDefaultPaymentMethodLabel;
  }

  /**
   * Sets the defaultPaymentMethodLabel property.
   *
   * @param pDefaultPaymentMethodLabel the defaultPaymentMethodLabel to set
   */
  public final void setDefaultPaymentMethodLabel(final String pDefaultPaymentMethodLabel) {
    mDefaultPaymentMethodLabel = pDefaultPaymentMethodLabel;
  }

  //-------------------------------------
  // property: purchasesAvailabilityPeriod
  private String mPurchasesAvailabilityPeriod;

  /**
   * Gets the purchasesAvailabilityPeriod property.
   *
   * @return the purchasesAvailabilityPeriod
   */
  public final String getPurchasesAvailabilityPeriod() {
    return mPurchasesAvailabilityPeriod;
  }

  /**
   * Sets the purchasesAvailabilityPeriod property.
   *
   * @param pPurchasesAvailabilityPeriod the purchasesAvailabilityPeriod to set
   */
  public final void setPurchasesAvailabilityPeriod(final String pPurchasesAvailabilityPeriod) {
    mPurchasesAvailabilityPeriod = pPurchasesAvailabilityPeriod;
  }

  //-------------------------------------
  // property: accountAvailabilityPeriod
  private String mAccountAvailabilityPeriod;

  /**
   * Gets the accountAvailabilityPeriod property.
   *
   * @return the accountAvailabilityPeriod
   */
  public final String getAccountAvailabilityPeriod() {
    return mAccountAvailabilityPeriod;
  }

  /**
   * Sets the accountAvailabilityPeriod property.
   *
   * @param pAccountAvailabilityPeriod the accountAvailabilityPeriod to set
   */
  public final void setAccountAvailabilityPeriod(final String pAccountAvailabilityPeriod) {
    mAccountAvailabilityPeriod = pAccountAvailabilityPeriod;
  }

  //-------------------------------------
  // property: cardAvailabilityPeriod
  private String mCardAvailabilityPeriod;

  /**
   * Gets the cardAvailabilityPeriod property.
   *
   * @return the cardAvailabilityPeriod
   */
  public final String getCardAvailabilityPeriod() {
    return mCardAvailabilityPeriod;
  }

  /**
   * Sets the cardAvailabilityPeriod property.
   *
   * @param pCardAvailabilityPeriod the cardAvailabilityPeriod to set
   */
  public final void setCardAvailabilityPeriod(final String pCardAvailabilityPeriod) {
    mCardAvailabilityPeriod = pCardAvailabilityPeriod;
  }

  /**
   * Gets the defaultTVARate property.
   *
   * @return the defaultTVARate
   */
  public final double getDefaultTVARate() {
    return mCastVATManager.getVATValue();
  }

  //-------------------------------------
  // property: defaultDiscountAmount
  private double mDefaultDiscountAmount = 0;

  /**
   * Gets the defaultDiscountAmount property.
   *
   * @return the defaultDiscountAmount
   */
  public final double getDefaultDiscountAmount() {
    return mDefaultDiscountAmount;
  }

  /**
   * Sets the defaultDiscountAmount property.
   *
   * @param pDefaultDiscountAmount the defaultDiscountAmount to set
   */
  public final void setDefaultDiscountAmount(final double pDefaultDiscountAmount) {
    mDefaultDiscountAmount = pDefaultDiscountAmount;
  }

  //-------------------------------------
  // property: purgeEnabled
  private boolean mPurgeEnabled = true;

  /**
   * Gets the purgeEnabled property.
   *
   * @return the purgeEnabled
   */
  public final boolean isPurgeEnabled() {
    return mPurgeEnabled;
  }

  /**
   * Sets the purgeEnabled property.
   *
   * @param pPurgeEnabled the purgeEnabled to set
   */
  public final void setPurgeEnabled(final boolean pPurgeEnabled) {
    mPurgeEnabled = pPurgeEnabled;
  }

  //-------------------------------------
  // property: holdsTicketsYears
  private int mHoldsTicketsYears = 3;

  /**
   * Gets the holdsTicketsYears property.
   *
   * @return the holdsTicketsYears
   */
  public final int getHoldsTicketsYears() {
    return mHoldsTicketsYears;
  }

  /**
   * Sets the holdsTicketsYears property.
   *
   * @param pHoldsTicketsYears the holdsTicketsYears to set
   */
  public final void setHoldsTicketsYears(final int pHoldsTicketsYears) {
    mHoldsTicketsYears = pHoldsTicketsYears;
  }

  //-------------------------------------
  // property: daysBeforeNotifyMissingImport
  private int mDaysBeforeNotifyMissingImport = 10;

  /**
   * Gets the daysBeforeNotifyMissingImport property.
   *
   * @return the mDaysBeforeNotifyMissingImport
   */
  public final int getDaysBeforeNotifyMissingImport() {
    return mDaysBeforeNotifyMissingImport;
  }

  /**
   * Sets the daysBeforeNotifyMissingImport property.
   *
   * @param pDaysBeforeNotifyMissingImport the mDaysBeforeNotifyMissingImport to set
   */
  public final void setDaysBeforeNotifyMissingImport(final int pDaysBeforeNotifyMissingImport) {
    mDaysBeforeNotifyMissingImport = pDaysBeforeNotifyMissingImport;
  }

  //-------------------------------------
  // property: daysSendAlertBeforePurge
  private int mDaysSendAlertBeforePurge = 30;

  /**
   * Gets the daysSendAlertBeforePurge property.
   *
   * @return the daysSendAlertBeforePurge
   */
  public final int getDaysSendAlertBeforePurge() {
    return mDaysSendAlertBeforePurge;
  }

  /**
   * Sets the daysSendAlertBeforePurge property.
   *
   * @param pDaysSendAlertBeforePurge the daysSendAlertBeforePurge to set
   */
  public final void setDaysSendAlertBeforePurge(final int pDaysSendAlertBeforePurge) {
    mDaysSendAlertBeforePurge = pDaysSendAlertBeforePurge;
  }

  //-------------------------------------
  // property: purgeNotificationSendTo
  private String mPurgeNotificationSendTo;

  /**
   * Gets the purgeNotificationSendTo property.
   *
   * @return the purgeNotificationSendTo
   */
  public final String getPurgeNotificationSendTo() {
    return mPurgeNotificationSendTo;
  }

  /**
   * Sets the purgeNotificationSendTo property.
   *
   * @param pPurgeNotificationSendTo the purgeNotificationSendTo to set
   */
  public final void setPurgeNotificationSendTo(final String pPurgeNotificationSendTo) {
    mPurgeNotificationSendTo = pPurgeNotificationSendTo;
  }

  //-------------------------------------
  // property: importFailedNotificationSendTo
  private String mImportFailedNotificationSendTo;

  /**
   * Gets the importFailedNotificationSendTo property.
   *
   * @return the importFailedNotificationSendTo
   */
  public final String getImportFailedNotificationSendTo() {
    return mImportFailedNotificationSendTo;
  }

  /**
   * Sets the importFailedNotificationSendTo property.
   *
   * @param pImportFailedNotificationSendTo the importFailedNotificationSendTo to set
   */
  public final void setImportFailedNotificationSendTo(final String pImportFailedNotificationSendTo) {
    mImportFailedNotificationSendTo = pImportFailedNotificationSendTo;
  }
  //-------------------------------------
  // property: advancePaymentCode
  private String mAdvancePaymentCode;

  /**
   * Gets the advancePaymentCode property. 
   *
   * @return the advancePaymentCode
   */
  public final String getAdvancePaymentCode() {
    return mAdvancePaymentCode;
  }

  /**
   * Sets the advancePaymentCode property.
   *
   * @param pAdvancePaymentCode the advancePaymentCode to set
   */
  public final void setAdvancePaymentCode(final String pAdvancePaymentCode) {
    mAdvancePaymentCode = pAdvancePaymentCode;
  }

    public CastVATManager getCastVATManager() {
        return mCastVATManager;
    }

    public void setCastVATManager(CastVATManager mCastVATManager) {
        this.mCastVATManager = mCastVATManager;
    }
}
