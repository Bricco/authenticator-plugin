<%@ taglib uri="/WEB-INF/escenic-article.tld" prefix="article"%>
<%@ taglib uri="/WEB-INF/escenic-template.tld" prefix="template"%>
<%@ taglib uri="/WEB-INF/escenic-util.tld" prefix="util"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<bean:define id="templateContext" name="com.escenic.context"
	toScope="request" />

<html>
<head>
<link rel="STYLESHEET" type="text/css"
	href="<util:valueof param="apipublication.rootSection.url" />template/css/forum.css">
<title>Authenticator DEMO</title>
</head>
<body class="body">
<table border="0" cellspacing="0">
	<tr>
		<td colspan="3" valign="top"><template:call file="header.jsp" />
		</td>
	</tr>
	<tr>
		<td width="250" bgcolor="#cccccc" valign="top">
          <template:call file="menu.jsp"/>
        </td>
		<td valign="top">
		<table border="0" cellspacing="0">
			<tr>
				<td width="550" valign="top">
					<logic:equal name="templateContext" value="art">
						<template:call file="/template/article/${article.articleTypeName}.jsp" />
					</logic:equal>
					<logic:equal name="templateContext" value="sec">
						<template:insert typeName="grid" />
					</logic:equal>
				</td>
			</tr>
		</table>
		</td>
		<td width="200" valign="top">
		<table width="100%">
			<tr>
				<td><template:call file="latestArticles.jsp" /></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td colspan="3" valign="top" class="footer"><template:call
			file="footer.jsp" /></td>
	</tr>
</table>
</body>
</html>
