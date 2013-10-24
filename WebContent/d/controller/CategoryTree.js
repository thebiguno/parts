Ext.define("Parts.controller.CategoryTree", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"categorytree": { 
				"select": function(row, record) {
					var toolbar = row.view.up('categorytree').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(!record.data.id);
					toolbar.down('button[itemId=add]').enable();
					
					var partlist = row.view.up('viewport').down('partlist');
					var data = record.data;
					partlist.getStore().load({
						"url": "catalog/parts",
						"params": {
							"category": data.id,
							"terms": "" // TODO
						},
						"callback": function(records, op, success) {
							console.log(records);
						}
					});
				}
			},
			"categorytree toolbar button[itemId=add]": {
				"click": function(button) {
					var tree = button.up('categorytree');
					var selected = tree.getSelectionModel().getSelection()[0];

					Ext.Ajax.request({
						"url": "catalog/categories",
						"method": "POST",
						"params": selected.data.id,
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								selected.leaf = false;
								selected.appendChild(object.node, false, true).parentNode.expand(true);
							}
						}
					});
				}
			},
			"categorytree toolbar button[itemId=remove]": {
				"click": function(button) {
					var tree = button.up('categorytree');
					var selected = tree.getSelectionModel().getSelection()[0];
					
					Ext.Ajax.request({
						"url": "catalog/categories/" + encodeURIComponent(selected.data.id),
						"method": "DELETE",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								// TODO in theory destroy should be tree but that causes the tree store to issue a post for some reason
								selected.remove(false);
							}
						}
					});
				}
			}
		});
	}
});
