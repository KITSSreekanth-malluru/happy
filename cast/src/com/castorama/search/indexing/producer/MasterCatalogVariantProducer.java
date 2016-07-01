package com.castorama.search.indexing.producer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import atg.commerce.search.producer.CustomCatalogVariantProducer;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;

public class MasterCatalogVariantProducer extends CustomCatalogVariantProducer {

    /** masterCatalog repository id constant. */
    private static final String MASTER_CATALOG_REPOSITORY_ID = "masterCatalog";

    @Override
    protected Collection getAncestorCatalogs(Context pContext, RepositoryItem pItem, Map pUniqueParams) {
        Collection<?> catalogs = super.getAncestorCatalogs(pContext, pItem, pUniqueParams);
        Collection<RepositoryItem> filteredCatalogs = new HashSet<RepositoryItem>(1);
        if (catalogs != null && !catalogs.isEmpty()) {
            Iterator<?> catalogsIter = catalogs.iterator();
            while (catalogsIter.hasNext()) {
                Object catalog = catalogsIter.next();
                if (catalog instanceof RepositoryItem && ((RepositoryItem) catalog).getRepositoryId().equals(MASTER_CATALOG_REPOSITORY_ID)) {
                    filteredCatalogs.add((RepositoryItem) catalog);
                    return filteredCatalogs;
                }
            }
        }
        return catalogs;
    }
}
