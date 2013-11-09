Ext.define("Parts.controller.Login", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"login button[itemId=back]": {
				"click": function(button) {
					button.up('form').up('panel').getLayout().prev();
				}
			},
			"login button[itemId=authenticate]": { "click": this.authenticate },
			"login form[itemId=authenticate] textfield": { "keypress": this.authenticate },
			"login button[itemId=enrole]": { "click": this.enrole },
			"login form[itemId=enrole] textfield": { "keypress": this.enrole },
			"login button[itemId=reset]": { "click": this.reset },
			"login form[itemId=reset] textfield": { "keypress": this.enrole },
			"login button[itemId=activate]": { "click": this.activate },
			"login form[itemId=activate] textfield": { "keypress": this.activate },
			
			"login textfield[name=secret]": {
				"change": function(field) {
					var password = field.getValue();
					var length = password.length;
					var factor = 0;

					if (password.match(/.*[a-z].*/)) factor += 2.6;
					if (password.match(/.*[A-Z].*/)) factor += 2.6;
					if (password.match(/.*[0-9].*/)) factor += 1.0;
					if (password.match(/.*[ ].*/)) factor += 0.1;
					if (password.match(/.*[!@#$%^&*()].*/)) factor += 1.0;
					if (password.match(/.*[^ a-zA-Z0-9!@#$%^&*()].*/)) factor += 2.2;
					
					var combinations = Math.pow(factor*10.0,length);
					var ms = combinations / 4000000;
					var seconds = ms / 1000; 
					var minutes = seconds / 60;
					var hours = minutes / 60;
					var days = hours / 24;
					var years = days / 365;
					var thousand = years / 1000;
					var duration = '';
					if (thousand > 1000) duration = 'more than 1,000 years';
					else if (thousand > 1) duration = Math.round(thousand) + ' thousand years';
					else if (years > 1) duration = Math.round(years) + ' years';
					else if (days > 1) duration = Math.round(days) + ' days';
					else if (hours > 1) duration = Math.round(hours) + ' hours';
					else if (minutes > 1) duration = Math.round(minutes) + ' minutes';
					else if (seconds > 1) duration = Math.round(seconds) + ' seconds'; 
					else duration = Math.round(ms) + ' milliseconds';
					field.up('form').down('label[itemId=message]').setText(duration + ' to crack');
				}
			}
		});
	},

	"authenticate": function(cmp, e) {
		if (e.getKey() == 0 || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
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
						cmp.up('form').down('label[itemId=message]').setText(action.result.msg);
					}
				}
			});
		}
	},
	
	"enrole": function(cmp, e) {
		if (e.getKey() == 0 || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "enrole" },
				"success": function() {
					cmp.up('form').up('panel').getLayout().next();
				},
				"failure": function(form, action) {
					cmp.up('form').down('label[itemId=message]').setText(action.result.msg);
				}
			});
		}
	},
	
	"reset": function(cmp, e) {
		if (e.getKey() == 0 || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "reset" },
				"success": function() {
					cmp.up('form').up('panel').getLayout().next();
				},
				"failure": function(form, action) {
					cmp.up('form').down('label[itemId=message]').setText(action.result.msg);
				}
			});
		}
	},
	
	"activate": function(cmp, e) {
		if (e.getKey() == 0 || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "activate" },
				"success": function() {
					window.location.reload();
				},
				"failure": function(form, action) {
					cmp.up('form').down('label[itemId=message]').setText(action.result.msg);
				}
			});
		}
	}
});
