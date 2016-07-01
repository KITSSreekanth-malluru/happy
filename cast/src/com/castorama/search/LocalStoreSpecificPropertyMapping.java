package com.castorama.search;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.nucleus.logging.LoggingSupport;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

public class LocalStoreSpecificPropertyMapping extends LoggingSupport {
    private static final String DELIMITER = ",";

    private String mLocalStoreSpecificProperty;
    private String mLocalStoreSpecificPropertyPrefix;

    private List<String> mStores;
    private String mDefaultStoreId;

    public String[] getLocalStoreSpecificPropertyMapping() {
        StringBuffer buf = new StringBuffer();
        List<String> storeMapping = new ArrayList<String>();

        buf.append(mLocalStoreSpecificProperty);
        RepositoryItem currentProfile = ServletUtil.getCurrentUserProfile();

        StringBuffer propertyMapping = getPropertyMapping(currentProfile);
        if (StringUtils.isEmpty(propertyMapping.toString()) && !StringUtils.isEmpty(mDefaultStoreId)) {
            propertyMapping.append(DELIMITER)
                    .append(mLocalStoreSpecificPropertyPrefix)
                    .append(mDefaultStoreId);
        }

        buf.append(propertyMapping);

        if (isLoggingDebug()) {
            logDebug(mLocalStoreSpecificProperty + " property mapping is: " + buf.toString());
        }

        storeMapping.add(buf.toString());

        return (String[]) storeMapping.toArray(new String[storeMapping.size()]);
    }

    private StringBuffer getPropertyMapping(RepositoryItem pCurrentProfile) {
        StringBuffer buf = new StringBuffer();
        List<String> stores = getStores();
      
        if (stores != null) {
            for (String store : stores) {
                String propName = (String) store;
                try {
                    if (pCurrentProfile != null
                            && pCurrentProfile.getItemDescriptor().hasProperty(
                                    propName)) {
                        RepositoryItem storeItem = (RepositoryItem) pCurrentProfile
                                .getPropertyValue(propName);
                        if (storeItem != null) {
                            String storeId = storeItem.getRepositoryId();
                            buf.append(DELIMITER);
                            buf.append(mLocalStoreSpecificPropertyPrefix).append(
                                    storeId);
                        } else {
                            if (isLoggingDebug()) {
                                logDebug("Cannot retrieve store for profile: "
                                        + pCurrentProfile.getRepositoryId());
                            }
                        }
                    }
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError("Cannot retrieve property", e);
                    }
                }
            }
        }

        return buf;
    }

    public String getLocalStoreSpecificProperty() {
        return mLocalStoreSpecificProperty;
    }

    public void setLocalStoreSpecificProperty(String pLocalStoreSpecificProperty) {
        mLocalStoreSpecificProperty = pLocalStoreSpecificProperty;
    }

    public String getLocalStoreSpecificPropertyPrefix() {
        return mLocalStoreSpecificPropertyPrefix;
    }

    public void setLocalStoreSpecificPropertyPrefix(String pLocalStoreSpecificPropertyPrefix) {
        mLocalStoreSpecificPropertyPrefix = pLocalStoreSpecificPropertyPrefix;
    }

    public List<String> getStores() {
        return mStores;
    }

    public void setStores(List<String> pStores) {
        mStores = pStores;
    }

    public String getDefaultStoreId() {
        return mDefaultStoreId;
    }

    public void setDefaultStoreId(String pDefaultStoreId) {
        mDefaultStoreId = pDefaultStoreId;
    }
}
