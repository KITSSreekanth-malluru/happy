/**
 *
 */
package com.castorama.droplet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userdirectory.droplet.Constants;
import atg.userdirectory.droplet.Utils;

/**
 * Droplet that returns map with indexed items. Where Key is index (are got from
 * {@link #getAlphaMap()}), value - list of items corresponding to this index.
 * <dt>Input params:
 * <dd>
 * <li><code>indexedItem</code> - view name from repository {@link
 * #getProductCatalogRepository()} that should be indexed<br />
 *
 * <dt>Oparams:
 * <dd>
 * <li><code>output</code> if the map is not empty
 * <dd>
 * <li><code>empty</code> if the map is empty or if <code>indexedItem</code> is
 * not set
 * <dt>Output params:
 * <dd>
 * <li><code>indexedMap</code> maps with indexed items
 *
 * @author Katsiaryna Dmitrievich
 */
public class IndexedMapDroplet extends DynamoServlet implements Constants {
    /** Input param: items that should be indexed. */
    private static final ParameterName INDEXED_ITEM = ParameterName.getParameterName("indexedItem");

    /** Output parameter: map with indexed items. */
    private static final String INDEXED_MAP = "indexedMap";

    /** Brand view name. */
    private static final String MARQUE = "marque";

    /** Category view name. */
    private static final String CATEGORY = "category";

    /** RQL query for pivot categories. */
    private static final String PIVOT_TRUE = "pivot = true";

    /** Category display name property. */
    private static final String CATEGORY_NAME_PROP = "displayName";

    /** Brand name property. */
    private static final String BRAND_NAME_PROP = "name";

    /** RQL Query for ordering. */
    private static final String ORDER_BY = "ORDER BY";

    /** OR property */
    private static final String OR = "OR";

    /** RQL search condition. */
    private static final String STARTS_WITH_IGNORECASE = "STARTS WITH IGNORECASE ?";

    /** AND property */
    private static final String AND = "AND";

    /** SPACE property */
    private static final String SPACE = " ";

    /** String with numbers. */
    private static final String NUMBERS = "0123456789";

    /** Product Catalog repository. */
    private Repository mProductCatalogRepository;

    /** alphaMap property */
    private Map<String, String> alphaMap;

    /**
     * Gets ProductCatalogRepository.
     *
     * @return the productCatalogRepository
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * Sets ProductCatalogRepository
     *
     * @param pProductCatalogRepository the productCatalogRepository to set
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }

    /**
     * Returns map with indexed items.
     *
     * @param  pRequest  the servlet's request
     * @param  pResponse the servlet's response
     *
     * @throws ServletException if servlet exception occures
     * @throws IOException      if servlet io exception occures
     */
    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String indexedItem = pRequest.getParameter(INDEXED_ITEM);
        if (isMissingParameters(indexedItem)) {
            pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
            return;
        }
        Map<String, List<RepositoryItem>> indexedList = getIndexedMap(indexedItem);
        if (null != indexedList) {
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
            pRequest.setParameter(INDEXED_MAP, indexedList);
        } else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
        }
    }

    /**
     * Searches all items starts with letters from <code>alpha</code> in
     * alphabetic order.
     *
     * @param  rqlConditions      pre RQL condition
     * @param  indexedElementName all items are indexed by this name
     * @param  repositoryViewName repository view of indexed items
     * @param  alpha              - first letters of indexed items.
     *
     * @return list of RepositoryItem - list of items that starts with letters
     *         from <code>alpha</code>.
     */
    private List<RepositoryItem> getItemsStartsWithAlphs(final String rqlConditions, final String indexedElementName,
                                                         final String repositoryViewName, final String alpha) {
        List<RepositoryItem> catList = null;

        StringBuffer query =
        	new StringBuffer(((rqlConditions != null) && (rqlConditions.trim().length() > 0)) ? (rqlConditions + SPACE + AND) : "");
        int lenght = alpha.length();
        Object[] params = new Object[lenght * 11];
        int k = 0;
        for (int i = 0; i < lenght; i++) {
            /*
             * i - alpha index
             * k - query index
             * j - numbers index
             */
        	query.append(SPACE).append(indexedElementName).append(SPACE + STARTS_WITH_IGNORECASE).append(k).append(SPACE + OR + SPACE);
            String curAlpha = Character.toString(alpha.charAt(i));
            params[k++] = curAlpha;

            /* If element starts with a number index it by the second letter. */
            for (int j = 0; j < NUMBERS.length(); j++) {
            	query.append(SPACE).append(indexedElementName).append(SPACE + STARTS_WITH_IGNORECASE).append(k);
                params[k++] = Character.toString(NUMBERS.charAt(j)) + curAlpha;
                if ((j + 1) != NUMBERS.length()) {
                	query.append(SPACE + OR + SPACE);
                }
            }
            if ((i + 1) != lenght) {
            	query.append(SPACE + OR + SPACE);
            }
        }  // end for

        query.append(SPACE + ORDER_BY + SPACE).append(indexedElementName);
        try {
            Repository productCatalogRepository = getProductCatalogRepository();
            RqlStatement findCat = RqlStatement.parseRqlStatement(query.toString());
            RepositoryView catView = productCatalogRepository.getView(repositoryViewName);
            RepositoryItem[] cats = findCat.executeQuery(catView, params);
            catList = (cats == null) ? null : Arrays.asList(cats);
        } catch (RepositoryException rpe) {
            logError(this.getClass() + ".service : ERROR ==> " + rpe.toString());
        }
        return catList;
    }

    /**
     * Returns indexed map. Key is index (are got from {@link #getAlphaMap()}),
     * value - list of items corresponding to this index.
     *
     * @param  itemsForIndexing name of items view that should be indexed
     *
     * @return indexedMap with indexed items.
     */
    private Map<String, List<RepositoryItem>> getIndexedMap(final String itemsForIndexing) {
        Map<String, List<RepositoryItem>> indexedMap = new HashMap<String, List<RepositoryItem>>();
        final Map<String, String> alphaMap = getAlphaMap();
        String rqlPreConditions = "";
        String indexedElementName = "";
        String repositoryViewName = "";
        if (itemsForIndexing.equalsIgnoreCase(MARQUE)) {
            indexedElementName = BRAND_NAME_PROP;
            repositoryViewName = MARQUE;
        } else if (itemsForIndexing.equalsIgnoreCase(CATEGORY)) {
            rqlPreConditions = PIVOT_TRUE;
            indexedElementName = CATEGORY_NAME_PROP;
            repositoryViewName = CATEGORY;
        } else {
            return null;
        }
       for (Map.Entry<String,String> entry : alphaMap.entrySet()) {
            indexedMap.put(entry.getKey(),
                           getItemsStartsWithAlphs(rqlPreConditions, indexedElementName, repositoryViewName, entry.getValue()));
        }

        return indexedMap;
    }

    /**
     * Gets alphabet maps.
     *
     * @return the alphaMap
     */
    public Map<String, String> getAlphaMap() {
        return alphaMap;
    }

    /**
     * Sets alphabet maps.
     *
     * @param alphaMap the alphaMap to set
     */
    public void setAlphaMap(Map<String, String> alphaMap) {
        this.alphaMap = alphaMap;
    }

    /**
     * This method will determine if there are any missing parameters passed to
     * this droplet. If there are then an error is logged and this method will
     * return true. This method will look for the following parameters:<br>
     *
     * <ul>
     * <li>indexedItem - this is the view name of item that should be indexed.
     * </ul>
     *
     * @param  indexedItem a <code>String</code> value
     *
     * @return a <code>boolean</code> value
     */
    protected boolean isMissingParameters(String indexedItem) {
        if (StringUtils.isBlank(indexedItem)) {
            if (isLoggingError()) {
                Object[] args = {INDEXED_ITEM.getName()};
                logError(Utils.fillInArgs(MISSING_REQUIRED_PARAM, args));
            }
            return true;
        }

        return false;
    }
}
