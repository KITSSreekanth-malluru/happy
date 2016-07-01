package com.castorama.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.castorama.commerce.inventory.CastRepositoryInventoryManager;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

/**
 * Tools for calculating addition parameters to show price block
 * 
 * @author MK
 */
public class CastoDropletsPricingTools extends GenericService {

    /**
     * 
     */
    private static final String CARD_PRICE_SHOWED = "cardPriceShowed";
    public static final String CUP = "CUP";
    public static final String CODES_UNITE = "codesUnite";
    public static final String FLAG = "flag";
    public static final String LIBELLE = "libelle";
    public static final String FLAG_O = "O";
    public static final String PUPUV = "PUPUV";
    
    private static final String M2_CONST = "m2";

    public static final String ECONOMY = "economy";
    public static final String ON_DISCOUNT = "onDiscount";
    public static final String PERCENT = "percent";
    public static final String PRICE_PER_UNITE = "pricePerUnite";
    
    public static final String SKU_PROPERTY_EXONERATIONPFE = "exonerationPFE";

    static final String LIST_PRICE = "listPrice";
    static final String SALE_PRICE = "salePrice";
    static final String CARD_PRICE = "cardPrice";

    public Map<String, Object> calculateForCastPriceDroplet(BigDecimal listPrice, BigDecimal salePrice, BigDecimal cardPrice,
            boolean showCardPrice, boolean enabledCastCart)
            throws ServletException, IOException {
        return calculateForCastPriceDroplet(listPrice, salePrice, cardPrice,
                showCardPrice, enabledCastCart, false);
    }
    
    public Map<String, Object> calculateForCastPriceDroplet(BigDecimal listPrice, BigDecimal salePrice, BigDecimal cardPrice,
            boolean showCardPrice, boolean enabledCastCart, boolean localPriceApplied)
            throws ServletException, IOException {
        HashMap<String, Object> tempMap = new HashMap<String, Object>();

        // In click and collect store sale price may be greater than list price.
        boolean showSalePrice = salePrice != null && (localPriceApplied || listPrice.compareTo(salePrice) > 0); 
        showCardPrice = (cardPrice != null) && (listPrice.compareTo(cardPrice) > 0) && (cardPrice.signum() > 0) 
                && enabledCastCart && showCardPrice;
        if (listPrice != null && listPrice.signum() > 0) {
            if (showSalePrice && showCardPrice){
                if(cardPrice.compareTo(salePrice) <= 0 ) {
                    tempMap = addEconomyToRequest(listPrice, cardPrice);
                    tempMap.put(CARD_PRICE_SHOWED, true);
                } else {
                    tempMap = addEconomyToRequest(listPrice, salePrice);
                }
            } else {
                if (showSalePrice){
                    tempMap = addEconomyToRequest(listPrice, salePrice);
                } else {
                    if (showCardPrice){
                        tempMap = addEconomyToRequest(listPrice, cardPrice);
                        tempMap.put(CARD_PRICE_SHOWED, true);
                    } else {
                        tempMap.put(ON_DISCOUNT, false);
                    }
                }
            }
        }
        return tempMap;
    }

    public Map<String, Object> calculateForUnitPriceCodeLookupDroplet(RepositoryItem sku, Double price) throws RepositoryException{

        HashMap<String, Object> tempMap = new HashMap<String, Object>();
        Double pricePerUnite = null;
        String libelle = null;
        if (getProductCatalogRepository() != null && sku != null) {
            String code = (String) sku.getPropertyValue(CUP);
            if (!StringUtils.isBlank(code)) {
                RepositoryItem codeUnite = getProductCatalogRepository().getItem(code, CODES_UNITE);
                if (codeUnite != null) {
                    String flag = (String) codeUnite.getPropertyValue(FLAG);
                    libelle = (String) codeUnite.getPropertyValue(LIBELLE);
                    if (libelle != null && libelle.equalsIgnoreCase(M2_CONST)) {
                        libelle = "m<span class=\"m2\">2</span>";
                    }
                    Float coefficient = (Float) sku.getPropertyValue(PUPUV);
                    if (!StringUtils.isBlank(flag) && FLAG_O.equals(flag) && coefficient != null && coefficient != 0) {
                        if (price != null) {
                            pricePerUnite = price / coefficient;
                        }
                    }
                }
            }
        }

        if (pricePerUnite != null) {
            tempMap.put(PRICE_PER_UNITE, pricePerUnite);
            tempMap.put(LIBELLE, libelle);
        }
        return tempMap;

    }
    
    public Map<String, Object> calculateForFreeShippingDroplet(RepositoryItem sku, RepositoryItem store, Map<Integer, Integer> svAvailableMap) {
        
        HashMap<String, Object> tempMap = null;
          boolean availabilityStatusSoldInStore = getInventoryManager().isSoldOnlyInStoreForFreeShippingOnly(sku, store, svAvailableMap);
          if(!availabilityStatusSoldInStore) {
              Boolean isFreeShipping = (Boolean) sku.getPropertyValue(SKU_PROPERTY_EXONERATIONPFE);
              if(isFreeShipping!=null && isFreeShipping) {
                  return new HashMap<String, Object>();
              }
          }
        return null;
    }

    private HashMap<String, Object> addEconomyToRequest(BigDecimal listPrice, BigDecimal value){
        HashMap<String, Object> settedParams = new HashMap<String, Object>();
        BigDecimal economyVar = listPrice.subtract(value);
        BigDecimal decimalPercentVar = (economyVar.multiply(new BigDecimal(100)).divide(listPrice, 10,
                RoundingMode.HALF_UP));
        if (decimalPercentVar.signum() > 0) {
            long percentVar = Math.round(decimalPercentVar.doubleValue());
            if (percentVar != 0) {
                settedParams.put(PERCENT, "-" + percentVar + "%");
            }
        }
        settedParams.put(ON_DISCOUNT, true);
        settedParams.put(ECONOMY, economyVar);
        return settedParams;
    }

    public void putParamsToRequest(DynamoHttpServletRequest pRequest, Map<String, Object> params){
        for (String key: params.keySet()){
            pRequest.setParameter(key, params.get(key));
        }
    }
    
    private Repository mProductCatalogRepository;

    /**
     * @return the productCatalogRepository
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * @param pProductCatalogRepository
     *            the productCatalogRepository to set
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }
    
    private CastRepositoryInventoryManager inventoryManager;
    
    public CastRepositoryInventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public void setInventoryManager(CastRepositoryInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }
}