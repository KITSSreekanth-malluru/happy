package com.castorama.search.droplet;

import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet render table: emulate fulfilling table by columns.
 *
 * <p>Description of AjouterTableRenderer parameters:</p>
 *
 * <dl>
 * <dt>outputTableStart</dt>
 * <dd>is rendered one time during rendering table at the beginning: should
 * render start table tag</dd>
 *
 * <dt>outputTableEnd</dt>
 * <dd>is rendered one time during rendering table at the end: should render end
 * table tag</dd>
 *
 * <dt>outputRowStart</dt>
 * <dd>is rendered for each table row at the row beginning</dd>
 *
 * <dt>outputRowEnd</dt>
 * <dd>is rendered for each table row at the row end</dd>
 *
 * <dt>outputColumnStart</dt>
 * <dd>is rendered for each table column at the column beginning</dd>
 *
 * <dt>outputColumnEnd</dt>
 * <dd>is rendered for each table column at the column end</dd>
 *
 * <dt>elementName</dt>
 * <dd>input parameter for passing element name for rendering this element at
 * the UI</dd>
 *
 * <dt>output</dt>
 * <dd>rendered for each element from the passed list</dd>
 *
 * <dt>element</dt>
 * <dd>element from passed list</dd>
 *
 * <dt>arrayToRender</dt>
 * <dd>input parameter for passing array to render, only List should passed(only
 * list is supported at the current version)</dd>
 *
 * <dt>maxRowCount</dt>
 * <dd>determines number of rows of table: if (maxRowCount > size of
 * arrayToRender || maxRowCount == 0) then maxRowCount = size of arrayToRender
 * </dd>
 *
 * <dt>maxColumnCount</dt>
 * <dd>input parameter - pass maximum column's count, if maxColumnCount
 * parameter isn't passed or equals 0, than parameter value determined
 * automatically on basis maxRowCount parameter passed: columnAmount = if modulo
 * of (size of arrayToRender / maxRowCount) > 0 then columnAmount = (size of
 * arrayToRender / maxRowCount) + 1, otherwise columnAmount = (size of
 * arrayToRender / maxRowCount)</dd>
 *
 * <dt>columnCount</dt>
 * <dd>Returns number of columns</dd>
 * </dl>
 *
 * @author Katsiaryna Sharstsiuk
 */
public class AjouterTableRenderer extends DynamoServlet {
    /** OUTPUT constant. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** OUTPUT_TABLE_START constant. */
    public static final ParameterName OUTPUT_TABLE_START = ParameterName.getParameterName("outputTableStart");

    /** OUTPUT_TABLE_END constant. */
    public static final ParameterName OUTPUT_TABLE_END = ParameterName.getParameterName("outputTableEnd");

    /** OUTPUT_ROW_START constant. */
    public static final ParameterName OUTPUT_ROW_START = ParameterName.getParameterName("outputRowStart");

    /** OUTPUT_ROW_END constant. */
    public static final ParameterName OUTPUT_ROW_END = ParameterName.getParameterName("outputRowEnd");

    /** OUTPUT_COLUMN_START constant. */
    public static final ParameterName OUTPUT_COLUMN_START = ParameterName.getParameterName("outputColumnStart");

    /** OUTPUT_COLUMN_END constant. */
    public static final ParameterName OUTPUT_COLUMN_END = ParameterName.getParameterName("outputColumnEnd");

    /** COLUMN_COUNT constant. */
    public static final String COLUMN_COUNT = "columnCount";

    /** ELEMENT_NAME constant. */
    public static final String ELEMENT_NAME = "elementName";

    /** ELEMENT_NAME constant. */
    public static final String ELEMENT = "element";

    /** ARRAY_TO_RENDER constant. */
    public static final String ARRAY_TO_RENDER = "arrayToRender";

    /** MAX_COLUMN_COUNT constant. */
    public static final String MAX_COLUMN_COUNT = "maxColumnCount";

    /** MAX_ROW_COUNT constant. */
    public static final String MAX_ROW_COUNT = "maxRowCount";

    /**
     * Emulate rendering table column by column.
     *
     * @param  pRequest  dynamo http request
     * @param  pResponse dynamo http response
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        String maxRowCountParam = pRequest.getParameter(MAX_ROW_COUNT);
        String maxColumnCountParam = pRequest.getParameter(MAX_COLUMN_COUNT);
        Object arrayToRenderObj = pRequest.getObjectParameter(ARRAY_TO_RENDER);
        String elementName = pRequest.getParameter(ELEMENT_NAME);

        int maxRowCount = 0;
        int maxColumnCount = 0;
        if (!StringUtils.isBlank(maxRowCountParam)) {
            try {
                maxRowCount = Integer.parseInt(maxRowCountParam);
            } catch (NumberFormatException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
        }
        if (!StringUtils.isBlank(maxColumnCountParam)) {
            try {
                maxColumnCount = Integer.parseInt(maxColumnCountParam);
            } catch (NumberFormatException e) {
                if (isLoggingError()) {
                    logError(e.getMessage());
                }
            }
        }
        if (StringUtils.isBlank(elementName)) {
            elementName = ELEMENT;
        }
        if ((arrayToRenderObj != null) && (arrayToRenderObj instanceof List) && !((List) arrayToRenderObj).isEmpty()) {
            List arrayToRender = (List) arrayToRenderObj;
            int arraySize = arrayToRender.size();
            if ((maxRowCount > arraySize) || (maxRowCount == 0)) {
                maxRowCount = arraySize;
            }

            int columnCount = arraySize / maxRowCount;
            int g = arraySize % maxRowCount;
            if (g > 0) {
                ++columnCount;
            }
            if ((columnCount > maxColumnCount) && (maxColumnCount > 0)) {
                columnCount = maxColumnCount;
            }
            pRequest.serviceLocalParameter(OUTPUT_TABLE_START, pRequest, pResponse);
            pRequest.setParameter(COLUMN_COUNT, columnCount);
            for (int i = 0; i < maxRowCount; i++) {
                pRequest.serviceLocalParameter(OUTPUT_ROW_START, pRequest, pResponse);
                if ((i == g) && (i != 0)) {
                    columnCount--;
                }
                for (int j = 0; j < columnCount; j++) {
                    pRequest.serviceLocalParameter(OUTPUT_COLUMN_START, pRequest, pResponse);
                    pRequest.setParameter(elementName, arrayToRender.get(i + (maxRowCount * j)));
                    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
                    pRequest.serviceLocalParameter(OUTPUT_COLUMN_END, pRequest, pResponse);
                }
                pRequest.serviceLocalParameter(OUTPUT_ROW_END, pRequest, pResponse);
            }
            pRequest.serviceLocalParameter(OUTPUT_TABLE_END, pRequest, pResponse);
        }  // end if

    }
}
