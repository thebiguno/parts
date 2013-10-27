Ext.define("Parts.store.CategoryTree", {
	"extend": "Ext.data.TreeStore",
	"fields": [ "id", "name" ],
	"autoLoad": true,
	"remoteSort": true,
	"remoteFilter": false,
	
	"root": {
		"id": 0,
		"name": "All",
		"expanded": true,
		"icon": "img/categories.png",
	},

	"proxy": {
		"type": "ajax",
		"method": "GET",
		"url": "categories",
		"reader": {
			"type": "json",
			"rootProperty": "children"
		}
	}
});
