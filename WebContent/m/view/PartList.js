Ext.define("Parts.view.PartList", {
	"extend": "Ext.dataview.List",
	
	"alias": "widget.partlist",
	"config": {
		"fullscreen": true,
		"store": "PartList",
		"grouped": true,
		"itemTpl": "<div>{number}<span style='float: right'>{available} / {minimum}</span><br/><span style='font-size:.6em'>{description}<span></div>",
		"items": [
			{
				"xtype": "titlebar",
				"docked": "top",
				"items": [
					{
						"xtype": "searchfield",
						"placeHolder": "Search",
						"itemId": "search",
						"align": "left"
					},
					{
						"xtype": "button",
						"text": "Logout",
						"itemId": "logout",
						"align": "right"
					}
				]
			}
		]
	}
});
