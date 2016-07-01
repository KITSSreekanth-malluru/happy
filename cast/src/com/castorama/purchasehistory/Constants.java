/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory;


/**
 *  Helper class: contains "Purchase History" related constants.
 *
 * @author Aliaksandr Surma
 */
public class Constants {
  /** Order History ("Mon Memo Web" page) Resource bundle name. */
  public static final String RESOURCE_BUNDLE_NAME = "com.castorama.purchasehistory.PurchaseHistoryResources";

  //-------------------------------------
  // Generic constants.
  /** 'All stores' constant. */
  public static final String ALL_STORES = "all";

  //-------------------------------------
  // Ticket Repository constants.
  /** Ticket Header item descriptor name. */
  public static final String TICKET_HEADER_DESC = "ticketHeader";

  //-------------------------------------
  // Ticket Header property names.
  /** Ticket Id property name. */
  public static final String TICKET_HEADER_TICKET_ID_PROP = "ticketId";

  /** Home ID property name. */
  public static final String TICKET_HEADER_HOME_ID_PROP = "homeId";

  /** Ticket's date property name. */
  public static final String TICKET_HEADER_TICKET_DATE_PROP = "ticketDate";

  /** Store id property name. */
  public static final String TICKET_HEADER_STORE_ID_PROP = "storeId";

  /** Till id property name. */
  public static final String TICKET_HEADER_TILL_ID_PROP = "tillId";

  /** Total HT property name. */
  public static final String TICKET_HEADER_TOTAL_HT_PROP = "totalHT";

  /** Lines property name. */
  public static final String TICKET_HEADER_LINES_PROP = "lines";

  /** Methods property name. */
  public static final String TICKET_HEADER_METHODS_PROP = "methods";

  /** Total quantity property name. */
  public static final String TICKET_HEADER_TOTAL_QUANTITY = "totalQuantity";

  /** Total price TTC property name. */
  public static final String TICKET_HEADER_TOTAL_PRICE_TTC = "totalPriceTTC";

  /** Total price HT property name. */
  public static final String TICKET_HEADER_TOTAL_PRICE_HT = "totalHT";
  
  //-------------------------------------
  // Ticket Line property names.
  /** Ticket Product label property name. */
  public static final String TICKET_LINE_PRODUCT_LABEL_PROP = "productLabel";

  /** Ticket Line Product Id property name. */
  public static final String TICKET_LINE_PRODUCT_ID_PROP = "productId";

  /** Ticket Line LineNumber property name. */
  public static final String TICKET_LINE_LINE_NUMBER_PROP = "lineNumber";

  /** Ticket Line productQuantity property name. */
  public static final String TICKET_LINE_PRODUCT_QUANTITY_PROP = "productQuantity";

  /** Ticket Line discountAmount property name. */
  public static final String TICKET_LINE_DISCOUNT_AMOUNT_PROP = "discountAmount";

  /** Ticket Line discountLabel property name. */
  public static final String TICKET_LINE_DISCOUNT_LABEL_PROP = "discountLabel";

  /** Ticket Line unit price TTC property name. */
  public static final String TICKET_LINE_UNIT_PRICE_TTC_PROP = "unitPriceTTC";

  /** Ticket Line unit price HT property name. */
  public static final String TICKET_LINE_UNIT_PRICE_HT_PROP = "unitPriceHT";

  /** Ticket Line rate TVA property name. */
  public static final String TICKET_LINE_RATE_TVA_PROP = "rateTVA";
  
  //-------------------------------------
  // Ticket Method property names.
  /** Ticket Method paymentAmount property name. */
  public static final String TICKET_METHOD_PAYMENT_AMOUNT_PROP = "paymentAmmount";

  /** Ticket Method paymentType property name. */
  public static final String TICKET_METHOD_PAYMENT_TYPE_PROP = "paymentType";

  //-------------------------------------
  // Other property names.
  /** SKU Code property name. */
  public static final String SKU_CODE_PROP = "CodeArticle";

  /** SKU Creation date property name. */
  public static final String SKU_CREATION_DATE = "dateCreationCodeArticle";

  /** SKU Display Name property name. */
  public static final String SKU_DISPLAY_NAME = "displayName";
  
}
