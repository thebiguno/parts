Ext.define("Parts.view.PartDetail", {
	extend: 'Ext.form.Panel',
	alias: "widget.partdetail",
	config: {
		scrollable:'vertical',

		items: [
			{
				xtype: "toolbar",
				docked: "top",
				items: [
					{
						"xtype": "button",
						"text": "Family",
						"ui": "back",
						"itemId":"back"
					},
					{
						"xtype": "spacer"
					}
				]
			},
			{
				"xtype": "fieldset",
				items: [
					{
						"xtype": "textfield",
						"name": "number",
						"label": "Part #"
					},
					{
						"xtype": "spinnerfield",
						"name": "available",
						"label": "Available",
						"minValue": 0,
						"increment": 1
					},
					{
						"xtype": "spinnerfield",
						"name": "minimum",
						"label": "Qty",
						"minValue": 0,
						"increment": 1
					},
					{
						xtype: "textfield",
						name: "description",
						label: "Desc"
					},
					{
						xtype: "textfield",
						name: "notes",
						label: "Notes"
					}
				]
			}
		]
	},
	setPart: function(record){
		this.down('button[itemId=back]').setText(record.data.group);
		this.setRecord(record);
	}
});
