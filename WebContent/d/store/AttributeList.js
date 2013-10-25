Ext.define("Parts.store.AttributeList", {
	"extend": "Ext.data.Store",
	"fields": [ "id", "name", "value", "href", "icon" ],
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
