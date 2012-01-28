<html>
	<head>
		<title>${category?html} | ${family?html}</title>
		<script type="text/javascript" src="media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript">
		</script>
	</head>
	<body>
		<form action="../../index" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" value=""/>
			<button type="submit">Search</button>
		</form>
		<h2>${category?html} | ${family?html}</h2>
		<table>
			<tr>
				<th>Manufacturer Part Number</th>
				<th>Description</th>
				<th>Manufacturer</th>
				<th>Quantity In Stock</th>
			</tr>
			<#list parts as part>
			<tr>
				<td><a href="../${part.getId()}">${part.findAttribute("Manufacturer Part Number").getValue()?html}</a></td>
				<td>${part.findAttribute("Description").getValue()?html}</td>
				<td>${part.findAttribute("Manufacturer").getValue()?html}</td>
				<td><#attempt>${part.findAttribute("Quantity In Stock").getValue()?html}<#recover></#attempt></td>
			</tr>
			</#list>
		</table>
	<body>
</html>