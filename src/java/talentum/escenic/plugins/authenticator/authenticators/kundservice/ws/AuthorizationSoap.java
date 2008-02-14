/**
 * AuthorizationSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package talentum.escenic.plugins.authenticator.authenticators.kundservice.ws;

public interface AuthorizationSoap extends java.rmi.Remote {

    /**
     * Loggar in en användare. Retunerar de roller en användare har.
     */
    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct login(java.lang.String userName, java.lang.String password, java.lang.String product) throws java.rmi.RemoteException;

    /**
     * Loggar in en användare. Returnerar roller för vald tidning
     * samt en lista på förlagets samtliga tidningar.
     */
    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.SubscriberStruct login_Publisher(java.lang.String smNo, java.lang.String PCode, java.lang.String product) throws java.rmi.RemoteException;

    /**
     * Används internt hos infodata. Verifierar en användare utifrån
     * token.
     */
    public talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct verifyUser(java.lang.String token, java.lang.String product) throws java.rmi.RemoteException;

    /**
     * Loggar ut en användare.
     */
    public boolean logout(java.lang.String token) throws java.rmi.RemoteException;
}
