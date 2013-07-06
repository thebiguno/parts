Ext.define("Parts.store.PartList", {
	extend: 'Ext.data.Store',
	fields: [
		"name"
	],
	"autoLoad": false,
	
	"proxy": {
		"type": "ajax",
		url: "data/", // this will be dynamic
		reader: {
			type: 'json',
			rootProperty: 'data'
		}
	}
});
