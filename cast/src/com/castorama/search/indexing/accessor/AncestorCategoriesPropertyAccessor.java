/**
 * 
 */
package com.castorama.search.indexing.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;

/**
 * @author Mikalai_Khatsko
 * 
 */
public class AncestorCategoriesPropertyAccessor extends PropertyAccessorImpl {

    @Override
    public Object getSubItemPropertyValue(Context pContext, RepositoryItem pItem, String pPropertyName) {

        List<RepositoryItem> result = new ArrayList<RepositoryItem>();
        List<RepositoryItem> ancestorCategories = (List<RepositoryItem>) pItem.getPropertyValue("ancestorCategories");
        for (RepositoryItem ancCat : ancestorCategories) {
            Set<RepositoryItem> computedCatalogs = (Set<RepositoryItem>) ancCat.getPropertyValue("computedCatalogs");
            if (computedCatalogs != null && (computedCatalogs.size() == 1) 
                    && ("masterCatalog".equals(((RepositoryItem)computedCatalogs.toArray()[0]).getRepositoryId()))) {
                result.add(ancCat);
            }
        }
        return result;
    }

}
