/**
 * 
 */
package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * @author EPAM team
 *
 */
public class PermanentRedirectDroplet extends DynamoServlet {

    /**
     * 
     * 
     * @param pRequest parameter
     * @param pResponse parameter
     * 
     * @throws ServletException exception
     * @throws IOException exception
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        String url = pRequest.getParameter("url");
        if ((url != null) && (url.length() > 0)) {
            pResponse.setStatus(301);
            pResponse.setHeader("Location", url);
            pResponse.setHeader("Connection", "close");
        }
    }
}
