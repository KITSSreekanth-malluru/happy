package com.castorama.commerce.search.refinement;

import java.util.List;

import atg.commerce.search.refinement.custom.CustomCatalogFacetSearchTools;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.repository.search.refinement.DisjunctionMultiValue;
import atg.repository.search.refinement.FacetDisjunctionMultiValue;
import atg.repository.search.refinement.FacetException;
import atg.repository.search.refinement.FacetHolder;
import atg.repository.search.refinement.FacetTrail;
import atg.repository.search.refinement.FacetValue;
import atg.repository.search.refinement.RangeFacetDisjunctionMultiValue;
import atg.repository.search.refinement.RefinementFacet;
import atg.repository.search.refinement.admin.RefinementConfiguration;

import atg.search.client.SearchClientException;
import atg.search.client.SearchSession;

import atg.search.routing.command.search.ConstraintsGroup;
import atg.search.routing.command.search.DocumentSetConstraint;
import atg.search.routing.command.search.QueryRequest;
import atg.search.routing.command.search.RefinementValue;
import atg.search.routing.command.search.SearchRequest;
import atg.search.routing.command.search.SearchResponse;
import atg.search.routing.command.search.QueryRequest.Response;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;

/**
 * Override isEligibleFacetValue method for maintaining excluding multi selected
 * facet values. Add performance monitors to platform methods.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastFacetSearchTools extends CustomCatalogFacetSearchTools {
    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#addCategoryConstraint(java.lang.String,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override protected void addCategoryConstraint(String pCategoryId, QueryRequest pRequest) {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.addCategoryConstraint");
            }
            super.addCategoryConstraint(pCategoryId, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.addCategoryConstraint");
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#convertRefineValue(atg.repository.search.refinement.RefinementFacet,
     *      atg.search.routing.command.search.RefinementValue, boolean)
     */
    @Override protected FacetValue convertRefineValue(RefinementFacet pRefineFacet, RefinementValue pRefineValue,
                                                      boolean pIsLastValue) {
        FacetValue fv = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.convertRefineValue");
            }
            fv = super.convertRefineValue(pRefineFacet, pRefineValue, pIsLastValue);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.convertRefineValue");
            }
        }
        return fv;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#determineCommerceRefineConfigName(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineCommerceRefineConfigName(FacetTrail pTrail, QueryRequest pRequest) {
        String crcn = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.determineCommerceRefineConfigName");
            }
            crcn = super.determineCommerceRefineConfigName(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.determineCommerceRefineConfigName");
            }
        }
        return crcn;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#determineDocSetPath(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineDocSetPath(FacetTrail pTrail, QueryRequest pRequest) {
        String ddsp = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.determineDocSetPath");
            }
            ddsp = super.determineDocSetPath(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.determineDocSetPath");
            }
        }
        return ddsp;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#findCategory(java.lang.String)
     */
    @Override public RepositoryItem findCategory(String pCategoryId) throws RepositoryException {
        RepositoryItem ri = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.findCategory");
            }
            ri = super.findCategory(pCategoryId);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.findCategory");
            }
        }
        return ri;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#processSearchRequest(atg.search.routing.command.search.SearchRequest,
     *      atg.servlet.DynamoHttpServletRequest, java.lang.Object)
     */
    @Override public SearchRequest processSearchRequest(SearchRequest pSearchRequest,
                                                        DynamoHttpServletRequest pDynamoRequest, Object pCallback)
                                                 throws SearchClientException {
        SearchRequest sr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.processSearchRequest");
            }
            sr = super.processSearchRequest(pSearchRequest, pDynamoRequest, pCallback);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.processSearchRequest");
            }
        }
        return sr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#reorderCategories(java.util.List)
     */
    @Override public List<String> reorderCategories(List<String> pCategoryIds) {
        List<String> l = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.reorderCategories");
            }
            l = super.reorderCategories(pCategoryIds);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.reorderCategories");
            }
        }
        return l;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#containsConstraint(atg.search.routing.command.search.DocumentSetConstraint,
     *      atg.search.routing.command.search.ConstraintsGroup)
     */
    @Override protected boolean containsConstraint(DocumentSetConstraint pConstraint, ConstraintsGroup pGroup) {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.containsConstraint");
            }
            result = super.containsConstraint(pConstraint, pGroup);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.containsConstraint");
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createDocSetConstraint(java.lang.String,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public DocumentSetConstraint createDocSetConstraint(String pDocSetPath, QueryRequest pRequest) {
        DocumentSetConstraint dsc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createDocSetConstraint");
            }
            dsc = super.createDocSetConstraint(pDocSetPath, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createDocSetConstraint");
            }
        }
        return dsc;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createFacetDocSetConstraint(atg.repository.search.refinement.FacetTrail,
     *      boolean)
     */
    @Override public DocumentSetConstraint createFacetDocSetConstraint(FacetTrail pTrail, boolean pIsCatNav) {
        DocumentSetConstraint dsc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.createFacetDocSetConstraint:pTrail:pIsCatNav");
            }
            dsc = super.createFacetDocSetConstraint(pTrail, pIsCatNav);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.createFacetDocSetConstraint:pTrail:pIsCatNav");
            }
        }
        return dsc;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createFacetDocSetConstraint(atg.repository.search.refinement.FacetTrail)
     */
    @Override public DocumentSetConstraint createFacetDocSetConstraint(FacetTrail pTrail) {
        DocumentSetConstraint dsc = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.createFacetDocSetConstraint:pTrail");
            }
            dsc = super.createFacetDocSetConstraint(pTrail);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.createFacetDocSetConstraint:pTrail");
            }
        }
        return dsc;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createFacetTrail(java.lang.String,
     *      atg.repository.search.refinement.admin.RefinementConfiguration)
     */
    @Override public FacetTrail createFacetTrail(String pTrailString, RefinementConfiguration pRefineConfig)
                                          throws FacetException {
        FacetTrail ft = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.createFacetTrail:pTrailString:pRefineConfig:RefConf");
            }
            ft = super.createFacetTrail(pTrailString, pRefineConfig);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.createFacetTrail:pTrailString:pRefineConfig:RefConf");
            }
        }
        return ft;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createFacetTrail(java.lang.String,
     *      atg.repository.RepositoryItem)
     */
    @Override public FacetTrail createFacetTrail(String pTrailString, RepositoryItem pRefineConfig)
                                          throws FacetException {
        FacetTrail ft = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.createFacetTrail:pTrailString:pRefineConfig");
            }
            ft = super.createFacetTrail(pTrailString, pRefineConfig);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.createFacetTrail:pTrailString:pRefineConfig");
            }
        }
        return ft;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createFacetTrail(java.lang.String)
     */
    @Override public FacetTrail createFacetTrail(String pTrailString) throws FacetException {
        FacetTrail ft = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetTrail:pTrailString");
            }
            ft = super.createFacetTrail(pTrailString);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetTrail:pTrailString");
            }
        }
        return ft;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createQueryRequest()
     */
    @Override public QueryRequest createQueryRequest() {
        QueryRequest qr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            qr = super.createQueryRequest();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return qr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#createSession()
     */
    @Override public SearchSession createSession() throws FacetException {
        SearchSession ss = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            ss = super.createSession();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return ss;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#determineQuestionText(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineQuestionText(FacetTrail pTrail, QueryRequest pRequest) {
        String s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            s = super.determineQuestionText(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#determineRefineConfigMap(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineRefineConfigMap(FacetTrail pTrail, QueryRequest pRequest) {
        String s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            s = super.determineRefineConfigMap(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#determineRefineConfigName(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public String determineRefineConfigName(FacetTrail pTrail, QueryRequest pRequest) {
        String s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            s = super.determineRefineConfigName(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#escapeAsterisk(java.lang.String)
     */
    @Override protected String escapeAsterisk(String pString) {
        String s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            s = super.escapeAsterisk(pString);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#getAdditionalConstraints()
     */
    @Override public String[] getAdditionalConstraints() {
        String[] s = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.getAdditionalConstraints");
            }
            s = super.getAdditionalConstraints();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.getAdditionalConstraints");
            }
        }
        return s;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#getAllFacets()
     */
    @Override public FacetHolder[] getAllFacets() {
        FacetHolder[] fh = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            fh = super.getAllFacets();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return fh;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#getCurrentFacetTrail(atg.servlet.DynamoHttpServletRequest,
     *      java.lang.Object)
     */
    @Override protected FacetTrail getCurrentFacetTrail(DynamoHttpServletRequest pDynamoRequest, Object pCallback)
                                                 throws FacetException {
        FacetTrail ft = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
            ft = super.getCurrentFacetTrail(pDynamoRequest, pCallback);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.createFacetDocSetConstraint");
            }
        }
        return ft;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#handleAutoRedirect(atg.search.routing.command.search.SearchResponse,
     *      atg.servlet.DynamoHttpServletRequest, java.lang.Object)
     */
    @Override public boolean handleAutoRedirect(SearchResponse pSearchResponse, DynamoHttpServletRequest pDynamoRequest,
                                                Object pCallback) throws SearchClientException {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.handleAutoRedirect");
            }
            result = super.handleAutoRedirect(pSearchResponse, pDynamoRequest, pCallback);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.handleAutoRedirect");
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#isIncludeOverflowAndUnderflowRangeValues()
     */
    @Override public boolean isIncludeOverflowAndUnderflowRangeValues() {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.isIncludeOverflowAndUnderflowRangeValues");
            }
            result = super.isIncludeOverflowAndUnderflowRangeValues();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.isIncludeOverflowAndUnderflowRangeValues");
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#isSkipValuesAlreadyInTrail()
     */
    @Override public boolean isSkipValuesAlreadyInTrail() {
        boolean result = false;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.isSkipValuesAlreadyInTrail");
            }
            result = super.isSkipValuesAlreadyInTrail();
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.isSkipValuesAlreadyInTrail");
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#mergeConstraints(atg.search.routing.command.search.ConstraintsGroup,
     *      atg.search.routing.command.search.DocumentSetConstraint)
     */
    @Override protected ConstraintsGroup mergeConstraints(ConstraintsGroup pNewConstraints,
                                                          DocumentSetConstraint pOldConstraints) {
        ConstraintsGroup cg = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.mergeConstraints");
            }
            cg = super.mergeConstraints(pNewConstraints, pOldConstraints);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.mergeConstraints");
            }
        }
        return cg;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#processFacetTrail(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest, boolean)
     */
    @Override public QueryRequest processFacetTrail(FacetTrail pTrail, QueryRequest pRequest,
                                                    boolean pIsCategoryNavigation) {
        QueryRequest qr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.processFacetTrail:pTrail:pRequest:pIsCategoryNavigation");
            }
            qr = super.processFacetTrail(pTrail, pRequest, pIsCategoryNavigation);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.processFacetTrail:pTrail:pRequest:pIsCategoryNavigation");
            }
        }
        return qr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#processFacetTrail(atg.repository.search.refinement.FacetTrail,
     *      atg.search.routing.command.search.QueryRequest)
     */
    @Override public QueryRequest processFacetTrail(FacetTrail pTrail, QueryRequest pRequest) {
        QueryRequest qr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search",
                                                  "TEST:CastFacetSearchTools.processFacetTrail:pTrail:pRequest");
            }
            qr = super.processFacetTrail(pTrail, pRequest);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search",
                                                "TEST:CastFacetSearchTools.processFacetTrail:pTrail:pRequest");
            }
        }
        return qr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#processSearchResponse(atg.search.routing.command.search.SearchResponse,
     *      atg.servlet.DynamoHttpServletRequest, java.lang.Object)
     */
    @Override public SearchResponse processSearchResponse(SearchResponse pSearchResponse,
                                                          DynamoHttpServletRequest pDynamoRequest,
                                                          Object pCallback) throws SearchClientException {
        SearchResponse sr = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.processSearchResponse");
            }
            sr = super.processSearchResponse(pSearchResponse, pDynamoRequest, pCallback);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.processSearchResponse");
            }
        }
        return sr;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#search(atg.repository.search.refinement.FacetTrail)
     */
    @Override public Response search(FacetTrail pTrail) throws FacetException {
        Response r = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.search:pTrail");
            }
            r = super.search(pTrail);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.search:pTrail");
            }
        }
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#search(atg.search.routing.command.search.QueryRequest,
     *      atg.search.client.SearchSession)
     */
    @Override public Response search(QueryRequest pRequest, SearchSession pSession) throws SearchClientException {
        Response r = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.search:pRequest:pSession");
            }
            r = super.search(pRequest, pSession);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.search:pRequest:pSession");
            }
        }
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.repository.search.refinement.FacetSearchTools#search(java.lang.String)
     */
    @Override public Response search(String pTrailString) throws FacetException {
        Response r = null;
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.search:pTrailString");
            }
            r = super.search(pTrailString);
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.search:pTrailString");

            }
        }
        return r;
    }

    /*
     * (non-Javadoc)
     *
     * @see atg.commerce.search.refinement.CommerceFacetSearchTools#isEligibleFacetValue(atg.repository.search.refinement.FacetTrail,
     *      atg.repository.search.refinement.FacetValue)
     */
    @Override protected boolean isEligibleFacetValue(FacetTrail pFacetTrail, FacetValue pFacetValue) {
        boolean result = true;

        if (PerformanceMonitor.isEnabled()) {
            PerformanceMonitor.startOperation("search", "TEST:CastFacetSearchTools.isEligibleFacetValue");
        }
        if (isSkipValuesAlreadyInTrail()) {
            FacetValue[] trailValues = pFacetTrail.getFacetValues();
            for (int i = 0; i < trailValues.length; i++) {
                if (trailValues[i] instanceof DisjunctionMultiValue) {
                    for (Object o : ((DisjunctionMultiValue) trailValues[i]).getValues()) {
                        Object value = pFacetValue instanceof FacetDisjunctionMultiValue ? ((FacetDisjunctionMultiValue)pFacetValue).getSingleValue() : 
                            pFacetValue instanceof RangeFacetDisjunctionMultiValue ? ((RangeFacetDisjunctionMultiValue)pFacetValue).getSingleValue() : 
                                pFacetValue.getValue();
                        if (value.equals(o)) {
                            if (PerformanceMonitor.isEnabled()) {
                                PerformanceMonitor.endOperation("search",
                                                                "TEST:CastFacetSearchTools.isEligibleFacetValue");
                            }
                            return false;
                        }
                    }

                } else {
                    result = super.isEligibleFacetValue(pFacetTrail, pFacetValue);
                    break;
                }
            }
        }  // end if
        if (PerformanceMonitor.isEnabled()) {
            PerformanceMonitor.endOperation("search", "TEST:CastFacetSearchTools.isEligibleFacetValue");
        }
        return result;
    }
}
