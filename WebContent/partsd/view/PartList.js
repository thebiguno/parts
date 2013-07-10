Ext.define('Parts.view.PartList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.partlist",
	
	"rootVisible": false,
	"store": "PartList",
	
	"columns": [
		{
			"text": "Part #",
			"width": 250,
			"dataIndex": "part",
			"sortable": false
		},
		{
			"text": "Description",
			"flex": 1,
			"dataIndex": "description",
			"sortable": false
		},
		{
			"text": "Quantity",
			"width": 80,
			"dataIndex": "quantity",
			"sortable": false
		},
		{
			"text": "Datasheets",
			"width": 250,
			"dataIndex": "datasheets",
			"sortable": false
		},
		{
			"width": 100
			// TODO actions
		}
	]
});