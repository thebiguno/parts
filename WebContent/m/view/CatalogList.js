Ext.define("Parts.view.CatalogList", {
	"extend": 'Ext.NestedList',
	"alias": "widget.catalog-list",

	"config": {
		"fullscreen": true,
		"store": "CatalogList",
		"title": "Categories",
		"displayField": "name",
		"items": [
			{
				"xtype": "toolbar",
				"docked": "top",
				"items": [
					{
						"xtype": "searchfield",
						"placeHolder": "Search",
						"id":"search"
					}
				]
			}
		]
	}
});
