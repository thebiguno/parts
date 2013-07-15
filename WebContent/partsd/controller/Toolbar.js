Ext.define("Parts.controller.Toolbar", {
	"extend": "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"button[itemId=searchbutton]": { "click": this.search },
			"textfield[itemId=searchterms]": { "keypress": this.search }
		});
	},
	
	"search": function(cmp, e) {
		if (e.getKey() == null || e.getKey() == e.ENTER) {
			var terms = cmp.up('viewport').down('textfield[itemId=searchterms]').getValue();
			
			var catalog = cmp.up('viewport').down('catalogtree').getStore();
			catalog.load({
				"params": {
					"q": terms
				}
			});
		}
	}
});
