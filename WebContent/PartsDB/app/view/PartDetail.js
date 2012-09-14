Ext.define("PartsDB.view.PartDetail", {
	extend: 'Ext.tab.Panel',
	alias: "widget.part-detail",
	config: {
		tabBarPosition: 'bottom',

		items: [
			{
				xtype: "toolbar",
				docked: "top",
				items: [
					{
						xtype: "button",
						text: "Back",
						ui: "back",
						id:"back-family-list"
					},
					{
						xtype: "spacer"
					}
				]
			}
		]
	}
});
