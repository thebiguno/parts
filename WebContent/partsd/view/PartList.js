Ext.define('Parts.view.PartList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.partlist",
	
	"rootVisible": false,
	//"store": "PartList",
	
	"columns": [
		{
			"text": "Name",
			"flex": 1,
			"dataIndex": "name",
			"sortable": false
		}
	]
});