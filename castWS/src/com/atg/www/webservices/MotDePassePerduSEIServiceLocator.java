/**
 * MotDePassePerduSEIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.atg.www.webservices;

public class MotDePassePerduSEIServiceLocator extends org.apache.axis.client.Service implements com.atg.www.webservices.MotDePassePerduSEIService {

    public MotDePassePerduSEIServiceLocator() {
    }


    public MotDePassePerduSEIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MotDePassePerduSEIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MotDePassePerduSEIPort
    private java.lang.String MotDePassePerduSEIPort_address = "https://ws.castorama.fr/motDePassePerdu/motDePassePerdu/motDePassePerdu";

    public java.lang.String getMotDePassePerduSEIPortAddress() {
        return MotDePassePerduSEIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MotDePassePerduSEIPortWSDDServiceName = "MotDePassePerduSEIPort";

    public java.lang.String getMotDePassePerduSEIPortWSDDServiceName() {
        return MotDePassePerduSEIPortWSDDServiceName;
    }

    public void setMotDePassePerduSEIPortWSDDServiceName(java.lang.String name) {
        MotDePassePerduSEIPortWSDDServiceName = name;
    }

    public com.atg.www.webservices.MotDePassePerduSEI getMotDePassePerduSEIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MotDePassePerduSEIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMotDePassePerduSEIPort(endpoint);
    }

    public com.atg.www.webservices.MotDePassePerduSEI getMotDePassePerduSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.atg.www.webservices.MotDePassePerduSEIBindingStub _stub = new com.atg.www.webservices.MotDePassePerduSEIBindingStub(portAddress, this);
            _stub.setPortName(getMotDePassePerduSEIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMotDePassePerduSEIPortEndpointAddress(java.lang.String address) {
        MotDePassePerduSEIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.atg.www.webservices.MotDePassePerduSEI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.atg.www.webservices.MotDePassePerduSEIBindingStub _stub = new com.atg.www.webservices.MotDePassePerduSEIBindingStub(new java.net.URL(MotDePassePerduSEIPort_address), this);
                _stub.setPortName(getMotDePassePerduSEIPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MotDePassePerduSEIPort".equals(inputPortName)) {
            return getMotDePassePerduSEIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.atg.com/webservices", "MotDePassePerduSEIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.atg.com/webservices", "MotDePassePerduSEIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MotDePassePerduSEIPort".equals(portName)) {
            setMotDePassePerduSEIPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
