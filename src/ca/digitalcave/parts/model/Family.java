package ca.digitalcave.parts.model;

import java.util.List;

public class Family {

	private String category;
	private String name;
	private List<Integer> partIds;
	
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Integer> getPartIds() {
		return partIds;
	}
	public void setPartIds(List<Integer> partIds) {
		this.partIds = partIds;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
