package com.springnewshub.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "comment")
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String commentText;
	private Integer postId;
	private Integer userId;
	
	public Comment () {
		
	}

	public Comment(Integer id, String commentText, Integer postId, Integer userId) {
		super();
		this.id = id;
		this.commentText = commentText;
		this.postId = postId;
		this.userId = userId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(commentText, id, postId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		return Objects.equals(commentText, other.commentText) && Objects.equals(id, other.id)
				&& Objects.equals(postId, other.postId) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", commentText=" + commentText + ", postId=" + postId + ", userId=" + userId + "]";
	}
	
	
	
}
