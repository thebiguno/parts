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

					var viewport = row.view.up('viewport');
					var terms = viewport.down('textfield[itemId=searchterms]').getValue();
					var partlist = viewport.down('partlist');
					partlist.getStore().load({
						"url": "categories/" + data.id + "/parts",
						"params": {
							"q": terms
						}
					});
					
					partlist.down('toolbar').down('button[itemId=add]').setDisabled(record.data.id == 0);
				},
				"edit": function(editor, evt) {
					Ext.Ajax.request({
						"url": "categories/" + evt.record.data.id,
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
