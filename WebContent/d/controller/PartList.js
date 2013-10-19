Ext.define("Parts.controller.PartList", {
	extend: "Ext.app.Controller",
	
	"init": function() {
		this.control({
			"attributelist toolbar button[itemId=add]": {
				"click": function(button) {
					
				}
			},
			"attributelist toolbar button[itemId=remove]": {
				"click": function(button) {
					
				}
			}
		});
	}
	
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
