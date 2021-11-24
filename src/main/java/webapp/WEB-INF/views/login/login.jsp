<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login</title>
<link href="https://unpkg.com/tailwindcss@^1.0/dist/tailwind.min.css"
	rel="stylesheet">
<style type="text/css">
body {
	background-color: darkgray;
}
nav {
	display: flex;
	justify-content: space-around;
	background-color: darkgreen;
	color: white;
	font-weight: bold;
}
</style>
</head>

<body>
	<jsp:include page="../common/navbar.jsp"></jsp:include>
<!-- 	<nav> <a href="./register">New here?</a> <a href="./">Home</a> </nav> -->

	<div
		class="login-form text-center mx-auto my-4 w-64 flex shadow-lg flex-col bg-cover bg-center justify-content bg-white p-6 rounded pt-8 pb-8">
		<div class="text-center text-gray-500 mb-6">
			<h2>Log in</h2>
		</div>
		<div>
			<form:form id="loginForm" modelAttribute="login" method="post">
				<form:input path="username"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="text" placeholder="Username" />
				<form:input path="password"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="password" placeholder="password" />
				<!-- 				<div class="flex mt-4">
					<input type="checkbox" class="mr-2" name="agreement" value="agree">
					<p class="text-grey">
						Accept the <a href="#"
							class=" no-underline text-green-500 hover:text-green-400">Terms
							and Conditions </a>
					</p>
				</div>
 -->
				<form:button
					class="shadow-lg pt-3 pb-3 mt-6 w-full text-white bg-green-500 hover:bg-green-400 rounded-full "
					id="login" name="SIGN IN">Sign In
				</form:button>
			</form:form>
		</div>
		<div>
			<p class="mt-4 text-gray-500 text-sm">
				Need an account? <a href="./register"
					class="no-underline text-green-500 hover:text-green-400">Register
				</a>
			</p>
		</div>
	</div>
</body>

</html>