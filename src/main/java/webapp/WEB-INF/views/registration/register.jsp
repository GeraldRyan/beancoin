<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Register here</title>
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
<!-- 	<nav>
		<a href="./">Home</a>

	</nav> -->
	<div
		class="login-form text-center mx-auto my-4 w-64 flex shadow-lg flex-col bg-cover bg-center justify-content bg-white p-6 rounded pt-8 pb-8">
		<div class="text-center text-gray-500 mb-6">
			<h2>Register</h2>
			<h2 style="color: red">${regmsg }</h2>
		</div>
		<div>
			<form:form modelAttribute="user" method="post">
				<form:input path="username"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="text" placeholder="Username" />
				<form:errors path="username" />
				<form:input path="password"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="password" placeholder="password" />
				<form:errors path="password" />
				<form:input path="email"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="email" placeholder="email" />
				<form:errors path="email" />
				<form:input path="hint"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="hint" placeholder="hint" />
				<form:errors path="hint" />
				<form:input path="answer"
					class="bg-transparent border-b m-auto block border-gray-500 w-full mb-6 text-gray-500 pb-1"
					type="password" placeholder="answer" />
				<form:errors path="answer" />
				<div class="flex mt-4">
					<input type="checkbox" class="mr-2" name="agreement" value="agree">
					<p class="text-grey">
						Accept the <a href="#"
							class=" no-underline text-green-500 hover:text-green-400">Terms
							and Conditions </a>
					</p>
				</div>

				<form:button
					class="shadow-lg pt-3 pb-3 mt-6 w-full text-white bg-green-500 hover:bg-green-400 rounded-full "
					id="login" name="SIGN IN">Register				</form:button>
			</form:form>
		</div>
		<div>
			<p class="mt-4 text-gray-500 text-sm">
				Already have an account? <a href="./login"
					class="no-underline text-green-500 hover:text-green-400">Login
				</a>
			</p>
		</div>
	</div>



</body>
</html>