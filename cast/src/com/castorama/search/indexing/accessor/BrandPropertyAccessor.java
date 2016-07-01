package com.castorama.search.indexing.accessor;

import java.util.List;

import atg.core.util.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

/**
 * Brand property accessor : get first not-null brand from : product or any of
 * its skus.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class BrandPropertyAccessor extends PropertyAccessorImpl {
    /** MarqueCommerciale constant */
    public static final String MARQUE_COMMERCIALE = "MarqueCommerciale";

    /** marque constant */
    public static final String MARQUE = "marque";

    /** childSkus constant */
    public static final String CHILD_SKUS = "childSkus";

    /** product constant */
    public static final String PRODUCT = "product";

    /** type constant */
    public static final String TYPE = "type";

    /** name constant */
    public static final String NAME = "name";

    /** casto-grouped-product constant */
    public static final String CASTO_GROUPED_PRODUCT = "casto-grouped-product";

    /** casto_product constant */
    public static final String CASTO_PRODUCT = "casto_product";

    /** casto_sku constant */
    public static final String CASTO_SKU = "casto_sku";

    /** product catalog repository property */
    private Repository mProductCatalogRepository;

    /** logging error property */
    private boolean mLoggingError = true;

    /**
     * Returns productCatalogRepository property
     *
     * @return the productCatalogRepository
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * Sets productCatalogRepository property
     *
     * @param pProductCatalogRepository the productCatalogRepository to set
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }

    /**
     * Sets loggingError property
     *
     * @param pLoggingError property to set
     */
    public void setLoggingError(boolean pLoggingError) {
        mLoggingError = pLoggingError;
    }

    /**
     * Returns loggingError property
     *
     * @return loggingError property
     */
    public boolean isLoggingError() {
        return mLoggingError;
    }

    /**
     * Method for setting correct "brand" property for indexing into the *.xhtml
     * files for products. Gets product's marqueCommerciale, if product's
     * marqueCommerciale is empty, iterates through product's sku and sets first
     * not-null sku's marqueCommerciale.
     *
     * @param  pContext      context parameter
     * @param  pItem         product item.
     * @param  pPropertyName property name of pItem for indexing from IOC.
     * @param  pType         type of property(PropertyTypeEnum).
     *
     * @return true if item isn't "immediate withdrawal".
     */
    protected Object getTextOrMetaPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName,
                                                PropertyTypeEnum pType) {
        if ((getProductCatalogRepository() != null) && (pItem != null)) {
            String brandId = null;
            String brandName = null;
            RepositoryItem brandRepItem = null;
            try {
                if (CASTO_PRODUCT.equalsIgnoreCase((String) pItem.getPropertyValue(TYPE))) {
                    brandId = (String) pItem.getPropertyValue(MARQUE_COMMERCIALE);
                    if (!StringUtils.isBlank(brandId)) {
                        brandRepItem = getProductCatalogRepository().getItem(brandId, MARQUE);
                        if (brandRepItem != null) {
                            brandName = (String) brandRepItem.getPropertyValue(NAME);
                            if (!StringUtils.isBlank(brandName)) {
                                return brandName;
                            }
                        }
                    }

                }
                List<RepositoryItem> childSkus = (List<RepositoryItem>) pItem.getPropertyValue(CHILD_SKUS);
                if ((childSkus != null) && !childSkus.isEmpty()) {
                    for (RepositoryItem skuRepItem : childSkus) {
                        if ((skuRepItem != null) &&
                                CASTO_SKU.equalsIgnoreCase((String) skuRepItem.getPropertyValue(TYPE))) {
                            brandId = (String) skuRepItem.getPropertyValue(MARQUE_COMMERCIALE);
                            if (!StringUtils.isBlank(brandId)) {
                                brandRepItem = getProductCatalogRepository().getItem(brandId, MARQUE);
                                if (brandRepItem != null) {
                                    brandName = (String) brandRepItem.getPropertyValue(NAME);
                                    if (!StringUtils.isBlank(brandName)) {
                                        return brandName;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (RepositoryException e) {
                if (isLoggingError() && (pContext != null)) {
                    pContext.getIndexingOutputConfig().logError("Error is caused by retrieving of property's value - " +
                                                                pPropertyName + " : " + e.getMessage());
                }
            }  // end try-catch

        }  // end if
        return null;
    }
}
