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
	<nav>
		<a href="./">Home</a>
	</nav>



	<section class="py-20 bg-white">
		<div class="container max-w-6xl mx-auto">
			<h2 class="text-4xl font-bold tracking-tight text-center">Your
				Wallet</h2>
			<p class="mt-2 text-lg text-center text-gray-600">Check out our
				list of awesome features below.</p>
			<div
				class="grid grid-cols-1 gap-8 mt-10 sm:grid-cols-1 lg:grid-cols-1 sm:px-8 xl:px-0">

				<div
					class="relative flex flex-col items-center justify-between col-span-4 px-8 py-12 space-y-4 overflow-hidden bg-gray-100 rounded-none rounded-xl">
					<div class="p-3 text-white bg-green-600 rounded-none rounded-xl">

					</div>
					<h4 class="text-xl font-medium text-gray-700">Certifications</h4>
					<p class="text-base text-center text-gray-500">Each of our plan
						will provide you and your team with certifications.</p>
				</div>

				<div
					class="flex flex-col items-center justify-between col-span-4 px-8 py-12 space-y-4 bg-gray-100 rounded-none rounded-xl">
					<div class="p-3 text-white bg-green-600 rounded-none rounded-xl">
						<svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 "
							viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"
							fill="none" stroke-linecap="round" stroke-linejoin="round">
							<path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
							<path d="M18 8a3 3 0 0 1 0 6"></path>
							<path d="M10 8v11a1 1 0 0 1 -1 1h-1a1 1 0 0 1 -1 -1v-5"></path>
							<path
								d="M12 8h0l4.524 -3.77a0.9 .9 0 0 1 1.476 .692v12.156a0.9 .9 0 0 1 -1.476 .692l-4.524 -3.77h-8a1 1 0 0 1 -1 -1v-4a1 1 0 0 1 1 -1h8"></path></svg>
					</div>
					<h4 class="text-xl font-medium text-gray-700">Notifications</h4>
					<p class="text-base text-center text-gray-500">Send out
						notifications to all your customers to keep them engaged.</p>
				</div>

				<div
					class="flex flex-col items-center justify-between col-span-4 px-8 py-12 space-y-4 bg-gray-100 rounded-none rounded-xl">
					<div class="p-3 text-white bg-green-600 rounded-none rounded-xl">
						<svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8 "
							viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor"
							fill="none" stroke-linecap="round" stroke-linejoin="round">
							<path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
							<polyline points="12 3 20 7.5 20 16.5 12 21 4 16.5 4 7.5 12 3"></polyline>
							<line x1="12" y1="12" x2="20" y2="7.5"></line>
							<line x1="12" y1="12" x2="12" y2="21"></line>
							<line x1="12" y1="12" x2="4" y2="7.5"></line>
							<line x1="16" y1="5.25" x2="8" y2="9.75"></line></svg>
					</div>
					<h4 class="text-xl font-medium text-gray-700">Bundles</h4>
					<p class="text-base text-center text-gray-500 cc_cursor">High-quality
						bundles of awesome tools to help you out.</p>
				</div>

			</div>
		</div>
	</section>

	<h1>Your Wallet</h1>

	Address: ${wallet.getAddress() }
	<br> Balance: ${wallet.getBalance() }
	<br>
	<br> public key: ${wallet.getPublickey().toString() }
	<br>
	<br>
	<a href="./transact/">Make a transaction</a>
	<br>
	<p>Or send a post request to (/wallet/transact") in the following
		JSON format</p>
	<p>{ "address": "foo", "amount": 500 }</p>


	<!-- Success alert -->
	<div class="flex bg-green-200 p-4">
		<div class="mr-4">
			<div
				class="h-10 w-10 text-white bg-green-600 rounded-full flex justify-center items-center">
				<i class="material-icons">done</i>
			</div>
		</div>
		<div class="flex justify-between w-full">
			<div class="text-green-600">
				<p class="mb-2 font-bold">Succes alert</p>
				<p class="text-xs">description text</p>
			</div>
			<div class="text-sm text-gray-500">
				<span>x</span>
			</div>
		</div>
	</div>
	<div>
		Icons made by <a
			href="https://www.flaticon.com/authors/vitaly-gorbachev"
			title="Vitaly Gorbachev">Vitaly Gorbachev</a> from <a
			href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
	</div>
</body>
</html>



















