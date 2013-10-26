Ext.define('Parts.view.ImportDialog', {
	"extend": "Ext.window.Window",
	"alias": "widget.importdialog",
	"title": "Import",
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
			"text": "Import",
			"itemId": "okbutton"
		},
	]
});