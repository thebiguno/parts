Ext.define("mobile.view.PartDetail", {
	extend: 'Ext.form.Panel',
	requires: ["Ext.form.FieldSet", "mobile.view.components.LabelField"],
	alias: "widget.part-detail",
	config: {
		scrollable:'vertical',

		items: [
			{
				xtype: "toolbar",
				docked: "top",
				items: [
					{
						xtype: "button",
						text: "Family",
						ui: "back",
						id:"back-family-list"
					},
					{
						xtype: "spacer"
					}
				]
			},
			{
				xtype: "fieldset",
				items: [
					{
						xtype: "labelfield",
						name: "part",
						label: "Part #"
					},
					{
						xtype: "labelfield",
						name: "description",
						label: "Desc"
					},
					{
						xtype: "labelfield",
						name: "notes",
						label: "Notes"
					},
					{
						xtype: "spinnerfield",
						name: "quantity",
						id: "quantity-spinner",
						label: "Qty",
						minValue: 0,
						increment: 1
					}
				]
			},
			{
				xtype: "fieldset",
				items: [
					{
						xtype: "labelfield",
						name: "datasheets",
						label: "Docs"
					}
				]
			}
		]
	},
	setPart: function(record){
		this.setRecord(record);
	}
});
