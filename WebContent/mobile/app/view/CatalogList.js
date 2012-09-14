Ext.define("mobile.view.CatalogList", {
	extend: 'Ext.dataview.List',
	alias: "widget.catalog-list",
	requires: ["mobile.store.CatalogList"],
	config: {
		title: "Catalog",
		fullscreen: true,
		store: Ext.create("mobile.store.CatalogList"),
		grouped: true,
		itemTpl: "<div>{family}</div>"
	}
});
