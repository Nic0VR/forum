package com.carp.forum.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
@Entity
public class FileInfo extends DbObject{
	@ManyToOne(optional = true)
	private Post post;
	@ManyToOne(optional = true)
	private Thread thread;
	@Column(unique = true)
	private String fileName;
	private String prefiewFileName;
	private String originalFileName;
	private String fileSize;
	private String fileType;
	
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
	public String getPrefiewFileName() {
		return prefiewFileName;
	}
	public void setPrefiewFileName(String prefiewFileName) {
		this.prefiewFileName = prefiewFileName;
	}
	

	

}
