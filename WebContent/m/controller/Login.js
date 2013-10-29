Ext.define("Parts.controller.Login", {
	"extend": "Ext.app.Controller",
	"config": {
		"control": {
			"login button[itemId=authenticate]": {
				"tap": function(button) {
					var login = button.up('viewport').down('login');
					login.down('label[itemId=authenticationFailed]').hide(true);

					Ext.Ajax.request({
						"url": "index",
						"method": "POST",
						"jsonData": {
							"identifier": login.down('textfield[name=identifier]').getValue(),
							"secret": login.down('textfield[name=secret]').getValue()
						},
						"success": function(response) {
							window.location.reload();
						},
						"failure": function(response) {
							login.down('label[itemId=authenticationFailed]').show(true);
						}
					});
				}
			}
		}
	}
});
