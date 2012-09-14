Ext.define("mobile.controller.PartDetail", {
	extend: "Ext.app.Controller",
	config: {
		refs: {
			partDetail: "part-detail",
			familyList: "family-list",
			familyListBack: "#back-family-list"
		},
		control: {
			familyListBack: {
				tap: "backToFamilyList"
			}
		}
	},
	backToFamilyList: function(){
		var familyList = this.getFamilyList();
		Ext.Viewport.animateActiveItem(familyList, {type: 'slide', direction: 'right'});
	}
});
