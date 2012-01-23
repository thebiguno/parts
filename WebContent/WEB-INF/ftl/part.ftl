<html>
	<head>
		<title>${title}</title>
		<script type="text/javascript" src="media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="media/js/jquery.jeditable.mini.js"></script>
		<script type="text/javascript">
$(document).ready(function() {
	$('.deleteAttrButton').click(function() {
		$.ajax({
			url: document.location.href,
			data: { name: $(this).parent().attr('name'), action: 'delete' }
			type: "POST",
			success: function() { $(this).parent().remove(); }
		});
	});
	$('#deletePartButton').click(function() {
		$.ajax({
			url: document.location.href,
			type: "DELETE",
			success: function() { document.location.href = "../"; }
		});
	});
	$('.propertyName').editable(document.location.href, { id: 'name', submitdata: { action: 'update_name' } });
	$('.propertyValue').editable(document.location.href, { id: 'name', submitdata: { action: 'update_value' } });
});
		</script>
	</head>
	<body>
		<form action="../search" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" maxlength="250" value=""/>
		</form>
		<h2>${title}</h2>
		<button type="button" id="deletePartButton" title="Delete part">
		<table>
			<thead>
				<tr>
					<th>Name</th>
					<th>Value</th>
				</tr>
			</thead>
			<tbody>
				<#list attributes as a>
				<tr>
					<th class="attributeName" name="${a.getName()?html}">${a.getName()?html}</th>
					<td class="attributeName" name="${a.getName()?html}">
					<#if a.getHref()??><a href="${a.getHref()?html}">${a.getValue()?html}</a><#else>${a.getValue()?html}</#if>
					</td>
				</tr>
				</#list>
			</tbody>
		</table>
	<body>
</html>