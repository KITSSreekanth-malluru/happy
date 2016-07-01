package com.castorama.integration.troc;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryView;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class ThematiqueListDroplet extends DynamoServlet {
    public final static String OUTPUT              = "output";
    public final static String EMPTY               = "empty";
    public final static String TEMATIQUE_LIST      = "thematiqueList";
    public final static String TEMATIQUE_CONTAINER = "thematiqueContainer";
    public final static String AMOUNT              = "amount";
    public final static String CURR_INDEX          = "currentIndex";
    public final static String CAN_BACK            = "canBack";
    public final static String CAN_FORW            = "canForw";

    private GSARepository      mRepository;

    public GSARepository getRepository() {
        return mRepository;
    }

    public void setRepository(GSARepository pRepository) {
        mRepository = pRepository;
    }

    public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) throws ServletException,
            IOException {
        boolean canBack = true;
        boolean canForw = true;
//        int amount = (Integer) pReq.getLocalParameter(AMOUNT);
        int amount = 8;
        String currIndexStr = (String)(pReq.getLocalParameter(CURR_INDEX)!=null?pReq.getLocalParameter(CURR_INDEX):"");
        if (currIndexStr == null || currIndexStr.length()==0) {
            pReq.serviceParameter(EMPTY, pReq, pRes);
            return;
        }
        int currIndex = Integer.parseInt(currIndexStr);

        if (currIndex <= 0) {
            currIndex = 0;
            canBack = false;
        }
        
        int endIndex = amount + currIndex;
        AbstractList allThemList = getAllThematiqueList(pReq, pRes);

        if (allThemList == null || allThemList.isEmpty()) {
            if (isLoggingWarning()) {
                logWarning("There are no thematique in thematiqueContainer");
            }
            pReq.serviceParameter(EMPTY, pReq, pRes);
        } else {
            int allThemListSize = allThemList.size();
            if (endIndex >= allThemListSize) {
                currIndex -= (endIndex - allThemListSize);
                if (allThemListSize % 2 != 0) {
                    currIndex++;
                }
                endIndex = allThemListSize;
                canForw = false;
            }
            if (currIndex < 0 ) currIndex = 0;
            ArrayList<RepositoryItem> themList = new ArrayList<RepositoryItem>();
            for (int i = currIndex; i < endIndex; i++) {
                themList.add((RepositoryItem) allThemList.get(i));
            }

            pReq.setParameter(CAN_BACK, canBack);
            pReq.setParameter(CAN_FORW, canForw);
            pReq.setParameter(TEMATIQUE_LIST, themList);
            pReq.setParameter(CURR_INDEX, currIndex);
            pReq.setParameter(AMOUNT, themList.size());
            pReq.serviceParameter(OUTPUT, pReq, pRes);

        }
    }

    private AbstractList getAllThematiqueList(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes) {
        RepositoryView themContView;
        AbstractList allThemList = null;
        try {
            themContView = getRepository().getView(TEMATIQUE_CONTAINER);

            QueryBuilder themContQueryBuilder = themContView.getQueryBuilder();
            Query getAllThemContainers = themContQueryBuilder.createUnconstrainedQuery();
            RepositoryItem[] themContList = themContView.executeQuery(getAllThemContainers);
            if (themContList == null) {
                if (isLoggingWarning()) {
                    logWarning("There are no thematiqueContainer in repository");
                }
            } else if (themContList.length != 1) {
                if (isLoggingWarning()) {
                    logWarning("Should be only one thematiqueContainer");
                }
            } else {
                allThemList = (AbstractList) themContList[0].getPropertyValue(TEMATIQUE_LIST);
            }
        } catch (RepositoryException e) {
            if (isLoggingError()) {
                logError(e);
            }
        }
        return allThemList;
    }

}
