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
@Table(name = "vote")
public class Vote implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private Integer userId;
	private Integer postId;
	
	
	public Vote() {
		
	}


	public Vote(Integer id, Integer userId, Integer postId) {
		super();
		this.id = id;
		this.userId = userId;
		this.postId = postId;
	}

	public Vote(Integer userId, Integer postId) {
		  this.userId = userId;
		  this.postId = postId;
		}	
	

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public Integer getPostId() {
		return postId;
	}


	public void setPostId(Integer postId) {
		this.postId = postId;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id, postId, userId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vote other = (Vote) obj;
		return Objects.equals(id, other.id) && Objects.equals(postId, other.postId)
				&& Objects.equals(userId, other.userId);
	}


	@Override
	public String toString() {
		return "Vote [id=" + id + ", userId=" + userId + ", postId=" + postId + "]";
	}
	
	
	
}
