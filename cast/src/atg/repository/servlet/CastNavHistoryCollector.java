package atg.repository.servlet;

import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extend existent NavHistoryCollector class for overriding service method
 *
 * @author Epam Team
 */
public class CastNavHistoryCollector extends NavHistoryCollector {
    /**
     * Override service method for correct breadcrumbs collecting.
     *
     * @param  pRequest  - dynamo http request
     * @param  pResponse - dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    
    /** enableDedfaultParentsToBreadcrumbs property. */
    private boolean mEnableDedfaultParentsToBreadcrumbs = false;
    

    @Override public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                           throws ServletException, IOException {
        String navCountString = pRequest.getParameter(NAV_COUNT);
        String itemName = pRequest.getParameter(ITEM_NAME);
        Object itemObject = pRequest.getObjectParameter(ITEM);
        NavHistory navHistory = (NavHistory) pRequest.resolveName(mNavHistoryPath);

        if (navHistory == null) {
            return;
        }

        if ((itemName == null) && (itemObject == null)) {
            return;
        }

        Object item = null;
        if (itemName != null) {
            Object fUri = pRequest.getAttribute("javax.servlet.forward.request_uri");
            if (fUri != null){
                item = new NavHistoryItem(itemName, (String)fUri);
            } else {
                item = new NavHistoryItem(itemName, pRequest.getRequestURI());
            }
        } else {
            item = itemObject;
        }

        String disableGJ = pRequest.getParameter("disableGJ");
        if (!StringUtils.isBlank(navCountString)) {
            int count = 0;
            try {
                count = Integer.parseInt(navCountString);
            } catch (Exception e) {
                // TODO: handle exception
            }
            RepositoryItem toAdd = null;
            LinkedList history = null;
            if ((navHistory.getNavCount() - count) == 1) {
                history = navHistory.getNavHistory();
                if (history.size() > 0) {
                    Object last = history.getLast();
                    if ((item instanceof RepositoryItem) && (last instanceof RepositoryItem)) {
                        RepositoryItem current = (RepositoryItem) last;
                        toAdd = (RepositoryItem) item;
                        if (current.getRepositoryId().equals(toAdd.getRepositoryId())) {
                            return;
                        }
                    }
                }
            }
            
            if (disableGJ == null || !disableGJ.equalsIgnoreCase("true") || toAdd == null || history == null) {
                navHistory.checkBackButton(Integer.toString(count));
            } else {
                history.set(history.size()-1, toAdd);
                navHistory.setNavHistory(history);
            }
        }  // end if-else
        String action = pRequest.getParameter(NAV_ACTION);
        
        
        if (mEnableDedfaultParentsToBreadcrumbs && (disableGJ == null || !disableGJ.equalsIgnoreCase("true"))){
            action="jump";
        } else {
            if(action==null) action="jump";
        }

        if ("push".equals(action) && (itemObject instanceof RepositoryItem)) {
            RepositoryItem ri = (RepositoryItem) itemObject;
            try {
                if (ri.getItemDescriptor().getItemDescriptorName().endsWith("product") ||
                        ri.getItemDescriptor().getItemDescriptorName().endsWith("Document")) {
                    LinkedList history = navHistory.getNavHistory();
                    LinkedList categories = new LinkedList();
                    for (Iterator it = history.iterator(); it.hasNext();) {
                        Object nameObj = it.next();
                        if (nameObj instanceof NavHistoryItem) {
                            break;
                        }
                        RepositoryItem name = (RepositoryItem) nameObj;
                        if (name.getItemDescriptor().getItemDescriptorName().endsWith("category")) {
                            categories.add(name);
                        }
                    }
                    if (categories.size() > 0) {
                        navHistory.navigate(categories.get(0), "pop");
                        for (int i = 1; i < categories.size(); i++) {
                            navHistory.navigate(categories.get(i), "push");
                        }
                    }
                }
            } catch (Exception e) {
                logError(e);
            }  // end try-catch

        }  // end if
        navHistory.navigate(item, action);
    }


    /**
     * Returns enableDedfaultParentsToBreadcrumbs property.
     *
     * @return enableDedfaultParentsToBreadcrumbs property.
     */
    public boolean isEnableDedfaultParentsToBreadcrumbs() {
        return mEnableDedfaultParentsToBreadcrumbs;
    }

    /**
     * Sets the value of the enableDedfaultParentsToBreadcrumbs property.
     *
     * @param pEnableDedfaultParentsToBreadcrumbs parameter to set.
     */
    public void setEnableDedfaultParentsToBreadcrumbs(
            boolean pEnableDedfaultParentsToBreadcrumbs) {
        mEnableDedfaultParentsToBreadcrumbs = pEnableDedfaultParentsToBreadcrumbs;
    }
}
