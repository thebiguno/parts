Ext.define("Parts.controller.PartDetail", {
	extend: "Ext.app.Controller",
	config: {
		refs: {
			partDetail: "part-detail",
			familyList: "family-list",
			familyListBack: "#back-family-list",
			quantitySpinner: "#quantity-spinner"
		},
		control: {
			familyListBack: {
				tap: "backToFamilyList"
			},
			quantitySpinner: {
				spin: "persistQuantity"
			}
		}
	},
	conn: new Ext.data.Connection({
		"autoAbort": true
	}),
	backToFamilyList: function(){
		var familyList = this.getFamilyList();
		Ext.Viewport.animateActiveItem(familyList, {type: 'slide', direction: 'right'});
	},
	persistQuantity: function(){
		this.conn.request({
			"url": "../m/part/foo",
			"method": "POST",
			"success": function(response) {
				callDetail.currentId = id;
				callDetail.update({
					"xtype": "panel",
					"layout": "border",
					"html": response.responseText
				});
			}
		});
	}
});
