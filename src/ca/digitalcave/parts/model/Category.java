package ca.digitalcave.parts.model;

import java.util.List;

public class Category {

	private Integer id;
	private Account account;
	private String name;
	private List<Family> families;
	
	public Category() {
	}
	
	public Category(int id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
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
