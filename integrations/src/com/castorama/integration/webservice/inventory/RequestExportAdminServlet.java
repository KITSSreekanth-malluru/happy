package com.castorama.integration.webservice.inventory;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceAdminServlet;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class RequestExportAdminServlet extends ServiceAdminServlet {

    //region Statics
    public static final SimpleDateFormat rqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final String MESSAGE_NO_DATA = "There is no data to display.\n";
    private static final String ENCODING = "UTF-8";
    private static final String RQL_QUERY = "date(\"%s\") <= requestDate AND requestDate <= date(\"%s\") ORDER BY requestDate ASC";
    private static final String WEB_SERVICE_REQUESTS_DESCRIPTOR = "inventoryWebServiceRequests";
    private static final String REQUEST_DATE = "requestDate";
    private static final String STORE_ID = "storeId";
    private static final String ORDER_ID = "orderId";
    private static final String ORDER_ITEMS = "orderItems";
    private static final String REQUEST_STATUS = "requestStatus";
    private static final String BEGIN_DATE = "beginDate";
    private static final String END_DATE = "endDate";
    private static final String EXPORT_TYPE = "exportType";
    private static final String FILE_EXPORT_TYPE = "file";
    //endregion

    public RequestExportAdminServlet(Object pService, Nucleus pNucleus) {
        super(pService, pNucleus);
    }

    protected void printAdmin(HttpServletRequest pRequest, HttpServletResponse pResponse, ServletOutputStream pOut)
            throws ServletException, IOException {
        RequestsExportManager exportManager = (RequestsExportManager) this.mService;
        Repository webServicesLogRepository = exportManager.getWebServicesLogRepository();

        pOut.println("<h1>Export inventory check web service requests</h1>");

        pOut.println("<p>Insert begin, end date (format: yyyy-MM-dd) of request export and select export type</p>");
        printForm(pOut, pRequest);

        String beginDateString = pRequest.getParameter(BEGIN_DATE);
        String endDateString = pRequest.getParameter(END_DATE);
        String exportType = pRequest.getParameter(EXPORT_TYPE);
        Date beginDate = null, endDate = null;

        try {
            if (beginDateString != null && endDateString != null && exportType != null) {
                boolean dateIsCorrect = true;
                try {
                    beginDate = rqlDateFormat.parse(beginDateString);
                    endDate = rqlDateFormat.parse(endDateString);
                } catch (Throwable throwable) {
                    pOut.println("<p>Please, insert correct begin and end date (format: yyyy-MM-dd) for export</p>");
                    dateIsCorrect = false;
                }
                if (dateIsCorrect) {
                    if (exportType.equals(FILE_EXPORT_TYPE)) {
                        FileOutputStream fileOutputStream = null;
                        try {
                            File exportFile = new File(exportManager.getRootDirectoryPath()
                                    + exportManager.getExportFilePrefix()
                                    + fileNameDateFormat.format(Calendar.getInstance().getTime()));
                            if (!exportFile.exists()) {
                                boolean isFileCreated = exportFile.createNewFile();
                                if (isFileCreated) {
                                    fileOutputStream = new FileOutputStream(exportFile);
                                    printQueryResult(webServicesLogRepository, beginDate, endDate, fileOutputStream);
                                    pOut.println("<p>Data was exported successfully</p>");
                                }
                            }
                        } finally {
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                        }
                    } else {
                        printHtmlQueryResult(webServicesLogRepository, beginDate, endDate, pOut);
                    }
                }
            } else {
                pOut.println("<p>Please, fill all input fields</p>");
            }
        } catch (Throwable e) {
            pOut.print(e.toString());
        }

        super.printAdmin(pRequest, pResponse, pOut);
    }

    private void printHtmlQueryResult(Repository webServicesLogRepository, Date beginDate, Date endDate, OutputStream outputStream)
            throws IOException, RepositoryException {
        RepositoryItem[] queryResult = getQueryResult(webServicesLogRepository, beginDate, endDate);
        if (queryResult != null && queryResult.length > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("<table border><tbody>");
            builder.append("<tr>");
            builder.append("<th>").append("Store id").append("</th>");
            builder.append("<th>").append("Order id").append("</th>");
            builder.append("<th>").append("Request status").append("</th>");
            builder.append("<th>").append("Request date").append("</th>");
            builder.append("<th>").append("Order items").append("</th>");
            builder.append("</tr>");

            for (RepositoryItem item : queryResult) {
                String storeId = (String) item.getPropertyValue(STORE_ID);
                String orderId = (String) item.getPropertyValue(ORDER_ID);
                String requestStatus = (String) item.getPropertyValue(REQUEST_STATUS);
                Set itemsIds = (Set) item.getPropertyValue(ORDER_ITEMS);
                Date requestDate = (Date) item.getPropertyValue(REQUEST_DATE);

                builder.append("<tr class=\"even\">");
                builder.append("<th>").append(storeId).append("</th>");
                builder.append("<th>").append(orderId).append("</th>");
                builder.append("<th>").append(requestStatus).append("</th>");
                builder.append("<th>").append(requestDate).append("</th>");
                builder.append("<th>").append(itemsIds).append("</th>");
                builder.append("</tr>");

            }
            builder.append("</tbody></table>");
            outputStream.write(builder.toString().getBytes(ENCODING));
        } else {
            outputStream.write(MESSAGE_NO_DATA.getBytes(ENCODING));
        }
    }

    private void printQueryResult(Repository webServicesLogRepository, Date beginDate, Date endDate, OutputStream outputStream)
            throws IOException, RepositoryException {
        RepositoryItem[] queryResult = getQueryResult(webServicesLogRepository, beginDate, endDate);
        if (queryResult != null && queryResult.length > 0) {
            StringBuilder builder = new StringBuilder();

            for (RepositoryItem item : queryResult) {
                String storeId = (String) item.getPropertyValue(STORE_ID);
                String orderId = (String) item.getPropertyValue(ORDER_ID);
                String requestStatus = (String) item.getPropertyValue(REQUEST_STATUS);
                Set itemsIds = (Set) item.getPropertyValue(ORDER_ITEMS);
                Date requestDate = (Date) item.getPropertyValue(REQUEST_DATE);

                builder.append(storeId).append(",");
                builder.append(orderId).append(",");
                builder.append(requestStatus).append(",");
                builder.append(requestDate).append(",");
                builder.append(itemsIds).append("\n");
            }
            outputStream.write(builder.toString().getBytes(ENCODING));
        } else {
            outputStream.write(MESSAGE_NO_DATA.getBytes(ENCODING));
        }
    }

    private RepositoryItem[] getQueryResult(Repository webServicesLogRepository, Date beginDate, Date endDate)
            throws RepositoryException {
        String rqlRequest = String.format(RQL_QUERY, rqlDateFormat.format(beginDate), rqlDateFormat.format(endDate));
        RepositoryView webServicesLogView = webServicesLogRepository.getView(WEB_SERVICE_REQUESTS_DESCRIPTOR);
        RqlStatement byDateStatement = RqlStatement.parseRqlStatement(rqlRequest);

        return byDateStatement.executeQuery(webServicesLogView, null);
    }

    private void printForm(ServletOutputStream outputStream, HttpServletRequest pRequest) throws IOException {
        String beginDateString = pRequest.getParameter(BEGIN_DATE);
        String endDateString = pRequest.getParameter(END_DATE);
        String exportType = pRequest.getParameter(EXPORT_TYPE);
        exportType = (exportType != null) ? exportType : "web";
        beginDateString = (beginDateString != null) ? beginDateString : "";
        endDateString = (endDateString != null) ? endDateString : "";
        outputStream.print("<form action=\"");
        outputStream.print(formatServiceName(pRequest.getPathInfo(), pRequest));
        outputStream.println("\" method=POST>");
        outputStream.println("Begin date: <input type=TEXT name=beginDate value=" + beginDateString + "><br/>");
        outputStream.println("End date:&nbsp;&nbsp;&nbsp;&nbsp;<input type=TEXT name=endDate value=" + endDateString + "><br/><br/>");
        outputStream.println("Select export type:<br/>");
        outputStream.println("<input type=RADIO name=exportType value=web " + (!(exportType.equals(FILE_EXPORT_TYPE)) ? "checked" : "") + ">Web output<br/>");
        outputStream.println("<input type=RADIO name=exportType value=file " + ((exportType.equals(FILE_EXPORT_TYPE)) ? "checked" : "") + ">File output<br/><br/>");
        outputStream.println("<input type=submit name=submit value=Submit>");
        outputStream.println("</form>");
    }
}
