/**
 * AuthorizationTestCase.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package talentum.escenic.plugins.authenticator.authenticators.kundservice.ws;

public class AuthorizationTestCase extends junit.framework.TestCase {
    public AuthorizationTestCase(java.lang.String name) {
        super(name);
    }

    public void testAuthorizationSoapWSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoapAddress() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test1AuthorizationSoapLogin() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct value = null;
        value = binding.login(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test2AuthorizationSoapLogin_Publisher() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.SubscriberStruct value = null;
        value = binding.login_Publisher(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test3AuthorizationSoapVerifyUser() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct value = null;
        value = binding.verifyUser(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test4AuthorizationSoapLogout() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap();
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
        boolean value = false;
        value = binding.logout(new java.lang.String());
        // TBD - validate results
    }

    public void testAuthorizationSoap12WSDL() throws Exception {
        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();
        java.net.URL url = new java.net.URL(new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap12Address() + "?WSDL");
        javax.xml.rpc.Service service = serviceFactory.createService(url, new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getServiceName());
        assertTrue(service != null);
    }

    public void test5AuthorizationSoap12Login() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap12();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct value = null;
        value = binding.login(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test6AuthorizationSoap12Login_Publisher() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap12();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.SubscriberStruct value = null;
        value = binding.login_Publisher(new java.lang.String(), new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test7AuthorizationSoap12VerifyUser() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap12();
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
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct value = null;
        value = binding.verifyUser(new java.lang.String(), new java.lang.String());
        // TBD - validate results
    }

    public void test8AuthorizationSoap12Logout() throws Exception {
        talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub binding;
        try {
            binding = (talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoap12Stub)
                          new talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator().getAuthorizationSoap12();
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
        boolean value = false;
        value = binding.logout(new java.lang.String());
        // TBD - validate results
    }

}
