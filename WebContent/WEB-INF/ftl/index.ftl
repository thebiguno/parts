<html>
	<head>
	</head>
	<body>
		<form action="index" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" maxlength="250" value=""/>
		</form>
		
		<form action="index" method="POST">
			<label for="name">Name:</label>
			<input type="text" name="name" size="35" maxlength="250" value=""/>
			<label for="name">Category:</label>
			<input type="text" name="category" size="35" maxlength="250" value=""/>
			<label for="name">Family:</label>
			<input type="text" name="family" size="35" maxlength="250" value=""/>
		</form>
		
		<form action="index" method="POST">
			<label for="name">Digikey PN:</label>
			<input type="text" name="dk" size="35" value=""/>
		</form>

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