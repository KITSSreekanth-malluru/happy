package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import com.castorama.commerce.catalog.CastCatalogTools;
import com.castorama.commerce.order.CastOrderHolder;

import atg.commerce.order.CommerceItem;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.targeting.TargetingRandom;

/**
 *
 * @author EPAM team
 */
public class RecommendedProductDroplet extends TargetingRandom {
    /** catalogTools constant. */
    private CastCatalogTools mCatalogTools;

    /** shoppingCart constant. */
    private CastOrderHolder mShoppingCart;

    /**
     *
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        List out = new ArrayList();
        int count;
        try {
            count = Integer.parseInt((String) pRequest.getObjectParameter(HOW_MANY));
        } catch (Exception e) {
            count = Integer.MAX_VALUE;
        }
        CastCatalogTools catalogTools = getCatalogTools();
        CommerceItem item = getShoppingCart().getLastAddedCommerceItem();
        if (null != item) {
            // Related products
            try {
                String productId = item.getAuxiliaryData().getProductId();
                RepositoryItem product = catalogTools.findProduct(productId);
                if (null != product) {
                    List relatedProducts = (List) product.getPropertyValue("childSKUs");
                    if (null != relatedProducts && relatedProducts.size() > 0) {
                        RepositoryItem childSku = (RepositoryItem)relatedProducts.get(0);
                        List crossSelling = (List) childSku.getPropertyValue("crossSelling");
                        if(crossSelling != null) {
                            for (Iterator it = crossSelling.iterator(); it.hasNext();) {
                                if (out.size() < count) {
                                    RepositoryItem rItem = (RepositoryItem) it.next();
                                    RepositoryItem product1 = (RepositoryItem)rItem.getPropertyValue("product");
                                    if (checkProductOnlineSale(product1)) {
                                        out.add(product1);
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (isLoggingError()) {
                    logError(e);
                }
            }  // end try-catch

            // Customer targets recommendation
            if (out.size() < count) {
                try {
                    Object[] targets = getTargetArray(pRequest);
                    if (null != targets) {
                        List targets_list = Arrays.asList(targets);
                        Collections.shuffle(targets_list);
                        for (Iterator it = targets_list.iterator(); it.hasNext();) {
                            if (out.size() < count) {
                                RepositoryItem r = (RepositoryItem) it.next();
                                if (checkProductOnlineSale(r)) {
                                    out.add(r);
                                }
                            } else {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    if (isLoggingError()) {
                        logError(e);
                    }
                }  // end try-catch
            }  // end if
        }  // end if
        if (out.isEmpty()) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        } else {
            String indexName = pRequest.getParameter(INDEX_NAME);
            if (indexName == null) {
                indexName = INDEX;
            }
            String elementName = pRequest.getParameter(ELEMENT_NAME);
            if (elementName == null) {
                elementName = ELEMENT;
            }
            pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
            for (int i = 0; (i < out.size()) && (i < count); i++) {
                pRequest.setParameter(COUNT, new Integer(i + 1));
                pRequest.setParameter(indexName, new Integer(i));
                pRequest.setParameter(elementName, out.get(i));
                pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
            }
            pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
        }  // end if-else
    }

    /**
     *
     *
     * @param  pProduct parameter
     *
     * @return
     */
    private boolean checkProductOnlineSale(RepositoryItem pProduct) {
        return true;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CastCatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CastCatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns shoppingCart property.
     *
     * @return shoppingCart property.
     */
    public CastOrderHolder getShoppingCart() {
        return mShoppingCart;
    }

    /**
     * Sets the value of the shoppingCart property.
     *
     * @param pShoppingCart parameter to set.
     */
    public void setShoppingCart(CastOrderHolder pShoppingCart) {
        mShoppingCart = pShoppingCart;
    }

}
