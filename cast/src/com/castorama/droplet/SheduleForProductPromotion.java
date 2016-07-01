package com.castorama.droplet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class SheduleForProductPromotion extends DynamoServlet {

    private Date startDateCommerce;
    private Date endDateCommerce;
    private Date startDateContenu;
    private Date endDateContenu;
    private boolean isVisibleCommerce;
    private boolean isVisibleContenu;


    /**
     * Sting constants
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * Input parameters
     */
    static final ParameterName START_DATE_COMMERCE = ParameterName.getParameterName("startDateCommerce");
    static final ParameterName START_DATE_CONTENU = ParameterName.getParameterName("startDateContenu");
    static final ParameterName END_DATE_COMMERCE = ParameterName.getParameterName("endDateCommerce");
    static final ParameterName END_DATE_CONTENU = ParameterName.getParameterName("endDateContenu");

    /**
     * Output parameters
     */
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    static final String IS_VISIBLE_COMMERCE = "isVisibleCommerce";
    static final String IS_VISIBLE_CONTENU = "isVisibleContenu";

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
            IOException {
        String dateStartCommerceParameter = pRequest.getParameter(START_DATE_COMMERCE);
        String dateEndCommerceParameter = pRequest.getParameter(END_DATE_COMMERCE);
        String dateStartContenuParameter = pRequest.getParameter(START_DATE_CONTENU);
        String dateEndContenuParameter = pRequest.getParameter(END_DATE_CONTENU);
        startDateCommerce = parseDate(dateStartCommerceParameter);
        endDateCommerce = parseDate(dateEndCommerceParameter);
        isVisibleCommerce = checkView(startDateCommerce, endDateCommerce);
        startDateContenu = parseDate(dateStartContenuParameter);
        endDateContenu = parseDate(dateEndContenuParameter);
        isVisibleContenu = checkView(startDateContenu, endDateContenu);
        if (isVisibleCommerce) {
            pRequest.setParameter(IS_VISIBLE_COMMERCE, isVisibleCommerce);
        }
        if (isVisibleContenu) {
            pRequest.setParameter(IS_VISIBLE_CONTENU, isVisibleContenu);
        }
        pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
    }


    public Date parseDate(String dateParam) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        Date date = null;
        try {
            if (dateParam != null) {
                date = format.parse(dateParam);
            }
        } catch (ParseException e) {
            if (isLoggingError()) {
                logError("ParseException in com.castorama.droplet.SheduleForProductPromotio", e);
            }
        }
        return date;
    }

    public static boolean checkView(Date startDate, Date endDate) {
        Date currentDate = new Date();
        if (startDate == null || endDate == null) {
            return false;
        } else if (currentDate.after(startDate) && currentDate.before(endDate)) {
            return true;
        }
        return false;
    }

    public Date getStartDateCommerce() {
        return startDateCommerce;
    }

    public Date getStartDateContenu() {
        return startDateContenu;
    }

    public Date getEndDateCommerce() {
        return endDateCommerce;
    }

    public Date getEndDateContenu() {
        return endDateContenu;
    }

    public void setStartDateCommerce(Date startDateCommerce) {
        this.startDateCommerce = startDateCommerce;
    }

    public void setStartDateContenu(Date startDateContenu) {
        this.startDateContenu = startDateContenu;
    }

    public void setEndDateCommerce(Date endDateCommerce) {
        this.endDateCommerce = endDateCommerce;
    }

    public void setEndDateContenu(Date endDateContenu) {
        this.endDateContenu = endDateContenu;
    }
}
