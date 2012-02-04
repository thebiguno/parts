package ca.digitalcave.parts.digi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.visitors.NodeVisitor;

import ca.digitalcave.parts.model.Attribute;

public class DigiKeyVisitor extends NodeVisitor {
	private static final HashSet<String> IGNORE = new HashSet<String>();
	private static final HashMap<String, String> ENTITIES = new HashMap<String, String>();

	ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	String title;
	LinkedHashMap<String, String> inputs = new LinkedHashMap<String, String>();
	String script;
	String action;
	
	static {
		for (String string : new String[] { "Customer Reference", "Extended Price", "Quantity Available", "For Use With/Related Products", "Catalog Page", "For Use With", "Associated Product", "Other Names", "3D Model", "Standard Package", "RoHS Information", "Lead Free Status / RoHS Status", "Catalog Drawings", "Minimum Quantity", "Product Change Notification" }) {
			IGNORE.add(string);
		}
		ENTITIES.put("&reg;", "\u00ae");
		ENTITIES.put("&deg;", "\u00b0");
		ENTITIES.put("&plusmn;", "\u00b1");
		ENTITIES.put("&sup2;", "\u00b2");
		ENTITIES.put("&sup3;", "\u00b3");
		ENTITIES.put("&micro;", "\u00b5");
		ENTITIES.put("&Omega;", "\u03a9");
	}
	
	// NOTES
	// each row has one th and one td
	// td will either be a string or an anchor
	
	// td's with colspan can be ignored

	private final Stack<Tag> tables = new Stack<Tag>();
	private boolean listening = false;
	private Attribute attribute;
	
	@Override
	public void visitTag(Tag tag) {
//		System.out.println(tag.getText());
		if ("TITLE".equals(tag.getTagName())) {
			title = tag.getText();
		}
		
		if ("SCRIPT".equals(tag.getTagName()) && title == null) {
			this.script = tag.toPlainTextString();
		}
		if ("INPUT".equals(tag.getTagName()) && title == null) {
			inputs.put(tag.getAttribute("name"), tag.getAttribute("value"));
		}
		if ("FORM".equals(tag.getTagName()) && title == null) {
			action = tag.getAttribute("action");
		}
		if ("TABLE".equals(tag.getTagName()) && title != null) {
			if (listening) {
				if (tag.isEndTag()) {
					tables.pop();
				} else {
					tables.push(tag);
				}
			}
			// <table border=1 cellspacing=1 cellpadding=2>
			if (!tag.isEndTag() 
					&& "1".equals(tag.getAttribute("border"))
					&& "1".equals(tag.getAttribute("cellspacing"))
					&& "2".equals(tag.getAttribute("cellpadding"))) {
				listening = !tag.isEndTag();
			}
		}
		if (listening) {
			String text = tag.toPlainTextString().trim();
			if ("TH".equals(tag.getTagName())) {
				attribute = new Attribute();
				attribute.setName(text);
			}
			if ("TD".equals(tag.getTagName())) {
				if (!(attribute == null || IGNORE.contains(attribute.getName()))) {
					if (tag.getChildren().elementAt(0) instanceof TableTag) {
						return;
					} else if ("Datasheets".equals(attribute.getName())) {
						final DatasheetVisitor datasheetVisitor = new DatasheetVisitor();
						tag.accept(datasheetVisitor);
						attributes.addAll(datasheetVisitor.datasheets);
						return;
					} else if (tag.getChildren().elementAt(0) instanceof LinkTag) {
						attribute.setHref(((LinkTag) tag.getChildren().elementAt(0)).getAttribute("href"));
					}
					for (Map.Entry<String, String> entity : ENTITIES.entrySet()) {
						text = text.replaceAll(entity.getKey(), entity.getValue());
					}
					attribute.setValue(text);
					attributes.add(attribute);
				}
			}
		}
	}
	
	private static class DatasheetVisitor extends NodeVisitor {
		private final ArrayList<Attribute> datasheets = new ArrayList<Attribute>();
		@Override
		public void visitTag(Tag tag) {
			if ("A".equals(tag.getTagName())) {
				final Attribute datasheet = new Attribute();
				datasheet.setName("Datasheet");
				datasheet.setHref(((LinkTag) tag).getAttribute("href"));
				datasheet.setValue(tag.toPlainTextString().trim());
				datasheets.add(datasheet);
			}
		}
	}
}
