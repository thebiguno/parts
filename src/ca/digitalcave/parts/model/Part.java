package ca.digitalcave.parts.model;

import java.util.List;


public class Part {

	private short id;
	private List<Attribute> attributes;
	
	public int getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public Attribute findAttribute(String name) {
		for (Attribute attribute : attributes) {
			if (name.equals(attribute.getName())) {
				return attribute;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
