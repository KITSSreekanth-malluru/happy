package com.castorama.searchadmin.adapter.content.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import atg.nucleus.GenericService;

import atg.search.adapter.content.ContentItemAdapter;
import atg.search.adapter.content.ContentItemSource;
import atg.search.adapter.exception.ContentItemSourceNotFoundException;

/**
 * Definition of CastoramaDocumentAdapter
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaDocumentAdapter extends GenericService implements ContentItemAdapter {
    /** CASTORAMA_DOCUMENT_ADAPTER_NAME constant. */
    public static final String CASTORAMA_DOCUMENT_ADAPTER_NAME = "CastoramaDocumentAdapter";

    /** SOURCE_ID constant. */
    public static final String SOURCE_ID = "document_source_item";

    /** CONTEXT_PATH constant. */
    public static final String CONTEXT_PATH =
        "/com/castorama/searchadmin/adapter/content/impl/CastoramaDocumentAdapter";

    /** contentItemSourceMap property */
    HashMap mContentItemSourceMap = new HashMap();

    /** configuration property */
    private String mConfiguration;

    /**
     * Constructor
     */
    public CastoramaDocumentAdapter() {
    }

    /**
     * Returns descriptiveName property.
     *
     * @return descriptiveName property.
     */
    public String getDescriptiveName() {
        return CASTORAMA_DOCUMENT_ADAPTER_NAME;
    }

    /**
     * Method for cleaning some properties before delition
     *
     * @param contentSourceId parameter
     */
    public void cleanupBeforeDeletion(String contentSourceId) {
    }

    /**
     * Returns list of ids for content item sources
     *
     * @return list of ids for content item sources
     */
    public List enumerateContentItemSources() {
        initContentItemSourceMap();
        return new ArrayList(mContentItemSourceMap.keySet());
    }

    /**
     * Returns ContentItemSource by pId
     *
     * @param  pId parameter for retriving ContentItemSource
     *
     * @return content item source
     *
     * @throws ContentItemSourceNotFoundException ToDo: DOCUMENT ME!
     */
    public ContentItemSource getContentItemSource(String pId) throws ContentItemSourceNotFoundException {
        initContentItemSourceMap();
        if (!mContentItemSourceMap.containsKey(pId)) {
            throw new ContentItemSourceNotFoundException("No ContentItemSource for Id:" + pId);
        }
        return (ContentItemSource) mContentItemSourceMap.get(pId);
    }

    /**
     * Returns includeAllSources property.
     *
     * @return includeAllSources property.
     */
    public boolean isIncludeAllSources() {
        return true;
    }

    /**
     * Returns configuration property.
     *
     * @return configuration property.
     */
    public String getConfiguration() {
        return mConfiguration;
    }

    /**
     * Sets the value of the configuration property.
     *
     * @param pConfiguration parameter to set.
     */
    public void setConfiguration(String pConfiguration) {
        mConfiguration = pConfiguration;
    }

    /**
     * Returns contextPath property.
     *
     * @return contextPath property.
     */
    public String getContextPath() {
        return CONTEXT_PATH;
    }

    /**
     * Init map of ContentItemSource objects
     */
    private synchronized void initContentItemSourceMap() {
        Properties props = null;
        if (getConfiguration() != null) {
            props = new Properties();
            try {
                props.load(new ByteArrayInputStream(getConfiguration().getBytes()));
            } catch (IOException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
        }
        if (mContentItemSourceMap == null) {
            mContentItemSourceMap = new HashMap();
        }
        CastoramaDocumentSource source = new CastoramaDocumentSource(props);
        mContentItemSourceMap.put(SOURCE_ID, source);

    }

}
