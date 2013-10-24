window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CategoryTree", "PartList"],
	"views": ["CategoryTree","PartList"],
	"controllers": ["Toolbar", "CategoryTree"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});
