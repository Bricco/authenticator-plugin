/**
 * LoginServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.kundservice.www.prenstatusws.login;

public class LoginServiceLocator extends org.apache.axis.client.Service implements net.kundservice.www.prenstatusws.login.LoginService {

    public LoginServiceLocator() {
    }


    public LoginServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public LoginServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for LoginServiceSoap12
    private java.lang.String LoginServiceSoap12_address = "https://www.kundservice.net/prenstatusws/LoginService.asmx";

    public java.lang.String getLoginServiceSoap12Address() {
        return LoginServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LoginServiceSoap12WSDDServiceName = "LoginServiceSoap12";

    public java.lang.String getLoginServiceSoap12WSDDServiceName() {
        return LoginServiceSoap12WSDDServiceName;
    }

    public void setLoginServiceSoap12WSDDServiceName(java.lang.String name) {
        LoginServiceSoap12WSDDServiceName = name;
    }

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LoginServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLoginServiceSoap12(endpoint);
    }

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub _stub = new net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub(portAddress, this);
            _stub.setPortName(getLoginServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLoginServiceSoap12EndpointAddress(java.lang.String address) {
        LoginServiceSoap12_address = address;
    }


    // Use to get a proxy class for LoginServiceSoap
    private java.lang.String LoginServiceSoap_address = "https://www.kundservice.net/prenstatusws/LoginService.asmx";

    public java.lang.String getLoginServiceSoapAddress() {
        return LoginServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String LoginServiceSoapWSDDServiceName = "LoginServiceSoap";

    public java.lang.String getLoginServiceSoapWSDDServiceName() {
        return LoginServiceSoapWSDDServiceName;
    }

    public void setLoginServiceSoapWSDDServiceName(java.lang.String name) {
        LoginServiceSoapWSDDServiceName = name;
    }

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(LoginServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getLoginServiceSoap(endpoint);
    }

    public net.kundservice.www.prenstatusws.login.LoginServiceSoap getLoginServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.kundservice.www.prenstatusws.login.LoginServiceSoapStub _stub = new net.kundservice.www.prenstatusws.login.LoginServiceSoapStub(portAddress, this);
            _stub.setPortName(getLoginServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setLoginServiceSoapEndpointAddress(java.lang.String address) {
        LoginServiceSoap_address = address;
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
            if (net.kundservice.www.prenstatusws.login.LoginServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub _stub = new net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub(new java.net.URL(LoginServiceSoap12_address), this);
                _stub.setPortName(getLoginServiceSoap12WSDDServiceName());
                return _stub;
            }
            if (net.kundservice.www.prenstatusws.login.LoginServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                net.kundservice.www.prenstatusws.login.LoginServiceSoapStub _stub = new net.kundservice.www.prenstatusws.login.LoginServiceSoapStub(new java.net.URL(LoginServiceSoap_address), this);
                _stub.setPortName(getLoginServiceSoapWSDDServiceName());
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
        if ("LoginServiceSoap12".equals(inputPortName)) {
            return getLoginServiceSoap12();
        }
        else if ("LoginServiceSoap".equals(inputPortName)) {
            return getLoginServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "LoginService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "LoginServiceSoap12"));
            ports.add(new javax.xml.namespace.QName("http://www.kundservice.net/prenstatusws/login", "LoginServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("LoginServiceSoap12".equals(portName)) {
            setLoginServiceSoap12EndpointAddress(address);
        }
        else 
if ("LoginServiceSoap".equals(portName)) {
            setLoginServiceSoapEndpointAddress(address);
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
