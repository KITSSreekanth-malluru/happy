package com.castorama.seo;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLEncoder;

import javax.servlet.ServletException;

/**
 *
 * @author EPAM team
 */
public class SearchSiteMapDroplet extends DynamoServlet implements SiteMapConstants {
    /** mCloudRepository constant. */
    private Repository mCloudRepository;

    /** mDefPriority constant. */
    private String mDefPriority = "0.7";

    /**
     * Returns cloudRepository property.
     *
     * @return cloudRepository property.
     */
    public Repository getCloudRepository() {
        return mCloudRepository;
    }

    /**
     * Sets the value of the cloudRepository property.
     *
     * @param pCloudRepository parameter to set.
     */
    public void setCloudRepository(Repository pCloudRepository) {
        mCloudRepository = pCloudRepository;
    }

    /**
     * Returns defPriority property.
     *
     * @return defPriority property.
     */
    public String getDefPriority() {
        return mDefPriority;
    }

    /**
     * Sets the value of the defPriority property.
     *
     * @param pDefPriority parameter to set.
     */
    public void setDefPriority(String pDefPriority) {
        mDefPriority = pDefPriority;
    }

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String startIndex = pRequest.getParameter("startIndex");
        String endIndex = pRequest.getParameter("endIndex");
        String changeFreq = pRequest.getParameter("changeFreq");

        PrintWriter os = pResponse.getWriter();
        int port = pRequest.getServerPort();
        String location = null;

        if (port == 80) {
            location = "http://" + pRequest.getServerName() + pRequest.getContextPath();
        } else {
            location = "http://" + pRequest.getServerName() + ":" + port + pRequest.getContextPath();
        }

        try {
            os.write(URLSET_HEADER);

            try {
                RepositoryView repositoryView =
                    getCloudRepository().getItemDescriptor("cloud_term").getRepositoryView();
                QueryBuilder qb = repositoryView.getQueryBuilder();
                Query query = qb.createUnconstrainedQuery();

                SortDirectives sds = new SortDirectives();
                sds.addDirective(new SortDirective("id", SortDirective.DIR_ASCENDING));

                RepositoryItem[] items =
                    repositoryView.executeQuery(query, Integer.parseInt(startIndex), Integer.parseInt(endIndex), sds);

                for (int i = 0; i < items.length; i++) {
                    os.write(URL_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + "/rechercher/" +
                             URLEncoder.encode((String) items[i].getPropertyValue("term"), "UTF-8").replaceAll("\\+",
                                                                                                               "%20"));
                    os.write(LOC_END_TAG);
                    os.write(CHANGEFREQ_START_TAG);
                    os.write(changeFreq);
                    os.write(CHANGEFREQ_END_TAG);
                    os.write(PRIORITY_START_TAG);
                    os.write(getDefPriority());
                    os.write(PRIORITY_END_TAG);
                    os.write(URL_END_TAG);
                }
            } catch (RepositoryException e) {
                logError(e);
            }  // end try-catch

            os.write(URLSET_FOOTER);
        } finally {
            try {
                os.close();
            } catch (Exception e) {
            }
        }  // end try-finally
    }
}
