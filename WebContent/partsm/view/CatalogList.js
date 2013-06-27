Ext.define("Parts.view.CatalogList", {
	extend: 'Ext.dataview.List',
	alias: "widget.catalog-list",
	config: {
		fullscreen: true,
		store: "CatalogList",
		grouped: true,
		itemTpl: "<div>{family}</div>"
	}
});
