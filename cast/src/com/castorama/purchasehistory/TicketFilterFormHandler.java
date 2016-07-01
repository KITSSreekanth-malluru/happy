/*
 * Copyright (c) 2010 EPAM Systems.
 * All rights reserved.
 */
package com.castorama.purchasehistory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import org.joda.time.DateTime;

import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;


/**
 * Form handler for "Mon Memo Web" page actions.
 *
 * @author Aliaksandr Surma
  */
public class TicketFilterFormHandler extends GenericFormHandler {
  //--------------------------------
  //Constants
  private static final String DATE_FORMAT_PARSE = "dd/MM/yy";
  private static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";

  private static final String DATE_FORMAT_IN_ERROR_MSG = "dd MMMM yyyy";

  /**
   * Resource bundle.
   */
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /*
   * Message resource constants
   */
  protected static final String MSG_START_DATE_EALIER = "startDateNotEalier";
  protected static final String MSG_INCORRECT_START_DATE = "incorrectStartDate";
  protected static final String MSG_INCORRECT_END_DATE = "incorrectEndDate";
  protected static final String MSG_EMPTY_SEARCH_RESULTS = "emptySearchResults";
  protected static final String MSG_EMPTY_SEARCH_RESULTS_BY_CRITERIA = "emptySearchResultsByCriteria";
  protected static final String MSG_NO_CASTORAMA_CARD = "noCastoramaCard";
  protected static final String MSG_INFORMATION_TEXT_TYPE_1 = "filterResultsMessageType1";
  protected static final String MSG_INFORMATION_TEXT_TYPE_2 = "filterResultsMessageType2";
  protected static final String MSG_INFORMATION_TEXT_TYPE_3 = "filterResultsMessageType3";
  protected static final String MSG_INFORMATION_TEXT_TYPE_4 = "filterResultsMessageType4";
  protected static final String MSG_INFORMATION_TEXT_TYPE_5 = "filterResultsMessageType5";
  protected static final String MSG_INFORMATION_TEXT_TYPE_6 = "filterResultsMessageType6";
  protected static final String MSG_INFORMATION_TEXT_TYPE_7 = "filterResultsMessageType7";
  protected static final String MSG_INFORMATION_TEXT_TYPE_8 = "filterResultsMessageType8";

  //--------------------------------
  // Properties

  //--------------------------------
  // property: productName
  private String mProductName;

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

  //--------------------------------
  // property: dateFrome
  private String mDateFrom;

  /**
   * Gets the dateFrom property.
   *
   * @return the dateFrom
   */
  public final String getDateFrom() {
    return mDateFrom;
  }

  /**
   * Sets the dateFrom property.
   *
   * @param pDateFrom the dateFrom to set
   */
  public final void setDateFrom(final String pDateFrom) {
    mDateFrom = pDateFrom;
  }

  //--------------------------------
  // property: dateTo
  private String mDateTo;

  /**
   * Gets the dateTo property.
   *
   * @return the dateTo
   */
  public final String getDateTo() {
    return mDateTo;
  }

  /**
   * Sets the dateTo property.
   *
   * @param pDateTo the dateTo to set
   */
  public final void setDateTo(final String pDateTo) {
    mDateTo = pDateTo;
  }

  //--------------------------------
  // property: storeId
  private String mStoreId;

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

  //--------------------------------
  // property: tickets
  private List<RepositoryItem> mTickets;

  /**
   * Gets the tickets property.
   *
   * @return the tickets
   */
  public final List<RepositoryItem> getTickets() {
    return mTickets;
  }

  /**
   * Sets the tickets property.
   *
   * @param pTickets the tickets to set
   */
  public final void setTickets(final List<RepositoryItem> pTickets) {
    mTickets = pTickets;
  }

  //--------------------------------
  // property: tickets
  private String mProfileId;

  /**
   * Gets the profileId property.
   *
   * @return the profileId
   */
  public final String getProfileId() {
    return mProfileId;
  }

  /**
   * Sets the profileId property.
   *
   * @param pProfileId the profileId to set
   */
  public final void setProfileId(final String pProfileId) {
    mProfileId = pProfileId;
  }

  //--------------------------------
  // property: profile
  private Profile mProfile;

  /**
   * Gets the profile property.
   *
   * @return the profile
   */
  public final Profile getProfile() {
    return mProfile;
  }

  /**
   * Sets the profile property.
   *
   * @param pProfile the profile to set
   */
  public final void setProfile(final Profile pProfile) {
    mProfile = pProfile;
  }

  //--------------------------------
  // property: mInformationText
  private String mInformationText;

  /**
   * Gets the informationText property.
   *
   * @return the informationText
   */
  public final String getInformationText() {
    return mInformationText;
  }

  /**
   * Sets the informationText property.
   *
   * @param pInformationText the informationText to set
   */
  public final void setInformationText(final String pInformationText) {
    mInformationText = pInformationText;
  }

  //--------------------------------
  // property: mFilterErrorURL
  private String mFilterErrorURL;

  /**
   * Gets the filterErrorURL property.
   *
   * @return the filterErrorURL
   */
  public final String getFilterErrorURL() {
    return mFilterErrorURL;
  }

  /**
   * Sets the filterErrorURL property.
   *
   * @param pFilterErrorURL the filterErrorURL to set
   */
  public final void setFilterErrorURL(final String pFilterErrorURL) {
    mFilterErrorURL = pFilterErrorURL;
  }

  //--------------------------------
  // property: mFilterSuccessURL
  private String mFilterSuccessURL;

  /**
   * Gets the filterSuccessURL property.
   *
   * @return the filterSuccessURL
   */
  public final String getFilterSuccessURL() {
    return mFilterSuccessURL;
  }

  /**
   * Sets the filterSuccessURL property.
   *
   * @param pFilterSuccessURL the filterSuccessURL to set
   */
  public final void setFilterSuccessURL(final String pFilterSuccessURL) {
    mFilterSuccessURL = pFilterSuccessURL;
  }

  //--------------------------------
  // property: productName
  private Boolean mValiderAction = Boolean.FALSE;
  
  /**
   * Gets the validerAction property. 
   *
   * @return the validerAction
   */
  public final Boolean getValiderAction() {
    return mValiderAction;
  }

  /**
   * Sets the validerAction property.
   *
   * @param pValiderAction the validerAction to set
   */
  public final void setValiderAction(Boolean pValiderAction) {
    mValiderAction = pValiderAction;
  }
  

  //--------------------------------
  // property: ticketRetrievalService
  private TicketRetrievalService mTicketRetrievalService;

  /**
   * Gets the ticketRetrievalService property.
   *
   * @return the ticketRetrievalService
   */
  public final TicketRetrievalService getTicketRetrievalService() {
    return mTicketRetrievalService;
  }

  /**
   * Sets the ticketRetrievalService property.
   *
   * @param pTicketRetrievalService the ticketRetrievalService to set
   */
  public final void setTicketRetrievalService(final TicketRetrievalService pTicketRetrievalService) {
    mTicketRetrievalService = pTicketRetrievalService;
  }

  //--------------------------------
  // property: orderHistoryConfiguration
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
   * Sets the PurchaseHistoryConfiguration property.
   *
   * @param pPurchaseHistoryConfiguration the PurchaseHistoryConfiguration to set
   */
  public final void setPurchaseHistoryConfiguration(final PurchaseHistoryConfiguration pPurchaseHistoryConfiguration) {
    mPurchaseHistoryConfiguration = pPurchaseHistoryConfiguration;
  }

  /**
   * Gets list of available stores.
   *
   * @return list of available stores
   */
  public List<RepositoryItem> getAvailableStores() {
    List<RepositoryItem> stores = new ArrayList<RepositoryItem>();
    try {
      stores = getTicketRetrievalService().getStoresForProfile(getProfile());
    } catch (TicketRetrievalException e) {
      if (isLoggingError()){
        logError("Unable to get list of available stores");
      }
    }
    return stores;
  }

  /**
   * Generates empty results message.
   *
   * @return empty results message
   */
  public String getEmptyResultsMessage(final boolean pActiveSearch) {
    String message = null;
    if (pActiveSearch) {
      message = ResourceUtils.getMsgResource(MSG_EMPTY_SEARCH_RESULTS_BY_CRITERIA, null, getResourceBundle());
    } else {
      final Date historyStartDateAsDate = getPurchaseHistoryConfiguration().getHistoryStartDateAsDate();
      
      final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_IN_ERROR_MSG, Locale.FRENCH);
      String startDateFormatted = dateFormat.format(historyStartDateAsDate);
      
      final String purchasePeriod = getPurchaseHistoryConfiguration().getPurchasesAvailabilityPeriod();
      final String accountAvailabilityPeriod = getPurchaseHistoryConfiguration().getAccountAvailabilityPeriod();
      final String cardAvailabilityPeriod = getPurchaseHistoryConfiguration().getCardAvailabilityPeriod();
      
      message = ResourceUtils.getMsgResource(MSG_EMPTY_SEARCH_RESULTS, null, getResourceBundle(),
          new String[]{ startDateFormatted, purchasePeriod, accountAvailabilityPeriod, cardAvailabilityPeriod });
    }
    
    

    return message;
  }

  /**
   * Generates message for 'No Castorama Card' page.
   *
   * @return 'No Castorama Card' message
   */
  public String getNoCastoramaCardMessage() {
    final String accountAvailabilityPeriod = getPurchaseHistoryConfiguration().getAccountAvailabilityPeriod();
    final String cardAvailabilityPeriod = getPurchaseHistoryConfiguration().getCardAvailabilityPeriod();
    
    final String message = ResourceUtils.getMsgResource(MSG_NO_CASTORAMA_CARD, null, getResourceBundle(),
        new String[]{ accountAvailabilityPeriod, cardAvailabilityPeriod });

    return message;
  }

  /**
   * Handles 'Filter' action.
   *
   * @param pRequest DynamoHttpServletRequest instance
   * @param pResponse DynamoHttpServletResponse instance
   *
   * @return true if no errors
   *
   * @throws ServletException if any error occurs
   * @throws IOException
   */
  public boolean handleFilter(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    preFilter(pRequest, pResponse);

    if(getFormError()) {
      if(isLoggingDebug()) {
        logDebug("Redirecting due to form error in preFilter.");
      }

      return checkFormRedirect(null, getFilterErrorURL(), pRequest, pResponse);
    }

    filter(pRequest, pResponse);

    if(getFormError()) {
      if(isLoggingDebug()) {
        logDebug("Redirecting due to form error in filter");
      }

      return checkFormRedirect(null, getFilterErrorURL(), pRequest, pResponse);
    }

    postFilter(pRequest, pResponse);

    //If NO form errors are found, redirect to the success URL.
    //If form errors are found, redirect to the error URL.
    return checkFormRedirect(getFilterSuccessURL(), getFilterErrorURL(), pRequest, pResponse);
  }

  /**
   * Validates input fields.
   *
   * @param pRequest DynamoHttpServletRequest instance
   * @param pResponse DynamoHttpServletResponse instance
   */
  private void preFilter(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
    
    final Boolean validerAction = getValiderAction();

    if (Boolean.FALSE.equals(validerAction)) {
      setStoreId(Constants.ALL_STORES);
    }
    
    boolean validDateFrom = true;
    boolean validDateTo = true;

    if(!StringUtils.isEmpty(getDateFrom()) && !isValidDate(getDateFrom())) {
      final String errorMsg = ResourceUtils.getMsgResource(MSG_INCORRECT_START_DATE, null, getResourceBundle());
      addFormException(new DropletFormException(errorMsg, null));
      validDateFrom = false;
    }

    if(!StringUtils.isEmpty(getDateTo()) && !isValidDate(getDateTo())) {
      final String errorMsg = ResourceUtils.getMsgResource(MSG_INCORRECT_END_DATE, null, getResourceBundle());
      addFormException(new DropletFormException(errorMsg, null));
      validDateTo = false;
    }

    if(!StringUtils.isEmpty(getDateFrom()) && !StringUtils.isEmpty(getDateTo()) && validDateFrom && validDateTo) {
      final Date dateFrom = formatDate(getDateFrom());
      final Date dateTo = formatDate(getDateTo());

      if(dateFrom.after(dateTo)) {
        final String errorMsg = ResourceUtils.getMsgResource(MSG_START_DATE_EALIER, null, getResourceBundle());
        addFormException(new DropletFormException(errorMsg, null));
      }
    }
  }

  /**
   * Method generates information message about search results and filtering criteria.
   *
   * @param pRequest DynamoHttpServletRequest instance
   * @param pResponse DynamoHttpServletResponse instance
   */
  private void postFilter(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {
    String informationMessage = "";
    int size = getTickets().size();
    final Boolean validerAction = getValiderAction();
    
    if(size == 0) {
      // 0001963: [Purchase History] No ticket found after search criteria entered 
      // check if user entered search criteria and clicked button 'Valider' 
      if ((!StringUtils.isEmpty(getDateFrom()) || !StringUtils.isEmpty(getDateTo()) ||
          !Constants.ALL_STORES.equals(getStoreId()) || !StringUtils.isEmpty(getProductName())) && Boolean.TRUE.equals(validerAction)) {
        informationMessage = getEmptyResultsMessage(true);
      }else{
        informationMessage = getEmptyResultsMessage(false);
      }
    } else {
      informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_1, null, getResourceBundle());
      if(!StringUtils.isEmpty(getDateFrom()) && StringUtils.isEmpty(getDateTo()) &&
          Constants.ALL_STORES.equals(getStoreId())) {
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_2, null, getResourceBundle(),
            new String[]{ getDateFrom() });
      } else if(StringUtils.isEmpty(getDateFrom()) && !StringUtils.isEmpty(getDateTo()) &&
          Constants.ALL_STORES.equals(getStoreId())) {
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_3, null, getResourceBundle(),
            new String[]{ getDateTo() });
      } else if(!StringUtils.isEmpty(getDateFrom()) && !StringUtils.isEmpty(getDateTo()) &&
          Constants.ALL_STORES.equals(getStoreId())) {
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_4, null, getResourceBundle(),
            new Object[]{ getDateFrom(), getDateTo() });
      } else if(StringUtils.isEmpty(getDateFrom()) && StringUtils.isEmpty(getDateTo()) &&
          !Constants.ALL_STORES.equals(getStoreId())) {
        //retrieve store name from Magasin repository
        final String storeName = getTicketRetrievalService().getStoreNameByStoreId(getStoreId());
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_5, null, getResourceBundle(),
            new String[]{ storeName });
      } else if(!StringUtils.isEmpty(getDateFrom()) && !StringUtils.isEmpty(getDateTo()) &&
          !Constants.ALL_STORES.equals(getStoreId())) {
        //retrieve store name from Magasin repository
        final String storeName = getTicketRetrievalService().getStoreNameByStoreId(getStoreId());
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_6, null, getResourceBundle(),
            new String[]{ storeName, getDateFrom(), getDateTo() });
      } else if(!StringUtils.isEmpty(getDateFrom()) && StringUtils.isEmpty(getDateTo()) &&
          !Constants.ALL_STORES.equals(getStoreId())) {
        //retrieve store name from Magasin repository
        final String storeName = getTicketRetrievalService().getStoreNameByStoreId(getStoreId());
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_7, null, getResourceBundle(),
            new String[]{ storeName, getDateFrom() });
      } else if(StringUtils.isEmpty(getDateFrom()) && !StringUtils.isEmpty(getDateTo()) &&
          !Constants.ALL_STORES.equals(getStoreId())) {
        //retrieve store name from Magasin repository
        final String storeName = getTicketRetrievalService().getStoreNameByStoreId(getStoreId());
        informationMessage = ResourceUtils.getMsgResource(MSG_INFORMATION_TEXT_TYPE_8, null, getResourceBundle(),
            new String[]{ storeName, getDateTo() });
      }
      
      // 0001971: [Purchase History] Fill start date and end date when click on Mon Memo Web 
      if (Boolean.FALSE.equals(validerAction)) {
        
        //we have already checked the size - can safely call get(..) method
        final Date endDate = (Date)getTickets().get(0).getPropertyValue(Constants.TICKET_HEADER_TICKET_DATE_PROP);
        final Date startDate = (Date)getTickets().get(size - 1).getPropertyValue(Constants.TICKET_HEADER_TICKET_DATE_PROP);
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DISPLAY);
        
        if (startDate != null) {
          setDateFrom(dateFormat.format(startDate));
        }
        if (endDate != null) {
          setDateTo(dateFormat.format(endDate));
        }
      }
    }

    setInformationText(informationMessage);
  }

  /**
   * 'Filter' method
   *
   * @param pRequest DynamoHttpServletRequest instance
   * @param pRespons DynamoHttpServletResponse instance
   */
  private void filter(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pRespons) {
    
    TicketRequestData ticketRequestData = generateTicketRequestData();

    List<RepositoryItem> findTickets = new ArrayList<RepositoryItem>();;
    try {
      findTickets = getTicketRetrievalService().findTickets(ticketRequestData, getProfile());
    } catch (TicketRetrievalException e) {
      String informationMessage = getEmptyResultsMessage(false);
      setInformationText(informationMessage);
    }
    setTickets(findTickets);
  }

  /**
   * Generates TicketRequestData based on search criteria.
   *
   * @return TicketRequestData object
   */
  private TicketRequestData generateTicketRequestData() {
    TicketRequestData ticketRequestData = new TicketRequestData();
    ticketRequestData.setProductName(getProductName());

    //Check start date
    Date fromDate = formatDate(getDateFrom());
    Date historyStartDateAsDate = getPurchaseHistoryConfiguration().getHistoryStartDateAsDate();

    if((fromDate == null) || fromDate.before(historyStartDateAsDate)) {
      fromDate = historyStartDateAsDate;
    }

    ticketRequestData.setStartDate(fromDate);
    
    Date endDate = formatDate(getDateTo());
    if (endDate != null) {
      //Shift date 1 day forward to include ticket's time
      DateTime endDateTime = new DateTime(endDate.getTime());
      endDate = endDateTime.plusDays(1).toDate();
    }
    
    ticketRequestData.setEndDate(endDate);
    ticketRequestData.setStoreId(getStoreId());

    return ticketRequestData;
  }

  /**
   * Checks if a String is a valid date by parsing the String with an instance
   * of the SimpleDateFormat class and returns true or false.
   *
   * @param pDate Date to check
   *
   * @return <code>true</code> if date is valid.
   */
  private boolean isValidDate(final String pDate) {
    final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PARSE);

    try {
      dateFormat.setLenient(false);
      dateFormat.parse(pDate);
    } catch(ParseException e) {
      // nothing to log
      return false;
    }

    return true;
  }

  /**
   * Formats date. 
   *
   * @param pDate date as string
   *
   * @return Date object
   */
  private Date formatDate(final String pDate) {
    if(StringUtils.isEmpty(pDate)) {
      return null;
    }

    final Date date;
    final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PARSE);

    try {
      dateFormat.setLenient(false);
      date = dateFormat.parse(pDate);
    } catch(ParseException e) {
      // nothing to log
      return null;
    }

    return date;
  }

  /**
   * Returns the ResourceBundle.
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }
}
