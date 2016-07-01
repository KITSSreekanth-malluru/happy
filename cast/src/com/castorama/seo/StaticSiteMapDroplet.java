package com.castorama.seo;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
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

import javax.servlet.ServletException;

/**
 *
 * @author EPAM team
 */
public class StaticSiteMapDroplet extends DynamoServlet implements SiteMapConstants {
    /** productCatalog property. */
    private Repository mProductCatalog;

    /** defPriority property. */
    private String mDefPriority = "0.5";

    /**
     * Returns productCatalog property.
     *
     * @return productCatalog property.
     */
    public Repository getProductCatalog() {
        return mProductCatalog;
    }

    /**
     * Sets the value of the productCatalog property.
     *
     * @param pProductCatalog parameter to set.
     */
    public void setProductCatalog(Repository pProductCatalog) {
        mProductCatalog = pProductCatalog;
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
                    getProductCatalog().getItemDescriptor("media-internal-text").getRepositoryView();
                QueryBuilder qb = repositoryView.getQueryBuilder();
                QueryExpression parentFolderName = qb.createPropertyQueryExpression("parentfolder.name");
                QueryExpression sitemapFolder = qb.createConstantQueryExpression("StaticSitemap");
                Query query = qb.createComparisonQuery(parentFolderName, sitemapFolder, QueryBuilder.EQUALS);
                ;

                SortDirectives sds = new SortDirectives();
                sds.addDirective(new SortDirective("name", SortDirective.DIR_ASCENDING));

                RepositoryItem[] items = repositoryView.executeQuery(query);

                for (int i = 0; (items != null) && (i < items.length); i++) {
                    os.write(URL_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + items[i].getPropertyValue("name"));
                    os.write(LOC_END_TAG);
                    os.write(CHANGEFREQ_START_TAG);
                    os.write(changeFreq);
                    os.write(CHANGEFREQ_END_TAG);
                    os.write(PRIORITY_START_TAG);
                    os.write((String) items[i].getPropertyValue("data"));
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
