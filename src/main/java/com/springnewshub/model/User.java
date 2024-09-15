package com.springnewshub.model ;

import java.util.List;
import java.io.Serializable;
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
import jakarta.persistence.Transient;



@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "app_user")

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
//	Uses integer wrapper for better compatibility with the ORM
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String username;
    
    @Column(unique = true)
    private String email;
    private String password;
    
    @Transient
    private boolean loggedIn;
    
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Post> posts;
    
//    Uses lazy fetch to avoid multiple bags exception
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;
	@OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
	
	public User() {
	}
	
	public User(Integer id, String username, String email, String password) {
	    this.id = id;
	    this.username = username;
	    this.email = email;
	    this.password = password;
	}
    
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	public List<Post> getPosts() {
		return posts;
	}
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}
	public List<Vote> getVotes() {
		return votes;
	}
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		return Objects.hash(comments, email, id, loggedIn, password, posts, username, votes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(comments, other.comments) && Objects.equals(email, other.email)
				&& Objects.equals(id, other.id) && loggedIn == other.loggedIn
				&& Objects.equals(password, other.password) && Objects.equals(posts, other.posts)
				&& Objects.equals(username, other.username) && Objects.equals(votes, other.votes);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", loggedIn=" + loggedIn + ", posts=" + posts + ", votes=" + votes + ", comments=" + comments + "]";
	}
	
	
}
