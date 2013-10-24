Ext.define("Parts.controller.PartList", {
	extend: "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"partlist": {
				"select": function(row, record) {
					var data = record.data;
					
					var toolbar = row.view.up('partlist').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(data == null);
					
					var attrlist = row.view.up('viewport').down('attributelist');
					attrlist.getStore().load({
						"url": "catalog/parts/" + data.id + "/attributes"
					});
					
					attrlist.down('toolbar').down('button[itemId=add]').setDisabled(record.data);
				}
			},
			"partlist toolbar button[itemId=add]": {
				"click": function(button) {
					var tree = button.up('viewport').down('categorytree');
					var selected = tree.getSelectionModel().getSelection()[0];
					var list = button.up('partlist');
					
					Ext.Ajax.request({
						"url": "categories/" + selected.data.id + "/parts",
						"method": "POST",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								list.getStore().add(object.part);
							}
						}
					});
				}
			},
			"partlist toolbar button[itemId=remove]": {
				"click": function(button) {
					var list = button.up('partlist');
					var selected = list.getSelectionModel().getSelection()[0];
					
					Ext.Ajax.request({
						"url": "categories/" + encodeURIComponent(selected.data.category) + "/parts/" + encodeURIComponent(selected.data.id),
						"method": "DELETE",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								list.getStore().remove(selected);
							}
						}
					});
				}
			}
		});
	}
});
