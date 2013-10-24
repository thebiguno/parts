Ext.define('Parts.view.PartList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.partlist",
	
	"rootVisible": false,
	"store": "PartList",
	
	"dockedItems": [
		{
			"xtype": "toolbar",
			"dock": "top",
			"items": [
				{
					"xtype": "button",
					"itemId": "add",
					"icon": "img/plus-button.png",
					"text": "Add",
					"disabled": true
				},
				{
					"xtype": "button",
					"itemId": "remove",
					"icon": "img/minus-button.png",
					"text": "Remove",
					"disabled": true
				}
			]
		}
	],
	"columns": [
		{
			"text": "Part #",
			"flex": 1,
			"dataIndex": "number",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			}
		},
		{
			"text": "Description",
			"flex": 2,
			"dataIndex": "description",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			},
			"renderer": function(value) {
				return '<div class="word-wrap">' + value + '</div>';
			}
		},
		{
			"text": "Notes",
			"flex": 2,
			"dataIndex": "notes",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 2048
			},
			"renderer": function(value) {
				return '<div class="word-wrap">' + value + '</div>';
			}
		},
		{
			"text": "Available",
			"width": 60,
			"dataIndex": "available",
			"sortable": false,
			"editor": {
				"xtype": "numberfield",
				"minValue": 0
			}
		},
		{
			"text": "Minimum",
			"width": 60,
			"dataIndex": "minimum",
			"sortable": false,
			"editor": {
				"xtype": "numberfield",
				"minValue": 0
			}
		}
	],
	"plugins": [
		Ext.create('Ext.grid.plugin.CellEditing', {
			"clicksToEdit": 2,
			"listeners": {
				"edit": function(editor, evt) {
					Ext.Ajax.request({
						"url": "categories/" + encodeURIComponent(evt.record.data.category) + "/parts/" + encodeURIComponent(evt.record.data.id),
						"method": "PUT",
						"jsonData": evt.record.data,
						"success": function(response) {
							evt.record.commit();
						},
						"failure": function(response) {
							evt.record.reject();
						}
					});
				}
			}
		})
	]
});