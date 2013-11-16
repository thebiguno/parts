Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"views": ["Login"],
	"controllers": ["Login"],

	"launch": function() {
		Parts.app = this;
		var dialog = Ext.create("Parts.view.Login");
		Ext.create('Ext.container.Viewport', {
			"layout": "fit",
			"items": [
				dialog
			]
		});
		dialog.center();
	}
});
