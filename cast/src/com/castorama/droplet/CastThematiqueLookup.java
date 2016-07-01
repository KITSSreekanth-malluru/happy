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
public class CastThematiqueLookup extends DynamoServlet {

    
    
    /** OUTPUT constant. */
    public static final String  OUTPUT             = "output";

    /** EMPTY constant. */
    public static final String  EMPTY              = "empty";

    /** REFERENCE_ID constant. */
    public static final String  REFERENCE_ID       = "referenceId";

    /** DOCUMENT_ID constant. */
    private static final String DOCUMENT_ID        = "documentId";

    /** PRODUCT_ID constant. */
    private static final String THEMATIQUE_ID         = "thematiqueId";

    /** THEMATIQUE constant. */
    private static final String THEMATIQUE         = "thematique";
    
    /** THEMATIQUE_CONTAINER constant. */
    private static final String THEMATIQUE_CONTAINER         = "thematiqueContainer";

    /** THEMATIQUE constant. */
    private static final String CHAPTER_THEMATIQUE = "chapterThematique";

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

        String themId = pRequest.getParameter(THEMATIQUE_ID);
        if (themId == null || themId.length() == 0) return;
        
        try {
            MutableRepository mutableRepos = (MutableRepository) getCatalogRepository();

            // readAllChapterThematiques from repository
//            RepositoryView viewChapter = mutableRepos.getView(CHAPTER_THEMATIQUE);
            RepositoryItem tempThematique = mutableRepos.getItem(themId, THEMATIQUE);
            pRequest.setParameter("thematique", tempThematique);
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
