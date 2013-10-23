package ca.digitalcave.parts.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Category {

	private Integer id;
	private Integer parentId;
	private Account account;
	private String name;
	private List<Category> children = new LinkedList<Category>();
	
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
	
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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

	public List<Category> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Category buildTree(List<Category> categories) {
		final HashMap<Integer, Category> lookup = new HashMap<Integer, Category>();
		final Category root = new Category();
		for (Category category : categories) {
			lookup.put(category.getId(), category);
		}
		for (Category category : categories) {
			final Category parent = category.parentId == null ? root : lookup.get(category.parentId);
			parent.getChildren().add(category);
		}
		return root;
	}
}
