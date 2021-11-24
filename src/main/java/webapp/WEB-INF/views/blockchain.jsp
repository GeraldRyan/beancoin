<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.gerald.ryan.blocks.entity.Blockchain"%>


<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title></title>
<link rel="stylesheet" href="./blockchain.css" type="text/css"></link>
</head>
</body>
<body>
	<pre style="word-wrap: break-word; white-space: pre-wrap;">${blockchain.toJSONtheChain()}</pre>
</body>
</html>