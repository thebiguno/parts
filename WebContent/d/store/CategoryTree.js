Ext.define("Parts.store.CategoryTree", {
	"extend": "Ext.data.TreeStore",
	"fields": [ "id", "name" ],
	"autoLoad": true,
	"remoteSort": true,
	"remoteFilter": false,
	
	"root": {
		"name": "All",
		"expanded": true,
		"icon": "img/categories.png"
	},

	"proxy": {
		"type": "ajax",
		"method": "GET",
		"url": "categories",
		"reader": {
			"type": "json",
			"rootProperty": "data"
		}
	}
});
