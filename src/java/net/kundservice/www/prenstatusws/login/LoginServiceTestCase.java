/**
 * LoginServiceTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.kundservice.www.prenstatusws.login;

public class LoginServiceTestCase extends junit.framework.TestCase {
    public LoginServiceTestCase(java.lang.String name) {
        super(name);
    }

    public void testLoginServiceSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getLoginServiceSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1LoginServiceSoap12LoginPublisher() throws Exception {
        net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub binding;
        try {
            binding = (net.kundservice.www.prenstatusws.login.LoginServiceSoap12Stub)
                          new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getLoginServiceSoap12();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        net.kundservice.www.prenstatusws.login.UserStatusDto value = null;
        value = binding.loginPublisher(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void testLoginServiceSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getLoginServiceSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test2LoginServiceSoapLoginPublisher() throws Exception {
        net.kundservice.www.prenstatusws.login.LoginServiceSoapStub binding;
        try {
            binding = (net.kundservice.www.prenstatusws.login.LoginServiceSoapStub)
                          new net.kundservice.www.prenstatusws.login.LoginServiceLocator().getLoginServiceSoap();
        }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
            throw new junit.framework.AssertionFailedError("JAX-RPC ServiceException caught: " + jre);
        }
        assertNotNull("binding is null", binding);

        // Time out after a minute
        binding.setTimeout(60000);

        // Test operation
        net.kundservice.www.prenstatusws.login.UserStatusDto value = null;
        value = binding.loginPublisher(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

}
