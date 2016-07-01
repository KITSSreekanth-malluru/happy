package com.castorama.search.adapter.loader.command;

import atg.search.adapter.content.ContentItem;
import atg.search.adapter.loader.command.AddContentItem;

import atg.search.routing.command.SearchEngineRequest;

import com.castorama.repository.search.indexing.CastIndexDocument;

/**
 * Castorama document content item representation
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastAddContentItem extends AddContentItem {
    /** COMMAND_NAME constant. */
    public static final String COMMAND_NAME = "CastIndexDocument";

    /**
     * Constructor.
     */
    public CastAddContentItem() {
        super(null);
    }

    /**
     * Constructor
     *
     * @param pContentItem parameter
     */
    public CastAddContentItem(ContentItem pContentItem) {
        super(pContentItem);
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
     * Returns specificSearchEngineRequest property.
     *
     * @return specificSearchEngineRequest property.
     */
    public CastIndexDocument getSpecificSearchEngineRequest() {
        return (CastIndexDocument) getSearchEngineRequest();
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.search.adapter.loader.command.AddContentItem#createSearchEngineRequest()
     */
    @Override protected SearchEngineRequest createSearchEngineRequest() {
        CastIndexDocument castIndexDocument = new CastIndexDocument();
        return castIndexDocument;
    }

}
