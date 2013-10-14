Ext.define('Parts.view.CatalogTree', {
	"extend": "Ext.tree.Panel",
	"alias": "widget.catalogtree",
	
	"rootVisible": true,
	"store": "CatalogTree",
	
	"columns": [
		{
			"xtype": "treecolumn",
			"text": "Name",
			"flex": 1,
			"sortable": false,
			"dataIndex": "name"
		}
	],

	"initComponent": function() {
		this.callParent(arguments);
		
		var me = this;
		this.getStore().addListener("load", function() {
			//me.getSelectionModel().selectPath('/' + me.getRootNode().getId());
		});
	}
});