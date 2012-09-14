Ext.define("mobile.view.FamilyList", {
	extend: 'Ext.dataview.List',
	alias: "widget.family-list",
	requires: ["mobile.store.FamilyList"],
	config: {
		store: Ext.create("mobile.store.FamilyList"),
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
