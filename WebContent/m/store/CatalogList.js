Ext.define("Parts.store.CatalogList", {
	"extend": 'Ext.data.TreeStore',
	"root": {
		"id": 0,
		"name": "All"
	},
	"rootVisible": true,
	"config": {
		"fields": [ "id", "name" ],
		"autoLoad": true,
		
		"proxy": {
			"type": "ajax",
			"method": "GET",
			"url": "categories",
			"reader": {
				"type": "json",
				"rootProperty": "children"
			}
		}
	}
});
