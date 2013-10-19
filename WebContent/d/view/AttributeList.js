Ext.define('Parts.view.AttributeList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.attributelist",
	
	//"store": "AttributeList",
	
	"columns": [
		{
			"text": "Name",
			"flex": 1,
			"dataIndex": "name",
			"sortable": false
		},
		{
			"text": "Value",
			"flex": 1,
			"dataIndex": "value",
			"sortable": false
		}
	]
});