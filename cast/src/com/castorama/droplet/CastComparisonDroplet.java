package com.castorama.droplet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;

import com.castorama.comparison.ComparisonSessionBean;

/**
 * CastComparisonDroplet compares the given products 
 *
 * @author  EPAM team
 */
public class CastComparisonDroplet extends DynamoServlet {
    /** ACTION_PARAM constant */
    public static final String ACTION_PARAM = "action";

    /** ACTION_ADD constant */
    public static final String ACTION_ADD = "add";

    /** ACTION_REMOVE constant */
    public static final String ACTION_REMOVE = "remove";

    /** ACTION_COMPARE constant */
    public static final String ACTION_COMPARE = "compare";

    /** SKU_ID constant */
    public static final String SKU_ID = "skuId";

    /** CASTO_SKU constant */
    public static final String CASTO_SKU = "casto_sku";

    /** PRODUCT_ID constant */
    public static final String PRODUCT_ID = "productId";

    /** CASTO_PRODUCT constant */
    private static final String CASTO_PRODUCT = "casto_product";

    /** CATALOG constant */
    private static final String CATALOG = "catalog";

    /** COMPARABLE_PROPERTIES constant */
    private static final String COMPARABLE_PROPERTIES = "comparableProperties";
    
    /** COMPARE_PRODUCTS constant */
    private static final String COMPARE_PRODUCTS = "compareProducts";

    /** OUTPUT constant */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** EMPTY constant */
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** repository property */
    private Repository mRepository;

    /** profile property */
    private Profile mProfile;

    /** comparisonSessionBean property */
    private ComparisonSessionBean mComparisonSessionBean;

    /** PARAM_INDEX constant */
    private static final String INDEX_PARAM = "productIndex";
    
    /** DISCOUNT_PARAM constant */
    private static final String DISCOUNT_PARAM = "skuDiscount";
    
    /** DISCOUNTS constant */
    private static final String DISCOUNTS = "discounts";

    
    enum ComparedProducts{
        FIRST("compared0"),
        SECOND("compared1"),
        THIRD("compared2"),
        FORTH("compared3");
        private String mProductIndex;
        private ComparedProducts(String pProductIndex){
            this.mProductIndex = pProductIndex;
        }
    }
    
    /**
     * Depends on given parameter adds to ComparisonSessionBean products or shows them with a set of comparable properties.
     *
     * @param  pRequest  DynamoHttpServletRequest object
     * @param  pResponse DynamoHttpServletResponse object
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String action = pRequest.getParameter(ACTION_PARAM);
        String skutId = pRequest.getParameter(SKU_ID);
        String productId = pRequest.getParameter(PRODUCT_ID);
        String productIndex = pRequest.getParameter(INDEX_PARAM);
        String skuDiscount = pRequest.getParameter(DISCOUNT_PARAM);
        
        if (ACTION_ADD.equals(action)) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put(productId, skutId);
            getComparisonSessionBean().getComparedProducts().put(productIndex, map);
            getComparisonSessionBean().addSkuDiscount(skutId, skuDiscount);
        } else if (ACTION_REMOVE.equals(action)) {
            getComparisonSessionBean().removeComparedProduct(productIndex);
            getComparisonSessionBean().removeSkuDiscount(skutId);
        } else {
            RepositoryItem castCatalog = (RepositoryItem) getProfile().getPropertyValue(CATALOG);
            List<String> comparableProperties = (List<String>) castCatalog.getPropertyValue(COMPARABLE_PROPERTIES);
            Map<String, String> props = new LinkedHashMap<String, String>();
            Map<String, Map<RepositoryItem, RepositoryItem>> result = new TreeMap<String, Map<RepositoryItem, RepositoryItem>>();
            for(ComparedProducts cProd : ComparedProducts.values()){
                result.put(cProd.mProductIndex, null);
            }
            
            for (Map.Entry<String, Map<String, String>> entry : getComparisonSessionBean().getComparedProducts().entrySet()) {
                String compareIndex = entry.getKey();
                Map<String, String> map = entry.getValue();
                if(map != null) {
                    for (Map.Entry<String, String> thisEntry : map.entrySet()) {
                        String thisproductId = thisEntry.getKey();
                        String thisSkuId = thisEntry.getValue();
                        if(!StringUtils.isBlank(thisSkuId) && !StringUtils.isBlank(thisproductId)){
                            try {
                                final RepositoryItem skuObject = getRepository().getItem(thisSkuId, CASTO_SKU);
                                final RepositoryItem productObject = getRepository().getItem(thisproductId, CASTO_PRODUCT);
                                Map<RepositoryItem, RepositoryItem> tempMap = new HashMap<RepositoryItem, RepositoryItem>(1);
                                tempMap.put(productObject, skuObject);
                                result.put(compareIndex, tempMap);
            
                                RepositoryItemDescriptor itemdescr = skuObject.getItemDescriptor();
                                String[] propertyNames = itemdescr.getPropertyNames();
                                Iterator<String> iterator = comparableProperties.iterator();
                                while (iterator.hasNext()) {
                                    String name = iterator.next();
                                    if ((Arrays.asList(propertyNames)).contains(name)) {
                                        DynamicPropertyDescriptor desr = itemdescr.getPropertyDescriptor(name);
                                        props.put(name, desr.getDisplayName());
                                    }
                                }
                                
                                
                            } catch (RepositoryException e) {
                                pRequest.serviceParameter(EMPTY, pRequest, pResponse);
                            } 
                        }
                    }
                } 
            }
            
            pRequest.setParameter(DISCOUNTS, getComparisonSessionBean().getSkuDiscounts());
            pRequest.setParameter(COMPARE_PRODUCTS, result);
            pRequest.setParameter(COMPARABLE_PROPERTIES, props);
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
        }  
    }

    /**
     * Returns comparisonSessionBean property.
     *
     * @return comparisonSessionBean property.
     */
    public ComparisonSessionBean getComparisonSessionBean() {
        return mComparisonSessionBean;
    }

    /**
     * Sets the value of the comparisonSessionBean property.
     *
     * @param pComparisonSessionBean parameter to set.
     */
    public void setComparisonSessionBean(ComparisonSessionBean pComparisonSessionBean) {
        mComparisonSessionBean = pComparisonSessionBean;
    }

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
     * Returns profile property.
     *
     * @return profile property.
     */
    public Profile getProfile() {
        return mProfile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param pProfile parameter to set.
     */
    public void setProfile(Profile pProfile) {
        mProfile = pProfile;
    }

}
