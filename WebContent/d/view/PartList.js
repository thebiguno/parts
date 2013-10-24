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
		},
		{
			"xtype": "panel",
			"dock": "bottom",
			"title": "Notes",
			"height": 200
		}
	],
	"columns": [
		{
			"text": "Part #",
			"width": 250,
			"dataIndex": "number",
			"sortable": false,
			"editor": {
				"xtype": "textfield"
			}
		},
		{
			"text": "Description",
			"flex": 1,
			"dataIndex": "description",
			"sortable": false,
			"editor": {
				"xtype": "textfield"
			}
		},
		{
			"text": "Available",
			"width": 80,
			"dataIndex": "available",
			"sortable": false,
			"editor": {
				"xtype": "numberfield"
			}
		},
		{
			"text": "Minimum",
			"width": 80,
			"dataIndex": "minimum",
			"sortable": false,
			"editor": {
				"xtype": "numberfield"
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