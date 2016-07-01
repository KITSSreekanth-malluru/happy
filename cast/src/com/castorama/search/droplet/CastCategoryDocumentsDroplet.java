package com.castorama.search.droplet;

import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;


import atg.adapter.gsa.GSAItem;
import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryItem;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns category documents.
 * Returns map of pairs document type to castorama document repository items
 *
 * @author Mikalai Khatsko
 */
public class CastCategoryDocumentsDroplet extends DynamoServlet {

    /*
     * Open parameter names
     */
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** ERROR constant. */
    public static final ParameterName ERROR = ParameterName.getParameterName("error");

    /*
     * Parameter names
     */
    /** PRODUCT constant. */
    public static final String PRODUCT = "product";

    /** CATEGORY constant. */
    public static final String CATEGORY = "category";

    /** PIVOT_CATEGORY constant. */
    public static final String PIVOT_CATEGORY = "pivotCategory";

    /** DOCUMENT constant. */
    public static final String DOCUMENT = "document";

    /** SEARCH_RESPONSE constant.. */
    public static final String SEARCH_RESPONSE = "searchResponse";

    /** NUMBER_DOCUMENT_SECTION property */
    public static final String NUMBER_DOCUMENT_SECTION = "numberOfDocumentPerSection";
    
    /** PIVOT constant */
    public static final String PIVOT = "pivot";

    /** PARENT_CATEGORY constant */
    public static final String PARENT_CATEGORY = "parentCategory";

    /*
     * Properties
     */
    /** mAdvicePageKeywordsForSearchCount property */
    private int mAdvicePageKeywordsForSearchCount = 3;

    /** mRepository property */
    private Repository mRepository;

    /** mNumberDocumentPerSection property */
    private int mNumberDocumentPerSection;

    /**
     * Returns amount of keywords which should be used for search until
     * successfull search will be returned.
     *
     * @return the advicePageKeywordsForSearchCount property
     */
    public int getAdvicePageKeywordsForSearchCount() {
        return mAdvicePageKeywordsForSearchCount;
    }

    /**
     * Sets advicePageKeywordsForSearchCount property
     *
     * @param pAdvicePageKeywordsForSearchCount the
     *                                          advicePageKeywordsForSearchCount
     *                                          to set
     */
    public void setAdvicePageKeywordsForSearchCount(int pAdvicePageKeywordsForSearchCount) {
        mAdvicePageKeywordsForSearchCount = pAdvicePageKeywordsForSearchCount;
    }

    /**
     * Returns repository property.
     *
     * @return repository property.
     */
    public Repository getRepository() {
        return mRepository;
    }

    /**
     * Sets the value of the repository property.
     *
     * @param pRepository parameter to set.
     */
    public void setRepository(Repository pRepository) {
        mRepository = pRepository;
    }

    /**
     * Returns NumberDocumentPerSection property.
     *
     * @return NumberDocumentPerSection property.
     */
    public int getNumberDocumentPerSection() {
        return mNumberDocumentPerSection;
    }

    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pNumberDocumentPerSection parameter to set.
     */
    public void setNumberDocumentPerSection(int pNumberDocumentPerSection) {
        mNumberDocumentPerSection = pNumberDocumentPerSection;
    }

    /**
     * Returns faceted search result for document on category or product page.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.startOperation("search", "TEST:CastDocumentSearchDroplet.service");
            }
            Object productObj = pRequest.getObjectParameter(PRODUCT);
            Object categoryObj = pRequest.getObjectParameter(CATEGORY);
            Object pivotCategoryObj = pRequest.getObjectParameter(PIVOT_CATEGORY);
            String numberDocumentPerSectionStr = pRequest.getParameter(NUMBER_DOCUMENT_SECTION);
            int numberDocumentPerSection = getNumberDocumentPerSection();
            List<RepositoryItem> documents = null;
            if (!StringUtils.isBlank(numberDocumentPerSectionStr)) {
                numberDocumentPerSection = Integer.parseInt(numberDocumentPerSectionStr);
            }
            if (numberDocumentPerSection > 0) {
                if ((productObj != null) && (productObj instanceof RepositoryItem)) {
                    if (categoryObj == null) {
                        pivotCategoryObj = returnPivotCategory((GSAItem) ((RepositoryItem)productObj).getPropertyValue(PARENT_CATEGORY));
                    } else {
                        pivotCategoryObj = returnPivotCategory((GSAItem) categoryObj);
                    }
                    if ((pivotCategoryObj != null) &&
                        (pivotCategoryObj instanceof RepositoryItem)) {
                        documents =
                            (List<RepositoryItem>) ((RepositoryItem)pivotCategoryObj).getPropertyValue("castoramaDocuments");
                    }
                } else  if ((categoryObj != null) && (categoryObj instanceof RepositoryItem)) {
                    documents = (List<RepositoryItem>) ((RepositoryItem)categoryObj).getPropertyValue("castoramaDocuments");
                } else if ((pivotCategoryObj != null) && (pivotCategoryObj instanceof RepositoryItem)) {
                    documents = (List<RepositoryItem>) ((RepositoryItem)pivotCategoryObj).getPropertyValue("castoramaDocuments");
                }
            }  // end if
            pRequest.setParameter("documentMap", filterDocumentsByType(documents, numberDocumentPerSection));
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastDocumentSearchDroplet.service");
            }
        }  // end try-finally

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }

    /**
     * Checks if category is "Pivot", if not, gets its parent category and checks it.
     * Returns the first parent pivot category of the given item.  
     *
     * @param  GSAItem pItem
     *
     * @return pivot category
     */
    private GSAItem returnPivotCategory(GSAItem pItem) {
        GSAItem tmpItem;
        if (!(Boolean) pItem.getPropertyValue(PIVOT)) {
            GSAItem aItem = (GSAItem) pItem.getPropertyValue(PARENT_CATEGORY);
            if (aItem != null) {
                tmpItem = returnPivotCategory(aItem);
            } else {
                tmpItem = pItem;
            }
        } else {
            tmpItem = pItem;
        }
        return tmpItem;
    }

    /**
     * Filter pDocuments list by document's type. Forms map - Map<key,
     * Object[]>, where key-"document type", value - array of possible values
     * for this key from pDocuments list.
     *
     * @param  pDocuments                list of items
     * @param  pDocumentsCountPerSection parameter
     *
     * @return map of filtered by type document items
     */
    private Map<String, Object[]> filterDocumentsByType(List<RepositoryItem> pDocuments,
                                                        int pDocumentsCountPerSection) {
        Map<String, Object[]> documentMap = new HashMap<String, Object[]>();
        Set<RepositoryItem> ideaSet = new HashSet<RepositoryItem>();
        Set<RepositoryItem> rightProductSet = new HashSet<RepositoryItem>();
        Set<RepositoryItem> howToSet = new HashSet<RepositoryItem>();
        Set<RepositoryItem> castocheSet = new HashSet<RepositoryItem>();
        if ((pDocuments != null) && !pDocuments.isEmpty() && (pDocumentsCountPerSection > 0)) {
            for (RepositoryItem repItem : pDocuments) {
                if (repItem != null) {
                    switch ((Integer) repItem.getPropertyValue("documentType")) {
                        case 0:
                            if (ideaSet.size() < pDocumentsCountPerSection) {
                                ideaSet.add(repItem);
                            }
                            break;

                        case 1:
                            if (rightProductSet.size() < pDocumentsCountPerSection) {
                                rightProductSet.add(repItem);
                            }
                            break;

                        case 2:
                            if (howToSet.size() < pDocumentsCountPerSection) {
                                howToSet.add(repItem);
                            }
                            break;
                            
                        case 3:
                            if (howToSet.size() < pDocumentsCountPerSection) {
                                howToSet.add(repItem);
                            }
                            break;
                    }
                }  // end if
                if ((ideaSet.size() >= pDocumentsCountPerSection) &&
                        (rightProductSet.size() >= pDocumentsCountPerSection) &&
                        (howToSet.size() >= pDocumentsCountPerSection) &&
                        (castocheSet.size() >= pDocumentsCountPerSection)) {
                    break;
                }
            }  // end for
        }  // end if

        documentMap.put("ideas", ideaSet.toArray());
        documentMap.put("rightProducts", rightProductSet.toArray());
        documentMap.put("howTo", howToSet.toArray());
        documentMap.put("castoche", castocheSet.toArray());
        return documentMap;
    }
}
