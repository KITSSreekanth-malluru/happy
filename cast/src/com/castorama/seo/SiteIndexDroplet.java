package com.castorama.seo;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;

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
public class SiteIndexDroplet extends DynamoServlet implements SiteMapConstants {
    /** productCatalog constant. */
    private Repository mProductCatalog;

    /** cloudRepository constant. */
    private Repository mCloudRepository;

    /** catalagBatchSize constant. */
    private int mCatalagBatchSize = 700;

    /** searchBatchSize constant. */
    private int mSearchBatchSize = 700;

    /** dynamicMapsChangeFreq constant. */
    private String mDynamicMapsChangeFreq = "weekly";

    /** staticMapsChangeFreq constant. */
    private String mStaticMapsChangeFreq = "weekly";

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
     * Returns catalagBatchSize property.
     *
     * @return catalagBatchSize property.
     */
    public int getCatalagBatchSize() {
        return mCatalagBatchSize;
    }

    /**
     * Sets the value of the catalagBatchSize property.
     *
     * @param pBatchSize parameter to set.
     */
    public void setCatalagBatchSize(int pBatchSize) {
        mCatalagBatchSize = pBatchSize;
    }

    /**
     * Returns searchBatchSize property.
     *
     * @return searchBatchSize property.
     */
    public int getSearchBatchSize() {
        return mSearchBatchSize;
    }

    /**
     * Sets the value of the searchBatchSize property.
     *
     * @param pSearchBatchSize parameter to set.
     */
    public void setSearchBatchSize(int pSearchBatchSize) {
        mSearchBatchSize = pSearchBatchSize;
    }

    /**
     * Returns dynamicMapsChangeFreq property.
     *
     * @return dynamicMapsChangeFreq property.
     */
    public String getDynamicMapsChangeFreq() {
        return mDynamicMapsChangeFreq;
    }

    /**
     * Sets the value of the dynamicMapsChangeFreq property.
     *
     * @param pChangeFreq parameter to set.
     */
    public void setDynamicMapsChangeFreq(String pChangeFreq) {
        mDynamicMapsChangeFreq = pChangeFreq;
    }

    /**
     * Returns staticMapsChangeFreq property.
     *
     * @return staticMapsChangeFreq property.
     */
    public String getStaticMapsChangeFreq() {
        return mStaticMapsChangeFreq;
    }

    /**
     * Sets the value of the staticMapsChangeFreq property.
     *
     * @param pStaticMapsChangeFreq parameter to set.
     */
    public void setStaticMapsChangeFreq(String pStaticMapsChangeFreq) {
        mStaticMapsChangeFreq = pStaticMapsChangeFreq;
    }

    /**
     *
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        PrintWriter os = pResponse.getWriter();
        int port = pRequest.getServerPort();
        String location = null;

        if (port == 80) {
            location = "http://" + pRequest.getServerName() + pRequest.getContextPath();
        } else {
            location = "http://" + pRequest.getServerName() + ":" + port + pRequest.getContextPath();
        }

        try {
            os.write(SITEMAPINDEX_START_TAG);
            os.write(SITEMAP_START_TAG);
            os.write(LOC_START_TAG);
            os.write(location + "/staticSitemap.jsp?changeFreq=" + getStaticMapsChangeFreq());
            os.write(LOC_END_TAG);
            os.write(SITEMAP_END_TAG);

            try {
                //search
            	RepositoryView repositoryView = getCloudRepository().getItemDescriptor("cloud_term").getRepositoryView();
            	QueryBuilder qb = repositoryView.getQueryBuilder();
            	Query query = qb.createUnconstrainedQuery();
                int count = repositoryView.executeCountQuery(query);
                int numberOfLoc = (int) Math.ceil((double) count / getSearchBatchSize());
                int startIndex = 0;

                for (int i = 0; i < numberOfLoc; i++) {
                    os.write(SITEMAP_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + "/searchSitemap.jsp?changeFreq=" + getDynamicMapsChangeFreq() +
                             "&amp;startIndex=" + startIndex + "&amp;endIndex=" +
                             Math.min(startIndex + getSearchBatchSize(), count));
                    os.write(LOC_END_TAG);
                    os.write(SITEMAP_END_TAG);
                    startIndex = startIndex + getSearchBatchSize();
                }
                //category	
            	repositoryView = getProductCatalog().getItemDescriptor("category").getRepositoryView();
                qb = repositoryView.getQueryBuilder();
                query = qb.createUnconstrainedQuery();
                count = repositoryView.executeCountQuery(query);
                numberOfLoc = (int) Math.ceil((double) count / getCatalagBatchSize());
                startIndex = 0;

                for (int i = 0; i < numberOfLoc; i++) {
                    os.write(SITEMAP_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + "/dynamicSitemap.jsp?item=category&amp;changeFreq=" +
                             getDynamicMapsChangeFreq() + "&amp;startIndex=" + startIndex + "&amp;endIndex=" +
                             Math.min(startIndex + getCatalagBatchSize(), count));
                    os.write(LOC_END_TAG);
                    os.write(SITEMAP_END_TAG);
                    startIndex = startIndex + getCatalagBatchSize();
                }
                //product
                repositoryView = getProductCatalog().getItemDescriptor("product").getRepositoryView();
                qb = repositoryView.getQueryBuilder();
                query = qb.createUnconstrainedQuery();
                count = repositoryView.executeCountQuery(query);
                numberOfLoc = (int) Math.ceil((double) count / getCatalagBatchSize());
                startIndex = 0;

                for (int i = 0; i < numberOfLoc; i++) {
                    os.write(SITEMAP_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + "/dynamicSitemap.jsp?item=product&amp;changeFreq=" +
                             getDynamicMapsChangeFreq() + "&amp;startIndex=" + startIndex + "&amp;endIndex=" +
                             Math.min(startIndex + getCatalagBatchSize(), count));
                    os.write(LOC_END_TAG);
                    os.write(SITEMAP_END_TAG);
                    startIndex = startIndex + getCatalagBatchSize();
                }
                //document
                repositoryView = getProductCatalog().getItemDescriptor("castoramaDocument").getRepositoryView();
                qb = repositoryView.getQueryBuilder();
                query = qb.createUnconstrainedQuery();
                count = repositoryView.executeCountQuery(query);
                numberOfLoc = (int) Math.ceil((double) count / getCatalagBatchSize());
                startIndex = 0;

                for (int i = 0; i < numberOfLoc; i++) {
                    os.write(SITEMAP_START_TAG);
                    os.write(LOC_START_TAG);
                    os.write(location + "/dynamicSitemap.jsp?item=document&amp;changeFreq=" +
                             getDynamicMapsChangeFreq() + "&amp;startIndex=" + startIndex + "&amp;endIndex=" +
                             Math.min(startIndex + getCatalagBatchSize(), count));
                    os.write(LOC_END_TAG);
                    os.write(SITEMAP_END_TAG);
                    startIndex = startIndex + getCatalagBatchSize();
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }  // end try-catch

            os.write(SITEMAPINDEX_END_TAG);
        } finally {
            try {
                os.close();
            } catch (Exception e) {
            }
        }  // end try-finally
    }
}
