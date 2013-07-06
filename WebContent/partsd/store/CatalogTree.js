Ext.define("Parts.store.CatalogTree", {
	extend: 'Ext.data.TreeStore',
	fields: [
		"name"
	],
	"autoLoad": true,
	
	"proxy": {
		"type": "ajax",
		url: "data/",
		reader: {
			type: 'json',
			rootProperty: 'data'
		}
	}
});
