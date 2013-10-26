package ca.digitalcave.parts.model;

import java.sql.Blob;
import java.util.Iterator;
import java.util.List;

public class Attribute {

	private Long id;
	private Integer part;
	private String name;
	private String value;
	private String href;
	private String mimeType;
	private Blob data; // TODO change this to be a blob when PostgreSql supports Conn.createBlob
	
	public Attribute() {
	}
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getPart() {
		return part;
	}
	public void setPart(Integer part) {
		this.part = part;
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
	
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public Blob getData() {
		return data;
	}
	public void setData(Blob data) {
		this.data = data;
	}
	
	public static Attribute find(String name, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			if (attribute.getName().equals(name)) return attribute;
		}
		return null;
	}
	public static Attribute remove(String name, List<Attribute> attributes) {
		final Iterator<Attribute> i = attributes.iterator();
		while (i.hasNext()) {
			final Attribute a = i.next();
			if (a.getName().equals(name)) {
				i.remove();
				return a;
			}
		}
		return null;
	}
	
}
