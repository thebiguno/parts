window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "d",
	
	"viewport": {
		"autoMaximize": true
	},

	"views": ["CategoryTree","PartList","AttributeList"],
	"stores": ["CategoryTree", "PartList", "AttributeList"],
	"controllers": ["Toolbar", "CategoryTree", "PartList", "AttributeList"],

	"launch": function() {
		Parts.app = this;
		Ext.create( "Parts.view.Viewport");
	}
});
