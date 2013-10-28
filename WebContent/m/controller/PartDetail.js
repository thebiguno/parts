Ext.define("Parts.controller.PartDetail", {
	"extend": "Ext.app.Controller",
	"config": {
		"refs": {
		},
		"control": {
			"partdetail button[itemId=back]": {
				"tap": function(button) {
					var partlist = button.up('viewport').down('partlist');
					Ext.Viewport.animateActiveItem(partlist, {"type": 'slide', "direction": 'right'});
				}
			},
			"partdetail field": {
				"change": function(field) {
					var record = field.up('formpanel').getValues();
					Ext.Ajax.request({
						"url": "categories/" + record.data.category + "/parts/" + record.data.id,
						"method": "PUT",
						"jsonData": record.data,
						"success": function(response) {
							record.commit();
						},
						"failure": function(response) {
							record.reject();
						}
					});
				}
			},
		}
	}
});
