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
					"text": "Add"
				},
				{
					"xtype": "button",
					"itemId": "remove",
					"icon": "img/minus-button.png",
					"text": "Remove"
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
			"dataIndex": "part",
			"sortable": false
		},
		{
			"text": "Description",
			"flex": 1,
			"dataIndex": "description",
			"sortable": false
		},
		{
			"text": "Available",
			"width": 80,
			"dataIndex": "available",
			"sortable": false
		},
		{
			"text": "Minimum",
			"width": 80,
			"dataIndex": "minimum",
			"sortable": false
		},
		{
			"text": "Datasheets",
			"width": 100,
			"dataIndex": "datasheets",
			"sortable": false
		}
	]
});