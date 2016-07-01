package com.castorama.search.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.castorama.commerce.catalog.CastCatalogTools;

import atg.adapter.gsa.GSAItem;
import atg.commerce.catalog.CatalogTools;
import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.search.formhandlers.QueryFormHandler;

import atg.search.routing.command.search.QueryRequest;
import atg.search.routing.command.search.Result;
import atg.search.routing.command.search.SearchResultsResponse;
import atg.search.routing.command.search.StringConstraint;
import atg.search.routing.command.search.StringOp;

import atg.service.perfmonitor.PerformanceMonitor;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Returns faceted search result for document on category or product page.
 * Returns map of pairs document type to castorama document repository items
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastDocumentSearchDroplet extends DynamoServlet {
    private static final String PIVOT_PROPERTY_NAME = "enablePivotCategotyGlobalSearch";

    private static final String REGULAR_PROPERTY_NAME = "enableRegularCategoryGlobalSearch";

    private static final String PRODUCT_PROPERTY_NAME = "enableProductGlobalSearch";

    private static final String CAST_CATEGORIES = "castDocAncestorCategories.$repositoryId";

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

    /** mItemDescriptorName property */
    private String mItemDescriptorName;

    /** mQueryFormHandler property */
    private String mQueryFormHandlerPath;
    
    /** Catalog tools. */
    private CatalogTools mCatalogTools;

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
     * Returns itemDescriptorName property.
     *
     * @return itemDescriptorName property.
     */
    public String getItemDescriptorName() {
        return mItemDescriptorName;
    }

    /**
     * Sets the value of the itemDescriptorName property.
     *
     * @param pItemDescriptorName parameter to set.
     */
    public void setItemDescriptorName(String pItemDescriptorName) {
        mItemDescriptorName = pItemDescriptorName;
    }

    /**
     * Returns queryFormHandlerPath property.
     *
     * @return queryFormHandlerPath property.
     */
    public String getQueryFormHandlerPath() {
        return mQueryFormHandlerPath;
    }

    /**
     * Sets the value of the queryFormHandlerPath property.
     *
     * @param pQueryFormHandlerPath parameter to set.
     */
    public void setQueryFormHandlerPath(String pQueryFormHandlerPath) {
        mQueryFormHandlerPath = pQueryFormHandlerPath;
    }

    /**
     * Returns catalogTools property.
     *
     * @return catalogTools property.
     */
    public CatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    /**
     * Sets the value of the catalogTools property.
     *
     * @param tools parameter to set.
     */
    public void setCatalogTools(CatalogTools tools) {
        mCatalogTools = tools;
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
            Object documentObj = pRequest.getObjectParameter(DOCUMENT);
            String numberDocumentPerSectionStr = pRequest.getParameter(NUMBER_DOCUMENT_SECTION);
            int numberDocumentPerSection = 5;
            List<RepositoryItem> documents = null;
            if (!StringUtils.isBlank(numberDocumentPerSectionStr)) {
                numberDocumentPerSection = Integer.parseInt(numberDocumentPerSectionStr);
            }
            if (numberDocumentPerSection > 0) {
                if ((productObj != null) && (productObj instanceof RepositoryItem)) {
                    pivotCategoryObj = returnPivotCategory((GSAItem) ((RepositoryItem)productObj).getPropertyValue(PARENT_CATEGORY));
                    if ((pivotCategoryObj != null) &&
                        (pivotCategoryObj instanceof RepositoryItem)) {
                        StringConstraint con = createSearchConstraint(PRODUCT_PROPERTY_NAME, ((RepositoryItem) pivotCategoryObj).getRepositoryId());
                        documents =
                            performSearch(pRequest, pResponse, ((RepositoryItem) productObj).getItemDisplayName(), con);
                    }

                } else if ((categoryObj != null) && (categoryObj instanceof RepositoryItem)) {
                    StringConstraint con = createSearchConstraint(REGULAR_PROPERTY_NAME, ((RepositoryItem) categoryObj).getRepositoryId());
                    documents = performSearch(pRequest, pResponse, ((RepositoryItem) categoryObj).getItemDisplayName(), con);
                } else if ((pivotCategoryObj != null) && (pivotCategoryObj instanceof RepositoryItem)) {
                    StringConstraint con = createSearchConstraint(PIVOT_PROPERTY_NAME, ((RepositoryItem) pivotCategoryObj).getRepositoryId());
                    documents =
                        performSearch(pRequest, pResponse, ((RepositoryItem) pivotCategoryObj).getItemDisplayName(), con);
                    
                } else if ((documentObj != null) && (documentObj instanceof RepositoryItem)) {
                    List<String> keywords = (List) ((RepositoryItem) documentObj).getPropertyValue("keywords");
                    if ((keywords != null) && !keywords.isEmpty() && (getAdvicePageKeywordsForSearchCount() > 0)) {
                        for (int i = 0;
                                 i <
                                 ((keywords.size() < getAdvicePageKeywordsForSearchCount())
                                      ? keywords.size() : getAdvicePageKeywordsForSearchCount()); i++) {
                            documents = performSearch(pRequest, pResponse, keywords.get(i), null);
                            if (!documents.isEmpty()) {
                                break;
                            }
                        }
                    }
                }  // end if-else
            }  // end if
            pRequest.setParameter("documentMap", filterDocumentsByType(documents, numberDocumentPerSection));
        } finally {
            if (PerformanceMonitor.isEnabled()) {
                PerformanceMonitor.endOperation("search", "TEST:CastDocumentSearchDroplet.service");
            }
        }  // end try-finally

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

    }
    
    
    private StringConstraint createSearchConstraint(String pName, String value){
        RepositoryItem castCatalog = ((CastCatalogTools) getCatalogTools()).getCastoramaCatalog();
        if (castCatalog.getPropertyValue(pName) instanceof Boolean){
            Boolean productFlag = (Boolean)castCatalog.getPropertyValue(pName);
            StringConstraint con = null;
            if (!productFlag) {
                con = new StringConstraint();
                con.setOperation(StringOp.EQUAL);
                con.setProperty(CAST_CATEGORIES);
                con.setValue(value);
            }
            return con;
        }
        return null;
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
     * Perform search operation based on pQuestion parameters. Then forms list
     * of repository document's items. Return formed list of repository items.
     *
     * @param  pRequest  request
     * @param  pResponse response
     * @param  pQuestion question parameter for search
     *
     * @return list of castoramaDocment's repository items
     */
    private List<RepositoryItem> performSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
                                               String pQuestion, StringConstraint constraint) {
        List<RepositoryItem> documents = new ArrayList<RepositoryItem>();
        if ((getRepository() != null) && !StringUtils.isBlank(getItemDescriptorName())) {
            if (!StringUtils.isBlank(getQueryFormHandlerPath())) {

                try {
                    QueryFormHandler qfh = (QueryFormHandler)pRequest.resolveName(getQueryFormHandlerPath());
                    if (qfh != null && qfh.getSearchRequest() instanceof QueryRequest){
                        if (constraint != null){
                            ((QueryRequest) qfh.getSearchRequest()).setDocumentSetConstraints(constraint);
                        }
                        ((QueryRequest) qfh.getSearchRequest()).setQuestion(pQuestion);
                        qfh.handleSearch(pRequest, pResponse);
                        if ((qfh.getSearchResponse() != null) &&
                                (qfh.getSearchResponse() instanceof SearchResultsResponse)) {
                            List<Result> searchResults =
                                ((SearchResultsResponse) qfh.getSearchResponse()).getResults();
                            if ((searchResults != null) && !searchResults.isEmpty()) {
                                for (Result searchResult : searchResults) {
                                    if (searchResult.getDocument().getProperties().get("$repositoryId") != null) {
                                        RepositoryItem document =
                                            getRepository().getItem((String) searchResult.getDocument().getProperties().get("$repositoryId"),
                                                                    getItemDescriptorName().trim());
                                        if (document != null) {
                                            documents.add(document);
                                        }
                                    }
                                }
                            } else {
                                return documents;
                            }
                        }
                    }
                } catch (IOException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                } catch (ServletException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                } catch (RepositoryException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                }catch (ClassCastException e) {
                    if (isLoggingError()) {
                        logError(e.getMessage());
                    }
                }  // end try-catch
            }  // end if
        }  // end if
        return documents;
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
                                castocheSet.add(repItem);
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
