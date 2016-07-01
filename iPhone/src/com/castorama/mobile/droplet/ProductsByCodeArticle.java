/**
 *
 */
package com.castorama.mobile.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.json.JSONException;
import atg.json.JSONObject;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import static com.castorama.mobile.tools.CastoramaConstants.*;
import com.castorama.mobile.tools.CastoramaTools;

/**
 * Find all  parent products for passed codeArtcile: 1) retrieves code article
 * get all skus with the same code article
 *
 * @author Katsiaryna Sharstsiuk
 */
public class ProductsByCodeArticle extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** ERROR_END_OF_LIFE constant. */
    public static final ParameterName ERROR_END_OF_LIFE = ParameterName.getParameterName("errorEndOfLife");

    /** IS_GROUPED_OR_PACK constant. */
    public static final String IS_GROUPED_OR_PACK = "isGroupedOrPack";

    /** BARCODE_CONVERTER_SERVICE_UNAVAILABLE constant. */
    private static final String BARCODE_CONVERTER_SERVICE_UNAVAILABLE = "barcodeConverterServiceUnavailable";

    /** repository property */
    private Repository mRepository;

    /** itemDescriptorName property */
    private String mItemDescriptorName = "casto_sku";

    /** maxCodeArticleCount property */
    private int mMaxCodeArticleCount = 0;
    
    /** mExcludedProductTypes constant. */
    private List<String> mExcludedProductTypes;

    /** mCastoramaTools constant. */
    private CastoramaTools mCastoramaTools;

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns itemDescriptorName property.
     *
     * @return itemDescriptorName property.
     */
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pItemDescriptorName parameter to set.
     */
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    /**
     * Returns maxCodeArticleCount property.
     *
     * @return maxCodeArticleCount property.
     */
    public int getMaxCodeArticleCount() {
        return mMaxCodeArticleCount;
    }
    
    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pMaxCodeArticleCount parameter to set.
     */
    public void setMaxCodeArticleCount(int pMaxCodeArticleCount) {
        mMaxCodeArticleCount = pMaxCodeArticleCount;
    }
    
    /**
     * Returns excludedProductTypes property.
     *
     * @return excludedProductTypes property.
     */
    public List<String> getExcludedProductTypes() {
        return mExcludedProductTypes;
    }

    /**
     * Sets the value of the excludedProductTypes property.
     *
     * @param pExcludedProductTypes parameter to set.
     */
    public void setExcludedProductTypes(List<String> pExcludedProductTypes) {
        mExcludedProductTypes = pExcludedProductTypes;
    }

    /**
     * Returns castoramaTools property.
     *
     * @return castoramaTools property.
     */
    public CastoramaTools getCastoramaTools() {
        return mCastoramaTools;
    }

    /**
     * Sets the value of the castoramaTools property.
     *
     * @param pCastoramaTools parameter to set.
     */
    public void setCastoramaTools(CastoramaTools pCastoramaTools) {
        mCastoramaTools = pCastoramaTools;
    }

    /**
     * Find all  parent products for passed codeArtcile: 1) retrieves code
     * article get all skus with the same code article
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String codeArticle = pRequest.getParameter(CODE_ARTICLE);
        String barcode = pRequest.getParameter(BARCODE);
        Repository rep = getRepository();

        boolean isBarcodeConverterServiceUnavailable = false;
        CastoramaTools castoramaTools = getCastoramaTools();
        String errorCode = "1";
        List<String> strCodeArticles = new ArrayList<String>();
        if (!StringUtils.isBlank(barcode) && StringUtils.isBlank(codeArticle)) {
            JSONObject jsonObject = castoramaTools.convertBarcodeToCodeArticle(barcode);
            if (jsonObject != null && jsonObject.has(ERROR_CODE)) {
            	try {	
                    errorCode = jsonObject.getString(ERROR_CODE);
                    if ("0".equals(errorCode) && jsonObject.has(CODE_ARTICLE)) {
                        String joCA = jsonObject.getString(CODE_ARTICLE);
                        if (joCA != null) {
                            strCodeArticles.add(joCA);
                        }
                    }          	
            	} catch (JSONException e) {
                    if (isLoggingError()) logError(e.getMessage());
                }
            } else {
            	isBarcodeConverterServiceUnavailable = true;
            }
        } else if (!StringUtils.isBlank(codeArticle)) {
            String[] initialCodeArticles = codeArticle.split(",");
            if ((initialCodeArticles != null) && (initialCodeArticles.length > 0)) {
            	strCodeArticles.addAll(Arrays.asList(initialCodeArticles));
            } 
        }  // end if-else

        if (strCodeArticles != null && !strCodeArticles.isEmpty()) {
            Map<String, Set<RepositoryItem>> caToParentProducts = new LinkedHashMap<String, Set<RepositoryItem>>();
            int result = -1;
            for (String ca : strCodeArticles) {
                if (countProducts(caToParentProducts) < mMaxCodeArticleCount){
                    result = getSkuParentProducts(ca, rep, castoramaTools, caToParentProducts);
                } else {
                    break;
                }
            }
            if (strCodeArticles.size() == 1) {
                switch (result) {
                    case -1:
                        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
                        break;

                    case 1:
                        pRequest.setParameter(IS_GROUPED_OR_PACK, true);
                        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
                        break;

                    case -2:
                        pRequest.serviceLocalParameter(ERROR_END_OF_LIFE, pRequest, pResponse);
                        break;

                    case 2:
                        pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
                        break;

                    default:
                        pRequest.setParameter(PRODUCTS_SET, caToParentProducts);
                        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                }
            } else {  // end if
                if ((caToParentProducts != null) && !caToParentProducts.isEmpty()) {
                    pRequest.setParameter(PRODUCTS_SET, caToParentProducts);
                    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

                } else {
                    pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
                }
            }  // end if-else
        } else {
            if (isBarcodeConverterServiceUnavailable) {
                pRequest.serviceLocalParameter(BARCODE_CONVERTER_SERVICE_UNAVAILABLE, pRequest, pResponse);
            } else {
                pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
            }

        }  // end if-else
    }

    /**
     * Retrieves Parent products
     *
     * @param  pCa
     * @param  pRep                   parameter
     * @param  pCastoramaTools        parameter
     * @param  pSkuIdToParentProducts parameter
     *
     * @return error code.
     */
    private int getSkuParentProducts(String pCa, Repository pRep, CastoramaTools pCastoramaTools,
                                     Map pSkuIdToParentProducts) {
        int result = -1;
        java.util.Set<RepositoryItem> parentProducts = null;
        TreeSet<RepositoryItem> productSet = new TreeSet<RepositoryItem>();
        int parentProductsInitCount = 0;
        if ((pCastoramaTools != null) && !StringUtils.isBlank(pCa) && (pRep != null) &&
                !StringUtils.isBlank(getItemDescriptorName())) {
            try {
                RepositoryView repView = pRep.getView(getItemDescriptorName());

                RqlStatement skusRQL = RqlStatement.parseRqlStatement(CODEARTICLE_QUERY);
                RepositoryItem[] skus = skusRQL.executeQuery(repView, new Object[] {pCa});
                if ((skus != null) && (skus.length > 0)) {
                    //NOTE : Should be only one code article per each sku.
                    //for (RepositoryItem sku : skus) {
                    Date currentDate = new Date();
                    Date dateFinVie = (Date) skus[0].getPropertyValue(DATE_FIN_VIE);
                    if (((dateFinVie != null) && currentDate.before(dateFinVie)) || (dateFinVie == null)) {
                        parentProducts = (java.util.Set<RepositoryItem>) skus[0].getPropertyValue(PARENT_PRODUCTS);
                        if (parentProducts != null) {
                            for (RepositoryItem parentProduct : parentProducts) {
                                if (parentProduct != null) {
                                    Set<RepositoryItem> catalogs = (Set<RepositoryItem>) parentProduct.getPropertyValue(CATALOGS);
                                    //TODO check if catalog is masterCatalog, but not IK catalog
                                    if ((catalogs != null) && (catalogs.size() > 0)) {
                                        for (RepositoryItem catalog: catalogs){
                                            if (catalog.getRepositoryId().equalsIgnoreCase("masterCatalog")){
                                                productSet.add(parentProduct);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            filterProducts(productSet);
                            if ((productSet != null) && !productSet.isEmpty()) {
                                String selectedSkuId = skus[0].getRepositoryId();
                                pSkuIdToParentProducts.put(selectedSkuId, productSet);
                                result = 0;
                            } else {
                                result = 1;
                            }
                        }  // end if
                    } else {
                        result = -2;
                    }  // end if-else
                    //}
                }  // end if
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
                result = 2;
            }  // end try-catch
        }  // end if
        return result;
    }

    /**
     * Filter products by type. Remove all products with type enumerated in
     * excludedProductTypes list.
     *
     * @param  pProductSet parameter
     *
     * @return filtered products.
     */
    private Set<RepositoryItem> filterProducts(Set<RepositoryItem> pProductSet) {
        List<String> excludedProductTypes = getExcludedProductTypes();
        if ((excludedProductTypes != null) && !excludedProductTypes.isEmpty() && (pProductSet != null) &&
                !pProductSet.isEmpty()) {
            for (Iterator<RepositoryItem> it = pProductSet.iterator(); it.hasNext();) {
                RepositoryItem product = it.next();
                String productType = (String) product.getPropertyValue(TYPE);
                if (!StringUtils.isBlank(productType)) {
                    for (String ept : excludedProductTypes) {
                        if (productType.equalsIgnoreCase(ept)) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Filter products by type. Remove all products with type enumerated in
     * excludedProductTypes list.
     *
     * @param  pProductSet parameter
     *
     * @return filtered products.
     */
    private int countProducts(Map<String, Set<RepositoryItem>> pCaToParentProducts ) {
        int counter = 0;
        for (Set<RepositoryItem> ept : pCaToParentProducts.values()) {
            counter+=ept.size();
        }
        return counter;
    }

}
