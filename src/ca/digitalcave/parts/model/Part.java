package ca.digitalcave.parts.model;

import java.util.ArrayList;
import java.util.List;


public class Part {

	private Integer id;
	private List<Attribute> attributes;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public List<Attribute> findAttributes(String name) {
		final ArrayList<Attribute> result = new ArrayList<Attribute>();
		for (Attribute attribute : attributes) {
			if (name.equals(attribute.getName())) {
				result.add(attribute);
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
