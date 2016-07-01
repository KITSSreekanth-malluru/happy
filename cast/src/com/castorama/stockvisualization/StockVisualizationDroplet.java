package com.castorama.stockvisualization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.commerce.profile.Constants;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.core.util.StringUtils;
import atg.droplet.IsEmpty;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;


/**
 * 
 * @author Aliaksandr_Belakurski
 */
public class StockVisualizationDroplet extends DynamoServlet {

  // constants
  private static final String PRODID_PARAM_NAME = "prodId";
  private static final String MAGASIN_ID_PARAM_NAME = "magasinId";
  private static final String QUANTITY_PARAM_NAME = "quantity";
  private static final String POSTAL_CODE_PARAM_NAME = "postalCode";
  private static final String QUERY_CONDITION_PARAM_NAME = "queryCondition";

  // query condition param possible values
  private static final String QUERY_BY_FAVORITE_STORE = "byFavoriteStore";
  private static final String QUERY_BY_DEFINED_STORE = "byDefinedStore";
  private static final String QUERY_BY_POSTAL_CODE = "byPostalCode";

  private static final String OUTPUT_OPARAM_NAME = "output";
  private static final String EMPTY_OPARAM_NAME = "empty";
  private static final String ERROR_OPARAM_NAME = "error";

  private static final String STOCK_MODELS_PARAM_NAME = "stockModels";

  public static final String DEFAULT_QUNTITY = "1";

  public static final String STORE_ID_PROPERTY_NAME = "storeId";
  public static final String STORE_POSTAL_CODE_NOT_FOUND_STATUS_CODE = "12";
  public static final String NOT_AVAILABLE_STATUS_CODE = "15";
  public static final String CASTORAMA_DIRECT_ID = "999";

  private StockVisualizationManager mStockVisualizationManager;

  public StockVisualizationManager getStockVisualizationManager() {
    return mStockVisualizationManager;
  }

  public void setStockVisualizationManager(StockVisualizationManager pStockVisualizationManager) {
    mStockVisualizationManager = pStockVisualizationManager;
  }

  private Profile mProfile;

  public Profile getProfile() {
    return mProfile;
  }

  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  private StockModelHolder stockModelHolder;

  public StockModelHolder getStockModelHolder() {
    return stockModelHolder;
  }

  public void setStockModelHolder(StockModelHolder stockModelHolder) {
    this.stockModelHolder = stockModelHolder;
  }

  /*
   * (non-Javadoc)
   * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   */
  public void service (DynamoHttpServletRequest pReq, 
	  DynamoHttpServletResponse pRes)  throws ServletException, IOException {
	StockVisualizationManager stockVisualizationManager = getStockVisualizationManager();

	String queryCondition = pReq.getParameter(QUERY_CONDITION_PARAM_NAME);
	String prodId = pReq.getParameter(PRODID_PARAM_NAME);
	String quantity = StringUtils.isEmpty(pReq.getParameter(QUANTITY_PARAM_NAME))? DEFAULT_QUNTITY: 
	  	pReq.getParameter(QUANTITY_PARAM_NAME);

	if (StringUtils.isEmpty(queryCondition)) {
	  if(isLoggingDebug()) 
		logDebug("queryCondition mandatory parameter was not specified");
	  return;
	}

	if (StringUtils.isEmpty(prodId)) {
	  if (isLoggingDebug()) 
		logDebug("prodId mandatory parameter was not specified");
	  return;
	}

	if (QUERY_BY_FAVORITE_STORE.equalsIgnoreCase(queryCondition)) {
	  processQueryByFavoriteStore (pReq, prodId, quantity);
	} else if (QUERY_BY_DEFINED_STORE.equalsIgnoreCase(queryCondition)) {
	  processQueryByDefinedStore(pReq, prodId, quantity);
	} else if (QUERY_BY_POSTAL_CODE.equalsIgnoreCase(queryCondition)) {
	  processQueryByPostalCode (pReq, prodId, quantity);
	}
  }

  private void processQueryByFavoriteStore (DynamoHttpServletRequest pReq, String pProdId, 
	  		  String pQuantity)  {
	StockVisualizationManager stockVisualizationManager = getStockVisualizationManager();

	if(isLoggingDebug())
	  logDebug("byFaviroteStore queryCondition was specified for prodId: " + 
		  pProdId + ", trying to detect user's favorite magasin");

	RepositoryItem favoriteStore = null;
	try {
	  favoriteStore = stockVisualizationManager.
	  		obtainUsersFavoriteStore(getProfile());
	} catch (StockVisualizationException svExc) {
	  if (isLoggingError()) 
		logError(svExc);
	}

	if(favoriteStore != null && !CASTORAMA_DIRECT_ID.equals((String)favoriteStore.getRepositoryId())) {
	  if (isLoggingDebug()) 
		logDebug("user: " + getProfile().getRepositoryId() + " has favorite magasin with id: " + 
			  favoriteStore.getRepositoryId());

	  List<StockModel> stockModels = queryForNearestStores(favoriteStore, pProdId, pQuantity);
	  if ((stockModels != null) && 
		  !(stockModels.isEmpty())) {
		stockModelHolder.clearStockModels();
		stockModelHolder.applyStockModels(stockModels);
		stockModelHolder.setFavoriteStoreStock(stockModels.get(0));
	  } else {
		stockModelHolder.clearStockModels();
		// per reqs: if user has favorite store but service is unavailable - 
		// display appropriate single row for favorite store
		StockModel favoriteStockModel = new StockModel(pProdId, (String) favoriteStore.getPropertyValue(STORE_ID_PROPERTY_NAME), 
			  NOT_AVAILABLE_STATUS_CODE, "0", favoriteStore);
		stockModelHolder.applyStockModels(new ArrayList<StockModel>(
			  Arrays.asList( new StockModel[] { favoriteStockModel } )));
		stockModelHolder.setFavoriteStoreStock(favoriteStockModel);
	  }

	  stockModelHolder.getStockModels().get(0).setQuantity(pQuantity);
	} else {
	  if (isLoggingDebug()) 
		logDebug("user: " + getProfile().getRepositoryId() + " hasn't favorite magasin");

	  stockModelHolder.clearStockModels();
	}
  }

  private void processQueryByDefinedStore (DynamoHttpServletRequest pReq, String pProdId, 
	  		  String pQuantity)  {
	StockVisualizationManager stockVisualizationManager = getStockVisualizationManager();

	if(isLoggingDebug())
	  logDebug("byDefinedStore queryCondition was specified for prodId: " + pProdId);

	String magasinId = pReq.getParameter(MAGASIN_ID_PARAM_NAME);
	if (StringUtils.isEmpty(magasinId)) {
	  if (isLoggingDebug()) 
		logDebug("mandatory parameter magasinId in byDefinedStore case was" +
			  " not specified");
	  stockModelHolder.clearStockModels();
	  return;
	}

	RepositoryItem magasinItem = null;
	try {
	  magasinItem = stockVisualizationManager.getMagasin(magasinId);
	} catch (StockVisualizationException svExc) {
	  if (isLoggingError()) 
		logError(svExc);
	}
	
	if(magasinItem != null) {
	  List<StockModel> stockModels = queryForNearestStores(magasinItem, pProdId, pQuantity);

	  if ((stockModels != null) && 
		  !(stockModels.isEmpty())) {
		stockModelHolder.clearStockModels();
		stockModelHolder.applyStockModels(stockModels);
		stockModelHolder.setDefinedStoreStock(stockModels.get(0));
	  } else {
		stockModelHolder.clearStockModels();
		// per reqs: if store was selected but stock service is unavailable - 
		// display appropriate single row for selected store
		StockModel definedStockModel = new StockModel(pProdId, (String) magasinItem.getPropertyValue(STORE_ID_PROPERTY_NAME), 
 			  NOT_AVAILABLE_STATUS_CODE, "0", magasinItem);
		stockModelHolder.applyStockModels(new ArrayList<StockModel>(
			  Arrays.asList( new StockModel[] { definedStockModel } )));
		stockModelHolder.setDefinedStoreStock(definedStockModel);
	  }

	  stockModelHolder.getStockModels().get(0).setQuantity(pQuantity);
	} else {
	  if (isLoggingDebug()) 
		logDebug("user: " + getProfile().getRepositoryId() + " hasn't favorite store");

	  stockModelHolder.clearStockModels();
	  return;
	}
  }

  private void processQueryByPostalCode (DynamoHttpServletRequest pReq, String pProdId, 
	  		  String pQuantity)   {
	StockVisualizationManager stockVisualizationManager = getStockVisualizationManager();

	if(isLoggingDebug())
	  logDebug("byPostalCode queryCondition was specified for prodId: " + pProdId);

	String postalCode = pReq.getParameter(POSTAL_CODE_PARAM_NAME);
	if (StringUtils.isEmpty(postalCode)) {
	  if (isLoggingDebug()) 
		logDebug("mandatory parameter postalCode in bypostalCode case was" +
			  " not specified");
	  stockModelHolder.clearStockModels();
	  return;
	}

	List<StockModel> stockModels = null;
	try {
	  stockModels = stockVisualizationManager.queryStockLevelByPostalCode (pProdId, postalCode.trim(), 
		    pQuantity);
	} catch (StockVisualizationException svExc) {
	  if (isLoggingError()) 
		logError(svExc);
	}

	if ((stockModels != null) && 
		!(stockModels.isEmpty())) {
	  if (stockModels.size() == 1 && stockModels.get(0).getStatus().equalsIgnoreCase(STORE_POSTAL_CODE_NOT_FOUND_STATUS_CODE)) {
		stockModelHolder.clearStockModels();
		return;
	  }
	  
	  stockModelHolder.clearStockModels();
	  stockModelHolder.applyStockModels(stockModels);
	  stockModelHolder.setNearestStoreStock(stockModels.get(0));
	  stockModelHolder.getStockModels().get(0).setQuantity(pQuantity);
	} else {
	  stockModelHolder.clearStockModels();
	}
  }

  /*
   * Queries service for stock levels of provided and four nearest magasins.
   * NOTE: first result of service response should correspond to original magasin.
   */
  private List<StockModel> queryForNearestStores (RepositoryItem pMagasin, String pProdId, 
	  		  String pQuantity) {
	StockVisualizationManager stockVisualizationManager = getStockVisualizationManager();
	String postalCode = null;

	  try {
		postalCode = (String) DynamicBeans.getSubPropertyValue(pMagasin, "entite.adresse.cp");
	  } catch (PropertyNotFoundException pnfExc) {
		return null;
	  }

	  if (isLoggingDebug()) 
		logDebug("trying to query service for nearest stores by postal code: " + postalCode);

	  List<StockModel> stockModels = null;
	  try {
		stockModels = stockVisualizationManager.queryStockLevelByPostalCode (pProdId, postalCode, 
			  pQuantity);
	  } catch (StockVisualizationException svExc) {
		if(isLoggingError()) 
		  logError(svExc);
	  }

	  if ((stockModels != null) && !(stockModels.isEmpty())) {
		if ((stockModels.size() == 1) && 
			(stockModels.get(0).getStatus().equals(STORE_POSTAL_CODE_NOT_FOUND_STATUS_CODE))) 
		return null;

		// compare original magasin id with the first result returned by service
		if(!pMagasin.getRepositoryId().equals(
			  stockModels.get(0).getMagasin().getRepositoryId())) {

		  for ( StockModel currStockModel: stockModels ) {
			if (currStockModel.getMagasin().getRepositoryId().equals(pMagasin.getRepositoryId())) {
			  List <StockModel> correctedList = new ArrayList<StockModel>();
			  correctedList.add(currStockModel);
			  stockModels.remove(currStockModel);
			  correctedList.addAll(stockModels);
			  return correctedList;
			}
		  }

		  return null;
		}
	  }

	  return stockModels;
  }
}
