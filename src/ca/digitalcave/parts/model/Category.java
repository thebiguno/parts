package ca.digitalcave.parts.model;

import java.util.List;

public class Category {

	private Integer partId;
	private String name;
	private List<Family> families;
	
	public Integer getPartId() {
		return partId;
	}
	
	public void setPartId(Integer partId) {
		this.partId = partId;
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
