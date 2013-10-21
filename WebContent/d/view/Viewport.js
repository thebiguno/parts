Ext.override("Ext.panel.Panel", {
	"border": false
});

Ext.define("Parts.view.Viewport", {
	"extend": "Ext.container.Viewport", 
	
	"stateful": true,
	"stateId": "viewport",
	
	"requires": [
		"Parts.view.CatalogTree",
		"Parts.view.PartList",
		"Parts.view.AttributeList"
	],
	
	"layout": "border",

	"items": [
		{
			"region": "north",
			"xtype": "toolbar",
			"items": [
				{
					"xtype": "textfield",
					"emptyText": "Search Terms",
					"itemId": "searchterms"
				},
				{
					"xtype": "button",
					"text": "Search",
					"itemId": "searchbutton",
					"icon": "img/magnifier.png"
				},
				"-",
				{
					"xtype": "textfield",
					"emptyText": "Digikey URL",
					"itemId": "digikeyurl"
				},
				{
					"xtype": "numberfield",
					"emptyText": "Available",
					"itemId": "digikeyavail",
					//"value": 0,
					"width": 75
				},
				{
					"xtype": "numberfield",
					"emptyText": "Minimum",
					"itemId": "digikeymin",
					//"value": 0,
					"width": 75
				},
				{
					"xtype": "button",
					"text": "Add",
					"itemId": "digikeyadd",
					"icon": "img/digikey.png"
				},
				"-",
				{
					"xtype": "button",
					"text": "Report",
					"itemId": "reportbutton",
					"icon": "img/report.png"
				}
			]
		},
		{
			"region": "west",
			"xtype": "catalogtree",
			"width": 250,
			"split": true
		},
		{
			"region": "center",
			"xtype": "partlist"
		},
		{
			"region": "east",
			"xtype": "attributelist",
			"width": 350,
			"split": true
		}
	]
});