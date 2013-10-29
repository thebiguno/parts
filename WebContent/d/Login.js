window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"views": ["Login"],
	//"controllers": ["Login"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Login");
	}
});
