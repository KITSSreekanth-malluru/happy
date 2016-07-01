/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory;

import atg.commerce.catalog.CatalogTools;

import atg.core.util.StringUtils;

import atg.nucleus.GenericService;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.userprofiling.Profile;

import com.castorama.constantes.CastoConstantes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * Class which holds basic "Purchase History" methods.
 *
 * @author Aliaksandr Surma
  */
public class TicketRetrievalService extends GenericService {
  //--------------------------------
  //Constants

  //--------------------------------
  // Properties

  //--------------------------------
  // property: ticketsRepository
  private Repository mTicketsRepository;

  /**
   * Gets the ticketsRepository property.
   *
   * @return the ticketsRepository
   */
  public final Repository getTicketsRepository() {
    return mTicketsRepository;
  }

  /**
   * Sets the ticketsRepository property.
   *
   * @param pTicketsRepository the ticketsRepository to set
   */
  public final void setTicketsRepository(final Repository pTicketsRepository) {
    mTicketsRepository = pTicketsRepository;
  }

  //--------------------------------
  // property: storeRepository
  private Repository mStoreRepository;

  /**
  * Gets the storeRepository property.
  *
  * @return the storeRepository
  */
  public final Repository getStoreRepository() {
    return mStoreRepository;
  }

  /**
   * Sets the storeRepository property.
   *
   * @param pStoreRepository the storeRepository to set
   */
  public final void setStoreRepository(final Repository pStoreRepository) {
    mStoreRepository = pStoreRepository;
  }

  //--------------------------------
  // property: catalogTools
  private CatalogTools mCatalogTools;

  /**
   * Gets the catalogTools property.
   *
   * @return the catalogTools
   */
  public final CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the catalogTools property.
   *
   * @param pCatalogTools the catalogTools to set
   */
  public final void setCatalogTools(final CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  //--------------------------------
  // property: purchaseHistoryConfiguration
  private PurchaseHistoryConfiguration mPurchaseHistoryConfiguration;

  /**
   * Gets the purchaseHistoryConfiguration property.
   *
   * @return the purchaseHistoryConfiguration
   */
  public final PurchaseHistoryConfiguration getPurchaseHistoryConfiguration() {
    return mPurchaseHistoryConfiguration;
  }

  /**
   * Sets the purchaseHistoryConfiguration property.
   *
   * @param pPurchaseHistoryConfiguration the purchaseHistoryConfiguration to set
   */
  public final void setPurchaseHistoryConfiguration(final PurchaseHistoryConfiguration pPurchaseHistoryConfiguration) {
    mPurchaseHistoryConfiguration = pPurchaseHistoryConfiguration;
  }

  /**
   * Method to retrieve tickets form TicketRepository based on search criteria.
   *
   * @param pTicketRequestData search criteria
   *
   * @return tickets
   * @throws TicketRetrievalException id error occurs
   */
  public List<RepositoryItem> findTickets(final TicketRequestData pTicketRequestData, final Profile pProfile)
    throws TicketRetrievalException {
    final List<RepositoryItem> result = new ArrayList<RepositoryItem>();

    final Repository ticketsRepository = getTicketsRepository();

    try {
      final String homeId = getHomeId(pProfile);

      if(StringUtils.isEmpty(homeId)) {
        final String message = "Profile homeId property is empty";

        if(isLoggingWarning()) {
          logWarning(message);
        }
      }

      final String rql = generateTicketsRQLQuery(pTicketRequestData, homeId);

      final RqlStatement statement = RqlStatement.parseRqlStatement(rql);

      final RepositoryView view = ticketsRepository.getView(Constants.TICKET_HEADER_DESC);
      RepositoryItem[] items = statement.executeQuery(view,
          new Object[]{
            homeId, pTicketRequestData.getStartDate(), pTicketRequestData.getEndDate(), pTicketRequestData.getStoreId()
          });

      if((items != null) && !StringUtils.isEmpty(pTicketRequestData.getProductName())) {
        // The method is a bit time-consuming, but we can't use RQL to search in 2 repositories.
        // This change is in scope of mantis
        // 0001955: [Purchase History] Product search criteria should applied on catalog product label
        final String searchCriteria = pTicketRequestData.getProductName().trim().toUpperCase();
        items = applyFilterByProduct(items, searchCriteria);
      }

      if(items != null) {
        result.addAll(Arrays.asList(items));
      }
    } catch(RepositoryException e) {
      if(isLoggingError()) {
        logError(e);
      }

      throw new TicketRetrievalException(e.getMessage());
    }

    return result;
  }

  /**
   * Performs search by product label.
   *
   * @param pItems Ticket repository items
   * @param pProductName Product name to search
   *
   * @return List of tickets whose line items match search criteria
   */
  private RepositoryItem[] applyFilterByProduct(final RepositoryItem[] pItems, final String pSearchCriteria) {
    RepositoryItem[] result = null;

    final List<RepositoryItem> tickets = new ArrayList<RepositoryItem>();

    for(int i = 0; i < pItems.length; i++) {
      final RepositoryItem ticket = pItems[i];

      final Set lines = (Set) ticket.getPropertyValue(Constants.TICKET_HEADER_LINES_PROP);
      final Date ticketDate = (Date) ticket.getPropertyValue(Constants.TICKET_HEADER_TICKET_DATE_PROP);

      if((lines != null) && !(lines.isEmpty())) {
        final Iterator it = lines.iterator();
        while (it.hasNext()) {
          final RepositoryItem ticketLine = (RepositoryItem)it.next();
          final String productLabel = retrieveProductLabel(ticketLine, ticketDate);

          if(!StringUtils.isEmpty(productLabel) &&
              productLabel.toUpperCase().contains(pSearchCriteria)) {
            tickets.add(ticket);

            break;
          }

          // Mantis 0001964: [Purchase History] Looking for a product id in search ticket 
          final String productId = (String) ticketLine.getPropertyValue(Constants.TICKET_LINE_PRODUCT_ID_PROP);
          
          if(!StringUtils.isEmpty(productId) &&
              productId.toUpperCase().contains(pSearchCriteria)) {
            tickets.add(ticket);

            break;
          }

        
        }
      }
    }

    if(tickets.size() > 0) {
      final RepositoryItem[] array = new RepositoryItem[tickets.size()];
      result = (RepositoryItem[]) tickets.toArray(array);
    }

    return result;
  }

  /**
   * Generates RQL query to retrieve tickets.
   *
   * @param pTicketRequestData Search criteria
   * @param pHomeId Profile homeId property
   *
   * @return RQL query
   */
  private String generateTicketsRQLQuery(final TicketRequestData pTicketRequestData, final String pHomeId) {
    final StringBuilder sb = new StringBuilder();

    // RQL query:
    // homeId = ?0 AND ticketDate >= ?1 AND ticketDate <= ?2 AND storeId = ?3
    // ORDER BY ticketDate SORT DESC
    //
    sb.append(Constants.TICKET_HEADER_HOME_ID_PROP).append(" = ?0");

    if(pTicketRequestData.getStartDate() != null) {
      sb.append(" AND ").append(Constants.TICKET_HEADER_TICKET_DATE_PROP).append(" >= ?1");
    }

    if(pTicketRequestData.getEndDate() != null) {
      sb.append(" AND ").append(Constants.TICKET_HEADER_TICKET_DATE_PROP).append(" <= ?2");
    }

    if((pTicketRequestData.getStoreId() != null) && !Constants.ALL_STORES.equals(pTicketRequestData.getStoreId())) {
      sb.append(" AND ").append(Constants.TICKET_HEADER_STORE_ID_PROP).append(" = ?3");
    }

    // Commented according Mantis 0001955: [Purchase History] Product search criteria should applied on catalog product label
    // The logic was moved to applyFilterByProductName(..) method
    //
    //    if(!StringUtils.isEmpty(pTicketRequestData.getProductName())) {
    //      sb.append(" AND lines INCLUDES ITEM (").append(Constants.TICKET_LINE_PRODUCT_LABEL_PROP).append(" CONTAINS IGNORECASE ?4)");
    //    }

    //sort by date
    sb.append(" ORDER BY ").append(Constants.TICKET_HEADER_TICKET_DATE_PROP).append(" SORT DESC");

    return sb.toString();
  }

  /**
   * Generates RQL query to retrieve SKU from catalog.
   *
   * @param pTicketRequestData Search criteria
   * @param pHomeId Profile homeId property
   *
   * @return RQL query
   */
  private String generateSKURQLQuery() {
    final StringBuilder sb = new StringBuilder();

    // RQL query:
    // CodeArticle = ?0 AND (dateCreationCodeArticle IS NULL OR dateCreationCodeArticle <= ?1
    sb.append(Constants.SKU_CODE_PROP).append(" = ?0");

    // Mantis 0001922: [Purchase History] If product creation date is null, display link for product page 
    sb.append(" AND (").append(Constants.SKU_CREATION_DATE).append(" IS NULL");
    sb.append(" OR ").append(Constants.SKU_CREATION_DATE).append(" <= ?1)");

    return sb.toString();
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pProfileId ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   *
   * @throws RepositoryException ToDo: DOCUMENT ME!
   */
  private String getHomeId(final Profile pProfile) throws RepositoryException {
    String result = null;

    final Boolean isActive = (Boolean) pProfile.getPropertyValue(com.castorama.commerce.profile.Constants.IS_ACTIVE);

    if((isActive != null) && isActive) {
      result = (String) pProfile.getPropertyValue(com.castorama.commerce.profile.Constants.HOME_ID);
    }

    return result;
  }

  /**
   * Gets all stores where user's tickets exist.
   *
   * @param pProfile Profile instance
   *
   * @return Available stores
   * @throws TicketRetrievalException if error occurs
   */
  public List<RepositoryItem> getStoresForProfile(final Profile pProfile)
    throws TicketRetrievalException {
    final List<RepositoryItem> result = new ArrayList<RepositoryItem>();

    try {
      final String homeId = getHomeId(pProfile);

      if(StringUtils.isEmpty(homeId)) {
        final String message = "Profile homeId property is empty";

        if(isLoggingWarning()) {
          logWarning(message);
        }
      }

      final List<RepositoryItem> allUserTickets = getTicketsByHomeId(homeId);

      if(allUserTickets.size() > 0) {
        result.addAll(getStoresByTickets(allUserTickets));
      }
    } catch(RepositoryException e) {
      if(isLoggingError()) {
        logError(e);
      }

      throw new TicketRetrievalException(e.getMessage());
    }

    return result;
  }

  /**
   * Gets all stores from tickets.
   *
   * @param pAllUserTickets list of tickets
   *
   * @return list of stores
   *
   * @throws RepositoryException if error occurs
   */
  private List<RepositoryItem> getStoresByTickets(final List<RepositoryItem> pAllUserTickets)
    throws RepositoryException {
    final List<RepositoryItem> result = new ArrayList<RepositoryItem>();
    final Repository storeRepository = getStoreRepository();

    final StringBuilder sb = new StringBuilder();

    for(int i = 0; i < pAllUserTickets.size(); i++) {
      sb.append(Constants.TICKET_HEADER_STORE_ID_PROP).append(" EQUALS \"")
        .append(pAllUserTickets.get(i).getPropertyValue(Constants.TICKET_HEADER_STORE_ID_PROP)).append("\"");

      if(i < (pAllUserTickets.size() - 1)) {
        sb.append(" OR ");
      }
    }

    final String storeIdsQuery = sb.toString();
    final RqlStatement storeStatement = RqlStatement.parseRqlStatement(storeIdsQuery);
    final RepositoryView magazinView = storeRepository.getView(CastoConstantes.DESCRIPTEUR_MAGASIN);

    final RepositoryItem[] stores = storeStatement.executeQuery(magazinView, new Object[]{  });

    if((stores != null) && (stores.length > 0)) {
      result.addAll(Arrays.asList(stores));
    }

    return result;
  }

  /**
   * Gets tickets by Profile homeId
   *
   * @param pHomeId Profile homeId property
   *
   * @return list of tickets
   *
   * @throws RepositoryException if error occurs
   */
  private List<RepositoryItem> getTicketsByHomeId(final String pHomeId)
    throws RepositoryException {
    final List<RepositoryItem> result = new ArrayList<RepositoryItem>();

    final Repository ticketsRepository = getTicketsRepository();
    final StringBuilder sb = new StringBuilder();
    sb.append(Constants.TICKET_HEADER_HOME_ID_PROP).append(" = ?0");
    sb.append(" AND ").append(Constants.TICKET_HEADER_TICKET_DATE_PROP).append(" >= ?1");
    final RqlStatement statement = RqlStatement.parseRqlStatement(sb.toString());

    final RepositoryView view = ticketsRepository.getView(Constants.TICKET_HEADER_DESC);
    final RepositoryItem[] items = statement.executeQuery(view, new Object[]{ pHomeId, getPurchaseHistoryConfiguration().getHistoryStartDateAsDate()});

    if((items != null) && (items.length > 0)) {
      result.addAll(Arrays.asList(items));
    }

    return result;
  }

  /**
   * Gets store name by provided store ID.
   *
   * @param pStoreId Store Id property
   *
   * @return Store name
   * @throws TicketRetrievalException
   */
  public String getStoreNameByStoreId(final String pStoreId) {
    String result = null;

    final RepositoryItem store = getStoreFromStoreRepository(pStoreId);

    if(store != null) {
      result = (String) store.getPropertyValue(CastoConstantes.NOM);
    }

    return result;
  }

  /**
   * Gets store form magazin repository.
   *
   * @param pStoreId Store ID property
   *
   * @return Store Repository Item
   * @throws TicketRetrievalException
   */
  public RepositoryItem getStoreFromStoreRepository(final String pStoreId) {
    RepositoryItem result = null;

    if(StringUtils.isEmpty(pStoreId)) {
      if(isLoggingWarning()) {
        logWarning("Store Id parameter is empty");
      }
    }

    final Repository storeRepository = getStoreRepository();

    final StringBuilder sb = new StringBuilder();
    sb.append(Constants.TICKET_HEADER_STORE_ID_PROP).append(" EQUALS \"").append(pStoreId).append("\"");

    final String rql = sb.toString();

    try {
      final RqlStatement storeStatement = RqlStatement.parseRqlStatement(rql);
      final RepositoryView magazinView = storeRepository.getView(CastoConstantes.DESCRIPTEUR_MAGASIN);

      final RepositoryItem[] stores = storeStatement.executeQuery(magazinView, new Object[]{  });

      if((stores != null) && (stores.length > 0)) {
        result = stores[0];
      }
    } catch(RepositoryException e) {
      if(isLoggingError()) {
        logError(e);
      }
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pTicketId ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   * @throws TicketRetrievalException
   */
  public RepositoryItem getTicketByRepositoryId(final String pTicketId)
    throws TicketRetrievalException {
    RepositoryItem result = null;
    final Repository ticketRepository = getTicketsRepository();

    if(pTicketId != null) {
      try {
        result = ticketRepository.getItem(pTicketId, Constants.TICKET_HEADER_DESC);
      } catch(RepositoryException e) {
        if(isLoggingError()) {
          logError(e);
        }

        throw new TicketRetrievalException(e.getMessage());
      }
    } else {
      if(isLoggingError()) {
        logError("Ticket id is null");
      }
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pTicket ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   * @throws TicketRetrievalException
   */
  public RepositoryItem getStoreByTicket(final RepositoryItem pTicket)
    throws TicketRetrievalException {
    RepositoryItem result = null;

    if(pTicket != null) {
      final String storeId = (String) pTicket.getPropertyValue(Constants.TICKET_HEADER_STORE_ID_PROP);

      result = getStoreFromStoreRepository(storeId);
    } else {
      if(isLoggingError()) {
        logError("Unable to find store. Ticket param is null");
      }
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pProductRef ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public RepositoryItem findSKUInCatalog(final String pProductRef, final Date pTicketDate) {
    RepositoryItem result = null;

    // To verify that the product in the receipt and product in the catalog are the same,
    // verify that creation date of the product in the catalog is not after the date&time when ticket was made.
    if(StringUtils.isEmpty(pProductRef)) {
      if(isLoggingWarning()) {
        logWarning("ProductRef param is empty");
      }

      return result;
    }

    if(pTicketDate == null) {
      if(isLoggingWarning()) {
        logWarning("Ticket Date param is empty");
      }

      return result;
    }

    try {
      final RqlStatement statement = RqlStatement.parseRqlStatement(generateSKURQLQuery());
      final RepositoryView view = getCatalogTools().getCatalog().getView(getCatalogTools().getBaseSKUItemType());
      final RepositoryItem[] items = statement.executeQuery(view, new Object[]{ pProductRef, pTicketDate });

      if((items != null) && (items.length > 0)) {
        result = items[0];
      }
    } catch(RepositoryException e) {
      if(isLoggingDebug()) {
        logError(e);
      }
    }

    return result;
  }

  /**
   * Method retrieves product label.
   *
   * @param pLine Ticket Line Item
   *
   * @return Product label
   */
  public String retrieveProductLabel(final RepositoryItem pLine, final Date pTicketDate) {
    String result = (String) pLine.getPropertyValue(Constants.TICKET_LINE_PRODUCT_LABEL_PROP);
    final String productRef = (String) pLine.getPropertyValue(Constants.TICKET_LINE_PRODUCT_ID_PROP);

    final RepositoryItem skuFromCatalog = findSKUInCatalog(productRef, pTicketDate);

    if(skuFromCatalog != null) {
      final String skuDisplayName = (String) skuFromCatalog.getPropertyValue(Constants.SKU_DISPLAY_NAME);
      
      //Mantis 0001956: [Purchase History] No label displayed on ticket / result list 
      if(!StringUtils.isEmpty(skuDisplayName)) {
        result = skuDisplayName;
      }
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pCode ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public String getMappedPaymentMethod(final String pCode) {
    String result = null;

    final Properties paymentMethodsCodeToLabelMap = getPurchaseHistoryConfiguration().getPaymentMethodsCodeToLabelMap();

    if((pCode != null) && (paymentMethodsCodeToLabelMap != null)) {
      result = paymentMethodsCodeToLabelMap.getProperty(pCode);
    }

    //Check if label is still null and assign default value
    if(result == null) {
      result = getPurchaseHistoryConfiguration().getDefaultPaymentMethodLabel();
    }

    return result;
  }

  /**
   * ToDo: DOCUMENT ME!
   *
   * @param pCode ToDo: DOCUMENT ME!
   *
   * @return ToDo: DOCUMENT ME!
   */
  public String getAdvancePaidByCustomerPaymentCode() {
    return getPurchaseHistoryConfiguration().getAdvancePaymentCode();
  }
}
