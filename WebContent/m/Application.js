Ext.application({
	"name": "Parts",
	"appFolder": "m",
	
	"viewport": {
		"autoMaximize": true
	},
	
	"css": [
		{
			"path": "resources/css/cupertino.css",
			"platform": ["ios"],
			"theme": "cupertino"
		}
	],

	"stores": ["PartList"],
	"views": ["PartList", "PartDetail"],
	"controllers": ["PartList", "PartDetail"],

	"launch": function() {
		Parts.app = this;
		Ext.Viewport.add(Ext.create('Parts.view.PartList'));
	}
});
