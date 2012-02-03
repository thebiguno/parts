package ca.digitalcave.parts.digi;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.htmlparser.Parser;

import ca.digitalcave.parts.model.Attribute;

/**
 * This class is NOT thread safe
 */
public class DigiKeyClient {
	
	private String action;
	private String cookie;
	private StringBuilder entity = new StringBuilder();
	
	public List<Attribute> parse(String url) throws Exception {
		final Parser parser = new Parser(url);
		
		final DigiKeyVisitor visitor = new DigiKeyVisitor();
		parser.visitAllNodesWith(visitor);
		
		if (visitor.title == null) {
			final ScriptEngineManager manager = new ScriptEngineManager();
			final ScriptEngine engine = manager.getEngineByName("javascript");
			
			// setup
			engine.put("out", this);
			engine.eval("document = {};");
			engine.eval("document.forms = [];");
			engine.eval("document.forms[0] = {};");
			engine.eval("document.forms[0].elements = [];");
			for (Map.Entry<String, String> entry : visitor.inputs.entrySet()) {
				engine.eval(String.format("document.forms[0].elements.push({\"name\":\"%s\",\"value\":\"%s\"});",entry.getKey(),entry.getValue()));
			}
			engine.eval("document.forms[0].attributes = {};");
			engine.eval(String.format("document.forms[0].action = '%s'", visitor.action));
			engine.eval("document.forms[0].submit = function() { out.setCookie(document.cookie); out.setAction(document.forms[0].action); for (var i = 0; i < document.forms[0].elements.length; i++) { out.setInput(i, document.forms[0].elements[i].name, document.forms[0].elements[i].value); } };");
			engine.eval(visitor.script);
			engine.eval("test()");
			
			final URLConnection connection = new URL(action).openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.getOutputStream().write(entity.toString().getBytes("UTF-8"));
			final Parser parser2 = new Parser(connection);
			parser2.visitAllNodesWith(visitor);
		}
		
		return visitor.attributes;
	}
	
	public void setAction(String action) {
		this.action = "http://search.digikey.com" + action;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public void setInput(int i, String name, String value) throws Exception {
		if (entity.length() > 0) entity.append("&");

		entity.append(URLEncoder.encode(name, "UTF-8"));
		entity.append("=");
		entity.append(URLEncoder.encode(value, "UTF-8"));
	}
	
	
	public static void main(String[] args) throws Exception {
		final String[] parts = new String[] {
				"http://search.digikey.com/scripts/dksearch/dksus.dll?vendor=0&keywords=490-3637-nd",
//				"http://search.digikey.com/ca/en/products/ATMEGA644A-PU/ATMEGA644A-PU-ND/2271041",
//				"http://search.digikey.com/ca/en/products/ERD-S2TJ5R1V/P5.1BATB-ND/503242",
		};
		
		final DigiKeyClient client = new DigiKeyClient();
		for (String part : parts) {
			System.out.println(part);
			final List<Attribute> attributes = client.parse(part);
			for (Attribute attribute : attributes) {
				System.out.println(attribute);
			}
		}
	}

}
