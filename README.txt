Building
========

Build plugin using ant. Target "pack" will produce a plugin jar file.


Installing
==========

- Unpack jar file in escenic/engine/plugins.

- Add libraries in authenticator/lib to engine classpath.

- Copy files in authenticator/misc/siteconfig/ to localconfig

- Add to localconfig/neo/io/managers/AgreementManager.properties

  agreementPartner.pressdata=/talentum/agreements/AFVAgreement

- Add to Initial.properties

  service.3.2-authenticator=/talentum/escenic/plugins/authenticator/AuthenticatorManager