package com.castorama.seo;

/**
 * Interface describing a manager for providing 
 * category id, if any, for an URL. Subclasses should provide own
 * logic for category id retrieval and caching.
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBE
 */
public interface LegacyURLManager {
    /** uri property. */
    String PROPERTY_URI = "uri";

    /** legacyURL property. */
    String ITEM_LEGACY_URL = "legacyURL";
    
    /** attachedTo property. */
    String PROPERTY_ATTACHED_TO = "attachedTo";

    /** RQL for find category by URL */
    String GET_LEGACY_LINK_BY_URL_RQL = "uri EQUALS IGNORECASE ?0";
    
    /**
     * Returns id of category mapped to the passed URL, if any.
     *
     * @param  pURL parameter to set.
     *
     * @return categoryForURL property.
     */
    String getCategoryForURL(String pURL);
}
