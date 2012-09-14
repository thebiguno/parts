Ext.define("mobile.controller.CatalogList", {
	extend: "Ext.app.Controller",
	config: {
		refs: {
			catalogList: "catalog-list",
			familyList: "family-list"
		},
		control: {
			catalogList: {
				itemtap: "activateFamilyList"
			}
		}
	},
	activateFamilyList: function (list, index, target, record) {
		var familyList = this.getFamilyList();
		if (familyList == null){
			familyList = Ext.create("mobile.view.FamilyList", {});
		}
		familyList.getStore().getProxy().setUrl("../m/parts/" + record.data.category + "/" + record.data.family);
		familyList.getStore().load({
			callback: function(){
				Ext.Viewport.animateActiveItem(familyList, {type: 'slide', direction: 'left'});
			}
		});
	}
});
