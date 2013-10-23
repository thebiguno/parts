Ext.define("Parts.store.CatalogTree", {
	"extend": "Ext.data.TreeStore",
	"fields": [ "name", "category", "family" ],
	"autoLoad": true,
	
	"root": {
		"name": "All",
		"expanded": true,
		"category": null,
		"family": null
	},
	

	"proxy": {
		"type": "ajax",
		"url": "catalog",
		"reader": {
			"type": "json",
			"rootProperty": "data"
		}
	}
});
