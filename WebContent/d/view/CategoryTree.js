Ext.define('Parts.view.CategoryTree', {
	"extend": "Ext.tree.Panel",
	"alias": "widget.categorytree",
	
	"rootVisible": true,
	"store": "CategoryTree",
	
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
				"xtype": "textfield"
			}
		}
	],

	"initComponent": function() {
		this.callParent(arguments);

		this.plugins = [
			Ext.create("Ext.grid.plugin.CellEditing", {
				"clicksToEdit": 2,
				"listeners": {
					"edit": function(editor, evt) {
						Ext.Ajax.request({
							"url": "catalog/categories/" + encodeURIComponent(evt.record.data.id),
							"method": "PUT",
							"params": evt.record.data.name,
							"success": function(response) {
								evt.record.commit();
							},
							"failure": function(response) {
								evt.record.reject();
							}
						});
					},
					"beforeedit": function(editor, evt) {
						// don't allow the root node to be edited
						return evt.record.data.id != null;
					}
				}
			})
		];
	}
});