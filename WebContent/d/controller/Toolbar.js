Ext.define("Parts.controller.Toolbar", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"toolbar button[itemId=searchbutton]": { "click": this.search },
			"toolbar textfield[itemId=searchterms]": { "keypress": this.search },
			
			"toolbar button[itemId=importbutton]": { 
				"click": function(button) {
					Ext.widget({
						"xtype": "importdialog"
					}).show(this);
				}
			},
			
			"importdialog button[itemId=okbutton]": {
				"click": function(button) {
					var win = button.up('window');
					var form = win.down('form').getForm();
					if (form.isValid()) {
						form.submit({
							"url": "import/csv",
							"waitMsg": "Importing",
							"success": function(fp, p) {
								this.search();
								win.close();
							},
							"failure": function(fp, p) {
								this.search();
								win.close();
							}
						});
					}
				}
			},
			
			"toolbar button[itemId=digikeyadd]": {
				"click": function(button) {
					var object = {
						"url": button.up('toolbar').down('digikeyurl').getValue(),
						"min": button.up('toolbar').down('digikeymin').getValue(),
						"qty": button.up('toolbar').down('digikeyqty').getValue()
					};
					Ext.Ajax.request({
						"url": "import/digikey",
						"jsonData": object,
						"success": function() {
							this.search();
						},
						"failure": function() {
							
						}
					});
				}
			}
		});
	},
	
	"search": function(cmp, e) {
		if (e.getKey() == 0 || e.getKey() == e.ENTER) {
			var terms = cmp.up('viewport').down('textfield[itemId=searchterms]').getValue();
			
			var tree = cmp.up('viewport').down('categorytree').getStore();
			tree.load({
				"params": {
					"q": terms
				}
			});
		}
	}
	
});
