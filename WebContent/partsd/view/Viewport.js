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