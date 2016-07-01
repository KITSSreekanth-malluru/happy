package com.castorama.droplet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.DictionariesKeysEnumeration;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CastQueryStringDroplet extends DynamoServlet {

    /** CHARSET_UTF_8 property */
    private static final String CHARSET_UTF_8 = "UTF-8";

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
    IOException {
        
        String url = pRequest.getRequestURIWithQueryString();
        String parametersNames = pRequest.getParameter("names");
        if (StringUtils.isEmpty(parametersNames)){
            logError("Parameter names is required for request: "+ (StringUtils.isEmpty(url)?"":url));
            return;
        }
        Enumeration<String> names = pRequest.getParameterNames();
        StringBuilder sb = new StringBuilder();
        while (names.hasMoreElements()){
            String el = (String) names.nextElement();
            if (parametersNames.lastIndexOf(el)!= -1){
                String elValue = pRequest.getLocalParameter(el)!=null?pRequest.getLocalParameter(el).toString():null;
                if (!StringUtils.isEmpty(elValue)){
                    sb.append(el);
                    sb.append('=');
                    sb.append(URLEncoder.encode(elValue,CHARSET_UTF_8));
//                    sb.append(elValue);
//                    System.out.println(sb.toString());
                    sb.append('&');
                }
            }
        }
        if (sb.length() > 0) sb.delete(sb.length()-1, sb.length());
        pRequest.serviceLocalParameter("output", pRequest, pResponse);
        pRequest.setParameter("qString", sb.toString());
    }

}
