Ext.define("Parts.controller.CatalogTree", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"catalogtree": { "select": function(row, record) { 
				var partlist = row.view.up('viewport').down('partlist');
				var data = record.data;
				partlist.getStore().load({
					"url": 'data/' + encodeURIComponent(data.category) + '/' + encodeURIComponent(data.family),
					"callback": function(records, op, success) {
						console.log(records);
					}
				});
			} }
		});
	}
});
