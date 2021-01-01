<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
Design by Free CSS Templates
http://www.freecsstemplates.org
Released for free under a Creative Commons Attribution 3.0 License

Name       : Unofficial Channels
Description: A two-column, fixed-width design with a bright color scheme.
Version    : 1.0
Released   : 20120723
-->
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<title>
			VAPORware Marketplace
		</title>
		<link href="http://fonts.googleapis.com/css?family=Arvo" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="style.css" />
		<script language="JavaScript">

			// Put the active cursor on the search box when the page first loads
			window.onload = function() {
				document.forms[0].elements["searchString"].focus();
			}
			
			// When the search button is clicked, ensure that the search box is not empty and then submit the search form
			function submitSearchForm() {
				var searchString = document.forms[0].elements["searchString"];
				if(searchString.value == null || searchString.value.replace(/^\s+|\s+$/g, '') == "") {
					alert("You must enter some search words");
				} else {
					document.forms[0].submit();
				}
			}
		</script>
	</head>
	<body>
		<div id="bg1"></div>
		<div id="bg2"></div>
		<div id="outer">
			<div id="header">
				<div id="logo">
					<h1><a href="index.html"><i>VAPORware</i> Marketplace</a></h1>
				</div>
				<form action="search" method="post">
					<div id="search">
						<div>
							<input class="text" name="searchString" size="32" maxlength="64" />
							<img src="images/search.png" style="margin: -13px;" onclick="submitSearchForm()"/>
						</div>
					</div>
				</form>
			</div>
			<div id="banner">
				<div class="captions">
					<h2><i>The</i>&nbsp;&nbsp;source for apps that may or may not ever be developed</h2>
				</div>
				<img src="images/banner.jpg" alt="" height="150" width="1180"/>
			</div>
			<div id="main">
				<div id="sidebar">
					<div class="box">
						<h3>Upcoming Releases</h3>
						<div class="dateList">
							<ul class="linkedList dateList">
								<li class="first">
									<span class="date">Jul 23</span> Frustrated Flamingos
								</li>
								<li>
									<span class="date">Jul 28</span> Athena Internet Radio
								</li>
								<li>
									<span class="date">Aug 7</span> Dome Web Browser
								</li>
								<li>
									<span class="date">Aug 9</span> Gripe Video Conferencing
								</li>
							</ul>
						</div>
					</div>
				</div>
				<div id="content">
					<div class="box">
						<h2>Search Results</h2>
						
						<!-- Iterate through the search results inserted into this request by the controller servlet, displaying a row for each. -->
						
						<table style="width: 100%; margin-left: auto; margin-right: auto;">
						<c:forEach var="app" items="${apps}">
						<tr>
							<td style="width: 25%; text-align: center; vertical-align: middle;" rowspan="2"><img src="images/apps/${app.image}"/></td>
							<td style="height: 1em; width: 75%; text-align: left; vertical-align: bottom; border-bottom: thin dotted black;"><b>${app.name}</b></td>							
						</tr>
						<tr>
							<td style="text-align: left; vertical-align: top; padding-bottom: 30px;">${app.description}</td>
						</tr>
						</c:forEach>
						</table>
					</div><br class="clear" />
				</div><br class="clear" />
			</div>
		</div>
		<div id="copyright">
			This application uses HTML/CSS design from <a href="http://www.freecsstemplates.org/">freecsstemplates.org</a>, 
			and photos by <a href="http://fotogrph.com/">Fotogrph</a>, both made available under the 
			<a href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution license</a>.
		</div>
	</body>
</html>
