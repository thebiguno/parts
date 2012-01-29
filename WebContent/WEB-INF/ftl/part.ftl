<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title><#attempt>${title}<#recover>Untitled</#attempt></title>
		<link rel="stylesheet" type="text/css" href="../media/css/reset.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../media/css/text.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../media/css/grid.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../media/css/layout.css" media="screen" />
		<script type="text/javascript" src="../media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript">
$(document).ready(function() {
	$('.remove').live('click', function() {
		$(this).parent().parent().remove();
	});
	$('#add').click(function() {
		var row = [];
		row.push('<tr>');
		row.push('<td><input type="text" name="name" maxlength="255" value=""/></td>');
		row.push('<td><input type="text" name="value" maxlength="255" value=""/></td>');
		row.push('<td><input type="url" name="href" maxlength="255" value=""/></td>');
		row.push('<td width="32"><a class="remove" href="#"><img src="../media/img/minus-button.png" alt="Remove"/></a></td>');
		row.push('</tr>');
		$('#attributes tbody').append(row.join(''));
		return false;
	});
});
		</script>
		<style type="text/css">
td input { width: 100%; }
		</style>
	</head>
	<body>
		<div class="container_12">
			<div class="grid_4">
				<div class="box">
					<div class="block">
						<form action="../index" method="POST">
							<fieldset class="login">
								<p>
									<label for="keywords">Search Keywords:</label>
									<input type="text" name="keywords" value=""/>
								</p>
							</fieldset>
						</form>
					</div>
				</div>
			</div>
			<div class="grid_4">
				<div class="box">
					<div class="block">
						<form action="../index" method="POST">
							<fieldset class="login">
								<p>
									<label for="name">Add from Digikey URL:</label>
									<input type="text" name="dk" size="35" value=""/>
								</p>
							</fieldset>
						</form>
						
					</div>
				</div>
			</div>
			<div class="grid_4">
				<div class="box">
					<div class="block">
						<ul class="menu">
							<li><a href="../parts/new.html">Add new Part</a></li>
							<li><a href="../index.html">Catalog</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="clear"></div>
			
			<div class="grid_12"/>
				<div class="box">
					<h2><#attempt>${title}<#recover>Untitled</#attempt></h2>
					<div class="block">
						<form action="../parts/${part}" method="POST">
							<table id="attributes">
								<thead>
									<tr>
										<th>Name</th>
										<th>Value</th>
										<th>Link</th>
										<th width="34">
											<a id="add" href="#"><img src="../media/img/plus-button.png" alt="Add"/></a><input type="image" src="../media/img/tick-button.png" alt="Submit"/>
										</th>
									</tr>
								</thead>
								<tbody>
									<#list attributes as a>
									<tr>
										<td><input type="text" name="name" maxlength="255" value="${a.getName()?html}"/></td>
										<td><input type="text" name="value" maxlength="255" value="${a.getValue()?html}"/></td>
										<td><input type="url" name="href" maxlength="255" value="${a.getHref()!?html}"/></td>
										<td width="34">
											<a class="remove" href="#"><img src="../media/img/minus-button.png" alt="Remove"/></a><#if a.getHref()??><a target="datasheet" href="${a.getHref()?html}"><img src="../media/img/navigation-000-button.png"/></a><#else>&nbsp;</#if>
										</td>
									</tr>
									</#list>
								</tbody>
							</table>
						</form>
					</div/>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	<body>
</html>