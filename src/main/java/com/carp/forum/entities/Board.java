package com.carp.forum.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Représente un sous-forum sur un thème dédié.
 * @author nicolas
 *
 */
@Entity
public class Board extends DbObject {
	/**
	 * titre du board
	 */
	@Column(unique = true, nullable = false)
	private String title;
	/**
	 * description du board
	 */
	private String description;
	/**
	 * reference du board;
	 */
	@Column(unique = true,nullable = false)
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

	@Override
	public int hashCode() {
		return Objects.hash(ref, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Board))
			return false;
		Board other = (Board) obj;
		return Objects.equals(ref, other.ref) && Objects.equals(title, other.title);
	}


	
}
