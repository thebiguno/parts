<html>
	<head>
		<title>${family.getName()?html}</title>
		<script type="text/javascript" src="media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="media/js/jquery.jeditable.mini.js"></script>
		<script type="text/javascript">
$(document).ready(function() {
	$('.partName').editable(document.location.href, { id: 'name', submitdata: { action: 'update_name' } });
});
		</script>
	</head>
	<body>
		<form action="../search" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" maxlength="250" value=""/>
		</form>
		<h2>${family.getName()?html}}</h2>
		<table>
			<tr>
				<th>Name</th>
				<th>Manufacturer Part Number</th>
				<th>Description</th>
				<th>Manufacturer</th>
				<th>Quantity In Stock</th>
			</tr>
			<#list family.getNodes() as part>
			<tr>
				<td class="partName">${part.getName()?html}</td>
				<td>${part.getValue("Manufacturer Part Number").getString()?html}</td>
				<td>${part.getValue("Description").getString()?html}</td>
				<td>${part.getValue("Manufacturer").getString()?html}</td>
				<td>${part.getValue("Quantity In Stock").getString()?html}</td>
			</tr>
			</#list>
		</table>
	<body>
</html>