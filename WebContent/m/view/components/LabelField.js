Ext.define("Parts.view.components.LabelField", {
	extend: 'Ext.form.Field',
	alias: "widget.labelfield",
	isField: true,
	value: '',
	renderSelectors: {fieldEl: '.x-form-labelfield'},
	config: {
		disabled: true,
		disabledCls: null,
		component: {disabledCls: null},
	},
	setValue:function(val) {
		this.value = (!val ? '' : val);
		this.getComponent().setHtml("<div class='x-field-input'><span class='x-input-el x-form-field x-input-text'>" + this.value + "</span></div>");
		return this;
	}
});