package com.castorama.integration.ticket;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import com.castorama.commerce.pricing.CastVATManager;
import com.castorama.integration.IntegrationBase;
import com.castorama.integration.util.MiscUtils;

import atg.adapter.gsa.GSARepository;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ParseHeaderObject implements ParseTicketable {
  
  public static final String LINE_TYPE_HEADER = "1";
  
  public static final String LINE_TYPE_METHOD = "2";
  
  public static final String LINE_TYPE_LINE = "3";
  
  public static final String USER = "user";

  public static final String ACTIVE = "active";

  public static final String TICKET_HEADER = "ticketHeader";
  
  public static final String TICKET_METHOD = "ticketMethod";
  
  public static final String TICKET_LINE = "ticketLine";

  public static final String HEADER_TICKET_ID = "ticketId";
  
  public static final String HEADER_STORE_ID = "storeId";

  public static final String HEADER_TICKET_DATE = "ticketDate";

  public static final String HEADER_WDM_ID = "wdmId";

  public static final String HEADER_HOME_ID = "homeId";

  public static final String HEADER_HOSTESS_ID = "hostessId";

  public static final String HEADER_TOTAL_QUANTITY = "totalQuantity";

  public static final String HEADER_TOTAL_PRICE_TTC = "totalPriceTTC";

  public static final String HEADER_TOTAL_HT = "totalHT";

  public static final String HEADER_TILL_ID = "tillId";

  public static final String METHOD_HEADER = "header";

  public static final String METHOD_PAYMENT_AMMOUNT = "paymentAmmount";

  public static final String METHOD_PAYMENT_TYPE = "paymentType";

  public static final String LINE_HEADER = "header";

  public static final String LINE_PRODUCT_ID = "productId";

  public static final String LINE_PRODUCT_QUANTITY = "productQuantity";

  public static final String LINE_PRODUCT_LABEL = "productLabel";

  public static final String LINE_UNIT_PRICE_HT = "unitPriceHT";

  public static final String LINE_UNIT_PRICE_TTC = "unitPriceTTC";

  public static final String LINE_RATE_TVA = "rateTVA";

  public static final String LINE_DISCOUNT_AMOUNT = "discountAmount";

  public static final String LINE_DISCOUNT_LABEL = "discountLabel";

  public static final String LINE_PRODUCT_CREATION_DATE = "productCreationDate";

  public static final String LINE_LINE_NUMBER = "lineNumber";
  
  public static final String FIND_HEADER_BY_UNIQUE = "ticketId = ?0 AND storeId = ?1 AND ticketDate = ?2 RANGE +1";
  
  public static final String FIND_MAPPING_BY_HOME = "homeId = ?0";

  private TicketHeaderObject header = new TicketHeaderObject();

  private final List<String> chunk = new ArrayList<String>();

  private boolean isOk = true;
  
  private ImportTicketManager manager = null;
  
  private BufferedWriter writer = null;
  
  private Repository repository = null;
  
  private Repository profileRepository = null;
  
  private ParseTicketObject helper = null;

  private double defaultTVARate;

  private double defaultDiscountAmount;
  
  private TransactionManager transactionManager;

  private CastVATManager castVATManager;

    public ParseHeaderObject(String name,
                             ImportTicketManager manager,
                             BufferedWriter writer,
                             Repository repository,
                             Repository profileRepository,
                             TransactionManager transactionManager,
                             CastVATManager castVATManager) {
        this.manager = manager;
        this.writer = writer;
        this.repository = repository;
        this.profileRepository = profileRepository;
        this.defaultTVARate = manager.getDefaultTVARate();
        this.defaultDiscountAmount = manager.getDefaultDiscountAmount();
        this.transactionManager = transactionManager;
        this.castVATManager = castVATManager;
        this.helper = new ParseTicketObject(HEADER, name, repository, manager);
    }

  public void addLine(String line) {
    boolean isHeader = false;
    if ( null != line ) {
      String[] tokens = line.split(PARSE_DELIMITER);
      if ( null != tokens && 0 < tokens.length ) {
        if ( LINE_TYPE_HEADER.equals(tokens[0]) ) {
          if ( null != header.getTicketId() ) {
            write(null);
          }
          isHeader = true;
          header.clear();
          parseHeader(tokens);
        } else if ( LINE_TYPE_METHOD.equals(tokens[0]) ) {
          parseMethod(tokens);
        } else if ( LINE_TYPE_LINE.equals(tokens[0]) ) {
          parseLine(tokens);
        }
      }
    }
    if ( !isHeader ) {
      write(line);
    }
  }

  protected void parseHeader(String[] pTokens) {
    if ( isOk ) {
      try {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        for (int i = 1; i < pTokens.length; i++) {
          switch (i) {
            case 1: header.setTicketId(pTokens[i]); break;
            case 2: header.setStoreId(pTokens[i]); break;
            case 3: header.setHomeId(pTokens[i]); break;
            case 4: parseDate(calendar, pTokens[i]); break;
            case 5: header.setTicketDate(parseSeconds(calendar, pTokens[i])); break;
            case 6: header.setTotalQuantity(parseDouble(pTokens[i])); break;
            case 7: header.setTotalPriceTTC(parseDouble(pTokens[i])); break;
            case 8: header.setTotalHT(parseDouble(pTokens[i])); break;
            case 9: header.setTillId(pTokens[i]); break;
            case 10: header.setHostessId(pTokens[i]); break;
          }
        }
      } catch (Exception e) {
        if ( manager.isLoggingDebug() ) {
          manager.logDebug("Can't parse header, because: " + e.getMessage());
        }
        System.out.println(e.getMessage()); 
        isOk = false;
      }
    }
  }

  protected void parseMethod(String[] pTokens) {
    if ( isOk ) {
      try {
        TicketMethodObject method = new TicketMethodObject();
        for (int i = 1; i < pTokens.length; i++) {
          switch (i) {
          case 2: method.setPaymentType(parseInt(pTokens[i])); break;
          case 3: method.setPaymentAmmount(parseDouble(pTokens[i])); break;
          }
        }
        header.getMethods().add(method);
      } catch (Exception e) {
        if ( manager.isLoggingDebug() ) {
          manager.logDebug("Can't parse method, because: " + e.getMessage());
        }
        System.out.println(e.getMessage());
        isOk = false;
      }
    }
  }

  protected void parseLine(String[] pTokens) {
    if ( isOk ) {
      try {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        TicketLineObject line = new TicketLineObject();
        for (int i = 1; i < pTokens.length; i++) {
          switch (i) {
            case 2: line.setProductId(pTokens[i]); break;
            case 3: line.setProductQuantity(parseDouble(pTokens[i])); break;
            case 4: line.setUnitPriceHT(parseDouble(pTokens[i])); break;
            case 5: line.setUnitPriceTTC(parseDouble(pTokens[i])); break;
            case 6: line.setRateTVA(parseDouble(pTokens[i], castVATManager.getVATValue(header.getTicketDate()))); break;
            case 7: line.setLineNumber(parseInt(pTokens[i])); break;
            case 8: line.setDiscountAmount(parseDouble(pTokens[i], defaultDiscountAmount)); break;
            case 9: line.setProductLabel(MiscUtils.encodeString(pTokens[i])); break;
            case 10: line.setProductCreationDate(parseDate(calendar, pTokens[i])); break;
            case 11: line.setDiscountLabel(MiscUtils.encodeString(pTokens[i])); break;
          }
        }
        header.getLines().add(line);
      } catch (Exception e) {
        if ( manager.isLoggingDebug() ) {
          manager.logDebug("Can't parse line, because: " + e.getMessage());
        }
        System.out.println(e.getMessage());
        isOk = false;
      }
    }
  }

  private Date parseSeconds(Calendar calendar, String token) {
    calendar.set(Calendar.HOUR_OF_DAY, parseInt(token.substring(0,2)));
    calendar.set(Calendar.MINUTE, parseInt(token.substring(3,5)));
    calendar.set(Calendar.SECOND, parseInt(token.substring(6,8)));
    return calendar.getTime();
  }

  private Date parseDate(Calendar calendar, String token) {
    calendar.set(Calendar.YEAR, parseInt(token.substring(0,4)));
    calendar.set(Calendar.MONTH, parseInt(token.substring(4,6)) - 1);
    calendar.set(Calendar.DAY_OF_MONTH, parseInt(token.substring(6,8)));
    return calendar.getTime();
  }

  private int parseInt(String token) {
    return Integer.parseInt(token);
  }

  private double parseDouble(String token) {
    return Double.parseDouble(token);
  }

  private double parseDouble(String token, double def) {
    return ( 0 < token.trim().length() ) ? parseDouble(token) : def;
  }

  public List<String> getChunk() {
    return chunk;
  }

  // I have assumption, that main import tickets activity is add new items, but update appear rarely.
  // Thus update = remove + add, becuase remove items with relations is very simple.
  private MutableRepositoryItem saveItem() throws RepositoryException {
    MutableRepositoryItem result = null;
    boolean rollback = true;
    try {
      TransactionDemarcation trd = new TransactionDemarcation();
      try {
        transactionManager.setTransactionTimeout(IntegrationBase.DEFAULT_TRANSACTION_TIMEOUT);
        trd.begin(transactionManager, TransactionDemarcation.REQUIRES_NEW);
        clearItem();
        if ( isActiveMapping() ) {
          result = addHeader();
          addMethods(result);
          addLines(result);
          ((MutableRepository) repository).updateItem(result);
        }
        rollback = false;
      } finally {
        try {
          trd.end(rollback);
        } catch (TransactionDemarcationException e) {
          manager.logError("TransactionDemarcationException occured: ", e);
        }
        transactionManager.setTransactionTimeout(0);
      }
    } catch (SystemException e) {
      if (manager.isLoggingError()) {
        manager.logError("setTransactionTimeout failed", e);
      }
    } catch (TransactionDemarcationException e) {
      if (manager.isLoggingError()) {
        manager.logError("Creating transaction demarcation failed", e);
      }
    }
    return result;
  }

  private void clearItem() throws RepositoryException {
    RepositoryView view  = repository.getView(TICKET_HEADER);
    RqlStatement statement = RqlStatement.parseRqlStatement(FIND_HEADER_BY_UNIQUE);
    Object params[] = new Object[3];
    params[0] = header.getTicketId();
    params[1] = header.getStoreId();
    params[2] = header.getTicketDate();
    RepositoryItem [] items = statement.executeQuery (view, params);
    if ( null != items && 0 < items.length ) {
      ((MutableRepository) repository).removeItem(items[0].getRepositoryId(), TICKET_HEADER);
    } 
  }
  
  protected boolean isActiveMapping() throws RepositoryException {
    boolean result = false;
    RepositoryView view = profileRepository.getView(USER);
    RqlStatement statement = RqlStatement.parseRqlStatement(FIND_MAPPING_BY_HOME);
    Object params[] = new Object[1];
    params[0] = header.getHomeId();
    RepositoryItem [] items = statement.executeQuery(view, params);
    if ( null != items ) {
      for ( int index = 0; index < items.length; index++ ) {
        if ( (Boolean) items[index].getPropertyValue(ACTIVE) ) {
          result = true;
          break;
        }
      }
    }
    return result;
  }
  
  private MutableRepositoryItem addHeader() throws RepositoryException {
    MutableRepositoryItem result = ((MutableRepository) repository).createItem(TICKET_HEADER);
    result.setPropertyValue(HEADER_TICKET_ID, header.getTicketId());
    result.setPropertyValue(HEADER_STORE_ID, header.getStoreId());
    result.setPropertyValue(HEADER_TICKET_DATE, header.getTicketDate());
    result.setPropertyValue(HEADER_WDM_ID, header.getWdmId());
    result.setPropertyValue(HEADER_HOME_ID, header.getHomeId());
    result.setPropertyValue(HEADER_HOSTESS_ID, header.getHostessId());
    result.setPropertyValue(HEADER_TOTAL_QUANTITY, header.getTotalQuantity());
    result.setPropertyValue(HEADER_TOTAL_PRICE_TTC, header.getTotalPriceTTC());
    result.setPropertyValue(HEADER_TOTAL_HT, header.getTotalHT());
    result.setPropertyValue(HEADER_TILL_ID, header.getTillId());
    ((MutableRepository) repository).addItem(result);
    return result;
  }
  
  private void addMethods(MutableRepositoryItem pHeader) throws RepositoryException {
    MutableRepositoryItem method;
    for (int index = 0; index < header.getMethods().size(); index++ ) {
      method = ((MutableRepository) repository).createItem(TICKET_METHOD);
      method.setPropertyValue(METHOD_HEADER, pHeader);
      method.setPropertyValue(METHOD_PAYMENT_AMMOUNT, header.getMethods().get(index).getPaymentAmmount());
      method.setPropertyValue(METHOD_PAYMENT_TYPE, header.getMethods().get(index).getPaymentType());
      ((MutableRepository) repository).addItem(method);
    }
  }
  
  private void addLines(MutableRepositoryItem pHeader) throws RepositoryException {
    MutableRepositoryItem line;
    for (int index = 0; index < header.getLines().size(); index++ ) {
      line = ((MutableRepository) repository).createItem(TICKET_LINE);
      line.setPropertyValue(LINE_HEADER, pHeader);
      line.setPropertyValue(LINE_PRODUCT_ID, header.getLines().get(index).getProductId());
      line.setPropertyValue(LINE_PRODUCT_QUANTITY, header.getLines().get(index).getProductQuantity());
      line.setPropertyValue(LINE_PRODUCT_LABEL, header.getLines().get(index).getProductLabel());
      line.setPropertyValue(LINE_UNIT_PRICE_HT, header.getLines().get(index).getUnitPriceHT());
      line.setPropertyValue(LINE_UNIT_PRICE_TTC, header.getLines().get(index).getUnitPriceTTC());
      line.setPropertyValue(LINE_RATE_TVA, header.getLines().get(index).getRateTVA());
      line.setPropertyValue(LINE_DISCOUNT_AMOUNT, header.getLines().get(index).getDiscountAmount());
      line.setPropertyValue(LINE_DISCOUNT_LABEL, header.getLines().get(index).getDiscountLabel());
      line.setPropertyValue(LINE_PRODUCT_CREATION_DATE, header.getLines().get(index).getProductCreationDate());
      line.setPropertyValue(LINE_LINE_NUMBER, header.getLines().get(index).getLineNumber());
      ((MutableRepository) repository).addItem(line);
    }
  }
  
  public void finish() {
    addLine(null);
    helper.finish();
    ((GSARepository)repository).invalidateCaches();
  }

  public void write(String line) {
    if ( null == line ) {
      boolean saved = false;
      if ( isOk ) {
        try {
          saveItem();
          saved = true;
        } catch (RepositoryException e) {
          if ( manager.isLoggingError() ) {
            manager.logError("Can't save ticket", e);
          }
        }
      } else {
        helper.writeError(writer, chunk);
      }
      if ( saved ) {
        helper.incrementSuccess();
      } else {
        helper.incrementError();
      }
      chunk.clear();
      isOk = true;
    } else {
      chunk.add(line);
    }
  }
}
