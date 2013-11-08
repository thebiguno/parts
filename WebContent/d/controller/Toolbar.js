Ext.define("Parts.controller.Toolbar", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"toolbar button[itemId=searchbutton]": { "click": this.search },
			"toolbar textfield[itemId=searchterms]": { "keypress": this.search },
			"categorytree": { "select": this.search },
			
			"toolbar button[itemId=importbutton]": { 
				"click": function(button) {
					Ext.widget({
						"xtype": "importdialog"
					}).show(this);
				}
			},
			"toolbar button[itemId=requiredbutton]": {
				"toggle": this.search
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
						"url": button.up('toolbar').down('textfield[itemId=digikeyurl]').getValue(),
						"min": button.up('toolbar').down('textfield[itemId=digikeymin]').getValue() || 0,
						"qty": button.up('toolbar').down('textfield[itemId=digikeyqty]').getValue() || 0
					};
					Ext.Ajax.request({
						"url": "import/digikey",
						"jsonData": object,
						"success": function() {
							this.search();
						}
					});
				}
			},
			
			"toolbar button[itemId=logoutbutton]": {
				"click": function(button) {
					Ext.Ajax.request({
						"url": "index",
						"method": "DELETE",
						"success": function() {
							window.location.reload();
						}
					});
				}
			}
		});
	},
	
	"search": function(cmp, e) {
		if (e.getKey == null || e.getKey() == 0 || e.getKey() == e.ENTER) {

			var viewport = cmp.view ? cmp.view.up('viewport') : cmp.up('viewport');
			var terms = viewport.down('textfield[itemId=searchterms]').getValue();
			var required = viewport.down('button[itemId=requiredbutton]').pressed;
			var tree = viewport.down('categorytree');
			var partlist = viewport.down('partlist');
			
			if (cmp.view == null) {
				tree.getStore().load({
					"params": {
						"q": terms,
						"required": required
					}
				});
			}

			var selected = tree.getSelectionModel().getSelection()[0];
			var category = selected ? selected.data.id : 0;
			partlist.getStore().load({
				"url": "categories/" + category + "/parts",
				"params": {
					"q": terms,
					"required": required
				}
			});
		}
	}
	
});
