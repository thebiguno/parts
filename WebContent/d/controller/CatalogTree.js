Ext.define("Parts.controller.CatalogTree", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"catalogtree": { 
				"select": function(row, record) {
					var toolbar = row.view.up('catalogtree').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(!record.data.category);
					toolbar.down('button[itemId=add]').setDisabled(record.data.family);
					
					var partlist = row.view.up('viewport').down('partlist');
					var data = record.data;
					partlist.getStore().load({
						"url": 'catalog?category=' + encodeURIComponent(data.category) + '&family=' + encodeURIComponent(data.family),
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
					
					Ext.Ajax.request({
						"url": 'catalog?category=' + encodeURIComponent(selected.data.category),
						"method": "POST",
						"params": selected.data.category,
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								selected.appendChild(object.node).expand(true);
							}
						}
					});
				}
			},
			"catalogtree toolbar button[itemId=remove]": {
				"click": function(button) {
					var tree = button.up('catalogtree');
					var selected = tree.getSelectionModel().getSelection()[0];
					
					Ext.Ajax.request({
						"url": 'catalog?category=' + encodeURIComponent(selected.data.category) + '&family=' + encodeURIComponent(selected.data.family),
						"method": "DELETE",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								selected.remove(true);
							}
						}
					});
				}
			}
		});
	}
});
