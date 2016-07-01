/**
 * TelFormSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.castorama.sponkey;

public interface TelFormSoap_PortType extends java.rmi.Remote {
    public com.castorama.sponkey.MaTelForm testTelForm() throws java.rmi.RemoteException;
    public com.castorama.sponkey.MaTelForm getTelForm(java.lang.String numeroTel, int IDPartenaire, java.lang.String pwdPartenaire, int IDLanguePart, java.lang.String IDUserSession, java.lang.String refererPartenaire) throws java.rmi.RemoteException;
    public com.castorama.sponkey.MaTelFormV2 getTelFormV2(java.lang.String numeroTel, int IDPartenaire, java.lang.String pwdPartenaire, int IDLanguePart, java.lang.String IDUserSession, java.lang.String refererPartenaire) throws java.rmi.RemoteException;
}
