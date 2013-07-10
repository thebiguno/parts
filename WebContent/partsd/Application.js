window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "partsd",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CatalogTree", "PartList"],
	"views": ["CatalogTree","PartList"],
	"controllers": ["CatalogTree"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});
