/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.stockvisualization;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.userprofiling.Profile;
import atg.userprofiling.PropertyManager;
import com.castorama.constantes.CastoConstantes;
import com.castorama.utils.CastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

import static com.castorama.utils.CastUtils.buildSVSProductId;


/**
 * @author Aliaksandr_Belakurski
 */
public class StockVisualizationManager extends GenericService {

    // constans
    static final String MAGASIN_ITEM_DESCRIPTOR_NAME = "magasin";
    static final String BY_STORE_ID_RQL_STATEMENT = "storeId = ?0";
    static final String ALL_MAGASINS_RQL_STATEMENT = "entite.adresse.departement.numero != ?0";
    static final String BY_MAGASIN_NAME_RQL_STATEMENT = "nom = ?0";
    static final String BY_POSTAL_CODE_RQL_STATEMENT = "entite.adresse.cp = ?0";
    static final String BY_MAGASIN_NAME_AND_POSTAL_CODE_RQL_STATEMENT = "nom = ?0 AND entite.adresse.cp = ?1";
    static final String CASTORAMA_MAGASIN_POSTAL_CODE = "999";
    static final String ABONNEMENT_NEWSLETTER_ITEM_DESCRIPTOR_NAME = "abonnementNewsletter";
    static final String ABONNEMENT_MAGASIN_REF_PROPERTY_NAME = "id_magasin_ref";
    static final String PROFILE_SECURITY_STATUS_PROPERTY_NAME = "securityStatus";
    static final String PROFILE_LOGIN_PROPERTY_NAME = "login";
    private String mStoreURLTemplate;
    private String mCodePostalURLTemplate;
    private String mChannel;
    private String mMIMEType;
    private StockVisualizationSender mStockVisualizationSender;
    private DataProcessible mRequestDataProcessor;
    private Repository mStoreRepository;
    private PropertyManager propertyManager;
    private Repository newsletterRepository;
    private boolean stockVisualizationDisabled;
    private String stockVerifyUrlTemplate;

    public List<StockModel> queryStockLevelByStoreId(String pProdId, String pStoreId, String pQuantity, String pReduce)
            throws StockVisualizationException {
        if (isLoggingDebug()){
            logDebug("about to query stock level of product with id " + pProdId + " for store " + pStoreId);
        }

        if (StringUtils.isEmpty(pProdId)){
            throw new StockVisualizationException("prodId parameter is either empty or null");
        }
        if (StringUtils.isEmpty(pStoreId)){
            throw new StockVisualizationException("storeId parameter is either empty or null");
        }

        StockVisualizationURLBuilder urlBuilder = new ByStoreIdURLBuilder(pStoreId, buildSVSProductId(pProdId), getChannel(),
                pQuantity, pReduce, getStoreURLTemplate());

        return queryStockLevel(urlBuilder);
    }

    public List<StockModel> queryStockLevelByPostalCode(String pProdId, String pPostalCode, String pQuantity)
            throws StockVisualizationException {
        if (isLoggingDebug()) {
            logDebug("about to query stock level of product with id " + pProdId +
                    " for nearest stores to the following postal code " + pPostalCode);
        }

        if (StringUtils.isEmpty(pProdId)){
            throw new StockVisualizationException("prodId parameter is either null or empty");
        }
        if (StringUtils.isEmpty(pPostalCode)){
            throw new StockVisualizationException("postal code parameter is either null or empty");
        }

        StockVisualizationURLBuilder urlBuilder = new ByPostalCodeURLBuilder(pPostalCode, buildSVSProductId(pProdId), getChannel(),
                pQuantity, getCodePostalURLTemplate());

        return queryStockLevel(urlBuilder);
    }

    /**
     * Tries to obtain magasin {@link RepositoryItem} by id.
     *
     * @param pMagasinId
     * @return
     * @throws StockVisualizationException
     */
    public RepositoryItem getMagasin(String pMagasinId)
            throws StockVisualizationException {
        if (isLoggingDebug()){
            logDebug("about to obtain magasin by id" + pMagasinId);
        }

        if (pMagasinId == null){
            throw new StockVisualizationException("provided magasinId param is null");
        }

        Repository storeRepository = getStoreRepository();
        try {
            return storeRepository.getItem(pMagasinId, CastoConstantes.DESCRIPTEUR_MAGASIN);
        } catch (RepositoryException repExc) {
            if (isLoggingError())
                logError(repExc);
            throw new StockVisualizationException(repExc);
        }
    }

    public RepositoryItem obtainUsersFavoriteStore(Profile pProfile) throws StockVisualizationException {
        if (isLoggingDebug()){
            logDebug("about to obtain storeId value assotiated with provided profile" + pProfile);
        }

        PropertyManager pm = getPropertyManager();
        int securityStatusCookie = pm.getSecurityStatusCookie();

        if (pProfile == null)
            throw new StockVisualizationException("provided profile property is null");

        Object profileSecurityStatus = pProfile.getPropertyValue(
                PROFILE_SECURITY_STATUS_PROPERTY_NAME);

        if ((profileSecurityStatus instanceof Integer) &&
                ((Integer) profileSecurityStatus >= securityStatusCookie)) {

            RepositoryItem abonnementItem = null;
            try {
                abonnementItem = getNewsletterRepository().getItem(
                        (String) pProfile.getPropertyValue(PROFILE_LOGIN_PROPERTY_NAME), ABONNEMENT_NEWSLETTER_ITEM_DESCRIPTOR_NAME);
            } catch (RepositoryException repExc) {
                throw new StockVisualizationException(repExc);
            }

            if (abonnementItem != null) {
                RepositoryItem magasinItem = (RepositoryItem) abonnementItem.getPropertyValue(
                        ABONNEMENT_MAGASIN_REF_PROPERTY_NAME);

                if (magasinItem != null) {
                    if (isLoggingDebug())
                        logDebug("user's " + pProfile + " favorite store is " + magasinItem.getRepositoryId());

                    return magasinItem;
                }
            }
        }

        return null;
    }

    /**
     * Tries to obtain <tt>magasin</tt> repository item based on its
     * <tt>storeId</tt> property.
     *
     * @param pStoreId
     * @return magasin RepositoryItem
     * @throws StockVisualizationException
     */
    public RepositoryItem claimMagasinByStoreId(String pStoreId) throws StockVisualizationException {
        if (isLoggingDebug()){
            logDebug("about to obtain magasin repository item based storeId: " + pStoreId);
        }

        if (pStoreId == null){
            throw new StockVisualizationException("provided storeId param is null");
        }

        Repository storeRepository = getStoreRepository();

        try {
            RepositoryView magasinView = storeRepository.getView(MAGASIN_ITEM_DESCRIPTOR_NAME);
            RqlStatement byStoreidStatement = RqlStatement.parseRqlStatement(BY_STORE_ID_RQL_STATEMENT);

            Object[] params = new String[]{pStoreId};
            RepositoryItem[] claimedItems = byStoreidStatement.executeQuery(magasinView, params);

            RepositoryItem magasinItem = null;
            if ((claimedItems != null) && ((magasinItem = claimedItems[0]) != null)) {
                if (isLoggingDebug()){
                    logDebug("the following magasin item: " + magasinItem.getRepositoryId() +
                            " was claimed by storeId: " + pStoreId);
                }

                return magasinItem;
            } else {
                if (isLoggingDebug()){
                    logDebug("no one magasin item was claimed by storeId: " + pStoreId);
                }

                return null;
            }
        } catch (RepositoryException repExc) {
            if (isLoggingError())
                logError(repExc);

            throw new StockVisualizationException("unable to clain magasin repository item. " + repExc);
        }
    }

    public List<RepositoryItem> claimAllMagasins() throws StockVisualizationException {
        RepositoryItem[] claimedItems = queryMagasinRepository(ALL_MAGASINS_RQL_STATEMENT,
                new String[]{CASTORAMA_MAGASIN_POSTAL_CODE});
        return claimedItems == null ? null :
                new ArrayList<RepositoryItem>(Arrays.asList(claimedItems));
    }

    public RepositoryItem claimMagasinByName(String magasinName)
            throws StockVisualizationException {
        if (StringUtils.isEmpty(magasinName)){
            throw new StockVisualizationException("provided magasinName parameter is either empty or null");
        }

        RepositoryItem[] claimedItems = queryMagasinRepository(BY_MAGASIN_NAME_RQL_STATEMENT,
                new String[]{magasinName});

        return (claimedItems == null || claimedItems.length == 0) ? null : claimedItems[0];
    }

    public RepositoryItem claimMagasinByPostalCode(String postalCode)
            throws StockVisualizationException {
        if (StringUtils.isEmpty(postalCode)){
            throw new StockVisualizationException("provided postalCode parameter is either null or empty");
        }

        RepositoryItem[] claimedItems = queryMagasinRepository(BY_POSTAL_CODE_RQL_STATEMENT,
                new String[]{postalCode});

        return (claimedItems == null || claimedItems.length == 0) ? null : claimedItems[0];
    }

    public RepositoryItem claimMagasinByNameAndPostalCode(String magasinName, String postalCode)
            throws StockVisualizationException {
        if (StringUtils.isEmpty(magasinName) || StringUtils.isEmpty(postalCode)){
            throw new StockVisualizationException("either magasinName or postal code parameter is empty or null");
        }

        RepositoryItem[] claimedItems = queryMagasinRepository(BY_MAGASIN_NAME_AND_POSTAL_CODE_RQL_STATEMENT,
                new String[]{magasinName, postalCode});

        return (claimedItems == null || claimedItems.length == 0) ? null : claimedItems[0];
    }

    private RepositoryItem[] queryMagasinRepository(String rqlStatement, String[] params)
            throws StockVisualizationException {
        Repository storeRepository = getStoreRepository();

//	try {
        if (params == null || params.length == 0) return null;
        List<StockModel> list = queryStockLevelByPostalCode("000000", params[0].trim(), "");
        if (list == null || list.size() == 0) return null;
        return new RepositoryItem[]{list.get(0).getMagasin()};

//	  RepositoryView magasinView = storeRepository.getView(MAGASIN_ITEM_DESCRIPTOR_NAME);
//	  RqlStatement preparedStatement = RqlStatement.parseRqlStatement(rqlStatement);
//
//	  RepositoryItem[] claimedItems = preparedStatement.executeQuery(magasinView, params);
//
//	  if (isLoggingDebug()) 
//		logDebug("upon execution of the following query " + rqlStatement + " with the following params" + 
//			  params + (claimedItems == null? "0": claimedItems.length) + " magasin items was claimed"  );
//
//	  return claimedItems;
//	} catch (RepositoryException repExc) {
//	  if ( isLoggingError() ) 
//		logError(repExc);
//
//	  throw new StockVisualizationException(repExc);
//	}
    }

    public List<StockModel> createDummyStockModelListByStoreId(String pProdId, String pStoreId, String pQuantity)
            throws StockVisualizationException {

        return Arrays.asList(new StockModel("prodId", "storeId", "12", "0", null),
                new StockModel("prodId1", "storeId1", "1", "10", null),
                new StockModel("prodId2", "storeId2", "1", "20", null));
    }

    protected List<StockModel> queryStockLevel(StockVisualizationURLBuilder pURLBuilder) throws StockVisualizationException {
        StockVisualizationSender sender = getStockVisualizationSender();
        DataProcessible requestDataProdessor = getRequestDataProcessor();

        URL formattedURL = pURLBuilder.obtainFormattedURL();
        String mimeType = getMIMEType();

        if (isLoggingDebug()){
            logDebug("trying to send request with MIME type " + mimeType +
                    " to the following URL - " + formattedURL.toString());
        }

        String response = sender.sendRequest(formattedURL, mimeType);
        List<StockModel> receivedStockModels = requestDataProdessor.processData(response);

        if (isLoggingDebug()){
            logDebug("the following list of stock levels was received from " +
                    "service " + receivedStockModels);
        }

        return receivedStockModels;
    }


    public List<StockModel> verifyStockLevelWithUpdate(Stocks stocks) throws StockVisualizationException {

        if (stocks == null) {
            throw new StockVisualizationException("Provided parameter 'stocks' is null.");
        }

        StockVisualizationSender sender = getStockVisualizationSender();
        DataProcessible requestDataProcessor = getRequestDataProcessor();

        String jsonRequest = getStocksJsonRequestMessage(toSVSFormat(stocks));
        String requestUrl = MessageFormat.format(stockVerifyUrlTemplate, jsonRequest);
        URL url;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            throw new StockVisualizationException("Incorrect stock verify request url.", e);
        }
        String response = sender.sendRequest(url, mMIMEType);

        return requestDataProcessor.processData(response);
    }

    private String getStocksJsonRequestMessage(Stocks stocks) {
        //there is no mention of Gson's thread safety
        Gson gson = new GsonBuilder().create();
        gson.toJson(stocks);

        return gson.toJson(stocks);
    }

    private Stocks toSVSFormat(Stocks stocks) {
        Map<String, Long> productsMap = new HashMap<String, Long>();
        Map<String, Long> stocksMap = stocks.productsQuantityMap;
        for (String codeArticle : stocksMap.keySet()) {
            productsMap.put(CastUtils.buildSVSProductId(codeArticle), stocksMap.get(codeArticle));
        }

        return new Stocks(stocks.getStoreId(), productsMap);
    }

    public String getStoreURLTemplate() {
        return mStoreURLTemplate;
    }

    public void setStoreURLTemplate(String pURLTemplate) {
        mStoreURLTemplate = pURLTemplate;
    }

    public String getCodePostalURLTemplate() {
        return mCodePostalURLTemplate;
    }

    public void setCodePostalURLTemplate(String pCodePostalURLTemplate) {
        mCodePostalURLTemplate = pCodePostalURLTemplate;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String pChannel) {
        mChannel = pChannel;
    }

    public String getMIMEType() {
        return mMIMEType;
    }

    public void setMIMEType(String pMIMEType) {
        mMIMEType = pMIMEType;
    }

    public StockVisualizationSender getStockVisualizationSender() {
        return mStockVisualizationSender;
    }

    public void setStockVisualizationSender(StockVisualizationSender pStockVisualizationSender) {
        mStockVisualizationSender = pStockVisualizationSender;
    }

    public DataProcessible getRequestDataProcessor() {
        return mRequestDataProcessor;
    }

    public void setRequestDataProcessor(DataProcessible pRequestDataProcessor) {
        mRequestDataProcessor = pRequestDataProcessor;
    }

    public Repository getStoreRepository() {
        return mStoreRepository;
    }

    public void setStoreRepository(Repository pStoreRepository) {
        mStoreRepository = pStoreRepository;
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public Repository getNewsletterRepository() {
        return newsletterRepository;
    }

    public void setNewsletterRepository(Repository newsletterRepository) {
        this.newsletterRepository = newsletterRepository;
    }

    public boolean isStockVisualizationDisabled() {
        return stockVisualizationDisabled;
    }

    public void setStockVisualizationDisabled(boolean stockVisualizationDisabled) {
        this.stockVisualizationDisabled = stockVisualizationDisabled;
    }

    public String getStockVerifyUrlTemplate() {
        return stockVerifyUrlTemplate;
    }

    public void setStockVerifyUrlTemplate(String stockVerifyUrlTemplate) {
        this.stockVerifyUrlTemplate = stockVerifyUrlTemplate;
    }
}
