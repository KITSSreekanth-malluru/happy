/**
 * UpdateUserSEIServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 19, 2006 (02:31:34 GMT+00:00) WSDL2Java emitter.
 */

package com.atg.www.webservices;

public class UpdateUserSEIServiceLocator extends org.apache.axis.client.Service implements com.atg.www.webservices.UpdateUserSEIService {

    public UpdateUserSEIServiceLocator() {
    }


    public UpdateUserSEIServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public UpdateUserSEIServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for UpdateUserSEIPort
    private java.lang.String UpdateUserSEIPort_address = "https://ws.castorama.fr/updateUser/updateUser/updateUser";

    public java.lang.String getUpdateUserSEIPortAddress() {
        return UpdateUserSEIPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String UpdateUserSEIPortWSDDServiceName = "UpdateUserSEIPort";

    public java.lang.String getUpdateUserSEIPortWSDDServiceName() {
        return UpdateUserSEIPortWSDDServiceName;
    }

    public void setUpdateUserSEIPortWSDDServiceName(java.lang.String name) {
        UpdateUserSEIPortWSDDServiceName = name;
    }

    public com.atg.www.webservices.UpdateUserSEI getUpdateUserSEIPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(UpdateUserSEIPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getUpdateUserSEIPort(endpoint);
    }

    public com.atg.www.webservices.UpdateUserSEI getUpdateUserSEIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.atg.www.webservices.UpdateUserSEIBindingStub _stub = new com.atg.www.webservices.UpdateUserSEIBindingStub(portAddress, this);
            _stub.setPortName(getUpdateUserSEIPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setUpdateUserSEIPortEndpointAddress(java.lang.String address) {
        UpdateUserSEIPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.atg.www.webservices.UpdateUserSEI.class.isAssignableFrom(serviceEndpointInterface)) {
                com.atg.www.webservices.UpdateUserSEIBindingStub _stub = new com.atg.www.webservices.UpdateUserSEIBindingStub(new java.net.URL(UpdateUserSEIPort_address), this);
                _stub.setPortName(getUpdateUserSEIPortWSDDServiceName());
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
        if ("UpdateUserSEIPort".equals(inputPortName)) {
            return getUpdateUserSEIPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.atg.com/webservices", "UpdateUserSEIService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.atg.com/webservices", "UpdateUserSEIPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("UpdateUserSEIPort".equals(portName)) {
            setUpdateUserSEIPortEndpointAddress(address);
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
