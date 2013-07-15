Ext.override("Ext.panel.Panel", {
	"border": false
});

Ext.define("Parts.view.Viewport", {
	"extend": "Ext.container.Viewport", 
	
	"stateful": true,
	"stateId": "viewport",
	
	"requires": [
		"Parts.view.CatalogTree",
		"Parts.view.PartList"
	],
	
	"layout": "border",

	"items": [
		{
			"region": "north",
			"xtype": "toolbar",
			"items": [
				{
					"xtype": "button",
					"text": "New",
					"itemId": "newbutton"
				},
				"-",
				{
					"xtype": "textfield",
					"emptyText": "Search Terms",
					"itemId": "searchterms"
				},
				{
					"xtype": "button",
					"text": "Search",
					"itemId": "searchbutton"
				},
				"-",
				{
					"xtype": "textfield",
					"emptyText": "Digikey URL",
					"itemId": "digikeyurl"
				},
				{
					"xtype": "textfield",
					"emptyText": "Quantity",
					"itemId": "digikeyqty"
				},
				{
					"xtype": "button",
					"text": "Add",
					"itemId": "addbutton"
				},
				"-",
				{
					"xtype": "button",
					"text": "Report",
					"itemId": "reportbutton"
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
		}
	]
});