package com.castorama.stockvisualization;


import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * 
 * @author Aliaksandr Belakurski
 */
public class MagasinSearchFormHandler
extends GenericFormHandler
{
  /** PATTERN_POSTAL_CODE constant. */
  private static final Pattern PATTERN_POSTAL_CODE = Pattern.compile("[0-9]+");
    
  private String searchQuestion;

  public String getSearchQuestion() {
    return searchQuestion;
  }

  public void setSearchQuestion(String searchQuestion) {
    this.searchQuestion = searchQuestion;
  }

  private String handleSearchSuccessURL;

  public String getHandleSearchSuccessURL() {
    return handleSearchSuccessURL;
  }

  public void setHandleSearchSuccessURL(String handleSearchSuccessURL) {
    this.handleSearchSuccessURL = handleSearchSuccessURL;
  }

  private String handleSearchErrorURL;
  
  public String getHandleSearchErrorURL() {
    return handleSearchErrorURL;
  }

  public void setHandleSearchErrorURL(String handleSearchErrorURL) {
    this.handleSearchErrorURL = handleSearchErrorURL;
  }
  private StockVisualizationManager stockVisualizationManager;

  public StockVisualizationManager getStockVisualizationManager() {
    return stockVisualizationManager;
  }

  public void setStockVisualizationManager(StockVisualizationManager stockVisualizationManager) {
    this.stockVisualizationManager = stockVisualizationManager;
  }

  private String addFromSVContextParam;

  public String getAddFromSVContextParam() {
    return addFromSVContextParam;
  }

  public void setAddFromSVContextParam(String addFromSVContextParam) {
    this.addFromSVContextParam = addFromSVContextParam;
  }

  public boolean handleSearchMagasin (DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) 
  		  throws ServletException, IOException {
	String magasinId = null;

	try {
	  magasinId = doSearch();
	} catch (StockVisualizationException svExc) {
	  if (isLoggingError()) 
		logError(svExc);
	}

	if (!StringUtils.isEmpty(magasinId)) {
	  if (!StringUtils.isEmpty(getHandleSearchSuccessURL())) {
		StringBuilder successURL = new StringBuilder();
		successURL.append(getHandleSearchSuccessURL()).append("?magasinId=").
				  append(magasinId).append("&departamentId=&regionId=");

		if ("true".equalsIgnoreCase(getAddFromSVContextParam())) {
			successURL.append("&fromSVContext=true");
		}

		pRes.sendLocalRedirect(successURL.toString(), pReq);
		return false;
	  } else 
		return true;
	} else {
	  addFormException(new DropletException(null));

	  if (!StringUtils.isEmpty(getHandleSearchErrorURL())) {
		StringBuilder errorURL = new StringBuilder();
		String handleErrorURL = getHandleSearchErrorURL();
		
		int pos = handleErrorURL.indexOf("_requestid=");
		if (pos != -1) {
		    handleErrorURL = handleErrorURL.substring(0, pos - 1);
		}
		
		errorURL.append(handleErrorURL);
		
		if ("true".equalsIgnoreCase(getAddFromSVContextParam())) {
		  errorURL.append("?fromSVContext=true");
		}
		
		pRes.sendLocalRedirect(errorURL.toString(), pReq);
		return false;
	  } else 
		return false;
	}
  }

  private String doSearch () 
  		  throws StockVisualizationException {
	StockVisualizationManager svManager = getStockVisualizationManager();

	String question = getSearchQuestion();
	
	String postalCode = null;
	Matcher m = PATTERN_POSTAL_CODE.matcher(question);
	while (m.find()){
	    postalCode = m.group();
	}
	
	RepositoryItem claimedMagasinItem = svManager.claimMagasinByPostalCode(postalCode);

	return claimedMagasinItem == null? null: (String) claimedMagasinItem.getRepositoryId();
  }
}
