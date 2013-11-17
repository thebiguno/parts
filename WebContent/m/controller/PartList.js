Ext.define("Parts.controller.PartList", {
	"extend": "Ext.app.Controller",
	"config": {
		"refs": {
			"part": "partdetail"
		},
		"control": {
			"partlist": {
				"itemtap": function(list, index, target, record) {
					var part = this.getPart();
					if (part == null){
						part = Ext.create("Parts.view.PartDetail", {});
					}
					part.setPart(record);
					Ext.Viewport.animateActiveItem(part, {type: 'slide', direction: 'left'});
				}
			},
			"searchfield[itemId=search]": {
				"change": function(field, newValue) {
					var store = field.up('partlist').getStore();
					store.setParams({"q":newValue});
					store.load();
				}
			},
			"button[itemId=logout]": {
				"tap": function() {
					Ext.Ajax.request({
						"url": "index",
						"method": "DELETE",
						"success": function() {
							window.location.reload();
						}
					});
				}
			}
		}
	}
});
