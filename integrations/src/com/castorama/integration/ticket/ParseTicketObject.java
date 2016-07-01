package com.castorama.integration.ticket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ParseTicketObject implements ParseTicketable {

  public static final String TICKET_STATISTIC = "ticketStatistic";
  public static final String IMPORT_NAME = "importName";
  public static final String IMPORT_DATE = "importDate";
  public static final String IMPORT_TYPE = "importType";
  public static final String SUCCESS_COUNT = "successCount";
  public static final String ERROR_COUNT = "errorCount";

  public static final String FIND_DATE = "importDate > ?0";


  private Repository repository = null;
  
  private int errorCount = 0;
  
  private int successCount = 0;
  
  private String name = null;

  private int type = 0;
  
  private ImportTicketManager manager = null;
  
  public ParseTicketObject(int type, String name, Repository repository, ImportTicketManager manager) {
    this.type = type;
    this.name = name;
    this.repository = repository;
    this.manager = manager;
  }
  
  public void incrementError() {
    errorCount++;
  }
  
  public void incrementSuccess() {
    successCount++;
  }
  
  public void addLine(String line) {
  }
  
  public void writeError(BufferedWriter writer, List<String> chunk) {
    if ( null != writer ) {
      for (int i = 0; i < chunk.size(); i++ ) {
        try {
          writer.write(chunk.get(i));
          writer.newLine();
        } catch (IOException e) {
          if ( manager.isLoggingDebug() ) {
            manager.logDebug("Can't write errors, because: " + e.getMessage());
          }
        }
      }
      try {
        writer.flush();
      } catch (IOException e) {
        if ( manager.isLoggingDebug() ) {
          manager.logDebug("Can't write errors, because: " + e.getMessage());
        }
      }
    }
  }

  public void finish() {
    try {
      MutableRepositoryItem item = ((MutableRepository) repository).createItem(TICKET_STATISTIC);
      item.setPropertyValue(IMPORT_NAME, name);
      item.setPropertyValue(IMPORT_DATE, GregorianCalendar.getInstance().getTime());
      item.setPropertyValue(IMPORT_TYPE, type);
      item.setPropertyValue(SUCCESS_COUNT, successCount);
      item.setPropertyValue(ERROR_COUNT, errorCount);
      ((MutableRepository) repository).addItem(item);
    } catch (Exception e) {
      if ( manager.isLoggingWarning() ) {
        manager.logWarning("Can't write import ticket statistic");
      }
    }
  }

  public boolean checkLastImport(Integer daysBeforeNotify) {
    boolean result;
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -daysBeforeNotify);
    Timestamp importDate = new Timestamp(calendar.getTimeInMillis());
    try {
      RepositoryView view = repository.getView(TICKET_STATISTIC);
      RqlStatement statement = RqlStatement.parseRqlStatement(FIND_DATE);
      Object params[] = new Object[1];
      params[0] = importDate;
      RepositoryItem [] items = statement.executeQuery (view, params);
      result = ( null != items && 0 < items.length ); 
    } catch (Exception e) {
      result = false;
    }
    return result;
  }
  
}
