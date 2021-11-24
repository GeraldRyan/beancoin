<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Transact with us</title>
<link href="https://unpkg.com/tailwindcss@^1.0/dist/tailwind.min.css"
	rel="stylesheet">

<style>
nav {
	background-color: darkgreen;
	color: white;
	font-weight: bold;
}

form {
	display: flex;
	flex-direction: column;
	margin-bottom: 100px;
	background-color: gray;
	color: white;
	font-weight: bold;
	font-size: 2rem;
}

input {
	border: 1px solid grey;
	margin: 4px 0 4px 4px;
	color: black;
}

.note {
	margin-bottom: 10rem;
}
</style>
</head>
<body class="mx-auto text-center">
	<jsp:include page="../common/navbar.jsp"></jsp:include>
	<nav class="flex justify-around mb-10 text-2xl">

		<a href="/blocks">Home</a> <br> <a href="/blocks/blockchain/">Our
			Chain</a> <br> <a href="/blocks/transactionpool/">Transaction
			Pool</a> <br>
	</nav>
	<h1 class="text-5xl mb-8">Transact on the blockchain</h1>

	<form action="../transaction" class="border-gray-200 border-2 gap-2">
		<div>Address to send money to</div>
		<div>
			<input type="text" name="address">
		</div>
		<div>Amount to send</div>
		<div>
			<input type="text" name="amount">
		</div>
		<div>
			<input type="submit" name="transact"
				class="mb-3 rounded-full my-4 shadow bg-green-700 px-4 py-2 text-white hover:bg-green-600">
		</div>

	</form>

	<p>Also, Send a single transaction to a specific address by POST-
		ing to this same endpoint</p>
	<p class="note">{"address":"my address", "amount":"integer
		amount")}</p>

	<p>For Dev Purposes, To create n bulk test transactions, send POST
		to</p>

	<p>http://localhost:8080/blocks/wallet/transactt [note extra t]</p>


	<p>{"number"":[number of test transactions to make],
		"fromaddress":[your from address]}</p>
	<p>Amounts and recipients will always be random</p>
	<p>And both of these fields are optional in which case they each
		default to random/multiple</p>
	<p>Might crash due to session error after 1st transaction:
		java.lang.IllegalStateException: Cannot create a session after the
		response has been committed</p>
	<p>low priority, can simulate multiple transactions in Initializer
		class by running main</p>
	<p>e.g. {number:7}</p>

</body>
</html>