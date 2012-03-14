<html>
	<head>
		<title>${category?html} | ${family?html}</title>
		<link rel="stylesheet" type="text/css" href="../../media/css/reset.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../../media/css/text.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../../media/css/grid.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="../../media/css/layout.css" media="screen" />
		<script type="text/javascript" src="../../media/js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript">
		</script>
	</head>
	<body>
		<div class="container_12">
			<div class="grid_4">
				<div class="box">
					<div class="block">
						<form action="../../index" method="POST">
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
						<form action="../../index" method="POST">
							<fieldset class="login">
								<p>
									<label for="name">Add from Digikey URL:</label>
									<input style="display:inline; width: 45%" type="text" name="dk" size="30" value=""/>
									<input style="display:inline; width: 10%" type="text" name="qty" size="5" value=""/>
									<input style="display:inline; width: 16px;" type="image" src="media/img/tick-button.png" alt="Submit"/>
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
							<li><a href="../../parts/new.html">Add new Part</a></li>
							<li><a href="../../index.html">Catalog</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="clear"></div>
			<div class="grid_12">
				<div class="box">
					<h2>${category?html} | ${family?html}</h2>
					<div class="block">
						<table>
							<thead>
								<tr>
									<th>Manufacturer Part Number</th>
									<th>Description</th>
									<th>Notes</th>
									<th>Manufacturer</th>
									<th>Quantity In Stock</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
								<#list parts as part>
								<tr>
									<td><a href="../${part.getId()}"><#attempt>${part.findAttribute("Manufacturer Part Number").getValue()?html}<#recover>Undefined</#attempt></a></td>
									<td><#attempt>${part.findAttribute("Description").getValue()?html}<#recover>-</#attempt></td>
									<td><#attempt>${part.findAttribute("Manufacturer").getValue()?html}<#recover>-</#attempt></td>
									<td><#attempt>${part.findAttribute("Notes").getValue()?html}<#recover>-</#attempt></td>
									<td><#attempt>${part.findAttribute("Quantity In Stock").getValue()?html}<#recover>-</#attempt></td>
									<td width="34">
										<form action="../${part.getId()}?method=delete" method="POST"><input type="image" src="../../media/img/minus-button.png" alt="Remove"/></form>
									</td>
								</tr>
								</#list>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	<body>
</html>