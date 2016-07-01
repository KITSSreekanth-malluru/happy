package atg.repository.servlet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import atg.adapter.gsa.ChangeAwareList;

import atg.commerce.catalog.CatalogTools;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.castorama.commerce.catalog.CastCatalogTools;

/**
 * A subclass of CatalogNavHistory that is to be used with the castorama catalog
 * or other hierarchical repository.
 *
 * @author Vasili_Ivus
 */
public class CastCatalogNavHistory extends NavHistory {
    private static final String NAV_ACTION_JUMP = "jump";

    private static final String NAV_ACTION_POP = "pop";

    /** startJumpCategoryId property. */
    private String mStartJumpCategoryId;

    /** catalogTools property. */
    private CatalogTools mCatalogTools;

    /**
     * Sets the value of the catalogTools property.
     *
     * @param pCatalogTools parameter to set.
     */
    public void setCatalogTools(CatalogTools pCatalogTools) {
        mCatalogTools = pCatalogTools;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Returns startJumpCategoryId property.
     *
     * @return startJumpCategoryId property.
     */
    public String getStartJumpCategoryId() {
        return mStartJumpCategoryId;
    }

    /**
     * Sets the value of the startJumpCategoryId property.
     *
     * @param pStartJumpCategoryId parameter to set.
     */
    public void setStartJumpCategoryId(String pStartJumpCategoryId) {
        mStartJumpCategoryId = pStartJumpCategoryId;
    }

    /**
     *
     * @param  pItem      parameter
     * @param  pNavAction parameter
     *
     * @return true if item was placed to navigation history
     */
    public boolean navigate(Object pItem, String pNavAction) {
        boolean filled = false;

        if ((pNavAction != null) && pNavAction.equals(NAV_ACTION_JUMP)) {
            resetHistory();
            if (pItem instanceof RepositoryItem) {
                RepositoryItem ri = (RepositoryItem) pItem;
                if (isCategory(ri)) {
                    mStartJumpCategoryId = ri.getRepositoryId();
                } else {
                    mStartJumpCategoryId = null;
                }
            } else {
                mStartJumpCategoryId = null;
            }
        }

        filled = fillInNavHistory(pItem);

        if (!filled && (pNavAction != null) && pNavAction.equals(NAV_ACTION_POP)) {
            popTo(pItem);
        } else {
            push(pItem);
        }
        return true;
    }

    /**
     *
     * ToDo: DOCUMENT ME!
     *
     * @param  pItem parameter
     *
     * @return
     */
    public LinkedList getDefaultHistory(Object pItem) {
        LinkedList result = null;
        if (pItem instanceof RepositoryItem) {
            RepositoryItem item = (RepositoryItem) pItem;

            // determine mStartJumpCategoryId in case of document item(get first)
            if (isDocument(item)) {
                Set parents = (Set) item.getPropertyValue("categories");
                if ((parents != null) && !parents.isEmpty()) {
                    mStartJumpCategoryId = ((RepositoryItem) parents.iterator().next()).getRepositoryId();
                }
            }

            if ((mStartJumpCategoryId != null) && (isProduct(item) || isDocument(item))) {
                Set parents = null;
                if (isProduct(item)) {
                    parents = (Set) item.getPropertyValue("parentCategories");
                } else if (isDocument(item)) {
                    parents = (Set) item.getPropertyValue("categories");
                }

                for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
                    RepositoryItem parent = (RepositoryItem) iterator.next();
                    LinkedList<RepositoryItem> parentsList = getCatalogTools().getAncestors(parent);
                    parentsList.addLast(parent);
                    boolean containsJumpCategory = false;
                    for (int i = 0; i < parentsList.size(); i++) {
                        RepositoryItem candidate = parentsList.get(i);
                        if (mStartJumpCategoryId.equals(candidate.getRepositoryId())) {
                            containsJumpCategory = true;
                            break;
                        }
                    }
                    if (containsJumpCategory) {
                        result = parentsList;
                        break;
                    }
                }
                if ((result == null) && !isDocument(item)) {
                    result = getCatalogTools().getAncestors((RepositoryItem) pItem);
                }
            } else {
                if (!isDocument(item)) {
                    result = getCatalogTools().getAncestors((RepositoryItem) pItem);
                }

            }  // end if-else
        } else {
            result = super.getDefaultHistory(pItem);
        }  // end if-else

        if (pItem instanceof RepositoryItem) {
            for (Iterator it = result.iterator(); it.hasNext();) {
                RepositoryItem categoty = (RepositoryItem) it.next();
                if (((ChangeAwareList) categoty.getPropertyValue("ancestorcategories")).isEmpty()) {
                    it.remove();
                }
            }
            CastCatalogTools catalogTools = (CastCatalogTools) getCatalogTools();
            RepositoryItem catalogItem = catalogTools.getCastoramaCatalog();
            ChangeAwareList topNavigationCategories =
                (ChangeAwareList) catalogItem.getPropertyValue("topNavigationCategories");
            for (Iterator iterator = topNavigationCategories.iterator(); iterator.hasNext();) {
                RepositoryItem name = (RepositoryItem) iterator.next();
                int offset = result.indexOf(name);

                //check it is not the last one in the list
                if ((-1 < offset) && iterator.hasNext()) {
                    result = new LinkedList(result.subList(offset + 1, result.size()));
                    break;
                }
            }
            if (0 < result.size()) {
                for (Iterator it = result.iterator(); it.hasNext();) {
                    RepositoryItem name = (RepositoryItem) it.next();
                    Boolean pivot = (Boolean) name.getPropertyValue("pivot");
                    if (pivot.booleanValue()) {
                        int offset = result.indexOf(name);
                        if (-1 < offset) {
                            result = new LinkedList(result.subList(0, 1 + offset));
                            break;
                        }
                    }
                }
            }

        } else {
            return result;
        }  // end if-else

        return result;
    }

    /**
     * Check whether passed item is "product" item or no
     *
     * @param  item - item for check
     *
     * @return true if pItem is "product" item.
     */
    protected boolean isProduct(RepositoryItem item) {
        try {
            return item.getItemDescriptor().getItemDescriptorName().endsWith("product");
        } catch (RepositoryException e) {
            return false;
        }
    }

    /**
     * Check whether passed item is castoramaDocument item or no
     *
     * @param  pItem - item for check
     *
     * @return true if pItem is CastoramaDocument item.
     */
    protected boolean isDocument(RepositoryItem pItem) {
        boolean result = false;
        if (pItem != null) {
            try {
                result = pItem.getItemDescriptor().getItemDescriptorName().endsWith("Document");
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }
    
    /**
     * Check whether passed item is category item or no
     *
     * @param  pItem - item for check
     *
     * @return true if pItem is CastoramaDocument item.
     */
    protected boolean isCategory(RepositoryItem pItem) {
        boolean result = false;
        if (pItem != null) {
            try {
                result = pItem.getItemDescriptor().getItemDescriptorName().endsWith("category");
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }
}
