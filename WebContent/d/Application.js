window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"views": ["CategoryTree","PartList","AttributeList"],
	"stores": ["CategoryTree", "PartList"],
	"controllers": ["Toolbar", "CategoryTree", "PartList"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});
