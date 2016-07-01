package com.castorama.comparison;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * ComparisonSessionBean class - container for compared products
 * It holds Map with key - SKU id and value - PRODUCT id 
 *
 * @author  EPAM team
 */
public class ComparisonSessionBean {

    Map<String, Map<String, String>> mComparedProducts = new TreeMap<String, Map<String, String>>();
    
    Map<String, String> mSkuDiscounts = new HashMap<String, String>();
    
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
    
    
    public ComparisonSessionBean(){
        for(ComparedProducts cProd : ComparedProducts.values()){
            mComparedProducts.put(cProd.mProductIndex, null);
        }
    }
    
    /**
     * Returns comparedProducts property.
     *
     * @return comparedProducts property.
     */
    public Map<String, Map<String, String>> getComparedProducts() {
        return mComparedProducts;
    }
    
    /**
     * Removes element from the comparedProducts array.
     *
     * @param pIndex index in array.
     */
    public void removeComparedProduct(String pIndex) {
        this.mComparedProducts.remove(pIndex);
    }

    public Map<String, String> getSkuDiscounts() {
        return mSkuDiscounts;
    }

    public void addSkuDiscount(String skuId, String discount) {
        getSkuDiscounts().put(skuId, discount);
    }
    
    public void removeSkuDiscount(String pIndex) {
        getSkuDiscounts().remove(pIndex);
    }
}