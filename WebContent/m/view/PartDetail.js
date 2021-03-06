Ext.define("Parts.view.PartDetail", {
	"extend": "Ext.form.Panel",
	"alias": "widget.partdetail",
	"config": {
		"scrollable": "vertical",

		"items": [
			{
				"xtype": "toolbar",
				"docked": "top",
				"items": [
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
				"title": "Attributes",
				"items": [
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
						"stepValue": 1
					},
					{
						"xtype": "spinnerfield",
						"name": "minimum",
						"label": "Minimum",
						"minValue": 0,
						"stepValue": 1
					},
					{
						"xtype": "textfield",
						"name": "description",
						"label": "Desc"
					},
					{
						"xtype": "textfield",
						"name": "notes",
						"label": "Notes"
					}
				]
			},
			{
				"xtype": "fieldset",
				"title": "Additional Attributes",
				"itemId": "extended",
				"items": []
			}
		]
	},
	"setPart": function(record){
		this.down('button[itemId=back]').setText(record.data.group);
		this.setRecord(record);
		
		var fieldset = this.down('fieldset[itemId=extended]');
		fieldset.removeAll(true);
		Ext.Ajax.request({
			"url": "categories/" + record.data.category + "/parts/" + record.data.id + "/attributes",
			"success": function(response) {
				var object = Ext.decode(response.responseText);
				if (object.success) {
					Ext.each(object.data, function(attr) {
						fieldset.add(Ext.create('widget.textfield',{
							"readOnly": true,
							"name": attr.id,
							"label": attr.name,
							"value": attr.value
						}));
					});
				}
			}
		});
	}
});
