window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CatalogTree", "PartList"],
	"views": ["CatalogTree","PartList"],
	"controllers": ["Toolbar", "CatalogTree"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});