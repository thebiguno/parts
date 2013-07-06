Ext.override("Ext.panel.Panel", {
	"border": false
});

Ext.define("Parts.view.Viewport", {
	"extend": "Ext.container.Viewport", 
	
	"stateful": true,
	"stateId": "viewport",
	
	"requires": [
		"Parts.view.CatalogTree",
		//"Parts.view.PartList"
	],
	
	"layout": "border",

	"items": [
		{
			"region": "center",
			"xtype": "catalogtree"
		}
//		{
//			"region": "center",
//			"xtype": "partlist"
//		}
	]
});