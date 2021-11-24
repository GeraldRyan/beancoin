<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
<!-- <link href="https://unpkg.com/tailwindcss@^1.0/dist/tailwind.min.css"
	rel="stylesheet"> -->
<style type="text/css">
body {
	background-color: #3D3D3D;
}

nav {
	background-color: darkgreen;
	color: white;
	font-weight: bold;
	font-size: 1.5rem;
}

.salutations a {
	font-weight: bold;
}

.heading {
	margin: 4rem;
}

h1, .h1 {
	font-size: 3rem;
}

h2, .h2 {
	font-size: 2.6rem;
}

h3, .h3 {
	font-weight: bold;
	font-size: 2.5rem;
}

.menu-ctn {
	display: flex;
	flex-direction: column;
}

.menu-ctn a {
	margin: 5px 0;
	font-size: 1.5rem;
	color: orange;
}
</style>

</head>
<body class="text-white">
	<jsp:include page="common/navbar.jsp"></jsp:include>
	<%-- 	<nav>
		<c:if test="${isloggedin == true }">
			<a href="./logout">Logout</a>
		</c:if>
		<c:if test="${isloggedin == false}">
			<nav class="text-4xl flex justify-around">
				<a class="" href="./login">Login</a> <a class="" href="./register">Register</a>
			</nav>
		</c:if>

	</nav> --%>
	<c:if test="${isloggedin == false }">
		<div class="m-auto text-center">
			<!-- 			<p class="text-2xl m-auto text-center text-red-700">You are not
				logged in.</p>
			 -->
			<c:if test="${failed == true}">
				<p class="text-red-300">${msg}</p>
			</c:if>

			<div class="heading">
				<h1 class="salutations">
					Welcome to the blockchain. Please <a href="./register">Register</a>
					or <a class="" href="./login">Login</a>
				</h1>
			</div>
			<h3>Or just look around</h3>
			<div class="menu-ctn">



				<a href="./blockchain">Our live blockchain status</a> <a
					href="./transactionpool">Live Transaction Pool (unmined
					transactions)</a>

			</div>
	</c:if>
	</div>
	<!-- 	<img class="m-auto rounded-full" alt="coffee bean close up"
		src="./resources/bean.png"> -->


	<c:if test="${isloggedin == true }">
		<h1 class="h1">Welcome to the club ${user.getUsername()}</h1>

		<h2 class="h2">Some things you can do</h2>

		<a class="text-4xl text-green-700" href="./wallet/">Open Your
			wallet</a>
		<br>
		<a class="text-4xl text-green-700" href="./wallet/transact">Make a
			Transaction</a>
		<br>
		<a class="text-4xl text-green-700" href="./blockchain">Explore our
			live blockchain</a>
		<br>
		<a class="text-4xl text-green-700" href="./blockchain/mine">Mine a
			Block</a>
		<br>

		<a class="text-4xl text-green-700" href="./transactionpool">View
			the live Transaction Pool</a>
		<br>


	</c:if>
</body>
</html>