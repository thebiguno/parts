<html>
	<head>
		<title>${title}</title>
		<script type="text/javascript" src="media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript">
$(document).ready(function() {
	$('.remove').click(function() {
	});
	$('#add').click(function() {
	});
});
		</script>
		<style type="text/css">
td input { width: 100%; }
		</style>
	</head>
	<body>
		<form action="../index" method="POST">
			<label for="keywords">Keywords:</label>
			<input type="text" name="keywords" size="35" value=""/>
			<input type="image" src="../media/img/magnifier.png" alt="Search"/>
		</form>
		<h2>${title}</h2>
		<button type="button" id="deletePartButton" title="Delete part">
		<form action="../part/${part}" method="POST">
			<input type="image" src="../media/img/tick-button.png" alt="Submit"/>
			<table width="100%">
				<thead>
					<tr>
						<th>Name</th>
						<th>Value</th>
						<th>Link</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<#list attributes as a>
					<tr>
						<td><input type="text" name="name" maxlength="255" value="${a.getName()?html}"/></td>
						<td><input type="text" name="value" maxlength="255" value="${a.getValue()?html}"/></td>
						<td><input type="url" name="href" maxlength="255" value="${a.getHref()!?html}"/></td>
						<td width="32">
							<a class="remove" href="#"><img src="../media/img/minus-button.png" alt="Remove"/></a>
							<#if a.getHref()??><a target="datasheet" href="${a.getHref()?html}"><img src="../media/img/chain.png"/></a><#else>&nbsp;</#if>
						</td>
					</tr>
					</#list>
				</tbody>
				</tfoot>
					<tr>
						<td/><td/><td/>
						<td><a class="add" href="#"><img src="../media/img/plus-button.png" alt="Add"/></a>
					</tr>
				</tfoot>
			</table>
		</form>
	<body>
</html>