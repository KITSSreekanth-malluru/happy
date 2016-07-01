package com.castorama.seo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

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

/**
 *
 * @author EPAM team
 */
public class DynamicSiteMapDroplet extends DynamoServlet implements SiteMapConstants {
    /** PROPERTY_ID constant. */
    private static final String PROPERTY_ID = "id";

    /** HTTP_PREFIX constant. */
    private static final String HTTP_PREFIX = "http://";

    /** PARAMETER_ITEM constant. */
    private static final String PARAMETER_ITEM = "item";

    /** PARAMETER_CHANGE_FREQ constant. */
    private static final String PARAMETER_CHANGE_FREQ = "changeFreq";

    /** PARAMETER_END_INDEX constant. */
    private static final String PARAMETER_END_INDEX = "endIndex";

    /** PARAMETER_START_INDEX constant. */
    private static final String PARAMETER_START_INDEX = "startIndex";

    /** ITEM_CATEGORY constant. */
    private static final String ITEM_CATEGORY = "category";

    /** ITEM_PRODUCT constant. */
    private static final String ITEM_PRODUCT = "product";

    /** ITEM_DOCUMENT constant. */
    private static final String ITEM_DOCUMENT = "document";

    /** productCatalog property. */
    private Repository mProductCatalog;

    /** defPriority property. */
    private String mDefPriority = "0.7";

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
        String startIndex = pRequest.getParameter(PARAMETER_START_INDEX);
        String endIndex = pRequest.getParameter(PARAMETER_END_INDEX);
        String changeFreq = pRequest.getParameter(PARAMETER_CHANGE_FREQ);
        String item = pRequest.getParameter(PARAMETER_ITEM);
        if (ITEM_DOCUMENT.equalsIgnoreCase(item)){
            item = "castoramaDocument";
        }

        PrintWriter os = pResponse.getWriter();
        int port = pRequest.getServerPort();
        String location = null;

        if (port == 80) {
            location = HTTP_PREFIX + pRequest.getServerName() + pRequest.getContextPath();
        } else {
            location = HTTP_PREFIX + pRequest.getServerName() + ":" + port + pRequest.getContextPath();
        }

        try {
            os.write(URLSET_HEADER);

            try {
                RepositoryView repositoryView = getProductCatalog().getItemDescriptor(item).getRepositoryView();
                QueryBuilder qb = repositoryView.getQueryBuilder();
                Query query = qb.createUnconstrainedQuery();

                SortDirectives sds = new SortDirectives();
                sds.addDirective(new SortDirective(PROPERTY_ID, SortDirective.DIR_ASCENDING));

                RepositoryItem[] items =
                    repositoryView.executeQuery(query, Integer.parseInt(startIndex), Integer.parseInt(endIndex), sds);

                for (int i = 0; i < items.length; i++) {
                    os.write(URL_START_TAG);
                    os.write(LOC_START_TAG);

                    if (ITEM_CATEGORY.equals(item)) {
                        os.write(location + SEOUtils.getCategoryURL(items[i]));
                    } else {
                        if (ITEM_PRODUCT.equals(item)) {
                            os.write(location + SEOUtils.getProductURL(items[i]));
                        } 
                        else {
                            os.write(SEOUtils.getDocumentURL(items[i],location));
                        }
                    }

                    os.write(LOC_END_TAG);
                    os.write(CHANGEFREQ_START_TAG);
                    os.write(changeFreq);
                    os.write(CHANGEFREQ_END_TAG);
                    os.write(PRIORITY_START_TAG);
                    os.write(getDefPriority());
                    os.write(PRIORITY_END_TAG);
                    os.write(URL_END_TAG);
                }  // end for
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
