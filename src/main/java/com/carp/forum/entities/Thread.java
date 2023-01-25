package com.carp.forum.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/**
 * Un Thread représente un fil de discussion. Il comporte un titre et un texte
 * faisant office de 1er post.
 * 
 * @author nicolas
 *
 */
@Entity
public class Thread extends DbObject {

	/**
	 * Liste des posts du thread
	 */
	@OneToMany(mappedBy = "thread", orphanRemoval = true)
	private Set<Post> posts = new HashSet<Post>();
	/**
	 * Board contenant le thread
	 */
	@ManyToOne(targetEntity = Board.class)
	private Board board;
	/**
	 * Titre du thread
	 */
	private String title;
	/**
	 * Texte du thread
	 */
	private String text;
	
	private LocalDateTime creationDate;

	
	@ManyToOne(targetEntity = User.class)
	private User user;
	
	public Thread() {
		super();
		this.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS)); 
	}

	public void addPost(Post post) {
		posts.add(post);
		post.setThread(this);
	}

	public void removePost(Post post) {
		posts.remove(post);
		post.setThread(null);
	}

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

	public Set<Post> getPosts() {
		return posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(board, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Thread))
			return false;
		Thread other = (Thread) obj;
		return Objects.equals(board, other.board) && Objects.equals(title, other.title);
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	private void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	
	
}
