Ext.define('Parts.view.AttachmentDialog', {
	"extend": "Ext.window.Window",
	"alias": "widget.attachmentdialog",
	"title": "Attachment",
	"height": 200,
	"width": 400,
	"layout": "fit",
	"items": {
		"xtype": "form",
		"layout": { "type": "vbox", "align": "stretch" },
		"border": false,
		"bodyPadding": 10,
		"fieldDefaults": {
			"labelAlign": "top",
			"labelWidth": 100
		},
		"items": [
			{
				"xtype": "textfield",
				"fieldLabel": "URL",
				"maxLength": 1024,
				"vtype": "url",
				"name": "url"
			},
			{
				"xtype": "filefield",
				"fieldLabel": "File",
				"name": "file"
			}
		]
	},
	"buttons": [
		{
			"text": "Cancel",
			"handler": function(button) {
				button.up('window').close();
			}
		},
		{
			"text": "OK",
			"itemId": "okbutton"
		},
	]
});