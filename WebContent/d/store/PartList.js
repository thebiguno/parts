Ext.define("Parts.store.PartList", {
	"extend": "Ext.data.Store",
	"fields": [ "id", "category", "number", "description", "notes", "minimum", "available" ],
	"autoLoad": false,
	
	"proxy": {
		"type": "ajax",
		"url": "dynamic",
		"reader": {
			"type": "json",
			"root": "data"
		}
	}
});
