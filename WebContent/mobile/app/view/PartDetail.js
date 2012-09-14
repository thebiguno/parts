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
						label: "Description"
					},
					{
						xtype: "labelfield",
						name: "notes",
						label: "Notes"
					}
				]
			},
			{
				xtype: "fieldset",
				items: [
					{
						xtype: "labelfield",
						name: "datasheets",
						label: "Datasheets"
					}
				]
			}
		]
	},
	setPart: function(record){
		this.setRecord(record);
		//this.down("labelfield").setValue(record.data.datasheets);
	}
});
