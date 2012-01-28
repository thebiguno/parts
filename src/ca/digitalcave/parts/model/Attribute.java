package ca.digitalcave.parts.model;

public class Attribute {

	private short partId;
	private String name;
	private String value;
	private String href;
	private short sort;
	
	public Attribute() {
	}
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public short getPartId() {
		return partId;
	}
	public void setPartId(short partId) {
		this.partId = partId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	public short getSort() {
		return sort;
	}
	public void setSort(short sort) {
		this.sort = sort;
	}
	
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" => ");
		sb.append(value);
		if (href != null) {
			sb.append(" => ");
			sb.append(href);
		}
		return sb.toString();
	}

}
