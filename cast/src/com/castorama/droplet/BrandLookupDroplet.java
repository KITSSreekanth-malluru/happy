package com.castorama.droplet;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Retrieves brand for current product.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class BrandLookupDroplet extends DynamoServlet {
    /** BRAND constant. */
    public static final String BRAND = "brand";

    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    private static final ParameterName ERROR = ParameterName.getParameterName("error");

    /** EMPTY constant. */
    private static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** MARQUE_COMMERCIALE constant. */
    public static final String MARQUE_COMMERCIALE = "MarqueCommerciale";

    /** MARQUE constant. */
    public static final String MARQUE = "marque";

    /** CHILD_SKUS constant. */
    public static final String CHILD_SKUS = "childSkus";

    /** PRODUCT constant. */
    public static final String PRODUCT = "product";

    /** TYPE constant. */
    public static final String TYPE = "type";

    /** CASTO_GROUPED_PRODUCT constant. */
    public static final String CASTO_GROUPED_PRODUCT = "casto-grouped-product";

    /** CASTO_PRODUCT constant. */
    public static final String CASTO_PRODUCT = "casto_product";

    /** CASTO_SKU constant. */
    public static final String CASTO_SKU = "casto_sku";

    /** productCatalogRepository property */
    private Repository mProductCatalogRepository;

    /**
     * Returns productCatalogRepository property.
     *
     * @return productCatalogRepository property.
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * Sets the value of the productCatalogRepository property.
     *
     * @param pProductCatalogRepository parameter to set.
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }

    /**
     * Retrieves brand for current product. Get first not-null brand: from
     * product or from product's skus
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException - exception
     * @throws IOException      - exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        RepositoryItem product = (atg.repository.RepositoryItemImpl) pRequest.getObjectParameter(PRODUCT);
        RepositoryItem brand = null;
        try {
            if ((getProductCatalogRepository() != null) && (product != null)) {
                String brandId = null;
                if (CASTO_PRODUCT.equalsIgnoreCase((String) product.getPropertyValue(TYPE))) {
                    brandId = (String) product.getPropertyValue(MARQUE_COMMERCIALE);

                    if (!StringUtils.isBlank(brandId)) {
                        brand = getProductCatalogRepository().getItem(brandId, MARQUE);
                    }
                }

                if (brand == null) {
                    List<RepositoryItem> childSkus = (List<RepositoryItem>) product.getPropertyValue(CHILD_SKUS);
                    if ((childSkus != null) && !childSkus.isEmpty()) {
                        for (RepositoryItem skuRepItem : childSkus) {
                            if ((skuRepItem != null) &&
                                    CASTO_SKU.equalsIgnoreCase((String) skuRepItem.getPropertyValue("type"))) {
                                brandId = (String) skuRepItem.getPropertyValue(MARQUE_COMMERCIALE);
                                if (!StringUtils.isBlank(brandId)) {
                                    brand = getProductCatalogRepository().getItem(brandId, MARQUE);
                                    if (brand != null) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

            }  // end if

        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e.getMessage());
            }
            pRequest.serviceLocalParameter(ERROR, pRequest, pResponse);
        }  // end try-catch
        if (brand != null) {
            pRequest.setParameter(BRAND, brand);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
        }
    }
}
