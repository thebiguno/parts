Ext.define("Parts.view.Login", {
	"extend": 'Ext.form.Panel',
	"alias": "widget.login",
	"config": {
		"scrollable": "vertical",

		"items": [
			{
				"xtype": "fieldset",
				"title": "Parts",
				"items": [
					{ "xtype": "textfield", "name": "identifier", "placeHolder": "Identifier" },
					{ "xtype": "passwordfield", "name": "secret", "placeHolder": "Password" },
					{ "xtype": "checkboxfield", "name": "remember", "label": "Remember Me" }
				]
			},
			{ "xtype": "label", "itemId": "message", "margin": 10, "html": "&nbsp;" },
			{
				"xtype": "button",
				"ui": "action",
				"text": "Authenticate",
				"itemId": "authenticate",
				"margin": "10"
			}
		]
	}
});
