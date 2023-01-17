package com.carp.forum.dto;

import com.carp.forum.entities.DbObject;

public class BoardDto extends DbObject {

	private String title;

	private String description;

	private String ref;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
	
	
}
