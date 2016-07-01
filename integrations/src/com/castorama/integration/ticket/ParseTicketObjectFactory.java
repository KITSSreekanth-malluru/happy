package com.castorama.integration.ticket;

import java.io.BufferedWriter;

import javax.transaction.TransactionManager;

import atg.repository.Repository;
import com.castorama.commerce.pricing.CastVATManager;

public class ParseTicketObjectFactory {
  
  public static ParseTicketable create(final int pMappingType,
                                        String name,
                                        ImportTicketManager manager, 
                                        BufferedWriter writer, 
                                        Repository repository,
                                        Repository profileRepository,
                                        TransactionManager transactionManager,
                                        CastVATManager castVATManager) {
    ParseTicketable result = null;
    switch (pMappingType) {
    case ParseTicketable.MAPPING:
      result = new ParseMappingObject(name, manager, writer, repository, profileRepository, transactionManager);
      break;
    case ParseTicketable.HEADER:
      result = new ParseHeaderObject(name, manager, writer, repository, profileRepository, transactionManager, castVATManager);
      break;
    }
    return result;
  }
}
