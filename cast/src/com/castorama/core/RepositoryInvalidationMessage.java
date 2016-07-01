package com.castorama.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 */
public class RepositoryInvalidationMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5638081348180408823L;
    /**
     * TODO: Document Me
     */

    private final Set<String> mItemIds = new HashSet<String>();
    private final String mRepositoryPath;
    private String mItemDescriptorName;

    /**
     * TODO: Document Me !!!
     */
    public RepositoryInvalidationMessage(String pRepositoryPath) {
        mRepositoryPath = pRepositoryPath;
    }

    /**
     * TODO: Document Me !!!
     */
    public RepositoryInvalidationMessage(String pRepositoryPath, String pItemDescriptorName, Set<String> pItemIds) {
        mRepositoryPath = pRepositoryPath;
        mItemDescriptorName = pItemDescriptorName;
        mItemIds.addAll(pItemIds);
    }

    /**
     * @return the repositoryPath
     */
    public String getRepositoryPath() {
        return mRepositoryPath;
    }

    /**
     * @return the itemDescriptorName
     */
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /**
     * @return the mItemIds
     */
    public Set<String> getItemIds() {
        return mItemIds;
    }
}
