/**
 * 
 */
package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author EPAM team
 * 
 */
public class CastGUIMenuLookup extends DynamoServlet {

    /** OUTPUT constant. */
    public static final String  OUTPUT        = "output";

    /** EMPTY constant. */
    public static final String  EMPTY         = "empty";

    /** MENU_ID constant. */
    public static final String  MENU_ID       = "menuId";

    /** MENU_ID constant. */
    public static final String  GUIMENU       = "guiMenu";


    /** catalogRepository property. */
    private Repository          mCatalogRepository;

    /**
     * 
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws ServletException exception
     * @throws IOException exception
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        String menuId = pRequest.getParameter(MENU_ID);
        if (menuId == null || menuId.length() == 0)
            return;

        try {
            MutableRepository mutableRepos = (MutableRepository) getCatalogRepository();

            // readAllChapterThematiques from repository
            // RepositoryView viewChapter =
            // mutableRepos.getView(CHAPTER_THEMATIQUE);
            RepositoryItem tempMenu = mutableRepos.getItem(menuId, GUIMENU);
            pRequest.setParameter("menu", tempMenu);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns catalogRepository property.
     * 
     * @return catalogRepository property.
     */
    public Repository getCatalogRepository() {
        return mCatalogRepository;
    }

    /**
     * Sets the value of the catalogRepository property.
     * 
     * @param pCatalogRepository parameter to set.
     */
    public void setCatalogRepository(Repository pCatalogRepository) {
        mCatalogRepository = pCatalogRepository;
    }

}
