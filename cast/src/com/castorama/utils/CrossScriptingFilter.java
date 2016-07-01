package com.castorama.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.GenericFilterService;
import atg.servlet.ServletUtil;

public class CrossScriptingFilter extends GenericFilterService {

    private static final Pattern pattern1 = Pattern.compile("(?i)<script>.*</script>");
    private static final Pattern pattern2 = Pattern.compile("(?i)[\"']\\s*javascript:.*[\"']");
    private static final Pattern pattern3 = Pattern.compile("(?i)eval\\(.*\\)");
    private static final Pattern pattern4 = Pattern.compile("<");
    private static final Pattern pattern5 = Pattern.compile(">");
    private static final Pattern pattern6 = Pattern.compile("\\(");
    private static final Pattern pattern7 = Pattern.compile("\\)");
    private static final Pattern pattern8 = Pattern.compile("\"");
    
    private static final String EMPTY = "";
    private static final String LESS_THAN = "&lt;";
    private static final String GREAT_THAN = "&gt;";
    private static final String LEFT_BRACKET = "&#40;";
    private static final String RIGHT_BRACKET = "&#41;";
    private static final String QUOT = "&quot;";

    protected boolean doFilterRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        DynamoHttpServletRequest dRequest = ServletUtil.getDynamoRequest(getServletContext(), servletRequest, servletResponse);
        for (Enumeration<String> e = dRequest.getParameterNames(); e.hasMoreElements();) {
            String param = e.nextElement();
            String[] vals = dRequest.getParameterValues(param);
            for (int i = 0; i < vals.length; i++) {
                vals[i] = cleanXSS(vals[i]);
            }
            if (vals.length == 1) {
                dRequest.setParameter(param, vals[0]);
            } else {
                dRequest.setParameter(param, vals);
            }
        }
        return true;
    }

    private String cleanXSS(String value) {

        value = pattern1.matcher(value).replaceAll(EMPTY);
        value = pattern2.matcher(value).replaceAll(EMPTY);
        value = pattern3.matcher(value).replaceAll(EMPTY);
        value = pattern4.matcher(value).replaceAll(LESS_THAN);
        value = pattern5.matcher(value).replaceAll(GREAT_THAN);
        value = pattern6.matcher(value).replaceAll(LEFT_BRACKET);
        value = pattern7.matcher(value).replaceAll(RIGHT_BRACKET);
        value = pattern8.matcher(value).replaceAll(QUOT);

        return value;
    }
}
