Ext.define('Parts.view.AttributeList', {
	"extend": "Ext.grid.Panel",
	"alias": "widget.attributelist",
	
	"store": "AttributeList",
	
	"dockedItems": [
		{
			"xtype": "toolbar",
			"items": [
				{
					"xtype": "button",
					"itemId": "add",
					"icon": "img/plus-button.png",
					"text": "Add",
					"disabled": true
				},
				{
					"xtype": "button",
					"itemId": "remove",
					"icon": "img/minus-button.png",
					"text": "Remove",
					"disabled": true
				},
				{
					"xtype": "button",
					"itemId": "upload",
					"icon": "img/upload-cloud.png",
					"text": "Upload",
					"disabled": true
				}
			]
		},
	],
	
	"columns": [
		{
			"text": "Name",
			"flex": 1,
			"dataIndex": "name",
			"sortable": false,
			"editor": {
				"xtype": "textfield",
				"maxLength": 255
			}
		},
		{
			"text": "Value",
			"flex": 1,
			"dataIndex": "value",
			"sortable": false,
			"editor": {
				"xtype": "trigger",
				"triggerCls": Ext.baseCSSPrefix + 'form-search-trigger',
				"maxLength": 255,
				"onTriggerClick": function(evt) {
					Ext.widget({
						"xtype": "attachmentdialog",
						"record": this.up('grid').getSelectionModel().getSelection()[0].data
					}).show(this);
				}
			}
		},
		{
			"text": "",
			"width": 25,
			"sortable": false,
			"renderer": function(value, md, record) {
				if (record.data.href) {
					return '<a target="_blank" href="' + record.data.href + '"><img src="' + record.data.icon + '"/></a>';
				} else {
					return '';
				}
			}
		}
	],
	"plugins": [
		{ "ptype": "cellediting", "clicksToEdit": 2 }
	]
});