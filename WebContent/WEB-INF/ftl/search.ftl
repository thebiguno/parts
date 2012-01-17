<html>
	<head>
	</head>
	<body>
		<form action="search" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" maxlength="250" value=""/>
		</form>
		<#list categories! as c>
		<h2>${c.getName()?html}</h2>
		<ul>
			<#list c.families as f>
			<li><a href="../catalog/${c.getName()?url}/${f.getName()?url}"/>${f.getName()?html} (${f.productCount})</li>
			</#list>
		</ul>
		</#list>
	<body>
</html>