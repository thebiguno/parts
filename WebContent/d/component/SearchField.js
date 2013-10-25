Ext.define("Parts.component.SearchField", {
	"extend": "Ext.form.field.Trigger",
	"alias": "widget.searchfield",
	"triggerCls": Ext.baseCSSPrefix + 'form-search-trigger',
	
	"displayField": "text",
	"valueField": "value",
	
	"setValue": function(value, doSelect) {
		var v = Array.isArray(value) ? value[0] : value;
		if (v && v.isModel) {
			v = v.data;
		}
		if (Ext.isObject(v)) {
			this.value = v;
			this.setRawValue(v ? v[this.initialConfig.displayField || 'text'] : '');
			this.checkChange();
		}
		return this;
	},
	"getValue": function() {
		return this.value;
	}
});