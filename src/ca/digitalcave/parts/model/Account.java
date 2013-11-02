package ca.digitalcave.parts.model;

import java.util.Date;

import org.restlet.security.User;

public class Account extends User {

	private int id;
	private String activationKey;
	private Date createdAt;
	private Date modifiedAt;

	public Account() {
		
	}
	
	public Account(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	public void setSecretString(String secret) {
		setSecret(secret.toCharArray());
	}
	public String getSecretString() {
		return new String(getSecret());
	}
	
	public String getActivationKey() {
		return activationKey;
	}
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
}
