Ext.define('Parts.view.AttributeList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.attributelist",
	
	//"store": "AttributeList",
	
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
			"sortable": false
		},
		{
			"text": "Value",
			"flex": 1,
			"dataIndex": "value",
			"sortable": false
		}
	]
});