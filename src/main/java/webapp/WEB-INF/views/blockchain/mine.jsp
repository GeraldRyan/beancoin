<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ page import="com.gerald.ryan.blocks.entity.Blockchain"%>
<%@ page import="com.gerald.ryan.blocks.entity.Block"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>


	<!-- NOT EVEN USING THIS PAGE. MINE ENDPOINT JUST RETURNS RESPONSE BODY APPLICATION/JSON -->
	<h2>The dwarves are hard at work mining your Block</h2>
	<h3>
		<%
		Blockchain bc = (Blockchain) request.getAttribute("blockchain");
		Block mined_block = (Block) request.getAttribute("minedblock");
		Block last_block = bc.getLastBlock();
		Block second_last_block = bc.getNthBlock(-1);
		%>
		<!-- 	Really this is second to last -->
		<%-- 		<%=bc.getLastBlock().toJSONtheBlock()%> --%>

	</h3>

	<h4>Newly Mined Block</h4>
	
<%-- 	<%=mined_block.webworthyJson().getAsString()%> --%>
</body>
</html>