package ca.digitalcave.parts.model;

import java.util.ArrayList;
import java.util.List;


public class Part {

	private Category category;
	private Integer id;
	private Integer available;
	private Integer minimum;
	private String number;
	private String description;
	private String notes;
	private List<Attribute> attributes;
	
	public Part() {
		
	}
	public Part(int id) {
		this.id = id;
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getAvailable() {
		return available;
	}
	public void setAvailable(Integer available) {
		this.available = available;
	}
	
	public Integer getMinimum() {
		return minimum;
	}
	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
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
