Ext.define('Parts.view.CatalogTree', {
	"extend": "Ext.tree.Panel",
	"alias": "widget.catalogtree",
	
	"rootVisible": false,
	"store": "CatalogTree",
	
	"columns": [
		{
			"xtype": "treecolumn",
			"text": "Name",
			"flex": 1,
			"sortable": false,
			"dataIndex": "name"
		}
	]
});