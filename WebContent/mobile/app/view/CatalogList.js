Ext.define("mobile.view.CatalogList", {
	extend: 'Ext.dataview.List',
	alias: "widget.catalog-list",
	config: {
		title: "Catalog",
		fullscreen: true,
		store: Ext.create("mobile.store.CatalogList"),
		grouped: true,
		itemTpl: "<div>{family}</div>"
	}
});
