package com.castorama.seo;

/**
 *
 * @author EPAM team
 */
public interface SiteMapConstants {
    /** SITEMAPINDEX_START_TAG constant. */
    String SITEMAPINDEX_START_TAG = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";

    /** SITEMAPINDEX_END_TAG constant. */
    String SITEMAPINDEX_END_TAG = "</sitemapindex>";

    /** URLSET_HEADER constant. */
    String URLSET_HEADER = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";

    /** URLSET_FOOTER constant. */
    String URLSET_FOOTER = "</urlset>";

    /** URL_START_TAG constant. */
    String URL_START_TAG = "\t<url>\n";

    /** URL_END_TAG constant. */
    String URL_END_TAG = "\t</url>\n";

    /** LOC_START_TAG constant. */
    String LOC_START_TAG = "\t\t<loc>";

    /** LOC_END_TAG constant. */
    String LOC_END_TAG = "</loc>\n";

    /** CHANGEFREQ_START_TAG constant. */
    String CHANGEFREQ_START_TAG = "\t\t<changefreq>";

    /** CHANGEFREQ_END_TAG constant. */
    String CHANGEFREQ_END_TAG = "</changefreq>\n";

    /** PRIORITY_START_TAG constant. */
    String PRIORITY_START_TAG = "\t\t<priority>";

    /** PRIORITY_END_TAG constant. */
    String PRIORITY_END_TAG = "</priority>\n";

    /** SITEMAP_START_TAG constant. */
    String SITEMAP_START_TAG = "\t<sitemap>\n";

    /** SITEMAP_END_TAG constant. */
    String SITEMAP_END_TAG = "\t</sitemap>\n";
}
