/**
 * TelFormLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.castorama.sponkey;

public class TelFormLocator extends org.apache.axis.client.Service implements com.castorama.sponkey.TelForm {

    public TelFormLocator() {
    }


    public TelFormLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TelFormLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TelFormSoap
    private java.lang.String TelFormSoap_address = "https://ssl.pass-connect.com/TelForm.asmx";

    public java.lang.String getTelFormSoapAddress() {
        return TelFormSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TelFormSoapWSDDServiceName = "TelFormSoap";

    public java.lang.String getTelFormSoapWSDDServiceName() {
        return TelFormSoapWSDDServiceName;
    }

    public void setTelFormSoapWSDDServiceName(java.lang.String name) {
        TelFormSoapWSDDServiceName = name;
    }

    public com.castorama.sponkey.TelFormSoap_PortType getTelFormSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TelFormSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTelFormSoap(endpoint);
    }

    public com.castorama.sponkey.TelFormSoap_PortType getTelFormSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.castorama.sponkey.TelFormSoap_BindingStub _stub = new com.castorama.sponkey.TelFormSoap_BindingStub(portAddress, this);
            _stub.setPortName(getTelFormSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTelFormSoapEndpointAddress(java.lang.String address) {
        TelFormSoap_address = address;
    }


    // Use to get a proxy class for TelFormSoap12
    private java.lang.String TelFormSoap12_address = "https://ssl.pass-connect.com/TelForm.asmx";

    public java.lang.String getTelFormSoap12Address() {
        return TelFormSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TelFormSoap12WSDDServiceName = "TelFormSoap12";

    public java.lang.String getTelFormSoap12WSDDServiceName() {
        return TelFormSoap12WSDDServiceName;
    }

    public void setTelFormSoap12WSDDServiceName(java.lang.String name) {
        TelFormSoap12WSDDServiceName = name;
    }

    public com.castorama.sponkey.TelFormSoap_PortType getTelFormSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TelFormSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTelFormSoap12(endpoint);
    }

    public com.castorama.sponkey.TelFormSoap_PortType getTelFormSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.castorama.sponkey.TelFormSoap12Stub _stub = new com.castorama.sponkey.TelFormSoap12Stub(portAddress, this);
            _stub.setPortName(getTelFormSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTelFormSoap12EndpointAddress(java.lang.String address) {
        TelFormSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.castorama.sponkey.TelFormSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.castorama.sponkey.TelFormSoap_BindingStub _stub = new com.castorama.sponkey.TelFormSoap_BindingStub(new java.net.URL(TelFormSoap_address), this);
                _stub.setPortName(getTelFormSoapWSDDServiceName());
                return _stub;
            }
            if (com.castorama.sponkey.TelFormSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.castorama.sponkey.TelFormSoap12Stub _stub = new com.castorama.sponkey.TelFormSoap12Stub(new java.net.URL(TelFormSoap12_address), this);
                _stub.setPortName(getTelFormSoap12WSDDServiceName());
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
        if ("TelFormSoap".equals(inputPortName)) {
            return getTelFormSoap();
        }
        else if ("TelFormSoap12".equals(inputPortName)) {
            return getTelFormSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "TelForm");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "TelFormSoap"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "TelFormSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TelFormSoap".equals(portName)) {
            setTelFormSoapEndpointAddress(address);
        }
        else 
if ("TelFormSoap12".equals(portName)) {
            setTelFormSoap12EndpointAddress(address);
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
