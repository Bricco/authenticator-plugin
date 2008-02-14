/**
 * AuthorizationLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package talentum.escenic.plugins.authenticator.authenticators.kundservice.ws;

public class AuthorizationLocator extends org.apache.axis.client.Service implements talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.Authorization {

    public AuthorizationLocator() {
    }


    public AuthorizationLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AuthorizationLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AuthorizationSoap
    private java.lang.String AuthorizationSoap_address = "https://www.kundservice.net/WS/Authorization.asmx";

    public java.lang.String getAuthorizationSoapAddress() {
        return AuthorizationSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AuthorizationSoapWSDDServiceName = "AuthorizationSoap";

    public java.lang.String getAuthorizationSoapWSDDServiceName() {
        return AuthorizationSoapWSDDServiceName;
    }

    public void setAuthorizationSoapWSDDServiceName(java.lang.String name) {
        AuthorizationSoapWSDDServiceName = name;
    }

    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap getAuthorizationSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AuthorizationSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAuthorizationSoap(endpoint);
    }

    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap getAuthorizationSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub _stub = new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub(portAddress, this);
            _stub.setPortName(getAuthorizationSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAuthorizationSoapEndpointAddress(java.lang.String address) {
        AuthorizationSoap_address = address;
    }


    // Use to get a proxy class for AuthorizationSoap12
    private java.lang.String AuthorizationSoap12_address = "https://www.kundservice.net/WS/Authorization.asmx";

    public java.lang.String getAuthorizationSoap12Address() {
        return AuthorizationSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AuthorizationSoap12WSDDServiceName = "AuthorizationSoap12";

    public java.lang.String getAuthorizationSoap12WSDDServiceName() {
        return AuthorizationSoap12WSDDServiceName;
    }

    public void setAuthorizationSoap12WSDDServiceName(java.lang.String name) {
        AuthorizationSoap12WSDDServiceName = name;
    }

    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap getAuthorizationSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AuthorizationSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAuthorizationSoap12(endpoint);
    }

    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap getAuthorizationSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub _stub = new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub(portAddress, this);
            _stub.setPortName(getAuthorizationSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAuthorizationSoap12EndpointAddress(java.lang.String address) {
        AuthorizationSoap12_address = address;
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
            if (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub _stub = new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub(new java.net.URL(AuthorizationSoap_address), this);
                _stub.setPortName(getAuthorizationSoapWSDDServiceName());
                return _stub;
            }
            if (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub _stub = new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub(new java.net.URL(AuthorizationSoap12_address), this);
                _stub.setPortName(getAuthorizationSoap12WSDDServiceName());
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
        if ("AuthorizationSoap".equals(inputPortName)) {
            return getAuthorizationSoap();
        }
        else if ("AuthorizationSoap12".equals(inputPortName)) {
            return getAuthorizationSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://www.kundservice.net/WS/Authorization", "Authorization");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://www.kundservice.net/WS/Authorization", "AuthorizationSoap"));
            ports.add(new javax.xml.namespace.QName("https://www.kundservice.net/WS/Authorization", "AuthorizationSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AuthorizationSoap".equals(portName)) {
            setAuthorizationSoapEndpointAddress(address);
        }
        else 
if ("AuthorizationSoap12".equals(portName)) {
            setAuthorizationSoap12EndpointAddress(address);
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
