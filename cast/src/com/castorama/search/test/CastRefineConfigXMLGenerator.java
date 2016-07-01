package com.castorama.search.test;

import java.io.OutputStream;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.indexing.refinement.RefineConfigXMLGenerator;
import atg.repository.search.indexing.refinement.RefineConfigXMLInfo;
import atg.repository.search.refinement.admin.RefinementElement;

import atg.service.perfmonitor.PerformanceMonitor;

/**
 * Extension for adding performance monitors.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastRefineConfigXMLGenerator extends RefineConfigXMLGenerator {
    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#createDocument()
     */
    @Override protected Document createDocument() {
        Document d = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastRefineConfigXMLGenerator.createDocument");
            }
            d = super.createDocument();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastRefineConfigXMLGenerator.createDocument");
            }
        }
        return d;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#createRefineConfigDocument(java.util.List, atg.repository.search.indexing.refinement.RefineConfigXMLInfo)
     */
    @Override protected Document createRefineConfigDocument(List pRefineElementItemList, RefineConfigXMLInfo pInfo) {
        Document d = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefineConfigXMLGenerator.createRefineConfigDocument");
            }
            d = super.createRefineConfigDocument(pRefineElementItemList, pInfo);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefineConfigXMLGenerator.createRefineConfigDocument");
            }
        }
        return d;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#createRefineElement(org.w3c.dom.Document, atg.repository.search.refinement.admin.RefinementElement)
     */
    @Override protected Element createRefineElement(Document pDocument, RefinementElement pRefineElement) {
        Element el = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastRefineConfigXMLGenerator.createRefineElement");
            }
            el = super.createRefineElement(pDocument, pRefineElement);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastRefineConfigXMLGenerator.createRefineElement");
            }
        }
        return el;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#generateRefineConfigXML(atg.repository.RepositoryItem, atg.repository.search.indexing.refinement.RefineConfigXMLInfo, java.io.OutputStream)
     */
    @Override public void generateRefineConfigXML(RepositoryItem pRefineConfig, RefineConfigXMLInfo pInfo,
                                                  OutputStream pOut) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefineConfigXMLGenerator.generateRefineConfigXML:RefineConfigXMLInfo");
            }
            super.generateRefineConfigXML(pRefineConfig, pInfo, pOut);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefineConfigXMLGenerator.generateRefineConfigXML:RefineConfigXMLInfo");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#generateRefineConfigXML(java.lang.String, java.util.List, atg.repository.search.indexing.refinement.RefineConfigXMLInfo, java.io.OutputStream)
     */
    @Override public void generateRefineConfigXML(String pName, List pRefineElementItemList, RefineConfigXMLInfo pInfo,
                                                  OutputStream pOut) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefineConfigXMLGenerator.generateRefineConfigXML:List");
            }
            super.generateRefineConfigXML(pName, pRefineElementItemList, pInfo, pOut);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefineConfigXMLGenerator.generateRefineConfigXML:List");
            }
        }
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#getRefinementElementForItem(atg.repository.RepositoryItem)
     */
    @Override protected RefinementElement getRefinementElementForItem(RepositoryItem pItem) throws RepositoryException {
        RefinementElement re = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastRefineConfigXMLGenerator.getRefinementElementForItem");
            }
            re = super.getRefinementElementForItem(pItem);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastRefineConfigXMLGenerator.getRefinementElementForItem");
            }
        }
        return re;
    }

    /* (non-Javadoc)
     * @see atg.repository.search.indexing.refinement.RefineConfigXMLGenerator#transformToXML(org.w3c.dom.Node, java.io.OutputStream)
     */
    @Override protected void transformToXML(Node pDocument, OutputStream pOut) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastRefineConfigXMLGenerator.transformToXML");
            }
            super.transformToXML(pDocument, pOut);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastRefineConfigXMLGenerator.transformToXML");
            }
        }
    }

}
