AUTHENTICATOR PLUGIN
--------------------

This is an Escenic plugin that provides cookie based authentication functionality to a publication.
It utilises the [Agreement functionality](http://docs.escenic.com/ece-advanced-temp-dev-guide/5.4/restricting_access_to_content.html) in Escenic. That makes it possible to restrict access to sections in Escenic WebStudio.
It uses memcached as a backend for storing authenticated users. It also provides login/logout
Struts actions and a AuthenticatedUser object to be used in the publication.

Building
========

Build plugin using ant. Target "pack" will produce a plugin jar file in dist dir.


Installation
============

- Install [memcached](http://danga.com/memcached/) on a server accessible from the Escenic server or on localhost.

- Unpack jar file in escenic/engine/plugins.

- Add libraries in authenticator/lib to engine classpath.

- Copy files in authenticator/misc/siteconfig/ to localconfig

- Add to localconfig/neo/io/managers/AgreementManager.properties

  agreementPartner.afv=/talentum/escenic/plugins/authenticator/DefaultAgreement

- Make sure that the correct memcached server is configured in localconfig/com/danga/SockIOPool.properties

- Add to Initial.properties
```
  service.0.0-memcached-socket-pool=/com/danga/SockIOPool
  service.3.2-authenticator=/talentum/escenic/plugins/authenticator/AuthenticatorManager
```
  
- Restart web app server.


Usage in publication
====================

The AuthenticatorFilter has to be added in the EscenicStandardFilterChain in the config.3xx range.

The publication has to be deployed through Assembly Tool to pick up supporting jar file.

Login/logout actions can be added in struts-config.xml see demo publication for example.

User guide
==========

To lock a section:
- In Web Studio, edit the section and check "Is agreement required" and enter 'afv T' in "Agreement information".
Where 'afv' is the identifier of the AgreementPartner specified in AgreementManager.properties and 'T' is the
required role.
- Save the changes.

In escenic-admin there is a user interface for the plugin listed on the start page. It lists all users currently 
logged in.

Authenticators
==============

The Authenticator plugin is itself pluggable. The authentication mechanism is operated by an interface called Authenticator. 
All authenticators have these properties in common:
```
cookieName=webb_paywall
cookieDomain=domain.com
autoLoginCookieName=webb_autologin
```
The plugin comes with several implementations of Authenticator that are located in src/java/talentum/escenic/plugins/authenticator/authenticators.
- CSVFileAuthenticator will look for the user in a comma seperated file. The configuration might look something like
```
$class=talentum.escenic.plugins.authenticator.authenticators.CSVFIleAuthenticator

filePath=/tmp/users.csv
```
- DBAuthenticator will look for the user in a specified database table. The configuration might look something like
```
$class=talentum.escenic.plugins.authenticator.authenticators.DBAuthenticator

table=Kayak
logTable=KayakLogins
column.username=id
column.altusername=email
column.password=pass 
column.reference=pubref
userClass=talentum.escenic.plugins.authenticator.authenticators.KayakUser
reference=NWT
```
- PressDataProductAuthenticator and PressDataPublisherAuthenticator will call the web service of [PressData](http://www.pressdata.se/) to authenticate. The web service returns roles that are then used in the authorization process. The configuration might look something like
```
$class=talentum.escenic.plugins.authenticator.authenticators.PressDataPublisherAuthenticator
publisher=AFFVAR
# product for registration NTOrder
product=AFF
# coupon for registration NTOrder
coupon=1

```
- SimpleAuthenticator is a single username/password implementation. The configuration might look something like
```
$class=talentum.escenic.plugins.authenticator.authenticators.SimpleAuthenticator
# Username and password are distributed in the paper.
username=the_username
password=secret_password
# The static role to use when enabling agreement. I e "basic X"
role=X
```
- TalentumHRLoginAccountAuthenticator is a web service authenticator for a deprecated system.
- TitelDataAuthenticator will call the REST web service of [TitelData](http://www.titeldata.se/) to authenticate. The web service returns roles that are then used in the authorization process. The configuration might look something like
```
$class=talentum.escenic.plugins.authenticator.authenticators.TitelDataAuthenticator
RESTUrl=https://webapi2.prenservice.se
APIKey=882844DD-96YE-49B0-B22B-C60F6K17B982
defaultRole=T
titelNr=318
```

Escenic bug
===========

There is an unsolved bug in ECE core (as of version 5.7). It has been reported several times to Escenic without getting a fix. The problem is in neo/util/servlet/AgreementUtil. There is a line missing:
``` java
    checkHeaders(response, servletresponse);
```
The Authenticator will use HTTP headers for it's communication with Varnish.

To install the patch simply add the patch jar to engine/patches and re-assemble ECE.
In the ece-patches there are a couple of patches for various 5.x versions of ECE.

Varnish
=======

Authenticator can act as the backend for a paywall solution configured in Varnish. The cookie reading is then elevated to Varnish configuration (VCL) with callbacks to primarily the AuthorizeAction class. The call to AuthorizeAction is cached in Varnish for maximum efficency.

The configuration in Varnish is made in the two VCL subroutines vcl_recv and vcl_deliver.

In vcl_recv the paywall cookies are read and any autologin is performed there too.
```
    if(req.url ~ "^/logout\.do"
      || req.url ~ "^/userinfo\.do"
      || req.url ~ "^/checkAutologin\.do"
      || req.url ~ "^/status\.do"
      || req.url ~ "^/changepassword\.do"
      || req.url ~ "^/login\.do") {
      return(pass);
    }
    
    if (req.http.Cookie) {
  
      // normalize the cookie header by adding a ; in front and removing spaces between cookies
      set req.http.Cookie = ";" + req.http.Cookie;
      set req.http.Cookie = regsuball(req.http.Cookie, "; +", ";");
      
      // set header X-Paywall-Cookie for later use
      set req.http.X-Paywall-Cookie = regsuball(req.http.Cookie, ";paywall=", "; \1");
      set req.http.X-Paywall-Cookie = regsuball(req.http.X-Paywall-Cookie, ";[^ ][^;]*", "");
      set req.http.X-Paywall-Cookie = regsuball(req.http.X-Paywall-Cookie, "^[; ]+|[; ]+$", "");
  
      // set header X-Metered-Cookie for later use
      set req.http.X-Metered-Cookie = regsuball(req.http.Cookie, ";metered=", "; \1");
      set req.http.X-Metered-Cookie = regsuball(req.http.X-Metered-Cookie, ";[^ ][^;]*", "");
      set req.http.X-Metered-Cookie = regsuball(req.http.X-Metered-Cookie, "^[; ]+|[; ]+$", "");
  
      // set header X-Autologin-Cookie for later use
      set req.http.X-Autologin-Cookie = regsuball(req.http.Cookie, ";autologin=", "; \1");
      set req.http.X-Autologin-Cookie = regsuball(req.http.X-Autologin-Cookie, ";[^ ][^;]*", "");
      set req.http.X-Autologin-Cookie = regsuball(req.http.X-Autologin-Cookie, "^[; ]+|[; ]+$", "");
  
        /**
         * If no login cookie is set, but there is a autologin cookie, call backend to verify autologin
         */
        
        if(req.http.X-Paywall-Cookie == "" && req.http.X-Autologin-Cookie){
          //std.syslog(180, "Calling checkAutologin.do");
    
          curl.fetch("http://" + req.http.host + "/checkAutologin.do?cookieValue=" + req.http.X-Autologin-Cookie);
          if(curl.status() == 200) {    
            //std.syslog(180, "Response from checkAutologin" + curl.header("X-Autologin-Response"));
      
            set req.http.X-Paywall-Cookie = curl.header("X-Autologin-Response");
            set req.http.X-Autologin-OK = "true";           
          }
        }
        
        
      /* for testing
      std.syslog(180, "X-Paywall-Cookie: " + req.http.X-Paywall-Cookie);
      std.syslog(180, "X-Metered-Cookie: " + req.http.X-Metered-Cookie);
      std.syslog(180, "X-Autologin-Cookie: " + req.http.X-Autologin-Cookie);
      */
  
      // use some inline-C to count identifiers in X-Metered-Cookie
      // put result in X-Metered-Views
      C{
        VRT_SetHdr(sp, HDR_REQ, "\020X-Metered-Views:",           
          countCommas( VRT_GetHdr(sp, HDR_REQ, "\021X-Metered-Cookie:")),
          vrt_magic_string_end
        );
      }C
      // this would be nice if it worked
      /* set req.http.X-Metered-Views = this.countCommas(req.http.X-Metered-Cookie);*/
  
      remove req.http.Cookie;
  
    }
    ```

    In vcl_deliver the AuthorizeAction is called to authorize the request.

    ```
    sub vcl_deliver {
  
  // if we have autologin ok in vcl_recv we set the cookie on the response
  if(req.http.X-Autologin-Ok) {
    set resp.http.Set-Cookie = "paywall=" + req.http.X-Paywall-Cookie + "; domain=" + regsuball(req.http.host, "^[^.]*\.(?=\w+\.\w+$)", "") + "; path=/"; 
    //std.syslog(180, "Cookie paywall IS SET TO: " + req.http.X-Paywall-Cookie);
  }

  // if the requested resource allows metered views
  // if the requested url resource role(s) set we need to authorize
  if (resp.http.X-Paywall-Roles) {
    // Call Authenticator to authorize the token from cookie.
    // Make call through Varnish to cache the response.
    curl.fetch(resp.http.X-Paywall-Authorization-Url + "?token=" + req.http.X-Paywall-Cookie + "&roles=" + resp.http.X-Paywall-Roles);
    if (curl.status() == 200) {
    
      // user was authorized, remove metered header to skip check on metered
      unset resp.http.X-Paywall-Metered-Allowed-Views;
      unset resp.http.X-Paywall-Metered-Identifier;
    
    } else {

      // missing args to authorize.do, shouldn't happen. set denied-url
      if(curl.status() == 400) {
        set req.http.x-do-error = resp.http.X-Paywall-Denied-Url;
      }

      // user too old or roles did not match. set unauthorized-url
      if(curl.status() == 401) {
        set req.http.x-do-error = resp.http.X-Paywall-Unauthorized-Url;
      }

      // user not found. set denied-url
      if(curl.status() == 403) {
        set req.http.x-do-error = resp.http.X-Paywall-Denied-Url;
      }
      
      // user passive. set passive-url and restart. no further checking on metered.
      if(curl.status() == 406) {
        set req.http.x-do-error = resp.http.X-Paywall-Passive-Url;
        return (restart);
      }

      // user rejected. set rejected-url and restart. no further checking on metered.
      if(curl.status() == 409) {
        set req.http.x-do-error = resp.http.X-Paywall-Rejected-Url;
        return (restart);
      }
      
      // only restart the request if metering is not on
      // otherwise give the request a chance to be viewed with metering
      if(!resp.http.X-Paywall-Metered-Allowed-Views) {
        return (restart);
      }
    }
  }

  if (resp.http.X-Paywall-Metered-Allowed-Views && resp.http.X-Paywall-Metered-Identifier) {

    // normalize identifier by adding a ","
    set resp.http.X-Paywall-Metered-Identifier = resp.http.X-Paywall-Metered-Identifier + ",";

    if (req.http.X-Metered-Cookie) {
      // check if identifier is already in cookie
      // if so set header X-Paywall-Already-In-Cookie
      C{
        char* identifier = VRT_GetHdr(sp, HDR_RESP, "\035X-Paywall-Metered-Identifier:");
        char* cookie = VRT_GetHdr(sp, HDR_REQ, "\021X-Metered-Cookie:");
        char* found = strstr( cookie, identifier );
        if(found != NULL) {
          VRT_SetHdr(sp, HDR_RESP, "\034X-Paywall-Already-In-Cookie:", "true", vrt_magic_string_end);
        }
      }C
    }


    // if this is a new identifier and user has filled quota, set denied-url and restart request.
    if(!resp.http.X-Paywall-Already-In-Cookie && std.integer(req.http.X-Metered-Views, 0) >= std.integer(resp.http.X-Paywall-Metered-Allowed-Views, 0)) {
      set req.http.x-do-error = resp.http.X-Paywall-Metered-Denied-Url;
      return (restart);
    } else {

      // if identifier is NOT already in cookie, we add it
      if(!resp.http.X-Paywall-Already-In-Cookie) {
        set resp.http.Set-Cookie = "metered=" + req.http.X-Metered-Cookie + resp.http.X-Paywall-Metered-Identifier + "; path=/; expires=" + resp.http.X-Paywall-Metered-Cookie-Expires; 
      }

    }
  }



  // unset response headers
  unset resp.http.X-Paywall-Metered-Allowed-Views;
  unset resp.http.X-Paywall-Metered-Identifier;
  unset resp.http.X-Paywall-Metered-Denied-Url;
  unset resp.http.X-Paywall-Already-In-Cookie;
  unset resp.http.X-Paywall-Metered-Cookie-Expires;
  unset resp.http.X-Paywall-Roles;
  unset resp.http.X-Paywall-Denied-Url;
  unset resp.http.X-Paywall-Unauthorized-Url;
  unset resp.http.X-Paywall-Passive-Url;
  unset resp.http.X-Paywall-Rejected-Url;


}
```
