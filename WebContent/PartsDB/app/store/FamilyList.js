Ext.define("PartsDB.store.FamilyList", {
	extend: 'Ext.data.Store',
	config: {
		model: "PartsDB.model.FamilyList",
		autoLoad: true,
		proxy: {
			type: "ajax",
			url: "../m/index.json",
			reader: {
				type: 'json',
				rootProperty: 'data'
			}
		}
	}
});
