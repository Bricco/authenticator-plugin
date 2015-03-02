AUTHENTICATOR PLUGIN
--------------------

This is an Escenic plugin that provides cookie based authentication functionality to a publication.
It uses memcached as a backend for storing authenticated users. It also provides login/logout
Struts actions and a AuthenticatedUser object to be used in the publication.

Building
========

Build plugin using ant. Target "pack" will produce a plugin jar file in dist dir.


Installation
============

- Install memcached (http://danga.com/memcached/) on a server accessible from the Escenic server or on localhost.

- Unpack jar file in escenic/engine/plugins.

- Add libraries in authenticator/lib to engine classpath.

- Copy files in authenticator/misc/siteconfig/ to localconfig

- Add to localconfig/neo/io/managers/AgreementManager.properties

  agreementPartner.afv=/talentum/escenic/plugins/authenticator/DefaultAgreement

- Make sure that the correct memcached server is configured in localconfig/com/danga/SockIOPool.properties

- Add to Initial.properties

  `service.0.0-memcached-socket-pool=/com/danga/SockIOPool`
  `service.3.2-authenticator=/talentum/escenic/plugins/authenticator/AuthenticatorManager`
  
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

