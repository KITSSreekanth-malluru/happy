package com.castorama.search.test;

import java.io.InputStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import atg.commerce.search.refinement.CommerceFacetSourceItem;
import atg.commerce.search.refinement.custom.CustomFacetSourceGenerator;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.admin.RefinementConfiguration;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastCustomFacetSourceGenerator extends CustomFacetSourceGenerator {
    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.custom.CustomFacetSourceGenerator#determineInheritanceState(atg.repository.RepositoryItem)
     */
    @Override protected int determineInheritanceState(RepositoryItem pRefineConfig) {
        int i = 0;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomFacetSourceGenerator.determineInheritanceState");
            }
            i = super.determineInheritanceState(pRefineConfig);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomFacetSourceGenerator.determineInheritanceState");
            }
        }
        return i;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.custom.CustomFacetSourceGenerator#getAncestorCatalogs(atg.repository.RepositoryItem, boolean)
     */
    @Override protected Collection getAncestorCatalogs(RepositoryItem pSourceItem, boolean pSourceIsACatalog) {
        Collection c = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.getRefineConfigById");
            }
            c = super.getAncestorCatalogs(pSourceItem, pSourceIsACatalog);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getRefineConfigById");
            }
        }
        return c;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.custom.CustomFacetSourceGenerator#getCatalogSources(org.w3c.dom.Document, org.w3c.dom.Element, atg.repository.RepositoryItem, java.util.Set)
     */
    @Override protected void getCatalogSources(Document pSourceDoc, Element pParent, RepositoryItem pSourceItem,
                                               Set pFacets) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.getCatalogSources");
            }
            super.getCatalogSources(pSourceDoc, pParent, pSourceItem, pFacets);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getCatalogSources");
            }
        }

    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.custom.CustomFacetSourceGenerator#getSourceDocument(atg.repository.RepositoryItem, java.util.Set, boolean)
     */
    @Override public Document getSourceDocument(RepositoryItem pSourceItem, Set pFacets, boolean pGenerateAncestors) {
        Document d = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.getSourceDocument");
            }
            d = super.getSourceDocument(pSourceItem, pFacets, pGenerateAncestors);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getSourceDocument");
            }
        }
        return d;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#addFacetListToSource(org.w3c.dom.Document, org.w3c.dom.Element, java.util.List, java.util.Set, boolean)
     */
    @Override protected void addFacetListToSource(Document pSourceDoc, Element pParent, List pFacets, Set pFinalFacets,
                                                  boolean pIsChildFacet) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetListToSource");
            }
            super.addFacetListToSource(pSourceDoc, pParent, pFacets, pFinalFacets, pIsChildFacet);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetListToSource");
            }
        }

    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#addFacetListToSource(org.w3c.dom.Document, org.w3c.dom.Element, java.util.List, java.util.Set)
     */
    @Override protected void addFacetListToSource(Document pSourceDoc, Element pParent, List pFacets,
                                                  Set pFinalFacets) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetListToSource");
            }
            super.addFacetListToSource(pSourceDoc, pParent, pFacets, pFinalFacets);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetListToSource");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#addFacetSource(org.w3c.dom.Document, org.w3c.dom.Element, java.lang.String, java.lang.String, int, atg.repository.RepositoryItem, java.util.Set)
     */
    @Override protected void addFacetSource(Document pSourceDoc, Element pParent, String pSourceId,
                                            String pSourceDisplayName, int pLevel, RepositoryItem pRefineConfig,
                                            Set pFacets) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetSource");
            }
            super.addFacetSource(pSourceDoc, pParent, pSourceId, pSourceDisplayName, pLevel, pRefineConfig, pFacets);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.addFacetSource");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#addGlobalSource(org.w3c.dom.Document, org.w3c.dom.Element, java.util.Set)
     */
    @Override protected void addGlobalSource(Document pSourceDoc, Element pParent, Set pFacets) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.addGlobalSource");
            }
            super.addGlobalSource(pSourceDoc, pParent, pFacets);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.addGlobalSource");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#generateAncestorsForCategory(atg.repository.RepositoryItem, java.util.Map, java.util.Stack)
     */
    @Override public Set<RepositoryItem> generateAncestorsForCategory(RepositoryItem pCategory,
                                                                      Map<String, Set<RepositoryItem>> pFinishedCategoryMap,
                                                                      Stack<RepositoryItem> pCategoryStack)
                                                               throws RepositoryException {
        Set<RepositoryItem> set = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomFacetSourceGenerator.generateAncestorsForCategory");
            }
            set = super.generateAncestorsForCategory(pCategory, pFinishedCategoryMap, pCategoryStack);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastCustomFacetSourceGenerator.generateAncestorsForCategory");
            }
        }
        return set;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#getCategoryAncestors(atg.repository.RepositoryItem, boolean)
     */
    @Override protected HashMap<RepositoryItem, Integer> getCategoryAncestors(RepositoryItem pCategory,
                                                                              boolean pGenerateAncestors) {
        HashMap<RepositoryItem, Integer> map = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.getCategoryAncestors");
            }
            map = super.getCategoryAncestors(pCategory, pGenerateAncestors);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getCategoryAncestors");
            }
        }
        return map;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#getCategorySources(org.w3c.dom.Document, org.w3c.dom.Element, atg.repository.RepositoryItem, java.util.Set, boolean)
     */
    @Override protected void getCategorySources(Document pSourceDoc, Element pParent, RepositoryItem pSourceItem,
                                                Set pFacets, boolean pGenerateAncestors) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.getCategorySources");
            }
            super.getCategorySources(pSourceDoc, pParent, pSourceItem, pFacets, pGenerateAncestors);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getCategorySources");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#getGlobalRefineConfig()
     */
    @Override protected RefinementConfiguration getGlobalRefineConfig() {
        RefinementConfiguration rc = null;

        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomFacetSourceGenerator.getGlobalRefineConfig");
            }
            rc = super.getGlobalRefineConfig();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.getGlobalRefineConfig");
            }
        }
        return rc;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#parseSource(java.io.InputStream)
     */
    @Override public List<CommerceFacetSourceItem> parseSource(InputStream pStream) throws TransformerException {
        List<CommerceFacetSourceItem> l = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.parseSource:pStream");
            }
            l = super.parseSource(pStream);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.parseSource:pStream");
            }
        }
        return l;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#parseSource(java.lang.String)
     */
    @Override public List<CommerceFacetSourceItem> parseSource(String pSourceXml) throws TransformerException {
        List<CommerceFacetSourceItem> l = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastCustomFacetSourceGenerator.parseSource:pSourceXml");
            }
            l = super.parseSource(pSourceXml);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.parseSource:pSourceXml");
            }
        }
        return l;
    }

    /* (non-Javadoc)
     * @see atg.commerce.search.refinement.CommerceFacetSourceGenerator#sortCategories(java.util.HashMap)
     */
    @Override public List sortCategories(HashMap<RepositoryItem, Integer> pCategories) {
        List l = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastCustomFacetSourceGenerator.sortCategories");
            }
            l = super.sortCategories(pCategories);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastCustomFacetSourceGenerator.sortCategories");
            }
        }
        return l;
    }

}
