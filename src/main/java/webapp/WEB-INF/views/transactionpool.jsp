<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<!-- CSS style to set alternate table 
            row using color -->
<style>
table {
	border-collapse: collapse;
	width: 100%;
}

th, td {
	text-align: left;
	padding: 8px;
}

tr:nth-child(even) {
	background-color: Lightgreen;
}

tr:nth-child(odd) {
	background-color: #EFEFEF;
}
</style>
<body>
	<jsp:include page="./common/navbar.jsp"></jsp:include>
	<h1>Transaction Pool</h1>
	<h2>Note this displays transactions broken down by individual, for
		uniform display in table format. It is not displayed in bundles of
		multiple recipients per sender, as processed in the blockchain.</h2>
	<h2>You can get the original JSON format by submitting a POST
		request to this same endpoint with any given payload ||| {} |||</h2>




	<table border="2">
		<tr>
			<td>Sender Address</td>
			<td>Beginning Balance</td>
			<td>Recipient Address</td>
			<td>Amount</td>
			<td>Return balance</td>
			<td>Timestamp</td>
			<td>Transaction ID</td>
		</tr>

		<c:forEach items="${transactionpoollist}" var="t">
			<c:forEach items="${t.getOutput().keySet()}" var="rAdds">
				<c:if test="${ !rAdds.equals(t.getInput().get(\"address\"))}">
					<tr>
						<td>${t.getInput().get("address")}</td>
						<td>${t.getInput().get("amount")}</td>
						<td>${rAdds}</td>
						<td>${t.getOutput().get(rAdds)}</td>
						<td>${t.getInput().get("amount") - t.getOutput().get(rAdds)}</td>
						<td>${t.getInput().get("timestamp")}</td>
						<td>${t.getUuid()}</td>
					</tr>
				</c:if>
			</c:forEach>
		</c:forEach>

	</table>
	<br>
	<p>TODO: Hard to display other important data columns for two
		reasons--- one transaction might have multiple recipients. (Would just
		have to split using plain old java function. Wouldn't technically
		represent a "transaction" object as processed, but a more human
		readable rendering of the same info). Also, the lookup key values are
		also often not known in advance. They represent wallet addresses,
		which can be iterated through and gotten but otherwise are not
		constant.</p>
	<p>In other words- displaying values whose lookup keys are
		constants, and which relate to one other value</p>




	<%-- <h3>${transactionList} }</h3> --%>

</body>
</html>