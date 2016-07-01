package com.castorama.droplet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author Epam Team
 */
public class ConseilsForumsFiltersDroplet extends DynamoServlet {

    private static final int MAX_COLUMNS_NUM_1_CAT = 8;
    
    private static final int MAX_COLUMNS_NUM_2_CAT = 4;
    
    private static final int MAX_COLUMNS_NUM_3_CAT_BIG = 3;
    private static final int MAX_COLUMNS_NUM_3_CAT_SMALL = 2;
    
    private static final int MAX_COLUMNS_NUM_4_CAT = 2;
    
    private static final int MIN_FILTERS_PER_COLUMN = 3;
    
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    
    
    /** productCatalogRepository property */
    private Repository mProductCatalogRepository;
    
    /**
     * Returns productCatalogRepository property.
     *
     * @return productCatalogRepository property.
     */
    public Repository getProductCatalogRepository() {
        return mProductCatalogRepository;
    }

    /**
     * Sets the value of the productCatalogRepository property.
     *
     * @param pProductCatalogRepository parameter to set.
     */
    public void setProductCatalogRepository(Repository pProductCatalogRepository) {
        mProductCatalogRepository = pProductCatalogRepository;
    }
    
    /**
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException
     * @throws IOException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        try {
            RepositoryView catsView = getProductCatalogRepository().getView("castoDocFilterTagCategory");
            RqlStatement catsStatement = RqlStatement.parseRqlStatement("ALL ORDER BY tagCatNumber");
            RepositoryItem[] allCatsItems = catsStatement.executeQuery(catsView, new Object[] {});
            
            Map<RepositoryItem, List<List<RepositoryItem>>> filtersCats = new LinkedHashMap<RepositoryItem, List<List<RepositoryItem>>>();
            
            if (allCatsItems.length > 0) {
                RepositoryView filtersView = getProductCatalogRepository().getView("castoDocFilterTag");
                RqlStatement filtersStatement = RqlStatement.parseRqlStatement("tagCategory = ?0 ORDER BY tagNumber");
                
                List<RepositoryItem> catsItems = new ArrayList<RepositoryItem>();
                for (RepositoryItem catItem : allCatsItems) {
                    if (filtersStatement.executeCountQuery(filtersView, new Object[] { catItem.getRepositoryId() }) > 0) {
                        catsItems.add(catItem);
                    }
                }
                
                if (catsItems.size() == 1) {
                    RepositoryItem cat = catsItems.get(0);
                    
                    RepositoryItem[] filtersItems = filtersStatement.executeQuery(filtersView, new Object[] { cat.getRepositoryId() });
                    
                    int colsNum = 0;
                    int colsNumWithAdditionalFilters = 0;
                    int filtersPerColumn = filtersItems.length / MAX_COLUMNS_NUM_1_CAT;
                    if (filtersPerColumn < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn = MIN_FILTERS_PER_COLUMN;
                        colsNum = (int) Math.ceil(filtersItems.length / (double)filtersPerColumn);
                    } else {
                        colsNum = MAX_COLUMNS_NUM_1_CAT;
                        colsNumWithAdditionalFilters = filtersItems.length % colsNum;
                    }
                    
                    List<List<RepositoryItem>> filterCols = new ArrayList<List<RepositoryItem>>(colsNum);
                    for (int i = 0; i < colsNum; i++) {
                        filterCols.add(new ArrayList<RepositoryItem>());
                    }
                    
                    int currFilterNumInCurrCol = 0;
                    int currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn) || 
                                ((currFilterNumInCurrCol == filtersPerColumn) && (currColNum < colsNumWithAdditionalFilters)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat, filterCols);
                    
                } else if (catsItems.size() == 2) {
                    RepositoryItem cat1 = catsItems.get(0);
                    RepositoryItem cat2 = catsItems.get(1);
                    
                    RepositoryItem[] filtersItems1 = filtersStatement.executeQuery(filtersView, new Object[] { cat1.getRepositoryId() });
                    RepositoryItem[] filtersItems2 = filtersStatement.executeQuery(filtersView, new Object[] { cat2.getRepositoryId() });
                    
                    int catNotToRecalculate = 0;
                    
                    int colsNum1 = 0;
                    int filtersPerColumn1 = filtersItems1.length / MAX_COLUMNS_NUM_2_CAT;
                    int colsNumWithAdditionalFilters1 = 0;
                    if (filtersPerColumn1 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn1 = MIN_FILTERS_PER_COLUMN;
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)filtersPerColumn1);
                    } else {
                        colsNum1 = MAX_COLUMNS_NUM_2_CAT;
                        colsNumWithAdditionalFilters1 = filtersItems1.length % colsNum1;
                    }
                    
                    int maxfiltersPerColumn = filtersPerColumn1;
                    if (colsNumWithAdditionalFilters1 > 0) {
                        maxfiltersPerColumn++;
                    }
                    
                    int colsNum2 = 0;
                    int filtersPerColumn2 = filtersItems2.length / MAX_COLUMNS_NUM_2_CAT;
                    int colsNumWithAdditionalFilters2 = 0;
                    if (filtersPerColumn2 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn2 = MIN_FILTERS_PER_COLUMN;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)filtersPerColumn2);
                    } else {
                        colsNum2 = MAX_COLUMNS_NUM_2_CAT;
                        colsNumWithAdditionalFilters2 = filtersItems2.length % colsNum2;
                    }
                    
                    if (colsNumWithAdditionalFilters2 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn2 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn2 + 1;
                            catNotToRecalculate = 1;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn2) {
                            maxfiltersPerColumn = filtersPerColumn2;
                            catNotToRecalculate = 1;
                        }
                    }
                    
                    if (catNotToRecalculate == 1) {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                    } else {
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                    }
                    
                    
                    List<List<RepositoryItem>> filterCols1 = new ArrayList<List<RepositoryItem>>(colsNum1);
                    for (int i = 0; i < colsNum1; i++) {
                        filterCols1.add(new ArrayList<RepositoryItem>());
                    }
                    int currFilterNumInCurrCol = 0;
                    int currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems1) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn1) || 
                                ((currFilterNumInCurrCol == filtersPerColumn1) && (currColNum < colsNumWithAdditionalFilters1)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols1.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat1, filterCols1);
                    
                    List<List<RepositoryItem>> filterCols2 = new ArrayList<List<RepositoryItem>>(colsNum2);
                    for (int i = 0; i < colsNum2; i++) {
                        filterCols2.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems2) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn2) || 
                                ((currFilterNumInCurrCol == filtersPerColumn2) && (currColNum < colsNumWithAdditionalFilters2)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols2.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat2, filterCols2);
                    
                } else if (catsItems.size() == 3) {
                    
                    RepositoryItem cat1 = catsItems.get(0);
                    RepositoryItem cat2 = catsItems.get(1);
                    RepositoryItem cat3 = catsItems.get(2);
                    
                    RepositoryItem[] filtersItems1 = filtersStatement.executeQuery(filtersView, new Object[] { cat1.getRepositoryId() });
                    RepositoryItem[] filtersItems2 = filtersStatement.executeQuery(filtersView, new Object[] { cat2.getRepositoryId() });
                    RepositoryItem[] filtersItems3 = filtersStatement.executeQuery(filtersView, new Object[] { cat3.getRepositoryId() });
                    
                    int preCols1 = MAX_COLUMNS_NUM_3_CAT_BIG;
                    int preCols2 = MAX_COLUMNS_NUM_3_CAT_BIG;
                    int preCols3 = MAX_COLUMNS_NUM_3_CAT_SMALL;
                    if (filtersItems3.length > filtersItems2.length) {
                        preCols2 = MAX_COLUMNS_NUM_3_CAT_SMALL;
                        preCols3 = MAX_COLUMNS_NUM_3_CAT_BIG;
                        if (filtersItems2.length > filtersItems1.length) {
                            preCols2 = MAX_COLUMNS_NUM_3_CAT_BIG;
                            preCols1 = MAX_COLUMNS_NUM_3_CAT_SMALL;
                        }
                    } else if (filtersItems3.length > filtersItems1.length) {
                        preCols1 = MAX_COLUMNS_NUM_3_CAT_SMALL;
                        preCols3 = MAX_COLUMNS_NUM_3_CAT_BIG;
                    }
                    
                    int catNotToRecalculate = 0;
                    
                    int colsNum1 = 0;
                    int filtersPerColumn1 = filtersItems1.length / preCols1;
                    int colsNumWithAdditionalFilters1 = 0;
                    if (filtersPerColumn1 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn1 = MIN_FILTERS_PER_COLUMN;
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)filtersPerColumn1);
                    } else {
                        colsNum1 = preCols1;
                        colsNumWithAdditionalFilters1 = filtersItems1.length % preCols1;
                    }
                    
                    int maxfiltersPerColumn = filtersPerColumn1;
                    if (colsNumWithAdditionalFilters1 > 0) {
                        maxfiltersPerColumn++;
                    }
                    
                    int colsNum2 = 0;
                    int filtersPerColumn2 = filtersItems2.length / preCols2;
                    int colsNumWithAdditionalFilters2 = 0;
                    if (filtersPerColumn2 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn2 = MIN_FILTERS_PER_COLUMN;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)filtersPerColumn2);
                    } else {
                        colsNum2 = preCols2;
                        colsNumWithAdditionalFilters2 = filtersItems2.length % preCols2;
                    }
                    
                    if (colsNumWithAdditionalFilters2 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn2 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn2 + 1;
                            catNotToRecalculate = 1;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn2) {
                            maxfiltersPerColumn = filtersPerColumn2;
                            catNotToRecalculate = 1;
                        }
                    }
                    
                    int colsNum3 = 0;
                    int filtersPerColumn3 = filtersItems3.length / preCols3;
                    int colsNumWithAdditionalFilters3 = 0;
                    if (filtersPerColumn3 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn3 = MIN_FILTERS_PER_COLUMN;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)filtersPerColumn3);
                    } else {
                        colsNum3 = preCols3;
                        colsNumWithAdditionalFilters3 = filtersItems3.length % preCols3;
                    }
                    
                    if (colsNumWithAdditionalFilters3 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn3 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn3 + 1;
                            catNotToRecalculate = 2;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn3) {
                            maxfiltersPerColumn = filtersPerColumn3;
                            catNotToRecalculate = 2;
                        }
                    }
                    
                    
                    if (catNotToRecalculate == 0) {
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters3 = 0;
                        filtersPerColumn3 = maxfiltersPerColumn;
                    } else if (catNotToRecalculate == 1) {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters3 = 0;
                        filtersPerColumn3 = maxfiltersPerColumn;
                    } else {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                    }
                    
                    
                    List<List<RepositoryItem>> filterCols1 = new ArrayList<List<RepositoryItem>>(colsNum1);
                    for (int i = 0; i < colsNum1; i++) {
                        filterCols1.add(new ArrayList<RepositoryItem>());
                    }
                    int currFilterNumInCurrCol = 0;
                    int currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems1) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn1) || 
                                ((currFilterNumInCurrCol == filtersPerColumn1) && (currColNum < colsNumWithAdditionalFilters1)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols1.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat1, filterCols1);
                    
                    List<List<RepositoryItem>> filterCols2 = new ArrayList<List<RepositoryItem>>(colsNum2);
                    for (int i = 0; i < colsNum2; i++) {
                        filterCols2.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems2) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn2) || 
                                ((currFilterNumInCurrCol == filtersPerColumn2) && (currColNum < colsNumWithAdditionalFilters2)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols2.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat2, filterCols2);
                    
                    List<List<RepositoryItem>> filterCols3 = new ArrayList<List<RepositoryItem>>(colsNum3);
                    for (int i = 0; i < colsNum3; i++) {
                        filterCols3.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems3) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn3) || 
                                ((currFilterNumInCurrCol == filtersPerColumn3) && (currColNum < colsNumWithAdditionalFilters3)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols3.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat3, filterCols3);
                    
                } else if (catsItems.size() == 4) {
                    
                    RepositoryItem cat1 = catsItems.get(0);
                    RepositoryItem cat2 = catsItems.get(1);
                    RepositoryItem cat3 = catsItems.get(2);
                    RepositoryItem cat4 = catsItems.get(3);
                    
                    RepositoryItem[] filtersItems1 = filtersStatement.executeQuery(filtersView, new Object[] { cat1.getRepositoryId() });
                    RepositoryItem[] filtersItems2 = filtersStatement.executeQuery(filtersView, new Object[] { cat2.getRepositoryId() });
                    RepositoryItem[] filtersItems3 = filtersStatement.executeQuery(filtersView, new Object[] { cat3.getRepositoryId() });
                    RepositoryItem[] filtersItems4 = filtersStatement.executeQuery(filtersView, new Object[] { cat4.getRepositoryId() });
                    
                    int catNotToRecalculate = 0;
                    
                    int colsNum1 = 0;
                    int filtersPerColumn1 = filtersItems1.length / MAX_COLUMNS_NUM_4_CAT;
                    int colsNumWithAdditionalFilters1 = 0;
                    if (filtersPerColumn1 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn1 = MIN_FILTERS_PER_COLUMN;
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)filtersPerColumn1);
                    } else {
                        colsNum1 = MAX_COLUMNS_NUM_4_CAT;
                        colsNumWithAdditionalFilters1 = filtersItems1.length % colsNum1;
                    }
                    
                    int maxfiltersPerColumn = filtersPerColumn1;
                    if (colsNumWithAdditionalFilters1 > 0) {
                        maxfiltersPerColumn++;
                    }
                    
                    int colsNum2 = 0;
                    int filtersPerColumn2 = filtersItems2.length / MAX_COLUMNS_NUM_4_CAT;
                    int colsNumWithAdditionalFilters2 = 0;
                    if (filtersPerColumn2 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn2 = MIN_FILTERS_PER_COLUMN;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)filtersPerColumn2);
                    } else {
                        colsNum2 = MAX_COLUMNS_NUM_4_CAT;
                        colsNumWithAdditionalFilters2 = filtersItems2.length % colsNum2;
                    }
                    
                    if (colsNumWithAdditionalFilters2 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn2 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn2 + 1;
                            catNotToRecalculate = 1;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn2) {
                            maxfiltersPerColumn = filtersPerColumn2;
                            catNotToRecalculate = 1;
                        }
                    }
                    
                    int colsNum3 = 0;
                    int filtersPerColumn3 = filtersItems3.length / MAX_COLUMNS_NUM_4_CAT;
                    int colsNumWithAdditionalFilters3 = 0;
                    if (filtersPerColumn3 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn3 = MIN_FILTERS_PER_COLUMN;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)filtersPerColumn3);
                    } else {
                        colsNum3 = MAX_COLUMNS_NUM_4_CAT;
                        colsNumWithAdditionalFilters3 = filtersItems3.length % colsNum3;
                    }
                    
                    if (colsNumWithAdditionalFilters3 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn3 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn3 + 1;
                            catNotToRecalculate = 2;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn3) {
                            maxfiltersPerColumn = filtersPerColumn3;
                            catNotToRecalculate = 2;
                        }
                    }
                    
                    int colsNum4 = 0;
                    int filtersPerColumn4 = filtersItems4.length / MAX_COLUMNS_NUM_4_CAT;
                    int colsNumWithAdditionalFilters4 = 0;
                    if (filtersPerColumn4 < MIN_FILTERS_PER_COLUMN) {
                        filtersPerColumn4 = MIN_FILTERS_PER_COLUMN;
                        colsNum4 = (int) Math.ceil(filtersItems4.length / (double)filtersPerColumn4);
                    } else {
                        colsNum4 = MAX_COLUMNS_NUM_4_CAT;
                        colsNumWithAdditionalFilters4 = filtersItems4.length % colsNum4;
                    }
                    
                    if (colsNumWithAdditionalFilters4 > 0) {
                        if (maxfiltersPerColumn < (filtersPerColumn4 + 1)) {
                            maxfiltersPerColumn = filtersPerColumn4 + 1;
                            catNotToRecalculate = 3;
                        }
                    } else {
                        if (maxfiltersPerColumn < filtersPerColumn4) {
                            maxfiltersPerColumn = filtersPerColumn4;
                            catNotToRecalculate = 3;
                        }
                    }
                    
                    
                    if (catNotToRecalculate == 0) {
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters3 = 0;
                        filtersPerColumn3 = maxfiltersPerColumn;
                        colsNum4 = (int) Math.ceil(filtersItems4.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters4 = 0;
                        filtersPerColumn4 = maxfiltersPerColumn;
                    } else if (catNotToRecalculate == 1) {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters3 = 0;
                        filtersPerColumn3 = maxfiltersPerColumn;
                        colsNum4 = (int) Math.ceil(filtersItems4.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters4 = 0;
                        filtersPerColumn4 = maxfiltersPerColumn;
                    } else if (catNotToRecalculate == 2) {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                        colsNum4 = (int) Math.ceil(filtersItems4.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters4 = 0;
                        filtersPerColumn4 = maxfiltersPerColumn;
                    } else {
                        colsNum1 = (int) Math.ceil(filtersItems1.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters1 = 0;
                        filtersPerColumn1 = maxfiltersPerColumn;
                        colsNum2 = (int) Math.ceil(filtersItems2.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters2 = 0;
                        filtersPerColumn2 = maxfiltersPerColumn;
                        colsNum3 = (int) Math.ceil(filtersItems3.length / (double)maxfiltersPerColumn);
                        colsNumWithAdditionalFilters3 = 0;
                        filtersPerColumn3 = maxfiltersPerColumn;
                    }
                    
                    
                    List<List<RepositoryItem>> filterCols1 = new ArrayList<List<RepositoryItem>>(colsNum1);
                    for (int i = 0; i < colsNum1; i++) {
                        filterCols1.add(new ArrayList<RepositoryItem>());
                    }
                    int currFilterNumInCurrCol = 0;
                    int currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems1) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn1) || 
                                ((currFilterNumInCurrCol == filtersPerColumn1) && (currColNum < colsNumWithAdditionalFilters1)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols1.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat1, filterCols1);
                    
                    List<List<RepositoryItem>> filterCols2 = new ArrayList<List<RepositoryItem>>(colsNum2);
                    for (int i = 0; i < colsNum2; i++) {
                        filterCols2.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems2) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn2) || 
                                ((currFilterNumInCurrCol == filtersPerColumn2) && (currColNum < colsNumWithAdditionalFilters2)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols2.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat2, filterCols2);
                    
                    List<List<RepositoryItem>> filterCols3 = new ArrayList<List<RepositoryItem>>(colsNum3);
                    for (int i = 0; i < colsNum3; i++) {
                        filterCols3.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems3) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn3) || 
                                ((currFilterNumInCurrCol == filtersPerColumn3) && (currColNum < colsNumWithAdditionalFilters3)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols3.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat3, filterCols3);
                    
                    List<List<RepositoryItem>> filterCols4 = new ArrayList<List<RepositoryItem>>(colsNum4);
                    for (int i = 0; i < colsNum4; i++) {
                        filterCols4.add(new ArrayList<RepositoryItem>());
                    }
                    currFilterNumInCurrCol = 0;
                    currColNum = 0;
                    for (RepositoryItem filtersItem : filtersItems4) {
                        if (!((currFilterNumInCurrCol < filtersPerColumn4) || 
                                ((currFilterNumInCurrCol == filtersPerColumn4) && (currColNum < colsNumWithAdditionalFilters4)))) {
                            currColNum++;
                            currFilterNumInCurrCol = 0;
                        }
                        
                        filterCols4.get(currColNum).add(filtersItem);
                        currFilterNumInCurrCol++;
                    }
                    
                    filtersCats.put(cat4, filterCols4);
                    
                } else {
                    if (isLoggingWarning()) {
                        logWarning("Nubmer of not empty filter categories are not in 1 - 4 range");
                    }
                }
            } else {
                if (isLoggingWarning()) {
                    logWarning("There are no filter categories");
                }
            }
            
            pRequest.setParameter("filtersCats", filtersCats);
            pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        } catch (RepositoryException re) {
            if (isLoggingError()) {
                logError(re);
            }
        }
    }
}
