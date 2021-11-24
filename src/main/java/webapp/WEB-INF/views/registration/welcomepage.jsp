<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
<style type="text/css">
body {
	background-color: #3D3D3D;
	color: white;
}

nav {
	display: flex;
	justify-content: space-around;
	background-color: darkgreen;
	color: white;
	font-weight: bold;
}

.card {
	display: flex;
	flex-direction: column;
}

.card p {
	font-size: 1.5rem;
}

#mySecretInput {
	z-index: -10;
}

h1, .h1 {
	font-size: 4rem;
}

h3, a, .h3 {
	font-size: 3rem;
	color: darkgreen;
	text-decoration: none;
}
</style>
</head>

<body>
	<jsp:include page="../common/navbar.jsp"></jsp:include>
	<h1 class="h1">Welcome to the club ${user.getUsername() }</h1>

	<br>

	<h3 class="h3">
		We created a <a href="./wallet/">Wallet</a> for you and initialized it
		with 1.000 beancoin. Enjoy
	</h3>
	<br>
	<br>
	<a href="./">Home</a>

</body>
</html>