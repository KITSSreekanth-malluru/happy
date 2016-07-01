package com.castorama.mobile.droplet;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Find first product from child category.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class FirstProductFromChildCategories extends DynamoServlet {
    /** OUTPUT constant. */
    public static final String OUTPUT = "output";

    /** EMPTY constant. */
    public static final String EMPTY = "empty";

    /** CATEGORY constant. */
    public static final String FP_CATEGORY = "fpCategory";

    /** PRODUCT constant. */
    public static final String FP_SKU = "fpSKU";

    /** TYPE constant. */
    public static final String TYPE = "type";

    /** CASTO_PRODUCT constant. */
    public static final String CASTO_PRODUCT = "casto_product";

    /** PRODUCT constant. */
    public static final String PRODUCT = "product";

    /** CHILD_SKUS constant. */
    public static final String CHILD_SKUS = "childSkus";

    /** BUNDLE_LINKS constant. */
    public static final String BUNDLE_LINKS = "bundleLinks";

    /** ITEM constant. */
    public static final String ITEM = "item";

    /** CHILD_PRODUCTS constant. */
    public static final String CHILD_PRODUCTS = "childProducts";

    /** CHILD_CATEGORIES constant. */
    public static final String CHILD_CATEGORIES = "childCategories";

    /**
     * Find first product from child category.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        Object fpCategory = pRequest.getObjectParameter(FP_CATEGORY);
        Object result = null;
        if ((fpCategory != null) && (fpCategory instanceof RepositoryItem)) {
            RepositoryItem repFpCategory = (RepositoryItem) fpCategory;
            Object product = findFirstProduct(repFpCategory);
            if ((product != null) && (product instanceof RepositoryItem)) {
                RepositoryItem repProduct = (RepositoryItem) product;
                String prType = (String) repProduct.getPropertyValue(TYPE);
                if (prType != null) {
                    if (CASTO_PRODUCT.equalsIgnoreCase(prType) || PRODUCT.equalsIgnoreCase(prType)) {
                        List childSkus = (List) repProduct.getPropertyValue(CHILD_SKUS);
                        if ((childSkus != null) && !childSkus.isEmpty()) {
                            result = childSkus.get(0);
                        }
                    } else {
                        List<RepositoryItem> childSkus = (List) repProduct.getPropertyValue(CHILD_SKUS);
                        if ((childSkus != null) && !childSkus.isEmpty()) {
                            RepositoryItem childSku = childSkus.get(0);
                            List<RepositoryItem> bundleLinks = (List) childSku.getPropertyValue(BUNDLE_LINKS);
                            if ((bundleLinks != null) && !bundleLinks.isEmpty()) {
                                result = (RepositoryItem) bundleLinks.get(0).getPropertyValue(ITEM);
                            }
                        }

                    }
                }
            }  // end if
        }  // end if
        if (result != null) {
            pRequest.setParameter(FP_SKU, result);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * Find first product from child category.
     *
     * @param  pFpCategory parameter
     *
     * @return returns first product
     */
    Object findFirstProduct(RepositoryItem pFpCategory) {
        List childProducts = (List) pFpCategory.getPropertyValue(CHILD_PRODUCTS);
        Object childProduct = null;
        if ((childProducts != null) && !childProducts.isEmpty()) {
            return childProducts.get(0);
        } else {
            List<RepositoryItem> childCategories = (List) pFpCategory.getPropertyValue(CHILD_CATEGORIES);
            if (childCategories != null) {
                for (RepositoryItem cc : childCategories) {
                    childProduct = findFirstProduct(cc);
                    if ((cc != null) && (childProduct != null)) {
                        break;
                    }
                }
            } else {
                childProduct = null;
            }
        }
        return childProduct;
    }
}
