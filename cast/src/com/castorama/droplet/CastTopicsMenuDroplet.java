package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 * ToDo: DOCUMENT ME!
 * 
 * @author EPAM team
 */
public class CastTopicsMenuDroplet extends DynamoServlet {
    /** OUTPUT constant. */
    public static final String  OUTPUT               = "output";

    /** EMPTY constant. */
    public static final String  EMPTY                = "empty";

    /** REFERENCE_ID constant. */
    public static final String  REFERENCE_ID         = "referenceId";

    /** CONTAINER_ID constant. */
    private static final String CONTAINER_ID          = "contaunerId";

    /** THEMATIQUE_CONTAINER constant. */
    private static final String THEMATIQUE_CONTAINER = "thematiqueContainer";

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
    @SuppressWarnings("unchecked")
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        try {
            Map<RepositoryItem, List<RepositoryItem>> map = new LinkedHashMap<RepositoryItem, List<RepositoryItem>>();

            MutableRepository mutableRepos = (MutableRepository) getCatalogRepository();
            String containerId = pRequest.getParameter("containerId");
            
            // get container
            RepositoryView viewThematique = mutableRepos.getView(THEMATIQUE_CONTAINER);
            RqlStatement requestRQLContainer = RqlStatement.parseRqlStatement("id = ?0");
            RepositoryItem[] container = requestRQLContainer.executeQuery(viewThematique, new Object[] {containerId});

            if (container.length == 0)
                return;

            // read thematiques from container
            List<RepositoryItem> thematiques = (List<RepositoryItem>) container[0].getPropertyValue("thematiqueList");

            // read chapters from container
            List<RepositoryItem> chapters = (List<RepositoryItem>) container[0].getPropertyValue("chapterList");

            for (int j = 0; j < chapters.size(); j++) {
                map.put(chapters.get(j), new ArrayList<RepositoryItem>());
            }
            for (int i = 0; i < thematiques.size(); i++) {
                List<RepositoryItem> thematiqueChapters = (List<RepositoryItem>) thematiques.get(i).getPropertyValue(
                        "chapterList");
                if (thematiqueChapters != null) {
                    for (RepositoryItem thematiqueChapter : thematiqueChapters) {
                        String thematiqueChapterId = thematiqueChapter.getRepositoryId();
                        for (int j = 0; j < chapters.size(); j++) {
                            String chapterId = chapters.get(j).getRepositoryId();
                            if (thematiqueChapterId.equals(chapterId)) {
                                map.get(chapters.get(j)).add(thematiques.get(i));
                                break;
                            }
                        }
                    }
                }
            }
            pRequest.setParameter("map", map);
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
