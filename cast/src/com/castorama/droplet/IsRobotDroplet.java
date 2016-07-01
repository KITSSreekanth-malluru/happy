package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 *
 * @author EPAM team
 */
public class IsRobotDroplet extends DynamoServlet {
    /**
     *
     * @param  pRequest  parameter
     * @param  pResponse parameter
     *
     * @throws ServletException exception
     * @throws IOException      exception
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
        if (pRequest.isBrowserType("robot")) {
            pRequest.serviceLocalParameter("true", pRequest, pResponse);
        } else {
            pRequest.serviceLocalParameter("end", pRequest, pResponse);
        }
    }
}
