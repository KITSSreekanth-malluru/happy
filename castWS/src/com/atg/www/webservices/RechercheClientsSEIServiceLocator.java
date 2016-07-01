/**
 * RechercheClientsSEIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.atg.www.webservices;

public class RechercheClientsSEIServiceLocator extends org.apache.axis.client.Service implements com.atg.www.webservices.RechercheClientsSEIService {

    public RechercheClientsSEIServiceLocator() {
    }


    public RechercheClientsSEIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public RechercheClientsSEIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for RechercheClientsSEIPort
    private java.lang.String RechercheClientsSEIPort_address = "https://ws.castorama.fr/rechercheClients/rechercheClients/rechercheClients";

    public java.lang.String getRechercheClientsSEIPortAddress() {
        return RechercheClientsSEIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String RechercheClientsSEIPortWSDDServiceName = "RechercheClientsSEIPort";

    public java.lang.String getRechercheClientsSEIPortWSDDServiceName() {
        return RechercheClientsSEIPortWSDDServiceName;
    }

    public void setRechercheClientsSEIPortWSDDServiceName(java.lang.String name) {
        RechercheClientsSEIPortWSDDServiceName = name;
    }

    public com.atg.www.webservices.RechercheClientsSEI getRechercheClientsSEIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(RechercheClientsSEIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getRechercheClientsSEIPort(endpoint);
    }

    public com.atg.www.webservices.RechercheClientsSEI getRechercheClientsSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.atg.www.webservices.RechercheClientsSEIBindingStub _stub = new com.atg.www.webservices.RechercheClientsSEIBindingStub(portAddress, this);
            _stub.setPortName(getRechercheClientsSEIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setRechercheClientsSEIPortEndpointAddress(java.lang.String address) {
        RechercheClientsSEIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.atg.www.webservices.RechercheClientsSEI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.atg.www.webservices.RechercheClientsSEIBindingStub _stub = new com.atg.www.webservices.RechercheClientsSEIBindingStub(new java.net.URL(RechercheClientsSEIPort_address), this);
                _stub.setPortName(getRechercheClientsSEIPortWSDDServiceName());
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
        if ("RechercheClientsSEIPort".equals(inputPortName)) {
            return getRechercheClientsSEIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.atg.com/webservices", "RechercheClientsSEIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.atg.com/webservices", "RechercheClientsSEIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("RechercheClientsSEIPort".equals(portName)) {
            setRechercheClientsSEIPortEndpointAddress(address);
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
