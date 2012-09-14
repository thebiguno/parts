Ext.define("PartsDB.controller.FamilyList", {
	extend: "Ext.app.Controller",
	config: {
		refs: {
			catalogList: "catalog-list",
			familyList: "family-list",
			partDetail: "part-detail",
			catalogListBack: "#back-catalog-list"
		},
		control: {
			familyList: {
				itemtap: "activatePartDetail"
			},
			catalogListBack: {
				tap: "backToCatalogList"
			}
		}
	},
	activatePartDetail: function(list, index, target, record){
		var partDetail = this.getPartDetail();
		if (partDetail == null){
			partDetail = Ext.create("PartsDB.view.PartDetail", {});
		}
		//partDetail.getStore().load({});
		Ext.Viewport.animateActiveItem(partDetail, {type: 'slide', direction: 'left'});
	},
	backToCatalogList: function(){
		var catalogList = this.getCatalogList();
		Ext.Viewport.animateActiveItem(catalogList, {type: 'slide', direction: 'right'});
	}
});
