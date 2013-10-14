Ext.define("Parts.controller.PartDetail", {
	extend: "Ext.app.Controller",
//	config: {
//		refs: {
//			catalogList: "catalog-",
//			familyList: "family-list"
//		},
//		control: {
//			catalogList: {
//				itemtap: "activateFamilyList"
//			}
//		}
//	},
//	activateFamilyList: function (list, index, target, record) {
//		var familyList = this.getFamilyList();
//		if (familyList == null){
//			familyList = Ext.create("Parts.view.FamilyList", {});
//		}
//		familyList.getStore().getProxy().setUrl("datam/" + record.data.category + "/" + record.data.family);
//		familyList.getStore().load({
//			callback: function(){
//				Ext.Viewport.animateActiveItem(familyList, {type: 'slide', direction: 'left'});
//			}
//		});
//	}
});
