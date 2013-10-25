Ext.define('Parts.view.AttributeList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.attributelist",
	
	"store": "AttributeList",
	
	"dockedItems": [
		{
			"xtype": "toolbar",
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
				},
				{
					"xtype": "button",
					"itemId": "upload",
					"icon": "img/upload-cloud.png",
					"text": "Upload",
					"disabled": true
				}
			]
		},
	],
	
	"columns": [
		{
			"text": "Name",
			"flex": 1,
			"dataIndex": "name",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			}
		},
		{
			"text": "Value",
			"flex": 1,
			"dataIndex": "value",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			}
		},
		{
			"text": "",
			"width": 20,
			"sortable": false,
			"renderer": function(value, md, record) {
				if (record.href) {
					return '<a href="' + record.href + '"><img target="_blank" src="' + record.icon + '"/></a>';
				} else {
					return '';
				}
			}
		}
	],
	"plugins": [
		Ext.create('Ext.grid.plugin.CellEditing', {
			"clicksToEdit": 2,
			"listeners": {
				"edit": function(editor, evt) {
					Ext.Ajax.request({
						"url": "categories/" + evt.record.data.category + "/parts/" + evt.record.data.part + "/attributes/" + evt.record.data.id,
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