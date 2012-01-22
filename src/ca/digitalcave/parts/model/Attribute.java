package ca.digitalcave.parts.model;

public class Attribute {

	private int partId;
	private String name;
	private String value;
	private String href;
	
	public int getPartId() {
		return partId;
	}
	public void setPartId(int partId) {
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
