window.onbeforeunload = function(){return " ";};

Ext.application({
	"name": "Parts",
	"appFolder": "m",
	
	"viewport": {
		"autoMaximize": true
	},

	"stores": ["PartList"],
	"views": ["PartList", "PartDetail"],
	"controllers": ["PartList", "PartDetail", "Toolbar"],

	"launch": function() {
		Parts.app = this;
		Ext.Viewport.add(Ext.create('Parts.view.PartList'));
	}
});
