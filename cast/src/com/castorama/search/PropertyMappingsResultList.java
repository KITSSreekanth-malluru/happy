package com.castorama.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import atg.commerce.search.PriceListPropertyMapping;

public class PropertyMappingsResultList {
    private LocalStoreSpecificPropertyMapping[] mLocalStoreSpecificPropertyMappings;
    private PriceListPropertyMapping mPriceListPropertyMapping;

    public LocalStoreSpecificPropertyMapping[] getLocalStoreSpecificPropertyMappings() {
        return mLocalStoreSpecificPropertyMappings;
    }

    public void setLocalStoreSpecificPropertyMappings(LocalStoreSpecificPropertyMapping[] pLocalStoreSpecificPropertyMappings) {
        mLocalStoreSpecificPropertyMappings = pLocalStoreSpecificPropertyMappings;
    }

    public PriceListPropertyMapping getPriceListPropertyMapping() {
        return mPriceListPropertyMapping;
    }

    public void setPriceListPropertyMapping(
            PriceListPropertyMapping pPriceListPropertyMapping) {
        mPriceListPropertyMapping = pPriceListPropertyMapping;
    }

    public String[] getPropertyMappings() {
        List<String> propertyMappings = new ArrayList<String>();
        
        for (LocalStoreSpecificPropertyMapping mapping : mLocalStoreSpecificPropertyMappings) {
            String[] propertyMapping = mapping.getLocalStoreSpecificPropertyMapping();
            if (propertyMapping != null && propertyMapping.length > 0) {
                propertyMappings.add(StringUtils.join(propertyMapping, ","));
            }
        }
        
        String[] priceMapping = mPriceListPropertyMapping.getPriceMapping();
        if (priceMapping != null && priceMapping.length > 0) {
            propertyMappings.add(StringUtils.join(priceMapping, ","));
        }
        
        return propertyMappings.toArray(new String[propertyMappings.size()]);
    }
}
