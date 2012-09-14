Ext.define("mobile.store.FamilyList", {
	extend: 'Ext.data.Store',
	config: {
		model: "mobile.model.FamilyList",
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
