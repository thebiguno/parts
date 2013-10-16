package ca.digitalcave.parts.model;

import java.sql.Blob;

public class Attachment {

	private int partId;
	private Integer id;
	private String name;
	private String mediaType;
	private Blob media;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Blob getMedia() {
		return media;
	}
	public void setMedia(Blob media) {
		this.media = media;
	}
	
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	
}
