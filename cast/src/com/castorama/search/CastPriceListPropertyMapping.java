/**
 * 
 */
package com.castorama.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import atg.commerce.search.PriceListPropertyMapping;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

/**
 * @author MK
 *
 */
public class CastPriceListPropertyMapping extends PriceListPropertyMapping {

    /**
     * Creates pricelist mapping
     *
     * @return price list mapping
     */
    public String [] getPriceMapping() {
        StringBuffer sb = new StringBuffer();
        List tmpMapping = new ArrayList();
        sb.append(getPrice());
        RepositoryItem profile = ServletUtil.getCurrentUserProfile();
        StringBuffer propertyMapping = calculatePropertyMapping(profile);
        //use default price list if mapping is empty
        if(StringUtils.isEmpty(propertyMapping.toString())) {
          propertyMapping.append(DELIMITER).append(getPricePropertyPrefix()).append(getDefaultPriceListId());
        }
        sb.append(propertyMapping);
        if(isLoggingDebug()) {
          logDebug("Price property mapping is: " + sb.toString());
        }
        tmpMapping.add(sb.toString());
        String[] mapping = (String []) tmpMapping.toArray( new String[tmpMapping.size()] );
        return mapping;
      }
    
    
    /**
     * Iterates over the list of priceLists and append price ids to the buffer according
     * castorama context
     * 
     * @param pCurrentProfile repository item
     * @return String buffer with properties
     */
    private StringBuffer calculatePropertyMapping(RepositoryItem pCurrentProfile) {
        StringBuffer sb = new StringBuffer();
        List priceLists = getPriceLists();
        String storeId = null;
        try {
            if (pCurrentProfile != null && pCurrentProfile.getItemDescriptor().hasProperty("currentLocalStore")) {
                RepositoryItem userStore = (RepositoryItem) pCurrentProfile.getPropertyValue("currentLocalStore");
                if (userStore != null) {
                    storeId = userStore.getRepositoryId();
                } else {
                    if (isLoggingDebug()) {
                        logDebug("Cannot retrieve currentLocalStore for profile: " + pCurrentProfile.getRepositoryId());
                    }
                }
            }
        } catch (RepositoryException e1) {
            if (isLoggingError()) {
                logError("Cannot retrieve property", e1);
            }
        }

        if (priceLists != null) {
            for (Iterator iter = priceLists.iterator(); iter.hasNext();) {
                String propName = (String) iter.next();

                try {
                    if (pCurrentProfile != null && pCurrentProfile.getItemDescriptor().hasProperty(propName)) {
                        RepositoryItem priceList = (RepositoryItem) pCurrentProfile.getPropertyValue(propName);
                        if (priceList != null) {
                            String priceId = priceList.getRepositoryId();
                            boolean isWebPriceInWebMode = "999".equals(storeId) && ("salePrices".equals(priceId) || "listPrices".equals(priceId));
                            boolean isNotSalePriceInLocalMode = !"999".equals(storeId) && !priceId.equals("salePrices");
                            if (isWebPriceInWebMode || isNotSalePriceInLocalMode) {
                                sb.append(DELIMITER);
                                sb.append(getPricePropertyPrefix()).append(priceId);
                            }
                        } else {
                            if (isLoggingDebug()) {
                                logDebug("Cannot retrieve price list for profile: " + pCurrentProfile.getRepositoryId());
                            }
                            sb.append("");
                        }
                    }
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError("Cannot retrieve property", e);
                    }
                }
            }
        }

        return sb;
    }
}
