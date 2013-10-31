Ext.define("Parts.view.PartList", {
	"extend": "Ext.dataview.List",
	
	"alias": "widget.partlist",
	"config": {
		"fullscreen": true,
		"store": "PartList",
		"grouped": true,
		"itemTpl": "<div>{number}<br/><span style='font-size:.6em'>{description}<span></div>",
		"items": [
			{
				"xtype": "toolbar",
				"docked": "top",
				"items": [
					{
						"xtype": "searchfield",
						"placeHolder": "Search",
						"itemId": "search"
					}
				]
			}
		]
	}
});
