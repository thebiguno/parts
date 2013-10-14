window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "m",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CatalogList", "FamilyList"],
	"views": ["CatalogList"],
	"controllers": ["CatalogList", "FamilyList", "PartDetail"],

	"launch": function() {
		Parts.app = this;
		Ext.Viewport.add(Ext.create('Parts.view.CatalogList'));
	}
});
