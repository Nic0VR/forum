package com.carp.forum.dto;

import com.carp.forum.entities.DbObject;

public class ThreadDto extends DbObject{

	private String title;
	private String text;
	private long boardId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getBoardId() {
		return boardId;
	}
	public void setBoardId(long boardId) {
		this.boardId = boardId;
	}
	
	
}
