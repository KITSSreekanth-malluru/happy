/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */

package com.castorama.droplet;

import atg.commerce.catalog.CatalogTools;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;


/**
 *
 *
 * @author EPAM team
 */
public class SKUFindDroplet extends DynamoServlet {
  /** OUTPUT constant. */
  public static final String OUTPUT = "output";

  /** EMPTY constant. */
  public static final String EMPTY = "empty";

  /** CODE constant. */
  public static final String CODE = "code";

  /** Creation date constant. */
  public static final String CREATION_DATE = "creationDate";

  /** Creation date constant. */
  public static final String WITH_NULL_CREATION_DATE = "withNullCreationDate";

  /** SKU constant. */
  public static final String SKU = "sku";

  /** catalogTools property. */
  private CatalogTools mCatalogTools;

  /**
   *
   * @param  pRequest  parameter
   * @param  pResponse parameter
   *
   * @throws ServletException exception
   * @throws IOException      exception
   */
  public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    boolean empty = true;
    String code = pRequest.getParameter(CODE);
    Date creationDate = (Date)pRequest.getObjectParameter(CREATION_DATE);

    if((null != code) && (0 < code.length())) {
      try {
        // Part of Purchase History functionality
        // if creation date parameter is specified - verify the creation date of 
        // the product in catalog is not after creationDate param
        // 
        RqlStatement statement = RqlStatement.parseRqlStatement("CodeArticle" + " = ?0");
        
        if (creationDate != null) {
          statement = RqlStatement.parseRqlStatement("CodeArticle = ?0 AND NOT displayName IS NULL AND (dateCreationCodeArticle IS NULL OR dateCreationCodeArticle <= ?1)" );
        }
        
        RepositoryView view = getCatalogTools().getCatalog().getView(getCatalogTools().getBaseSKUItemType());
        RepositoryItem[] items = statement.executeQuery(view, new Object[]{ code, creationDate });

        if((null != items) && (1 == items.length)) {
          pRequest.setParameter(SKU, items[0]);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
          empty = false;
        }
      } catch(Exception e) {
        if(isLoggingError()) {
          logError(e);
        }
      }
    }

    if(empty) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
  }

  /**
   * Returns catalogTools property.
   *
   * @return catalogTools property.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the value of the catalogTools property.
   *
   * @param pCatalogTools parameter to set.
   */
  public void setCatalogTools(final CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
}
