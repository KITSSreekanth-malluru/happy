package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;
import com.castorama.commerce.inventory.CastRepositoryInventoryManager.StockLevelStatus;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns correct list of product for carousel in case of "mother category
 * carousel". Removes "casto-pack" && "immidiate withdrawal products".
 *
 * @author Epam Team
 */
public class CastCarouselFilterDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** PRODUCTS_FOR_CAROUSEL constant. */
    public static final String PRODUCTS_FOR_CAROUSEL = "productsForCarousel";

    /** FILTERED_PRODUCTS constant. */
    public static final String FILTERED_PRODUCTS = "filteredProducts";
    
    /** STORE constant. */
    public static final String STORE = "store";
    
    /** SV_AVAILABLE_MAP constant. */
    public static final String SV_AVAILABLE_MAP = "svAvailableMap";
    

    /** excludedFromCarouselProductTypes property. */
    private List<String> mExcludedFromCarouselProductTypes;
    
    /** inventoryManager property */
    private CastRepositoryInventoryManager mInventoryManager;


    /**
     * Returns excludedFromCarouselProductTypes property.
     *
     * @return excludedFromCarouselProductTypes property.
     */
    public List<String> getExcludedFromCarouselProductTypes() {
        return mExcludedFromCarouselProductTypes;
    }

    /**
     * Sets the value of the excludedFromCarouselProductTypes property.
     *
     * @param pExcludedFromCarouselProductTypes parameter to set.
     */
    public void setExcludedFromCarouselProductTypes(List<String> pExcludedFromCarouselProductTypes) {
        mExcludedFromCarouselProductTypes = pExcludedFromCarouselProductTypes;
    }
   
    /**
     * Returns inventoryManager property.
     *
     * @return inventoryManager property.
     */
    public CastRepositoryInventoryManager getInventoryManager() {
        return mInventoryManager;
    }

    /**
     * Sets the value of the inventoryManager property.
     *
     * @param pInventoryManager parameter to set.
     */
    public void setInventoryManager(CastRepositoryInventoryManager pInventoryManager) {
        mInventoryManager = pInventoryManager;
    }

    /**
     * Returns correct list of product for carousel in case of "mother category
     * carousel".
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Object productsForCarouselObj = pRequest.getObjectParameter(PRODUCTS_FOR_CAROUSEL);
        
        RepositoryItem store = (RepositoryItem) pRequest.getObjectParameter(STORE);
        Map<Integer, Integer> svAvailableMap = (Map<Integer, Integer>) pRequest.getObjectParameter(SV_AVAILABLE_MAP);
        
        List<RepositoryItem> filteredProducts = new ArrayList<RepositoryItem>();
        if ((productsForCarouselObj != null) && (getExcludedFromCarouselProductTypes() != null) &&
                !getExcludedFromCarouselProductTypes().isEmpty() && getInventoryManager() != null) {
            for (RepositoryItem prodRepItem : (List<RepositoryItem>) productsForCarouselObj) {
                boolean includeProduct = true;
                for (String exclPrType : getExcludedFromCarouselProductTypes()) {
                    if (!StringUtils.isBlank(exclPrType) &&
                            exclPrType.equalsIgnoreCase((String) prodRepItem.getPropertyValue("type"))) {
                        includeProduct = false;
                    }
                }
                //filter "immediate withdrawal" products for carousel
                if (includeProduct){
                    // store parameter
                    // svAvailableMap parameter
                    if (getInventoryManager().inventoryStockLevelStatus(prodRepItem, store, svAvailableMap) == StockLevelStatus.S0){
                        includeProduct = false;
                    }
                }
                if (includeProduct) {
                    filteredProducts.add(prodRepItem);
                }
            }
            pRequest.setParameter(FILTERED_PRODUCTS, filteredProducts);
        } else {
            pRequest.setParameter(FILTERED_PRODUCTS, productsForCarouselObj);
        }

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }
}
