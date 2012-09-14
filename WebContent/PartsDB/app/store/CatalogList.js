Ext.define("PartsDB.store.CatalogList", {
	extend: 'Ext.data.Store',
	requires: ["PartsDB.model.CatalogList"],
	config: {
		model: "PartsDB.model.CatalogList",
		grouper: {
			groupFn: function(record) {
				return record.get('category');
			}
		},
		autoLoad: true,
		proxy: {
			type: "ajax",
			url: "../m/index",
			reader: {
				type: 'json',
				rootProperty: 'data'
			}
		}
	}
});
