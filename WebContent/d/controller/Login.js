Ext.define("Parts.controller.Login", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"login button[itemId=back]": {
				"click": function(button) {
					button.up('form').up('panel').getLayout().prev();
				}
			},
			"login button[itemId=authenticate]": {
				"click": function(button) {
					var form = button.up('form').getForm();
					if (form.isValid() == false) return;
					form.submit({
						"url": "index",
						"params": { "action": "login" },
						"success": function() {
							window.location.reload();
						},
						"failure": function(form, action) {
							var key = action.result.key;
							if (key) {
								var card = button.up('form').up('panel').getLayout().next();
								card.down('hiddenfield[name=identifier]').setValue(key);
							} else {
								button.up('form').down('label[itemId=message]').setText(action.result.msg);
							}
						}
					});
				}
			},
			"login button[itemId=enrole]": {
				"click": function(button) {
					var form = button.up('form').getForm();
					if (form.isValid() == false) return;
					form.submit({
						"url": "index",
						"params": { "action": "enrole" },
						"success": function() {
							button.up('form').up('panel').getLayout().next();
						},
						"failure": function(form, action) {
							button.up('form').down('label[itemId=message]').setText(action.result.msg);
						}
					});
				}
			},
			"login button[itemId=reset]": {
				"click": function(button) {
					var form = button.up('form').getForm();
					if (form.isValid() == false) return;
					form.submit({
						"url": "index",
						"params": { "action": "reset" },
						"success": function() {
							button.up('form').up('panel').getLayout().next();
						},
						"failure": function(form, action) {
							button.up('form').down('label[itemId=message]').setText(action.result.msg);
						}
					});
				}
			},
			"login button[itemId=activate]": {
				"click": function(button) {
					var form = button.up('form').getForm();
					if (form.isValid() == false) return;
					form.submit({
						"url": "index",
						"params": { "action": "activate" },
						"success": function() {
							window.location.reload();
						},
						"failure": function(form, action) {
							button.up('form').down('label[itemId=message]').setText(action.result.msg);
						}
					});
				}
			},
		});
	}
});
