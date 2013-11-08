Ext.define("Parts.controller.CategoryTree", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"categorytree": { 
				"select": function(row, record) {
					var data = record.data;

					var toolbar = row.view.up('categorytree').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(data.id == 'root');
					toolbar.down('button[itemId=add]').enable();

					partlist.down('toolbar').down('button[itemId=add]').setDisabled(record.data.id == 0);
				},
				"edit": function(editor, evt) {
					Ext.Ajax.request({
						"url": "categories/" + evt.record.data.id,
						"method": "PUT",
						"jsonData": {
							"name": evt.record.data.name,
						},
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
			},
			"categorytree > treeview": {
				"beforedrop": function(node, source, target) {
					var data = source.records[0].data;
					if (data.category) {
						data.category = target.data.id;
						Ext.Ajax.request({
							"url": "categories/" + data.category + '/parts/' + data.id,
							"method": "PUT",
							"jsonData": data,
							"success": function(response) {
								source.view.up('grid').getStore().reload();
							},
							"failure": function(response) {
							}
						});
						return false;
					} else {
						Ext.Ajax.request({
							"url": "categories/" + data.id,
							"method": "PUT",
							"jsonData": {
								"parent": target.data.id
							},
							"success": function(response) {
								target.appendChild(source.records[0]);
							},
							"failure": function(response) {
							}
						});
						return false;
					}
				}
			},
			"categorytree toolbar button[itemId=add]": {
				"click": function(button) {
					var tree = button.up('categorytree');
					var selected = tree.getSelectionModel().getSelection()[0];

					Ext.Ajax.request({
						"url": "categories" + (selected.data.id == 'root' ? '' : '/' + selected.data.id), 
						"method": "POST",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
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
						"url": "categories/" + selected.data.id,
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
