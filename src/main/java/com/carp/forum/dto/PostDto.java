package com.carp.forum.dto;

import java.util.HashSet;
import java.util.Set;

import com.carp.forum.entities.DbObject;

public class PostDto extends DbObject {

	private Long userId;
	private long threadId;
	private long ordre;
	private Set<Long> replyTo = new HashSet<>();
	private Set<Long> repliedBy = new HashSet<>();
	private String text;
	private String username;
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public long getOrdre() {
		return ordre;
	}
	public void setOrdre(long ordre) {
		this.ordre = ordre;
	}

	public Set<Long> getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(Set<Long> replyTo) {
		this.replyTo = replyTo;
	}
	public Set<Long> getRepliedBy() {
		return repliedBy;
	}
	public void setRepliedBy(Set<Long> repliedBy) {
		this.repliedBy = repliedBy;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
