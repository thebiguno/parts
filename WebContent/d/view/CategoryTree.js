Ext.define('Parts.view.CategoryTree', {
	"extend": "Ext.tree.Panel",
	"alias": "widget.categorytree",
	
	"rootVisible": true,
	"store": "CategoryTree",
	
//	"viewConfig": {
//		"plugins": { "ptype": "Ext.tree.plugin.TreeViewDragDrop" }
//	},
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
					"text": "Remove",
					"disabled": true
				}
			]
		}
	],
	"columns": [
		{
			"xtype": "treecolumn",
			"text": "Name",
			"flex": 1,
			"sortable": false,
			"dataIndex": "name",
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			}
		}
	],
	"plugins": [
		{ "ptype": "cellediting", "clicksToEdit": 2 }
	]
});