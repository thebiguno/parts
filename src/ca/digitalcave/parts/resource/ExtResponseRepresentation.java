package ca.digitalcave.parts.resource;

import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.representation.StringRepresentation;

public class ExtResponseRepresentation extends StringRepresentation {

	public ExtResponseRepresentation() {
		super("{\"success\":true}");
	}
	public ExtResponseRepresentation(String message) {
		super("{\"success\":false,\"message\":\"" + StringEscapeUtils.escapeJavaScript(message) + "\"}");
	}
}
