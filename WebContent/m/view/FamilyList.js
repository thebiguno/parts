Ext.define("Parts.view.FamilyList", {
	extend: 'Ext.dataview.List',
	alias: "widget.family-list",
	config: {
		store: "FamilyList",
		itemTpl: "<div>{description}</div>",
		detailCard: {
			html: "<p>Foo</p><p>Bar Baz!</p>"
		},
		items: [
			{
				xtype: "toolbar",
				docked: "top",
				items: [
					{
						xtype: "button",
						text: "Catalog",
						ui: "back",
						id:"back-catalog-list"
					}
				]
			}
		]
	}
});