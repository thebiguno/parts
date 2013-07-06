window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "partsd",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CatalogTree"],
	"views": ["CatalogTree","PartList"],
	//"controllers": ["CatalogTree", "PartList"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});
