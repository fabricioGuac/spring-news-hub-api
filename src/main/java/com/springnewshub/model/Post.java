package com.springnewshub.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;




@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "post")
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String title;
	private String postUrl;
	@Transient
	private String username;
	@Transient
	private int voteCount;
	private Integer userId;

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "posted_at")
	private Date postedAt = new Date();
	
	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "updated_at")
	private  Date updatesAt = new Date();

	
	// Uses lazy fetch to avoid multiple bags exception
	@OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comment> comments;
	
	public Post() {
		
	}	
	
	public Post(Integer id, String title, String postUrl, int voteCount, Integer userId) {
		super();
		this.id = id;
		this.title = title;
		this.postUrl = postUrl;
		this.voteCount = voteCount;
		this.userId = userId;
	}


	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getPostedAt() {
		return postedAt;
	}

	public void setPostedAt(Date postedAt) {
		this.postedAt = postedAt;
	}

	public Date getUpdatesAt() {
		return updatesAt;
	}

	public void setUpdatesAt(Date updatesAt) {
		this.updatesAt = updatesAt;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		return Objects.hash(comments, id, postUrl, postedAt, title, updatesAt, userId, username, voteCount);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Objects.equals(comments, other.comments) && Objects.equals(id, other.id)
				&& Objects.equals(postUrl, other.postUrl) && Objects.equals(postedAt, other.postedAt)
				&& Objects.equals(title, other.title) && Objects.equals(updatesAt, other.updatesAt)
				&& Objects.equals(userId, other.userId) && Objects.equals(username, other.username)
				&& voteCount == other.voteCount;
	}
	

	@Override
	public String toString() {
		return "Post [id=" + id + ", title=" + title + ", postUrl=" + postUrl + ", username=" + username
				+ ", voteCount=" + voteCount + ", userId=" + userId + ", postedAt=" + postedAt + ", updatesAt="
				+ updatesAt + ", comments=" + comments + "]";
	}
	
	
	
}
