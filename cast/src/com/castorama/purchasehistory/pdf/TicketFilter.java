/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory.pdf;

import atg.core.util.StringUtils;

import atg.repository.RepositoryItem;

import com.castorama.constantes.CastoConstantes;

import com.castorama.purchasehistory.Constants;
import com.castorama.purchasehistory.TicketRetrievalService;

import com.castorama.utils.SaxXmlBuilder;

import java.io.IOException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;


/**
 * <code>TicketFilter</code> is subclass of <code>SaxXmlBuilder</code>. It is
 * used for generation xml with Ticket data.
 *
 * @author Aliaksandr Surma
 */
public final class TicketFilter extends SaxXmlBuilder {
  //-------------------------------------
  // Attribute names
  /** Tickets's repository id attribute name. */
  private static final String ATTR_ID = "id";

  /** Common value attribute name. */
  private static final String ATTR_VALUE = "value";

  /** Ticket's id attribute name. */
  private static final String ATTR_TICKET_ID = "ticketId";

  /** Ticket's date attribute name. */
  private static final String ATTR_DATE = "date";

  /** Ticket's time attribute name. */
  private static final String ATTR_TIME = "time";

  /** Ticket's totalQuantity attribute name. */
  private static final String ATTR_TOTAL_QUANTITY = "totalQuantity";

  /** Ticket's total price TTC attribute name. */
  private static final String ATTR_TOTAL_PRICE_TTC = "totalPriceTTC";

  /** Ticket's total price HT attribute name. */
  private static final String ATTR_TOTAL_PRICE_HT = "totalHT";

  /** Ticket's sous-total price attribute name. */
  private static final String ATTR_SOUS_TOTAL = "sousTotal";

  /** Method's payment amount attribute name. */
  private static final String ATTR_PAYMENT_AMOUNT = "paymentAmmount";

  /** Method's payment code attribute name. */
  private static final String ATTR_PAYMENT_CODE = "paymentCode";

  /** Method's payment label attribute name. */
  private static final String ATTR_PAYMENT_LABEL = "paymentLabel";

  /** TVA's rate attribute name. */
  private static final String TVA_RATE = "tvaRate";

  /** TVA's diff attribute name. */
  private static final String TVA_DIFF = "diff";

  /** TVA's totalHT attribute name. */
  private static final String TVA_TOTAL_HT = "totalHT";

  /** Discount's label attribute name. */
  private static final String ATTR_DISCOUNT_LABEL = "discountLabel";

  /** Discount's amount attribute name. */
  private static final String ATTR_DISCOUNT_AMOUNT = "discountAmount";

  /** Item's lineNumber attribute name. */
  private static final String ATTR_LINE_NUMBER = "lineNumber";

  /** Item's productQuantity attribute name. */
  private static final String ATTR_PRODUCT_QUANTITY = "productQuantity";

  /** Item's productRef attribute name. */
  private static final String ATTR_PRODUCT_REF = "productRef";

  /** Item's productLabel attribute name. */
  private static final String ATTR_PRODUCT_LABEL = "productLabel";

  /** Item's productLabelLines attribute name. */
  private static final String ATTR_PRODUCT_LABEL_LINES = "productLabelLines";

  /** Items's unit price TTC attribute name. */
  private static final String ATTR_UNIT_PRICE_TTC = "unitPriceTTC";

  /** Items's unit price HT attribute name. */
  private static final String ATTR_UNIT_PRICE_HT = "unitPriceHT";

  /** Items's rate TVA attribute name. */
  private static final String ATTR_RATE_TVA = "rateTVA";

  /** Items's amount(price TTC * quantity) attribute name. */
  private static final String ATTR_AMOUNT = "amount";

  /** Store's street line1 attribute name. */
  private static final String ATTR_STREET_LINE1 = "line1";

  /** Store's street line2 attribute name. */
  private static final String ATTR_STREET_LINE2 = "line2";

  //-------------------------------------
  // Elements
  /** <ticket> element. */
  private static final String EL_TICKET = "ticket";

  /** <items> element. */
  private static final String EL_ITEMS = "items";

  /** <item> element. */
  private static final String EL_ITEM = "item";

  /** <rendu> element. */
  private static final String EL_RENDU = "rendu";

  /** <store> element. */
  private static final String STORE = "store";

  /** <address> element. */
  private static final String ADDRESS = "address";

  /** <street> element. */
  private static final String EL_STREET = "street";

  /** <tvas> element. */
  private static final String EL_TVAS = "tvas";

  /** <tva> element. */
  private static final String EL_TVA = "tva";

  /** <payments> element. */
  private static final String EL_PAYMENTS = "payments";

  /** <method> element. */
  private static final String EL_METHOD = "method";

  /** <discounts> element. */
  private static final String EL_DISCOUNTS = "discounts";

  /** <discount> element. */
  private static final String EL_DISCOUNT = "discount";

  /** <city> element. */
  private static final String EL_CITY = "city";

  /** <user> element. */
  private static final String EL_USER = "user";

  //-------------------------------------
  // Constants
  /** Holds the date format used on the receipt. */
  private static final String DATE_FORMAT = "dd-MM-yyyy";

  /** Holds the time format used on the receipt. */
  private static final String TIME_FORMAT = "HH:mm:ss";

  /** Holds the formatter for ticket's date. */
  private static final SimpleDateFormat SDF = new SimpleDateFormat(TicketFilter.DATE_FORMAT);

  /** Holds the formatter for ticket's time. */
  private static final SimpleDateFormat STF = new SimpleDateFormat(TicketFilter.TIME_FORMAT);

  /** NAME constant. */
  private static final String NAME = "name";

  /** CDATA constant. */
  private static final String CDATA = "CDATA";

  /** Wrap cutoff constant. */
  private static final int WRAP_CUTOFF = 22;

  /** Default discount label. */
  private static final String DEFAULT_DISCOUNT_LABEL = "REMISE PERSONNEL";

  /** Apache Commons Logging instance. */
  private static final Log LOGGER = LogFactory.getLog(TicketFilter.class);
  private static final NumberFormat numberFormatter = NumberFormat.getIntegerInstance();

  static {
    numberFormatter.setMinimumFractionDigits(0);
    numberFormatter.setMaximumFractionDigits(0);
    numberFormatter.setGroupingUsed(false);
  }

  private static final DecimalFormat decimalQuantityFormatter = new DecimalFormat("#.####");
  private static final NumberFormat numberFormatter2Digit = NumberFormat.getIntegerInstance();

  static {
    numberFormatter2Digit.setMinimumFractionDigits(2);
    numberFormatter2Digit.setMaximumFractionDigits(2);
    numberFormatter2Digit.setGroupingUsed(false);
  }

  /** Ticket repository object */
  private RepositoryItem mTicket;

  /** Store repository object */
  private RepositoryItem mStore;

  /** User object */
  private RepositoryItem mUser;

  /** TicketRetrievalService object */
  private TicketRetrievalService mTicketRetrievalService;

  /**
   * Creates a new TicketFilter object.
   */
  public TicketFilter(final RepositoryItem pItem, final RepositoryItem pStore, final RepositoryItem pUser,
    final TicketRetrievalService pTicketRetrievalService) {
    this.mTicket = pItem;
    this.mStore = pStore;
    this.mUser = pUser;
    this.mTicketRetrievalService = pTicketRetrievalService;
  }

  /**
   * Parse a document.
   *
   * @param  pInputSource The input source for the document entity
   *
   * @throws SAXException Any SAX exception, possibly wrapping another
   *                      exception.
   * @throws IOException  An IO exception from the parser, possibly from a
   *                      byte stream or character stream supplied by the
   *                      application
   */
  public void parse(final InputSource pInputSource) throws SAXException, IOException {
    startDocument();

    startTicketElement();

    if(mStore != null) {
      addStoreInfo();
    }

    addUserInfo();
    addTicketLines();
    // 0001969: [Purchase History] Add 'Remise perso' on ticket line with DISCOUNT_AMOUNT
    //
    // The sum of the discounts REMISO PERSO must be delete.
    // We already get it from payment type in ticket flow.
    //
    //addTicketDiscounts();
    addPaymentMethods();
    // 0001970: [Purchase History] Add line RENDU on ticket 
    addRenduLine();
    addTicketTvas();

    endTicketElement();

    endDocument();
  }

  /**
   * Adds <rendu></rendu> section.
   *
   * @throws SAXException if error occurs
   */
  private void addRenduLine() throws SAXException {
    final AttributesImpl attrs = new AttributesImpl();

    final Double value = retrieveRenduValue();

    attrs.addAttribute("", ATTR_VALUE, ATTR_VALUE, CDATA, (value != null) ? numberFormatter2Digit.format(Math.abs(value)) : "0.00");

    startElement("", EL_RENDU, EL_RENDU, attrs);

    endElement("", EL_RENDU, EL_RENDU);
  }

  /**
   * Get 'rendu' value from ticket's payments method.
   *
   * @return 'rendu' value
   */
  private Double retrieveRenduValue() {
    Double result = null;
    
    final Set<RepositoryItem> methods = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_METHODS_PROP);

    final Iterator<RepositoryItem> iterator = methods.iterator();

    while(iterator.hasNext()) {
      final RepositoryItem method = iterator.next();

      final Integer paymentType = (Integer) method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_TYPE_PROP);
      final Double paymentAmount = (Double) method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_AMOUNT_PROP);

      if((paymentType != null) && (paymentAmount != null) && (paymentType == 6) && (paymentAmount < 0)) {
        result = paymentAmount;

        break;
      }
    }

    return result;
  }

  /**
   * Adds <payments><method></method></payments> section.
   *
   * @throws SAXException if error occurs
   */
  private void addPaymentMethods() throws SAXException {
    final Set<RepositoryItem> methods = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_METHODS_PROP);

    startElement("", EL_PAYMENTS, EL_PAYMENTS, new AttributesImpl());

    final Iterator<RepositoryItem> iterator = methods.iterator();

    while(iterator.hasNext()) {
      final RepositoryItem method = iterator.next();

      final AttributesImpl methodAttributes = new AttributesImpl();
      methodAttributes.addAttribute("", ATTR_ID, ATTR_ID, CDATA, method.getRepositoryId());

      final Double paymentAmount = (Double)method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_AMOUNT_PROP);
      methodAttributes.addAttribute("", ATTR_PAYMENT_AMOUNT, ATTR_PAYMENT_AMOUNT, CDATA,
        (paymentAmount != null) ? numberFormatter2Digit.format(paymentAmount) : "");

      final Integer paymentType = (Integer) method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_TYPE_PROP);
      String code = null;

      if(paymentType != null) {
        //0001967: [Purchase History] Don't display payment type 58 
        if(paymentType == 58) {
          continue;
        }
        
        if(paymentType == 6 && paymentAmount < 0) {
          // Skip this payment - it will be displayed in 'rendu' line
          // See 0001970: [Purchase History] Add line RENDU on ticket
          continue;
        }
        
        
        code = paymentType.toString();
      }

      final String paymentLabel = mTicketRetrievalService.getMappedPaymentMethod(code);

      methodAttributes.addAttribute("", ATTR_PAYMENT_CODE, ATTR_PAYMENT_CODE, CDATA, (code != null) ? code : "");
      methodAttributes.addAttribute("", ATTR_PAYMENT_LABEL, ATTR_PAYMENT_LABEL, CDATA,
        (paymentLabel != null) ? paymentLabel.toUpperCase().trim() : "");

      startElement("", EL_METHOD, EL_METHOD, methodAttributes);
      endElement("", EL_METHOD, EL_METHOD);
    }

    endElement("", EL_PAYMENTS, EL_PAYMENTS);
  }

  /**
   * Adds </ticket> element.
   *
   * @throws SAXException if error occurs
   */
  private void endTicketElement() throws SAXException {
    endElement("", EL_TICKET, EL_TICKET);
  }

  /**
   * Adds <tvas><tva></tva></tvas> section
   *
   * @throws SAXException if error occurs
   */
  private void addTicketTvas() throws SAXException {
    final Set<RepositoryItem> lines = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_LINES_PROP);

    startElement("", EL_TVAS, EL_TVAS, new AttributesImpl());

    final Map<String, TvaDataHolder> tvas = retrieveTvasFromLineItems(lines);
    final Set<String> tvaKeySet = tvas.keySet();

    final Iterator<String> tvaIterator = tvaKeySet.iterator();

    while(tvaIterator.hasNext()) {
      final String key = (String) tvaIterator.next();
      final TvaDataHolder dataHolder = (TvaDataHolder) tvas.get(key);

      final AttributesImpl tvaAttributes = new AttributesImpl();

      tvaAttributes.addAttribute("", TVA_RATE, TVA_RATE, CDATA, key);
      tvaAttributes.addAttribute("", TVA_DIFF, TVA_DIFF, CDATA, numberFormatter2Digit.format(dataHolder.getPriceDiff()));
      tvaAttributes.addAttribute("", TVA_TOTAL_HT, TVA_TOTAL_HT, CDATA,
        numberFormatter2Digit.format(dataHolder.getTotalHT()));

      startElement("", EL_TVA, EL_TVA, tvaAttributes);
      endElement("", EL_TVA, EL_TVA);
    }

    endElement("", EL_TVAS, EL_TVAS);
  }

  /**
   * Adds ticket <discount><method></method></discount> section.
   *
   * @throws SAXException if error occurs.
   */
  private void addTicketDiscounts() throws SAXException {
    Set<RepositoryItem> lines = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_LINES_PROP);

    startElement("", EL_DISCOUNTS, EL_DISCOUNTS, new AttributesImpl());

    final Map<String, DiscountDataHolder> discounts = retrieveDiscountsFromLineItems(lines);
    final Set<String> keySet = discounts.keySet();

    final Iterator<String> discountIterator = keySet.iterator();

    while(discountIterator.hasNext()) {
      final String key = (String) discountIterator.next();
      final DiscountDataHolder dataHolder = (DiscountDataHolder) discounts.get(key);
      final AttributesImpl tvaAttributes = new AttributesImpl();

      tvaAttributes.addAttribute("", ATTR_DISCOUNT_LABEL, ATTR_DISCOUNT_LABEL, CDATA, key);
      tvaAttributes.addAttribute("", ATTR_DISCOUNT_AMOUNT, ATTR_DISCOUNT_AMOUNT, CDATA,
        numberFormatter2Digit.format(dataHolder.getDiscountAmount()));

      startElement("", EL_DISCOUNT, EL_DISCOUNT, tvaAttributes);
      endElement("", EL_DISCOUNT, EL_DISCOUNT);
    }

    endElement("", EL_DISCOUNTS, EL_DISCOUNTS);
  }

  /**
   * Adds <items><item></item></items> section
   *
   * @throws SAXException if error occurs
   */
  private void addTicketLines() throws SAXException {
    startElement("", EL_ITEMS, EL_ITEMS, new AttributesImpl());

    Set<RepositoryItem> lines = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_LINES_PROP);
    Iterator<RepositoryItem> iterator = lines.iterator();

    while(iterator.hasNext()) {
      RepositoryItem line = (RepositoryItem) iterator.next();

      AttributesImpl lineAttributes = new AttributesImpl();
      lineAttributes.addAttribute("", ATTR_ID, ATTR_ID, CDATA, line.getRepositoryId());

      Object value = line.getPropertyValue(Constants.TICKET_LINE_LINE_NUMBER_PROP);
      lineAttributes.addAttribute("", ATTR_LINE_NUMBER, ATTR_LINE_NUMBER, CDATA, (value != null) ? value.toString() : "");

      final Double quantity = (Double) line.getPropertyValue(Constants.TICKET_LINE_PRODUCT_QUANTITY_PROP);
      lineAttributes.addAttribute("", ATTR_PRODUCT_QUANTITY, ATTR_PRODUCT_QUANTITY, CDATA,
        (value != null) ? decimalQuantityFormatter.format(quantity) : "");

      value = line.getPropertyValue(Constants.TICKET_LINE_PRODUCT_ID_PROP);
      lineAttributes.addAttribute("", ATTR_PRODUCT_REF, ATTR_PRODUCT_REF, CDATA,
        (value != null) ? value.toString().trim() : "");

      final Date ticketDate = (Date) mTicket.getPropertyValue(Constants.TICKET_HEADER_TICKET_DATE_PROP);
      final String productLabel = mTicketRetrievalService.retrieveProductLabel(line, ticketDate);

      lineAttributes.addAttribute("", ATTR_PRODUCT_LABEL, ATTR_PRODUCT_LABEL, CDATA,
        (productLabel != null) ? productLabel.toUpperCase().trim() : "");

      // Logic was added in scope of Mantis 0001924.
      //
      // New attribute holds the number of lines for productLabel string.
      // This value is used during PDF generation to estimate the ticket size.
      int length = 1; //default value

      if(!StringUtils.isEmpty(productLabel)) {
        final String productLabelWrapped = WordUtils.wrap(productLabel, WRAP_CUTOFF);
        String[] productLabelLines = productLabelWrapped.split(System.getProperty("line.separator"));
        length = productLabelLines.length;
      }

      lineAttributes.addAttribute("", ATTR_PRODUCT_LABEL_LINES, ATTR_PRODUCT_LABEL_LINES, CDATA, String.valueOf(length));

      value = line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_HT_PROP);
      lineAttributes.addAttribute("", ATTR_UNIT_PRICE_HT, ATTR_UNIT_PRICE_HT, CDATA,
        (value != null) ? numberFormatter2Digit.format(value) : "");

      final Double unitPriceTTC = (Double) line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_TTC_PROP);
      lineAttributes.addAttribute("", ATTR_UNIT_PRICE_TTC, ATTR_UNIT_PRICE_TTC, CDATA,
        (value != null) ? numberFormatter2Digit.format(unitPriceTTC) : "");

      Double amount = 0.0d;

      if((unitPriceTTC != null) && (quantity != null)) {
        amount = unitPriceTTC * quantity;
      }

      lineAttributes.addAttribute("", ATTR_AMOUNT, ATTR_AMOUNT, CDATA,
        (value != null) ? numberFormatter2Digit.format(amount) : "");

      value = line.getPropertyValue(Constants.TICKET_LINE_RATE_TVA_PROP);
      lineAttributes.addAttribute("", ATTR_RATE_TVA, ATTR_RATE_TVA, CDATA,
        (value != null) ? numberFormatter2Digit.format(value) : "");

      Double discountAmount = (Double) line.getPropertyValue(Constants.TICKET_LINE_DISCOUNT_AMOUNT_PROP);
      //      String discountLabel = (String) line.getPropertyValue(Constants.TICKET_LINE_DISCOUNT_LABEL_PROP);
      startElement("", EL_ITEM, EL_ITEM, lineAttributes);

      //Check only discountAmount
      //0001969: [Purchase History] Add 'Remise perso' on ticket line with DISCOUNT_AMOUNT 
      if((discountAmount != null) && (discountAmount > 0)) {
        AttributesImpl discountAttributes = new AttributesImpl();

        discountAttributes.addAttribute("", ATTR_DISCOUNT_AMOUNT, ATTR_DISCOUNT_AMOUNT, CDATA,
          numberFormatter2Digit.format(discountAmount));
        //        discountAttributes.addAttribute("", ATTR_DISCOUNT_LABEL, ATTR_DISCOUNT_LABEL, CDATA,
        //          discountLabel.toUpperCase().trim());
        startElement("", EL_DISCOUNT, EL_DISCOUNT, discountAttributes);
        endElement("", EL_DISCOUNT, EL_DISCOUNT);
      }

      endElement("", EL_ITEM, EL_ITEM);
    }

    endElement("", EL_ITEMS, EL_ITEMS);
  }

  /**
   * Starts <ticket> element.
   *
   * @throws SAXException if error occurs
   */
  private void startTicketElement() throws SAXException {
    final AttributesImpl ticketAttrs = new AttributesImpl();
    Object value = mTicket.getPropertyValue(Constants.TICKET_HEADER_TICKET_ID_PROP);

    ticketAttrs.addAttribute("", ATTR_ID, ATTR_ID, CDATA, mTicket.getRepositoryId());

    ticketAttrs.addAttribute("", ATTR_TICKET_ID, ATTR_TICKET_ID, CDATA, (value != null) ? value.toString() : "");

    value = mTicket.getPropertyValue(Constants.TICKET_HEADER_TICKET_DATE_PROP);
    ticketAttrs.addAttribute("", ATTR_DATE, ATTR_DATE, CDATA, (value != null) ? SDF.format(value) : "");

    ticketAttrs.addAttribute("", ATTR_TIME, ATTR_TIME, CDATA, (value != null) ? STF.format(value) : "");

    value = mTicket.getPropertyValue(Constants.TICKET_HEADER_TOTAL_QUANTITY);
    ticketAttrs.addAttribute("", ATTR_TOTAL_QUANTITY, ATTR_TOTAL_QUANTITY, CDATA,
      (value != null) ? decimalQuantityFormatter.format(value) : "");

    value = mTicket.getPropertyValue(Constants.TICKET_HEADER_TOTAL_PRICE_TTC);

    final Double totalTTC = (Double) value;

    ticketAttrs.addAttribute("", ATTR_TOTAL_PRICE_TTC, ATTR_TOTAL_PRICE_TTC, CDATA,
      (value != null) ? numberFormatter2Digit.format(value) : "");

    value = mTicket.getPropertyValue(Constants.TICKET_HEADER_TOTAL_PRICE_HT);
    ticketAttrs.addAttribute("", ATTR_TOTAL_PRICE_HT, ATTR_TOTAL_PRICE_HT, CDATA,
      (value != null) ? numberFormatter2Digit.format(value) : "");

    Double sousTotal = calculateSousTotal(totalTTC);

    ticketAttrs.addAttribute("", ATTR_SOUS_TOTAL, ATTR_SOUS_TOTAL, CDATA,
      (value != null) ? numberFormatter2Digit.format(sousTotal) : "");

    value = mTicket.getPropertyValue("storeId");
    ticketAttrs.addAttribute("", "storeId", "storeId", CDATA, (value != null) ? value.toString() : "");

    value = mTicket.getPropertyValue("tillId");
    ticketAttrs.addAttribute("", "tillId", "tillId", CDATA, (value != null) ? value.toString() : "");

    value = mTicket.getPropertyValue("hostessId");
    ticketAttrs.addAttribute("", "hostessId", "hostessId", CDATA, (value != null) ? value.toString() : "");

    Set lines = (Set) mTicket.getPropertyValue(Constants.TICKET_HEADER_LINES_PROP);

    int size = lines.size();
    ticketAttrs.addAttribute("", "numberOfItems", "numberOfItems", CDATA, String.valueOf(size));

    startElement("", EL_TICKET, EL_TICKET, ticketAttrs);
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pTotalTTC ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private Double calculateSousTotal(final Double pTotalTTC) {
    if(pTotalTTC == null) {
      return null;
    }

    final Set<RepositoryItem> methods = (Set<RepositoryItem>) mTicket.getPropertyValue(Constants.TICKET_HEADER_METHODS_PROP);
    final Iterator<RepositoryItem> iterator = methods.iterator();

    double result = pTotalTTC.doubleValue();
    double advance = 0.0d;

    while(iterator.hasNext()) {
      final RepositoryItem method = iterator.next();

      final Integer paymentType = (Integer) method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_TYPE_PROP);

      String code = null;

      if(paymentType != null) {
        code = paymentType.toString();
      }

      // The advance paid by the customer (corresponding to payment type = 11).
      final String advancePaidByCustomerPaymentCode = mTicketRetrievalService.getAdvancePaidByCustomerPaymentCode();

      if((code != null) && code.equals(advancePaidByCustomerPaymentCode)) {
        final Double paymentAmount = (Double) method.getPropertyValue(Constants.TICKET_METHOD_PAYMENT_AMOUNT_PROP);

        if(paymentAmount != null) {
          advance += paymentAmount;
        }
      }
    }

    return result - advance;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pLines ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private Map<String, TvaDataHolder> retrieveTvasFromLineItems(final Set<RepositoryItem> pLines) {
    final Map<String, TvaDataHolder> tvas = new HashMap<String, TvaDataHolder>();
    final Iterator<RepositoryItem> iterator = pLines.iterator();

    while(iterator.hasNext()) {
      final RepositoryItem line = iterator.next();
      final Double tvaDouble = (Double) line.getPropertyValue("rateTVA");

      // (tvaDouble > 0) condition was removed 
      // 0001974: [Purchase History] Display a TVA rate equals to 0 in TVA detail 
      if((tvaDouble != null)) {
        String tvaKey = numberFormatter2Digit.format(tvaDouble);

        if(tvas.containsKey(tvaKey)) {
          final TvaDataHolder tvaData = (TvaDataHolder) tvas.get(tvaKey);
          final Double itemPriceHT = (Double) line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_HT_PROP);
          final Double itemPriceTTC = (Double) line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_TTC_PROP);
          final Double quantity = (Double) line.getPropertyValue(Constants.TICKET_LINE_PRODUCT_QUANTITY_PROP);
          
          if((itemPriceHT != null) && (itemPriceTTC != null) && (quantity != null)) {
            tvaData.addTotalHT(itemPriceHT);
            // 0001983: [Purchase History] TVA amount less than 0
            // add product quantity
            tvaData.addTotalTTC(itemPriceTTC * quantity);
          }
        } else {
          final TvaDataHolder tvaData = new TvaDataHolder();
          final Double itemPriceHT = (Double) line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_HT_PROP);
          final Double itemPriceTTC = (Double) line.getPropertyValue(Constants.TICKET_LINE_UNIT_PRICE_TTC_PROP);
          final Double quantity = (Double) line.getPropertyValue(Constants.TICKET_LINE_PRODUCT_QUANTITY_PROP);

          if((itemPriceHT != null) && (itemPriceTTC != null) && (quantity != null)) {
            tvaData.addTotalHT(itemPriceHT);
            // 0001983: [Purchase History] TVA amount less than 0
            // add product quantity
            tvaData.addTotalTTC(itemPriceTTC * quantity);
          }

          tvas.put(tvaKey, tvaData);
        }
      }
    }

    return tvas;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pLines ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  private Map<String, DiscountDataHolder> retrieveDiscountsFromLineItems(final Set<RepositoryItem> pLines) {
    final Map<String, DiscountDataHolder> discounts = new HashMap<String, DiscountDataHolder>();
    final Iterator<RepositoryItem> iterator = pLines.iterator();

    while(iterator.hasNext()) {
      final RepositoryItem line = iterator.next();

      //Mantis 0001969: [Purchase History] Add 'Remise perso' on ticket line with DISCOUNT_AMOUNT 
      //final String discountLabel = (String) line.getPropertyValue(Constants.TICKET_LINE_DISCOUNT_LABEL_PROP);
      final Double discountAmount = (Double) line.getPropertyValue(Constants.TICKET_LINE_DISCOUNT_AMOUNT_PROP);

      if((discountAmount != null) && (discountAmount > 0)) {
        //String discountKey = discountLabel.toUpperCase().trim();
        String discountKey = DEFAULT_DISCOUNT_LABEL;

        if(discounts.containsKey(discountKey)) {
          final DiscountDataHolder discountData = (DiscountDataHolder) discounts.get(discountKey);

          if(discountAmount != null) {
            discountData.addDiscountAmount(discountAmount);
          }
        } else {
          final DiscountDataHolder discountData = new DiscountDataHolder();

          if(discountAmount != null) {
            discountData.addDiscountAmount(discountAmount);
          }

          discounts.put(discountKey, discountData);
        }
      }
    }

    return discounts;
  }

  /**
   * Adds <user></user> section.
   *
   * @throws SAXException if error occurs
   */
  private void addUserInfo() throws SAXException {
    final AttributesImpl userAttrs = new AttributesImpl();

    String value = (String) mUser.getPropertyValue("sofincoName1");
    userAttrs.addAttribute("", "sofincoName1", "sofincoName1", CDATA, (value != null) ? value.toUpperCase() : "");

    value = (String) mUser.getPropertyValue("sofincoName2");
    userAttrs.addAttribute("", "sofincoName2", "sofincoName2", CDATA, (value != null) ? value.toUpperCase() : "");

    startElement("", EL_USER, EL_USER, userAttrs);
    endElement("", EL_USER, EL_USER);
  }

  /**
   * Adds <store></store> section.
   *
   * @throws SAXException if error occurs
   */
  private void addStoreInfo() throws SAXException {
    final AttributesImpl storeAttrs = new AttributesImpl();

    String value = (String) mStore.getPropertyValue(CastoConstantes.NOM);
    storeAttrs.addAttribute("", NAME, NAME, CDATA, (value != null) ? value.toUpperCase() : "");

    startElement("", STORE, STORE, storeAttrs);

    final AttributesImpl addressAttributes = new AttributesImpl();
    startElement("", ADDRESS, ADDRESS, addressAttributes);

    String street = "";
    String postalCode = "";
    String city = "";
    String tel = "";
    String fax = "";

    final RepositoryItem entite = (RepositoryItem) mStore.getPropertyValue(CastoConstantes.ENTITE);

    if(entite != null) {
      RepositoryItem address = (RepositoryItem) entite.getPropertyValue(CastoConstantes.ADRESSE);

      if(address != null) {
        street = (String) address.getPropertyValue("rue");
        postalCode = (String) address.getPropertyValue("cp");
        city = (String) address.getPropertyValue("ville");
        tel = (String) address.getPropertyValue("tel");
        fax = (String) address.getPropertyValue("fax");
      }
    }

    startElement("", EL_STREET, EL_STREET, new AttributesImpl());

    String delimeter = null;

    if(street != null) {
      street = street.toLowerCase();

      if(street.contains("<br/>")) {
        delimeter = "<br/>";
      } else if(street.contains("<br>")) {
        delimeter = "<br>";
      } else if(street.contains("<br />")) {
        delimeter = "<br />";
      }

      if(delimeter != null) {
        String[] splitStringAtString = StringUtils.splitStringAtString(street, delimeter);

        if(!StringUtils.isEmpty(splitStringAtString[0])) {
          startElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1, new AttributesImpl());
          characters(splitStringAtString[0].toUpperCase().toCharArray(), 0, splitStringAtString[0].length());
          endElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1);
        }

        if(!StringUtils.isEmpty(splitStringAtString[1])) {
          startElement("", ATTR_STREET_LINE2, ATTR_STREET_LINE2, new AttributesImpl());
          characters(splitStringAtString[1].toUpperCase().toCharArray(), 0, splitStringAtString[1].length());
          endElement("", ATTR_STREET_LINE2, ATTR_STREET_LINE2);
        }
      } else {
        startElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1, new AttributesImpl());
        characters(street.toUpperCase().toCharArray(), 0, street.length());
        endElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1);
      }
    } else {
      //Street is null. For ex: castorama.fr store doesn't have it
      startElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1, new AttributesImpl());
      endElement("", ATTR_STREET_LINE1, ATTR_STREET_LINE1);
    }

    endElement("", EL_STREET, EL_STREET);

    startElement("", EL_CITY, EL_CITY, new AttributesImpl());

    if(city != null) {
      // Mantis 0001923: [Purchase History] HTML Tag on store address 
      city = removeHTMLtags(city);
      characters(city.toUpperCase().toCharArray(), 0, city.length());
    }

    endElement("", EL_CITY, EL_CITY);

    startElement("", "postal-code", "postal-code", new AttributesImpl());

    if(postalCode != null) {
      characters(postalCode.toCharArray(), 0, postalCode.length());
    }

    endElement("", "postal-code", "postal-code");

    startElement("", "tel", "tel", new AttributesImpl());

    if(tel != null) {
      characters(tel.toCharArray(), 0, tel.length());
    }

    endElement("", "tel", "tel");

    startElement("", "fax", "fax", new AttributesImpl());

    if(fax != null) {
      characters(fax.toCharArray(), 0, fax.length());
    }

    endElement("", "fax", "fax");

    endElement("", ADDRESS, ADDRESS);
    endElement("", STORE, STORE);
  }

  /**
   * Removes HTML tags from Address's city
   *
   * @param pCity city
   *
   * @return result string
   */
  private String removeHTMLtags(final String pCity) {
    String result = pCity.toLowerCase();

    // Remove img tag
    // For example:
    // Address 78240
    // Ville = CHAMBOURCY <br /><br /><img src="/homepage/images/offre-15euros-magasins.gif" />
    if(result.contains("<br />")) {
      result = org.apache.commons.lang.StringUtils.remove(result, "<br />");
    }

    if(result.contains("<br/>")) {
      result = org.apache.commons.lang.StringUtils.remove(result, "<br/>");
    }

    if(result.contains("<img")) {
      int beginIndex = result.indexOf("<img");
      int endIndex = result.indexOf("/>", beginIndex);
      String strToRemove = result.substring(beginIndex, endIndex + "/>".length());

      result = org.apache.commons.lang.StringUtils.remove(result, strToRemove);
    }

    // For example:
    // Address 69570
    // Ville = DARDILLY <a href="/magasins/offre-dardilly.pdf" class="offre">Votre offre exceptionnelle ouverture</a>
    if(result.contains("<a href=") && result.contains("</a>")) {
      int beginIndex = result.indexOf("<a href=");
      int endIndex = result.indexOf("</a>");
      String strToRemove = result.substring(beginIndex, endIndex + "</a>".length());

      result = org.apache.commons.lang.StringUtils.remove(result, strToRemove);
    }

    return result;
  }

  /**
   * Helper class for TVA data.
   *
   * @author Aliaksandr Surma
    */
  private class TvaDataHolder {
    private double mTotalHT = 0.0d;
    private double mTotalTTC = 0.0d;

    /**
     * Gets the totalHT property.
     *
     * @return the totalHT
     */
    public final double getTotalHT() {
      return mTotalHT;
    }

    /**
     * Sets the totalHT property.
     *
     * @param pTotalHT the totalHT to set
     */
    public final void setTotalHT(final double pTotalHT) {
      mTotalHT = pTotalHT;
    }

    /**
     * Adds value to the totalHT property.
     *
     * @param pTotalHT the totalHT to add
     */
    public final void addTotalHT(final double pTotalHT) {
      mTotalHT += pTotalHT;
    }

    /**
     * Gets the totalTTC property.
     *
     * @return the totalTTC
     */
    public final double getTotalTTC() {
      return mTotalTTC;
    }

    /**
     * Sets the totalTTC property.
     *
     * @param pTotalTTC the totalTTC to set
     */
    public final void setTotalTTC(final double pTotalTTC) {
      mTotalTTC = pTotalTTC;
    }

    /**
     * Adds value to the totalTTC property.
     *
     * @param pTotalTTC the totalTTC to add
     */
    public final void addTotalTTC(final double pTotalTTC) {
      mTotalTTC += pTotalTTC;
    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @return ToDo: DOCUMENT ME!
     */
    public final double getPriceDiff() {
      return mTotalTTC - mTotalHT;
    }
  }

  /**
   * Helper class for discount data.
   *
   * @author Aliaksandr Surma
    */
  private class DiscountDataHolder {
    private double mDiscountAmount = 0.0d;

    /**
     * Gets the discountAmount property.
     *
     * @return the discountAmount
     */
    public final double getDiscountAmount() {
      return mDiscountAmount;
    }

    /**
     * Sets the discountAmount property.
     *
     * @param pDiscountAmount the discountAmount to set
     */
    public final void setDiscountAmount(final double pDiscountAmount) {
      mDiscountAmount = pDiscountAmount;
    }

    /**
     * Adds value to the discountAmount property.
     *
     * @param pDiscountAmount the discountAmount to add
     */
    public final void addDiscountAmount(final double pDiscountAmount) {
      mDiscountAmount += pDiscountAmount;
    }
  }
}
