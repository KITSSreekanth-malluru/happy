package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class ComponentExistenceDroplet extends DynamoServlet {
    /** Oparam: output. */
    public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    /** Oparam: empty. */
    public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

    /** COMPONENT NAME constant */
    public static final String COMPONENT_NAME = "componentName";

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param  pRequest  ToDo: DOCUMENT ME!
     * @param  pResponse ToDo: DOCUMENT ME!
     *
     * @throws ServletException ToDo: DOCUMENT ME!
     * @throws IOException      ToDo: DOCUMENT ME!
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
                                                                                                       IOException {
    	String componentName = (String)pRequest.getObjectParameter(COMPONENT_NAME);
    	Object component = getNucleus().resolveName(componentName);
    	if(component != null) {
            pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
    	} else {
            pRequest.serviceParameter(EMPTY, pRequest, pResponse);
    	}
    	
    }
}
