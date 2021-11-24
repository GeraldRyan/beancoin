<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.gerald.ryan.blocks.entity.Transaction"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
</head>
<body>
	<jsp:include page="../common/navbar.jsp"></jsp:include>
	<h1>Transact on the blockchain</h1>
	<a href="/blocks">Home</a>
	<br>
	<h2>You made a transaction</h2>
	${transaction.toJSONtheTransaction() }

	<br>
	<a href="/blockchain/">See our version of the blockchain</a>
	<br>
	<a href="/blockchaindesc/">Same as above but with description</a>

	<form action="/wallet/transact">
		Address to send money to <input type="text" name="address"><br>
		<br> Amount to send<input type="text" name="amount"><br>
		<br> <input type="submit" name="submit">
	</form>

</body>
</html>