package ca.digitalcave.parts.model;

import java.util.List;


public class Family {

	private Category category;
	private Integer id;
	private String name;
	private List<Part> parts;
	
	public Family() {

	}
	
	public Family(int id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
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
