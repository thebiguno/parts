window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "partsm",
	
	"viewport": {
		autoMaximize: true
	},

	stores: ["CatalogList", "FamilyList"],
	views: ["CatalogList"],
	controllers: ["CatalogList", "FamilyList", "PartDetail"],

	launch: function() {
		Ext.Viewport.add(Ext.create('Parts.view.CatalogList'));
		
		Parts.app = this;
	}
});
