Ext.define("Parts.controller.AttributeList", {
	extend: "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"attributelist": {
				"select": function(row, record) {
					var data = record.data;
					
					var toolbar = row.view.up('attributelist').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(data == null);
				}
			},
			"attributelist toolbar button[itemId=add]": {
				"click": function(button) {
					var parts = button.up('viewport').down('partlist');
					var selected = parts.getSelectionModel().getSelection()[0];
					var list = button.up('attributelist');
					
					Ext.Ajax.request({
						"url": "categories/" + selected.data.category + "/parts/" + selected.data.id + "/attributes",
						"method": "POST",
						"success": function(response) {
							var object = Ext.decode(response.responseText);
							if (object.success) {
								list.getStore().add(object.attribute);
							}
						}
					});
				}
			},
			"attributelist toolbar button[itemId=remove]": {
				"click": function(button) {
					var list = button.up('attributelist');
					var selected = list.getSelectionModel().getSelection()[0];
					
					Ext.Ajax.request({
						"url": "categories/" + selected.data.category + "/parts/" + selected.data.part + "/attributes/" + selected.data.id,
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
