package ca.digitalcave.parts.model;

import java.util.List;

public class Category {

	private Integer id;
	private String name;
	private List<Family> families;
	
	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Family> getFamilies() {
		return families;
	}
	public void setFamilies(List<Family> families) {
		this.families = families;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
