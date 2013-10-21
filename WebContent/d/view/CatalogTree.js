Ext.define('Parts.view.CatalogTree', {
	"extend": "Ext.tree.Panel",
	"alias": "widget.catalogtree",
	
	"rootVisible": true,
	"store": "CatalogTree",
	
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
				"clicksToEdit": 1,
				"listeners": {
					"edit": function(editor, evt) {
						alert(evt.record.data.name);
					},
					"beforeedit": function(editor, evt) {
						// don't allow the root node to be edited
						return evt.record.data.category != null;
					}
				}
			})
		];
		var me = this;
		this.getStore().addListener("load", function() {
			//me.getSelectionModel().selectPath('/' + me.getRootNode().getId());
		});
	}
});