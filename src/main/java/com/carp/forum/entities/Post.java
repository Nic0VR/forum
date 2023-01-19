package com.carp.forum.entities;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Post extends DbObject {

	@ManyToOne(optional = true, targetEntity = User.class)
	private User user;
	@Column(columnDefinition = "TEXT")
	private String text;
	private LocalDateTime creationDate;
	private long ordre;
	@ManyToOne(optional = false, targetEntity = Thread.class)
	private Thread thread;

	/**
	 * Set contenant les posts auxquels ce post répond
	 * 
	 */
	@ManyToMany
	@JoinTable(name="responses",joinColumns = @JoinColumn(name="responseId"),
	inverseJoinColumns = @JoinColumn(name="postId"))
	private Set<Post> replyTo = new HashSet<Post>();
	/**
	 * Set contenant les posts qui répondent a ce post
	 */
	@ManyToMany
	@JoinTable(name = "responses", joinColumns = @JoinColumn(name = "postId"),
	inverseJoinColumns = @JoinColumn(name = "responseId"))
	private Set<Post> repliedBy = new HashSet<Post>();

	public Post() {
		super();
		this.creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
	}

	/**
	 * Ajoute un post dans la liste des posts cités par ce post
	 * 
	 * @param post le post a ajouter
	 */
	public void addReplyTo(Post post) {
		replyTo.add(post);
	}

	/**
	 * Retire un post de la liste des posts cités par ce post
	 * 
	 * @param post
	 */
	public void removeReplyTo(Post post) {
		replyTo.remove(post);
	}
	/**
	 * Ajoute un post dans la liste des posts qui citent ce post
	 * 
	 * @param post le post a ajouter
	 */
	public void addRepliedBy(Post post) {
		repliedBy.add(post);
	}

	/**
	 * Retire un post de la liste des posts qui citent ce post
	 * 
	 * @param post
	 */
	public void removeRepliedBy(Post post) {
		repliedBy.remove(post);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getOrdre() {
		return ordre;
	}

	public void setOrdre(long ordre) {
		this.ordre = ordre;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Set<Post> getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Set<Post> replyTo) {
		this.replyTo = replyTo;
	}

	public Set<Post> getRepliedBy() {
		return repliedBy;
	}

	public void setRepliedBy(Set<Post> repliedBy) {
		this.repliedBy = repliedBy;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(creationDate, text, thread);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Post))
			return false;
		Post other = (Post) obj;
		return Objects.equals(creationDate, other.creationDate) && Objects.equals(text, other.text)
				&& Objects.equals(thread, other.thread);
	}


	
}
