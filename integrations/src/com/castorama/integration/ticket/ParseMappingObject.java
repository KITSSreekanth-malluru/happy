package com.castorama.integration.ticket;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.TransactionManager;

import com.castorama.integration.util.MiscUtils;

import atg.adapter.gsa.GSARepository;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

public class ParseMappingObject implements ParseTicketable {

  public static final String TICKET_HEADER = "ticketHeader";

  public static final String USER = "user";

  public static final String HOME_ID = "homeId";

  public static final String SOFINCO_ACCOUNT = "sofincoAccount";

  public static final String FIND_BY_HOME_CONDITION = "homeId = ?0";

  public static final String ACTIVE = "active";

  public static final String SOFINCO_NAME1 = "sofincoName1";

  public static final String SOFINCO_NAME2 = "sofincoName2";

  private TicketMappingObject mapping = new TicketMappingObject();

  private final List<String> chunk = new ArrayList<String>();

  private boolean isOk = true;
  
  private ImportTicketManager manager = null;
  
  private BufferedWriter writer = null;
  
  private Repository profileRepository = null;
  
  private Repository repository = null;
  
  private ParseTicketObject helper = null;
  
  private TransactionManager transactionManager;
  
  public ParseMappingObject(String name,
                            ImportTicketManager manager, 
                            BufferedWriter writer,
                            Repository repository,
                            Repository profileRepository,
                            TransactionManager transactionManager) {
    this.manager = manager;
    this.writer = writer;
    this.profileRepository = profileRepository;
    this.repository = repository;
    this.transactionManager = transactionManager;
    this.helper = new ParseTicketObject(MAPPING, name, repository, manager);
  }

  public void addLine(String line) {
    if ( null != line ) {
      chunk.add(line);
      String[] tokens = line.split(PARSE_DELIMITER);
      parseMapping(tokens);
      write(line);
    }
  }
  
  public void finish() {
    helper.finish();
    ((GSARepository)profileRepository).invalidateCaches();
    ((GSARepository)repository).invalidateCaches();
  }
  

  public void parseMapping(String[] pTokens) {
    if ( isOk ) {
      try {
        for (int i = 0; i < pTokens.length; i++) {
          switch (i) {
            case 0: mapping.setProfileId(pTokens[i]); break;
            case 1: mapping.setHomeId(pTokens[i]); break;
            case 2: mapping.setSofincoAccount(pTokens[i]); break;
            case 3: mapping.setActive(parseActive(pTokens[i])); break;
            case 4: mapping.setSofincoName1(MiscUtils.encodeString(pTokens[i])); break;
            case 5: mapping.setSofincoName2(MiscUtils.encodeString(pTokens[i])); break;
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

  private boolean parseActive(String token) {
    return "y".equalsIgnoreCase(token);
  }

  public List<String> getChunk() {
    return chunk;
  }

  protected MutableRepositoryItem saveItem() throws RepositoryException {
    MutableRepositoryItem result = null;
    result = ((MutableRepository) profileRepository).getItemForUpdate(mapping.getProfileId(), USER);
    if (result != null) {
      result.setPropertyValue(HOME_ID, mapping.getHomeId());
      result.setPropertyValue(SOFINCO_ACCOUNT, mapping.getSofincoAccount());
      result.setPropertyValue(ACTIVE, mapping.isActive());
      result.setPropertyValue(SOFINCO_NAME1, mapping.getSofincoName1());
      result.setPropertyValue(SOFINCO_NAME2, mapping.getSofincoName2());
      ((MutableRepository) profileRepository).updateItem(result);
    }
    clearTickets();
    return result;
  }

  protected void clearTickets() throws RepositoryException {
    if ( !mapping.isActive() ) {
      RepositoryView view = repository.getView(TICKET_HEADER);
      RqlStatement statement = RqlStatement.parseRqlStatement(FIND_BY_HOME_CONDITION);
      Object params[] = new Object[1];
      params[0] = mapping.getHomeId();
      RepositoryItem [] items = statement.executeQuery(view, params);
      if ( null != items ) {
        for ( int index = 0; index < items.length; index++ ) {
          ((MutableRepository) repository).removeItem(items[index].getRepositoryId(), TICKET_HEADER);
        }
      }
    }
  }

  public void write(String line) {
    boolean saved = false;
    if ( isOk ) {
      try {
        saveItem();
        saved = true;
      } catch (RepositoryException e) {
        if ( manager.isLoggingError() ) {
          manager.logError("Can't save mapping", e);
        }
      }
    } else {
      helper.incrementError();
      helper.writeError(writer, chunk);
    }
    if ( saved ) {
      helper.incrementSuccess();
    } else {
      helper.incrementError();
    }
    chunk.clear();
    isOk = true;
  }
}
