Ext.define("PartsDB.view.CatalogList", {
	extend: 'Ext.dataview.List',
	alias: "widget.catalog-list",
	config: {
		title: "Catalog",
		store: Ext.create("PartsDB.store.CatalogList"),
		grouped: true,
		itemTpl: "<div>{family}</div>"
	}
});
