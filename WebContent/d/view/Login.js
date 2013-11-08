Ext.define('Parts.view.Login', {
	"extend": "Ext.tab.Panel",
	"alias": "widget.login",

	"title": "Parts",
	"margin": "300%",
	"minWidth": 400,
	"maxWidth": 400,
	"minHeight": 200,
	"maxHeight": 200,
	"style": "text-align: center",

	"tabPosition": "bottom",
	"items": [
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "Authenticate",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "authenticate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "Identifier", "name": "identifier" },
						{ "fieldLabel": "Password", "inputType": "password", "name": "secret" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "Authenticate", "itemId": "authenticate" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "inputType": "password", "enableKeyEvents": true },
					"items": [
						{ "xtype": "hiddenfield", "name": "identifier", "value": "" },
						{ "fieldLabel": "New Password", "name": "secret" },
						{ "fieldLabel": "Verify Password", "name": "verify" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "< Back", "itemId": "back" },
						{ "text": "Change Password", "itemId": "activate" }
					]
				}
			]
		},
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "Enrole",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "enrole",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "Identifier", "name": "identifier" },
						{ "fieldLabel": "Email", "name": "email", "vtype": "email" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "Enrole", "itemId": "enrole" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "Activation Key", "name": "identifier" },
						{ "fieldLabel": "New Password", "name": "secret", "inputType": "password" },
						{ "fieldLabel": "Verify Password", "name": "verify", "inputType": "password" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "< Back", "itemId": "back" },
						{ "text": "Activate", "itemId": "activate" }
					]
				}
			]
		},
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "Forgot Password",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "enrole",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "Identifier or Email", "name": "identifier" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "Reset", "itemId": "reset" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "Activation Key", "name": "identifier" },
						{ "fieldLabel": "New Password", "name": "secret", "inputType": "password" },
						{ "fieldLabel": "Password", "name": "verify", "inputType": "password" },
						{ "xtype": "label", "itemId": "message" }
					],
					"buttons": [
						{ "text": "< Back", "itemId": "back" },
						{ "text": "Activate", "itemId": "activate" }
					]
				}
			]
		}
	]
});
