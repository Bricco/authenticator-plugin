<%@ taglib uri="/WEB-INF/escenic-template.tld" prefix="template" %>

<!-- Calling frame -->
<template:call file="/pages/base/frame.jsp">
  <template:parameter key="title" value="h.Menu" />
  <template:parameter key="content" value="/plugins/authenticator/index-content.jsp" />
</template:call>
<!-- Finished call to frame -->