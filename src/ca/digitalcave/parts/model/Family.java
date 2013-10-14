package ca.digitalcave.parts.model;

import java.util.List;


public class Family {

	private Integer categoryId;
	private Integer id;
	private String name;
	private List<Part> parts;
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Part> getParts() {
		return parts;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
