Ext.define("Parts.controller.CatalogTree", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"catalogtree": { 
				"select": function(row, record) { 
					var toolbar = row.view.up('catalogtree').down('toolbar');
					toolbar.down('button[itemId=add]').enable();
					toolbar.down('button[itemId=remove]').enable();
					
					var partlist = row.view.up('viewport').down('partlist');
					var data = record.data;
					partlist.getStore().load({
						"url": 'data/' + encodeURIComponent(data.category) + '/' + encodeURIComponent(data.family),
						"callback": function(records, op, success) {
							console.log(records);
						}
					});
				}
			},
			"catalogtree toolbar button[itemId=add]": {
				"click": function(button) {
					var tree = button.up('catalogtree');
					var selected = tree.getSelectionModel().getSelection()[0];
					var node = tree.getRootNode().findChildBy(function(node) { 
						return node.data.category == selected.data.category && node.data.family == null; }
					) || tree.getRootNode();
					node.appendChild({
						"name": "Untitled",
						"expanded": true,
						"category": selected.data.category,
						"family": null
					});
				}
			},
			"catalogtree toolbar button[itemId=remove]": {
				"click": function(button) {
					
				}
			}
		});
	}
});
