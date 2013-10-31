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
		Ext.create('Ext.container.Viewport', {
			"layout": "fit",
			"items": [
				Ext.create( "Parts.view.Login")
			]
		});
	}
});
