package com.castorama.search.adapter.loader.command;

import atg.search.adapter.content.ContentItem;
import atg.search.adapter.loader.command.UpdateContentItem;

import atg.search.routing.command.SearchEngineRequest;

import com.castorama.repository.search.indexing.CastUpdateDocument;

/**
 * Castorama document content item representation
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastUpdateContentItem extends UpdateContentItem {
    /** COMMAND_NAME constant */
    public static final String COMMAND_NAME = "CastUpdateContentItem";

    /**
     * Creates a new CastUpdateContentItem object.
     *
     * @param contentItem parameter
     */
    public CastUpdateContentItem(ContentItem contentItem) {
        super(contentItem);
    }

    /**
     * Returns commandName property.
     *
     * @return commandName property.
     */
    public String getCommandName() {
        return COMMAND_NAME;
    }

    /**
     * Returns specific search engine request
     *
     * @return specific search engine request
     */
    public CastUpdateDocument getSpecificSearchEngineRequest() {
        return (CastUpdateDocument) getSearchEngineRequest();
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.adapter.loader.command.UpdateContentItem#createSearchEngineRequest()
     */
    @Override protected SearchEngineRequest createSearchEngineRequest() {
        CastUpdateDocument castUpdateDocument = new CastUpdateDocument();
        return castUpdateDocument;
    }

}
