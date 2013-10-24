Ext.define("Parts.controller.PartList", {
	extend: "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"attributelist": {
				"select": function(row, record) {
					var data = record.data;
					
					var toolbar = row.view.up('partlist').down('toolbar');
					toolbar.down('button[itemId=remove]').setDisabled(data == null);
				}
			},
			"attributelist toolbar button[itemId=add]": {
				"click": function(button) {
					
				}
			},
			"attributelist toolbar button[itemId=remove]": {
				"click": function(button) {
					
				}
			}
		});
	}

});
