<%@ taglib uri="/WEB-INF/escenic-article.tld" prefix="article" %>
<%@ taglib uri="/WEB-INF/escenic-template.tld" prefix="template" %>

<article:field id="url" field="sourceUrl" type="String"/>
<template:call file="${url}" />
