Ext.define('Parts.view.Login', {
	"extend": "Ext.form.Panel",
	"alias": "widget.login",

	"title": "Parts",
	"margin": "300%",
	"minWidth": 250,
	"minHeight": 150,
	"maxHeight": 150,
	"style": "text-align: center",

	"items": [
		{
			"margin": 10,
			"xtype": "label",
			"anchor": "100%",
			"html": "Authentication failed.",
			"itemId": "authenticationFailed",
			"hidden": true,
			"hideAnimation": 'fadeOut',
			"showAnimation": 'fadeIn',
			"style": 'color:#990000'
		},
		{
			"margin": 10,
			"xtype": "textfield",
			"anchor": "100%",
			"name": "identifier",
			"emptyText": "Identifier"
		},
		{
			"margin": 10,
			"xtype": "textfield",
			"anchor": "100%",
			"name": "secret",
			"inputType": "password",
			"emptyText": "Password"
		},
		{
			"xtype": "container",
 			"margin": 10,
			"items": [
				{
					"xtype": "button",
					"text": "Authenticate",
					"itemId": "authenticate"
				}
			]
		}
	]
});
