package com.castorama.shoppinglist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Session bean for storing current user's shopping list items 
 * @author Andrei_Krauchanka
 *
 */
public class ShoppingListBean {
    
    /**
     * Class for holding together productId and skuId
     * @author Andrei_Krauchanka
     *
     */
    public class ProductInfo {
        
        private String skuId;
        private String productId;
        
        public ProductInfo(String skuId, String productId) {
            this.skuId = skuId;
            this.productId = productId;
        }
        
        public String getSkuId() {
            return skuId;
        }
        
        public String getProductId() {
            return productId;
        }
    }
    
    private int mNextIndex = 0;
    
    //Contain list of pair of skuId and productId
    private Map<Integer, ProductInfo> shoppingListProducts = new LinkedHashMap<Integer, ProductInfo>();

    public Map<Integer, ProductInfo> getShoppingListProducts() {
        return shoppingListProducts;
    }
    
    public void addProductToShoppingList(String skuId, String productId) {
        ProductInfo productInfo = new ProductInfo(skuId, productId);
        getShoppingListProducts().put(getNextIndex(), productInfo);
    }
    
    public void removeProductFromShoppingList(int index) {
        getShoppingListProducts().remove(index);
    }

    public int getNextIndex() {
        mNextIndex += 1;
        return mNextIndex;
    }

    public void setNextIndex(int pNextIndex) {
        this.mNextIndex = pNextIndex;
    }
}
