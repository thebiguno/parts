<html>
	<head>
	</head>
	<body>
		<form action="index" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" value=""/>
			<button type="submit">Search</button>
		</form>
		
		<form action="index" method="POST">
			<label for="name">Digikey URL:</label>
			<input type="text" name="dk" size="35" value=""/>
			<button type="submit">Add Part</button>
		</form>
		
		<a href="parts/new.html">Add new Part</a>

		<#list categories as c>
		<h2>${c.getName()?html}</h2>
		<ul>
			<#list c.families as f>
			<li><a href="parts/${c.getName()?url}/${f.getName()?url}"/>${f.getName()?html} (${f.getPartIds().size()})</li>
			</#list>
		</ul>
		</#list>
	<body>
</html>