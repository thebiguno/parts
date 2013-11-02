Ext.define("Parts.controller.Login", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"login button[itemId=back]": {
				"click": function(button) {
					button.up('panel').getLayout().prev();
				}
			},
			"login form[itemId=authenticate] button[itemId=authenticate]": {
				"click": function(button) {
					var form = button.up('form').getForm();
					if (form.isValid() == false) return;
					form.submit({
						"url": "index",
						"method": "POST",
						"success": function(response) {
							window.location.reload();
						},
						"failure": function(response) {
							var key = response.getResponseHeader('WWW-Authenticate').split(' ')[1];
							if (key) {
								var card = button.up('panel').getLayout().next();
								card.down('hiddenfield[name=identifier]').setValue(key);
							} else {
								button.up('form').down('label[itemId=message]').setText('Authentication Failed');
							}
						}
					});
				}
			},
			"login form[itemId=enrole] button[itemId=enrole]": {
				"click": function(button) {
					
				}
			}
		});
	}
});
