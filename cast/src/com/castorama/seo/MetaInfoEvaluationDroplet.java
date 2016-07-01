package com.castorama.seo;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.core.util.StringUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class MetaInfoEvaluationDroplet extends DynamoServlet {

    /** FIRST constant. */
    private static final String FIRST = "1";

    /** PAGENUM_PREFIX constant. */
    private static final String PAGENUM_PREFIX = " - P";

    /** TITLE_PARAM constant. */
    private static final String TITLE_PARAM = "title";

    /** NUM_TEXT constant. */
    private static final String NUM_TEXT = "numText";

    /** PAGE_NUM constant. */
    private static final String PAGE_NUM = "pageNum";

    /** PARAMETER_VALUE constant. */
    private static final String PARAMETER_VALUE = "value";

    /** OPARAM_OUTPUT constant. */
    private static final String OPARAM_OUTPUT = "output";

    /** OPARAM_ERROR constant. */
    private static final String OPARAM_ERROR = "error";

    /** PARAMETER_TAG constant. */
    private static final String PARAMETER_TAG = "tag";

    /** PARAMETER_ITEM constant. */
    private static final String PARAMETER_ITEM = "item";

    /** PARAMETER_ID constant. */
    private static final String PARAMETER_ID = "id";

    /** productCatalog property. */
    private Repository mProductCatalog;

    /** tagToPropertyMap property. */
    private Map<String, String> mTagToPropertyMap = new HashMap<String, String>();

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
     * Returns tagToPropertyMap property.
     *
     * @return tagToPropertyMap property.
     */
    public Map<String, String> getTagToPropertyMap() {
        return mTagToPropertyMap;
    }

    /**
     * Sets the value of the tagToPropertyMap property.
     *
     * @param pTagToPropertyMap parameter to set.
     */
    public void setTagToPropertyMap(Map<String, String> pTagToPropertyMap) {
        mTagToPropertyMap = pTagToPropertyMap;
    }

    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    @SuppressWarnings("unchecked")
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String id = pRequest.getParameter(PARAMETER_ID);
        String item = pRequest.getParameter(PARAMETER_ITEM);
        String tag = pRequest.getParameter(PARAMETER_TAG);
        RepositoryItem repItem = null;
        try {
            repItem = getProductCatalog().getItem(id, item);
            if (repItem == null) {
                pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                if (isLoggingWarning()) {
                    logWarning("Null was returned instead of item with item-descriptor=" + item + " and id=" + id);
                }
                return;
            }
        } catch (RepositoryException e) {
            pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            logError(e);
            return;
        }
        String propertyName = null;
        if (tag != null) { 
            propertyName = getTagToPropertyMap().get(tag);
        }
        if (propertyName == null) {
            if (isLoggingDebug()) {
                logDebug("No pattern in property " + propertyName + " for tag " + tag);
            }
            pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            return;
        }
        String pattern = null;
        try {
            pattern = (String) repItem.getPropertyValue(propertyName);
        } catch (IllegalArgumentException e) {
            pattern = null;
        }
        if (pattern == null) {
            if (isLoggingDebug()) {
                logDebug("No pattern in property " + propertyName + " for " + item + " with id=" + id);
            }
            pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            return;
        }
        MetaPattern metaPattern = MetaPattern.compile(pattern);
        MetaParameter[] params = metaPattern.getMetaParameters();
        String formattedMessage = metaPattern.getFormattedMsg();
        if ((params == null) || (params.length == 0)) {
            if (isLoggingDebug()) {
                logDebug("No pattern specified");
            }
            pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
            return;
        }
        Object[] evalResult = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String propertName = params[i].getParam().trim();
            Object propertyValue = null;
            if (propertName.indexOf('.') > -1) {
                List<Object> itemsToGetPropertyFrom = new ArrayList<Object>();
                itemsToGetPropertyFrom.add(repItem);
                StringTokenizer st = new StringTokenizer(propertName, ".");
                for (int j = 0, length = st.countTokens(); j < length; j++) {
                    String subProperty = st.nextToken();
                    List<Object> itemsForNextIteration = new ArrayList<Object>();
                    for (int k = 0; k < itemsToGetPropertyFrom.size(); k++) {
                        Object val = null;
                        try {
                            val = DynamicBeans.getPropertyValue(itemsToGetPropertyFrom.get(k), subProperty);
                        } catch (PropertyNotFoundException e) {
                            logError(e);
                            continue;
                        }
                        if (val instanceof Collection) {
                            itemsForNextIteration.addAll((Collection) val);
                        } else {
                            if (val != null) {
                                itemsForNextIteration.add(val);
                            }
                        }
                    }
                    itemsToGetPropertyFrom = itemsForNextIteration;
                }  // end for
                evalResult[i] = getStringValue(itemsToGetPropertyFrom, params[i].getSeparator());
            } else {
                try {
                    propertyValue = DynamicBeans.getSubPropertyValue(repItem, params[i].getParam().trim());
                } catch (PropertyNotFoundException e) {
                    logError(e);
                    pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
                    continue;
                }
                String evalValue = null;
                if (propertyValue == null) {
                    evalValue = "";
                } else {
                    if (propertyValue instanceof Collection) {
                        evalValue = getStringValue((Collection<?>) propertyValue, params[i].getSeparator());
                    } else {
                        evalValue = propertyValue.toString();
                    }
                }
                evalResult[i] = evalValue;
            }  // end if-else

        }  // end for
        
        pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
        pRequest.setParameter(PARAMETER_VALUE, MessageFormat.format(formattedMessage, evalResult));
        if (tag.equalsIgnoreCase(TITLE_PARAM)){
            String pageNum = pRequest.getParameter(PAGE_NUM);
            if (!StringUtils.isEmpty(pageNum) && !pageNum.equalsIgnoreCase(FIRST)){
                pRequest.setParameter(NUM_TEXT, PAGENUM_PREFIX+pageNum);
            }
        }
    }

    /**
     * Returns stringValue property.
     *
     * @param  pCollection parameter to set.
     * @param  pSeparator  parameter to set.
     *
     * @return stringValue property.
     */
    private static String getStringValue(Collection<?> pCollection, char pSeparator) {
        String evalValue = "";
        for (Iterator<?> iterator = pCollection.iterator(); iterator.hasNext();) {
            Object obj = iterator.next();
            if (evalValue.length() > 0) {
                evalValue += pSeparator;
            }
            evalValue += obj.toString();
        }
        return evalValue;
    }

}
