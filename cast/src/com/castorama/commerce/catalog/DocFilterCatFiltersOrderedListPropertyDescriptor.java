package com.castorama.commerce.catalog;

import java.util.LinkedHashMap;
import java.util.Map;

import atg.nucleus.Nucleus;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * 
 * @author
 */
public class DocFilterCatFiltersOrderedListPropertyDescriptor extends RepositoryPropertyDescriptor {

    private static final long serialVersionUID = -5025140173797869799L;
    
    private static final String PRODUCT_CATALOG_PATH = "/atg/commerce/catalog/ProductCatalog";
    private static final String CASTO_DOC_FILTER_TAG = "castoDocFilterTag";
    private static final String FILTER_TAG_CAT = "tagCategory";
    private static final String FILTER_TAG_CAT_NUM = "tagNumber";
    
    private static final String FILTER_TAG_CAT_NUM_DEFAULT = "10000";
    
    
    @Override
    public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
        Map<String, RepositoryItem> filtersMap = new LinkedHashMap<String, RepositoryItem>();
        
        String filterTagCatId = pItem.getRepositoryId();
        if (filterTagCatId.contains(":")) {
            filterTagCatId = filterTagCatId.substring(0, filterTagCatId.indexOf(':'));
        }
        
        Repository productCatalog = (Repository) Nucleus.getGlobalNucleus().resolveName(PRODUCT_CATALOG_PATH);
        
        RepositoryItem[] filters = null;
        try {
            RepositoryView filtersView = productCatalog.getView(CASTO_DOC_FILTER_TAG);
            RqlStatement filtersStatement = RqlStatement.parseRqlStatement(FILTER_TAG_CAT + " = ?0 ORDER BY " + FILTER_TAG_CAT_NUM);
            filters = filtersStatement.executeQuery(filtersView, new Object[] { filterTagCatId });
        } catch (RepositoryException re) {
            re.printStackTrace();
        }
        
        if (filters != null) {
            for (RepositoryItem filter : filters) {
                Integer orderNum = (Integer) filter.getPropertyValue(FILTER_TAG_CAT_NUM);
                String key = FILTER_TAG_CAT_NUM_DEFAULT;
                if (orderNum != null) {
                    key = orderNum.toString();
                }
                
                String tempKey = key;
                int keyPostfix = 0;
                while (filtersMap.containsKey(tempKey)) {
                    tempKey = key + ":" + ++keyPostfix;
                }
                
                filtersMap.put(tempKey, filter);
            }
        }
         
        return filtersMap;
    }
    
    @Override
    public boolean isCacheable() {
        return false;
    }
    
    @Override
    public boolean isWritable() {
        return false;
    }
    
    @Override
    public boolean isQueryable() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Class getPropertyType() {
        return Map.class;
    }

}
