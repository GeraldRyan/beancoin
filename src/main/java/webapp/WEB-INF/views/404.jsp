<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
<link href="https://unpkg.com/tailwindcss@^1.0/dist/tailwind.min.css"
	rel="stylesheet">
<style type="text/css">
body {
	background-color: #3D3D3D;
	display: flex;
	justify-content: center;
	align-items: center;
	flex-direction: column;
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

h1 {
	font-size: 3rem;
}

h2 {
	font-size: 2.6rem;
}

a {
	font-size: 2.6rem;
	text-decoration: none;
	color: darkgreen;
}

h3 {
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

	<h1>OOPS, looks like you're lost</h1>
	<a href="/blocks">Head Home</a>
	<p>Web page redirects after 5 seconds.</p>
	<script type="text/javascript">
		setTimeout(function() {
			window.location.href = "http://localhost:8080/blocks/";
		}, 5000);
	</script>

</body>
</html>