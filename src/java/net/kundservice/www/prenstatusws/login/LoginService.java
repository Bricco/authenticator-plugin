/**
 * LoginService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.kundservice.www.prenstatusws.login;

public interface LoginService extends javax.xml.rpc.Service {
    public java.lang.String getLoginServiceSoap12Address();

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap12() throws javax.xml.rpc.ServiceException;

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getLoginServiceSoapAddress();

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap() throws javax.xml.rpc.ServiceException;

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
