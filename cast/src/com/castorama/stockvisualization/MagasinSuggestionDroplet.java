package com.castorama.stockvisualization;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * 
 * @author Aliaksandr Belakurski
 */
public class MagasinSuggestionDroplet 
extends DynamoServlet
{
  static final String HANDLE_SEARCH_MAGASIN_PARAM_NAME = "searchMagasinRequest";
  static final String SEARCH_QUESTION_PARAM_NAME = "question";
  static final String HINTS_OUTPUT_PARAM_NAME = "hints";
  static final String STORE_ID_PARAM_NAME = "storeId";

  static final String OUTPUT_OPARAM_NAME = "output";
  static final String EMPTY_OPARAM_NAME = "empty";
  static final String ERROR_OPARAM_NAME = "error";

  static final String MAGASIN_NAME_PROPERTY_NAME = "nom";
  static final String MAGASIN_SITY_NAME_PROPERTY_NAME = "entite.adresse.ville";
  static final String MAGASIN_POSTAL_CODE_PROPERTY_NAME = "entite.adresse.cp";

  static final String MAGASIN_ID_PROPERTY_NAME = "storeId";

  static final String HINTS_DELIMITER = ".";
  static final String HINTS_SPLITTER = ";";

  private StockVisualizationManager stockVisualizationManager;

  public StockVisualizationManager getStockVisualizationManager() {
    return stockVisualizationManager;
  }

  public void setStockVisualizationManager(StockVisualizationManager stockVisualizationManager) {
    this.stockVisualizationManager = stockVisualizationManager;
  }

  @Override
  public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) 
  		  throws ServletException, IOException {
	StockVisualizationManager svManager = getStockVisualizationManager();

	List<RepositoryItem> magasinItems = null;
	try {
	  magasinItems = svManager.claimAllMagasins();
	} catch (StockVisualizationException svExc) {
	  if (isLoggingError())
		logError("unable to query magasin repository due to the following reason: " +
			  svExc.getMessage());
	}

	String hints = null;
	if ((magasinItems != null) && !magasinItems.isEmpty()) {
	  try {
		hints = fillHintsString (magasinItems);
	  } catch (StockVisualizationException svExc) {
		if (isLoggingError()) 
		  logError("unable to compile hints string due to the following reason: " + 
			  svExc);
	  }
	}

	req.setParameter(HINTS_OUTPUT_PARAM_NAME, hints);
	req.serviceLocalParameter(OUTPUT_OPARAM_NAME, req, res);
  }

  private String fillHintsString (List<RepositoryItem> magasinItems) 
  		  throws StockVisualizationException {
	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < magasinItems.size(); i++) {
	  RepositoryItem currMagasinItem = magasinItems.get(i);

	  try {
		String magasinName = (String) 
			  DynamicBeans.getSubPropertyValue(currMagasinItem, MAGASIN_NAME_PROPERTY_NAME);
		String postalCode = (String)
			  DynamicBeans.getSubPropertyValue(currMagasinItem, MAGASIN_POSTAL_CODE_PROPERTY_NAME);

		//sb.append(StringUtils.toUpperCase(magasinName)).append(HINTS_DELIMITER).append(postalCode).append(HINTS_SPLITTER);
		sb.append(magasinName).append(HINTS_DELIMITER).append(postalCode).append(HINTS_SPLITTER);
		//sb.append(postalCode).append(HINTS_DELIMITER).append(StringUtils.toUpperCase(magasinName));
		sb.append(postalCode).append(HINTS_DELIMITER).append(magasinName);
		
		if (i != magasinItems.size() - 1) 
		  sb.append(HINTS_SPLITTER);
	  } catch (PropertyNotFoundException pnfExc) {
		throw new StockVisualizationException(pnfExc);
	  }
	}

	return sb.toString();
  }
}
