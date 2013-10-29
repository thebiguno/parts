Ext.application({
	"name": "Parts",
	"appFolder": "m",
	
	"viewport": {
		"autoMaximize": true
	},

	"views": ["Login"],
	"controllers": ["Login"],

	"launch": function() {
		Parts.app = this;
		Ext.Viewport.add(Ext.create('Parts.view.Login'));
	}
});
