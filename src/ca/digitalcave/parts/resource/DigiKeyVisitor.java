package ca.digitalcave.parts.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.visitors.NodeVisitor;

public class DigiKeyVisitor extends NodeVisitor {
	private static final HashSet<String> IGNORE = new HashSet<String>();
	private static final HashMap<String, String> ENTITIES = new HashMap<String, String>();
	private final ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	
	static {
		for (String string : new String[] { "Customer Reference", "Extended Price", "Quantity Available", "Product Photos", "For Use With/Related Products", "Catalog Page", "For Use With", "Associated Product", "Other Names", "3D Model", "Standard Package", "RoHS Information", "Lead Free Status / RoHS Status", "Catalog Drawings", "Minimum Quantity", "Product Change Notification" }) {
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
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	@Override
	public void visitTag(Tag tag) {
		if ("TABLE".equals(tag.getTagName())) {
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
				attribute.name = text;
			}
			if ("TD".equals(tag.getTagName())) {
				if (!(attribute == null || IGNORE.contains(attribute.name))) {
					if (tag.getChildren().elementAt(0) instanceof TableTag) {
						return;
					} else if ("Datasheets".equals(attribute.name)) {
						final DatasheetVisitor datasheetVisitor = new DatasheetVisitor();
						tag.accept(datasheetVisitor);
						attributes.addAll(datasheetVisitor.datasheets);
						return;
					} else if (tag.getChildren().elementAt(0) instanceof LinkTag) {
						attribute.href = ((LinkTag) tag.getChildren().elementAt(0)).getAttribute("href");
					}
					for (Map.Entry<String, String> entity : ENTITIES.entrySet()) {
						text = text.replaceAll(entity.getKey(), entity.getValue());
					}
					attribute.value = text;
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
				datasheet.name = "Datasheet";
				datasheet.href = ((LinkTag) tag).getAttribute("href");
				datasheet.value = tag.toPlainTextString().trim();
				datasheets.add(datasheet);
			}
		}
	}

	public static class Attribute {
		String name;
		String value;
		String href;
		
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(" => ");
			sb.append(value);
			if (href != null) {
				sb.append(" => ");
				sb.append(href);
			}
			return sb.toString();
		}
	}
	
	public static void main(String[] args) throws Exception {
		final String[] parts = new String[] {
				"http://search.digikey.com/ca/en/products/ATMEGA644A-PU/ATMEGA644A-PU-ND/2271041",
				"http://search.digikey.com/ca/en/products/ERD-S2TJ5R1V/P5.1BATB-ND/503242",
		};
		
		final ConnectionManager connectionManager = Page.getConnectionManager();
		for (String part : parts) {
			System.out.println(part);
			final Parser parser = new Parser(connectionManager.openConnection(part));
			final DigiKeyVisitor visitor = new DigiKeyVisitor();
			parser.visitAllNodesWith(visitor);
			for (Attribute attribute : visitor.attributes) {
				System.out.println(attribute);
			}
		}
	}
}
