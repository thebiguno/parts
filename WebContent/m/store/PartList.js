Ext.define("Parts.store.PartList", {
	"extend": 'Ext.data.Store',
	"config": {
		"fields": [ "id", "category", "number", "group", "description", "available", "minimum", "notes" ],
		"autoLoad": true,
		
		"grouper": {
			"groupFn": function(record) {
				return record.get('group');
			}
		},

		"proxy": {
			"type": "ajax",
			"method": "GET",
			"url": "categories/0/parts",
			"reader": {
				"type": "json",
				"rootProperty": "data"
			}
		}
	}
});
