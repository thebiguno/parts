Ext.define("Parts.store.CatalogTree", {
	"extend": "Ext.data.TreeStore",
	"fields": [ "name", "category", "family" ],
	"autoLoad": true,
	
	"root": {
		"name": "All",
		"expanded": true,
		"category": "*",
		"family": "*"
	},
	

	"proxy": {
		"type": "ajax",
		"url": "data/",
		"reader": {
			"type": "json",
			"rootProperty": "data"
		}
	}
});
