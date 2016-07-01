/**
 * LoginUserSEIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.atg.www.webservices;

public class LoginUserSEIServiceLocator extends org.apache.axis.client.Service implements com.atg.www.webservices.LoginUserSEIService {

    public LoginUserSEIServiceLocator() {
    }


    public LoginUserSEIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LoginUserSEIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LoginUserSEIPort
    private java.lang.String LoginUserSEIPort_address = "https://ws.castorama.fr/loginUser/loginUser/loginUser";

    public java.lang.String getLoginUserSEIPortAddress() {
        return LoginUserSEIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LoginUserSEIPortWSDDServiceName = "LoginUserSEIPort";

    public java.lang.String getLoginUserSEIPortWSDDServiceName() {
        return LoginUserSEIPortWSDDServiceName;
    }

    public void setLoginUserSEIPortWSDDServiceName(java.lang.String name) {
        LoginUserSEIPortWSDDServiceName = name;
    }

    public com.atg.www.webservices.LoginUserSEI getLoginUserSEIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LoginUserSEIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLoginUserSEIPort(endpoint);
    }

    public com.atg.www.webservices.LoginUserSEI getLoginUserSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.atg.www.webservices.LoginUserSEIBindingStub _stub = new com.atg.www.webservices.LoginUserSEIBindingStub(portAddress, this);
            _stub.setPortName(getLoginUserSEIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLoginUserSEIPortEndpointAddress(java.lang.String address) {
        LoginUserSEIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.atg.www.webservices.LoginUserSEI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.atg.www.webservices.LoginUserSEIBindingStub _stub = new com.atg.www.webservices.LoginUserSEIBindingStub(new java.net.URL(LoginUserSEIPort_address), this);
                _stub.setPortName(getLoginUserSEIPortWSDDServiceName());
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
        if ("LoginUserSEIPort".equals(inputPortName)) {
            return getLoginUserSEIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.atg.com/webservices", "LoginUserSEIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.atg.com/webservices", "LoginUserSEIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LoginUserSEIPort".equals(portName)) {
            setLoginUserSEIPortEndpointAddress(address);
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
