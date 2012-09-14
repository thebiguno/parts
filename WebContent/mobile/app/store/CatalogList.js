Ext.define("mobile.store.CatalogList", {
	extend: 'Ext.data.Store',
	requires: ["mobile.model.CatalogList"],
	config: {
		model: "mobile.model.CatalogList",
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
