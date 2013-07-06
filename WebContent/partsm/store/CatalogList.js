Ext.define("Parts.store.CatalogList", {
	extend: 'Ext.data.Store',
	config: {
		fields: [
			"family",
			"category"
		],
		grouper: {
			groupFn: function(record) {
				return record.get('category');
			}
		},
		autoLoad: true,
		proxy: {
			type: "ajax",
			url: "data/",
			reader: {
				type: 'json',
				rootProperty: 'data'
			}
		}
	}
});
