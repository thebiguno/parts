<html>
	<head>
		<title>Parts Database</title>
		<link rel="stylesheet" type="text/css" href="media/css/reset.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="media/css/text.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="media/css/grid.css" media="screen" />
		<link rel="stylesheet" type="text/css" href="media/css/layout.css" media="screen" />
	</head>
	<body>
		<div class="container_12">
			<div class="grid_4">
				<div class="box">
					<div class="block">
						<form action="index" method="POST">
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
						<form action="index" method="POST">
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
							<li><a href="parts/new.html">Add new Part</a></li>
							<li><a href="index.html">Catalog</a></li>
						</ul>
					</div>
				</div>
			</div>
			<div class="clear"></div>
			
			<div class="grid_12">
				<div class="box">
					<#list categories as c>
					<h2>${c.getName()?html}</h2>
					<div class="block">
						<ul>
							<#list c.families as f>
							<li><a href="parts/${c.getName()?url}/${f.getName()?url}"/>${f.getName()?html} (${f.getPartIds().size()})</a></li>
							</#list>
						</ul>
					</div>
					</#list>
				</div>
			</div>
			<div class="clear"></div>
		</div>
	<body>
</html>