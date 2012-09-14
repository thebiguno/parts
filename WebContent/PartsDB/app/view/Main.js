Ext.define("PartsDB.view.Main", {
	extend: 'Ext.tab.Panel',
	requires: [
		'Ext.TitleBar'
	],
	config: {
		activeItem: 1,
		tabBarPosition: 'bottom',

		items: [
			{
				xtype: "panel",
				title: "Search"
			},
			{
				xtype: "catalog-list",
				listeners: {
					disclose: {
						fn: this.onCatalogListDisclose,
						scope: this
					}
				}
			}
		]
	}
});
