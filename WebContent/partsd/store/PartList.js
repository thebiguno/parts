Ext.define("Parts.store.PartList", {
	"extend": "Ext.data.Store",
	"fields": [ "part", "description", "quantity", "datasheets" ],
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
