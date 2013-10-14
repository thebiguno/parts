Ext.define("Parts.store.FamilyList", {
	extend: 'Ext.data.Store',
	config: {
		fields: [
			"part",
			"description",
			"notes",
			"quantity",
			"datasheets"
		],
		autoLoad: true,
		proxy: {
			type: "ajax",
			url: "dynamic",
			reader: {
				type: 'json',
				rootProperty: 'data'
			}
		}
	}
});
