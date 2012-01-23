<html>
	<head>
		<title>${category?html} | ${family?html}</title>
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
		<h2>${category?html} | ${family?html}</h2>
		<table>
			<tr>
				<th>Name</th>
				<th>Manufacturer Part Number</th>
				<th>Description</th>
				<th>Manufacturer</th>
				<th>Quantity In Stock</th>
			</tr>
			<#list parts as part>
			<tr>
				<td>${part.findAttribute("Manufacturer Part Number").getValue()?html}</td>
				<td>${part.findAttribute("Description").getValue()?html}</td>
				<td>${part.findAttribute("Manufacturer").getValue()?html}</td>
				<td><#attempt>${part.findAttribute("Quantity In Stock").getValue()?html}<#recover></#attempt></td>
			</tr>
			</#list>
		</table>
	<body>
</html>