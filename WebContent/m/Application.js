window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "m",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["CatalogList"],
	"views": ["CatalogList"],
	"controllers": ["CatalogList", "PartDetail"],

	"launch": function() {
		Parts.app = this;
		Ext.Viewport.add(Ext.create('Parts.view.CatalogList'));
	}
});
