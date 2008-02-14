<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml;charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<afv-user>
	<userid><bean:write name="authenticatedUser" property="userId" /></userid>
	<username><bean:write name="authenticatedUser" property="userName" /></username>
	<email><bean:write name="authenticatedUser" property="email" /></email>
	<name><bean:write name="authenticatedUser" property="name" /></name>
	<token><bean:write name="authenticatedUser" property="token" /></token>
	<companyname><bean:write name="authenticatedUser" property="companyName" /></companyname>
</afv-user>